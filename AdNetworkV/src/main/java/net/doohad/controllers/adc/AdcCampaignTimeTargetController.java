package net.doohad.controllers.adc;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.doohad.exceptions.ServerOperationForbiddenException;
import net.doohad.info.StringInfo;
import net.doohad.models.AdnMessageManager;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.adc.AdcAd;
import net.doohad.models.adc.AdcCampaign;
import net.doohad.models.service.AdcService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;

/**
 * 캠페인 컨트롤러(시간 타겟팅)
 */
@Controller("adc-campaign-time-target-controller")
@RequestMapping(value="/adc/campaign/timetarget")
public class AdcCampaignTimeTargetController {

	private static final Logger logger = LoggerFactory.getLogger(AdcCampaignTimeTargetController.class);

	
    @Autowired 
    private AdcService adcService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;

    
	/**
	 * 캠페인 컨트롤러(시간 타겟팅)
	 */
    @RequestMapping(value = {"/{campId}", "/{campId}/", "/{campId}/{adId}", "/{campId}/{adId}/"}, method = RequestMethod.GET)
    public String index1(HttpServletRequest request, HttpServletResponse response, HttpSession session,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap,
    		Model model, Locale locale) {

    	AdcCampaign campaign = adcService.getCampaign(Util.parseInt(pathMap.get("campId")));
    	if (campaign == null || campaign.getMedium().getId() != Util.getSessionMediumId(session)) {
    		return "forward:/adc/campaign";
    	}

    	// "현재" 광고 선택 변경의 경우
    	int adId = Util.parseInt(pathMap.get("adId"));
    	if (adId > 0) {
    		AdcAd ad = adcService.getAd(adId);
    		if (ad == null || ad.getCampaign().getId() != campaign.getId()) {
    			adId = -1;
    		}
    	}
    	
		
		// 캠페인의 상태카드 설정
    	SolUtil.setCampaignStatusCard(campaign);
		
		// 쿠키에 있는 "현재" 광고 정보 등을 확인하고, 최종적으로 session에 currAdId, currAds 이름으로 정보를 설정한다.
		int currAdId = SolUtil.saveCurrAdsToSession(request, response, session, campaign.getId(), adId);
    	AdcAd ad = adcService.getAd(currAdId);		// ad가 null일 수도 있음

		// 광고의 인벤 타겟팅 여부 및 상태카드 설정
    	SolUtil.setAdInvenTargeted(ad);
		SolUtil.setAdStatusCard(ad);
		SolUtil.setAdResolutions(ad);

    	
    	modelMgr.addMainMenuModel(model, locale, session, request, "AdcAd");
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

    	// 페이지 제목
    	model.addAttribute("pageTitle", "광고");

    	model.addAttribute("Campaign", campaign);
    	model.addAttribute("Ad", ad);

    	
        return "adc/campaign/camp-timetarget";
    }
    
    
	/**
	 * 저장 액션
	 */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public @ResponseBody String saveExpTime(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	AdcAd target = adcService.getAd((int)model.get("id"));

    	String expHour = (String)model.get("expHour");
    	
    	// 파라미터 검증
    	if (target == null || Util.isNotValid(expHour)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	if (target != null) {

    		target.setExpHour(expHour);

            
            target.touchWho(session);
            
            try {
                adcService.saveOrUpdate(target);
            } catch (Exception e) {
        		logger.error("saveExpTime", e);
            	throw new ServerOperationForbiddenException("SaveError");
            }
    	}
    	
        return "Ok";
    }
    
    
	/**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destory", method = RequestMethod.POST)
    public @ResponseBody String destoryExpTime(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	AdcAd target = adcService.getAd((int)model.get("id"));
    	
    	// 파라미터 검증
    	if (target == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	if (target != null) {

    		target.setExpHour("");

            
            target.touchWho(session);
            
            try {
                adcService.saveOrUpdate(target);
            } catch (Exception e) {
        		logger.error("destoryExpTime", e);
            	throw new ServerOperationForbiddenException("DeleteError");
            }
    	}
    	
        return "Ok";
    }

}
