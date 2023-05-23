package net.doohad.models.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.persistence.Tuple;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
import net.doohad.models.rev.dao.RevAdSelCacheDao;
import net.doohad.models.rev.dao.RevAdSelectDao;
import net.doohad.models.rev.dao.RevCreatDecnDao;
import net.doohad.models.rev.dao.RevCreatHourlyPlayDao;
import net.doohad.models.rev.dao.RevFbSelCacheDao;
import net.doohad.models.rev.dao.RevAdHourlyPlayDao;
import net.doohad.models.rev.dao.RevInvenRequestDao;
import net.doohad.models.rev.dao.RevObjTouchDao;
import net.doohad.models.rev.dao.RevPlayHistDao;
import net.doohad.models.rev.dao.RevScrHourlyPlayDao;
import net.doohad.models.rev.dao.RevScrHrlyFailTotDao;
import net.doohad.models.rev.dao.RevScrHrlyFbTotDao;
import net.doohad.models.rev.dao.RevScrHrlyNoAdTotDao;
import net.doohad.models.rev.dao.RevScrHrlyPlyTotDao;
import net.doohad.models.rev.dao.RevScrStatusLineDao;
import net.doohad.models.rev.dao.RevSitHrlyPlyTotDao;
import net.doohad.viewmodels.rev.RevScrHrlyPlySumItem;

@Transactional
@Service("revService")
public class RevServiceImpl implements RevService {
	private static final Logger logger = LoggerFactory.getLogger(RevServiceImpl.class);

	//
    // General
    //
    @Autowired
    private SessionFactory sessionFactory;
    
	@Override
	public void flush() {
		
		sessionFactory.getCurrentSession().flush();
	}

	
    
    //
    // DAO
    //
    @Autowired
    private RevAdSelectDao adSelectDao;

    @Autowired
    private RevPlayHistDao playHistDao;

    @Autowired
    private RevScrHourlyPlayDao scrHourlyPlayDao;

    @Autowired
    private RevAdSelCacheDao adSelCacheDao;

    @Autowired
    private RevFbSelCacheDao fbSelCacheDao;

    @Autowired
    private RevCreatDecnDao creatDecnDao;

    @Autowired
    private RevScrHrlyPlyTotDao scrHrlyPlyTotDao;

    @Autowired
    private RevSitHrlyPlyTotDao sitHrlyPlyTotDao;

    @Autowired
    private RevScrStatusLineDao scrStatusLineDao;

    @Autowired
    private RevInvenRequestDao invenRequestDao;

    @Autowired
    private RevScrHrlyFailTotDao scrHrlyFailTotDao;

    @Autowired
    private RevScrHrlyNoAdTotDao scrHrlyNoAdTotDao;

    @Autowired
    private RevScrHrlyFbTotDao scrHrlyFbTotDao;

    @Autowired
    private RevObjTouchDao objTouchDao;

    @Autowired
    private RevAdHourlyPlayDao adHourlyPlayDao;

    @Autowired
    private RevCreatHourlyPlayDao creatHourlyPlayDao;

    @Autowired
    private InvService invService;

    
    
	//
	// for RevAdSelectDao
	//
	@Override
	public RevAdSelect getAdSelect(int id) {
		return adSelectDao.get(id);
	}

	@Override
	public void saveOrUpdate(RevAdSelect adSelect) {
		adSelectDao.saveOrUpdate(adSelect);
	}

	@Override
	public void deleteAdSelect(RevAdSelect adSelect) {
		adSelectDao.delete(adSelect);
	}

	@Override
	public void deleteAdSelects(List<RevAdSelect> adSelects) {
		adSelectDao.delete(adSelects);
	}

	@Override
	public DataSourceResult getAdSelectList(DataSourceRequest request) {
		return adSelectDao.getList(request);
	}

	@Override
	public RevAdSelect getAdSelect(UUID uuid) {
		return adSelectDao.get(uuid);
	}

	@Override
	public List<RevAdSelect> getLastAdSelectListByScreenId(int screenId, int maxRecords) {
		return adSelectDao.getLastListByScreenId(screenId, maxRecords);
	}

	@Override
	public List<RevAdSelect> getAdSelectListByScreenId(int screenId) {
		return adSelectDao.getListByScreenId(screenId);
	}

	@Override
	public List<RevAdSelect> getReportedAdSelectListOrderBySelDateBeforeReportDate(Date date) {
		return adSelectDao.getReportedListOrderBySelDateBeforeReportDate(date);
	}

