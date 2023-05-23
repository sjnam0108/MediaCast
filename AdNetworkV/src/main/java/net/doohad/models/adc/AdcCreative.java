package net.doohad.models.adc;

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
import net.doohad.models.org.OrgAdvertiser;
import net.doohad.models.rev.RevAdSelect;
import net.doohad.models.rev.RevCreatDecn;
import net.doohad.models.rev.RevCreatHourlyPlay;
import net.doohad.models.rev.RevFbSelCache;
import net.doohad.utils.Util;

@Entity
@Table(name="ADC_CREATIVES")
public class AdcCreative {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CREATIVE_ID")
	private int id;
	
	// 광고 소재명
	@Column(name = "NAME", nullable = false, length = 220)
	private String name;
	
	// 최근 상태
	//  
	//   소재의 승인 프로세스에 대한 최근 상태를 가짐.
	//   가능한 상태로는,
	//
	//     - D		준비		Draft
	//     - P		승인대기	Pending approval
	//     - Y(보류)		일부승인	Partially approved (buyers only)
	//     - A		승인		Approved
	//     - J		거절		Rejected
	//
	//     - V      보관		Archived
	//     - T      삭제		Deleted
	//
	//   보관: 변경 불가, 관계 설정 불가, 서비스(파일, 광고) 불가
	//         기존 설정된 관계(광고 - 광고 소재 연결 등)는 끊지 않음
	//         보관 해제시에는 D(Draft) 상태로 변경
	//
	@Column(name = "STATUS", nullable = false, length = 1)
	private String status = "D";
	
	// 중지 여부
	//
	//   일시적으로 전송이 중지된 상태. 재개(resume)를 통해 원래 상태로 복귀
	//   위의 모든 상태(D P Y A J)에 대해 중지가 가능, 삭제 상태(V T)에서는 불가능
	//
	@Column(name = "PAUSED", nullable = false)
	private boolean paused; 
	
	// 삭제 여부
	//
	//   소프트 삭제 플래그
	//
	//   삭제 요청 시 아래 값처럼 변경하고 실제 삭제는 진행하지 않음
	//
	//        name = name + '_yyyyMMdd_HHmm'
	//
	@Column(name = "DELETED", nullable = false)
	private boolean deleted;

	
	// 승인 요청 일시
	@Column(name = "SUBMIT_DATE")
	private Date submitDate;


	// 광고 소재 유형
	//
	//   C		일반 광고
	//   F		대체 광고
	//
	@Column(name = "TYPE", nullable = false, length = 1)
	private String type = "C";

	// 매체 화면 재생 시간 정책 무시 여부
	@Column(name = "FB_DUR_OVERRIDEN", nullable = false)
	private boolean durPolicyOverriden;
	
	// 대체 광고간 가중치
	@Column(name = "FB_WEIGHT", nullable = false)
	private int fbWeight = 1;
	
	
	// 화면의 노출 시간 타겟팅(24x7=168)
	//
	//   값이 없으면, 별도 시간 타겟팅 없음
	//   값이 있으면, 시간 타겟팅 적용(길이는 168바이트)
	//
	@Column(name = "EXP_HOUR", nullable = false, length = 168)
	private String expHour = "";


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
	
	
	// 소재 파일의 해상도
	@Transient
	private String fileResolutions = "";
	
	// 인벤토리 타겟팅 존재 여부
	@Transient
	private boolean invenTargeted;
	
	// 최근 방송일시
	//
	//   방송의 대상은 광고 소재 파일이며, 하위의 광고 소재 파일이 방송되면 이 값을 터치하는 것으로.
	//
	@Transient
	private Date lastPlayDate;
	
	
	// 다른 개체 연결(S)
	
	// 상위 개체: 매체
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MEDIUM_ID", nullable = false)
	private KnlMedium medium;
	
	// 상위 개체: 광고주
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ADVERTISER_ID", nullable = false)
	private OrgAdvertiser advertiser;
	
