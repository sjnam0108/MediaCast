package net.doohad.models.inv;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgSiteCond;
import net.doohad.models.rev.RevSitHrlyPlyTot;
import net.doohad.utils.Util;

@Entity
@Table(name="INV_SITES", uniqueConstraints = {
	@javax.persistence.UniqueConstraint(columnNames = {"MEDIUM_ID", "SHORT_NAME"}),
	@javax.persistence.UniqueConstraint(columnNames = {"MEDIUM_ID", "NAME"}),
})
public class InvSite {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "inv_site_seq_gen")
	@Column(name = "SITE_ID")
	private int id;
	
	// 사이트ID
	@Column(name = "SHORT_NAME", nullable = false, length = 70)
	private String shortName;
	
	// 사이트명
	@Column(name = "NAME", nullable = false, length = 220)
	private String name;

	// 서비스중 여부
	//   - 화면의 activeStatus에 따라 자동 설정됨
	//   - 포함된 화면 중 하나 이상 서비스중이면 서비스중으로 설정, 그외 false
	@Column(name = "ACTIVE_STATUS", nullable = false)
	private boolean activeStatus;
	
	// 삭제 여부
	//
	//   소프트 삭제 플래그
	//
	@Column(name = "DELETED", nullable = false)
	private boolean deleted;
	
	// 서비스 개시 여부
	//
	//   이 값에 따라 실제 삭제 대신 소프트 삭제가 수행됨
	//   삭제 요청 시 아래 값처럼 변경하고 실제 삭제는 진행하지 않음
	//
	//        shortName = shortName + '_yyyyMMdd_HHmm'
	//        name = name + '_yyyyMMdd_HHmm'
	//
	@Column(name = "SERVED", nullable = false)
	private boolean served;
	
	// 사이트에 포함된 화면 수
	@Column(name = "SCREEN_CNT", nullable = false)
	private int screenCount = 0;
	
	// 위도
	@Column(name = "LATITUDE", nullable = false)
	private String latitude = "";
	
	// 경도
	@Column(name = "LONGITUDE", nullable = false)
	private String longitude = "";
	
	// 지역코드(시/군/구 까지의 코드)
	@Column(name = "REGION_CODE", nullable = false, length = 10)
	private String regionCode = "";
	
	// 지역명(시/군/구 이름)
	//
	//   FndRegion과의 관계가 끊어져 있기 때문에 이 컬럼(지역명) 필요
	//
	@Column(name = "REGION_NAME", nullable = false, length = 100)
	private String regionName = "";

	// 최근 상태
	@Column(name = "LAST_STATUS", length = 1)
	private String lastStatus = "0";
	
	
	// 주소(시/군구부터 시작하는 모든 주소 내용)
	@Column(name = "ADDRESS", length = 200)
	private String address;
	

	// 장소 유형
	//
	//    리테일
	//      - 편의점		CVS
	//
	//    옥외
	//      - 버스 쉘터		BUSSH
	//      - 빌딩 전광판	BLDG
	//
	//    현장 진료(POC)
	//      - 병/의원       HOSP
	//
	@Column(name = "VENUE_TYPE", nullable = false, length = 10)
	private String venueType = "";
	
	
	// 유효시작일
	@Column(name = "EFFECTIVE_START_DATE", nullable = false)
	private Date effectiveStartDate;
	
	// 유효종료일
	@Column(name = "EFFECTIVE_END_DATE")
	private Date effectiveEndDate;
	
	// 운영자 메모
	@Column(name = "MEMO", length = 300)
	private String memo;


	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "LAST_UPDATE_DATE", nullable = false)
	private Date whoLastUpdateDate;
	
	@Column(name = "CREATED_BY", nullable = false)
	private int whoCreatedBy;
	
	@Column(name = "LAST_UPDATED_BY", nullable = false)
	private int whoLastUpdatedBy;
	
	@Column(name = "LAST_UPDATE_LOGIN", nullable = false)
	private int whoLastUpdateLogin;
	// WHO 컬럼들(E)
	
	
	// 다른 개체 연결(S)
	
	// 상위 개체: 매체
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MEDIUM_ID", nullable = false)
	private KnlMedium medium;
	
	// 상위 개체: 입지 유형
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SITE_COND_ID", nullable = false)
	private OrgSiteCond siteCond;
	
	// 하위 개체: 화면
	@OneToMany(mappedBy = "site", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<InvScreen> screens = new HashSet<InvScreen>(0);
	
	// 하위 개체: 시간당 사이트 재생 합계
	@OneToMany(mappedBy = "site", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevSitHrlyPlyTot> sitHrlyPlyTots = new HashSet<RevSitHrlyPlyTot>(0);
	
	// 다른 개체 연결(E)

	
	public InvSite() {}
	
	public InvSite(KnlMedium medium,
			String shortName, String name, String latitude, String longitude,
			String regionCode, String address,
			Date effectiveStartDate, Date effectiveEndDate, String memo, HttpSession session) {
		
		this.medium = medium;
		
		this.shortName = shortName;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.regionCode = regionCode;
		this.address = address;
		
		this.effectiveStartDate = Util.removeTimeOfDate(effectiveStartDate == null ? new Date() : effectiveStartDate);
		this.effectiveEndDate = Util.setMaxTimeOfDate(effectiveEndDate);
		this.memo = memo;
		
		touchWhoC(session);
	}

	private void touchWhoC(HttpSession session) {
		this.whoCreatedBy = Util.loginUserId(session);
		this.whoCreationDate = new Date();
		touchWho(session);
	}
	
	public void touchWho(HttpSession session) {
		this.whoLastUpdatedBy = Util.loginUserId(session);
		this.whoLastUpdateDate = new Date();
		this.whoLastUpdateLogin = Util.loginId(session);
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public Date getEffectiveStartDate() {
		return effectiveStartDate;
	}

	public void setEffectiveStartDate(Date effectiveStartDate) {
		this.effectiveStartDate = effectiveStartDate;
	}

	public Date getEffectiveEndDate() {
		return effectiveEndDate;
	}

	public void setEffectiveEndDate(Date effectiveEndDate) {
		this.effectiveEndDate = effectiveEndDate;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public boolean isActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(boolean activeStatus) {
		this.activeStatus = activeStatus;
	}

	@JsonIgnore
	public Set<InvScreen> getScreens() {
		return screens;
	}

	public void setScreens(Set<InvScreen> screens) {
		this.screens = screens;
	}

	public Date getWhoCreationDate() {
		return whoCreationDate;
	}

	public void setWhoCreationDate(Date whoCreationDate) {
		this.whoCreationDate = whoCreationDate;
	}

	public Date getWhoLastUpdateDate() {
		return whoLastUpdateDate;
	}

	public void setWhoLastUpdateDate(Date whoLastUpdateDate) {
		this.whoLastUpdateDate = whoLastUpdateDate;
	}

	public int getWhoCreatedBy() {
		return whoCreatedBy;
	}

	public void setWhoCreatedBy(int whoCreatedBy) {
		this.whoCreatedBy = whoCreatedBy;
	}

	public int getWhoLastUpdatedBy() {
		return whoLastUpdatedBy;
	}

	public void setWhoLastUpdatedBy(int whoLastUpdatedBy) {
		this.whoLastUpdatedBy = whoLastUpdatedBy;
	}

	public int getWhoLastUpdateLogin() {
		return whoLastUpdateLogin;
	}

	public void setWhoLastUpdateLogin(int whoLastUpdateLogin) {
		this.whoLastUpdateLogin = whoLastUpdateLogin;
	}

	public KnlMedium getMedium() {
		return medium;
	}

	public void setMedium(KnlMedium medium) {
		this.medium = medium;
	}

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public int getScreenCount() {
		return screenCount;
	}

	public void setScreenCount(int screenCount) {
		this.screenCount = screenCount;
	}

	public OrgSiteCond getSiteCond() {
		return siteCond;
	}

	public void setSiteCond(OrgSiteCond siteCond) {
		this.siteCond = siteCond;
	}

	public String getLastStatus() {
		return lastStatus;
	}

	public void setLastStatus(String lastStatus) {
		this.lastStatus = lastStatus;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isServed() {
		return served;
	}

	public void setServed(boolean served) {
		this.served = served;
	}

	public String getVenueType() {
		return venueType;
	}

	public void setVenueType(String venueType) {
		this.venueType = venueType;
	}

	@JsonIgnore
	public Set<RevSitHrlyPlyTot> getSitHrlyPlyTots() {
		return sitHrlyPlyTots;
	}

	public void setSitHrlyPlyTots(Set<RevSitHrlyPlyTot> sitHrlyPlyTots) {
		this.sitHrlyPlyTots = sitHrlyPlyTots;
	}

}
