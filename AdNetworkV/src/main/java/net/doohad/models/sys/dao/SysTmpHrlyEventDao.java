package net.doohad.models.sys.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

public interface SysTmpHrlyEventDao {
	// Common

	// for Kendo Grid Remote Read

	// for DAO specific
	public void insert(int screenId, Date eventDate, int type);
	public void deleteBulkRowsInIds(List<Integer> ids);
	public List<Tuple> getTupleList();

}
