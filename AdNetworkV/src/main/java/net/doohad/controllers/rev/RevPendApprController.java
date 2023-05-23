package net.doohad.controllers.rev;

import java.util.ArrayList;
import java.util.Date;
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
import net.doohad.info.StringInfo;
import net.doohad.models.AdnMessageManager;
import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.adc.AdcCreatFile;
import net.doohad.models.adc.AdcCreative;
import net.doohad.models.knl.KnlUser;
import net.doohad.models.rev.RevCreatDecn;
import net.doohad.models.service.AdcService;
import net.doohad.models.service.KnlService;
import net.doohad.models.service.RevService;
import net.doohad.utils.Util;
import net.doohad.viewmodels.DropDownListItem;
import net.doohad.viewmodels.adc.AdcCreatFilePAItem;

/**
 * 승인 대기 컨트롤러
 */
@Controller("rev-pend-appr-controller")
@RequestMapping(value="/rev/pendappr")
public class RevPendApprController {

	private static final Logger logger = LoggerFactory.getLogger(RevMonitoringController.class);


    @Autowired 
    private AdcService adcService;

    @Autowired 
    private RevService revService;

    @Autowired 
    private KnlService knlService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 승인 대기 페이지
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
    	model.addAttribute("pageTitle", "승인 대기");
    	
    	
        return "rev/pendappr";
    }
    
    
	/**
	 * 읽기 액션 - 승인 대기
	 */
    @RequestMapping(value = "/readPAs", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readPAs(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		DataSourceResult result = adcService.getPendApprCreativeList(request);
    		
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
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("readPAs", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 승인/거절 이력
	 */
    @RequestMapping(value = "/readCreatDecns", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readCreatDecns(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		DataSourceResult result = revService.getCreatDecnList(request);
    		
    		for(Object obj : result.getData()) {
    			RevCreatDecn creatDecn = (RevCreatDecn) obj;
    			KnlUser actedByUser = knlService.getUser(creatDecn.getActedBy());
    			
    			creatDecn.setActedByShortName(actedByUser == null ? "-" : actedByUser.getShortName());
    		}
    			
    		return result;
    	} catch (Exception e) {
    		logger.error("readCreatDecns", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 승인 대기 - 광고 소재 파일
	 */
    @RequestMapping(value = "/readCreatFiles", method = RequestMethod.POST)
    public @ResponseBody List<AdcCreatFilePAItem> readCreatFiles(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		List<AdcCreatFile> list = adcService.getCreatFileListByCreativeId(request.getReqIntValue1());
    		
    		ArrayList<AdcCreatFilePAItem> retList = new ArrayList<AdcCreatFilePAItem>();
    		for(AdcCreatFile creatFile : list) {
    			retList.add(new AdcCreatFilePAItem(creatFile));
    		}
    		
    		return retList;
    	} catch (Exception e) {
    		logger.error("readCreatFiles", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }

    
    /**
	 * 승인 액션
	 */
    @RequestMapping(value = "/approve", method = RequestMethod.POST)
    public @ResponseBody String approve(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	// 파라미터 검증
    	if (objs.size() == 0) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}

    	// 승인 대기 페이지에서의 승인이기 때문에 현재 상태는 승인대기(P)만 가능
    	ArrayList<AdcCreative> creativeList = new ArrayList<AdcCreative>();
    	for (Object id : objs) {
    		AdcCreative creative = adcService.getCreative((int)id);
    		if (creative == null) {
    			throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    		} else if (!creative.getStatus().equals("P")) {
				throw new ServerOperationForbiddenException(StringInfo.UPD_ERROR_NOT_PROPER_STATUS);
    		}
    		
    		creativeList.add(creative);
    	}
    	
    	
    	for(AdcCreative creative : creativeList) {
    		
    		// 광고 소재 승인/거절 판단 추가
    		RevCreatDecn creatDecn = new RevCreatDecn(creative, session);
    		creatDecn.setStatus("A");
    		creatDecn.setActDate(new Date());
    		creatDecn.setActedBy(Util.loginUserId(session));
    		
    		revService.saveOrUpdate(creatDecn);
    		
    		// 광고 소재 상태 변경
			creative.setStatus("A");
			
			creative.touchWho(session);
			
        	adcService.saveOrUpdate(creative);
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
    	String reason = (String)model.get("reason");
    	
    	// 파라미터 검증
    	if (objs.size() == 0 || Util.isNotValid(reason)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}

    	// 승인 대기 페이지에서의 거절이기 때문에 현재 상태는 승인대기(P)만 가능
    	ArrayList<AdcCreative> creativeList = new ArrayList<AdcCreative>();
    	for (Object id : objs) {
    		AdcCreative creative = adcService.getCreative((int)id);
    		if (creative == null) {
    			throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    		} else if (!creative.getStatus().equals("P")) {
				throw new ServerOperationForbiddenException(StringInfo.UPD_ERROR_NOT_PROPER_STATUS);
    		}
    		
    		creativeList.add(creative);
    	}
    	
    	
    	for(AdcCreative creative : creativeList) {
    		
    		// 광고 소재 승인/거절 판단 추가
    		RevCreatDecn creatDecn = new RevCreatDecn(creative, session);
    		creatDecn.setStatus("J");
    		creatDecn.setActDate(new Date());
    		creatDecn.setActedBy(Util.loginUserId(session));
    		creatDecn.setReason(reason);
    		
    		revService.saveOrUpdate(creatDecn);
    		
    		// 광고 소재 상태 변경
			creative.setStatus("J");
			
			creative.touchWho(session);
			
        	adcService.saveOrUpdate(creative);
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
		list.add(new DropDownListItem("fa-regular fa-square-check text-blue fa-fw", "승인", "A"));
		list.add(new DropDownListItem("fa-regular fa-do-not-enter fa-fw", "거절", "J"));
		list.add(new DropDownListItem("fa-regular fa-box-archive fa-fw", "보관", "V"));
		
		return list;
    }

    
	/**
	 * 읽기 액션 - 상태 정보
	 */
    @RequestMapping(value = "/readActStatuses", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readActStatuses(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-square-check text-blue fa-fw", "승인", "A"));
		list.add(new DropDownListItem("fa-regular fa-do-not-enter fa-fw", "거절", "J"));
		
		return list;
    }
}
