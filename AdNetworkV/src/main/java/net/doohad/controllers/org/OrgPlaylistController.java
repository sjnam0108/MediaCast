package net.doohad.controllers.org;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.doohad.exceptions.ServerOperationForbiddenException;
import net.doohad.info.StringInfo;
import net.doohad.models.AdnMessageManager;
import net.doohad.models.CustomComparator;
import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.DataSourceRequest.FilterDescriptor;
import net.doohad.models.adc.AdcAdCreative;
import net.doohad.models.adc.AdcCreatFile;
import net.doohad.models.adc.AdcCreatTarget;
import net.doohad.models.adc.AdcCreative;
import net.doohad.models.fnd.FndRegion;
import net.doohad.models.fnd.FndState;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.inv.InvSite;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.knl.KnlUser;
import net.doohad.models.org.OrgAdvertiser;
import net.doohad.models.org.OrgPlTarget;
import net.doohad.models.org.OrgPlaylist;
import net.doohad.models.service.AdcService;
import net.doohad.models.service.FndService;
import net.doohad.models.service.InvService;
import net.doohad.models.service.KnlService;
import net.doohad.models.service.OrgService;
import net.doohad.utils.Util;
import net.doohad.viewmodels.DropDownListItem;
import net.doohad.viewmodels.adc.AdcCreatFilePAItem;
import net.doohad.viewmodels.org.OrgPlTargetItem;
import net.doohad.viewmodels.org.OrgPlTargetOrderItem;
import net.doohad.viewmodels.org.OrgPlaylistItem;
import net.doohad.viewmodels.org.OrgPlaylistOrderItem;

/**
 * 재생 목록 컨트롤러
 */
@SuppressWarnings("unused")
@Controller("org-playlist-controller")
@RequestMapping(value="/org/playlist")
public class OrgPlaylistController {

	private static final Logger logger = LoggerFactory.getLogger(OrgCurrMediumController.class);


    @Autowired 
    private OrgService orgService;

    @Autowired 
    private KnlService knlService;

    @Autowired 
    private AdcService adcService;

    @Autowired 
    private InvService invService;

