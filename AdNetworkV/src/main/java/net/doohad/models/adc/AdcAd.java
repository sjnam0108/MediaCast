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
import net.doohad.models.rev.RevAdHourlyPlay;
import net.doohad.models.rev.RevScrHourlyPlay;
import net.doohad.utils.Util;

@Entity
@Table(name="ADC_ADS", uniqueConstraints = {
	@javax.persistence.UniqueConstraint(columnNames = {"MEDIUM_ID", "NAME"}),
})
public class AdcAd {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AD_ID")
	private int id;
	
	// 광고명
	@Column(name = "NAME", nullable = false, length = 200)
	private String name;
	
	// 최근 상태
	//  
	//   광고의 승인 프로세스에 대한 최근 상태를 가짐.
	//   가능한 상태로는,
	//
	//     - D		준비		Draft
	//     - P		승인대기	Pending approval (publishers only)
	//     - A		예약		Active
	//     - R		진행		Running
	//     - C      완료        Completed
	//     - J		거절		Rejected (publishers only)
	//
	//     - V      보관		Archived(변경 불가, 관계 설정 불가)
	//     - T      삭제		Deleted
	//
	//   보관: 변경 불가, 관계 설정 불가, 서비스(파일, 광고) 불가
	//         기존 설정된 관계(광고 - 광고 소재 연결 등)는 끊지 않음
	//         보관 해제시에는 D(Draft) 상태로 변경
	//
	//   시작 종료일에 따라, 혹은 날짜 변경에 따라 A R C 상태 자동 변경 필요
	//
	@Column(name = "STATUS", nullable = false, length = 1)
	private String status = "D";
	
	// 중지 여부
	//
	//   일시적으로 전송이 중지된 상태. 재개(resume)를 통해 원래 상태로 복귀
	//   위의 모든 상태(D P A R C J)에 대해 중지가 가능, 삭제 상태(V T)에서는 불가능
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
	

	// 구매 유형
	//
	//   G		목표 보장
	//   N		목표 비보장
	//   H		하우스 광고
	//
	@Column(name = "PURCH_TYPE", nullable = false, length = 1)
	private String purchType = "G";
	
	// 우선 순위
	//
	//  최고 1에서 최저 10 사이의 값으로, 동일 구매유형 광고 내에서 우선권을 갖게 됨
	//
	@Column(name = "PRIORITY", nullable = false)
	private int priority = 5;
	
	// 재생시간
	//
	//   이 값은 0이거나, 혹은 5이상이어야 함
	//
	//   0		매체 화면에서 결정	- 화면 혹은 매체의 재생 시간 설정값에 따라 다운로드 및 송출
	//   >=5    고정 값				- 매체 회면의 여러 설정(기본/최소/최대 등)보다 먼저 적용
	//
	//  값						컨텐츠		15(범위재생X)		15(10-20)		20(20-30)
	//  ------------------------------------------------------------------------------------
	//	화면 설정값 기준		9s V
	//							10s V							10
	//							15s V		15					15
	//							20s V							20				20
	//							27s V											27
	//							30s V											30
	//							31s V
	//							I			15					15				20
	//  ------------------------------------------------------------------------------------
	// 	고정값					9s V		9					9				9
	//							10s V		10					10				10
	//							27s V		27					27				27
	//							13s I		13					13				13
	//							30s I		30					30				30
	//  ------------------------------------------------------------------------------------
	//
	//   설정 화면에서는 재생시간 유형 Selector가 표시되며, 선택가능한 항목은 다음과 같음
	//
	//       - 매체 화면에서 결정(0)
	//       - 값 고정(>=5)
	//
	@Column(name = "DURATION", nullable = false)
	private int duration = 0;
	
	
	// 광고 시작일과 종료일
	//
	//   시간 값은 없음
	//
	// 시작일
	@Column(name = "START_DATE", nullable = false)
	private Date startDate;
	
	// 종료일
	@Column(name = "END_DATE", nullable = false)
	private Date endDate;
	

	// --- (설명 삭제 예정)
	// 전송 목표 유형
	//
	//   A		광고 예산(AD spend)
	//   I		전체 노출량(Impressions)
	//   J		화면당 1일 노출
	//   U		무제한 노출(Unlimited budget)
	// ------
	// 집행 방법
	//
	//   사용자에 의한 직접적인 수정 불가. 대신 "보장 노출횟수"와 "예산" 값에 따라 이 값이 달라짐
	//
	//   A		광고예산(AD spend)
	//   I		노출횟수(Impressions)
	//   U		무제한 노출(Unlimited budget)
	//
	//   if "목표 노출횟수" > 0 || "보장 노출횟수" > 0  --> 노출횟수(I)
	//   else if "예산" > 0      --> 광고예산(A)
	//   else                    --> 무제한 노출(U)
	//
	@Column(name = "GOAL_TYPE", nullable = false, length = 1)
	private String goalType = "U";
	
