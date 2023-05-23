package net.doohad.interceptors;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import net.doohad.exceptions.ServerOperationForbiddenException;
import net.doohad.models.LoginUser;
import net.doohad.models.fnd.FndLoginLog;
import net.doohad.models.knl.KnlUser;
import net.doohad.models.service.FndService;
import net.doohad.models.service.KnlService;
import net.doohad.utils.Util;

public class LoginCheckInterceptor extends HandlerInterceptorAdapter {

	private static final Logger logger = LoggerFactory.getLogger(LoginCheckInterceptor.class);

	@Autowired 
    private FndService fndService;

	@Autowired 
    private KnlService knlService;

    @Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {

    	// request.getSession(false)가 null을 되돌려줄 수 있으나,
    	// 아래의 Util.isLoginUser(session)에서 null 처리가 되어 있음.
		HttpSession session = request.getSession(false);
		String loginUri = "/";
		
		String requestUri = request.getRequestURI();
		

		// Agent 요청 접근일 때 "통과"
		//     agent 요청을 모두 /adn URI 하단으로 구성
		if (requestUri != null && !requestUri.isEmpty()) {
			if (requestUri.startsWith("/adn/")) {
				return true;
			}
		}


		if (session == null) {
			// ajax 요청에 의한 오류를 먼저 처리
			if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
				
				throw new ServerOperationForbiddenException("ToLoginPage");
			}
			
			response.sendRedirect(loginUri);
			return false;
		}
		
		session.removeAttribute("prevUri");
		session.removeAttribute("prevQuery");
		
		
		if (!Util.isLoginUser(session)) {
			
			session.setAttribute("prevUri", requestUri);
			session.setAttribute("prevQuery", Util.parseString(request.getQueryString()));

			response.sendRedirect(loginUri);
			
			return false;
		}
		
		LoginUser loginUser = (LoginUser) session.getAttribute("loginUser");
		if (loginUser == null) {
			response.sendRedirect(loginUri);
			return false;
		}

		String allowedRequestUri = "";
		if (Util.isValid(request.getMethod()) && request.getMethod().equals("GET")) {
			allowedRequestUri = requestUri;
		}
		
		// 동일 계정 동시 사용중 체크
    	if (!Util.hasThisPriv(session, "internal.NoConcurrentCheck")) {
    		FndLoginLog lastLoginLog = fndService.getLastLoginLogByUserId(loginUser.getId());
    		if (lastLoginLog != null && lastLoginLog.getId() != loginUser.getLoginId()) {
    			fndService.logout(session, true);
    			
    			if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With")))
    			{
    				throw new ServerOperationForbiddenException("ToLoginPage2");
    			} else {
    				response.sendRedirect("/common/loginAfterForcedLogout");
    			}
    			
    			return false;
    		}
    	}

		// 패스워드 수정 페이지 접근일 때 "통과"
		if (requestUri.startsWith("/common/passwordupdate")) {
			return true;
		}
    	
    	// 패스워드 유효 기간 지정(password.life.days)이 있을 경우
    	int lifeDays = Util.parseInt(Util.getFileProperty("password.life.days"), 0);
    	if (lifeDays > 0) {
    		KnlUser user = knlService.getUser(loginUser.getId());
    		if (user != null) {
    			Date limitDate = Util.addDays(Util.setMaxTimeOfDate(user.getPasswordUpdateDate()), lifeDays);
    			if (limitDate.compareTo(new Date()) < 0) {
        			response.sendRedirect("/common/passwordupdate?aging=Y");
        			return false;
    			}
    		}
    	}
    	

		// 모든 페이지 접근 권한 가질 때 "통과"
		//if (loginUser.isAnyMenuAccessAllowed()) {
		//	return true;
		//}
		
		if (!requestUri.endsWith("/")) {
			requestUri += "/";
		}
		
		List<String> allowedUrlList = loginUser.getAllowedUrlList();
		for (String url : allowedUrlList) {
			String tmpUrl = url;
			if (!tmpUrl.endsWith("/")) {
				tmpUrl += "/";
			}
			
			if (requestUri.startsWith(tmpUrl)) {
				return true;
			}
		}
		
		// 허용되지 않은 페이지 접근
		response.sendRedirect(loginUri);
		
		return false;
	}
}
