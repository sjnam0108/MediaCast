package net.doohad.models.knl;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.doohad.models.adc.AdcAd;
import net.doohad.models.adc.AdcAdCreative;
import net.doohad.models.adc.AdcAdTarget;
import net.doohad.models.adc.AdcCampaign;
import net.doohad.models.adc.AdcCreatFile;
import net.doohad.models.adc.AdcCreatTarget;
import net.doohad.models.adc.AdcCreative;
import net.doohad.models.adn.AdnExcelRow;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.inv.InvSite;
import net.doohad.models.org.OrgAdvertiser;
import net.doohad.models.org.OrgMediumOpt;
import net.doohad.models.org.OrgPlTarget;
import net.doohad.models.org.OrgPlaylist;
import net.doohad.models.rev.RevAdSelect;
import net.doohad.models.rev.RevCreatDecn;
import net.doohad.models.rev.RevCreatHourlyPlay;
import net.doohad.models.rev.RevAdHourlyPlay;
import net.doohad.models.rev.RevInvenRequest;
import net.doohad.models.rev.RevScrHourlyPlay;
import net.doohad.models.rev.RevScrHrlyFailTot;
import net.doohad.models.rev.RevScrHrlyFbTot;
import net.doohad.models.rev.RevScrHrlyNoAdTot;
import net.doohad.models.rev.RevScrHrlyPlyTot;
import net.doohad.models.rev.RevSitHrlyPlyTot;
import net.doohad.utils.Util;

