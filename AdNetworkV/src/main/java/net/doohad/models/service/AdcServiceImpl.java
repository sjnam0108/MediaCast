package net.doohad.models.service;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;
import javax.servlet.http.HttpSession;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
import net.doohad.models.adc.dao.AdcAdCreativeDao;
import net.doohad.models.adc.dao.AdcAdDao;
import net.doohad.models.adc.dao.AdcAdTargetDao;
import net.doohad.models.adc.dao.AdcCampaignDao;
import net.doohad.models.adc.dao.AdcCreatFileDao;
import net.doohad.models.adc.dao.AdcCreatTargetDao;
import net.doohad.models.adc.dao.AdcCreativeDao;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.knl.dao.KnlMediumDao;
import net.doohad.utils.Util;

@Transactional
@Service("adcService")
public class AdcServiceImpl implements AdcService {

	private static final Logger logger = LoggerFactory.getLogger(AdcServiceImpl.class);
	

	//
    // General
    //
    @Autowired
    private SessionFactory sessionFactory;
    
	@Override
	public void flush() {
		
		sessionFactory.getCurrentSession().flush();
	}

	
    
    //
    // DAO
    //
    @Autowired
    private AdcCreativeDao creativeDao;

    @Autowired
    private AdcCreatFileDao creatFileDao;

    @Autowired
    private AdcCampaignDao campaignDao;

    @Autowired
    private AdcAdDao adDao;

    @Autowired
    private AdcAdCreativeDao adCreativeDao;
    
    @Autowired
    private AdcCreatTargetDao creatTargetDao;
    
    @Autowired
    private AdcAdTargetDao adTargetDao;

    @Autowired
    private KnlMediumDao mediumDao;

    
    
	//
	// for AdcCreativeDao
	//
	@Override
	public AdcCreative getCreative(int id) {
		return creativeDao.get(id);
	}

	@Override
	public void saveOrUpdate(AdcCreative creative) {
		creativeDao.saveOrUpdate(creative);
	}

	@Override
	public void deleteCreative(AdcCreative creative) {
		creativeDao.delete(creative);
	}

	@Override
	public void deleteCreatives(List<AdcCreative> creatives) {
		creativeDao.delete(creatives);
	}

	@Override
	public DataSourceResult getCreativeList(DataSourceRequest request) {
		return creativeDao.getList(request);
	}

	@Override
	public DataSourceResult getPendApprCreativeList(DataSourceRequest request) {
		return creativeDao.getPendApprList(request);
	}

	@Override
	public DataSourceResult getCreativeList(DataSourceRequest request, int advertiserId) {
		return creativeDao.getList(request, advertiserId);
	}

	@Override
	public List<AdcCreative> getCreativeListByMediumIdNameLike(int mediumId, String name) {
		return creativeDao.getListByMediumIdNameLike(mediumId, name);
	}

	@Override
	public List<Tuple> getCreativeCountGroupByMediumAdvertiserId(int mediumId) {
		return creativeDao.getCountGroupByMediumAdvertiserId(mediumId);
	}

	@Override
	public List<AdcCreative> getCreativeListByAdvertiserId(int advertiserId) {
		return creativeDao.getListByAdvertiserId(advertiserId);
	}

	@Override
	public List<AdcCreative> getValidCreativeList() {
		return creativeDao.getValidList();
	}

	@Override
	public List<AdcCreative> getValidCreativeFallbackListByMediumId(int mediumId) {
		return creativeDao.getValidFallbackListByMediumId(mediumId);
	}

	@Override
	public List<AdcCreative> getCreativeListByMediumIdName(int mediumId, String name) {
		return creativeDao.getListByMediumIdName(mediumId, name);
	}

	@Override
	public int getCreativeCountByAdvertiserId(int advertiserId) {
		return creativeDao.getCountByAdvertiserId(advertiserId);
	}

    
    
	//
	// for AdcCreatFileDao
	//
	@Override
	public AdcCreatFile getCreatFile(int id) {
		return creatFileDao.get(id);
	}

	@Override
	public void saveOrUpdate(AdcCreatFile creatFile) {
		creatFileDao.saveOrUpdate(creatFile);
	}

	@Override
	public void deleteCreatFile(AdcCreatFile creatFile) {
		creatFileDao.delete(creatFile);
	}

	@Override
	public void deleteCreatFiles(List<AdcCreatFile> creatFiles) {
		creatFileDao.delete(creatFiles);
	}

