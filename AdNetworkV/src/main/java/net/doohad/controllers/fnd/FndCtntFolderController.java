package net.doohad.controllers.fnd;

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
import net.doohad.models.fnd.FndCtntFolder;
import net.doohad.models.service.AdcService;
import net.doohad.models.service.FndService;
import net.doohad.utils.Util;

/**
 * 컨텐츠 폴더 컨트롤러
 */
@Controller("fnd-ctnt-folder-controller")
@RequestMapping(value="/fnd/ctntfolder")
public class FndCtntFolderController {

	private static final Logger logger = LoggerFactory.getLogger(FndCtntFolderController.class);


    @Autowired 
    private FndService fndService;

    @Autowired 
    private AdcService adcService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 컨텐츠 폴더 페이지
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
    	model.addAttribute("pageTitle", "컨텐츠 폴더");
    	
    	
        return "fnd/ctntfolder";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request) {
    	try {
    		DataSourceResult result = fndService.getCtntFolderList(request);
    		
    		List<Tuple> countList = adcService.getCreatFileCountGroupByCtntFolderId();
    		HashMap<String, Long> countMap = new HashMap<String, Long>();
    		for(Tuple tuple : countList) {
    			countMap.put("K" + String.valueOf((Integer) tuple.get(0)), (Long) tuple.get(1));
    		}
    		
    		for(Object obj : result.getData()) {
    			FndCtntFolder ctntFolder = (FndCtntFolder)obj;
    			
    			Long value = countMap.get("K" + ctntFolder.getId());
    			if (value != null) {
    				ctntFolder.setCreatFileCount(value.intValue());
    			}
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 임의의 폴더명 생성
	 */
    private String getRandomFolderName() {
    	
		// folder name format: AAAA9999
		char[] alphaSeed = "ABCDEFGHJKLMNPQRSTUVWXYZ".toCharArray();
		char[] numberSeed = "1234567890".toCharArray();
		
		return Util.random(4, alphaSeed) + Util.random(4, numberSeed);
    }
    
    
	/**
	 * 추가 액션
	 */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody String create(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String name = (String)model.get("name");
    	String webPath = (String)model.get("webPath");
    	String localPath = (String)model.get("localPath");
    	
    	// 파라미터 검증
    	if (Util.isNotValid(webPath) || Util.isNotValid(localPath)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	if (Util.isNotValid(name)) {
    		name = getRandomFolderName();
    	}

    	
    	FndCtntFolder target = new FndCtntFolder(name, webPath, localPath, session);

        // 추가 로직:
        //   현재까지 등록된 전체 자료 수가 0건이라면, 이번 건을 "현재 기본 이용" 자료로 처리
    	if (fndService.getCtntFolderCount() == 0) {
    		target.setCurr(true);
    	}

        saveOrUpdate(target, locale, session);
        
        
        return "Ok";
    }
    
    
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String name = (String)model.get("name");
    	String webPath = (String)model.get("webPath");
    	String localPath = (String)model.get("localPath");
    	
    	// 파라미터 검증
    	if (Util.isNotValid(webPath) || Util.isNotValid(localPath)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	if (Util.isNotValid(name)) {
    		name = getRandomFolderName();
    	}

    	
    	FndCtntFolder target = fndService.getCtntFolder((int)model.get("id"));
    	if (target != null) {
        	
            target.setName(name);
    		target.setWebPath(webPath);
    		target.setLocalPath(localPath);

            
            saveOrUpdate(target, locale, session);
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(FndCtntFolder target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
    	
    	// 비즈니스 로직 검증
        
        // DB 작업 수행 결과 검증
        try {
            fndService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_FOLDER_NAME);
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_FOLDER_NAME);
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
    	
    	List<FndCtntFolder> ctntFolders = new ArrayList<FndCtntFolder>();

    	for (Object id : objs) {
    		FndCtntFolder ctntFolder = new FndCtntFolder();
    		
    		ctntFolder.setId((int)id);
    		
    		ctntFolders.add(ctntFolder);
    	}

    	try {
        	fndService.deleteCtntFolders(ctntFolders);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("destroy", dive);
        	throw new ServerOperationForbiddenException(StringInfo.DEL_ERROR_CHILD_AD);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }

    
	/**
	 * 기본 이용으로 설정 액션
	 */
    @RequestMapping(value = "/defaultvalue", method = RequestMethod.POST)
    public @ResponseBody String defaultValue(@RequestBody Map<String, Object> model, 
    		Locale locale, HttpSession session) {

		@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	if (objs.size() == 0) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}
    	
    	try {
        	for (Object id : objs) {
        		FndCtntFolder target = fndService.getCtntFolder((int)id);
        		
        		if (target != null) {
        			//
        			List<FndCtntFolder> list = fndService.getCtntFolderList();
        			for(FndCtntFolder row : list) {
        				if (row.isCurr()) {
        					row.setCurr(false);
        					row.touchWho(session);
        					
        					fndService.saveOrUpdate(row);
        				}
        			}
        			
        			target.setCurr(true);
        			target.touchWho(session);
        			
        			fndService.saveOrUpdate(target);
        			
        			break;
        		}
        	}
    	} catch (Exception e) {
    		logger.error("defaultValue", e);
    		throw new ServerOperationForbiddenException("OperationError");
    	}

        return "OK";
    }
}
