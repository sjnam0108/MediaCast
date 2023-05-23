package net.doohad.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.doohad.info.GlobalInfo;
import net.doohad.models.UserCookie;
import net.doohad.models.adc.AdcAd;
import net.doohad.models.adc.AdcAdCreative;
import net.doohad.models.adc.AdcCampaign;
import net.doohad.models.adc.AdcCreatFile;
import net.doohad.models.adc.AdcCreative;
import net.doohad.models.knl.KnlAccount;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.knl.KnlUser;
import net.doohad.models.rev.RevScrHourlyPlay;
import net.doohad.models.service.AdcService;
import net.doohad.models.service.KnlService;
import net.doohad.models.service.OrgService;
import net.doohad.viewmodels.DropDownListItem;
import net.doohad.viewmodels.knl.KnlAccountItem;
import net.doohad.viewmodels.knl.KnlMediumItem;

@Component
public class SolUtil {
	
	//private static final Logger logger = LoggerFactory.getLogger(SolUtil.class);
	
	static KnlService sKnlService;
	static AdcService sAdcService;
	static OrgService sOrgService;
	
	
	@Autowired
	public void setStaticKnlService(KnlService knlService) {
		SolUtil.sKnlService = knlService;
	}
	
	@Autowired
	public void setStaticAdcService(AdcService adcService) {
		SolUtil.sAdcService = adcService;
	}
	
	@Autowired
	public void setStaticOrgService(OrgService orgService) {
		SolUtil.sOrgService = orgService;
	}
	

	/**
	 * 사이트 설정 String 값의 동일 여부 반환(session 값으로)
	 */
	public static boolean propEqVal(HttpSession session, String code, String value) {
		String tmp = getProperty(session, code);
		
		return (Util.isValid(tmp) && tmp.equals(value));
	}

	/**
	 * 사이트 설정 String 값의 동일 여부 반환(사이트 번호로)
	 */
	public static boolean propEqVal(int siteId, String code, String value) {
		String tmp = getProperty(siteId, code);
		
		return (Util.isValid(tmp) && tmp.equals(value));
	}
	
	/**
	 * 사이트 설정 String 값 획득
	 */
	public static String getProperty(HttpSession session, String code) {
		return getProperty(session, code, null, "");
	}

	/**
	 * 사이트 설정 String 값 획득
	 */
	public static String getProperty(HttpSession session, String code, Locale locale) {
		return getProperty(session, code, locale, "");
	}

	/**
	 * 사이트 설정 String 값 획득
	 */
	public static String getProperty(HttpSession session, String code, Locale locale, 
			String defaultValue) {
		String value = getPropertyValue(Util.getSessionSiteId(session), code, locale);
		
		return Util.isValid(value) ? value : defaultValue;
	}
	
	/**
	 * 사이트 설정 String 값 획득
	 */
	public static String getProperty(int siteId, String code) {
		return getProperty(siteId, code, null, "");
	}

	/**
	 * 사이트 설정 String 값 획득
	 */
	public static String getProperty(int siteId, String code, Locale locale) {
		return getProperty(siteId, code, locale, "");
	}

	/**
	 * 사이트 설정 String 값 획득
	 */
	public static String getProperty(int siteId, String code, Locale locale, 
			String defaultValue) {
		String value = getPropertyValue(siteId, code, locale);
		
		return Util.isValid(value) ? value : defaultValue;
	}
	
	private static String getPropertyValue(int siteId, String code, Locale locale) {
		
    	// [WAB] --------------------------------------------------------------------------
		//
		if (Util.isValid(code) && 
				(code.equals("logo.title") || code.equals("quicklink.max.menu"))) {
			return Util.getFileProperty(code);
		} else {
			return "";
		}
		//
    	// [WAB] --------------------------------------------------------------------------
    	// [SignCast] ext ----------------------------------------------------------- start
    	//
    	//
		
		/*
		return sOptService.getSiteOption(siteId, code, locale);
		*/
		
    	//
    	//
    	// [SignCast] ext ------------------------------------------------------------- end
	}

	

	/**
	 * 물리적인 루트 디렉토리 획득
	 */
	public static String getPhysicalRoot(String ukid) {
		return getPhysicalRoot(ukid, "");
	}

