package net.doohad.models.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.adc.AdcAdCreative;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgAdvertiser;
import net.doohad.models.org.OrgMediumOpt;
import net.doohad.models.org.OrgPlTarget;
import net.doohad.models.org.OrgPlaylist;
import net.doohad.models.org.OrgSiteCond;
import net.doohad.models.org.dao.OrgAdvertiserDao;
import net.doohad.models.org.dao.OrgMediumOptDao;
import net.doohad.models.org.dao.OrgPlTargetDao;
import net.doohad.models.org.dao.OrgPlaylistDao;
import net.doohad.models.org.dao.OrgSiteCondDao;
import net.doohad.utils.Util;

@Transactional
@Service("orgService")
public class OrgServiceImpl implements OrgService {

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
    private OrgAdvertiserDao advertiserDao;

    @Autowired
    private OrgSiteCondDao siteCondDao;

    @Autowired
    private OrgMediumOptDao mediumOptDao;

    @Autowired
    private OrgPlaylistDao playlistDao;

    @Autowired
    private OrgPlTargetDao plTargetDao;

    @Autowired
    private InvService invService;

    @Autowired
    private AdcService adcService;

    
    
	//
	// for OrgAdvertiserDao
	//
	@Override
	public OrgAdvertiser getAdvertiser(int id) {
		return advertiserDao.get(id);
	}

	@Override
	public void saveOrUpdate(OrgAdvertiser advertiser) {
		advertiserDao.saveOrUpdate(advertiser);
	}

	@Override
	public void deleteAdvertiser(OrgAdvertiser advertiser) {
		advertiserDao.delete(advertiser);
	}

	@Override
	public void deleteAdvertisers(List<OrgAdvertiser> advertisers) {
		advertiserDao.delete(advertisers);
	}

	@Override
	public DataSourceResult getAdvertiserList(DataSourceRequest request) {
		return advertiserDao.getList(request);
	}

	@Override
	public OrgAdvertiser getAdvertiser(KnlMedium medium, String name) {
		return advertiserDao.get(medium, name);
	}

	@Override
	public List<OrgAdvertiser> getAdvertiserListByMediumId(int mediumId) {
		return advertiserDao.getListByMediumId(mediumId);
	}

    
    
	//
	// for OrgSiteCondDao
	//
	@Override
	public OrgSiteCond getSiteCond(int id) {
		return siteCondDao.get(id);
	}

	@Override
	public void saveOrUpdate(OrgSiteCond siteCond) {
		siteCondDao.saveOrUpdate(siteCond);
	}

	@Override
	public void deleteSiteCond(OrgSiteCond siteCond) {
		siteCondDao.delete(siteCond);
	}

	@Override
	public void deleteSiteConds(List<OrgSiteCond> siteConds) {
		siteCondDao.delete(siteConds);
	}

	@Override
	public DataSourceResult getSiteCondList(DataSourceRequest request) {
		return siteCondDao.getList(request);
	}

	@Override
	public OrgSiteCond getSiteCond(KnlMedium medium, String code) {
		return siteCondDao.get(medium, code);
	}

	@Override
	public List<OrgSiteCond> getSiteCondListByMediumId(int mediumId) {
		return siteCondDao.getListByMediumId(mediumId);
	}

	@Override
	public List<OrgSiteCond> getSiteCondListByMediumIdActiveStatus(int mediumId, boolean activeStatus) {
		return siteCondDao.getListByMediumIdActiveStatus(mediumId, activeStatus);
	}

	@Override
	public List<OrgSiteCond> getSiteCondListByMediumIdNameLike(int mediumId, String name) {
		return siteCondDao.getListByMediumIdNameLike(mediumId, name);
	}

    
    
	//
	// for OrgMediumOptDao
	//
	@Override
	public OrgMediumOpt getMediumOpt(int id) {
		return mediumOptDao.get(id);
	}

	@Override
	public void saveOrUpdate(OrgMediumOpt mediumOpt) {
		mediumOptDao.saveOrUpdate(mediumOpt);
	}

	@Override
	public void deleteMediumOpt(OrgMediumOpt mediumOpt) {
		mediumOptDao.delete(mediumOpt);
	}

	@Override
	public void deleteMediumOpts(List<OrgMediumOpt> mediumOpts) {
		mediumOptDao.delete(mediumOpts);
	}

	@Override
	public OrgMediumOpt getMediumOpt(KnlMedium medium, String code) {
		return mediumOptDao.get(medium, code);
	}

	@Override
	public List<OrgMediumOpt> getMediumOptListByMediumId(int mediumId) {
		return mediumOptDao.getListByMediumId(mediumId);
	}

	@Override
	public String getMediumOptValue(int mediumId, String code) {
		return mediumOptDao.getValue(mediumId, code);
	}

    
    
	//
	// for OrgPlaylistDao
	//
	@Override
	public OrgPlaylist getPlaylist(int id) {
		return playlistDao.get(id);
	}

	@Override
	public void saveOrUpdate(OrgPlaylist playlist) {
		playlistDao.saveOrUpdate(playlist);
	}

