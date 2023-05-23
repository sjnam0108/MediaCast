package net.doohad.controllers.adc;

import java.util.ArrayList;
import java.util.Collections;
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
import net.doohad.models.CustomComparator;
import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.adc.AdcAd;
import net.doohad.models.adc.AdcCampaign;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgAdvertiser;
import net.doohad.models.service.AdcService;
import net.doohad.models.service.KnlService;
import net.doohad.models.service.OrgService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.DropDownListItem;

/**
 * 캠페인 컨트롤러
 */
@Controller("adc-campaign-controller")
@RequestMapping(value="/adc/campaign")
public class AdcCampaignController {

	private static final Logger logger = LoggerFactory.getLogger(AdcCampaignController.class);


    @Autowired 
    private AdcService adcService;

    @Autowired 
    private OrgService orgService;

    @Autowired 
    private KnlService knlService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 캠페인 페이지
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
    	model.addAttribute("pageTitle", "캠페인");

    	model.addAttribute("Advertisers", getAdvertiserListByMediumId(Util.getSessionMediumId(session)));

    	
    	int campaignId = Util.parseInt(request.getParameter("campaignid"));
    	if (campaignId > 0) {
    		model.addAttribute("initFilterApplied", true);
    		model.addAttribute("campaignId", campaignId);
    	}
    	
    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "adc/campaign";
    }
    
    
	/**
	 * 광고주 목록 획득
	 */
    public List<DropDownListItem> getAdvertiserListByMediumId(int mediumId) {
    	
		ArrayList<DropDownListItem> retList = new ArrayList<DropDownListItem>();

		List<OrgAdvertiser> list = orgService.getAdvertiserListByMediumId(mediumId);
		
		retList.add(new DropDownListItem("", String.valueOf(-1)));
		
		for(OrgAdvertiser advertiser : list) {
			retList.add(new DropDownListItem(advertiser.getName(), 
					String.valueOf(advertiser.getId())));
		}
		
		Collections.sort(retList, CustomComparator.DropDownListItemTextComparator);
    	
    	return retList;
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request) {
    	try {
    		DataSourceResult result = adcService.getCampaignList(request);
    		
    		for(Object obj : result.getData()) {
    			AdcCampaign campaign = (AdcCampaign) obj;
    			
    			campaign.setAdCount(adcService.getAdCountByCampaignId(campaign.getId()));
    			
    			// 캠페인의 상태카드 설정
    	    	SolUtil.setCampaignStatusCard(campaign);
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 추가 액션 - 캠페인
	 */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody AdcCampaign create(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String name = (String)model.get("name");
    	String memo = (String)model.get("memo");
    	
    	int freqCap = (int)model.get("freqCap");
    	
    	OrgAdvertiser advertiser = orgService.getAdvertiser((int)model.get("advertiser"));
    	
    	// 파라미터 검증
    	if (advertiser == null || Util.isNotValid(name) || freqCap < 0) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	AdcCampaign target = new AdcCampaign(advertiser, name, freqCap, memo, session);
    	
        saveOrUpdate(target, locale, session);

        return target;
    }
    
    
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String name = (String)model.get("name");
    	String memo = (String)model.get("memo");
    	
    	int freqCap = (int)model.get("freqCap");
    	
    	// 광고주는 수정 불가
    	//OrgAdvertiser advertiser = orgService.getAdvertiser((int)model.get("advertiser"));
    	
    	// 파라미터 검증
    	if (Util.isNotValid(name) || freqCap < 0) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	AdcCampaign target = adcService.getCampaign((int)model.get("id"));
    	if (target != null) {
    		
            target.setName(name);
            target.setMemo(memo);
            target.setFreqCap(freqCap);
            
            target.touchWho(session);
            
            saveOrUpdate(target, locale, session);
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(AdcCampaign target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
    	// 비즈니스 로직 검증
        
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
        		AdcCampaign campaign = adcService.getCampaign((int)id);
        		if (campaign != null) {
        			// 소프트 삭제 진행
        			adcService.deleteSoftCampaign(campaign, session);
        			
        			// 광고도 함께 소프트 삭제 진행
        			List<AdcAd> ads = adcService.getAdListByCampaignId(campaign.getId());
        			for(AdcAd ad : ads) {
        				adcService.deleteSoftAd(ad, session);
        			}
        		}
        	}
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }
    
    
	/**
	 * 추가 액션 - 광고
	 */
    @RequestMapping(value = "/createAd", method = RequestMethod.POST)
    public @ResponseBody String createAd(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	OrgAdvertiser advertiser = orgService.getAdvertiser((int)model.get("advertiser"));
    	
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
    	if (advertiser == null || Util.isNotValid(name) || Util.isNotValid(purchType) || 
    			Util.isNotValid(goalType) || startDate == null || endDate == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	// 비즈니스 로직 검증
    	if (startDate.after(endDate)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_NOT_BEFORE_END_DATE);
    	} else if (durSecs < 0 || (durSecs > 0 && durSecs < 5)) {
    		throw new ServerOperationForbiddenException(StringInfo.VAL_WRONG_DUR);
    	}
        

    	String msgError = "동일한 이름의 광고 자료가 이미 등록되어 있습니다.";
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	
    	AdcCampaign prevCamp = adcService.getCampaign(medium, name);
    	if (prevCamp != null) {
    		throw new ServerOperationForbiddenException("동일한 이름의 캠페인 자료가 이미 등록되어 있습니다.");
    	}
    	AdcAd prevAd = adcService.getAd(medium, name);
    	if (prevAd != null) {
    		throw new ServerOperationForbiddenException(msgError);
    	}
    	
    	AdcCampaign target = new AdcCampaign(advertiser, name, 0, "", session);
    	saveOrUpdate(target, locale, session);

    	
    	AdcAd ad = new AdcAd(target, name, purchType, startDate, endDate, session);

    	ad.setCpm(cpm);
    	
        ad.setPriority(priority);
        ad.setDuration(durSecs);
        ad.setFreqCap(freqCap);
    	
        ad.setGoalType(goalType);
        ad.setGoalValue(goalValue);
        ad.setDailyCap(dailyCap);
    	
        ad.setBudget(budget);
        ad.setDailyScrCap(dailyScrCap);
        ad.setSysValue(sysValue);
        
        ad.setMemo(memo);

        
        // DB 작업 수행 결과 검증
        try {
            adcService.saveOrUpdate(ad);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException(msgError);
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException(msgError);
        } catch (Exception e) {
    		logger.error("saveOrUpdate", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }
        
        
        // 캠페인의 시작일/종료일/상태 재계산
        adcService.refreshCampaignInfoBasedAds(target.getId());
    	
        return "Ok";
    }
    
    
	/**
	 * 읽기 액션 - 상태 정보
	 */
    @RequestMapping(value = "/readStatuses", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readStatuses(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-alarm-clock fa-fw", "시작전", "U"));
		list.add(new DropDownListItem("fa-regular fa-bolt-lightning fa-fw text-orange", "진행", "R"));
		list.add(new DropDownListItem("fa-regular fa-flag-checkered fa-fw", "완료", "C"));
		list.add(new DropDownListItem("fa-regular fa-box-archive fa-fw", "보관", "V"));
		
		return list;
    }

}
