package net.doohad.models.adc.dao;

import java.util.Date;
import java.util.List;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.adc.AdcAdCreative;

public interface AdcAdCreativeDao {
	// Common
	public AdcAdCreative get(int id);
	public void saveOrUpdate(AdcAdCreative adCreative);
	public void delete(AdcAdCreative adCreative);
	public void delete(List<AdcAdCreative> adCreatives);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public List<AdcAdCreative> getListByAdId(int adId);
	public List<AdcAdCreative> getListByCreativeId(int creativeId);
	public int getCountByAdId(int adId);
	public List<AdcAdCreative> getCandiListByMediumIdDate(int mediumId, Date sDate, Date eDate);
	public List<AdcAdCreative> getPlListByMediumId(int mediumId);
	public AdcAdCreative getEff(int id, int mediumId, Date sDate, Date eDate);
	public List<AdcAdCreative> getActiveListByAdId(int adId);
	public List<AdcAdCreative> getActiveListByCampaignId(int campaignId);

}
