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
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.doohad.models.knl.KnlMedium;
import net.doohad.models.rev.RevAdSelCache;
import net.doohad.models.rev.RevAdSelect;
import net.doohad.models.rev.RevFbSelCache;
import net.doohad.models.rev.RevScrHourlyPlay;
import net.doohad.models.rev.RevScrHrlyFailTot;
import net.doohad.models.rev.RevScrHrlyFbTot;
import net.doohad.models.rev.RevScrHrlyNoAdTot;
import net.doohad.models.rev.RevScrHrlyPlyTot;
import net.doohad.models.rev.RevScrStatusLine;
import net.doohad.utils.Util;

@Entity
@Table(name="INV_SCREENS", uniqueConstraints = {
	@javax.persistence.UniqueConstraint(columnNames = {"MEDIUM_ID", "SHORT_NAME"}),
	@javax.persistence.UniqueConstraint(columnNames = {"MEDIUM_ID", "NAME"}),
})
public class InvScreen {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "inv_screen_seq_gen")
	@Column(name = "SCREEN_ID")
	private int id;
	
	// 화면ID
	@Column(name = "SHORT_NAME", nullable = false, length = 70)
	private String shortName;
	
	// 화면명
	@Column(name = "NAME", nullable = false, length = 220)
	private String name;

	// 서비스중 여부
	@Column(name = "ACTIVE_STATUS", nullable = false)
	private boolean activeStatus; 
	
	// 화면 해상도
	@Column(name = "RESOLUTION", nullable = false, length = 20)
	private String resolution;
	
	// 최저 CPM
	@Column(name = "FLOOR_CPM", nullable = false)
	private int floorCpm = 0;
	
	// 삭제 여부
	//
	//   소프트 삭제 플래그
	//
	@Column(name = "DELETED", nullable = false)
	private boolean deleted;
	
	// 서비스 개시 여부의 판단(lastAdRequestDate null 여부)
	//
	//   이 값에 따라 실제 삭제 대신 소프트 삭제가 수행됨
	//   매체 화면에서는 lastAdRequestDate null 여부로 이 값을 대신함
	//   삭제 요청 시 아래 값처럼 변경하고 실제 삭제는 진행하지 않음
	//
	//        deleted = true
	//        shortName = shortName + '_yyyyMMdd_HHmm'
	//        name = name + '_yyyyMMdd_HHmm'
	//
	
	
	// 기본 및 범위 재생시간 설정은 기본적으로 "매체"에서 진행한다
	//   화면에서 지정되지 않으면, 매체의 값을 그대로 가져간다
	//
	// 기본 재생시간
	@Column(name = "DEFAULT_DUR_SECS")
	private Integer defaultDurSecs;
	
	// 범위 재생시간 허용
	@Column(name = "RANGE_DUR_ALLOWED")
	private Boolean rangeDurAllowed;
	
	// 최저 재생시간
	@Column(name = "MIN_DUR_SECS")
	private Integer minDurSecs;
	
	// 최고 재생시간
	@Column(name = "MAX_DUR_SECS")
	private Integer maxDurSecs;
	

	// 광고 서버용으로 이 화면 사용 가능
	@Column(name = "AD_SERVER_AVAILABLE", nullable = false)
	private boolean adServerAvailable = true; 
	
	
	// 이미지 컨텐츠 유형 허용
	@Column(name = "IMAGE_ALLOWED", nullable = false)
	private boolean imageAllowed; 
	
	// 동영상 컨텐츠 유형 허용
	@Column(name = "VIDEO_ALLOWED", nullable = false)
	private boolean videoAllowed; 


	// 최근 상태
	@Column(name = "LAST_STATUS", length = 1)
	private String lastStatus = "0";
	
	
	// 화면의 운영 시간(24x7=168)
	//
	//   값이 없으면, 매체의 디폴트 운영 시간을 그대로 적용
	//   값이 있으면, 개별 운영 시간 적용(길이는 168바이트)
	//
	@Column(name = "BIZ_HOUR", nullable = false, length = 168)
	private String bizHour = "";
	
	
	// 유효시작일
	@Column(name = "EFFECTIVE_START_DATE", nullable = false)
	private Date effectiveStartDate;
	
	// 유효종료일
	@Column(name = "EFFECTIVE_END_DATE")
	private Date effectiveEndDate;
	
	// 최근 API 동기화 일시
	@Column(name = "API_SYNC_DATE")
	private Date apiSyncDate;
	
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
	
	
	// 최근 파일 목록 요청일시
	@Transient
	private Date lastFileApiDate;

	// 최근 광고 요청일시
	@Transient
	private Date lastAdRequestDate;

	// 최근 광고노출 결과 보고일시
	@Transient
	private Date lastAdReportDate;

	// 최근 광고노출 누락 결과 보고일시
	@Transient
	private Date lastRetryReportDate;

	
	// 다른 개체 연결(S)
	
	// 상위 개체: 매체
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MEDIUM_ID", nullable = false)
	private KnlMedium medium;
	
	// 상위 개체: 사이트
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SITE_ID", nullable = false)
	private InvSite site;
	
	// 하위 개체: 광고 선택
	@OneToMany(mappedBy = "screen", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevAdSelect> adSelects = new HashSet<RevAdSelect>(0);
	
	// 하위 개체: 시간당 화면/광고 재생
	@OneToMany(mappedBy = "screen", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevScrHourlyPlay> scrHourlyPlays = new HashSet<RevScrHourlyPlay>(0);
	
	// 하위 개체: 광고 선택 캐쉬
	@OneToMany(mappedBy = "screen", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevAdSelCache> adSelCaches = new HashSet<RevAdSelCache>(0);
	
	// 하위 개체: 시간당 화면 재생 합계
	@OneToMany(mappedBy = "screen", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevScrHrlyPlyTot> scrHrlyPlyTots = new HashSet<RevScrHrlyPlyTot>(0);
	
	// 하위 개체: 화면 동작 상태 문자열
	@OneToMany(mappedBy = "screen", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevScrStatusLine> scrStatusLines = new HashSet<RevScrStatusLine>(0);
	
	// 하위 개체: 대체 광고 선택 캐쉬
	@OneToMany(mappedBy = "screen", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevFbSelCache> fbSelCaches = new HashSet<RevFbSelCache>(0);
	
	// 하위 개체: 시간당 화면 실패 합계
	@OneToMany(mappedBy = "screen", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevScrHrlyFailTot> scrHrlyFailTots = new HashSet<RevScrHrlyFailTot>(0);
	
	// 하위 개체: 시간당 화면 광고없음 합계
	@OneToMany(mappedBy = "screen", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevScrHrlyNoAdTot> scrHrlyNoAdTots = new HashSet<RevScrHrlyNoAdTot>(0);
	
	// 하위 개체: 시간당 화면 대체광고 합계
	@OneToMany(mappedBy = "screen", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevScrHrlyFbTot> scrHrlyFbTots = new HashSet<RevScrHrlyFbTot>(0);
	
	// 다른 개체 연결(E)
	

	public InvScreen() {}
	
	public InvScreen(InvSite site,
			String shortName, String name, boolean activeStatus, String resolution, 
			boolean imageAllowed, boolean videoAllowed,
			Date effectiveStartDate, Date effectiveEndDate, String memo, HttpSession session) {
		
		this.medium = site.getMedium();
		this.site = site;
		
		this.shortName = shortName;
		this.name = name;
		this.activeStatus = activeStatus;
		this.resolution = resolution;
		this.imageAllowed = imageAllowed;
		this.videoAllowed = videoAllowed;
		
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

	
	public boolean isDurationOverridden() {
		
		boolean overridden = defaultDurSecs != null && defaultDurSecs.intValue() != medium.getDefaultDurSecs();
		overridden = overridden || rangeDurAllowed != null && rangeDurAllowed.booleanValue() != medium.isRangeDurAllowed();
		overridden = overridden || minDurSecs != null && minDurSecs.intValue() != medium.getMinDurSecs();
		overridden = overridden || maxDurSecs != null && maxDurSecs.intValue() != medium.getMaxDurSecs();		
		
		return overridden;
	}
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public KnlMedium getMedium() {
		return medium;
	}

	public void setMedium(KnlMedium medium) {
		this.medium = medium;
	}

	public InvSite getSite() {
		return site;
	}

	public void setSite(InvSite site) {
		this.site = site;
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

	public boolean isActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(boolean activeStatus) {
		this.activeStatus = activeStatus;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
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

	public Integer getDefaultDurSecs() {
		return defaultDurSecs;
	}

	public void setDefaultDurSecs(Integer defaultDurSecs) {
		this.defaultDurSecs = defaultDurSecs;
	}

	public Boolean getRangeDurAllowed() {
		return rangeDurAllowed;
	}

	public void setRangeDurAllowed(Boolean rangeDurAllowed) {
		this.rangeDurAllowed = rangeDurAllowed;
	}

	public Integer getMinDurSecs() {
		return minDurSecs;
	}

	public void setMinDurSecs(Integer minDurSecs) {
		this.minDurSecs = minDurSecs;
	}

	public Integer getMaxDurSecs() {
		return maxDurSecs;
	}

	public void setMaxDurSecs(Integer maxDurSecs) {
		this.maxDurSecs = maxDurSecs;
	}

	public Date getLastAdRequestDate() {
		return lastAdRequestDate;
	}

	public void setLastAdRequestDate(Date lastAdRequestDate) {
		this.lastAdRequestDate = lastAdRequestDate;
	}

	public Date getLastAdReportDate() {
		return lastAdReportDate;
	}

	public void setLastAdReportDate(Date lastAdReportDate) {
		this.lastAdReportDate = lastAdReportDate;
	}

	public boolean isAdServerAvailable() {
		return adServerAvailable;
	}

	public void setAdServerAvailable(boolean adServerAvailable) {
		this.adServerAvailable = adServerAvailable;
	}

	public boolean isImageAllowed() {
		return imageAllowed;
	}

	public void setImageAllowed(boolean imageAllowed) {
		this.imageAllowed = imageAllowed;
	}

	public boolean isVideoAllowed() {
		return videoAllowed;
	}

	public void setVideoAllowed(boolean videoAllowed) {
		this.videoAllowed = videoAllowed;
	}

	public Date getLastFileApiDate() {
		return lastFileApiDate;
	}

	public void setLastFileApiDate(Date lastFileApiDate) {
		this.lastFileApiDate = lastFileApiDate;
	}

	public String getLastStatus() {
		return lastStatus;
	}

	public void setLastStatus(String lastStatus) {
		this.lastStatus = lastStatus;
	}

	@JsonIgnore
	public Set<RevAdSelect> getAdSelects() {
		return adSelects;
	}

	public void setAdSelects(Set<RevAdSelect> adSelects) {
		this.adSelects = adSelects;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@JsonIgnore
	public Set<RevScrHourlyPlay> getScrHourlyPlays() {
		return scrHourlyPlays;
	}

	public void setScrHourlyPlays(Set<RevScrHourlyPlay> scrHourlyPlays) {
		this.scrHourlyPlays = scrHourlyPlays;
	}

	@JsonIgnore
	public Set<RevAdSelCache> getAdSelCaches() {
		return adSelCaches;
	}

	public void setAdSelCaches(Set<RevAdSelCache> adSelCaches) {
		this.adSelCaches = adSelCaches;
	}

	public String getBizHour() {
		return bizHour;
	}

	public void setBizHour(String bizHour) {
		this.bizHour = bizHour;
	}

	@JsonIgnore
	public Set<RevScrHrlyPlyTot> getScrHrlyPlyTots() {
		return scrHrlyPlyTots;
	}

	public void setScrHrlyPlyTots(Set<RevScrHrlyPlyTot> scrHrlyPlyTots) {
		this.scrHrlyPlyTots = scrHrlyPlyTots;
	}

	@JsonIgnore
	public Set<RevScrStatusLine> getScrStatusLines() {
		return scrStatusLines;
	}

	public void setScrStatusLines(Set<RevScrStatusLine> scrStatusLines) {
		this.scrStatusLines = scrStatusLines;
	}

	public Date getApiSyncDate() {
		return apiSyncDate;
	}

	public void setApiSyncDate(Date apiSyncDate) {
		this.apiSyncDate = apiSyncDate;
	}

	@JsonIgnore
	public Set<RevFbSelCache> getFbSelCaches() {
		return fbSelCaches;
	}

	public void setFbSelCaches(Set<RevFbSelCache> fbSelCaches) {
		this.fbSelCaches = fbSelCaches;
	}

	@JsonIgnore
	public Set<RevScrHrlyFailTot> getScrHrlyFailTots() {
		return scrHrlyFailTots;
	}

	public void setScrHrlyFailTots(Set<RevScrHrlyFailTot> scrHrlyFailTots) {
		this.scrHrlyFailTots = scrHrlyFailTots;
	}

	@JsonIgnore
	public Set<RevScrHrlyNoAdTot> getScrHrlyNoAdTots() {
		return scrHrlyNoAdTots;
	}

	public void setScrHrlyNoAdTots(Set<RevScrHrlyNoAdTot> scrHrlyNoAdTots) {
		this.scrHrlyNoAdTots = scrHrlyNoAdTots;
	}

	@JsonIgnore
	public Set<RevScrHrlyFbTot> getScrHrlyFbTots() {
		return scrHrlyFbTots;
	}

	public void setScrHrlyFbTots(Set<RevScrHrlyFbTot> scrHrlyFbTots) {
		this.scrHrlyFbTots = scrHrlyFbTots;
	}

	public Date getLastRetryReportDate() {
		return lastRetryReportDate;
	}

	public void setLastRetryReportDate(Date lastRetryReportDate) {
		this.lastRetryReportDate = lastRetryReportDate;
	}

	public int getFloorCpm() {
		return floorCpm;
	}

	public void setFloorCpm(int floorCpm) {
		this.floorCpm = floorCpm;
	}

}
