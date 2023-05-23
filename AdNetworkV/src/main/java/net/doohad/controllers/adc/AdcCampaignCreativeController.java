package net.doohad.controllers.adc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Tuple;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.doohad.exceptions.ServerOperationForbiddenException;
import net.doohad.info.StringInfo;
import net.doohad.models.AdnMessageManager;
import net.doohad.models.CustomComparator;
import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.adc.AdcAd;
import net.doohad.models.adc.AdcAdCreative;
import net.doohad.models.adc.AdcCampaign;
import net.doohad.models.adc.AdcCreatFile;
import net.doohad.models.adc.AdcCreative;
import net.doohad.models.service.AdcService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.DropDownListItem;

/**
 * 캠페인 컨트롤러(광고 소재)
 */
@Controller("adc-campaign-creative-controller")
@RequestMapping(value="/adc/campaign/creatives")
public class AdcCampaignCreativeController {

	private static final Logger logger = LoggerFactory.getLogger(AdcCampaignCreativeController.class);

	
    @Autowired 
    private AdcService adcService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;

    
	/**
	 * 캠페인 컨트롤러(광고 소재)
	 */
    @RequestMapping(value = {"/{campId}", "/{campId}/", "/{campId}/{adId}", "/{campId}/{adId}/"}, method = RequestMethod.GET)
    public String index1(HttpServletRequest request, HttpServletResponse response, HttpSession session,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap,
    		Model model, Locale locale) {

    	AdcCampaign campaign = adcService.getCampaign(Util.parseInt(pathMap.get("campId")));
    	if (campaign == null || campaign.getMedium().getId() != Util.getSessionMediumId(session)) {
    		return "forward:/adc/campaign";
    	}

    	// "현재" 광고 선택 변경의 경우
    	int adId = Util.parseInt(pathMap.get("adId"));
    	if (adId > 0) {
    		AdcAd ad = adcService.getAd(adId);
    		if (ad == null || ad.getCampaign().getId() != campaign.getId()) {
    			adId = -1;
    		}
    	}

		
		// 캠페인의 상태카드 설정
    	SolUtil.setCampaignStatusCard(campaign);
		
		// 쿠키에 있는 "현재" 광고 정보 등을 확인하고, 최종적으로 session에 currAdId, currAds 이름으로 정보를 설정한다.
		int currAdId = SolUtil.saveCurrAdsToSession(request, response, session, campaign.getId(), adId);
    	AdcAd ad = adcService.getAd(currAdId);		// ad가 null일 수도 있음

		// 광고의 인벤 타겟팅 여부 및 상태카드 설정
    	SolUtil.setAdInvenTargeted(ad);
		SolUtil.setAdStatusCard(ad);
		SolUtil.setAdResolutions(ad);

    	
    	modelMgr.addMainMenuModel(model, locale, session, request, "AdcAd");
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

    	// 페이지 제목
    	model.addAttribute("pageTitle", "광고");

    	model.addAttribute("Campaign", campaign);
    	model.addAttribute("Ad", ad);
    	
		model.addAttribute("Creatives", getCreativeListByAd(ad));
    	
    	
        return "adc/campaign/camp-creative";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, HttpSession session) {
    	
    	try {
    		DataSourceResult result = adcService.getAdCreativeList(request);
    		
    		// 하나라도 타겟팅이 존재하는 것만 기록
    		ArrayList<Integer> targetIds = new ArrayList<Integer>();
    		List<Tuple> countList = adcService.getCreatTargetCountGroupByMediumCreativeId(Util.getSessionMediumId(session));
    		for(Tuple tuple : countList) {
    			targetIds.add((Integer) tuple.get(0));
    		}
    		
    		for(Object obj : result.getData()) {
    			AdcAdCreative adCreate = (AdcAdCreative)obj;
    			List<AdcCreatFile> fileList = adcService.getCreatFileListByCreativeId(adCreate.getCreative().getId());
    			
    			String resolutions = "";
    			for(AdcCreatFile creatFile : fileList) {
        			
        			// 20% 범위로 적합도 판정
        			int fitness = adcService.measureResolutionWithMedium(
        					creatFile.getResolution(), creatFile.getMedium().getId(), 20);
    				
    				if (Util.isValid(resolutions)) {
    					resolutions += "|" + String.valueOf(fitness) + ":" + creatFile.getResolution();
    				} else {
    					resolutions = String.valueOf(fitness) + ":" + creatFile.getResolution();
    				}
    			}
    			
    			adCreate.getCreative().setFileResolutions(resolutions);
    			
    			if (targetIds.contains(adCreate.getCreative().getId())) {
    				adCreate.getCreative().setInvenTargeted(true);
    			}
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 광고 소재 목록 획득
	 */
    private List<DropDownListItem> getCreativeListByAd(AdcAd ad) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		if (ad != null) {
			List<AdcCreative> creativeList = adcService.getCreativeListByAdvertiserId(ad.getCampaign().getAdvertiser().getId());
			for (AdcCreative creative : creativeList) {
				// 대체 광고는 제외
				if (creative.getType().equals("F") || creative.getStatus().equals("V")) {
					continue;
				}
				
				list.add(new DropDownListItem(creative.getName(), String.valueOf(creative.getId())));
			}
			
			Collections.sort(list, CustomComparator.DropDownListItemTextComparator);
		}

		if (list.size() == 0) {
			list.add(0, new DropDownListItem("", "-1"));
		}
		
		return list;
    }
    
    
	/**
	 * 소재와 연결 액션 - Ad & Creative
	 */
    @RequestMapping(value = "/link", method = RequestMethod.POST)
    public @ResponseBody String link(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	int weight = (int)model.get("weight");
    	
    	Date startDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("startDate")));
    	Date endDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("endDate")));
    	
    	AdcCreative creative = adcService.getCreative((int)model.get("creative"));
    	AdcAd ad = adcService.getAd((int)model.get("ad"));
    	
    	// 파라미터 검증
    	if (creative == null || ad == null || startDate == null || endDate == null || weight < 1) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	
    	AdcAdCreative target = new AdcAdCreative(ad, creative, weight, startDate, endDate, session);
    	
        saveOrUpdate(target, locale, session);

        return "Ok";
    }

    
    /**
	 * 연결 해제 액션
	 */
    @RequestMapping(value = "/unlink", method = RequestMethod.POST)
    public @ResponseBody String unlink(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<AdcAdCreative> adCreatives = new ArrayList<AdcAdCreative>();

    	for (Object id : objs) {
    		AdcAdCreative adCreative = new AdcAdCreative();
    		
    		adCreative.setId((int)id);
    		
    		adCreatives.add(adCreative);
    	}
    	
    	try {
        	adcService.deleteAdCreatives(adCreatives);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("destroy", dive);
        	throw new ServerOperationForbiddenException(StringInfo.DEL_ERROR_CHILD_AD_SELECT);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("OperationError");
    	}

        return "Ok";
    }
    
    
	/**
	 * 소재와 연결 변경 액션 - Ad & Creative
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	int weight = (int)model.get("weight");
    	
    	Date startDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("startDate")));
    	Date endDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("endDate")));
    	
    	AdcCreative creative = adcService.getCreative((int)model.get("creative"));
    	AdcAd ad = adcService.getAd((int)model.get("ad"));
    	
    	// 파라미터 검증
    	if (creative == null || ad == null || startDate == null || endDate == null || weight < 1) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	
    	AdcAdCreative target = adcService.getAdCreative((int)model.get("id"));
    	if (target != null) {
    		target.setStartDate(startDate);
    		target.setEndDate(endDate);
    		target.setWeight(weight);
    		
    		target.touchWho(session);
    	}
    	
        saveOrUpdate(target, locale, session);

        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(AdcAdCreative target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {

        // DB 작업 수행 결과 검증
        try {
            adcService.saveOrUpdate(target);
        } catch (Exception e) {
    		logger.error("saveOrUpdate", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }
    }
}