	@Override
	public List<RevAdSelect> getAdSelectListBeforeSelectDateOrderBySelDate(Date selectDate) {
		return adSelectDao.getListBeforeSelectDateOrderBySelDate(selectDate);
	}

	@Override
	public void deleteAdSelectBulkRowsInIds(List<Integer> ids) {
		adSelectDao.deleteBulkRowsInIds(ids);
	}

	@Override
	public List<Tuple> getAdSelectHourStatTupleList1() {
		return adSelectDao.getHourStatTupleList1();
	}

	@Override
	public List<Tuple> getAdSelectHourStatTupleList2() {
		return adSelectDao.getHourStatTupleList2();
	}

	@Override
	public List<Tuple> getAdSelectMediumStatTupleList() {
		return adSelectDao.getMediumStatTupleList();
	}

	@Override
	public List<Tuple> getAdSelectMinStatTupleList1() {
		return adSelectDao.getMinStatTupleList1();
	}

	@Override
	public List<Tuple> getAdSelectMinStatTupleList2() {
		return adSelectDao.getMinStatTupleList2();
	}

    
    
	//
	// for RevPlayHistDao
	//
	@Override
	public RevPlayHist getPlayHist(int id) {
		return playHistDao.get(id);
	}

	@Override
	public void saveOrUpdate(RevPlayHist playHist) {
		playHistDao.saveOrUpdate(playHist);
	}

	@Override
	public void deletePlayHist(RevPlayHist playHist) {
		playHistDao.delete(playHist);
	}

	@Override
	public void deletePlayHists(List<RevPlayHist> playHists) {
		playHistDao.delete(playHists);
	}

	@Override
	public int getPlayHistCountByScreenIdStartDate(int screenId, Date startDate) {
		return playHistDao.getCountByScreenIdStartDate(screenId, startDate);
	}

	@Override
	public List<RevPlayHist> getFirstPlayHistList(int maxRecords) {
		return playHistDao.getFirstList(maxRecords);
	}

	@Override
	public void deleteBulkPlayHistRowsInIds(List<Integer> ids) {
		playHistDao.deleteBulkRowsInIds(ids);
	}

	@Override
	public List<RevPlayHist> getLastPlayHistListByScreenId(int screenId, int maxRecords) {
		return playHistDao.getLastListByScreenId(screenId, maxRecords);
	}

	@Override
	public List<RevPlayHist> getPlayHistListByScreenId(int screenId) {
		return playHistDao.getListByScreenId(screenId);
	}

    
    
	//
	// for RevScrHourlyPlayDao
	//
	@Override
	public RevScrHourlyPlay getScrHourlyPlay(int id) {
		return scrHourlyPlayDao.get(id);
	}

	@Override
	public void saveOrUpdate(RevScrHourlyPlay hourPlay) {
		scrHourlyPlayDao.saveOrUpdate(hourPlay);
	}

	@Override
	public void deleteScrHourlyPlay(RevScrHourlyPlay hourPlay) {
		scrHourlyPlayDao.delete(hourPlay);
	}

	@Override
	public void deleteScrHourlyPlays(List<RevScrHourlyPlay> hourPlays) {
		scrHourlyPlayDao.delete(hourPlays);
	}

	@Override
	public DataSourceResult getScrHourlyPlayList(DataSourceRequest request) {
		return scrHourlyPlayDao.getList(request);
	}

	@Override
	public RevScrHourlyPlay getScrHourlyPlay(InvScreen screen, AdcAd ad, Date playDate) {
		return scrHourlyPlayDao.get(screen, ad, playDate);
	}

	@Override
	public RevScrHourlyPlay getScrHourlyPlay(int screenId, int adId, Date playDate) {
		return scrHourlyPlayDao.get(screenId, adId, playDate);
	}

	@Override
	public List<RevScrHourlyPlay> getScrHourlyPlayListBySiteIdAdIdPlayDate(int siteId, int adId, Date playDate) {
		return scrHourlyPlayDao.getListBySiteIdAdIdPlayDate(siteId, adId, playDate);
	}

	@Override
	public List<Tuple> getScrSumScrHourlyPlayListByPlayDate(Date playDate) {
		return scrHourlyPlayDao.getScrSumListByPlayDate(playDate);
	}

	@Override
	public List<Tuple> getSitSumScrHourlyPlayListByPlayDate(Date playDate) {
		return scrHourlyPlayDao.getSitSumListByPlayDate(playDate);
	}

