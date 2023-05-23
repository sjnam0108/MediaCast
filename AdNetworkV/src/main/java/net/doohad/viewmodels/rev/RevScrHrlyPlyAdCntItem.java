package net.doohad.viewmodels.rev;

import java.util.Date;

public class RevScrHrlyPlyAdCntItem {

	private int cnt = 0;
	private int screenId;
	private int adId;
	private int adCreativeId;
	
	private Date selectDate;
	
	
	public RevScrHrlyPlyAdCntItem() {}
	
	public RevScrHrlyPlyAdCntItem(int screenId, int adId, int adCreativeId, Date selectDate) {
		this.screenId = screenId;
		this.adId = adId;
		this.adCreativeId = adCreativeId;
		this.selectDate = selectDate;
		this.cnt = 1;
	}

	
	public void addOneCount() {
		this.cnt ++;
	}
	
	
	public int getCnt() {
		return cnt;
	}

	public void setCnt(int cnt) {
		this.cnt = cnt;
	}

	public int getScreenId() {
		return screenId;
	}

	public void setScreenId(int screenId) {
		this.screenId = screenId;
	}

	public int getAdId() {
		return adId;
	}

	public void setAdId(int adId) {
		this.adId = adId;
	}

	public Date getSelectDate() {
		return selectDate;
	}

	public void setSelectDate(Date selectDate) {
		this.selectDate = selectDate;
	}

	public int getAdCreativeId() {
		return adCreativeId;
	}

	public void setAdCreativeId(int adCreativeId) {
		this.adCreativeId = adCreativeId;
	}
	
	
}
