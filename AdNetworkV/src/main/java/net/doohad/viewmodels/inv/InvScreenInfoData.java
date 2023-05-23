package net.doohad.viewmodels.inv;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.doohad.models.inv.InvScreen;
import net.doohad.models.rev.RevObjTouch;
import net.doohad.models.rev.RevScrStatusLine;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.rev.RevRunningTimeItem;

public class InvScreenInfoData {

	//
	// [화면] 탭 속성
	//
	private String scrScreenID = "";
	private String scrReso = "";
	private String scrEffPeriod = "";
	private String scrMemo = "";
	
	private String scrDefaultDur = "";
	private String scrRangeDurs = "";
	
	private boolean scrActiveStatus = false;
	private boolean scrAdServerAvailable = false;
	
	// 세 조건(ActiveStatus, AdServerAvailable, Eff) 결과에 따라 설정
	private boolean scrActive = false;
	
	private boolean scrImageAllowed = false;
	private boolean scrVideoAllowed = false;
	
	private boolean scrMediumDurApplied = false;

	
	//
	// [사이트] 탭 속성
	//
	private String stName = "";
	private String stSiteID = "";
	private String stEffPeriod = "";
	private String stAddress = "";
	private String stMemo = "";
	
	private String stSiteCondName = "";
	private String stVenueType = "";
	private String stRegionName = "";

	
	//
	// [API] 탭 속성
	//
	private String apiLastFile = "";
	private String apiLastAd = "";
	private String apiLastReport = "";
	private String apiLastRetryReport = "";
	
	private String apiLastFileLong = "";
	private String apiLastAdLong = "";
	private String apiLastReportLong = "";
	private String apiLastRetryReportLong = "";

	
	//
	// [상태] 탭 속성
	//
	private String piePlayDate = "";
	private String piePlayingCount = "0";
	private String pieNothingCount = "0";
	private String pieBizHours = "";
	
	private List<RevRunningTimeItem> pieRunningTimeItems = new ArrayList<RevRunningTimeItem>();

	
	//
	// [위치] 탭 속성
	//
	
	// 기본 위치를 남산타워로
	private double mapLat = 37.5512164;
	private double mapLng = 126.98824864606178;
	
	private String mapMarkerUrl = "";
	
	
	public InvScreenInfoData() {}

