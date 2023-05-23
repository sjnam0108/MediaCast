package net.doohad.models.org.dao;

import java.util.Date;
import java.util.List;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgPlaylist;

public interface OrgPlaylistDao {
	// Common
	public OrgPlaylist get(int id);
	public void saveOrUpdate(OrgPlaylist playlist);
	public void delete(OrgPlaylist playlist);
	public void delete(List<OrgPlaylist> playlists);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public OrgPlaylist get(KnlMedium medium, String name);
	public List<OrgPlaylist> getListByMediumId(int mediumId);
	public void saveAndReorder(OrgPlaylist playlist);
	public List<OrgPlaylist> getEffListByMediumId(int mediumId);
	public List<OrgPlaylist> getEffListByMediumIdDate(int mediumId, Date sDate, Date eDate);
}
