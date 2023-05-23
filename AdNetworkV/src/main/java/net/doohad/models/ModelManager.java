package net.doohad.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import net.doohad.models.knl.KnlMedium;
import net.doohad.models.knl.KnlMenu;
import net.doohad.models.knl.KnlUser;
import net.doohad.models.service.KnlService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.fnd.TreeViewItem;

@Component
public class ModelManager {
	
	private static final Logger logger = LoggerFactory.getLogger(ModelManager.class);
	
	@Autowired
	private MessageManager msgMgr;

	@Autowired 
    private KnlService knlService;

	
	/**
	 * 메인 메뉴 자료를 모델에 추가
	 */
    public void addMainMenuModel(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	
    	addMainMenuModel(model, locale, session, request, "");
    	
    }

	
	/**
	 * 메인 메뉴 자료를 모델에 추가
	 */
    public void addMainMenuModel(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request, String checkedMenuUkid) {
    	if (session != null) {
    		// 세션에 메인 메뉴 자료 존재 파악, 없으면 생성
    		String lang = (String) session.getAttribute("mainMenuLang");
    		@SuppressWarnings("unchecked")
			List<TreeViewItem> data = (List<TreeViewItem>) session.getAttribute("mainMenuData");
    		
    		String appMode = Util.getAppModeFromRequest(request);
    		if (lang == null || !lang.equals(Util.kendoLangCountryCode(locale)) || data == null) {
    			lang = Util.kendoLangCountryCode(locale);
    			data = getMainMenuData(appMode, session, locale);
    			session.setAttribute("mainMenuLang", lang);
    			session.setAttribute("mainMenuData", data);
    		}
    		
    		collapseAllTreeNode(data);
    		
    		int menuTotCnt = getReachableCount(appMode, locale, session);
    		int maxQuickLinkCnt = Util.parseInt(SolUtil.getProperty(session, "quicklink.max.menu"), 5);
    		boolean isOnlyQuickLinkMode = menuTotCnt <= maxQuickLinkCnt;
    		
    		
    		String requestUri = request.getRequestURI();
    		if (Util.isValid(checkedMenuUkid)) {
    			KnlMenu checkedMenu = knlService.getMenu(checkedMenuUkid);
    			if (checkedMenu != null) {
    				requestUri = checkedMenu.getUrl();
    			}
    		}
    		String targetIdStr = expandTreeNodeIncludingParent(data, requestUri);
    		
    		List<TreeViewItem> quickLinks = getQuickLinkMenus(isOnlyQuickLinkMode, appMode, locale, session);
    		if (Util.isValid(targetIdStr)) {
        		for(TreeViewItem item : quickLinks) {
        			if (String.valueOf(item.getId()).equals(targetIdStr)) {
        				item.setChecked(true);
        			}
        		}
    		}
    		
    		model.addAttribute("main_menu_data", data);
    		model.addAttribute("main_quick_link", quickLinks);
    		model.addAttribute("menuTotCnt", menuTotCnt);
    		model.addAttribute("isOnlyQuickLinkMode", isOnlyQuickLinkMode);
    		
    		if (targetIdStr != null && !targetIdStr.isEmpty()) {
    			model.addAttribute("main_menu_current", targetIdStr);
    		}
    		//-
			
			
			// 매체 선택기 출력 여부 및 아이콘 이름 설정
    		LoginUser loginUser = (LoginUser) session.getAttribute("loginUser");
			if (loginUser != null && Util.isValid(requestUri)) {
				
				// 페이지 아이콘 설정
	    		TreeViewItem tvi = getTreeViewItem(data, requestUri);
				loginUser.setIcon(getIconName(tvi));
	    		
	    		// 매체 선택기
	    		loginUser.setMediumSwitcherAvailable(loginUser.getAvailMediumList().size() > 1);
	    		
	    		KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
	    		if (medium == null || Util.isNotValid(medium.getShortName())) {
	    			loginUser.setDispName("");
	    		} else {
	    			loginUser.setDispName(medium.getShortName());
	    		}
			}
    	}
    	
    	String logoFile = Util.getLogoPathFile("top", request.getServerName());
    	model.addAttribute("logoTopPathFile", logoFile);
    	model.addAttribute("watermarkDisplayed", !logoFile.startsWith("/resources/shared"));
    	model.addAttribute("logoTitleText", SolUtil.getProperty(session, "logo.title"));
    }
	