	public InvScreenInfoData(InvScreen screen, RevScrStatusLine statusLine, Date playDate, RevObjTouch objTouch) {
		
		if (screen != null) {
			
			//
			// 화면
			//
			
			scrScreenID = screen.getShortName();
			scrReso = screen.getResolution().replace("x", " x ");
			
			scrEffPeriod = Util.getSmartPeriodStr(screen.getEffectiveStartDate(), screen.getEffectiveEndDate());
			
			scrActiveStatus = screen.isActiveStatus();
			scrAdServerAvailable = screen.isAdServerAvailable();
			
			scrActive = scrActiveStatus && scrAdServerAvailable && Util.isBetween(new Date(), 
					screen.getEffectiveStartDate(), screen.getEffectiveEndDate());
			
			scrImageAllowed = screen.isImageAllowed();
			scrVideoAllowed = screen.isVideoAllowed();
			
			if (screen.getDefaultDurSecs() == null || screen.getRangeDurAllowed() == null ||
					screen.getMinDurSecs() == null || screen.getMaxDurSecs() == null) {
				scrMediumDurApplied = true;
				scrDefaultDur = String.valueOf(screen.getMedium().getDefaultDurSecs());
				if (screen.getMedium().isRangeDurAllowed()) {
					scrRangeDurs = String.format("%d ~ %d", screen.getMedium().getMinDurSecs(), screen.getMedium().getMaxDurSecs());
				} else {
					scrRangeDurs = "허용 안됨";
				}
			} else {
				scrMediumDurApplied = false;
				scrDefaultDur = String.valueOf(screen.getDefaultDurSecs());
				if (screen.getRangeDurAllowed().booleanValue()) {
					scrRangeDurs = String.format("%d ~ %d", screen.getMinDurSecs().intValue(), screen.getMaxDurSecs().intValue());
				} else {
					scrRangeDurs = "허용 안됨";
				}
			}
			
			scrMemo = screen.getMemo();
			
			
			//
			// 사이트
			//
			
			stName = screen.getSite().getName();
			stSiteID = screen.getSite().getShortName();
			
			stEffPeriod = Util.getSmartPeriodStr(screen.getSite().getEffectiveStartDate(), screen.getSite().getEffectiveEndDate());
			
			stSiteCondName = screen.getSite().getSiteCond().getName();
			stVenueType = screen.getSite().getVenueType();
			
			stRegionName = screen.getSite().getRegionName();
			stAddress = screen.getSite().getAddress();
			stMemo = screen.getSite().getMemo();
			
			
			//
			// API
			//
			
			if (objTouch != null) {
				screen.setLastFileApiDate(objTouch.getDate1());
				screen.setLastAdRequestDate(objTouch.getDate2());
				screen.setLastAdReportDate(objTouch.getDate3());
				screen.setLastRetryReportDate(objTouch.getDate4());
			}
			apiLastFile = Util.getSmartDate(screen.getLastFileApiDate(), true, true);
			apiLastAd = Util.getSmartDate(screen.getLastAdRequestDate(), true, true);
			apiLastReport = Util.getSmartDate(screen.getLastAdReportDate(), true, true);
			apiLastRetryReport = Util.getSmartDate(screen.getLastRetryReportDate(), true, true);
			
			if (!apiLastFile.equals("-")) {
				apiLastFileLong = Util.getHumanTime((int)(new Date().getTime() - screen.getLastFileApiDate().getTime()) / 1000);
			}
			if (!apiLastAd.equals("-")) {
				apiLastAdLong = Util.getHumanTime((int)(new Date().getTime() - screen.getLastAdRequestDate().getTime()) / 1000);
			}
			if (!apiLastReport.equals("-")) {
				apiLastReportLong = Util.getHumanTime((int)(new Date().getTime() - screen.getLastAdReportDate().getTime()) / 1000);
			}
			if (!apiLastRetryReport.equals("-")) {
				apiLastRetryReportLong = Util.getHumanTime((int)(new Date().getTime() - screen.getLastRetryReportDate().getTime()) / 1000);
			}
			
			
			//
			// 상태
			//
			
			piePlayDate = Util.toDateString(playDate);
			
			if (statusLine != null && statusLine.getStatusLine().length() == 1440) {
    			char prevChar = '9';  // 의미없는 숫자 '9'
    			String hhmm = "00:00";
    			int prevIndex = -1;
    			
    			int status6 = 0;
    			int status2 = 0;
    			
    			String line = statusLine.getStatusLine();
    			if (playDate.compareTo(Util.removeTimeOfDate(new Date())) == 0) {
    				line = SolUtil.getTodayScrStatusLine(line);
    			}
    			
    			for (int i = 0; i < 1440; i ++) {
    				char aChar = line.charAt(i);
    				
    				switch (aChar) {
    				case '2':
    					status2 ++;
    					break;
    				case '6':
    					status6 ++;
    					break;
    				case '9':
    					break;
    				default:
    					status2 ++;
    					aChar = '2';
    					
    					break;
    				}
    				
    				if (prevChar != aChar) {
    					if (prevChar != '9') {
    						pieRunningTimeItems.add(
    		    					new RevRunningTimeItem(getStatusStr(prevChar) + "(" + hhmm + 
    		    							(i - prevIndex == 1 ? "" : " - " + Util.getHHmm(i - 1)) + ")", 
    		    							String.valueOf(prevChar), i - prevIndex));
    					}
    					
    					prevChar = aChar;
    					prevIndex = i;
    					hhmm = Util.getHHmm(i);
    				}
    			}
    			pieRunningTimeItems.add(
    					new RevRunningTimeItem(getStatusStr(prevChar) + "(" + hhmm + 
    							(1440 - prevIndex == 1 ? "" : " - " + Util.getHHmm(1439)) + ")", 
    							String.valueOf(prevChar), 1440 - prevIndex));

    			DecimalFormat dFormat = new DecimalFormat("##,##0");
    			
    			piePlayingCount = dFormat.format(status6);
    			pieNothingCount = dFormat.format(status2);
				
			}
			
			if (pieRunningTimeItems.size() == 0) {
				pieRunningTimeItems.add(new RevRunningTimeItem(getStatusStr('9'), "0", 1440));
			}
			
			pieBizHours = (Util.isValid(screen.getBizHour()) && screen.getBizHour().length() == 168)
					? screen.getBizHour() : screen.getMedium().getBizHour();
			
			
			//
			// 위치
			//
			
			double tmpLat = Util.parseDouble(screen.getSite().getLatitude());
			double tmpLng = Util.parseDouble(screen.getSite().getLongitude());
			
			if (tmpLat != -1d && tmpLng != -1d) {
				mapLat = tmpLat;
				mapLng = tmpLng;
			}
			
			mapMarkerUrl = SolUtil.getMarkerUrl(screen.getMedium().getShortName());

		}
	}
	
