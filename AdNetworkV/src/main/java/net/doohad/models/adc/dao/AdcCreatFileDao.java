package net.doohad.models.adc.dao;

import java.util.List;

import javax.persistence.Tuple;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.adc.AdcCreatFile;

public interface AdcCreatFileDao {
	// Common
	public AdcCreatFile get(int id);
	public void saveOrUpdate(AdcCreatFile creatFile);
	public void delete(AdcCreatFile creatFile);
	public void delete(List<AdcCreatFile> creatFiles);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public List<AdcCreatFile> getListByCreativeId(int creativeId);
	public AdcCreatFile getByCreativeIdResolution(int creativeId, String resolution);
	public List<Tuple> getCountGroupByMediumMediaType(int mediumId);
	public List<Tuple> getCountGroupByCtntFolderId();
	public int getCountByAdvertiserId(int advertiserId);
	
}
