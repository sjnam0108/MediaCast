package net.doohad.controllers.sys;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.doohad.controllers.rev.RevGeofenceController;
import net.doohad.exceptions.ServerOperationForbiddenException;
import net.doohad.info.StringInfo;
import net.doohad.models.AdnMessageManager;
import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.service.SysService;
import net.doohad.models.sys.SysRtUnit;
import net.doohad.models.sys.SysSvcRespTime;
import net.doohad.utils.Util;

/**
 * 응답 시간 컨트롤러
 */
@Controller("sys-resp-time-controller")
@RequestMapping(value="/sys/rt")
public class SysRespTimeController {

	private static final Logger logger = LoggerFactory.getLogger(RevGeofenceController.class);


    @Autowired 
    private SysService sysService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 응답 시간 페이지
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
    	model.addAttribute("pageTitle", "응답 시간");
    	
    	
        return "sys/rt";
    }
    
    
	/**
	 * 읽기 액션 - 응답 시간 보고
	 */
    @RequestMapping(value = "/readRt", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readRt(@RequestBody DataSourceRequest request) {
    	try {
            return sysService.getSvcRespTimeList(request);
    	} catch (Exception e) {
    		logger.error("readRt", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 응답 시간 유닛
	 */
    @RequestMapping(value = "/readRtUnit", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readRtUnit(@RequestBody DataSourceRequest request) {
    	try {
            return sysService.getRtUnitList(request);
    	} catch (Exception e) {
    		logger.error("readRtUnit", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 추가 액션 - 응답 시간 유닛
	 */
    @RequestMapping(value = "/createRtUnit", method = RequestMethod.POST)
    public @ResponseBody String createRtUnit(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String name = (String)model.get("name");
    	String ukid = (String)model.get("ukid");
    	
    	boolean active = (Boolean)model.get("active");
    	
    	// 파라미터 검증
    	if (Util.isNotValid(ukid) || Util.isNotValid(name)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	SysRtUnit target = new SysRtUnit(ukid, name, active);

        saveOrUpdate(target, locale, session);

        return "Ok";
    }
    
    
	/**
	 * 변경 액션 - 응답 시간 유닛
	 */
    @RequestMapping(value = "/updateRtUnit", method = RequestMethod.POST)
    public @ResponseBody String updateRtUnit(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String name = (String)model.get("name");
    	String ukid = (String)model.get("ukid");
    	
    	boolean active = (Boolean)model.get("active");
    	
    	// 파라미터 검증
    	if (Util.isNotValid(ukid) || Util.isNotValid(name)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	SysRtUnit target = sysService.getRtUnit((int)model.get("id"));
    	if (target != null) {
        	
    		target.setUkid(ukid);
            target.setName(name);
            target.setActive(active);
            
            saveOrUpdate(target, locale, session);
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장 - 응답 시간 유닛
	 */
    private void saveOrUpdate(SysRtUnit target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
    	
    	// 비즈니스 로직 검증
        
        // DB 작업 수행 결과 검증
        try {
            sysService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_UKID);
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_UKID);
        } catch (Exception e) {
    		logger.error("saveOrUpdate", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }
    }

    
    /**
	 * 삭제 액션 - 응답 시간 보고
	 */
    @RequestMapping(value = "/destroyRt", method = RequestMethod.POST)
    public @ResponseBody String destroyRt(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<SysSvcRespTime> restTimes = new ArrayList<SysSvcRespTime>();

    	for (Object id : objs) {
    		SysSvcRespTime restTime = new SysSvcRespTime();
    		
    		restTime.setId((int)id);
    		
    		restTimes.add(restTime);
    	}
    	
    	try {
        	sysService.deleteSvcRespTimes(restTimes);
    	} catch (Exception e) {
    		logger.error("destroyRt", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }

    
    /**
	 * 삭제 액션 - 응답 시간 유닛
	 */
    @RequestMapping(value = "/destroyRtUnit", method = RequestMethod.POST)
    public @ResponseBody String destroyRtUnit(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<SysRtUnit> rtUnits = new ArrayList<SysRtUnit>();

    	for (Object id : objs) {
    		SysRtUnit rtUnit = new SysRtUnit();
    		
    		rtUnit.setId((int)id);
    		
    		rtUnits.add(rtUnit);
    	}
    	
    	try {
        	sysService.deleteRtUnits(rtUnits);
    	} catch (Exception e) {
    		logger.error("destroyRtUnit", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }

}
