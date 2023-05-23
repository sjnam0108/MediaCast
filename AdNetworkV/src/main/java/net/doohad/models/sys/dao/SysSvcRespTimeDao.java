package net.doohad.models.sys.dao;

import java.util.Date;
import java.util.List;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.sys.SysRtUnit;
import net.doohad.models.sys.SysSvcRespTime;

public interface SysSvcRespTimeDao {
	// Common
	public SysSvcRespTime get(int id);
	public void saveOrUpdate(SysSvcRespTime svcRespTime);
	public void delete(SysSvcRespTime svcRespTime);
	public void delete(List<SysSvcRespTime> svcRespTimes);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public SysSvcRespTime get(SysRtUnit rtUnit, Date checkDate);

}
