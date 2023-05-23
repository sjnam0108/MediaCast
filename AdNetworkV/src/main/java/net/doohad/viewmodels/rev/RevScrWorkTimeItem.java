package net.doohad.viewmodels.rev;

import java.util.Date;

public class RevScrWorkTimeItem {

	private int screenId;
	private Date date;
	
	
	public RevScrWorkTimeItem() {}
	
	public RevScrWorkTimeItem(int screenId, Date date) {
		this.screenId = screenId;
		this.date = date;
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
	
}
