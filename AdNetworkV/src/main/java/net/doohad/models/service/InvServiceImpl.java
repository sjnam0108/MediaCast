package net.doohad.models.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Tuple;
import javax.servlet.http.HttpSession;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.adc.AdcAdTarget;
import net.doohad.models.adc.AdcCreatTarget;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.inv.InvSite;
import net.doohad.models.inv.dao.InvScreenDao;
import net.doohad.models.inv.dao.InvSiteDao;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgPlTarget;
import net.doohad.utils.Util;
import net.doohad.viewmodels.inv.InvSiteMapLocItem;

@Transactional
@Service("invService")
public class InvServiceImpl implements InvService {

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
    private InvSiteDao siteDao;

    @Autowired
    private InvScreenDao screenDao;

    @Autowired
    private AdcService adcService;

    @Autowired
    private OrgService orgService;

    
    
	//
	// for InvSiteDao
	//
	@Override
	public InvSite getSite(int id) {
		return siteDao.get(id);
	}

	@Override
	public void saveOrUpdate(InvSite site) {
		siteDao.saveOrUpdate(site);
	}

	@Override
	public void deleteSite(InvSite site) {
		siteDao.delete(site);
	}

	@Override
	public void deleteSites(List<InvSite> sites) {
		siteDao.delete(sites);
	}

	@Override
	public DataSourceResult getSiteList(DataSourceRequest request) {
		return siteDao.getList(request);
	}

	@Override
	public InvSite getSite(KnlMedium medium, String shortName) {
		return siteDao.get(medium, shortName);
	}

	@Override
	public List<InvSite> getSiteListByMediumIdNameLike(int mediumId, String name) {
		return siteDao.getListByMediumIdNameLike(mediumId, name);
	}

	@Override
	public List<InvSite> getSiteListByMediumIdShortNameLike(int mediumId, String shortName) {
		return siteDao.getListByMediumIdShortNameLike(mediumId, shortName);
	}

	@Override
	public List<InvSite> getSiteList() {
		return siteDao.getList();
	}

	@Override
	public List<Tuple> getSiteCountGroupByMediumSiteCondId(int mediumId) {
		return siteDao.getCountGroupByMediumSiteCondId(mediumId);
	}

	@Override
	public List<InvSite> getMonitSiteList() {
		return siteDao.getMonitList();
	}

	@Override
	public List<InvSite> getMonitSiteListByMediumId(int mediumId) {
		return siteDao.getMonitListByMediumId(mediumId);
	}

	@Override
	public List<InvSite> getMonitSiteListByMediumNameLike(int mediumId, String name) {
		return siteDao.getMonitListByMediumNameLike(mediumId, name);
	}

	@Override
	public List<Tuple> getSiteLocListBySiteIdIn(List<Integer> list) {
		return siteDao.getLocListBySiteIdIn(list);
	}

	@Override
	public List<Tuple> getSiteLocListByVenueType(String venueType) {
		return siteDao.getLocListByVenueType(venueType);
	}

	@Override
	public int getMonitSiteCountByMediumRegionCodeIn(int mediumId, List<String> list) {
		return siteDao.getMonitCountByMediumRegionCodeIn(mediumId, list);
	}

	@Override
	public int getMonitSiteCountByMediumStateCodeIn(int mediumId, List<String> list) {
		return siteDao.getMonitCountByMediumStateCodeIn(mediumId, list);
	}

	@Override
	public int getMonitSiteCountByMediumScreenIdIn(int mediumId, List<Integer> list) {
		return siteDao.getMonitCountByMediumScreenIdIn(mediumId, list);
	}

	@Override
	public int getMonitSiteCountByMediumSiteIdIn(int mediumId, List<Integer> list) {
		return siteDao.getMonitCountByMediumSiteIdIn(mediumId, list);
	}

	@Override
	public int getMonitSiteCountByMediumSiteCondCodeIn(int mediumId, List<String> list) {
		return siteDao.getMonitCountByMediumSiteCondCodeIn(mediumId, list);
	}

