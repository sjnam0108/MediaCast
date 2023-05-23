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
import javax.servlet.http.HttpSession;

import net.doohad.models.knl.KnlMedium;
import net.doohad.utils.Util;

@Entity
@Table(name="REV_INVEN_REQUESTS")
public class RevInvenRequest {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "INVEN_REQUEST_ID")
	private int id;
	
	
	// 사이트ID
	@Column(name = "SIT_SHORT_NAME", length = 70)
	private String siteShortName;
	
	// 사이트명
	@Column(name = "SIT_NAME", length = 220)
	private String siteName;

	// 화면ID
	@Column(name = "SCR_SHORT_NAME", length = 70)
	private String screenShortName;
	
	// 화면명
	@Column(name = "SCR_NAME", length = 220)
	private String screenName;
	
	
	// 요청 내용
	@Column(name = "REQUEST", length = 500)
	private String request = "";
	
	
	// DB upsert 결과 코드
	//
	//    초기		I
	//    성공		S
	//    실패		F
	//    패스      P
	//
	@Column(name = "RESULT", nullable = false, length = 1)
	private String result = "I";
	
	// 결과 코드(실패/패스 시)
	@Column(name = "RESULT_CODE", length = 30)
	private String resultCode = "";
	

	// 요청 유형
	//
	//    - 업데이트/추가		U
	//    - 삭제				D
	//
	@Column(name = "TYPE", length = 1)
	private String type = "";

	
	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "LAST_UPDATE_DATE", nullable = false)
	private Date whoLastUpdateDate;
	
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
	
	// 다른 개체 연결(E)
	
	
	public RevInvenRequest() {}

	public RevInvenRequest(KnlMedium medium,
			String siteShortName, String siteName, String screenShortName, String screenName, String type) {
		
		this.medium = medium;
		
		this.siteShortName = siteShortName;
		this.siteName = siteName;
		this.screenShortName = screenShortName;
		this.screenName = screenName;
		this.type = type;
		
		touchWhoC(null);
	}

	private void touchWhoC(HttpSession session) {
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

	public String getSiteShortName() {
		return siteShortName;
	}

	public void setSiteShortName(String siteShortName) {
		this.siteShortName = siteShortName;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getScreenShortName() {
		return screenShortName;
	}

	public void setScreenShortName(String screenShortName) {
		this.screenShortName = screenShortName;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
	
}
