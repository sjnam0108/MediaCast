package net.doohad.models.adn.dao;

import java.util.List;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.adn.AdnExcelRow;

public interface AdnExcelRowDao {
	// Common
	public AdnExcelRow get(int id);
	public void saveOrUpdate(AdnExcelRow row);
	public void delete(AdnExcelRow row);
	public void delete(List<AdnExcelRow> rows);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request, String type);

	// for DAO specific
	public List<AdnExcelRow> getListByMediumIdType(int mediumId, String type);

}
