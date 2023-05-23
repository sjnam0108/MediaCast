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
import net.doohad.utils.Util;

@Entity
@Table(name="ADC_CAMPAIGNS", uniqueConstraints = {
	@javax.persistence.UniqueConstraint(columnNames = {"MEDIUM_ID", "NAME"}),
})
public class AdcCampaign {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CAMPAIGN_ID")
	private int id;
	
	// 캠페인명
	@Column(name = "NAME", nullable = false, length = 220)
	private String name;
	
	// 최근 상태
	//  
	//   가능한 상태로는,
	//
	//     - U		시작전		Upcoming
	//     - R		진행		Running
	//     - C		완료		Completed
	//
	//     - V      보관		Archived(변경 불가, 관계 설정 불가)
	//     - T      삭제		Deleted
	//
	//   광고에 의해 설정된 시작 종료일에 따라, 혹은 날짜 변경에 따라 U R C 상태 자동 변경 필요
	//
	//   보관 해제시에는 하위의 광고 시작/종료일에 따라 U R C 설정
	//
	@Column(name = "STATUS", nullable = false, length = 1)
	private String status = "U";
	
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
	
	// 동일 광고주 광고 송출 금지 시간
	//
	//  동일 캠페인의 광고가 방송되지 않도록 유지될 수 있는 초단위 시간
	//
	@Column(name = "FREQ_CAP", nullable = false)
	private int freqCap = 0;
	
	
	// 캠페인 시작일과 종료일
	//
	//   시작일과 종료일은 표시 목적이며, 사용자의 입력을 필요로 하지 않는다.
	//   하위에 속하는 광고"들"의 시작/종료일에 따라 코드에 의해 설정된다
	//
	// 시작일
	@Column(name = "START_DATE")
	private Date startDate;
	
	// 종료일
	@Column(name = "END_DATE")
	private Date endDate;
	
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
	
	
	// 광고 수
	@Transient
	private int adCount = 0;

	// 광고의 상태 카드 문자열(옐로, 레드 카드 처리를 위한)
	@Transient
	private String statusCard = "";
	
	
	// 다른 개체 연결(S)
	
	// 상위 개체: 매체
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MEDIUM_ID", nullable = false)
	private KnlMedium medium;
	
	// 상위 개체: 광고주
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ADVERTISER_ID", nullable = false)
	private OrgAdvertiser advertiser;
	
	// 하위 개체: 광고
	@OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<AdcAd> ads = new HashSet<AdcAd>(0);
	
	// 다른 개체 연결(E)

	
	public AdcCampaign() {}
	
	public AdcCampaign(OrgAdvertiser advertiser, String name, int freqCap, 
			String memo, HttpSession session) {
		
		this.medium = advertiser.getMedium();
		this.advertiser = advertiser;
		
		this.name = name;
		this.freqCap = freqCap;
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

	public int getFreqCap() {
		return freqCap;
	}

	public void setFreqCap(int freqCap) {
		this.freqCap = freqCap;
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

	public OrgAdvertiser getAdvertiser() {
		return advertiser;
	}

	public void setAdvertiser(OrgAdvertiser advertiser) {
		this.advertiser = advertiser;
	}

	@JsonIgnore
	public Set<AdcAd> getAds() {
		return ads;
	}

	public void setAds(Set<AdcAd> ads) {
		this.ads = ads;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public int getAdCount() {
		return adCount;
	}

	public void setAdCount(int adCount) {
		this.adCount = adCount;
	}

	public String getStatusCard() {
		return statusCard;
	}

	public void setStatusCard(String statusCard) {
		this.statusCard = statusCard;
	}
}
