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
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.doohad.utils.Util;

@Entity
@Table(name="KNL_ACCOUNTS")
public class KnlAccount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "knl_account_seq_gen")
	@Column(name = "ACCOUNT_ID")
	private int id;
	
	// 계정명
	@Column(name = "NAME", nullable = false, length = 200, unique = true)
	private String name;
	
	// 관리 대상 매체
	@Column(name = "DEST_MEDIA", nullable = false)
	private String destMedia = "";

	
	// 커널 관리
	@Column(name = "SCOPE_KERNEL", nullable = false)
	private boolean scopeKernel; 

	// 매체 관리
	@Column(name = "SCOPE_MEDIUM", nullable = false)
	private boolean scopeMedium; 

	// 광고 제공
	@Column(name = "SCOPE_AD", nullable = false)
	private boolean scopeAd; 

	
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
	
	// 하위 개체: 사용자
	@OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<KnlUser> users = new HashSet<KnlUser>(0);
	
	// 다른 개체 연결(E)
	

	public KnlAccount() {}
	
	public KnlAccount(String name,
			Date effectiveStartDate, Date effectiveEndDate, String memo, HttpSession session) {
		
		this.name = name;
		
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

	public boolean isScopeKernel() {
		return scopeKernel;
	}

	public void setScopeKernel(boolean scopeKernel) {
		this.scopeKernel = scopeKernel;
	}

	public boolean isScopeMedium() {
		return scopeMedium;
	}

	public void setScopeMedium(boolean scopeMedium) {
		this.scopeMedium = scopeMedium;
	}

	public boolean isScopeAd() {
		return scopeAd;
	}

	public void setScopeAd(boolean scopeAd) {
		this.scopeAd = scopeAd;
	}

	public String getDestMedia() {
		return destMedia;
	}

	public void setDestMedia(String destMedia) {
		this.destMedia = destMedia;
	}

	@JsonIgnore
	public Set<KnlUser> getUsers() {
		return users;
	}

	public void setUsers(Set<KnlUser> users) {
		this.users = users;
	}
	
}
