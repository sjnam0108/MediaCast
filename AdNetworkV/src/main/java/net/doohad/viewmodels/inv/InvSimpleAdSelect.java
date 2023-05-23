package net.doohad.viewmodels.inv;

import java.util.Date;

import net.doohad.models.rev.RevAdSelect;
import net.doohad.models.rev.RevPlayHist;
import net.doohad.utils.Util;

public class InvSimpleAdSelect {

	private Date date;
	
	private String adName;
	
	private boolean completed;
	private boolean retryReported;
	private boolean fallbackAd;
	
	
	public InvSimpleAdSelect() {}
	
	public InvSimpleAdSelect(RevAdSelect adSelect) {

		this.date = adSelect.getSelectDate();
		this.adName = adSelect.getAdCreative().getAd().getName();
		this.completed = adSelect.getResult() != null && adSelect.getResult().booleanValue();
		this.retryReported = adSelect.getReportDate() != null && adSelect.getPlayBeginDate() != null &&
				adSelect.getReportDate().compareTo(adSelect.getPlayBeginDate()) == 0;
	}
	
	public InvSimpleAdSelect(RevPlayHist playHist) {
		this.date = playHist.getSelectDate();
		
		if (Util.isValid(playHist.getAdName())) {
			this.adName = playHist.getAdName();
			this.completed = playHist.getResult() != null && playHist.getResult().booleanValue();
			this.retryReported = playHist.getReportDate() != null && playHist.getPlayBeginDate() != null &&
					playHist.getReportDate().compareTo(playHist.getPlayBeginDate()) == 0;
		} else {
			this.adName = "<span class='badge badge-outline-secondary' style='font-weight: 300;'>대체</span><span class='pl-1'></span>" + playHist.getCreativeName();
			this.completed = true;
			this.retryReported = false;
		}
	}

	
	public String getTime() {
		return Util.getSmartDate(date, true, true);
	}
	
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getAdName() {
		return adName;
	}

	public void setAdName(String adName) {
		this.adName = adName;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public boolean isRetryReported() {
		return retryReported;
	}

	public void setRetryReported(boolean retryReported) {
		this.retryReported = retryReported;
	}

	public boolean isFallbackAd() {
		return fallbackAd;
	}

	public void setFallbackAd(boolean fallbackAd) {
		this.fallbackAd = fallbackAd;
	}
	
}
