package net.doohad.models.adc;

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
@Table(name="ADC_CREAT_TARGETS")
public class AdcCreatTarget {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CREAT_TARGET_ID")
	private int id;
	
	// 삭제 여부
	//
	//   별도의 플래그 컬럼은 존재하지 않음. 상위인 Creative의 상태에 따라 살아있거나, 죽어있거나.
	//   Creative의 표시없이는 이 값들은 표시되지 않기 때문
	//
	
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
	
	// 동일 광고 소재에서 순서
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
	
	// 상위 개체: 광고 소재
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CREATIVE_ID", nullable = false)
	private AdcCreative creative;
	
	// 다른 개체 연결(E)
	
	
	public AdcCreatTarget() {}
	
	public AdcCreatTarget(AdcCreative creative, String invenType, int tgtCount, String tgtValue,
			String tgtDisplay, int tgtScrCount, int siblingSeq, HttpSession session) {
		this.medium = creative.getMedium();
		this.creative = creative;
		
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

	public AdcCreative getCreative() {
		return creative;
	}

	public void setCreative(AdcCreative creative) {
		this.creative = creative;
	}

}