    @Autowired 
    private FndService fndService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 재생 목록 페이지
	 */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String index(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	modelMgr.addMainMenuModel(model, locale, session, request);
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

    	// 페이지 제목
    	model.addAttribute("pageTitle", "재생 목록");
    	
    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "org/playlist";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		// 가능한 광고/광고 소재 자료를 미리 확인
    		List<AdcAdCreative> list = adcService.getPlAdCreativeListByMediumId(Util.getSessionMediumId(session));
    		ArrayList<String> validIds = new ArrayList<String>();
    		Date today = Util.removeTimeOfDate(new Date());
    		
    		for(AdcAdCreative adCreat : list) {
    			if (Util.isBetween(today, adCreat.getStartDate(), adCreat.getEndDate())) {
    				validIds.add("A" + adCreat.getId());
    			}
    		}

    		// 각 재생 목록별 대상 화면 id 수를 미리 계산
    		HashMap<String, Integer> map = orgService.getPlaylistScreenCountByMediumId(Util.getSessionMediumId(session));
    		
    		
    		DataSourceResult result = orgService.getPlaylistList(request);
    		
    		for(Object obj : result.getData()) {
    			OrgPlaylist playlist = (OrgPlaylist)obj;
    			
    			int cnt = 0;
    			List<String> adCreatIds = Util.tokenizeValidStr(playlist.getAdValue());
    			for(String s : adCreatIds) {
    				if (validIds.contains("A" + s)) {
    					cnt++;
    				}
    			}
    			playlist.setValidAdCount(cnt);
    			
    			Integer sCnt = map.get("P" + playlist.getId());
    			if (sCnt != null) {
    				playlist.setScreenCount(sCnt.intValue());
    			}
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 가능한 재생 목록 전체 
	 */
    @RequestMapping(value = "/readAds", method = RequestMethod.POST)
    public @ResponseBody List<OrgPlaylistItem> readAds(Locale locale, HttpSession session) {
    	try {
    		ArrayList<String> keys = new ArrayList<String>();
    		ArrayList<OrgPlaylistItem> plItems = new ArrayList<OrgPlaylistItem>();
    		List<AdcAdCreative> list = adcService.getPlAdCreativeListByMediumId(Util.getSessionMediumId(session));
    		
    		for(AdcAdCreative adCreative : list) {
    			OrgPlaylistItem pi = new OrgPlaylistItem(adCreative);
    			if (!keys.contains(pi.getKey())) {
        			plItems.add(pi);
        			keys.add(pi.getKey());
    			}
    		}
    		
    		Collections.sort(plItems, new Comparator<OrgPlaylistItem>() {
    	    	public int compare(OrgPlaylistItem item1, OrgPlaylistItem item2) {
    	    		if (item1.getCreatName().equals(item2.getCreatName())) {
    	    			if (item1.getStartDate().compareTo(item2.getStartDate()) == 0) {
    	    				return item1.getEndDate().compareTo(item2.getEndDate());
    	    			} else {
    	    				return item2.getStartDate().compareTo(item1.getStartDate());
    	    			}
    	    		} else {
        	    		return item1.getCreatName().compareTo(item2.getCreatName());
    	    		}
    	    	}
    	    });
    		
    		return plItems;
    	} catch (Exception e) {
    		logger.error("readAds", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 추가 액션
	 */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody String create(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	
    	String name = (String)model.get("name");
    	String ids = (String)model.get("ids");
    	
    	List<String> idList = Util.tokenizeValidStr(ids);
    	
    	Date startDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("startDate")));
    	Date endDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("endDate")));
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(name) || Util.isNotValid(ids) || idList.size() == 0) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	// 비즈니스 로직 검증
    	if (startDate != null && endDate != null && startDate.after(endDate)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_NOT_BEFORE_END_DATE);
    	}
    	
    	
    	OrgPlaylist target = new OrgPlaylist(medium, name, ids, idList.size(), startDate, endDate, 1000, session);

        saveOrUpdate(target, locale, session);