	/**
	 * 사용자의 메인 메뉴 트리 중 첫번째로 접근 가능한 URL 정보를 반환
	 */
    public String getFirstReachableUrl(String appMode, Locale locale, HttpSession session) {
    	if (session == null) {
    		return null;
    	}
    	
    	List<TreeViewItem> items = getMainMenuData(appMode, session, locale);
    	for(TreeViewItem item1 : items) {
    		if (item1.getChildrenCount() > 0) {
    	    	for(TreeViewItem item2 : item1.getItems()) {
    	    		if (item2.getChildrenCount() > 0) {
    	    	    	for(TreeViewItem item3 : item2.getItems()) {
    	    	    		if (item3.getChildrenCount() > 0) {
    	    	    	    	for(TreeViewItem item4 : item3.getItems()) {
    	    	    	    		if (Util.isValid(item4.getCustom1()) && Util.isValid(item4.getCustom2())) {
    	    	    	    			return item4.getCustom2();
    	    	    	    		}
    	    	    	    	}
    	    	    		}
    	    	    		
    	    	    		if (Util.isValid(item3.getCustom1()) && Util.isValid(item3.getCustom2())) {
    	    	    			return item3.getCustom2();
    	    	    		}
    	    	    	}
    	    		}
    	    		
    	    		if (Util.isValid(item2.getCustom1()) && Util.isValid(item2.getCustom2())) {
    	    			return item2.getCustom2();
    	    		}
    	    	}
    		}
    		
    		if (Util.isValid(item1.getCustom1()) && Util.isValid(item1.getCustom2())) {
    			return item1.getCustom2();
    		}
    	}
    	
    	return null;
    }
    
	/**
	 * 사용자의 메인 메뉴 트리 중 실행 가능한 메뉴 항목 수를 반환
	 */
    public int getReachableCount(String appMode, Locale locale, HttpSession session) {
    	if (session == null) {
    		return 0;
    	}
    	
    	int cnt = 0;
    	
    	List<TreeViewItem> items = getMainMenuData(appMode, session, locale);
    	for(TreeViewItem item1 : items) {
    		if (item1.getChildrenCount() > 0) {
    	    	for(TreeViewItem item2 : item1.getItems()) {
    	    		if (item2.getChildrenCount() > 0) {
    	    	    	for(TreeViewItem item3 : item2.getItems()) {
    	    	    		if (item3.getChildrenCount() > 0) {
    	    	    	    	for(TreeViewItem item4 : item3.getItems()) {
    	    	    	    		if (Util.isValid(item4.getCustom1()) && Util.isValid(item4.getCustom2())) {
    	    	    	    			cnt++;
    	    	    	    		}
    	    	    	    	}
    	    	    		}
    	    	    		
    	    	    		if (Util.isValid(item3.getCustom1()) && Util.isValid(item3.getCustom2())) {
    	    	    			cnt++;
    	    	    		}
    	    	    	}
    	    		}
    	    		
    	    		if (Util.isValid(item2.getCustom1()) && Util.isValid(item2.getCustom2())) {
    	    			cnt++;
    	    		}
    	    	}
    		}
    		
    		if (Util.isValid(item1.getCustom1()) && Util.isValid(item1.getCustom2())) {
    			cnt++;
    		}
    	}
    	
    	return cnt;
    }
    
	/**
	 * 빠른 링크 메뉴(레벨 1에서 실행가능한 메뉴) 반환
	 */
    public List<TreeViewItem> getQuickLinkMenus(boolean isOnlyQuickLinkMode, String appMode, Locale locale, HttpSession session) {
    	ArrayList<TreeViewItem> list = new ArrayList<TreeViewItem>();
    	
    	if (session != null) {
        	List<TreeViewItem> items = getMainMenuData(appMode, session, locale);
        	
        	if (isOnlyQuickLinkMode) {
            	for(TreeViewItem item1 : items) {
            		if (item1.getChildrenCount() > 0) {
            	    	for(TreeViewItem item2 : item1.getItems()) {
            	    		if (item2.getChildrenCount() > 0) {
            	    	    	for(TreeViewItem item3 : item2.getItems()) {
            	    	    		if (item3.getChildrenCount() > 0) {
            	    	    	    	for(TreeViewItem item4 : item3.getItems()) {
            	    	    	    		if (Util.isValid(item4.getCustom1()) && Util.isValid(item4.getCustom2())) {
            	    	    	    			list.add(item4);
            	    	    	    		}
            	    	    	    	}
            	    	    		}
            	    	    		
            	    	    		if (Util.isValid(item3.getCustom1()) && Util.isValid(item3.getCustom2())) {
    	    	    	    			list.add(item3);
            	    	    		}
            	    	    	}
            	    		}
            	    		
            	    		if (Util.isValid(item2.getCustom1()) && Util.isValid(item2.getCustom2())) {
    	    	    			list.add(item2);
            	    		}
            	    	}
            		}
            		
            		if (Util.isValid(item1.getCustom1()) && Util.isValid(item1.getCustom2())) {
    	    			list.add(item1);
            		}
            	}
        	} else {
            	for(TreeViewItem item : items) {
            		if (item.getItems() == null && item.getChildrenCount() == 0 && 
            				Util.isValid(item.getCustom1()) && Util.isValid(item.getCustom2())) {
            			list.add(item);
            		}
            	}
        	}
    	}
    	
    	return list;
    }
    
