package net.doohad.controllers.rev;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.Tuple;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.doohad.models.AdnMessageManager;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.service.InvService;
import net.doohad.models.service.KnlService;
import net.doohad.models.service.RevService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.inv.InvSiteMapLocItem;

/**
 * 지오펜스 컨트롤러
 */
@Controller("rev-geofence-controller")
@RequestMapping(value="/rev/geofence")
public class RevGeofenceController {

	private static final Logger logger = LoggerFactory.getLogger(RevGeofenceController.class);


    @Autowired 
    private RevService revService;

    @Autowired 
    private InvService invService;

    @Autowired 
    private KnlService knlService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 송출일지 페이지
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
    	model.addAttribute("pageTitle", "지오펜스");
    	
    	
    	// 임시 코드
    	/*
    	KnlMedium mediumCU = knlService.getMedium("CU");
    	KnlMedium mediumHello = knlService.getMedium("hello");
    	
    	List<Integer> resultCuIds = invService.getMonitScreenIdsByMediumId(mediumCU.getId());
    	List<Integer> resultHelloIds = invService.getMonitScreenIdsByMediumId(mediumHello.getId());
    	
    	ArrayList<InvSiteMapLocItem> mapItemCuList = new ArrayList<InvSiteMapLocItem>();
    	ArrayList<InvSiteMapLocItem> mapItemHelloList = new ArrayList<InvSiteMapLocItem>();
    	
    	
    	
    	
    	List<Tuple> locList = invService.getSiteLocListBySiteIdIn(invService.getMonitSiteIdsByMediumScreenIdIn(mediumCU.getId(), resultCuIds));
    	for(Tuple tuple : locList) {
    		double lat = (double)Util.parseFloat((String) tuple.get(1));
    		double lng = (double)Util.parseFloat((String) tuple.get(2));
    		
    		mapItemCuList.add(new InvSiteMapLocItem((String) tuple.get(0),
    				lat, lng));
    	}
    	
    	model.addAttribute("cuMarkerList", mapItemCuList);
    	model.addAttribute("cuMarkerUrl", SolUtil.getMarkerUrl(mediumCU.getShortName()));
    	
    	
    	locList = invService.getSiteLocListBySiteIdIn(invService.getMonitSiteIdsByMediumScreenIdIn(mediumHello.getId(), resultHelloIds));
    	for(Tuple tuple : locList) {
    		double lat = (double)Util.parseFloat((String) tuple.get(1));
    		double lng = (double)Util.parseFloat((String) tuple.get(2));
    		
    		mapItemHelloList.add(new InvSiteMapLocItem((String) tuple.get(0),
    				lat, lng));
    	}
    	
    	model.addAttribute("helloMarkerList", mapItemHelloList);
    	model.addAttribute("helloMarkerUrl", SolUtil.getMarkerUrl(mediumHello.getShortName()));
    	//-
   	    */

    	// 임시 코드 2
    	model.addAttribute("data",SolUtil.getMarkerUrl(Util.getSessionMediumId(session)));
    	model.addAttribute("regionList", getRegionLocItems());
    	model.addAttribute("stateList", getStateLocItems());
    	model.addAttribute("CmarkerList", getLocItemByVenueType("cvs"));
    	model.addAttribute("HmarkerList", getLocItemByVenueType("hosp"));
    	//-

    	
    	
