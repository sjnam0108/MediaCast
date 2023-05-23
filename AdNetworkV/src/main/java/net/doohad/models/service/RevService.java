package net.doohad.models.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Tuple;

import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.adc.AdcAd;
import net.doohad.models.adc.AdcCreative;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.inv.InvSite;
import net.doohad.models.rev.RevAdSelCache;
import net.doohad.models.rev.RevAdSelect;
import net.doohad.models.rev.RevCreatDecn;
import net.doohad.models.rev.RevCreatHourlyPlay;
import net.doohad.models.rev.RevFbSelCache;
import net.doohad.models.rev.RevAdHourlyPlay;
import net.doohad.models.rev.RevInvenRequest;
import net.doohad.models.rev.RevObjTouch;
import net.doohad.models.rev.RevPlayHist;
import net.doohad.models.rev.RevScrHourlyPlay;
import net.doohad.models.rev.RevScrHrlyFailTot;
import net.doohad.models.rev.RevScrHrlyFbTot;
import net.doohad.models.rev.RevScrHrlyNoAdTot;
import net.doohad.models.rev.RevScrHrlyPlyTot;
import net.doohad.models.rev.RevScrStatusLine;
import net.doohad.models.rev.RevSitHrlyPlyTot;

@Transactional
public interface RevService {
	
	// Common
	public void flush();

	
	//
	// for RevAdSelect
	//
	// Common
	public RevAdSelect getAdSelect(int id);
	public void saveOrUpdate(RevAdSelect adSelect);
	public void deleteAdSelect(RevAdSelect adSelect);
	public void deleteAdSelects(List<RevAdSelect> adSelects);

	// for Kendo Grid Remote Read
	public DataSourceResult getAdSelectList(DataSourceRequest request);

	// for DAO specific
	public RevAdSelect getAdSelect(UUID uuid);
	public List<RevAdSelect> getLastAdSelectListByScreenId(int screenId, int maxRecords);
	public List<RevAdSelect> getAdSelectListByScreenId(int screenId);
	public List<RevAdSelect> getReportedAdSelectListOrderBySelDateBeforeReportDate(Date date);
	public List<RevAdSelect> getAdSelectListBeforeSelectDateOrderBySelDate(Date selectDate);
	public void deleteAdSelectBulkRowsInIds(List<Integer> ids);
	public List<Tuple> getAdSelectHourStatTupleList1();
	public List<Tuple> getAdSelectHourStatTupleList2();
	public List<Tuple> getAdSelectMediumStatTupleList();
	public List<Tuple> getAdSelectMinStatTupleList1();
	public List<Tuple> getAdSelectMinStatTupleList2();

	
	//
	// for RevPlayHist
	//
	// Common
	public RevPlayHist getPlayHist(int id);
	public void saveOrUpdate(RevPlayHist playHist);
	public void deletePlayHist(RevPlayHist playHist);
	public void deletePlayHists(List<RevPlayHist> playHists);

	// for Kendo Grid Remote Read

	// for DAO specific
	public int getPlayHistCountByScreenIdStartDate(int screenId, Date startDate);
	public List<RevPlayHist> getFirstPlayHistList(int maxRecords);
	public void deleteBulkPlayHistRowsInIds(List<Integer> ids);
	public List<RevPlayHist> getLastPlayHistListByScreenId(int screenId, int maxRecords);
	public List<RevPlayHist> getPlayHistListByScreenId(int screenId);
	
	
	//
	// for RevScrHourlyPlay
	//
	// Common
	public RevScrHourlyPlay getScrHourlyPlay(int id);
	public void saveOrUpdate(RevScrHourlyPlay hourPlay);
	public void deleteScrHourlyPlay(RevScrHourlyPlay hourPlay);
	public void deleteScrHourlyPlays(List<RevScrHourlyPlay> hourPlays);

