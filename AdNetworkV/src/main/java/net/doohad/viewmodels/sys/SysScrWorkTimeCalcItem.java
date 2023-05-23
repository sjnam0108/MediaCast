package net.doohad.viewmodels.sys;

import java.util.Date;

public class SysScrWorkTimeCalcItem {

	private int screenId;
	private Date date;
	private String statusLine;
	
	
	public SysScrWorkTimeCalcItem() {}
	
	public SysScrWorkTimeCalcItem(int screenId, Date date, String statusLine) {
		this.screenId = screenId;
		this.date = date;
		this.statusLine = statusLine;
	}

	
	public int getScreenId() {
		return screenId;
	}

	public void setScreenId(int screenId) {
		this.screenId = screenId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getStatusLine() {
		return statusLine;
	}

	public void setStatusLine(String statusLine) {
		this.statusLine = statusLine;
	}

}
