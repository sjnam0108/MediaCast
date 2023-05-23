package net.doohad.models.rev.dao;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.rev.RevCreatDecn;

public interface RevCreatDecnDao {
	// Common
	public RevCreatDecn get(int id);
	public void saveOrUpdate(RevCreatDecn creatDecn);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific

}
