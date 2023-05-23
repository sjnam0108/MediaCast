package net.doohad.models.rev;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import net.doohad.utils.Util;

@Entity
@Table(name="REV_PLAY_HISTS")
public class RevPlayHist {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PLAY_HIST_ID")
	private int id;
	
	// 광고 선택 UUID
	@Column(name = "UUID", length = 36)
	private String uuid;
	
	// 광고 선택 일시 == whoCreationDate
	@Column(name = "SEL_DATE")
	private Date selectDate;
	
	// 보고 일시
	@Column(name = "REPORT_DATE")
	private Date reportDate;
	
	// 보고 결과
	@Column(name = "RESULT")
	private Boolean result;

	// 방송 시작 일시
	@Column(name = "PLAY_BEGIN_DATE")
	private Date playBeginDate;
	
	// 방송 종료 일시
	@Column(name = "PLAY_END_DATE")
	private Date playEndDate;
	
	// 재생 시간(밀리초 단위)
	@Column(name = "DURATION")
	private Integer duration;
	
	// 매체 번호
	@Column(name = "MEDIUM_ID")
	private Integer mediumId;

	// 매체ID
	@Column(name = "MEDIUM_SHORT_NAME", length = 50)
	private String mediumShortName;
	
	// 광고 소재 번호
	@Column(name = "CREATIVE_ID")
	private Integer creativeId;
	
	// 광고 소재명
	@Column(name = "CREATIVE_NAME", length = 200)
	private String creativeName;
	
	// 광고 번호
	@Column(name = "AD_ID")
	private Integer adId;
	
	// 광고명
	@Column(name = "AD_NAME", length = 200)
	private String adName;
	
	// 화면 번호
	@Column(name = "SCREEN_ID")
	private Integer screenId;
	
	// 화면명
	@Column(name = "SCREEN_NAME", length = 200)
	private String screenName;

	
	public RevPlayHist() {}
	
	public RevPlayHist(RevAdSelect adSelect) {
		
		this.uuid = adSelect.getUuid().toString();
		this.selectDate = adSelect.getSelectDate();
		this.reportDate = adSelect.getReportDate();
		this.result = adSelect.getResult();
		this.playBeginDate = adSelect.getPlayBeginDate();
		this.playEndDate = adSelect.getPlayEndDate();
		this.duration = adSelect.getDuration();
		this.mediumId = adSelect.getMedium().getId();
		this.mediumShortName = adSelect.getMedium().getShortName();
		this.screenId = adSelect.getScreen().getId();
		this.screenName = adSelect.getScreen().getName();
		this.creativeId = adSelect.getCreative().getId();
		this.creativeName = adSelect.getCreative().getName();
		
		// 추가
		if (adSelect.getAdCreative() != null) {
			this.adId = adSelect.getAdCreative().getAd().getId();
			this.adName = adSelect.getAdCreative().getAd().getName();
		}
	}
	
	public RevPlayHist(Date selectDate, int mediumId, String mediumShortName, int screenId, String screenName,
			int creativeId, String creativeName) {
		this.selectDate = selectDate;
		this.mediumId = mediumId;
		this.mediumShortName = mediumShortName;
		this.screenId = screenId;
		this.screenName = screenName;
		this.creativeId = creativeId;
		this.creativeName = creativeName;
	}
	
	public String toLogString() {
		
		//selectDate	adName	adId	creativeName	creativeId	screenName	screenId	result	reportDate	
		//playBeginDate	playEndDate		duration	mediumShortName	mediumId	uuid	id

		//2023.03.15.183015
		String dateFormat = "yyyy.MM.dd.HHmmss";

		return (selectDate == null ? "-" : Util.toSimpleString(selectDate, dateFormat)) + "\t" + 
				(adName == null ? "-" : adName) + "\t" + 
				(adId == null ? "-" : String.valueOf(adId)) + "\t" +
				creativeName + "\t" + String.valueOf(creativeId) + "\t" + screenName + "\t" + String.valueOf(screenId) + "\t" +
				(result == null ? "-" : (result ? "Y" : "N")) + "\t" + (reportDate == null ? "-" : Util.toSimpleString(reportDate, dateFormat)) + "\t" +
				(playBeginDate == null ? "-" : Util.toSimpleString(playBeginDate, dateFormat)) + "\t" +
				(playEndDate == null ? "-" : Util.toSimpleString(playEndDate, dateFormat)) + "\t" +
				(duration == null ? "-" : String.valueOf(duration)) + "\t" + mediumShortName + "\t" + String.valueOf(mediumId) + "\t" +
				(uuid == null ? "-" : uuid) + "\t" + 
				String.valueOf(id);
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Date getSelectDate() {
		return selectDate;
	}

	public void setSelectDate(Date selectDate) {
		this.selectDate = selectDate;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public Boolean getResult() {
		return result;
	}

	public void setResult(Boolean result) {
		this.result = result;
	}

	public Date getPlayBeginDate() {
		return playBeginDate;
	}

	public void setPlayBeginDate(Date playBeginDate) {
		this.playBeginDate = playBeginDate;
	}

	public Date getPlayEndDate() {
		return playEndDate;
	}

	public void setPlayEndDate(Date playEndDate) {
		this.playEndDate = playEndDate;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Integer getMediumId() {
		return mediumId;
	}

	public void setMediumId(Integer mediumId) {
		this.mediumId = mediumId;
	}

	public String getMediumShortName() {
		return mediumShortName;
	}

	public void setMediumShortName(String mediumShortName) {
		this.mediumShortName = mediumShortName;
	}

	public Integer getCreativeId() {
		return creativeId;
	}

	public void setCreativeId(Integer creativeId) {
		this.creativeId = creativeId;
	}

	public String getCreativeName() {
		return creativeName;
	}

	public void setCreativeName(String creativeName) {
		this.creativeName = creativeName;
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

	public Integer getAdId() {
		return adId;
	}

	public void setAdId(Integer adId) {
		this.adId = adId;
	}

	public String getAdName() {
		return adName;
	}

	public void setAdName(String adName) {
		this.adName = adName;
	}
	
}
