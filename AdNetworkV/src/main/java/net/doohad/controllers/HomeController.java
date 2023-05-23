package net.doohad.controllers;

import java.io.IOException;
import java.security.PrivateKey;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import net.doohad.exceptions.ServerOperationForbiddenException;
import net.doohad.info.GlobalInfo;
import net.doohad.info.StringInfo;
import net.doohad.info.WebAppInfo;
import net.doohad.models.ExcelDownloadView;
import net.doohad.models.FormRequest;
import net.doohad.models.LoginUser;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.adn.AdnUserCookie;
import net.doohad.models.fnd.FndLoginLog;
import net.doohad.models.knl.KnlUser;
import net.doohad.models.service.FndService;
import net.doohad.models.service.KnlService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;

/**
 * 홈 컨트롤러
 */
@Controller("home-controller")
@RequestMapping(value="")
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired 
    private KnlService knlService;

    @Autowired 
    private FndService fndService;
    

	@Autowired
	private MessageManager msgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	/**
	 * 웹 애플리케이션 컨텍스트 홈
	 */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model, Locale locale, HttpServletRequest request,
    		HttpSession session) {
    	
		String logoutType = Util.parseString(request.getParameter("forcedLogout"));
		if (Util.isValid(logoutType) && logoutType.equals("true")) {
	    	model.addAttribute("forcedLogout", true);
		}

		String appMode = Util.getAppModeFromRequest(request);
		
		if (Util.isValid(appMode)) {
			if (appMode.equals("A") || appMode.equals("I")) {
				return "forward:/applogin";
			} else {
				return "forward:/home";
			}
		} else {
			return "forward:/home";
		}
	}
	
	/**
	 * 로그인 페이지
	 */
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String toLogin(Model model, Locale locale, HttpServletRequest request,
    		HttpSession session) {
    	msgMgr.addCommonMessages(model, locale, session, request);
    	
    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
   					new Message("pageTitle", "home.title"),
       				new Message("label_username", "home.username"),
       				new Message("label_password", "home.password"),
    				new Message("tip_remember", "home.remember"),
    				new Message("btn_login", "home.login"),
    				new Message("msg_forcedLogout", "home.msg.forcedLogout"),
    			});

    	model.addAttribute("logoPathFile", Util.getLogoPathFile("login", request.getServerName()));
    	
    	Util.prepareKeyRSA(model, session);
    	
		return "home";
	}
	
	/**
	 * FavIcon 액션
	 */
    @RequestMapping(value = "/favicon.ico", method = RequestMethod.GET)
    public void favicon(HttpServletRequest request, HttpServletResponse response) {
    	try {
    		response.sendRedirect("/resources/favicon.ico");
    	} catch (IOException e) {
    		logger.error("favicon", e);
    	}
    }

	/**
	 * 로그인 암호키 확인 액션
	 */
    @RequestMapping(value = "/loginkey", method = RequestMethod.POST)
    public @ResponseBody String checkLoginKey(@RequestBody Map<String, Object> model) {
    	
    	String clientKey = Util.parseString((String)model.get("key"));
    	
    	return (Util.isValid(clientKey) && clientKey.equals(GlobalInfo.RSAKeyMod)) ? "Y" : "N";
    }
    
	/**
	 * 로그인 프로세스
	 */
    private String doLogin(String shortName, String password, String appMode, HttpSession session, 
    		Locale locale, HttpServletRequest request, HttpServletResponse response) {
    	
    	if (Util.isNotValid(shortName) || Util.isNotValid(password)) {
    		return StringInfo.LOGIN_WRONG_ID_PWD;
    	}
    	
    	// RSA 인코딩되었을 때의 처리
    	if (shortName.length() == 512 && password.length() == 512) {
    		PrivateKey privateKey = (PrivateKey) session.getAttribute("rsaPrivateKey");
    		if (privateKey != null) {
    			shortName = Util.decryptRSA(privateKey, shortName);
    			password = Util.decryptRSA(privateKey, password);
    		}
    	}
    	//-
    	
    	KnlUser dbUser = knlService.getUser(shortName);
    	if (dbUser == null || !Util.isSameUserPassword(password, dbUser.getSalt(), dbUser.getPassword())) {
    		logger.info("Login Error(WrongIdPwd): {}/{}", shortName, password);
    		
    		return StringInfo.LOGIN_WRONG_ID_PWD;
    	}
    	
    	
    	// 여기까지 오면 패스워드까지 일치
    	
    	FndLoginLog lastLoginLog = fndService.getLastLoginLogByUserId(dbUser.getId());
    	if (lastLoginLog != null) {
        	DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
        	
    		session.setAttribute("loginUserLastLoginTime", df.format(lastLoginLog.getWhoCreationDate()));
    	}
    	
    	FndLoginLog loginLog = new FndLoginLog(dbUser, request.getRemoteAddr(), session);

    	try {
    		fndService.saveOrUpdate(loginLog);
        	
        	LoginUser successLoginUser = new LoginUser(dbUser, loginLog.getId());
        	successLoginUser.setAvailMediumList(SolUtil.getAvailMediumListByUserId(dbUser.getId()));
        	successLoginUser.setAvailAdAccountList(SolUtil.getAvailAdAccountListByUserId(dbUser.getId()));
        	
        	session.setAttribute("loginUser", successLoginUser);
    		

        	AdnUserCookie userCookie = new AdnUserCookie(request);
        	if (Util.isValid(appMode)) {
        		userCookie.setAppMode(appMode);
        	}
        	session.setAttribute("userCookie", userCookie);

        	
        	//
        	// 매체 관리 페이지들을 위한 설정
        	//
        	String cookieKey = String.format("%s.medium.user_%d", WebAppInfo.APP_ID, dbUser.getId());
        	String cookieValue = Util.cookieValue(request, cookieKey);
        	
        	if (cookieValue == null) {
        		// LoginUser의 dropdownlist의 첫 값 획득
        		// 있으면 변수에 설정하고 쿠키 저장
        		// 없으면 패스
        		
        		cookieValue = successLoginUser.getFirstMediumIdInAvailMediumList();
        		if (cookieValue != null) {
        			response.addCookie(Util.cookie(cookieKey, cookieValue));
        		}
        	} else {
        		// LoginUser의 dropdownlist의 값 존재 확인
        		// 있으면 패스
        		// 없으면 
        		// LoginUser의 dropdownlist의 첫 값 획득
        		// 있으면 변수에 설정하고 쿠키 저장
        		// 없으면 패스
        		
        		if (!successLoginUser.hasMediumIdInAvailMediumList(cookieValue)) {
            		cookieValue = successLoginUser.getFirstMediumIdInAvailMediumList();
            		if (cookieValue != null) {
            			response.addCookie(Util.cookie(cookieKey, cookieValue));
            		}
        		}
        	}
        	
        	session.setAttribute("currMediumId", cookieValue);

        	
        	//
        	// 광고 제공 페이지들을 위한 설정
        	//
        	cookieKey = String.format("%s.account.user_%d", WebAppInfo.APP_ID, dbUser.getId());
        	cookieValue = Util.cookieValue(request, cookieKey);
        	
        	if (cookieValue == null) {
        		
        		cookieValue = successLoginUser.getFirstAccountIdInAvailAdAccountList();
        		if (cookieValue != null) {
        			response.addCookie(Util.cookie(cookieKey, cookieValue));
        		}
        	} else {
        		
        		if (!successLoginUser.hasAccountIdInAvailAdAccountList(cookieValue)) {
            		cookieValue = successLoginUser.getFirstAccountIdInAvailAdAccountList();
            		if (cookieValue != null) {
            			response.addCookie(Util.cookie(cookieKey, cookieValue));
            		}
        		}
        	}
        	
        	session.setAttribute("currAccountId", cookieValue);

        	
        	
        	session.removeAttribute("mainMenuLang");
        	session.removeAttribute("mainMenuData");
        	
        	// 세션 무효화 권한 사용자 처리
        	if (Util.hasThisPriv(session, "internal.NoTimeOut")) {
        		session.setMaxInactiveInterval(-1);
        	}
        	
        	// 사용자의 View 설정
        	//userService.setUserViews(successLoginUser, cookieValue, null, session, locale);
    	} catch (Exception e) {
    		logger.error("doLogin", e);
    		
    		return StringInfo.LOGIN_ERROR;
    	}

        return "OK";
    }
    
	/**
	 * 로그인 액션
	 */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public @ResponseBody String login(@RequestBody KnlUser loginUser, HttpSession session, 
    		Locale locale, HttpServletRequest request, HttpServletResponse response) {
    	
    	String username = loginUser.getShortName();
    	String password = loginUser.getPassword();
    	
    	String result = doLogin(username, password, "", session, locale, request, response);
    	
    	if (result.equals("OK")) {
    		return result;
    	} else {
    		throw new ServerOperationForbiddenException(result);
    	}
    }
    
	/**
	 * 로그아웃 액션
	 */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView logout(HttpSession session) {
    	fndService.logout(session);
    	
    	return new ModelAndView("redirect:/");
    }
    
	/**
	 * 패스워드 변경 액션
	 */
    @RequestMapping(value = "/passwordupdate", method = RequestMethod.POST)
    public @ResponseBody String updatePassword(@RequestBody FormRequest form, HttpSession session, 
    		Locale locale, HttpServletRequest request) {
    	String currentPwd = form.getCurrentPassword();
    	String newPwd = form.getNewPassword();
    	String confirmPwd = form.getConfirmPassword();
    	
    	LoginUser loginUser = (LoginUser) session.getAttribute("loginUser");

    	if (currentPwd == null || currentPwd.isEmpty() || newPwd == null || newPwd.isEmpty() ||
    			confirmPwd == null || confirmPwd.isEmpty() || loginUser == null) {
    		throw new ServerOperationForbiddenException(msgMgr.message("common.server.msg.wrongParamError", locale));
    	}
    	
    	// RSA 인코딩되었을 때의 처리
    	if (currentPwd.length() == 512 && newPwd.length() == 512 && confirmPwd.length() == 512) {
    		PrivateKey privateKey = (PrivateKey) session.getAttribute("rsaPrivateKey");
    		if (privateKey != null) {
    			currentPwd = Util.decryptRSA(privateKey, currentPwd);
    			newPwd = Util.decryptRSA(privateKey, newPwd);
    			confirmPwd = Util.decryptRSA(privateKey, confirmPwd);
    		}
    	}
    	//-
    	
    	if (!newPwd.equals(confirmPwd)) {
    		throw new ServerOperationForbiddenException(msgMgr.message("passwordupdate.msg.samePassword", locale));
    	}
    	
    	KnlUser dbUser = knlService.getUser(loginUser.getId());
    	if (dbUser == null || !Util.isSameUserPassword(currentPwd, dbUser.getSalt(), dbUser.getPassword())) {
    		throw new ServerOperationForbiddenException(msgMgr.message("common.server.msg.wrongParamError", locale));
    	}
    	
    	// 지금부터 새 패스워드 저장
    	dbUser.setPassword(Util.encrypt(newPwd, dbUser.getSalt()));
    	dbUser.setPasswordUpdateDate(new Date());
        
    	dbUser.touchWho(session);
    	
        try {
        	knlService.saveOrUpdate(dbUser);
        } catch (Exception e) {
    		logger.error("updatePassword", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }

        return "OK";
    }
	
	/**
	 * 엑셀 다운로드 최종 페이지
	 */
	@RequestMapping(value = "/export", method = RequestMethod.GET)
    public View excelExportFile() {
    	return new ExcelDownloadView();
    }
	
	/**
	 * 사용자의 현재 매체 변경 액션
	 */
	@RequestMapping(value = "/changemedium", method = RequestMethod.GET)
    public ModelAndView changeSite(HttpServletRequest request, HttpServletResponse response, 
    		HttpSession session, Locale locale) {
		String userUrl = request.getParameter("uri");
		
		if (userUrl == null || userUrl.isEmpty()) {
			userUrl = "/userhome";
		}

		if (session != null) {
			LoginUser loginUser = (LoginUser) session.getAttribute("loginUser");
			if (loginUser != null) {
	        	String cookieKey = String.format("%s.medium.user_%d", WebAppInfo.APP_ID, Util.loginUserId(session));
				String cookieValue = request.getParameter("mediumId");

				if (!loginUser.hasMediumIdInAvailMediumList(cookieValue)) {
		    		cookieValue = loginUser.getFirstMediumIdInAvailMediumList();
				}

				response.addCookie(Util.cookie(cookieKey, cookieValue));
				
	        	session.setAttribute("currMediumId", cookieValue);
	        	
	        	// 사용자의 View 설정
	        	//userService.setUserViews(loginUser, cookieValue, null, session, locale);
			}
		}
		
    	return new ModelAndView("redirect:" + userUrl);
    }
	
    /**
     * 로컬 파일 저장을 지원하지 않는 브라우저를 위한 프록시 기능 액션
     * 대상 브라우저: IE9 혹은 그 이하, Safari
     */
    
    @RequestMapping(value = "/proxySave", method = RequestMethod.POST)
    public @ResponseBody void save(String fileName, String base64, 
    		String contentType, HttpServletResponse response) throws IOException {

        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

        response.setContentType(contentType);

        byte[] data = DatatypeConverter.parseBase64Binary(base64);

        response.setContentLength(data.length);
        response.getOutputStream().write(data);
        response.flushBuffer();
    }
    
    /**
     * 로그인 후 엔트리 페이지로 이동 액션
     */
    @RequestMapping(value = "/userhome", method = RequestMethod.GET)
    public ModelAndView userhome(HttpSession session, Locale locale, HttpServletRequest request) {
    	
    	// getFirstReachableUrl의 로직을 먼저 실행 후 선택 필요
    	String url = modelMgr.getFirstReachableUrl(Util.getAppModeFromRequest(request), locale, session);
    	
    	String tmp = Util.parseString((String)request.getParameter("dst"));
    	if (Util.isValid(tmp)) {
    		url =  tmp;
    	}
		
		if (Util.isValid(url)) {
			return new ModelAndView("redirect:" + url);
		} else {
			return new ModelAndView("redirect:/common/passwordupdate");
		}
    }
	
	/**
	 * 앱 로그인 페이지
	 */
    @RequestMapping(value = "/applogin", method = RequestMethod.GET)
    public String appLogin(Model model, Locale locale, HttpServletRequest request,
    		HttpSession session) {
    	
    	// 하이브리드 앱에서의 접근 z코드
    	model.addAttribute("appMode", Util.getAppModeFromRequest(request));
    	
		return "applogin";
	}
	
	//
	// [SignCast] ext -----------------------------------------------------------------
	//
	
	//
	// Spring Version 기준
	//
	// 1. /info/stb -> /dsg/agent/stbinfo
	// 2. /mon/stbsttsreport -> /dsg/agent/stbsttsreport
	// 3. /info/dctnt -> /dsg/agent/dctntinfo
	// 4. /mon/stbrccmd -> /dsg/agent/stbrccmd
	// 5. /info/dctntreport -> /dsg/agent/dctntreport
	// 11. /mgrlogin -> /dsg/mgr/mgrlogin
	// 12. /mgrstb -> /dsg/mgr/mgrstb
	// 13. /mgrcontentfile -> /dsg/mgr/mgrcontentfile
	// 14. /mgrschedule -> /dsg/mgr/mgrschedule
	// 15. /info/repos -> /dsg/mgr/mgrrepos
	// 16. /info/server -> /dsg/mgr/mgrserver
	
	//
	/**
	 * STB 초기 구동 시 서버에 등록된 STB 정보 반환
	 */
	@RequestMapping(value = "/info/stb", method = RequestMethod.GET)
    public String stbInfo(HttpServletRequest request, HttpServletResponse response, 
    		HttpSession session) {
		return "forward:/dsg/agent/stbinfo";
    }
	
	/**
	 * STB 구동 후 전달된 STB 정보에 따른 STB 상태 보고
	 */
	@RequestMapping(value = "/mon/stbsttsreport", method = RequestMethod.GET)
    public String stbStatusReport(HttpServletRequest request, HttpServletResponse response, 
    		HttpSession session) {
		return "forward:/dsg/agent/stbsttsreport";
    }
	
	/**
	 * 컨텐츠 동기화 시 필요한 게시된 컨텐츠 목록 획득
	 */
	@RequestMapping(value = "/info/dctnt", method = RequestMethod.GET)
    public String dctntInfo(HttpServletRequest request, HttpServletResponse response, 
    		HttpSession session) {
		return "forward:/dsg/agent/dctntinfo";
    }
	
	/**
	 * 컨텐츠 동기화 시 STB 존재 컨텐츠 파일 보고
	 */
	@RequestMapping(value = "/info/dctntreport", method = { RequestMethod.GET, RequestMethod.POST })
    public String dctntReport(HttpServletRequest request, HttpServletResponse response, 
    		HttpSession session) {
		return "forward:/dsg/agent/dctntreport";
    }
	
	/**
	 * STB 원격제어 명령 수행 결과 보고
	 */
	@RequestMapping(value = "/mon/stbrccmd", method = RequestMethod.GET)
    public String stbRcCmd(HttpServletRequest request, HttpServletResponse response, 
    		HttpSession session) {
		return "forward:/dsg/agent/stbrccmd";
    }
	
	/**
	 * SignCast Manager 로그인 인증
	 */
	@RequestMapping(value = "/mgrlogin", method = RequestMethod.POST)
    public String mgrLogin(HttpServletRequest request, HttpServletResponse response) {
		return "forward:/dsg/mgr/mgrlogin";
    }
	
	/**
	 * SignCast Manager 기기 정보 동기화
	 */
	@RequestMapping(value = "/mgrstb", method = RequestMethod.POST)
    public String mgrStb(HttpServletRequest request, HttpServletResponse response) {
		return "forward:/dsg/mgr/mgrstb";
    }
	
	/**
	 * SignCast Manager 컨텐츠 동기화 시 필요한 서버 컨텐츠 파일 목록
	 */
	@RequestMapping(value = "/mgrcontentfile", method = RequestMethod.POST)
    public String mgrContent(HttpServletRequest request, HttpServletResponse response) {
		return "forward:/dsg/mgr/mgrcontentfile";
    }
	
	/**
	 * SignCast Manager 스케줄 목록
	 */
	@RequestMapping(value = "/mgrschedule", method = RequestMethod.POST)
    public String mgrSchedule(HttpServletRequest request, HttpServletResponse response) {
		return "forward:/dsg/mgr/mgrschedule";
    }
	
	/**
	 * SignCast Manager 저장소 목록
	 */
	@RequestMapping(value = "/info/repos", method = RequestMethod.POST)
    public String mgrRepository(HttpServletRequest request, HttpServletResponse response) {
		return "forward:/dsg/mgr/mgrrepos";
    }
	
	/**
	 * SignCast Manager 서버 소프트웨어 정보
	 */
	@RequestMapping(value = "/info/server", method = RequestMethod.GET)
    public String mgrServer(HttpServletRequest request, HttpServletResponse response) {
		return "forward:/dsg/mgr/mgrserver";
    }

	
	/**
	 * SignCast Update 액션
	 */
    @RequestMapping(value = "/SignCastUpdate.xml", method = { RequestMethod.GET, RequestMethod.POST })
    public void updateXml(HttpServletRequest request, HttpServletResponse response) {
    	try {
    		response.sendRedirect("/dsg/agent/updateinfo");
    	} catch (IOException e) {
    		logger.error("updateXml", e);
    	}
    }
    
    /**
	 * 자산 QR 접근 시 이동
	 */
    @RequestMapping(value = "/asset", method = RequestMethod.GET)
    public void viewQR(Model model, Locale locale, HttpServletRequest request, 
    		HttpServletResponse response, HttpSession session) {
    	String ID = Util.parseString(request.getParameter("ID"), "");
    	
    	try {
    		response.sendRedirect("/ast/astview?ID=" + ID);
    	} catch (IOException e) {
    		logger.error("viewQR", e);
    	}
	}
	
	/**
	 * SSO 페이지(로그인 처리를 위한 임시 페이지)
	 */
    @RequestMapping(value = "/sso", method = RequestMethod.GET)
    public String toSsoLogin(Model model, Locale locale, HttpServletRequest request,
    		HttpSession session) {
    	
    	msgMgr.addCommonMessages(model, locale, session, request);
    	
    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
   					new Message("user", Util.parseString(request.getParameter("user"), "")),
   					new Message("referer", Util.parseString((String)request.getHeader("referer"), "")),
    			});
    	
		return "ssologin";
	}

}
