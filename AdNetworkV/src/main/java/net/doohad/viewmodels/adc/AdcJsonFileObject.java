package net.doohad.viewmodels.adc;

import java.util.Date;
import java.util.List;

import net.doohad.models.adc.AdcCreatFile;
import net.doohad.models.adc.AdcCreative;
import net.doohad.utils.Util;

public class AdcJsonFileObject {

	private int adId;
	
	private String adName;
	private String advertiserName;
	private String adUuid;
	private String httpFilename;
	private String mimeType;
	private String resolution;
	
	private long fileLength;
	private int durSecs;
	
	private Date creationDate;
	
	private Integer formalDurSecs;
	private double srcDurSecs;
	private String filename;
	
	private AdcCreative creative;

	
	public AdcJsonFileObject() {}
	
	public AdcJsonFileObject(AdcCreatFile creatFile) {
		this.adId = creatFile.getCreative().getId();
		this.adName = creatFile.getCreative().getName();
		this.advertiserName = creatFile.getCreative().getAdvertiser().getName();
		this.adUuid = creatFile.getUuid().toString();
		this.httpFilename = creatFile.getHttpFilename();
		this.fileLength = creatFile.getFileLength();
		this.mimeType = creatFile.getMimeType();
		this.resolution = creatFile.getResolution();
		this.durSecs = creatFile.getDurSecs();
		this.creationDate = creatFile.getWhoCreationDate();
		
		this.formalDurSecs = creatFile.getFormalDurSecs();
		this.srcDurSecs = creatFile.getSrcDurSecs();
		this.filename = creatFile.getFilename();
	}
	
	
	public String getUuidDurFilename() {
		if (formalDurSecs == null) {
			return adUuid + "_(" + (Math.round(srcDurSecs * 10d) / 10d) + "s)." + Util.getFileExt(filename);
		} else {
			return adUuid + "_(" + formalDurSecs.intValue() + "s)." + Util.getFileExt(filename);
		}
	}
	
	public int getWidth() {
		if (Util.isValid(resolution)) {
			List<String> items = Util.tokenizeValidStr(resolution, "x");
			return Util.parseInt(items.get(0));
		}
		return 0;
	}
	
	public int getHeight() {
		if (Util.isValid(resolution)) {
			List<String> items = Util.tokenizeValidStr(resolution, "x");
			return Util.parseInt(items.get(1));
		}
		return 0;
	}

	public int getDurSecs() {
		return (formalDurSecs == null) ? durSecs : formalDurSecs.intValue();
	}
	
	public int getFormalDurMillis() {
		
		if (formalDurSecs == null) {
			return (int)Math.round(srcDurSecs * 1000);
		} else {
			return formalDurSecs.intValue() * 1000;
		}
	}
	
	public String getCreationDateStr() {
		return Util.toSimpleString(creationDate, "yyyyMMdd HHmmss");
	}

	public String getMimeType() {
		return mimeType;
	}

	public long getFileLength() {
		return fileLength;
	}

	public String getHttpFilename() {
		return httpFilename;
	}

	public String getAdvertiserName() {
		return advertiserName;
	}

	public String getAdUuid() {
		return adUuid;
	}

	public int getAdId() {
		return adId;
	}

	public String getAdName() {
		return adName;
	}

	public void setFormalDurSecs(Integer formalDurSecs) {
		this.formalDurSecs = formalDurSecs;
	}

	public Integer getFormalDurSecs() {
		return formalDurSecs;
	}

	public AdcCreative getCreative() {
		return creative;
	}

	public void setCreative(AdcCreative creative) {
		this.creative = creative;
	}

}
