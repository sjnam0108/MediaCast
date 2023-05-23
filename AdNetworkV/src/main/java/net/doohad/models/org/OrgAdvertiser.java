package net.doohad.models.org;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

import net.doohad.models.adc.AdcCampaign;
import net.doohad.models.adc.AdcCreative;
import net.doohad.models.knl.KnlMedium;
import net.doohad.utils.Util;

@Entity
@Table(name="ORG_ADVERTISERS", uniqueConstraints = {
	@javax.persistence.UniqueConstraint(columnNames = {"MEDIUM_ID", "NAME"}),
	@javax.persistence.UniqueConstraint(columnNames = {"MEDIUM_ID", "DOMAIN_NAME"}),
})
public class OrgAdvertiser {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "org_advertiser_seq_gen")
	@Column(name = "ADVERTISER_ID")
	private int id;
	
	//
	// 광고주 자료는 생성 및 삭제만 가능. 변경은 불가능
	//
	
	// 광고주명
	@Column(name = "NAME", nullable = false, length = 200)
	private String name;
	
	// 광고주 도메인/식별자
	//
	//   식별자로 하기엔 이용 개인에 따른 인식의 차가 크기 때문에, 도메인과 같은 공통된
	//   의미의 대상이 더 적절해 보임
	//
	@Column(name = "DOMAIN_NAME", nullable = false, length = 200)
	private String domainName;

	// 썸네일 파일명
	//
	//   썸네일 경로 고정: /adv
	//   썸네일 파일명: be8072eb-f735-4133-9ce6-557a96f37f39.png 형식
	//
	//   썸네일에 대한 등록은 커널에서 진행. 
	//   식별자에 따라 분류하고 썸네일을 추가. 느슨한 관계로 연결
	//
	/*
	@Column(name = "THUMB_FILENAME", length = 100)
	private String thumbFilename = "";
	*/


	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "CREATED_BY", nullable = false)
	private int whoCreatedBy;
	
	@Column(name = "LAST_UPDATE_LOGIN", nullable = false)
	private int whoLastUpdateLogin;
	// WHO 컬럼들(E)
	
	
	// 광고 소재의 갯수
	@Transient
	private int creativeCount = 0;
	
	
	// 다른 개체 연결(S)
	
	// 상위 개체: 매체
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MEDIUM_ID", nullable = false)
	private KnlMedium medium;
	
	// 하위 개체: 광고 소재
	//
	//   광고주가 삭제될 때 광고 소재가 삭제되면 안됨!!
	//   cascade = CascadeType.REMOVE 제외
	//
	@OneToMany(mappedBy = "advertiser", fetch = FetchType.LAZY)
	private Set<AdcCreative> creatives = new HashSet<AdcCreative>(0);
	
	// 하위 개체: 캠페인
	//
	//   광고주가 삭제될 때 광고 소재가 삭제되면 안됨!!
	//   cascade = CascadeType.REMOVE 제외
	//
	@OneToMany(mappedBy = "advertiser", fetch = FetchType.LAZY)
	private Set<AdcCampaign> campaigns = new HashSet<AdcCampaign>(0);
	
	// 다른 개체 연결(E)

	
	public OrgAdvertiser() {}
	
	public OrgAdvertiser(KnlMedium medium, String name, String domainName, HttpSession session) {
		
		this.medium = medium;
		
		this.name = name;
		this.domainName = domainName;
		
		this.whoCreatedBy = Util.loginUserId(session);
		this.whoCreationDate = new Date();
		this.whoLastUpdateLogin = Util.loginId(session);
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

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public Date getWhoCreationDate() {
		return whoCreationDate;
	}

	public void setWhoCreationDate(Date whoCreationDate) {
		this.whoCreationDate = whoCreationDate;
	}

	public int getWhoCreatedBy() {
		return whoCreatedBy;
	}

	public void setWhoCreatedBy(int whoCreatedBy) {
		this.whoCreatedBy = whoCreatedBy;
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

	@JsonIgnore
	public Set<AdcCreative> getCreatives() {
		return creatives;
	}

	public void setCreatives(Set<AdcCreative> creatives) {
		this.creatives = creatives;
	}

	public int getCreativeCount() {
		return creativeCount;
	}

	public void setCreativeCount(int creativeCount) {
		this.creativeCount = creativeCount;
	}

	@JsonIgnore
	public Set<AdcCampaign> getCampaigns() {
		return campaigns;
	}

	public void setCampaigns(Set<AdcCampaign> campaigns) {
		this.campaigns = campaigns;
	}
	
}
