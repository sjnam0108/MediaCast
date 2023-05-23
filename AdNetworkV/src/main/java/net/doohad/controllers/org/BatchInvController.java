package net.doohad.controllers.org;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import net.doohad.exceptions.ServerOperationForbiddenException;
import net.doohad.info.StringInfo;
import net.doohad.models.AdnMessageManager;
import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.UploadTransitionModel;
import net.doohad.models.adn.AdnExcelRow;
import net.doohad.models.fnd.FndRegion;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.inv.InvSite;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgSiteCond;
import net.doohad.models.service.AdnService;
import net.doohad.models.service.FndService;
import net.doohad.models.service.InvService;
import net.doohad.models.service.KnlService;
import net.doohad.models.service.OrgService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.DropDownListItem;
import net.doohad.viewmodels.inv.InvUploadOverview;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * 인벤 일괄 업로드 컨트롤러
 */
@Controller("org-batch-inv-controller")
@RequestMapping(value="/org/batchinv")
public class BatchInvController {

	private static final Logger logger = LoggerFactory.getLogger(BatchInvController.class);


    @Autowired 
    private KnlService knlService;

    @Autowired 
    private AdnService adnService;

    @Autowired 
    private InvService invService;

    @Autowired 
    private FndService fndService;

    @Autowired 
    private OrgService orgService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	// 현재의 엑셀 템플릿 버전
	private static String CURRENT_TEMPLATE_VERSION = "v4";
	
	
	
	/**
	 * 인벤 일괄 업로드 페이지
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
    	model.addAttribute("pageTitle", "인벤 일괄 업로드");
    	
    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);


    	// 업로드 마법사에서 사용되는 업로드 진행 모델
    	UploadTransitionModel uploadModel = new UploadTransitionModel();

		uploadModel.setSaveUrl("/org/batchinv/uploadsave");
		uploadModel.setAllowedExtensions("[\".xlsx\"]");

    	model.addAttribute("uploadModel", uploadModel);
    	
    	
        return "org/batchinv";
    }
    
    
    /**
     * 품목 일괄 업로드 저장 액션
     */
    @RequestMapping(value = "/uploadsave", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody String uploadSave(@RequestParam List<MultipartFile> files, HttpSession session) {
    	
    	logger.info("/uploadsave new file entered. size = " + files.size());
    	
    	try {
			String typeRootDir = SolUtil.getPhysicalRoot("UpTemp");
			
	        for (MultipartFile file : files) {
	        	if (!file.isEmpty()) {
	        		File uploadedFile = new File(typeRootDir + "/" + file.getOriginalFilename());
	        		Util.checkParentDirectory(uploadedFile.getAbsolutePath());
	        		
	        		FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(uploadedFile));
	            	logger.info("/uploadsave file copy to " + uploadedFile.getAbsolutePath());
	        	}
	        }
    	} catch (Exception e) {
    		logger.error("uploadSave", e);
    	}
        
        // Return an empty string to signify success
        return "";
    }
    
    
    /**
     * 일괄 업로드 개요 자료 읽기 액션
     */
    @RequestMapping(value = "/readbatchoverview", method = RequestMethod.POST)
    public @ResponseBody InvUploadOverview readBatchOverview(@RequestBody Map<String, Object> model, 
    		Locale locale, HttpSession session) {

    	return getBatchOverview((String)model.get("file"), Util.getSessionMediumId(session));
    }
    
    private InvUploadOverview getBatchOverview(String filename, int mediumId) {
    	
    	InvUploadOverview retObj = new InvUploadOverview();
    	
    	if (Util.isValid(filename)) {
    		
    		retObj.setFilename(filename);
    		String tempFilename = filename.toLowerCase();
    		if (tempFilename.endsWith(".xlsx") && tempFilename.lastIndexOf(".v") > 0) {
    			retObj.setVersion(Util.removeFileExt(filename.substring(tempFilename.lastIndexOf(".v") + 1)));
    		}
    		
    		if (retObj.getVersion().equals("?")) {
    			retObj.setErrorMsg("파일명에 버전 정보가 정확하게 포함되지 않았습니다.");
    		} else if (!retObj.getVersion().equals(CURRENT_TEMPLATE_VERSION)) {
    			retObj.setErrorMsg("현재의 템플릿 버전에 맞추어 자료를 준비해 주십시오.");
    		} else {
        		
        		String typeRootDir = SolUtil.getPhysicalRoot("UpTemp");
        		File file = new File(typeRootDir + "/" + filename);
        		if (file.exists()) {
            		try {
        				FileInputStream fis = new FileInputStream(file.getAbsolutePath());
        				XSSFWorkbook workbook = new XSSFWorkbook(fis);
        				
        				retObj.setSiteCount(getValidExcelRowCount(workbook, 0,
        						getJSONObject(SolUtil.getOptValue(mediumId, "inven.default"))));
        				retObj.setScreenCount(getValidExcelRowCount(workbook, 1,
        						getJSONObject(SolUtil.getOptValue(mediumId, "inven.default"))));
        				
        				if (retObj.getSiteCount() + retObj.getScreenCount() < 1) {
        					retObj.setErrorMsg("변경 대상의 자료가 없거나, 잘못된 형식의 파일입니다.");
        				}
        				
        				workbook.close();
            			
            		} catch (Exception e) {
            			logger.error("readBatchOverview", e);
            			
            			retObj.setErrorMsg("자료 확인 중 예기치 않은 예외가 발생하였습니다.");
            		}
        		} else {
            		retObj.setErrorMsg("잘못된 경로 / 파일입니다.");
        		}
    		}
    	}
    	
    	return retObj;
    }
    