	// --- (설명 삭제 예정)
	// 목표 값
	//
	//   전송 목표의 설정에 따라, 광고 예산 혹은 노출량의 목표값을 가지거나, 0(무제한 예산)이어야 함
	//   무제한 예산이 아닌 A / I / J 에서는 이 값이 0일 수 없음
	//
	// ---
	// 보장 노출횟수
	// 
	//   계약된, 혹은 공식적으로 보장되는 노출 횟수.
	//   이 값이 "예산"값보다 우선 순위가 높으며, 이 값이 입력되면(0초과) 무조건 노출횟수 기반으로
	//   이 광고는 진행된다.
	//   이 값이 0이면, "예산" 기반 진행, 혹은 "무제한 노출" 진행
	// 
	@Column(name = "GOAL_VALUE", nullable = false)
	private int goalValue = 0;
	
	// --- (설명 삭제 예정)
	// 1일 한도
	//
	//  전송 목표에 따른 1일 한도 값으로, 1일 광고 예산 한도 혹은 1일 노출량 한도가 됨
	//  무제한 예산 목표일 경우 이 값의 기반은 광고 예산 비용임
	//  0일 경우 1일 한도가 없다는 의미
	//
	// -----
	// 하루 노출한도
	//
	//  전체 화면의 하루 노출한도 값. 이 값은 광고 금액을 표시하지 않으며, 오직 노출 횟수의 값을 가짐
	//  0일 경우 하루 노출한도가 없다는 의미
	// 
	@Column(name = "DAILY_CAP", nullable = false)
	private int dailyCap = 0;
	
	
	// 예산
	@Column(name = "BUDGET", nullable = false)
	private int budget = 0;
	
	// 목표 노출횟수
	//
	//   솔루션(시스템)에서 우선적으로 노출횟수의 목표로 설정되는 값
	//
	//   "보장 노출횟수"보다 보통은 큰 값이며, 광고 선출 후 노출까지의 상태 변환 단계에서의 오차를
	//   줄이기 위해 솔루션(시스템)에서 "보장 노출횟수"보다 우선적으로 채택되는 값
	//
	@Column(name = "SYS_VALUE", nullable = false)
	private int sysValue = 0;
	
	// 화면당 하루 노출한도
	//
	//  한 화면의 하루 노출한도 값. 이 값은 광고 금액을 표시하지 않으며, 오직 노출 횟수의 값을 가짐
	//  0일 경우 화면당 하루 노출한도가 없다는 의미
	// 
	@Column(name = "DAILY_SCR_CAP", nullable = false)
	private int dailyScrCap = 0;
	
	// 집행 노출횟수
	//
	//   목표로 설정한 노출횟수(목표 혹은 보장)에 대한 실제 진행된 노출 횟수
	//   목표 및 보장 노출횟수가 0이라도, 이 값은 0 초과의 실제 값을 가지게 됨
	//
	@Column(name = "ACTUAL_VALUE", nullable = false)
	private int actualValue = 0;
	
	// 집행 금액
	//
	//   예산이 입력되지 않았다고 하더라도, 화면 혹은 광고의 CPM 설정에 의해 이 금액을 계산하는 것이 가능
	//
	@Column(name = "ACTUAL_AMOUNT", nullable = false)
	private int actualAmount = 0;
	
	// 집행 CPM
	//
	//   집행 금액과 집행 노출횟수를 통한 계산값
	//
	@Column(name = "ACTUAL_CPM", nullable = false)
	private int actualCpm = 0;
	
	// 달성률
	//
	//   %값으로, 보통은 100에 수렴함. 집행정책에 따라 그 대상의 항목이 다름.
	//
	//   if 집행정책 == 노출횟수, 
	//      if 보장 노출횟수 == 0, 달성률 == 0
	//      else 집행 노출횟수 / 보장 노출횟수
	//   else if 집행정책 == 광고예산, 집행 금액 / 예산
	//   else 달성률 == 0
	//     
	@Column(name = "ACHV_RATIO", nullable = false)
	private double achvRatio = 0;

