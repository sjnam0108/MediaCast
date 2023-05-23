package net.doohad.controllers.org;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.doohad.exceptions.ServerOperationForbiddenException;
import net.doohad.models.AdnMessageManager;
import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.fnd.FndRegion;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.inv.InvSite;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgSiteCond;
import net.doohad.models.rev.RevInvenRequest;
import net.doohad.models.service.FndService;
import net.doohad.models.service.InvService;
import net.doohad.models.service.KnlService;
import net.doohad.models.service.OrgService;
import net.doohad.models.service.RevService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.DropDownListItem;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * 인벤 API 요청 컨트롤러
 */
@Controller("org-inven-request-controller")
@RequestMapping(value="/org/invenrequest")
public class OrgInvenRequestController {

	private static final Logger logger = LoggerFactory.getLogger(OrgInvenRequestController.class);


    @Autowired 
    private OrgService orgService;

    @Autowired 
    private RevService revService;

    @Autowired 
    private InvService invService;

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
	 * 인벤 API 요청 페이지
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
    	model.addAttribute("pageTitle", "인벤 API 요청");
    	
    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "org/invenrequest";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readScr(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		return revService.getInvenRequestList(request);
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 결과 코드 정보
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
	 * 읽기 액션 - 유형 정보
	 */
    @RequestMapping(value = "/readTypes", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readTypes(Locale locale) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-asterisk text-success fa-fw", "추가/변경", "U"));
		list.add(new DropDownListItem("fa-regular fa-trash-can text-danger fa-fw", "삭제", "D"));
		
		return list;
    }

    
    /**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<RevInvenRequest> invenRequests = new ArrayList<RevInvenRequest>();

    	for (Object id : objs) {
    		RevInvenRequest invenRequest = new RevInvenRequest();
    		
    		invenRequest.setId((int)id);
    		
    		invenRequests.add(invenRequest);
    	}

    	try {
    		revService.deleteInvenRequests(invenRequests);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }

    
    /**
	 * 실행 액션
	 */
    @RequestMapping(value = "/execute", method = RequestMethod.POST)
    public @ResponseBody String execute(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");

    	ArrayList<Integer> ids = new ArrayList<Integer>();
    	for (Object id : objs) {
    		ids.add((int)id);
    	}
		
		Collections.sort(ids, new Comparator<Integer>() {
	    	public int compare(Integer item1, Integer item2) {
	    		return Integer.compare(item1.intValue(), item2.intValue());
	    	}
	    });
    	
		try {
			KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
			if (medium != null) {

				JSONObject mObj = getJSONObject(SolUtil.getOptValue(medium.getId(), "inven.default"));

				String mLat = getPropStringValue(mObj, "lat");
				String mLng = getPropStringValue(mObj, "lng");
				String mRegion = getPropStringValue(mObj, "region");
				String mAddr = getPropStringValue(mObj, "addr");
				String mSiteCond = getPropStringValue(mObj, "siteCond");
				String mVenueType = getPropStringValue(mObj, "venueType");

				String mReso = getPropStringValue(mObj, "reso");
				Boolean mVideo = getPropBooleanValue(mObj, "video");
				Boolean mImage = getPropBooleanValue(mObj, "image");
				Boolean mActive = getPropBooleanValue(mObj, "active");
				Boolean mAd = getPropBooleanValue(mObj, "ad");

				List<String> resos = Util.tokenizeValidStr(medium.getResolutions());
				
				for(Integer i : ids) {
					
					RevInvenRequest invenRequest = revService.getInvenRequest(i);
					if (invenRequest != null) {
						
						JSONObject jsonObj = JSONObject.fromObject(JSONSerializer.toJSON(invenRequest.getRequest()));
						if (jsonObj != null) {
							
		        			String siteID = getPropStringValue(jsonObj, "siteID");
		        			String siteName = getPropStringValue(jsonObj, "siteName");
		        			String lat = getPropStringValue(jsonObj, "lat");
		        			String lng = getPropStringValue(jsonObj, "lng");
		        			String region = getPropStringValue(jsonObj, "region");
		        			String addr = getPropStringValue(jsonObj, "addr");
		        			String siteCond = getPropStringValue(jsonObj, "siteCond");
		        			String venueType = getPropStringValue(jsonObj, "venueType");
		        			
		        			String screenID = getPropStringValue(jsonObj, "screenID");
		        			String screenName = getPropStringValue(jsonObj, "screenName");
		        			String reso = getPropStringValue(jsonObj, "reso");
		        			Boolean video = getPropBooleanValue(jsonObj, "video");
		        			Boolean image = getPropBooleanValue(jsonObj, "image");
		        			Boolean active = getPropBooleanValue(jsonObj, "active");
		        			Boolean ad = getPropBooleanValue(jsonObj, "ad");

		        			String resultCode = "";
		        			
		        			
		        			InvScreen screen = null;
		        			InvSite site = null;
		        			
		        			FndRegion regionObj = null;
		        			OrgSiteCond siteCondObj = null;
		        			
		        			// type == U or D
		        			if (invenRequest.getType().equals("U")) {
		        				
		        				// upsert 모드
	        					boolean screenInsertMode = false;
	        					boolean siteInsertMode = false;
	        					screen = invService.getScreen(medium, screenID);
	        					if (screen != null) {
	        						site = screen.getSite();
	        						if (site.getShortName().equals(siteID)) {
	        							if (Util.isNotValid(screenName)) {
	        								resultCode = "화면명 오류";
	        							} else if (Util.isNotValid(siteName)) {
	        								resultCode = "사이트명 오류";
	        							}
	        						} else {
	        							resultCode = "사이트ID 오류";
	        						}
	        					} else {
	        						screenInsertMode = true;

	        						// screen은 확실히 insert 인데, site는?
	        						site = invService.getSite(medium, siteID);
	        						siteInsertMode = (site == null);
	        					}
		        				
	        					if (Util.isNotValid(resultCode)) {
	        						
	        						if (siteInsertMode) {
	        							// 사이트 추가 모드에서의 필수 항목:
	        							// 		siteID, siteName, lat, lng, region, addr, siteCond, venueType
	        							if (Util.isNotValid(lat)) { lat = mLat; }
	        							if (Util.isNotValid(lng)) { lng = mLng; }
	        							if (Util.isNotValid(region)) { region = mRegion; }
	        							if (Util.isNotValid(addr)) { addr = mAddr; }
	        							if (Util.isNotValid(siteCond)) { siteCond = mSiteCond; }
	        							if (Util.isNotValid(venueType)) { venueType = mVenueType; }
	        							
	        							if (Util.isNotValid(siteID) || Util.isNotValid(siteName) || Util.isNotValid(lat) ||
	        									Util.isNotValid(lng) || Util.isNotValid(region) || Util.isNotValid(addr) ||
	        									Util.isNotValid(siteCond) || Util.isNotValid(venueType)) {
	        								resultCode = "사이트 필수 누락";
	        							}
	        						}
	        						
	        						if (Util.isNotValid(resultCode) && screenInsertMode) {
	        							// 화면 추가 모드에서의 필수 항목:
	        							// 		screenID, screenName, reso, video, image, active, ad
	        							if (Util.isNotValid(reso)) { reso = mReso; }
	        							if (video == null) { video = mVideo; }
	        							if (image == null) { image = mImage; }
	        							if (active == null) { active = mActive; }
	        							if (ad == null) { ad = mAd; }
	        							
	        							if (Util.isNotValid(reso) || video == null || image == null || active == null || ad == null) {
	        								resultCode = "화면 필수 누락";
	        							}
	        						}
	        					}
    							
    							if (Util.isNotValid(resultCode) && Util.isValid(lat)) {
    								// 위도: N 33˚06′40″ ~ N 43˚00′39″
    								double latVal = Util.parseDouble(lat);
    								if (latVal < 33d || latVal > 43d) {
    									resultCode = "위도lat 오류";
    								}
    							}
    							
    							if (Util.isNotValid(resultCode) && Util.isValid(lng)) {
    								//경도: E 124˚11′00″ ~ E 131˚52′42″
    								double lngVal = Util.parseDouble(lng);
    								if (lngVal < 124d || lngVal > 131d) {
    									resultCode = "경도lng 오류";
    								}
    							}
	    						
    							if (Util.isNotValid(resultCode) && Util.isValid(region)) {
    								regionObj = fndService.getRegionByName(region);
    								if (regionObj == null) {
    									resultCode = "지역 오류";
    								}
    							}
    							
    							if (Util.isNotValid(resultCode) && Util.isValid(siteCond)) {
    								siteCondObj = orgService.getSiteCond(medium, siteCond);
    								if (siteCondObj == null) {
    									resultCode = "입지 유형 오류";
    								}
    							}
    							
    							if (Util.isNotValid(resultCode) && Util.isValid(venueType)) {
    								if (!venueType.equals("CVS") && !venueType.equals("BUSSH") && !venueType.equals("BLDG") && !venueType.equals("HOSP")) {
    									resultCode = "장소 유형 오류";
    								}
    							}
    							
    							if (Util.isNotValid(resultCode) && Util.isValid(reso)) {
    								if (!resos.contains(reso)) {
    									resultCode = "해상도 오류";
    								}
    							}
    							
    							
    							if (Util.isValid(resultCode)) {
    								invenRequest.setResultCode(resultCode);
    								invenRequest.setResult("F");
    								invenRequest.touchWho(session);
    								
    								revService.saveOrUpdate(invenRequest);
    								
    								continue;
    							}
    							
    							
    							try {
        							if (siteInsertMode) {
        								
        								// site insert
        								site = new InvSite(medium, siteID, siteName, lat, lng, regionObj.getCode(), addr, new Date(), null, "", session);
        								site.setRegionName(regionObj.getName());
        								site.setSiteCond(siteCondObj);
        								site.setVenueType(venueType);
        								
        								invService.saveOrUpdate(site);
        							} else {
        								
        								// site update
        								if (Util.isValid(siteName)) { site.setName(siteName); }
        								if (Util.isValid(lat)) { site.setLatitude(lat); }
        								if (Util.isValid(lng)) { site.setLongitude(lng); }
        								if (regionObj != null) { 
        									site.setRegionCode(regionObj.getCode());
        									site.setRegionName(regionObj.getName());
        								}
        								if (Util.isValid(addr)) { site.setAddress(addr); }
        								if (siteCondObj != null) { site.setSiteCond(siteCondObj); }
        								if (Util.isValid(venueType)) { site.setVenueType(venueType); }
        								
        								site.touchWho(session);
        								invService.saveOrUpdate(site);
        							}
    							} catch (Exception e) {
    								logger.error("execute - upsert site", e);
    								
    								invenRequest.setResultCode("로그 확인 필요");
    								invenRequest.setResult("F");
    								invenRequest.touchWho(session);
    								
    								revService.saveOrUpdate(invenRequest);
    								
    								continue;
    							}

    							try {
        							if (screenInsertMode) {
        								
        								// screen insert
        								screen = new InvScreen(site, screenID, screenName, active, reso, image, video, new Date(), null, "", session);
        								screen.setAdServerAvailable(ad.booleanValue());
        								screen.setApiSyncDate(new Date());
        								
        								invService.saveOrUpdate(screen);
        							} else {
        								
        								// screen update
        								if (Util.isValid(screenName)) { screen.setName(screenName); }
        								if (Util.isValid(reso)) { screen.setResolution(reso); }
        								if (video != null) { screen.setVideoAllowed(video.booleanValue()); }
        								if (image != null) { screen.setImageAllowed(image.booleanValue()); }
        								if (active != null) { screen.setActiveStatus(active.booleanValue()); }
        								if (ad != null) { screen.setAdServerAvailable(ad.booleanValue()); }
        								
        								screen.setApiSyncDate(new Date());
        								screen.touchWho(session);
        								invService.saveOrUpdate(screen);
        							}
    							} catch (Exception e) {
    								logger.error("execute - upsert screen", e);
    								
    								invenRequest.setResultCode("로그 확인 필요");
    								invenRequest.setResult("F");
    								invenRequest.touchWho(session);
    								
    								revService.saveOrUpdate(invenRequest);
    								
    								continue;
    							}
						        
    							invenRequest.setResultCode("");
								invenRequest.setResult("S");
								invenRequest.touchWho(session);
								
								revService.saveOrUpdate(invenRequest);

								
						        // 화면 상태 및 수량 기준으로 사이트 정보 변경
						        invService.updateSiteActiveStatusCountBasedScreens(site.getId());
						        
		        			} else if (invenRequest.getType().equals("D")) {
		        				
		        				// delete 모드
		        				if (Util.isValid(screenID)) {
		        					
		        					screen = invService.getScreen(medium, screenID);
		        					if (screen != null) {
		        						site = screen.getSite();
		        						if (!site.getShortName().equals(siteID)) {
		        							
		    								invenRequest.setResultCode("사이트ID 오류");
		    								invenRequest.setResult("F");
		    								invenRequest.touchWho(session);
		    								
		    								revService.saveOrUpdate(invenRequest);
		    								
		    								continue;
		        						}
		        					}
		        				}
		        				
		        				if (screen == null && Util.isValid(siteID)) {
		        					
		        					site = invService.getSite(medium, siteID);
		        					if (site == null) {
		        						
		    							invenRequest.setResultCode("대상 화면/사이트 미존재");
										invenRequest.setResult("P");
										invenRequest.touchWho(session);
										
										revService.saveOrUpdate(invenRequest);
										
										continue;
		        						
		        					}
		        				}
		        				
		        				
		        				// 화면부터 삭제
		        				if (screen != null) {
		        					try {
		        						int siteId = screen.getSite().getId();
		        						
			        	    			if (screen.getLastAdRequestDate() == null) {
			        	    				invService.deleteScreen(screen);
			        	    			} else {
			        	    				// 소프트 삭제 진행
			        	    				invService.deleteSoftScreen(screen, session);
			        	    			}
			        	    			
		        	    				invService.updateSiteActiveStatusCountBasedScreens(siteId);
		        					} catch (Exception e) {
	    								logger.error("execute - delete screen", e);
	    								
	    								invenRequest.setResultCode("로그 확인 필요");
	    								invenRequest.setResult("F");
	    								invenRequest.touchWho(session);
	    								
	    								revService.saveOrUpdate(invenRequest);
	    								
	    								continue;
		        					}
		        				}
		        				
		        				if (site != null) {
		        					try {
		        						List<InvScreen> screenList = invService.getScreenListBySiteId(site.getId());
		        						if (screenList.size() == 0) {
		        							
			        						if (site.isServed()) {
			        							// 소프트 삭제 진행
			        							invService.deleteSoftSite(site, session);
			        						} else {
			        							invService.deleteSite(site);
			        						}
		        						}
		        					} catch (Exception e) {
	    								logger.error("execute - delete site", e);
	    								
	    								invenRequest.setResultCode("로그 확인 필요");
	    								invenRequest.setResult("F");
	    								invenRequest.touchWho(session);
	    								
	    								revService.saveOrUpdate(invenRequest);
	    								
	    								continue;
		        					}
		        				}
						        
    							invenRequest.setResultCode("");
								invenRequest.setResult("S");
								invenRequest.touchWho(session);
								
								revService.saveOrUpdate(invenRequest);
		        			}
						}
					}
				}
				
			}
			
		} catch (Exception e) {
    		logger.error("execute", e);
    		throw new ServerOperationForbiddenException("OperationError");
		}

		
        return "Ok";
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

}
