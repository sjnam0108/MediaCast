package net.doohad.models.rev.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.rev.RevScrHrlyNoAdTot;

public interface RevScrHrlyNoAdTotDao {
	// Common
	public RevScrHrlyNoAdTot get(int id);
	public void saveOrUpdate(RevScrHrlyNoAdTot hrlyNoAdTot);
	public void delete(RevScrHrlyNoAdTot hrlyNoAdTot);
	public void delete(List<RevScrHrlyNoAdTot> hrlyNoAdTots);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request, Date playDate);

	// for DAO specific
	public RevScrHrlyNoAdTot get(InvScreen screen, Date playDate);
	public RevScrHrlyNoAdTot get(int screenId, Date playDate);
	public Tuple getStatByMediumIdPlayDate(int mediumId, Date playDate);

}
