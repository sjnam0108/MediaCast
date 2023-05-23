package net.doohad.controllers.adn;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Tuple;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import net.doohad.exceptions.ServerOperationForbiddenException;
import net.doohad.models.AdnMessageManager;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.UploadTransitionModel;
import net.doohad.models.adc.AdcAd;
import net.doohad.models.adc.AdcAdTarget;
import net.doohad.models.adc.AdcCreatTarget;
import net.doohad.models.adc.AdcCreative;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgPlTarget;
import net.doohad.models.org.OrgPlaylist;
import net.doohad.models.rev.RevAdSelect;
import net.doohad.models.rev.RevPlayHist;
import net.doohad.models.rev.RevScrStatusLine;
import net.doohad.models.service.AdcService;
import net.doohad.models.service.InvService;
import net.doohad.models.service.KnlService;
import net.doohad.models.service.OrgService;
import net.doohad.models.service.RevService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.inv.InvScreenInfoData;
import net.doohad.viewmodels.inv.InvSimpleAdSelect;
import net.doohad.viewmodels.inv.InvSiteMapLocItem;

/**
 * ADN 공통 컨트롤러
 */
@Controller("adn-common-controller")
@RequestMapping(value="/adn/common")
public class CommonController {
	
	private static final Logger logger = LoggerFactory.getLogger(CommonController.class);


    @Autowired 
    private KnlService knlService;

    @Autowired 
    private InvService invService;

    @Autowired 
    private RevService revService;

    @Autowired 
    private OrgService orgService;

    @Autowired 
    private AdcService adcService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;

    
    /**
     * 파일 다운로드 액션
     */
	@RequestMapping(value = "/download", method = RequestMethod.GET)
    public ModelAndView download(HttpServletRequest request,
    		HttpServletResponse response) {

    	String type = Util.parseString(request.getParameter("type"));
    	String file = Util.parseString(request.getParameter("file"));
    	//String code = Util.parseString(request.getParameter("code"));
    	
    	String prefix = Util.parseString(request.getParameter("prefix"));

    	File target = null;
    	
    	try {
    		if (Util.isValid(file) && Util.isValid(type)) {
    			if (type.equals("XlsTemplate")) {
    				target = new File(SolUtil.getPhysicalRoot(type) + "/" + file);
    			}
    		}
        	
        	if (target == null) {
        		throw new ServerOperationForbiddenException("OperationError");
        	}

            response.setContentType("application/octet-stream;charset=UTF-8");
            
            if (target.length() < Integer.MAX_VALUE) {
                response.setContentLength((int)target.length());
            }

            String userAgent = request.getHeader("User-Agent");
            
            String targetName = (Util.isValid(prefix) ? prefix + "_" : "") + target.getName();
        	if (userAgent.indexOf("MSIE 5.5") > -1) { // MS IE 5.5 이하
        	    response.setHeader("Content-Disposition", "filename=\""
        		    + URLEncoder.encode(targetName, "UTF-8") + "\";");
        	} else if (userAgent.indexOf("MSIE") > -1) { // MS IE (보통은 6.x 이상 가정)
        	    response.setHeader("Content-Disposition", "attachment; filename=\""
        		    + URLEncoder.encode(targetName, "UTF-8") + "\";");
        	} else {
        	    response.setHeader("Content-Disposition", "attachment; filename=\""
        		    + new String(targetName.getBytes("UTF-8"), "latin1") + "\";");
        	}
        	
            FileCopyUtils.copy(new FileInputStream(target), response.getOutputStream());
    	} catch (Exception e) {
			logger.error("download", e);
			throw new ServerOperationForbiddenException("OperationError");
    	}
 
        return null;
    }
	
	
	/**
	 * 모듈 전용 파일 업로드 페이지
	 */
    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String upload(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {

    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	String type = Util.parseString(request.getParameter("type"));
    	String code = Util.parseString(request.getParameter("code"));
    	
    	UploadTransitionModel uploadModel = new UploadTransitionModel();
    	
    	try {
        	if (medium != null && Util.isValid(type)) {
        		uploadModel.setMediumId(medium.getId());
        		uploadModel.setType(type);
        		uploadModel.setCode(code);
        		uploadModel.setSaveUrl("/adn/common/uploadsave");
        		
        		if (type.equals("MEDIA")) {
        			uploadModel.setAllowedExtensions("[\".jpg\", \".png\", \".mp4\"]");
        		}
        	}
    	} catch (Exception e) {
    		logger.error("upload", e);
    	}

    	model.addAttribute("uploadModel", uploadModel);
    	
        return "adn/modal/upload";
    }
	
    
	/**
	 * 화면 정보 모달
	 */
    @RequestMapping(value = "/screenInfo", method = {RequestMethod.GET, RequestMethod.POST })
    public String screenInfo(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	
    	int screenId = Util.parseInt(request.getParameter("id"));
    	Date date = Util.setMaxTimeOfDate(Util.parseDate(request.getParameter("date")));
    	String dateStr = date == null ? Util.toDateString(new Date()) : Util.toDateString(date);
    	

    	ArrayList<String> svcDateList = new ArrayList<String>();
    	List<RevScrStatusLine> statusList = revService.getScrStatusLineListByScreenId(screenId);
    	for(RevScrStatusLine statusLine : statusList) {
    		String dateNumber = String.format("%1$tQ", statusLine.getPlayDate());
			
			if (!svcDateList.contains(dateNumber)) {
				svcDateList.add(dateNumber);
			}
    	}
    	
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

    	model.addAttribute("dates", svcDateList);
    	
    	model.addAttribute("value_id", String.valueOf(screenId));
    	model.addAttribute("value_date", dateStr);
    	
    	model.addAttribute("markerList", getNearBySiteList(screenId, 10));
    	
        return "inv/modal/screenInfo";
    }
    
	/**
	 * 화면 정보 모달 - 모든 요약 정보 읽기 액션
	 */
    @RequestMapping(value = "/readScreenInfo", method = RequestMethod.POST)
    public @ResponseBody InvScreenInfoData readScreenInfoData(@RequestBody Map<String, Object> model,
    		Locale locale, HttpSession session) {

    	int screenId = (int)model.get("id");
    	Date playDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("date")));
    	if (playDate == null) {
    		playDate = Util.removeTimeOfDate(new Date());
    	}

    	
    	InvScreenInfoData ret = new InvScreenInfoData(
    			invService.getScreen(screenId), 
    			revService.getScrStatusLine(screenId, playDate),
    			playDate,
    			revService.getObjTouch("S", screenId));
    	