        return "rev/geofence";
    }

    private List<InvSiteMapLocItem> getLocItemByVenueType(String venueType) {
    	
    	ArrayList<InvSiteMapLocItem> retList = new ArrayList<InvSiteMapLocItem>();
    	
    	List<Tuple> locList = invService.getSiteLocListByVenueType(venueType);
    	for(Tuple tuple : locList) {
    		double lat = (double)Util.parseFloat((String) tuple.get(1));
    		double lng = (double)Util.parseFloat((String) tuple.get(2));
    		
    		retList.add(new InvSiteMapLocItem((String) tuple.get(0), lat, lng));
    	}
    	
    	return retList;
    }
    
    private List<InvSiteMapLocItem> getStateLocItems() {
    	
    	ArrayList<InvSiteMapLocItem> retList = new ArrayList<InvSiteMapLocItem>();
    	
    	retList.add(new InvSiteMapLocItem("강원도", 37.8853984, 127.7297758));
    	retList.add(new InvSiteMapLocItem("경기도", 37.2892903, 127.0534814));
    	retList.add(new InvSiteMapLocItem("경상남도", 35.2382905, 128.692398));
    	retList.add(new InvSiteMapLocItem("경상북도", 36.5760207, 128.5055956));
    	retList.add(new InvSiteMapLocItem("광주시", 35.1599785, 126.8513072));
    	retList.add(new InvSiteMapLocItem("대구시", 35.8715411, 128.601505));
    	retList.add(new InvSiteMapLocItem("대전시", 36.3504567, 127.3848187));
    	retList.add(new InvSiteMapLocItem("부산시", 35.179665, 129.0747635));
    	retList.add(new InvSiteMapLocItem("서울시", 37.5662952, 126.9779451));
    	retList.add(new InvSiteMapLocItem("세종시", 36.480167, 127.2891341));
    	retList.add(new InvSiteMapLocItem("울산시", 35.5396224, 129.3115276));
    	retList.add(new InvSiteMapLocItem("인천시", 37.4560123, 126.7052277));
    	retList.add(new InvSiteMapLocItem("전라남도", 34.8162186, 126.4629242));
    	retList.add(new InvSiteMapLocItem("전라북도", 35.8203989, 127.1087521));
    	retList.add(new InvSiteMapLocItem("제주도", 33.4888341, 126.4980797));
    	retList.add(new InvSiteMapLocItem("충청남도", 36.6598307, 126.6734285));
    	retList.add(new InvSiteMapLocItem("충청북도", 36.6358093, 127.4913338));
    	
    	return retList;
    }
    
    private List<InvSiteMapLocItem> getRegionLocItems() {
    	
    	ArrayList<InvSiteMapLocItem> retList = new ArrayList<InvSiteMapLocItem>();
    	
    	retList.add(new InvSiteMapLocItem("강릉시", 37.7519967, 128.8759524));
    	retList.add(new InvSiteMapLocItem("고성군", 38.3805632, 128.4680551));
    	retList.add(new InvSiteMapLocItem("동해시", 37.5250249, 129.114494));
    	retList.add(new InvSiteMapLocItem("삼척시", 37.4499796, 129.1654335));
    	retList.add(new InvSiteMapLocItem("속초시", 38.2073706, 128.5922597));
    	retList.add(new InvSiteMapLocItem("양구군", 38.1104416, 127.9922667));
    	retList.add(new InvSiteMapLocItem("양양군", 38.0753921, 128.6191279));
    	retList.add(new InvSiteMapLocItem("영월군", 37.1836921, 128.4618369));
    	retList.add(new InvSiteMapLocItem("원주시", 37.3420473, 127.91973));
    	retList.add(new InvSiteMapLocItem("인제군", 38.0696415, 128.1703348));
    	retList.add(new InvSiteMapLocItem("정선군", 37.3806165, 128.6608674));
    	retList.add(new InvSiteMapLocItem("철원군", 38.1465863, 127.3125449));
    	retList.add(new InvSiteMapLocItem("춘천시", 37.8812763, 127.7300762));
    	retList.add(new InvSiteMapLocItem("태백시", 37.164066, 128.985565));
    	retList.add(new InvSiteMapLocItem("평창군", 37.3707415, 128.390338));
    	retList.add(new InvSiteMapLocItem("홍천군", 37.6970355, 127.8888215));
    	retList.add(new InvSiteMapLocItem("화천군", 38.1062848, 127.7081308));
    	retList.add(new InvSiteMapLocItem("횡성군", 37.4917027, 127.9850466));
    	retList.add(new InvSiteMapLocItem("가평군", 37.8312549, 127.5095764));
    	retList.add(new InvSiteMapLocItem("고양시 덕양구", 37.6371386, 126.832296));
    	retList.add(new InvSiteMapLocItem("고양시 일산동구", 37.6588532, 126.7750145));
    	retList.add(new InvSiteMapLocItem("고양시 일산서구", 37.6779607, 126.7452817));
    	retList.add(new InvSiteMapLocItem("과천시", 37.4291883, 126.9876194));
    	retList.add(new InvSiteMapLocItem("광명시", 37.4785138, 126.8646843));
    	retList.add(new InvSiteMapLocItem("광주시", 37.4294077, 127.2551421));
    	retList.add(new InvSiteMapLocItem("구리시", 37.5942844, 127.1297424));
    	retList.add(new InvSiteMapLocItem("군포시", 37.3615563, 126.9352975));
    	retList.add(new InvSiteMapLocItem("김포시", 37.6153488, 126.715451));
    	retList.add(new InvSiteMapLocItem("남양주시", 37.6358193, 127.216554));
    	retList.add(new InvSiteMapLocItem("동두천시", 37.9035141, 127.0611557));
    	retList.add(new InvSiteMapLocItem("부천시", 37.5034813, 126.7658019));
    	retList.add(new InvSiteMapLocItem("성남시 분당구", 37.3826243, 127.1188797));
    	retList.add(new InvSiteMapLocItem("성남시 수정구", 37.4502742, 127.1455398));
    	retList.add(new InvSiteMapLocItem("성남시 중원구", 37.4304987, 127.1372634));
    	retList.add(new InvSiteMapLocItem("수원시 권선구", 37.2576833, 126.9724145));
    	retList.add(new InvSiteMapLocItem("수원시 영통구", 37.2596172, 127.0464609));
    	retList.add(new InvSiteMapLocItem("수원시 장안구", 37.3039008, 127.0101682));
    	retList.add(new InvSiteMapLocItem("수원시 팔달구", 37.28253, 127.0198668));
    	retList.add(new InvSiteMapLocItem("시흥시", 37.38011, 126.8028807));
    	retList.add(new InvSiteMapLocItem("안산시 단원구", 37.3209984, 126.8152389));
    	retList.add(new InvSiteMapLocItem("안산시 상록구", 37.3008415, 126.8464219));
    	retList.add(new InvSiteMapLocItem("안성시", 37.0078632, 127.2798557));
    	retList.add(new InvSiteMapLocItem("안양시 동안구", 37.3925344, 126.9514227));
    	retList.add(new InvSiteMapLocItem("안양시 만안구", 37.3865929, 126.9325269));
    	retList.add(new InvSiteMapLocItem("양주시", 37.785287, 127.04579));
    	retList.add(new InvSiteMapLocItem("양평군", 37.4916785, 127.4874383));
    	retList.add(new InvSiteMapLocItem("여주시", 37.2981844, 127.63661));
    	retList.add(new InvSiteMapLocItem("연천군", 38.0964092, 127.0750566));
    	retList.add(new InvSiteMapLocItem("오산시", 37.1497727, 127.0770233));
    	retList.add(new InvSiteMapLocItem("용인시 기흥구", 37.2804055, 127.1146587));
    	retList.add(new InvSiteMapLocItem("용인시 수지구", 37.3220678, 127.0976155));
    	retList.add(new InvSiteMapLocItem("용인시 처인구", 37.23444, 127.201346));
    	retList.add(new InvSiteMapLocItem("의왕시", 37.3448076, 126.9687218));
    	retList.add(new InvSiteMapLocItem("의정부시", 37.7380991, 127.033736));
    	retList.add(new InvSiteMapLocItem("이천시", 37.2722456, 127.4350445));
    	retList.add(new InvSiteMapLocItem("파주시", 37.7594363, 126.7803264));
    	retList.add(new InvSiteMapLocItem("평택시", 36.992329, 127.112695));
    	retList.add(new InvSiteMapLocItem("포천시", 37.8948841, 127.2004042));
    	retList.add(new InvSiteMapLocItem("하남시", 37.5391091, 127.2146082));
    	retList.add(new InvSiteMapLocItem("화성시", 37.199408, 126.8316951));
    	retList.add(new InvSiteMapLocItem("거제시", 34.8809062, 128.6218472));
    	retList.add(new InvSiteMapLocItem("거창군", 35.6866787, 127.9095168));
    	retList.add(new InvSiteMapLocItem("고성군", 34.9729819, 128.3222453));
    	retList.add(new InvSiteMapLocItem("김해시", 35.2285453, 128.889352));
    	retList.add(new InvSiteMapLocItem("남해군", 34.837685, 127.892595));
    	retList.add(new InvSiteMapLocItem("밀양시", 35.5034209, 128.7465335));
    	retList.add(new InvSiteMapLocItem("사천시", 35.0033541, 128.064228));
    	retList.add(new InvSiteMapLocItem("산청군", 35.4151331, 127.8738813));
    	retList.add(new InvSiteMapLocItem("양산시", 35.3350077, 129.0370575));
    	retList.add(new InvSiteMapLocItem("의령군", 35.32219, 128.261742));
    	retList.add(new InvSiteMapLocItem("진주시", 35.1803062, 128.1087476));
    	retList.add(new InvSiteMapLocItem("창녕군", 35.5445928, 128.492328));
    	retList.add(new InvSiteMapLocItem("창원시 마산합포구 ", 35.1969469, 128.5678981));
    	retList.add(new InvSiteMapLocItem("창원시 마산회원구 ", 35.2208986, 128.5797679));
    	retList.add(new InvSiteMapLocItem("창원시 성산구 ", 35.1984343, 128.7027314));
    	retList.add(new InvSiteMapLocItem("창원시 의창구", 35.2551919, 128.6344357));
    	retList.add(new InvSiteMapLocItem("창원시 진해구 ", 35.1334431, 128.7101863));
    	retList.add(new InvSiteMapLocItem("통영시", 34.8539374, 128.4340397));
    	retList.add(new InvSiteMapLocItem("함안군", 35.2724758, 128.406535));
    	retList.add(new InvSiteMapLocItem("함양군", 35.5205452, 127.7251759));
    	retList.add(new InvSiteMapLocItem("합천군", 35.5666931, 128.1658708));
    	retList.add(new InvSiteMapLocItem("경산시", 35.8251121, 128.7413664));
    	retList.add(new InvSiteMapLocItem("경주시", 35.8563061, 129.2250935));
    	retList.add(new InvSiteMapLocItem("고령군", 35.7261414, 128.2629528));
    	retList.add(new InvSiteMapLocItem("구미시", 36.1198554, 128.3442447));
    	retList.add(new InvSiteMapLocItem("군위군", 36.242919, 128.572937));
    	retList.add(new InvSiteMapLocItem("김천시", 36.139867, 128.1136501));
    	retList.add(new InvSiteMapLocItem("문경시", 36.5868172, 128.1870909));
    	retList.add(new InvSiteMapLocItem("봉화군", 36.8931267, 128.7325885));
    	retList.add(new InvSiteMapLocItem("상주시", 36.41093057391861, 128.15916723668323));
    	retList.add(new InvSiteMapLocItem("성주군", 35.91921311170467, 128.28311715807828));
    	retList.add(new InvSiteMapLocItem("안동시", 36.56833997409377, 128.72952883466584));
    	retList.add(new InvSiteMapLocItem("영덕군", 36.415009, 129.3657269));
    	retList.add(new InvSiteMapLocItem("영양군", 36.6666558, 129.1124011));
    	retList.add(new InvSiteMapLocItem("영주시", 36.805692, 128.62396));
    	retList.add(new InvSiteMapLocItem("영천시", 35.9732063, 128.9386614));
    	retList.add(new InvSiteMapLocItem("예천군", 36.6468877, 128.4373627));
    	retList.add(new InvSiteMapLocItem("울릉군", 37.4843768, 130.9057809));
    	retList.add(new InvSiteMapLocItem("울진군", 36.9930666, 129.4005861));
    	retList.add(new InvSiteMapLocItem("의성군", 36.3527134, 128.6970886));
    	retList.add(new InvSiteMapLocItem("청도군", 35.647349, 128.734383));
    	retList.add(new InvSiteMapLocItem("청송군", 36.436294, 129.05708));
    	retList.add(new InvSiteMapLocItem("칠곡군", 35.9955007, 128.4017378));
    	retList.add(new InvSiteMapLocItem("남구", 36.0085449, 129.3593971));
    	retList.add(new InvSiteMapLocItem("북구", 36.0432996, 129.3684504));
    	retList.add(new InvSiteMapLocItem("광산구", 35.1395924, 126.7937701));
    	retList.add(new InvSiteMapLocItem("남구", 35.1330375, 126.90219));
    	retList.add(new InvSiteMapLocItem("동구", 35.1459525, 126.9231488));
    	retList.add(new InvSiteMapLocItem("북구", 35.1742068, 126.912188));
    	retList.add(new InvSiteMapLocItem("서구", 35.1520132, 126.8899575));
    	retList.add(new InvSiteMapLocItem("남구", 35.845992, 128.5973969));
    	retList.add(new InvSiteMapLocItem("달서구", 35.8297339, 128.5326441));
    	retList.add(new InvSiteMapLocItem("달성군", 35.7745414, 128.4313244));
    	retList.add(new InvSiteMapLocItem("동구", 35.8866013, 128.6355799));
    	retList.add(new InvSiteMapLocItem("북구", 35.8852292, 128.5830577));
    	retList.add(new InvSiteMapLocItem("서구", 35.8718022, 128.5592204));
    	retList.add(new InvSiteMapLocItem("수성구", 35.8580437, 128.630552));
    	retList.add(new InvSiteMapLocItem("중구", 35.8692838, 128.6060779));
    	retList.add(new InvSiteMapLocItem("대덕구", 36.3465933, 127.4156962));
    	retList.add(new InvSiteMapLocItem("동구", 36.3121271, 127.4549561));
    	retList.add(new InvSiteMapLocItem("서구", 36.3553221, 127.3838132));
    	retList.add(new InvSiteMapLocItem("유성구", 36.3622711, 127.3561885));
    	retList.add(new InvSiteMapLocItem("중구", 36.3255706, 127.4213488));
    	retList.add(new InvSiteMapLocItem("강서구", 35.2122, 128.9804979));
    	retList.add(new InvSiteMapLocItem("금정구", 35.2428426, 129.0921739));
    	retList.add(new InvSiteMapLocItem("기장군", 35.2446266, 129.2222527));
    	retList.add(new InvSiteMapLocItem("남구", 35.1365243, 129.0844196));
    	retList.add(new InvSiteMapLocItem("동구", 35.129335, 129.0454233));
    	retList.add(new InvSiteMapLocItem("동래구", 35.1965099, 129.0930973));
    	retList.add(new InvSiteMapLocItem("부산진구", 35.1628318, 129.0531225));
    	retList.add(new InvSiteMapLocItem("북구", 35.197122, 128.9901437));
    	retList.add(new InvSiteMapLocItem("사상구", 35.1526107, 128.9911898));
    	retList.add(new InvSiteMapLocItem("사하구", 35.1043965, 128.9749537));
    	retList.add(new InvSiteMapLocItem("서구", 35.0979244, 129.0242041));
    	retList.add(new InvSiteMapLocItem("수영구", 35.1455593, 129.1131945));
    	retList.add(new InvSiteMapLocItem("연제구", 35.1762217, 129.0796413));
    	retList.add(new InvSiteMapLocItem("영도구", 35.0912037, 129.0678947));
    	retList.add(new InvSiteMapLocItem("중구", 35.1063286, 129.0322904));
    	retList.add(new InvSiteMapLocItem("해운대구", 35.1631596, 129.1638525));
    	retList.add(new InvSiteMapLocItem("강남구", 37.5175066, 127.0473753));
    	retList.add(new InvSiteMapLocItem("강동구", 37.5300852, 127.1237639));
    	retList.add(new InvSiteMapLocItem("강북구", 37.6397767, 127.0255184));
    	retList.add(new InvSiteMapLocItem("강서구", 37.5509103, 126.8495742));
    	retList.add(new InvSiteMapLocItem("관악구", 37.4781098, 126.9514931));
    	retList.add(new InvSiteMapLocItem("광진구", 37.5383742, 127.0822077));
    	retList.add(new InvSiteMapLocItem("구로구", 37.4954703, 126.8876391));
    	retList.add(new InvSiteMapLocItem("금천구", 37.4568163, 126.8954085));
    	retList.add(new InvSiteMapLocItem("노원구", 37.6540782, 127.0566045));
    	retList.add(new InvSiteMapLocItem("도봉구", 37.6687735, 127.047071));
    	retList.add(new InvSiteMapLocItem("동대문구", 37.5742015, 127.0398327));
    	retList.add(new InvSiteMapLocItem("동작구", 37.5124298, 126.9397997));
    	retList.add(new InvSiteMapLocItem("마포구", 37.5660739, 126.9014792));
    	retList.add(new InvSiteMapLocItem("서대문구", 37.5792607, 126.9364946));
    	retList.add(new InvSiteMapLocItem("서초구", 37.4835872, 127.0326987));
    	retList.add(new InvSiteMapLocItem("성동구", 37.5634092, 127.0369449));
    	retList.add(new InvSiteMapLocItem("성북구", 37.589366, 127.016743));
    	retList.add(new InvSiteMapLocItem("송파구", 37.5144533, 127.1059047));
    	retList.add(new InvSiteMapLocItem("양천구", 37.5169508, 126.8665644));
    	retList.add(new InvSiteMapLocItem("영등포구", 37.526344, 126.896256));
    	retList.add(new InvSiteMapLocItem("용산구", 37.5323264, 126.9907031));
    	retList.add(new InvSiteMapLocItem("은평구", 37.602749, 126.929256));
    	retList.add(new InvSiteMapLocItem("종로구", 37.5734684, 126.978984));
    	retList.add(new InvSiteMapLocItem("중구", 37.5637584, 126.9975517));
    	retList.add(new InvSiteMapLocItem("중랑구", 37.6063046, 127.0931523));
    	retList.add(new InvSiteMapLocItem("세종시", 36.480167, 127.2891341));
    	retList.add(new InvSiteMapLocItem("남구", 35.5437296, 129.3301153));
    	retList.add(new InvSiteMapLocItem("동구", 35.5049429, 129.4167348));
    	retList.add(new InvSiteMapLocItem("북구", 35.5824935, 129.3610967));
    	retList.add(new InvSiteMapLocItem("울주군", 35.5224565, 129.2425173));
    	retList.add(new InvSiteMapLocItem("중구", 35.5694854, 129.3327733));
    	retList.add(new InvSiteMapLocItem("강화군", 37.7466661, 126.4878739));
    	retList.add(new InvSiteMapLocItem("계양구", 37.537367, 126.737744));
    	retList.add(new InvSiteMapLocItem("남동구", 37.4470653, 126.7314616));
    	retList.add(new InvSiteMapLocItem("동구", 37.4738458, 126.6432548));
    	retList.add(new InvSiteMapLocItem("미추홀구", 37.4634703, 126.6502865));
    	retList.add(new InvSiteMapLocItem("부평구", 37.5070463, 126.7218584));
    	retList.add(new InvSiteMapLocItem("서구", 37.5452851, 126.6759938));
    	retList.add(new InvSiteMapLocItem("연수구", 37.4781098, 126.9514931));
    	retList.add(new InvSiteMapLocItem("옹진군", 37.4464879, 126.6368118));
    	retList.add(new InvSiteMapLocItem("중구", 37.4737081, 126.6215278));
    	retList.add(new InvSiteMapLocItem("강진군", 34.6420498, 126.7672885));
    	retList.add(new InvSiteMapLocItem("고흥군", 34.6045347, 127.2755891));
    	retList.add(new InvSiteMapLocItem("곡성군", 35.2820188, 127.2921946));
    	retList.add(new InvSiteMapLocItem("광양시", 34.9406485, 127.695974));
    	retList.add(new InvSiteMapLocItem("구례군", 35.2025, 127.4627778));
    	retList.add(new InvSiteMapLocItem("나주시", 35.015893, 126.710868));
    	retList.add(new InvSiteMapLocItem("담양군", 35.3212228, 126.9882508));
    	retList.add(new InvSiteMapLocItem("목포시", 34.8118291, 126.3922087));
    	retList.add(new InvSiteMapLocItem("무안군", 34.9904204, 126.4816959));
    	retList.add(new InvSiteMapLocItem("보성군", 34.7714286, 127.0800892));
    	retList.add(new InvSiteMapLocItem("순천시", 34.9506366, 127.4873242));
    	retList.add(new InvSiteMapLocItem("신안군", 34.8333259, 126.3513787));
    	retList.add(new InvSiteMapLocItem("여수시", 34.7604121, 127.6622848));
    	retList.add(new InvSiteMapLocItem("영광군", 35.277181, 126.512088));
    	retList.add(new InvSiteMapLocItem("영암군", 34.8001832, 126.6968032));
    	retList.add(new InvSiteMapLocItem("완도군", 34.3109776, 126.7550754));
    	retList.add(new InvSiteMapLocItem("장성군", 35.3018328, 126.784854));
    	retList.add(new InvSiteMapLocItem("장흥군", 34.681603, 126.90708));
    	retList.add(new InvSiteMapLocItem("진도군", 34.48681, 126.263562));
    	retList.add(new InvSiteMapLocItem("함평군", 35.065912, 126.5166908));
    	retList.add(new InvSiteMapLocItem("해남군", 34.5733608, 126.5992505));
    	retList.add(new InvSiteMapLocItem("화순군", 35.0644478, 126.9866463));
    	retList.add(new InvSiteMapLocItem("고창군", 35.435788, 126.702109));
    	retList.add(new InvSiteMapLocItem("군산시", 35.9674688, 126.7368563));
    	retList.add(new InvSiteMapLocItem("김제시", 35.8033532, 126.8806015));
    	retList.add(new InvSiteMapLocItem("남원시", 35.4163573, 127.3904047));
    	retList.add(new InvSiteMapLocItem("무주군", 36.006819, 127.660808));
    	retList.add(new InvSiteMapLocItem("부안군", 35.7318083, 126.7334869));
    	retList.add(new InvSiteMapLocItem("순창군", 35.3744256, 127.1375012));
    	retList.add(new InvSiteMapLocItem("완주군", 35.9047643, 127.162137));
    	retList.add(new InvSiteMapLocItem("익산시", 35.9482584, 126.9577099));
    	retList.add(new InvSiteMapLocItem("임실군", 35.617804, 127.2891042));
    	retList.add(new InvSiteMapLocItem("장수군", 35.6473043, 127.5212196));
    	retList.add(new InvSiteMapLocItem("덕진구", 35.8293678, 127.1343146));
    	retList.add(new InvSiteMapLocItem("완산구", 35.8121664, 127.1197813));
    	retList.add(new InvSiteMapLocItem("정읍시", 35.5700179, 126.8563768));
    	retList.add(new InvSiteMapLocItem("진안군", 35.8287825, 127.4300174));
    	retList.add(new InvSiteMapLocItem("서귀포시", 33.2539385, 126.5595922));
    	retList.add(new InvSiteMapLocItem("제주시", 33.499597, 126.531254));
    	retList.add(new InvSiteMapLocItem("계룡시", 36.2742292, 127.248827));
    	retList.add(new InvSiteMapLocItem("공주시", 36.446551, 127.1190325));
    	retList.add(new InvSiteMapLocItem("금산군", 36.1088323, 127.4881102));
    	retList.add(new InvSiteMapLocItem("논산시", 36.187159, 127.0988903));
    	retList.add(new InvSiteMapLocItem("당진시", 36.8896494, 126.6460204));
    	retList.add(new InvSiteMapLocItem("보령시", 36.3326108, 126.6121563));
    	retList.add(new InvSiteMapLocItem("부여군", 36.275718, 126.909789));
    	retList.add(new InvSiteMapLocItem("서산시", 36.7848153, 126.45032));
    	retList.add(new InvSiteMapLocItem("서천군", 36.0803199, 126.6918748));
    	retList.add(new InvSiteMapLocItem("아산시", 36.7898211, 127.0025933));
    	retList.add(new InvSiteMapLocItem("예산군", 36.6808593, 126.8449329));
    	retList.add(new InvSiteMapLocItem("천안시 동남구", 36.8067814, 127.1516222));
    	retList.add(new InvSiteMapLocItem("천안시 서북구", 36.8778711, 127.1548541));
    	retList.add(new InvSiteMapLocItem("청양군", 36.4591572, 126.8022596));
    	retList.add(new InvSiteMapLocItem("태안군", 36.7455952, 126.2980581));
    	retList.add(new InvSiteMapLocItem("홍성군", 36.6013022, 126.6608855));
    	retList.add(new InvSiteMapLocItem("괴산군", 36.8153352, 127.7866624));
    	retList.add(new InvSiteMapLocItem("단양군", 36.9846588, 128.3654631));
    	retList.add(new InvSiteMapLocItem("보은군", 36.4893953, 127.729483));
    	retList.add(new InvSiteMapLocItem("영동군", 36.1748913, 127.7834363));
    	retList.add(new InvSiteMapLocItem("옥천군", 36.3062769, 127.5713543));
    	retList.add(new InvSiteMapLocItem("음성군", 36.940235, 127.6905015));
    	retList.add(new InvSiteMapLocItem("제천시", 37.1325821, 128.1909478));
    	retList.add(new InvSiteMapLocItem("증평군", 36.7853074, 127.5814886));
    	retList.add(new InvSiteMapLocItem("진천군", 36.854668, 127.435768));
    	retList.add(new InvSiteMapLocItem("청주시 상당구", 36.58974, 127.5052569));
    	retList.add(new InvSiteMapLocItem("청주시 서원구", 36.6375895, 127.4698136));
    	retList.add(new InvSiteMapLocItem("청주시 청원구", 36.6515325, 127.4867018));
    	retList.add(new InvSiteMapLocItem("청주시 흥덕구", 36.6289284, 127.375915));
    	retList.add(new InvSiteMapLocItem("충주시", 36.9909318, 127.9257875));
    	
    	return retList;
    }
}
