package net.doohad.dbsetup;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.Transaction;

import net.doohad.models.fnd.FndPriv;
import net.doohad.models.fnd.FndRegion;
import net.doohad.models.fnd.FndState;
import net.doohad.models.knl.KnlAccount;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.knl.KnlMenu;
import net.doohad.models.knl.KnlUser;
import net.doohad.utils.HibernateUtil;

public class InitDataCreation {
	/**
	 * Main Method
	 */
	public static void main(String[] args) {
		Session session = HibernateUtil.getSessionFactory().openSession();

		
		// ----------------------------------------------------------------
		// 
		// 권한:
		//
		//     AccessAnyChildSite   모든 자식 사이트 접근 권한
		//     AccessAnyMenu        모든 메뉴 접근 권한
		//     ManageSiteJob        사이트 관리 권한
		//     NoConcurrentCheck    동일계정 동시사용체크 무효화 권한
		//     NoTimeOut            세션만기시간 무효화 권한
		//
		//
		//     상세 설명:
		//     
		//         AccessAnyChildSite:
		//            - 현재 사이트와 매핑된 모든 자식 사이트 및 자식 사이트 하위 STB 그룹 "사용자 뷰"를 
		//              이용할 수 있는 권한.
		//            - 이 권한의 원할한 사용을 위해서는 "ManageSiteJob" 권한이 필요함.
		//
		//         AccessAnyMenu:
		//            - 서버에 등록된 모든 메뉴에 접근할 수 있는 권한.
		//            - super user인 경우에는 이 권한만 부여하면 충분하며, 어떤 사용자 계정에 특정 페이지 
		//              접근만 허락할 경우에는 대상 페이지 메뉴 권한을 사용자 계정에 부여해야 함.
		//
		//         ManageSiteJob:
		//            - 사이트 수준의 모든 관리 작업을 수행할 수 있는 권한.
		//
		//         NoConcurrentCheck:
		//            - 동일 계정 사용자의 동시 사용(타 브라우저, 타 기기) 체크를 무효화 시키는 권한.
		//            - 보안의 요소보다 편의성 요소를 더 중요시 하는 고객 또는 상황 발생 시 적용.
		//            - 이 권한을 가진 사용자 계정은 동일 시점 동시 사용이 가능함.
		//
		//         NoTimeOut:
		//            - WAS에 설정된 세션 만기 시간을 무효화 시키는 권한.
		//            - 보안의 요소보다 편의성 요소를 더 중요시 하는 고객 또는 상황 발생 시 적용.
		//            - 이 권한을 가진 사용자는 로그인 시 세션 만기 시간이 자동적으로 무효화되며 수동적으로
		//              로그아웃 버튼을 누르거나 사용중인 브라우저가 닫히기 전까지 계속 세션이 유효하게 됨.
		//            - 기본적으로 롤에 포함되지 않으며, 필요시 개별 사용자에게 권한으로 부여함.
		//
		// ----------------------------------------------------------------
		// 
		// SignCast 권한:
		//
		//     AccessAnyAd          모든 광고 접근 권한
		//     AccessAnyStbGroup    모든 STB 그룹 접근 권한
		//     ControlFireAlarm		화재경보스위치 제어 권한
		//     LoginSSO             SSO 로그인 허용 권한
		//     ManageEtcJob         통합 관제 업무 중 STB 기타 관리 권한
		//     ManageHWJob          통합 관제 업무 중 STB H/W 관리 권한
		//     ManageMAJob          통합 관제 업무 중 STB 유지보수 관리 권한
		//     ManageNWJob          통합 관제 업무 중 STB N/W 관리 권한
		//     ManagerAdmin         SignCast Manager 로그인/동기화 권한
		//     ViewRecentTask       STB 최근 작업 조회
		//
		//
		//     상세 설명:
		//     
		//         AccessAnyAd:
		//            - 모든 광고 정보를 조회/관리할 수 있는 권한.
		//
		//         AccessAnyStbGroup:
		//            - 모든 STB 그룹 "사용자 뷰"를 이용할 수 있는 권한.
		//
		//         ControlFireAlarm:
		//            - 사용자 홈에서 화재경보스위치를 제어할 수 있는 권한.
		//
		//         LoginSSO:
		//            - SSO 로그인 경로(/sso)를 통해 접근한 사용자 계정의 SSO 허가 여부 지정 권한.
		//
		//         ManageEtcJob:
		//            - 통합 관제 업무진행 항목 중 "기타 파트" 업무 진행을 조회/관리할 수 있는 권한.
		//            - 통합 관제 업무는 관제센터, MA 파트, H/W 파트, N/W 파트, 기타 파트 등 총 5개 그룹으로 
		//              구분되며, 전체 업무 진행을 위해서는 "ManageSiteJob" 권한이 필요함.
		//
		//         ManageHWJob:
		//            - 통합 관제 업무진행 항목 중 "H/W 파트" 업무 진행을 조회/관리할 수 있는 권한.
		//            - 통합 관제 업무는 관제센터, MA 파트, H/W 파트, N/W 파트, 기타 파트 등 총 5개 그룹으로 
		//              구분되며, 전체 업무 진행을 위해서는 "ManageSiteJob" 권한이 필요함.
		//
		//         ManageMAJob:
		//            - 통합 관제 업무진행 항목 중 "MA 파트" 업무 진행을 조회/관리할 수 있는 권한.
		//            - 통합 관제 업무는 관제센터, MA 파트, H/W 파트, N/W 파트, 기타 파트 등 총 5개 그룹으로 
		//              구분되며, 전체 업무 진행을 위해서는 "ManageSiteJob" 권한이 필요함.
		//
		//         ManageNWJob:
		//            - 통합 관제 업무진행 항목 중 "N/W 파트" 업무 진행을 조회/관리할 수 있는 권한.
		//            - 통합 관제 업무는 관제센터, MA 파트, H/W 파트, N/W 파트, 기타 파트 등 총 5개 그룹으로 
		//              구분되며, 전체 업무 진행을 위해서는 "ManageSiteJob" 권한이 필요함.
		//
		//         ManagerAdmin:
		//            - SignCast Manager 프로그램을 이용한 서버 로그인이 가능하며, 로그인 후에는 
		//              Manager 프로그램에 있는 서버와의 모든 작업(서버 스케줄 저장, 컨텐츠 동기화 등)을 
		//              수행하는 것이 가능함.
		//
		//         ManageSiteJob:
		//            - 사이트 수준의 모든 관리 작업을 수행할 수 있는 권한.
		//            - 모든 통합 관제 업무 진행(관제센터, MA 파트, H/W 파트, N/W 파트, 기타 파트 등 총 5개 그룹)을 
		//              조회/관리할 수 있는 권한.
		//            - 모든 광고 정보를 조회/관리할 수 있는 권한(AccessAnyAd).
		//            - 모든 STB 그룹 "사용자 뷰"를 이용할 수 있는 권한(AccessAnyStbGroup).
		//
		//         ViewRecentTask:
		//            - 글로벌 페이지 기능인 "STB 최근 작업" 목록을 조회할 수 있는 권한.
		//            - 글로벌 페이지 기능인 "FTP 서버 파일 전송" 목록을 조회할 수 있는 권한.
		//
		// ----------------------------------------------------------------
		
		

		// ----------------------------------------------------------------
		// 
		// 롤:
		//
		//     SystemAdmin      모든 사이트 관리 목적의 슈퍼 관리자 롤
		//
		//
		//     상세 설명:
		//     
		//         SystemAdmin:
		//            - 모든 사이트 관리 목적의 슈퍼 관리자 롤
		//            - AccessAnyMenu 권한을 포함하기 때문에 별도의 페이지 접근 권한을 포함하지 않음
		//            - 아래의 특수 권한을 포함:
		//                AccessAnyChildSite   모든 자식 사이트 접근 권한
		//                AccessAnyMenu        모든 메뉴 접근 권한
		//                ManageSiteJob        사이트 관리 권한
		//
		// ----------------------------------------------------------------
		// 
		// SignCast 롤:
		//
		//     Admin            단일 사이트 관리 목적의 사이트 관리자 롤(간략형)
		//     AssetAdmin       자산 관리자 롤
		//     SiteAdmin        단일 사이트 관리 목적의 사이트 관리자 롤
		//
		//
		//     상세 설명:
		//     
		//         Admin:
		//            - 단일 사이트 관리를 위한 사이트 관리자 롤
		//            - 사전에 지정된 페이지 접근 권한을 포함
		//            - AccessAnyMenu 권한이 포함되지 않기 때문에 접근 가능한 페이지 권한을 모두 포함
		//            - 아래의 특수 권한을 포함:
		//                ManagerAdmin         SignCast Manager 로그인/동기화 권한
		//                ManageSiteJob        사이트 관리 권한
		//                ViewRecentTask       STB 최근 작업 조회
		//
		//         SiteAdmin:
		//            - 단일 사이트 관리를 위한 사이트 관리자 롤
		//            - Foundation 기능 모듈을 제외한 거의 대부분의 페이지 접근 권한을 포함
		//            - AccessAnyMenu 권한이 포함되지 않기 때문에 접근 가능한 페이지 권한을 모두 포함
		//            - 아래의 특수 권한을 포함:
		//                ManagerAdmin         SignCast Manager 로그인/동기화 권한
		//                ManageSiteJob        사이트 관리 권한
		//                ViewRecentTask       STB 최근 작업 조회
		//
		//         AssetAdmin:
		//            - 자산 관리자 롤
		//            - 자산 관리 페이지 접근 권한을 포함
		//            - 아래의 특수 권한을 포함:
		//
		//         SystemAdmin:
		//            - 모든 사이트 관리 목적의 슈퍼 관리자 롤
		//            - AccessAnyMenu 권한을 포함하기 때문에 별도의 페이지 접근 권한을 포함하지 않음
		//            - 아래의 특수 권한을 포함:
		//                AccessAnyChildSite   모든 자식 사이트 접근 권한
		//                AccessAnyMenu        모든 메뉴 접근 권한
		//                ManagerAdmin         SignCast Manager 로그인/동기화 권한
		//                ManageSiteJob        사이트 관리 권한
		//                ViewRecentTask       STB 최근 작업 조회
		//
		// ----------------------------------------------------------------
		
		
		Transaction tx = session.beginTransaction();

		
		//
		//
		// Step 1. 필수 자료
		//
		//
		
		
		//
		//  Top menu
		//
		KnlMenu revKMenu = new KnlMenu("Review", "", "gauge-high", 100, true, true, false, null);
		KnlMenu adcKMenu = new KnlMenu("Ad", "", "paintbrush-pencil", 200, true, true, false, null);
		KnlMenu invKMenu = new KnlMenu("Inventory", "", "objects-column", 300, true, true, false, null);
		KnlMenu orgKMenu = new KnlMenu("Organization", "", "building", 400, true, true, false, null);
		KnlMenu fndKMenu = new KnlMenu("Foundation", "", "kaaba", 700, true, false, false, null);
		KnlMenu knlKMenu = new KnlMenu("Kernel", "", "atom-simple", 800, true, false, false, null);
		KnlMenu sysKMenu = new KnlMenu("System", "", "chart-network", 900, true, false, false, null);
		
		session.save(revKMenu);
		session.save(adcKMenu);
		session.save(invKMenu);
		session.save(orgKMenu);
		session.save(fndKMenu);
		session.save(knlKMenu);
		session.save(sysKMenu);
		
		
		//
		//  Review menu
		//
		KnlMenu pendApprKMenu = new KnlMenu("RevPendAppr", "/rev/pendappr", "hourglass-clock", 10, true, true, false, null);
		KnlMenu monitoringKMenu = new KnlMenu("RevMonitoring", "/rev/monitoring", "monitor-waveform", 20, true, true, false, null);
		KnlMenu dailyApiKMenu = new KnlMenu("RevDailyApi", "/rev/dailyapi", "plug", 30, true, true, false, null);
		KnlMenu geofenceKMenu = new KnlMenu("RevGeofence", "/rev/geofence", "globe-stand", 40, true, true, false, null);
		
		pendApprKMenu.setParent(revKMenu);
		monitoringKMenu.setParent(revKMenu);
		dailyApiKMenu.setParent(revKMenu);
		geofenceKMenu.setParent(revKMenu);

		session.save(pendApprKMenu);
		session.save(monitoringKMenu);
		session.save(dailyApiKMenu);
		session.save(geofenceKMenu);
		
		
		//
		//  Ad menu
		//
		KnlMenu campaignKMenu = new KnlMenu("AdcCampaign", "/adc/campaign", "briefcase", 10, true, true, false, null);
		KnlMenu adKMenu = new KnlMenu("AdcAd", "/adc/ad", "audio-description", 20, true, true, false, null);
		KnlMenu creativeKMenu = new KnlMenu("AdcCreative", "/adc/creative", "clapperboard-play", 30, true, true, false, null);
		
		campaignKMenu.setParent(adcKMenu);
		adKMenu.setParent(adcKMenu);
		creativeKMenu.setParent(adcKMenu);

		session.save(campaignKMenu);
		session.save(adKMenu);
		session.save(creativeKMenu);
		
		
		//
		//  Inventory menu
		//
		KnlMenu siteKMenu = new KnlMenu("InvSite", "/inv/site", "map-pin", 10, true, true, false, null);
		KnlMenu screenKMenu = new KnlMenu("InvScreen", "/inv/screen", "screen-users", 20, true, true, false, null);
		
		siteKMenu.setParent(invKMenu);
		screenKMenu.setParent(invKMenu);

		session.save(siteKMenu);
		session.save(screenKMenu);
		
		
		//
		//  Organization menu
		//
		KnlMenu currMediumKMenu = new KnlMenu("OrgCurrMedium", "/org/currmedium", "earth-asia", 10, true, true, false, null);
		KnlMenu playlistKMenu = new KnlMenu("OrgPlaylist", "/org/playlist", "list-ol", 20, true, true, false, null);
		KnlMenu advertiserKMenu = new KnlMenu("OrgAdvertiser", "/org/advertiser", "user-tie-hair", 30, true, true, false, null);
		KnlMenu siteCondKMenu = new KnlMenu("OrgSiteCond", "/org/sitecond", "location-crosshairs", 40, true, true, false, null);
		KnlMenu batchInvKMenu = new KnlMenu("BatchInv", "/org/batchinv", "upload", 50, true, true, false, null);
		KnlMenu invenRequestKMenu = new KnlMenu("RevInvenRequest", "/org/invenrequest", "plug", 60, true, true, false, null);
		KnlMenu userKMenu = new KnlMenu("OrgUser", "/org/user", "users", 70, true, true, false, null);
		
		currMediumKMenu.setParent(orgKMenu);
		playlistKMenu.setParent(orgKMenu);
		advertiserKMenu.setParent(orgKMenu);
		siteCondKMenu.setParent(orgKMenu);
		batchInvKMenu.setParent(orgKMenu);
		invenRequestKMenu.setParent(orgKMenu);
		userKMenu.setParent(orgKMenu);

		session.save(currMediumKMenu);
		session.save(playlistKMenu);
		session.save(advertiserKMenu);
		session.save(siteCondKMenu);
		session.save(batchInvKMenu);
		session.save(invenRequestKMenu);
		session.save(userKMenu);
		
		
		//
		//  Foundation menu
		//
		KnlMenu privKMenu = new KnlMenu("FndPriv", "/fnd/priv", "cog", 10, true, false, false, null);
		KnlMenu userPrivKMenu = new KnlMenu("FndUserPriv", "/fnd/userpriv", "user-cog", 20, true, false, false, null);
		KnlMenu stateKMenu = new KnlMenu("FndState", "/fnd/state", "city", 30, true, false, false, null);
		KnlMenu regionKMenu = new KnlMenu("FndRegion", "/fnd/region", "mountain-city", 40, true, false, false, null);
		KnlMenu ctntFolderKMenu = new KnlMenu("FndCtntFolder", "/fnd/ctntfolder", "folder", 50, true, false, false, null);
		KnlMenu loginLogKMenu = new KnlMenu("FndLoginLog", "/fnd/loginlog", "sign-in-alt", 60, true, false, false, null);
		
		privKMenu.setParent(fndKMenu);
		userPrivKMenu.setParent(fndKMenu);
		stateKMenu.setParent(fndKMenu);
		regionKMenu.setParent(fndKMenu);
		ctntFolderKMenu.setParent(fndKMenu);
		loginLogKMenu.setParent(fndKMenu);

		session.save(privKMenu);
		session.save(userPrivKMenu);
		session.save(stateKMenu);
		session.save(regionKMenu);
		session.save(ctntFolderKMenu);
		session.save(loginLogKMenu);
		
		
		//
		//  Kernel menu
		//
		KnlMenu mediumKMenu = new KnlMenu("KnlMedium", "/knl/medium", "earth-asia", 10, true, false, false, null);
		KnlMenu accountKMenu = new KnlMenu("KnlAccount", "/knl/account", "building-circle-check", 20, true, false, false, null);
		KnlMenu managerKMenu = new KnlMenu("KnlManager", "/knl/manager", "user-gear", 30, true, false, false, null);
		KnlMenu menuKMenu = new KnlMenu("KnlMenu", "/knl/menu", "plate-utensils", 40, true, false, false, null);
		
		mediumKMenu.setParent(knlKMenu);
		accountKMenu.setParent(knlKMenu);
		managerKMenu.setParent(knlKMenu);
		menuKMenu.setParent(knlKMenu);

		session.save(mediumKMenu);
		session.save(accountKMenu);
		session.save(managerKMenu);
		session.save(menuKMenu);
		
		
		//
		//  System menu
		//
		KnlMenu respTimeKMenu = new KnlMenu("SysRespTime", "/sys/rt", "stopwatch", 10, true, false, false, null);
		
		respTimeKMenu.setParent(sysKMenu);

		session.save(respTimeKMenu);

		
		
		
		// 메뉴 관련 권한 및 롤 자동 생성
		//syncWithPrivAndRole(session);
		
		
		/*
		Privilege mgrAdminPriv = new Privilege("internal.ManagerAdmin");
		session.save(mgrAdminPriv);
		
		Privilege accessAnyStbGroupPriv = new Privilege("internal.AccessAnyStbGroup");
		session.save(accessAnyStbGroupPriv);
		
		Privilege accessAnyAdPriv = new Privilege("internal.AccessAnyAd");
		session.save(accessAnyAdPriv);
		
		Privilege viewRecentTaskPriv = new Privilege("internal.ViewRecentTask");
		session.save(viewRecentTaskPriv);
		
		Privilege manageMAJobPriv = new Privilege("internal.ManageMAJob");
		session.save(manageMAJobPriv);
		
		Privilege manageHWJobPriv = new Privilege("internal.ManageHWJob");
		session.save(manageHWJobPriv);
		
		Privilege manageNWJobPriv = new Privilege("internal.ManageNWJob");
		session.save(manageNWJobPriv);
		
		Privilege manageEtcJobPriv = new Privilege("internal.ManageEtcJob");
		session.save(manageEtcJobPriv);
		
		Privilege controlFireAlarmPriv = new Privilege("internal.ControlFireAlarm");
		session.save(controlFireAlarmPriv);
		
		Privilege loginSsoPriv = new Privilege("internal.LoginSSO");
		session.save(loginSsoPriv);
		*/
    	
    	//
    	//
    	// [SignCast] ext ------------------------------------------------------------- end
		
		
		
		
		
		/*
		session.save(new RolePrivilege(systemAdminRole, mgrAdminPriv));
		session.save(new RolePrivilege(systemAdminRole, accessAnyAdPriv));
		session.save(new RolePrivilege(systemAdminRole, accessAnyStbGroupPriv));
		session.save(new RolePrivilege(systemAdminRole, viewRecentTaskPriv));
		
		session.save(new RolePrivilege(siteAdminRole, mgrAdminPriv));
		session.save(new RolePrivilege(siteAdminRole, manageSiteJobPriv));
		session.save(new RolePrivilege(siteAdminRole, viewRecentTaskPriv));

		
		// 메뉴 그룹 접근 권한을 롤에 부여
		grantMenuGroupsToRole(session, siteAdminMenuGroups, "internal.SiteAdmin");

		grantMenuGroupsToRole(session, assetAdminMenuGroups, "internal.AssetAdmin");

		
		// Amin 롤에 페이지 접근 권한 부여
		String[] menus = {
				"UserHome", 
				"Stb", "StbGroup", "StbGroupStb", "StbGroupUser",
				"CtaPkg", "SchdSchdPkg", "SchdBndlCond", "SchdBndlFile", 
				"MonDashboard", "MonGridView", "MonMapView", "StbMsg", "OptSetting", "MonTask", "RtnSchdTask", "MonEventReport", "RecStatus", "RecPeriod",
				"UpAgentLog", "UpDebugLog", 
				"OptPlaytime", "OptGuideImg", "OptHoliday", 
				"CurrSiteSetting", "CurrSiteTask", "CurrDispMenu", "BasRegion",
		};  
		
		grantMenusToRole(session, Arrays.asList(menus), "internal.Admin");
		*/
    	
    	//
    	//
    	// [SignCast] ext ------------------------------------------------------------- end
		
		

		//
		// 사용자 권한
		//
		//grantRoleToUser(session, "internal.SystemAdmin", "system");
		

		
		
		
		KnlAccount account = new KnlAccount("비비엠씨", new Date(), null, "", null);
		account.setScopeKernel(true);
		session.save(account);
		
		KnlMedium medium = new KnlMedium("adnetwork", "광고 네트워크", "1920x1080|1080x1920", "CY9YOU0ALsq0ac3Zek9TQr", 
				30, false, 25, 35, new Date(), null, "", null);
		session.save(medium);

		KnlUser manager = new KnlUser(account, "system", "관리자", "adnet4312", "M1", "", null);
		session.save(manager);

		
		FndPriv noConCheckPriv = new FndPriv("internal.NoConcurrentCheck", "동일계정 동시사용체크 무효화 권한", null);
		session.save(noConCheckPriv);
		
		FndPriv noTimeOutPriv = new FndPriv("internal.NoTimeOut", "세션만기 무효화 권한", null);
		session.save(noTimeOutPriv);
		
		
		//
		//
		// Step 3. 추가 옵션 자료
		//
		//

		
		session.save(new FndState("01", "강원도", true, null));
		session.save(new FndState("02", "경기도", true, null));
		session.save(new FndState("03", "경상남도", true, null));
		session.save(new FndState("04", "경상북도", true, null));
		session.save(new FndState("05", "광주시", true, null));
		session.save(new FndState("06", "대구시", true, null));
		session.save(new FndState("07", "대전시", true, null));
		session.save(new FndState("08", "부산시", true, null));
		session.save(new FndState("09", "서울시", true, null));
		session.save(new FndState("17", "세종시", true, null));
		session.save(new FndState("10", "울산시", true, null));
		session.save(new FndState("11", "인천시", true, null));
		session.save(new FndState("12", "전라남도", true, null));
		session.save(new FndState("13", "전라북도", true, null));
		session.save(new FndState("14", "제주도", true, null));
		session.save(new FndState("15", "충청남도", true, null));
		session.save(new FndState("16", "충청북도", true, null));
		
		
		session.save(new FndRegion("01150101", "강원도 강릉시", true, null));
		session.save(new FndRegion("01820250", "강원도 고성군", true, null));
		session.save(new FndRegion("01170101", "강원도 동해시", true, null));
		session.save(new FndRegion("01230101", "강원도 삼척시", true, null));
		session.save(new FndRegion("01210101", "강원도 속초시", true, null));
		session.save(new FndRegion("01800250", "강원도 양구군", true, null));
		session.save(new FndRegion("01830250", "강원도 양양군", true, null));
		session.save(new FndRegion("01750250", "강원도 영월군", true, null));
		session.save(new FndRegion("01130101", "강원도 원주시", true, null));
		session.save(new FndRegion("01810250", "강원도 인제군", true, null));
		session.save(new FndRegion("01770250", "강원도 정선군", true, null));
		session.save(new FndRegion("01780250", "강원도 철원군", true, null));
		session.save(new FndRegion("01110101", "강원도 춘천시", true, null));
		session.save(new FndRegion("01190101", "강원도 태백시", true, null));
		session.save(new FndRegion("01760250", "강원도 평창군", true, null));
		session.save(new FndRegion("01720250", "강원도 홍천군", true, null));
		session.save(new FndRegion("01790250", "강원도 화천군", true, null));
		session.save(new FndRegion("01730250", "강원도 횡성군", true, null));
		session.save(new FndRegion("02820250", "경기도 가평군", true, null));
		session.save(new FndRegion("02281101", "경기도 고양시 덕양구", true, null));
		session.save(new FndRegion("02285101", "경기도 고양시 일산동구", true, null));
		session.save(new FndRegion("02287101", "경기도 고양시 일산서구", true, null));
		session.save(new FndRegion("02290101", "경기도 과천시", true, null));
		session.save(new FndRegion("02210101", "경기도 광명시", true, null));
		session.save(new FndRegion("02610101", "경기도 광주시", true, null));
		session.save(new FndRegion("02310101", "경기도 구리시", true, null));
		session.save(new FndRegion("02410101", "경기도 군포시", true, null));
		session.save(new FndRegion("02570101", "경기도 김포시", true, null));
		session.save(new FndRegion("02360101", "경기도 남양주시", true, null));
		session.save(new FndRegion("02250101", "경기도 동두천시", true, null));
		session.save(new FndRegion("02190101", "경기도 부천시", true, null));
		session.save(new FndRegion("02135101", "경기도 성남시 분당구", true, null));
		session.save(new FndRegion("02131101", "경기도 성남시 수정구", true, null));
		session.save(new FndRegion("02133101", "경기도 성남시 중원구", true, null));
		session.save(new FndRegion("02113126", "경기도 수원시 권선구", true, null));
		session.save(new FndRegion("02117101", "경기도 수원시 영통구", true, null));
		session.save(new FndRegion("02111129", "경기도 수원시 장안구", true, null));
		session.save(new FndRegion("02115120", "경기도 수원시 팔달구", true, null));
		session.save(new FndRegion("02390101", "경기도 시흥시", true, null));
		session.save(new FndRegion("02273101", "경기도 안산시 단원구", true, null));
		session.save(new FndRegion("02271101", "경기도 안산시 상록구", true, null));
		session.save(new FndRegion("02550101", "경기도 안성시", true, null));
		session.save(new FndRegion("02173101", "경기도 안양시 동안구", true, null));
		session.save(new FndRegion("02171101", "경기도 안양시 만안구", true, null));
		session.save(new FndRegion("02630101", "경기도 양주시", true, null));
		session.save(new FndRegion("02830250", "경기도 양평군", true, null));
		session.save(new FndRegion("02670101", "경기도 여주시", true, null));
		session.save(new FndRegion("02800250", "경기도 연천군", true, null));
		session.save(new FndRegion("02370101", "경기도 오산시", true, null));
		session.save(new FndRegion("02463101", "경기도 용인시 기흥구", true, null));
		session.save(new FndRegion("02465101", "경기도 용인시 수지구", true, null));
		session.save(new FndRegion("02461101", "경기도 용인시 처인구", true, null));
		session.save(new FndRegion("02430101", "경기도 의왕시", true, null));
		session.save(new FndRegion("02150101", "경기도 의정부시", true, null));
		session.save(new FndRegion("02500101", "경기도 이천시", true, null));
		session.save(new FndRegion("02480101", "경기도 파주시", true, null));
		session.save(new FndRegion("02220101", "경기도 평택시", true, null));
		session.save(new FndRegion("02650101", "경기도 포천시", true, null));
		session.save(new FndRegion("02450101", "경기도 하남시", true, null));
		session.save(new FndRegion("02590101", "경기도 화성시", true, null));
		session.save(new FndRegion("03310101", "경상남도 거제시", true, null));
		session.save(new FndRegion("03880250", "경상남도 거창군", true, null));
		session.save(new FndRegion("03820250", "경상남도 고성군", true, null));
		session.save(new FndRegion("03250101", "경상남도 김해시", true, null));
		session.save(new FndRegion("03840250", "경상남도 남해군", true, null));
		session.save(new FndRegion("03270101", "경상남도 밀양시", true, null));
		session.save(new FndRegion("03240101", "경상남도 사천시", true, null));
		session.save(new FndRegion("03860250", "경상남도 산청군", true, null));
		session.save(new FndRegion("03330101", "경상남도 양산시", true, null));
		session.save(new FndRegion("03720250", "경상남도 의령군", true, null));
		session.save(new FndRegion("03170101", "경상남도 진주시", true, null));
		session.save(new FndRegion("03740250", "경상남도 창녕군", true, null));
		session.save(new FndRegion("03125101", "경상남도 창원시 마산합포구", true, null));
		session.save(new FndRegion("03127101", "경상남도 창원시 마산회원구", true, null));
		session.save(new FndRegion("03123101", "경상남도 창원시 성산구", true, null));
		session.save(new FndRegion("03121101", "경상남도 창원시 의창구", true, null));
		session.save(new FndRegion("03129101", "경상남도 창원시 진해구", true, null));
		session.save(new FndRegion("03220101", "경상남도 통영시", true, null));
		session.save(new FndRegion("03850250", "경상남도 하동군", true, null));
		session.save(new FndRegion("03730250", "경상남도 함안군", true, null));
		session.save(new FndRegion("03870250", "경상남도 함양군", true, null));
		session.save(new FndRegion("03890250", "경상남도 합천군", true, null));
		session.save(new FndRegion("04290101", "경상북도 경산시", true, null));
		session.save(new FndRegion("04130101", "경상북도 경주시", true, null));
		session.save(new FndRegion("04830250", "경상북도 고령군", true, null));
		session.save(new FndRegion("04190101", "경상북도 구미시", true, null));
		session.save(new FndRegion("04720250", "경상북도 군위군", true, null));
		session.save(new FndRegion("04150101", "경상북도 김천시", true, null));
		session.save(new FndRegion("04280101", "경상북도 문경시", true, null));
		session.save(new FndRegion("04920250", "경상북도 봉화군", true, null));
		session.save(new FndRegion("04250101", "경상북도 상주시", true, null));
		session.save(new FndRegion("04840250", "경상북도 성주군", true, null));
		session.save(new FndRegion("04170101", "경상북도 안동시", true, null));
		session.save(new FndRegion("04770250", "경상북도 영덕군", true, null));
		session.save(new FndRegion("04760250", "경상북도 영양군", true, null));
		session.save(new FndRegion("04210101", "경상북도 영주시", true, null));
		session.save(new FndRegion("04230101", "경상북도 영천시", true, null));
		session.save(new FndRegion("04900250", "경상북도 예천군", true, null));
		session.save(new FndRegion("04940250", "경상북도 울릉군", true, null));
		session.save(new FndRegion("04930250", "경상북도 울진군", true, null));
		session.save(new FndRegion("04730250", "경상북도 의성군", true, null));
		session.save(new FndRegion("04820250", "경상북도 청도군", true, null));
		session.save(new FndRegion("04750250", "경상북도 청송군", true, null));
		session.save(new FndRegion("04850250", "경상북도 칠곡군", true, null));
		session.save(new FndRegion("04111101", "경상북도 포항시 남구", true, null));
		session.save(new FndRegion("04113101", "경상북도 포항시 북구", true, null));
		session.save(new FndRegion("05200101", "광주시 광산구", true, null));
		session.save(new FndRegion("05155101", "광주시 남구", true, null));
		session.save(new FndRegion("05110101", "광주시 동구", true, null));
		session.save(new FndRegion("05170101", "광주시 북구", true, null));
		session.save(new FndRegion("05140104", "광주시 서구", true, null));
		session.save(new FndRegion("06200101", "대구시 남구", true, null));
		session.save(new FndRegion("06290101", "대구시 달서구", true, null));
		session.save(new FndRegion("06710250", "대구시 달성군", true, null));
		session.save(new FndRegion("06140101", "대구시 동구", true, null));
		session.save(new FndRegion("06230101", "대구시 북구", true, null));
		session.save(new FndRegion("06170101", "대구시 서구", true, null));
		session.save(new FndRegion("06260101", "대구시 수성구", true, null));
		session.save(new FndRegion("06110101", "대구시 중구", true, null));
		session.save(new FndRegion("07230101", "대전시 대덕구", true, null));
		session.save(new FndRegion("07110101", "대전시 동구", true, null));
		session.save(new FndRegion("07170101", "대전시 서구", true, null));
		session.save(new FndRegion("07200101", "대전시 유성구", true, null));
		session.save(new FndRegion("07140101", "대전시 중구", true, null));
		session.save(new FndRegion("08440101", "부산시 강서구", true, null));
		session.save(new FndRegion("08410101", "부산시 금정구", true, null));
		session.save(new FndRegion("08710250", "부산시 기장군", true, null));
		session.save(new FndRegion("08290106", "부산시 남구", true, null));
		session.save(new FndRegion("08170101", "부산시 동구", true, null));
		session.save(new FndRegion("08260101", "부산시 동래구", true, null));
		session.save(new FndRegion("08230101", "부산시 부산진구", true, null));
		session.save(new FndRegion("08320101", "부산시 북구", true, null));
		session.save(new FndRegion("08530101", "부산시 사상구", true, null));
		session.save(new FndRegion("08380101", "부산시 사하구", true, null));
		session.save(new FndRegion("08140101", "부산시 서구", true, null));
		session.save(new FndRegion("08500101", "부산시 수영구", true, null));
		session.save(new FndRegion("08470101", "부산시 연제구", true, null));
		session.save(new FndRegion("08200101", "부산시 영도구", true, null));
		session.save(new FndRegion("08110101", "부산시 중구", true, null));
		session.save(new FndRegion("08350101", "부산시 해운대구", true, null));
		session.save(new FndRegion("09680101", "서울시 강남구", true, null));
		session.save(new FndRegion("09740101", "서울시 강동구", true, null));
		session.save(new FndRegion("09305101", "서울시 강북구", true, null));
		session.save(new FndRegion("09500101", "서울시 강서구", true, null));
		session.save(new FndRegion("09620101", "서울시 관악구", true, null));
		session.save(new FndRegion("09215101", "서울시 광진구", true, null));
		session.save(new FndRegion("09530101", "서울시 구로구", true, null));
		session.save(new FndRegion("09545101", "서울시 금천구", true, null));
		session.save(new FndRegion("09350102", "서울시 노원구", true, null));
		session.save(new FndRegion("09320105", "서울시 도봉구", true, null));
		session.save(new FndRegion("09230101", "서울시 동대문구", true, null));
		session.save(new FndRegion("09590101", "서울시 동작구", true, null));
		session.save(new FndRegion("09440101", "서울시 마포구", true, null));
		session.save(new FndRegion("09410101", "서울시 서대문구", true, null));
		session.save(new FndRegion("09650101", "서울시 서초구", true, null));
		session.save(new FndRegion("09200101", "서울시 성동구", true, null));
		session.save(new FndRegion("09290101", "서울시 성북구", true, null));
		session.save(new FndRegion("09710101", "서울시 송파구", true, null));
		session.save(new FndRegion("09470101", "서울시 양천구", true, null));
		session.save(new FndRegion("09560101", "서울시 영등포구", true, null));
		session.save(new FndRegion("09170101", "서울시 용산구", true, null));
		session.save(new FndRegion("09380101", "서울시 은평구", true, null));
		session.save(new FndRegion("09110101", "서울시 종로구", true, null));
		session.save(new FndRegion("09140101", "서울시 중구", true, null));
		session.save(new FndRegion("09260101", "서울시 중랑구", true, null));
		session.save(new FndRegion("17110101", "세종시", true, null));
		session.save(new FndRegion("10140101", "울산시 남구", true, null));
		session.save(new FndRegion("10170101", "울산시 동구", true, null));
		session.save(new FndRegion("10200101", "울산시 북구", true, null));
		session.save(new FndRegion("10710250", "울산시 울주군", true, null));
		session.save(new FndRegion("10110101", "울산시 중구", true, null));
		session.save(new FndRegion("11710250", "인천시 강화군", true, null));
		session.save(new FndRegion("11245101", "인천시 계양구", true, null));
		session.save(new FndRegion("11200101", "인천시 남동구", true, null));
		session.save(new FndRegion("11140101", "인천시 동구", true, null));
		session.save(new FndRegion("11177106", "인천시 미추홀구", true, null));
		session.save(new FndRegion("11237101", "인천시 부평구", true, null));
		session.save(new FndRegion("11260101", "인천시 서구", true, null));
		session.save(new FndRegion("11185101", "인천시 연수구", true, null));
		session.save(new FndRegion("11720310", "인천시 옹진군", true, null));
		session.save(new FndRegion("11110101", "인천시 중구", true, null));
		session.save(new FndRegion("12810250", "전라남도 강진군", true, null));
		session.save(new FndRegion("12770250", "전라남도 고흥군", true, null));
		session.save(new FndRegion("12720250", "전라남도 곡성군", true, null));
		session.save(new FndRegion("12230101", "전라남도 광양시", true, null));
		session.save(new FndRegion("12730250", "전라남도 구례군", true, null));
		session.save(new FndRegion("12170101", "전라남도 나주시", true, null));
		session.save(new FndRegion("12710250", "전라남도 담양군", true, null));
		session.save(new FndRegion("12110101", "전라남도 목포시", true, null));
		session.save(new FndRegion("12840250", "전라남도 무안군", true, null));
		session.save(new FndRegion("12780250", "전라남도 보성군", true, null));
		session.save(new FndRegion("12150101", "전라남도 순천시", true, null));
		session.save(new FndRegion("12910250", "전라남도 신안군", true, null));
		session.save(new FndRegion("12130101", "전라남도 여수시", true, null));
		session.save(new FndRegion("12870250", "전라남도 영광군", true, null));
		session.save(new FndRegion("12830250", "전라남도 영암군", true, null));
		session.save(new FndRegion("12890250", "전라남도 완도군", true, null));
		session.save(new FndRegion("12880250", "전라남도 장성군", true, null));
		session.save(new FndRegion("12800250", "전라남도 장흥군", true, null));
		session.save(new FndRegion("12900250", "전라남도 진도군", true, null));
		session.save(new FndRegion("12860250", "전라남도 함평군", true, null));
		session.save(new FndRegion("12820250", "전라남도 해남군", true, null));
		session.save(new FndRegion("12790250", "전라남도 화순군", true, null));
		session.save(new FndRegion("13790250", "전라북도 고창군", true, null));
		session.save(new FndRegion("13130101", "전라북도 군산시", true, null));
		session.save(new FndRegion("13210101", "전라북도 김제시", true, null));
		session.save(new FndRegion("13190101", "전라북도 남원시", true, null));
		session.save(new FndRegion("13730250", "전라북도 무주군", true, null));
		session.save(new FndRegion("13800250", "전라북도 부안군", true, null));
		session.save(new FndRegion("13770250", "전라북도 순창군", true, null));
		session.save(new FndRegion("13710250", "전라북도 완주군", true, null));
		session.save(new FndRegion("13140101", "전라북도 익산시", true, null));
		session.save(new FndRegion("13750250", "전라북도 임실군", true, null));
		session.save(new FndRegion("13740250", "전라북도 장수군", true, null));
		session.save(new FndRegion("13113102", "전라북도 전주시 덕진구", true, null));
		session.save(new FndRegion("13111101", "전라북도 전주시 완산구", true, null));
		session.save(new FndRegion("13180101", "전라북도 정읍시", true, null));
		session.save(new FndRegion("13720250", "전라북도 진안군", true, null));
		session.save(new FndRegion("14130101", "제주도 서귀포시", true, null));
		session.save(new FndRegion("14110101", "제주도 제주시", true, null));
		session.save(new FndRegion("15250101", "충청남도 계룡시", true, null));
		session.save(new FndRegion("15150101", "충청남도 공주시", true, null));
		session.save(new FndRegion("15710250", "충청남도 금산군", true, null));
		session.save(new FndRegion("15230101", "충청남도 논산시", true, null));
		session.save(new FndRegion("15270101", "충청남도 당진시", true, null));
		session.save(new FndRegion("15180101", "충청남도 보령시", true, null));
		session.save(new FndRegion("15760250", "충청남도 부여군", true, null));
		session.save(new FndRegion("15210101", "충청남도 서산시", true, null));
		session.save(new FndRegion("15770250", "충청남도 서천군", true, null));
		session.save(new FndRegion("15200101", "충청남도 아산시", true, null));
		session.save(new FndRegion("15810250", "충청남도 예산군", true, null));
		session.save(new FndRegion("15131101", "충청남도 천안시 동남구", true, null));
		session.save(new FndRegion("15133101", "충청남도 천안시 서북구", true, null));
		session.save(new FndRegion("15790250", "충청남도 청양군", true, null));
		session.save(new FndRegion("15825250", "충청남도 태안군", true, null));
		session.save(new FndRegion("15800250", "충청남도 홍성군", true, null));
		session.save(new FndRegion("16760250", "충청북도 괴산군", true, null));
		session.save(new FndRegion("16800250", "충청북도 단양군", true, null));
		session.save(new FndRegion("16720250", "충청북도 보은군", true, null));
		session.save(new FndRegion("16740250", "충청북도 영동군", true, null));
		session.save(new FndRegion("16730250", "충청북도 옥천군", true, null));
		session.save(new FndRegion("16770250", "충청북도 음성군", true, null));
		session.save(new FndRegion("16150101", "충청북도 제천시", true, null));
		session.save(new FndRegion("16745250", "충청북도 증평군", true, null));
		session.save(new FndRegion("16750250", "충청북도 진천군", true, null));
		session.save(new FndRegion("16111101", "충청북도 청주시 상당구", true, null));
		session.save(new FndRegion("16112101", "충청북도 청주시 서원구", true, null));
		session.save(new FndRegion("16114101", "충청북도 청주시 청원구", true, null));
		session.save(new FndRegion("16113250", "충청북도 청주시 흥덕구", true, null));
		session.save(new FndRegion("16130101", "충청북도 충주시", true, null));
		
		System.out.println("--------------------------------------------------");
		System.out.println("creation finished!!");

		tx.commit();
		session.close();
		
		HibernateUtil.shutdown();
	}

}
