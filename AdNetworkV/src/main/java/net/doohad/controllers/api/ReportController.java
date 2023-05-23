package net.doohad.controllers.api;

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
import net.doohad.models.inv.InvScreen;
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
 * 결과 보고 API 컨트롤러
 */
@Controller("api-report-controller")
@RequestMapping(value="")
public class ReportController {
	
	private static final Logger logger = LoggerFactory.getLogger(ReportController.class);


    @Autowired 
    private RevService revService;

    @Autowired 
    private InvService invService;

    
    //
    // 상태 코드 정리:
    //
    //		-1		WrongApiKey					등록되지 않은 API key가 전달되었습니다.
    //		-2		WrongDisplayID				등록되지 않은 디스플레이 ID가 전달되었습니다.
    //		-3		WrongParams					필수 인자의 값이 전달되지 않았습니다.
    //		-4		EffectiveDateExpired		유효 기간의 범위에 포함되지 않습니다.
    //		-5		NotActive					정상 서비스 중이 아닙니다.
    //		-6		NotAdServerAvailable		광고 서비스로 이용할 수 없습니다.
    //
    //		-11		WrongAttemptID				등록되지 않은 재생 시도 ID가 전달되었습니다.
    //		-12		AlreadyReportedAttemptID	이미 완료 처리된 보고입니다.
    //
    