	// for Kendo Grid Remote Read
	public DataSourceResult getScrHourlyPlayList(DataSourceRequest request);

	// for DAO specific
	public RevScrHourlyPlay getScrHourlyPlay(InvScreen screen, AdcAd ad, Date playDate);
	public RevScrHourlyPlay getScrHourlyPlay(int screenId, int adId, Date playDate);
	public List<RevScrHourlyPlay> getScrHourlyPlayListBySiteIdAdIdPlayDate(
			int siteId, int adId, Date playDate);
	public List<Tuple> getScrSumScrHourlyPlayListByPlayDate(Date playDate);
	public List<Tuple> getSitSumScrHourlyPlayListByPlayDate(Date playDate);
	public List<Tuple> getScrHourlyPlayPlayDateListByLastUpdateDate(Date date);
	public List<RevScrHourlyPlay> getScrHourlyPlayListByAdIdPlayDate(int adId, Date playDate);
	public List<Tuple> getScrHourlyPlayAdStatListByScreenIdPlayDate(int screenId, Date playDate);
	public List<Tuple> getScrHourlyPlayStatGroupByAdPlayDate(Date playDate);
	public List<Tuple> getScrHourlyPlayStatGroupByCreatPlayDate(Date playDate);
	

	
	//
	// for RevAdSelCache
	//
	// Common
	public RevAdSelCache getAdSelCache(int id);
	public void saveOrUpdate(RevAdSelCache adSelCache);
	public void deleteAdSelCache(RevAdSelCache adSelCache);
	public void deleteAdSelCaches(List<RevAdSelCache> adSelCaches);

	// for Kendo Grid Remote Read
	public DataSourceResult getAdSelCacheList(DataSourceRequest request);

	// for DAO specific
	public RevAdSelCache getLastAdSelCacheByScreenIdAdCreativeId(int screenId, int adCreativeId);
	public Tuple getLastAdSelCacheTupleByScreenId(int screenId);
	public Tuple getLastAdSelCacheTupleByScreenIdAdId(int screenId, int adId);
	public Tuple getLastAdSelCacheTupleByScreenIdAdvertiserId(int screenId, int advertiserId);
	public List<Tuple> getAdSelCacheTupleListByScreenId(int screenId);
	
	
	//
	// for RevCreatDecn
	//
	// Common
	public RevCreatDecn getCreatDecn(int id);
	public void saveOrUpdate(RevCreatDecn creatDecn);

	// for Kendo Grid Remote Read
	public DataSourceResult getCreatDecnList(DataSourceRequest request);

	// for DAO specific

	
	//
	// for RevScrHrlyPlyTot
	//
	// Common
	public RevScrHrlyPlyTot getScrHrlyPlyTot(int id);
	public void saveOrUpdate(RevScrHrlyPlyTot hrlyPlyTot);
	public void deleteScrHrlyPlyTot(RevScrHrlyPlyTot hrlyPlyTot);
	public void deleteScrHrlyPlyTots(List<RevScrHrlyPlyTot> hrlyPlyTots);

	// for Kendo Grid Remote Read
	public DataSourceResult getScrHrlyPlyTotList(DataSourceRequest request, Date playDate);

	// for DAO specific
	public RevScrHrlyPlyTot getScrHrlyPlyTot(InvScreen screen, Date playDate);
	public List<RevScrHrlyPlyTot> getScrHrlyPlyTotListByMediumIdPlayDate(
			int mediumId, Date playDate);
	public List<RevScrHrlyPlyTot> getScrHrlyPlyTotListByPlayDate(Date playDate);
	public List<Tuple> getScrHrlyPlyTotTupleListByPlayDate(Date playDate);
	public Tuple getScrHrlyPlyTotStatByMediumIdPlayDate(int mediumId, Date playDate);
	public Double getStdScrHrlyPlyTotByMediumIdPlayDate(int mediumId, Date playDate);
	public Tuple getAvgScrHrlyPlyTotByMediumIdBetweenPlayDates(int mediumId, Date date1, Date date2);

	
	//
	// for RevSitHrlyPlyTot
	//
	// Common
	public RevSitHrlyPlyTot getSitHrlyPlyTot(int id);
	public void saveOrUpdate(RevSitHrlyPlyTot hrlyPlyTot);
	public void deleteSitHrlyPlyTot(RevSitHrlyPlyTot hrlyPlyTot);
	public void deleteSitHrlyPlyTots(List<RevSitHrlyPlyTot> hrlyPlyTots);