	@Override
	public List<Tuple> getScrHourlyPlayPlayDateListByLastUpdateDate(Date date) {
		return scrHourlyPlayDao.getPlayDateListByLastUpdateDate(date);
	}

	@Override
	public List<RevScrHourlyPlay> getScrHourlyPlayListByAdIdPlayDate(int adId, Date playDate) {
		return scrHourlyPlayDao.getListByAdIdPlayDate(adId, playDate);
	}

	@Override
	public List<Tuple> getScrHourlyPlayAdStatListByScreenIdPlayDate(int screenId, Date playDate) {
		return scrHourlyPlayDao.getAdStatListByScreenIdPlayDate(screenId, playDate);
	}

	@Override
	public List<Tuple> getScrHourlyPlayStatGroupByAdPlayDate(Date playDate) {
		return scrHourlyPlayDao.getStatGroupByAdPlayDate(playDate);
	}

	@Override
	public List<Tuple> getScrHourlyPlayStatGroupByCreatPlayDate(Date playDate) {
		return scrHourlyPlayDao.getStatGroupByCreatPlayDate(playDate);
	}

    
    
	//
	// for RevAdSelCacheDao
	//
	@Override
	public RevAdSelCache getAdSelCache(int id) {
		return adSelCacheDao.get(id);
	}

	@Override
	public void saveOrUpdate(RevAdSelCache adSelCache) {
		adSelCacheDao.saveOrUpdate(adSelCache);
	}

	@Override
	public void deleteAdSelCache(RevAdSelCache adSelCache) {
		adSelCacheDao.delete(adSelCache);
	}

	@Override
	public void deleteAdSelCaches(List<RevAdSelCache> adSelCaches) {
		adSelCacheDao.delete(adSelCaches);
	}

	@Override
	public DataSourceResult getAdSelCacheList(DataSourceRequest request) {
		return adSelCacheDao.getList(request);
	}

	@Override
	public RevAdSelCache getLastAdSelCacheByScreenIdAdCreativeId(int screenId, int adCreativeId) {
		return adSelCacheDao.getLastByScreenIdAdCreativeId(screenId, adCreativeId);
	}

	@Override
	public Tuple getLastAdSelCacheTupleByScreenId(int screenId) {
		return adSelCacheDao.getLastTupleByScreenId(screenId);
	}

	@Override
	public Tuple getLastAdSelCacheTupleByScreenIdAdId(int screenId, int adId) {
		return adSelCacheDao.getLastTupleByScreenIdAdId(screenId, adId);
	}

	@Override
	public Tuple getLastAdSelCacheTupleByScreenIdAdvertiserId(int screenId, int advertiserId) {
		return adSelCacheDao.getLastTupleByScreenIdAdvertiserId(screenId, advertiserId);
	}

	@Override
	public List<Tuple> getAdSelCacheTupleListByScreenId(int screenId) {
		return adSelCacheDao.getTupleListByScreenId(screenId);
	}

    
    
	//
	// for RevCreatDecnDao
	//
	@Override
	public RevCreatDecn getCreatDecn(int id) {
		return creatDecnDao.get(id);
	}

	@Override
	public void saveOrUpdate(RevCreatDecn creatDecn) {
		creatDecnDao.saveOrUpdate(creatDecn);
	}

	@Override
	public DataSourceResult getCreatDecnList(DataSourceRequest request) {
		return creatDecnDao.getList(request);
	}

    
    
	//
	// for RevScrHrlyPlyTotDao
	//
	@Override
	public RevScrHrlyPlyTot getScrHrlyPlyTot(int id) {
		return scrHrlyPlyTotDao.get(id);
	}

	@Override
	public void saveOrUpdate(RevScrHrlyPlyTot hrlyPlyTot) {
		scrHrlyPlyTotDao.saveOrUpdate(hrlyPlyTot);
	}

	@Override
	public void deleteScrHrlyPlyTot(RevScrHrlyPlyTot hrlyPlyTot) {
		scrHrlyPlyTotDao.delete(hrlyPlyTot);
	}

	@Override
	public void deleteScrHrlyPlyTots(List<RevScrHrlyPlyTot> hrlyPlyTots) {
		scrHrlyPlyTotDao.delete(hrlyPlyTots);
	}

	@Override
	public DataSourceResult getScrHrlyPlyTotList(DataSourceRequest request, Date playDate) {
		return scrHrlyPlyTotDao.getList(request, playDate);
	}

	@Override
	public RevScrHrlyPlyTot getScrHrlyPlyTot(InvScreen screen, Date playDate) {
		return scrHrlyPlyTotDao.get(screen, playDate);
	}

