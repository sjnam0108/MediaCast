package net.doohad.models.rev.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.adc.AdcAd;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.rev.RevScrHourlyPlay;

public interface RevScrHourlyPlayDao {
	// Common
	public RevScrHourlyPlay get(int id);
	public void saveOrUpdate(RevScrHourlyPlay hourPlay);
	public void delete(RevScrHourlyPlay hourPlay);
	public void delete(List<RevScrHourlyPlay> hourPlays);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public RevScrHourlyPlay get(InvScreen screen, AdcAd ad, Date playDate);
	public RevScrHourlyPlay get(int screenId, int adId, Date playDate);
	public List<RevScrHourlyPlay> getListBySiteIdAdIdPlayDate(
			int siteId, int adId, Date playDate);
	public List<Tuple> getScrSumListByPlayDate(Date playDate);
	public List<Tuple> getSitSumListByPlayDate(Date playDate);
	public List<Tuple> getPlayDateListByLastUpdateDate(Date date);
	public List<RevScrHourlyPlay> getListByAdIdPlayDate(int adId, Date playDate);
	public List<Tuple> getAdStatListByScreenIdPlayDate(int screenId, Date playDate);
	public List<Tuple> getStatGroupByAdPlayDate(Date playDate);
	public List<Tuple> getStatGroupByCreatPlayDate(Date playDate);
}
