package net.doohad.models.fnd.dao;

import java.util.List;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.fnd.FndRegion;

public interface FndRegionDao {
	// Common
	public FndRegion get(int id);
	public void saveOrUpdate(FndRegion region);
	public void delete(FndRegion region);
	public void delete(List<FndRegion> regions);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public FndRegion get(String code);
	public FndRegion getByName(String name);
	public List<FndRegion> getListByListIncluded(boolean listIncluded);
	public List<FndRegion> getListByNameLike(String name);
}