	@Override
	public List<RevScrHrlyPlyTot> getScrHrlyPlyTotListByMediumIdPlayDate(int mediumId, Date playDate) {
		return scrHrlyPlyTotDao.getListByMediumIdPlayDate(mediumId, playDate);
	}

	@Override
	public List<RevScrHrlyPlyTot> getScrHrlyPlyTotListByPlayDate(Date playDate) {
		return scrHrlyPlyTotDao.getListByPlayDate(playDate);
	}

	@Override
	public List<Tuple> getScrHrlyPlyTotTupleListByPlayDate(Date playDate) {
		return scrHrlyPlyTotDao.getTupleListByPlayDate(playDate);
	}

	@Override
	public Tuple getScrHrlyPlyTotStatByMediumIdPlayDate(int mediumId, Date playDate) {
		return scrHrlyPlyTotDao.getStatByMediumIdPlayDate(mediumId, playDate);
	}

	@Override
	public Double getStdScrHrlyPlyTotByMediumIdPlayDate(int mediumId, Date playDate) {
		return scrHrlyPlyTotDao.getStdByMediumIdPlayDate(mediumId, playDate);
	}

	@Override
	public Tuple getAvgScrHrlyPlyTotByMediumIdBetweenPlayDates(int mediumId, Date date1, Date date2) {
		return scrHrlyPlyTotDao.getAvgByMediumIdBetweenPlayDates(mediumId, date1, date2);
	}

    
    
	//
	// for RevSitHrlyPlyTotDao
	//
	@Override
	public RevSitHrlyPlyTot getSitHrlyPlyTot(int id) {
		return sitHrlyPlyTotDao.get(id);
	}

	@Override
	public void saveOrUpdate(RevSitHrlyPlyTot hrlyPlyTot) {
		sitHrlyPlyTotDao.saveOrUpdate(hrlyPlyTot);
	}

	@Override
	public void deleteSitHrlyPlyTot(RevSitHrlyPlyTot hrlyPlyTot) {
		sitHrlyPlyTotDao.delete(hrlyPlyTot);
	}

	@Override
	public void deleteSitHrlyPlyTots(List<RevSitHrlyPlyTot> hrlyPlyTots) {
		sitHrlyPlyTotDao.delete(hrlyPlyTots);
	}

	@Override
	public DataSourceResult getSitHrlyPlyTotList(DataSourceRequest request, Date playDate) {
		return sitHrlyPlyTotDao.getList(request, playDate);
	}

	@Override
	public RevSitHrlyPlyTot getSitHrlyPlyTot(InvSite site, Date playDate) {
		return sitHrlyPlyTotDao.get(site, playDate);
	}

	@Override
	public List<RevSitHrlyPlyTot> getSitHrlyPlyTotListByMediumIdPlayDate(int mediumId, Date playDate) {
		return sitHrlyPlyTotDao.getListByMediumIdPlayDate(mediumId, playDate);
	}

	@Override
	public List<RevSitHrlyPlyTot> getSitHrlyPlyTotListByPlayDate(Date playDate) {
		return sitHrlyPlyTotDao.getListByPlayDate(playDate);
	}

	@Override
	public List<Tuple> getSitHrlyPlyTotTupleListByPlayDate(Date playDate) {
		return sitHrlyPlyTotDao.getTupleListByPlayDate(playDate);
	}

	@Override
	public Tuple getSitHrlyPlyTotStatByMediumIdPlayDate(int mediumId, Date playDate) {
		return sitHrlyPlyTotDao.getStatByMediumIdPlayDate(mediumId, playDate);
	}

    
    
	//
	// for RevScrStatusLineDao
	//
	@Override
	public RevScrStatusLine getScrStatusLine(int id) {
		return scrStatusLineDao.get(id);
	}

	@Override
	public void saveOrUpdate(RevScrStatusLine statusLine) {
		scrStatusLineDao.saveOrUpdate(statusLine);
	}

	@Override
	public void deleteScrStatusLine(RevScrStatusLine statusLine) {
		scrStatusLineDao.delete(statusLine);
	}

	@Override
	public void deleteScrStatusLines(List<RevScrStatusLine> statusLines) {
		scrStatusLineDao.delete(statusLines);
	}

	@Override
	public RevScrStatusLine getScrStatusLine(int screenId, Date playDate) {
		return scrStatusLineDao.get(screenId, playDate);
	}