	// for Kendo Grid Remote Read
	public DataSourceResult getSitHrlyPlyTotList(DataSourceRequest request, Date playDate);

	// for DAO specific
	public RevSitHrlyPlyTot getSitHrlyPlyTot(InvSite site, Date playDate);
	public List<RevSitHrlyPlyTot> getSitHrlyPlyTotListByMediumIdPlayDate(
			int mediumId, Date playDate);
	public List<RevSitHrlyPlyTot> getSitHrlyPlyTotListByPlayDate(Date playDate);
	public List<Tuple> getSitHrlyPlyTotTupleListByPlayDate(Date playDate);
	public Tuple getSitHrlyPlyTotStatByMediumIdPlayDate(int mediumId, Date playDate);

	
	//
	// for RevScrStatusLine
	//
	// Common
	public RevScrStatusLine getScrStatusLine(int id);
	public void saveOrUpdate(RevScrStatusLine statusLine);
	public void deleteScrStatusLine(RevScrStatusLine statusLine);
	public void deleteScrStatusLines(List<RevScrStatusLine> statusLines);

	// for Kendo Grid Remote Read

	// for DAO specific
	public RevScrStatusLine getScrStatusLine(int screenId, Date playDate);
	public List<RevScrStatusLine> getScrStatusLineListByScreenId(int screenId);
	public List<RevScrStatusLine> getScrStatusLineListByPlayDate(Date playDate);
	public Tuple getScrStatusLineTuple(int screenId, Date playDate);
	public void insertScrStatusLine(int screenId, Date playDate, String statusLine);
	public void updateScrStatusLine(int id, String statusLine);

	
	//
	// for RevInvenRequest
	//
	// Common
	public RevInvenRequest getInvenRequest(int id);
	public void saveOrUpdate(RevInvenRequest invenRequest);
	public void deleteInvenRequest(RevInvenRequest invenRequest);
	public void deleteInvenRequests(List<RevInvenRequest> invenRequests);

	// for Kendo Grid Remote Read
	public DataSourceResult getInvenRequestList(DataSourceRequest request);

	// for DAO specific

	
	//
	// for RevFbSelCache
	//
	// Common
	public RevFbSelCache getFbSelCache(int id);
	public void saveOrUpdate(RevFbSelCache fbSelCache);
	public void deleteFbSelCache(RevFbSelCache fbSelCache);
	public void deleteFbSelCaches(List<RevFbSelCache> fbSelCaches);

	// for Kendo Grid Remote Read

	// for DAO specific
	public Tuple getLastFbSelCacheTupleByScreenId(int screenId);

	
	//
	// for RevScrHrlyFailTot
	//
	// Common
	public RevScrHrlyFailTot getScrHrlyFailTot(int id);
	public void saveOrUpdate(RevScrHrlyFailTot hrlyFailTot);
	public void deleteScrHrlyFailTot(RevScrHrlyFailTot hrlyFailTot);
	public void deleteScrHrlyFailTots(List<RevScrHrlyFailTot> hrlyFailTots);

	// for Kendo Grid Remote Read
	public DataSourceResult getScrHrlyFailTotList(DataSourceRequest request, Date playDate);

