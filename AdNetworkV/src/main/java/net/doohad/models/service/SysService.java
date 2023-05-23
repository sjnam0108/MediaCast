package net.doohad.models.service;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.sys.SysRtUnit;
import net.doohad.models.sys.SysSvcRespTime;

@Transactional
public interface SysService {
	
	// Common
	public void flush();

	
	//
	// for SysRtUnit
	//
	// Common
	public SysRtUnit getRtUnit(int id);
	public void saveOrUpdate(SysRtUnit rtUnit);
	public void deleteRtUnit(SysRtUnit rtUnit);
	public void deleteRtUnits(List<SysRtUnit> rtUnits);

	// for Kendo Grid Remote Read
	public DataSourceResult getRtUnitList(DataSourceRequest request);

	// for DAO specific
	public SysRtUnit getRtUnit(String ukid);

	
	//
	// for SysSvcRespTime
	//
	// Common
	public SysSvcRespTime getSvcRespTime(int id);
	public void saveOrUpdate(SysSvcRespTime svcRespTime);
	public void deleteSvcRespTime(SysSvcRespTime svcRespTime);
	public void deleteSvcRespTimes(List<SysSvcRespTime> svcRespTimes);

	// for Kendo Grid Remote Read
	public DataSourceResult getSvcRespTimeList(DataSourceRequest request);

	// for DAO specific
	public SysSvcRespTime getSvcRespTime(SysRtUnit rtUnit, Date checkDate);

	
	//
	// for SysTmpStatusLine
	//
	// Common

	// for Kendo Grid Remote Read

	// for DAO specific
	public void insertTmpStatusLine(int screenId, Date playDate, String statusLine);
	public void deleteTmpStatusLineBulkRowsInIds(List<Integer> ids);
	public List<Tuple> getTmpStatusLineTupleList();

	
	//
	// for SysTmpHrlyEvent
	//
	// Common

	// for Kendo Grid Remote Read

	// for DAO specific
	public void insertTmpHrlyEvent(int screenId, Date eventDate, int type);
	public void deleteTmpHrlyEventBulkRowsInIds(List<Integer> ids);
	public List<Tuple> getTmpHrlyEventTupleList();
	
}
