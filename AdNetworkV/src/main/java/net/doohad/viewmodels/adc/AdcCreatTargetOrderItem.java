package net.doohad.viewmodels.adc;

import net.doohad.models.adc.AdcCreatTarget;
import net.doohad.utils.Util;

public class AdcCreatTargetOrderItem {

	private int id;
	private int seq;
	
	private String invenType;
	private String tgtDisplay;
	
	public AdcCreatTargetOrderItem(AdcCreatTarget creatTarget, int seq) {
		this.id = creatTarget.getId();
		this.invenType = creatTarget.getInvenType();
		this.tgtDisplay = creatTarget.getTgtDisplay();
		this.seq = seq;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getInvenType() {
		return invenType;
	}

	public void setInvenType(String invenType) {
		this.invenType = invenType;
	}

	public String getTgtDisplay() {
		return tgtDisplay;
	}

	public void setTgtDisplay(String tgtDisplay) {
		this.tgtDisplay = tgtDisplay;
	}
	
	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getTgtDisplayShort() {
		if (Util.isValid(tgtDisplay) && tgtDisplay.length() >= 20) {
			return tgtDisplay.substring(0, 20) + "...";
		} else {
			return tgtDisplay;
		}
	}
}