	@Override
	public DataSourceResult getCreatFileList(DataSourceRequest request) {
		return creatFileDao.getList(request);
	}

	@Override
	public List<AdcCreatFile> getCreatFileListByCreativeId(int creativeId) {
		return creatFileDao.getListByCreativeId(creativeId);
	}

	@Override
	public AdcCreatFile getCreatFileByCreativeIdResolution(int creativeId, String resolution) {
		return creatFileDao.getByCreativeIdResolution(creativeId, resolution);
	}

	@Override
	public List<Tuple> getCreatFileCountGroupByMediumMediaType(int mediumId) {
		return creatFileDao.getCountGroupByMediumMediaType(mediumId);
	}

	@Override
	public List<Tuple> getCreatFileCountGroupByCtntFolderId() {
		return creatFileDao.getCountGroupByCtntFolderId();
	}

	@Override
	public int getCreatFileCountByAdvertiserId(int advertiserId) {
		return creatFileDao.getCountByAdvertiserId(advertiserId);
	}

    
    
	//
	// for AdcCampaignDao
	//
	@Override
	public AdcCampaign getCampaign(int id) {
		return campaignDao.get(id);
	}

	@Override
	public void saveOrUpdate(AdcCampaign campaign) {
		campaignDao.saveOrUpdate(campaign);
	}

	@Override
	public void deleteCampaign(AdcCampaign campaign) {
		campaignDao.delete(campaign);
	}

	@Override
	public void deleteCampaigns(List<AdcCampaign> campaigns) {
		campaignDao.delete(campaigns);
	}

	@Override
	public DataSourceResult getCampaignList(DataSourceRequest request) {
		return campaignDao.getList(request);
	}

	@Override
	public AdcCampaign getCampaign(KnlMedium medium, String name) {
		return campaignDao.get(medium, name);
	}

	@Override
	public List<AdcCampaign> getCampaignListByMediumId(int mediumId) {
		return campaignDao.getListByMediumId(mediumId);
	}

	@Override
	public List<AdcCampaign> getCampaignList() {
		return campaignDao.getList();
	}

	@Override
	public List<AdcCampaign> getCampaignLisyByAdvertiserId(int advertiserId) {
		return campaignDao.getLisyByAdvertiserId(advertiserId);
	}

    
    
	//
	// for AdcAdDao
	//
	@Override
	public AdcAd getAd(int id) {
		return adDao.get(id);
	}

	@Override
	public void saveOrUpdate(AdcAd ad) {
		adDao.saveOrUpdate(ad);
	}

	@Override
	public void deleteAd(AdcAd ad) {
		adDao.delete(ad);
	}

	@Override
	public void deleteAds(List<AdcAd> ads) {
		adDao.delete(ads);
	}

	@Override
	public DataSourceResult getAdList(DataSourceRequest request) {
		return adDao.getList(request);
	}

	@Override
	public DataSourceResult getAdList(DataSourceRequest request, int campaignId) {
		return adDao.getList(request, campaignId);
	}

	@Override
	public AdcAd getAd(KnlMedium medium, String name) {
		return adDao.get(medium, name);
	}

	@Override
	public List<AdcAd> getAdListByMediumId(int mediumId) {
		return adDao.getListByMediumId(mediumId);
	}

	@Override
	public List<AdcAd> getAdListByCampaignId(int campaignId) {
		return adDao.getListByCampaignId(campaignId);
	}

	@Override
	public int getAdCountByCampaignId(int campaignId) {
		return adDao.getCountByCampaignId(campaignId);
	}

	@Override
	public List<AdcAd> getAdList() {
		return adDao.getList();
	}

	@Override
	public List<AdcAd> getAdListByMediumIdNameLike(int mediumId, String name) {
		return adDao.getListByMediumIdNameLike(mediumId, name);
	}

	@Override
	public List<Tuple> getAdCountGroupByMediumStatus(int mediumId) {
		return adDao.getCountGroupByMediumStatus(mediumId);
	}

	@Override
	public List<AdcAd> getValidAdList() {
		return adDao.getValidList();
	}

    
    
	//
	// for AdcAdCreativeDao
	//
	@Override
	public AdcAdCreative getAdCreative(int id) {
		return adCreativeDao.get(id);
	}

	@Override
	public void saveOrUpdate(AdcAdCreative adCreative) {
		adCreativeDao.saveOrUpdate(adCreative);
	}

	@Override
	public void deleteAdCreative(AdcAdCreative adCreative) {
		adCreativeDao.delete(adCreative);
	}