        return "Ok";
    }
    
    
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	
    	String name = (String)model.get("name");
    	String ids = (String)model.get("ids");
    	
    	List<String> idList = Util.tokenizeValidStr(ids);
    	
    	Date startDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("startDate")));
    	Date endDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("endDate")));
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(name) || Util.isNotValid(ids) || idList.size() == 0) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	// 비즈니스 로직 검증
    	if (startDate != null && endDate != null && startDate.after(endDate)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_NOT_BEFORE_END_DATE);
    	}
    	
    	OrgPlaylist target = orgService.getPlaylist((int)model.get("id"));
    	if (target != null) {
        	
            target.setName(name);
            target.setAdValue(ids);
            target.setAdCount(idList.size());
            target.setStartDate(startDate);
            target.setEndDate(endDate);

            
            target.touchWho(session);
            
            saveOrUpdate(target, locale, session);
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(OrgPlaylist target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
    	
    	// 비즈니스 로직 검증
        
        // DB 작업 수행 결과 검증
        try {
            orgService.saveAndReorderPlaylist(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_NAME);
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_NAME);
        } catch (Exception e) {
    		logger.error("saveOrUpdate", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }
    }
    
    
	/**
	 * 읽기 액션 - 재생 목록 순서 변경용
	 */
    @RequestMapping(value = "/readOrdered", method = RequestMethod.POST)
    public @ResponseBody List<OrgPlaylistOrderItem> readOrderedPlaylists(@RequestBody Map<String, Object> model, 
    		HttpSession session) {
    	
    	List<OrgPlaylist> list = orgService.getPlaylistListByMediumId(Util.getSessionMediumId(session));
    	
    	ArrayList<OrgPlaylistOrderItem> retList = new ArrayList<OrgPlaylistOrderItem>();
    	
    	int idx = 1;
    	for (OrgPlaylist playlist : list) {
    		retList.add(new OrgPlaylistOrderItem(playlist, idx++));
    	}
    	
    	return retList;
    }
    
    
	/**
	 * 순서 변경 액션
	 */
    @RequestMapping(value = "/reorder", method = RequestMethod.POST)
    public @ResponseBody String reorder(@RequestBody Map<String, Object> model, HttpSession session) {
    	
    	String items = (String)model.get("items");
    	if (Util.isValid(items)) {
    		List<String> list = Util.tokenizeValidStr(items);
    		
    		try {
    			int idx = 0;
        		for (String idStr : list) {
        			idx ++;
        			
        			OrgPlaylist playlist = orgService.getPlaylist(Util.parseInt(idStr));
        			if (playlist != null) {
        				int seq = idx * 10;
        				if (playlist.getSiblingSeq() != seq) {
        					playlist.setSiblingSeq(seq);
        					playlist.touchWho(session);
            				
        					orgService.saveOrUpdate(playlist);
        				}
        			}
        		}
    		} catch (Exception e) {
        		logger.error("reorder", e);
            	throw new ServerOperationForbiddenException("OperationError");
    		}
    	}
    	
    	return "OK";
    }

    
    /**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<OrgPlaylist> playlists = new ArrayList<OrgPlaylist>();
    	
    	try {
        	for (Object id : objs) {
        		OrgPlaylist playlist = new OrgPlaylist();
        		
        		playlist.setId((int)id);
        		
        		playlists.add(playlist);
        	}
        	
        	orgService.deletePlaylists(playlists);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }
    
    
	/**
	 * 읽기 액션 - 재생 목록 타겟팅
	 */
    @RequestMapping(value = "/readPlTargets", method = RequestMethod.POST)
    public @ResponseBody List<OrgPlTargetItem> readPlTargets(@RequestBody DataSourceRequest request, 
    		HttpSession session) {
    	try {
    		// 카운팅 계산을 먼저 수행
    		invService.getTargetScreenCountByPlaylistId(request.getReqIntValue1());
    		
    		List<OrgPlTarget> list = orgService.getPlTargetListByPlaylistId(request.getReqIntValue1());
    		
    		ArrayList<OrgPlTargetItem> retList = new ArrayList<OrgPlTargetItem>();
    		for(OrgPlTarget plTarget : list) {
    			retList.add(new OrgPlTargetItem(plTarget));
    		}
    		
    		return retList;
    	} catch (Exception e) {
    		logger.error("readPlTargets", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 변경 액션 - 재생 목록 And/Or 토글 처리
	 */
	@RequestMapping(value = "/updateFilterType", method = RequestMethod.POST)
    public @ResponseBody String updateFilterType(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String filterType = (String)model.get("filterType");

    	OrgPlTarget target = orgService.getPlTarget((int)model.get("id"));
    	
    	// 파라미터 검증
    	if (target == null || Util.isNotValid(filterType)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	
    	target.setFilterType(filterType);
    	
    	target.touchWho(session);

    	
    	try {
    		orgService.saveOrUpdate(target);
    	} catch (Exception e) {
    		logger.error("updateFilterType", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }

        return "Ok";
    }
    
    
	/**
	 * 읽기 액션 - 재생 목록 타겟팅 순서 변경용
	 */
    @RequestMapping(value = "/readOrderedTargets", method = RequestMethod.POST)
    public @ResponseBody List<OrgPlTargetOrderItem> readOrderedTargets(@RequestBody Map<String, Object> model, 
    		HttpSession session) {
    	
    	List<OrgPlTarget> list = orgService.getPlTargetListByPlaylistId((int)model.get("playlistId"));
    	
    	ArrayList<OrgPlTargetOrderItem> retList = new ArrayList<OrgPlTargetOrderItem>();
    	
    	int idx = 1;
    	for (OrgPlTarget plTarget : list) {
    		retList.add(new OrgPlTargetOrderItem(plTarget, idx++));
    	}
    	
    	return retList;
    }
    
    
	/**
	 * 순서 변경 액션 - 재생 목록 타겟팅
	 */
    @RequestMapping(value = "/reorderTargets", method = RequestMethod.POST)
    public @ResponseBody String reorderTargets(@RequestBody Map<String, Object> model, HttpSession session) {
    	
    	String items = (String)model.get("items");
    	if (Util.isValid(items)) {
    		List<String> list = Util.tokenizeValidStr(items);
    		
    		try {
    			int idx = 0;
        		for (String idStr : list) {
        			idx ++;
        			
        			OrgPlTarget plTarget = orgService.getPlTarget(Util.parseInt(idStr));
        			if (plTarget != null) {
        				int seq = idx * 10;
        				if (plTarget.getSiblingSeq() != seq) {
        					plTarget.setSiblingSeq(seq);
        					plTarget.touchWho(session);
            				
        					orgService.saveOrUpdate(plTarget);
        				}
        			}
        		}
    		} catch (Exception e) {
        		logger.error("reorder", e);
            	throw new ServerOperationForbiddenException("OperationError");
    		}
    	}
    	
    	return "OK";
    }
    
    
	/**
	 * 현재의 타겟팅에 대한 대상 화면 갯수 계산
	 */
	@RequestMapping(value = "/calcScrCount", method = RequestMethod.POST)
    public @ResponseBody int calcScreenCount(@RequestBody Map<String, Object> model, Locale locale) {
    	
    	return invService.getTargetScreenCountByPlaylistId((int)model.get("playlistId"));
	}

	
    /**
	 * 삭제 액션 - 재생 목록 타겟팅
	 */
    @RequestMapping(value = "/destroyTargets", method = RequestMethod.POST)
    public @ResponseBody String destroyTargets(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<OrgPlTarget> plTargets = new ArrayList<OrgPlTarget>();

    	for (Object id : objs) {
    		OrgPlTarget plTarget = new OrgPlTarget();
    		
    		plTarget.setId((int)id);
    		
    		plTargets.add(plTarget);
    	}
    	
    	try {
        	orgService.deletePlTargets(plTargets);
    	} catch (Exception e) {
    		logger.error("destroyTargets", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }
	
    
	/**
	 * 읽기 액션 - Kendo AutoComplete 용 광역시/도 정보
	 */
    @RequestMapping(value = "/readACState", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readAutoComplStates(@RequestBody DataSourceRequest request, 
    		HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();

		FilterDescriptor filter = request.getFilter();
		List<FilterDescriptor> filters = filter.getFilters();
		String userInput = "";
		if (filters.size() > 0) {
			userInput = Util.parseString((String) filters.get(0).getValue());
		}

		List<FndState> stateList = fndService.getStateListByNameLike(userInput);
		
		// 전부 읽어 오지 않을 경우, "수정" 모드에서의 값 설정이 정상적이지 않기에 갯수에 대한 제한을 해제
		for(FndState state : stateList) {
			list.add(new DropDownListItem(state.getName(), state.getCode()));
		}
		
		Collections.sort(list, CustomComparator.DropDownListItemTextComparator);

    	return list;
    }
	
    
	/**
	 * 읽기 액션 - Kendo AutoComplete 용 시/군/구 정보
	 */
    @RequestMapping(value = "/readACRegion", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readAutoComplRegions(@RequestBody DataSourceRequest request, 
    		HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();

		FilterDescriptor filter = request.getFilter();
		List<FilterDescriptor> filters = filter.getFilters();
		String userInput = "";
		if (filters.size() > 0) {
			userInput = Util.parseString((String) filters.get(0).getValue());
		}

		List<FndRegion> regionList = fndService.getRegionListByNameLike(userInput);
		
		// 전부 읽어 오지 않을 경우, "수정" 모드에서의 값 설정이 정상적이지 않기에 갯수에 대한 제한을 해제
		for(FndRegion region : regionList) {
			list.add(new DropDownListItem(region.getName(), region.getCode()));
		}
		
		Collections.sort(list, CustomComparator.DropDownListItemTextComparator);

    	return list;
    }
	
    
	/**
	 * 읽기 액션 - Kendo AutoComplete 용 화면 정보
	 */
    @RequestMapping(value = "/readACScreen", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readAutoComplScreens(@RequestBody DataSourceRequest request, 
    		HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();

		FilterDescriptor filter = request.getFilter();
		List<FilterDescriptor> filters = filter.getFilters();
		String userInput = "";
		if (filters.size() > 0) {
			userInput = Util.parseString((String) filters.get(0).getValue());
		}

		List<InvScreen> screenList = invService.getMonitScreenListByMediumNameLike(Util.getSessionMediumId(session), userInput);
		
		// 전부 읽어 오지 않을 경우, "수정" 모드에서의 값 설정이 정상적이지 않기에 갯수에 대한 제한을 해제
		for(InvScreen screen : screenList) {
			list.add(new DropDownListItem(screen.getName(), String.valueOf(screen.getId())));
		}
		
		Collections.sort(list, CustomComparator.DropDownListItemTextComparator);

    	return list;
    }
	
    
	/**
	 * 읽기 액션 - Kendo AutoComplete 용 사이트 정보
	 */
    @RequestMapping(value = "/readACSite", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readAutoComplSites(@RequestBody DataSourceRequest request, 
    		HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();

		FilterDescriptor filter = request.getFilter();
		List<FilterDescriptor> filters = filter.getFilters();
		String userInput = "";
		if (filters.size() > 0) {
			userInput = Util.parseString((String) filters.get(0).getValue());
		}

		List<InvSite> siteList = invService.getMonitSiteListByMediumNameLike(Util.getSessionMediumId(session), userInput);
		
		// 전부 읽어 오지 않을 경우, "수정" 모드에서의 값 설정이 정상적이지 않기에 갯수에 대한 제한을 해제
		for(InvSite site : siteList) {
			list.add(new DropDownListItem(site.getName(), String.valueOf(site.getId())));
		}
		
		Collections.sort(list, CustomComparator.DropDownListItemTextComparator);

    	return list;
    }
    
    
	/**
	 * 저장 액션 - 광역시/도
	 */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/saveState", method = RequestMethod.POST)
    public @ResponseBody String saveState(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
		ArrayList<Object> stateCodes = (ArrayList<Object>) model.get("stateCodes");
		ArrayList<Object> stateTexts = (ArrayList<Object>) model.get("stateTexts");
    	OrgPlaylist playlist = orgService.getPlaylist((int)model.get("playlist"));

    	// 파라미터 검증
    	if (playlist == null || stateCodes.size() == 0 || stateTexts.size() == 0) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	
    	int scrCnt = invService.getMonitScreenCountByMediumStateCodeIn(playlist.getMedium().getId(), 
    			Util.getStringList(stateCodes.toString()));
    	
    	OrgPlTarget target = orgService.getPlTarget((int)model.get("id"));
    	if (target == null) {
        	target = new OrgPlTarget(playlist, "CT", stateCodes.size(), stateCodes.toString(), 
        			stateTexts.toString(), scrCnt, 1000, session);
    	} else {
        	target.setTgtCount(stateCodes.size());
        	target.setTgtValue(stateCodes.toString());
        	target.setTgtDisplay(stateTexts.toString());
        	target.setTgtScrCount(scrCnt);
        	
        	target.touchWho(session);
    	}
    	
    	try {
    		orgService.saveAndReorderPlTarget(target);
    	} catch (Exception e) {
    		logger.error("saveState", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }

        return "Ok";
    }
    
    
	/**
	 * 저장 액션 - 시/군/구
	 */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/saveRegion", method = RequestMethod.POST)
    public @ResponseBody String saveRegion(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
		ArrayList<Object> regionCodes = (ArrayList<Object>) model.get("regionCodes");
		ArrayList<Object> regionTexts = (ArrayList<Object>) model.get("regionTexts");
    	OrgPlaylist playlist = orgService.getPlaylist((int)model.get("playlist"));

    	// 파라미터 검증
    	if (playlist == null || regionCodes.size() == 0 || regionTexts.size() == 0) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	
    	int scrCnt = invService.getMonitScreenCountByMediumRegionCodeIn(playlist.getMedium().getId(), 
    			Util.getStringList(regionCodes.toString()));
    	
    	OrgPlTarget target = orgService.getPlTarget((int)model.get("id"));
    	if (target == null) {
        	target = new OrgPlTarget(playlist, "RG", regionCodes.size(), regionCodes.toString(), 
        			regionTexts.toString(), scrCnt, 1000, session);
    	} else {
        	target.setTgtCount(regionCodes.size());
        	target.setTgtValue(regionCodes.toString());
        	target.setTgtDisplay(regionTexts.toString());
        	target.setTgtScrCount(scrCnt);
        	
        	target.touchWho(session);
    	}
    	
    	try {
    		orgService.saveAndReorderPlTarget(target);
    	} catch (Exception e) {
    		logger.error("saveRegion", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }

        return "Ok";
    }
    
    
	/**
	 * 저장 액션 - 매체 화면
	 */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/saveScreen", method = RequestMethod.POST)
    public @ResponseBody String saveScreen(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
		ArrayList<Object> screenIds = (ArrayList<Object>) model.get("screenIds");
		ArrayList<Object> screenTexts = (ArrayList<Object>) model.get("screenTexts");
		OrgPlaylist playlist = orgService.getPlaylist((int)model.get("playlist"));

    	// 파라미터 검증
    	if (playlist == null || screenIds.size() == 0 || screenTexts.size() == 0) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	
    	int scrCnt = invService.getMonitScreenCountByMediumScreenIdIn(playlist.getMedium().getId(), 
    			Util.getIntegerList(screenIds.toString()));
    	
    	OrgPlTarget target = orgService.getPlTarget((int)model.get("id"));
    	if (target == null) {
        	target = new OrgPlTarget(playlist, "SC", screenIds.size(), screenIds.toString(), 
        			screenTexts.toString(), scrCnt, 1000, session);
    	} else {
        	target.setTgtCount(screenIds.size());
        	target.setTgtValue(screenIds.toString());
        	target.setTgtDisplay(screenTexts.toString());
        	target.setTgtScrCount(scrCnt);
        	
        	target.touchWho(session);
    	}
    	
    	try {
    		orgService.saveAndReorderPlTarget(target);
    	} catch (Exception e) {
    		logger.error("saveScreen", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }

        return "Ok";
    }
    
    
	/**
	 * 저장 액션 - 사이트
	 */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/saveSite", method = RequestMethod.POST)
    public @ResponseBody String saveSite(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
		ArrayList<Object> siteIds = (ArrayList<Object>) model.get("siteIds");
		ArrayList<Object> siteTexts = (ArrayList<Object>) model.get("siteTexts");
		OrgPlaylist playlist = orgService.getPlaylist((int)model.get("playlist"));

    	// 파라미터 검증
    	if (playlist == null || siteIds.size() == 0 || siteTexts.size() == 0) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	
    	int scrCnt = invService.getMonitScreenCountByMediumSiteIdIn(playlist.getMedium().getId(), 
    			Util.getIntegerList(siteIds.toString()));
    	
    	OrgPlTarget target = orgService.getPlTarget((int)model.get("id"));
    	if (target == null) {
        	target = new OrgPlTarget(playlist, "ST", siteIds.size(), siteIds.toString(), 
        			siteTexts.toString(), scrCnt, 1000, session);
    	} else {
        	target.setTgtCount(siteIds.size());
        	target.setTgtValue(siteIds.toString());
        	target.setTgtDisplay(siteTexts.toString());
        	target.setTgtScrCount(scrCnt);
        	
        	target.touchWho(session);
    	}
    	
    	try {
    		orgService.saveAndReorderPlTarget(target);
    	} catch (Exception e) {
    		logger.error("saveSite", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }

        return "Ok";
    }

}
