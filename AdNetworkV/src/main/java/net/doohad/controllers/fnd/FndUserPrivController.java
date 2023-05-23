package net.doohad.controllers.fnd;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import net.doohad.models.DataSourceRequest.SortDescriptor;
import net.doohad.models.DataSourceResult;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.fnd.FndPriv;
import net.doohad.models.fnd.FndUserPriv;
import net.doohad.models.knl.KnlUser;
import net.doohad.models.service.FndService;
import net.doohad.models.service.KnlService;
import net.doohad.utils.Util;

/**
 * 사용자 권한 컨트롤러
 */
@Controller("fnd-user-priv-controller")
@RequestMapping(value="/fnd/userpriv")
public class FndUserPrivController {

	private static final Logger logger = LoggerFactory.getLogger(FndUserPrivController.class);


    @Autowired 
    private FndService fndService;

    @Autowired 
    private KnlService knlService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 사용자 권한 페이지
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
    	model.addAttribute("pageTitle", "사용자 권한");

    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
    	
        return "fnd/userpriv";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request) {
    	try {
            return fndService.getUserPrivList(request);
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }

    
	/**
	 * 추가 액션(자료 저장 포함)
	 */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody String create(@RequestBody Map<String, Object> model, Locale locale, 
    		HttpSession session) {
    	ArrayList<Object> userIds = (ArrayList<Object>) model.get("userIds");
    	ArrayList<Object> privIds = (ArrayList<Object>) model.get("privIds");
    	
		int cnt = 0;

		if (privIds.size() > 0 && userIds.size() > 0) {
    		try {
    			for(Object userObj : userIds) {
    				KnlUser user = knlService.getUser((int) userObj);

    				for(Object privObj : privIds) {
    					FndPriv priv = fndService.getPriv((int) privObj);
    					
    					if (!fndService.isRegisteredUserPriv(user.getId(), priv.getId())) {
    						fndService.saveOrUpdate(new FndUserPriv(user, priv, session));
    						cnt ++;
    					}
    				}
    			}
    		} catch (Exception e) {
        		logger.error("create", e);
        		throw new ServerOperationForbiddenException("SaveError");
        	}
    	} else {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}
    	
		if (cnt == 0) {
			return StringInfo.CMN_OPERATION_NOT_REQUIRED;
		}
		
    	return StringInfo.CMN_SAVE_SUCCESS_WITH_COUNT.replace("{0}", String.valueOf(cnt));
    }


	/**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<FndUserPriv> userPrivs = new ArrayList<FndUserPriv>();

    	for (Object id : objs) {
    		FndUserPriv userPriv = new FndUserPriv();
    		
    		userPriv.setId((int)id);
    		
    		userPrivs.add(userPriv);
    	}
    	
    	try {
        	fndService.deleteUserPrivs(userPrivs);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }
    
    
	/**
	 * 읽기 액션 - 사용자 정보
	 */
    @RequestMapping(value = "/readUsers", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readUsers(@RequestBody DataSourceRequest request) {
    	try {
    		SortDescriptor sort = new SortDescriptor();
    		sort.setDir("asc");
    		sort.setField("name");
    		
    		ArrayList<SortDescriptor> list = new ArrayList<SortDescriptor>();
    		list.add(sort);
    		
    		request.setSort(list);
    		
    		// hibernate 4.x -> 5.x로 올리면서 발생한 문제 해결
    		request.setTake(1000);
    		
    		return knlService.getUserList(request);
    	} catch (Exception e) {
    		logger.error("readUsers", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }

    
	/**
	 * 읽기 액션 - 권한 정보
	 */
    @RequestMapping(value = "/readPrivs", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readPrivilieges(@RequestBody DataSourceRequest request,
    		Locale locale) {
    	try {
    		SortDescriptor sort = new SortDescriptor();
    		sort.setDir("asc");
    		sort.setField("ukid");
    		
    		ArrayList<SortDescriptor> list = new ArrayList<SortDescriptor>();
    		list.add(sort);
    		
    		request.setSort(list);
    		
    		// hibernate 4.x -> 5.x로 올리면서 발생한 문제 해결
    		request.setTake(1000);
    		
    		return fndService.getPrivList(request);
    	} catch (Exception e) {
    		logger.error("readPrivs", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
}