	@Override
	public List<RevScrStatusLine> getScrStatusLineListByScreenId(int screenId) {
		return scrStatusLineDao.getListByScreenId(screenId);
	}

	@Override
	public List<RevScrStatusLine> getScrStatusLineListByPlayDate(Date playDate) {
		return scrStatusLineDao.getListByPlayDate(playDate);
	}

	@Override
	public Tuple getScrStatusLineTuple(int screenId, Date playDate) {
		return scrStatusLineDao.getTuple(screenId, playDate);
	}

	@Override
	public void insertScrStatusLine(int screenId, Date playDate, String statusLine) {
		scrStatusLineDao.insert(screenId, playDate, statusLine);
	}

	@Override
	public void updateScrStatusLine(int id, String statusLine) {
		scrStatusLineDao.update(id, statusLine);
	}

    
    
	//
	// for RevInvenRequestDao
	//
	@Override
	public RevInvenRequest getInvenRequest(int id) {
		return invenRequestDao.get(id);
	}

	@Override
	public void saveOrUpdate(RevInvenRequest invenRequest) {
		invenRequestDao.saveOrUpdate(invenRequest);
	}

	@Override
	public void deleteInvenRequest(RevInvenRequest invenRequest) {
		invenRequestDao.delete(invenRequest);
	}

	@Override
	public void deleteInvenRequests(List<RevInvenRequest> invenRequests) {
		invenRequestDao.delete(invenRequests);
	}

	@Override
	public DataSourceResult getInvenRequestList(DataSourceRequest request) {
		return invenRequestDao.getList(request);
	}

    
    
	//
	// for RevFbSelCacheDao
	//
	@Override
	public RevFbSelCache getFbSelCache(int id) {
		return fbSelCacheDao.get(id);
	}

	@Override
	public void saveOrUpdate(RevFbSelCache fbSelCache) {
		fbSelCacheDao.saveOrUpdate(fbSelCache);
	}

	@Override
	public void deleteFbSelCache(RevFbSelCache fbSelCache) {
		fbSelCacheDao.delete(fbSelCache);
	}

	@Override
	public void deleteFbSelCaches(List<RevFbSelCache> fbSelCaches) {
		fbSelCacheDao.delete(fbSelCaches);
	}

	@Override
	public Tuple getLastFbSelCacheTupleByScreenId(int screenId) {
		return fbSelCacheDao.getLastTupleByScreenId(screenId);
	}

    
    
	//
	// for RevScrHrlyFailTotDao
	//
	@Override
	public RevScrHrlyFailTot getScrHrlyFailTot(int id) {
		return scrHrlyFailTotDao.get(id);
	}

	@Override
	public void saveOrUpdate(RevScrHrlyFailTot hrlyFailTot) {
		scrHrlyFailTotDao.saveOrUpdate(hrlyFailTot);
	}

	@Override
	public void deleteScrHrlyFailTot(RevScrHrlyFailTot hrlyFailTot) {
		scrHrlyFailTotDao.delete(hrlyFailTot);
	}

	@Override
	public void deleteScrHrlyFailTots(List<RevScrHrlyFailTot> hrlyFailTots) {
		scrHrlyFailTotDao.delete(hrlyFailTots);
	}

	@Override
	public DataSourceResult getScrHrlyFailTotList(DataSourceRequest request, Date playDate) {
		return scrHrlyFailTotDao.getList(request, playDate);
	}

	@Override
	public RevScrHrlyFailTot getScrHrlyFailTot(InvScreen screen, Date playDate) {
		return scrHrlyFailTotDao.get(screen, playDate);
	}

	@Override
	public RevScrHrlyFailTot getScrHrlyFailTot(int screenId, Date playDate) {
		return scrHrlyFailTotDao.get(screenId, playDate);
	}

    
    
	//
	// for RevScrHrlyNoAdTotDao
	//
	@Override
	public RevScrHrlyNoAdTot getScrHrlyNoAdTot(int id) {
		return scrHrlyNoAdTotDao.get(id);
	}

	@Override
	public void saveOrUpdate(RevScrHrlyNoAdTot hrlyNoAdTot) {
		scrHrlyNoAdTotDao.saveOrUpdate(hrlyNoAdTot);
	}

	@Override
	public void deleteScrHrlyNoAdTot(RevScrHrlyNoAdTot hrlyNoAdTot) {
		scrHrlyNoAdTotDao.delete(hrlyNoAdTot);
	}