	/**
	 * 물리적인 루트 디렉토리 획득
	 */
	public static String getPhysicalRoot(String ukid, String medium) {
		if (Util.isNotValid(ukid)) {
			return null;
		}
		
		String rootDirPath = Util.getFileProperty("dir.rootPath");
		
		//
		//   CtntUpTemp: 광고 소재 파일 업로드 서버 폴더
		//   UpTemp: 품목 일괄업로드 서버 폴더
		//   XlsTemplate: 품목 일괄업로드 페이지에서 템플릿 파일이 위치하는 폴더
		//
		if (ukid.equals("Thumb")) {
            return Util.getValidRootDir(rootDirPath) + "thumbs";
		} else if (ukid.equals("UpCtntTemp")) {
            return Util.getValidRootDir(rootDirPath) + "upctnttemp";
		} else if (ukid.equals("UpTemp")) {
            return Util.getValidRootDir(rootDirPath) + "uptemp";
		} else if (ukid.equals("XlsTemplate")) {
            return Util.getValidRootDir(rootDirPath) + "templates";
		}

        return Util.getPhysicalRoot(ukid);
	}

	/**
	 * 물리적인 루트 디렉토리 획득
	 */
	public static String getPhysicalRoot(String ukid, String site, String repos) {
		if (ukid == null || ukid.isEmpty()) {
			return null;
		}
		
		/*
		String rootDirPath = Util.getFileProperty("dir.rootPath");
		
		if (ukid.equals("Repository")) {
			if (repos == null || repos.isEmpty()) {
				return Util.getValidRootDir(rootDirPath) + "repositories/" + site;
			} else {
				return Util.getValidRootDir(rootDirPath) + "repositories/" + site + "/Additional/" + repos;
			}
		} else if (ukid.equals("Schedule")) {
			if (repos == null || repos.isEmpty()) {
				return Util.getValidRootDir(rootDirPath) + "repositories/" + site + "/Schedule";
			} else {
				return Util.getValidRootDir(rootDirPath) + "repositories/" + site + "/Additional/" + repos
						+ "/Schedule";
			}
		}
		*/
		
		return null;
	}

	/**
	 * 관리 가능한 매체 목록 획득
	 */
	public static List<KnlMediumItem> getAvailMediumListByUserId(int userId) {
		
		ArrayList<KnlMediumItem> list = new ArrayList<KnlMediumItem>();
		
		KnlUser user = sKnlService.getUser(userId);
		if (user != null) {
			
			// 연결된 계정의 관리 영역 확인
			//   - "커널 관리 가능" -> 유효한 모든 매체
			//   - "매체 관리 가능" -> 계정의 관리 대상 매체 목록
			if (user.getAccount().isScopeKernel()) {
				List<KnlMedium> mediumList = sKnlService.getValidMediumList();
				for(KnlMedium medium : mediumList) {
					list.add(new KnlMediumItem(medium.getId(), medium.getShortName(),
							medium.getName()));
				}
			} else if (user.getAccount().isScopeMedium()) {
				List<String> media = Util.tokenizeValidStr(user.getAccount().getDestMedia());
				for(String m : media) {
					KnlMedium medium = sKnlService.getMedium(m);
					if (medium != null) {
						list.add(new KnlMediumItem(medium.getId(), medium.getShortName(),
								medium.getName()));
					}
				}
			}
			
			if (list.size() > 0) {
				Collections.sort(list, new Comparator<KnlMediumItem>() {
	    	    	public int compare(KnlMediumItem item1, KnlMediumItem item2) {
	    	    		return item1.getShortName().toLowerCase().compareTo(item2.getShortName().toLowerCase());
	    	    	}
	    	    });
			}
		}

		return list;
	}

