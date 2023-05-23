package net.doohad.models.rev.dao;

import java.util.Date;
import java.util.List;

import net.doohad.models.rev.RevPlayHist;

public interface RevPlayHistDao {
	// Common
	public RevPlayHist get(int id);
	public void saveOrUpdate(RevPlayHist playHist);
	public void delete(RevPlayHist playHist);
	public void delete(List<RevPlayHist> playHists);

	// for Kendo Grid Remote Read

	// for DAO specific
	public int getCountByScreenIdStartDate(int screenId, Date startDate);
	public List<RevPlayHist> getFirstList(int maxRecords);
	public void deleteBulkRowsInIds(List<Integer> ids);
	public List<RevPlayHist> getLastListByScreenId(int screenId, int maxRecords);
	public List<RevPlayHist> getListByScreenId(int screenId);
}
