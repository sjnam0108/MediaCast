package net.doohad.viewmodels.adc;

import net.doohad.models.adc.AdcCreatFile;

public class AdcCreatFilePAItem {
	
	private int id;
	
	private String srcFilename;
	private String ctntFolderName;
	private String ctntFolderWebPath;
	private String thumbFilename;
	private String filename;
	private String uuid;
	private String resolution;
	private String mimeType;
	private String durSecs;
	private String dispSrcDurSecs;
	private String smartLength;
	private String dispFileLength;
	
	
	public AdcCreatFilePAItem(AdcCreatFile creatFile) {
		this.id = creatFile.getId();
		this.srcFilename = creatFile.getSrcFilename();
		this.ctntFolderName = creatFile.getCtntFolder().getName();
		this.ctntFolderWebPath = creatFile.getCtntFolder().getWebPath();
		this.thumbFilename = creatFile.getThumbFilename();
		this.filename = creatFile.getFilename();
		this.uuid = creatFile.getUuid().toString();
		this.resolution = creatFile.getResolution();
		this.mimeType = creatFile.getMimeType();
		this.smartLength = creatFile.getSmartLength();
		this.dispFileLength = creatFile.getDispFileLength();
		
		if (creatFile.getMediaType().equals("I")) {
			this.durSecs = "-";
			this.dispSrcDurSecs = "";
		} else {
			this.durSecs = String.valueOf(creatFile.getDurSecs()) + "s";
			this.dispSrcDurSecs = creatFile.getDispSrcDurSecs();
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSrcFilename() {
		return srcFilename;
	}

	public void setSrcFilename(String srcFilename) {
		this.srcFilename = srcFilename;
	}

	public String getCtntFolderName() {
		return ctntFolderName;
	}

	public void setCtntFolderName(String ctntFolderName) {
		this.ctntFolderName = ctntFolderName;
	}

	public String getThumbFilename() {
		return thumbFilename;
	}

	public void setThumbFilename(String thumbFilename) {
		this.thumbFilename = thumbFilename;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getCtntFolderWebPath() {
		return ctntFolderWebPath;
	}

	public void setCtntFolderWebPath(String ctntFolderWebPath) {
		this.ctntFolderWebPath = ctntFolderWebPath;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getDurSecs() {
		return durSecs;
	}

	public void setDurSecs(String durSecs) {
		this.durSecs = durSecs;
	}

	public String getDispSrcDurSecs() {
		return dispSrcDurSecs;
	}

	public void setDispSrcDurSecs(String dispSrcDurSecs) {
		this.dispSrcDurSecs = dispSrcDurSecs;
	}

	public String getSmartLength() {
		return smartLength;
	}

	public void setSmartLength(String smartLength) {
		this.smartLength = smartLength;
	}

	public String getDispFileLength() {
		return dispFileLength;
	}

	public void setDispFileLength(String dispFileLength) {
		this.dispFileLength = dispFileLength;
	}

}