	/**
	 * 관리 가능한 광고 제공 계정 목록 획득
	 */
	public static List<KnlAccountItem> getAvailAdAccountListByUserId(int userId) {
		
		ArrayList<KnlAccountItem> list = new ArrayList<KnlAccountItem>();
		
		KnlUser user = sKnlService.getUser(userId);
		if (user != null) {
			
			// 연결된 계정의 관리 영역 확인
			//   - "커널 관리 가능" -> 유효한 모든 광고 제공 계정
			//   - "광고 제공 가능" -> 연결된 현재 계정만
			if (user.getAccount().isScopeKernel()) {
				List<KnlAccount> accountList = sKnlService.getValidAccountList();
				for(KnlAccount account : accountList) {
					list.add(new KnlAccountItem(account.getId(), account.getName()));
				}
			} else if (user.getAccount().isScopeAd()) {
				list.add(new KnlAccountItem(user.getAccount().getId(), user.getAccount().getName()));
			}
			
			if (list.size() > 0) {
				Collections.sort(list, new Comparator<KnlAccountItem>() {
	    	    	public int compare(KnlAccountItem item1, KnlAccountItem item2) {
	    	    		return item1.getName().toLowerCase().compareTo(item2.getName().toLowerCase());
	    	    	}
	    	    });
			}
		}

		return list;
	}

	/**
	 * 유효 시작 및 종료일에 대한 유효성 검사 결과 획득
	 */
	public static boolean isEffectiveDates(Date startDate, Date endDate) {
		
		Date now = new Date();
		if (startDate == null || now.before(startDate)) {
			return false;
		}
		
		if (endDate != null && now.after(endDate)) {
			return false;
		}
		
		return true;
	}
	

	/**
	 * Hibernate: 표준 count 메소드
	 */
	public static int getCount(org.hibernate.Session session, Class<?> clazz) {
		
		if (session == null) return 0;
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		cq.select(cb.count(cq.from(clazz)));

		return (session.createQuery(cq).getSingleResult()).intValue();
	}
	

