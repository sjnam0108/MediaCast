package net.doohad.viewmodels.org;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.doohad.models.adc.AdcAdCreative;
import net.doohad.utils.Util;

public class OrgPlaylistItem {

	// 여기서의 id는 adCreative의 id임
	private int id;
	
	private String adName;
	private String creatName;
	private String purchType;
	private String key;
	
	// 광고의 시작/종료일이 아니라, 광고/광고 소재의 시작/종료일임
	private Date startDate;
	private Date endDate;
	
	private boolean valid;
	
	public OrgPlaylistItem(AdcAdCreative adCreat) {
		this.adName = adCreat.getAd().getName();
		this.creatName = adCreat.getCreative().getName();
		this.purchType = adCreat.getAd().getPurchType();
		
		this.startDate = adCreat.getStartDate();
		this.endDate = adCreat.getEndDate();
		
		this.key = "A" + adCreat.getAd().getId() + "C" + adCreat.getCreative().getId() + "P" +
				Util.toSimpleString(startDate, "yyyyMMdd") + Util.toSimpleString(endDate, "yyyyMMdd");
		
		this.id = adCreat.getId();
		
		if (Util.isBetween(Util.removeTimeOfDate(new Date()), startDate, endDate)) {
			this.valid = true;
		}
	}

	public String getAdName() {
		return adName;
	}

	public void setAdName(String adName) {
		this.adName = adName;
	}

	public String getCreatName() {
		return creatName;
	}

	public void setCreatName(String creatName) {
		this.creatName = creatName;
	}

	public String getPurchType() {
		return purchType;
	}

	public void setPurchType(String purchType) {
		this.purchType = purchType;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public String getIcon() {
		if (Util.isValid(purchType)) {
			if (purchType.equals("G")) {
				return "fa-hexagon-check text-blue";
			} else if (purchType.equals("N")) {
				return "fa-hexagon-exclamation";
			} else if (purchType.equals("H")) {
				return "fa-house";
			}
		}
		
		return "fa-blank";
	}
	
	public String getPeriod() {
		if (startDate == null || endDate == null) {
			return "";
		}
		
		String ret = new SimpleDateFormat("yyyy M/d").format(startDate);
		if (Util.isSameDate(startDate, endDate)) {
			return ret;
		} else if (Util.isSameYear(startDate, endDate)) {
			return ret + " ~ " + new SimpleDateFormat("M/d").format(endDate);
		} else {
			return ret + " ~ " + new SimpleDateFormat("yyyy M/d").format(endDate);
		}
	}
}
