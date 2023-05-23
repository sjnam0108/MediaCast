package net.doohad.controllers.adc;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Tuple;
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
import net.doohad.models.AdnMessageManager;
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

/**
 * 캠페인 컨트롤러(광고 목록)
 */
@Controller("adc-campaign-ad-controller")
@RequestMapping(value="")
public class AdcCampaignAdController {

	private static final Logger logger = LoggerFactory.getLogger(AdcCampaignAdController.class);

	
    @Autowired 
    private AdcService adcService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;

    
	/**
	 * 캠페인 컨트롤러(광고 목록)
	 */
    @RequestMapping(value = {"/adc/campaign/{campId}", "/adc/campaign/{campId}/", 
    		"/adc/campaign/ads/{campId}", "/adc/campaign/ads/{campId}/"}, method = RequestMethod.GET)
    public String index1(HttpServletRequest request, HttpServletResponse response, HttpSession session,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap,
    		Model model, Locale locale) {

    	AdcCampaign campaign = adcService.getCampaign(Util.parseInt(pathMap.get("campId")));
    	if (campaign == null || campaign.getMedium().getId() != Util.getSessionMediumId(session)) {
    		return "forward:/adc/campaign";
    	}

    	
    	// 캠페인의 상태카드 설정
    	SolUtil.setCampaignStatusCard(campaign);
		
		// 쿠키에 있는 "현재" 광고 정보 등을 확인하고, 최종적으로 session에 currAdId, currAds 이름으로 정보를 설정한다.
		SolUtil.saveCurrAdsToSession(request, response, session, campaign.getId(), -1);

		
    	modelMgr.addMainMenuModel(model, locale, session, request, "AdcAd");
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

    	// 페이지 제목
    	model.addAttribute("pageTitle", "캠페인");

    	model.addAttribute("Campaign", campaign);
    	
    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "adc/campaign/camp-ad";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/adc/campaign/ads/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, HttpSession session) {
    	
    	try {
    		DataSourceResult result = adcService.getAdList(request, (int)request.getReqIntValue1());
    		
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
}