	@Override
	public List<Integer> getMonitSiteIdsByMediumRegionCodeIn(int mediumId, List<String> list) {
		return siteDao.getMonitIdsByMediumRegionCodeIn(mediumId, list);
	}

	@Override
	public List<Integer> getMonitSiteIdsByMediumStateCodeIn(int mediumId, List<String> list) {
		return siteDao.getMonitIdsByMediumStateCodeIn(mediumId, list);
	}

	@Override
	public List<Integer> getMonitSiteIdsByMediumScreenIdIn(int mediumId, List<Integer> list) {
		return siteDao.getMonitIdsByMediumScreenIdIn(mediumId, list);
	}

	@Override
	public List<Integer> getMonitSiteIdsByMediumSiteIdIn(int mediumId, List<Integer> list) {
		return siteDao.getMonitIdsByMediumSiteIdIn(mediumId, list);
	}

	@Override
	public List<Integer> getMonitSiteIdsByMediumSiteCondCodeIn(int mediumId, List<String> list) {
		return siteDao.getMonitIdsByMediumSiteCondCodeIn(mediumId, list);
	}

	
    
	//
	// for InvScreenDao
	//
	@Override
	public InvScreen getScreen(int id) {
		return screenDao.get(id);
	}

	@Override
	public void saveOrUpdate(InvScreen screen) {
		screenDao.saveOrUpdate(screen);
	}

	@Override
	public void deleteScreen(InvScreen screen) {
		screenDao.delete(screen);
	}

	@Override
	public void deleteScreens(List<InvScreen> screens) {
		screenDao.delete(screens);
	}

	@Override
	public DataSourceResult getScreenList(DataSourceRequest request) {
		return screenDao.getList(request);
	}

	@Override
	public DataSourceResult getMonitScreenList(DataSourceRequest request) {
		return screenDao.getMonitList(request);
	}

	@Override
	public List<Integer> getMonitScreenIdsByMediumId(int mediumId) {
		return screenDao.getMonitIdsByMediumId(mediumId);
	}

	@Override
	public List<Tuple> getScreenIdResoListByScreenIdIn(List<Integer> list) {
		return screenDao.getIdResoListByScreenIdIn(list);
	}

	@Override
	public List<InvScreen> getScreenList() {
		return screenDao.getList();
	}

	@Override
	public void updateScreenLastAdReportDate(int id, Date lastAdReportDate) {
		screenDao.updateLastAdReportDate(id, lastAdReportDate);
	}

	@Override
	public void updateScreenLastAdRequestDate(int id, Date lastAdRequestDate) {
		screenDao.updateLastAdRequestDate(id, lastAdRequestDate);
	}

	@Override
	public InvScreen getScreen(KnlMedium medium, String shortName) {
		return screenDao.get(medium, shortName);
	}

	@Override
	public List<Tuple> getScreenCountGroupByMediumSiteCondId(int mediumId) {
		return screenDao.getCountGroupByMediumSiteCondId(mediumId);
	}

	@Override
	public List<InvScreen> getScreenListBySiteId(int siteId) {
		return screenDao.getListBySiteId(siteId);
	}

	@Override
	public List<InvScreen> getMonitScreenListByMediumId(int mediumId) {
		return screenDao.getMonitListByMediumId(mediumId);
	}

	@Override
	public List<InvScreen> getMonitScreenList() {
		return screenDao.getMonitList();
	}

	@Override
	public List<Tuple> getScreenCountGroupByMediumResolution(int mediumId) {
		return screenDao.getCountGroupByMediumResolution(mediumId);
	}

	@Override
	public List<InvScreen> getMonitScreenListByMediumNameLike(int mediumId, String name) {
		return screenDao.getMonitListByMediumNameLike(mediumId, name);
	}

	@Override
	public InvScreen getMonitScreen(int id) {
		return screenDao.getMonit(id);
	}

