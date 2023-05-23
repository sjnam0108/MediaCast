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
import net.doohad.models.LoginUser;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.adc.AdcCampaign;
import net.doohad.models.adc.AdcCreatFile;
import net.doohad.models.adc.AdcCreative;
import net.doohad.models.org.OrgAdvertiser;
import net.doohad.models.rev.RevObjTouch;
import net.doohad.models.service.AdcService;
import net.doohad.models.service.OrgService;
import net.doohad.models.service.RevService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.knl.KnlMediumItem;

/**
 * 광고주 컨트롤러(동일 광고주 소재 목록)
 */
@Controller("adc-creative-creative-controller")
@RequestMapping(value="")
public class AdcCreativeCreativeController {

	private static final Logger logger = LoggerFactory.getLogger(AdcCampaignAdController.class);

	
    @Autowired 
    private AdcService adcService;

    @Autowired 
    private OrgService orgService;

    @Autowired 
    private RevService revService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;

    
	/**
	 * 광고주 컨트롤러(동일 광고주 소재 목록)
	 */
    @RequestMapping(value = {"/adc/creative/{advId}", "/adc/creative/{advId}/", 
    		"/adc/creative/creatives/{advId}", "/adc/creative/creatives/{advId}/"}, method = RequestMethod.GET)
    public String index1(HttpServletRequest request, HttpServletResponse response, HttpSession session,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap,
    		Model model, Locale locale) {

    	OrgAdvertiser advertiser = orgService.getAdvertiser(Util.parseInt(pathMap.get("advId")));
    	if (advertiser == null || advertiser.getMedium().getId() != Util.getSessionMediumId(session)) {
    		return "forward:/adc/creative";
    	}

		
		List<AdcCampaign> campList = adcService.getCampaignLisyByAdvertiserId(advertiser.getId());
    	for(AdcCampaign campaign : campList) {
    		SolUtil.setCampaignStatusCard(campaign);
    	}
    	model.addAttribute("Camp01", (campList.size() > 0 ? Util.getObjectToJson(campList.get(0), false) : "null"));
    	model.addAttribute("Camp02", (campList.size() > 1 ? Util.getObjectToJson(campList.get(1), false) : "null"));
    	model.addAttribute("Camp03", (campList.size() > 2 ? Util.getObjectToJson(campList.get(2), false) : "null"));
    	
		// 쿠키에 있는 "현재" 광고 정보 등을 확인하고, 최종적으로 session에 currAdId, currAds 이름으로 정보를 설정한다.
		SolUtil.saveCurrCreativesToSession(request, response, session, advertiser.getId(), -1);


    	modelMgr.addMainMenuModel(model, locale, session, request, "AdcCreative");
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

    	// 페이지 제목
    	model.addAttribute("pageTitle", "광고주");

    	model.addAttribute("Advertiser", advertiser);
    	model.addAttribute("CreatCount", adcService.getCreativeCountByAdvertiserId(advertiser.getId()));
    	model.addAttribute("CreatFileCount", adcService.getCreatFileCountByAdvertiserId(advertiser.getId()));
    	
    	
    	ArrayList<KnlMediumItem> otherMedia = new ArrayList<KnlMediumItem>();
    	if (session != null) {
    		LoginUser loginUser = (LoginUser)session.getAttribute("loginUser");
    		int mediumId = Util.getSessionMediumId(session);
    		if (loginUser != null) {
    			List<KnlMediumItem> media = SolUtil.getAvailMediumListByUserId(loginUser.getId());
    			for(KnlMediumItem item : media) {
    				if (item.getId() == mediumId) {
    					continue;
    				}
    				otherMedia.add(item);
    			}
    		}
    	}
		model.addAttribute("otherMedia", otherMedia);
    	
    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "adc/creative/creat-creative";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/adc/creative/creatives/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, HttpSession session) {
    	
    	try {
    		DataSourceResult result = adcService.getCreativeList(request, (int)request.getReqIntValue1());
    		
    		// 하나라도 타겟팅이 존재하는 것만 기록
    		ArrayList<Integer> targetIds = new ArrayList<Integer>();
    		List<Tuple> countList = adcService.getCreatTargetCountGroupByMediumCreativeId(Util.getSessionMediumId(session));
    		for(Tuple tuple : countList) {
    			targetIds.add((Integer) tuple.get(0));
    		}
    		
    		
    		for(Object obj : result.getData()) {
    			AdcCreative creative = (AdcCreative)obj;
    			List<AdcCreatFile> fileList = adcService.getCreatFileListByCreativeId(creative.getId());
    			
    			String resolutions = "";
    			for(AdcCreatFile creatFile : fileList) {
        			
        			// 20% 범위로 적합도 판정
        			int fitness = adcService.measureResolutionWithMedium(
        					creatFile.getResolution(), creatFile.getMedium().getId(), 20);
    				
    				if (Util.isValid(resolutions)) {
    					resolutions += "|" + String.valueOf(fitness) + ":" + creatFile.getResolution();
    				} else {
    					resolutions = String.valueOf(fitness) + ":" + creatFile.getResolution();
    				}
    			}
    			
    			creative.setFileResolutions(resolutions);
    			
    			if (targetIds.contains(creative.getId())) {
    				creative.setInvenTargeted(true);
    			}
    			
    			RevObjTouch objTouch = revService.getObjTouch("C", creative.getId());
    			if (objTouch != null) {
    				creative.setLastPlayDate(objTouch.getDate1());
    			}
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
}
