package net.doohad.controllers.api;

import java.io.BufferedReader;
import java.io.IOException;
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

import net.doohad.models.knl.KnlMedium;
import net.doohad.models.rev.RevInvenRequest;
import net.doohad.models.service.KnlService;
import net.doohad.models.service.RevService;
import net.doohad.utils.Util;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * 인벤 요청 API 컨트롤러
 */
@Controller("api-inven-controller")
@RequestMapping(value="")
public class InvenController {
	
	private static final Logger logger = LoggerFactory.getLogger(InvenController.class);


    @Autowired 
    private RevService revService;

    @Autowired 
    private KnlService knlService;
	
    
    /**
	 * 인벤 요청 API
	 */
    @RequestMapping(value = {"/v1/inven"}, method = RequestMethod.POST)
    public void processApiInven(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap) {
		
    	BufferedReader reader = null;
		String line = null;
		StringBuilder builder = new StringBuilder();
		
    	try {
			reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append(System.getProperty("line.separator"));
			}
		} catch (Exception e) {
			logger.error("processApiInven", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
	    			logger.error("processApiInven", ex);
				}
			}
		}
    	
    	String json = Util.removeTrailingChar(builder.toString(), System.getProperty("line.separator"));
    	if (Util.isValid(json)) {
        	logger.info("----------------- /v1/inven");
        	logger.info(json);
        	
        	try {
        		JSONObject jsonObj = JSONObject.fromObject(JSONSerializer.toJSON(json));
        		if (jsonObj != null) {
        			String apiKey = getPropStringValue(jsonObj, "apiKey");
        			String type = getPropStringValue(jsonObj, "type");
        			
        			String siteID = getPropStringValue(jsonObj, "siteID");
        			String siteName = getPropStringValue(jsonObj, "siteName");
        			String lat = getPropStringValue(jsonObj, "lat");
        			String lng = getPropStringValue(jsonObj, "lng");
        			String region = getPropStringValue(jsonObj, "region");
        			String addr = getPropStringValue(jsonObj, "addr");
        			String siteCond = getPropStringValue(jsonObj, "siteCond");
        			String venueType = getPropStringValue(jsonObj, "venueType");
        			
        			String screenID = getPropStringValue(jsonObj, "screenID");
        			String screenName = getPropStringValue(jsonObj, "screenName");
        			String reso = getPropStringValue(jsonObj, "reso");
        			Boolean video = getPropBooleanValue(jsonObj, "video");
        			Boolean image = getPropBooleanValue(jsonObj, "image");
        			Boolean active = getPropBooleanValue(jsonObj, "active");
        			Boolean ad = getPropBooleanValue(jsonObj, "ad");

        			String resultCode = "";
        			
        			KnlMedium medium = null;
        			if (Util.isValid(apiKey)) {
        				medium = knlService.getMediumByApiKey(apiKey);
        				if (medium == null) {
        					resultCode = "Wrong apiKey";
        				} else if (Util.isNotValid(type) || !(type.equals("U") || type.equals("D"))) {
        					resultCode = "Wrong type";
        				}
        			} else {
        				resultCode = "Wrong apiKey";
        			}
        			
        			//
        			//													등록시	변경시		템플릿		CU 템플릿	egs 템플릿	CU 등록시	egs 등록시
        			// 													필수	필수		설정가능	포함항목	포함항목	필수		필수
        			//
        			//  사이트 전용-------------------------
        			//			사이트ID		siteID		String		O		O(key)											O			O
        			//  		사이트명		siteName	String		O														O			O
        			//  		위도			lat			String		O					O									O			O
        			//  		경도			lng			String		O					O									O			O
        			//  		지역			region		String		O					O									O			O
        			//  		주소			addr		String		O					O									O			O
        			//  		입지 유형		siteCond	String		O					O						O			O
        			//  		장소 유형		venueType	String		O					O			O			O
        			//  화면 전용---------------------------
        			//  		화면ID			screenID	String		O		O(key)											O			O
        			//			화면명			screenName	String		O														O			O
        			//			해상도			reso		String		O					O						O			O
        			//			동영상허용		video		boolean		O					O			O			O
        			//			이미지허용		image		boolean		O					O			O			O
        			//			활성화여부		active		boolean		O					O			O			O
        			//			광고에 이용		ad			boolean		O					O			O			O
        			//  사이트/화면 공통--------------------
        			//  		요청 유형		type		String		O		O
        			//
        			
        			//
        			if (Util.isNotValid(resultCode) && medium != null) {
        				// apiKey는 유효. 이 조건 이후로는 모두 DB에 저장

        				JSONObject newObj = new JSONObject();
        				
        				if (Util.isValid(siteID)) { newObj.put("siteID", siteID); }
        				if (Util.isValid(siteName)) { newObj.put("siteName", siteName); }
        				if (Util.isValid(lat)) { newObj.put("lat", lat); }
        				if (Util.isValid(lng)) { newObj.put("lng", lng); }
        				if (Util.isValid(region)) { newObj.put("region", region); }
        				if (Util.isValid(addr)) { newObj.put("addr", addr); }
        				if (Util.isValid(siteCond)) { newObj.put("siteCond", siteCond); }
        				if (Util.isValid(venueType)) { newObj.put("venueType", venueType); }
        				
        				if (Util.isValid(screenID)) { newObj.put("screenID", screenID); }
        				if (Util.isValid(screenName)) { newObj.put("screenName", screenName); }
        				if (Util.isValid(reso)) { newObj.put("reso", screenName); }
        				if (video != null) { newObj.put("video", video); }
        				if (image != null) { newObj.put("image", image); }
        				if (active != null) { newObj.put("active", active); }
        				if (ad != null) { newObj.put("ad", ad); }

        				
        				RevInvenRequest invRequest = new RevInvenRequest(medium, siteID, siteName,
        						screenID, screenName, type);
        				invRequest.setRequest(Util.getObjectToJson(newObj, false));
        				
        				revService.saveOrUpdate(invRequest);
        			} else {
        				logger.info("----- " + resultCode);
        			}
        		}
    		} catch (Exception e) {
    			logger.error("processApiInven", e);
    		}
    	}
    }
    
    private String getPropStringValue(JSONObject jsonObj, String propName) {
    	
    	try {
    		
    		return Util.parseString(jsonObj.getString(propName));
    	} catch (Exception e) {
    		// 의도적인 예외 로깅 생략
    	}
    	
    	return null;
    }
    
    private Boolean getPropBooleanValue(JSONObject jsonObj, String propName) {
    	
    	try {
    		
    		return jsonObj.getBoolean(propName);
    	} catch (Exception e) {
    		// 의도적인 예외 로깅 생략
    	}
    	
    	return null;
    }
}
