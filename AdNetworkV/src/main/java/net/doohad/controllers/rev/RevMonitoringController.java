package net.doohad.controllers.rev;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.doohad.exceptions.ServerOperationForbiddenException;
import net.doohad.info.GlobalInfo;
import net.doohad.models.AdnMessageManager;
import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.adc.AdcAd;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.rev.RevAdSelect;
import net.doohad.models.rev.RevObjTouch;
import net.doohad.models.rev.RevPlayHist;
import net.doohad.models.service.AdcService;
import net.doohad.models.service.InvService;
import net.doohad.models.service.KnlService;
import net.doohad.models.service.RevService;
import net.doohad.utils.Util;
import net.doohad.viewmodels.DropDownListItem;
import net.doohad.viewmodels.rev.RevApiLogItem;

/**
 * 모니터링 컨트롤러
 */
@Controller("rev-monitoring-controller")
@RequestMapping(value="/rev/monitoring")
public class RevMonitoringController {

	private static final Logger logger = LoggerFactory.getLogger(RevMonitoringController.class);


    @Autowired 
    private InvService invService;

    @Autowired 
    private KnlService knlService;

    @Autowired 
    private RevService revService;

    @Autowired 
    private AdcService adcService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 모니터링 페이지
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
    	model.addAttribute("pageTitle", "모니터링");

    	
    	model.addAttribute("screenID", Util.parseString((String)request.getParameter("screen"), ""));
    	model.addAttribute("apiTestServer", GlobalInfo.ApiTestServer);
    			
    	
        return "rev/monitoring";
    }
    
    
	/**
	 * 읽기 액션 - 활성 화면
	 */
    @RequestMapping(value = "/readScr", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readScr(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		DataSourceResult result = invService.getMonitScreenList(request);
    		
    		for(Object obj : result.getData()) {
    			InvScreen screen = (InvScreen) obj;
    			
    			RevObjTouch objTouch = revService.getObjTouch("S", screen.getId());
    			if (objTouch != null) {
    				screen.setLastFileApiDate(objTouch.getDate1());
    				screen.setLastAdRequestDate(objTouch.getDate2());
    				screen.setLastAdReportDate(objTouch.getDate3());
    				screen.setLastRetryReportDate(objTouch.getDate4());
    			}
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("readScr", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 광고 선택 / 보고 로그
	 */
    @RequestMapping(value = "/readApiLog", method = RequestMethod.POST)
    public @ResponseBody List<RevApiLogItem> readApiLog(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		String screenID = request.getReqStrValue1();
    		KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));

    		
    		ArrayList<RevApiLogItem> retList = new ArrayList<RevApiLogItem>();
    		
    		if (medium != null) {
        		InvScreen screen = invService.getScreen(medium, screenID);
        		if (screen != null) {
        			
        			List<AdcAd> adList = adcService.getAdListByMediumId(medium.getId());
        			HashMap<String, String> adPurchTypeMap = new HashMap<String, String>();
        			for(AdcAd ad : adList) {
        				adPurchTypeMap.put("A" + ad.getId(), ad.getPurchType());
        			}
        			
        			List<RevPlayHist> playHistList = revService.getPlayHistListByScreenId(screen.getId());
        			for(RevPlayHist playHist : playHistList) {
        				retList.add(new RevApiLogItem(playHist, adPurchTypeMap.get("A" + playHist.getAdId())));
        			}
        			
        			List<RevAdSelect> adSelectList = revService.getAdSelectListByScreenId(screen.getId());
        			for(RevAdSelect adSelect : adSelectList) {
        				retList.add(new RevApiLogItem(adSelect));
        			}
        		}
    		}

    		return retList;
    	} catch (Exception e) {
    		logger.error("readApiLog", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
    /**
	 * 화면의 최근 상태 재계산 액션
	 */
    @RequestMapping(value = "/recalcScr", method = RequestMethod.POST)
    public @ResponseBody String recalcStatus(@RequestBody Map<String, Object> model, 
    		HttpSession session, Locale locale) {

		String status = "0";

		try {
			// 화면 현재 상태 새로고침
			
			HashMap<String, RevObjTouch> map = new HashMap<String, RevObjTouch>();
			List<RevObjTouch> objList = revService.getObjTouchList();
			for(RevObjTouch objTouch : objList) {
				if (objTouch.getType().equals("S")) {
					map.put("S" + objTouch.getObjId(), objTouch);
				}
			}
			
			List<InvScreen> screenList = invService.getMonitScreenListByMediumId(Util.getSessionMediumId(session));
			for(InvScreen screen : screenList) {
				if (map.containsKey("S" + screen.getId())) {
					RevObjTouch objTouch = map.get("S" + screen.getId());
					if (objTouch != null) {
						status = objTouch.getScreenStatus();
						
						if (!screen.getLastStatus().equals(status)) {
							screen.setLastStatus(status);
							invService.saveOrUpdate(screen);
						}
					}
				}
			}
		} catch (Exception e) {
    		logger.error("recalcStatus", e);
    		throw new ServerOperationForbiddenException("OperationError");
		}

        return "OperationSuccess";
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
	 * 읽기 액션 - 상태 정보
	 */
    @RequestMapping(value = "/readStatuses", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readStatuses(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-solid fa-flag-swallowtail text-blue fa-fw", "10분내 요청", "6"));
		list.add(new DropDownListItem("fa-solid fa-flag-swallowtail text-green fa-fw", "1시간내 요청", "5"));
		list.add(new DropDownListItem("fa-solid fa-flag-swallowtail text-yellow fa-fw", "6시간내 요청", "4"));
		list.add(new DropDownListItem("fa-solid fa-flag-swallowtail text-orange fa-fw", "24시간내 요청", "3"));
		list.add(new DropDownListItem("fa-solid fa-flag-pennant text-danger fa-fw", "24시간내 없음", "1"));
		list.add(new DropDownListItem("fa-solid fa-flag-pennant text-secondary fa-fw", "기록 없음", "0"));
		
		return list;
    }
}