    	return ret;
    }
    
	/**
	 * 화면 정보 모달 - 화면 재생 기록 읽기 액션
	 */
    @RequestMapping(value = "/readScreenPlayHist", method = RequestMethod.POST)
    public @ResponseBody List<InvSimpleAdSelect> readScreenPlayHist(@RequestBody Map<String, Object> model,
    		Locale locale, HttpSession session) {

    	int screenId = (int)model.get("id");
    	
    	ArrayList<InvSimpleAdSelect> apiAdSelList = new ArrayList<InvSimpleAdSelect>();
    	
    	List<RevAdSelect> adSelList = revService.getLastAdSelectListByScreenId(screenId, 10);
    	for(RevAdSelect adSelect : adSelList) {
    		apiAdSelList.add(new InvSimpleAdSelect(adSelect));
    	}

    	List<RevPlayHist> playHistList = revService.getLastPlayHistListByScreenId(screenId, 10);
    	for(RevPlayHist playHist : playHistList) {
    		apiAdSelList.add(new InvSimpleAdSelect(playHist));
    	}

    	Collections.sort(apiAdSelList, new Comparator<InvSimpleAdSelect>() {
	    	public int compare(InvSimpleAdSelect item1, InvSimpleAdSelect item2) {
	    		return item2.getDate().compareTo(item1.getDate());
	    	}
		});
    	
    	return apiAdSelList.stream().limit(10).collect(Collectors.toList());
    }
    
	/**
	 * 화면 정보 모달 - 근처 사이트 마커 정보 획득
	 */
    private List<InvSiteMapLocItem> getNearBySiteList(int screenId, int cnt) {
    	
    	InvScreen screen = invService.getScreen(screenId);
    	if (screen == null) {
    		return new ArrayList<InvSiteMapLocItem>();
    	}
    	
    	return invService.getCloseSitesBy(screen.getSite(), cnt, true); 
    }
	
    
	/**
	 * 지리 위치 모달
	 */
    @RequestMapping(value = "/geoLoc", method = {RequestMethod.GET, RequestMethod.POST })
    public String geoLoc(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

    	int mediumId = Util.getSessionMediumId(session);
    	
    	String typeLvl = "";
    	String display = "";
    	String scrCnt = "";
    	String siteCnt = "";
    	
    	List<Integer> ids = new ArrayList<Integer>();
    	List<Integer> tmpIds = new ArrayList<Integer>();
    	
    	//
    	// 유형: type
    	//
    	//     PL - 재생 목록의 인벤타겟팅
    	//
    	String type = Util.parseString(request.getParameter("type"));
    	if (Util.isValid(type)) {
    		
    		String tgtValue = "";
    		
    		if (type.equals("PLINVEN")) {
    			OrgPlTarget tgt = orgService.getPlTarget(Util.parseInt(request.getParameter("id")));
    			if (tgt != null) {
    				typeLvl = tgt.getInvenType();
    				display = tgt.getTgtDisplay();
    				tgtValue = tgt.getTgtValue();
    			}
    		} else if (type.equals("CREATINVEN")) {
    			AdcCreatTarget tgt = adcService.getCreatTarget(Util.parseInt(request.getParameter("id")));
    			if (tgt != null) {
    				typeLvl = tgt.getInvenType();
    				display = tgt.getTgtDisplay();
    				tgtValue = tgt.getTgtValue();
    			}
    		} else if (type.equals("ADINVEN")) {
    			AdcAdTarget tgt = adcService.getAdTarget(Util.parseInt(request.getParameter("id")));
    			if (tgt != null) {
    				typeLvl = tgt.getInvenType();
    				display = tgt.getTgtDisplay();
    				tgtValue = tgt.getTgtValue();
    			}
    		} else if (type.equals("PL")) {
    			OrgPlaylist pl = orgService.getPlaylist(Util.parseInt(request.getParameter("id")));
    			if (pl != null) {
    				typeLvl = "PL";
    				display = pl.getName();
    				tgtValue = String.valueOf(pl.getId());
    				
    				scrCnt = "0";
    				siteCnt = "0";


    				List<Integer> currIds = new ArrayList<Integer>();
    				List<Integer> resultIds = invService.getMonitScreenIdsByMediumId(mediumId);
    				List<OrgPlaylist> playlistList = orgService.getEffPlaylistListByMediumId(mediumId);
    				
    				for(OrgPlaylist playlist : playlistList) {
    					currIds = invService.getTargetScreenOrAllIdsByPlaylistId(playlist.getId(), mediumId);
    					
    					currIds = Util.intersection(resultIds, currIds);
    					resultIds.removeAll(currIds);

    					if (playlist.getId() == pl.getId()) {
    						tmpIds = currIds;
    						break;
    					}
    					
    					if (resultIds.size() == 0) {
    						break;
    					}
    				}
    			}
    		} else if (type.equals("CREAT")) {
    			AdcCreative creat = adcService.getCreative(Util.parseInt(request.getParameter("id")));
    			if (creat != null) {
    				typeLvl = "CR";
    				display = creat.getName();
    				tgtValue = String.valueOf(creat.getId());
    				
    				scrCnt = "0";
    				siteCnt = "0";

    				tmpIds = invService.getTargetScreenIdsByCreativeId(creat.getId());
    			}
    		} else if (type.equals("AD")) {
    			AdcAd ad = adcService.getAd(Util.parseInt(request.getParameter("id")));
    			if (ad != null) {
    				typeLvl = "AD";
    				display = ad.getName();
    				tgtValue = String.valueOf(ad.getId());
    				
    				scrCnt = "0";
    				siteCnt = "0";

    				tmpIds = invService.getTargetScreenIdsByAdId(ad.getId());
    			}
    		}
    		
    		if (Util.isValid(tgtValue) && Util.isValid(typeLvl)) {
    			if (typeLvl.equals("CT")) {
    				
    		    	scrCnt = NumberFormat.getInstance().format(invService.getMonitScreenCountByMediumStateCodeIn(
    		    			mediumId, Util.getStringList(tgtValue)));
    		    	
    		    	siteCnt = NumberFormat.getInstance().format(invService.getMonitSiteCountByMediumStateCodeIn(
    		    			mediumId, Util.getStringList(tgtValue)));
    		    	
    		    	ids = invService.getMonitSiteIdsByMediumStateCodeIn(
    		    			mediumId, Util.getStringList(tgtValue));
    		    	
    			} else if (typeLvl.equals("RG")) {
    				
    		    	scrCnt = NumberFormat.getInstance().format(invService.getMonitScreenCountByMediumRegionCodeIn(
    		    			mediumId, Util.getStringList(tgtValue)));
    		    	
    		    	siteCnt = NumberFormat.getInstance().format(invService.getMonitSiteCountByMediumRegionCodeIn(
    		    			mediumId, Util.getStringList(tgtValue)));
    		    	
    		    	ids = invService.getMonitSiteIdsByMediumRegionCodeIn(
    		    			mediumId, Util.getStringList(tgtValue));
    		    	
    			} else if (typeLvl.equals("SC")) {
    				
    		    	scrCnt = NumberFormat.getInstance().format(invService.getMonitScreenCountByMediumScreenIdIn(
    		    			mediumId, Util.getIntegerList(tgtValue)));
    		    	
    		    	siteCnt = NumberFormat.getInstance().format(invService.getMonitSiteCountByMediumScreenIdIn(
    		    			mediumId, Util.getIntegerList(tgtValue)));
    		    	
    		    	ids = invService.getMonitSiteIdsByMediumScreenIdIn(
    		    			mediumId, Util.getIntegerList(tgtValue));
    		    	
    			} else if (typeLvl.equals("ST")) {
    				
    		    	scrCnt = NumberFormat.getInstance().format(invService.getMonitScreenCountByMediumSiteIdIn(
    		    			mediumId, Util.getIntegerList(tgtValue)));
    		    	
    		    	siteCnt = NumberFormat.getInstance().format(invService.getMonitSiteCountByMediumSiteIdIn(
    		    			mediumId, Util.getIntegerList(tgtValue)));
    		    	
    		    	ids = invService.getMonitSiteIdsByMediumSiteIdIn(
    		    			mediumId, Util.getIntegerList(tgtValue));
    		    	
    			} else if (typeLvl.equals("CD")) {
    				
    		    	scrCnt = NumberFormat.getInstance().format(invService.getMonitScreenCountByMediumSiteCondCodeIn(
    		    			mediumId, Util.getStringList(tgtValue)));
    		    	
    		    	siteCnt = NumberFormat.getInstance().format(invService.getMonitSiteCountByMediumSiteCondCodeIn(
    		    			mediumId, Util.getStringList(tgtValue)));
    		    	
    		    	ids = invService.getMonitSiteIdsByMediumSiteCondCodeIn(mediumId, Util.getStringList(tgtValue));
    		    	
    			} else if (typeLvl.equals("PL") || typeLvl.equals("CR") || typeLvl.equals("AD")) {
    				
    		    	scrCnt = NumberFormat.getInstance().format(invService.getMonitScreenCountByMediumScreenIdIn(
    		    			mediumId, tmpIds));
    		    	
    		    	siteCnt = NumberFormat.getInstance().format(invService.getMonitSiteCountByMediumScreenIdIn(
    		    			mediumId, tmpIds));
    		    	
    		    	ids = invService.getMonitSiteIdsByMediumScreenIdIn(mediumId, tmpIds);
    			}
    		}
    	}
    	
    	model.addAttribute("type", typeLvl);
    	model.addAttribute("display", display);
    	model.addAttribute("scrCnt", scrCnt);
    	model.addAttribute("siteCnt", siteCnt);

		model.addAttribute("markerUrl", SolUtil.getMarkerUrl(mediumId));
    	
    	ArrayList<InvSiteMapLocItem> mapItemList = new ArrayList<InvSiteMapLocItem>();
    	
    	List<Tuple> locList = invService.getSiteLocListBySiteIdIn(ids);
    	for(Tuple tuple : locList) {
    		double lat = (double)Util.parseFloat((String) tuple.get(1));
    		double lng = (double)Util.parseFloat((String) tuple.get(2));
    		
    		mapItemList.add(new InvSiteMapLocItem((String) tuple.get(0),
    				lat, lng));
    	}
    	
    	model.addAttribute("markerList", mapItemList);
    	
    	
        return "inv/modal/geoloc";
    }
}
