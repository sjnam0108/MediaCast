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
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.doohad.models.knl.KnlMedium;
import net.doohad.models.rev.RevAdSelCache;
import net.doohad.models.rev.RevScrHourlyPlay;
import net.doohad.utils.Util;

@Entity
@Table(name="ADC_AD_CREATIVES")
public class AdcAdCreative {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AD_CREATIVE_ID")
	private int id;
	
	// 삭제 여부
	//
	//   소프트 삭제 플래그
	//
	@Column(name = "DELETED", nullable = false)
	private boolean deleted;
	
	
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

	
	// 대체 광고간 가중치
	@Column(name = "WEIGHT", nullable = false)
	private int weight = 1;


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
	
	// 상위 개체: 광고
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "AD_ID", nullable = false)
	private AdcAd ad;
	
	// 상위 개체: 방송 소재
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CREATIVE_ID", nullable = false)
	private AdcCreative creative;
	
	// 하위 개체: 시간당 화면/광고 재생
	@OneToMany(mappedBy = "adCreative", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevScrHourlyPlay> scrHourlyPlays = new HashSet<RevScrHourlyPlay>(0);
	
	// 하위 개체: 광고 선택 캐쉬
	@OneToMany(mappedBy = "adCreative", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevAdSelCache> adSelCaches = new HashSet<RevAdSelCache>(0);
	
	// 다른 개체 연결(E)

	
	public AdcAdCreative() {}
	
	public AdcAdCreative(AdcAd ad, AdcCreative creative, int weight,
			Date startDate, Date endDate, HttpSession session) {
		
		this.medium = ad.getMedium();
		this.ad = ad;
		this.creative = creative;
		
		this.startDate = startDate;
		this.endDate = endDate;
		this.weight = weight;
		
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
	
	public String getSortID() {
		
		String code = "Z";
		if (ad.getPurchType().equals("G")) {
			
		} else if (ad.getPurchType().equals("N")) {
			code = "Y";
		} else {
			code = "A";
		}
		
		return code + String.format("%7d", 100000 - ad.getCampaign().getId());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
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

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
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

	public AdcAd getAd() {
		return ad;
	}

	public void setAd(AdcAd ad) {
		this.ad = ad;
	}

	public AdcCreative getCreative() {
		return creative;
	}

	public void setCreative(AdcCreative creative) {
		this.creative = creative;
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

}
