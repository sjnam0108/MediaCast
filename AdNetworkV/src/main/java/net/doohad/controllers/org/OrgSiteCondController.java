package net.doohad.controllers.org;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Tuple;
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
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgSiteCond;
import net.doohad.models.service.InvService;
import net.doohad.models.service.KnlService;
import net.doohad.models.service.OrgService;
import net.doohad.utils.Util;

/**
 * 입지 유형 컨트롤러
 */
@Controller("org-site-cond-controller")
@RequestMapping(value="/org/sitecond")
public class OrgSiteCondController {

	private static final Logger logger = LoggerFactory.getLogger(OrgSiteCondController.class);


    @Autowired 
    private OrgService orgService;

    @Autowired 
    private KnlService knlService;

    @Autowired 
    private InvService invService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 입지 유형 페이지
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
    	model.addAttribute("pageTitle", "입지 유형");
    	
    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "org/sitecond";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		DataSourceResult result = orgService.getSiteCondList(request);
    		
    		HashMap<String, Long> countMap = new HashMap<String, Long>();
    		List<Tuple> countList = invService.getSiteCountGroupByMediumSiteCondId(Util.getSessionMediumId(session));
    		for(Tuple tuple : countList) {
    			countMap.put("K" + String.valueOf((Integer) tuple.get(0)), (Long) tuple.get(1));
    		}
    		
    		countList = invService.getScreenCountGroupByMediumSiteCondId(Util.getSessionMediumId(session));
    		for(Tuple tuple : countList) {
    			countMap.put("L" + String.valueOf((Integer) tuple.get(0)), (Long) tuple.get(1));
    		}
    		
    		for(Object obj : result.getData()) {
    			OrgSiteCond siteCond = (OrgSiteCond)obj;
    			
    			Long value1 = countMap.get("K" + siteCond.getId());
    			if (value1 != null) {
    				siteCond.setSiteCount(value1.intValue());
    			}
    			
    			Long value2 = countMap.get("L" + siteCond.getId());
    			if (value2 != null) {
    				siteCond.setScreenCount(value2.intValue());
    			}
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
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	
    	String name = (String)model.get("name");
    	String code = (String)model.get("code");
    	
    	boolean activeStatus = (Boolean)model.get("activeStatus");
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(code) || Util.isNotValid(name)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	OrgSiteCond target = new OrgSiteCond(medium, name, code, activeStatus, session);

        saveOrUpdate(target, locale, session);

        return "Ok";
    }
    
    
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	
    	String name = (String)model.get("name");
    	String code = (String)model.get("code");
    	
    	boolean activeStatus = (Boolean)model.get("activeStatus");
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(code) || Util.isNotValid(name)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	OrgSiteCond target = orgService.getSiteCond((int)model.get("id"));
    	if (target != null) {
        	
    		target.setCode(code);
            target.setName(name);
            target.setActiveStatus(activeStatus);

            
            target.touchWho(session);
            
            saveOrUpdate(target, locale, session);
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(OrgSiteCond target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
    	
    	// 비즈니스 로직 검증
        
        // DB 작업 수행 결과 검증
        try {
            orgService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_CODE);
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_CODE);
        } catch (Exception e) {
    		logger.error("saveOrUpdate", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }
    }

    
    /**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<OrgSiteCond> siteConds = new ArrayList<OrgSiteCond>();

    	for (Object id : objs) {
    		OrgSiteCond siteCond = new OrgSiteCond();
    		
    		siteCond.setId((int)id);
    		
    		siteConds.add(siteCond);
    	}
    	
    	try {
        	orgService.deleteSiteConds(siteConds);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("destroy", dive);
        	throw new ServerOperationForbiddenException(StringInfo.DEL_ERROR_CHILD_INVENTORY);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }

}