	@Override
	public void deleteAdCreatives(List<AdcAdCreative> adCreatives) {
		adCreativeDao.delete(adCreatives);
	}

	@Override
	public DataSourceResult getAdCreativeList(DataSourceRequest request) {
		return adCreativeDao.getList(request);
	}

	@Override
	public List<AdcAdCreative> getAdCreativeListByAdId(int adId) {
		return adCreativeDao.getListByAdId(adId);
	}

	@Override
	public List<AdcAdCreative> getAdCreativeListByCreativeId(int creativeId) {
		return adCreativeDao.getListByCreativeId(creativeId);
	}

	@Override
	public int getAdCreativeCountByAdId(int adId) {
		return adCreativeDao.getCountByAdId(adId);
	}

	@Override
	public List<AdcAdCreative> getCandiAdCreativeListByMediumIdDate(int mediumId, Date sDate, Date eDate) {
		return adCreativeDao.getCandiListByMediumIdDate(mediumId, sDate, eDate);
	}

	@Override
	public List<AdcAdCreative> getPlAdCreativeListByMediumId(int mediumId) {
		return adCreativeDao.getPlListByMediumId(mediumId);
	}

	@Override
	public AdcAdCreative getEffAdCreative(int id, int mediumId, Date sDate, Date eDate) {
		return adCreativeDao.getEff(id, mediumId, sDate, eDate);
	}

	@Override
	public List<AdcAdCreative> getActiveAdCreativeListByAdId(int adId) {
		return adCreativeDao.getActiveListByAdId(adId);
	}

	@Override
	public List<AdcAdCreative> getActiveAdCreativeListByCampaignId(int campaignId) {
		return adCreativeDao.getActiveListByCampaignId(campaignId);
	}

    
    
	//
	// for AdcCreatTargetDao
	//
	@Override
	public AdcCreatTarget getCreatTarget(int id) {
		return creatTargetDao.get(id);
	}

	@Override
	public void saveOrUpdate(AdcCreatTarget creatTarget) {
		creatTargetDao.saveOrUpdate(creatTarget);
	}

	@Override
	public void deleteCreatTarget(AdcCreatTarget creatTarget) {
		creatTargetDao.delete(creatTarget);
	}

	@Override
	public void deleteCreatTargets(List<AdcCreatTarget> creatTargets) {
		creatTargetDao.delete(creatTargets);
	}

	@Override
	public DataSourceResult getCreatTargetList(DataSourceRequest request) {
		return creatTargetDao.getList(request);
	}

	@Override
	public List<AdcCreatTarget> getCreatTargetListByCreativeId(int creativeId) {
		return creatTargetDao.getListByCreativeId(creativeId);
	}

	@Override
	public void saveAndReorderCreatTarget(AdcCreatTarget creatTarget) {
		creatTargetDao.saveAndReorder(creatTarget);
	}

	@Override
	public List<Tuple> getCreatTargetCountGroupByMediumCreativeId(int mediumId) {
		return creatTargetDao.getCountGroupByMediumCreativeId(mediumId);
	}

	@Override
	public List<Tuple> getCreatTargetCountGroupByCreativeId() {
		return creatTargetDao.getCountGroupByCreativeId();
	}
    
    
	//
	// for AdcAdTargetDao
	//
	@Override
	public AdcAdTarget getAdTarget(int id) {
		return adTargetDao.get(id);
	}

	@Override
	public void saveOrUpdate(AdcAdTarget adTarget) {
		adTargetDao.saveOrUpdate(adTarget);
	}

	@Override
	public void deleteAdTarget(AdcAdTarget adTarget) {
		adTargetDao.delete(adTarget);
	}

	@Override
	public void deleteAdTargets(List<AdcAdTarget> adTargets) {
		adTargetDao.delete(adTargets);
	}

	@Override
	public DataSourceResult getAdTargetList(DataSourceRequest request) {
		return adTargetDao.getList(request);
	}

	@Override
	public List<AdcAdTarget> getAdTargetListByAdId(int adId) {
		return adTargetDao.getListByAdId(adId);
	}

	@Override
	public void saveAndReorderAdTarget(AdcAdTarget adTarget) {
		adTargetDao.saveAndReorder(adTarget);
	}

	@Override
	public List<Tuple> getAdTargetCountGroupByMediumAdId(int mediumId) {
		return adTargetDao.getCountGroupByMediumAdId(mediumId);
	}

	@Override
	public List<Tuple> getAdTargetCountGroupByAdId() {
		return adTargetDao.getCountGroupByAdId();
	}

    
	
