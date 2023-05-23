package net.doohad.controllers.sys;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.doohad.controllers.rev.RevGeofenceController;
import net.doohad.exceptions.ServerOperationForbiddenException;
import net.doohad.models.AdnMessageManager;
import net.doohad.models.DataSourceRequest;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.service.RevService;
import net.doohad.viewmodels.sys.SysAdSelItem;

/**
 * 광고 선택 후 보고 대기 컨트롤러
 */
@Controller("sys-ad-sel-controller")
@RequestMapping(value="/sys/adsel")
public class SysAdSelController {

	private static final Logger logger = LoggerFactory.getLogger(RevGeofenceController.class);


    @Autowired 
    private RevService revService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 광고 선택 후 보고 대기 페이지
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
    	model.addAttribute("pageTitle", "광고 선택 후 보고 대기");
    	
    	
    	ArrayList<String> IDs = new ArrayList<String>();
    	List<Tuple> mediumList = revService.getAdSelectMediumStatTupleList();
		for(Tuple tuple : mediumList) {
			IDs.add((String) tuple.get(1));
		}
		
		model.addAttribute("mediumTitle1", (IDs.size() > 0) ? IDs.get(0) : "-");
		model.addAttribute("mediumTitle2", (IDs.size() > 1) ? IDs.get(1) : "-");
		model.addAttribute("mediumTitle3", (IDs.size() > 2) ? IDs.get(2) : "-");
		model.addAttribute("mediumTitle4", (IDs.size() > 3) ? IDs.get(3) : "-");
		model.addAttribute("mediumTitle5", (IDs.size() > 4) ? IDs.get(4) : "-");

		
        return "sys/adsel";
    }
    
    
	/**
	 * 읽기 액션 - 시간(Hr) 단위
	 */
    @RequestMapping(value = "/readHourly", method = RequestMethod.POST)
    public @ResponseBody List<SysAdSelItem> readHourly(@RequestBody DataSourceRequest request, 
    		HttpSession session) {
    	try {
    		List<Tuple> mediumList = revService.getAdSelectMediumStatTupleList();
    		List<Tuple> hourList1 = revService.getAdSelectHourStatTupleList1();
    		List<Tuple> hourList2 = revService.getAdSelectHourStatTupleList2();
    		
    		HashMap<String, Integer> map = new HashMap<String, Integer>();
    		
    		ArrayList<Integer> ids = new ArrayList<Integer>();
    		ArrayList<String> IDs = new ArrayList<String>();
    		
    		for(Tuple tuple : mediumList) {
    			ids.add((int) tuple.get(0));
    			IDs.add((String) tuple.get(1));
    		}
    		
    		
    		//
    		//		SELECT date_format(sel_date, '%Y%m%d%H'), medium_id, count(*)
    		//
    		for(Tuple tuple : hourList1) {
    			String timeStr = (String)tuple.get(0);
    			int mediumId = (int)tuple.get(1);
    			BigInteger count = (BigInteger)tuple.get(2);
    			
				map.put(timeStr + "M" + mediumId, count.intValue());
    		}
    		
    		//
    		//		SELECT date_format(sel_date, '%Y%m%d%H'), medium_id, count(*)
    		//
    		ArrayList<SysAdSelItem> retList = new ArrayList<SysAdSelItem>();
    		for(Tuple tuple : hourList2) {
    			String timeStr = (String)tuple.get(0);
    			BigInteger count = (BigInteger)tuple.get(1);
    			
    			int sub1 = 0, sub2 = 0, sub3 = 0, sub4 = 0, sub5 = 0, sub0 = 0;
    			if (ids.size() > 0) {
        			Integer i = map.get(timeStr + "M" + ids.get(0));
        			if (i != null) {
        				sub1 = i.intValue();
        			}
    			}
    			if (ids.size() > 1) {
        			Integer i = map.get(timeStr + "M" + ids.get(1));
        			if (i != null) {
        				sub2 = i.intValue();
        			}
    			}
    			if (ids.size() > 2) {
        			Integer i = map.get(timeStr + "M" + ids.get(2));
        			if (i != null) {
        				sub3 = i.intValue();
        			}
    			}
    			if (ids.size() > 3) {
        			Integer i = map.get(timeStr + "M" + ids.get(3));
        			if (i != null) {
        				sub4 = i.intValue();
        			}
    			}
    			if (ids.size() > 4) {
        			Integer i = map.get(timeStr + "M" + ids.get(4));
        			if (i != null) {
        				sub5 = i.intValue();
        			}
    			}
    			
    			if (ids.size() > 5) {
    				sub0 = count.intValue() - (sub1 + sub2 + sub3 + sub4 + sub5);
    			}
    			
    			retList.add(new SysAdSelItem(net.doohad.utils.Util.parseDate(timeStr + "00"),
    					(long)sub1, (long)sub2, (long)sub3, (long)sub4, (long)sub5, (long)sub0));
    		}
    		
    		return retList;
    	} catch (Exception e) {
    		logger.error("readHourly", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 분(Min) 단위
	 */
    @RequestMapping(value = "/readMinly", method = RequestMethod.POST)
    public @ResponseBody List<SysAdSelItem> readMinly(@RequestBody DataSourceRequest request, 
    		HttpSession session) {
    	try {
    		List<Tuple> mediumList = revService.getAdSelectMediumStatTupleList();
    		List<Tuple> minList1 = revService.getAdSelectMinStatTupleList1();
    		List<Tuple> minList2 = revService.getAdSelectMinStatTupleList2();
    		
    		HashMap<String, Integer> map = new HashMap<String, Integer>();
    		
    		ArrayList<Integer> ids = new ArrayList<Integer>();
    		ArrayList<String> IDs = new ArrayList<String>();
    		
    		for(Tuple tuple : mediumList) {
    			ids.add((int) tuple.get(0));
    			IDs.add((String) tuple.get(1));
    		}
    		
    		
    		//
    		//		SELECT date_format(sel_date, '%Y%m%d%H%i'), medium_id, count(*)
    		//
    		for(Tuple tuple : minList1) {
    			String timeStr = (String)tuple.get(0);
    			int mediumId = (int)tuple.get(1);
    			BigInteger count = (BigInteger)tuple.get(2);
    			
				map.put(timeStr + "M" + mediumId, count.intValue());
    		}
    		
    		//
    		//		SELECT date_format(sel_date, '%Y%m%d%H%i'), medium_id, count(*)
    		//
    		ArrayList<SysAdSelItem> retList = new ArrayList<SysAdSelItem>();
    		for(Tuple tuple : minList2) {
    			String timeStr = (String)tuple.get(0);
    			BigInteger count = (BigInteger)tuple.get(1);
    			
    			int sub1 = 0, sub2 = 0, sub3 = 0, sub4 = 0, sub5 = 0, sub0 = 0;
    			if (ids.size() > 0) {
        			Integer i = map.get(timeStr + "M" + ids.get(0));
        			if (i != null) {
        				sub1 = i.intValue();
        			}
    			}
    			if (ids.size() > 1) {
        			Integer i = map.get(timeStr + "M" + ids.get(1));
        			if (i != null) {
        				sub2 = i.intValue();
        			}
    			}
    			if (ids.size() > 2) {
        			Integer i = map.get(timeStr + "M" + ids.get(2));
        			if (i != null) {
        				sub3 = i.intValue();
        			}
    			}
    			if (ids.size() > 3) {
        			Integer i = map.get(timeStr + "M" + ids.get(3));
        			if (i != null) {
        				sub4 = i.intValue();
        			}
    			}
    			if (ids.size() > 4) {
        			Integer i = map.get(timeStr + "M" + ids.get(4));
        			if (i != null) {
        				sub5 = i.intValue();
        			}
    			}
    			
    			if (ids.size() > 5) {
    				sub0 = count.intValue() - (sub1 + sub2 + sub3 + sub4 + sub5);
    			}
    			
    			retList.add(new SysAdSelItem(net.doohad.utils.Util.parseDate(timeStr + "00"),
    					(long)sub1, (long)sub2, (long)sub3, (long)sub4, (long)sub5, (long)sub0));
    		}
    		
    		return retList;
    	} catch (Exception e) {
    		logger.error("readMinly", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
}
