package net.doohad.models.inv.dao;

import java.util.List;

import javax.persistence.Tuple;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.inv.InvSite;
import net.doohad.models.knl.KnlMedium;

public interface InvSiteDao {
	// Common
	public InvSite get(int id);
	public void saveOrUpdate(InvSite site);
	public void delete(InvSite site);
	public void delete(List<InvSite> sites);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public InvSite get(KnlMedium medium, String shortName);
	public List<InvSite> getListByMediumIdNameLike(int mediumId, String name);
	public List<InvSite> getListByMediumIdShortNameLike(int mediumId, String shortName);
	public List<InvSite> getList();
	public List<Tuple> getCountGroupByMediumSiteCondId(int mediumId);
	public List<InvSite> getMonitList();
	public List<InvSite> getMonitListByMediumId(int mediumId);
	public List<InvSite> getMonitListByMediumNameLike(int mediumId, String name);
	public List<Tuple> getLocListBySiteIdIn(List<Integer> list);
	public List<Tuple> getLocListByVenueType(String venueType);
	
	public int getMonitCountByMediumRegionCodeIn(int mediumId, List<String> list);
	public int getMonitCountByMediumStateCodeIn(int mediumId, List<String> list);
	public int getMonitCountByMediumScreenIdIn(int mediumId, List<Integer> list);
	public int getMonitCountByMediumSiteIdIn(int mediumId, List<Integer> list);
	public int getMonitCountByMediumSiteCondCodeIn(int mediumId, List<String> list);
	
	public List<Integer> getMonitIdsByMediumRegionCodeIn(int mediumId, List<String> list);
	public List<Integer> getMonitIdsByMediumStateCodeIn(int mediumId, List<String> list);
	public List<Integer> getMonitIdsByMediumScreenIdIn(int mediumId, List<Integer> list);
	public List<Integer> getMonitIdsByMediumSiteIdIn(int mediumId, List<Integer> list);
	public List<Integer> getMonitIdsByMediumSiteCondCodeIn(int mediumId, List<String> list);
}
