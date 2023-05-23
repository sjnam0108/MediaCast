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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.servlet.http.HttpSession;

import net.doohad.utils.Util;

@Entity
@Table(name="KNL_MENUS")
public class KnlMenu {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "knl_menu_seq_gen")
	@Column(name = "USER_ID")
	private int id;
	
	// 식별자
	@Column(name = "UKID", nullable = false, length = 20, unique = true)
	private String ukid;

	// URL
	@Column(name = "URL", length = 50)
	private String url = "";
	
	// 아이콘
	@Column(name = "ICON_TYPE", length = 50)
	private String iconType = "";
	
	// 동일 레벨에서 순서
	@Column(name = "SIBLING_SEQ", nullable = false)
	private int siblingSeq;
	
	// 개별 롤에 대한 이용 여부 지정(일단 내부용)
	@Column(name = "USAGE_LEVEL", nullable = false)
	private int usageLevel = 128;
	
	
	// 커널 관리 범위 이용가능
	@Column(name = "SCOPE_KERNEL_AVAIL", nullable = false)
	private boolean scopeKernelAvailable = true;
	
	// 매체 관리 범위 이용가능
	@Column(name = "SCOPE_MEDIUM_AVAIL", nullable = false)
	private boolean scopeMediumAvailable = false;
	
	// 광고 제공 범위 이용가능
	@Column(name = "SCOPE_AD_AVAIL", nullable = false)
	private boolean scopeAdAvailable = false;


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
	
	// 상위 개체: 메뉴(자신)
	@ManyToOne
	@JoinColumn(name = "PARENT_ID")
	private KnlMenu parent;
	
	// 하위 개체: 메뉴(자신)
	@OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private Set<KnlMenu> subMenus = new HashSet<KnlMenu>(0);
	
	// 다른 개체 연결(E)
	

	public KnlMenu() {}
	
	public KnlMenu(String ukid, String url, String iconType, int siblingSeq, 
			boolean scopeKernelAvailable, boolean scopeMediumAvailable, boolean scopeAdAvailable,
			HttpSession session) {
		
		this.ukid = ukid;
		this.url = url;
		this.iconType = iconType;
		this.siblingSeq = siblingSeq;
		this.scopeKernelAvailable = scopeKernelAvailable;
		this.scopeMediumAvailable = scopeMediumAvailable;
		this.scopeAdAvailable = scopeAdAvailable;
		
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

	public String getUkid() {
		return ukid;
	}

	public void setUkid(String ukid) {
		this.ukid = ukid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIconType() {
		return iconType;
	}

	public void setIconType(String iconType) {
		this.iconType = iconType;
	}

	public int getSiblingSeq() {
		return siblingSeq;
	}

	public void setSiblingSeq(int siblingSeq) {
		this.siblingSeq = siblingSeq;
	}

	public boolean isScopeKernelAvailable() {
		return scopeKernelAvailable;
	}

	public void setScopeKernelAvailable(boolean scopeKernelAvailable) {
		this.scopeKernelAvailable = scopeKernelAvailable;
	}

	public boolean isScopeMediumAvailable() {
		return scopeMediumAvailable;
	}

	public void setScopeMediumAvailable(boolean scopeMediumAvailable) {
		this.scopeMediumAvailable = scopeMediumAvailable;
	}

	public boolean isScopeAdAvailable() {
		return scopeAdAvailable;
	}

	public void setScopeAdAvailable(boolean scopeAdAvailable) {
		this.scopeAdAvailable = scopeAdAvailable;
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

	public KnlMenu getParent() {
		return parent;
	}

	public void setParent(KnlMenu parent) {
		this.parent = parent;
	}

	public Set<KnlMenu> getSubMenus() {
		return subMenus;
	}

	public void setSubMenus(Set<KnlMenu> subMenus) {
		this.subMenus = subMenus;
	}

}
