package net.doohad.viewmodels.rev;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Tuple;

public class RevScrHrlyPlyAdStatItem {

	private int succTot;
	private int adId;
	private int cnt;
	
	private Integer currHourGoal;
	
	
	public RevScrHrlyPlyAdStatItem() {}
	
	public RevScrHrlyPlyAdStatItem(Tuple tuple) {
		
		if (tuple != null) {

			//		SELECT CNT_00, CNT_01, CNT_02, CNT_03, CNT_04, CNT_05, CNT_06, CNT_07,
			//		       CNT_08, CNT_09, CNT_10, CNT_11, CNT_12, CNT_13, CNT_14, CNT_15,
			//		       CNT_16, CNT_17, CNT_18, CNT_19, CNT_20, CNT_21, CNT_22, CNT_23,
			//		       SUCC_TOT, AD_ID, CURR_HOUR_GOAL
			
			this.succTot = (int) tuple.get(24);
			this.adId = (int) tuple.get(25);
			this.currHourGoal = (Integer) tuple.get(26);
			
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(new Date());		
			
			this.cnt = (int) tuple.get(calendar.get(Calendar.HOUR_OF_DAY));
		}
	}

	public int getSuccTot() {
		return succTot;
	}

	public void setSuccTot(int succTot) {
		this.succTot = succTot;
	}

	public int getAdId() {
		return adId;
	}

	public void setAdId(int adId) {
		this.adId = adId;
	}

	public int getCnt() {
		return cnt;
	}

	public void setCnt(int cnt) {
		this.cnt = cnt;
	}

	public Integer getCurrHourGoal() {
		return currHourGoal;
	}

	public void setCurrHourGoal(Integer currHourGoal) {
		this.currHourGoal = currHourGoal;
	}
	
}
