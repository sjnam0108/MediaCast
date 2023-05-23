package net.doohad.controllers.api;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import net.doohad.info.GlobalInfo;
import net.doohad.models.adc.AdcAdCreative;
import net.doohad.models.adc.AdcCreatFile;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.rev.RevAdSelCache;
import net.doohad.models.rev.RevAdSelect;
import net.doohad.models.rev.RevPlayHist;
import net.doohad.models.rev.RevScrHourlyPlay;
import net.doohad.models.service.InvService;
import net.doohad.models.service.RevService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.rev.RevObjEventTimeItem;
import net.doohad.viewmodels.rev.RevScrWorkTimeItem;
import net.sf.json.JSONObject;

/**
 * 누락 결과 보고 API 컨트롤러
 */
@Controller("api-retry-report-controller")
@RequestMapping(value="")
public class RetryReportController {
	
	private static final Logger logger = LoggerFactory.getLogger(RetryReportController.class);


    @Autowired 
    private RevService revService;

    @Autowired 
    private InvService invService;

    
    /**
	 * 누락 결과 보고 API
	 */
    @RequestMapping(value = {"/v1/report/{displayID}/{adID}"}, method = RequestMethod.GET)
    public void processApiRetryReport(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap) {
    	
    	String displayID = Util.parseString(pathMap.get("displayID"));
    	String adID = Util.parseString(pathMap.get("adID"));
    
    	String apiKey = Util.parseString(paramMap.get("apikey"));
    	long start = Util.parseLong(paramMap.get("start"), -1);
    	long end = Util.parseLong(paramMap.get("end"), -1);
    	
    	String test = Util.parseString(paramMap.get("test"));
    	boolean testMode = Util.isValid(test) && test.toLowerCase().equals("y");
    	
    	
    	int statusCode = 0;
    	String message = "Ok";
    	String localMessage = "Ok";
    	
    	JSONObject obj = new JSONObject();
    	InvScreen screen = null;
    	
    	AdcAdCreative adCreative = null;
    	
    	Date startDt = null;
    	Date endDt = null;
    	Integer dur = null;
    	
    	
    	if (Util.isNotValid(apiKey) || Util.isNotValid(displayID) || Util.isNotValid(adID) || start < 1 || end < 1) {
    		
    		statusCode = -3;
    		message = "WrongParams";
    		localMessage = "필수 인자의 값이 전달되지 않았습니다.";
    	} else {
    		
        	//KnlMedium medium = knlService.getMediumByApiKey(apiKey);
        	KnlMedium medium = SolUtil.getMediumByApiKey(apiKey);
        	if (medium == null) {
        		statusCode = -1;
        		message = "WrongApiKey";
        		localMessage = "등록되지 않은 API key가 전달되었습니다.";
        	} else {
        		screen = invService.getScreen(medium, displayID);
        		if (screen == null) {
            		statusCode = -2;
            		message = "WrongDisplayID";
            		localMessage = "등록되지 않은 디스플레이 ID가 전달되었습니다.";
        		}
        	}
    	}
    	
    	if (statusCode >= 0 && screen != null) {
    		// 아직까지는 큰 문제 없음
    		
    		if (!SolUtil.isEffectiveDates(screen.getEffectiveStartDate(), screen.getEffectiveEndDate())) {
    			statusCode = -4;
    			message = "EffectiveDateExpired";
    			localMessage = "유효 기간의 범위에 포함되지 않습니다.";
    		} else if (screen.isActiveStatus() != true) {
    			statusCode = -5;
    			message = "NotActive";
    			localMessage = "정상 서비스 중이 아닙니다.";
    		} else if (screen.isAdServerAvailable() != true) {
    			statusCode = -6;
    			message = "NotAdServerAvailable";
    			localMessage = "광고 서비스로 이용할 수 없습니다.";
    		} else {
    	    	if (start > 0) {
    	    		startDt = new Date(start);
    	    	}
    	    	if (end > 0) {
    	    		endDt = new Date(end);
    	    	}
    	    	dur = (int)(end - start);

    	    	// 시작일시가 현재 -7일 이내여야 함
    	    	if (dur < 1000 || dur > 3600000 || start > end || Util.addDays(new Date(), -7).after(startDt)) {
    	    		startDt = null;
    	    		endDt = null;
    	    		dur = null;
    	    		
        			statusCode = -14;
        			message = "InvalidTime";
        			localMessage = "시간 정보가 유효하지 않습니다.";
    	    	} else if (revService.getPlayHistCountByScreenIdStartDate(screen.getId(), startDt) > 0) {
    	    		startDt = null;
    	    		endDt = null;
    	    		dur = null;
    	    		
        			statusCode = -13;
        			message = "TimeCollision";
        			localMessage = "등록 시도한 시간과 충돌하는 자료가 존재합니다.";
    	    	} else {
    	    		UUID adUUID = UUID.fromString(adID);
    	    		
    	    		AdcCreatFile creatFile = null;
    	    		
	    			String key = GlobalInfo.FileCandiCreatFileVerKey.get("S" + screen.getId());
	        		if (Util.isValid(key)) {
	        			ArrayList<AdcCreatFile> list = GlobalInfo.FileCandiCreatFileMap.get(key);
	        			for(AdcCreatFile acf : list) {
	        				if (acf.getUuid().equals(adUUID)) {
	        					creatFile = acf;
	        					break;
	        				}
	        			}
	        		}

	        		if (creatFile != null) {
	        			
		        		List<AdcAdCreative> candiList = null;
	    	    		key = GlobalInfo.AdCandiAdCreatVerKey.get("S" + screen.getId());
	    	    		if (Util.isValid(key)) {
	    	    			candiList = GlobalInfo.AdCandiAdCreatMap.get(key);
	    	    		}

	    	    		if (candiList != null && candiList.size() > 0) {
	    	    			
	    	    			// 후보 리스트 중에 일치하는 첫 자료 설정
	    	    			// 나중에 리스트에 중복 항목이 있게 된다면...
	    	    			for(AdcAdCreative adC : candiList) {
	    	    				if (adC.getCreative().getId() == creatFile.getCreative().getId()) {
	    	    					adCreative = adC;
	    	    				}
	    	    			}
	    	    		}
	        			
	        		}
	        		
	        		if (adCreative == null) {
	        			statusCode = -15;
	        			message = "AdNotFound";
	        			localMessage = "광고 정보를 확인할 수 없습니다.";
	        		}
    	    	}
    		}
    	}
    	
		obj.put("code", statusCode);
		obj.put("message", message);
		obj.put("local_message", localMessage);
    	

    	if (statusCode >= 0 && screen != null) {

        	logger.info("[API] retry report: " + screen.getName());
        	
        	
        	boolean opResult = true;
        	try {
            	RevAdSelect adSelect = new RevAdSelect(screen, adCreative);
            	if (!testMode) {
            		Date now = new Date();
            		// 광고 선택일자를 재생의 시작일시로 변경
            		adSelect.setSelectDate(startDt);
    		    	adSelect.setReportDate(now);
    		    	
    		    	adSelect.setPlayBeginDate(startDt);
    		    	adSelect.setPlayEndDate(endDt);
    		    	adSelect.setDuration(dur);
    		    	
    		    	adSelect.setResult(true);
            		
            		revService.saveOrUpdate(adSelect);

            		
        			// 개체 이벤트 처리(RetryReport API 호출 시간 등록)
        			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(screen.getId(), now, 14));

	    			// 개체 이벤트 처리(광고 소재 최근 송출 등록)
	    			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(adSelect.getCreative().getId(), now, 21));

    	    		
                	RevAdSelCache adSelCache = revService.getLastAdSelCacheByScreenIdAdCreativeId(
                			screen.getId(), adCreative.getId());
                	if (adSelCache == null) {
                		revService.saveOrUpdate(new RevAdSelCache(adSelect));
                	} else if (adSelCache.getSelectDate().before(adSelect.getSelectDate())) {
                		adSelCache.setSelectDate(adSelect.getSelectDate());
                		revService.saveOrUpdate(adSelCache);
                	}
	        		
	        		// 상태 라인 처리 위해 공용리스트에 추가
		    		if (startDt != null && endDt != null) {
		    			// 광고 선택일시(없음)
		    			
		    			// 광고 재생기간(분단위 시간)
		    			List<Date> playMins = SolUtil.getOnTimeMinuteDateListBetween(startDt, endDt);
		    			for(Date d : playMins) {
			        		GlobalInfo.ScrWorkTimeItemList.add(new RevScrWorkTimeItem(screen.getId(), d));
		    			}
		    		}
		    		
		    		
    	    		// 집계 테이블에 바로 등록
    	    		//   기준 시간은 처음 이 자료가 생성된 시간
    	    		if (adSelect.getAdCreative() != null) {
    	    			
    	    			// 시간당 화면/광고 재생 계산
    	    			GregorianCalendar calendar = new GregorianCalendar();
    	    			
    		    		RevScrHourlyPlay hourlyPlay = revService.getScrHourlyPlay(adSelect.getScreen(), 
    		    				adSelect.getAdCreative().getAd(), Util.removeTimeOfDate(adSelect.getSelectDate()));
    		    		if (hourlyPlay == null) {
    		    			hourlyPlay = new RevScrHourlyPlay(adSelect.getScreen(), adSelect.getAdCreative(), 
    		    					Util.removeTimeOfDate(adSelect.getSelectDate()));
    		    		}
    		    		
    		    		calendar.setTime(adSelect.getSelectDate());
    			        
    			        switch (calendar.get(Calendar.HOUR_OF_DAY)) {
    			        case 0: hourlyPlay.setCnt00(hourlyPlay.getCnt00() + 1); break;
    			        case 1: hourlyPlay.setCnt01(hourlyPlay.getCnt01() + 1); break;
    			        case 2: hourlyPlay.setCnt02(hourlyPlay.getCnt02() + 1); break;
    			        case 3: hourlyPlay.setCnt03(hourlyPlay.getCnt03() + 1); break;
    			        case 4: hourlyPlay.setCnt04(hourlyPlay.getCnt04() + 1); break;
    			        case 5: hourlyPlay.setCnt05(hourlyPlay.getCnt05() + 1); break;
    			        case 6: hourlyPlay.setCnt06(hourlyPlay.getCnt06() + 1); break;
    			        case 7: hourlyPlay.setCnt07(hourlyPlay.getCnt07() + 1); break;
    			        case 8: hourlyPlay.setCnt08(hourlyPlay.getCnt08() + 1); break;
    			        case 9: hourlyPlay.setCnt09(hourlyPlay.getCnt09() + 1); break;
    			        case 10: hourlyPlay.setCnt10(hourlyPlay.getCnt10() + 1); break;
    			        case 11: hourlyPlay.setCnt11(hourlyPlay.getCnt11() + 1); break;
    			        case 12: hourlyPlay.setCnt12(hourlyPlay.getCnt12() + 1); break;
    			        case 13: hourlyPlay.setCnt13(hourlyPlay.getCnt13() + 1); break;
    			        case 14: hourlyPlay.setCnt14(hourlyPlay.getCnt14() + 1); break;
    			        case 15: hourlyPlay.setCnt15(hourlyPlay.getCnt15() + 1); break;
    			        case 16: hourlyPlay.setCnt16(hourlyPlay.getCnt16() + 1); break;
    			        case 17: hourlyPlay.setCnt17(hourlyPlay.getCnt17() + 1); break;
    			        case 18: hourlyPlay.setCnt18(hourlyPlay.getCnt18() + 1); break;
    			        case 19: hourlyPlay.setCnt19(hourlyPlay.getCnt19() + 1); break;
    			        case 20: hourlyPlay.setCnt20(hourlyPlay.getCnt20() + 1); break;
    			        case 21: hourlyPlay.setCnt21(hourlyPlay.getCnt21() + 1); break;
    			        case 22: hourlyPlay.setCnt22(hourlyPlay.getCnt22() + 1); break;
    			        case 23: hourlyPlay.setCnt23(hourlyPlay.getCnt23() + 1); break;
    			        }
    			        
    			        hourlyPlay.calcTotal();
    			        hourlyPlay.touchWho();
    			        
    			        revService.saveOrUpdate(hourlyPlay);
    			        
    			        
    			        // 재생 기록 생성
    			        revService.saveOrUpdate(new RevPlayHist(adSelect));
    			        
    			        
    			        // 광고 선택 삭제
    			        revService.deleteAdSelect(adSelect);
    	    		}
            	}
	    	} catch (Exception e) {
	    		logger.error("Retry Report API - process", e);
	    		opResult = false;
        	}
	    	
			obj.put("success", opResult);
    	}

		Util.toJson(response, obj);
    }
}
