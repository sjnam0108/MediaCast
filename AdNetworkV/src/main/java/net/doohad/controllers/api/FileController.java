package net.doohad.controllers.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import net.doohad.models.adc.AdcCreatFile;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.service.InvService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.adc.AdcJsonFileObject;
import net.doohad.viewmodels.rev.RevObjEventTimeItem;
import net.doohad.viewmodels.rev.RevScrWorkTimeItem;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 파일 API 컨트롤러
 */
@Controller("api-file-controller")
@RequestMapping(value="")
public class FileController {
	
	private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired 
    private InvService invService;
	
    
    /**
	 * 광고 파일 목록 API
	 */
    @RequestMapping(value = {"/v1/files/{displayID}"}, method = RequestMethod.GET)
    public void processApiFile(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap) {
    	
    	String displayID = Util.parseString(pathMap.get("displayID"));
    
    	String apiKey = Util.parseString(paramMap.get("apikey"));
    	String test = Util.parseString(paramMap.get("test"));
    	boolean testMode = Util.isValid(test) && test.toLowerCase().equals("y");
    	
    	
    	int statusCode = 0;
    	String message = "Ok";
    	String localMessage = "Ok";
    	
    	JSONObject obj = new JSONObject();
    	InvScreen screen = null;
    	
    	if (!GlobalInfo.FileApiReady) {
    		
    		statusCode = -7;
    		message = "RetryAgain";
    		localMessage = "잠시 후 다시 시도해 주시기 바랍니다.";
    	} else if (Util.isNotValid(apiKey) || Util.isNotValid(displayID)) {
    		
    		statusCode = -3;
    		message = "WrongParams";
    		localMessage = "필수 인자의 값이 전달되지 않았습니다.";
    	} else {
    		
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

    	
    	//
    	// 대상의 매체 화면이 정상적으로 광고 방송을 하려면,
    	//
    	//
    	//   매체 화면 기본 검증 사항:
    	//
    	//   1. 유효기간내
    	//   2. activeStatus == true
    	//   3. adServerAvailable == true
    	//   4. 매체 화면의 운영 시간(File API 제외)
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
    	//   32. 시간 타겟팅이 없거나, 있다면 대상 시간이어야 함(File API 제외)
    	//
    	//
    	//   광고의 검증 항목:
    	//
    	//   41. 인벤 타겟팅이 없거나, 있다면 포함되어야 함(File API 제외)
    	//   42. 시간 타겟팅이 없거나, 있다면 대상 시간이어야 함(File API 제외)
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

    			Date now = new Date();
    			
    			// 개체 이벤트 처리(File API 호출 시간 등록)
    			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(screen.getId(), now, 11));
        		
        		// 상태 라인 처리 위해 공용리스트에 추가
        		GlobalInfo.ScrWorkTimeItemList.add(new RevScrWorkTimeItem(screen.getId(), now));
    		}
    	}

    	
    	
		obj.put("code", statusCode);
		obj.put("message", message);
		obj.put("local_message", localMessage);

		if (statusCode == 0 && screen != null) {
			JSONArray files = new JSONArray();

			ArrayList<AdcCreatFile> list = new ArrayList<AdcCreatFile>();
			String key = GlobalInfo.FileCandiCreatFileVerKey.get("S" + screen.getId());
    		if (Util.isValid(key)) {
    			list = GlobalInfo.FileCandiCreatFileMap.get(key);
    		}
			
			for(AdcCreatFile creatFile : list) {
				
				// 인벤 타겟팅 확인
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
				//
				//   시간 타겟팅은 기반이 시간인데, 파일 API는 일자 기반이기 때문에 타겟팅 확인치 않고 모두 포함
				//
				

				//
				// creatFile 자료는 매체 전체에서 동일한 값으로 존재해야 하는 만큼 화면마다 달라질 수 있는 값 설정은 
				// 잘못된 결과를 얻을 수 있게 된다. 광고의 재생시간 직접 지정은 ok. 화면의 기본 재생 시간 지정은 no.
				//
				// -> creatFile로부터 별도의 모델 클래스를 생성한 후, fileObject 생성하도록 변경
				//
				AdcJsonFileObject jsonFileObject = new AdcJsonFileObject(creatFile);
				boolean goAhead = creatFile.getFormalDurSecs() != null;
				
				//
				// 이미지 형인 경우 재생 시간 == 0 이기 때문에, 이미지 형 소재 파일의 재생 시간은 "화면"의 기본 재생 시간을 이용한다.
				//
				if (creatFile.getMediaType().equals("I") && creatFile.getFormalDurSecs() == null) {
        			
					jsonFileObject.setFormalDurSecs(screen.isDurationOverridden() ?
							screen.getDefaultDurSecs().intValue() : screen.getMedium().getDefaultDurSecs());
				} else if (creatFile.getCreative().getType().equals("F") && creatFile.getMediaType().equals("V") &&
						creatFile.getCreative().isDurPolicyOverriden()) {
					// 대체 광고이면서, 화면의 재생시간 정책 무시 설정 상태라면
					goAhead = true;
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
				
				files.add(getFileObject(jsonFileObject));
			}
			
			// 대체 광고 투입 확인
			//if (files.si)
    		
			if (!testMode) {
	        	logger.info("[API] file: " + screen.getName());
			}

			
			obj.put("files", files);
			
			if (files.size() == 0 && GlobalInfo.FileCandiCreatFileVerKey.size() == 0) {
				
				setCodeAndMessage(obj, 1, "CandiListNotFound", "후보 리스트가 확인되지 않습니다.");
			}
    	}
		
		Util.toJson(response, obj);
    }
    
    
    private void setCodeAndMessage(JSONObject obj, int statusCode, String message, String localMessage) {
    	
		obj.put("code", statusCode);
		obj.put("message", message);
		obj.put("local_message", localMessage);
    }
    
    
    /**
	 * 광고 파일 목록 진단 API
	 */
    @RequestMapping(value = {"/v1/filesd/{displayID}"}, method = RequestMethod.GET)
    public void processApiFileDiag(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap) {
    	
    	String displayID = Util.parseString(pathMap.get("displayID"));
    
    	String apiKey = Util.parseString(paramMap.get("apikey"));
    	
    	
    	int statusCode = 0;
    	String message = "Ok";
    	String localMessage = "Ok";
    	
    	JSONObject obj = new JSONObject();
    	InvScreen screen = null;
    	
    	long startAt = new Date().getTime();
    	logger.info("     ==========> " + startAt + "    File API diag: 시작");
    	
    	if (Util.isNotValid(apiKey) || Util.isNotValid(displayID)) {
    		
    		statusCode = -3;
    		message = "WrongParams";
    		localMessage = "필수 인자의 값이 전달되지 않았습니다.";
    	} else {
    		
        	logger.info("     ==========> " + (new Date().getTime()) + "    API diag: knlService.getMediumByApiKey 전");
        	//KnlMedium medium = knlService.getMediumByApiKey(apiKey);
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
    		
    		// API 요청 기록 생략
    	}

    	
    	
		obj.put("code", statusCode);
		obj.put("message", message);
		obj.put("local_message", localMessage);

		if (statusCode == 0 && screen != null) {
			JSONArray files = new JSONArray();

        	logger.info("     ==========> " + (new Date().getTime()) + "    API diag: GlobalInfo.FileCandiCreativeMap.get 전");
			ArrayList<AdcCreatFile> list = new ArrayList<AdcCreatFile>();
			String key = GlobalInfo.FileCandiCreatFileVerKey.get("S" + screen.getId());
    		if (Util.isValid(key)) {
    			list = GlobalInfo.FileCandiCreatFileMap.get(key);
    		}
			
			for(AdcCreatFile creatFile : list) {
				
				// 인벤 타겟팅 확인
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
				//
				//   시간 타겟팅은 기반이 시간인데, 파일 API는 일자 기반이기 때문에 타겟팅 확인치 않고 모두 포함
				//
				

				//
				// creatFile 자료는 매체 전체에서 동일한 값으로 존재해야 하는 만큼 화면마다 달라질 수 있는 값 설정은 
				// 잘못된 결과를 얻을 수 있게 된다. 광고의 재생시간 직접 지정은 ok. 화면의 기본 재생 시간 지정은 no.
				//
				// -> creatFile로부터 별도의 모델 클래스를 생성한 후, fileObject 생성하도록 변경
				//
				AdcJsonFileObject jsonFileObject = new AdcJsonFileObject(creatFile);
				boolean goAhead = creatFile.getFormalDurSecs() != null;
				
				//
				// 이미지 형인 경우 재생 시간 == 0 이기 때문에, 이미지 형 소재 파일의 재생 시간은 "화면"의 기본 재생 시간을 이용한다.
				//
				if (creatFile.getMediaType().equals("I") && creatFile.getFormalDurSecs() == null) {
        			
					jsonFileObject.setFormalDurSecs(screen.isDurationOverridden() ?
							screen.getDefaultDurSecs().intValue() : screen.getMedium().getDefaultDurSecs());
				} else if (creatFile.getCreative().getType().equals("F") && creatFile.getMediaType().equals("V") &&
						creatFile.getCreative().isDurPolicyOverriden()) {
					// 대체 광고이면서, 화면의 재생시간 정책 무시 설정 상태라면
					goAhead = true;
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
				
				files.add(getFileObject(jsonFileObject));
			}
    		
        	logger.info("     ==========> " + (new Date().getTime()) + "    API diag: 완료 전 - " + screen.getName());

			
			obj.put("files", files);
			
			if (files.size() == 0 && GlobalInfo.FileCandiCreatFileVerKey.size() == 0) {
				
				setCodeAndMessage(obj, 1, "CandiListNotFound", "후보 리스트가 확인되지 않습니다.");
			}
    	}
		
		Util.toJson(response, obj);
		
		long endAt = new Date().getTime();
    	logger.info("     ==========> " + endAt + "    API diag: 종료 - " + (endAt - startAt));
    }
    
    
    private JSONObject getFileObject(AdcJsonFileObject jsonFileObj) {
    	
    	JSONObject obj = new JSONObject();
    	
    	obj.put("ad_id", jsonFileObj.getAdId());
    	
    	JSONObject propObj = new JSONObject();
    	propObj.put("ad_name", jsonFileObj.getAdName());
    	propObj.put("advertiser_name", jsonFileObj.getAdvertiserName());
    	
    	obj.put("ad_properties", propObj);
    	
    	obj.put("ad_uuid", jsonFileObj.getAdUuid());
    	obj.put("local_filename", jsonFileObj.getUuidDurFilename());
    	obj.put("url", jsonFileObj.getHttpFilename());
    	obj.put("file_size", jsonFileObj.getFileLength());
    	obj.put("mime_type", jsonFileObj.getMimeType());
    	obj.put("width", jsonFileObj.getWidth());
    	obj.put("height", jsonFileObj.getHeight());
    	obj.put("duration", jsonFileObj.getDurSecs());
    	obj.put("duration_millis", jsonFileObj.getFormalDurMillis());
    	obj.put("last_updated", jsonFileObj.getCreationDateStr());
    	
    	return obj;
    }

}
