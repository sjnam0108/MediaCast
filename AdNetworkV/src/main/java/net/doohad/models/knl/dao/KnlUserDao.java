package net.doohad.models.knl.dao;

import java.util.List;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.knl.KnlUser;

public interface KnlUserDao {
	// Common
	public KnlUser get(int id);
	public void saveOrUpdate(KnlUser user);
	public void delete(KnlUser user);
	public void delete(List<KnlUser> users);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public KnlUser get(String shortName);
	public List<KnlUser> getListByMediumId(int mediumId);
	public List<KnlUser> getM1List();
}
