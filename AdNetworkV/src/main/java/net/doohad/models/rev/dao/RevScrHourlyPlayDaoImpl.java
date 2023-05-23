package net.doohad.models.rev.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.adc.AdcAd;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.inv.InvSite;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.rev.RevScrHourlyPlay;

@Transactional
@Component
public class RevScrHourlyPlayDaoImpl implements RevScrHourlyPlayDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RevScrHourlyPlay get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevScrHourlyPlay> criteria = cb.createQuery(RevScrHourlyPlay.class);
		Root<RevScrHourlyPlay> oRoot = criteria.from(RevScrHourlyPlay.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<RevScrHourlyPlay> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RevScrHourlyPlay hourPlay) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(hourPlay);
	}

	@Override
	public void delete(RevScrHourlyPlay hourPlay) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RevScrHourlyPlay.class, hourPlay.getId()));
	}

	@Override
	public void delete(List<RevScrHourlyPlay> hourPlays) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RevScrHourlyPlay hourPlay : hourPlays) {
            session.delete(session.load(RevScrHourlyPlay.class, hourPlay.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("ad", AdcAd.class);
		map.put("screen", InvScreen.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RevScrHourlyPlay.class, map);
	}

	@Override
	public RevScrHourlyPlay get(InvScreen screen, AdcAd ad, Date playDate) {
		
		if (screen == null || ad == null || playDate == null) {
			return null;
		}

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevScrHourlyPlay> criteria = cb.createQuery(RevScrHourlyPlay.class);
		Root<RevScrHourlyPlay> oRoot = criteria.from(RevScrHourlyPlay.class);
		Join<RevScrHourlyPlay, InvScreen> joinO1 = oRoot.join("screen");
		Join<RevScrHourlyPlay, AdcAd> joinO2 = oRoot.join("ad");
		
		criteria.select(oRoot).where(
				cb.and(
					cb.and(cb.equal(joinO1.get("id"), screen.getId()), cb.equal(oRoot.get("playDate"), playDate)),
					cb.equal(joinO2.get("id"), ad.getId()))
				);

		List<RevScrHourlyPlay> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public RevScrHourlyPlay get(int screenId, int adId, Date playDate) {
		
		if (playDate == null) {
			return null;
		}

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevScrHourlyPlay> criteria = cb.createQuery(RevScrHourlyPlay.class);
		Root<RevScrHourlyPlay> oRoot = criteria.from(RevScrHourlyPlay.class);
		Join<RevScrHourlyPlay, InvScreen> joinO1 = oRoot.join("screen");
		Join<RevScrHourlyPlay, AdcAd> joinO2 = oRoot.join("ad");
		
		criteria.select(oRoot).where(
				cb.and(
					cb.and(cb.equal(joinO1.get("id"), screenId), cb.equal(oRoot.get("playDate"), playDate)),
					cb.equal(joinO2.get("id"), adId))
				);

		List<RevScrHourlyPlay> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<RevScrHourlyPlay> getListBySiteIdAdIdPlayDate(int siteId, int adId, Date playDate) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevScrHourlyPlay> criteria = cb.createQuery(RevScrHourlyPlay.class);
		Root<RevScrHourlyPlay> oRoot = criteria.from(RevScrHourlyPlay.class);
		Join<RevScrHourlyPlay, InvScreen> joinO1 = oRoot.join("screen");
		Join<RevScrHourlyPlay, AdcAd> joinO2 = oRoot.join("ad");
		Join<InvScreen, InvSite> joinO1O = joinO1.join("site");
		
		criteria.select(oRoot).where(
				cb.and(
					cb.and(cb.equal(joinO1O.get("id"), siteId), cb.equal(oRoot.get("playDate"), playDate)),
					cb.equal(joinO2.get("id"), adId))
				);

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<Tuple> getScrSumListByPlayDate(Date playDate) {

		// 관련 SQL)
		//
		//		select screen_id, count(ad_id), 
		//		sum(cnt_00), sum(cnt_01), sum(cnt_02), sum(cnt_03), sum(cnt_04), sum(cnt_05),
		//		sum(cnt_06), sum(cnt_07), sum(cnt_08), sum(cnt_09), sum(cnt_10), sum(cnt_11),
		//		sum(cnt_12), sum(cnt_13), sum(cnt_14), sum(cnt_15), sum(cnt_16), sum(cnt_17),
		//		sum(cnt_18), sum(cnt_19), sum(cnt_20), sum(cnt_21), sum(cnt_22), sum(cnt_23),
		//		sum(succ_tot), sum(fail_tot), sum(date_tot) 
		//
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Tuple> criteria = cb.createTupleQuery();
		Root<RevScrHourlyPlay> oRoot = criteria.from(RevScrHourlyPlay.class);
		Join<RevScrHourlyPlay, InvScreen> joinO1 = oRoot.join("screen");
		Join<RevScrHourlyPlay, AdcAd> joinO2 = oRoot.join("ad");
		
		criteria.multiselect(
				joinO1.get("id"), cb.count(joinO2.get("id")),
				cb.sum(oRoot.get("cnt00")), cb.sum(oRoot.get("cnt01")), cb.sum(oRoot.get("cnt02")), 
				cb.sum(oRoot.get("cnt03")), cb.sum(oRoot.get("cnt04")), cb.sum(oRoot.get("cnt05")), 
				cb.sum(oRoot.get("cnt06")), cb.sum(oRoot.get("cnt07")), cb.sum(oRoot.get("cnt08")), 
				cb.sum(oRoot.get("cnt09")), cb.sum(oRoot.get("cnt10")), cb.sum(oRoot.get("cnt11")), 
				cb.sum(oRoot.get("cnt12")), cb.sum(oRoot.get("cnt13")), cb.sum(oRoot.get("cnt14")), 
				cb.sum(oRoot.get("cnt15")), cb.sum(oRoot.get("cnt16")), cb.sum(oRoot.get("cnt17")), 
				cb.sum(oRoot.get("cnt18")), cb.sum(oRoot.get("cnt19")), cb.sum(oRoot.get("cnt20")), 
				cb.sum(oRoot.get("cnt21")), cb.sum(oRoot.get("cnt22")), cb.sum(oRoot.get("cnt23")),
				cb.sum(oRoot.get("succTotal")), cb.sum(oRoot.get("failTotal")), cb.sum(oRoot.get("dateTotal"))
		);
		criteria.where(
				cb.equal(oRoot.get("playDate"), playDate)
		);
		criteria.groupBy(joinO1.get("id"));
		
		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<Tuple> getSitSumListByPlayDate(Date playDate) {

		// 관련 SQL)
		//
		//		select site_id, count(ad_id), 
		//		sum(cnt_00), sum(cnt_01), sum(cnt_02), sum(cnt_03), sum(cnt_04), sum(cnt_05),
		//		sum(cnt_06), sum(cnt_07), sum(cnt_08), sum(cnt_09), sum(cnt_10), sum(cnt_11),
		//		sum(cnt_12), sum(cnt_13), sum(cnt_14), sum(cnt_15), sum(cnt_16), sum(cnt_17),
		//		sum(cnt_18), sum(cnt_19), sum(cnt_20), sum(cnt_21), sum(cnt_22), sum(cnt_23),
		//		sum(succ_tot), sum(fail_tot), sum(date_tot) 
		//
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Tuple> criteria = cb.createTupleQuery();
		Root<RevScrHourlyPlay> oRoot = criteria.from(RevScrHourlyPlay.class);
		Join<RevScrHourlyPlay, InvScreen> joinO1 = oRoot.join("screen");
		Join<RevScrHourlyPlay, AdcAd> joinO2 = oRoot.join("ad");
		Join<InvScreen, InvSite> joinO3 = joinO1.join("site");
		
		criteria.multiselect(
				joinO3.get("id"), cb.countDistinct(joinO2.get("id")),
				cb.sum(oRoot.get("cnt00")), cb.sum(oRoot.get("cnt01")), cb.sum(oRoot.get("cnt02")), 
				cb.sum(oRoot.get("cnt03")), cb.sum(oRoot.get("cnt04")), cb.sum(oRoot.get("cnt05")), 
				cb.sum(oRoot.get("cnt06")), cb.sum(oRoot.get("cnt07")), cb.sum(oRoot.get("cnt08")), 
				cb.sum(oRoot.get("cnt09")), cb.sum(oRoot.get("cnt10")), cb.sum(oRoot.get("cnt11")), 
				cb.sum(oRoot.get("cnt12")), cb.sum(oRoot.get("cnt13")), cb.sum(oRoot.get("cnt14")), 
				cb.sum(oRoot.get("cnt15")), cb.sum(oRoot.get("cnt16")), cb.sum(oRoot.get("cnt17")), 
				cb.sum(oRoot.get("cnt18")), cb.sum(oRoot.get("cnt19")), cb.sum(oRoot.get("cnt20")), 
				cb.sum(oRoot.get("cnt21")), cb.sum(oRoot.get("cnt22")), cb.sum(oRoot.get("cnt23")),
				cb.sum(oRoot.get("succTotal")), cb.sum(oRoot.get("failTotal")), cb.sum(oRoot.get("dateTotal"))
		);
		criteria.where(
				cb.equal(oRoot.get("playDate"), playDate)
		);
		criteria.groupBy(joinO3.get("id"));
		
		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<Tuple> getPlayDateListByLastUpdateDate(Date date) {

		// 관련 SQL)
		//
		//		select play_date from rev_scr_hourly_plays
		//      where last_update_date >= '2023-03-27'
		//      group by play_date
		//
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Tuple> criteria = cb.createTupleQuery();
		Root<RevScrHourlyPlay> oRoot = criteria.from(RevScrHourlyPlay.class);
		
		criteria.multiselect(
				oRoot.get("playDate")
		);
		criteria.where(
				cb.greaterThanOrEqualTo(oRoot.get("whoLastUpdateDate"), date)
		);
		criteria.groupBy(oRoot.get("playDate"));
		
		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<RevScrHourlyPlay> getListByAdIdPlayDate(int adId, Date playDate) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevScrHourlyPlay> criteria = cb.createQuery(RevScrHourlyPlay.class);
		Root<RevScrHourlyPlay> oRoot = criteria.from(RevScrHourlyPlay.class);
		Join<RevScrHourlyPlay, AdcAd> joinO1 = oRoot.join("ad");
		
		criteria.select(oRoot).where(
				cb.equal(oRoot.get("playDate"), playDate),
				cb.equal(joinO1.get("id"), adId)
		);

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<Tuple> getAdStatListByScreenIdPlayDate(int screenId, Date playDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT CNT_00, CNT_01, CNT_02, CNT_03, CNT_04, CNT_05, CNT_06, CNT_07,
		//		       CNT_08, CNT_09, CNT_10, CNT_11, CNT_12, CNT_13, CNT_14, CNT_15,
		//		       CNT_16, CNT_17, CNT_18, CNT_19, CNT_20, CNT_21, CNT_22, CNT_23,
		//		       SUCC_TOT, AD_ID, CURR_HOUR_GOAL
		//		FROM REV_SCR_HOURLY_PLAYS
		//		WHERE SCREEN_ID = 11892
		//		AND PLAY_DATE = '2023-04-08'
		//
		String sql = "SELECT CNT_00, CNT_01, CNT_02, CNT_03, CNT_04, CNT_05, CNT_06, CNT_07, " +
					"CNT_08, CNT_09, CNT_10, CNT_11, CNT_12, CNT_13, CNT_14, CNT_15, " +
					"CNT_16, CNT_17, CNT_18, CNT_19, CNT_20, CNT_21, CNT_22, CNT_23, " +
					"SUCC_TOT, AD_ID, CURR_HOUR_GOAL " +
					"FROM REV_SCR_HOURLY_PLAYS " +
					"WHERE SCREEN_ID = :screenId " +
					"AND PLAY_DATE = :playDate";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("screenId", screenId)
				.setParameter("playDate", playDate)
				.getResultList();
	}

	@Override
	public List<Tuple> getStatGroupByAdPlayDate(Date playDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT ad_id,
		//		       sum(cnt_00), sum(cnt_01), sum(cnt_02), sum(cnt_03), sum(cnt_04), sum(cnt_05),
		//		       sum(cnt_06), sum(cnt_07), sum(cnt_08), sum(cnt_09), sum(cnt_10), sum(cnt_11),
		//		       sum(cnt_12), sum(cnt_13), sum(cnt_14), sum(cnt_15), sum(cnt_16), sum(cnt_17),
		//		       sum(cnt_18), sum(cnt_19), sum(cnt_20), sum(cnt_21), sum(cnt_22), sum(cnt_23),
		//		       sum(fail_tot), count(*)
		//		FROM rev_scr_hourly_plays
		//		WHERE play_date = :playDate
		//		GROUP BY ad_id
		//
		String sql = "SELECT ad_id, " +
					"sum(cnt_00), sum(cnt_01), sum(cnt_02), sum(cnt_03), sum(cnt_04), sum(cnt_05), " +
					"sum(cnt_06), sum(cnt_07), sum(cnt_08), sum(cnt_09), sum(cnt_10), sum(cnt_11), " +
					"sum(cnt_12), sum(cnt_13), sum(cnt_14), sum(cnt_15), sum(cnt_16), sum(cnt_17), " +
					"sum(cnt_18), sum(cnt_19), sum(cnt_20), sum(cnt_21), sum(cnt_22), sum(cnt_23), " +
					"sum(fail_tot), count(*) " +
					"FROM rev_scr_hourly_plays " +
					"WHERE play_date = :playDate " +
					"GROUP BY ad_id";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("playDate", playDate)
				.getResultList();
	}

	@Override
	public List<Tuple> getStatGroupByCreatPlayDate(Date playDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT ac.creative_id,
		//		       sum(cnt_00), sum(cnt_01), sum(cnt_02), sum(cnt_03), sum(cnt_04), sum(cnt_05),
		//		       sum(cnt_06), sum(cnt_07), sum(cnt_08), sum(cnt_09), sum(cnt_10), sum(cnt_11),
		//		       sum(cnt_12), sum(cnt_13), sum(cnt_14), sum(cnt_15), sum(cnt_16), sum(cnt_17),
		//		       sum(cnt_18), sum(cnt_19), sum(cnt_20), sum(cnt_21), sum(cnt_22), sum(cnt_23),
		//		       sum(fail_tot), count(*)
		//		FROM rev_scr_hourly_plays hp, adc_ad_creatives ac
		//		WHERE hp.play_date = :playDate AND hp.ad_creative_id = ac.ad_creative_id
		//		GROUP BY ac.creative_id
		//
		String sql = "SELECT ac.creative_id, " +
					"sum(cnt_00), sum(cnt_01), sum(cnt_02), sum(cnt_03), sum(cnt_04), sum(cnt_05), " +
					"sum(cnt_06), sum(cnt_07), sum(cnt_08), sum(cnt_09), sum(cnt_10), sum(cnt_11), " +
					"sum(cnt_12), sum(cnt_13), sum(cnt_14), sum(cnt_15), sum(cnt_16), sum(cnt_17), " +
					"sum(cnt_18), sum(cnt_19), sum(cnt_20), sum(cnt_21), sum(cnt_22), sum(cnt_23), " +
					"sum(fail_tot), count(*) " +
					"FROM rev_scr_hourly_plays hp, adc_ad_creatives ac " +
					"WHERE hp.play_date = :playDate AND hp.ad_creative_id = ac.ad_creative_id " +
					"GROUP BY ac.creative_id";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("playDate", playDate)
				.getResultList();
	}

}
