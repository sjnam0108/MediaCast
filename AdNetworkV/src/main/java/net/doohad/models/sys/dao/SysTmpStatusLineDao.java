package net.doohad.models.sys.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

public interface SysTmpStatusLineDao {
	// Common

	// for Kendo Grid Remote Read

	// for DAO specific
	public void insert(int screenId, Date playDate, String statusLine);
	public void deleteBulkRowsInIds(List<Integer> ids);
	public List<Tuple> getTupleList();
	
}
