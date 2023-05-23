package net.doohad.controllers.adc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.io.Files;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import net.bramp.ffmpeg.probe.FFmpegStream.CodecType;
import net.doohad.exceptions.ServerOperationForbiddenException;
import net.doohad.info.StringInfo;
import net.doohad.models.AdnMessageManager;
import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.UploadTransitionModel;
import net.doohad.models.adc.AdcCampaign;
import net.doohad.models.adc.AdcCreatFile;
import net.doohad.models.adc.AdcCreative;
import net.doohad.models.fnd.FndCtntFolder;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgAdvertiser;
import net.doohad.models.service.AdcService;
import net.doohad.models.service.FndService;
import net.doohad.models.service.KnlService;
import net.doohad.models.service.OrgService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;

/**
 * 광고 소재 컨트롤러(소재 파일)
 */
@Controller("adc-creative-file-controller")
@RequestMapping(value="/adc/creative/files")
public class AdcCreativeFileController {

	private static final Logger logger = LoggerFactory.getLogger(AdcCreativeFileController.class);

	
    @Autowired 
    private AdcService adcService;

    @Autowired 
    private OrgService orgService;

    @Autowired 
    private KnlService knlService;

    @Autowired 
    private FndService fndService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;

    
	/**
	 * 광고 소재 컨트롤러(소재 파일)
	 */
    @RequestMapping(value = {"/{advId}", "/{advId}/", "/{advId}/{creatId}", "/{advId}/{creatId}/"}, method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response, HttpSession session,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap,
    		Model model, Locale locale) {

    	OrgAdvertiser advertiser = orgService.getAdvertiser(Util.parseInt(pathMap.get("advId")));
    	if (advertiser == null || advertiser.getMedium().getId() != Util.getSessionMediumId(session)) {
    		return "forward:/adc/creative";
    	}

    	// "현재" 광고 소재 선택 변경의 경우
    	int creatId = Util.parseInt(pathMap.get("creatId"));
    	if (creatId > 0) {
    		AdcCreative creative = adcService.getCreative(creatId);
    		if (creative == null || creative.getAdvertiser().getId() != advertiser.getId()) {
    			creatId = -1;
    		}
    	}
    	
		
		List<AdcCampaign> campList = adcService.getCampaignLisyByAdvertiserId(advertiser.getId());
    	for(AdcCampaign campaign : campList) {
    		SolUtil.setCampaignStatusCard(campaign);
    	}
    	model.addAttribute("Camp01", (campList.size() > 0 ? Util.getObjectToJson(campList.get(0), false) : "null"));
    	model.addAttribute("Camp02", (campList.size() > 1 ? Util.getObjectToJson(campList.get(1), false) : "null"));
    	model.addAttribute("Camp03", (campList.size() > 2 ? Util.getObjectToJson(campList.get(2), false) : "null"));
    	
		// 쿠키에 있는 "현재" 광고 소재 정보 등을 확인하고, 최종적으로 session에 currCreatId, currCreatives 이름으로 정보를 설정한다.
		int currCreatId = SolUtil.saveCurrCreativesToSession(request, response, session, advertiser.getId(), creatId);
		AdcCreative creative = adcService.getCreative(currCreatId);

		// 광고 소재의 인벤 타겟팅 여부 설정
    	SolUtil.setCreativeInvenTargeted(creative);
    	SolUtil.setCreativeResolutions(creative);
		

    	modelMgr.addMainMenuModel(model, locale, session, request, "AdcCreative");
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

    	// 페이지 제목
    	model.addAttribute("pageTitle", "광고 소재");

    	model.addAttribute("Advertiser", advertiser);
    	model.addAttribute("Creative", creative);
    	model.addAttribute("CreatCount", adcService.getCreativeCountByAdvertiserId(advertiser.getId()));
    	model.addAttribute("CreatFileCount", adcService.getCreatFileCountByAdvertiserId(advertiser.getId()));

    	
    	// 공통 업로드 모달 이용을 위한 열기 URL 지정
    	model.addAttribute("uploadOpenUrl", "/adc/creative/files/upload");
    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "adc/creative/creat-file";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, HttpSession session) {
    	
    	try {
    		DataSourceResult result = adcService.getCreatFileList(request);
    		
    		for(Object obj : result.getData()) {
    			AdcCreatFile creatFile = (AdcCreatFile)obj;
    			
    			// 20% 범위로 적합도 판정
    			int fitness = adcService.measureResolutionWithMedium(
    					creatFile.getResolution(), creatFile.getMedium().getId(), 20);
    			
    			creatFile.setFitnessOfResRatio(fitness);
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
	
	
	/**
	 * 파일 업로드 페이지
	 */
    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String upload(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {

    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	String type = Util.parseString(request.getParameter("type"));
    	String code = Util.parseString(request.getParameter("code"));
    	int custId = Util.parseInt(request.getParameter("custId"));
    	
    	UploadTransitionModel uploadModel = new UploadTransitionModel();
    	
    	try {
        	if (medium != null && Util.isValid(type)) {
        		uploadModel.setMediumId(medium.getId());
        		uploadModel.setType(type);
        		uploadModel.setCode(code);
        		uploadModel.setCustId(custId);
        		uploadModel.setSaveUrl("/adc/creative/files/uploadsave");
        		
        		if (type.equals("MEDIA")) {
        			uploadModel.setAllowedExtensions("[\".jpg\", \".png\", \".mp4\"]");
        		}
        	}
    	} catch (Exception e) {
    		logger.error("upload", e);
    	}

    	model.addAttribute("uploadModel", uploadModel);
    	
        return "adn/modal/upload";
    }

    
    /**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model, HttpSession session) {
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	if (medium == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}
    	
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	//List<AdcCreatFile> creatFiles = new ArrayList<AdcCreatFile>();

    	try {
        	for (Object id : objs) {
        		AdcCreatFile creatFile = adcService.getCreatFile((Integer)id);
        		if (creatFile != null) {
        			
        			// 물리적인 삭제가 아니기 때문에 파일은 그대로 유지
        			/*
        			String mediaFolder = creatFile.getCtntFolder().getLocalPath() + "/" + creatFile.getCtntFolder().getName() + "/" + creatFile.getUuid().toString();
        			File thumbFile = new File(SolUtil.getPhysicalRoot("Thumb") + "/" + creatFile.getCtntFolder().getName() + "/" + 
        						creatFile.getUuid().toString() + ".png");
        			
        			FileUtils.deleteDirectory(new File(mediaFolder));
        			
    	    		if (thumbFile.exists() && thumbFile.isFile()) {
    	    			thumbFile.delete();
    	    		}
            		
            		creatFiles.add(creatFile);
            		*/
        			
    				// 소프트 삭제 진행
    				adcService.deleteSoftCreatFile(creatFile, session);
        		}
        	}
        	
        	//adcService.deleteCreatFiles(creatFiles);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }
    
    
    /**
     * 파일 업로드 저장 액션
     * @throws ServerOperationForbiddenException 
     */
    @RequestMapping(value = "/uploadsave", method = RequestMethod.POST)
    public @ResponseBody String save(@RequestParam List<MultipartFile> files,
    		@RequestParam int mediumId, @RequestParam String type, @RequestParam String code, @RequestParam int custId, 
    		HttpSession session) throws ServerOperationForbiddenException {

    	uploadInternal(files, mediumId, type, code, custId, session);
        
        // Return an empty string to signify success
        return "";
    }
    
    private synchronized void uploadInternal(List<MultipartFile> files, int mediumId, String type, String code, int custId, HttpSession session) {
    	
    	try {
			// ffmpeg
			FFmpeg ffmpeg = getFFMpeg();
			FFprobe ffprobe = getFFProbe();
			
			if (ffmpeg == null || ffprobe == null) {
	    		throw new ServerOperationForbiddenException("OperationError");
			}
			
    		//
    		// type: MEDIA
    		// code: CREATFILE
    		// custId: creative id
    		//
    		boolean success = false;
    		if (Util.isValid(type) && type.equals("MEDIA")) {
        		KnlMedium medium = knlService.getMedium(mediumId);
        		AdcCreative creative = adcService.getCreative(custId);
        		FndCtntFolder ctntFolder = fndService.getDefCtntFolder();

        		if (medium != null && creative != null && ctntFolder != null) {
        			//
        			// Step 1. 업로드 요청 시, 새 폴더(ABCD1234)를 만들고 그 폴더에 이번 업로드 대상의 파일을 저장
        			// Step 2.
        			//   2-1. 파일 probe를 이용한 해상도 및 재생 시간 확인
        			//   2-2. 대상 해상도 자료가 기존재할 경우 중지
        			//   2-3. 대상 해상도 자료가 없을 경우, 새로운 uuid 폴더 생성하고, 파일 이동
        			//   2-4. 모든 파일에 대해 진행하고, 그 결과를 통보
        	    	
        			// 매 파일마다 별도 폴더 생성: 동일 파일명에 의한 간섭 회피
        			//
        			// folder name format: AAAA9999
        			char[] alphaSeed = "ABCDEFGHJKLMNPQRSTUVWXYZ".toCharArray();
        			char[] numberSeed = "1234567890".toCharArray();
        			
        			String typeRootDir = SolUtil.getPhysicalRoot("UpCtntTemp") + "/" + 
        					Util.random(4, alphaSeed) + Util.random(4, numberSeed);
        			
        			boolean result = Util.checkDirectory(typeRootDir);
        			if (result) {
            	        for (MultipartFile file : files) {
            	        	
            	        	File upFile = null;
            	        	if (!file.isEmpty()) {
            	        		upFile = new File(typeRootDir + "/" + file.getOriginalFilename());
            	        		FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(upFile));
            	        	}
            	        	
            	        	if (upFile != null && upFile.exists() && upFile.isFile()) {
        						
        		    			// 파일 유효성 검사: 1) 해상도, 2) 썸네일
        						String mediaType = "";
        						String mimeType = "";
        						String ext = Util.getFileExt(upFile.getName()).toLowerCase();
        						if (Util.isValid(ext)) {
        							if (ext.equals("mp4")) {
        								mediaType = "V";
        								mimeType = "video/mp4";
        							} else if (ext.equals("jpg")) {
        								mediaType = "I";
        								mimeType = "image/jpeg";
        							} else if (ext.equals("png")) {
        								mediaType = "I";
        								mimeType = "image/x-png";
        							} 
        						}

        						ProbeResult probeResult = getProbeResult(getFFProbe(), upFile.getAbsolutePath(), mediaType);

        						// 해상도 획득 실패면, 썸네일 추출 이유 없음
        						if (probeResult == null) {
        							throw new ServerOperationForbiddenException(StringInfo.UPLOAD_ERROR);
        						} else {
        							String thumbFilename0 = typeRootDir + "/0.png";
        							String thumbFilename = typeRootDir + "/th.png";

        							File thumbFile0 = new File(thumbFilename0);
        					        File thumbFile = new File(thumbFilename);
        					        
        							if (probeResult.isOriginalSized()) {
        								Files.copy(upFile,  thumbFile0);
        							} else {
        								FFmpegBuilder builder = null;
        								if (mediaType.equals("I")) {
        							        builder = new FFmpegBuilder()
        							                .overrideOutputFiles(true) // 오버라이드 여부
        							                .setInput(upFile.getAbsolutePath()) // 썸네일 생성대상 파일
        							                .addOutput(thumbFilename0) // 썸네일 파일의 Path
        							                .setVideoWidth(probeResult.getThumbWidth())
        							                .setVideoHeight(probeResult.getThumbHeight())
        							                .setFrames(1)
        							                .done();
        								} else if (mediaType.equals("V")) {
        							        builder = new FFmpegBuilder()
        							                .overrideOutputFiles(true) // 오버라이드 여부
        							                .setInput(upFile.getAbsolutePath()) // 썸네일 생성대상 파일
        							                .addExtraArgs("-ss", "00:00:01") // 썸네일 추출 시작점
        							                .addOutput(thumbFilename0) // 썸네일 파일의 Path
        							                .setVideoWidth(probeResult.getThumbWidth())
        							                .setVideoHeight(probeResult.getThumbHeight())
        							                .setFrames(1)
        							                .done();
        								}
        								
        								try {
        							        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        							        executor.createJob(builder).run();
        								} catch (Exception e1) {
        									// 썸네일 제작 중 오류 여부는 파일 생성 여부로 판단함
        								}
        							}
        							
        							if (thumbFile0.exists()) {
        								if (probeResult.getWidth() == ProbeResult.THUMB_LENGTH && probeResult.getHeight() == ProbeResult.THUMB_LENGTH) {
        									Files.copy(thumbFile0,  new File(thumbFilename));
        								} else {
        									// 파일의 크기가 규정 크기가 아니기 때문에 투명 바탕의 규정 크기로 변경이 필요
        									BufferedImage bufferedImage = new BufferedImage(ProbeResult.THUMB_LENGTH, ProbeResult.THUMB_LENGTH,
        							                BufferedImage.TYPE_INT_ARGB);
        							        Graphics2D g2d = bufferedImage.createGraphics();
        							        g2d.setPaint(new Color(0, 0, 0, 0));
        							        g2d.fillRect(0, 0, ProbeResult.THUMB_LENGTH, ProbeResult.THUMB_LENGTH);

        							        int tmpX = ProbeResult.THUMB_LENGTH / 2 - probeResult.getThumbWidth() / 2;
        							        int tmpY = ProbeResult.THUMB_LENGTH / 2 - probeResult.getThumbHeight() / 2;
        							        
        							        g2d.drawImage(javax.imageio.ImageIO.read(thumbFile0), tmpX, tmpY, probeResult.getThumbWidth(), probeResult.getThumbHeight(), null);
        							        
        							        g2d.dispose();
        							        javax.imageio.ImageIO.write(bufferedImage, "png", thumbFile);
        							    }
        							}
        					        
        							boolean readyReg = true;
        					        if (thumbFile.exists()) {
        					        	// 임시로 생성된 중간 단계의 썸네일 파일 삭제
        					        	thumbFile0.delete();
        					        	
        					        	
        					        	List<AdcCreatFile> creatFiles = adcService.getCreatFileListByCreativeId(custId);
        					        	for(AdcCreatFile creatFile : creatFiles) {
        					        		if (creatFile.getResolution().equals(probeResult.getResolution())) {
        					        			readyReg = false;
        					        			break;
        					        		}
        					        	}
        					        } else {
        					        	throw new ServerOperationForbiddenException(StringInfo.UPLOAD_ERROR);
        					        }
        					        
        					        if (readyReg) {
        					        	UUID uuid = UUID.randomUUID();
        					        	
        					        	
        					        	// 이제 Creative File로 등록 시작
        					        	AdcCreatFile cFile = new AdcCreatFile(creative, ctntFolder, file.getOriginalFilename(),
        					        			upFile.length(), mediaType, probeResult.getResolution(), mimeType, uuid, UUID.randomUUID(),
        					        			probeResult.getDuration(), session);
        					        	adcService.saveOrUpdate(cFile);
        					        	
        					        	
        					        	String thumbF = uuid.toString() + ".png";
        					        	String mediaF = cFile.getMediaType() + String.format("%05d", cFile.getId()) + "." + ext;
        					        	
        					        	cFile.setThumbFilename(thumbF);
        					        	cFile.setFilename(mediaF);
        					        	
        								String finalMediaFilename = ctntFolder.getLocalPath() + "/" + ctntFolder.getName() + "/" + uuid.toString() + "/" + mediaF;
        								String finalThumbFilename = SolUtil.getPhysicalRoot("Thumb") + "/" + ctntFolder.getName() + "/" + thumbF;
        								
        								Util.checkParentDirectory(finalMediaFilename);
        								Util.checkParentDirectory(finalThumbFilename);
        								
        								
        								// 컨텐츠 파일의 이동
        								upFile.renameTo(new File(finalMediaFilename));
        								thumbFile.renameTo(new File(finalThumbFilename));
        								
        								adcService.saveOrUpdate(cFile);
        								
        								FileUtils.deleteDirectory(new File(typeRootDir));
        					        } else {
        					        	// 이 단계까지 왔을 때
        					        	//   미디어 파일 및 썸네일 생성되어 있으나, 기존 해상도와 겹치는 상황
        					        	
        					        	FileUtils.deleteDirectory(new File(typeRootDir));

        					        	throw new ServerOperationForbiddenException(StringInfo.UPLOAD_ERROR_RESOLUTION);
        					        }
        						}
            	        	} else {
                				throw new ServerOperationForbiddenException(StringInfo.UPLOAD_ERROR);
            	        	}
            	        }
            	        
            	        success = true;
            	        
            	        creative.setStatus("D");
            	        creative.setSubmitDate(null);
            	        
            	        adcService.saveOrUpdate(creative);
        			} else {
        				throw new ServerOperationForbiddenException(StringInfo.UPLOAD_ERROR_FOLDER);
        			}
        		}
    		}
    		
    		if (!success) {
    			throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    		}
    	} catch (Exception e) {
    		logger.error("uploadsave", e);
    		throw new ServerOperationForbiddenException(e.getMessage());
    	}
    }
    
    private FFprobe getFFProbe() {
    	
		FFprobe ffprobe = null;
		
		String fileFFprobe = Util.getFileProperty("file.ffprobe");
		
		try {
			if (Util.isValid(fileFFprobe)) {
				ffprobe = new FFprobe(fileFFprobe);
				if (!ffprobe.isFFprobe()) {
					ffprobe = null;
				}
			}
		} catch (Exception e) {
			// 초기화 확인 중의 오류는 그냥 skip
		}
    	
		return ffprobe;
    }
    
    private FFmpeg getFFMpeg() {
    	
    	FFmpeg ffmpeg = null;
		
		String fileFFmpeg = Util.getFileProperty("file.ffmpeg");
		
		try {
			if (Util.isValid(fileFFmpeg)) {
				ffmpeg = new FFmpeg(fileFFmpeg);
				if (!ffmpeg.isFFmpeg()) {
					ffmpeg = null;
				}
			}
		} catch (Exception e) {
			// 초기화 확인 중의 오류는 그냥 skip
		}
    	
		return ffmpeg;
    }
    
    private ProbeResult getProbeResult(FFprobe ffprobe, String dstPathFile, String mediaType) {
    	
    	int width = 0, height = 0;
    	double duration = 0;
    	
		if (ffprobe != null) {
			try {
				FFmpegProbeResult probeResult = ffprobe.probe(dstPathFile);
				
				List<FFmpegStream> streams = probeResult.getStreams();
				if (streams.size() > 0) {
					if (mediaType.equals("I")) {
						FFmpegStream stream = streams.get(0);
						if (stream.codec_type == CodecType.VIDEO) {
							width = stream.width;
							height = stream.height;
						}
					} else if (mediaType.equals("V")) {
						for(FFmpegStream stream : streams) {
							if (stream.codec_type == CodecType.VIDEO) {
								width = stream.width;
								height = stream.height;
								duration = stream.duration;
								break;
							}
						}
					}
				}
			} catch (Exception e) {
				// probe 과정 중의 오류는 파일 오류로 보고 그냥 skip
			}
		}
		
		if (width == 0 || height == 0) {
			return null;
		}

		return new ProbeResult(width, height, duration);
    }
    
    private class ProbeResult {
    	private int width;
    	private int height;
    	
    	private int thumbWidth;
    	private int thumbHeight;
    	
    	private double duration;
    	
    	public static final int THUMB_LENGTH = 128;
    	private boolean originalSized = false;
    	
    	public ProbeResult(int width, int height, double duration) {
    		this.width = width;
    		this.height = height;
    		this.duration = duration;
    		
    		if (width <= THUMB_LENGTH && height <= THUMB_LENGTH) {
    			thumbWidth = width;
    			thumbHeight = height;
    			originalSized = true;
    		} else {
        		if (width > height) {
        			thumbWidth = THUMB_LENGTH;
        			thumbHeight = height * THUMB_LENGTH / width;
        		} else if (width < height) {
        			thumbWidth = width * THUMB_LENGTH / height;
        			thumbHeight = THUMB_LENGTH;
        		} else {
        			thumbWidth = THUMB_LENGTH;
        			thumbHeight = THUMB_LENGTH;
        		}
    		}
    	}
    	
		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}
		
		public int getThumbWidth() {
			return thumbWidth;
		}

		public int getThumbHeight() {
			return thumbHeight;
		}

		public double getDuration() {
			return duration;
		}

		public boolean isOriginalSized() {
			return originalSized;
		}

		public String getResolution() {
			return String.valueOf(width) + "x" + String.valueOf(height);
		}
    }

}