	// 오늘 목표
	//
	//   진행중인 광고에만 계산에 의해 설정되는 값
	//   집행정책에 따라 단위가 노출횟수(노출횟수, 무제한 노출) 혹은 금액(광고 예산)이 됨
	//
	//   if 집행정책 == 노출횟수, (목표or보장 노출횟수 - 집행 노출횟수) / (종료일까지 잔여일수)
	//   else if 집행정책 == 광고예산, (예산 - 집행 금액) / (종료일까지 잔여일수)
	//   else (집행정책 == 무제한 노출) 오늘 목표 == 0 
	//
	@Column(name = "TGT_TODAY", nullable = false)
	private int tgtToday = 0;

	
	// CPM
	//
	//   원 단위 값으로, 0이상이어야 하나, -1도 가능(매체 화면 설정값 적용)
	//
	//   설정 화면에서는 CPM 유형 Selector가 표시되며, 선택가능한 항목은 다음과 같음
	//
	//       - 매체 화면 설정값(-1)
	//       - 값 고정(>=0)
	//
	@Column(name = "CPM", nullable = false)
	private int cpm = 0;
	
	// 동일 광고 송출 금지 시간
	//
	//  동일 광고가 방송되지 않도록 유지될 수 있는 초단위 시간
	//
	@Column(name = "FREQ_CAP", nullable = false)
	private int freqCap = 0;
	
	
	// 화면의 노출 시간 타겟팅(24x7=168)
	//
	//   값이 없으면, 별도 시간 타겟팅 없음
	//   값이 있으면, 시간 타겟팅 적용(길이는 168바이트)
	//
	@Column(name = "EXP_HOUR", nullable = false, length = 168)
	private String expHour = "";
	
	// 운영자 메모
	@Column(name = "MEMO", length = 300)
	private String memo = "";


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
	
	
	// 광고 소재 수
	@Transient
	private int creativeCount = 0;
	
	// 인벤토리 타겟팅 존재 여부
	@Transient
	private boolean invenTargeted;

	// 광고의 상태 카드 문자열(옐로, 레드 카드 처리를 위한)
	@Transient
	private String statusCard = "";
	
	// 모든 소재의 해상도
	@Transient
	private String resolutions = "";

	
	// 다른 개체 연결(S)
	
	// 상위 개체: 매체
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MEDIUM_ID", nullable = false)
	private KnlMedium medium;
	
	// 상위 개체: 캠페인
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CAMPAIGN_ID", nullable = false)
	private AdcCampaign campaign;

