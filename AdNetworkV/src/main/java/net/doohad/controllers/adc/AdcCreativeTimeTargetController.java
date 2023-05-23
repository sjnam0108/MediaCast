package net.doohad.controllers.adc;

import java.util.List;
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
import net.doohad.models.adc.AdcCampaign;
import net.doohad.models.adc.AdcCreative;
import net.doohad.models.org.OrgAdvertiser;
import net.doohad.models.service.AdcService;
import net.doohad.models.service.OrgService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;

/**
 * 광고 소재 컨트롤러(시간 타겟팅)
 */
@Controller("adc-creative-time-target-controller")
@RequestMapping(value="/adc/creative/timetarget")
public class AdcCreativeTimeTargetController {

	private static final Logger logger = LoggerFactory.getLogger(AdcCreativeTimeTargetController.class);

	
    @Autowired 
    private AdcService adcService;

    @Autowired 
    private OrgService orgService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;

    
	/**
	 * 광고 소재 컨트롤러(시간 타겟팅)
	 */
    @RequestMapping(value = {"/{advId}", "/{advId}/", "/{advId}/{creatId}", "/{advId}/{creatId}/"}, method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response, HttpSession session,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap,
    		Model model, Locale locale) {

    	OrgAdvertiser advertiser = orgService.getAdvertiser(Util.parseInt(pathMap.get("advId")));
    	if (advertiser == null || advertiser.getMedium().getId() != Util.getSessionMediumId(session)) {
    		return "forward:/adc/creative";
    	}

    	// "현재" 광고 소재 선택 변경의 경우
    	int creatId = Util.parseInt(pathMap.get("creatId"));
    	if (creatId > 0) {
    		AdcCreative creative = adcService.getCreative(creatId);
    		if (creative == null || creative.getAdvertiser().getId() != advertiser.getId()) {
    			creatId = -1;
    		}
    	}
    	
		
		List<AdcCampaign> campList = adcService.getCampaignLisyByAdvertiserId(advertiser.getId());
    	for(AdcCampaign campaign : campList) {
    		SolUtil.setCampaignStatusCard(campaign);
    	}
    	model.addAttribute("Camp01", (campList.size() > 0 ? Util.getObjectToJson(campList.get(0), false) : "null"));
    	model.addAttribute("Camp02", (campList.size() > 1 ? Util.getObjectToJson(campList.get(1), false) : "null"));
    	model.addAttribute("Camp03", (campList.size() > 2 ? Util.getObjectToJson(campList.get(2), false) : "null"));
    	
		// 쿠키에 있는 "현재" 광고 소재 정보 등을 확인하고, 최종적으로 session에 currCreatId, currCreatives 이름으로 정보를 설정한다.
		int currCreatId = SolUtil.saveCurrCreativesToSession(request, response, session, advertiser.getId(), creatId);
		AdcCreative creative = adcService.getCreative(currCreatId);

		// 광고 소재의 인벤 타겟팅 여부 설정
    	SolUtil.setCreativeInvenTargeted(creative);
    	SolUtil.setCreativeResolutions(creative);
		

    	modelMgr.addMainMenuModel(model, locale, session, request, "AdcCreative");
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

    	// 페이지 제목
    	model.addAttribute("pageTitle", "광고 소재");

    	model.addAttribute("Advertiser", advertiser);
    	model.addAttribute("Creative", creative);
    	model.addAttribute("CreatCount", adcService.getCreativeCountByAdvertiserId(advertiser.getId()));
    	model.addAttribute("CreatFileCount", adcService.getCreatFileCountByAdvertiserId(advertiser.getId()));

    	
        return "adc/creative/creat-timetarget";
    }
    
    
	/**
	 * 저장 액션
	 */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public @ResponseBody String saveExpTime(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	AdcCreative target = adcService.getCreative((int)model.get("id"));

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
    	
    	AdcCreative target = adcService.getCreative((int)model.get("id"));
    	
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
