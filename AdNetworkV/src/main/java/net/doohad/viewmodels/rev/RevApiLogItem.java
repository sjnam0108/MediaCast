package net.doohad.viewmodels.rev;

import java.util.Date;

import net.doohad.models.rev.RevAdSelect;
import net.doohad.models.rev.RevPlayHist;
import net.doohad.utils.Util;

public class RevApiLogItem {

	private int id;
	private int mediumId;
	private Integer adId;
	private Integer creativeId;
	private Integer screenId;
	private Integer duration;
	
	private boolean result;
	
	private String adName;
	private String creativeName;
	private String screenName;
	private String uuid;
	private String purchType;
	
	private Date selectDate;
	private Date beginDate;
	private Date endDate;
	private Date reportDate;
	
	
	public RevApiLogItem() {}
	
	public RevApiLogItem(RevPlayHist playHist, String purchType) {
		this.id = playHist.getId();
		this.selectDate = playHist.getSelectDate();
		this.mediumId = playHist.getMediumId();
		this.adId = playHist.getAdId();
		this.adName = playHist.getAdName();
		this.creativeId = playHist.getCreativeId();
		this.creativeName = playHist.getCreativeName();
		this.screenId = playHist.getScreenId();
		this.screenName = playHist.getScreenName();
		this.uuid = playHist.getUuid();
		this.result = playHist.getResult() != null && playHist.getResult().booleanValue();
		this.beginDate = playHist.getPlayBeginDate();
		this.endDate = playHist.getPlayEndDate();
		this.duration = playHist.getDuration();
		this.reportDate = playHist.getReportDate();
		this.purchType = Util.isValid(purchType) ? purchType : "?";
	}
	
	public RevApiLogItem(RevAdSelect adSelect) {
		this.id = adSelect.getId() * -1;
		this.selectDate = adSelect.getSelectDate();
		this.mediumId = adSelect.getMedium().getId();
		this.adId = adSelect.getAdCreative().getAd().getId();
		this.adName = adSelect.getAdCreative().getAd().getName();
		this.creativeId = adSelect.getAdCreative().getCreative().getId();
		this.creativeName = adSelect.getAdCreative().getCreative().getName();
		this.screenId = adSelect.getScreen().getId();
		this.screenName = adSelect.getScreen().getName();
		this.uuid = adSelect.getUuid().toString();
		this.result = adSelect.getResult() != null && adSelect.getResult().booleanValue();
		this.beginDate = adSelect.getPlayBeginDate();
		this.endDate = adSelect.getPlayEndDate();
		this.duration = adSelect.getDuration();
		this.reportDate = adSelect.getReportDate();
		this.purchType = adSelect.getAdCreative().getAd().getPurchType();
	}

	
	public String getDurDisp() {
		if (duration == null) {
			return "";
		}
		
		return String.valueOf((int)Math.floor((double)duration / 1000d)) + 
				" <small>" + String.format("%03d", duration % 1000) + "</small>";
	}
	
	public boolean isRetryReported() {
		return selectDate != null && beginDate != null && selectDate.compareTo(beginDate) == 0;
	}
	
	public int getMediumId() {
		return mediumId;
	}

	public void setMediumId(int mediumId) {
		this.mediumId = mediumId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getAdId() {
		return adId;
	}

	public void setAdId(Integer adId) {
		this.adId = adId;
	}

	public Integer getCreativeId() {
		return creativeId;
	}

	public void setCreativeId(Integer creativeId) {
		this.creativeId = creativeId;
	}

	public String getAdName() {
		return adName;
	}

	public void setAdName(String adName) {
		this.adName = adName;
	}

	public String getCreativeName() {
		return creativeName;
	}

	public void setCreativeName(String creativeName) {
		this.creativeName = creativeName;
	}

	public Date getSelectDate() {
		return selectDate;
	}

	public void setSelectDate(Date selectDate) {
		this.selectDate = selectDate;
	}

	public Integer getScreenId() {
		return screenId;
	}

	public void setScreenId(Integer screenId) {
		this.screenId = screenId;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public String getPurchType() {
		return purchType;
	}

	public void setPurchType(String purchType) {
		this.purchType = purchType;
	}
	
}
