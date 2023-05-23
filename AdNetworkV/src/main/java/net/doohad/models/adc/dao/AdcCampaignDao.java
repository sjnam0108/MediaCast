package net.doohad.models.adc.dao;

import java.util.List;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.adc.AdcCampaign;
import net.doohad.models.knl.KnlMedium;

public interface AdcCampaignDao {
	// Common
	public AdcCampaign get(int id);
	public void saveOrUpdate(AdcCampaign campaign);
	public void delete(AdcCampaign campaign);
	public void delete(List<AdcCampaign> campaigns);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public AdcCampaign get(KnlMedium medium, String name);
	public List<AdcCampaign> getListByMediumId(int mediumId);
	public List<AdcCampaign> getList();
	public List<AdcCampaign> getLisyByAdvertiserId(int advertiserId);
}
