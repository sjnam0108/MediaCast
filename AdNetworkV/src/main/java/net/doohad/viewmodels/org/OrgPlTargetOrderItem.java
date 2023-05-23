package net.doohad.viewmodels.org;

import net.doohad.models.org.OrgPlTarget;
import net.doohad.utils.Util;

public class OrgPlTargetOrderItem {

	private int id;
	private int seq;
	
	private String invenType;
	private String tgtDisplay;
	
	public OrgPlTargetOrderItem(OrgPlTarget plTarget, int seq) {
		this.id = plTarget.getId();
		this.invenType = plTarget.getInvenType();
		this.tgtDisplay = plTarget.getTgtDisplay();
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