	/**
	 * 대상 페이지의 메뉴 및 그 상위 노드 펼치기
	 */
    private String expandTreeNodeIncludingParent(List<TreeViewItem> list, String uri) {
    	if (uri == null || uri.isEmpty() || list == null) {
    		return null;
    	}
    	
    	TreeViewItem target = getTreeViewItem(list, uri);
    	if (target == null) {
    		return null;
    	}
    	
    	String ret = String.valueOf(target.getId());

    	if (target != null) {
    		do {
    			if (target.getItems() != null && target.getItems().size() > 0) {
    				target.setExpanded(true);
    			} else {
    				target.setChecked(true);
    			}
    			
    			target = getParentTreeViewItem(list, target.getId());
    		} while (target != null);
    	}
    	
    	return ret;
    }
    
	/**
	 * 모든 트리 노드 닫기
	 */
	private void collapseAllTreeNode(List<TreeViewItem> list) {
		for (TreeViewItem item : list) {
			item.setExpanded(false);
			item.setChecked(false);
			
			if (item.getItems() != null && item.getItems().size() > 0) {
				collapseAllTreeNode(item.getItems());
			}
		}
	}
	
	/**
	 * 트리 구조의 자료에서 URL로 TreeViewItem 찾기
	 */
	private TreeViewItem getTreeViewItem(List<TreeViewItem> list, String uri) {
		for (TreeViewItem item : list) {
    		if (item.getCustom2() != null) {
    			if (item.getCustom2().equals(uri)) {
    				return item;
    			}
    		}

			if (item.getItems() != null && item.getItems().size() > 0) {
				TreeViewItem tmp = getTreeViewItem(item.getItems(), uri);
				if (tmp != null) {
					return tmp;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 트리 구조의 자료에서 번호로 TreeViewItem 찾기
	 */
	private TreeViewItem getTreeViewItem(List<TreeViewItem> list, int id) {
		for (TreeViewItem item : list) {
			if (item.getId() == id) {
				return item;
			}
			
			if (item.getItems() != null && item.getItems().size() > 0) {
				TreeViewItem tmp = getTreeViewItem(item.getItems(), id);
				if (tmp != null) {
					return tmp;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 트리 구조의 자료에서 부모 TreeViewItem 찾기
	 */
	private TreeViewItem getParentTreeViewItem(List<TreeViewItem> list, int id) {
		for (TreeViewItem item : list) {
			if (item.getItems() != null && item.getItems().size() > 0) {
				for (TreeViewItem child : item.getItems()) {
					if (child.getId() == id) {
						return item;
					}
				}
				
				TreeViewItem tmp = getParentTreeViewItem(item.getItems(), id);
				if (tmp != null) {
					return tmp;
				}
			}
		}
		
		return null;
	}
	
	private ArrayList<String> getUserMenuUrlList(ArrayList<String> list, String url) {
		if (url != null && !url.isEmpty() && !list.contains(url)) {
			list.add(url);
		}
		
		return list;
	}
	
	/**
	 * 메인 메뉴 트리 구조 자료 반환
	 */
    private List<TreeViewItem> getMainMenuData(String appMode, HttpSession session, Locale locale) {
    	
		List<KnlMenu> menuList = knlService.getMenuList();
    	
    	ArrayList<TreeViewItem> list = new ArrayList<TreeViewItem>();
    	LoginUser loginUser = null;
    	
    	// 사용자 소유 권한 체크
    	ArrayList<String> allowedMenuKeys = new ArrayList<String>();
    	if (session != null) {
    		loginUser = (LoginUser) session.getAttribute("loginUser");
    		
    		if (loginUser != null) {
    			
    			KnlUser user = knlService.getUser(loginUser.getId());
    			boolean scopeKernel = user.getAccount().isScopeKernel();
    			boolean scopeMedium = user.getAccount().isScopeMedium();
    			boolean scopeAd = user.getAccount().isScopeAd();

    			for (KnlMenu menu : menuList) {
    				if (Util.isNotValid(menu.getUrl())) {
    					continue;
    				}
    				if ((menu.isScopeKernelAvailable() && scopeKernel) || (menu.isScopeMediumAvailable() && scopeMedium) 
    						|| (menu.isScopeAdAvailable() && scopeAd)) {
    					if (!allowedMenuKeys.contains(menu.getUkid())) {
    						
    		    			// 사용자의 역할에 따른 페이지 제한
    		    			if (user.getRole().equals("M1") || user.getRole().equals("M2")) {
    		    				allowedMenuKeys.add(menu.getUkid());
    		    			} else if (user.getRole().equals("AA")) {
    		    				if (menu.getUkid().equals("RevPendAppr")) {
    		    					allowedMenuKeys.add(menu.getUkid());
    		    				}
    		    			}
    					}
    				}
    			}
    			
    		}
    		
    		// 리프 노드로부터 상위 메뉴 접근 활성화 진행
    		if (allowedMenuKeys.size() > 0) {
    	    	for (KnlMenu menu : menuList) {
    	    		if (allowedMenuKeys.contains(menu.getUkid())) {
    	    			
    	    			KnlMenu tmp = menu.getParent();
    	    			while (tmp != null) {
    	    				if (!allowedMenuKeys.contains(tmp.getUkid())) {
    	    					allowedMenuKeys.add(tmp.getUkid());
    	    				}
    	    				
    	    				tmp = tmp.getParent();
    	    			}
    	    		}
    	    	}
    		}
    	}

    	
    	ArrayList<String> urlList = new ArrayList<String>();
    	
    	for (KnlMenu menu : menuList) {
    		if (allowedMenuKeys.contains(menu.getUkid())) {
    			urlList = getUserMenuUrlList(urlList, menu.getUrl());
    			
    			KnlMenu tmp = menu.getParent();
    			while (tmp != null) {
    				urlList = getUserMenuUrlList(urlList, tmp.getUrl());
    				
    				tmp = tmp.getParent();
    			}
    		}
    	}
    	
    	for (KnlMenu menu : menuList) {
    		if (!allowedMenuKeys.contains(menu.getUkid())) {
    			continue;
    		}
    		
    		TreeViewItem item = new TreeViewItem(menu.getId(), 
    				msgMgr.message("mainmenu." + menu.getUkid(), locale), 
    				menu.getSiblingSeq());
    		
    		item.setCustom1(menu.getUkid());
    		item.setCustom2(menu.getUrl());
    		item.setCustom5(menu.isScopeKernelAvailable() ? "Y" : "N");
    		item.setCustom6(menu.isScopeMediumAvailable() ? "Y" : "N");
    		item.setCustom7(menu.isScopeAdAvailable() ? "Y" : "N");
    		item.setChildrenCount(menu.getSubMenus().size());
    		
    		item.setIcon(getIconName(menu.getIconType()));
    		
    		
    		list.add(item);
    	}

    	for (KnlMenu menu : menuList) {
    		if (!allowedMenuKeys.contains(menu.getUkid())) {
    			continue;
    		}
    		
    		if (menu.getParent() != null) {
        		TreeViewItem current = getTreeViewItem(list, menu.getId());
        		TreeViewItem parentOfCurrent = getTreeViewItem(list, menu.getParent().getId());
    			
        		parentOfCurrent.addSubItem(current);
        		list.remove(current);
    		}
    	}
    	
    	// 자식 메뉴 수 설정 검증(사용자 홈 메뉴의 자식 메뉴 존재 시 문제 발견)
    	for (TreeViewItem item : list) {
    		if (item.getItems() == null && item.getChildrenCount() != 0) {
    			item.setChildrenCount(0);
    		}
    	}
    	
    	
    	// Top Level Menu Sort
        Collections.sort(list, CustomComparator.TreeViewItemSiblingSeqComparator);

        // 사용자 접근 가능한 URL 등록
    	if (loginUser != null) {
    		loginUser.setAllowedUrlList(urlList);
    	}
    	
        return list;
    }
	
    private String getIconName(TreeViewItem tvi) {
    	
    	if (tvi == null) {
    		return "fas fa-arrow-circle-right";
    	} else {
    		return getIconName(tvi.getIcon());
    	}
    }
    
    private String getIconName(String value) {
    	
    	if (Util.isNotValid(value)) {
    		return "fal fa-arrow-circle-right";
    	}
    	
    	if (value.startsWith("fas fa-") || value.startsWith("far fa-") || 
    			value.startsWith("fal fa-")) {
    		return value;
    	} else {
    		return "fal fa-" + value;
    	}
    }
}