	@Override
	public void deleteScrHrlyNoAdTots(List<RevScrHrlyNoAdTot> hrlyNoAdTots) {
		scrHrlyNoAdTotDao.delete(hrlyNoAdTots);
	}

	@Override
	public DataSourceResult getScrHrlyNoAdTotList(DataSourceRequest request, Date playDate) {
		return scrHrlyNoAdTotDao.getList(request, playDate);
	}

	@Override
	public RevScrHrlyNoAdTot getScrHrlyNoAdTot(InvScreen screen, Date playDate) {
		return scrHrlyNoAdTotDao.get(screen, playDate);
	}

	@Override
	public RevScrHrlyNoAdTot getScrHrlyNoAdTot(int screenId, Date playDate) {
		return scrHrlyNoAdTotDao.get(screenId, playDate);
	}

	@Override
	public Tuple getScrHrlyNoAdTotStatByMediumIdPlayDate(int mediumId, Date playDate) {
		return scrHrlyNoAdTotDao.getStatByMediumIdPlayDate(mediumId, playDate);
	}

    
    
	//
	// for RevScrHrlyFbTotDao
	//
	@Override
	public RevScrHrlyFbTot getScrHrlyFbTot(int id) {
		return scrHrlyFbTotDao.get(id);
	}

	@Override
	public void saveOrUpdate(RevScrHrlyFbTot hrlyFbTot) {
		scrHrlyFbTotDao.saveOrUpdate(hrlyFbTot);
	}

	@Override
	public void deleteScrHrlyFbTot(RevScrHrlyFbTot hrlyFbTot) {
		scrHrlyFbTotDao.delete(hrlyFbTot);
	}

	@Override
	public void deleteScrHrlyFbTots(List<RevScrHrlyFbTot> hrlyFbTots) {
		scrHrlyFbTotDao.delete(hrlyFbTots);
	}

	@Override
	public DataSourceResult getScrHrlyFbTotList(DataSourceRequest request, Date playDate) {
		return scrHrlyFbTotDao.getList(request, playDate);
	}

	@Override
	public RevScrHrlyFbTot getScrHrlyFbTot(InvScreen screen, Date playDate) {
		return scrHrlyFbTotDao.get(screen, playDate);
	}

	@Override
	public RevScrHrlyFbTot getScrHrlyFbTot(int screenId, Date playDate) {
		return scrHrlyFbTotDao.get(screenId, playDate);
	}

	@Override
	public Tuple getScrHrlyFbTotStatByMediumIdPlayDate(int mediumId, Date playDate) {
		return scrHrlyFbTotDao.getStatByMediumIdPlayDate(mediumId, playDate);
	}

    
    
	//
	// for RevScrHrlyFbTotDao
	//
	@Override
	public RevObjTouch getObjTouch(int id) {
		return objTouchDao.get(id);
	}

	@Override
	public void saveOrUpdate(RevObjTouch objTouch) {
		objTouchDao.saveOrUpdate(objTouch);
	}

	@Override
	public void deleteObjTouch(RevObjTouch objTouch) {
		objTouchDao.delete(objTouch);
	}

	@Override
	public void deleteObjTouches(List<RevObjTouch> objTouches) {
		objTouchDao.delete(objTouches);
	}

	@Override
	public RevObjTouch getObjTouch(String type, int objId) {
		return objTouchDao.get(type, objId);
	}

	@Override
	public List<RevObjTouch> getObjTouchList() {
		return objTouchDao.getList();
	}

    
    
	//
	// for RevAdHourlyPlayDao
	//
	@Override
	public RevAdHourlyPlay getAdHourlyPlay(int id) {
		return adHourlyPlayDao.get(id);
	}

	@Override
	public void saveOrUpdate(RevAdHourlyPlay hourPlay) {
		adHourlyPlayDao.saveOrUpdate(hourPlay);
	}

	@Override
	public void deleteAdHourlyPlay(RevAdHourlyPlay hourPlay) {
		adHourlyPlayDao.delete(hourPlay);
	}

	@Override
	public void deleteAdHourlyPlays(List<RevAdHourlyPlay> hourPlays) {
		adHourlyPlayDao.delete(hourPlays);
	}

	@Override
	public DataSourceResult getAdHourlyPlayList(DataSourceRequest request) {
		return adHourlyPlayDao.getList(request);
	}

	@Override
	public RevAdHourlyPlay getAdHourlyPlay(AdcAd ad, Date playDate) {
		return adHourlyPlayDao.get(ad, playDate);
	}

    
    
