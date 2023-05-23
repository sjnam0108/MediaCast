package net.doohad.controllers.api;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Tuple;
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
import net.doohad.models.rev.RevFbSelCache;
import net.doohad.models.rev.RevPlayHist;
import net.doohad.models.service.AdcService;
import net.doohad.models.service.InvService;
import net.doohad.models.service.RevService;
import net.doohad.models.service.SysService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.adc.AdcJsonFileObject;
import net.doohad.viewmodels.rev.RevObjEventTimeItem;
import net.doohad.viewmodels.rev.RevScrHrlyPlyAdStatItem;
import net.doohad.viewmodels.rev.RevScrWorkTimeItem;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 현재 광고 API 컨트롤러
 */
@Controller("api-ad-controller")
@RequestMapping(value="")
public class AdController {
	
	private static final Logger logger = LoggerFactory.getLogger(AdController.class);


    @Autowired 
    private InvService invService;

    @Autowired 
    private AdcService adcService;

    @Autowired 
    private RevService revService;

    @Autowired 
    private SysService sysService;
	
    
    /**
	 * 현재 광고 API
	 */
    @RequestMapping(value = {"/v1/ad/{displayID}"}, method = RequestMethod.GET)
    public void processApiAd(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap) {
    	
    	String displayID = Util.parseString(pathMap.get("displayID"));
    
    	String apiKey = Util.parseString(paramMap.get("apikey"));
    	String test = Util.parseString(paramMap.get("test"));
    	boolean testMode = Util.isValid(test) && test.toLowerCase().equals("y");
    	
    	int cnt = Util.parseInt(paramMap.get("cnt"), 1);
    	if (cnt < 1 || cnt > 15) {
    		cnt = 1;
    	}
    	
    	
    	int statusCode = 0;
    	String message = "Ok";
    	String localMessage = "Ok";
    	
    	JSONObject obj = new JSONObject();
    	InvScreen screen = null;
    	
    	if (Util.isNotValid(apiKey) || Util.isNotValid(displayID)) {
    		
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

    	
    	Date now = new Date();
    	
    	//
    	// 대상의 매체 화면이 정상적으로 광고 방송을 하려면,
    	//
    	//
    	//   매체 화면 기본 검증 사항:
    	//
    	//   1. 유효기간내
    	//   2. activeStatus == true
    	//   3. adServerAvailable == true
    	//   4. 매체 화면의 운영 시간
    	//
    	//
    	//   쿼리에서의 제한 항목:
    	//
    	//   11. 광고의 상태 코드는 A/R/C 중 하나
    	//       - 예약이나 완료건에 대해서도 통과. 이유는 상태 A/R/C는 현재 날짜에 따른 가변값으로 
    	//         오늘 날짜에 대한 검증이 진행전이라면 선택 오류가 발생할 수도 있기 때문
    	//       - 예약이나 완료건에 대한 필터링은 아래 12에서 별도로 진행됨
    	//   12. 광고의 시작일과 종료일 내
    	//   13. 광고의 pause == false
    	//   14. 광고의 deleted == false
    	//   15. 광고 소재의 상태 코드는 A
    	//   16. 광고 소재의 paused == false
    	//   17. 광고 소재의 deleted == false
    	//
    	//
    	//   광고 소재 파일의 검증 항목:
    	//
    	//   21. 파일 해상도: 화면해상도가 광고 소재 파일의 해상도와 일치하거나 적합
    	//   22. 파일 미디어 유형: 광고 소재 파일의 미디어 유형(동영상, 이미지 등)이 가능하도록 매체 화면 설정 필요
    	//   23. 파일 재생시간: 파일 재생시간이 광고/매체/화면의 설정에 부합
    	//
    	//
    	//   광고 소재의 검증 항목:
    	//
    	//   31. 인벤 타겟팅이 없거나, 있다면 포함되어야 함
    	//   32. 시간 타겟팅이 없거나, 있다면 대상 시간이어야 함
    	//
    	//
    	//   광고의 검증 항목:
    	//
    	//   41. 인벤 타겟팅이 없거나, 있다면 포함되어야 함
    	//   42. 시간 타겟팅이 없거나, 있다면 대상 시간이어야 함
    	//
    	//
    	//   광고 송출 이력의 검증 항목:
    	//
    	//   51. 동일 광고 송출 금지 시간이 없거나, 그 시간 초과되어야 함
    	//   52. 동일 광고주 송출 금지 시간이 없거나, 그 시간 초과되어야 함
    	//
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
    		}
    		
    		// 화면만 확정되면 바로 요청일시 저장
    		if (!testMode) {
    			
    			// 개체 이벤트 처리(Ad API 호출 시간 등록)
    			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(screen.getId(), now, 12));
        		
        		// 상태 라인 처리 위해 공용리스트에 추가
        		GlobalInfo.ScrWorkTimeItemList.add(new RevScrWorkTimeItem(screen.getId(), now));
        		
        		if (!screen.getSite().isServed()) {
            		screen.getSite().setServed(true);
            		invService.saveOrUpdate(screen.getSite());
        		}
    		}
    	}
    	
		obj.put("code", statusCode);
		obj.put("message", message);
		obj.put("local_message", localMessage);

		
    	if (statusCode == 0 && screen != null) {
			JSONArray ads = new JSONArray();

			// 이후에 사용할 목적으로 보관
			ArrayList<AdcCreatFile> mapCreatFileList = null; 
			String key1 = GlobalInfo.FileCandiCreatFileVerKey.get("S" + screen.getId());
    		if (Util.isValid(key1)) {
    			mapCreatFileList = GlobalInfo.FileCandiCreatFileMap.get(key1);
    		}
					
			//
			// 매체 화면의 운영 시간 확인
			//
    		boolean isCurrentOpHours = SolUtil.isCurrentOpHours((Util.isValid(screen.getBizHour()) && screen.getBizHour().length() == 168)
					? screen.getBizHour() : screen.getMedium().getBizHour());
			if (isCurrentOpHours) {
				
				// 매체 화면의 운영 시간 내
				//
				
				
	    		List<AdcAdCreative> candiList = null;
	    		HashMap<String, AdcAdCreative> candiMap = null;
	    		
	    		String key = "";
	    		String selAdType = SolUtil.getOptValue(screen.getMedium().getId(), "selAd.type");
	    		boolean isRealTimeSelMode = Util.isValid(selAdType) && selAdType.equals("R");
	    		
	    		if (isRealTimeSelMode) {
		    		key = GlobalInfo.AdCandiAdCreatVerKey.get("M" + screen.getMedium().getId());
		    		if (Util.isValid(key)) {
		    			candiMap = GlobalInfo.AdRealAdCreatMap.get(key);
		    		}
	    		} else {
		    		key = GlobalInfo.AdCandiAdCreatVerKey.get("S" + screen.getId());
		    		if (Util.isValid(key)) {
		    			candiList = GlobalInfo.AdCandiAdCreatMap.get(key);
		    		}
	    		}
	    		

	    		if ((candiList != null && candiList.size() > 0) || (isRealTimeSelMode && candiMap != null)) {
	    			//
	    			// 복수개의 현재 광고 서비스 중지
	    			//
					AdcCreatFile creatFile = null;
			    	if (screen != null && Util.isValid(screen.getResolution())) {

			    		AdcAdCreative adCreative = null;
			    		AdcJsonFileObject jsonFileObject = null;
			    		
			    		ArrayList<AdcAdCreative> orderedList = new ArrayList<AdcAdCreative>();

			    		
			    		if (isRealTimeSelMode) {
			    			
			    			String orderStr = SolUtil.selectAdSeqList(GlobalInfo.AdRealAdCreatIdsMap.get("M" + screen.getMedium().getId()));
				    		List<String> seqList = Util.tokenizeValidStr(orderStr);
				    		for(String s : seqList) {
				    			AdcAdCreative adCreat = candiMap.get("AC" + s);
				    			if (adCreat != null) {
					    			orderedList.add(adCreat);
				    			}
				    		}
			    		} else {
			    			
			    			//
			    			// tuple: SELECT AD_SEL_CACHE_ID, SEL_DATE, AD_CREATIVE_ID
			    			//
				    		Tuple tuple = revService.getLastAdSelCacheTupleByScreenId(screen.getId());
				    		
				    		// input:
				    		//  RevAdSelect last : null 일수도
				    		//  candiList
				    		//

				    		// 다음에 처음 시도할 리스트 인덱스
				    		int idx = 0;
				    		int tmp = -1;
				    		if (tuple != null) {

				    			int adCreativeId = (Integer) tuple.get(2);
				    			for(AdcAdCreative adC : candiList) {
				    				tmp++;
				    				if (adCreativeId == adC.getId()) {
				    					tmp++;
				    					if (tmp >= candiList.size()) {
				    						tmp = 0;
				    					}
				    					idx = tmp;
			    						break;
				    				}
				    			}
				    		}


				    		// 비교 대상을 하나의 리스트로 정리
				    		if (candiList.size() == 1) {
				    			orderedList.add(candiList.get(0));
				    		} else if (idx == candiList.size() - 1) {
				    			orderedList.add(candiList.get(idx));
				    			for(int j = 0; j < candiList.size() - 1; j ++) {
				    				orderedList.add(candiList.get(j));
				    			}
				    		} else if (idx == 0) {
				    			for(int j = 0; j < candiList.size(); j ++) {
				    				orderedList.add(candiList.get(j));
				    			}
				    		} else {
				    			for(int j = idx; j < candiList.size(); j ++) {
				    				orderedList.add(candiList.get(j));
				    			}
				    			for(int j = 0; j < idx; j++) {
				    				orderedList.add(candiList.get(j));
				    			}
				    		}
			    		}

			    		
			    		// 해당 화면에 대한 오늘의 총 송출 자료 획득
			    		List<Tuple> shpList = revService.getScrHourlyPlayAdStatListByScreenIdPlayDate(screen.getId(), 
			    				Util.removeTimeOfDate(new Date()));
			    		HashMap<String, RevScrHrlyPlyAdStatItem> adStatMap = new HashMap<String, RevScrHrlyPlyAdStatItem>();
			    		for(Tuple tuple : shpList) {
			    			RevScrHrlyPlyAdStatItem item = new RevScrHrlyPlyAdStatItem(tuple);
			    			adStatMap.put("A" + item.getAdId(), item);
			    		}
			    		
			    		// 동일 광고 및 광고주 송출 금지 시간 자료 획득
			    		//
			    		// SELECT CCH.AD_SEL_CACHE_ID, CCH.SEL_DATE, CCH.AD_CREATIVE_ID, AC.AD_ID, C.ADVERTISER_ID
			    		//
			    		List<Tuple> cacheList = revService.getAdSelCacheTupleListByScreenId(screen.getId());
			    		
			    		for(AdcAdCreative adC : orderedList) {
			    			
			    			// 광고의 화면당 하루 노출한도 확인
			    			if (adC.getAd().getDailyScrCap() > 0) {
			    				RevScrHrlyPlyAdStatItem adStatItem = adStatMap.get("A" + adC.getAd().getId());
				    			if (adStatItem != null) {
				    				// 화면당 하루 노출횟수 초과 확인
			    					if (adStatItem.getSuccTot() >= adC.getAd().getDailyScrCap()) {
			    						continue;
			    					}
			    					// 시간당 횟수 초과 학인
			    					if (adStatItem.getCurrHourGoal() != null && adStatItem.getCnt() >= adStatItem.getCurrHourGoal().intValue()) {
			    						continue;
			    					}
			    					// 시간(60분) 분할 대비 노출 횟수 확인
			    					//   - 진행 성공 횟수: adStatItem.getCnt()
			    					//   - 이번 시도 차수: adStatItem.getCnt() + 1
			    					//   - 목표 횟수: adStatItem.getCurrHourGoal().intValue()
			    					//   - 현재 분: getMinutes(now)
			    					//
			    					// 식: (60분 % 목표횟수) x (이번 시도 차수 - 1) <= curr Mins 일 경우 진행
			    					//
			    					if (adStatItem.getCurrHourGoal() != null) {
				    					if ((60f / (float)adStatItem.getCurrHourGoal().intValue()) * (float)adStatItem.getCnt() > getMinutes(now)) {
				    						continue;
				    					}
			    					}
				    			}
			    			}
			    			
			    			// 동일 광고 및 광고주 송출 금지 시간 확인
			    			if (adC.getAd().getFreqCap() > 0) {
			    				Date date = getLastCacheDate(cacheList, adC.getAd().getId(), -1);
			    				if (date != null && Util.addSeconds(date, adC.getAd().getFreqCap()).after(now)) {
			    					continue;
			    				}
			    			}
			    			if (adC.getAd().getCampaign().getFreqCap() > 0) {
			    				Date date = getLastCacheDate(cacheList, -1, adC.getCreative().getAdvertiser().getId());
			    				if (date != null && Util.addSeconds(date, adC.getAd().getCampaign().getFreqCap()).after(now)) {
			    					continue;
			    				}
			    			}
			    			//-
			    			
			    			creatFile = null;
			    			if (mapCreatFileList != null) {
			        			for(AdcCreatFile acf : mapCreatFileList) {
			        				if (acf.getCreative().getId() == adC.getCreative().getId()) {
			        					creatFile = acf;
			        					break;
			        				}
			        			}
			    			}
			        		
			        		if (creatFile == null) {
				    			creatFile = adcService.getCreatFileByCreativeIdResolution(adC.getCreative().getId(), screen.getResolution());
			        		}
			        		
			    			if (creatFile != null) {
			    				
			    				
			    				// 화면의 미디어 유형 수용 확인
			    				if (creatFile.getMediaType().equals("V") && !screen.isVideoAllowed()) {
			    					continue;
			    				} else if (creatFile.getMediaType().equals("I") && !screen.isImageAllowed()) {
			    					continue;
			    				}

			    				
			    				// 광고 소재 인벤 타겟팅 확인
			    				//
			    				//   key가 없다는 것은 해당 소재 타겟팅이 없음을 의미
			    				//   list.size() > 0: 타겟팅에 포함되는 화면 수(꼭 현재 화면이 포함된다는 보장 없음)
			    				//   list.size() == 0: 타겟팅은 되었으나, 그 대상 화면 수가 0
			    				//
			    				key = GlobalInfo.TgtScreenIdVerKey.get("C" + creatFile.getCreative().getId());
			    				if (Util.isValid(key)) {
			    					List<Integer> idList = GlobalInfo.TgtScreenIdMap.get(key);
			    					if (!idList.contains(screen.getId())) {
			    						continue;
			    					}
			    				} else {
			    					// 소재에 대한 인벤 타겟팅이 없으므로 "통과!!"
			    				}
			    				
			    				
			    				// 광고 소재 시간 타겟팅 확인
			    				if (Util.isValid(creatFile.getCreative().getExpHour()) && creatFile.getCreative().getExpHour().length() == 168) {
			    					if (!SolUtil.isCurrentOpHours(creatFile.getCreative().getExpHour())) {
			    						continue;
			    					}
			    				} else {
			    					// 소재에 대한 시간 타겟팅이 없으므로 "통과!!"
			    				}

			    				
			    				// 광고 인벤 타겟팅 확인
			    				//
			    				//   key가 없다는 것은 해당 광고 타겟팅이 없음을 의미
			    				//   list.size() > 0: 타겟팅에 포함되는 화면 수(꼭 현재 화면이 포함된다는 보장 없음)
			    				//   list.size() == 0: 타겟팅은 되었으나, 그 대상 화면 수가 0
			    				//
			    				key = GlobalInfo.TgtScreenIdVerKey.get("A" + adC.getAd().getId());
			    				if (Util.isValid(key)) {
			    					List<Integer> idList = GlobalInfo.TgtScreenIdMap.get(key);
			    					if (!idList.contains(screen.getId())) {
			    						continue;
			    					}
			    				} else {
			    					// 광고에 대한 인벤 타겟팅이 없으므로 "통과!!"
			    				}
			    				
			    				
			    				// 광고 시간 타겟팅 확인
			    				if (Util.isValid(adC.getAd().getExpHour()) && adC.getAd().getExpHour().length() == 168) {
			    					if (!SolUtil.isCurrentOpHours(adC.getAd().getExpHour())) {
			    						continue;
			    					}
			    				} else {
			    					// 광고에 대한 시간 타겟팅이 없으므로 "통과!!"
			    				}
			    				
			    				
								jsonFileObject = new AdcJsonFileObject(creatFile);
								if (adC.getAd().getDuration() >= 5) {
									jsonFileObject.setFormalDurSecs(adC.getAd().getDuration());
								}
								boolean goAhead = creatFile.getFormalDurSecs() != null;
								
								//
								// 이미지 형인 경우 재생 시간 == 0 이기 때문에, 이미지 형 소재 파일의 재생 시간은 "화면"의 기본 재생 시간을 이용한다.
								//
								if (creatFile.getMediaType().equals("I") && jsonFileObject.getFormalDurSecs() == null) {
				        			
									jsonFileObject.setFormalDurSecs(screen.isDurationOverridden() ?
											screen.getDefaultDurSecs().intValue() : screen.getMedium().getDefaultDurSecs());
								}
								//-
								
								
								// 파일 재생시간이 대상 화면에 적합 확인
								if (!goAhead) {
									if (screen.isDurationOverridden()) {
										if (screen.getRangeDurAllowed() == true) {
											goAhead = screen.getMinDurSecs().intValue() <= jsonFileObject.getDurSecs() &&
													jsonFileObject.getDurSecs() <= screen.getMaxDurSecs().intValue();
										} else {
											goAhead = screen.getDefaultDurSecs().intValue() == jsonFileObject.getDurSecs();
										}
									} else {
										if (screen.getMedium().isRangeDurAllowed()) {
											goAhead = screen.getMedium().getMinDurSecs() <= jsonFileObject.getDurSecs() &&
													jsonFileObject.getDurSecs() <= screen.getMedium().getMaxDurSecs();
										} else {
											goAhead = screen.getMedium().getDefaultDurSecs() == jsonFileObject.getDurSecs();
										}
									}
								}
								if (! goAhead) {
									continue;
								}
			    				
			    				adCreative = adC;
			    				break;
			    			}
			    		}
			    		
						if (adCreative != null) {
							
							JSONObject jObj = getAdObject(adCreative, jsonFileObject, screen, testMode);
							if (jObj != null) {
								ads.add(jObj);
							}
						} else {
							// adCreative == null 이란, 목록에서 해당 해상도의 광고가 없다는 말
						}
			    	}
			    	
			    	if (cnt > 1) {
		    			
		    			setCodeAndMessage(obj, 2, "MultiAdDeprecated", "복수 개의 현재 광고 기능은 더 이상 지원되지 않습니다.");
			    	}
	    			
	    		} else if (GlobalInfo.AdCandiAdCreatVerKey.size() == 0) {
	    			
	    			setCodeAndMessage(obj, 1, "CandiListNotFound", "후보 리스트가 확인되지 않습니다.");
	    		}
				

			} else {
				// 운영 시간 아님
			}
			
			
			if (statusCode == 0 && ads.size() == 0) {
				
				// 대체 광고 존재 확인
				if (mapCreatFileList != null && mapCreatFileList.size() > 0) {

					ArrayList<AdcJsonFileObject> fileList = new ArrayList<AdcJsonFileObject>();
					int fbCnt = 0;
					
					for(AdcCreatFile cf : mapCreatFileList) {
						if (!cf.getCreative().getType().equals("F")) {
							continue;
						}

						//
						// 여기까지 대체 광고이고, 해상도 조건 ok
						
	    				
	    				// 화면의 미디어 유형 수용 확인
	    				if (cf.getMediaType().equals("V") && !screen.isVideoAllowed()) {
	    					continue;
	    				} else if (cf.getMediaType().equals("I") && !screen.isImageAllowed()) {
	    					continue;
	    				}

	    				
	    				// 광고 소재 인벤 타겟팅 확인
	    				//
	    				//   key가 없다는 것은 해당 소재 타겟팅이 없음을 의미
	    				//   list.size() > 0: 타겟팅에 포함되는 화면 수(꼭 현재 화면이 포함된다는 보장 없음)
	    				//   list.size() == 0: 타겟팅은 되었으나, 그 대상 화면 수가 0
	    				//
	    				String key = GlobalInfo.TgtScreenIdVerKey.get("C" + cf.getCreative().getId());
	    				if (Util.isValid(key)) {
	    					List<Integer> idList = GlobalInfo.TgtScreenIdMap.get(key);
	    					if (!idList.contains(screen.getId())) {
	    						continue;
	    					}
	    				} else {
	    					// 소재에 대한 인벤 타겟팅이 없으므로 "통과!!"
	    				}
	    				
	    				
	    				// 광고 소재 시간 타겟팅 확인
	    				if (Util.isValid(cf.getCreative().getExpHour()) && cf.getCreative().getExpHour().length() == 168) {
	    					if (!SolUtil.isCurrentOpHours(cf.getCreative().getExpHour())) {
	    						continue;
	    					}
	    				} else {
	    					// 소재에 대한 시간 타겟팅이 없으므로 "통과!!"
	    				}
	    				
	    				
	    				AdcJsonFileObject jsonFileObject = new AdcJsonFileObject(cf);
	    				jsonFileObject.setCreative(cf.getCreative());
						boolean goAhead = cf.getCreative().isDurPolicyOverriden();
						
						//
						// 이미지 형인 경우 재생 시간 == 0 이기 때문에, 이미지 형 소재 파일의 재생 시간은 "화면"의 기본 재생 시간을 이용한다.
						//
						if (cf.getMediaType().equals("I") && jsonFileObject.getFormalDurSecs() == null) {
		        			
							jsonFileObject.setFormalDurSecs(screen.isDurationOverridden() ?
									screen.getDefaultDurSecs().intValue() : screen.getMedium().getDefaultDurSecs());
						}
						
						
						// 파일 재생시간이 대상 화면에 적합 확인
						if (!goAhead) {
							if (screen.isDurationOverridden()) {
								if (screen.getRangeDurAllowed() == true) {
									goAhead = screen.getMinDurSecs().intValue() <= jsonFileObject.getDurSecs() &&
											jsonFileObject.getDurSecs() <= screen.getMaxDurSecs().intValue();
								} else {
									goAhead = screen.getDefaultDurSecs().intValue() == jsonFileObject.getDurSecs();
								}
							} else {
								if (screen.getMedium().isRangeDurAllowed()) {
									goAhead = screen.getMedium().getMinDurSecs() <= jsonFileObject.getDurSecs() &&
											jsonFileObject.getDurSecs() <= screen.getMedium().getMaxDurSecs();
								} else {
									goAhead = screen.getMedium().getDefaultDurSecs() == jsonFileObject.getDurSecs();
								}
							}
						}
						if (! goAhead) {
							continue;
						}
						
						if (cf.getCreative().getFbWeight() > 0) {
							fbCnt ++;
							for(int i = 0; i < cf.getCreative().getFbWeight(); i ++) {
								fileList.add(jsonFileObject);
							}
						}
					}
					
					int prevCreativeId = -1;
					if (fbCnt > 1) {
	        			// SELECT FB_SEL_CACHE_ID, SEL_DATE, CREATIVE_ID
	            		Tuple fbSelCacheTuple = revService.getLastFbSelCacheTupleByScreenId(screen.getId());
	            		if (fbSelCacheTuple != null) {
	            			RevFbSelCache fbSelCache = revService.getFbSelCache((int)fbSelCacheTuple.get(0));
	            			if (fbSelCache != null) {
	            				prevCreativeId = fbSelCache.getCreative().getId();
	            			}
	            		}
					}
					
					if (fileList.size() > 0) {
						Collections.shuffle(fileList);
						AdcJsonFileObject nextObj = null;
						
						if (prevCreativeId != -1) {
							for(int i = 0; i < fileList.size(); i ++) {
								AdcJsonFileObject jsonObj = fileList.get(i);
								if (jsonObj.getCreative().getId() != prevCreativeId) {
									nextObj = jsonObj;
									break;
								}
							}
						}
						if (nextObj == null) {
							nextObj = fileList.get(0);
						}
		    			
		    			// 개체 이벤트 처리(광고 소재 최근 송출 등록)
		    			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(nextObj.getAdId(), now, 21));
		    			
						
						ads.add(getFallbackAdObject(nextObj, screen, testMode));
					}
				}
				
				
				// 운영 시간일 경우에만 시간당 화면 통계 이벤트 등록
				// ads가 1이상일 경우는 대체 광고가 등록된 상태, 0이면 광고가 없음
				if (isCurrentOpHours) {
					sysService.insertTmpHrlyEvent(screen.getId(), now, (ads.size() > 0) ? 3 : 2);
				}
				
				
				if (ads.size() == 0) {
					
					setCodeAndMessage(obj, 3, "NoSchedAd", "일정이 잡혀 있는 광고를 확인할 수 없습니다.");
				}
			}
    		
			obj.put("ads", ads);
    	}
		
		Util.toJson(response, obj);
    }
    
    
    private void setCodeAndMessage(JSONObject obj, int statusCode, String message, String localMessage) {
    	
		obj.put("code", statusCode);
		obj.put("message", message);
		obj.put("local_message", localMessage);
    }
    
    
    private Date getLastCacheDate(List<Tuple> cacheList, int adId, int advertiserId) {
    	
    	if (cacheList == null) {
    		return null;
    	}

    	// SELECT CCH.AD_SEL_CACHE_ID, CCH.SEL_DATE, CCH.AD_CREATIVE_ID, AC.AD_ID, C.ADVERTISER_ID
		for(Tuple tuple : cacheList) {
			if (advertiserId == -1 && (int)tuple.get(3) == adId) {
				return (Date) tuple.get(1);
			}
		}

		return null;
    }
    
    
    private int getMinutes(Date date) {
    	
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return calendar.get(Calendar.MINUTE);
    }

    
    /**
	 * 현재 광고 진단 API
	 */
    @RequestMapping(value = {"/v1/add/{displayID}"}, method = RequestMethod.GET)
    public void processApiAdDiag(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap) {
    	
    	String displayID = Util.parseString(pathMap.get("displayID"));
    
    	String apiKey = Util.parseString(paramMap.get("apikey"));
    	
    	int cnt = Util.parseInt(paramMap.get("cnt"), 1);
    	if (cnt < 1 || cnt > 15) {
    		cnt = 1;
    	}
    	
    	
    	int statusCode = 0;
    	String message = "Ok";
    	String localMessage = "Ok";
    	
    	JSONObject obj = new JSONObject();
    	InvScreen screen = null;
    	
    	long startAt = new Date().getTime();
    	logger.info("     ==========> " + startAt + "    Ad API diag: 시작");
    	
    	if (Util.isNotValid(apiKey) || Util.isNotValid(displayID)) {
    		
    		statusCode = -3;
    		message = "WrongParams";
    		localMessage = "필수 인자의 값이 전달되지 않았습니다.";
    	} else {
    		
        	logger.info("     ==========> " + (new Date().getTime()) + "    API diag: knlService.getMediumByApiKey 전");
        	KnlMedium medium = SolUtil.getMediumByApiKey(apiKey);
        	if (medium == null) {
        		statusCode = -1;
        		message = "WrongApiKey";
        		localMessage = "등록되지 않은 API key가 전달되었습니다.";
        	} else {
            	logger.info("     ==========> " + (new Date().getTime()) + "    API diag: invService.getScreen 전");
        		screen = invService.getScreen(medium, displayID);
        		if (screen == null) {
            		statusCode = -2;
            		message = "WrongDisplayID";
            		localMessage = "등록되지 않은 디스플레이 ID가 전달되었습니다.";
        		}
        	}
    	}

    	
    	Date now = new Date();
    	
    	//
    	// 대상의 매체 화면이 정상적으로 광고 방송을 하려면,
    	//
    	//
    	//   매체 화면 기본 검증 사항:
    	//
    	//   1. 유효기간내
    	//   2. activeStatus == true
    	//   3. adServerAvailable == true
    	//   4. 매체 화면의 운영 시간
    	//
    	//
    	//   쿼리에서의 제한 항목:
    	//
    	//   11. 광고의 상태 코드는 A/R/C 중 하나
    	//       - 예약이나 완료건에 대해서도 통과. 이유는 상태 A/R/C는 현재 날짜에 따른 가변값으로 
    	//         오늘 날짜에 대한 검증이 진행전이라면 선택 오류가 발생할 수도 있기 때문
    	//       - 예약이나 완료건에 대한 필터링은 아래 12에서 별도로 진행됨
    	//   12. 광고의 시작일과 종료일 내
    	//   13. 광고의 pause == false
    	//   14. 광고의 deleted == false
    	//   15. 광고 소재의 상태 코드는 A
    	//   16. 광고 소재의 paused == false
    	//   17. 광고 소재의 deleted == false
    	//
    	//
    	//   광고 소재 파일의 검증 항목:
    	//
    	//   21. 파일 해상도: 화면해상도가 광고 소재 파일의 해상도와 일치하거나 적합
    	//   22. 파일 미디어 유형: 광고 소재 파일의 미디어 유형(동영상, 이미지 등)이 가능하도록 매체 화면 설정 필요
    	//   23. 파일 재생시간: 파일 재생시간이 광고/매체/화면의 설정에 부합
    	//
    	//
    	//   광고 소재의 검증 항목:
    	//
    	//   31. 인벤 타겟팅이 없거나, 있다면 포함되어야 함
    	//   32. 시간 타겟팅이 없거나, 있다면 대상 시간이어야 함
    	//
    	//
    	//   광고의 검증 항목:
    	//
    	//   41. 인벤 타겟팅이 없거나, 있다면 포함되어야 함
    	//   42. 시간 타겟팅이 없거나, 있다면 대상 시간이어야 함
    	//
    	//
    	//   광고 송출 이력의 검증 항목:
    	//
    	//   51. 동일 광고 송출 금지 시간이 없거나, 그 시간 초과되어야 함
    	//   52. 동일 광고주 송출 금지 시간이 없거나, 그 시간 초과되어야 함
    	//
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
    		}
    		
    		// 화면만 확정되면 바로 요청일시 저장
    		//if (!testMode) {

    		//}
    	}
    	
		obj.put("code", statusCode);
		obj.put("message", message);
		obj.put("local_message", localMessage);

		
    	if (statusCode == 0 && screen != null) {
			JSONArray ads = new JSONArray();

			// 이후에 사용할 목적으로 보관
			ArrayList<AdcCreatFile> mapCreatFileList = null; 
			String key1 = GlobalInfo.FileCandiCreatFileVerKey.get("S" + screen.getId());
    		if (Util.isValid(key1)) {
    			mapCreatFileList = GlobalInfo.FileCandiCreatFileMap.get(key1);
    		}
					
			//
			// 매체 화면의 운영 시간 확인
			//
    		boolean isCurrentOpHours = SolUtil.isCurrentOpHours((Util.isValid(screen.getBizHour()) && screen.getBizHour().length() == 168)
					? screen.getBizHour() : screen.getMedium().getBizHour());
			if (isCurrentOpHours) {
				
				// 매체 화면의 운영 시간 내
				//
				
				
	    		List<AdcAdCreative> candiList = null;
	    		HashMap<String, AdcAdCreative> candiMap = null;
	    		
	        	logger.info("     ==========> " + (new Date().getTime()) + "    API diag: SolUtil.getOptValue 전");
	    		String key = "";
	    		String selAdType = SolUtil.getOptValue(screen.getMedium().getId(), "selAd.type");
	    		boolean isRealTimeSelMode = Util.isValid(selAdType) && selAdType.equals("R");
	    		
	        	logger.info("     ==========> " + (new Date().getTime()) + "    API diag: if (isRealTimeSelMode) 전");
	    		if (isRealTimeSelMode) {
		    		key = GlobalInfo.AdCandiAdCreatVerKey.get("M" + screen.getMedium().getId());
		    		if (Util.isValid(key)) {
		    			candiMap = GlobalInfo.AdRealAdCreatMap.get(key);
		    		}
	    		} else {
		    		key = GlobalInfo.AdCandiAdCreatVerKey.get("S" + screen.getId());
		    		if (Util.isValid(key)) {
		    			candiList = GlobalInfo.AdCandiAdCreatMap.get(key);
		    		}
	    		}
	    		

	    		if ((candiList != null && candiList.size() > 0) || (isRealTimeSelMode && candiMap != null)) {
	    			//
	    			// 복수개의 현재 광고 서비스 중지
	    			//
					AdcCreatFile creatFile = null;
			    	if (screen != null && Util.isValid(screen.getResolution())) {

			    		AdcAdCreative adCreative = null;
			    		AdcJsonFileObject jsonFileObject = null;
			    		
			    		ArrayList<AdcAdCreative> orderedList = new ArrayList<AdcAdCreative>();

			    		
			    		if (isRealTimeSelMode) {
			    			
			    			String orderStr = SolUtil.selectAdSeqList(GlobalInfo.AdRealAdCreatIdsMap.get("M" + screen.getMedium().getId()));
				    		List<String> seqList = Util.tokenizeValidStr(orderStr);
				    		for(String s : seqList) {
				    			AdcAdCreative adCreat = candiMap.get("AC" + s);
				    			if (adCreat != null) {
					    			orderedList.add(adCreat);
				    			}
				    		}
			    		} else {
			    			
				        	logger.info("     ==========> " + (new Date().getTime()) + "    API diag: revService.getLastAdSelCacheTupleByScreenId 전");
			    			//
			    			// tuple: SELECT AD_SEL_CACHE_ID, SEL_DATE, AD_CREATIVE_ID
			    			//
				    		Tuple tuple = revService.getLastAdSelCacheTupleByScreenId(screen.getId());
				    		
				    		// input:
				    		//  RevAdSelect last : null 일수도
				    		//  candiList
				    		//

				    		// 다음에 처음 시도할 리스트 인덱스
				    		int idx = 0;
				    		int tmp = -1;
				    		if (tuple != null) {

				    			int adCreativeId = (Integer) tuple.get(2);
				    			for(AdcAdCreative adC : candiList) {
				    				tmp++;
				    				if (adCreativeId == adC.getId()) {
				    					tmp++;
				    					if (tmp >= candiList.size()) {
				    						tmp = 0;
				    					}
				    					idx = tmp;
			    						break;
				    				}
				    			}
				    		}


				    		// 비교 대상을 하나의 리스트로 정리
				    		if (candiList.size() == 1) {
				    			orderedList.add(candiList.get(0));
				    		} else if (idx == candiList.size() - 1) {
				    			orderedList.add(candiList.get(idx));
				    			for(int j = 0; j < candiList.size() - 1; j ++) {
				    				orderedList.add(candiList.get(j));
				    			}
				    		} else if (idx == 0) {
				    			for(int j = 0; j < candiList.size(); j ++) {
				    				orderedList.add(candiList.get(j));
				    			}
				    		} else {
				    			for(int j = idx; j < candiList.size(); j ++) {
				    				orderedList.add(candiList.get(j));
				    			}
				    			for(int j = 0; j < idx; j++) {
				    				orderedList.add(candiList.get(j));
				    			}
				    		}
			    		}

			    		
			    		// 해당 화면에 대한 오늘의 총 송출 자료 획득
			        	logger.info("     ==========> " + (new Date().getTime()) + "    API diag: revService.getScrHourlyPlayAdStatListByScreenIdPlayDate 전");
			    		List<Tuple> shpList = revService.getScrHourlyPlayAdStatListByScreenIdPlayDate(screen.getId(), 
			    				Util.removeTimeOfDate(new Date()));
			    		HashMap<String, RevScrHrlyPlyAdStatItem> adStatMap = new HashMap<String, RevScrHrlyPlyAdStatItem>();
			    		for(Tuple tuple : shpList) {
			    			RevScrHrlyPlyAdStatItem item = new RevScrHrlyPlyAdStatItem(tuple);
			    			adStatMap.put("A" + item.getAdId(), item);
			    		}
			    		
			    		// 동일 광고 및 광고주 송출 금지 시간 자료 획득
			    		//
			    		// SELECT CCH.AD_SEL_CACHE_ID, CCH.SEL_DATE, CCH.AD_CREATIVE_ID, AC.AD_ID, C.ADVERTISER_ID
			    		//
			        	logger.info("     ==========> " + (new Date().getTime()) + "    API diag: revService.getAdSelCacheTupleListByScreenId 전");
			    		List<Tuple> cacheList = revService.getAdSelCacheTupleListByScreenId(screen.getId());
			    		
			    		
			    		for(AdcAdCreative adC : orderedList) {
			    			
				        	logger.info("     ==========> " + (new Date().getTime()) + "    API diag: 광고의 화면당 하루 노출한도 확인 전");
			    			// 광고의 화면당 하루 노출한도 확인
			    			if (adC.getAd().getDailyScrCap() > 0) {
			    				RevScrHrlyPlyAdStatItem adStatItem = adStatMap.get("A" + adC.getAd().getId());
				    			if (adStatItem != null) {
				    				// 화면당 하루 노출횟수 초과 확인
			    					if (adStatItem.getSuccTot() >= adC.getAd().getDailyScrCap()) {
			    						continue;
			    					}
			    					// 시간당 횟수 초과 학인
			    					if (adStatItem.getCurrHourGoal() != null && adStatItem.getCnt() >= adStatItem.getCurrHourGoal().intValue()) {
			    						continue;
			    					}
			    					// 시간(60분) 분할 대비 노출 횟수 확인
			    					//   - 진행 성공 횟수: adStatItem.getCnt()
			    					//   - 이번 시도 차수: adStatItem.getCnt() + 1
			    					//   - 목표 횟수: adStatItem.getCurrHourGoal().intValue()
			    					//   - 현재 분: getMinutes(now)
			    					//
			    					// 식: (60분 % 목표횟수) x (이번 시도 차수 - 1) <= curr Mins 일 경우 진행
			    					//
			    					if (adStatItem.getCurrHourGoal() != null) {
				    					if ((60f / (float)adStatItem.getCurrHourGoal().intValue()) * (float)adStatItem.getCnt() > getMinutes(now)) {
				    						continue;
				    					}
			    					}
				    			}
			    			}
			    			
				        	logger.info("     ==========> " + (new Date().getTime()) + "    API diag: 동일 광고 및 광고주 송출 금지 시간 확인 전");
			    			// 동일 광고 및 광고주 송출 금지 시간 확인
			    			if (adC.getAd().getFreqCap() > 0) {
			    				Date date = getLastCacheDate(cacheList, adC.getAd().getId(), -1);
			    				if (date != null && Util.addSeconds(date, adC.getAd().getFreqCap()).after(now)) {
			    					continue;
			    				}
			    			}
			    			if (adC.getAd().getCampaign().getFreqCap() > 0) {
			    				Date date = getLastCacheDate(cacheList, -1, adC.getCreative().getAdvertiser().getId());
			    				if (date != null && Util.addSeconds(date, adC.getAd().getCampaign().getFreqCap()).after(now)) {
			    					continue;
			    				}
			    			}
			    			//-
			    			
			    			creatFile = null;
			    			if (mapCreatFileList != null) {
			        			for(AdcCreatFile acf : mapCreatFileList) {
			        				if (acf.getCreative().getId() == adC.getCreative().getId()) {
			        					creatFile = acf;
			        					break;
			        				}
			        			}
			    			}
			        		
			        		if (creatFile == null) {
					        	logger.info("     ==========> " + (new Date().getTime()) + "    API diag: adcService.getCreatFileByCreativeIdResolution 전 - loop");
				    			creatFile = adcService.getCreatFileByCreativeIdResolution(adC.getCreative().getId(), screen.getResolution());
			        		}
			        		
			    			if (creatFile != null) {
			    				
			    				
			    				// 화면의 미디어 유형 수용 확인
			    				if (creatFile.getMediaType().equals("V") && !screen.isVideoAllowed()) {
			    					continue;
			    				} else if (creatFile.getMediaType().equals("I") && !screen.isImageAllowed()) {
			    					continue;
			    				}

			    				
			    				// 광고 소재 인벤 타겟팅 확인
			    				//
			    				//   key가 없다는 것은 해당 소재 타겟팅이 없음을 의미
			    				//   list.size() > 0: 타겟팅에 포함되는 화면 수(꼭 현재 화면이 포함된다는 보장 없음)
			    				//   list.size() == 0: 타겟팅은 되었으나, 그 대상 화면 수가 0
			    				//
			    				key = GlobalInfo.TgtScreenIdVerKey.get("C" + creatFile.getCreative().getId());
			    				if (Util.isValid(key)) {
			    					List<Integer> idList = GlobalInfo.TgtScreenIdMap.get(key);
			    					if (!idList.contains(screen.getId())) {
			    						continue;
			    					}
			    				} else {
			    					// 소재에 대한 인벤 타겟팅이 없으므로 "통과!!"
			    				}
			    				
			    				
			    				// 광고 소재 시간 타겟팅 확인
			    				if (Util.isValid(creatFile.getCreative().getExpHour()) && creatFile.getCreative().getExpHour().length() == 168) {
			    					if (!SolUtil.isCurrentOpHours(creatFile.getCreative().getExpHour())) {
			    						continue;
			    					}
			    				} else {
			    					// 소재에 대한 시간 타겟팅이 없으므로 "통과!!"
			    				}

			    				
			    				// 광고 인벤 타겟팅 확인
			    				//
			    				//   key가 없다는 것은 해당 광고 타겟팅이 없음을 의미
			    				//   list.size() > 0: 타겟팅에 포함되는 화면 수(꼭 현재 화면이 포함된다는 보장 없음)
			    				//   list.size() == 0: 타겟팅은 되었으나, 그 대상 화면 수가 0
			    				//
			    				key = GlobalInfo.TgtScreenIdVerKey.get("A" + adC.getAd().getId());
			    				if (Util.isValid(key)) {
			    					List<Integer> idList = GlobalInfo.TgtScreenIdMap.get(key);
			    					if (!idList.contains(screen.getId())) {
			    						continue;
			    					}
			    				} else {
			    					// 광고에 대한 인벤 타겟팅이 없으므로 "통과!!"
			    				}
			    				
			    				
			    				// 광고 시간 타겟팅 확인
			    				if (Util.isValid(adC.getAd().getExpHour()) && adC.getAd().getExpHour().length() == 168) {
			    					if (!SolUtil.isCurrentOpHours(adC.getAd().getExpHour())) {
			    						continue;
			    					}
			    				} else {
			    					// 광고에 대한 시간 타겟팅이 없으므로 "통과!!"
			    				}
			    				
			    				
								jsonFileObject = new AdcJsonFileObject(creatFile);
								if (adC.getAd().getDuration() >= 5) {
									jsonFileObject.setFormalDurSecs(adC.getAd().getDuration());
								}
								boolean goAhead = creatFile.getFormalDurSecs() != null;
								
								//
								// 이미지 형인 경우 재생 시간 == 0 이기 때문에, 이미지 형 소재 파일의 재생 시간은 "화면"의 기본 재생 시간을 이용한다.
								//
								if (creatFile.getMediaType().equals("I") && jsonFileObject.getFormalDurSecs() == null) {
				        			
									jsonFileObject.setFormalDurSecs(screen.isDurationOverridden() ?
											screen.getDefaultDurSecs().intValue() : screen.getMedium().getDefaultDurSecs());
								}
								//-
								
								
								// 파일 재생시간이 대상 화면에 적합 확인
								if (!goAhead) {
									if (screen.isDurationOverridden()) {
										if (screen.getRangeDurAllowed() == true) {
											goAhead = screen.getMinDurSecs().intValue() <= jsonFileObject.getDurSecs() &&
													jsonFileObject.getDurSecs() <= screen.getMaxDurSecs().intValue();
										} else {
											goAhead = screen.getDefaultDurSecs().intValue() == jsonFileObject.getDurSecs();
										}
									} else {
										if (screen.getMedium().isRangeDurAllowed()) {
											goAhead = screen.getMedium().getMinDurSecs() <= jsonFileObject.getDurSecs() &&
													jsonFileObject.getDurSecs() <= screen.getMedium().getMaxDurSecs();
										} else {
											goAhead = screen.getMedium().getDefaultDurSecs() == jsonFileObject.getDurSecs();
										}
									}
								}
								if (! goAhead) {
									continue;
								}
			    				
			    				adCreative = adC;
			    				break;
			    			}
			    		}
			    		
						if (adCreative != null) {
							
							JSONObject jObj = getAdObject(adCreative, jsonFileObject, screen, true);
							if (jObj != null) {
								ads.add(jObj);
							}

				        	logger.info("     ==========> " + (new Date().getTime()) + "    API diag: 완료 전 - " + screen.getName());
						} else {
							// adCreative == null 이란, 목록에서 해당 해상도의 광고가 없다는 말
						}
			    	}
			    	
			    	if (cnt > 1) {
		    			
		    			setCodeAndMessage(obj, 2, "MultiAdDeprecated", "복수 개의 현재 광고 기능은 더 이상 지원되지 않습니다.");
			    	}
	    			
	    		} else if (GlobalInfo.AdCandiAdCreatVerKey.size() == 0) {
	    			
	    			setCodeAndMessage(obj, 1, "CandiListNotFound", "후보 리스트가 확인되지 않습니다.");
	    		}
				

			} else {
				// 운영 시간 아님
			}
			
			
			if (statusCode == 0 && ads.size() == 0) {
				
	        	logger.info("     ==========> " + (new Date().getTime()) + "    API diag: 대체 광고 존재 확인 전");
				// 대체 광고 존재 확인
				if (mapCreatFileList != null && mapCreatFileList.size() > 0) {

					ArrayList<AdcJsonFileObject> fileList = new ArrayList<AdcJsonFileObject>();
					
					for(AdcCreatFile cf : mapCreatFileList) {
						if (!cf.getCreative().getType().equals("F")) {
							continue;
						}

						//
						// 여기까지 대체 광고이고, 해상도 조건 ok
						
	    				
	    				// 화면의 미디어 유형 수용 확인
	    				if (cf.getMediaType().equals("V") && !screen.isVideoAllowed()) {
	    					continue;
	    				} else if (cf.getMediaType().equals("I") && !screen.isImageAllowed()) {
	    					continue;
	    				}

	    				
	    				// 광고 소재 인벤 타겟팅 확인
	    				//
	    				//   key가 없다는 것은 해당 소재 타겟팅이 없음을 의미
	    				//   list.size() > 0: 타겟팅에 포함되는 화면 수(꼭 현재 화면이 포함된다는 보장 없음)
	    				//   list.size() == 0: 타겟팅은 되었으나, 그 대상 화면 수가 0
	    				//
	    				String key = GlobalInfo.TgtScreenIdVerKey.get("C" + cf.getCreative().getId());
	    				if (Util.isValid(key)) {
	    					List<Integer> idList = GlobalInfo.TgtScreenIdMap.get(key);
	    					if (!idList.contains(screen.getId())) {
	    						continue;
	    					}
	    				} else {
	    					// 소재에 대한 인벤 타겟팅이 없으므로 "통과!!"
	    				}
	    				
	    				
	    				// 광고 소재 시간 타겟팅 확인
	    				if (Util.isValid(cf.getCreative().getExpHour()) && cf.getCreative().getExpHour().length() == 168) {
	    					if (!SolUtil.isCurrentOpHours(cf.getCreative().getExpHour())) {
	    						continue;
	    					}
	    				} else {
	    					// 소재에 대한 시간 타겟팅이 없으므로 "통과!!"
	    				}
	    				
	    				
	    				AdcJsonFileObject jsonFileObject = new AdcJsonFileObject(cf);
	    				jsonFileObject.setCreative(cf.getCreative());
						boolean goAhead = cf.getCreative().isDurPolicyOverriden();
						
						//
						// 이미지 형인 경우 재생 시간 == 0 이기 때문에, 이미지 형 소재 파일의 재생 시간은 "화면"의 기본 재생 시간을 이용한다.
						//
						if (cf.getMediaType().equals("I") && jsonFileObject.getFormalDurSecs() == null) {
		        			
							jsonFileObject.setFormalDurSecs(screen.isDurationOverridden() ?
									screen.getDefaultDurSecs().intValue() : screen.getMedium().getDefaultDurSecs());
						}
						
						
						// 파일 재생시간이 대상 화면에 적합 확인
						if (!goAhead) {
							if (screen.isDurationOverridden()) {
								if (screen.getRangeDurAllowed() == true) {
									goAhead = screen.getMinDurSecs().intValue() <= jsonFileObject.getDurSecs() &&
											jsonFileObject.getDurSecs() <= screen.getMaxDurSecs().intValue();
								} else {
									goAhead = screen.getDefaultDurSecs().intValue() == jsonFileObject.getDurSecs();
								}
							} else {
								if (screen.getMedium().isRangeDurAllowed()) {
									goAhead = screen.getMedium().getMinDurSecs() <= jsonFileObject.getDurSecs() &&
											jsonFileObject.getDurSecs() <= screen.getMedium().getMaxDurSecs();
								} else {
									goAhead = screen.getMedium().getDefaultDurSecs() == jsonFileObject.getDurSecs();
								}
							}
						}
						if (! goAhead) {
							continue;
						}
						
						if (cf.getCreative().getFbWeight() > 0) {
							for(int i = 0; i < cf.getCreative().getFbWeight(); i ++) {
								fileList.add(jsonFileObject);
							}
						}
					}
					
					if (fileList.size() > 0) {
						Collections.shuffle(fileList);
						ads.add(getFallbackAdObject(fileList.get(0), screen, true));
					}
				}
				
				if (ads.size() == 0) {
					
					setCodeAndMessage(obj, 3, "NoSchedAd", "일정이 잡혀 있는 광고를 확인할 수 없습니다.");
				}
			}
    		
			obj.put("ads", ads);
    	}
    	
		Util.toJson(response, obj);
		
		long endAt = new Date().getTime();
    	logger.info("     ==========> " + endAt + "    API diag: 종료 - " + (endAt - startAt));
    }

    
    private JSONObject getAdObject(AdcAdCreative adCreative, AdcJsonFileObject jsonFileObject, InvScreen screen, boolean testMode) {
    	
    	try {
    		
        	JSONObject obj = new JSONObject();
        	
        	obj.put("ad_uuid", jsonFileObject.getAdUuid());
        	obj.put("local_filename", jsonFileObject.getUuidDurFilename());
        	
        	RevAdSelect adSelect = new RevAdSelect(screen, adCreative);
        	if (!testMode) {
        		revService.saveOrUpdate(adSelect);
            	
            	RevAdSelCache adSelCache = revService.getLastAdSelCacheByScreenIdAdCreativeId(
            			screen.getId(), adCreative.getId());
            	if (adSelCache == null) {
            		revService.saveOrUpdate(new RevAdSelCache(adSelect));
            	} else {
            		adSelCache.setSelectDate(adSelect.getSelectDate());
            		revService.saveOrUpdate(adSelCache);
            	}

            	logger.info("[API] ad: " + screen.getName() + " - " + adSelect.getUuid().toString());
        	}

        	
        	String trialUuid = adSelect.getUuid().toString();
        	
        	obj.put("success_url", GlobalInfo.ReportServer + "/v1/report/success/" + trialUuid + "?start={1}&end={2}&duration={3}");
        	obj.put("error_url", GlobalInfo.ReportServer + "/v1/report/error/" + trialUuid + "?start={1}&end={2}&duration={3}");
        	
        	return obj;
    		
    	} catch (Exception e) {
    		logger.error("Ad API - getAdObject", e);
    	}
    	
    	return null;
    }

    
    private JSONObject getFallbackAdObject(AdcJsonFileObject jsonFileObject, InvScreen screen, boolean testMode) {
    	
    	try {
    		
        	JSONObject obj = new JSONObject();
        	
        	obj.put("ad_uuid", jsonFileObject.getAdUuid());
        	obj.put("local_filename", jsonFileObject.getUuidDurFilename());
        	obj.put("success_url", "");
        	obj.put("error_url", "");
        	
        	if (!testMode) {
        		
        		if (jsonFileObject.getCreative() != null) {
        			// SELECT FB_SEL_CACHE_ID, SEL_DATE, CREATIVE_ID
            		Tuple fbSelCacheTuple = revService.getLastFbSelCacheTupleByScreenId(screen.getId());
            		if (fbSelCacheTuple != null) {
            			RevFbSelCache fbSelCache = revService.getFbSelCache((int)fbSelCacheTuple.get(0));
            			if (fbSelCache == null) {
                			revService.saveOrUpdate(new RevFbSelCache(screen, jsonFileObject.getCreative(), new Date()));
            			} else {
            				fbSelCache.setSelectDate(new Date());
            				fbSelCache.setCreative(jsonFileObject.getCreative());
            				revService.saveOrUpdate(fbSelCache);
            			}
            		} else {
            			revService.saveOrUpdate(new RevFbSelCache(screen, jsonFileObject.getCreative(), new Date()));
            		}
        		}
        		
        		revService.saveOrUpdate(new RevPlayHist(new Date(), screen.getMedium().getId(), screen.getMedium().getShortName(),
        				screen.getId(), screen.getName(), jsonFileObject.getAdId(), jsonFileObject.getAdName()));

            	logger.info("[API] ad: " + screen.getName() + " - " + jsonFileObject.getAdName());
        	}
        	
        	return obj;
    		
    	} catch (Exception e) {
    		logger.error("Ad API - getFallbackAdObject", e);
    	}
    	
    	return null;
    }
}