    private String getPropStringValue(JSONObject jsonObj, String propName) {
    	
    	if (jsonObj == null) {
    		return null;
    	}
    	
    	try {
    		
    		return Util.parseString(jsonObj.getString(propName));
    	} catch (Exception e) {
    		// 의도적인 예외 로깅 생략
    	}
    	
    	return null;
    }
    
    private Boolean getPropBooleanValue(JSONObject jsonObj, String propName) {
    	
    	if (jsonObj == null) {
    		return null;
    	}
    	
    	try {
    		
    		return jsonObj.getBoolean(propName);
    	} catch (Exception e) {
    		// 의도적인 예외 로깅 생략
    	}
    	
    	return null;
    }

    private JSONObject getJSONObject(String str) {
    	
    	if (Util.isNotValid(str)) {
    		return null;
    	}
    	
    	try {
    		return JSONObject.fromObject(JSONSerializer.toJSON(str));
    	} catch (Exception e) {
    	}
    	
    	return null;
    }
    
    private int getValidExcelRowCount(XSSFWorkbook workbook, int index, JSONObject mObj) {
    	
    	if (workbook != null) {
    		
    		XSSFSheet sheet = workbook.getSheetAt(index);
    		
    		if (index == 0) {
        		// 사이트 sheet, index == 0
    			
    			// A: 사이트ID	siteID
    			// B: 사이트명	siteName
    			// C: 위도		lat				v
    			// D: 경도		lng				v
    			// E: 시/도		region 일부
    			// F: 시/군/구	region 일부
    			// G: 주소		addr			v
    			// H: 입지 유형	siteCond		v
    			// I: 장소 유형	venueType		v
    			// J: 액션

				String mLat = getPropStringValue(mObj, "lat");
				String mLng = getPropStringValue(mObj, "lng");
				String mAddr = getPropStringValue(mObj, "addr");
				String mSiteCond = getPropStringValue(mObj, "siteCond");
				String mVenueType = getPropStringValue(mObj, "venueType");

    			int i = 0;
    			for (i = 1; i <= 10000; i ++) {
	    			String valA = Util.getExcelCellValue(sheet, i, 0);
	    			String valB = Util.getExcelCellValue(sheet, i, 1);
	    			String valC = Util.getExcelCellValue(sheet, i, 2);
	    			String valD = Util.getExcelCellValue(sheet, i, 3);
	    			String valE = Util.getExcelCellValue(sheet, i, 4);
	    			//String valF = Util.getExcelCellValue(sheet, i, 5);
	    			String valG = Util.getExcelCellValue(sheet, i, 6);
	    			String valH = Util.getExcelCellValue(sheet, i, 7);
	    			String valI = Util.getExcelCellValue(sheet, i, 8);
	    			String valJ = Util.getExcelCellValue(sheet, i, 9);
    				if (Util.isValid(valA) &&
    						Util.isValid(valB) &&
    						(Util.isValid(valC) || Util.isValid(mLat)) &&
    						(Util.isValid(valD) || Util.isValid(mLng)) &&
    						Util.isValid(valE) &&
    						(Util.isValid(valG) || Util.isValid(mAddr)) &&
    						(Util.isValid(valH) || Util.isValid(mSiteCond)) &&
    						(Util.isValid(valI) || Util.isValid(mVenueType)) &&
    						Util.isValid(valJ)) {
    					// 5번(시/군/구)이 빠진 이유는 "세종시"의 경우 시도군이 없기 때문
    				} else {
    					return i - 1;
    				}
    			}
    			
    			return i;
    		} else if (index == 1) {
    			// 매체 화면 sheet, index == 1
    			
    			// A: 화면ID			screenID
    			// B: 화면명			screenName
    			// C: 사이트ID			siteID
    			// D: 해상도			reso			v
    			// E: 동영상허용		video			v
    			// F: 이미지허용		image			v
    			// G: 활성화 여부		active			v
    			// H: 광고 서버 이용	ad				v
    			// I: 액션

				String mReso = getPropStringValue(mObj, "reso");
				Boolean mVideo = getPropBooleanValue(mObj, "video");
				Boolean mImage = getPropBooleanValue(mObj, "image");
				Boolean mActive = getPropBooleanValue(mObj, "active");
				Boolean mAd = getPropBooleanValue(mObj, "ad");
    			
    			int i = 0;
    			for (i = 1; i <= 10000; i ++) {
	    			String valA = Util.getExcelCellValue(sheet, i, 0);
	    			String valB = Util.getExcelCellValue(sheet, i, 1);
	    			String valC = Util.getExcelCellValue(sheet, i, 2);
	    			String valD = Util.getExcelCellValue(sheet, i, 3);
	    			String valE = Util.getExcelCellValue(sheet, i, 4);
	    			String valF = Util.getExcelCellValue(sheet, i, 5);
	    			String valG = Util.getExcelCellValue(sheet, i, 6);
	    			String valH = Util.getExcelCellValue(sheet, i, 7);
	    			String valI = Util.getExcelCellValue(sheet, i, 8);
    				if (Util.isValid(valA) &&
    						Util.isValid(valB) &&
    						Util.isValid(valC) &&
    						(Util.isValid(valD) || Util.isValid(mReso)) &&
    						(Util.isValid(valE) || mVideo != null) &&
    						(Util.isValid(valF) || mImage != null) &&
    						(Util.isValid(valG) || mActive != null) &&
    						(Util.isValid(valH) || mAd != null) &&
    						Util.isValid(valI)) {
    				} else {
    					return i - 1;
    				}
    			}
    			
    			return i;
    		}
    	}
    	
    	return 0;
    }
    
    
	/**
	 * 업로드 파일 자료를 임시 저장 공간에 등록 액션
	 */
    @RequestMapping(value = "/replacetempdata", method = RequestMethod.POST)
    public @ResponseBody String replaceTempData(@RequestBody Map<String, Object> model, 
    		Locale locale, HttpSession session) {
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	if (medium == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}
		
    	
		logger.info("replaceExcelTempData: [" + medium.getName() + "]");
    	
    	boolean delResult = adnService.deleteBulkExcelRowsByMediumId(medium.getId());
    	if (!delResult) {
    		throw new ServerOperationForbiddenException(StringInfo.DEL_ERROR_PREV_DATA);
    	}
		logger.info("replaceExcelTempData: [" + medium.getName() + "] - delete all prev data: " + 
				String.valueOf(delResult));
    	
    	
    	String filename = (String)model.get("file");

    	InvUploadOverview retObj = getBatchOverview(filename, medium.getId());
    	
    	if (retObj != null && Util.isValid(retObj.getErrorMsg())) {
    		throw new ServerOperationForbiddenException(retObj.getErrorMsg());
    	}
    	
    	if (Util.isValid(filename)) {
    		String typeRootDir = SolUtil.getPhysicalRoot("UpTemp");
    		File file = new File(typeRootDir + "/" + filename);
    		if (file.exists()) {
    			
        		try (
        				FileInputStream fis = new FileInputStream(file.getAbsolutePath());
        				XSSFWorkbook workbook = new XSSFWorkbook(fis);
        			) {
    				
    				
    				
    				JSONObject mObj = getJSONObject(SolUtil.getOptValue(medium.getId(), "inven.default"));
        			
        			// A: 사이트ID	siteID
        			// B: 사이트명	siteName
        			// C: 위도		lat				v
        			// D: 경도		lng				v
        			// E: 시/도		region 일부
        			// F: 시/군/구	region 일부
        			// G: 주소		addr			v
        			// H: 입지 유형	siteCond		v
        			// I: 장소 유형	venueType		v
        			// J: 액션

    				String mLat = getPropStringValue(mObj, "lat");
    				String mLng = getPropStringValue(mObj, "lng");
    				String mAddr = getPropStringValue(mObj, "addr");
    				String mSiteCond = getPropStringValue(mObj, "siteCond");
    				String mVenueType = getPropStringValue(mObj, "venueType");

    				
    	    		XSSFSheet sheet = workbook.getSheetAt(0);
    	    		int i = 0;
    	    		for (i = 1; i <= 10000; i ++) {
    	    			AdnExcelRow row = new AdnExcelRow(medium, "T", session);
    	    			
    	    			String valA = Util.getExcelCellValue(sheet, i, 0);
    	    			String valB = Util.getExcelCellValue(sheet, i, 1);
    	    			String valC = Util.getExcelCellValue(sheet, i, 2);
    	    			String valD = Util.getExcelCellValue(sheet, i, 3);
    	    			String valE = Util.getExcelCellValue(sheet, i, 4);
    	    			String valF = Util.getExcelCellValue(sheet, i, 5);
    	    			String valG = Util.getExcelCellValue(sheet, i, 6);
    	    			String valH = Util.getExcelCellValue(sheet, i, 7);
    	    			String valI = Util.getExcelCellValue(sheet, i, 8);
    	    			String valJ = Util.getExcelCellValue(sheet, i, 9);
    	    			
    	    			if (Util.isNotValid(valC) && Util.isValid(mLat)) { valC = mLat; }
    	    			if (Util.isNotValid(valD) && Util.isValid(mLng)) { valD = mLng; }
    	    			if (Util.isNotValid(valG) && Util.isValid(mAddr)) { valG = mAddr; }
    	    			if (Util.isNotValid(valH) && Util.isValid(mSiteCond)) { valH = mSiteCond; }
    	    			if (Util.isNotValid(valI) && Util.isValid(mVenueType)) { valI = mVenueType; }
    	    			
    	    			// valF 유효성 검사가 생략된 이유는 "세종시 시/군/구" 때문
    	    			if (Util.isValid(valA) && Util.isValid(valB) && Util.isValid(valC) &&
    	    					Util.isValid(valD) && Util.isValid(valE) && Util.isValid(valG) &&
    	    					Util.isValid(valH) && Util.isValid(valI) && Util.isValid(valJ)) {
    	    				
    	    				if (Util.parseInt(valA) > -1) {
    	    					valA = "Site" + valA;
    	    				}
    	    				row.setColA(valA);
    	    				row.setColB(valB);
    	    				row.setColC(valC);
    	    				row.setColD(valD);
    	    				row.setColE(valE);
    	    				row.setColF(valF);
    	    				row.setColG(valG);
    	    				row.setColH(valH);
    	    				row.setColI(valI);
    	    				row.setColJ(valJ);
        	    			
        	    			adnService.saveOrUpdate(row);
        	    			
        	    			if (i % 500 == 0) {
        	    				logger.info("replaceExcelTempData: [" + medium.getName() + "] - site " + 
        	    						String.valueOf(i) + " rows...");
        	    			}
    	    			} else {
    	    				break;
    	    			}
    	    		}
	    			
    				logger.info("replaceExcelTempData: [" + medium.getName() + "] - site " + 
    						String.valueOf(i - 1) + " rows completed");
    				
        			
        			// A: 화면ID			screenID
        			// B: 화면명			screenName
        			// C: 사이트ID			siteID
        			// D: 해상도			reso			v
        			// E: 동영상허용		video			v
        			// F: 이미지허용		image			v
        			// G: 활성화 여부		active			v
        			// H: 광고 서버 이용	ad				v
        			// I: 액션

    				String mReso = getPropStringValue(mObj, "reso");
    				Boolean mVideo = getPropBooleanValue(mObj, "video");
    				Boolean mImage = getPropBooleanValue(mObj, "image");
    				Boolean mActive = getPropBooleanValue(mObj, "active");
    				Boolean mAd = getPropBooleanValue(mObj, "ad");
    	    		
    				
    	    		sheet = workbook.getSheetAt(1);
    	    		for (i = 1; i <= 10000; i ++) {
    	    			AdnExcelRow row = new AdnExcelRow(medium, "C", session);
    	    			
    	    			String valA = Util.getExcelCellValue(sheet, i, 0);
    	    			String valB = Util.getExcelCellValue(sheet, i, 1);
    	    			String valC = Util.getExcelCellValue(sheet, i, 2);
    	    			String valD = Util.getExcelCellValue(sheet, i, 3);
    	    			String valE = Util.getExcelCellValue(sheet, i, 4);
    	    			String valF = Util.getExcelCellValue(sheet, i, 5);
    	    			String valG = Util.getExcelCellValue(sheet, i, 6);
    	    			String valH = Util.getExcelCellValue(sheet, i, 7);
    	    			String valI = Util.getExcelCellValue(sheet, i, 8);
    	    			
    	    			if (Util.isNotValid(valD) && Util.isValid(mReso)) { valD = mReso; }
    	    			if (Util.isNotValid(valE) && mVideo != null) { valE = mVideo.booleanValue() ? "Y" : "N"; }
    	    			if (Util.isNotValid(valF) && mImage != null) { valF = mImage.booleanValue() ? "Y" : "N"; }
    	    			if (Util.isNotValid(valG) && mActive != null) { valG = mActive.booleanValue() ? "Y" : "N"; }
    	    			if (Util.isNotValid(valH) && mAd != null) { valH = mAd.booleanValue() ? "Y" : "N"; }
    	    			
    	    			if (Util.isValid(valA) && Util.isValid(valB) && Util.isValid(valC) &&
    	    					Util.isValid(valD) && Util.isValid(valE) && Util.isValid(valF) &&
    	    					Util.isValid(valG) && Util.isValid(valH) && Util.isValid(valI)) {
    	    				
    	    				if (Util.parseInt(valA) > -1) {
    	    					valA = "Scr" + valA;
    	    				}
    	    				if (Util.parseInt(valC) > -1) {
    	    					valC = "Site" + valC;
    	    				}
    	    				
    	    				row.setColA(valA);
    	    				row.setColB(valB);
    	    				row.setColC(valC);
    	    				row.setColD(valD);
    	    				row.setColE(valE);
    	    				row.setColF(valF);
    	    				row.setColG(valG);
    	    				row.setColH(valH);
    	    				row.setColI(valI);
        	    			
        	    			adnService.saveOrUpdate(row);
        	    			
        	    			if (i % 500 == 0) {
        	    				logger.info("replaceExcelTempData: [" + medium.getName() + "] - screen " + 
        	    						String.valueOf(i) + " rows...");
        	    			}
    	    			} else {
    	    				break;
    	    			}
    	    		}
	    			
    				logger.info("replaceExcelTempData: [" + medium.getName() + "] - screen " + 
    						String.valueOf(i - 1) + " rows completed");
    	    		
    				
    	    		logger.info("replaceExcelTempData: [" + medium.getName() + "] - completed");

        		} catch (Exception e) {
        			logger.error("replaceTempData", e);
        		}
    		}
    	}
    	
        return "OK";
    }
    
    
	/**
	 * 읽기 액션 - 사이트
	 */
    @RequestMapping(value = "/readSite", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readSite(@RequestBody DataSourceRequest request) {
    	try {
    		
    		return adnService.getExcelRowList(request, "T");
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 화면
	 */
    @RequestMapping(value = "/readScreen", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readScreen(@RequestBody DataSourceRequest request) {
    	try {
    		
    		return adnService.getExcelRowList(request, "C");
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }

    
    /**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<AdnExcelRow> rows = new ArrayList<AdnExcelRow>();

    	for (Object id : objs) {
    		AdnExcelRow row = new AdnExcelRow();
    		
    		row.setId((int)id);
    		
    		rows.add(row);
    	}
    	
    	try {
        	adnService.deleteExcelRows(rows);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }
    
    
	/**
	 * 읽기 액션 - 일괄 작업 결과 코드 정보
	 */
    @RequestMapping(value = "/readResults", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readResults(Locale locale) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-asterisk fa-fw text-muted", "초기", "I"));
		list.add(new DropDownListItem("fa-regular fa-flag-checkered fa-fw", "성공", "S"));
		list.add(new DropDownListItem("fa-regular fa-circle-stop fa-fw text-red", "실패", "F"));
		list.add(new DropDownListItem("fa-regular fa-circle-exclamation fa-fw text-yellow", "통과", "P"));
		
		return list;
    }
    
    
	/**
	 * 사이트 일괄 작업 시작 액션
	 */
    @RequestMapping(value = "/startsitebatch", method = RequestMethod.POST)
    public @ResponseBody String startSiteBatch(@RequestBody Map<String, Object> model, HttpSession session) {
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	if (medium == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}
    	
    	List<AdnExcelRow> list = adnService.getExcelRowListByMediumIdType(medium.getId(), "T");
		Collections.sort(list, new Comparator<AdnExcelRow>() {
	    	public int compare(AdnExcelRow item1, AdnExcelRow item2) {
	    		return item1.getId() - item2.getId();
	    	}
	    });

		
		logger.info("startSiteBatch: [" + medium.getName() + "]");
    	
		String regionCode = "";
		String regionName = "";
		
		Boolean success = null;
		Date now = new Date();
		int cnt = 0;
		
		//
		// v3 항목 비교
		//
		//    colA: 사이트ID(PK)*	colB: 사이트명*			colC: 위도*
		//    colD: 경도*			colE: 시/도*			colF: 시/군/구*
		//    colG: 주소			colH: 입지 유형			colI: 장소 유형
		//    colJ: 액션
		//
		for(AdnExcelRow row : list) {
			cnt ++;
			success = null;
			
			InvSite site = invService.getSite(medium, row.getColA());
			
			// 액션 column
			if (Util.isValid(row.getColJ())) {
				if (row.getColJ().equals("U")) {
					FndRegion region = fndService.getRegionByName(row.getColE() + " " + row.getColF());
					regionCode = (region == null) ? "" : region.getCode();
					regionName = (region == null) ? "" : region.getName();

					if (site == null) {
						InvSite target = new InvSite(medium, row.getColA(), row.getColB(), row.getColC(), 
								row.getColD(), regionCode, row.getColG(), new Date(), null, "", session);
						target.setRegionName(regionName);
						target.setVenueType(row.getColI());
						
						OrgSiteCond siteCond = orgService.getSiteCond(target.getMedium(), row.getColH());
						target.setSiteCond(siteCond);
						
				        try {
							success = false;
				            invService.saveOrUpdate(target);
				            success = true;
				        } catch (DataIntegrityViolationException dive) {
				    		logger.error("startSiteBatch", dive);
				        } catch (ConstraintViolationException cve) {
				    		logger.error("startSiteBatch", cve);
				        } catch (Exception e) {
				    		logger.error("startSiteBatch", e);
				        }
					} else {
						// 기존 자료 변경: 메모 항목에 임시로 지역입지 정보 저장
						// 변경되는 내용을 모두 비교하여 변경 요소가 있을 때만 변경하고, who 정보도 수정
						boolean updated = false;
						
						// 유효종료일 이전 자료만 적용, 지난 자료는 PASS 처리
						if (site.getEffectiveEndDate() != null && 
    							now.after(site.getEffectiveEndDate())) {
    					} else {
    						if (!site.getName().equals(row.getColB())) {
    							updated = true;
    							site.setName(row.getColB());
    						}
    						if (!site.getLatitude().equals(row.getColC())) {
    							updated = true;
    							site.setLatitude(row.getColC());
    						}
    						if (!site.getLongitude().equals(row.getColD())) {
    							updated = true;
    							site.setLongitude(row.getColD());
    						}
    						if (!site.getRegionCode().equals(regionCode)) {
    							updated = true;
    							site.setRegionCode(regionCode);
    						}
    						if (!site.getRegionName().equals(regionName)) {
    							updated = true;
    							site.setRegionName(regionName);
    						}
    						if (Util.isValid(site.getAddress())) {
    							if (Util.isValid(row.getColG())) {
    								if (!site.getAddress().equals(row.getColG())) {
    									updated = true;
    									site.setAddress(row.getColG());
    								}
    							} else {
    								updated = true;
    								site.setAddress("");
    							}
    						} else {
    							if (Util.isValid(row.getColG())) {
    								updated = true;
    								site.setAddress(row.getColG());
    							}
    						}
    						// 입지 유형
    						if (Util.isValid(row.getColH())) {
    							if (!row.getColH().equals(site.getSiteCond().getCode())) {
            						OrgSiteCond siteCond = orgService.getSiteCond(site.getMedium(), row.getColH());
            						if (siteCond != null) {
            							updated = true;
            							site.setSiteCond(siteCond);
            						}
    							}
    						}
    						//
    						if (!site.getVenueType().equals(row.getColI())) {
    							updated = true;
    							site.setVenueType(row.getColI());
    						}
    						
    						
    						if (updated) {
    							site.touchWho(session);
    							
    					        try {
    					        	success = false;
    					            invService.saveOrUpdate(site);
    					            success = true;
    					        } catch (DataIntegrityViolationException dive) {
    					    		logger.error("startSiteBatch", dive);
    					        } catch (ConstraintViolationException cve) {
    					    		logger.error("startSiteBatch", cve);
    					        } catch (Exception e) {
    					    		logger.error("startSiteBatch", e);
    					        }
    						}
    					}
					}
				} else if (row.getColJ().equals("D")) {
					if (site != null) {
						// 유효종료일 이전 자료만 적용, 지난 자료는 PASS 처리
						if (site.getEffectiveEndDate() != null && 
    							now.after(site.getEffectiveEndDate())) {
    					} else {
    						// mysql 버그로 인해 수초 수동 조정
    						Date endDate = Util.setMaxTimeOfDate(now);
    						endDate = Util.addSeconds(endDate, -3);
    						
    						site.setEffectiveEndDate(endDate);
    						site.touchWho(session);
							
					        try {
					        	success = false;
					            invService.saveOrUpdate(site);
					            success = true;
					        } catch (DataIntegrityViolationException dive) {
					    		logger.error("startSiteBatch", dive);
					        } catch (ConstraintViolationException cve) {
					    		logger.error("startSiteBatch", cve);
					        } catch (Exception e) {
					    		logger.error("startSiteBatch", e);
					        }
    					}
					}
				}
			}
			
			if (cnt % 500 == 0) {
				logger.info("startSiteBatch: [" + medium.getName() + "] - site " + 
						String.valueOf(cnt) + " rows...");
			}
			
	        try {
	        	String result = "P";
	        	if (success != null) {
	        		result = success.booleanValue() ? "S" : "F";
	        	}
	        	row.setResult(result);
	        	
	        	adnService.saveOrUpdate(row);
	        } catch (Exception e) {
	    		logger.error("startSiteBatch", e);
	        }
		}
		
		logger.info("startSiteBatch: [" + medium.getName() + "] - " + cnt + " rows completed");

        return "OK";
    }
    
    
	/**
	 * 매체 화면 일괄 작업 시작 액션
	 */
    @RequestMapping(value = "/startscreenbatch", method = RequestMethod.POST)
    public @ResponseBody String startScreenBatch(@RequestBody Map<String, Object> model, HttpSession session) {
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	if (medium == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}
    	
    	List<AdnExcelRow> list = adnService.getExcelRowListByMediumIdType(medium.getId(), "C");
		Collections.sort(list, new Comparator<AdnExcelRow>() {
	    	public int compare(AdnExcelRow item1, AdnExcelRow item2) {
	    		return item1.getId() - item2.getId();
	    	}
	    });

		
		logger.info("startScreenBatch: [" + medium.getName() + "]");
    	
		Boolean success = null;
		int statusChangedOldSiteId = 0;
		int statusChangedSiteId = 0;
		
		Date now = new Date();
		int cnt = 0;
		
		//
		// v3 항목 비교
		//
		//    colA: 화면ID(PK)*		colB: 화면명*			colC: 사이트ID*
		//    colD: 해상도*			colE: 동영상허용*		colF: 이미지허용*
		//    colG: 활성화여부*		colH: 광고서버에 이용*	colI: 액션
		//

		for(AdnExcelRow row : list) {
			cnt ++;
			success = null;
			statusChangedOldSiteId = 0;
			statusChangedSiteId = 0;
			
			InvScreen screen = invService.getScreen(medium, row.getColA());
			
			// 액션 column
			if (Util.isValid(row.getColI())) {
				if (row.getColI().equals("U")) {
					
					if (screen == null) {
						// 신규 등록
						
						InvSite site = invService.getSite(medium, row.getColC());
						if (site != null) {
							
							// 신규 등록 시에는 각종 boolean값은 Y 검증 중심으로 한다.
							boolean videoAllowed = Util.isValid(row.getColE()) && row.getColE().equals("Y");
							boolean imageAllowed = Util.isValid(row.getColF()) && row.getColF().equals("Y");
							boolean activeStatus = Util.isValid(row.getColG()) && row.getColG().equals("Y");
							boolean adServerAvailable = Util.isValid(row.getColH()) && row.getColH().equals("Y");
							
							
					    	InvScreen target = new InvScreen(site, row.getColA(), row.getColB(), activeStatus, 
					    			row.getColD(), imageAllowed, videoAllowed, new Date(), null, "", session);
					    	
					    	target.setAdServerAvailable(adServerAvailable);
					    	
							
					        try {
								success = false;
					            invService.saveOrUpdate(target);
					            success = true;
					            
					            statusChangedSiteId = target.getSite().getId();
					        } catch (DataIntegrityViolationException dive) {
					    		logger.error("startScreenBatch", dive);
					        } catch (ConstraintViolationException cve) {
					    		logger.error("startScreenBatch", cve);
					        } catch (Exception e) {
					    		logger.error("startScreenBatch", e);
					        }
						}
					} else {
						// 기존 자료 변경
						
						// 변경되는 내용을 모두 비교하여 변경 요소가 있을 때만 변경하고, who 정보도 수정
						boolean updated = false;
						
						// 유효종료일 이전 자료만 적용, 지난 자료는 PASS 처리
						if (screen.getEffectiveEndDate() != null && 
    							now.after(screen.getEffectiveEndDate())) {
    					} else {
    						// site 변경 확인
    						if (Util.isValid(row.getColC()) && !screen.getSite().getShortName().equals(row.getColC())) {
    							InvSite site = invService.getSite(medium, row.getColC());
    							if (site != null) {
        							statusChangedOldSiteId = screen.getSite().getId();
        							
    								updated = true;
    								screen.setSite(site);
        							
        							statusChangedSiteId = site.getId();
    							}
    						}
    						
    						if (!screen.getName().equals(row.getColB())) {
    							updated = true;
    							screen.setName(row.getColB());
    						}
    						if (!screen.getResolution().equals(row.getColD())) {
    							updated = true;
    							screen.setResolution(row.getColD());
    						}
    						
    						// 기존 자료 변경 시에는 각종 boolean값은 대상 값 Y/N 모두 확인한다.
    						if (Util.isValid(row.getColE())) {
    							boolean value = row.getColE().equals("Y");
        						if (screen.isVideoAllowed() != value) {
        							updated = true;
        							screen.setVideoAllowed(value);
        						}
    						}
    						if (Util.isValid(row.getColF())) {
    							boolean value = row.getColF().equals("Y");
        						if (screen.isImageAllowed() != value) {
        							updated = true;
        							screen.setImageAllowed(value);
        						}
    						}
    						if (Util.isValid(row.getColG())) {
    							boolean value = row.getColG().equals("Y");
        						if (screen.isActiveStatus() != value) {
        							updated = true;
        							screen.setActiveStatus(value);
        							
        							statusChangedSiteId = screen.getSite().getId();
        						}
    						}
    						if (Util.isValid(row.getColH())) {
    							boolean value = row.getColH().equals("Y");
        						if (screen.isAdServerAvailable() != value) {
        							updated = true;
        							screen.setAdServerAvailable(value);
        						}
    						}
    						
    						if (updated) {
    							screen.touchWho(session);
    							
    					        try {
    					        	success = false;
    					            invService.saveOrUpdate(screen);
    					            success = true;
    					        } catch (DataIntegrityViolationException dive) {
    					    		logger.error("startScreenBatch", dive);
    					        } catch (ConstraintViolationException cve) {
    					    		logger.error("startScreenBatch", cve);
    					        } catch (Exception e) {
    					    		logger.error("startScreenBatch", e);
    					        }
    						}
    					}
					}
				} else if (row.getColI().equals("D")) {
					if (screen != null) {
						// 유효종료일 이전 자료만 적용, 지난 자료는 PASS 처리
						if (screen.getEffectiveEndDate() != null && 
    							now.after(screen.getEffectiveEndDate())) {
    					} else {
    						// mysql 버그로 인해 수초 수동 조정
    						Date endDate = Util.setMaxTimeOfDate(now);
    						endDate = Util.addSeconds(endDate, -3);
    						
    						screen.setEffectiveEndDate(endDate);
    						screen.setActiveStatus(false);
    						screen.touchWho(session);
							
					        try {
					        	success = false;
					            invService.saveOrUpdate(screen);
					            success = true;
					            
					            statusChangedSiteId = screen.getSite().getId();
					        } catch (DataIntegrityViolationException dive) {
					    		logger.error("startScreenBatch", dive);
					        } catch (ConstraintViolationException cve) {
					    		logger.error("startScreenBatch", cve);
					        } catch (Exception e) {
					    		logger.error("startScreenBatch", e);
					        }
    					}
					}
				}
			}
			
			if (statusChangedSiteId > 0) {
	            // 화면 상태 및 수량 기준으로 사이트 정보 변경
	            invService.updateSiteActiveStatusCountBasedScreens(statusChangedSiteId);
			}
			if (statusChangedOldSiteId > 0) {
	            // 화면 상태 및 수량 기준으로 사이트 정보 변경
	            invService.updateSiteActiveStatusCountBasedScreens(statusChangedOldSiteId);
			}
			
			if (cnt % 500 == 0) {
				logger.info("startScreenBatch: [" + medium.getName() + "] - screen " + 
						String.valueOf(cnt) + " rows...");
			}
			
	        try {
	        	String result = "P";
	        	if (success != null) {
	        		result = success.booleanValue() ? "S" : "F";
	        	}
	        	row.setResult(result);
	        	
	        	adnService.saveOrUpdate(row);
	        } catch (Exception e) {
	    		logger.error("startScreenBatch", e);
	        }
		}
		
		logger.info("startScreenBatch: [" + medium.getName() + "] - " + cnt + " rows completed");

        return "OK";
    }

}
