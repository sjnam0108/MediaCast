package net.doohad.models.org;

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
import net.doohad.utils.Util;

@Entity
@Table(name="ORG_PLAYLISTS", uniqueConstraints = {
	@javax.persistence.UniqueConstraint(columnNames = {"MEDIUM_ID", "NAME"}),
})
public class OrgPlaylist {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "org_playlist_seq_gen")
	@Column(name = "PLAYLIST_ID")
	private int id;
	
	// 재생 목록 이름
	@Column(name = "NAME", nullable = false, length = 200)
	private String name;
	
	// 광고 갯수
	@Column(name = "AD_COUNT", nullable = false)
	private int adCount = 0;
	
	// 광고 id 값('|' 구분자)
	@Column(name = "AD_VALUE", nullable = false, length = 2000)
	private String adValue = "";
	
	// 동일 매체에서 순서
	@Column(name = "SIBLING_SEQ", nullable = false)
	private int siblingSeq = 0;
	
	
	// 시작일과 종료일
	//
	//   시간 값은 없음
	//
	// 시작일
	@Column(name = "START_DATE")
	private Date startDate;
	
	// 종료일
	@Column(name = "END_DATE")
	private Date endDate;
	
	
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
	
	
	// 유효 광고 수(광고의 시작/종료일 및 상태에 따라 유효/무효 판단)
	@Transient
	private int validAdCount = 0;

	// 대상 화면의 수
	@Transient
	private int screenCount = 0;

	
	// 다른 개체 연결(S)
	
	// 상위 개체: 매체
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MEDIUM_ID", nullable = false)
	private KnlMedium medium;
	
	// 하위 개체: 재생 목록 타겟팅
	@OneToMany(mappedBy = "playlist", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<OrgPlTarget> playlists = new HashSet<OrgPlTarget>(0);
	
	// 다른 개체 연결(E)

	
	public OrgPlaylist() {}
	
	public OrgPlaylist(KnlMedium medium, String name, String adValue, int adCount,
			Date startDate, Date endDate, int siblingSeq, HttpSession session) {
		
		this.medium = medium;
		
		this.name = name;
		this.adValue = adValue;
		this.adCount = adCount;
		this.startDate = startDate;
		this.endDate = endDate;
		this.siblingSeq = siblingSeq;
		
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

	public int getScreenCount() {
		return screenCount;
	}

	public void setScreenCount(int screenCount) {
		this.screenCount = screenCount;
	}

	public KnlMedium getMedium() {
		return medium;
	}

	public void setMedium(KnlMedium medium) {
		this.medium = medium;
	}

	@JsonIgnore
	public Set<OrgPlTarget> getPlaylists() {
		return playlists;
	}

	public void setPlaylists(Set<OrgPlTarget> playlists) {
		this.playlists = playlists;
	}

	public int getAdCount() {
		return adCount;
	}

	public void setAdCount(int adCount) {
		this.adCount = adCount;
	}

	public String getAdValue() {
		return adValue;
	}

	public void setAdValue(String adValue) {
		this.adValue = adValue;
	}

	public int getSiblingSeq() {
		return siblingSeq;
	}

	public void setSiblingSeq(int siblingSeq) {
		this.siblingSeq = siblingSeq;
	}

	public int getValidAdCount() {
		return validAdCount;
	}

	public void setValidAdCount(int validAdCount) {
		this.validAdCount = validAdCount;
	}
	
}
