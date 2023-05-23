package net.doohad.models.org;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.servlet.http.HttpSession;

import net.doohad.models.knl.KnlMedium;
import net.doohad.utils.Util;

@Entity
@Table(name="ORG_PL_TARGETS")
public class OrgPlTarget {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "org_pl_target_seq_gen")
	@Column(name = "PL_TARGET_ID")
	private int id;

	
	// 필터 유형
	//
	//   A		And
	//   O		Or
	//
	@Column(name = "FILTER_TYPE", nullable = false, length = 1)
	private String filterType = "O";
	
	// 인벤토리 유형
	//
	//   SC      화면
	//   ST      사이트
	//   CT      시/도
	//   RG	     시/군/구
	//   CD	     입지 유형
	//
	@Column(name = "INVEN_TYPE", nullable = false, length = 2)
	private String invenType = "";
	
	// 대상 갯수
	@Column(name = "TGT_COUNT", nullable = false)
	private int tgtCount = 0;
	
	// 대상(표시용)
	@Column(name = "TGT_DISPLAY", nullable = false, length = 2000)
	private String tgtDisplay = "";
	
	// 대상 값
	@Column(name = "TGT_VALUE", nullable = false, length = 2000)
	private String tgtValue = "";
	
	// 동일 재생 목록에서 순서
	@Column(name = "SIBLING_SEQ", nullable = false)
	private int siblingSeq = 0;
	
	// 대상 화면 갯수
	@Column(name = "TGT_SCR_COUNT", nullable = false)
	private int tgtScrCount = 0;
	
	
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
	
	// 상위 개체: 재생 목록
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PLAYLIST_ID", nullable = false)
	private OrgPlaylist playlist;
	
	// 다른 개체 연결(E)
	
	
	public OrgPlTarget() {}
	
	public OrgPlTarget(OrgPlaylist playlist, String invenType, int tgtCount, String tgtValue,
			String tgtDisplay, int tgtScrCount, int siblingSeq, HttpSession session) {
		this.medium = playlist.getMedium();
		this.playlist = playlist;
		
		//this.filterType = "O";	// 기본값 그대로
		this.invenType = invenType;
		this.tgtCount = tgtCount;
		this.tgtValue = tgtValue;
		this.tgtDisplay = tgtDisplay;
		this.tgtScrCount = tgtScrCount;
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

	public String getFilterType() {
		return filterType;
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

	public String getInvenType() {
		return invenType;
	}

	public void setInvenType(String invenType) {
		this.invenType = invenType;
	}

	public int getTgtCount() {
		return tgtCount;
	}

	public void setTgtCount(int tgtCount) {
		this.tgtCount = tgtCount;
	}

	public String getTgtDisplay() {
		return tgtDisplay;
	}

	public void setTgtDisplay(String tgtDisplay) {
		this.tgtDisplay = tgtDisplay;
	}

	public String getTgtValue() {
		return tgtValue;
	}

	public void setTgtValue(String tgtValue) {
		this.tgtValue = tgtValue;
	}

	public int getSiblingSeq() {
		return siblingSeq;
	}

	public void setSiblingSeq(int siblingSeq) {
		this.siblingSeq = siblingSeq;
	}

	public int getTgtScrCount() {
		return tgtScrCount;
	}

	public void setTgtScrCount(int tgtScrCount) {
		this.tgtScrCount = tgtScrCount;
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

	public OrgPlaylist getPlaylist() {
		return playlist;
	}

	public void setPlaylist(OrgPlaylist playlist) {
		this.playlist = playlist;
	}

}
