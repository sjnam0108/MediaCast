package net.doohad.controllers.knl;

import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.doohad.exceptions.ServerOperationForbiddenException;
import net.doohad.info.StringInfo;
import net.doohad.models.AdnMessageManager;
import net.doohad.models.CustomComparator;
import net.doohad.models.FormRequest;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.knl.KnlMenu;
import net.doohad.models.service.KnlService;
import net.doohad.utils.Util;
import net.doohad.viewmodels.fnd.TreeViewItem;

/**
 * 메뉴 컨트롤러
 */
@Controller("knl-menu-controller")
@RequestMapping(value="/knl/menu")
public class KnlMenuController {

	private static final Logger logger = LoggerFactory.getLogger(KnlMenuController.class);


    @Autowired 
    private KnlService knlService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 메뉴 페이지
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
    	model.addAttribute("pageTitle", "메뉴");
    	
    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	//Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "knl/menu";
    }
    
    
	/**
	 * 메뉴 트리 구조 자료 반환
	 */
    private List<TreeViewItem> getMenuData(Integer id, Locale locale) {
    	List<KnlMenu> menuList = knlService.getMenuListById(id);
    	
    	ArrayList<TreeViewItem> list = new ArrayList<TreeViewItem>();
    	
    	for (KnlMenu menu : menuList) {
    		TreeViewItem item = new TreeViewItem(menu.getId(), 
    				msgMgr.message("mainmenu." + menu.getUkid(), locale), 
    				menu.getSiblingSeq());
    		
    		item.setCustom1(menu.getUkid());
    		item.setCustom2(menu.getUrl());
    		item.setCustom3(menu.getIconType());

    		// 4번을 비움(TBD)
    		item.setCustom4("");
    		
    		item.setCustom5(menu.isScopeKernelAvailable() ? "Y" : "N");
    		item.setCustom6(menu.isScopeMediumAvailable() ? "Y" : "N");
    		item.setCustom7(menu.isScopeAdAvailable() ? "Y" : "N");
    		item.setChildrenCount(menu.getSubMenus().size());
    		
    		list.add(item);
    	}

    	// Top Level Menu Sort
        Collections.sort(list, CustomComparator.TreeViewItemSiblingSeqComparator);

        return list;
    }
    
    
	/**
	 * 추가/변경 액션
	 */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public @ResponseBody TreeViewItem create(@RequestBody FormRequest form, Locale locale,
    		HttpSession session) {
    	int id = form.getId();
    	String ukid = form.getUkid();
    	String url = form.getUrl();
    	String oper = form.getOper();
    	String iconType = form.getIcon();
    	
    	if (Util.isNotValid(ukid) || Util.isNotValid(oper)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}
    	
    	if (id == 0 && oper.equals("Update")) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}
    	
    	iconType = Util.isNotValid(iconType) ? null : iconType;
    	
    	KnlMenu target;

    	try {
        	if (oper.equals("Update")) {
        		target = knlService.getMenu(id);
        		
        		if (target == null) {
            		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        		}
        		
        		target.setUkid(ukid);
        		target.setUrl(url);
        		target.setIconType(iconType);
        		target.setScopeKernelAvailable(form.isScopeKernel());
        		target.setScopeMediumAvailable(form.isScopeMedium());
        		target.setScopeAdAvailable(form.isScopeAd());
        		
        		target.touchWho(session);
            	
        		knlService.saveAndReorderMenu(target, target, session);
        	} else {
        		target = new KnlMenu(ukid, url, iconType, 1000, form.isScopeKernel(), form.isScopeMedium(), form.isScopeAd(), session);
        		
        		KnlMenu parent = knlService.getMenu(id);
        		
        		if (parent != null) {
        			target.setParent(parent);
        		}
            	
        		knlService.saveAndReorderMenu(null, target, session);
        	}
        } catch (DataIntegrityViolationException dive) {
        	logger.error("create", dive);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_UKID);
        } catch (ConstraintViolationException cve) {
        	logger.error("create", cve);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_UKID);
        } catch (Exception e) {
        	logger.error("create", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }

        TreeViewItem item = new TreeViewItem(target.getId(), 
        		msgMgr.message("mainmenu." + target.getUkid(), locale), 
				target.getSiblingSeq());
		
		item.setCustom1(target.getUkid());
		item.setCustom2(target.getUrl());
		item.setCustom3(target.getIconType());
		item.setCustom4("");
		item.setCustom5(target.isScopeKernelAvailable() ? "Y" : "N");
		item.setCustom6(target.isScopeMediumAvailable() ? "Y" : "N");
		item.setCustom7(target.isScopeAdAvailable() ? "Y" : "N");
		item.setChildrenCount(target.getSubMenus().size());
    	
    	return item;
    }

    
	/**
	 * 마우스 끌어놓기 액션
	 */
    @RequestMapping(value = "/dragdrop", method = RequestMethod.POST)
    @Transactional
    public @ResponseBody String dragDrop(@RequestBody Map<String, Object> model, Locale locale,
    		HttpSession session) {
    	KnlMenu target = knlService.getMenu((int)model.get("sourceId"));
    	KnlMenu dest = knlService.getMenu((int)model.get("destId"));
    	String dropPosition = (String)model.get("position");
    	
    	if (target == null || dest == null || 
    			!(dropPosition.equals("over") || dropPosition.equals("before") || dropPosition.equals("after"))) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}
    	
    	try {
    		KnlMenu parent = target.getParent();
    		
    		if (dropPosition.equals("over")) {
    			target.setParent(dest);
    			target.setSiblingSeq(1000);
    		} else if (dropPosition.equals("before")) {
    			target.setParent(dest.getParent());
    			target.setSiblingSeq(dest.getSiblingSeq() - 1);
    		} else if (dropPosition.equals("after")) {
    			target.setParent(dest.getParent());
    			target.setSiblingSeq(dest.getSiblingSeq() + 1);
    		}
			
			target.touchWho(session);
    		
			knlService.saveAndReorderMenu(parent, target, session);
    	} catch (Exception e) {
        	logger.error("dragDrop", e);
    		throw new ServerOperationForbiddenException("OperationError");
    	}

        return "OK";
    }

    
	/**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model) {
    	KnlMenu target = new KnlMenu();
    	target.setId((int)model.get("id"));
    	
    	try {
    		knlService.deleteMenu(target);
    	} catch (Exception e) {
        	logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "OK";
    }
    
    
	/**
	 * 읽기 액션 - 메뉴 정보
	 */
    @RequestMapping(value = "/readMenus", method = RequestMethod.POST)
    public @ResponseBody List<TreeViewItem> readMenus(@RequestBody Map<String, Object> model, Locale locale) {
    	try {
    		return getMenuData((Integer)model.get("id"), locale);
    	} catch (Exception e) {
        	logger.error("readMenus", e);
    		throw new ServerOperationForbiddenException("readError");
    	}
    }

}