	//
	// for RevCreatHourlyPlayDao
	//
	@Override
	public RevCreatHourlyPlay getCreatHourlyPlay(int id) {
		return creatHourlyPlayDao.get(id);
	}

	@Override
	public void saveOrUpdate(RevCreatHourlyPlay hourPlay) {
		creatHourlyPlayDao.saveOrUpdate(hourPlay);
	}

	@Override
	public void deleteCreatHourlyPlay(RevCreatHourlyPlay hourPlay) {
		creatHourlyPlayDao.delete(hourPlay);
	}

	@Override
	public void deleteCreatHourlyPlays(List<RevCreatHourlyPlay> hourPlays) {
		creatHourlyPlayDao.delete(hourPlays);
	}

	@Override
	public DataSourceResult getCreatHourlyPlayList(DataSourceRequest request) {
		return creatHourlyPlayDao.getList(request);
	}

	@Override
	public RevCreatHourlyPlay getCreatHourlyPlay(AdcCreative creative, Date playDate) {
		return creatHourlyPlayDao.get(creative, playDate);
	}


    
	//
	// for Common
	//
	@Override
	public int calcDailyInvenConnectCountByPlayDate(Date playDate) {
		
		if (playDate == null) {
			return -1;
		}
		
		
		int ret1 = 0, ret2 = 0;
		
		
		HashMap<String, RevScrHrlyPlySumItem> prevScrMap = new HashMap<String, RevScrHrlyPlySumItem>();
		HashMap<String, RevScrHrlyPlySumItem> prevSitMap = new HashMap<String, RevScrHrlyPlySumItem>();
		
		HashMap<String, InvScreen> screenMap = new HashMap<String, InvScreen>();
		HashMap<String, InvSite> siteMap = new HashMap<String, InvSite>();
		
		
		//
		//  화면
		//
		
		// 화면 항목을 준비
		List<InvScreen> screenList = invService.getScreenList();
		for(InvScreen screen : screenList) {
			screenMap.put("S" + screen.getId(), screen);
		}
		
		// 기존 생성된 항목을 준비
		List<Tuple> prevScrList = getScrHrlyPlyTotTupleListByPlayDate(playDate);
		for(Tuple tuple : prevScrList) {
			RevScrHrlyPlySumItem item = new RevScrHrlyPlySumItem(tuple, false);
			prevScrMap.put("S" + item.getDestId(), item);
		}
		
		// 현재 값이 반영된 항목 쿼리
		ArrayList<RevScrHrlyPlySumItem> currScrList = new ArrayList<RevScrHrlyPlySumItem>();
		List<Tuple> scrList = getScrSumScrHourlyPlayListByPlayDate(playDate);
		for(Tuple tuple : scrList) {
			currScrList.add(new RevScrHrlyPlySumItem(tuple, true));
		}
		
		
		//
		//  사이트
		//
		
		// 사이트 항목을 준비
		List<InvSite> siteList = invService.getSiteList();
		for(InvSite site : siteList) {
			siteMap.put("T" + site.getId(), site);
		}
		
		// 기존 생성된 항목을 준비
		List<Tuple> prevSitList = getSitHrlyPlyTotTupleListByPlayDate(playDate);
		for(Tuple tuple : prevSitList) {
			RevScrHrlyPlySumItem item = new RevScrHrlyPlySumItem(tuple, false);
			prevSitMap.put("T" + item.getDestId(), item);
		}
		
		// 현재 값이 반영된 항목 쿼리
		ArrayList<RevScrHrlyPlySumItem> currSitList = new ArrayList<RevScrHrlyPlySumItem>();
		List<Tuple> sitList = getSitSumScrHourlyPlayListByPlayDate(playDate);
		for(Tuple tuple : sitList) {
			currSitList.add(new RevScrHrlyPlySumItem(tuple, true));
		}

		
		try {
			
			// 화면
			
			for (RevScrHrlyPlySumItem sumItem : currScrList) {
				boolean updateReq = false;
				
				RevScrHrlyPlySumItem hrlyPlyTot = prevScrMap.get("S" + sumItem.getDestId());
				RevScrHrlyPlyTot row = null;
				if (hrlyPlyTot == null) {
					InvScreen screen = screenMap.get("S" + sumItem.getDestId());
					if (screen != null) {
						row = new RevScrHrlyPlyTot(screen, playDate);
						updateReq = true;
					}
				} else {
					updateReq = !hrlyPlyTot.isSameData(sumItem);
					if (updateReq) {
						InvScreen screen = screenMap.get("S" + sumItem.getDestId());
						if (screen != null) {
							row = getScrHrlyPlyTot(screen, playDate);
							row.touchWho();
						}
						if (row == null) {
							updateReq = false;
						}
					}
				}
				
				if (updateReq) {
					row.setCnt00(sumItem.getCnt00());
					row.setCnt01(sumItem.getCnt01());
					row.setCnt02(sumItem.getCnt02());
					row.setCnt03(sumItem.getCnt03());
					row.setCnt04(sumItem.getCnt04());
					row.setCnt05(sumItem.getCnt05());
					row.setCnt06(sumItem.getCnt06());
					row.setCnt07(sumItem.getCnt07());
					row.setCnt08(sumItem.getCnt08());
					row.setCnt09(sumItem.getCnt09());
					row.setCnt10(sumItem.getCnt10());
					row.setCnt11(sumItem.getCnt11());
					row.setCnt12(sumItem.getCnt12());
					row.setCnt13(sumItem.getCnt13());
					row.setCnt14(sumItem.getCnt14());
					row.setCnt15(sumItem.getCnt15());
					row.setCnt16(sumItem.getCnt16());
					row.setCnt17(sumItem.getCnt17());
					row.setCnt18(sumItem.getCnt18());
					row.setCnt19(sumItem.getCnt19());
					row.setCnt20(sumItem.getCnt20());
					row.setCnt21(sumItem.getCnt21());
					row.setCnt22(sumItem.getCnt22());
					row.setCnt23(sumItem.getCnt23());
					
					row.setAdCount(sumItem.getAdCnt());
					row.setSuccTotal(sumItem.getSuccTot());
					row.setFailTotal(sumItem.getFailTot());
					row.setDateTotal(sumItem.getDateTot());
					
					saveOrUpdate(row);
					ret1 ++;
				}
			}
			
			
			// 사이트
			
			for (RevScrHrlyPlySumItem sumItem : currSitList) {
				boolean updateReq = false;
				
				RevScrHrlyPlySumItem hrlyPlyTot = prevSitMap.get("T" + sumItem.getDestId());
				RevSitHrlyPlyTot row = null;
				if (hrlyPlyTot == null) {
					InvSite site = siteMap.get("T" + sumItem.getDestId());
					if (site != null) {
						row = new RevSitHrlyPlyTot(site, playDate);
						updateReq = true;
					}
				} else {
					updateReq = !hrlyPlyTot.isSameData(sumItem);
					if (updateReq) {
						InvSite site = siteMap.get("T" + sumItem.getDestId());
						if (site != null) {
							row = getSitHrlyPlyTot(site, playDate);
							row.touchWho();
						}
						if (row == null) {
							updateReq = false;
						}
					}
				}
				
				if (updateReq) {
					row.setCnt00(sumItem.getCnt00());
					row.setCnt01(sumItem.getCnt01());
					row.setCnt02(sumItem.getCnt02());
					row.setCnt03(sumItem.getCnt03());
					row.setCnt04(sumItem.getCnt04());
					row.setCnt05(sumItem.getCnt05());
					row.setCnt06(sumItem.getCnt06());
					row.setCnt07(sumItem.getCnt07());
					row.setCnt08(sumItem.getCnt08());
					row.setCnt09(sumItem.getCnt09());
					row.setCnt10(sumItem.getCnt10());
					row.setCnt11(sumItem.getCnt11());
					row.setCnt12(sumItem.getCnt12());
					row.setCnt13(sumItem.getCnt13());
					row.setCnt14(sumItem.getCnt14());
					row.setCnt15(sumItem.getCnt15());
					row.setCnt16(sumItem.getCnt16());
					row.setCnt17(sumItem.getCnt17());
					row.setCnt18(sumItem.getCnt18());
					row.setCnt19(sumItem.getCnt19());
					row.setCnt20(sumItem.getCnt20());
					row.setCnt21(sumItem.getCnt21());
					row.setCnt22(sumItem.getCnt22());
					row.setCnt23(sumItem.getCnt23());
					
					row.setAdCount(sumItem.getAdCnt());
					row.setSuccTotal(sumItem.getSuccTot());
					row.setFailTotal(sumItem.getFailTot());
					row.setDateTotal(sumItem.getDateTot());
					
					saveOrUpdate(row);
					ret2 ++;
				}
			}
		} catch(Exception ex) {
			logger.error("calcDailyInvenConnectCountByPlayDate", ex);
		}

		return ret1 + ret2;
	}

}