	/**
	 * Hibernate: 표준 delete 메소드
	 */
	public static void delete(org.hibernate.Session session, Class<?> clazz, int id) {
		
		if (session != null) {
			session.delete(session.load(clazz, id));
		}
	}

	
	/**
	 * API 키를 이용하여 매체 정보 획득
	 */
	public static KnlMedium getMediumByApiKey(String apiKey) {
		
		KnlMedium medium = GlobalInfo.MediaMap.get(apiKey);
		if (medium == null) {
			medium = sKnlService.getMediumByApiKey(apiKey);
			if (medium == null) {
				return null;
			} else {
				GlobalInfo.MediaMap.put(apiKey, medium);
				return medium;
			}
		} else {
			return medium;
		}
	}

	
	/**
	 * 시스템 백그라운드 작업에서 필요한 일련번호 획득
	 */
	public static int getBgNextSeq(String key) {
		
		Integer maxVal = GlobalInfo.BgMaxValueMap.get(Util.getFirstToken(key, "_"));
		if (maxVal == null || maxVal.intValue() == 0) {
			maxVal = 10;
			GlobalInfo.BgMaxValueMap.put(Util.getFirstToken(key, "_"), maxVal);
		}
		
		Integer currVal = GlobalInfo.BgCurrValueMap.get(key);
		if (currVal == null) {
			currVal = 1;
		} else {
			currVal ++;
			if (currVal > maxVal) {
				currVal = 1;
			}
		}
		
		GlobalInfo.BgCurrValueMap.put(key, currVal);
		
		return currVal;
	}

	
	/**
	 * 시스템 백그라운드 작업에서 필요한 일련번호 최고값 설정
	 */
	public static void setBgMaxSeq(String key, int value) {
		
		if (Util.isNotValid(key)) {
			return;
		}
		
		GlobalInfo.BgMaxValueMap.put(key, value <= 0 ? 10 : value * 3);
	}

	
	/**
	 * 주간 및 시간별 문자열을 통해 시간 합계를 획득
	 */
	public static int getHourCnt(String hourStr) {
		
		if (Util.isNotValid(hourStr) || hourStr.length() != 168) {
			return 0;
		}
		
		int cnt = 0;
		for(int i = 0; i < 168; i ++) {
			if (hourStr.substring(i, i + 1).equals("1")) {
				cnt ++;
			}
		}
		
		return cnt;
	}

	
	/**
	 * 24x7 시간 문자열이 현재 시간 포함 여부 획득
	 */
	public static boolean isCurrentOpHours(String opHour) {
		
		if (Util.isNotValid(opHour) || opHour.length() != 168) {
			return false;
		}
		
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		
		int offDate = cal.get(Calendar.HOUR_OF_DAY);
		
		// 월요일 0, ..., 일요일 6
		int offDay = cal.get(Calendar.DAY_OF_WEEK);
		offDay -= 2;
		if (offDay < 0) {
			offDay = 6;
		}
		
		int offset = offDay * 24 + offDate;
		
		return opHour.substring(offset, offset + 1).equals("1");
		
	}

	
	/**
	 * 현재 상태 표시 문자열에 해당 시간(분)의 상태를 기록한 문자열 획득
	 */
	public static String getScrStatusLine(String prevStatusLine, Date date, String currStatus) {
		
		return getScrStatusLine(prevStatusLine, date, currStatus, false);
	}

	
	/**
	 * 현재 상태 표시 문자열에 해당 시간(분)의 상태를 기록한 문자열 획득
	 */
	public static String getScrStatusLine(String prevStatusLine, Date date, String currStatus, boolean forcedMode) {
		
		if (Util.isNotValid(prevStatusLine) || prevStatusLine.length() != 1440) {
			prevStatusLine = String.format("%1440s", "2").replace(' ', '2');
		}
		
		if (Util.isNotValid(currStatus) || currStatus.length() != 1) {
			return prevStatusLine;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		int pos = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
		if (pos < 0 || pos > 1439) {
			return prevStatusLine;
		}

		boolean goAhead = true;
		if (!forcedMode) {
			String prevStatus = prevStatusLine.substring(pos, pos + 1);
			if (prevStatus.compareTo(currStatus) >= 0) {
				goAhead = false;
			}
		}
		
		if (goAhead) {
			if (pos == 0) {
				return currStatus + prevStatusLine.substring(1);
			} else if (pos == 1439) {
				return prevStatusLine.substring(0, 1439) + currStatus;
			}
			
			return prevStatusLine.substring(0, pos) + currStatus + prevStatusLine.substring(pos + 1);
		}
		
		return prevStatusLine;
	}

	
	/**
	 * 현재 상태 표시 문자열에 해당 시간(분)의 상태를 기록한 문자열 획득
	 */
	public static String getTodayScrStatusLine(String statusLine) {
		
		String defaultStr = String.format("%1440s", "9").replace(' ', '9');
		
		if (Util.isNotValid(statusLine) || statusLine.length() != 1440) {
			return defaultStr;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());

		int pos = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);

		if (pos < 0 || pos > 1439) {
			return defaultStr;
		} else if (pos == 1439) {
			return statusLine;
		}
		
		pos ++;
		
		return statusLine.substring(0, pos) + defaultStr.substring(0, 1440 - pos);
	}

	
	/**
	 * 두 날짜에 걸쳐진 모든 분단위 날짜(시간) 목록 획득
	 */
	public static List<Date> getOnTimeMinuteDateListBetween(Date date1, Date date2) {
		
		ArrayList<Date> retList = new ArrayList<Date>();

		if (date1 == null || date2 == null) {
			return retList;
		}
		
		if (date1.compareTo(date2) > 0) {
			Date tmp = date1;
			date1 = date2;
			date2 = tmp;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date1);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		date1 = cal.getTime();
		
		cal.setTime(date2);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		date2 = cal.getTime();
		
		while (date1.compareTo(date2) <= 0) {
			retList.add(date1);
			date1 = Util.addMinutes(date1, 1);
		}
		
		return retList;
	}

	
	/**
	 * 광고 순서 문자열을 바탕으로 방송 소재 선택 및 그룹 내 순서 조정 문자열 획득
	 */
	public static String selectAdSeqList(String ordStr) {
		
		if (Util.isNotValid(ordStr)) {
			return "";
		}
		
		String ret = "";
		List<String> grpList = Util.tokenizeValidStr(ordStr, "|");
		for (String grpStr : grpList) {
			if (Util.isNotValid(grpStr)) {
				continue;
			}
			List<String> adList = Util.tokenizeValidStr(grpStr, ",");
			if (adList.size() == 0) {
				continue;
			} else {
				Collections.shuffle(adList);
				for(String ad : adList) {
					ret += ad + "|";
				}
			}
		}
		
		grpList = Util.tokenizeValidStr(ret, "|");
		ret = "";
		for (String s : grpList) {
			if (s.indexOf("_") > -1) {
				List<String> creatList = Util.tokenizeValidStr(s, "_");
				if (creatList.size() > 1) {
					ArrayList<String> ids = new ArrayList<String>();
					for (String c : creatList) {
						List<String> cp = Util.tokenizeValidStr(c, ":");
						if (cp.size() == 2) {
							int w = Util.parseInt(cp.get(1));
							if (w > 0) {
								for (int i = 0; i < w; i ++) {
									ids.add(cp.get(0));
								}
							}
						}
					}
					Collections.shuffle(ids);
					ret += ids.get(0) + "|";
				}
			} else {
				ret += s + "|";
			}
		}
		
		if (ret.equals("|")) {
			return "";
		} else {
			return Util.removeTrailingChar(ret);
		}
	}

	
	/**
	 * 두 날짜에 걸쳐진 모든 분단위 날짜(시간) 목록 획득
	 */
	public static int getCurrHourCount(RevScrHourlyPlay hourlyPlay) {

		if (hourlyPlay == null) {
			return 0;
		}
		
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());		
		
