package net.doohad.models.sys.dao;

import java.util.List;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.sys.SysRtUnit;

public interface SysRtUnitDao {
	// Common
	public SysRtUnit get(int id);
	public void saveOrUpdate(SysRtUnit rtUnit);
	public void delete(SysRtUnit rtUnit);
	public void delete(List<SysRtUnit> rtUnits);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public SysRtUnit get(String ukid);

}
