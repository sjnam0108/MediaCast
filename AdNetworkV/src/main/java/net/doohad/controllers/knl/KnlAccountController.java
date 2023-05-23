package net.doohad.controllers.knl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import net.doohad.models.DataSourceRequest.FilterDescriptor;
import net.doohad.models.DataSourceResult;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.knl.KnlAccount;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.service.KnlService;
import net.doohad.utils.Util;
import net.doohad.viewmodels.knl.KnlMediumItem;

/**
 * 계정 컨트롤러
 */
@Controller("knl-account-controller")
@RequestMapping(value="/knl/account")
public class KnlAccountController {

	private static final Logger logger = LoggerFactory.getLogger(KnlAccountController.class);


    @Autowired 
    private KnlService knlService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 계정 페이지
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
    	model.addAttribute("pageTitle", "계정");
    	
    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	//Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "knl/account";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request) {
    	try {
            return knlService.getAccountList(request);
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
    	String memo = (String)model.get("memo");
    	
    	Date effectiveStartDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("effectiveStartDate")));
    	Date effectiveEndDate = Util.setMaxTimeOfDate(Util.parseZuluTime((String)model.get("effectiveEndDate")));
    	
    	@SuppressWarnings("unchecked")
		ArrayList<Object> destMedia = (ArrayList<Object>) model.get("destMedia");
    	
    	// 파라미터 검증
    	if (Util.isNotValid(name) || effectiveStartDate == null || destMedia == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	boolean scopeKernel = (Boolean)model.get("scopeKernel");
    	boolean scopeMedium = (Boolean)model.get("scopeMedium");
    	boolean scopeAd = (Boolean)model.get("scopeAd");
    	
    	String media = "";
		for(Object obj : destMedia) {
			if (Util.isValid(media)) {
				media += "|" + (String)obj;
			} else {
				media = (String)obj;
			}
		}

		
    	KnlAccount target = new KnlAccount(name, effectiveStartDate, effectiveEndDate, memo, session);
    	
    	target.setScopeKernel(scopeKernel);
    	target.setScopeMedium(scopeMedium);
    	target.setScopeAd(scopeAd);
    	
    	target.setDestMedia(media);
    	
    	
        saveOrUpdate(target, locale, session);

        return "Ok";
    }
    
    
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String name = (String)model.get("name");
    	String memo = (String)model.get("memo");
    	
    	Date effectiveStartDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("effectiveStartDate")));
    	Date effectiveEndDate = Util.setMaxTimeOfDate(Util.parseZuluTime((String)model.get("effectiveEndDate")));
    	
    	@SuppressWarnings("unchecked")
		ArrayList<Object> destMedia = (ArrayList<Object>) model.get("destMedia");
    	
    	// 파라미터 검증
    	if (Util.isNotValid(name) || effectiveStartDate == null || destMedia == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	boolean scopeKernel = (Boolean)model.get("scopeKernel");
    	boolean scopeMedium = (Boolean)model.get("scopeMedium");
    	boolean scopeAd = (Boolean)model.get("scopeAd");
    	
    	String media = "";
		for(Object obj : destMedia) {
			if (Util.isValid(media)) {
				media += "|" + (String)obj;
			} else {
				media = (String)obj;
			}
		}
    	
    	
    	KnlAccount target = knlService.getAccount((int)model.get("id"));
    	if (target != null) {
            target.setName(name);
            target.setMemo(memo);
            target.setEffectiveStartDate(effectiveStartDate);
            target.setEffectiveEndDate(effectiveEndDate);
        	
        	target.setScopeKernel(scopeKernel);
        	target.setScopeMedium(scopeMedium);
        	target.setScopeAd(scopeAd);
        	
        	target.setDestMedia(media);

            
            target.touchWho(session);
            
            saveOrUpdate(target, locale, session);
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(KnlAccount target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
    	// 비즈니스 로직 검증
        if (target.getEffectiveStartDate() != null && target.getEffectiveEndDate() != null
        		&& target.getEffectiveStartDate().after(target.getEffectiveEndDate())) {
        	throw new ServerOperationForbiddenException(StringInfo.CMN_NOT_BEFORE_EFF_END_DATE);
        }
        
        // DB 작업 수행 결과 검증
        try {
            knlService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_ACCOUNT_NAME);
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_ACCOUNT_NAME);
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
    	
    	List<KnlAccount> accounts = new ArrayList<KnlAccount>();

    	for (Object id : objs) {
    		KnlAccount account = new KnlAccount();
    		
    		account.setId((int)id);
    		
    		accounts.add(account);
    	}
    	
    	try {
        	knlService.deleteAccounts(accounts);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }
	
    
	/**
	 * 읽기 액션 - Kendo AutoComplete 용 매체 정보
	 */
    @RequestMapping(value = "/readACMedia", method = RequestMethod.POST)
    public @ResponseBody List<KnlMediumItem> readAutoComplMedia(@RequestBody DataSourceRequest request, 
    		HttpSession session) {
    	
		ArrayList<KnlMediumItem> list = new ArrayList<KnlMediumItem>();

		FilterDescriptor filter = request.getFilter();
		List<FilterDescriptor> filters = filter.getFilters();
		String userInput = "";
		if (filters.size() > 0) {
			userInput = Util.parseString((String) filters.get(0).getValue());
		}

		List<KnlMedium> mediumList = knlService.getMediumListByShortNameLike(userInput);
		
		if (mediumList.size() <= 50) {
    		for(KnlMedium medium : mediumList) {
    			list.add(new KnlMediumItem(medium.getId(), medium.getShortName(), medium.getName()));
    		}
    		
    		Collections.sort(list, new Comparator<KnlMediumItem>() {
    	    	public int compare(KnlMediumItem item1, KnlMediumItem item2) {
    	    		return item1.getShortName().toLowerCase().compareTo(item2.getShortName().toLowerCase());
    	    	}
    	    });
		}

    	return list;
    }

}
