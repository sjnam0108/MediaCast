package net.doohad.models.service;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;
import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.adc.AdcAd;
import net.doohad.models.adc.AdcAdCreative;
import net.doohad.models.adc.AdcAdTarget;
import net.doohad.models.adc.AdcCampaign;
import net.doohad.models.adc.AdcCreatFile;
import net.doohad.models.adc.AdcCreatTarget;
import net.doohad.models.adc.AdcCreative;
import net.doohad.models.knl.KnlMedium;

@Transactional
public interface AdcService {
	
	// Common
	public void flush();

	
	//
	// for AdcCreative
	//
	// Common
	public AdcCreative getCreative(int id);
	public void saveOrUpdate(AdcCreative creative);
	public void deleteCreative(AdcCreative creative);
	public void deleteCreatives(List<AdcCreative> creatives);

	// for Kendo Grid Remote Read
	public DataSourceResult getCreativeList(DataSourceRequest request);
	public DataSourceResult getPendApprCreativeList(DataSourceRequest request);
	public DataSourceResult getCreativeList(DataSourceRequest request, int advertiserId);

	// for DAO specific
	public List<AdcCreative> getCreativeListByMediumIdNameLike(int mediumId, String name);
	public List<Tuple> getCreativeCountGroupByMediumAdvertiserId(int mediumId);
	public List<AdcCreative> getCreativeListByAdvertiserId(int advertiserId);
	public List<AdcCreative> getValidCreativeList();
	public List<AdcCreative> getValidCreativeFallbackListByMediumId(int mediumId);
	public List<AdcCreative> getCreativeListByMediumIdName(int mediumId, String name);
	public int getCreativeCountByAdvertiserId(int advertiserId);

	
	//
	// for AdcCreatFile
	//
	// Common
	public AdcCreatFile getCreatFile(int id);
	public void saveOrUpdate(AdcCreatFile creatFile);
	public void deleteCreatFile(AdcCreatFile creatFile);
	public void deleteCreatFiles(List<AdcCreatFile> creatFiles);

	// for Kendo Grid Remote Read
	public DataSourceResult getCreatFileList(DataSourceRequest request);

	// for DAO specific
	public List<AdcCreatFile> getCreatFileListByCreativeId(int creativeId);
	public AdcCreatFile getCreatFileByCreativeIdResolution(int creativeId, String resolution);
	public List<Tuple> getCreatFileCountGroupByMediumMediaType(int mediumId);
	public List<Tuple> getCreatFileCountGroupByCtntFolderId();
	public int getCreatFileCountByAdvertiserId(int advertiserId);

	
	//
	// for AdcCampaign
	//
	// Common
	public AdcCampaign getCampaign(int id);
	public void saveOrUpdate(AdcCampaign campaign);
	public void deleteCampaign(AdcCampaign campaign);
	public void deleteCampaigns(List<AdcCampaign> campaigns);

	// for Kendo Grid Remote Read
	public DataSourceResult getCampaignList(DataSourceRequest request);

	// for DAO specific
	public AdcCampaign getCampaign(KnlMedium medium, String name);
	public List<AdcCampaign> getCampaignListByMediumId(int mediumId);
	public List<AdcCampaign> getCampaignList();
	public List<AdcCampaign> getCampaignLisyByAdvertiserId(int advertiserId);

	
	//
	// for AdcAd
	//
	// Common
	public AdcAd getAd(int id);
	public void saveOrUpdate(AdcAd ad);
	public void deleteAd(AdcAd ad);
	public void deleteAds(List<AdcAd> ads);

	// for Kendo Grid Remote Read
	public DataSourceResult getAdList(DataSourceRequest request);
	public DataSourceResult getAdList(DataSourceRequest request, int campaignId);

	// for DAO specific
	public AdcAd getAd(KnlMedium medium, String name);
	public List<AdcAd> getAdListByMediumId(int mediumId);
	public List<AdcAd> getAdListByCampaignId(int campaignId);
	public int getAdCountByCampaignId(int campaignId);
	public List<AdcAd> getAdList();
	public List<AdcAd> getAdListByMediumIdNameLike(int mediumId, String name);
	public List<Tuple> getAdCountGroupByMediumStatus(int mediumId);
	public List<AdcAd> getValidAdList();

	
	//
	// for AdcAdCreative
	//
	// Common
	public AdcAdCreative getAdCreative(int id);
	public void saveOrUpdate(AdcAdCreative adCreative);
	public void deleteAdCreative(AdcAdCreative adCreative);
	public void deleteAdCreatives(List<AdcAdCreative> adCreatives);

	// for Kendo Grid Remote Read
	public DataSourceResult getAdCreativeList(DataSourceRequest request);

	// for DAO specific
	public List<AdcAdCreative> getAdCreativeListByAdId(int adId);
	public List<AdcAdCreative> getAdCreativeListByCreativeId(int creativeId);
	public int getAdCreativeCountByAdId(int adId);
	public List<AdcAdCreative> getCandiAdCreativeListByMediumIdDate(int mediumId, Date sDate, Date eDate);
	public List<AdcAdCreative> getPlAdCreativeListByMediumId(int mediumId);
	public AdcAdCreative getEffAdCreative(int id, int mediumId, Date sDate, Date eDate);
	public List<AdcAdCreative> getActiveAdCreativeListByAdId(int adId);
	public List<AdcAdCreative> getActiveAdCreativeListByCampaignId(int campaignId);

	
	//
	// for AdcCreatTarget
	//
	// Common
	public AdcCreatTarget getCreatTarget(int id);
	public void saveOrUpdate(AdcCreatTarget creatTarget);
	public void deleteCreatTarget(AdcCreatTarget creatTarget);
	public void deleteCreatTargets(List<AdcCreatTarget> creatTargets);

	// for Kendo Grid Remote Read
	public DataSourceResult getCreatTargetList(DataSourceRequest request);

	// for DAO specific
	public List<AdcCreatTarget> getCreatTargetListByCreativeId(int creativeId);
	public void saveAndReorderCreatTarget(AdcCreatTarget creatTarget);
	public List<Tuple> getCreatTargetCountGroupByMediumCreativeId(int mediumId);
	public List<Tuple> getCreatTargetCountGroupByCreativeId();

	
	//
	// for AdcAdTarget
	//
	// Common
	public AdcAdTarget getAdTarget(int id);
	public void saveOrUpdate(AdcAdTarget adTarget);
	public void deleteAdTarget(AdcAdTarget adTarget);
	public void deleteAdTargets(List<AdcAdTarget> adTargets);

	// for Kendo Grid Remote Read
	public DataSourceResult getAdTargetList(DataSourceRequest request);

	// for DAO specific
	public List<AdcAdTarget> getAdTargetListByAdId(int adId);
	public void saveAndReorderAdTarget(AdcAdTarget adTarget);
	public List<Tuple> getAdTargetCountGroupByMediumAdId(int mediumId);
	public List<Tuple> getAdTargetCountGroupByAdId();

	
	//
	// for Common
	//
	public int measureResolutionWithMedium(String resolution, int mediumId, int boundVal);
	public void refreshCampaignInfoBasedAds(int campaignId);
	public boolean refreshCampaignAdStatusBasedToday();
	public void deleteSoftCreatFile(AdcCreatFile creatFile, HttpSession session);
	public void deleteSoftCreative(AdcCreative creative, HttpSession session);
	public void deleteSoftCampaign(AdcCampaign campaign, HttpSession session);
	public void deleteSoftAd(AdcAd ad, HttpSession session);
	public void refreshAdCreativePeriodByAdDates(AdcAd ad, Date prevSDate, Date prevEDate);
	
}
