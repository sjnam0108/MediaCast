package net.doohad.viewmodels.adc;

import net.doohad.models.adc.AdcAdTarget;
import net.doohad.utils.Util;

public class AdcAdTargetOrderItem {

	private int id;
	private int seq;
	
	private String invenType;
	private String tgtDisplay;
	
	public AdcAdTargetOrderItem(AdcAdTarget adTarget, int seq) {
		this.id = adTarget.getId();
		this.invenType = adTarget.getInvenType();
		this.tgtDisplay = adTarget.getTgtDisplay();
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