	//
	// for Common
	//
	@Override
	public int measureResolutionWithMedium(String resolution, int mediumId, int boundVal) {
		
		//  반환값:
		//
		//     1  -  가장 적합(매체의 선택된 해상도와 정확히 일치)
		//     0  -  매체의 선택된 해상도 중 하나와 20% 범위 내 비율
		//    -1  -  매체의 어떤 해상도와도 20% 범위를 벗어남
		
		KnlMedium medium = mediumDao.get(mediumId);
		if (Util.isValid(resolution) && medium != null) {
			List<String> medRes = Util.tokenizeValidStr(medium.getResolutions());
			
			for(String res : medRes) {
				if (res.equals(resolution)) {
					return 1;
				}
			}
			
			float ratio = Util.getResolutionRatio(resolution);
			if (ratio > 0f) {
				for(String res : medRes) {
					float rt = Util.getResolutionRatio(res);
					if (rt > 0f) {
						if (Util.getPctDifference(ratio, rt) <= boundVal) {
							return 0;
						}
					}
				}
			}
		}
		
		return -1;
	}

	@Override
	public void refreshCampaignInfoBasedAds(int campaignId) {
		
		Date startDate = null;
		Date endDate = null;

		AdcCampaign campaign = getCampaign(campaignId);
		if (campaign != null && !campaign.getStatus().equals("V") && !campaign.getStatus().equals("T") && 
				!campaign.isDeleted()) {
			List<AdcAd> adList = getAdListByCampaignId(campaignId);
			for(AdcAd ad : adList) {
				if (startDate == null || ad.getStartDate().before(startDate)) {
					startDate = ad.getStartDate();
				}
				if (endDate == null || ad.getEndDate().after(endDate)) {
					endDate = ad.getEndDate();
				}
			}
			
			String status = "U";
			if (startDate != null && endDate != null) {
				Date today = Util.removeTimeOfDate(new Date());
				if (today.before(startDate)) {
					// "U"
				} else if (today.after(endDate)) {
					status = "C";
				} else {
					status = "R";
				}
			}
			
			campaign.setStatus(status);
			campaign.setStartDate(startDate);
			campaign.setEndDate(endDate);
			
			try {
				saveOrUpdate(campaign);
			} catch (Exception e) {
				logger.error("refreshCampaignInfoBasedAds", e);
			}
		}
	}