	@Override
	public int getMonitScreenCountByMediumRegionCodeIn(int mediumId, List<String> list) {
		return screenDao.getMonitCountByMediumRegionCodeIn(mediumId, list);
	}

	@Override
	public int getMonitScreenCountByMediumStateCodeIn(int mediumId, List<String> list) {
		return screenDao.getMonitCountByMediumStateCodeIn(mediumId, list);
	}

	@Override
	public int getMonitScreenCountByMediumScreenIdIn(int mediumId, List<Integer> list) {
		return screenDao.getMonitCountByMediumScreenIdIn(mediumId, list);
	}

	@Override
	public int getMonitScreenCountByMediumSiteIdIn(int mediumId, List<Integer> list) {
		return screenDao.getMonitCountByMediumSiteIdIn(mediumId, list);
	}

	@Override
	public int getMonitScreenCountByMediumSiteCondCodeIn(int mediumId, List<String> list) {
		return screenDao.getMonitCountByMediumSiteCondCodeIn(mediumId, list);
	}

	@Override
	public List<Integer> getMonitScreenIdsByMediumRegionCodeIn(int mediumId, List<String> list) {
		return screenDao.getMonitIdsByMediumRegionCodeIn(mediumId, list);
	}

	@Override
	public List<Integer> getMonitScreenIdsByMediumStateCodeIn(int mediumId, List<String> list) {
		return screenDao.getMonitIdsByMediumStateCodeIn(mediumId, list);
	}

	@Override
	public List<Integer> getMonitScreenIdsByMediumScreenIdIn(int mediumId, List<Integer> list) {
		return screenDao.getMonitIdsByMediumScreenIdIn(mediumId, list);
	}

	@Override
	public List<Integer> getMonitScreenIdsByMediumSiteIdIn(int mediumId, List<Integer> list) {
		return screenDao.getMonitIdsByMediumSiteIdIn(mediumId, list);
	}

	@Override
	public List<Integer> getMonitScreenIdsByMediumSiteCondCodeIn(int mediumId, List<String> list) {
		return screenDao.getMonitIdsByMediumSiteCondCodeIn(mediumId, list);
	}

    
    
	//
	// for Common
	//
	@Override
	public boolean updateSiteActiveStatusCountBasedScreens(int siteId) {
		
		InvSite site = getSite(siteId);
		if (site == null) {
			return false;
		}
		
		boolean value = false;
		List<InvScreen> screenList = getScreenListBySiteId(siteId);
		if (screenList.size() > 0) {
			for(InvScreen screen : screenList) {
				if (screen.isActiveStatus()) {
					value = true;
					break;
				}
			}
		}

		site.setActiveStatus(value);
		site.setScreenCount(screenList.size());
		
		siteDao.saveOrUpdate(site);
		
		return true;
	}

	@Override
	public void deleteSoftScreen(InvScreen screen, HttpSession session) {
		
		if (screen != null) {
			screen.setDeleted(true);
			screen.setShortName(screen.getShortName() + Util.toSimpleString(new Date(), "_yyyyMMdd_HHmm"));
			screen.setName(screen.getName() + Util.toSimpleString(new Date(), "_yyyyMMdd_HHmm"));
			screen.touchWho(session);
			
			screenDao.saveOrUpdate(screen);
		}
	}

	@Override
	public void deleteSoftSite(InvSite site, HttpSession session) {
		
		if (site != null) {
			
			site.setDeleted(true);
			site.setShortName(site.getShortName() + Util.toSimpleString(new Date(), "_yyyyMMdd_HHmm"));
			site.setName(site.getName() + Util.toSimpleString(new Date(), "_yyyyMMdd_HHmm"));
			site.touchWho(session);
			
			siteDao.saveOrUpdate(site);

			
			List<InvScreen> screens = new ArrayList<InvScreen>();
			List<InvScreen> screenList = getScreenListBySiteId(site.getId());
			
			for(InvScreen screen : screenList) {
				if (screen.getLastAdRequestDate() == null) {
					screens.add(screen);
				} else {
					deleteSoftScreen(screen, session);
				}
			}
			
			deleteScreens(screens);
		}
	}

