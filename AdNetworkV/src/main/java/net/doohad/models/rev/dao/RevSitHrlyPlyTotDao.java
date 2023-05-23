package net.doohad.models.rev.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.inv.InvSite;
import net.doohad.models.rev.RevSitHrlyPlyTot;

public interface RevSitHrlyPlyTotDao {
	// Common
	public RevSitHrlyPlyTot get(int id);
	public void saveOrUpdate(RevSitHrlyPlyTot hrlyPlyTot);
	public void delete(RevSitHrlyPlyTot hrlyPlyTot);
	public void delete(List<RevSitHrlyPlyTot> hrlyPlyTots);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request, Date playDate);

	// for DAO specific
	public RevSitHrlyPlyTot get(InvSite site, Date playDate);
	public List<RevSitHrlyPlyTot> getListByMediumIdPlayDate(
			int mediumId, Date playDate);
	public List<RevSitHrlyPlyTot> getListByPlayDate(Date playDate);
	public List<Tuple> getTupleListByPlayDate(Date playDate);
	public Tuple getStatByMediumIdPlayDate(int mediumId, Date playDate);
}
