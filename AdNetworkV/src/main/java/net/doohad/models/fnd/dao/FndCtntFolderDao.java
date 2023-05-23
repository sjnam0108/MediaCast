package net.doohad.models.fnd.dao;

import java.util.List;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.fnd.FndCtntFolder;

public interface FndCtntFolderDao {
	// Common
	public FndCtntFolder get(int id);
	public void saveOrUpdate(FndCtntFolder ctntFolder);
	public void delete(FndCtntFolder ctntFolder);
	public void delete(List<FndCtntFolder> ctntFolders);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public FndCtntFolder get(String name);
	public int getCount();
	public List<FndCtntFolder> getList();

}
