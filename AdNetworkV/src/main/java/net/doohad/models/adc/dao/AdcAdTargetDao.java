package net.doohad.models.adc.dao;

import java.util.List;

import javax.persistence.Tuple;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.adc.AdcAdTarget;

public interface AdcAdTargetDao {
	// Common
	public AdcAdTarget get(int id);
	public void saveOrUpdate(AdcAdTarget adTarget);
	public void delete(AdcAdTarget adTarget);
	public void delete(List<AdcAdTarget> adTargets);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public List<AdcAdTarget> getListByAdId(int adId);
	public void saveAndReorder(AdcAdTarget adTarget);
	public List<Tuple> getCountGroupByMediumAdId(int mediumId);
	public List<Tuple> getCountGroupByAdId();
	
}