	private String getStatusStr(char c) {
		
		switch (c) {
		case '2':
			return "요청없음";
		case '6':
			return "송출 및 보고";
		}
		
		return "해당없음";
	}

	public String getScrScreenID() {
		return scrScreenID;
	}

	public void setScrScreenID(String scrScreenID) {
		this.scrScreenID = scrScreenID;
	}

	public String getScrReso() {
		return scrReso;
	}

	public void setScrReso(String scrReso) {
		this.scrReso = scrReso;
	}

	public String getScrEffPeriod() {
		return scrEffPeriod;
	}

	public void setScrEffPeriod(String scrEffPeriod) {
		this.scrEffPeriod = scrEffPeriod;
	}

	public boolean isScrActiveStatus() {
		return scrActiveStatus;
	}

	public void setScrActiveStatus(boolean scrActiveStatus) {
		this.scrActiveStatus = scrActiveStatus;
	}

	public boolean isScrAdServerAvailable() {
		return scrAdServerAvailable;
	}

	public void setScrAdServerAvailable(boolean scrAdServerAvailable) {
		this.scrAdServerAvailable = scrAdServerAvailable;
	}

	public boolean isScrActive() {
		return scrActive;
	}

	public void setScrActive(boolean scrActive) {
		this.scrActive = scrActive;
	}

	public boolean isScrImageAllowed() {
		return scrImageAllowed;
	}

	public void setScrImageAllowed(boolean scrImageAllowed) {
		this.scrImageAllowed = scrImageAllowed;
	}

	public boolean isScrVideoAllowed() {
		return scrVideoAllowed;
	}

	public void setScrVideoAllowed(boolean scrVideoAllowed) {
		this.scrVideoAllowed = scrVideoAllowed;
	}

	public String getScrDefaultDur() {
		return scrDefaultDur;
	}

	public void setScrDefaultDur(String scrDefaultDur) {
		this.scrDefaultDur = scrDefaultDur;
	}

	public String getScrRangeDurs() {
		return scrRangeDurs;
	}

	public void setScrRangeDurs(String scrRangeDurs) {
		this.scrRangeDurs = scrRangeDurs;
	}

	public boolean isScrMediumDurApplied() {
		return scrMediumDurApplied;
	}

	public void setScrMediumDurApplied(boolean scrMediumDurApplied) {
		this.scrMediumDurApplied = scrMediumDurApplied;
	}

	public String getScrMemo() {
		return scrMemo;
	}

	public void setScrMemo(String scrMemo) {
		this.scrMemo = scrMemo;
	}

	public String getStName() {
		return stName;
	}

	public void setStName(String stName) {
		this.stName = stName;
	}

	public String getStSiteID() {
		return stSiteID;
	}

	public void setStSiteID(String stSiteID) {
		this.stSiteID = stSiteID;
	}

	public String getStEffPeriod() {
		return stEffPeriod;
	}

