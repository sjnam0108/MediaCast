package net.doohad.models;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class AdnMessageManager {

	@Autowired
	private MessageManager msgMgr;
	
	public AdnMessageManager() { }

	
	public void addCommonMessages(Model model, Locale locale, HttpSession session, HttpServletRequest request) {

		msgMgr.addCommonMessages(model, locale, session, request);
		
    	// 현재의 뷰 모드가 사이트 전체 여부: 사이트 전체로 전달되는 소켓 자료 사용 여부 파악
    	//model.addAttribute("isSiteLevelViewMode", monService.isSiteLevelViewMode(session));
    	model.addAttribute("isSiteLevelViewMode", true);
	}

}
