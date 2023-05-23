package net.doohad.controllers.common;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.UploadTransitionModel;
import net.doohad.utils.Util;

/**
 * 공통 컨트롤러
 */
@Controller("common-controller")
@RequestMapping(value="/common")
public class CommonController {
	private static final Logger logger = LoggerFactory.getLogger(CommonController.class);

	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private ModelManager modelMgr;
	
	/**
	 * 로그인 페이지(강제 로그아웃 메시지 처리 후)
	 */
    @RequestMapping(value = "/loginAfterForcedLogout", method = RequestMethod.GET)
    public String loginAfterForcedLogout(Model model, HttpServletRequest request, 
    		HttpServletResponse response, HttpSession session) {
    	model.addAttribute("forcedLogout", true);
		return "redirect:/";
    }
	
	/**
	 * 패스워드 변경 페이지
	 */
    @RequestMapping(value = "/passwordupdate", method = RequestMethod.GET)
    public String passwordUpdate(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	modelMgr.addMainMenuModel(model, locale, session, request);
    	msgMgr.addCommonMessages(model, locale, session, request);
    	
    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
    				new Message("pageTitle", "passwordupdate.title"),
    				new Message("label_current", "passwordupdate.current"),
    				new Message("label_new", "passwordupdate.new"),
    				new Message("label_confirm", "passwordupdate.confirm"),
    				new Message("btn_save", "passwordupdate.save"),
    				new Message("msg_samePassword", "passwordupdate.msg.samePassword"),
    				new Message("msg_updateComplete", "passwordupdate.msg.updateComplete"),
    				
    				new Message("msg_wrongLevel2Pwd", "passwordupdate.msg.wrongLevel2Pwd"),
    				new Message("msg_updateRequired", "passwordupdate.msg.updateRequired"),
    				new Message("val_pwdLevel", Util.parseString(Util.getFileProperty("password.level"), "0")),
    			});
    	
    	// 패스워드 유효 기간 초과에 의한 본 페이지 접근 확인
		String agingStr = Util.parseString(request.getParameter("aging"));
		
    	model.addAttribute("updateRequired", Util.isValid(agingStr) && agingStr.equals("Y"));
    	
        
    	// [SignCast] ext ----------------------------------------------------------- start
    	//
    	//
        
    	/*
    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
					new Message("navbar_recentTask", "navbar.recentTask"),
				});
    	*/
    	
    	//
    	//
    	// [SignCast] ext ------------------------------------------------------------- end

    	Util.prepareKeyRSA(model, session);
    	
        return "common/passwordupdate";
    }
	
	/**
	 * 파일 업로드 페이지
	 */
    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String upload(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {

    	String type = request.getParameter("type");
    	
    	UploadTransitionModel uploadModel = new UploadTransitionModel();
    	
    	try {
        	if (Util.isValid(type)) {
        		uploadModel.setType(type);
        		
        		// type에 따른 파일 유형 제한 및 메시지 추가
        		// uploadModel.setAllowedExtensions("[\".png\"]");
        		// uploadModel.setMessage(msgMgr.message("page.msg.addMsg", locale));
        	}
    	} catch (Exception e) {
    		logger.error("upload", e);
    	}

    	model.addAttribute("uploadModel", uploadModel);
    	
        return "common/modal/upload";
    }
    
    /**
     * 파일 업로드 저장 액션
     */
    @RequestMapping(value = "/uploadsave1", method = RequestMethod.POST)
    public @ResponseBody String save(@RequestParam List<MultipartFile> files,
    		@RequestParam int siteId, @RequestParam String type, HttpSession session) {
    	try {
    		if (Util.isValid(type)) {
    			if (type.equals("TEST")) {
        			String typeRootDir = Util.getPhysicalRoot("Logo");
        			
        	        for (MultipartFile file : files) {
        	        	if (!file.isEmpty()) {
        	        		File uploadedFile = new File(typeRootDir + "/" + file.getOriginalFilename());
        	        		FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(uploadedFile));
        	        	}
        	        }
    			}
    		}
    	} catch (Exception e) {
    		logger.error("uploadsave", e);
    	}
        
        // Return an empty string to signify success
        return "";
    }

}
