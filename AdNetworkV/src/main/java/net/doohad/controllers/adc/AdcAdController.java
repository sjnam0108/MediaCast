package net.doohad.controllers.adc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Tuple;
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
import net.doohad.models.adc.AdcAd;
import net.doohad.models.adc.AdcCampaign;
import net.doohad.models.service.AdcService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.DropDownListItem;

/**
 * 광고 컨트롤러
 */
@Controller("adc-ad-controller")
@RequestMapping(value="/adc/ad")
public class AdcAdController {

	private static final Logger logger = LoggerFactory.getLogger(AdcCampaignController.class);


    @Autowired 
    private AdcService adcService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 광고 페이지
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
    	model.addAttribute("pageTitle", "광고");
    	
    	model.addAttribute("Campaigns", getCampaignDropDownListByMediumId(Util.getSessionMediumId(session)));

    	
    	int campaignId = Util.parseInt(request.getParameter("campaignid"));
    	if (campaignId > 0) {
    		model.addAttribute("initFilterApplied", true);
    		model.addAttribute("campaignId", campaignId);
    	}
    	
    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "adc/ad";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		DataSourceResult result = adcService.getAdList(request);
    		
    		// 하나라도 타겟팅이 존재하는 것만 기록
    		ArrayList<Integer> targetIds = new ArrayList<Integer>();
    		List<Tuple> countList = adcService.getAdTargetCountGroupByMediumAdId(Util.getSessionMediumId(session));
    		for(Tuple tuple : countList) {
    			targetIds.add((Integer) tuple.get(0));
    		}
    		
    		
    		for(Object obj : result.getData()) {
    			AdcAd ad = (AdcAd) obj;
    			
    			ad.setCreativeCount(adcService.getAdCreativeCountByAdId(ad.getId()));
    			
    			if (targetIds.contains(ad.getId())) {
    				ad.setInvenTargeted(true);
    			}
    			
    			// 광고의 상태카드 설정
    			SolUtil.setAdStatusCard(ad);
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 캠페인 목록 획득
	 */
    public List<DropDownListItem> getCampaignDropDownListByMediumId(int mediumId) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		List<AdcCampaign> campaignList = adcService.getCampaignListByMediumId(mediumId);
		for (AdcCampaign campaign : campaignList) {
			list.add(new DropDownListItem(campaign.getName(), String.valueOf(campaign.getId())));
		}

		Collections.sort(list, CustomComparator.DropDownListItemTextComparator);
		
		list.add(0, new DropDownListItem("", "-1"));
		
		return list;
    }
    
    
	/**
	 * 추가 액션
	 */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody String create(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	AdcCampaign campaign = adcService.getCampaign((int)model.get("campaign"));
    	
    	String name = (String)model.get("name");
    	String purchType = (String)model.get("purchType");
    	String goalType = (String)model.get("goalType");
    	String memo = (String)model.get("memo");
    	
    	int cpm = (int)model.get("cpm");
    	int freqCap = (int)model.get("freqCap");
    	int goalValue = (int)model.get("goalValue");
    	int dailyCap = (int)model.get("dailyCap");

    	Date startDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("startDate")));
    	Date endDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("endDate")));
    	
    	int priority = (int)model.get("priority");
    	int durSecs = (int)model.get("durSecs");

    	int budget = (int)model.get("budget");
    	int dailyScrCap = (int)model.get("dailyScrCap");
    	int sysValue = (int)model.get("sysValue");
    	
    	// 파라미터 검증
    	if (campaign == null || Util.isNotValid(name) || Util.isNotValid(purchType) || 
    			Util.isNotValid(goalType) || startDate == null || endDate == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	// 비즈니스 로직 검증
    	if (startDate.after(endDate)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_NOT_BEFORE_END_DATE);
    	} else if (durSecs < 0 || (durSecs > 0 && durSecs < 5)) {
    		throw new ServerOperationForbiddenException(StringInfo.VAL_WRONG_DUR);
    	}
    	
    	
    	AdcAd target = new AdcAd(campaign, name, purchType, startDate, endDate, session);

    	target.setMemo(memo);
    	target.setCpm(cpm);
    	
        target.setPriority(priority);
        target.setDuration(durSecs);
    	target.setFreqCap(freqCap);
    	
    	target.setGoalType(goalType);
    	target.setGoalValue(goalValue);
    	target.setDailyCap(dailyCap);
    	
    	target.setBudget(budget);
    	target.setDailyScrCap(dailyScrCap);
    	target.setSysValue(sysValue);

    	
        saveOrUpdate(target, locale, session);
        
        
        // 캠페인의 시작일/종료일/상태 재계산
        adcService.refreshCampaignInfoBasedAds(campaign.getId());

        return "Ok";
    }
    
    
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	AdcCampaign campaign = adcService.getCampaign((int)model.get("campaign"));
    	
    	String name = (String)model.get("name");
    	String purchType = (String)model.get("purchType");
    	String goalType = (String)model.get("goalType");
    	String memo = (String)model.get("memo");
    	
    	int cpm = (int)model.get("cpm");
    	int freqCap = (int)model.get("freqCap");
    	int goalValue = (int)model.get("goalValue");
    	int dailyCap = (int)model.get("dailyCap");
    	
    	Date startDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("startDate")));
    	Date endDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("endDate")));
    	
    	int priority = (int)model.get("priority");
    	int durSecs = (int)model.get("durSecs");

    	int budget = (int)model.get("budget");
    	int dailyScrCap = (int)model.get("dailyScrCap");
    	int sysValue = (int)model.get("sysValue");

    	// 파라미터 검증
    	if (campaign == null || Util.isNotValid(name) || Util.isNotValid(purchType) || 
    			Util.isNotValid(goalType) || startDate == null || endDate == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	// 비즈니스 로직 검증
    	if (startDate.after(endDate)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_NOT_BEFORE_END_DATE);
    	} else if (durSecs < 0 || (durSecs > 0 && durSecs < 5)) {
    		throw new ServerOperationForbiddenException(StringInfo.VAL_WRONG_DUR);
    	}

    	
    	AdcAd target = adcService.getAd((int)model.get("id"));
    	if (target != null) {
    		
    		// 캠페인 변경 불가
    		//target.setCampaign(campaign);
    		
    		Date prevSDate = target.getStartDate();
    		Date prevEDate = target.getEndDate();
    		
            target.setName(name);
            target.setPurchType(purchType);
            target.setStartDate(startDate);
            target.setEndDate(endDate);
            target.setCpm(cpm);
            target.setMemo(memo);
            
            target.setPriority(priority);
            target.setDuration(durSecs);
            target.setFreqCap(freqCap);
        	
        	target.setGoalType(goalType);
        	target.setGoalValue(goalValue);
        	target.setDailyCap(dailyCap);
        	
        	target.setBudget(budget);
        	target.setDailyScrCap(dailyScrCap);
        	target.setSysValue(sysValue);
            
            // 현재 광고의 상태 확인
            if (target.getStatus().equals("A") || target.getStatus().equals("C") || 
            		target.getStatus().equals("R")) {
            	
                Date today = Util.removeTimeOfDate(new Date());
                if (today.before(startDate)) {
                	target.setStatus("A");
                } else if (today.after(endDate)) {
                	target.setStatus("C");
                } else {
                	target.setStatus("R");
                }
            }
            
            target.touchWho(session);
            
            saveOrUpdate(target, locale, session);
            
            
            // 캠페인의 시작일/종료일/상태 재계산
            adcService.refreshCampaignInfoBasedAds(campaign.getId());
            
            // 연결된 광고 소재의 시작일/종료일 점검
            adcService.refreshAdCreativePeriodByAdDates(target, prevSDate, prevEDate);
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(AdcAd target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {

        // DB 작업 수행 결과 검증
        try {
            adcService.saveOrUpdate(target);
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
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	try {
        	for (Object id : objs) {
        		AdcAd ad = adcService.getAd((int)id);
        		if (ad != null) {
        			// 소프트 삭제 진행
        			adcService.deleteSoftAd(ad, session);
                    
                    // 캠페인의 시작일/종료일/상태 재계산
                    adcService.refreshCampaignInfoBasedAds(ad.getCampaign().getId());
        		}
        	}
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }

    
	/**
	 * 읽기 액션 - 상태 정보
	 */
    @RequestMapping(value = "/readStatuses", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readStatuses(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-asterisk fa-fw", "준비", "D"));
		list.add(new DropDownListItem("fa-regular fa-square-question fa-fw", "승인대기", "P"));
		list.add(new DropDownListItem("fa-regular fa-do-not-enter fa-fw", "거절", "J"));
		list.add(new DropDownListItem("fa-regular fa-alarm-clock fa-fw", "예약", "A"));
		list.add(new DropDownListItem("fa-regular fa-bolt-lightning text-orange fa-fw", "진행", "R"));
		list.add(new DropDownListItem("fa-regular fa-flag-checkered fa-fw", "완료", "C"));
		list.add(new DropDownListItem("fa-regular fa-box-archive fa-fw", "보관", "V"));
		
		return list;
    }

    
	/**
	 * 읽기 액션 - 구매 유형 정보
	 */
    @RequestMapping(value = "/readPurchTypes", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readPurchTypes(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-hexagon-check text-blue fa-fw", "목표 보장", "G"));
		list.add(new DropDownListItem("fa-regular fa-hexagon-exclamation fa-fw", "목표 비보장", "N"));
		list.add(new DropDownListItem("fa-regular fa-house fa-fw", "하우스 광고", "H"));
		
		return list;
    }

    
	/**
	 * 읽기 액션 - 집행 방법 정보
	 */
    @RequestMapping(value = "/readGoalTypes", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readGoalTypes(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-sack-dollar fa-fw", "광고 예산", "A"));
		list.add(new DropDownListItem("fa-regular fa-eye fa-fw", "노출횟수", "I"));
		list.add(new DropDownListItem("fa-regular fa-eye fa-fw", "화면당 1일 노출(삭제예정)", "J"));
		list.add(new DropDownListItem("fa-regular fa-infinity fa-fw", "무제한 노출", "U"));
		
		return list;
    }

    
    /**
	 * 승인 액션
	 */
    @RequestMapping(value = "/approve", method = RequestMethod.POST)
    public @ResponseBody String approve(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
		// 단일 항목 액션만 가능하도록
    	for (Object id : objs) {
    		AdcAd ad = adcService.getAd((int)id);
    		if (ad != null) {
    			// 승인 처리는 준비(D), 승인대기(P), 거절(J), 보관(V)만 가능
    			if (ad.getStatus().equals("D") || ad.getStatus().equals("P") || ad.getStatus().equals("J") || 
    					ad.getStatus().equals("V")) {
    				// 오늘 날짜 기준으로 예약A/진행R/완료C 처리
    				String status = "A";
    				Date today = Util.removeTimeOfDate(new Date());
    				if (today.before(ad.getStartDate())) {
    					// "A"
    				} else if (today.after(ad.getEndDate())) {
    					status = "C";
    				} else {
    					status = "R";
    				}
    				
    				ad.setStatus(status);
    				ad.touchWho(session);
    				
                	adcService.saveOrUpdate(ad);
    			} else {
    				throw new ServerOperationForbiddenException(StringInfo.UPD_ERROR_NOT_PROPER_STATUS);
    			}
    		}
    	}

        return "Ok";
    }

    
    /**
	 * 거절 액션
	 */
    @RequestMapping(value = "/reject", method = RequestMethod.POST)
    public @ResponseBody String reject(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
		// 단일 항목 액션만 가능하도록
    	for (Object id : objs) {
    		AdcAd ad = adcService.getAd((int)id);
    		if (ad != null) {
    			// 거절 처리는 승인대기(P), 예약(A), 진행(R), 완료(C)만 가능
    			if (ad.getStatus().equals("P") || ad.getStatus().equals("A") || ad.getStatus().equals("R") || 
    					ad.getStatus().equals("C")) {
    				
    				ad.setStatus("J");
    				ad.touchWho(session);
    				
                	adcService.saveOrUpdate(ad);
    			} else {
    				throw new ServerOperationForbiddenException(StringInfo.UPD_ERROR_NOT_PROPER_STATUS);
    			}
    		}
    	}

        return "Ok";
    }

    
    /**
	 * 보관 액션
	 */
    @RequestMapping(value = "/archive", method = RequestMethod.POST)
    public @ResponseBody String archive(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	for (Object id : objs) {
    		AdcAd ad = adcService.getAd((int)id);
    		if (ad != null) {
    			// 보관 처리는 준비(D), 승인대기(P), 승인(A), 거절(J)만 가능
    			if (ad.getStatus().equals("D") || ad.getStatus().equals("P") ||
    					ad.getStatus().equals("A") || ad.getStatus().equals("R") ||
    					ad.getStatus().equals("C") || ad.getStatus().equals("J")) {
    				
    				ad.setStatus("V");
    				
    				ad.touchWho(session);
    				
                	adcService.saveOrUpdate(ad);
    			} else {
    				throw new ServerOperationForbiddenException(StringInfo.UPD_ERROR_NOT_PROPER_STATUS);
    			}
    		}
    	}

        return "Ok";
    }

    
    /**
	 * 보관 해제 액션
	 */
    @RequestMapping(value = "/unarchive", method = RequestMethod.POST)
    public @ResponseBody String unarchive(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	for (Object id : objs) {
    		AdcAd ad = adcService.getAd((int)id);
    		if (ad != null) {
    			// 보관 해제 처리는 보관(V)만 가능
    			if (ad.getStatus().equals("V")) {
    				
    				ad.setStatus("D");
    				
    				ad.touchWho(session);
    				
                	adcService.saveOrUpdate(ad);
    			} else {
    				throw new ServerOperationForbiddenException(StringInfo.UPD_ERROR_NOT_PROPER_STATUS);
    			}
    		}
    	}

        return "Ok";
    }

    
    /**
	 * 잠시 멈춤 액션
	 */
    @RequestMapping(value = "/pause", method = RequestMethod.POST)
    public @ResponseBody String pause(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	for (Object id : objs) {
    		AdcAd ad = adcService.getAd((int)id);
    		if (ad != null) {
    			// 잠시 멈춤 처리는 잠시 멈춤이 아닌 항목만 가능
    			if (!ad.isPaused()) {
    				
    				ad.setPaused(true);
    				
    				ad.touchWho(session);
    				
                	adcService.saveOrUpdate(ad);
    			} else {
    				throw new ServerOperationForbiddenException(StringInfo.UPD_ERROR_NOT_PROPER_STATUS);
    			}
    		}
    	}

        return "Ok";
    }

    
    /**
	 * 재개 액션
	 */
    @RequestMapping(value = "/resume", method = RequestMethod.POST)
    public @ResponseBody String resume(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	for (Object id : objs) {
    		AdcAd ad = adcService.getAd((int)id);
    		if (ad != null) {
    			// 재개 처리는 잠시 멈춤인 항목만 가능
    			if (ad.isPaused()) {
    				
    				ad.setPaused(false);
    				
    				ad.touchWho(session);
    				
                	adcService.saveOrUpdate(ad);
    			} else {
    				throw new ServerOperationForbiddenException(StringInfo.UPD_ERROR_NOT_PROPER_STATUS);
    			}
    		}
    	}

        return "Ok";
    }

}