	@Override
	public List<String> getAvailScreenResolutionListByMediumId(int mediumId) {
		
		ArrayList<String> retList = new ArrayList<String>();
		List<Tuple> countList = getScreenCountGroupByMediumResolution(mediumId);
		for(Tuple tuple : countList) {
			retList.add((String) tuple.get(0));
		}

		return retList;
	}

	@Override
	public int getTargetScreenCountByCreativeId(int creativeId) {
		
		return getTargetScreenIdsByCreativeId(creativeId).size();
	}

	@Override
	public List<Integer> getTargetScreenIdsByCreativeId(int creativeId) {
		
		List<AdcCreatTarget> targets = adcService.getCreatTargetListByCreativeId(creativeId);
		
		List<Integer> currIds = new ArrayList<Integer>();
		List<Integer> resultIds = null;
		
		for(AdcCreatTarget creatTarget : targets) {
			if (creatTarget.getInvenType().equals("RG")) {
				currIds = getMonitScreenIdsByMediumRegionCodeIn(creatTarget.getMedium().getId(), 
		    			Util.getStringList(creatTarget.getTgtValue()));
			} else if (creatTarget.getInvenType().equals("CT")) {
				currIds = getMonitScreenIdsByMediumStateCodeIn(creatTarget.getMedium().getId(), 
						Util.getStringList(creatTarget.getTgtValue()));
			} else if (creatTarget.getInvenType().equals("ST")) {
				currIds = getMonitScreenIdsByMediumSiteIdIn(creatTarget.getMedium().getId(), 
						Util.getIntegerList(creatTarget.getTgtValue()));
			} else if (creatTarget.getInvenType().equals("SC")) {
				currIds = getMonitScreenIdsByMediumScreenIdIn(creatTarget.getMedium().getId(), 
						Util.getIntegerList(creatTarget.getTgtValue()));
			} else if (creatTarget.getInvenType().equals("CD")) {
				currIds = getMonitScreenIdsByMediumSiteCondCodeIn(creatTarget.getMedium().getId(), 
						Util.getStringList(creatTarget.getTgtValue()));
			}
			if (creatTarget.getTgtScrCount() != currIds.size()) {
				creatTarget.setTgtScrCount(currIds.size());
				adcService.saveOrUpdate(creatTarget);
			}
			
			if (resultIds == null) {
				resultIds = currIds;
			} else {
				if (creatTarget.getFilterType().equals("A")) {
					resultIds = Util.intersection(resultIds, currIds);
				} else {
					resultIds = Util.union(resultIds, currIds);
				}
			}
		}
		
		return (resultIds == null ? new ArrayList<Integer>() : resultIds);
	}

	@Override
	public int getTargetScreenCountByPlaylistId(int playlistId) {
		
		return getTargetScreenIdsByPlaylistId(playlistId).size();
	}

	@Override
	public List<Integer> getTargetScreenIdsByPlaylistId(int playlistId) {
		
		List<OrgPlTarget> targets = orgService.getPlTargetListByPlaylistId(playlistId);
		
		List<Integer> currIds = new ArrayList<Integer>();
		List<Integer> resultIds = null;
		
		for(OrgPlTarget plTarget : targets) {
			if (plTarget.getInvenType().equals("RG")) {
				currIds = getMonitScreenIdsByMediumRegionCodeIn(plTarget.getMedium().getId(), 
		    			Util.getStringList(plTarget.getTgtValue()));
			} else if (plTarget.getInvenType().equals("CT")) {
				currIds = getMonitScreenIdsByMediumStateCodeIn(plTarget.getMedium().getId(), 
						Util.getStringList(plTarget.getTgtValue()));
			} else if (plTarget.getInvenType().equals("ST")) {
				currIds = getMonitScreenIdsByMediumSiteIdIn(plTarget.getMedium().getId(), 
						Util.getIntegerList(plTarget.getTgtValue()));
			} else if (plTarget.getInvenType().equals("SC")) {
				currIds = getMonitScreenIdsByMediumScreenIdIn(plTarget.getMedium().getId(), 
						Util.getIntegerList(plTarget.getTgtValue()));
			}
			if (plTarget.getTgtScrCount() != currIds.size()) {
				plTarget.setTgtScrCount(currIds.size());
				orgService.saveOrUpdate(plTarget);
			}
			
			if (resultIds == null) {
				resultIds = currIds;
			} else {
				if (plTarget.getFilterType().equals("A")) {
					resultIds = Util.intersection(resultIds, currIds);
				} else {
					resultIds = Util.union(resultIds, currIds);
				}
			}
		}
		
		return (resultIds == null ? new ArrayList<Integer>() : resultIds);
	}

