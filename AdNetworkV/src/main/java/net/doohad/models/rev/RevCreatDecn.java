package net.doohad.models.rev;

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
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

import net.doohad.models.adc.AdcCreative;
import net.doohad.models.knl.KnlMedium;
import net.doohad.utils.Util;

@Entity
@Table(name="REV_CREAT_DECNS")
public class RevCreatDecn {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CREAT_DECN_ID")
	private int id;


	// 의사 결정 상태
	//  
	//     - A		승인		Approved
	//     - J		거절		Rejected
	//
	@Column(name = "STATUS", nullable = false, length = 1)
	private String status = "A";
	
	// 거절 이유
	@Column(name = "REASON", nullable = false, length = 200)
	private String reason = "";
	
	// 승인 요청 일시
	@Column(name = "SUBMIT_DATE", nullable = false)
	private Date submitDate;

	// 승인 또는 거절 일시
	@Column(name = "ACT_DATE", nullable = false)
	private Date actDate;

	// 승인 또는 거절은 누가?
	@Column(name = "ACTED_BY", nullable = false)
	private Integer actedBy;
	
	
	// 승인 또는 거절한 이의 사용자 ID
	@Transient
	private String actedByShortName = "";
	
	
	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "CREATED_BY", nullable = false)
	private int whoCreatedBy;
	
	@Column(name = "LAST_UPDATE_LOGIN", nullable = false)
	private int whoLastUpdateLogin;
	// WHO 컬럼들(E)
	
	
	// 다른 개체 연결(S)
	
	// 상위 개체: 매체
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MEDIUM_ID", nullable = false)
	private KnlMedium medium;
	
	// 상위 개체: 방송 소재
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CREATIVE_ID", nullable = false)
	private AdcCreative creative;
	
	// 다른 개체 연결(E)
	
	
	public RevCreatDecn() {}
	
	public RevCreatDecn(AdcCreative creative, HttpSession session) {
		
		this.medium = creative.getMedium();
		this.creative = creative;
		
		this.submitDate = creative.getSubmitDate();
		if (this.submitDate == null) {
			this.submitDate = creative.getWhoLastUpdateDate();
		}
		
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Date getSubmitDate() {
		return submitDate;
	}

	public void setSubmitDate(Date submitDate) {
		this.submitDate = submitDate;
	}

	public Date getActDate() {
		return actDate;
	}

	public void setActDate(Date actDate) {
		this.actDate = actDate;
	}

	public Integer getActedBy() {
		return actedBy;
	}

	public void setActedBy(Integer actedBy) {
		this.actedBy = actedBy;
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

	public AdcCreative getCreative() {
		return creative;
	}

	public void setCreative(AdcCreative creative) {
		this.creative = creative;
	}

	public String getActedByShortName() {
		return actedByShortName;
	}

	public void setActedByShortName(String actedByShortName) {
		this.actedByShortName = actedByShortName;
	}

}