@Entity
@Table(name="KNL_MEDIA")
public class KnlMedium {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "knl_medium_seq_gen")
	@Column(name = "MEDIUM_ID")
	private int id;
	
	// 매체ID
	@Column(name = "SHORT_NAME", nullable = false, length = 50, unique = true)
	private String shortName;
	
	// 매체명
	@Column(name = "NAME", nullable = false, length = 200)
	private String name;
	
	// 화면 해상도
	//
	//     매체에 포함된 화면의 해상도 크기 목록을 구분자로 구분
	//     화면에 대한 전체 목록에서 선택된 항목으로 제한
	//       ex) 1080x1920|3840x1080
	//
	@Column(name = "RESOLUTIONS", nullable = false, length = 200)
	private String resolutions;
	
	// API 키
	//
	//   매체 화면에서 광고 서버 연결 시 매체의 소속임을 증명할 수 있는 문자열, 총 22 bytes
	//
	@Column(name = "API_KEY", nullable = false, length = 22, unique = true)
	private String apiKey = "";
	
	
	// 기본 재생시간
	@Column(name = "DEFAULT_DUR_SECS", nullable = false)
	private int defaultDurSecs = 15;
	
	// 범위 재생시간 허용
	@Column(name = "RANGE_DUR_ALLOWED", nullable = false)
	private boolean rangeDurAllowed = false;
	
	// 최저 재생시간
	@Column(name = "MIN_DUR_SECS", nullable = false)
	private int minDurSecs = 10;
	
	// 최고 재생시간
	@Column(name = "MAX_DUR_SECS", nullable = false)
	private int maxDurSecs = 20;
	
	
	// 매체 디폴트 운영 시간(24x7=168)
	@Column(name = "BIZ_HOUR", nullable = false, length = 168)
	private String bizHour = "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
	
	
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
	
	
	// 매체 1주일 운영 시간
	@Transient
	private int bizHours = 0;
	
	
	// 다른 개체 연결(S)
	
	// 하위 개체: 사이트
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<InvSite> sites = new HashSet<InvSite>(0);
	
	// 하위 개체: 화면
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<InvScreen> screens = new HashSet<InvScreen>(0);
	
	// 하위 개체: 업로드 임시 저장
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<AdnExcelRow> excelRows = new HashSet<AdnExcelRow>(0);
	
	// 하위 개체: 광고 소재
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<AdcCreative> creatives = new HashSet<AdcCreative>(0);
	
	// 하위 개체: 광고 소재 파일
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<AdcCreatFile> creatFiles = new HashSet<AdcCreatFile>(0);
	
	// 하위 개체: 광고 선택
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevAdSelect> adSelects = new HashSet<RevAdSelect>(0);
	
	// 하위 개체: 캠페인
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<AdcCampaign> campaigns = new HashSet<AdcCampaign>(0);
	
	// 하위 개체: 광고
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<AdcAd> ads = new HashSet<AdcAd>(0);
	
	// 하위 개체: 광고 방송 소재
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<AdcAdCreative> adCreatives = new HashSet<AdcAdCreative>(0);
	
	// 하위 개체: 시간당 화면/광고 재생
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevScrHourlyPlay> scrHourlyPlays = new HashSet<RevScrHourlyPlay>(0);
	
	// 하위 개체: 광고 소재 승인/거절 판단
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevCreatDecn> creatDecns = new HashSet<RevCreatDecn>(0);
	
	// 하위 개체: 광고 소재 인벤토리 타겟팅
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<AdcCreatTarget> creatTargets = new HashSet<AdcCreatTarget>(0);
	
	// 하위 개체: 매체 옵션
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<OrgMediumOpt> mediumOpts = new HashSet<OrgMediumOpt>(0);
	
	// 하위 개체: 재생 목록
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<OrgPlaylist> playlists = new HashSet<OrgPlaylist>(0);
	
	// 하위 개체: 재생 목록
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<OrgPlTarget> plTargets = new HashSet<OrgPlTarget>(0);
	
	// 하위 개체: 광고 인벤토리 타겟팅
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<AdcAdTarget> adTargets = new HashSet<AdcAdTarget>(0);
	
	// 하위 개체: 시간당 화면 재생 합계
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevScrHrlyPlyTot> scrHrlyPlyTots = new HashSet<RevScrHrlyPlyTot>(0);
	
	// 하위 개체: 시간당 사이트 재생 합계
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevSitHrlyPlyTot> sitHrlyPlyTots = new HashSet<RevSitHrlyPlyTot>(0);
	
	// 하위 개체: 인벤 요청
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevInvenRequest> invenRequests = new HashSet<RevInvenRequest>(0);
	
	// 하위 개체: 인벤 요청
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<OrgAdvertiser> advertisers = new HashSet<OrgAdvertiser>(0);
	
	// 하위 개체: 시간당 화면 실패 합계
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevScrHrlyFailTot> scrHrlyFailTots = new HashSet<RevScrHrlyFailTot>(0);
	
	// 하위 개체: 시간당 화면 광고없음 합계
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevScrHrlyNoAdTot> scrHrlyNoAdTots = new HashSet<RevScrHrlyNoAdTot>(0);
	
	// 하위 개체: 시간당 화면 대체광고 합계
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevScrHrlyFbTot> scrHrlyFbTots = new HashSet<RevScrHrlyFbTot>(0);
	
	// 하위 개체: 일별/광고별 하루 재생
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevAdHourlyPlay> hourlyPlays = new HashSet<RevAdHourlyPlay>(0);
	
	// 하위 개체: 일별/광고 소재별 하루 재생
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevCreatHourlyPlay> creatHourlyPlays = new HashSet<RevCreatHourlyPlay>(0);
	
	// 다른 개체 연결(E)

	
	public KnlMedium() {}
	
	public KnlMedium(String shortName, String name, String resolutions, String apiKey,
			int defaultDurSecs, boolean rangeDurAllowed, int minDurSecs, int maxDurSecs,
			Date effectiveStartDate, Date effectiveEndDate, String memo, HttpSession session) {
		
		this.shortName = shortName;
		this.name = name;
		this.resolutions = resolutions;
		this.apiKey = apiKey;
		this.defaultDurSecs = defaultDurSecs;
		this.rangeDurAllowed = rangeDurAllowed;
		this.minDurSecs = minDurSecs;
		this.maxDurSecs = maxDurSecs;
		
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

	public String getResolutions() {
		return resolutions;
	}

	public void setResolutions(String resolutions) {
		this.resolutions = resolutions;
	}

	@JsonIgnore
	public Set<InvSite> getSites() {
		return sites;
	}

	public void setSites(Set<InvSite> sites) {
		this.sites = sites;
	}

	@JsonIgnore
	public Set<InvScreen> getScreens() {
		return screens;
	}

	public void setScreens(Set<InvScreen> screens) {
		this.screens = screens;
	}

	public int getDefaultDurSecs() {
		return defaultDurSecs;
	}

	public void setDefaultDurSecs(int defaultDurSecs) {
		this.defaultDurSecs = defaultDurSecs;
	}

	public boolean isRangeDurAllowed() {
		return rangeDurAllowed;
	}

	public void setRangeDurAllowed(boolean rangeDurAllowed) {
		this.rangeDurAllowed = rangeDurAllowed;
	}

	public int getMinDurSecs() {
		return minDurSecs;
	}

	public void setMinDurSecs(int minDurSecs) {
		this.minDurSecs = minDurSecs;
	}

	public int getMaxDurSecs() {
		return maxDurSecs;
	}

	public void setMaxDurSecs(int maxDurSecs) {
		this.maxDurSecs = maxDurSecs;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	@JsonIgnore
	public Set<AdnExcelRow> getExcelRows() {
		return excelRows;
	}

	public void setExcelRows(Set<AdnExcelRow> excelRows) {
		this.excelRows = excelRows;
	}

	@JsonIgnore
	public Set<AdcCreative> getCreatives() {
		return creatives;
	}

	public void setCreatives(Set<AdcCreative> creatives) {
		this.creatives = creatives;
	}

	@JsonIgnore
	public Set<AdcCreatFile> getCreatFiles() {
		return creatFiles;
	}

	public void setCreatFiles(Set<AdcCreatFile> creatFiles) {
		this.creatFiles = creatFiles;
	}

	@JsonIgnore
	public Set<RevAdSelect> getAdSelects() {
		return adSelects;
	}

	public void setAdSelects(Set<RevAdSelect> adSelects) {
		this.adSelects = adSelects;
	}

	@JsonIgnore
	public Set<AdcCampaign> getCampaigns() {
		return campaigns;
	}

	public void setCampaigns(Set<AdcCampaign> campaigns) {
		this.campaigns = campaigns;
	}

	@JsonIgnore
	public Set<AdcAd> getAds() {
		return ads;
	}

	public void setAds(Set<AdcAd> ads) {
		this.ads = ads;
	}

	@JsonIgnore
	public Set<AdcAdCreative> getAdCreatives() {
		return adCreatives;
	}

	public void setAdCreatives(Set<AdcAdCreative> adCreatives) {
		this.adCreatives = adCreatives;
	}

	@JsonIgnore
	public Set<RevScrHourlyPlay> getScrHourlyPlays() {
		return scrHourlyPlays;
	}

	public void setScrHourlyPlays(Set<RevScrHourlyPlay> scrHourlyPlays) {
		this.scrHourlyPlays = scrHourlyPlays;
	}

	@JsonIgnore
	public Set<RevCreatDecn> getCreatDecns() {
		return creatDecns;
	}

	public void setCreatDecns(Set<RevCreatDecn> creatDecns) {
		this.creatDecns = creatDecns;
	}

	@JsonIgnore
	public Set<AdcCreatTarget> getCreatTargets() {
		return creatTargets;
	}

	public void setCreatTargets(Set<AdcCreatTarget> creatTargets) {
		this.creatTargets = creatTargets;
	}

	@JsonIgnore
	public Set<OrgMediumOpt> getMediumOpts() {
		return mediumOpts;
	}

	public void setMediumOpts(Set<OrgMediumOpt> mediumOpts) {
		this.mediumOpts = mediumOpts;
	}

	@JsonIgnore
	public Set<OrgPlaylist> getPlaylists() {
		return playlists;
	}

	public void setPlaylists(Set<OrgPlaylist> playlists) {
		this.playlists = playlists;
	}

	@JsonIgnore
	public Set<OrgPlTarget> getPlTargets() {
		return plTargets;
	}

	public void setPlTargets(Set<OrgPlTarget> plTargets) {
		this.plTargets = plTargets;
	}

	@JsonIgnore
	public Set<AdcAdTarget> getAdTargets() {
		return adTargets;
	}

	public void setAdTargets(Set<AdcAdTarget> adTargets) {
		this.adTargets = adTargets;
	}

	public String getBizHour() {
		return bizHour;
	}

	public void setBizHour(String bizHour) {
		this.bizHour = bizHour;
	}

	public int getBizHours() {
		return bizHours;
	}

	public void setBizHours(int bizHours) {
		this.bizHours = bizHours;
	}

	@JsonIgnore
	public Set<RevScrHrlyPlyTot> getScrHrlyPlyTots() {
		return scrHrlyPlyTots;
	}

	public void setScrHrlyPlyTots(Set<RevScrHrlyPlyTot> scrHrlyPlyTots) {
		this.scrHrlyPlyTots = scrHrlyPlyTots;
	}

	@JsonIgnore
	public Set<RevSitHrlyPlyTot> getSitHrlyPlyTots() {
		return sitHrlyPlyTots;
	}

	public void setSitHrlyPlyTots(Set<RevSitHrlyPlyTot> sitHrlyPlyTots) {
		this.sitHrlyPlyTots = sitHrlyPlyTots;
	}

	@JsonIgnore
	public Set<RevInvenRequest> getInvenRequests() {
		return invenRequests;
	}

	public void setInvenRequests(Set<RevInvenRequest> invenRequests) {
		this.invenRequests = invenRequests;
	}

	@JsonIgnore
	public Set<OrgAdvertiser> getAdvertisers() {
		return advertisers;
	}

	public void setAdvertisers(Set<OrgAdvertiser> advertisers) {
		this.advertisers = advertisers;
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

	@JsonIgnore
	public Set<RevAdHourlyPlay> getHourlyPlays() {
		return hourlyPlays;
	}

	public void setHourlyPlays(Set<RevAdHourlyPlay> hourlyPlays) {
		this.hourlyPlays = hourlyPlays;
	}

	@JsonIgnore
	public Set<RevCreatHourlyPlay> getCreatHourlyPlays() {
		return creatHourlyPlays;
	}

	public void setCreatHourlyPlays(Set<RevCreatHourlyPlay> creatHourlyPlays) {
		this.creatHourlyPlays = creatHourlyPlays;
	}

}