    /**
	 * 성공 결과 보고 API
	 */
    @RequestMapping(value = {"/v1/report/success/{attemptID}"}, method = RequestMethod.GET)
    public void processApiReportSuccess(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap) {
    	
    	process(Util.parseString(pathMap.get("attemptID")), paramMap.get("start"), paramMap.get("end"),
    			paramMap.get("duration"), true, response);
    }

    
    /**
	 * 오류 결과 보고 API
	 */
    @RequestMapping(value = {"/v1/report/error/{attemptID}"}, method = RequestMethod.GET)
    public void processApiReportError(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap) {
    	
    	process(Util.parseString(pathMap.get("attemptID")), paramMap.get("start"), paramMap.get("end"),
    			paramMap.get("duration"), false, response);
    }

    
    /**
	 * 보고 처리
	 */
    private void process(String attemptID, String startS, String endS, String durS, boolean success, 
    		HttpServletResponse response) {
    	
    	int statusCode = 0;
    	String message = "Ok";
    	String localMessage = "Ok";
    	
    	JSONObject obj = new JSONObject();
    	RevAdSelect adSelect = null;
    	
    	// 재생 시도 ID == adSelect UUID
    	if (Util.isNotValid(attemptID)) {
    		statusCode = -11;
    		message = "WrongAttemptID";
    		localMessage = "등록되지 않은 재생 시도 ID가 전달되었습니다.";
    	} else {
    		
    		adSelect = revService.getAdSelect(UUID.fromString(attemptID));
    		if (adSelect == null) {
        		statusCode = -11;
        		message = "WrongAttemptID";
        		localMessage = "등록되지 않은 재생 시도 ID가 전달되었습니다.";
    		} else if (adSelect.getReportDate() != null) {
        		statusCode = -12;
        		message = "AlreadyReportedAttemptID";
        		localMessage = "이미 완료 처리된 보고입니다.";
        		
	    		// 이 오류에 대한 것도 보고 기록으로 등록
	    		InvScreen screen = adSelect.getScreen();
	    		screen.setLastAdReportDate(new Date());
	    		
	    		invService.saveOrUpdate(screen);
    		}
    	}
    	
    	
		obj.put("code", statusCode);
		obj.put("message", message);
		obj.put("local_message", localMessage);

		if (statusCode == 0 && adSelect != null) {

        	logger.info("[API] report: " + attemptID);
		    
        	// 시작시간, 종료시간, 재생시간 정보는 모두 옵셔널 값
        	// 유효 조건 위반일 경우 모든 값을 null로 처리
        	//
        	// 유효 조건:
        	//   1) 재생시간: 1s <= dur <= 1hr
        	//   2) 종료시간이 시작시간 이후
        	//
	    	long start = Util.parseLong(startS, -1l);
	    	long end = Util.parseLong(endS, -1l);
	    	
	    	Date startDt = null;
	    	Date endDt = null;
	    	Integer dur = null;

	    	if (start > 0) {
	    		startDt = new Date(start);
	    	}
	    	if (end > 0) {
	    		endDt = new Date(end);
	    	}
	    	dur = (int)(end - start);
	    	if (dur < 1000 || dur > 3600000 || start > end) {
	    		startDt = null;
	    		endDt = null;
	    		dur = null;
	    	}
	    	
	    	// 시작시간, 종료시간에 대한 처리 완료
	    	// 재생시간 값이 유효하면 패스, 아니면 재생시간 값만 처리
	    	if (dur == null) {
	    		int duration = Util.parseInt(durS, -1);
	    		if (duration >= 1000 && duration <= 3600000) {
	    			dur = duration;
	    		}
	    	}
	    	
	    	
	    	boolean opResult = true;
	    	try {
		    	Date now = new Date();
		    	adSelect.setReportDate(now);
		    	
		    	adSelect.setPlayBeginDate(startDt);
		    	adSelect.setPlayEndDate(endDt);
		    	adSelect.setDuration(dur);
		    	
		    	adSelect.setResult(success);
		    	
	    		revService.saveOrUpdate(adSelect);


	    		int screenId = adSelect.getScreen().getId();
    			
    			// 개체 이벤트 처리(Report API 호출 시간 등록)
    			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(screenId, now, 13));
	    		
	    		
	    		if (success) {
	    			Date lastDate = now;
	    			if (endDt != null && endDt.before(lastDate)) {
	    				lastDate = endDt;
	    			}
	    			
	    			// 개체 이벤트 처리(광고 소재 최근 송출 등록)
	    			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(adSelect.getCreative().getId(), now, 21));

		    		
	        		// 상태 라인 처리 위해 공용리스트에 추가
		    		if (startDt != null && endDt != null) {
		    			// 광고 선택일시
		        		GlobalInfo.ScrWorkTimeItemList.add(new RevScrWorkTimeItem(screenId, adSelect.getSelectDate()));
		    			
		    			// 광고 재생기간(분단위 시간)
		    			List<Date> playMins = SolUtil.getOnTimeMinuteDateListBetween(startDt, endDt);
		    			for(Date d : playMins) {
			        		GlobalInfo.ScrWorkTimeItemList.add(new RevScrWorkTimeItem(screenId, d));
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
	    		logger.error("Report API - process", e);
	    		opResult = false;
	    	}
	    	
			obj.put("success", opResult);
    	}
		
		Util.toJson(response, obj);
    }

    
    /**
	 * 성공 결과 보고 진단 API
	 */
    @RequestMapping(value = {"/v1/reportd/success/{attemptID}"}, method = RequestMethod.GET)
    public void processApiReportSuccessDiag(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap) {
    	
    	processDiag(Util.parseString(pathMap.get("attemptID")), paramMap.get("start"), paramMap.get("end"),
    			paramMap.get("duration"), true, response);
    }

    
    /**
	 * 오류 결과 보고 진단 API
	 */
    @RequestMapping(value = {"/v1/reportd/error/{attemptID}"}, method = RequestMethod.GET)
    public void processApiReportErrorDiag(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap) {
    	
    	processDiag(Util.parseString(pathMap.get("attemptID")), paramMap.get("start"), paramMap.get("end"),
    			paramMap.get("duration"), false, response);
    }

    
    /**
	 * 보고 처리 진단
	 */
    private void processDiag(String attemptID, String startS, String endS, String durS, boolean success, 
    		HttpServletResponse response) {
    	
    	int statusCode = 0;
    	String message = "Ok";
    	String localMessage = "Ok";
    	
    	JSONObject obj = new JSONObject();
    	RevAdSelect adSelect = null;
    	
    	long startAt = new Date().getTime();
    	logger.info("     ==========> " + startAt + "    Report API diag: 시작");
    	
    	// 재생 시도 ID == adSelect UUID
    	if (Util.isNotValid(attemptID)) {
    		statusCode = -11;
    		message = "WrongAttemptID";
    		localMessage = "등록되지 않은 재생 시도 ID가 전달되었습니다.";
    	} else {
    		
        	logger.info("     ==========> " + (new Date().getTime()) + "    API diag: revService.getAdSelect 전");
    		adSelect = revService.getAdSelect(UUID.fromString(attemptID));
    		if (adSelect == null) {
        		statusCode = -11;
        		message = "WrongAttemptID";
        		localMessage = "등록되지 않은 재생 시도 ID가 전달되었습니다.";
    		} else if (adSelect.getReportDate() != null) {
        		statusCode = -12;
        		message = "AlreadyReportedAttemptID";
        		localMessage = "이미 완료 처리된 보고입니다.";
    		}
    	}
    	
    	
		obj.put("code", statusCode);
		obj.put("message", message);
		obj.put("local_message", localMessage);

		if (statusCode == 0 && adSelect != null) {
		    
        	// 시작시간, 종료시간, 재생시간 정보는 모두 옵셔널 값
        	// 유효 조건 위반일 경우 모든 값을 null로 처리
        	//
        	// 유효 조건:
        	//   1) 재생시간: 1s <= dur <= 1hr
        	//   2) 종료시간이 시작시간 이후
        	//
	    	long start = Util.parseLong(startS, -1l);
	    	long end = Util.parseLong(endS, -1l);
	    	
	    	Date startDt = null;
	    	Date endDt = null;
	    	Integer dur = null;

	    	if (start > 0) {
	    		startDt = new Date(start);
	    	}
	    	if (end > 0) {
	    		endDt = new Date(end);
	    	}
	    	dur = (int)(end - start);
	    	if (dur < 1000 || dur > 3600000 || start > end) {
	    		startDt = null;
	    		endDt = null;
	    		dur = null;
	    	}
	    	
	    	// 시작시간, 종료시간에 대한 처리 완료
	    	// 재생시간 값이 유효하면 패스, 아니면 재생시간 값만 처리
	    	if (dur == null) {
	    		int duration = Util.parseInt(durS, -1);
	    		if (duration >= 1000 && duration <= 3600000) {
	    			dur = duration;
	    		}
	    	}
	    	
	    	
	    	boolean opResult = true;
	    	try {
		    	Date now = new Date();
		    	adSelect.setReportDate(now);
		    	
		    	adSelect.setPlayBeginDate(startDt);
		    	adSelect.setPlayEndDate(endDt);
		    	adSelect.setDuration(dur);
		    	
		    	adSelect.setResult(success);
		    	
	    		//revService.saveOrUpdate(adSelect);

	    		
	    		//int screenId = adSelect.getScreen().getId();
	    		//invService.updateScreenLastAdReportDate(screenId, now);
	    		
	    		
	    		if (success) {
	    			Date lastDate = now;
	    			if (endDt != null && endDt.before(lastDate)) {
	    				lastDate = endDt;
	    			}
	    			
		    		//AdcCreative creative = adSelect.getCreative();
		    		//creative.setLastPlayDate(lastDate);
		    		
		    		//adcService.saveOrUpdate(creative);

		    		
	        		// 상태 라인 처리 위해 공용리스트에 추가 생략
		    		
		    		
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
				        
				        //revService.saveOrUpdate(hourlyPlay);
				        
				        
				        // 재생 기록 생성
				        //revService.saveOrUpdate(new RevPlayHist(adSelect));
				        
				        
				        // 광고 선택 삭제
				        //revService.deleteAdSelect(adSelect);
		    		}
	    		}
	    	} catch (Exception e) {
	    		logger.error("Report API - process", e);
	    		opResult = false;
	    	}
	    	
			obj.put("success", opResult);
    	}
		
		Util.toJson(response, obj);
		
		long endAt = new Date().getTime();
    	logger.info("     ==========> " + endAt + "    API diag: 종료 - " + (endAt - startAt));
    }

}
