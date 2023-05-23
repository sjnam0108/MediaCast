package net.doohad.models.rev.dao;

import java.util.Date;
import java.util.List;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.adc.AdcCreative;
import net.doohad.models.rev.RevCreatHourlyPlay;

public interface RevCreatHourlyPlayDao {
	// Common
	public RevCreatHourlyPlay get(int id);
	public void saveOrUpdate(RevCreatHourlyPlay hourPlay);
	public void delete(RevCreatHourlyPlay hourPlay);
	public void delete(List<RevCreatHourlyPlay> hourPlays);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public RevCreatHourlyPlay get(AdcCreative creative, Date playDate);

}
