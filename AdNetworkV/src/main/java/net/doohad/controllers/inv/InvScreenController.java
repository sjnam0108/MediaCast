package net.doohad.controllers.inv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceRequest.FilterDescriptor;
import net.doohad.models.DataSourceResult;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.fnd.FndRegion;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.inv.InvSite;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.service.FndService;
import net.doohad.models.service.InvService;
import net.doohad.models.service.KnlService;
import net.doohad.utils.Util;
import net.doohad.viewmodels.DropDownListItem;
import net.doohad.viewmodels.inv.InvSiteAutoCompleteItem;

/**
 * 화면 컨트롤러
 */
@Controller("inv-screen-controller")
@RequestMapping(value="/inv/screen")
public class InvScreenController {

	private static final Logger logger = LoggerFactory.getLogger(InvScreenController.class);


    @Autowired 
    private InvService invService;

    @Autowired 
    private KnlService knlService;

    @Autowired 
    private FndService fndService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 화면 페이지
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
    	model.addAttribute("pageTitle", "화면");

    	
    	int siteId = Util.parseInt(request.getParameter("siteid"));
    	if (siteId > 0) {
    		model.addAttribute("initFilterApplied", true);
    		model.addAttribute("siteId", siteId);
    	}

    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	model.addAttribute("Resolutions", getResolutionList(medium));
    	
    	// 매체의 재생 시간 정보 설정
    	int medium_defaultDurSecs = 15;
    	int medium_minDurSecs = 10;
    	int medium_maxDurSecs = 20;
    	boolean medium_rangeDurAllowed = false;
    	if (medium != null) {
    		medium_defaultDurSecs = medium.getDefaultDurSecs();
    		medium_rangeDurAllowed = medium.isRangeDurAllowed();
    		medium_minDurSecs = medium.getMinDurSecs();
    		medium_maxDurSecs = medium.getMaxDurSecs();
    	}
    	
    	model.addAttribute("medium_defaultDurSecs", medium_defaultDurSecs);
    	model.addAttribute("medium_rangeDurAllowed", medium_rangeDurAllowed);
    	model.addAttribute("medium_minDurSecs", medium_minDurSecs);
    	model.addAttribute("medium_maxDurSecs", medium_maxDurSecs);
    	
    	model.addAttribute("bizHour", medium.getBizHour());
    	
    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "inv/screen";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
        	//KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    		
    		DataSourceResult result = invService.getScreenList(request);
    		
