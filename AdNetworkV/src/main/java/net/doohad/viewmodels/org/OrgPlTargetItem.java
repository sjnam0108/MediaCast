package net.doohad.viewmodels.org;

import net.doohad.models.org.OrgPlTarget;

public class OrgPlTargetItem {
	
	private int id;
	private int siblingSeq;
	private int tgtCount;
	private int tgtScrCount;
	private int plId;
	
	private String filterType;
	private String invenType;
	private String tgtDisplay;
	private String tgtValue;

	public OrgPlTargetItem(OrgPlTarget plTarget) {
		this.id = plTarget.getId();
		this.siblingSeq = plTarget.getSiblingSeq();
		this.tgtCount = plTarget.getTgtCount();
		this.tgtScrCount = plTarget.getTgtScrCount();
		this.plId = plTarget.getPlaylist().getId();
		
		this.filterType = plTarget.getFilterType();
		this.invenType = plTarget.getInvenType();
		this.tgtDisplay = plTarget.getTgtDisplay();
		this.tgtValue = plTarget.getTgtValue();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSiblingSeq() {
		return siblingSeq;
	}

	public void setSiblingSeq(int siblingSeq) {
		this.siblingSeq = siblingSeq;
	}

	public int getTgtScrCount() {
		return tgtScrCount;
	}

	public void setTgtScrCount(int tgtScrCount) {
		this.tgtScrCount = tgtScrCount;
	}

	public String getFilterType() {
		return filterType;
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
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

	public String getTgtValue() {
		return tgtValue;
	}

	public void setTgtValue(String tgtValue) {
		this.tgtValue = tgtValue;
	}

	public int getTgtCount() {
		return tgtCount;
	}

	public void setTgtCount(int tgtCount) {
		this.tgtCount = tgtCount;
	}

	public int getPlId() {
		return plId;
	}

	public void setPlId(int plId) {
		this.plId = plId;
	}
}