	@Override
	public List<Integer> getTargetScreenOrAllIdsByPlaylistId(int playlistId, int mediumId) {
		
		List<OrgPlTarget> targets = orgService.getPlTargetListByPlaylistId(playlistId);
		
		List<Integer> currIds = new ArrayList<Integer>();
		List<Integer> resultIds = null;
		
		for(OrgPlTarget plTarget : targets) {
			if (plTarget.getInvenType().equals("RG")) {
				currIds = getMonitScreenIdsByMediumRegionCodeIn(plTarget.getMedium().getId(), 
		    			Util.getStringList(plTarget.getTgtValue()));
			} else if (plTarget.getInvenType().equals("CT")) {
				currIds = getMonitScreenIdsByMediumStateCodeIn(plTarget.getMedium().getId(), 
						Util.getStringList(plTarget.getTgtValue()));
			} else if (plTarget.getInvenType().equals("ST")) {
				currIds = getMonitScreenIdsByMediumSiteIdIn(plTarget.getMedium().getId(), 
						Util.getIntegerList(plTarget.getTgtValue()));
			} else if (plTarget.getInvenType().equals("SC")) {
				currIds = getMonitScreenIdsByMediumScreenIdIn(plTarget.getMedium().getId(), 
						Util.getIntegerList(plTarget.getTgtValue()));
			}
			if (plTarget.getTgtScrCount() != currIds.size()) {
				plTarget.setTgtScrCount(currIds.size());
				orgService.saveOrUpdate(plTarget);
			}
			
			if (resultIds == null) {
				resultIds = currIds;
			} else {
				if (plTarget.getFilterType().equals("A")) {
					resultIds = Util.intersection(resultIds, currIds);
				} else {
					resultIds = Util.union(resultIds, currIds);
				}
			}
		}
		
		if (resultIds == null) {
			return getMonitScreenIdsByMediumId(mediumId);
		} else {
			return resultIds;
		}
	}

	@Override
	public HashMap<String, List<Integer>> getResoScreenIdMapByScreenIdIn(List<Integer> list) {
		
		HashMap<String, List<Integer>> map = new HashMap<String, List<Integer>>();
		
		List<Tuple> tuples = getScreenIdResoListByScreenIdIn(list);
		for(Tuple tuple : tuples) {
			Integer id = (Integer) tuple.get(0);
			String reso = (String) tuple.get(1);
			
			if (map.containsKey(reso)) {
				List<Integer> ids = map.get(reso);
				ids.add(id);
			} else {
				ArrayList<Integer> ids = new ArrayList<Integer>();
				ids.add(id);
				map.put(reso, ids);
			}
		}
		
		return map;
	}

	@Override
	public int getTargetScreenCountByAdId(int adId) {
		
		return getTargetScreenIdsByAdId(adId).size();
	}

