package net.doohad.models.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.adn.AdnExcelRow;

@Transactional
public interface AdnService {
	
	// Common
	public void flush();

	
	//
	// for AdnExcelRow
	//
	// Common
	public AdnExcelRow getExcelRow(int id);
	public void saveOrUpdate(AdnExcelRow row);
	public void deleteExcelRow(AdnExcelRow row);
	public void deleteExcelRows(List<AdnExcelRow> rows);

	// for Kendo Grid Remote Read
	public DataSourceResult getExcelRowList(DataSourceRequest request, String type);

	// for DAO specific
	public List<AdnExcelRow> getExcelRowListByMediumIdType(int mediumId, String type);

	
	//
	// for Common
	//
	public boolean deleteBulkExcelRowsByMediumId(int mediumId);
}
