package net.doohad.controllers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import net.doohad.info.GlobalInfo;
import net.doohad.models.adc.AdcAd;
import net.doohad.models.adc.AdcAdCreative;
import net.doohad.models.adc.AdcCreatFile;
import net.doohad.models.adc.AdcCreative;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.inv.InvSite;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgPlaylist;
import net.doohad.models.rev.RevAdHourlyPlay;
import net.doohad.models.rev.RevAdSelect;
import net.doohad.models.rev.RevCreatHourlyPlay;
import net.doohad.models.rev.RevObjTouch;
import net.doohad.models.rev.RevPlayHist;
import net.doohad.models.rev.RevScrHourlyPlay;
import net.doohad.models.rev.RevScrHrlyFailTot;
import net.doohad.models.rev.RevScrHrlyFbTot;
import net.doohad.models.rev.RevScrHrlyNoAdTot;
import net.doohad.models.service.AdcService;
import net.doohad.models.service.InvService;
import net.doohad.models.service.KnlService;
import net.doohad.models.service.OrgService;
import net.doohad.models.service.RevService;
import net.doohad.models.service.SysService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.rev.RevAdOrderItem;
import net.doohad.viewmodels.rev.RevObjEventTimeItem;
import net.doohad.viewmodels.rev.RevScrHrlyPlyAdCntItem;
import net.doohad.viewmodels.rev.RevScrWorkTimeItem;
import net.doohad.viewmodels.sys.SysObjEventTimeCalcItem;
import net.doohad.viewmodels.sys.SysScrEventTimeCalcItem;
import net.doohad.viewmodels.sys.SysScrWorkTimeCalcItem;

@SuppressWarnings("unused")
@Component
public class StartupHouseKeeper implements ApplicationListener<ContextRefreshedEvent> {
	private static final Logger logger = LoggerFactory.getLogger(StartupHouseKeeper.class);

	private static Timer keyGenTimer;
	private static Timer minTickTimer;
	
	private static Timer calcStatsMinTimer;
	private static Timer calcStatsHrTimer1;
	private static Timer calcStatsHrTimer2;
	private static Timer calcStatsHrTimer3;
	private static Timer playHistOrganTimer;
	private static Timer playHistLogTimer;
	
	private static Timer adCreatTimer;
	
	private static Timer statusLineTimer;
	
	private static Timer objTouchTimer;
	private static Timer objTouchTimer2;
	
	private static Timer tmpWorkTimer1;
	private static Timer tmpWorkTimer2;

	
	private static int adCalcCount = 0;
	

	@Autowired
	private InvService invService;

	@Autowired
	private RevService revService;

	@Autowired
	private AdcService adcService;

	@Autowired
	private KnlService knlService;

	@Autowired
	private OrgService orgService;

	@Autowired
	private SysService sysService;
	
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		String appId = event.getApplicationContext().getId();
		logger.info("Enter onApplicationEvent() - id=" + appId);
		
		if (!appId.equals("org.springframework.web.context.WebApplicationContext:/" + GlobalInfo.AppId)) {
			return;
		}
		
		SolUtil.setBgMaxSeq("PM", 50);		// 임의의 큰 값으로 설정 후 나중에 조정
		SolUtil.setBgMaxSeq("CA", 50);		// 임의의 큰 값으로 설정 후 나중에 조정
		
		String repServer = Util.getFileProperty("url.report.server");
		if (Util.isValid(repServer) && repServer.startsWith("http")) {
			// http, https 로 한정 목적
			GlobalInfo.ReportServer = repServer;
		}
		
		String apiTestServer = Util.getFileProperty("url.apitest.server");
		if (Util.isValid(apiTestServer) && apiTestServer.startsWith("http")) {
			// http, https 로 한정 목적
			GlobalInfo.ApiTestServer = apiTestServer;
		}
		
		
		//
		// [RSA 키 변경]: RSA 키 주기적 갱신
		//   
		//   - 시작: 구동 즉시
		//   - 주기: 30분 단위
		// 
		if (keyGenTimer == null && isFilePropertyYes("bg.keyGen")) {
			keyGenTimer = new Timer();
			keyGenTimer.scheduleAtFixedRate(new TimerTask() {
		    	   public void run()
		 	       {
		    		   GlobalInfo.RSAKeyPair = Util.getKeyPairRSA();
		    		   GlobalInfo.RSAKeyMod = "";

		    		   logger.info("     -> [RSA 키 변경] - KeyPair: " + (GlobalInfo.RSAKeyPair == null ? "null" : "new"));
			       }
		        }, 0, 30 * (60 * 1000));
		}
		
		//
		// [분 정각 구동]: 분 정각 트리거링
		//   
		//   - 시작: 구동 즉시
		//   - 주기: 1분 단위(매분 정각)
		// 
		if (minTickTimer == null && isFilePropertyYes("bg.minTick")) {
			minTickTimer = new Timer();
			
			Date now = new Date();
			Date nextDt = DateUtils.ceiling(now, Calendar.MINUTE);
			
			// 분 정각까지의 지연 시간
			// 의도적인 0.5s 지연(현재 시간 기반 분기가 진행 예정)
			long delay = nextDt.getTime() - now.getTime() + 500;
			
			minTickTimer.scheduleAtFixedRate(new TimerTask() {
		    	   public void run() {
		    		   
		    		   GregorianCalendar calendar = new GregorianCalendar();
		    		   calendar.setTime(new Date());

		    		   // 매시간 정각: 오늘 기반 캠페인/광고 상태 변경
		    		   if (calendar.get(Calendar.MINUTE) == 0 || calendar.get(Calendar.MINUTE) == 1) {
		    			   logger.info("     -> [오늘 기반 캠페인/광고 상태 변경] - 시작");
		    			   logger.info("     ->           [오늘 기반 캠페인/광고 상태 변경] - refreshed: {}", adcService.refreshCampaignAdStatusBasedToday());
		    		   }
		    		   if (calendar.get(Calendar.MINUTE) == 2) {
			    		   logger.info("     -> [광고/화면별 시간 목표치 계산] - 시작");
			    		   logger.info("     ->           [광고/화면별 시간 목표치 계산] - {}", calcAdHourlyGoalValue());
		    		   }
		    		   
			       }
		        }, delay, 1 * (60 * 1000));
		}
		
		//
		// [광고 선택/보고 정리]: 예기치 않은 잔여 광고 선택/보고 자료 및 미보고 자료에 대한 정리
		//   
		//   - 시작: 구동 직후 20초 후
		//   - 주기: 30분 단위
		//
		if (playHistOrganTimer == null && isFilePropertyYes("bg.organPlayHist")) {
			playHistOrganTimer = new Timer();
			playHistOrganTimer.scheduleAtFixedRate(new TimerTask() {
		    	   public void run() {
		    		   
		    		   logger.info("     -> [광고 선택/보고 정리] - 시작");
		    		   logger.info("     ->           [광고 선택/보고 정리] - {} rows organized.", organizePlayHists());
			       }
		        }, 20 * 1000, 30 * (60 * 1000));
		}
		
		//
		// [광고/광고 소재 목록 생성]: 현재 광고 API의 시작점이 되는 목록을 백그라운드로 생성
		//   
		//   - 시작: 구동 직후 5초 후
		//   - 주기: 1분 단위
		//
		if (adCreatTimer == null && isFilePropertyYes("bg.createCandiAdCreat")) {
			adCreatTimer = new Timer();
			adCreatTimer.scheduleAtFixedRate(new TimerTask() {
		    	   public void run() {
		    		   
		    		   logger.info("     -> [광고/광고 소재 목록] - 시작");
		    		   logger.info("     ->           [광고/광고 소재 목록] - {}", createCandiAdCreatList());
		    		   logger.info("     -> [광고/광고 소재 타겟팅 대상] - 시작");
		    		   logger.info("     ->           [광고/광고 소재 타겟팅 대상] - {}", refreshInvenTargetScreenList());
			       }
		        }, 5 * 1000, 1 * (60 * 1000));
		}
		
		//
		// [화면 분단위 상태행 임시 기록]: 화면의 상태행을 WAS에서 받아 모아서 백그라운드로 임시 생성
		//   
		//   - 시작: 구동 직후 30초 후
		//   - 주기: 30초 단위
		//
		if (statusLineTimer == null && isFilePropertyYes("bg.createCandiAdCreat")) {
			statusLineTimer = new Timer();
			statusLineTimer.schedule(new TimerTask() {
		    	   public void run() {
		    		   
		    		   logger.info("     -> [화면 분단위 상태행 임시 기록] - 시작");
		    		   logger.info("     ->           [화면 분단위 상태행 임시 기록] - {}", saveTmpScreenStatusLines());
			       }
		        }, 30 * 1000, 30 * 1000);
		}
		
		//
		// [개체 최근 변경일시 임시 기록]: 개체의 최근 변경일시를 WAS에서 받아 모아서 백그라운드로 임시 생성
		//   
		//   - 시작: 구동 직후 30초 후
		//   - 주기: 30초 단위
		//
		if (objTouchTimer == null && isFilePropertyYes("bg.createCandiAdCreat")) {
			objTouchTimer = new Timer();
			objTouchTimer.schedule(new TimerTask() {
		    	   public void run() {
		    		   
		    		   logger.info("     -> [개체 최근 변경일시 임시 집계] - 시작");
		    		   logger.info("     ->           [개체 최근 변경일시 임시 집계] - {}", calcTmpObjEvents());
			       }
		        }, 30 * 1000, 30 * 1000);
		}
		
		//
		// [개체 최근 변경일시 집계 기록]: 임시로 저장된 개체 최근 변경일시를 집계해서 기록
		//   
		//   - 시작: 구동 직후 1분 후
		//   - 주기: 1분 단위
		//
		if (objTouchTimer2 == null && isFilePropertyYes("bg.createCandiAdCreat")) {
			objTouchTimer2 = new Timer();
			objTouchTimer2.schedule(new TimerTask() {
		    	   public void run() {
		    		   
		    		   logger.info("     -> [개체 최근 변경일시 집계 기록] - 시작");
		    		   logger.info("     ->           [개체 최근 변경일시 집계 기록] - {}", saveObjEvents());
			       }
		        }, 60 * 1000, 60 * 1000);
		}
		
