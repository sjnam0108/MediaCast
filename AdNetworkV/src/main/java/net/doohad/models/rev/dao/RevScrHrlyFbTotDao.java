package net.doohad.models.rev.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.rev.RevScrHrlyFbTot;

public interface RevScrHrlyFbTotDao {
	// Common
	public RevScrHrlyFbTot get(int id);
	public void saveOrUpdate(RevScrHrlyFbTot hrlyFbTot);
	public void delete(RevScrHrlyFbTot hrlyFbTot);
	public void delete(List<RevScrHrlyFbTot> hrlyFbTots);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request, Date playDate);

	// for DAO specific
	public RevScrHrlyFbTot get(InvScreen screen, Date playDate);
	public RevScrHrlyFbTot get(int screenId, Date playDate);
	public Tuple getStatByMediumIdPlayDate(int mediumId, Date playDate);

}
