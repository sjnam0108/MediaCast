package net.doohad.models.rev.dao;

import java.util.Date;
import java.util.List;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.rev.RevScrHrlyFailTot;

public interface RevScrHrlyFailTotDao {
	// Common
	public RevScrHrlyFailTot get(int id);
	public void saveOrUpdate(RevScrHrlyFailTot hrlyFailTot);
	public void delete(RevScrHrlyFailTot hrlyFailTot);
	public void delete(List<RevScrHrlyFailTot> hrlyFailTots);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request, Date playDate);

	// for DAO specific
	public RevScrHrlyFailTot get(InvScreen screen, Date playDate);
	public RevScrHrlyFailTot get(int screenId, Date playDate);

}
