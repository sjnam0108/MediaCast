package net.doohad.models.knl.dao;

import java.util.List;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.knl.KnlMedium;

public interface KnlMediumDao {
	// Common
	public KnlMedium get(int id);
	public void saveOrUpdate(KnlMedium medium);
	public void delete(KnlMedium medium);
	public void delete(List<KnlMedium> media);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public KnlMedium get(String shortName);
	public KnlMedium getByApiKey(String apiKey);
	public List<KnlMedium> getListByShortNameLike(String shortName);
	public List<KnlMedium> getValidList();
}
