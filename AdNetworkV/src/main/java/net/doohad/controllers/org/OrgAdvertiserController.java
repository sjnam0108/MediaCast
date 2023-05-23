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
import net.doohad.models.org.OrgAdvertiser;
import net.doohad.models.service.AdcService;
import net.doohad.models.service.KnlService;
import net.doohad.models.service.OrgService;
import net.doohad.utils.Util;

/**
 * 광고주 컨트롤러
 */
@Controller("org-advertiser-controller")
@RequestMapping(value="/org/advertiser")
public class OrgAdvertiserController {

	private static final Logger logger = LoggerFactory.getLogger(OrgAdvertiserController.class);


    @Autowired 
    private OrgService orgService;

    @Autowired 
    private KnlService knlService;

    @Autowired 
    private AdcService adcService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 광고주 페이지
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
    	model.addAttribute("pageTitle", "광고주");
    	
    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "org/advertiser";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		DataSourceResult result = orgService.getAdvertiserList(request);
    		
    		List<Tuple> countList = adcService.getCreativeCountGroupByMediumAdvertiserId(Util.getSessionMediumId(session));
    		HashMap<String, Long> countMap = new HashMap<String, Long>();
    		for(Tuple tuple : countList) {
    			countMap.put("K" + String.valueOf((Integer) tuple.get(0)), (Long) tuple.get(1));
    		}
    		
    		for(Object obj : result.getData()) {
    			OrgAdvertiser advertiser = (OrgAdvertiser)obj;
    			
    			Long value = countMap.get("K" + advertiser.getId());
    			if (value != null) {
    				advertiser.setCreativeCount(value.intValue());
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
    	
    	String name = (String)model.get("name");
    	String domainName = (String)model.get("domainName");
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(name) || Util.isNotValid(domainName)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	OrgAdvertiser target = new OrgAdvertiser(medium, name, domainName, session);
    	
        saveOrUpdate(target, locale, session);

        return "Ok";
    }
    
    
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String name = (String)model.get("name");
    	String domainName = (String)model.get("domainName");
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(name) || Util.isNotValid(domainName)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	OrgAdvertiser target = orgService.getAdvertiser((int)model.get("id"));
    	if (target != null) {
    		
            target.setName(name);
            target.setDomainName(domainName);
            
            saveOrUpdate(target, locale, session);
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(OrgAdvertiser target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
    	// 비즈니스 로직 검증
        
        // DB 작업 수행 결과 검증
        try {
            orgService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_NAME_OR_DOMAIN_NAME);
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_NAME_OR_DOMAIN_NAME);
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
    	
    	List<OrgAdvertiser> advertisers = new ArrayList<OrgAdvertiser>();

    	for (Object id : objs) {
    		OrgAdvertiser advertiser = new OrgAdvertiser();
    		
    		advertiser.setId((int)id);
    		
    		advertisers.add(advertiser);
    	}

    	try {
        	orgService.deleteAdvertisers(advertisers);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("destroy", dive);
        	throw new ServerOperationForbiddenException(StringInfo.DEL_ERROR_CHILD_AD);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }

}
