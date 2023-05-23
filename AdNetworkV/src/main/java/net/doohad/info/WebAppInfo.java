package net.doohad.info;


//
// jason:webappinfo: SignCast Server Version Info 정보(2014/07/10)
// WebAppInfo class 추가
public class WebAppInfo {
	
	//
	// 서버 버전 정보
	//
	// 형식: yy.mdd
	//   예) 21.102   21.615   21.1009   21.1221   22.127
	//
	public static String SERVER_VERSION = "23.515";
	
	
	// 앱의 ID(appID)
	public static String APP_ID = "adn";
	
	//
	//
	// 새 framework 쿠키 정보
	//
	// - {appID}.medium.user_{user.id}={medium.id}
	//
	//
	//
	// 기본 framework 쿠키 정보
	//
	// - currentSiteId_{user.id}={site.id}		; 현재 사용자의 가장 최근 사이트 번호 저장
	// - appMode=
	//
	//
	
	
	//
	//
	// 새 framework 세션 정보
	//
	// - currMediumId							; String - 현재 이용/관리중인 매체의 번호
	// - currAccountId							; String - 현재 이용/관리중인 계정의 번호
	//
	//
	//
	// 기본 framework 세션 정보
	//
	// - loginUserLastLoginTime					; String - 로그인 한 사용자의 최근 로그인일시 정보 저장 문자열
	//											;          1회성으로 출력하고, 세션에서 삭제
	// - loginUser								; LoginUser - 로그인 사용자 정보
	// - currentSiteId							; String - 현재 사용중인 사이트의 번호
	// - mainMenuLang							; String - 메인 트리 메뉴의 언어 코드(ko-KR, en-US 등)
	// - mainMenuData							; List<TreeViewItem> - 메인 트리 메뉴 정보
	// - rsaPrivateKey							; java.security.PrivateKey - RSA 개인 키 정보
	// - userCookie								; UserCookie - 1차 탭메뉴의 현재 설정값 저장
	// - prevUri								; String - 최근 요청된 URI 정보(request.getRequestURI())
	// - prevQuery								; String - 최근 요청된 QueryString 정보(request.getQueryString())
	//
	//
	// - currAccountId							; String - 현재 사용자의 계정 번호
	// - currMediumId							; String - 현재 사용자의 매체 번호
	//
	//
	// - recentMenus							; ArrayList<String> - 최근 접근한 메뉴 정보. 총 4개 보관 ((당분간 사용 안함))
	// - recentMenuItems						; List<QuickLinkItem> - 최근 접근한 메뉴 정보 ((당분간 사용 안함))
	// - currentViewId							; String - 현재 사용중인 뷰의 번호  ((당분간 사용 안함))
	//
	
	
	//
	// - RevScrHourlyPlay
	//     - 일별/광고별 성공/실패 횟수
	//     - 일별/광고별(광고 소재별) 시간당 성공 및 1일 실패 횟수
	//     - 계속 누적 - 라이프 주기에 따른 자동 에이징 적용 예정
	//     - 생성 트리거
	//         (성공 건)화면에서 보고(Report, RetryReport) 시
	//         (성공 건)시스템 백그라운드 - 예기치 않은 잔여 정상 자료(예를 들어 보고받은 기록을 한 직후, 서버 다운 등) 정리
	//         (실패 건)시스템 백그라운드 - 광고 선택 미보고 자료 정리 - 현재 24시간 유지
	//     - 1일 생성 건수: (모든 매체) 활성 화면 수 x 송출된 광고 수
	//
	// - RevAdHourlyPlay
	//     - 광고의 일별 성공/실패 횟수
	//     - 광고의 일별 시간당 성공 및 1일 실패 횟수
	//     - 계속 누적 - 라이프 주기에 따른 자동 에이징 적용 예정
	//     - 생성 트리거
	//         시스템 백그라운드 - 광고별, 광고 소재별 하루 송출량 계산 시
	//     - 1일 생성 건수: (모든 매체) 송출된 광고 수
	//
	// - RevCreatHourlyPlay
	//     - 광고 소재의 일별 성공/실패 횟수
	//     - 광고 소재의 일별 시간당 성공 및 1일 실패 횟수
	//     - 계속 누적 - 라이프 주기에 따른 자동 에이징 적용 예정
	//     - 생성 트리거
	//         시스템 백그라운드 - 광고별, 광고 소재별 하루 송출량 계산 시
	//     - 1일 생성 건수: (모든 매체) 송출된 광고 소재 수
	//
	// - RevScrHrlyPlyTot
	//     - 화면의 일별 성공/실패 횟수
	//     - 화면의 일별 시간당 성공 및 1일 실패 횟수
	//     - 계속 누적 - 라이프 주기에 따른 자동 에이징 적용 예정
	//     - 생성 트리거
	//         시스템 백그라운드 - 통계 자료 계산 시
	//         RevScrHourlyPlay(테이블: REV_SCR_HOURLY_PLAYS) 자료를 주기적으로 읽어서 계산
	//     - 1일 생성 건수: (모든 매체) 화면 수의 합
	//
	// - SysTmpHrlyEvent
	//     - 시간당 이벤트 계산 DB 개체
	//     - 시간당 이벤트(실패, 대체 광고, 광고없음) 계산용 임시 개체
	//     - 자동 삭제
	//     - 생성 트리거
	//         시스템 백그라운드 - 광고 선택/보고 정리 시(실패 이벤트)
	//         AD API 호출 시 최종적으로 등록된 광고 유무에 따라(대체 광고 혹은 광고없음 이벤트)
	//
	// - RevScrHrlyFailTot
	//     - 화면의 일별 실패 횟수
	//     - 화면의 일별 시간당 실패 횟수
	//     - 계속 누적 - 라이프 주기에 따른 자동 에이징 적용 예정
	//     - 생성 트리거
	//         시스템 백그라운드 - 화면 시간당 이벤트 합계 기록 시
	//         SysTmpHrlyEvent(테이블: SYS_TMP_HRLY_EVENTS) 자료를 주기적으로 읽어서 횟수 저장
	//     - 1일 생성 건수: (모든 매체) 실패가 발생한 화면 수의 합
	//
	// - RevScrHrlyNoAdTot
	//     - 화면의 일별 광고없음 횟수
	//     - 화면의 일별 시간당 광고없음 횟수
	//     - 계속 누적 - 라이프 주기에 따른 자동 에이징 적용 예정
	//     - 생성 트리거
	//         시스템 백그라운드 - 화면 시간당 이벤트 합계 기록 시
	//         SysTmpHrlyEvent(테이블: SYS_TMP_HRLY_EVENTS) 자료를 주기적으로 읽어서 횟수 저장
	//     - 1일 생성 건수: (모든 매체) 광고없음이 발생한 화면 수의 합
	//
	// - RevScrHrlyFbTot
	//     - 화면의 일별 대체 광고 횟수
	//     - 화면의 일별 시간당 대체 광고 횟수
	//     - 계속 누적 - 라이프 주기에 따른 자동 에이징 적용 예정
	//     - 생성 트리거
	//         시스템 백그라운드 - 화면 시간당 이벤트 합계 기록 시
	//         SysTmpHrlyEvent(테이블: SYS_TMP_HRLY_EVENTS) 자료를 주기적으로 읽어서 횟수 저장
	//     - 1일 생성 건수: (모든 매체) 대체 광고가 발생한 화면 수의 합
	//
	
}
