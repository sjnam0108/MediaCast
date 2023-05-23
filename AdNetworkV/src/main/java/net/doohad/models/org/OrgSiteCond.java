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

import net.doohad.models.inv.InvSite;
import net.doohad.models.knl.KnlMedium;
import net.doohad.utils.Util;

@Entity
@Table(name="ORG_SITE_CONDS", uniqueConstraints = {
	@javax.persistence.UniqueConstraint(columnNames = {"MEDIUM_ID", "CODE"}),
})
public class OrgSiteCond {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "org_site_condition_seq_gen")
	@Column(name = "SITE_COND_ID")
	private int id;
	
	// 입지 이름
	@Column(name = "NAME", nullable = false, length = 200)
	private String name;
	
	// 코드
	//
	//   코드는 매체 관리자가 정하기 나름이며, 굳이 alpha numeric일 이유 없음
	//
	//     name은 목록 선택에서 보이는 것
	//     code는 엑셀 항목으로 관리되는 것
	//
	@Column(name = "CODE", nullable = false, length = 200)
	private String code;

	// 서비스중 여부
	//
	//   더 이상 목록에서 보이고 싶지 않을 경우, 삭제 전 단계
	//
	@Column(name = "ACTIVE_STATUS", nullable = false)
	private boolean activeStatus = true; 
	
	
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
	
	
	// 사이트의 갯수
	@Transient
	private int siteCount = 0;

	// 화면의 갯수
	@Transient
	private int screenCount = 0;

	
	// 다른 개체 연결(S)
	
	// 상위 개체: 매체
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MEDIUM_ID", nullable = false)
	private KnlMedium medium;
	
	// 하위 개체: 사이트
	//
	//   입지 유형이 삭제될 때 사이트가 삭제되면 안됨!!
	//   cascade = CascadeType.REMOVE 제외
	//
	@OneToMany(mappedBy = "siteCond", fetch = FetchType.LAZY)
	private Set<InvSite> sites = new HashSet<InvSite>(0);
	
	// 다른 개체 연결(E)

	
	public OrgSiteCond() {}
	
	public OrgSiteCond(KnlMedium medium, String name, String code, boolean activeStatus, HttpSession session) {
		
		this.medium = medium;
		
		this.name = name;
		this.code = code;
		this.activeStatus = activeStatus;
		
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean isActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(boolean activeStatus) {
		this.activeStatus = activeStatus;
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

	@JsonIgnore
	public Set<InvSite> getSites() {
		return sites;
	}

	public void setSites(Set<InvSite> sites) {
		this.sites = sites;
	}

	public int getSiteCount() {
		return siteCount;
	}

	public void setSiteCount(int siteCount) {
		this.siteCount = siteCount;
	}

	public int getScreenCount() {
		return screenCount;
	}

	public void setScreenCount(int screenCount) {
		this.screenCount = screenCount;
	}

}
