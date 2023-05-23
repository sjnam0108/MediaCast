package net.doohad.models.fnd.dao;

import java.util.List;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.fnd.FndLoginLog;

public interface FndLoginLogDao {
	// Common
	public FndLoginLog get(int id);
	public void saveOrUpdate(FndLoginLog loginLog);
	public void delete(FndLoginLog loginLog);
	public void delete(List<FndLoginLog> loginLogs);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public FndLoginLog getLastByUserId(int userId);

}
