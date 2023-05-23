package net.doohad.controllers.api;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import net.doohad.models.service.SysService;
import net.doohad.models.sys.SysRtUnit;
import net.doohad.models.sys.SysSvcRespTime;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.sf.json.JSONObject;

/**
 * 응답 시간 API 컨트롤러
 */
@Controller("api-resp-time-controller")
@RequestMapping(value="")
public class RespTimeController {
	
    @Autowired 
    private SysService sysService;
	
    
    /**
	 * 응답 시간 등록 API
	 */
    @RequestMapping(value = {"/v1/rt/{rtUnitID}"}, method = RequestMethod.GET)
    public void processApiFile(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap) {
    	
    	String rtUnitID = Util.parseString(pathMap.get("rtUnitID"));
    
    	int time = Util.parseInt(paramMap.get("time"));
    	
    	
    	int statusCode = 0;
    	String message = "Ok";
    	String localMessage = "Ok";
    	
    	JSONObject obj = new JSONObject();
    	
    	if (Util.isNotValid(rtUnitID) || time <= 0) {
    		
    		statusCode = -3;
    		message = "WrongParams";
    		localMessage = "필수 인자의 값이 전달되지 않았습니다.";
    	}

    	
    	
		obj.put("code", statusCode);
		obj.put("message", message);
		obj.put("local_message", localMessage);

    	if (statusCode >= 0) {
    		
    		Date checkDate = SolUtil.getSvcRespTimeCheckDate(time);
    		
    		SysRtUnit rtUnit = sysService.getRtUnit(rtUnitID);
    		if (rtUnit == null) {
    			
    			rtUnit = new SysRtUnit(rtUnitID);
    			sysService.saveOrUpdate(rtUnit);
    			sysService.saveOrUpdate(new SysSvcRespTime(rtUnit, checkDate, time));
    			
    		} else if (rtUnit.isActive()) {
        		SysSvcRespTime svcRespTime = sysService.getSvcRespTime(rtUnit, checkDate);
    			if (svcRespTime == null) {
    				sysService.saveOrUpdate(new SysSvcRespTime(rtUnit, checkDate, time));
    			} else {
    				
    				// 기존에 등록된 동일 시간의 자료 존재
    				int count = svcRespTime.getCount();
    				int avg = ((svcRespTime.getTimeMillis() * count) + time) / (count + 1);
    						
    				svcRespTime.setCount(count + 1);
    				svcRespTime.setTimeMillis(avg);
    				
    				sysService.saveOrUpdate(svcRespTime);
    			}
    		}
    		
    	}
		
		Util.toJson(response, obj);
    }	

}