		//
		// [광고 선택/보고 로그]: 광고 선택/보고 자료를 텍스트 파일 로그로 만들고, DB 자료 삭제
		//   
		//   - 시작: 구동 후 1분 후
		//   - 주기: 1분 단위
		//
		if (playHistLogTimer == null && isFilePropertyYes("bg.logPlayHist")) {
			playHistLogTimer = new Timer();
			playHistLogTimer.scheduleAtFixedRate(new TimerTask() {
		    	   public void run() {
		    		   
		    		   logger.info("     -> [광고 선택/보고 로그] - 시작");
		    		   logger.info("     ->           [광고 선택/보고 로그] - {} rows logged.", logPlayHists());
			       }
		        }, 60 * 1000 * 0, 1 * (60 * 1000));
		}
		
		//
		// [인벤 상태 최신화]: 매체 화면과 사이트의 현재 상태 자동 계산
		//   시간 경과에 따라 상태값을 다르게 설정하기 위해 주기적으로 계산/설정
		//   
		//   - 시작: 구동 직후 10초 후
		//   - 주기: 1분 단위
		//
		if (calcStatsMinTimer == null && isFilePropertyYes("bg.calcStats")) {
			calcStatsMinTimer = new Timer();
			calcStatsMinTimer.scheduleAtFixedRate(new TimerTask() {
		    	   public void run() {
		    		   
		    		   logger.info("     -> [인벤 상태 최신화] - 시작");
		    		   logger.info("     ->           [인벤 상태 최신화] - {} rows recalced.", doRecalcLastStatus());
			       }
		        }, 10 * 1000, 1 * (60 * 1000));
		}
		
		//
		// [통계 자료 계산]: 시간당 화면 및 사이트 재생 합계를 계산
		//   
		//   - 시작: 구동 직후 1분 후
		//   - 주기: 30분시간 단위
		//
		if (calcStatsHrTimer1 == null && isFilePropertyYes("bg.calcStats")) {
			calcStatsHrTimer1 = new Timer();
			calcStatsHrTimer1.scheduleAtFixedRate(new TimerTask() {
		    	   public void run() {
		    		   
		    		   logger.info("     -> [통계 자료 계산] - 시작");
		    		   logger.info("     ->           [통계 자료 계산] - {}", calcStatsHourly());
			       }
		        }, 1 * (60 * 1000), 30 * (60 * 1000));
		}
		
		//
		// [광고/화면별 시간 목표치 계산]: 매체 화면과 사이트의 현재 상태 자동 계산
		//   시간 경과에 따라 상태값을 다르게 설정하기 위해 주기적으로 계산/설정
		//   
		//   - 시작: 구동 직후
		//   - 주기: 30분시간 단위
		//
		if (calcStatsHrTimer2 == null && isFilePropertyYes("bg.calcStats")) {
			calcStatsHrTimer2 = new Timer();
			calcStatsHrTimer2.scheduleAtFixedRate(new TimerTask() {
		    	   public void run() {
		    		   
		    		   logger.info("     -> [광고/화면별 시간 목표치 계산] - 시작");
		    		   logger.info("     ->           [광고/화면별 시간 목표치 계산] - {}", calcAdHourlyGoalValue());
			       }
		        }, 0 * (60 * 1000), 30 * (60 * 1000));
		}
		
		//
		// [광고별 하루 송출량 계산]: 매체 화면과 사이트의 현재 상태 자동 계산
		//   시간 경과에 따라 상태값을 다르게 설정하기 위해 주기적으로 계산/설정
		//   
		//   - 시작: 구동 1분 후
		//   - 주기: 10분시간 단위
		//
		if (calcStatsHrTimer3 == null && isFilePropertyYes("bg.calcStats")) {
			calcStatsHrTimer3 = new Timer();
			calcStatsHrTimer3.scheduleAtFixedRate(new TimerTask() {
		    	   public void run() {
		    		   
		    		   logger.info("     -> [광고별 하루 송출량 계산] - 시작");
		    		   logger.info("     ->           [광고별 하루 송출량 계산] - {}", calcDailyAdImpressionStat());
			       }
		        }, 1 * (60 * 1000), 5 * (60 * 1000));
		}
		
		//
		// [화면 분단위 상태행 최종 기록]: 임시로 저장된 화면 분단위 상태행을 집계해서 기록
		//   
		//   - 시작: 구동 직후 30초 후
		//   - 주기: 30초 단위
		//
		if (tmpWorkTimer1 == null && isFilePropertyYes("bg.gatherTmp")) {
			tmpWorkTimer1 = new Timer();
			tmpWorkTimer1.schedule(new TimerTask() {
		    	   public void run() {
		    		   
		    		   logger.info("     -> [화면 분단위 상태행 최종 기록] - 시작");
		    		   logger.info("     ->           [화면 분단위 상태행 최종 기록] - {}", saveScreenStatusLines());
			       }
		        }, 30 * 1000, 30 * 1000);
		}
		
		//
		// [화면 시간당 이벤트 합계 기록]: 임시로 저장된 화면 시간당 이벤트를 집계해서 기록
		//   
		//   - 시작: 구동 직후 30초 후
		//   - 주기: 1분 단위
		//
		if (tmpWorkTimer2 == null && isFilePropertyYes("bg.gatherTmp")) {
			tmpWorkTimer2 = new Timer();
			tmpWorkTimer2.schedule(new TimerTask() {
		    	   public void run() {
		    		   
		    		   logger.info("     -> [화면 시간당 이벤트 합계 기록] - 시작");
		    		   logger.info("     ->           [화면 시간당 이벤트 합계 기록] - {}", saveHourlyEvents());
			       }
		        }, 30 * 1000, 60 * 1000);
		}
		
		
		//
		// 테스트 코드를 이곳에.
		//
		//
		/*
		String ctnt = "{" + 
				"apiKey: \"CY9YOU0ALsq0ac3Zek9TQr\", " +
				"siteID: \"bbmc_what\", " +
				"siteName: \"BBMC 어디\", " +
				"screenID: \"screen_wh\", " +
				"screenName: \"QA9 어디\", " +
				//"lat: \"37.2656777\", " +
				//"lng: \"127.0003136\", " +
				//"region: \"경기도 수원시 팔달구\", " +
				//"addr: \"경기도 수원시 팔달구 매산로1가18번지 외 11필지 수원민자역사 제1호\", " +
				"type: \"D\", " +
				"}";
		Util.sendStreamToServer("https://spring.doohad.net/v1/inven", "application/json; charset=UTF-8", ctnt);
		*/
		/*
		Date start = new Date();
		RevScrStatusLine todayStatusLine = revService.getScrStatusLine(11883, Util.removeTimeOfDate(start));
		if (todayStatusLine != null) {
			System.out.println(todayStatusLine.getStatusLine());
			//todayStatusLine = new RevScrStatusLine(screen, now);
		}
		System.out.println("time = " + (new Date().getTime() - start.getTime()));
		start = new Date();
		Tuple tuple = revService.getScrStatusLineTuple(11883, Util.removeTimeOfDate(start));
		if (tuple != null) {
			System.out.println((String)tuple.get(0));
		}
		System.out.println("time = " + (new Date().getTime() - start.getTime()));
		*/

		/*
		Date start = new Date();
		List<RevPlayHist> playHistList11 = revService.getLastPlayHistListByScreenId(12198, 1);
		System.out.println("time1 = " + (new Date().getTime() - start.getTime()));
		start = new Date();
		List<RevAdSelect> adSelList11 = revService.getLastAdSelectListByScreenId(12198, 1);
		System.out.println("time2 = " + (new Date().getTime() - start.getTime()));
		*/
		