	@Override
	public List<Integer> getTargetScreenIdsByAdId(int adId) {
		
		List<AdcAdTarget> targets = adcService.getAdTargetListByAdId(adId);
		
		List<Integer> currIds = new ArrayList<Integer>();
		List<Integer> resultIds = null;
		
		for(AdcAdTarget adTarget : targets) {
			if (adTarget.getInvenType().equals("RG")) {
				currIds = getMonitScreenIdsByMediumRegionCodeIn(adTarget.getMedium().getId(), 
		    			Util.getStringList(adTarget.getTgtValue()));
			} else if (adTarget.getInvenType().equals("CT")) {
				currIds = getMonitScreenIdsByMediumStateCodeIn(adTarget.getMedium().getId(), 
						Util.getStringList(adTarget.getTgtValue()));
			} else if (adTarget.getInvenType().equals("ST")) {
				currIds = getMonitScreenIdsByMediumSiteIdIn(adTarget.getMedium().getId(), 
						Util.getIntegerList(adTarget.getTgtValue()));
			} else if (adTarget.getInvenType().equals("SC")) {
				currIds = getMonitScreenIdsByMediumScreenIdIn(adTarget.getMedium().getId(), 
						Util.getIntegerList(adTarget.getTgtValue()));
			} else if (adTarget.getInvenType().equals("CD")) {
				currIds = getMonitScreenIdsByMediumSiteCondCodeIn(adTarget.getMedium().getId(), 
						Util.getStringList(adTarget.getTgtValue()));
			}
			if (adTarget.getTgtScrCount() != currIds.size()) {
				adTarget.setTgtScrCount(currIds.size());
				adcService.saveOrUpdate(adTarget);
			}
			
			if (resultIds == null) {
				resultIds = currIds;
			} else {
				if (adTarget.getFilterType().equals("A")) {
					resultIds = Util.intersection(resultIds, currIds);
				} else {
					resultIds = Util.union(resultIds, currIds);
				}
			}
		}
		
		return (resultIds == null ? new ArrayList<Integer>() : resultIds);
	}

	@Override
	public List<InvSiteMapLocItem> getCloseSitesBy(InvSite mySite, int count) {
		
		return getCloseSitesBy(mySite, count, false);
	}

	@Override
	public List<InvSiteMapLocItem> getCloseSitesBy(InvSite mySite, int count, boolean includeMyself) {
		
		if (mySite == null) {
			return null;
		}
		
		InvSiteMapLocItem mapItem = new InvSiteMapLocItem(mySite);
		return getCloseSitesBy(mySite.getMedium().getId(), mapItem.getLat(), mapItem.getLng(), count, includeMyself);
	}

	@Override
	public List<InvSiteMapLocItem> getCloseSitesBy(int mediumId, double lat, double lng, int count) {
		
		return getCloseSitesBy(mediumId, lat, lng, count, false);
	}

	@Override
	public List<InvSiteMapLocItem> getCloseSitesBy(int mediumId, double lat, double lng, int count, boolean includeMyself) {
		
		ArrayList<InvSiteMapLocItem> list = new ArrayList<InvSiteMapLocItem>();
    	
    	List<InvSite> siteList = getMonitSiteListByMediumId(mediumId);
    	for(InvSite site : siteList) {
    		InvSiteMapLocItem mapItem = new InvSiteMapLocItem(site);
    		mapItem.setDistance(Util.distance(lat, lng, mapItem.getLat(), mapItem.getLng()));
    		list.add(mapItem);
    	}
    	
		Collections.sort(list, new Comparator<InvSiteMapLocItem>() {
	    	public int compare(InvSiteMapLocItem item1, InvSiteMapLocItem item2) {
	    		return Double.compare(item1.getDistance(),  item2.getDistance());
	    	}
	    });
		
		ArrayList<InvSiteMapLocItem> retList = new ArrayList<InvSiteMapLocItem>();
		int idx = 0;
		for(InvSiteMapLocItem item : list) {
			
			if (!includeMyself && item.getDistance() == 0) {
				continue;
			}
			
			if (idx < count) {
				retList.add(item);
			}
			
			idx ++;
		}
		
		return retList;
	}

}
