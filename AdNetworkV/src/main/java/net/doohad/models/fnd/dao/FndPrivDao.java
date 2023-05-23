package net.doohad.models.fnd.dao;

import java.util.List;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.fnd.FndPriv;

public interface FndPrivDao {
	// Common
	public FndPriv get(int id);
	public void saveOrUpdate(FndPriv priv);
	public void delete(FndPriv priv);
	public void delete(List<FndPriv> privs);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public FndPriv get(String ukid);
	public FndPriv get(org.hibernate.Session hnSession, String ukid);
}
