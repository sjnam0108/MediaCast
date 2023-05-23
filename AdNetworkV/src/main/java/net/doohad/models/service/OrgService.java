package net.doohad.models.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgAdvertiser;
import net.doohad.models.org.OrgMediumOpt;
import net.doohad.models.org.OrgPlTarget;
import net.doohad.models.org.OrgPlaylist;
import net.doohad.models.org.OrgSiteCond;

@Transactional
public interface OrgService {
	
	// Common
	public void flush();

	
	//
	// for OrgAdvertiser
	//
	// Common
	public OrgAdvertiser getAdvertiser(int id);
	public void saveOrUpdate(OrgAdvertiser advertiser);
	public void deleteAdvertiser(OrgAdvertiser advertiser);
	public void deleteAdvertisers(List<OrgAdvertiser> advertisers);

	// for Kendo Grid Remote Read
	public DataSourceResult getAdvertiserList(DataSourceRequest request);

	// for DAO specific
	public OrgAdvertiser getAdvertiser(KnlMedium medium, String name);
	public List<OrgAdvertiser> getAdvertiserListByMediumId(int mediumId);

	
	//
	// for OrgSiteCond
	//
	// Common
	public OrgSiteCond getSiteCond(int id);
	public void saveOrUpdate(OrgSiteCond siteCond);
	public void deleteSiteCond(OrgSiteCond siteCond);
	public void deleteSiteConds(List<OrgSiteCond> siteConds);

	// for Kendo Grid Remote Read
	public DataSourceResult getSiteCondList(DataSourceRequest request);

	// for DAO specific
	public OrgSiteCond getSiteCond(KnlMedium medium, String code);
	public List<OrgSiteCond> getSiteCondListByMediumId(int mediumId);
	public List<OrgSiteCond> getSiteCondListByMediumIdActiveStatus(
			int mediumId, boolean activeStatus);
	public List<OrgSiteCond> getSiteCondListByMediumIdNameLike(int mediumId, String name);

	
	//
	// for OrgMediumOpt
	//
	// Common
	public OrgMediumOpt getMediumOpt(int id);
	public void saveOrUpdate(OrgMediumOpt mediumOpt);
	public void deleteMediumOpt(OrgMediumOpt mediumOpt);
	public void deleteMediumOpts(List<OrgMediumOpt> mediumOpts);

	// for Kendo Grid Remote Read

	// for DAO specific
	public OrgMediumOpt getMediumOpt(KnlMedium medium, String code);
	public List<OrgMediumOpt> getMediumOptListByMediumId(int mediumId);
	public String getMediumOptValue(int mediumId, String code);

	
	//
	// for OrgPlaylist
	//
	// Common
	public OrgPlaylist getPlaylist(int id);
	public void saveOrUpdate(OrgPlaylist playlist);
	public void deletePlaylist(OrgPlaylist playlist);
	public void deletePlaylists(List<OrgPlaylist> playlists);

	// for Kendo Grid Remote Read
	public DataSourceResult getPlaylistList(DataSourceRequest request);

	// for DAO specific
	public OrgPlaylist getPlaylist(KnlMedium medium, String name);
	public List<OrgPlaylist> getPlaylistListByMediumId(int mediumId);
	public void saveAndReorderPlaylist(OrgPlaylist playlist);
	public List<OrgPlaylist> getEffPlaylistListByMediumId(int mediumId);
	public List<OrgPlaylist> getEffPlaylistListByMediumIdDate(
			int mediumId, Date sDate, Date eDate);

	
	//
	// for OrgPlTarget
	//
	// Common
	public OrgPlTarget getPlTarget(int id);
	public void saveOrUpdate(OrgPlTarget plTarget);
	public void deletePlTarget(OrgPlTarget plTarget);
	public void deletePlTargets(List<OrgPlTarget> plTargets);

	// for Kendo Grid Remote Read
	public DataSourceResult getPlTargetList(DataSourceRequest request);

	// for DAO specific
	public List<OrgPlTarget> getPlTargetListByPlaylistId(int playlistId);
	public void saveAndReorderPlTarget(OrgPlTarget plTarget);

	
	//
	// for Common
	//
	public OrgPlaylist getPlaylistByScreenId(int screenId, int mediumId);
	public HashMap<String, Integer> getPlaylistScreenCountByMediumId(int mediumId);
}
