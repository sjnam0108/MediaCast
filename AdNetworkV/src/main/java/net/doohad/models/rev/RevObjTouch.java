package net.doohad.models.rev;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import net.doohad.utils.Util;

@Entity
@Table(name="REV_OBJ_TOUCHES")
public class RevObjTouch {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "OBJ_TOUCH_ID")
	private int id;
	

	// 유형
	//
	//    S - 화면
	//    C - 광고 소재
	//
	@Column(name = "TYPE", length = 1, nullable = false)
	private String type;

	// 개체(화면 또는 광고) id
	@Column(name = "OBJ_ID", nullable = false)
	private int objId;

	
	// 날짜1. 주요 항목. 
	// 화면일 경우 최근 File API 요청일시
	@Column(name = "DATE1")
	private Date date1;
	
	// 날짜2.
	// 화면일 경우 최근 Ad API 요청일시
	@Column(name = "DATE2")
	private Date date2;
	
	// 날짜3.
	// 화면일 경우 최근 Report API 요청일시
	@Column(name = "DATE3")
	private Date date3;
	
	// 날짜4.
	// 화면일 경우 최근 RetryReport API 요청일시
	@Column(name = "DATE4")
	private Date date4;
	
	
	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "LAST_UPDATE_DATE", nullable = false)
	private Date whoLastUpdateDate;
	// WHO 컬럼들(E)
	
	
	// 다른 개체 연결(S)
	
	// 다른 개체 연결(E)
	
	
	public RevObjTouch() {}
	
	public RevObjTouch(String type, int objId) {
		
		this.type = type;
		this.objId = objId;

		this.whoCreationDate = new Date();
		this.whoLastUpdateDate = new Date();
	}
	
	public RevObjTouch(String type, int objId, Date date) {
		
		this.type = type;
		this.objId = objId;
		this.date1 = date;

		this.whoCreationDate = new Date();
		this.whoLastUpdateDate = new Date();
	}
	
	
	public String getScreenStatus() {
		Date now = new Date();
		Date max = Util.getMaxDate(date1, date2, date3, date4);
		
		if (max == null) {
			return "0";
		} else if (Util.addMinutes(now, -10).before(max)) {
			return "6";
		} else if (Util.addHours(now, -1).before(max)) {
			return "5";
		} else if (Util.addHours(now, -6).before(max)) {
			return "4";
		} else if (Util.addHours(now, -24).before(max)) {
			return "3";
		} else {
			return "1";
		}
	}

	public void touchWho() {
		this.whoLastUpdateDate = new Date();
	}
	
	public Date getLastFileApiDate() {
		return date1;
	}
	
	public void setLastFileApiDate(Date lastFileApiDate) {
		date1 = lastFileApiDate;
	}

	public Date getLastAdRequestDate() {
		return date2;
	}

	public void setLastAdRequestDate(Date lastAdRequestDate) {
		date2 = lastAdRequestDate;
	}

	public Date getLastAdReportDate() {
		return date3;
	}

	public void setLastAdReportDate(Date lastAdReportDate) {
		date3 = lastAdReportDate;
	}
	
	public Date getLastAdRetryReportDate() {
		return date4;
	}
	
	public void setLastAdRetryReportDate(Date lastAdRetryReportDate) {
		date4 = lastAdRetryReportDate;
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getObjId() {
		return objId;
	}

	public void setObjId(int objId) {
		this.objId = objId;
	}

	public Date getDate1() {
		return date1;
	}

	public void setDate1(Date date1) {
		this.date1 = date1;
	}

	public Date getDate2() {
		return date2;
	}

	public void setDate2(Date date2) {
		this.date2 = date2;
	}

	public Date getDate3() {
		return date3;
	}

	public void setDate3(Date date3) {
		this.date3 = date3;
	}

	public Date getDate4() {
		return date4;
	}

	public void setDate4(Date date4) {
		this.date4 = date4;
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

	
}