	// 하위 개체: 광고 소재 파일
	@OneToMany(mappedBy = "creative", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<AdcCreatFile> creatFiles = new HashSet<AdcCreatFile>(0);
	
	// 하위 개체: 광고 선택
	@OneToMany(mappedBy = "creative", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevAdSelect> adSelects = new HashSet<RevAdSelect>(0);
	
	// 하위 개체: 광고 방송 소재
	@OneToMany(mappedBy = "creative", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<AdcAdCreative> adCreatives = new HashSet<AdcAdCreative>(0);
	
	// 하위 개체: 광고 소재 승인/거절 판단
	@OneToMany(mappedBy = "creative", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevCreatDecn> creatDecns = new HashSet<RevCreatDecn>(0);
	
	// 하위 개체: 광고 소재 인벤토리 타겟팅
	@OneToMany(mappedBy = "creative", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<AdcCreatTarget> creatTargets = new HashSet<AdcCreatTarget>(0);
	
	// 하위 개체: 대체 광고 선택 캐쉬
	@OneToMany(mappedBy = "creative", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevFbSelCache> fbSelCaches = new HashSet<RevFbSelCache>(0);
	
	// 하위 개체: 일별/광고 소재별 하루 재생
	@OneToMany(mappedBy = "creative", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevCreatHourlyPlay> creatHourlyPlays = new HashSet<RevCreatHourlyPlay>(0);
	
	// 다른 개체 연결(E)

	
	public AdcCreative() {}
	
	public AdcCreative(OrgAdvertiser advertiser, String name, String type, 
			boolean durPolicyOverriden, int fbWeight, HttpSession session) {
		this.medium = advertiser.getMedium();
		this.advertiser = advertiser;
		
		this.name = name;
		this.type = type;
		this.durPolicyOverriden = durPolicyOverriden;
		this.fbWeight = fbWeight;
		
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

	
	// 광고 소재 파일 폼에서
	public String getDispRegDate() {
		
		if (Util.isToday(this.whoCreationDate)) {
			return Util.toSimpleString(this.whoCreationDate, "HH:mm:ss");
		} else if (Util.isThisYear(this.whoCreationDate)) {
			return "<small>" + Util.toSimpleString(this.whoCreationDate, "yyyy") + "</small> " + Util.toSimpleString(this.whoCreationDate, "M/d");
		} else {
			return Util.toSimpleString(this.whoCreationDate, "yyyy M/d");
		}
	}

	public boolean isTimeTargeted() {
		
		return Util.isValid(expHour) && expHour.length() == 168;
	}
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isDurPolicyOverriden() {
		return durPolicyOverriden;
	}

	public void setDurPolicyOverriden(boolean durPolicyOverriden) {
		this.durPolicyOverriden = durPolicyOverriden;
	}

	public int getFbWeight() {
		return fbWeight;
	}

	public void setFbWeight(int fbWeight) {
		this.fbWeight = fbWeight;
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

	public OrgAdvertiser getAdvertiser() {
		return advertiser;
	}

	public void setAdvertiser(OrgAdvertiser advertiser) {
		this.advertiser = advertiser;
	}

	@JsonIgnore
	public Set<AdcCreatFile> getCreatFiles() {
		return creatFiles;
	}

	public void setCreatFiles(Set<AdcCreatFile> creatFiles) {
		this.creatFiles = creatFiles;
	}

	public Date getLastPlayDate() {
		return lastPlayDate;
	}

	public void setLastPlayDate(Date lastPlayDate) {
		this.lastPlayDate = lastPlayDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFileResolutions() {
		return fileResolutions;
	}

	public void setFileResolutions(String fileResolutions) {
		this.fileResolutions = fileResolutions;
	}

	@JsonIgnore
	public Set<RevAdSelect> getAdSelects() {
		return adSelects;
	}

	public void setAdSelects(Set<RevAdSelect> adSelects) {
		this.adSelects = adSelects;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@JsonIgnore
	public Set<AdcAdCreative> getAdCreatives() {
		return adCreatives;
	}

	public void setAdCreatives(Set<AdcAdCreative> adCreatives) {
		this.adCreatives = adCreatives;
	}

	public Date getSubmitDate() {
		return submitDate;
	}

	public void setSubmitDate(Date submitDate) {
		this.submitDate = submitDate;
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

	public boolean isInvenTargeted() {
		return invenTargeted;
	}

	public void setInvenTargeted(boolean invenTargeted) {
		this.invenTargeted = invenTargeted;
	}

	public String getExpHour() {
		return expHour;
	}

	public void setExpHour(String expHour) {
		this.expHour = expHour;
	}

	@JsonIgnore
	public Set<RevFbSelCache> getFbSelCaches() {
		return fbSelCaches;
	}

	public void setFbSelCaches(Set<RevFbSelCache> fbSelCaches) {
		this.fbSelCaches = fbSelCaches;
	}

	@JsonIgnore
	public Set<RevCreatHourlyPlay> getCreatHourlyPlays() {
		return creatHourlyPlays;
	}

	public void setCreatHourlyPlays(Set<RevCreatHourlyPlay> creatHourlyPlays) {
		this.creatHourlyPlays = creatHourlyPlays;
	}

}
