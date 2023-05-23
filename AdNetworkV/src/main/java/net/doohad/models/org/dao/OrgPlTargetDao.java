package net.doohad.models.org.dao;

import java.util.List;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.org.OrgPlTarget;

public interface OrgPlTargetDao {
	// Common
	public OrgPlTarget get(int id);
	public void saveOrUpdate(OrgPlTarget plTarget);
	public void delete(OrgPlTarget plTarget);
	public void delete(List<OrgPlTarget> plTargets);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public List<OrgPlTarget> getListByPlaylistId(int playlistId);
	public void saveAndReorder(OrgPlTarget plTarget);
}