	@Override
	public void deletePlaylist(OrgPlaylist playlist) {
		playlistDao.delete(playlist);
	}

	@Override
	public void deletePlaylists(List<OrgPlaylist> playlists) {
		playlistDao.delete(playlists);
	}

	@Override
	public DataSourceResult getPlaylistList(DataSourceRequest request) {
		return playlistDao.getList(request);
	}

	@Override
	public OrgPlaylist getPlaylist(KnlMedium medium, String name) {
		return playlistDao.get(medium, name);
	}

	@Override
	public List<OrgPlaylist> getPlaylistListByMediumId(int mediumId) {
		return playlistDao.getListByMediumId(mediumId);
	}

	@Override
	public void saveAndReorderPlaylist(OrgPlaylist playlist) {
		playlistDao.saveAndReorder(playlist);
	}

	@Override
	public List<OrgPlaylist> getEffPlaylistListByMediumId(int mediumId) {
		return playlistDao.getEffListByMediumId(mediumId);
	}

	@Override
	public List<OrgPlaylist> getEffPlaylistListByMediumIdDate(int mediumId, Date sDate, Date eDate) {
		return playlistDao.getEffListByMediumIdDate(mediumId, sDate, eDate);
	}

    
    
	//
	// for OrgPlTargetDao
	//
	@Override
	public OrgPlTarget getPlTarget(int id) {
		return plTargetDao.get(id);
	}

	@Override
	public void saveOrUpdate(OrgPlTarget plTarget) {
		plTargetDao.saveOrUpdate(plTarget);
	}

	@Override
	public void deletePlTarget(OrgPlTarget plTarget) {
		plTargetDao.delete(plTarget);
	}

	@Override
	public void deletePlTargets(List<OrgPlTarget> plTargets) {
		plTargetDao.delete(plTargets);
	}

	@Override
	public DataSourceResult getPlTargetList(DataSourceRequest request) {
		return plTargetDao.getList(request);
	}

	@Override
	public List<OrgPlTarget> getPlTargetListByPlaylistId(int playlistId) {
		return plTargetDao.getListByPlaylistId(playlistId);
	}

	@Override
	public void saveAndReorderPlTarget(OrgPlTarget plTarget) {
		plTargetDao.saveAndReorder(plTarget);
	}

    
    
	//
	// for Common
	//
	@Override
	public OrgPlaylist getPlaylistByScreenId(int screenId, int mediumId) {
		
		//
		// step 1. 매체 화면은 모니터링이 가능한 상태(활성화, 유효성 등)여야 한다
		//         이 참에 매체 소속인가도 확인
		//
		InvScreen screen = invService.getMonitScreen(screenId);
		if (screen == null || screen.getMedium().getId() != mediumId) {
			return null;
		}
		
		//
		// step 2. 매체에 설정된 재생목록을 순서대로 검사
		//         1) 재생 목록의 기간 검사
		//         2) 포함 광고의 유효한 광고가 하나라도 존재하는가?
		//
		List<OrgPlaylist> playlistList = getEffPlaylistListByMediumId(mediumId);
		Date today = Util.removeTimeOfDate(new Date());
		
		// 가능한 광고/광고 소재 자료를 미리 확인
		List<AdcAdCreative> list = adcService.getPlAdCreativeListByMediumId(mediumId);
		ArrayList<String> validIds = new ArrayList<String>();
		
		for(AdcAdCreative adCreat : list) {
			if (Util.isBetween(today, adCreat.getStartDate(), adCreat.getEndDate())) {
				validIds.add("A" + adCreat.getId());
			}
		}
		
		for(OrgPlaylist playlist : playlistList) {
			boolean goAhead = false;
			List<String> adCreatIds = Util.tokenizeValidStr(playlist.getAdValue());
			for(String s : adCreatIds) {
				if (validIds.contains("A" + s)) {
					goAhead = true;
					break;
				}
			}
			if (!goAhead) {
				continue;
			}
			
			List<OrgPlTarget> tgtList = getPlTargetListByPlaylistId(playlist.getId());
			if (tgtList.size() == 0) {
				return playlist;
			}
			
			// 재생 목록에 대한 타겟팅은 이후에 진행
		}
		
		return null;
	}

	@Override
	public HashMap<String, Integer> getPlaylistScreenCountByMediumId(int mediumId) {
		
		HashMap<String, Integer> map = new HashMap<String, Integer>();

		List<Integer> currIds = new ArrayList<Integer>();
		List<Integer> resultIds = null;
		
		List<OrgPlaylist> playlistList = getEffPlaylistListByMediumId(mediumId);
		resultIds = invService.getMonitScreenIdsByMediumId(mediumId);
		
		for(OrgPlaylist playlist : playlistList) {
			currIds = invService.getTargetScreenOrAllIdsByPlaylistId(playlist.getId(), mediumId);
			
			currIds = Util.intersection(resultIds, currIds);
			resultIds.removeAll(currIds);

			map.put("P" + playlist.getId(), currIds.size());

			if (resultIds.size() == 0) {
				break;
			}
		}
		
		return map;
	}

}
