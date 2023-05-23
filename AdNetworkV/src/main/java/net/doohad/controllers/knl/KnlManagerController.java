package net.doohad.controllers.knl;

import java.util.ArrayList;
import java.util.Collections;
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
import net.doohad.models.CustomComparator;
import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.fnd.FndLoginLog;
import net.doohad.models.knl.KnlAccount;
import net.doohad.models.knl.KnlUser;
import net.doohad.models.service.FndService;
import net.doohad.models.service.KnlService;
import net.doohad.utils.Util;
import net.doohad.viewmodels.DropDownListItem;

/**
 * 관리자 컨트롤러
 */
@Controller("knl-manager-controller")
@RequestMapping(value="/knl/manager")
public class KnlManagerController {

	private static final Logger logger = LoggerFactory.getLogger(KnlManagerController.class);


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
	 * 관리자 페이지
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
    	model.addAttribute("pageTitle", "관리자");

    	model.addAttribute("Accounts", readAccounts());
    	
    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "knl/manager";
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
    	String memo = (String)model.get("memo");
    	String role = (String)model.get("role");
    	
    	KnlAccount account = knlService.getAccount(Util.parseInt((String)model.get("account")));
    	
    	// 파라미터 검증
    	if (Util.isNotValid(name) || Util.isNotValid(shortName) || Util.isNotValid(role) || 
    			Util.isNotValid(password) || account == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	

    	KnlUser target = new KnlUser(account, shortName, name, password, role, memo, session);
    	
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
    	String password = (String)model.get("password");
    	String memo = (String)model.get("memo");
    	String role = (String)model.get("role");
    	
    	KnlAccount account = knlService.getAccount(Util.parseInt((String)model.get("account")));
    	
    	// 파라미터 검증
    	if (Util.isNotValid(name) || Util.isNotValid(shortName) || Util.isNotValid(role) || account == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	KnlUser target = knlService.getUser((int)model.get("id"));
    	if (target != null) {
    		target.setAccount(account);
    		
            target.setShortName(shortName);
            target.setName(name);
            target.setRole(role);
            target.setMemo(memo);

            String newPassword = Util.encrypt(password, target.getSalt());
            if (Util.isValid(newPassword)) {
            	target.setPassword(newPassword);
            	target.setPasswordUpdateDate(new Date());
            }
            
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
	 * 매체 목록 획득
	 */
    public List<DropDownListItem> readAccounts() {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		List<KnlAccount> accountList = knlService.getValidAccountList();
		
		for(KnlAccount account : accountList) {
			list.add(new DropDownListItem("fa-light fa-building-circle-check text-green", account.getName(), String.valueOf(account.getId())));
		}

		Collections.sort(list, CustomComparator.DropDownListItemTextComparator);
		
		
		return list;
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