        switch (calendar.get(Calendar.HOUR_OF_DAY)) {
        case 0: return hourlyPlay.getCnt00();
        case 1: return hourlyPlay.getCnt01();
        case 2: return hourlyPlay.getCnt02();
        case 3: return hourlyPlay.getCnt03();
        case 4: return hourlyPlay.getCnt04();
        case 5: return hourlyPlay.getCnt05();
        case 6: return hourlyPlay.getCnt06();
        case 7: return hourlyPlay.getCnt07();
        case 8: return hourlyPlay.getCnt08();
        case 9: return hourlyPlay.getCnt09();
        case 10: return hourlyPlay.getCnt10();
        case 11: return hourlyPlay.getCnt11();
        case 12: return hourlyPlay.getCnt12();
        case 13: return hourlyPlay.getCnt13();
        case 14: return hourlyPlay.getCnt14();
        case 15: return hourlyPlay.getCnt15();
        case 16: return hourlyPlay.getCnt16();
        case 17: return hourlyPlay.getCnt17();
        case 18: return hourlyPlay.getCnt18();
        case 19: return hourlyPlay.getCnt19();
        case 20: return hourlyPlay.getCnt20();
        case 21: return hourlyPlay.getCnt21();
        case 22: return hourlyPlay.getCnt22();
        case 23: return hourlyPlay.getCnt23();
        }