    		/*
    		if (medium != null) {
        		for(Object obj : result.getData()) {
        			InvScreen screen = (InvScreen)obj;
        		}
    		}
    		*/
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
	
    
	/**
	 * 읽기 액션 - Kendo AutoComplete 용 SiteShortName 정보
	 */
    @RequestMapping(value = "/readACSiteShortName", method = RequestMethod.POST)
    public @ResponseBody List<InvSiteAutoCompleteItem> readAutoComplSiteShortName(@RequestBody DataSourceRequest request, 
    		HttpSession session) {
    	
    	int mediumId = Util.getSessionMediumId(session);
    	
		ArrayList<InvSiteAutoCompleteItem> list = new ArrayList<InvSiteAutoCompleteItem>();

		FilterDescriptor filter = request.getFilter();
		List<FilterDescriptor> filters = filter.getFilters();
		String userInput = "";
		if (filters.size() > 0) {
			userInput = Util.parseString((String) filters.get(0).getValue());
		}

		List<InvSite> siteList = invService.getSiteListByMediumIdShortNameLike(mediumId, userInput);
		
		if (siteList.size() <= 50) {
    		for(InvSite site : siteList) {
    			String regionName = "";
    			FndRegion region = fndService.getRegion(site.getRegionCode());
    			if (region != null) {
    				regionName = region.getName();
    			}
    			list.add(new InvSiteAutoCompleteItem(site, regionName));
    		}
    		
    		Collections.sort(list, new Comparator<InvSiteAutoCompleteItem>() {
    	    	public int compare(InvSiteAutoCompleteItem item1, InvSiteAutoCompleteItem item2) {
    	    		return item1.getShortName().compareTo(item2.getShortName());
    	    	}
    	    });
		}

    	return list;
    }
	
    
	/**
	 * 읽기 액션 - Kendo AutoComplete 용 SiteShortName 정보
	 */
    @RequestMapping(value = "/readACSiteName", method = RequestMethod.POST)
    public @ResponseBody List<InvSiteAutoCompleteItem> readAutoComplSiteName(@RequestBody DataSourceRequest request, 
    		HttpSession session) {
    	
    	int mediumId = Util.getSessionMediumId(session);
    	
		ArrayList<InvSiteAutoCompleteItem> list = new ArrayList<InvSiteAutoCompleteItem>();

		FilterDescriptor filter = request.getFilter();
		List<FilterDescriptor> filters = filter.getFilters();
		String userInput = "";
		if (filters.size() > 0) {
			userInput = Util.parseString((String) filters.get(0).getValue());
		}

		List<InvSite> siteList = invService.getSiteListByMediumIdNameLike(mediumId, userInput);
		
		if (siteList.size() <= 50) {
    		for(InvSite site : siteList) {
    			String regionName = "";
    			FndRegion region = fndService.getRegion(site.getRegionCode());
    			if (region != null) {
    				regionName = region.getName();
    			}
    			list.add(new InvSiteAutoCompleteItem(site, regionName));
    		}
    		
    		Collections.sort(list, new Comparator<InvSiteAutoCompleteItem>() {
    	    	public int compare(InvSiteAutoCompleteItem item1, InvSiteAutoCompleteItem item2) {
    	    		return item1.getName().compareTo(item2.getName());
    	    	}
    	    });
		}

    	return list;
    }
    
    
	/**
	 * 추가 액션
	 */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody String create(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String shortName = (String)model.get("shortName");
    	String name = (String)model.get("name");
    	String siteShortName = (String)model.get("siteShortName");
    	String siteName = (String)model.get("siteName");
    	String memo = (String)model.get("memo");
    	String resolution = (String)model.get("resolution");
    	
    	Date effectiveStartDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("effectiveStartDate")));
    	Date effectiveEndDate = Util.setMaxTimeOfDate(Util.parseZuluTime((String)model.get("effectiveEndDate")));
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	
    	Integer floorCpm = (Integer)model.get("cpm");
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(shortName) || Util.isNotValid(name) || effectiveStartDate == null ||
    			Util.isNotValid(siteShortName) || Util.isNotValid(siteName) || Util.isNotValid(resolution) ||
    			floorCpm == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	InvSite site = invService.getSite(medium, siteShortName);
    	if (site == null || !site.getName().equals(siteName)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}
    	
    	boolean durationOverridden = (Boolean)model.get("durationOverridden");
    	
    	Boolean rangeDurAllowed = (Boolean)model.get("rangeDurAllowed");
    	Integer defaultDurSecs = (Integer)model.get("defaultDurSecs");
    	Integer minDurSecs = (Integer)model.get("minDurSecs");
    	Integer maxDurSecs = (Integer)model.get("maxDurSecs");
    	
    	if (durationOverridden && (rangeDurAllowed == null || defaultDurSecs == null ||
    			minDurSecs == null || maxDurSecs == null)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	} else if (!durationOverridden) {
    		rangeDurAllowed = null;
    		defaultDurSecs = null;
    		minDurSecs = null;
    		maxDurSecs = null;
    	}
    	
    	boolean activeStatus = (Boolean)model.get("activeStatus");
    	
    	boolean adServerAvailable = (Boolean)model.get("adServerAvailable");
    	boolean imageAllowed = (Boolean)model.get("imageAllowed");
    	boolean videoAllowed = (Boolean)model.get("videoAllowed");
    	
    	
    	InvScreen target = new InvScreen(site, shortName, name, activeStatus, resolution, imageAllowed, videoAllowed, effectiveStartDate, effectiveEndDate, memo, session);
    	
    	target.setDefaultDurSecs(defaultDurSecs);
    	target.setRangeDurAllowed(rangeDurAllowed);
    	target.setMinDurSecs(minDurSecs);
    	target.setMaxDurSecs(maxDurSecs);
    	
    	target.setAdServerAvailable(adServerAvailable);
    	
    	target.setFloorCpm(floorCpm.intValue());
    	
    	
        saveOrUpdate(target, locale, session);
        
        
        // 화면 상태 및 수량 기준으로 사이트 정보 변경
        invService.updateSiteActiveStatusCountBasedScreens(site.getId());
        

        return "Ok";
    }
    
    
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String shortName = (String)model.get("shortName");
    	String name = (String)model.get("name");
    	String siteShortName = (String)model.get("siteShortName");
    	String siteName = (String)model.get("siteName");
    	String memo = (String)model.get("memo");
    	String resolution = (String)model.get("resolution");
    	
    	Date effectiveStartDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("effectiveStartDate")));
    	Date effectiveEndDate = Util.setMaxTimeOfDate(Util.parseZuluTime((String)model.get("effectiveEndDate")));
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	
    	Integer floorCpm = (Integer)model.get("cpm");
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(shortName) || Util.isNotValid(name) || effectiveStartDate == null ||
    			Util.isNotValid(siteShortName) || Util.isNotValid(siteName) || Util.isNotValid(resolution) ||
    			floorCpm == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	InvSite site = invService.getSite(medium, siteShortName);
    	if (site == null || !site.getName().equals(siteName)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}
    	
    	boolean durationOverridden = (Boolean)model.get("durationOverridden");
    	
    	Boolean rangeDurAllowed = (Boolean)model.get("rangeDurAllowed");
    	Integer defaultDurSecs = (Integer)model.get("defaultDurSecs");
    	Integer minDurSecs = (Integer)model.get("minDurSecs");
    	Integer maxDurSecs = (Integer)model.get("maxDurSecs");
    	
    	if (durationOverridden && (rangeDurAllowed == null || defaultDurSecs == null ||
    			minDurSecs == null || maxDurSecs == null)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	} else if (!durationOverridden) {
    		rangeDurAllowed = null;
    		defaultDurSecs = null;
    		minDurSecs = null;
    		maxDurSecs = null;
    	}
    	
    	boolean activeStatus = (Boolean)model.get("activeStatus");
    	
    	boolean adServerAvailable = (Boolean)model.get("adServerAvailable");
    	boolean imageAllowed = (Boolean)model.get("imageAllowed");
    	boolean videoAllowed = (Boolean)model.get("videoAllowed");

    	
    	InvScreen target = invService.getScreen((int)model.get("id"));
    	if (target != null) {
    		
    		int oldSiteId = target.getSite().getId();
    		target.setSite(site);
    		
    		target.setShortName(shortName);
            target.setName(name);
            target.setMemo(memo);
            target.setEffectiveStartDate(effectiveStartDate);
            target.setEffectiveEndDate(effectiveEndDate);
            
            target.setActiveStatus(activeStatus);
        	
        	target.setResolution(resolution);
        	
        	target.setDefaultDurSecs(defaultDurSecs);
        	target.setRangeDurAllowed(rangeDurAllowed);
        	target.setMinDurSecs(minDurSecs);
        	target.setMaxDurSecs(maxDurSecs);
        	
        	target.setVideoAllowed(videoAllowed);
        	target.setImageAllowed(imageAllowed);
        	
        	target.setAdServerAvailable(adServerAvailable);
        	
        	target.setFloorCpm(floorCpm.intValue());

            
            saveOrUpdate(target, locale, session);

            
            // 화면 상태 및 수량 기준으로 사이트 정보 변경
            invService.updateSiteActiveStatusCountBasedScreens(oldSiteId);
            if (oldSiteId != site.getId()) {
                invService.updateSiteActiveStatusCountBasedScreens(site.getId());
            }
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(InvScreen target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
    	// 비즈니스 로직 검증
        if (target.getEffectiveStartDate() != null && target.getEffectiveEndDate() != null
        		&& target.getEffectiveStartDate().after(target.getEffectiveEndDate())) {
        	throw new ServerOperationForbiddenException(StringInfo.CMN_NOT_BEFORE_EFF_END_DATE);
        }
        
        if (target.getDefaultDurSecs() != null && target.getMinDurSecs() != null && target.getMaxDurSecs() != null) {
            if (target.getMinDurSecs() > target.getDefaultDurSecs() || target.getMaxDurSecs() < target.getDefaultDurSecs()) {
            	throw new ServerOperationForbiddenException(StringInfo.VAL_NOT_BETWEEN_MIN_MAX_DUR);
            }
        }
        
        // DB 작업 수행 결과 검증
        try {
            invService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_SCREEN_ID_OR_NAME);
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_SCREEN_ID_OR_NAME);
        } catch (Exception e) {
    		logger.error("saveOrUpdate", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }
    }
    
    
	/**
	 * 화면 해상도 목록 획득
	 */
    private List<DropDownListItem> getResolutionList(KnlMedium medium) {

		ArrayList<DropDownListItem> retList = new ArrayList<DropDownListItem>();
		
		
		if (medium != null) {
			List<String> resolutions = Util.tokenizeValidStr(medium.getResolutions());
			for(String resolution : resolutions) {
				retList.add(new DropDownListItem(resolution.replace("x", " x ") , resolution));
			}
		}

		//Collections.sort(retList, CustomComparator.DropDownListItemTextComparator);
		
		return retList;
    }
    
    
	/**
	 * 읽기 액션 - 화면 해상도 정보
	 */
    @RequestMapping(value = "/readResolutions", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readResolutions(HttpSession session) {
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	return getResolutionList(medium);
    }

    
    /**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<InvScreen> screens = new ArrayList<InvScreen>();
    	
    	ArrayList<Integer> siteIds = new ArrayList<Integer>();

    	// 실제 화면의 사이트 정보가 필요하기 때문에 대상 화면을 직접 가져옴
    	for (Object id : objs) {
    		InvScreen screen = invService.getScreen((int)id);
    		if (screen != null) {
    			if (screen.getLastAdRequestDate() == null) {
        			screens.add(screen);
        			siteIds.add(screen.getSite().getId());
    			} else {
    				// 소프트 삭제 진행
    				invService.deleteSoftScreen(screen, session);
    				invService.updateSiteActiveStatusCountBasedScreens(screen.getSite().getId());
    			}
    		}
    	}
    	
    	try {
        	invService.deleteScreens(screens);
        	
        	for(Integer id : siteIds) {
        		invService.updateSiteActiveStatusCountBasedScreens(id.intValue());
        	}
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }
    
    
	/**
	 * 변경 액션 - 운영 시간
	 */
    @RequestMapping(value = "/updateTime", method = RequestMethod.POST)
    public @ResponseBody String updateBizTime(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	InvScreen target = invService.getScreen((int)model.get("id"));

    	String bizHour = (String)model.get("bizHour");
    	
    	// 파라미터 검증
    	if (target == null || Util.isNotValid(bizHour) || bizHour.length() != 168) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	if (target != null) {

    		target.setBizHour(bizHour.equals(target.getMedium().getBizHour()) ? "" : bizHour);
            
            target.touchWho(session);

            
            // DB 작업 수행 결과 검증
            try {
                invService.saveOrUpdate(target);
            } catch (Exception e) {
        		logger.error("updateBizTime", e);
            	throw new ServerOperationForbiddenException("SaveError");
            }
    	}
    	
        return "Ok";
    }
}
