package net.doohad.models.org.dao;

import java.util.List;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgSiteCond;

public interface OrgSiteCondDao {
	// Common
	public OrgSiteCond get(int id);
	public void saveOrUpdate(OrgSiteCond siteCond);
	public void delete(OrgSiteCond siteCond);
	public void delete(List<OrgSiteCond> siteConds);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public OrgSiteCond get(KnlMedium medium, String code);
	public List<OrgSiteCond> getListByMediumId(int mediumId);
	public List<OrgSiteCond> getListByMediumIdActiveStatus(int mediumId, boolean activeStatus);
	public List<OrgSiteCond> getListByMediumIdNameLike(int mediumId, String name);
}