	// 하위 개체: 광고 방송 소재
	@OneToMany(mappedBy = "ad", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<AdcAdCreative> adCreatives = new HashSet<AdcAdCreative>(0);
	
	// 하위 개체: 시간당 화면/광고 재생
	@OneToMany(mappedBy = "ad", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevScrHourlyPlay> scrHourlyPlays = new HashSet<RevScrHourlyPlay>(0);
	
	// 하위 개체: 광고 인벤토리 타겟팅
	@OneToMany(mappedBy = "ad", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<AdcAdTarget> adTargets = new HashSet<AdcAdTarget>(0);
	
	// 하위 개체: 시간당 광고 재생
	@OneToMany(mappedBy = "ad", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevAdHourlyPlay> adHourlyPlays = new HashSet<RevAdHourlyPlay>(0);
	
	// 다른 개체 연결(E)

	
	public AdcAd() {}
	
	public AdcAd(AdcCampaign campaign, String name, String purchType, 
			Date startDate, Date endDate, HttpSession session) {
		
		this.medium = campaign.getMedium();
		this.campaign = campaign;
		
		this.name = name;
		this.purchType = purchType;
		this.startDate = startDate;
		this.endDate = endDate;
		this.memo = "";
		
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

	// 광고 상세 폼에서
	public String getDispRegDate() {
		
		if (Util.isToday(this.whoCreationDate)) {
			return Util.toSimpleString(this.whoCreationDate, "HH:mm:ss");
		} else if (Util.isThisYear(this.whoCreationDate)) {
			return "<small>" + Util.toSimpleString(this.whoCreationDate, "yyyy") + "</small> " + Util.toSimpleString(this.whoCreationDate, "M/d");
		} else {
			return Util.toSimpleString(this.whoCreationDate, "yyyy M/d");
		}
	}
	
	public long getStartDateLong() {
		return startDate.getTime();
	}
	
	public long getEndDateLong() {
		return endDate.getTime();
	}

	public boolean isStartDateEditable() {
		return status.equals("D") || status.equals("P") || status.equals("A") || status.equals("J");
	}
	
	public String getDispPeriod() {
		if (startDate == null || endDate == null) {
			return "-";
		} else if (Util.isSameDate(startDate, endDate)) {
			if (Util.isThisYear(startDate)) {
				return "<small>" + Util.toSimpleString(startDate, "yyyy") + "</small> " + Util.toSimpleString(startDate, "M/d");
			} else {
				return Util.toSimpleString(startDate, "yyyy M/d");
			}
		} else if (Util.isSameYear(startDate, endDate)) {
			if (Util.isThisYear(startDate)) {
				return "<small>" + Util.toSimpleString(startDate, "yyyy") + "</small> " + Util.toSimpleString(startDate, "M/d") + 
						" ~ " + Util.toSimpleString(endDate, "M/d");
			} else {
				return Util.toSimpleString(startDate, "yyyy M/d") + 
						" ~ " + Util.toSimpleString(endDate, "M/d");
			}
		} else {
			return Util.toSimpleString(startDate, "yyyy M/d") + 
					" ~ " + Util.toSimpleString(endDate, "yyyy M/d");
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

	public String getPurchType() {
		return purchType;
	}

	public void setPurchType(String purchType) {
		this.purchType = purchType;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getGoalType() {
		return goalType;
	}

	public void setGoalType(String goalType) {
		this.goalType = goalType;
	}

	public int getGoalValue() {
		return goalValue;
	}

	public void setGoalValue(int goalValue) {
		this.goalValue = goalValue;
	}

	public int getDailyCap() {
		return dailyCap;
	}

	public void setDailyCap(int dailyCap) {
		this.dailyCap = dailyCap;
	}

	public int getCpm() {
		return cpm;
	}

	public void setCpm(int cpm) {
		this.cpm = cpm;
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

	public KnlMedium getMedium() {
		return medium;
	}

	public void setMedium(KnlMedium medium) {
		this.medium = medium;
	}

	public AdcCampaign getCampaign() {
		return campaign;
	}

	public void setCampaign(AdcCampaign campaign) {
		this.campaign = campaign;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public int getCreativeCount() {
		return creativeCount;
	}

	public void setCreativeCount(int creativeCount) {
		this.creativeCount = creativeCount;
	}

	@JsonIgnore
	public Set<RevScrHourlyPlay> getScrHourlyPlays() {
		return scrHourlyPlays;
	}

	public void setScrHourlyPlays(Set<RevScrHourlyPlay> scrHourlyPlays) {
		this.scrHourlyPlays = scrHourlyPlays;
	}

	@JsonIgnore
	public Set<AdcAdTarget> getAdTargets() {
		return adTargets;
	}

	public void setAdTargets(Set<AdcAdTarget> adTargets) {
		this.adTargets = adTargets;
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

	public String getStatusCard() {
		return statusCard;
	}

	public void setStatusCard(String statusCard) {
		this.statusCard = statusCard;
	}

	public int getFreqCap() {
		return freqCap;
	}

	public void setFreqCap(int freqCap) {
		this.freqCap = freqCap;
	}

	public String getResolutions() {
		return resolutions;
	}

	public void setResolutions(String resolutions) {
		this.resolutions = resolutions;
	}

	@JsonIgnore
	public Set<RevAdHourlyPlay> getAdHourlyPlays() {
		return adHourlyPlays;
	}

	public void setAdHourlyPlays(Set<RevAdHourlyPlay> adHourlyPlays) {
		this.adHourlyPlays = adHourlyPlays;
	}

	public int getBudget() {
		return budget;
	}

	public void setBudget(int budget) {
		this.budget = budget;
	}

	public int getSysValue() {
		return sysValue;
	}

	public void setSysValue(int sysValue) {
		this.sysValue = sysValue;
	}

	public int getDailyScrCap() {
		return dailyScrCap;
	}

	public void setDailyScrCap(int dailyScrCap) {
		this.dailyScrCap = dailyScrCap;
	}

	public int getActualValue() {
		return actualValue;
	}

	public void setActualValue(int actualValue) {
		this.actualValue = actualValue;
	}

	public int getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(int actualAmount) {
		this.actualAmount = actualAmount;
	}

	public int getActualCpm() {
		return actualCpm;
	}

	public void setActualCpm(int actualCpm) {
		this.actualCpm = actualCpm;
	}

	public double getAchvRatio() {
		return achvRatio;
	}

	public void setAchvRatio(double achvRatio) {
		this.achvRatio = achvRatio;
	}

	public int getTgtToday() {
		return tgtToday;
	}

	public void setTgtToday(int tgtToday) {
		this.tgtToday = tgtToday;
	}

}