		/*
		KnlMedium medium = knlService.getMedium(2);
		Date start = new Date();
		InvScreen screen1 = invService.getScreen(medium, "AST21084418");
		System.out.println("time1 = " + (new Date().getTime() - start.getTime()) + ", name = " + screen1.getName());
		*/
	}
	
	
	// 화면과 사이트에 대한 현재 상태 재계산
	private int doRecalcLastStatus() {

		String status = "0";
		Date now = new Date();
		int cnt = 0;

		try {
			// 화면 현재 상태 새로고침
			
			HashMap<String, RevObjTouch> map = new HashMap<String, RevObjTouch>();
			List<RevObjTouch> objList = revService.getObjTouchList();
			for(RevObjTouch objTouch : objList) {
				if (objTouch.getType().equals("S")) {
					map.put("S" + objTouch.getObjId(), objTouch);
				}
			}
			
			List<InvScreen> screenList = invService.getMonitScreenList();
			for(InvScreen screen : screenList) {
				if (map.containsKey("S" + screen.getId())) {
					RevObjTouch objTouch = map.get("S" + screen.getId());
					if (objTouch != null) {
						status = objTouch.getScreenStatus();
						
						if (!screen.getLastStatus().equals(status)) {
							screen.setLastStatus(status);
							invService.saveOrUpdate(screen);
							cnt++;
						}
					}
				}
			}
			
			// 사이트 현재 상태 새로고침
			List<InvSite> siteList = invService.getMonitSiteList();
			for(InvSite site : siteList) {
				status = "0";
				List<InvScreen> list = invService.getScreenListBySiteId(site.getId());
				for(InvScreen screen : list) {
					if (status.compareTo(screen.getLastStatus()) < 0) {
						status = screen.getLastStatus();
					}
				}
				
				if (site.getLastStatus() == null || !site.getLastStatus().equals(status)) {
					site.setLastStatus(status);
					invService.saveOrUpdate(site);
					cnt++;
				}
			}
		} catch (Exception e) {
    		logger.error("doRecalcLastStatus", e);
		}
		
		return cnt;
	}
	
	// 정상 처리되지 않은 자료 정리
	private int organizePlayHists() {
		
		GregorianCalendar calendar = new GregorianCalendar();
		int ret = 0;
		
		try {
			
			// Step 1. 예기치 않은 잔여 정상 자료(예를 들어 보고받은 기록을 한 직후, 서버 다운 등) 정리
			List<RevAdSelect> list = revService.getReportedAdSelectListOrderBySelDateBeforeReportDate(Util.addMinutes(new Date(), -30));
			for(RevAdSelect adSelect : list) {
				if (adSelect.getResult() != null && adSelect.getResult().booleanValue()) {
					
					// 시간당 화면/광고 재생 계산
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
			        
			        ret ++;
				}
			}
			
		} catch (Exception e) {
    		logger.error("organizePlayHists - Step 1", e);
		}
		
		
		try {
			
			ArrayList<Integer> ids = new ArrayList<Integer>();
			
			// Step 2. 미보고 자료 정리
			//
			// 미보고 자료(AdSelect)의 수가 과도하게 많아지게 되면, 시스템에서 처리하는 방법을 변경하는 것도 고려해야 한다.
			//
			//  method 1. 개별 자료 건에 대해 
			//            시간당 화면/광고 재생 계산 에서 실패 + 1, 재생 기록 생성, 광고 선택 삭제
			//            초당 10건 처리된다면, 분당 600건, 시간당 36,000건이 한계
			//
			//  method 2. 시간당 화면/광고 재생 계산 별로 그룹핑
			//            시간당 화면/광고 재생 계산 별로 그룹핑을 진행하고, 이후 재생 기록 생성(옵션), 광고 선택 삭제
			//
			//  method 2에 대해서는 이후에 다시 개선 예정
			//
			
			Date limitDate = Util.addHours(new Date(), -24);
			//Date limitDate = Util.parseDate("20230418210000");
			
			// method 1 시작
			//
			List<RevAdSelect> list = revService.getAdSelectListBeforeSelectDateOrderBySelDate(limitDate);
			for(RevAdSelect adSelect : list) {
				if (adSelect.getResult() == null || (adSelect.getResult() != null && adSelect.getResult().booleanValue() == false)) {
					
					// 시간당 화면/광고 재생 계산
		    		RevScrHourlyPlay hourlyPlay = revService.getScrHourlyPlay(adSelect.getScreen(), 
		    				adSelect.getAdCreative().getAd(), Util.removeTimeOfDate(adSelect.getSelectDate()));
		    		if (hourlyPlay == null) {
		    			hourlyPlay = new RevScrHourlyPlay(adSelect.getScreen(), adSelect.getAdCreative(), 
		    					Util.removeTimeOfDate(adSelect.getSelectDate()));
		    		}
					
		    		hourlyPlay.setFailTotal(hourlyPlay.getFailTotal() + 1);
		    		hourlyPlay.setDateTotal(hourlyPlay.getSuccTotal() + hourlyPlay.getFailTotal());
			        hourlyPlay.touchWho();
			        
			        revService.saveOrUpdate(hourlyPlay);
			        
			        
			        // 시간당 화면 실패 합계 임시 테이블로 추가(method 2에는 아직 포함안됨)
			        sysService.insertTmpHrlyEvent(adSelect.getScreen().getId(), adSelect.getSelectDate(), 1);
			        
			        
			        // 재생 기록 생성
			        revService.saveOrUpdate(new RevPlayHist(adSelect));
			        
			        // 광고 선택 삭제
			        //revService.deleteAdSelect(adSelect);
			        ids.add(adSelect.getId());
			        
			        ret ++;
			        
			        if (ret >= 10000) {
			        	break;
			        }
				}
			}
			revService.deleteAdSelectBulkRowsInIds(ids);
			//
			
			
			// method 2 시작
			/*
			logger.info("start");
			List<RevAdSelect> list = revService.getAdSelectListBeforeSelectDateOrderBySelDate(limitDate);
			HashMap<String, RevScrHrlyPlyAdCntItem> map = new HashMap<String, RevScrHrlyPlyAdCntItem>();
			logger.info("list size = " + list.size());
			for(RevAdSelect adSelect : list) {
				if (adSelect.getResult() == null || (adSelect.getResult() != null && adSelect.getResult().booleanValue() == false)) {
					
					String key = "S" + adSelect.getScreen().getId() + "A" + adSelect.getAdCreative().getAd().getId() +
							"D" + Util.toSimpleString(Util.removeTimeOfDate(adSelect.getSelectDate()), "dd");
					if (map.containsKey(key)) {
						RevScrHrlyPlyAdCntItem item = map.get(key);
						item.addOneCount();
					} else {
						map.put(key, new RevScrHrlyPlyAdCntItem(adSelect.getScreen().getId(), adSelect.getAdCreative().getAd().getId(),
								adSelect.getAdCreative().getId(), Util.removeTimeOfDate(adSelect.getSelectDate())));
					}
					
			        // 재생 기록 생성
			        //revService.saveOrUpdate(new RevPlayHist(adSelect));
			        
			        // 광고 선택 삭제
			        //revService.deleteAdSelect(adSelect);
			        ids.add(adSelect.getId());
			        
			        ret ++;
			        if (ret % 100 == 0) {
				        logger.info("cnt = " + ret);
			        }
				}
				
				//if (ret >= 2000) {
				//	break;
				//}
			}
			logger.info("cnt = " + ret);
			
			revService.deleteAdSelectBulkRowsInIds(ids);
			logger.info("deleted ");
			
			int cnt2 = 0;
			logger.info("map size = " + map.values().size());
			for(RevScrHrlyPlyAdCntItem item : map.values()) {
				
				// 시간당 화면/광고 재생 계산
	    		RevScrHourlyPlay hourlyPlay = revService.getScrHourlyPlay(item.getScreenId(), 
	    				item.getAdId(), item.getSelectDate());
	    		if (hourlyPlay == null) {
	    			hourlyPlay = new RevScrHourlyPlay(invService.getScreen(item.getScreenId()), 
	    					adcService.getAdCreative(item.getAdCreativeId()), 
	    					item.getSelectDate());
	    		}
				
	    		hourlyPlay.setFailTotal(hourlyPlay.getFailTotal() + item.getCnt());
	    		hourlyPlay.setDateTotal(hourlyPlay.getSuccTotal() + hourlyPlay.getFailTotal());
		        hourlyPlay.touchWho();
		        
		        revService.saveOrUpdate(hourlyPlay);
		        
		        cnt2 ++;
		        if (cnt2 % 100 == 0) {
		        	logger.info("cnt2 = " + cnt2);
		        }
			}
			*/
			
		} catch (Exception e) {
    		logger.error("organizePlayHists - Step 2", e);
		}
		
		return ret;
	}
	
	private boolean isFilePropertyYes(String propertyName) {
		
		if (Util.isValid(propertyName)) {
			String prop = Util.getFileProperty(propertyName);
			return Util.isValid(prop) && prop.equalsIgnoreCase("Y");
		}
		
		return false;
	}
	
	private String createCandiAdCreatList() {
		
		List<KnlMedium> mediumList = knlService.getValidMediumList();
		Date today = Util.removeTimeOfDate(new Date());
		
		long startAt = new Date().getTime();

		ArrayList<Integer> taskList1 = new ArrayList<Integer>();
		ArrayList<Integer> taskList2 = new ArrayList<Integer>();
		
		
		List<Integer> currScrIds = null;
		List<Integer> resultScrIds = null;
		List<AdcAdCreative> candiList = null;
		HashMap<String, AdcAdCreative> adCreativeMap = new HashMap<String, AdcAdCreative>();
		HashMap<String, AdcCreatFile> adCreatFileMap = new HashMap<String, AdcCreatFile>();
		HashMap<String, Integer> adCreatFileDurMap = new HashMap<String, Integer>();

		HashMap<String, RevAdOrderItem> adCreatOrderMap = new HashMap<String, RevAdOrderItem>();
		ArrayList<RevAdOrderItem> adCreatOrderList = new ArrayList<RevAdOrderItem>();
		
		
		//
		// Ad API용 광고 / 광고 소재 리스트 - 실시간 선택형
		//
		for(KnlMedium medium : mediumList) {
			//
			// 매체에 따른 화면 그룹을 따로 구분하지 않고 매체별 하나의 목록으로 처리
			//
			// 광고 선택 우선 순위:
			//     1. 구매유형(G: 목표 보장, N: 목표 비보장, H: 하우스 광고)
			//     2. 우선 순위(1-10, 1이 최고, 10이 최저, 하우스 광고에서는 의미없음)
			//
			// finalOrdStr 예:
			//     22|25:3_26:2,21,11|12:1_24:2,23|20|14,15,13
			//
			//     - id의 대상은 adCreativeId
			//     - 그룹 구분자(1차 구분자): |
			//     - 동일 그룹 내 광고 구분자: ,
			//     - 여러 소재로 구성된 광고일 경우: {id1}:{weight1}_{id2}:{weight2}_...
			//
			candiList = adcService.getCandiAdCreativeListByMediumIdDate
					(medium.getId(), today, today);
			
			adCreatOrderMap.clear();
			adCreatOrderList.clear();
			
			for(AdcAdCreative adc : candiList) {
				String key = "A" + adc.getAd().getId();
				if (adCreatOrderMap.containsKey(key)) {
					RevAdOrderItem item = adCreatOrderMap.get(key);
					item.add(String.valueOf(adc.getId()), String.valueOf(adc.getWeight()));
				} else {
					adCreatOrderMap.put(key, new RevAdOrderItem(adc));
				}
			}
			
			adCreatOrderList = new ArrayList<RevAdOrderItem>(adCreatOrderMap.values());
			Collections.sort(adCreatOrderList, new Comparator<RevAdOrderItem>() {
		    	public int compare(RevAdOrderItem item1, RevAdOrderItem item2) {
		    		return item1.getSortCode().compareTo(item2.getSortCode());
		    	}
		    });
			
			String prevId = "";
			String finalOrdStr = "";
			for(RevAdOrderItem ordItem : adCreatOrderList) {
				if (Util.isValid(prevId)) {
					if (prevId.equals(ordItem.getSortCode())) {
						finalOrdStr += ",";
					} else {
						finalOrdStr += "|";
					}
				}
				prevId = ordItem.getSortCode();
				
				finalOrdStr += ordItem.getItemStr();
			}
			
			if (Util.isValid(finalOrdStr)) {
				int seq = SolUtil.getBgNextSeq("PM_CandiAdCreative");
				taskList1.add(seq);
				
				HashMap<String, AdcAdCreative> map = new HashMap<String, AdcAdCreative>();
				for(AdcAdCreative adc : candiList) {
					map.put("AC" + adc.getId(), adc);
				}
				
				GlobalInfo.AdRealAdCreatMap.put("v" + seq, map);
				
				GlobalInfo.AdCandiAdCreatVerKey.put("M" + medium.getId(), "v" + seq);
				GlobalInfo.AdRealAdCreatIdsMap.put("M" + medium.getId(), finalOrdStr);
			}
		}
		
		//
		// Ad API용 광고 / 광고 소재 리스트 - 재생 목록 형
		//
		for(KnlMedium medium : mediumList) {
			currScrIds = new ArrayList<Integer>();
			resultScrIds = null;
			
			List<OrgPlaylist> playlistList = orgService.getEffPlaylistListByMediumId(medium.getId());
			resultScrIds = invService.getMonitScreenIdsByMediumId(medium.getId());
			if (resultScrIds.size() == 0) {
				continue;
			} else if (playlistList.size() == 0) {
				candiList = adcService.getCandiAdCreativeListByMediumIdDate
						(medium.getId(), today, today);

				// 순서는 특별한 지정이 없으면, 캠페인으로 가장 나중에 등록된 것부터
				Collections.sort(candiList, new Comparator<AdcAdCreative>() {
			    	public int compare(AdcAdCreative item1, AdcAdCreative item2) {
			    		return item2.getSortID().compareTo(item1.getSortID());
			    	}
			    });
				
				int seq = SolUtil.getBgNextSeq("PM_CandiAdCreative");
				GlobalInfo.AdCandiAdCreatMap.put("v" + seq, candiList);
				for(Integer id : resultScrIds) {
					GlobalInfo.AdCandiAdCreatVerKey.put("S" + id, "v" + seq);
				}
				taskList1.add(seq);

			} else {
				
				for(OrgPlaylist playlist : playlistList) {
					currScrIds = invService.getTargetScreenOrAllIdsByPlaylistId(playlist.getId(), medium.getId());
					
					currScrIds = Util.intersection(resultScrIds, currScrIds);
					resultScrIds.removeAll(currScrIds);

					List<String> tokens = Util.tokenizeValidStr(playlist.getAdValue());
					ArrayList<AdcAdCreative> list = new ArrayList<AdcAdCreative>();
					
					for(String adCreativeId : tokens) {
						String key = "A" + adCreativeId;
						AdcAdCreative ac = null;
						if (!adCreativeMap.containsKey(key)) {
							ac = adcService.getEffAdCreative(Util.parseInt(adCreativeId), medium.getId(), today, today);
							if (ac != null) {
								adCreativeMap.put(key, ac);
							}
						}
						
						ac = adCreativeMap.get(key);
						if (ac != null) {
							list.add(ac);
						}
					}
					
					int seq = SolUtil.getBgNextSeq("PM_CandiAdCreative");
					GlobalInfo.AdCandiAdCreatMap.put("v" + seq, list);
					for(Integer id : currScrIds) {
						GlobalInfo.AdCandiAdCreatVerKey.put("S" + id, "v" + seq);
					}
					taskList1.add(seq);
					
					if (resultScrIds.size() == 0) {
						break;
					}
				}
			}
		}
		

		//
		// File API용 광고 소재 파일 리스트
		//
		for(KnlMedium medium : mediumList) {
			currScrIds = new ArrayList<Integer>();
			resultScrIds = null;
			
			// 대체 광고를 미리 조회
			List<AdcCreative> fallbackList = adcService.getValidCreativeFallbackListByMediumId(medium.getId());
			
    		String selAdType = SolUtil.getOptValue(medium.getId(), "selAd.type");
    		boolean isRealTimeSelMode = Util.isValid(selAdType) && selAdType.equals("R");
    		
			List<OrgPlaylist> playlistList = orgService.getEffPlaylistListByMediumIdDate(medium.getId(), 
					today, Util.addDays(today, 1));
			resultScrIds = invService.getMonitScreenIdsByMediumId(medium.getId());
			if (resultScrIds.size() == 0) {
				continue;
			} else if (playlistList.size() == 0 || isRealTimeSelMode) {
				candiList = adcService.getCandiAdCreativeListByMediumIdDate
						(medium.getId(), today, Util.addDays(today, 1));
				HashMap<String, List<Integer>> map = invService.getResoScreenIdMapByScreenIdIn(resultScrIds);
				Set<String> keys = map.keySet();
				for(String reso : keys) {
					List<Integer> ids = map.get(reso);

					ArrayList<String> creatFileIds = new ArrayList<String>();
					ArrayList<AdcCreatFile> list = new ArrayList<AdcCreatFile>();
					
					for(AdcAdCreative ac : candiList) {
						AdcCreatFile creatFile = adcService.getCreatFileByCreativeIdResolution(ac.getCreative().getId(), reso);
						
						// 해당 광고 소재에 이 해상도의 파일이 존재하지 않을 수도 있다!!
						if (creatFile != null) {
							// 광고에 설정된 재생 시간 고정값 수용
							if (ac.getAd().getDuration() >= 5) {
								creatFile.setFormalDurSecs(ac.getAd().getDuration());
							}
							
							String idKey = "C" + String.valueOf(ac.getCreative().getId()) + "R" + reso + "D" + ac.getAd().getDuration();
							if (!creatFileIds.contains(idKey)) {
								creatFileIds.add(idKey);
								list.add(creatFile);
							}
						}
					}
					
					for(AdcCreative c : fallbackList) {
						AdcCreatFile creatFile = adcService.getCreatFileByCreativeIdResolution(c.getId(), reso);
						
						// 해당 광고 소재에 이 해상도의 파일이 존재하지 않을 수도 있다!!
						if (creatFile != null) {
							// 광고에 설정될 수 없기 때문에 추가 설정이 필요없고,
							// 해상도 뒤 Duration의 0은 화면 설정 값 기반을 의미
							String idKey = "C" + String.valueOf(c.getId()) + "R" + reso + "D0";
							if (!creatFileIds.contains(idKey)) {
								creatFileIds.add(idKey);
								list.add(creatFile);
							}
						}
					}
					
					int seq = SolUtil.getBgNextSeq("PM_CandiCreatFile");
					GlobalInfo.FileCandiCreatFileMap.put("v" + seq, list);
					for(Integer id : ids) {
						GlobalInfo.FileCandiCreatFileVerKey.put("S" + id, "v" + seq);
					}
					taskList2.add(seq);
				}

			} else {
				
				for(OrgPlaylist playlist : playlistList) {
					currScrIds = invService.getTargetScreenOrAllIdsByPlaylistId(playlist.getId(), medium.getId());
					
					currScrIds = Util.intersection(resultScrIds, currScrIds);
					resultScrIds.removeAll(currScrIds);

					HashMap<String, List<Integer>> map = invService.getResoScreenIdMapByScreenIdIn(currScrIds);
					Set<String> keys = map.keySet();
					for(String reso : keys) {
						List<Integer> ids = map.get(reso);

						List<String> tokens = Util.tokenizeValidStr(playlist.getAdValue());
						ArrayList<String> creatFileIds = new ArrayList<String>();
						ArrayList<AdcCreatFile> list = new ArrayList<AdcCreatFile>();
						
						for(String adCreativeId : tokens) {
							String key = "A" + adCreativeId + "R" + reso;
							if (!adCreatFileMap.containsKey(key)) {
								AdcAdCreative ac = adcService.getEffAdCreative(Util.parseInt(adCreativeId), medium.getId(), today, Util.addDays(today, 1));
								AdcCreatFile creatFile = null;
								Integer duration = null;
								if (ac != null) {
									creatFile = adcService.getCreatFileByCreativeIdResolution(ac.getCreative().getId(), reso);
									duration = ac.getAd().getDuration();
									
									// 광고에 설정된 재생 시간 고정값 수용
									if (creatFile != null && duration != null && duration.intValue() >= 5) {
										creatFile.setFormalDurSecs(duration);
									}
								}

								adCreatFileMap.put(key, creatFile);
								adCreatFileDurMap.put(key,  duration);
							}
							
							AdcCreatFile acf = adCreatFileMap.get(key);
							// 해당 광고 소재에 이 해상도의 파일이 존재하지 않을 수도 있다!!
							if (acf != null) {
								Integer duration = adCreatFileDurMap.get(key);
								if (duration == null) {
									duration = 0;
								}
								String idKey = "C" + String.valueOf(acf.getCreative().getId()) + "R" + reso + "D" + duration;
								if (!creatFileIds.contains(idKey)) {
									creatFileIds.add(idKey);
									list.add(acf);
								}
							}
						}
						
						for(AdcCreative c : fallbackList) {
							AdcCreatFile creatFile = adcService.getCreatFileByCreativeIdResolution(c.getId(), reso);
							
							// 해당 광고 소재에 이 해상도의 파일이 존재하지 않을 수도 있다!!
							if (creatFile != null) {
								// 광고에 설정될 수 없기 때문에 추가 설정이 필요없고,
								// 해상도 뒤 Duration의 0은 화면 설정 값 기반을 의미
								String idKey = "C" + String.valueOf(c.getId()) + "R" + reso + "D0";
								if (!creatFileIds.contains(idKey)) {
									creatFileIds.add(idKey);
									list.add(creatFile);
								}
							}
						}
						
						int seq = SolUtil.getBgNextSeq("PM_CandiCreatFile");
						GlobalInfo.FileCandiCreatFileMap.put("v" + seq, list);
						for(Integer id : ids) {
							GlobalInfo.FileCandiCreatFileVerKey.put("S" + id, "v" + seq);
						}
						taskList2.add(seq);
					}
					
					if (resultScrIds.size() == 0) {
						break;
					}
				}
			}
		}
		
		// 파일 API가 서비스 가능하도록 활성화
		GlobalInfo.FileApiReady = true;
		
		
		String ret = "AdCreative ";
		if (taskList1.size() == 0) {
			ret += "NO";
		} else {
			ret += "ver" + taskList1.toString();
		}
		ret += ", CreatFile ";
		if (taskList2.size() == 0) {
			ret += "NO";
		} else {
			ret += "ver" + taskList2.toString();
		}
		ret += ", time: " + (new Date().getTime() - startAt);
		
		// 예: AdCreative ver[1, 2], CreatFile ver[1, 2, 3], time: 1355
		return ret;
	}
	
	/*
	// 임시
	private String getIdList1(List<AdcAdCreative> list) {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (AdcAdCreative ac : list) {
			ids.add(ac.getId());
		}
		return ids.toString();
	}
	
	private String getIdList2(List<AdcCreatFile> list) {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (AdcCreatFile cf : list) {
			ids.add(cf.getId());
		}
		return ids.toString();
	}
	*/
	
	private String refreshInvenTargetScreenList() {
		
		// 
		// 광고와 광고 소재의 타겟팅을 확인하고, 인벤 타겟팅이 있으면, 최종 화면 목록을 만들어, 메모리에 올린다.
		//
		long startAt = new Date().getTime();

		ArrayList<Integer> taskList = new ArrayList<Integer>();

		List<AdcCreative> creativeList = adcService.getValidCreativeList();
		List<AdcAd> adList = adcService.getValidAdList();

		
		// 하나라도 타겟팅이 존재하는 광고 소재만 미리 확인
		ArrayList<Integer> targetIds = new ArrayList<Integer>();
		List<Tuple> countList = adcService.getCreatTargetCountGroupByCreativeId();
		for(Tuple tuple : countList) {
			targetIds.add((Integer) tuple.get(0));
		}
		
		for(AdcCreative creative : creativeList) {
			if (targetIds.contains(creative.getId())) {
				List<Integer> idList = invService.getTargetScreenIdsByCreativeId(creative.getId());
				
				int seq = SolUtil.getBgNextSeq("CA_ScreenId");
				GlobalInfo.TgtScreenIdMap.put("v" + seq, idList);
				GlobalInfo.TgtScreenIdVerKey.put("C" + creative.getId(), "v" + seq);
				
				taskList.add(seq);
			}
		}
		
		// 하나라도 타겟팅이 존재하는 광고만 미리 확인
		targetIds.clear();
		countList = adcService.getAdTargetCountGroupByAdId();
		for(Tuple tuple : countList) {
			targetIds.add((Integer) tuple.get(0));
		}
		
		for(AdcAd ad : adList) {
			if (targetIds.contains(ad.getId())) {
				List<Integer> idList = invService.getTargetScreenIdsByAdId(ad.getId());
				
				int seq = SolUtil.getBgNextSeq("CA_ScreenId");
				GlobalInfo.TgtScreenIdMap.put("v" + seq, idList);
				GlobalInfo.TgtScreenIdVerKey.put("A" + ad.getId(), "v" + seq);
				
				taskList.add(seq);
			}
		}
		
		
		String ret = "ScreenIds ";
		if (taskList.size() == 0) {
			ret += "NO";
		} else {
			ret += "ver" + taskList.toString();
		}
		ret += ", time: " + (new Date().getTime() - startAt);
		
		// 예: ScreenIds ver[1, 2], time: 1355
		return ret;
	}
	
	
	// 광고 선택/보고 자료 로그
	private int logPlayHists() {
		
		int ret = 0;
		
		try {
			
			String path = Util.getFileProperty("log.play.hist.path");
			String prefix = Util.getFileProperty("log.play.hist.prefix");
			
			int cpm = Util.parseInt(Util.getFileProperty("log.play.hist.count.per.min"));
			
			if (Util.checkDirectory(path) && Util.isValid(path) && Util.isValid(prefix) && cpm > 0) {
				
				Util.checkDirectory(path);
				
				List<RevPlayHist> list = revService.getFirstPlayHistList(cpm);
				String prevFilename = "";
				String filename = "";
				StringBuilder sb = new StringBuilder();
				
				ArrayList<Integer> ids = new ArrayList<Integer>();
				
				boolean error = false;
				
				for(RevPlayHist hist : list) {
					filename = path + "/" + prefix + Util.toSimpleString(hist.getSelectDate(), "yyyy.MM.dd_HH") + ".txt";
					if (Util.isNotValid(prevFilename)) {
						prevFilename = filename;
					}
					
					if (!filename.equals(prevFilename)) {
						if (error = !Util.appendStrToFile(sb.toString(), prevFilename)) {
							break;
						}

						sb = new StringBuilder();
						prevFilename = filename;
					}
					
					sb.append(hist.toLogString());
					sb.append(System.lineSeparator());
					ids.add(hist.getId());
				}
				
				if (!error) {
					Util.appendStrToFile(sb.toString(), filename);
					ret = list.size();
				}
				
				revService.deleteBulkPlayHistRowsInIds(ids);
			}
			
		} catch (Exception e) {
    		logger.error("logPlayHists", e);
		}
		
		return ret;
	}
	
	private String calcStatsHourly() {
		
		// 
		// 시간당 화면 및 사이트 재생 합계를 계산한다.
		//
		long startAt = new Date().getTime();
		
		
		ArrayList<Date> dateList = new ArrayList<Date>();
		
		List<Tuple> dateTupleList = revService.getScrHourlyPlayPlayDateListByLastUpdateDate(
				Util.addDays(Util.removeTimeOfDate(new Date()), -1));
		for(Tuple tuple : dateTupleList) {
			dateList.add((Date)tuple.get(0));
		}
		
		String dateStr = "";
		int cnt = 0;
		for(Date date : dateList) {
			if (Util.isValid(dateStr)) {
				dateStr += ", ";
			}
			dateStr += Util.toSimpleString(date, "M/d");
			int upsertCnt = revService.calcDailyInvenConnectCountByPlayDate(date);
			cnt += upsertCnt;
		}
		
		String ret = "HrlyPlyTots " + cnt + " rows, ";
		ret += " date: [" + dateStr + "], ";
		ret += " time: " + (new Date().getTime() - startAt);
		
		// 예: HrlyPlyTots 0 rows,  date: [2/22, 2/23],  time: 7084
		return ret;
	}

	private String calcAdHourlyGoalValue() {
		
		List<KnlMedium> mediumList = knlService.getValidMediumList();
		Date today = Util.removeTimeOfDate(new Date());
		
		long startAt = new Date().getTime();
		int cnt = 0;

		ArrayList<String> adIds = new ArrayList<String>();
		
		for(KnlMedium medium : mediumList) {
			List<AdcAdCreative> candiList = adcService.getCandiAdCreativeListByMediumIdDate
					(medium.getId(), today, today);
			
			for(AdcAdCreative adc : candiList) {
				String key = "A" + adc.getAd().getId();
				if (!adIds.contains(key)) {
					adIds.add(key);

					List<RevScrHourlyPlay> list = revService.getScrHourlyPlayListByAdIdPlayDate(adc.getAd().getId(), today);
					for(RevScrHourlyPlay hourlyPlay : list) {
						int dailyScrCap = adc.getAd().getDailyScrCap();
						if (dailyScrCap == 0 || hourlyPlay.getSuccTotal() >= dailyScrCap) {
							continue;
						}

						int reqValues = dailyScrCap - hourlyPlay.getSuccTotal();
						InvScreen screen = hourlyPlay.getScreen();
						int remainHours = SolUtil.getRemainOpHours((Util.isValid(screen.getBizHour()) && screen.getBizHour().length() == 168)
								? screen.getBizHour() : screen.getMedium().getBizHour());
						
						int currHourCount = SolUtil.getCurrHourCount(hourlyPlay);
						int hourValue = reqValues;
						if (remainHours != 0) {
							hourValue = (reqValues + currHourCount) / remainHours;
							if (remainHours > 1) {
								hourValue += ((reqValues + currHourCount) % remainHours > 0) ? 1 : 0;
							}
						}
						
						if (hourlyPlay.getCurrHourGoal() == null || hourlyPlay.getCurrHourGoal().intValue() != hourValue) {
							
							hourlyPlay.setCurrHourGoal(hourValue);
							
							// who 컬럼 변경하지 않음
							revService.saveOrUpdate(hourlyPlay);
							cnt++;
						}
					}
				}
			}
		}
		
		return cnt + " rows, time: " + (new Date().getTime() - startAt);
	}
	
	private String saveTmpScreenStatusLines() {
		
		// 
		// 화면 분단위 상태행을 임시로 집계 후 임시 테이블에 기록한다.
		//
		long startAt = new Date().getTime();
		
		
		ArrayList<RevScrWorkTimeItem> itemList = GlobalInfo.ScrWorkTimeItemList;
		int cnt = 0;

		try {
			GlobalInfo.ScrWorkTimeItemList = new ArrayList<RevScrWorkTimeItem>();
			
			ArrayList<Integer> screenIds = new ArrayList<Integer>();
			
			for(RevScrWorkTimeItem item : itemList) {
				if (!screenIds.contains(item.getScreenId())) {
					screenIds.add(item.getScreenId());
				}
			}

			for(Integer screenId : screenIds) {
				List<RevScrWorkTimeItem> screenItems = itemList.stream()
					    .filter(t -> t.getScreenId() == screenId)
					    .collect(Collectors.toList());
				
				ArrayList<String> dates = new ArrayList<String>();
				for(RevScrWorkTimeItem scrItem : screenItems) {
					String key = Util.toSimpleString(scrItem.getDate(), "yyyyMMdd");
					if (!dates.contains(key)) {
						dates.add(key);
					}
				}
				
				for(String d : dates) {
					Date currDate = Util.parseDate(d);
					String statusLine = SolUtil.getScrStatusLine("", currDate, "2");
		    		
					for (RevScrWorkTimeItem scrItem : screenItems) {
						if (!Util.toSimpleString(scrItem.getDate(), "yyyyMMdd").equals(d)) {
							continue;
						}
						
						statusLine = SolUtil.getScrStatusLine(statusLine, scrItem.getDate(), "6", true);
					}
					
			        cnt ++;
			        sysService.insertTmpStatusLine(screenId, currDate, statusLine);
				}
			}
			
		} catch (Exception e) {
    		logger.error("saveTmpScreenStatusLines", e);
    		cnt = -1;
		}
		
		
		String ret = "ScrWorkTimeItem " + itemList.size() + " rows, ";
		ret += " statusLine " + cnt + " rows, ";
		ret += " time: " + (new Date().getTime() - startAt);
		
		return ret;
	}
	
	private String saveScreenStatusLines() {
		
		// 
		// 화면 분단위 상태행을 집계 후 기록한다.
		//
		long startAt = new Date().getTime();
		
		
		int cnt = 0;
		List<Tuple> itemList = new ArrayList<Tuple>();
		
		try {
			HashMap<String, SysScrWorkTimeCalcItem> map = new HashMap<String, SysScrWorkTimeCalcItem>();
			ArrayList<Integer> ids = new ArrayList<Integer>();
			
			//
			// SELECT SCREEN_ID, PLAY_DATE, STATUS_LINE, STATUS_LINE_ID
			//
			itemList = sysService.getTmpStatusLineTupleList();
			for(Tuple tuple : itemList) {
				int screenId = (int) tuple.get(0);
				Date playDate = (Date) tuple.get(1);
				String statusLine = (String) tuple.get(2);
				int id = (int) tuple.get(3);
				
				String key = Util.toSimpleString(playDate, "yyyyMMdd") + "S" + screenId;
				if (map.containsKey(key)) {
					SysScrWorkTimeCalcItem item = map.get(key);
					item.setStatusLine(SolUtil.mergeScrStatusLines(item.getStatusLine(), statusLine));
				} else {
					map.put(key, new SysScrWorkTimeCalcItem(screenId, playDate, statusLine));
				}
				
				ids.add(id);
			}

			ArrayList<SysScrWorkTimeCalcItem> list = new ArrayList<SysScrWorkTimeCalcItem>(map.values());
			for(SysScrWorkTimeCalcItem item : list) {
				
				Tuple currStatusLine = revService.getScrStatusLineTuple(item.getScreenId(), item.getDate());
	    		String statusLine = "";
	    		int statusLineId = -1;
	    		if (currStatusLine == null) {
	    			statusLine = item.getStatusLine();
	    		} else {
	    			statusLine = SolUtil.mergeScrStatusLines((String)currStatusLine.get(0), item.getStatusLine());
	    			statusLineId = (int)currStatusLine.get(1);
	    		}
				
		        cnt ++;
        		if (statusLineId == -1) {
        			revService.insertScrStatusLine(item.getScreenId(), item.getDate(), statusLine);
        		} else {
        			revService.updateScrStatusLine(statusLineId, statusLine);
        		}
			}
			
			sysService.deleteTmpStatusLineBulkRowsInIds(ids);
			
		} catch (Exception e) {
    		logger.error("saveScreenStatusLines", e);
    		cnt = -1;
		}
		
		
		String ret = "SysScrWorkTimeCalcItem " + itemList.size() + " rows, ";
		ret += " statusLine " + cnt + " rows, ";
		ret += " time: " + (new Date().getTime() - startAt);
		
		return ret;
	}
	
	private String saveHourlyEvents() {
		
		// 
		// 화면의 시간단위 이벤트를 집계 후 기록한다.
		//
		long startAt = new Date().getTime();
		
		
		GregorianCalendar calendar = new GregorianCalendar();
		int cnt1 = 0;
		int cnt2 = 0;
		int cnt3 = 0;
		List<Tuple> itemList = new ArrayList<Tuple>();
		
		
		try {
			ArrayList<Integer> ids = new ArrayList<Integer>();
			
			HashMap<String, SysScrEventTimeCalcItem> map = new HashMap<String, SysScrEventTimeCalcItem>();
			
			//
			// SELECT SCREEN_ID, EVENT_DATE, TYPE, HRLY_EVENT_ID
			//
			itemList = sysService.getTmpHrlyEventTupleList();
			for(Tuple tuple : itemList) {
				
				int screenId = (int) tuple.get(0);
				Date playDate = (Date) tuple.get(1);
				int type = (int) tuple.get(2);
				int id = (int) tuple.get(3);
				
				if (type == 1) {
					cnt1 ++;
				} else if (type == 2) {
					cnt2 ++;
				} else if (type == 3) {
					cnt3 ++;
				}
				
				String key = "S" + screenId + "T" + type + "D" + Util.toSimpleString(Util.removeTimeOfDate(playDate), "dd");
				if (!map.containsKey(key)) {
					map.put(key, new SysScrEventTimeCalcItem(tuple));
				}
				SysScrEventTimeCalcItem item = map.get(key);

				calendar.setTime(playDate);
				item.addCount(calendar.get(Calendar.HOUR_OF_DAY));
				
				ids.add(id);
			}
			
			
			List<SysScrEventTimeCalcItem> sumItems = new ArrayList<SysScrEventTimeCalcItem>(map.values());
			
			for(SysScrEventTimeCalcItem item : sumItems) {
				if (item.getType() == 1) {
					// 시간당 화면 실패 합계 계산
					RevScrHrlyFailTot failTot = revService.getScrHrlyFailTot(item.getScreenId(), Util.removeTimeOfDate(item.getDate()));
					if (failTot == null) {
						InvScreen screen = invService.getScreen(item.getScreenId());
						if (screen != null) {
							failTot = new RevScrHrlyFailTot(screen, Util.removeTimeOfDate(item.getDate()));
						}
					}
					
					if (failTot != null) {
						failTot.setCnt00(failTot.getCnt00() + item.getCnt00());
						failTot.setCnt01(failTot.getCnt01() + item.getCnt01());
						failTot.setCnt02(failTot.getCnt02() + item.getCnt02());
						failTot.setCnt03(failTot.getCnt03() + item.getCnt03());
						failTot.setCnt04(failTot.getCnt04() + item.getCnt04());
						failTot.setCnt05(failTot.getCnt05() + item.getCnt05());
						failTot.setCnt06(failTot.getCnt06() + item.getCnt06());
						failTot.setCnt07(failTot.getCnt07() + item.getCnt07());
						failTot.setCnt08(failTot.getCnt08() + item.getCnt08());
						failTot.setCnt09(failTot.getCnt09() + item.getCnt09());
						failTot.setCnt10(failTot.getCnt10() + item.getCnt10());
						failTot.setCnt11(failTot.getCnt11() + item.getCnt11());
						failTot.setCnt12(failTot.getCnt12() + item.getCnt12());
						failTot.setCnt13(failTot.getCnt13() + item.getCnt13());
						failTot.setCnt14(failTot.getCnt14() + item.getCnt14());
						failTot.setCnt15(failTot.getCnt15() + item.getCnt15());
						failTot.setCnt16(failTot.getCnt16() + item.getCnt16());
						failTot.setCnt17(failTot.getCnt17() + item.getCnt17());
						failTot.setCnt18(failTot.getCnt18() + item.getCnt18());
						failTot.setCnt19(failTot.getCnt19() + item.getCnt19());
						failTot.setCnt20(failTot.getCnt20() + item.getCnt20());
						failTot.setCnt21(failTot.getCnt21() + item.getCnt21());
						failTot.setCnt22(failTot.getCnt22() + item.getCnt22());
						failTot.setCnt23(failTot.getCnt23() + item.getCnt23());

						
				        failTot.calcTotal();
				        failTot.touchWho();
				        
				        revService.saveOrUpdate(failTot);
					}
				} else if (item.getType() == 2) {
					// 시간당 화면 광고없음 합계 계산
					RevScrHrlyNoAdTot noAdTot = revService.getScrHrlyNoAdTot(item.getScreenId(), Util.removeTimeOfDate(item.getDate()));
					if (noAdTot == null) {
						InvScreen screen = invService.getScreen(item.getScreenId());
						if (screen != null) {
							noAdTot = new RevScrHrlyNoAdTot(screen, Util.removeTimeOfDate(item.getDate()));
						}
					}
					
					if (noAdTot != null) {
						noAdTot.setCnt00(noAdTot.getCnt00() + item.getCnt00());
						noAdTot.setCnt01(noAdTot.getCnt01() + item.getCnt01());
						noAdTot.setCnt02(noAdTot.getCnt02() + item.getCnt02());
						noAdTot.setCnt03(noAdTot.getCnt03() + item.getCnt03());
						noAdTot.setCnt04(noAdTot.getCnt04() + item.getCnt04());
						noAdTot.setCnt05(noAdTot.getCnt05() + item.getCnt05());
						noAdTot.setCnt06(noAdTot.getCnt06() + item.getCnt06());
						noAdTot.setCnt07(noAdTot.getCnt07() + item.getCnt07());
						noAdTot.setCnt08(noAdTot.getCnt08() + item.getCnt08());
						noAdTot.setCnt09(noAdTot.getCnt09() + item.getCnt09());
						noAdTot.setCnt10(noAdTot.getCnt10() + item.getCnt10());
						noAdTot.setCnt11(noAdTot.getCnt11() + item.getCnt11());
						noAdTot.setCnt12(noAdTot.getCnt12() + item.getCnt12());
						noAdTot.setCnt13(noAdTot.getCnt13() + item.getCnt13());
						noAdTot.setCnt14(noAdTot.getCnt14() + item.getCnt14());
						noAdTot.setCnt15(noAdTot.getCnt15() + item.getCnt15());
						noAdTot.setCnt16(noAdTot.getCnt16() + item.getCnt16());
						noAdTot.setCnt17(noAdTot.getCnt17() + item.getCnt17());
						noAdTot.setCnt18(noAdTot.getCnt18() + item.getCnt18());
						noAdTot.setCnt19(noAdTot.getCnt19() + item.getCnt19());
						noAdTot.setCnt20(noAdTot.getCnt20() + item.getCnt20());
						noAdTot.setCnt21(noAdTot.getCnt21() + item.getCnt21());
						noAdTot.setCnt22(noAdTot.getCnt22() + item.getCnt22());
						noAdTot.setCnt23(noAdTot.getCnt23() + item.getCnt23());

						
				        noAdTot.calcTotal();
				        noAdTot.touchWho();
				        
				        revService.saveOrUpdate(noAdTot);
					}
				} else if (item.getType() == 3) {
					// 시간당 화면 대체광고 합계 계산
					RevScrHrlyFbTot fbTot = revService.getScrHrlyFbTot(item.getScreenId(), Util.removeTimeOfDate(item.getDate()));
					if (fbTot == null) {
						InvScreen screen = invService.getScreen(item.getScreenId());
						if (screen != null) {
							fbTot = new RevScrHrlyFbTot(screen, Util.removeTimeOfDate(item.getDate()));
						}
					}
					
					if (fbTot != null) {
						fbTot.setCnt00(fbTot.getCnt00() + item.getCnt00());
						fbTot.setCnt01(fbTot.getCnt01() + item.getCnt01());
						fbTot.setCnt02(fbTot.getCnt02() + item.getCnt02());
						fbTot.setCnt03(fbTot.getCnt03() + item.getCnt03());
						fbTot.setCnt04(fbTot.getCnt04() + item.getCnt04());
						fbTot.setCnt05(fbTot.getCnt05() + item.getCnt05());
						fbTot.setCnt06(fbTot.getCnt06() + item.getCnt06());
						fbTot.setCnt07(fbTot.getCnt07() + item.getCnt07());
						fbTot.setCnt08(fbTot.getCnt08() + item.getCnt08());
						fbTot.setCnt09(fbTot.getCnt09() + item.getCnt09());
						fbTot.setCnt10(fbTot.getCnt10() + item.getCnt10());
						fbTot.setCnt11(fbTot.getCnt11() + item.getCnt11());
						fbTot.setCnt12(fbTot.getCnt12() + item.getCnt12());
						fbTot.setCnt13(fbTot.getCnt13() + item.getCnt13());
						fbTot.setCnt14(fbTot.getCnt14() + item.getCnt14());
						fbTot.setCnt15(fbTot.getCnt15() + item.getCnt15());
						fbTot.setCnt16(fbTot.getCnt16() + item.getCnt16());
						fbTot.setCnt17(fbTot.getCnt17() + item.getCnt17());
						fbTot.setCnt18(fbTot.getCnt18() + item.getCnt18());
						fbTot.setCnt19(fbTot.getCnt19() + item.getCnt19());
						fbTot.setCnt20(fbTot.getCnt20() + item.getCnt20());
						fbTot.setCnt21(fbTot.getCnt21() + item.getCnt21());
						fbTot.setCnt22(fbTot.getCnt22() + item.getCnt22());
						fbTot.setCnt23(fbTot.getCnt23() + item.getCnt23());

						
				        fbTot.calcTotal();
				        fbTot.touchWho();
				        
				        revService.saveOrUpdate(fbTot);
					}
				}
			}
			
			sysService.deleteTmpHrlyEventBulkRowsInIds(ids);
			
		} catch (Exception e) {
    		logger.error("saveHourlyEvents", e);
    		cnt1 = -1;
    		cnt2 = -1;
    		cnt3 = -1;
		}
		
		
		String ret = "SysTmpHrlyEvent " + itemList.size() + " rows, ";
		ret += " FailTot " + cnt1 + " rows, ";
		ret += " NoAdTot " + cnt2 + " rows, ";
		ret += " FbTot " + cnt3 + " rows, ";
		ret += " time: " + (new Date().getTime() - startAt);
		
		return ret;
	}
	
	private String calcTmpObjEvents() {
		
		// 
		// 개체 최근 변경일시를 임시 테이블에 기록한다.
		//
		long startAt = new Date().getTime();
		
		
		ArrayList<RevObjEventTimeItem> itemList = GlobalInfo.ObjEventTimeItemList;
		int cnt = 0;

		try {
			GlobalInfo.ObjEventTimeItemList = new ArrayList<RevObjEventTimeItem>();
			
			ArrayList<Integer> screenIds = new ArrayList<Integer>();
			
			for(RevObjEventTimeItem item : itemList) {
		        cnt ++;
		        
		        // DB 테이블로 저장하지 않고, 메모리 상에 집계 작업을 진행
		        //sysService.insertTmpObjEvent(item.getObjId(), item.getDate(), item.getType());
		        String typeS = "";
		        if (item.getType() >= 10 && item.getType() < 20) {
		        	typeS = "S";
		        } else if (item.getType() >= 20) {
		        	typeS = "C";
		        }
		        
		        if (Util.isValid(typeS)) {
			        String key = typeS + item.getObjId();
					
					if (Util.isValid(typeS) && !GlobalInfo.ObjTouchMap.containsKey(key)) {
						GlobalInfo.ObjTouchMap.put(key, new SysObjEventTimeCalcItem(typeS, item.getObjId(), item.getDate(), item.getType()));
					}
					SysObjEventTimeCalcItem cItem = GlobalInfo.ObjTouchMap.get(key);

					if (cItem != null) {
						if (item.getType() == 11 || item.getType() == 21) {
							if (cItem.getDate1() == null || cItem.getDate1().before(item.getDate())) {
								cItem.setDate1(item.getDate());
								cItem.setLastUpdateDate(new Date());
							}
						} else if (item.getType() == 12) {
							if (cItem.getDate2() == null || cItem.getDate2().before(item.getDate())) {
								cItem.setDate2(item.getDate());
								cItem.setLastUpdateDate(new Date());
							}
						} else if (item.getType() == 13) {
							if (cItem.getDate3() == null || cItem.getDate3().before(item.getDate())) {
								cItem.setDate3(item.getDate());
								cItem.setLastUpdateDate(new Date());
							}
						} else if (item.getType() == 14) {
							if (cItem.getDate4() == null || cItem.getDate4().before(item.getDate())) {
								cItem.setDate4(item.getDate());
								cItem.setLastUpdateDate(new Date());
							}
						}
					}
		        }
			}
			
		} catch (Exception e) {
    		logger.error("calcTmpObjEvents", e);
    		cnt = -1;
		}
		
		
		String ret = "ObjEventTimeItem " + itemList.size() + " rows, ";
		ret += " objEvent " + cnt + " rows, ";
		ret += " calcMap "  + GlobalInfo.ObjTouchMap.size() + " items, ";
		ret += " time: " + (new Date().getTime() - startAt);
		
		return ret;
	}
	
	private String saveObjEvents() {
		
		// 
		// 개체 최근 변경일시를 집계 후 기록한다.
		//
		long startAt = new Date().getTime();
		
		
		GregorianCalendar calendar = new GregorianCalendar();
		int cnt11 = 0;
		int cnt12 = 0;
		int cnt13 = 0;
		int cnt14 = 0;
		int cnt21 = 0;
		List<SysObjEventTimeCalcItem> itemList = new ArrayList<SysObjEventTimeCalcItem>();
		
		try {
			HashMap<String, SysObjEventTimeCalcItem> currMap = GlobalInfo.ObjTouchMap;
			GlobalInfo.ObjTouchMap = new HashMap<String, SysObjEventTimeCalcItem>();
			
			itemList = new ArrayList<SysObjEventTimeCalcItem>(currMap.values());
			
			for(SysObjEventTimeCalcItem item : itemList) {
				if (item.getType().equals("S")) {
					RevObjTouch objTouch = revService.getObjTouch("S", item.getObjId());
					if (objTouch == null) {
						objTouch = new RevObjTouch("S", item.getObjId());
					}
					
					if (objTouch.getDate1() == null) {
						objTouch.setDate1(item.getDate1());
					} else if (item.getDate1() != null && objTouch.getDate1().before(item.getDate1())) {
						objTouch.setDate1(item.getDate1());
						cnt11++;
					}
					
					if (objTouch.getDate2() == null) {
						objTouch.setDate2(item.getDate2());
					} else if (item.getDate2() != null && objTouch.getDate2().before(item.getDate2())) {
						objTouch.setDate2(item.getDate2());
						cnt12++;
					}
					
					if (objTouch.getDate3() == null) {
						objTouch.setDate3(item.getDate3());
					} else if (item.getDate3() != null && objTouch.getDate3().before(item.getDate3())) {
						objTouch.setDate3(item.getDate3());
						cnt13++;
					}
					
					if (objTouch.getDate4() == null) {
						objTouch.setDate4(item.getDate4());
					} else if (item.getDate4() != null && objTouch.getDate4().before(item.getDate4())) {
						objTouch.setDate4(item.getDate4());
						cnt14++;
					}
					
					objTouch.touchWho();
					revService.saveOrUpdate(objTouch);

				} else if (item.getType().equals("C")) {
					
					RevObjTouch objTouch = revService.getObjTouch("C", item.getObjId());
					if (objTouch == null) {
						objTouch = new RevObjTouch("C", item.getObjId());
					}
					
					if (objTouch.getDate1() == null) {
						objTouch.setDate1(item.getDate1());
					} else if (item.getDate1() != null && objTouch.getDate1().before(item.getDate1())) {
						objTouch.setDate1(item.getDate1());
						cnt21++;
					}
					
					objTouch.touchWho();
					revService.saveOrUpdate(objTouch);
				}
			}
			
		} catch (Exception e) {
    		logger.error("saveObjEvents", e);
    		cnt11 = -1;
    		cnt12 = -1;
    		cnt13 = -1;
    		cnt14 = -1;
    		cnt21 = -1;
		}
		
		
		String ret = "Touch Objs " + itemList.size() + " rows, ";
		ret += " File_API " + cnt11 + " items, ";
		ret += " Ad_API " + cnt12 + " items, ";
		ret += " Report_API " + cnt13 + " items, ";
		ret += " RetryReport_API " + cnt14 + " items, ";
		ret += " Creative " + cnt21 + " items, ";
		ret += " time: " + (new Date().getTime() - startAt);
		
		return ret;
	}
	
	private void setAdTupleValues(RevAdHourlyPlay hp, Tuple tuple) {
		
		hp.setCnt00(((BigDecimal) tuple.get(1)).intValue());
		hp.setCnt01(((BigDecimal) tuple.get(2)).intValue());
		hp.setCnt02(((BigDecimal) tuple.get(3)).intValue());
		hp.setCnt03(((BigDecimal) tuple.get(4)).intValue());
		hp.setCnt04(((BigDecimal) tuple.get(5)).intValue());
		hp.setCnt05(((BigDecimal) tuple.get(6)).intValue());
		hp.setCnt06(((BigDecimal) tuple.get(7)).intValue());
		hp.setCnt07(((BigDecimal) tuple.get(8)).intValue());
		hp.setCnt08(((BigDecimal) tuple.get(9)).intValue());
		hp.setCnt09(((BigDecimal) tuple.get(10)).intValue());
		hp.setCnt10(((BigDecimal) tuple.get(11)).intValue());
		hp.setCnt11(((BigDecimal) tuple.get(12)).intValue());
		hp.setCnt12(((BigDecimal) tuple.get(13)).intValue());
		hp.setCnt13(((BigDecimal) tuple.get(14)).intValue());
		hp.setCnt14(((BigDecimal) tuple.get(15)).intValue());
		hp.setCnt15(((BigDecimal) tuple.get(16)).intValue());
		hp.setCnt16(((BigDecimal) tuple.get(17)).intValue());
		hp.setCnt17(((BigDecimal) tuple.get(18)).intValue());
		hp.setCnt18(((BigDecimal) tuple.get(19)).intValue());
		hp.setCnt19(((BigDecimal) tuple.get(20)).intValue());
		hp.setCnt20(((BigDecimal) tuple.get(21)).intValue());
		hp.setCnt21(((BigDecimal) tuple.get(22)).intValue());
		hp.setCnt22(((BigDecimal) tuple.get(23)).intValue());
		hp.setCnt23(((BigDecimal) tuple.get(24)).intValue());

		hp.setFailTotal(((BigDecimal) tuple.get(25)).intValue());
		hp.setCntScreen(((BigInteger) tuple.get(26)).intValue());
		
		hp.calcTotal();
	}
	
	private void setCreatTupleValues(RevCreatHourlyPlay hp, Tuple tuple) {
		
		hp.setCnt00(((BigDecimal) tuple.get(1)).intValue());
		hp.setCnt01(((BigDecimal) tuple.get(2)).intValue());
		hp.setCnt02(((BigDecimal) tuple.get(3)).intValue());
		hp.setCnt03(((BigDecimal) tuple.get(4)).intValue());
		hp.setCnt04(((BigDecimal) tuple.get(5)).intValue());
		hp.setCnt05(((BigDecimal) tuple.get(6)).intValue());
		hp.setCnt06(((BigDecimal) tuple.get(7)).intValue());
		hp.setCnt07(((BigDecimal) tuple.get(8)).intValue());
		hp.setCnt08(((BigDecimal) tuple.get(9)).intValue());
		hp.setCnt09(((BigDecimal) tuple.get(10)).intValue());
		hp.setCnt10(((BigDecimal) tuple.get(11)).intValue());
		hp.setCnt11(((BigDecimal) tuple.get(12)).intValue());
		hp.setCnt12(((BigDecimal) tuple.get(13)).intValue());
		hp.setCnt13(((BigDecimal) tuple.get(14)).intValue());
		hp.setCnt14(((BigDecimal) tuple.get(15)).intValue());
		hp.setCnt15(((BigDecimal) tuple.get(16)).intValue());
		hp.setCnt16(((BigDecimal) tuple.get(17)).intValue());
		hp.setCnt17(((BigDecimal) tuple.get(18)).intValue());
		hp.setCnt18(((BigDecimal) tuple.get(19)).intValue());
		hp.setCnt19(((BigDecimal) tuple.get(20)).intValue());
		hp.setCnt20(((BigDecimal) tuple.get(21)).intValue());
		hp.setCnt21(((BigDecimal) tuple.get(22)).intValue());
		hp.setCnt22(((BigDecimal) tuple.get(23)).intValue());
		hp.setCnt23(((BigDecimal) tuple.get(24)).intValue());

		hp.setFailTotal(((BigDecimal) tuple.get(25)).intValue());
		hp.setCntScreen(((BigInteger) tuple.get(26)).intValue());
		
		hp.calcTotal();
	}
	
	private List<Integer> proceedOneDayAdImpressCount(Date playDate) {
		
		int addCnt = 0;
		int updCnt = 0;
		
		List<Tuple> list = revService.getScrHourlyPlayStatGroupByAdPlayDate(playDate);
		for(Tuple tuple : list) {
			AdcAd ad = adcService.getAd((int) tuple.get(0));
			if (ad != null) {
				RevAdHourlyPlay hp = revService.getAdHourlyPlay(ad, playDate);
				if (hp == null) {
					hp = new RevAdHourlyPlay(playDate, ad);
					addCnt++;
				} else {
					hp.touchWho();
					updCnt ++;
				}
				setAdTupleValues(hp, tuple);

				revService.saveOrUpdate(hp);
			}
		}
		
		return Arrays.asList(addCnt, updCnt);
	}
	
	private List<Integer> proceedOneDayCreativeImpressCount(Date playDate) {
		
		int addCnt = 0;
		int updCnt = 0;
		
		List<Tuple> list = revService.getScrHourlyPlayStatGroupByCreatPlayDate(playDate);
		for(Tuple tuple : list) {
			AdcCreative creative = adcService.getCreative((int) tuple.get(0));
			if (creative != null) {
				RevCreatHourlyPlay hp = revService.getCreatHourlyPlay(creative, playDate);
				if (hp == null) {
					hp = new RevCreatHourlyPlay(playDate, creative);
					addCnt++;
				} else {
					hp.touchWho();
					updCnt ++;
				}
				setCreatTupleValues(hp, tuple);

				revService.saveOrUpdate(hp);
			}
		}
		
		return Arrays.asList(addCnt, updCnt);
	}
	
	private String calcDailyAdImpressionStat() {
		
		// 
		// 광고별, 광고 소재별 하루 송출량을 계산한다.
		//
		//   - 오늘 자료(D-0)는 5분 단위(5분 단위 매번)
		//   - 어제 자료(D-1)는 1시간 단위(5분 단위 12번째)
		//   - 2일전 자료(D-2)는 6시간 단위(5분 단위 72번째)
		//
		long startAt = new Date().getTime();
		
		int cntAdUpd = 0;
		int cntAdIns = 0;
		int cntCreatUpd = 0;
		int cntCreatIns = 0;
		
		
		Date day0 = Util.removeTimeOfDate(new Date());
		Date day1 = Util.addDays(day0, -1);
		Date day2 = Util.addDays(day0, -2);
		
		boolean day1Included = false;
		boolean day2Included = false;

		List<Integer> retList = new ArrayList<Integer>();
		
		try {
			adCalcCount++;
			
			if (adCalcCount % 12 == 0) {
				day1Included = true;
				
				retList = proceedOneDayAdImpressCount(day1);
				if (retList != null && retList.size() == 2) {
					cntAdIns += retList.get(0);
					cntAdUpd += retList.get(1);
				}
				
				retList = proceedOneDayCreativeImpressCount(day1);
				if (retList != null && retList.size() == 2) {
					cntCreatIns += retList.get(0);
					cntCreatUpd += retList.get(1);
				}
			}
			if (adCalcCount % 72 == 0) {
				day2Included = true;
				
				retList = proceedOneDayAdImpressCount(day2);
				if (retList != null && retList.size() == 2) {
					cntAdIns += retList.get(0);
					cntAdUpd += retList.get(1);
				}
				
				retList = proceedOneDayCreativeImpressCount(day2);
				if (retList != null && retList.size() == 2) {
					cntCreatIns += retList.get(0);
					cntCreatUpd += retList.get(1);
				}
			}
			
			
			retList = proceedOneDayAdImpressCount(day0);
			if (retList != null && retList.size() == 2) {
				cntAdIns += retList.get(0);
				cntAdUpd += retList.get(1);
			}

			retList = proceedOneDayCreativeImpressCount(day0);
			if (retList != null && retList.size() == 2) {
				cntCreatIns += retList.get(0);
				cntCreatUpd += retList.get(1);
			}
			
		} catch (Exception e) {
			logger.error("calcDailyAdImpressionStat", e);
			cntAdUpd = -1;
			cntAdIns = -1;
			cntCreatUpd = -1;
			cntCreatIns = -1;
		}
		
		String ret = "Daily Ad Stat, ins " + cntAdIns + " rows,";
		ret += " upd " + cntAdUpd + " rows, ";
		ret += " Daily Creative Stat, ins " + cntCreatIns + " rows, ";
		ret += " upd " + cntCreatUpd + " rows, ";
		
		if (day1Included) {
			ret += " day-1, ";
		}
		if (day2Included) {
			ret += " day-2, ";
		}
		
		ret += " time: " + (new Date().getTime() - startAt);
		
		return ret;
	}
}
