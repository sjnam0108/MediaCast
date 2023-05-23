package net.doohad.models.adc.dao;

import java.util.List;

import javax.persistence.Tuple;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.adc.AdcAd;
import net.doohad.models.knl.KnlMedium;

public interface AdcAdDao {
	// Common
	public AdcAd get(int id);
	public void saveOrUpdate(AdcAd ad);
	public void delete(AdcAd ad);
	public void delete(List<AdcAd> ads);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);
	public DataSourceResult getList(DataSourceRequest request, int campaignId);

	// for DAO specific
	public AdcAd get(KnlMedium medium, String name);
	public List<AdcAd> getListByMediumId(int mediumId);
	public List<AdcAd> getListByCampaignId(int campaignId);
	public int getCountByCampaignId(int campaignId);
	public List<AdcAd> getList();
	public List<AdcAd> getListByMediumIdNameLike(int mediumId, String name);
	public List<Tuple> getCountGroupByMediumStatus(int mediumId);
	public List<AdcAd> getValidList();
}
