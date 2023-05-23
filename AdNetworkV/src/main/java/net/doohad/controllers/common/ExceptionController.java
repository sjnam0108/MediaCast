package net.doohad.controllers.common;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.doohad.models.Message;
import net.doohad.models.MessageManager;

/**
 * 예외 처리 컨트롤러
 */
@Controller("exception-controller")
@RequestMapping(value="/common/error")
public class ExceptionController {
	private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);
    
	@Autowired
	private MessageManager msgMgr;
	
	/**
	 * 400 예외 페이지
	 */
    @RequestMapping(value = "/400", method = RequestMethod.GET)
    public String exception400(Model model, Locale locale, HttpServletRequest request, HttpSession session) {
    	logException(request);
    	
    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
    				new Message("pageTitle", "error.pageTitle"),
    				new Message("code", "error.code.400"),
    				new Message("title", "error.title.400"),
    				new Message("desc", "error.desc.400"),
    			});
    	
        return "common/error";
    }
	
	/**
	 * 401 예외 페이지
	 */
    @RequestMapping(value = "/401", method = RequestMethod.GET)
    public String exception401(Model model, Locale locale, HttpServletRequest request, HttpSession session) {
    	logException(request);
    	
    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
    				new Message("pageTitle", "error.pageTitle"),
    				new Message("code", "error.code.401"),
    				new Message("title", "error.title.401"),
    				new Message("desc", "error.desc.401"),
    			});
    	
        return "common/error";
    }
	
	/**
	 * 403 예외 페이지
	 */
    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public String exception403(Model model, Locale locale, HttpServletRequest request, HttpSession session) {
    	logException(request);
    	
    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
    				new Message("pageTitle", "error.pageTitle"),
    				new Message("code", "error.code.403"),
    				new Message("title", "error.title.403"),
    				new Message("desc", "error.desc.403"),
    			});
    	
        return "common/error";
    }
	
	/**
	 * 404 예외 페이지
	 */
    @RequestMapping(value = "/404", method = RequestMethod.GET)
    public String exception404(Model model, Locale locale, HttpServletRequest request, HttpSession session) {
    	logException(request);
    	
    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
    				new Message("pageTitle", "error.pageTitle"),
    				new Message("code", "error.code.404"),
    				new Message("title", "error.title.404"),
    				new Message("desc", "error.desc.404"),
    			});
    	
        return "common/error";
    }
	
	/**
	 * 500 예외 페이지
	 */
    @RequestMapping(value = "/500", method = RequestMethod.GET)
    public String exception500(Model model, Locale locale, HttpServletRequest request, HttpSession session) {
    	logException(request);
    	
    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
    				new Message("pageTitle", "error.pageTitle"),
    				new Message("code", "error.code.500"),
    				new Message("title", "error.title.500"),
    				new Message("desc", "error.desc.500"),
    			});
    	
    	return "common/error";
    }
	
	/**
	 * Throwable 예외 유형 페이지
	 */
    @RequestMapping(value = "/throwable", method = RequestMethod.GET)
    public String throwable(Model model, Locale locale, HttpServletRequest request, HttpSession session) {
    	logException(request);
    	
    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
    				new Message("pageTitle", "error.pageTitle"),
    				new Message("code", "error.code"),
    				new Message("desc", "error.desc"),
    			});
    	
        return "common/error";
    }
	
    private void logException(HttpServletRequest request) {
    	logger.info("==========");
    	logger.info("Exception log");
    	logger.info("----------");
    	logger.info("status_code: " + request.getAttribute("javax.servlet.error.status_code"));
    	logger.info("exception_type: " + request.getAttribute("javax.servlet.error.exception_type"));
    	logger.info("message: " + request.getAttribute("javax.servlet.error.message"));
    	logger.info("request_uri: " + request.getAttribute("javax.servlet.error.request_uri"));
    	logger.info("exception: " + request.getAttribute("javax.servlet.error.exception"));
    	logger.info("servlet_name: " + request.getAttribute("javax.servlet.error.servlet_name"));
    	logger.info("==========");
    }
}
