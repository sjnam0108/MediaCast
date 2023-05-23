package net.doohad.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.RSAPublicKeySpec;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.doohad.info.GlobalInfo;
import net.doohad.models.CalculateFileSizeVisitor;
import net.doohad.models.CustomObjectMapper;
import net.doohad.models.LoginUser;
import net.doohad.models.MessageManager;
import net.doohad.models.UserCookie;
import net.doohad.models.fnd.FndPriv;
import net.doohad.models.fnd.FndUserPriv;
import net.doohad.models.service.FndService;

@Component
public class Util {
	private static final Logger logger = LoggerFactory.getLogger(Util.class);

	@SuppressWarnings("unused")
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	public void setStaticMsgMgr(MessageManager msgMgr) {
		Util.sMsgMgr = msgMgr;
	}
	
	
	@Autowired
	public void setStaticFndService(FndService fndService) {
		Util.sFndService = fndService;
	}

	static MessageManager sMsgMgr;

	static FndService sFndService;
    
    /**
	 * 소스 파일을 목적 위치 폴더에 복사
	 */
    public static void checkDirAndCopyFile(File srcFile, String dstPath) throws IOException {
    	checkDirectory(dstPath);

    	FileCopyUtils.copy(srcFile, new File(dstPath + "/" + 
    				srcFile.getAbsolutePath().substring(srcFile.getAbsolutePath().lastIndexOf(File.separator) + 1)));
    }
    
    /**
	 * 소스 파일을 목적 위치 폴더에 복사
	 */
    public static void checkDirAndCopyFile(File srcFile, String dstPath, 
    		String dstFilename) throws IOException {
    	checkDirectory(dstPath);

    	FileCopyUtils.copy(srcFile, new File(dstPath + "/" + dstFilename));
    }

	/**
	 * 파일 설정 String 값 획득
	 */
	public static String getFileProperty(String code) {
		return getFileProperty(code, null, "");
	}

	/**
	 * 파일 설정 String 값 획득
	 */
	public static String getFileProperty(String code, Locale locale) {
		return getFileProperty(code, locale, "");
	}

	/**
	 * 파일 설정 String 값 획득
	 */
	public static String getFileProperty(String code, Locale locale, String defaultValue) {
		//String value = sMsgMgr.message(code, locale);
		String value = Util.message(code, locale);
		
		return isValid(value) ? value : defaultValue;
	}

	/**
	 * 구성 파일의 Property 항목의 값 확인 후 있으면 반환 아니면 표준 구성 파일의 값 획득
	 */
	public static String message(String code, Locale locale) {
		
		String configPathFile = System.getenv("CAST_CONFIG");
		
		if (Util.isValid(configPathFile) && Util.isValid(code)) {
			File configFile = new File(configPathFile);
			if (configFile.isFile() && configFile.exists()) {
				
				try (InputStreamReader input = new InputStreamReader( 
						new FileInputStream(configPathFile), StandardCharsets.UTF_8)) {
					
					Properties prop = new Properties();
					prop.load(input);

					String value = prop.getProperty(code);
					if (Util.isValid(value)) {
						return value;
					}
				} catch (Exception e) {
					logger.error("message", e);
				}
			}
		}
		
		return sMsgMgr.message(code, locale);
	}

	/**
	 * 파일 설정 String 값의 동일 여부 반환
	 */
	public static boolean filePropEqVal(String code, String value) {
		String tmp = Util.getFileProperty(code);
		
		return (isValid(tmp) && tmp.equals(value));
	}
	
	/**
	 * 파일 설정 boolean 값 획득
	 */
	public static boolean getBooleanFileProperty(String code) {
		return getBooleanFileProperty(code, null, false);
	}

	/**
	 * 파일 설정 boolean 값 획득
	 */
	public static boolean getBooleanFileProperty(String code, boolean defaultValue) {
		return getBooleanFileProperty(code, null, defaultValue);
	}

	/**
	 * 파일 설정 boolean 값 획득
	 */
	public static boolean getBooleanFileProperty(String code, Locale locale, boolean defaultValue) {
		//String value = sMsgMgr.message(code, locale);
		String value = Util.message(code, locale);
		
		if (isNotValid(value)) {
			return defaultValue;
		}
		
		String tmpValue = value.toLowerCase();
		
		if (tmpValue.equals("y") || tmpValue.equals("t") || tmpValue.equals("true") || 
				tmpValue.equals("yes")) {
			return true;
		} else if (tmpValue.equals("n") || tmpValue.equals("f") || tmpValue.equals("false") || 
				tmpValue.equals("no")) {
			return false;
		}
		
		return defaultValue;
	}
	
	/**
	 * 파일 설정 int 값 획득
	 */
	public static int getIntFileProperty(String code) {
		return getFileProperty(code, null, -1);
	}

	/**
	 * 파일 설정 int 값 획득
	 */
	public static int getFileProperty(String code, int defaultValue) {
		return getFileProperty(code, null, defaultValue);
	}

	/**
	 * 파일 설정 int 값 획득
	 */
	public static int getFileProperty(String code, Locale locale, int defaultValue) {
		//String value = sMsgMgr.message(code, locale);
		String value = Util.message(code, locale);
		
		if (isNotValid(value)) {
			return defaultValue;
		}
		
		if (isIntNumber(value)) {
			return Integer.parseInt(value);
		}
		
		return defaultValue;
	}
	
	/**
	 * 16진수 문자열로 파싱
	 */
	public static byte[] parseHexString(String str) {
		str = str.replaceAll("-", "");
		str = str.replaceAll(":", "");

        byte[] bytes = new byte[str.length() / 2];

        for (int i = 0, j = 0; i < str.length(); i += 2, j++)
        {
            bytes[j] = (byte) Integer.parseInt(str.substring(i, i + 2), 16);
        }
        
        return bytes;
    }
	   
	/**
	 * 시간 문자열로부터 특정 날짜, 시간을 획득
	 */
	public static Date getDate(String timeStr) {
		return getDate(timeStr, null);
	}

	/**
	 * 시간 문자열로부터 특정 날짜, 시간을 획득
	 */
	public static Date getDate(String timeStr, Date date) {
		Date tmpDate = null;
		if (date == null) {
			tmpDate = removeTimeOfDate(new Date());
		} else {
			tmpDate = date;
		}
		
		if (timeStr.length() != 8) {
			return tmpDate;
		}
		
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(tmpDate);
			
			cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(timeStr.substring(0, 2)));
			cal.add(Calendar.MINUTE, Integer.parseInt(timeStr.substring(3, 5)));
			cal.add(Calendar.SECOND, Integer.parseInt(timeStr.substring(6)));
			