	// for DAO specific
	public RevScrHrlyFailTot getScrHrlyFailTot(InvScreen screen, Date playDate);
	public RevScrHrlyFailTot getScrHrlyFailTot(int screenId, Date playDate);

	
	//
	// for RevScrHrlyNoAdTot
	//
	// Common
	public RevScrHrlyNoAdTot getScrHrlyNoAdTot(int id);
	public void saveOrUpdate(RevScrHrlyNoAdTot hrlyNoAdTot);
	public void deleteScrHrlyNoAdTot(RevScrHrlyNoAdTot hrlyNoAdTot);
	public void deleteScrHrlyNoAdTots(List<RevScrHrlyNoAdTot> hrlyNoAdTots);

	// for Kendo Grid Remote Read
	public DataSourceResult getScrHrlyNoAdTotList(DataSourceRequest request, Date playDate);

	// for DAO specific
	public RevScrHrlyNoAdTot getScrHrlyNoAdTot(InvScreen screen, Date playDate);
	public RevScrHrlyNoAdTot getScrHrlyNoAdTot(int screenId, Date playDate);
	public Tuple getScrHrlyNoAdTotStatByMediumIdPlayDate(int mediumId, Date playDate);

	
	//
	// for RevScrHrlyFbTot
	//
	// Common
	public RevScrHrlyFbTot getScrHrlyFbTot(int id);
	public void saveOrUpdate(RevScrHrlyFbTot hrlyFbTot);
	public void deleteScrHrlyFbTot(RevScrHrlyFbTot hrlyFbTot);
	public void deleteScrHrlyFbTots(List<RevScrHrlyFbTot> hrlyFbTots);

	// for Kendo Grid Remote Read
	public DataSourceResult getScrHrlyFbTotList(DataSourceRequest request, Date playDate);

	// for DAO specific
	public RevScrHrlyFbTot getScrHrlyFbTot(InvScreen screen, Date playDate);
	public RevScrHrlyFbTot getScrHrlyFbTot(int screenId, Date playDate);
	public Tuple getScrHrlyFbTotStatByMediumIdPlayDate(int mediumId, Date playDate);

	
	//
	// for RevObjTouch
	//
	// Common
	public RevObjTouch getObjTouch(int id);
	public void saveOrUpdate(RevObjTouch objTouch);
	public void deleteObjTouch(RevObjTouch objTouch);
	public void deleteObjTouches(List<RevObjTouch> objTouches);

	// for Kendo Grid Remote Read

	// for DAO specific
	public RevObjTouch getObjTouch(String type, int objId);
	public List<RevObjTouch> getObjTouchList();
	
	
	//
	// for RevAdHourlyPlay
	//
	// Common
	public RevAdHourlyPlay getAdHourlyPlay(int id);
	public void saveOrUpdate(RevAdHourlyPlay hourPlay);
	public void deleteAdHourlyPlay(RevAdHourlyPlay hourPlay);
	public void deleteAdHourlyPlays(List<RevAdHourlyPlay> hourPlays);

	// for Kendo Grid Remote Read
	public DataSourceResult getAdHourlyPlayList(DataSourceRequest request);

	// for DAO specific
	public RevAdHourlyPlay getAdHourlyPlay(AdcAd ad, Date playDate);
	
	
	//
	// for RevCreatHourlyPlay
	//
	// Common
	public RevCreatHourlyPlay getCreatHourlyPlay(int id);
	public void saveOrUpdate(RevCreatHourlyPlay hourPlay);
	public void deleteCreatHourlyPlay(RevCreatHourlyPlay hourPlay);
	public void deleteCreatHourlyPlays(List<RevCreatHourlyPlay> hourPlays);

	// for Kendo Grid Remote Read
	public DataSourceResult getCreatHourlyPlayList(DataSourceRequest request);

	// for DAO specific
	public RevCreatHourlyPlay getCreatHourlyPlay(AdcCreative creative, Date playDate);

	
	//
	// for Common
	//
	public int calcDailyInvenConnectCountByPlayDate(Date playDate);
}
