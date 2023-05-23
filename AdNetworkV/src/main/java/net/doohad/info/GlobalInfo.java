package net.doohad.info;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.doohad.models.adc.AdcAdCreative;
import net.doohad.models.adc.AdcCreatFile;
import net.doohad.models.knl.KnlMedium;
import net.doohad.viewmodels.rev.RevObjEventTimeItem;
import net.doohad.viewmodels.rev.RevScrWorkTimeItem;
import net.doohad.viewmodels.sys.SysObjEventTimeCalcItem;

public class GlobalInfo {
	
	public static String AppId = "adnetwork";
	public static KeyPair RSAKeyPair = null;
	public static String RSAKeyMod = "";
	
	public static Date ServerStartDt = new Date();
	
	
	// 파일 API의 정상 동작 상태 플래그
	public static boolean FileApiReady = false;
	
	
	// API 테스트 서버 URL
	//
	//   대표 서비스 URL이 됨
	//
	public static String ApiTestServer = "https://ad.doohad.net";
	
	// 재생 결과 보고 서버 URL
	//
	//   집계 전용의 서버로 분리될 수도 있음
	//
	public static String ReportServer = "https://ad.doohad.net";
	
	
	// 시스템 옵션(코드 / 값 쌍) 정보
	public static HashMap<String, String> FndOptMap = new HashMap<String, String>();
	
	// 시스템 옵션 유효기간(일시까지 유효) 정보
	public static HashMap<String, Date> FndOptExpiredMap = new HashMap<String, Date>();
	
	
	// API 처리 속도 향상을 위해 매체 정보를 hashMap으로 처리
	public static HashMap<String, KnlMedium> MediaMap = new HashMap<String, KnlMedium>();
	
	
	// 백그라운드 시퀀스 제어
	public static HashMap<String, Integer> BgMaxValueMap = new HashMap<String, Integer>();
	public static HashMap<String, Integer> BgCurrValueMap = new HashMap<String, Integer>();
	
	
	
	// 현재 광고 API에서의 기본 광고/광고 소재 리스트 백그라운드 생성
	public static HashMap<String, String> AdCandiAdCreatVerKey = new HashMap<String, String>();
	public static HashMap<String, List<AdcAdCreative>> AdCandiAdCreatMap = new HashMap<String, List<AdcAdCreative>>();

	// 현재 광고 API에서의 광고/광고 소재 순서 리스트 문자열 백그라운드 생성
	public static HashMap<String, String> AdRealAdCreatIdsMap = new HashMap<String, String>();
	public static HashMap<String, HashMap<String, AdcAdCreative>> AdRealAdCreatMap = new HashMap<String, HashMap<String, AdcAdCreative>>();
	
	
	// 파일 목록 API에서의 기본 광고/광고 소재 파일 리스트 백그라운드 생성
	public static HashMap<String, String> FileCandiCreatFileVerKey = new HashMap<String, String>();
	public static HashMap<String, ArrayList<AdcCreatFile>> FileCandiCreatFileMap = new HashMap<String, ArrayList<AdcCreatFile>>();
	
	
	// 광고 소재 인벤 타겟팅 대상의 화면 Id를 미리 백그라운드 생성
	public static HashMap<String, String> TgtScreenIdVerKey = new HashMap<String, String>();
	public static HashMap<String, List<Integer>> TgtScreenIdMap = new HashMap<String, List<Integer>>();
	
	
	// 매체 화면의 일한 시간 메모리 보관용
	public static ArrayList<RevScrWorkTimeItem> ScrWorkTimeItemList = new ArrayList<RevScrWorkTimeItem>();
	
	// 개체의 이벤트 작업 메모리 보관용(일단 단순화된 보관용으로 옮기고, 비동기로 집계로 넘김)
	public static ArrayList<RevObjEventTimeItem> ObjEventTimeItemList = new ArrayList<RevObjEventTimeItem>();
	
	// 개체의 이벤트 작업 메모리 집계용
	public static HashMap<String, SysObjEventTimeCalcItem> ObjTouchMap = new HashMap<String, SysObjEventTimeCalcItem>();
	
}