		return 0;
	}

	
	/**
	 * 24x7 시간 문자열을 통해 남은 시간을 획득
	 */
	public static int getRemainOpHours(String opHour) {
		
		if (Util.isNotValid(opHour) || opHour.length() != 168) {
			return 0;
		}
		
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		
		int offDate = cal.get(Calendar.HOUR_OF_DAY);
		
		// 월요일 0, ..., 일요일 6
		int offDay = cal.get(Calendar.DAY_OF_WEEK);
		offDay -= 2;
		if (offDay < 0) {
			offDay = 6;
		}
		
		int offset = offDay * 24 + offDate;
		int offsetEnd = (offDay + 1) * 24;
		
		int cnt = 0;
		for(int i = offset; i < offsetEnd; i++) {
			if (opHour.substring(i, i + 1).equals("1")) {
				cnt++;
			}
		}
		
		return cnt;
	}
	
	
    /**
	 * 현재 시간 기반 30분 정각의 시간 목록 획득
	 */
    public static List<DropDownListItem> get30MonitMinsDropDownList() {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		Date now = new Date();
		ArrayList<Date> dates = new ArrayList<Date>();
		dates.add(now);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);

		if (cal.get(Calendar.MINUTE) >= 30) {
			cal.set(Calendar.MINUTE, 30);
		} else {
			cal.set(Calendar.MINUTE, 0);
		}
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		Date firstDt = cal.getTime();
		for (int i = 0; i < 48; i ++) {
			dates.add(Util.addMinutes(firstDt, (-1) * i * 30));
		}
		
		String nowDate = Util.toSimpleString(now, "dd");
		String display = "", currDate = "";
		boolean isFirst = true;
		for(Date date : dates) {
			if (isFirst) {
				isFirst = false;
				display = "현재";
				
				list.add(new DropDownListItem(display, String.valueOf(-1)));
			} else {
				currDate = Util.toSimpleString(date, "dd");
				if (nowDate.equals(currDate)) {
					display = "오늘 " + Util.toSimpleString(date, "HH:mm");
				} else {
					display = "어제 " + Util.toSimpleString(date, "HH:mm");
				}
				
				list.add(new DropDownListItem(display, String.valueOf(date.getTime())));
			}
		}
		
		return list;
    }
	
	
    /**
	 * 매체의 옵션 값 획득(유효 시간 30초)
	 */
    public static String getOptValue(int mediumId, String code) {

		if (Util.isNotValid(code) || mediumId < 0) {
			return "";
		}
		
		Date now = new Date();
		String key = "M" + mediumId + code;
		
		String value = GlobalInfo.FndOptMap.get(key);
		if (Util.isValid(value)) {
			Date date = GlobalInfo.FndOptExpiredMap.get(key);
			if (date != null && date.after(now)) {
				return value;
			}
		}
		
		value = sOrgService.getMediumOptValue(mediumId, code);
		if (Util.isValid(value)) {
			GlobalInfo.FndOptMap.put(key, value);
			GlobalInfo.FndOptExpiredMap.put(key, Util.addSeconds(now, 30));
			return value;
		}
		
		// 값이 없거나, 아직 저장이 되지 않음
		if (Util.isValid(code)) {
			value = "";
			if (code.equals("selAd.type")) {
				value = "L";
			}
			
			if (Util.isValid(value)) {
				GlobalInfo.FndOptMap.put(key, value);
				GlobalInfo.FndOptExpiredMap.put(key, Util.addSeconds(now, 30));
				return value;
			}
		}
    	
		return "";
    }
	
	
    /**
	 * 매체의 지도 마커 이미지 URL 획득
	 */
    public static String getMarkerUrl(int mediumId) {
    	
    	KnlMedium medium = sKnlService.getMedium(mediumId);
    	if (medium == null) {
    		return GlobalInfo.ApiTestServer + "/resources/shared/images/marker/marker-default.png";
    	}
    	
    	return getMarkerUrl(medium.getShortName());
    }
	
	
    /**
	 * 매체의 지도 마커 이미지 URL 획득
	 */
    public static String getMarkerUrl(String mediumID) {
    	
    	if (Util.webFileExists(GlobalInfo.ApiTestServer + "/resources/shared/images/marker/marker-" + mediumID + ".png")) {
    		return GlobalInfo.ApiTestServer + "/resources/shared/images/marker/marker-" + mediumID + ".png";
    	}
    	
    	return GlobalInfo.ApiTestServer + "/resources/shared/images/marker/marker-default.png";
    }
	
	
    /**
	 * 서비스 응답시간의 체크 시작일시 획득
	 */
    public static Date getSvcRespTimeCheckDate(int timeMillis) {
    	
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		cal.add(Calendar.MILLISECOND, timeMillis * -1);
		
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
    }

	
	/**
	 * 상태 표시행 두 개를 하나로 합친 결과 획득
	 */
	public static String mergeScrStatusLines(String statusLine1, String statusLine2) {
		
		if (Util.isNotValid(statusLine1) && Util.isNotValid(statusLine2)) {
			return "";
		} else if (Util.isNotValid(statusLine1)) {
			return statusLine2;
		} else if (Util.isNotValid(statusLine2)) {
			return statusLine1;
		} else if (statusLine1.length() != 1440 || statusLine2.length() != 1440) {
			return statusLine1;
		}
		
		StringBuffer sb = new StringBuffer();
		
		for(int i = 0; i < 1440; i ++) {
			String s1 = statusLine1.substring(i, i + 1);
			String s2 = statusLine2.substring(i, i + 1);
			
			if (s1.compareTo(s2) < 0) {
				sb.append(s2);
			} else {
				sb.append(s1);
			}
		}
		
		return sb.toString();
	}
	
	
    /**
	 * 요청에 포함된 쿠키 중 currAdId의 값을 획득
	 */
	public static int getCurrAdIdFromRequest(HttpServletRequest request) {
		
		int ret = -1;
		UserCookie userCookie = new UserCookie(request);
		if (userCookie != null) {
			ret = userCookie.getCurrAdId();
		}
		
		return ret;
	}
	
	
    /**
	 * 세션에 "현재" 광고 정보를 기록
	 */
	public static int saveCurrAdsToSession(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			int campaignId, int adId) {
		
		int currAdId = adId;
		if (adId < 0) {
	    	currAdId = Util.parseInt(Util.cookieValue(request, "currAdId"));
		}
    	
    	boolean goAhead = false;
    	List<AdcAd> adList = sAdcService.getAdListByCampaignId(campaignId);
    	ArrayList<DropDownListItem> currAds = new ArrayList<DropDownListItem>();
    	for(AdcAd ad : adList) {
			currAds.add(new DropDownListItem(ad.getName(), String.valueOf(ad.getId())));
    		if (ad.getId() == currAdId) {
    			goAhead = true;
    		}
    	}
		Collections.sort(currAds, new Comparator<DropDownListItem>() {
	    	public int compare(DropDownListItem item1, DropDownListItem item2) {
	    		return item1.getText().compareTo(item2.getText());
	    	}
	    });
    	
    	if (!goAhead) {
    		if (adList.size() == 0) {
    			currAdId = -1;
    		} else {
    			currAdId = Util.parseInt(currAds.get(0).getValue());
    		}
    	}
    	if ((goAhead && adId > 0) || !goAhead) {
    		response.addCookie(Util.cookie("currAdId", String.valueOf(currAdId)));
    	}
    	
		session.setAttribute("currAdId", String.valueOf(currAdId));
		session.setAttribute("currAds", currAds);
		
		return currAdId;
	}

	
    /**
	 * 전달된 캠페인의 상태카드를 설정
	 */
	public static void setCampaignStatusCard(AdcCampaign campaign) {
		
		// 진행 중인 캠페인에 대해서만 상태 카드 처리
		if (campaign != null && campaign.getStatus().equals("R")) {
			
			List<AdcAdCreative> adCreatList = sAdcService.getActiveAdCreativeListByCampaignId(campaign.getId());
			if (adCreatList.size() == 0) {
				campaign.setStatusCard("R");
			} else {
    			
    			boolean hasEffActive = false;
    			for(AdcAdCreative adCreative : adCreatList) {
    				if (Util.isBetween(Util.removeTimeOfDate(new Date()), adCreative.getStartDate(), adCreative.getEndDate())) {
    					hasEffActive = true;
    					break;
    				}
    			}
    			if (!hasEffActive) {
    				campaign.setStatusCard("Y");
    			}
			}
		}
	}
	
	
    /**
	 * 전달된 광고의 상태카드를 설정
	 */
	public static void setAdStatusCard(AdcAd ad) {
		
		// 진행 중인 광고에 대해서만 상태 카드 처리
		if (ad != null && ad.getStatus().equals("R")) {
			
			List<AdcAdCreative> adCreatList = sAdcService.getActiveAdCreativeListByAdId(ad.getId());
			if (adCreatList.size() == 0) {
				ad.setStatusCard("R");
			} else {
    			
    			boolean hasEffActive = false;
    			for(AdcAdCreative adCreative : adCreatList) {
    				if (Util.isBetween(Util.removeTimeOfDate(new Date()), adCreative.getStartDate(), adCreative.getEndDate())) {
    					hasEffActive = true;
    					break;
    				}
    			}
    			if (!hasEffActive) {
    				ad.setStatusCard("Y");
    			}
			}
		}
	}
	
	
    /**
	 * 전달된 광고의 인벤 타겟팅 여부를 설정
	 */
	public static void setAdInvenTargeted(AdcAd ad) {
		
		if (ad != null) {
			ArrayList<Integer> targetIds = new ArrayList<Integer>();
			
			if (ad != null) {
				List<Tuple> countList = sAdcService.getAdTargetCountGroupByMediumAdId(ad.getMedium().getId());
				for(Tuple tuple : countList) {
					targetIds.add((Integer) tuple.get(0));
				}
				
			}

			if (targetIds.contains(ad.getId())) {
				ad.setInvenTargeted(true);
			}
		}
	}
	
	
    /**
	 * 전달된 광고의 광고 소재 해상도를 설정
	 */
	public static void setAdResolutions(AdcAd ad) {
		
		if (ad != null) {
			List<AdcAdCreative> list = sAdcService.getAdCreativeListByAdId(ad.getId());
			ArrayList<String> resolutions = new ArrayList<String>();
			
			for(AdcAdCreative adCreate : list) {
				List<AdcCreatFile> fileList = sAdcService.getCreatFileListByCreativeId(adCreate.getCreative().getId());
				for(AdcCreatFile creatFile : fileList) {
        			
        			// 20% 범위로 적합도 판정
        			int fitness = sAdcService.measureResolutionWithMedium(
        					creatFile.getResolution(), creatFile.getMedium().getId(), 20);
    				
        			String value = String.valueOf(fitness) + ":" + creatFile.getResolution();
        			if (!resolutions.contains(value)) {
        				resolutions.add(value);
        			}
				}
			}
			
			String reso = "";
			for(String r : resolutions) {
				if (Util.isValid(reso)) {
					reso += "|" + r;
				} else {
					reso = r;
				}
				
			}
			
			ad.setResolutions(reso);
		}
	}
	
	
    /**
	 * 세션에 "현재" 광고 소재 정보를 기록
	 */
	public static int saveCurrCreativesToSession(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			int advertiserId, int creativeId) {
		
		int currCreatId = creativeId;
		if (creativeId < 0) {
			currCreatId = Util.parseInt(Util.cookieValue(request, "currCreatId"));
		}
    	
    	boolean goAhead = false;
    	List<AdcCreative> creatList = sAdcService.getCreativeListByAdvertiserId(advertiserId);
    	ArrayList<DropDownListItem> currCreatives = new ArrayList<DropDownListItem>();
    	for(AdcCreative creative : creatList) {
    		currCreatives.add(new DropDownListItem(creative.getName(), String.valueOf(creative.getId())));
    		if (creative.getId() == currCreatId) {
    			goAhead = true;
    		}
    	}
		Collections.sort(currCreatives, new Comparator<DropDownListItem>() {
	    	public int compare(DropDownListItem item1, DropDownListItem item2) {
	    		return item1.getText().compareTo(item2.getText());
	    	}
	    });
    	
    	if (!goAhead) {
    		if (creatList.size() == 0) {
    			currCreatId = -1;
    		} else {
    			currCreatId = Util.parseInt(currCreatives.get(0).getValue());
    		}
    	}
    	if ((goAhead && creativeId > 0) || !goAhead) {
    		response.addCookie(Util.cookie("currCreatId", String.valueOf(currCreatId)));
    	}
    	
		session.setAttribute("currCreatId", String.valueOf(currCreatId));
		session.setAttribute("currCreatives", currCreatives);
		
		return currCreatId;
	}
	
	
    /**
	 * 전달된 광고 소재의 인벤 타겟팅 여부를 설정
	 */
	public static void setCreativeInvenTargeted(AdcCreative creative) {
		
		if (creative != null) {
			ArrayList<Integer> targetIds = new ArrayList<Integer>();
			
			List<Tuple> countList = sAdcService.getCreatTargetCountGroupByMediumCreativeId(creative.getMedium().getId());
			for(Tuple tuple : countList) {
				targetIds.add((Integer) tuple.get(0));
			}

			if (targetIds.contains(creative.getId())) {
				creative.setInvenTargeted(true);
			}
		}
	}
	
	
    /**
	 * 전달된 광고 소재의 해상도를 설정
	 */
	public static void setCreativeResolutions(AdcCreative creative) {
		
		if (creative != null) {
			List<AdcCreatFile> fileList = sAdcService.getCreatFileListByCreativeId(creative.getId());
			
			String resolutions = "";
			for(AdcCreatFile creatFile : fileList) {
    			
    			// 20% 범위로 적합도 판정
    			int fitness = sAdcService.measureResolutionWithMedium(
    					creatFile.getResolution(), creatFile.getMedium().getId(), 20);
				
				if (Util.isValid(resolutions)) {
					resolutions += "|" + String.valueOf(fitness) + ":" + creatFile.getResolution();
				} else {
					resolutions = String.valueOf(fitness) + ":" + creatFile.getResolution();
				}
			}
			
			creative.setFileResolutions(resolutions);
		}
	}

}
