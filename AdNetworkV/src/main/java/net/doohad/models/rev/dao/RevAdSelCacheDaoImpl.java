package net.doohad.models.rev.dao;

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
import net.doohad.models.adc.AdcAdCreative;
import net.doohad.models.adc.AdcCreative;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.rev.RevAdSelCache;

@Transactional
@Component
public class RevAdSelCacheDaoImpl implements RevAdSelCacheDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RevAdSelCache get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevAdSelCache> criteria = cb.createQuery(RevAdSelCache.class);
		Root<RevAdSelCache> oRoot = criteria.from(RevAdSelCache.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<RevAdSelCache> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RevAdSelCache adSelCache) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(adSelCache);
	}

	@Override
	public void delete(RevAdSelCache adSelCache) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RevAdSelCache.class, adSelCache.getId()));
	}

	@Override
	public void delete(List<RevAdSelCache> adSelCaches) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RevAdSelCache adSelCache : adSelCaches) {
            session.delete(session.load(RevAdSelCache.class, adSelCache.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("creative", AdcCreative.class);
		map.put("screen", InvScreen.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RevAdSelCache.class, map);
	}

	@Override
	public RevAdSelCache getLastByScreenIdAdCreativeId(int screenId, int adCreativeId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevAdSelCache> criteria = cb.createQuery(RevAdSelCache.class);
		Root<RevAdSelCache> oRoot = criteria.from(RevAdSelCache.class);
		Join<RevAdSelCache, InvScreen> joinO1 = oRoot.join("screen");
		Join<RevAdSelCache, AdcAdCreative> joinO2 = oRoot.join("adCreative");
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(joinO1.get("id"), screenId),
				cb.equal(joinO2.get("id"), adCreativeId)
		);
		criteria.orderBy(cb.desc(oRoot.get("selectDate")));
		
		List<RevAdSelCache> list = sessionFactory.getCurrentSession().createQuery(criteria)
				.getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public Tuple getLastTupleByScreenId(int screenId) {
		
		Session session = sessionFactory.getCurrentSession();

		String sql = "SELECT AD_SEL_CACHE_ID, SEL_DATE, AD_CREATIVE_ID " +
					"FROM REV_AD_SEL_CACHES " +
					"WHERE SCREEN_ID = :screenId " +
					"ORDER BY SEL_DATE DESC LIMIT 0, 1";
		
		
		List<Tuple> list = session.createNativeQuery(sql, Tuple.class)
				.setParameter("screenId", screenId).getResultList();
				
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public Tuple getLastTupleByScreenIdAdId(int screenId, int adId) {
		
		Session session = sessionFactory.getCurrentSession();

		String sql = "SELECT CCH.AD_SEL_CACHE_ID, CCH.SEL_DATE, CCH.AD_CREATIVE_ID " +
					"FROM REV_AD_SEL_CACHES CCH, ADC_AD_CREATIVES AC " +
					"WHERE CCH.SCREEN_ID = :screenId AND AC.AD_ID = :adId " +
					"AND CCH.AD_CREATIVE_ID = AC.AD_CREATIVE_ID " +
					"ORDER BY SEL_DATE DESC LIMIT 0, 1";
		
		
		List<Tuple> list = session.createNativeQuery(sql, Tuple.class)
				.setParameter("screenId", screenId)
				.setParameter("adId", adId)
				.getResultList();
				
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public Tuple getLastTupleByScreenIdAdvertiserId(int screenId, int advertiserId) {
		
		Session session = sessionFactory.getCurrentSession();

		String sql = "SELECT CCH.AD_SEL_CACHE_ID, CCH.SEL_DATE, CCH.AD_CREATIVE_ID, C.ADVERTISER_ID " +
					"FROM REV_AD_SEL_CACHES CCH, ADC_AD_CREATIVES AC, ADC_CREATIVES C " +
					"WHERE CCH.SCREEN_ID = :screenId AND C.ADVERTISER_ID = :advertiserId " +
					"AND CCH.AD_CREATIVE_ID = AC.AD_CREATIVE_ID AND AC.CREATIVE_ID = C.CREATIVE_ID " +
					"ORDER BY CCH.SEL_DATE DESC LIMIT 0, 1";
		
		
		List<Tuple> list = session.createNativeQuery(sql, Tuple.class)
				.setParameter("screenId", screenId)
				.setParameter("advertiserId", advertiserId)
				.getResultList();
				
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<Tuple> getTupleListByScreenId(int screenId) {
		
		Session session = sessionFactory.getCurrentSession();

		String sql = "SELECT CCH.AD_SEL_CACHE_ID, CCH.SEL_DATE, CCH.AD_CREATIVE_ID, AC.AD_ID, C.ADVERTISER_ID " +
					"FROM REV_AD_SEL_CACHES CCH, ADC_AD_CREATIVES AC, ADC_CREATIVES C " +
					"WHERE CCH.SCREEN_ID = :screenId " +
					"AND CCH.AD_CREATIVE_ID = AC.AD_CREATIVE_ID AND AC.CREATIVE_ID = C.CREATIVE_ID " +
					"ORDER BY CCH.SEL_DATE DESC";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("screenId", screenId)
				.getResultList();
	}

}