	public void setStEffPeriod(String stEffPeriod) {
		this.stEffPeriod = stEffPeriod;
	}

	public String getStMemo() {
		return stMemo;
	}

	public void setStMemo(String stMemo) {
		this.stMemo = stMemo;
	}

	public String getStAddress() {
		return stAddress;
	}

	public void setStAddress(String stAddress) {
		this.stAddress = stAddress;
	}

	public String getStSiteCondName() {
		return stSiteCondName;
	}

	public void setStSiteCondName(String stSiteCondName) {
		this.stSiteCondName = stSiteCondName;
	}

	public String getStRegionName() {
		return stRegionName;
	}

	public void setStRegionName(String stRegionName) {
		this.stRegionName = stRegionName;
	}

	public String getStVenueType() {
		return stVenueType;
	}

	public void setStVenueType(String stVenueType) {
		this.stVenueType = stVenueType;
	}

	public String getApiLastFile() {
		return apiLastFile;
	}

	public void setApiLastFile(String apiLastFile) {
		this.apiLastFile = apiLastFile;
	}

	public String getApiLastAd() {
		return apiLastAd;
	}

	public void setApiLastAd(String apiLastAd) {
		this.apiLastAd = apiLastAd;
	}

	public String getApiLastReport() {
		return apiLastReport;
	}

	public void setApiLastReport(String apiLastReport) {
		this.apiLastReport = apiLastReport;
	}

	public String getApiLastFileLong() {
		return apiLastFileLong;
	}

	public void setApiLastFileLong(String apiLastFileLong) {
		this.apiLastFileLong = apiLastFileLong;
	}

	public String getApiLastAdLong() {
		return apiLastAdLong;
	}

	public void setApiLastAdLong(String apiLastAdLong) {
		this.apiLastAdLong = apiLastAdLong;
	}

	public String getApiLastReportLong() {
		return apiLastReportLong;
	}

	public void setApiLastReportLong(String apiLastReportLong) {
		this.apiLastReportLong = apiLastReportLong;
	}

	public String getPiePlayDate() {
		return piePlayDate;
	}

	public void setPiePlayDate(String piePlayDate) {
		this.piePlayDate = piePlayDate;
	}

	public String getPiePlayingCount() {
		return piePlayingCount;
	}

	public void setPiePlayingCount(String piePlayingCount) {
		this.piePlayingCount = piePlayingCount;
	}

	public String getPieNothingCount() {
		return pieNothingCount;
	}

	public void setPieNothingCount(String pieNothingCount) {
		this.pieNothingCount = pieNothingCount;
	}

	public List<RevRunningTimeItem> getPieRunningTimeItems() {
		return pieRunningTimeItems;
	}

	public void setPieRunningTimeItems(List<RevRunningTimeItem> pieRunningTimeItems) {
		this.pieRunningTimeItems = pieRunningTimeItems;
	}

	public double getMapLat() {
		return mapLat;
	}

	public void setMapLat(double mapLat) {
		this.mapLat = mapLat;
	}

	public double getMapLng() {
		return mapLng;
	}

	public void setMapLng(double mapLng) {
		this.mapLng = mapLng;
	}

	public String getPieBizHours() {
		return pieBizHours;
	}

	public void setPieBizHours(String pieBizHours) {
		this.pieBizHours = pieBizHours;
	}

	public String getMapMarkerUrl() {
		return mapMarkerUrl;
	}

	public void setMapMarkerUrl(String mapMarkerUrl) {
		this.mapMarkerUrl = mapMarkerUrl;
	}

	public String getApiLastRetryReport() {
		return apiLastRetryReport;
	}

	public void setApiLastRetryReport(String apiLastRetryReport) {
		this.apiLastRetryReport = apiLastRetryReport;
	}

	public String getApiLastRetryReportLong() {
		return apiLastRetryReportLong;
	}

	public void setApiLastRetryReportLong(String apiLastRetryReportLong) {
		this.apiLastRetryReportLong = apiLastRetryReportLong;
	}
	
}