	@Override
	public boolean refreshCampaignAdStatusBasedToday() {
		
		// 오늘 날짜 기준으로 상태가 변경되어야 하는 캠페인 및 광고에 대한 상태 변경
		//
		
		Date today = Util.removeTimeOfDate(new Date());
		boolean ret = true;
		
		// 1. 캠페인 상태 변경
		List<AdcCampaign> campaignList = getCampaignList();
		
		try {
			for(AdcCampaign campaign : campaignList) {
				// 대상 상태는 U/R/C
				if (campaign.getStatus().equals("U") || campaign.getStatus().equals("R") || 
						campaign.getStatus().equals("C")) {
					if (campaign.getStartDate() != null && campaign.getEndDate() != null) {
						String status = "U";
						if (today.before(campaign.getStartDate())) {
							// "U"
						} else if (today.after(campaign.getEndDate())) {
							status = "C";
						} else {
							status = "R";
						}
						
						if (!campaign.getStatus().equals(status)) {
							campaign.setStatus(status);
							
							saveOrUpdate(campaign);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("refreshCampaignAdStatusBasedToday - campaign", e);
			ret = false;
		}
		
		// 2. 광고 상태 변경
		List<AdcAd> adList = getAdList();
		
		try {
			for(AdcAd ad : adList) {
				// 대상 상태는 A/R/C
				if (ad.getStatus().equals("A") || ad.getStatus().equals("R") || 
						ad.getStatus().equals("C")) {
					String status = "A";
					if (today.before(ad.getStartDate())) {
						// "A"
					} else if (today.after(ad.getEndDate())) {
						status = "C";
					} else {
						status = "R";
					}
					
					if (!ad.getStatus().equals(status)) {
						ad.setStatus(status);
						
						saveOrUpdate(ad);
					}
				}
			}
		} catch (Exception e) {
			logger.error("refreshCampaignAdStatusBasedToday - ad", e);
			ret = false;
		}
		
		return ret;
	}

	@Override
	public void deleteSoftCreatFile(AdcCreatFile creatFile, HttpSession session) {
		
		if (creatFile != null) {
			
			creatFile.setDeleted(true);
			creatFile.setResolution(creatFile.getResolution() + Util.toSimpleString(new Date(), "_yyyyMMdd_HHmm"));
			
			creatFileDao.saveOrUpdate(creatFile);
		}
	}

	@Override
	public void deleteSoftCreative(AdcCreative creative, HttpSession session) {
		
		if (creative != null) {
			
			creative.setDeleted(true);
			creative.setStatus("T");
			creative.setName(creative.getName() + Util.toSimpleString(new Date(), "_yyyyMMdd_HHmm"));
			
			creative.touchWho(session);
			
			creativeDao.saveOrUpdate(creative);
		}
	}

	@Override
	public void deleteSoftCampaign(AdcCampaign campaign, HttpSession session) {
		
		if (campaign != null) {
			
			campaign.setDeleted(true);
			campaign.setStatus("T");
			campaign.setName(campaign.getName() + Util.toSimpleString(new Date(), "_yyyyMMdd_HHmm"));
			
			campaign.touchWho(session);
			
			campaignDao.saveOrUpdate(campaign);
		}
	}

	@Override
	public void deleteSoftAd(AdcAd ad, HttpSession session) {
		
		if (ad != null) {
			
			ad.setDeleted(true);
			ad.setStatus("T");
			ad.setName(ad.getName() + Util.toSimpleString(new Date(), "_yyyyMMdd_HHmm"));
			
			ad.touchWho(session);
			
			adDao.saveOrUpdate(ad);
		}
	}

	@Override
	public void refreshAdCreativePeriodByAdDates(AdcAd ad, Date prevSDate, Date prevEDate) {
		
		if (ad == null || prevSDate == null || prevEDate == null || prevSDate.compareTo(prevEDate) > 0) {
			return;
		}

		if (ad.getStartDate() == null || ad.getEndDate() == null || ad.getStartDate().compareTo(ad.getEndDate()) > 0) {
			return;
		}
		
		List<AdcAdCreative> list = getAdCreativeListByAdId(ad.getId());
		if (prevSDate.compareTo(ad.getStartDate()) != 0 && prevEDate.compareTo(ad.getEndDate()) != 0) {
			// 둘 다 변경
			for(AdcAdCreative adCreative : list) {
				if (adCreative.getStartDate().compareTo(prevSDate) == 0 && adCreative.getEndDate().compareTo(prevEDate) == 0) {
					// 이전의 광고 기간과 동일하면 그대로 변경
					adCreative.setStartDate(ad.getStartDate());
					adCreative.setEndDate(ad.getEndDate());
					
					adCreativeDao.saveOrUpdate(adCreative);
				} else if (adCreative.getStartDate().compareTo(prevSDate) == 0) {
					// 이전의 광고 시작일과 동일
					if (adCreative.getEndDate().compareTo(ad.getStartDate()) >= 0) {
						adCreative.setStartDate(ad.getStartDate());
						
						adCreativeDao.saveOrUpdate(adCreative);
					}
				} else if (adCreative.getEndDate().compareTo(prevEDate) == 0) {
					// 이전의 광고 종료일과 동일
					if (adCreative.getStartDate().compareTo(ad.getEndDate()) <= 0) {
						adCreative.setEndDate(ad.getEndDate());
						
						adCreativeDao.saveOrUpdate(adCreative);
					}
				} else {
					// 이전의 광고 시작일도 다르고, 종료일도 다르네...
				}
			}
		} else if (prevSDate.compareTo(ad.getStartDate()) != 0) {
			// 시작일만 변경
			for(AdcAdCreative adCreative : list) {
				if (adCreative.getStartDate().compareTo(prevSDate) == 0) {
					// 이전의 광고 시작일과 동일
					if (adCreative.getEndDate().compareTo(ad.getStartDate()) >= 0) {
						adCreative.setStartDate(ad.getStartDate());
						
						adCreativeDao.saveOrUpdate(adCreative);
					}
				}
			}
		} else if (prevEDate.compareTo(ad.getEndDate()) != 0) {
			// 종료일만 변경
			for(AdcAdCreative adCreative : list) {
				if (adCreative.getEndDate().compareTo(prevEDate) == 0) {
					// 이전의 광고 종료일과 동일
					if (adCreative.getStartDate().compareTo(ad.getEndDate()) <= 0) {
						adCreative.setEndDate(ad.getEndDate());
						
						adCreativeDao.saveOrUpdate(adCreative);
					}
				}
			}
		}
	}

}
