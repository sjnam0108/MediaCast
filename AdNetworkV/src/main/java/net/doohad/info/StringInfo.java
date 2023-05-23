package net.doohad.info;

public class StringInfo {

	//
	//
	//  공통
	//
	//
	public static String CMN_WRONG_PARAM_ERROR = "잘못된 정보가 전달되었습니다. 값을 다시 확인하시기 바랍니다.";
	public static String CMN_OPERATION_NOT_REQUIRED = "요청 자료는 이미 등록되어 있어 신규 자료 등록을 수행할 수 없습니다.";
	public static String CMN_SAVE_SUCCESS_WITH_COUNT = "총 {0} 건의 자료 등록이 성공적으로 수행되었습니다.";
	
	//
	//
	//  일정 부분 공통
	//
	//
	public static String CMN_NOT_BEFORE_END_DATE = "시작일은 반드시 종료일 이전이어야 합니다.";
	public static String CMN_NOT_BEFORE_EFF_END_DATE = "유효시작일은 반드시 유효종료일 이전이어야 합니다.";
	
	//
	//
	//  일반
	//
	//
	public static String UK_ERROR_ACCOUNT_NAME = "동일한 계정명의 자료가 이미 등록되어 있습니다.";
	public static String UK_ERROR_CODE = "동일한 코드의 자료가 이미 등록되어 있습니다.";
	public static String UK_ERROR_CODE_OR_NAME = "동일한 코드 혹은 이름의 자료가 이미 등록되어 있습니다.";
	public static String UK_ERROR_CREATIVE_NAME = "동일한 이름(\"{0}\")의 광고 소재가 발견되어 복사를 진행할 수 없습니다.";
	public static String UK_ERROR_FOLDER_NAME = "동일한 폴더명의 자료가 이미 등록되어 있습니다.";
	public static String UK_ERROR_MEDIUM_ID_OR_API_KEY = "동일한 매체ID 혹은 API 키의 자료가 이미 등록되어 있습니다.";
	public static String UK_ERROR_NAME = "동일한 이름의 자료가 이미 등록되어 있습니다.";
	public static String UK_ERROR_NAME_OR_DOMAIN_NAME = "동일한 이름 혹은 도메인명의 자료가 이미 등록되어 있습니다.";
	public static String UK_ERROR_SITE_ID_OR_NAME = "동일한 사이트ID 혹은 이름의 자료가 이미 등록되어 있습니다.";
	public static String UK_ERROR_SCREEN_ID_OR_NAME = "동일한 화면ID 혹은 이름의 자료가 이미 등록되어 있습니다.";
	public static String UK_ERROR_UKID = "동일한 식별자의 자료가 이미 등록되어 있습니다.";
	public static String UK_ERROR_UKID_OR_NAME = "동일한 식별자 혹은 이름의 자료가 이미 등록되어 있습니다.";
	public static String UK_ERROR_USER_ID = "동일한 사용자ID의 자료가 이미 등록되어 있습니다.";
	
	public static String VAL_DIFF_ADVERTISER = "복사 대상 소재의 광고주 정보(도메인명: \"{0}\")가 일치하지 않습니다.";
	public static String VAL_LESS_THAN_MIN_DURATION = "최저 재생시간은 최고 재생시간보다 클 수는 없습니다.";
	public static String VAL_NOT_BETWEEN_MIN_MAX_DUR = "기본 재생시간은 최저와 최고 사이의 값이어야 합니다.";
	public static String VAL_WRONG_DUR = "잘못된 재생 시간 값입니다.";
	
	public static String DEL_ERROR_CHILD_AD = "이 자료를 이용 중인 광고 자료가 존재하기 때문에 삭제할 수 없습니다.";
	public static String DEL_ERROR_CHILD_AD_SELECT = "송출 이력이 있는 소재와의 연결 해제는 불가능합니다. 대신 종료일 변경을 통해 송출을 중지시켜 주세요.";
	public static String DEL_ERROR_CHILD_INVENTORY = "이 자료를 이용 중인 인벤토리 자료가 존재하기 때문에 삭제할 수 없습니다.";
	public static String DEL_ERROR_PREV_DATA = "이전에 등록된 자료를 삭제하는 동안 오류가 발생하였습니다.";
	
	public static String UPD_ERROR_NOT_PROPER_STATUS = "현재 상태에서는 요청하신 상태 처리를 진행할 수 없습니다.";

	public static String LOGIN_WRONG_ID_PWD = "입력한 사용자ID와 패스워드에 일치하는 사용자가 없습니다. 사용자ID와 패스워드는 대소문자를 구분합니다.";
	public static String LOGIN_ERROR = "로그인 중 예기치 않은 예외가 발생하였습니다. 이후에 다시 시도해 주시기 바랍니다.";
	
	public static String UPLOAD_ERROR = "파일 업로드 중 예기치 않은 예외가 발생하였습니다.";
	public static String UPLOAD_ERROR_FOLDER = "업로드 파일 저장 폴더를 생성할 수 없습니다.";
	public static String UPLOAD_ERROR_RESOLUTION = "동일한 해상도의 파일이 이미 등록되어 있습니다.";

}
