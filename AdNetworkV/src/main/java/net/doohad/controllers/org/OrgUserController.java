package net.doohad.controllers.org;

import java.util.ArrayList;
import java.util.Date;
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

import net.doohad.exceptions.ServerOperationForbiddenException;
import net.doohad.info.StringInfo;
import net.doohad.models.AdnMessageManager;
import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.fnd.FndLoginLog;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.knl.KnlUser;
import net.doohad.models.service.FndService;
import net.doohad.models.service.KnlService;
import net.doohad.utils.Util;
import net.doohad.viewmodels.DropDownListItem;

/**
 * 사용자 컨트롤러
 */
@Controller("org-user-controller")
@RequestMapping(value="/org/user")
public class OrgUserController {

	private static final Logger logger = LoggerFactory.getLogger(OrgUserController.class);


    @Autowired 
    private KnlService knlService;

    @Autowired 
    private FndService fndService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 사용자 페이지
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
    	model.addAttribute("pageTitle", "사용자");
    	
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	model.addAttribute("defaultPwd", (medium == null ? "" : medium.getShortName()));

    	KnlUser user = knlService.getUser(Util.loginUserId(session));
    	model.addAttribute("mUser", user == null ? false : 
    		user.getRole().equals("M1") || user.getRole().equals("M2"));
    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "org/user";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request) {
    	try {
    		DataSourceResult result = knlService.getUserList(request);
    		
    		for(Object obj : result.getData()) {
    			KnlUser user = (KnlUser) obj;
    			
    			FndLoginLog loginLog = fndService.getLastLoginLogByUserId(user.getId());
    			user.setLastLoginDate(loginLog == null ? null : loginLog.getWhoCreationDate());
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 추가 액션
	 */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody String create(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String shortName = (String)model.get("shortName");
    	String name = (String)model.get("name");
    	String password = (String)model.get("password");
    	String role = (String)model.get("role");
    	
    	KnlUser opUser = knlService.getUser(Util.loginUserId(session));
    	
    	// 파라미터 검증
    	if (opUser == null || Util.isNotValid(name) || Util.isNotValid(shortName) || Util.isNotValid(password) || Util.isNotValid(role)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	KnlUser target = new KnlUser(opUser.getAccount(), shortName, name, password, role, "", session);
    	
    	
        saveOrUpdate(target, locale, session);

        return "Ok";
    }
    
    
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String shortName = (String)model.get("shortName");
    	String name = (String)model.get("name");
    	String role = (String)model.get("role");
    	
    	// 파라미터 검증
    	if (Util.isNotValid(name) || Util.isNotValid(shortName) || Util.isNotValid(role)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	KnlUser target = knlService.getUser((int)model.get("id"));
    	if (target != null) {
            target.setRole(role);
            target.setShortName(shortName);
            target.setName(name);
            
            target.touchWho(session);
            
            saveOrUpdate(target, locale, session);
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(KnlUser target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
        
        // DB 작업 수행 결과 검증
        try {
            knlService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_USER_ID);
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_USER_ID);
        } catch (Exception e) {
    		logger.error("saveOrUpdate", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }
    }

    
    /**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	try {
        	for (Object id : objs) {
        		KnlUser user = knlService.getUser((int)id);
        		if (user != null) {
        			user.setShortName(user.getShortName() + Util.toSimpleString(new Date(), "_yyyyMMdd_HHmm"));
        			user.setDeleted(true);
                    
        			user.touchWho(session);
        			
        			knlService.saveOrUpdate(user);
        		}
        	}
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }

    
	/**
	 * 읽기 액션 - 역할 정보
	 */
    @RequestMapping(value = "/readRoles", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readPurchTypes(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-crown text-blue fa-fw", "총괄 관리자", "M1"));
		list.add(new DropDownListItem("fa-regular fa-user-gear fa-fw", "관리자", "M2"));
		list.add(new DropDownListItem("fa-regular fa-signs-post fa-fw", "광고 승인자", "AA"));
		
		return list;
    }
    

}
