package net.doohad.models.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Tuple;
import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.inv.InvSite;
import net.doohad.models.knl.KnlMedium;
import net.doohad.viewmodels.inv.InvSiteMapLocItem;

@Transactional
public interface InvService {
	
	// Common
	public void flush();

	
	//
	// for InvSite
	//
	// Common
	public InvSite getSite(int id);
	public void saveOrUpdate(InvSite site);
	public void deleteSite(InvSite site);
	public void deleteSites(List<InvSite> sites);

	// for Kendo Grid Remote Read
	public DataSourceResult getSiteList(DataSourceRequest request);

	// for DAO specific
	public InvSite getSite(KnlMedium medium, String shortName);
	public List<InvSite> getSiteListByMediumIdNameLike(int mediumId, String name);
	public List<InvSite> getSiteListByMediumIdShortNameLike(int mediumId, String shortName);
	public List<InvSite> getSiteList();
	public List<Tuple> getSiteCountGroupByMediumSiteCondId(int mediumId);
	public List<InvSite> getMonitSiteList();
	public List<InvSite> getMonitSiteListByMediumId(int mediumId);
	public List<InvSite> getMonitSiteListByMediumNameLike(int mediumId, String name);
	public List<Tuple> getSiteLocListBySiteIdIn(List<Integer> list);
	public List<Tuple> getSiteLocListByVenueType(String venueType);
	
	public int getMonitSiteCountByMediumRegionCodeIn(int mediumId, List<String> list);
	public int getMonitSiteCountByMediumStateCodeIn(int mediumId, List<String> list);
	public int getMonitSiteCountByMediumScreenIdIn(int mediumId, List<Integer> list);
	public int getMonitSiteCountByMediumSiteIdIn(int mediumId, List<Integer> list);
	public int getMonitSiteCountByMediumSiteCondCodeIn(int mediumId, List<String> list);
	
	public List<Integer> getMonitSiteIdsByMediumRegionCodeIn(int mediumId, List<String> list);
	public List<Integer> getMonitSiteIdsByMediumStateCodeIn(int mediumId, List<String> list);
	public List<Integer> getMonitSiteIdsByMediumScreenIdIn(int mediumId, List<Integer> list);
	public List<Integer> getMonitSiteIdsByMediumSiteIdIn(int mediumId, List<Integer> list);
	public List<Integer> getMonitSiteIdsByMediumSiteCondCodeIn(int mediumId, List<String> list);
	
	
	//
	// for InvScreen
	//
	// Common
	public InvScreen getScreen(int id);
	public void saveOrUpdate(InvScreen screen);
	public void deleteScreen(InvScreen screen);
	public void deleteScreens(List<InvScreen> screens);

	// for Kendo Grid Remote Read
	public DataSourceResult getScreenList(DataSourceRequest request);
	public DataSourceResult getMonitScreenList(DataSourceRequest request);

	// for DAO specific
	public InvScreen getScreen(KnlMedium medium, String shortName);
	public List<InvScreen> getScreenListBySiteId(int siteId);
	public List<Tuple> getScreenCountGroupByMediumSiteCondId(int mediumId);
	public List<InvScreen> getMonitScreenListByMediumId(int mediumId);
	public List<InvScreen> getMonitScreenList();
	public List<Tuple> getScreenCountGroupByMediumResolution(int mediumId);
	public List<InvScreen> getMonitScreenListByMediumNameLike(int mediumId, String name);
	public InvScreen getMonitScreen(int id);
	public List<Integer> getMonitScreenIdsByMediumId(int mediumId);
	public List<Tuple> getScreenIdResoListByScreenIdIn(List<Integer> list);
	public List<InvScreen> getScreenList();
	public void updateScreenLastAdReportDate(int id, Date lastAdReportDate);
	public void updateScreenLastAdRequestDate(int id, Date lastAdRequestDate);
	
	public int getMonitScreenCountByMediumRegionCodeIn(int mediumId, List<String> list);
	public int getMonitScreenCountByMediumStateCodeIn(int mediumId, List<String> list);
	public int getMonitScreenCountByMediumScreenIdIn(int mediumId, List<Integer> list);
	public int getMonitScreenCountByMediumSiteIdIn(int mediumId, List<Integer> list);
	public int getMonitScreenCountByMediumSiteCondCodeIn(int mediumId, List<String> list);
	
	public List<Integer> getMonitScreenIdsByMediumRegionCodeIn(int mediumId, List<String> list);
	public List<Integer> getMonitScreenIdsByMediumStateCodeIn(int mediumId, List<String> list);
	public List<Integer> getMonitScreenIdsByMediumScreenIdIn(int mediumId, List<Integer> list);
	public List<Integer> getMonitScreenIdsByMediumSiteIdIn(int mediumId, List<Integer> list);
	public List<Integer> getMonitScreenIdsByMediumSiteCondCodeIn(int mediumId, List<String> list);

	
	//
	// for Common
	//
	public boolean updateSiteActiveStatusCountBasedScreens(int siteId);
	public void deleteSoftScreen(InvScreen screen, HttpSession session);
	public void deleteSoftSite(InvSite site, HttpSession session);
	public List<String> getAvailScreenResolutionListByMediumId(int mediumId);
	public int getTargetScreenCountByCreativeId(int creativeId);
	public List<Integer> getTargetScreenIdsByCreativeId(int creativeId);
	public int getTargetScreenCountByPlaylistId(int playlistId);
	public List<Integer> getTargetScreenIdsByPlaylistId(int playlistId);
	public List<Integer> getTargetScreenOrAllIdsByPlaylistId(int playlistId, int mediumId);
	public HashMap<String, List<Integer>> getResoScreenIdMapByScreenIdIn(List<Integer> list);
	public int getTargetScreenCountByAdId(int adId);
	public List<Integer> getTargetScreenIdsByAdId(int adId);
	public List<InvSiteMapLocItem> getCloseSitesBy(InvSite mySite, int count);
	public List<InvSiteMapLocItem> getCloseSitesBy(InvSite mySite, int count, boolean includeMyself);
	public List<InvSiteMapLocItem> getCloseSitesBy(int mediumId, double lat, double lng, int count);
	public List<InvSiteMapLocItem> getCloseSitesBy(int mediumId, double lat, double lng, int count, boolean includeMyself);

}
