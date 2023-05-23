package net.doohad.models.rev.dao;

import java.util.Date;
import java.util.List;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.adc.AdcAd;
import net.doohad.models.rev.RevAdHourlyPlay;

public interface RevAdHourlyPlayDao {
	// Common
	public RevAdHourlyPlay get(int id);
	public void saveOrUpdate(RevAdHourlyPlay hourPlay);
	public void delete(RevAdHourlyPlay hourPlay);
	public void delete(List<RevAdHourlyPlay> hourPlays);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public RevAdHourlyPlay get(AdcAd ad, Date playDate);

}