			return cal.getTime();
		} catch (Exception e) { }
		
		return tmpDate;
	}

	/**
	 * 전달 날짜에 days 값을 더한 날짜를 획득
	 */
	public static Date addDays(Date date, int days) {
		GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
                 
        return cal.getTime();
	}

	/**
	 * 전달 날짜에 hours 값을 더한 날짜를 획득
	 */
	public static Date addHours(Date date, int hours) {
		GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hours);
                 
        return cal.getTime();
	}

	/**
	 * 전달 날짜에 minutes 값을 더한 날짜를 획득
	 */
	public static Date addMinutes(Date date, int minutes) {
		GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minutes);
                 
        return cal.getTime();
	}

	/**
	 * 전달 날짜에 seconds 값을 더한 날짜를 획득
	 */
	public static Date addSeconds(Date date, int seconds) {
		GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.SECOND, seconds);
                 
        return cal.getTime();
	}

	/**
	 * 구분자에 의한 값 리스트 획득
	 */
	public static List<String> tokenizeValidStr(String val) {
		return tokenizeValidStr(val, "|");
	}

	/**
	 * 구분자에 의한 값 리스트 획득
	 */
	public static List<String> tokenizeValidStr(String val, 
			String delimiter) {
		if (isNotValid(val)) {
			return new ArrayList<String>();
		}
		StringTokenizer st = new StringTokenizer(val, delimiter);
		ArrayList<String> list = new ArrayList<String>();

		while(st.hasMoreElements()) {
			String s = (String)st.nextElement();
			
			if (isValid(s)) {
				list.add(s);
			}
		}
		
		return list;
	}
	
	/**
	 * 로컬화된 문자열이 있을 경우 함께 전달
	 */
	public static String getLocalizedMessageString(String code, Locale locale) {
		if (isNotValid(code)) {
			return code;
		}
		
		String localizedCode = code;
		
		if (code.startsWith("menu.")) {
			localizedCode = "mainmenu." + code.substring(code.indexOf('.') + 1);
		}
		
		String msg = sMsgMgr.message(localizedCode, locale);
		
		return isValid(msg) && !code.equals(msg) ? code + " - " + msg : code;
	}
	
	/**
	 * Java String 값 기본 유효성 결과 획득
	 */
	public static boolean isNotValid(String str) {
		return !isValid(str);
	}
	
	/**
	 * Java String 값 기본 유효성 결과 획득
	 */
	public static boolean isValid(String str) {
		return str != null && !str.isEmpty();
	}
	
    /**
	 * 전체 분 값에 대한 HH:mm 의 값 획득
	 */
	public static String getHHmm(int totalMins) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(Util.removeTimeOfDate(new Date()));
		
		cal.add(Calendar.MINUTE, totalMins);

		return new SimpleDateFormat("HH:mm").format(cal.getTime());
	}
	
    /**
	 * String 값 파싱
	 */
	public static String parseString(String value) {
		return parseString(value, "");
	}
	
    /**
	 * String 값 파싱
	 */
	public static String parseString(String value, String defaultValue) {
		String ret = defaultValue;
		
		if (value == null || value.isEmpty()) {
			return ret;
		}
		
		return value;
	}
	
    /**
	 * Integer 값 파싱
	 */
	public static int parseInt(String value) {
		return parseInt(value, -1);
	}
	
    /**
	 * Integer 값 파싱
	 */
	public static int parseInt(String value, int defaultValue) {
		int ret = defaultValue;
		
		if (isNotValid(value)) {
			return ret;
		}
		
		try {
			ret = Integer.parseInt(value);
		} catch (Exception e) {}
		
		return ret;
	}
	
    /**
	 * Long 값 파싱
	 */
	public static long parseLong(String value) {
		return parseLong(value, -1);
	}
	
    /**
	 * Long 값 파싱
	 */
	public static long parseLong(String value, long defaultValue) {
		long ret = defaultValue;
		
		if (isNotValid(value)) {
			return ret;
		}
		
		try {
			ret = Long.parseLong(value);
		} catch (Exception e) {}
		
		return ret;
	}
	
    /**
	 * Float 값 파싱
	 */
	public static float parseFloat(String value) {
		return parseFloat(value, -1);
	}
	
    /**
	 * Float 값 파싱
	 */
	public static float parseFloat(String value, float defaultValue) {
		float ret = defaultValue;
		
		if (isNotValid(value)) {
			return ret;
		}
		
		try {
			ret = Float.parseFloat(value);
		} catch (Exception e) {}
		
		return ret;
	}
	
    /**
	 * Double 값 파싱
	 */
	public static double parseDouble(String value) {
		return parseDouble(value, -1);
	}
	
    /**
	 * Double 값 파싱
	 */
	public static double parseDouble(String value, double defaultValue) {
		double ret = defaultValue;
		
		if (isNotValid(value)) {
			return ret;
		}
		
		try {
			ret = Double.parseDouble(value);
		} catch (Exception e) {}
		
		return ret;
	}
	
    /**
	 * Boolean 값 파싱
	 */
	public static boolean parseBoolean(String value) {
		return parseBoolean(value, false);
	}
	
    /**
	 * Boolean 값 파싱
	 */
	public static boolean parseBoolean(String value, boolean defaultValue) {
		boolean ret = defaultValue;
		
		if (isNotValid(value)) {
			return ret;
		}
		
		if (value.equals("Y") || value.equals("T")) {
			return true;
		}
		
		try {
			ret = Boolean.parseBoolean(value);
		} catch (Exception e) {}
		
		return ret;
	}
	
    /**
	 * 단위 기준 파일 크기 문자열 획득
	 */
	public static String getSmartFileLength(int fileLength) {
		return getSmartFileLength((long)fileLength);
	}
	
    /**
	 * 단위 기준 파일 크기 문자열 획득
	 */
	public static String getSmartFileLength(long fileLength) {
		DecimalFormat dFormat = new DecimalFormat("#,##0.0");

		if (fileLength < 1024) {
			return dFormat.format(fileLength) + " B";
		} else if (fileLength < 1024 * 1024) {
			return dFormat.format(Math.round(
					(double)fileLength * 100 / 1024) / 100.0) + " KB";
		} else if (fileLength < 1024 * 1024 * 1024) {
			return dFormat.format((Math.round(
					(double)fileLength * 100.0 / 1024.0 / 1024.0) / 100.0)) + " MB";
		} else {
			return dFormat.format((Math.round(
					(double)fileLength * 100.0 / 1024.0 / 1024.0 / 1024.0) / 100.0)) + " GB";
		}
	}
	
    /**
	 * Long Number Validation
	 */
	public static boolean isLongNumber(String value) {
		try {
			Long.parseLong(value);
			return true;
		} catch (Exception e) {}
		
		return false;
	}
	
    /**
	 * Float Number Validation
	 */
	public static boolean isFloatNumber(String value) {
		try {
			Float.parseFloat(value);
			return true;
		} catch (Exception e) {}
		
		return false;
	}
	
    /**
	 * Integer Number Validation
	 */
	public static boolean isIntNumber(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (Exception e) {}
		
		return false;
	}
	
    /**
	 * 날짜/시간 문자열을 구분자로 구분
	 */
	public static String delimitDateStr(String value) {
		return delimitDateStr(value, "-");
	}
	
    /**
	 * 날짜/시간 문자열을 구분자로 구분
	 */
	public static String delimitDateStr(String value, String delimiter) {
		if (value == null || !(value.length() == 6 || value.length() == 8)) {
			return value;
		}
		
		if (value.length() == 6) {
			return value.substring(0, 2) + delimiter + value.substring(2, 4) + delimiter
					+ value.substring(4);
		} else {
			return value.substring(0, 4) + delimiter + value.substring(4, 6) + delimiter
					+ value.substring(6);
		}
	}

    /**
	 * 쿠키값 획득
	 */
	public static String cookieValue(HttpServletRequest request, String key) {
		if (request == null || key == null || key.isEmpty()) {
			return null;
		} else if (request.getCookies() == null) {
			return null;
		}
		
		for (Cookie cookie : request.getCookies()) {
			if (key.equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		
		return null;
	}
	
	/**
	 * 쿠키 생성
	 */
	public static Cookie cookie(String key, String value) {
		if (key == null || key.isEmpty()) {
			return null;
		}
		
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(60 * 60 * 24 * 365);
		cookie.setPath("/");

		return cookie;
	}
	
	/**
	 * 로그인 사용자의 로그인 id
	 */
	public static int loginId(HttpSession session) {
		if (session == null) {
			return -1;
		}
		
		LoginUser loginUser = (LoginUser) session.getAttribute("loginUser");
		if (loginUser == null) {
			return -1;
		}

		return loginUser.getLoginId();
	}
	
	/**
	 * 로그인 사용자 id
	 */
	public static int loginUserId(HttpSession session) {
		if (session == null) {
			return -1;
		}
		
		LoginUser loginUser = (LoginUser) session.getAttribute("loginUser");
		if (loginUser == null) {
			return -1;
		}

		return loginUser.getId();
	}
	
	/**
	 * 로그인 여부 체크
	 */
	public static boolean isLoginUser(HttpSession session) {
		if (session == null) {
			return false;
		}
		
		LoginUser loginUser = (LoginUser) session.getAttribute("loginUser");
		if (loginUser == null) {
			return false;
		}

		return true;
	}
	
	/**
	 * Kendo의 언어 국가 코드
	 */
	public static String kendoLangCountryCode(Locale locale) {
		if (locale == null) {
			return "en-US";
		}
		
		String langCode = locale.getLanguage();
		
		if (langCode == null || langCode.isEmpty()) {
			return "en-US";
		}
		
		String countryCode = locale.getCountry();
		
		if (countryCode == null || countryCode.isEmpty()) {
			return langCode.replace('_', '-');
		} else {
			return (langCode + "-" + countryCode).replace('_', '-');
		}
	}
	
	/**
	 * 엑셀 엑스포트 시 언어별 기본 폰트 이름
	 */
	public static String excelDefaultFontName(Locale locale) {
		String langCode = kendoLangCountryCode(locale);
		
		if (isNotValid(langCode)) {
			return "Segoe UI";
		} else if (langCode.startsWith("ko")) {
			return "맑은 고딕";
		} else if (langCode.startsWith("ja")) {
			return "Meiryo UI";
		}
		
		return "Segoe UI";
	}
	
	/**
	 * http header의 User-Agent를 분석한 개체 획득
	 */
	public static eu.bitwalker.useragentutils.UserAgent getUserAgent(HttpServletRequest req) {
		if (req == null) {
			return null;
		} else {
			return eu.bitwalker.useragentutils.UserAgent.parseUserAgentString(
					req.getHeader("User-Agent"));
		}
	}
	
	/**
	 * 모바일, 혹은 태블릿 등이 아닌 일반 PC에서의 접근 여부 획득
	 */
	public static boolean isFromComputer(HttpServletRequest req) {
		eu.bitwalker.useragentutils.UserAgent userAgent = getUserAgent(req);
		
		if (userAgent == null || userAgent.getOperatingSystem() == null) {
			return false;
		} else {
			String device = userAgent.getOperatingSystem().getDeviceType().getName();
			
			return isValid(device) && device.equals("Computer");
		}
	}
	
	/**
	 * 요청 디바이스가 일반 PC일 경우에만,다중 행 선택 설정
	 */
	public static void setMultiSelectableIfFromComputer(Model model, 
			HttpServletRequest request) {
		if (model != null && request != null) {
			model.addAttribute("value_gridSelectable", isFromComputer(request) ? "multiple" : "row");
		}
	}
	
	/**
	 * 간단한 날짜 문자열 생성
	 */
	public static String toDateString(Date date) {
		if (date == null) {
			date = new Date();
		}
		
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}
	
	/**
	 * 타임존을 무시한 간단한 날짜 문자열 생성
	 */
	public static String toSimpleString(Date date) {
		return toSimpleString(date, true);
	}
	
	/**
	 * 타임존을 무시한 간단한 날짜 문자열 생성
	 */
	public static String toSimpleString(Date date, boolean isHypenDelimitered) {
		if (date == null) {
			return "";
		}
		
		return new SimpleDateFormat(isHypenDelimitered ?
				"yyyy-MM-dd HH:mm:ss" : "yyyy/MM/dd HH:mm:ss").format(date);
	}
	
	/**
	 * 타임존을 무시한 간단한 날짜 문자열 생성
	 */
	public static String toSimpleString(Date date, String format) {
		if (date == null || isNotValid(format)) {
			return "";
		}
		
		return new SimpleDateFormat(format).format(date);
	}
	
	/**
	 * 날짜값 파싱
	 */
	public static Date parseDate(String dateStr) {
		if (Util.isNotValid(dateStr) || !(dateStr.length() == 6 || dateStr.length() == 8 || dateStr.length() == 10
				|| dateStr.length() == 14 || dateStr.length() == 12)) {
			return null;
		}
		
		SimpleDateFormat df = null;
		
		if (dateStr.length() == 10) {
			df = new SimpleDateFormat("yyyy-MM-dd");
		} else if (dateStr.length() == 14) {
			df = new SimpleDateFormat("yyyyMMddHHmmss");
		} else if (dateStr.length() == 12) {
			df = new SimpleDateFormat("yyyyMMddHHmm");
		} else if (dateStr.length() == 6) {
			df = new SimpleDateFormat("yyMMdd");
		} else {
			df = new SimpleDateFormat("yyyyMMdd");
		}
		
		try {
			return df.parse(dateStr);
		} catch (Exception e) { }
		
		return null;
	}
	
	/**
	 * Zulu 타임존 날짜값 파싱
	 */
	public static Date parseZuluTime(String dateStr) {
		if (dateStr == null || dateStr.isEmpty()) {
			return null;
		}
		
		SimpleDateFormat df = null;
		
		if (dateStr.length() == 10) {
			df = new SimpleDateFormat("yyyy-MM-dd");
		} else {
			df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
			df.setTimeZone(TimeZone.getTimeZone("Zulu"));
		}
		
		try {
			return df.parse(dateStr);
		} catch (Exception e) { }
		
		return null;
	}
	
	/**
	 * 전달된 Date의 최고 시간(23:59:59.000)으로 설정
	 */
	public static Date setMaxTimeOfDate(Date date) {
		if (date == null) {
			return date;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		// jason:hibmysqlmillisecond: hibernate-mysql 밀리초 저장 오류 개선(2015/01/19)
		// hibernate mysql에서 밀리초 저장이 안되어 부정확한 날짜가 저장되는 문제를 해결
		// 수정 전: 12/25 23:59:59.999 -> 12/26 00:00:00.000
		// 수정 후: 12/25 23:59:59.000 -> 12/25 23:59:59.000
		//cal.set(Calendar.MILLISECOND, 999);
		//-
		
		return cal.getTime();
	}
	
	/**
	 * 전달된 Date에서 시간 이하 0, 날짜는 1로 설정
	 */
	public static Date removeDayOfDate(Date date) {
		if (date == null) {
			return date;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTime();
	}
	
	/**
	 * 전달된 Date에서 시간 이하 0으로 설정
	 */
	public static Date removeTimeOfDate(Date date) {
		if (date == null) {
			return date;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTime();
	}
	
	/**
	 * 전달된 Date에서 분 시간 미만 0으로 설정
	 */
	public static Date removeSecTimeOfDate(Date date) {
		if (date == null) {
			return date;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTime();
	}
	
	/**
	 * 임의 키 문자열 생성
	 */
	public static String getRandomKey(int length) {
		if (length < 1) {
			length = 5;
		}
		
		char[] _tmpseed = "ABCDEFGHJKLMNPQRSTUVWXYZ1234567890".toCharArray();
		
		return random(length, _tmpseed);
	}
	
	/**
	 * 임의 인증키 생성
	 */
	public static String randomAuthKey(int i, char[] ac, int key) {
		StringBuffer stringbuffer = new StringBuffer(i);
		Random random1 = new Random(System.currentTimeMillis() + key);
		int j = ac.length;
		
		for (int k = 0; k < i; k ++) {
			stringbuffer.append(ac[(int) (random1.nextFloat() * (float) j)]);
		}
		
		return stringbuffer.toString();
	}
	
	/**
	 * 특정 문자로 한정된 임의 인증키 생성
	 */
	public static String getRandomAuthKey(int length, int key) {
		if (length < 1) {
			length = 5;
		}

		char[] _tmpseed = "ABCDEFGHJKLMNPQRSTUVWXYZ1234567890".toCharArray();
		
		return randomAuthKey(length, _tmpseed, key);
	}
	
	/**
	 * 임의 Salt 문자열 생성
	 */
	public static String getRandomSalt() {
		int    i = 22;
		char[] _tmpseed =
				"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
				.toCharArray();

		return random(i, _tmpseed);
	}
	
	/**
	 * 임의 문자열 생성
	 */
	public static String random(int i, char[] ac) {
		StringBuffer stringbuffer = new StringBuffer(i);
		Random       random1      = new Random(System.currentTimeMillis());
		int          j            = ac.length;
		
		for (int k = 0; k < i; k++) {
			stringbuffer.append(ac[(int) (random1.nextFloat() * (float) j)]);
		}
		
		return stringbuffer.toString();
	}
	
	/**
	 * 문자열 암호화
	 */
	public static String encrypt(String password, String salt) {
		// 기존 MD5에서 보안성이 더 좋은 SHA-256으로 변경
		//return encrypt(password, salt, "MD5");
		return encrypt(password, salt, "SHA-256");
	}
	
	/**
	 * 문자열 암호화
	 */
	public static String encrypt(String password, String salt, String type) {
		if (password == null || password.isEmpty()) {
			return password;
		}
		
		try {
			StringHash stringhash = new StringHash(type);
			
			return stringhash.doStringHash(password, salt);
		} catch (NoSuchAlgorithmException nosuchalgorithmexception) {
			logger.error("encrypt", nosuchalgorithmexception);
		}
		
		return null;
	}
	
	/**
	 * 사용자의 패스워드 동일 여부 체크(유효 일자 체크는 제외)
	 */
	public static boolean isSameUserPassword(String testPwd, String salt, String dbPwd) {
		try {
			String encryptType = "SHA-256";
			if (isValid(dbPwd) && dbPwd.length() != 44) {
				encryptType = "MD5";
			}
			
			StringHash stringhash = new StringHash(encryptType);
			
			return stringhash.doStringHashVerify(testPwd, salt, dbPwd);
		} catch (NoSuchAlgorithmException nosuchalgorithmexception) {
			logger.error("isSameUserPassword", nosuchalgorithmexception);
		}
		
		return false;
	}
	
	/**
	 * 문자열 암호화를 위한 StringHash Class
	 */
	static class StringHash {
		private MessageDigest mdAlgorithm;
		
		public StringHash() throws NoSuchAlgorithmException {
			mdAlgorithm = MessageDigest.getInstance("SHA-1");
		}
		
		public StringHash(String s) throws NoSuchAlgorithmException {
			mdAlgorithm = MessageDigest.getInstance(s);
		}
		
		private String doBase64(byte[] abyte0) {
			StringBuffer stringbuffer = new StringBuffer();
			Base64OutputStream base64outputstream = new Base64OutputStream(stringbuffer);
			ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(abyte0);
			
			int i;
			
			try {
				while ((i = bytearrayinputstream.read()) != -1) {
						base64outputstream.write(i);
				}
				
				base64outputstream.flush();
				base64outputstream.close();
			} catch (IOException ioe) {
				return null;
			}
			
			return stringbuffer.toString();
		}
		
		private byte[] doHash(String s) {
			StringReader stringreader = new StringReader(s);
			ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
			
			int i;
			
			try {
				while ((i = stringreader.read()) != -1) {
					bytearrayoutputstream.write(i);
				}
			} catch (IOException ioexception) {
				if (stringreader != null) {
					stringreader.close();
				}
				
				return null;
			}
			
			mdAlgorithm.reset();
			mdAlgorithm.update(bytearrayoutputstream.toByteArray());
			
			return mdAlgorithm.digest();
		}
		
		public String doStringHash(String s) {
			return doBase64(doHash(s));
		}
		
		public String doStringHash(String s, String s1) {
			return doBase64(doHash(s + s1));
		}
		
		public boolean doStringHashVerify(String s, String s1) {
			String s2 = doStringHash(s);
			
			return s2.equals(s1);
		}
		
		public boolean doStringHashVerify(String s, String s1, String s2) {
			String s3 = doStringHash(s + s1);
			
			return s3.equals(s2);
		}
	}
	
	/**
	 * 문자열 암호화를 위한 Base64OutputStream Class
	 */
	static class Base64OutputStream extends OutputStream {
	    private static char[] toBase64 =
	        {
	            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
	            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b',
	            'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
	            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3',
	            '4', '5', '6', '7', '8', '9', '+', '/'
	        };

	    private int iColumn;
	    private int iCursor;
	    private int[] iaCursorStore;
	    private StringBuffer sbInternalBuffer;
	    
	    public Base64OutputStream(StringBuffer stringbuffer) {
	    	iColumn = 0;
	    	iCursor = 0;
	    	iaCursorStore = new int[3];
	    	sbInternalBuffer = stringbuffer;
	    }
	    
	    public void flush() {
	    	if (iCursor == 1) {
	    		sbInternalBuffer.append(toBase64[(iaCursorStore[0] & 0xfc) >> 2]);
	    		sbInternalBuffer.append(toBase64[(iaCursorStore[0] & 3) << 4]);
	    		sbInternalBuffer.append('=');
	    		sbInternalBuffer.append('=');
	    	} else if (iCursor == 2) {
	    		sbInternalBuffer.append(toBase64[(iaCursorStore[0] & 0xfc) >> 2]);
	    		sbInternalBuffer.append(toBase64[((iaCursorStore[0] & 3) << 4) | ((iaCursorStore[1] & 0xf0) >> 4)]);
	    		sbInternalBuffer.append(toBase64[(iaCursorStore[1] & 0xf) << 2]);
	    		sbInternalBuffer.append('=');
	    	}
	    }
	    
	    public void write(int i) throws IOException {
	    	iaCursorStore[iCursor] = i;
	    	iCursor++;
	    	
	    	if (iCursor == 3) {
	    		sbInternalBuffer.append(toBase64[(iaCursorStore[0] & 0xfc) >> 2]);
	    		sbInternalBuffer.append(toBase64[((iaCursorStore[0] & 3) << 4) | ((iaCursorStore[1] & 0xf0) >> 4)]);
	    		sbInternalBuffer.append(toBase64[((iaCursorStore[1] & 0xf) << 2) | ((iaCursorStore[2] & 0xc0) >> 6)]);
	    		sbInternalBuffer.append(toBase64[iaCursorStore[2] & 0x3f]);
	    		
	    		iColumn += 4;
	    		iCursor = 0;
	    		
	    		if (iColumn >= 76) {
	    			sbInternalBuffer.append('\n');
	    			iColumn = 0;
	    		}
	    	}
	    }
	}
	
	/**
	 * 지정된 디렉토리 및 하위 디렉토리 삭제
	 */
	public static void deleteDirectory(String dirPath) {
		try {
			File dir = new File(dirPath);
			if (dir.exists()) {
				Path root = Paths.get(dirPath);
				Files.walkFileTree(root, new FileVisitor<Object>() {
					@Override
					public FileVisitResult preVisitDirectory(Object dir,
							BasicFileAttributes attrs) throws IOException {
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFile(Object file,
							BasicFileAttributes attrs) throws IOException {
						Files.delete((Path) file);
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFileFailed(Object file,
							IOException exc) throws IOException {
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult postVisitDirectory(Object dir,
							IOException exc) throws IOException {
						Files.delete((Path) dir);
						return FileVisitResult.CONTINUE;
					}
				});
			}
		} catch (Exception e) {
			logger.error("deleteDirectory", e);
		}
	}
	
	/**
	 * 지정된 디렉토리에 있는 파일 크기 합 획득
	 */
	public static long getDirectoryFileSizeSum(String dirPath, boolean isRecursiveMode) {
		long sum = 0;
		
		File dir = new File(dirPath);
		if (dir.exists()) {
			try {
				Path root = Paths.get(dirPath);
				CalculateFileSizeVisitor visitor = new CalculateFileSizeVisitor();
				
				if (!isRecursiveMode) {
					visitor.setOnlyThisPath(root);
				}
				
				Files.walkFileTree(root, visitor);
				
				sum = visitor.getSizeSum();
			} catch (Exception ex) { }
		}
		
		return sum;
	}
	
	/**
	 * 지정된 디렉토리에 있는 파일/디렉토리 수 획득
	 */
	public static long getDirectoryFileCount(String dirPath, boolean isRecursiveMode, 
			boolean isFileMode) {
		long cnt = 0;
		
		File dir = new File(dirPath);
		if (dir.exists()) {
			try {
				Path root = Paths.get(dirPath);
				CalculateFileSizeVisitor visitor = new CalculateFileSizeVisitor();
				
				if (!isRecursiveMode) {
					visitor.setOnlyThisPath(root);
				}
				
				Files.walkFileTree(root, visitor);
				
				cnt = isFileMode ? visitor.getNumFiles() : visitor.getNumDirs() - 1;
			} catch (Exception ex) { }
		}
		
		return cnt;
	}
	
	/**
	 * 유효한 루트 디렉토리 획득
	 */
	public static String getValidRootDir(String rootDir) {
		if (rootDir == null || rootDir.isEmpty()) {
			// 최악의 경우
			return "C:/";
		}
		
		String tmp = rootDir.replace("\\", "/");
		
		return tmp.endsWith("/") ? tmp : tmp + "/";
	}
	
	/**
	 * 물리적인 루트 디렉토리 획득
	 */
	public static String getPhysicalRoot(String ukid) {
		if (ukid == null || ukid.isEmpty()) {
			return null;
		}
		
		String rootDirPath = getFileProperty("dir.rootPath");
		
		if (ukid.equals("Logo")) {
            return getValidRootDir(rootDirPath) + "logos";
		}

        return null;
	}
	
	/**
	 * 지정된 디렉토리 존재 확인(없으면 생성)
	 */
	public static boolean checkDirectory(String dirName) {
		if (dirName == null || dirName.isEmpty()) {
			return false;
		}
		
		File newDir = new File(dirName);
		if (!newDir.exists()) {
			newDir.mkdirs();
		}
		
		return true;
	}
	
	/**
	 * 지정된 디렉토리 존재 확인(없으면 생성)
	 */
	public static boolean checkParentDirectory(String filename) {
		
		if (isNotValid(filename)) {
			return false;
		}
		
		return checkDirectory(new File(filename).getParent());
	}

	/**
	 * 초기 디렉토리 확인(없으면 생성)
	 */
	public static boolean checkInitDirectory() {
		String rootDirPath = getFileProperty("dir.rootPath");
		if (rootDirPath == null || rootDirPath.isEmpty()) {
			logger.error("checkSiteDirectory: No Root Directory Path");
			return false;
		}
		
		String ftpDirName = getFileProperty("dir.ftp");
		if (ftpDirName == null || ftpDirName.isEmpty()) {
			logger.error("checkSiteDirectory: No FTP Directory Name");
			return false;
		}
		
		if (Util.checkDirectory(getPhysicalRoot("Logo"))) {
			return true;
		}
		
		return false;
	}

	/**
	 * 지정된 사이트 단축명으로 사이트 디렉토리 확인(없으면 생성)
	 */
	public static boolean checkSiteDirectory1(String siteShortName) {
		if (siteShortName == null || siteShortName.isEmpty()) {
			return false;
		}

		String rootDirPath = getFileProperty("dir.rootPath");
		if (rootDirPath == null || rootDirPath.isEmpty()) {
			logger.error("checkSiteDirectory: No Root Directory Path");
			return false;
		}
		
		String ftpDirName = getFileProperty("dir.ftp");
		if (ftpDirName == null || ftpDirName.isEmpty()) {
			logger.error("checkSiteDirectory: No FTP Directory Name");
			return false;
		}

		//
		// 기본 디렉토리 구조
		//
		// [Root]/logos                 Logo
		//
		
		if (checkInitDirectory()) {
			return true;
		}
		
		return false;
	}

	/**
	 * 정해진 Path, 파일의 내용을 문자열로 획득
	 */
	public static String getFileContent(String pathFilename) {
		if (pathFilename == null || pathFilename.isEmpty()) {
			return "";
		}
		
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(
					new FileInputStream(pathFilename), "UTF-8"));
			
            StringBuilder content = new StringBuilder();
            String line = input.readLine();
            
            while (line != null) {
                content.append(line + "\r\n");
                line = input.readLine();
            }
            
            input.close();
            
            return content.toString();
		} catch (Exception e) {
			logger.error("getFileContent", e);
		}
		
		return "";
	}
	
	/**
	 * 정해진 Path, 파일의 직하위 디렉토리를 포함한 디렉토리 및 파일을 획득
	 */
	public static List<File> listOneDepth(String pathFilename) {
		ArrayList<File> list = new ArrayList<File>();
		
		File dir = new File(pathFilename);
		if (dir.exists()) {
			if (dir.isDirectory()) {
				File[] listFiles = dir.listFiles();
				if (listFiles != null && listFiles.length > 0) {
					Arrays.sort(listFiles);
					for (File file : listFiles) {
						list.add(file);
					}
				}
			} else if (dir.isFile()) {
				list.add(dir);
			}
		}
		
		return list;
	}
	
	/**
	 * long 형식의 날짜 정보에 해당하는 Date 획득
	 */
	public static Date getDate(long timeValue) {
		Date date = new Date();
		date.setTime(timeValue);
		
		return date;
	}
	
	/**
	 * 2.1.32.1 과 같은 형태의 Version 문자열에 대응하는 int 값 반환
	 */
	public static int getVersionAsInt(String version) {
		int ret = -1;
		
		if (isNotValid(version) || version.indexOf(".") == -1) {
			return ret;
		}
		
		List<String> numbers = tokenizeValidStr(version, ".");
		if (numbers.size() < 4) {
			return ret;
		}
		
		try {
			ret = Integer.parseInt(numbers.get(0)) * 10000000 +
					Integer.parseInt(numbers.get(1)) * 1000000 +
					Integer.parseInt(numbers.get(2)) * 1000 +
					Integer.parseInt(numbers.get(3));
		} catch (Exception e) {}
		
		return ret;
	}

	/**
	 * 압축 파일 풀기
	 */
	public static void unzip(InputStream is, File destDir, String charsetName) 
			throws IOException {
		ZipArchiveInputStream zis = new ZipArchiveInputStream(is, charsetName, false);
		ZipArchiveEntry entry ;
		String name ;
		
		File target ;
		File targetParent;
		
		int nWritten = 0;
		BufferedOutputStream bos ;
		byte [] buf = new byte[1024 * 8];
		
		if (! destDir.exists()) {
			destDir.mkdirs();
		}
		
		try {
			while ((entry = zis.getNextZipEntry()) != null) {
				name = entry.getName();
				
				target = new File(destDir, name);
				targetParent = new File(target.getParent());
				
				if (entry.isDirectory()) {
					target.mkdirs();
				} else {
					if (! targetParent.exists()) {
						targetParent.mkdirs();
					}
					
					target.createNewFile();
					bos = new BufferedOutputStream(new FileOutputStream(target));
					
					try {
						while ((nWritten = zis.read(buf)) >= 0) {
							bos.write(buf, 0, nWritten);
						}
					} finally {
						bos.close();
					}
				}
			}
		} finally {
			zis.close();
		}
	}

	/**
	 * 압축 파일 풀기
	 */
	public static boolean unzipFile(String zipPathFileName, String dstFolder) {
		if (Util.isNotValid(zipPathFileName) || Util.isNotValid(dstFolder))
		{
			return false;
		}
		
		try {
			File zippedFile = new File(zipPathFileName);
			
			unzip(new FileInputStream(zippedFile), new File(dstFolder), 
					Charset.defaultCharset().name());
			
			return true;
		} catch (Exception e) {
			logger.error("unzipFile", e);
		}
		
		return false;
	}

	/**
	 * 세션에 저장되어 있는 사이트 id 획득
	 */
	public static int getSessionSiteId(HttpSession session) {
		int siteId = -1;
		
		if (session != null) {
			String siteIdStr = (String)session.getAttribute("currentSiteId");
			siteId = Util.parseInt(siteIdStr, -1);
		}
		
		return siteId;
	}

	/**
	 * 세션에 저장되어 있는 매체 id 획득
	 */
	public static int getSessionMediumId(HttpSession session) {
		int mediumId = -1;
		
		if (session != null) {
			mediumId = Util.parseInt((String)session.getAttribute("currMediumId"), -1);
		}
		
		return mediumId;
	}

	/**
	 * 자릿수에 따른 선행 0을 표시하는 문자열 획득
	 */
	public static String getZeroFormattedNumber(int seq, int digits) {
		if (digits < 1) {
			return "";
		}
		
		int maxValue = (int)(Math.pow(10, digits)) - 1;
		
		if (seq < 0 || seq > maxValue) {
			return "";
		}
		
		return String.format("%0" + digits + "d", seq);
	}

	/**
	 * 두 개 날짜의 동일 여부 획득
	 */
	public static boolean isSameDate(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			return false;
		}
		
		return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
	}

	/**
	 * 오늘 날짜 여부 획득
	 */
	public static boolean isToday(Date date) {
		return isSameDate(date, Calendar.getInstance().getTime());
	}

	/**
	 * 오늘 날짜 여부 획득
	 */
	public static boolean isToday(Calendar cal) {
		return isSameDate(cal, Calendar.getInstance());
	}

	/**
	 * 사용자의 모든 권한 획득
	 */
	public static List<String> getAllPrivsOfThisUser(int userId) {
		
		ArrayList<String> userPrivKeys = new ArrayList<String>();

		List<FndUserPriv> userPrivList = sFndService.getUserPrivListByUserId(userId);
		
		for (FndUserPriv userPriv : userPrivList) {
			FndPriv priv = userPriv.getPriv();
			
			if (!userPrivKeys.contains(priv.getUkid())) {
				userPrivKeys.add(priv.getUkid());
			}
		}
		
		return userPrivKeys;
	}
	
	/**
	 * 사용자의 지정 권한 소유 여부 획득
	 */
	public static boolean hasThisPriv(HttpSession session, String... a) {
		if (session == null) {
			return false;
		}
		
		return hasThisPriv(loginUserId(session), a);
	}
	
	/**
	 * 사용자의 지정 권한 소유 여부 획득
	 */
	public static boolean hasThisPriv(int userId, String... a) {
		List<String> testPrivs = Arrays.asList(a);
		
		if (testPrivs.size() < 1) {
			return false;
		}
		
		List<String> privs = getAllPrivsOfThisUser(userId);
		for(String testPriv : testPrivs) {
			if (privs.contains(testPriv)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 커스텀/URL 기반 사이트 로고 사용 설정 여부에 따른 로고 파일 경로 및 파일 획득
	 */
	public static String getLogoPathFile(String code, String serverName) {
		String ret = "";
		if (Util.isNotValid(code)) {
			return ret;
		} else if (!code.equals("login") && !code.equals("top")) {
			return ret;
		} else {
			ret = "/resources/shared/images/logo/logo_" + code + ".png";
		}
		
		if (ret.startsWith("/resources") && Util.getBooleanFileProperty("custom.logo.displayed", false)) {
			String tmp = "/logo_" + code + ".png";
			
			if (new File(Util.getPhysicalRoot("Logo") + tmp).exists()) {
				ret = "/logo" + tmp;
			}
		}
		
		/*
		String logoDomainName = Util.getFileProperty("logo.domain.name");
		if (isValid(logoDomainName) && isValid(serverName) && serverName.toLowerCase().endsWith(logoDomainName)) {
			serverName = serverName.toLowerCase();
			if (serverName.endsWith("." + logoDomainName)) {
				Site reqSite = sSiteService.getSite(serverName.replace("." + logoDomainName, ""));
				if (reqSite != null) {
					String tmp = "/logo_" + code + "." + reqSite.getShortName() + ".png";
					
					if (new File(Util.getPhysicalRoot("Logo") + tmp).exists()) {
						ret = "/logo" + tmp;
					}
				}
			}
		}
		*/
		
		return ret;
	}
	
	/**
	 * 두 날짜 간의 Timespan을 배열로 획득
	 */
	public static long[] getDiffTimespanArr(Date dt1) {
		
		return getDiffTimespanArr(dt1, new Date());
	}
	
	/**
	 * 두 날짜 간의 Timespan을 배열로 획득
	 */
	public static long[] getDiffTimespanArr(Date dt1, Date dt2) {
		return getDiffTimespanArr((dt1.getTime() - dt2.getTime()) / 1000);
	}

	/**
	 * 초 단위 시간을 배열로 획득
	 */
	public static long[] getDiffTimespanArr(long diffInSeconds) {
		long sign = 1;
		long yrs = 0;
		long mon = 0;
		long day = 0;
		long hrs = 0;
		long min = 0;
		long sec = 0;
		
		if (diffInSeconds == 0) {
			sign = 0;
		} else if (diffInSeconds < 0) {
			diffInSeconds *= -1;
			sign = -1;
		}
		
		sec = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
	    min = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
	    hrs = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
	    day = (diffInSeconds = (diffInSeconds / 24)) >= 30 ? diffInSeconds % 30 : diffInSeconds;
	    mon = (diffInSeconds = (diffInSeconds / 30)) >= 12 ? diffInSeconds % 12 : diffInSeconds;
	    yrs = (diffInSeconds = (diffInSeconds / 12));
		
		return new long[] { sign, yrs, mon, day, hrs, min, sec };
	}

	/**
	 * Timespan에 대해 영어식 표현 문자열을 획득
	 */
	public static String getEngTimespan(long[] diffs)
	{
		return getEngTimespan(diffs, true);
	}
	
	/**
	 * Timespan에 대해 영어식 표현 문자열을 획득
	 */
	public static String getEngTimespan(long[] diffs, boolean signAdded)
	{
		if (diffs == null || diffs.length != 7) {
			return "";
		}

		if (diffs[0] != 1 && diffs[0] != -1) {
			return "";
		}
		
		try {
			StringBuffer sb = new StringBuffer();

			if (signAdded) {
				if (diffs[0] == 1) {
					sb.append("+");
				} else {
					sb.append("-");
				}
			}
			
			if (diffs[1] > 0) {
				sb.append(diffs[1] + "y ");
			}
			
			if (diffs[2] > 0) {
				sb.append(diffs[2] + "mon ");
			}
			
			if (diffs[3] > 0) {
				sb.append(diffs[3] + "d ");
			}
			
			if (diffs[4] > 0) {
				sb.append(diffs[4] + "h ");
			}
			
			if (diffs[5] > 0) {
				sb.append(diffs[5] + "min ");
			}
			
			if (diffs[6] > 0) {
				sb.append(diffs[6] + "s ");
			}
			
			String tmp = sb.toString();
			
			return tmp.substring(0, tmp.length() - 1);
			
		} catch (Exception ex) {}
		
		return "";
	}
	
	/**
	 * 압축 파일 풀기
	 */
	public static boolean createZipFile(String zipPathFileName, String[] folderName,
			String[] srcFileFullPathName, String[] dstFileZipPathName) {
		if (Util.isNotValid(zipPathFileName)) {
			return false;
		}

		// 파일읽기를 위한 버퍼 생성
		int bufferSize = 2048;
		byte[] buf = new byte[bufferSize];

		try {
		    ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
			    new FileOutputStream(zipPathFileName)));

		    // 폴더 생성
		    if ((folderName != null) && (folderName.length > 0)) {
			for (int i = 0; i < folderName.length; i++) {
			    out.putNextEntry(new ZipEntry(folderName[i] + "/"));
			}
		    }

		    // 파일 생성
		    if (srcFileFullPathName != null && srcFileFullPathName.length > 0 &&
		    		dstFileZipPathName != null && srcFileFullPathName.length <= dstFileZipPathName.length) {
				FileInputStream fs = null;
				BufferedInputStream in = null;
	
				for (int i = 0; i < srcFileFullPathName.length; i++) {
				    fs = new FileInputStream(srcFileFullPathName[i]);
				    in = new BufferedInputStream(fs, bufferSize);
				    out.putNextEntry(new ZipEntry(dstFileZipPathName[i]));
	
				    // 대상 파일을 ZIP 파일로 전송
				    int readLength;
	
				    while ((readLength = in.read(buf, 0, bufferSize)) > 0) {
				    	out.write(buf, 0, readLength);
				    }
	
				    in.close();
				}

				// 엔트리 완료
				out.closeEntry();
		    }

		    // ZIP 파일 완료
		    out.close();
		    
		    return true;
		} catch (Exception e) {
			logger.error("createZipFile", e);
		}
		
		return false;
	}
	
	/**
	 * 파일의 확장자를 제거한 결과 획득
	 */
	public static String removeFileExt(String filename) {
		if (Util.isNotValid(filename) || filename.indexOf(".") == -1) {
			return filename;
		} else {
			return filename.substring(0, filename.lastIndexOf('.'));
		}
	}

	/**
	 * 파일의 확장자를 획득
	 */
	public static String getFileExt(String filename) {
		int i = filename.lastIndexOf('.');
		if (i > 0) {
		    return filename.substring(i + 1);
		} else {
			return "";
		}
	}

	/**
	 * 문자열 후미의 특정 문자 제거 결과 획득
	 */
	public static String removeTrailingChar(String str) {
		return removeTrailingChar(str, "|");
	}

	/**
	 * 문자열 후미의 특정 문자 제거 결과 획득
	 */
	public static String removeTrailingChar(String str, String chr) {
		if (isNotValid(str)) {
			return str;
		}
		
		if (str.endsWith(chr)) {
			return str.substring(0, str.length() - chr.length());
		} else {
			return str;
		}
	}

	/**
	 * 문자열 후미의 날짜 정보 제거 결과 획득
	 */
	public static String removeTrailingDateInfo(String str) {
		if (isNotValid(str)) {
			return str;
		}
		
		// 날짜 정보는 다음과 같은 규칙이어야 함
		//   1) 날짜의 표현은 yy.MM.dd 혹은 yyyy.MM.dd
		//   2) 날짜 정보는 반드시 문자열의 끝에 위치
		//   3) 구분자로 "_" 혹은 날짜 정보를 괄호 안에 넣은 형태
		//   예) 전용스케줄(22.04.05), 홍대상행LCD1_2022.05.24
		
		if (str.endsWith(")") && str.lastIndexOf("(") > -1) {
			String dStr = str.substring(str.lastIndexOf("(") + 1, str.lastIndexOf(")"));
			if (Util.isValid(dStr) && (dStr.length() == 8 || dStr.length() == 10)) {
				dStr = dStr.replace(".", "");
				
				String regEx = "[0-9]+";
				if (dStr.matches(regEx)) {
					return str.substring(0, str.lastIndexOf("("));
				}
			}
		} else if (str.lastIndexOf("_") > -1 && !str.endsWith("_")) {
			String dStr = str.substring(str.lastIndexOf("_") + 1);
			if (Util.isValid(dStr) && (dStr.length() == 8 || dStr.length() == 10)) {
				dStr = dStr.replace(".", "");
				
				String regEx = "[0-9]+";
				if (dStr.matches(regEx)) {
					return str.substring(0, str.lastIndexOf("_"));
				}
			}
		}
		
		return str;
	}

	/**
	 * 문자열 후미의 날짜 정보에 맞는 날짜 획득
	 */
	public static Date getDateInfoFromString(String str) {
		if (isNotValid(str)) {
			return null;
		}
		
		// 날짜 정보는 다음과 같은 규칙이어야 함
		//   1) 날짜의 표현은 yy.MM.dd 혹은 yyyy.MM.dd
		//   2) 날짜 정보는 반드시 문자열의 끝에 위치
		//   3) 구분자로 "_" 혹은 날짜 정보를 괄호 안에 넣은 형태
		//   예) 전용스케줄(22.04.05), 홍대상행LCD1_2022.05.24
		
		if (str.endsWith(")") && str.lastIndexOf("(") > -1) {
			String dStr = str.substring(str.lastIndexOf("(") + 1, str.lastIndexOf(")"));
			if (Util.isValid(dStr) && (dStr.length() == 8 || dStr.length() == 10)) {
				return Util.parseDate(dStr.replace(".", ""));
			}
		} else if (str.lastIndexOf("_") > -1 && !str.endsWith("_")) {
			String dStr = str.substring(str.lastIndexOf("_") + 1);
			if (Util.isValid(dStr) && (dStr.length() == 8 || dStr.length() == 10)) {
				return Util.parseDate(dStr.replace(".", ""));
			}
		}
		
		return null;
	}

	
    /**
	 * 개체를 XML 형식으로 반환
	 */
	public static String getObjectToXml(Class<?> clazz, Object obj, boolean formattedMode) {
		String ret = "";
		StringWriter sw = null;
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Marshaller m = context.createMarshaller();

            if (formattedMode) {
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            }

            sw = new StringWriter();
            m.marshal(obj, sw);
            ret = sw.toString();
        } catch (Exception e) {
			logger.error("getObjectToXml", e);
        } finally {
    		if (sw != null) {
    			try {
    				sw.close();
    			} catch (IOException ex) {
        			logger.error("getObjectToXml", ex);
    			}
    		}
        }
        
        return ret;
	}
	
    /**
	 * 개체를 XML 형식으로 출력
	 */
	public static void toXml(HttpServletResponse response, Class<?> clazz, 
			Object obj) {
		toXml(response, clazz, obj, true);
	}
	
    /**
	 * 개체를 XML 형식으로 출력
	 */
	public static void toXml(HttpServletResponse response, Class<?> clazz, 
			Object obj, boolean formattedMode) {
        response.setHeader("Cache-Control", "no-store");              // HTTP 1.1
        response.setHeader("Pragma", "no-cache, must-revalidate");    // HTTP 1.0
        response.setContentType("text/xml;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Marshaller m = context.createMarshaller();

            if (formattedMode) {
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            }

            m.marshal(obj, response.getWriter());
        } catch (Exception e) {
			logger.error("toXml", e);
        }
	}
	
    /**
	 * 개체를 json 형식으로 반환
	 */
	public static String getObjectToJson(Object obj, boolean formattedMode) {
		String ret = "";
    	try {
        	ObjectMapper mapper = new CustomObjectMapper();
        	
        	if (formattedMode) {
            	ret = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        	} else {
            	ret = mapper.writeValueAsString(obj);
        	}
    	} catch (Exception e) {
			logger.error("getObjectToJson", e);
    	}
    	
    	return ret;
	}
	
    /**
	 * 개체를 json 형식으로 반환
	 */
	public static void toJson(HttpServletResponse response, Object obj) {
		toJson(response, obj, true);
	}
	
    /**
	 * 개체를 json 형식으로 반환
	 */
	public static void toJson(HttpServletResponse response, Object obj, 
			boolean formattedMode) {
    	try {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(
          		  response.getOutputStream(), "UTF-8"));
            
            response.setHeader("Cache-Control", "no-store");              // HTTP 1.1
            response.setHeader("Pragma", "no-cache, must-revalidate");    // HTTP 1.0
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
    		
        	out.print(getObjectToJson(obj, formattedMode));
        	
        	out.close();
    	} catch (Exception e) {
			logger.error("toJson", e);
    	}
	}
	
    /**
	 * 서버로 문자열 스트림 전송
	 */
	public static int sendStreamToServer(String url, String data) {
		
		return sendStreamToServer(url, "application/json; charset=UTF-8", data);
	}
	
    /**
	 * 서버로 문자열 스트림 전송
	 */
	public static int sendStreamToServer(String url, String ctntType, String data) {
		if (Util.isNotValid(url) || Util.isNotValid(ctntType) || 
				Util.isNotValid(data)) {
			return 0;
		}
		
		OutputStreamWriter wr= null;
		try {
    		URL urlObj = new URL(url);
        	HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
			
        	// 서버 통신을 위한 필수 설정
        	conn.setDoOutput(true);
        	conn.setDoInput(true);

        	conn.setRequestProperty("Content-Type", ctntType);
        	conn.setRequestMethod("POST");
        	
           	wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            wr.write(data);
            wr.flush();
            
            return conn.getResponseCode();
    	} catch (Exception e) {
    		logger.error("sendStreamToServer", e);
    	} finally {
    		if (wr != null) {
    			try {
    				wr.close();
    			} catch (IOException ex) {
        			logger.error("sendStreamToServer", ex);
    			}
    		}
    	}
		
		return -2;
	}
	
    /**
	 * RSA Key Pair 획득
	 */
	public static KeyPair getKeyPairRSA() {
		KeyPair keyPair = null;

		try {
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(2048);       // RSA (1024, 2048)
			keyPair = generator.genKeyPair();
		} catch (Exception e) {
			keyPair = null;
			logger.error("getKeyPairRSA", e);
		}
		
		return keyPair;
	}
	
    /**
	 * RSA Public Key Spec 획득
	 */
	public static RSAPublicKeySpec getPublicKeySpecRSA(KeyPair keyPair) {
		RSAPublicKeySpec keySpec = null;
		
		if (keyPair != null) {
			try {
				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				
				keySpec = (RSAPublicKeySpec) keyFactory.getKeySpec(keyPair.getPublic(), RSAPublicKeySpec.class);
			} catch (Exception e) {
				keySpec = null;
				logger.error("getPublicKeySpecRSA", e);
			}
		}
		
		return keySpec;
	}
	
    /**
	 * RSA 복호화 문자열 획득
	 */
	public static String decryptRSA(PrivateKey privateKey, String encryptVal) {
		if (privateKey == null || isNotValid(encryptVal)) {
			return encryptVal;
		}
		
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			
			byte[] encryptedBytes = parseHexString(encryptVal);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			
			return new String(cipher.doFinal(encryptedBytes), "UTF-8");
		} catch (Exception e) {
			logger.error("decryptRSA", e);
		}
		
		return encryptVal;
	}
	
    /**
	 * RSA 암호화를 위한 컨트롤러 처리 부분
	 */
	public static void prepareKeyRSA(Model model, HttpSession session) {
		if (model != null && session != null && GlobalInfo.RSAKeyPair != null) {
        	RSAPublicKeySpec keySpec = Util.getPublicKeySpecRSA(GlobalInfo.RSAKeyPair);
        	if (keySpec != null) {
            	String keyModulus = keySpec.getModulus().toString(16);
    			String keyExponent = keySpec.getPublicExponent().toString(16);
    			
    			// model에는 jsp에서 사용될 키 관련 값을 저장
    			// session에는 암호화된 값을 풀 수 있는 개인키를 저장하여, 이후 java 코드에서 입력값을 확인
    			model.addAttribute("RSAKeyMod", keyModulus);
    			model.addAttribute("RSAKeyExp", keyExponent);
    			
    			if (!keyModulus.equals(GlobalInfo.RSAKeyMod)) {
    				GlobalInfo.RSAKeyMod = keyModulus;
    			}
    			
        		session.setAttribute("rsaPrivateKey", GlobalInfo.RSAKeyPair.getPrivate());
        		//-
        	}
    	}
	}
	
    /**
	 * 와일드카드 문자가 포함된 패턴 일치 체크
	 */
	public static boolean match(String text, String pattern) {
		return text.matches(pattern.replace("?", ".?").replace("*", ".*?"));
	}
	
    /**
	 * URL에 접속하여 응답 문자열을 반환
	 */
	public static String readResponseFromUrl(String url) {
		if (isNotValid(url)) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		try {
			URL serverUrl = new URL(url);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(serverUrl.openStream(), Charset.forName("UTF-8")));
			
			try {
				String inputLine;
		        while ((inputLine = in.readLine()) != null) {
		        	sb.append(inputLine);
		        }
			} finally {
				in.close();
			}
		} catch (Exception e) {
			logger.error("readResponseFromUrl", e);
		}
		
		return sb.toString();
	}
	
	/**
	 * HTML 문서의 언어 속성 설정을 위한 언어 코드
	 */
	public static String htmlLang(Locale locale) {
		if (locale == null) {
			return "en";
		}
		
		String langCode = locale.getLanguage();
		
		if (langCode == null || langCode.isEmpty()) {
			return "en";
		}
		
		return langCode.equals("ko") ? "ko" : "en";
	}
	
    /**
	 * 일자 기준 날짜 문자열 획득(Kendo Grid 용)
	 */
	public static String getSmartDate() {
		
		return getSmartDate("whoCreationDate", true);
	}
	
    /**
	 * 일자 기준 날짜 문자열 획득(Kendo Grid 용)
	 */
	public static String getSmartDate(String col) {
		
		return getSmartDate(col, true);
	}
	
    /**
	 * 일자 기준 날짜 문자열 획득(Kendo Grid 용)
	 */
	public static String getSmartDate(String col, boolean secondsIncluded) {
		
		return getSmartDate(col, secondsIncluded, true);
	}
	
    /**
	 * 일자 기준 날짜 문자열 획득(Kendo Grid 용)
	 */
	public static String getSmartDate(String col, boolean secondsIncluded, boolean timeIncluded) {
		
		if (timeIncluded) {
			
			String todayFormat = "HH:mm";
			String thisYearFormat1 = "M/d";
			String thisYearFormat2 = "HH:mm";
			String generalFormat1 = "yyyy M/d";
			String generalFormat2 = "HH:mm";
			
			if (secondsIncluded) {
				todayFormat += ":ss";
				thisYearFormat2 += ":ss";
				generalFormat2 += ":ss";
			}
			
			return  "# if (" + col + " == null) { #" + "<span>-</span>" +
					"# } else if (isToday(" + col + ")) { #" + "<span>#: kendo.format('{0:" + todayFormat + "}', " + col + ") #</span>" +
					"# } else if (isThisYear(" + col + ")) { #" + "<span>#: kendo.format('{0:" + thisYearFormat1 + "}', " + col + ") # <small>#: kendo.format('{0:" + thisYearFormat2 + "}', " + col + ") #</small></span>" +
					"# } else { #" + "<span>#: kendo.format('{0:" + generalFormat1 + "}', " + col + ") # <small>#: kendo.format('{0:" + generalFormat2 + "}', " + col + ") #</small></span>" +
					"# } #";
		} else {
			
			return  "# if (" + col + " == null) { #" + "<span>-</span>" +
					"# } else if (isThisYear(" + col + ")) { #" + "<span><small>#: kendo.format('{0:yyyy}', " + col + ") #</small> #: kendo.format('{0:M/d}', " + col + ") #</span>" +
					"# } else { #" + "<span>#: kendo.format('{0:yyyy M/d}', " + col + ") #</span>" +
					"# } #";
		}
		
	}
	
    /**
	 * 일자 기준 날짜 문자열 획득
	 */
	public static String getSmartDate(Date date, boolean secondsIncluded, boolean timeIncluded) {
		
		if (date == null) {
			return "-";
		}
		
		if (timeIncluded) {
			
			String todayFormat = "HH:mm";
			String thisYearFormat1 = "M/d";
			String thisYearFormat2 = "HH:mm";
			String generalFormat1 = "yyyy M/d";
			String generalFormat2 = "HH:mm";
			
			if (secondsIncluded) {
				todayFormat += ":ss";
				thisYearFormat2 += ":ss";
				generalFormat2 += ":ss";
			}
			
			if (isToday(date)) {
				return "<span>" + toSimpleString(date, todayFormat) + "</span>";
			} else if (isThisYear(date)) {
				return "<span>" + toSimpleString(date, thisYearFormat1) + " <small>" + toSimpleString(date, thisYearFormat2) + "</small></span>";
			} else {
				return "<span>" + toSimpleString(date, generalFormat1) + " <small>" + toSimpleString(date, generalFormat2) + "</small></span>";
			}
		} else {
			
			if (isThisYear(date)) {
				return "<span><small>" + toSimpleString(date, "yyyy") + "</small> " + toSimpleString(date, "M/d") + "</span>";
			} else {
				return "<span>" + toSimpleString(date, "yyyy M/d") + "</span>";
			}
		}
	}
	
    /**
	 * 요청에 포함된 쿠키 중 appMode의 값을 획득
	 */
	public static String getAppModeFromRequest(HttpServletRequest request) {
		
		String ret = "";
		UserCookie userCookie = new UserCookie(request);
		if (userCookie != null) {
			ret = userCookie.getAppMode();
		}
		
		return ret;
	}
	
    /**
	 * 언어 코드에 일치하는 메시지 획득
	 */
	public static String getMessage(String code, String lang) {
		
		return getMessage(code, null, lang);
	}
	
    /**
	 * 언어 코드에 일치하는 메시지 획득
	 */
	public static String getMessage(String code, String[] params, String lang) {
		
		if (isNotValid(lang)) {
			return "";
		}
		
		Locale locale = new Locale(lang);
		String msg = sMsgMgr.message(code, locale);
		
		if (params == null || params.length == 0) {
			return msg;
		}
		
		for (int i = 0; i < params.length; i ++) {
			msg = msg.replace("{" + i + "}", params[i]);
		}
		
		return msg;
	}
	
    /**
	 * 사람이 쉽게 이해할 수 있는 시간 문자열 획득
	 */
	public static String getHumanDuration(int duration) {
		
		int tmpD = duration;
		int sec = tmpD >= 60 ? tmpD % 60 : tmpD;
		
		tmpD = tmpD / 60;
		int min = tmpD >= 60 ? tmpD % 60 : tmpD;
		int hr = tmpD / 60;
		
		String ret = "";
		if (hr > 0) {
			ret = hr + "hr ";
		}
		if (min > 0) {
			ret += min + "m ";
		}
		if (sec > 0) {
			ret += sec + "s";
		}

		return Util.removeTrailingChar(ret, " ");
	}
	
    /**
	 * 사람이 쉽게 이해할 수 있는 시간 문자열 획득
	 */
	public static String getHumanTime(int secs) {
		
		int tmpD = secs;
		int sec = tmpD >= 60 ? tmpD % 60 : tmpD;
		
		tmpD = tmpD / 60;
		int min = tmpD >= 60 ? tmpD % 60 : tmpD;
		
		tmpD = tmpD / 60;
		int hr = tmpD >= 24 ? tmpD % 24 : tmpD;
		int d = tmpD / 24;
		
		
		String ret = "";
		if (d > 0) {
			ret = d + "d ";
		}
		if (hr > 0) {
			ret += hr + "hr ";
		}
		if (min > 0) {
			ret += min + "m ";
		}
		if (sec > 0) {
			ret += sec + "s";
		}

		ret = Util.removeTrailingChar(ret, " ");
		if (Util.isNotValid(ret)) {
			return "0s";
		} else {
			return ret;
		}
	}
	
    /**
	 * 플레이어에서 인지할 수 있는 시간 문자열(예 00:08:00.000) 획득
	 */
	public static String getLongTypeDuration(int duration) {
		
		int tmpD = duration;
		int sec = tmpD >= 60 ? tmpD % 60 : tmpD;
		
		tmpD = tmpD / 60;
		int min = tmpD >= 60 ? tmpD % 60 : tmpD;
		int hr = tmpD / 60;
		
		return String.format("%02d:%02d:%02d.000", hr, min, sec);
	}
	
    /**
	 * 엑셀내의 셀 값을 문자열 형태로 획득
	 */
	public static String getExcelCellValue(XSSFSheet sheet, int rowIndex, int colIndex) {
		
		String ret = "";
		
		XSSFRow row = sheet.getRow(rowIndex);
		if (row != null) {
			XSSFCell cell = row.getCell(colIndex);
			if (cell == null) {
				ret = "";
			} else {
				switch (cell.getCellTypeEnum()) {
				case _NONE:
				case BOOLEAN:
				case ERROR:
				case BLANK:
				case FORMULA:
					ret = "";
					break;
				case NUMERIC:
	                ret = String.valueOf(cell.getNumericCellValue());
	                break;
				case STRING:
					ret = cell.getStringCellValue();
					break;
				}
			}
		}
		
		return ret;
	}
	
    /**
	 * 해상도 문자열로부터 해상도의 가로세로 비율 획득
	 */
	public static float getResolutionRatio(String res) {
		
		if (Util.isValid(res)) {
			List<String> tokens = Util.tokenizeValidStr(res, "x");
			if (tokens.size() == 2) {
				float w = Util.parseFloat(tokens.get(0));
				float h = Util.parseFloat(tokens.get(1));
				
				return w / h;
			}
		}
		
		return 0f;
	}
	
    /**
	 * 두 float 숫자값의 percentage difference 획득
	 */
	public static int getPctDifference(float f1, float f2) {
		
		float r = Math.abs(f1 - f2) / ((f1 + f2) / 2f) * 100f;
		
		return Math.round(r);
	}
	
	
    /**
	 * 전달 인자 날짜 중 가장 큰 날짜 획득
	 */
	public static Date getMaxDate(Date... date) {
		
		List<Date> dates = Arrays.asList(date);
		Date ret = null;
		for(Date d : dates) {
			if (d == null) continue;
			
			if (ret == null || ret.before(d)) {
				ret = d;
			}
		}
		
		return ret;
	}
	
	
    /**
	 * JavaScript 형식의 문자열을 java Integer List로 변환 
	 */
	public static List<Integer> getIntegerList(String s) {
		
		ArrayList<Integer> retList = new ArrayList<Integer>();
		
		if (isValid(s)) {
			StringTokenizer st = new StringTokenizer(s, "[ ,]");
			while(st.hasMoreTokens()) {
				retList.add(parseInt(st.nextToken()));
			}
		}
		
		return retList;
	}
	
	
    /**
	 * JavaScript 형식의 문자열을 java String List로 변환 
	 */
	public static List<String> getStringList(String s) {
		
		ArrayList<String> retList = new ArrayList<String>();
		
		if (isValid(s)) {
			StringTokenizer st = new StringTokenizer(s, "[,]");
			while(st.hasMoreTokens()) {
				String t = st.nextToken().trim();
				if (t.startsWith("\"")) {
					t = t.substring(1);
				}
				if (t.endsWith("\"")) {
					t = t.substring(0, t.length() - 1);
				}
				retList.add(t);
			}
		}
		
		return retList;
	}
	
	
    /**
	 * 리스트에 대한 union 연산 
	 */
	public static <T> List<T> union(List<T> list1, List<T> list2) {
		
		Set<T> set = new HashSet<T>();
		
		set.addAll(list1);
		set.addAll(list2);
		
		return new ArrayList<T>(set);
	}
	
	
    /**
	 * 리스트에 대한 intersection 연산 
	 */
	public static <T> List<T> intersection(List<T> list1, List<T> list2) {
		
		List<T> list = new ArrayList<T>();
		
		for (T t : list1) {
			if (list2.contains(t)) {
				list.add(t);
			}
		}
		
		return list;
	}
	
	
    /**
	 * 문자열의 첫 토큰 값 획득 
	 */
	public static String getFirstToken(String s, String sep) {
		
		if (isNotValid(sep) || isNotValid(s)) {
			return s;
		}
		
		List<String> tokens = tokenizeValidStr(s, sep);
		return tokens.get(0);
	}
	
	
    /**
	 * 두 날짜가 동일한 해인가를 테스트 
	 */
	public static boolean isSameYear(Date date1, Date date2) {
		
		if (date1 == null || date2 == null) {
			return false;
		}
		
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy");
		return fmt.format(date1).equals(fmt.format(date2));
	}
	
	
    /**
	 * 두 날짜가 동일한 날짜인가를 테스트 
	 */
	public static boolean isSameDate(Date date1, Date date2) {
		
		if (date1 == null || date2 == null) {
			return false;
		}
		
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		return fmt.format(date1).equals(fmt.format(date2));
	}
	
	
    /**
	 * 두 날짜가 동일한 해인가를 테스트 
	 */
	public static boolean isThisYear(Date date) {
		
		if (date == null) {
			return false;
		}
		
		return isSameYear(date, new Date());
	}
	
	
    /**
	 * 테스트 대상의 날짜(첫 인자)가 두 날짜 사이에 위치하는 지를 테스트 
	 */
	public static boolean isBetween(Date date, Date date1, Date date2) {
		
		if (date == null || (date1 == null && date2 == null)) {
			return false;
		}
		if (date1 != null && date.before(date1)) {
			return false;
		}
		if (date2 != null && date.after(date2)) {
			return false;
		}
		
		return true;
	}
	
	
    /**
	 * 전달 문자열을 경로 파일명에 append
	 */
	public static boolean appendStrToFile(String s, String pathFile) {
		
		if (Util.isNotValid(s) || Util.isNotValid(pathFile)) {
			return false;
		}
		
		try (FileWriter fw = new FileWriter(pathFile, true)) {
			fw.write(s);
		} catch(Exception e) {
			logger.error("appendStrToFile", e);
			return false;
		}
		
		return true;
	}
	
	
    /**
	 * 전달된 두 날짜에 대한 기간 문자열 획득 
	 */
	public static String getSmartPeriodStr(Date date1, Date date2) {
		
		if (date1 == null) {
			return "?";
		}
		
		if (date2 == null) {
			String year = new SimpleDateFormat("yyyy").format(date1);
			if (isThisYear(date1)) {
				year = "<small>" + year + "</small>";
			}
			
			return year + " " + new SimpleDateFormat("M/d").format(date1) + " ~";
		} else {
			if (isSameDate(date1, date2)) {
				String year = new SimpleDateFormat("yyyy").format(date1);
				if (!isThisYear(date1)) {
					year = "<small>" + year + "</small>";
				}
				
				return year + " " + new SimpleDateFormat("M/d").format(date1);
			} else if (isSameYear(date1, date2)) {
				String year = new SimpleDateFormat("yyyy").format(date1);
				if (!isThisYear(date1)) {
					year = "<small>" + year + "</small>";
				}
				
				return year + " " + new SimpleDateFormat("M/d").format(date1) +
						" ~ " + new SimpleDateFormat("M/d").format(date2);
			} else {
				return new SimpleDateFormat("yyyy M/d").format(date1) +
						" ~ " + new SimpleDateFormat("yyyy M/d").format(date2);
			}
		}
	}
	
	
    /**
	 * 위도 경도 정보의 두 지점의 거리를 km 단위로 획득 
	 */
	public static double distance(double lat1, double lng1, double lat2, double lng2) {
		
		if ((lat1 == lat2) && (lng1 == lng2)) {
			return 0;
		} else {
			double theta = lng1 - lng2;
			double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + 
					Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
			dist = Math.acos(dist);
			dist = Math.toDegrees(dist);
			dist = dist * 60 * 1.1515 * 1.609344;

			return dist;
		}
	}
	
	
    /**
	 * 웹 상의 파일 존재 여부 확인
	 */
	public static boolean webFileExists(String url) {
		
		try {
			//HttpURLConnection.setFollowRedirects(false);
			HttpURLConnection con = 
					(HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("HEAD");
			
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
			
		} catch (Exception e) {}
		
		return false;
	}

}
