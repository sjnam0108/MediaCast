package net.doohad.viewmodels.sys;

import java.util.Date;

public class SysObjEventTimeCalcItem {

	private int objId;
	private String type;
	
	private Date date1;
	private Date date2;
	private Date date3;
	private Date date4;
	
	private Date lastUpdateDate;
	
	
	public SysObjEventTimeCalcItem() {}
	
	public SysObjEventTimeCalcItem(String typeS, int objId, Date date, int type) {
		this.type = typeS;
		this.objId = objId;
		
		if (type == 11 || type == 21) {
			this.date1 = date;
		} else if (type == 12) {
			this.date2 = date;
		} else if (type == 13) {
			this.date3 = date;
		} else if (type == 14) {
			this.date4 = date;
		}
	}

	
	public int getObjId() {
		return objId;
	}

	public void setObjId(int objId) {
		this.objId = objId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	
}
