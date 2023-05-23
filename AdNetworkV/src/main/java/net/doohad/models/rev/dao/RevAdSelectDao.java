package net.doohad.models.rev.dao;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Tuple;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.rev.RevAdSelect;

public interface RevAdSelectDao {
	// Common
	public RevAdSelect get(int id);
	public void saveOrUpdate(RevAdSelect adSelect);
	public void delete(RevAdSelect adSelect);
	public void delete(List<RevAdSelect> adSelects);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public RevAdSelect get(UUID uuid);
	public List<RevAdSelect> getLastListByScreenId(int screenId, int maxRecords);
	public List<RevAdSelect> getListByScreenId(int screenId);
	public List<RevAdSelect> getReportedListOrderBySelDateBeforeReportDate(Date date);
	public List<RevAdSelect> getListBeforeSelectDateOrderBySelDate(Date selectDate);
	public void deleteBulkRowsInIds(List<Integer> ids);
	public List<Tuple> getHourStatTupleList1();
	public List<Tuple> getHourStatTupleList2();
	public List<Tuple> getMediumStatTupleList();
	public List<Tuple> getMinStatTupleList1();
	public List<Tuple> getMinStatTupleList2();
}
