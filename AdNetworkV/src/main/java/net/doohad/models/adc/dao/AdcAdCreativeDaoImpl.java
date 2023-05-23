package net.doohad.models.adc.dao;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.adc.AdcAd;
import net.doohad.models.adc.AdcAdCreative;
import net.doohad.models.adc.AdcCampaign;
import net.doohad.models.adc.AdcCreative;
import net.doohad.models.knl.KnlMedium;
import net.doohad.utils.SolUtil;

@Transactional
@Component
public class AdcAdCreativeDaoImpl implements AdcAdCreativeDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public AdcAdCreative get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcAdCreative> criteria = cb.createQuery(AdcAdCreative.class);
		Root<AdcAdCreative> oRoot = criteria.from(AdcAdCreative.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<AdcAdCreative> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(AdcAdCreative adCreative) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(adCreative);
	}

	@Override
	public void delete(AdcAdCreative adCreative) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), AdcAdCreative.class, adCreative.getId());
	}

	@Override
	public void delete(List<AdcAdCreative> adCreatives) {

		Session session = sessionFactory.getCurrentSession();
		
        for (AdcAdCreative adCreative : adCreatives) {
            session.delete(session.load(AdcAdCreative.class, adCreative.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("ad", AdcAd.class);
		map.put("creative", AdcCreative.class);
		
		Criterion criterion = Restrictions.eq("deleted", false);

        return request.toDataSourceResult(sessionFactory.getCurrentSession(), AdcAdCreative.class, map, criterion);
	}

	@Override
	public List<AdcAdCreative> getListByAdId(int adId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcAdCreative> criteria = cb.createQuery(AdcAdCreative.class);
		Root<AdcAdCreative> oRoot = criteria.from(AdcAdCreative.class);
		Join<AdcAdCreative, AdcAd> joinO = oRoot.join("ad");
		
		criteria.select(oRoot).where(
				cb.and(cb.equal(joinO.get("id"), adId), cb.equal(oRoot.get("deleted"), false)));

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<AdcAdCreative> getListByCreativeId(int creativeId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcAdCreative> criteria = cb.createQuery(AdcAdCreative.class);
		Root<AdcAdCreative> oRoot = criteria.from(AdcAdCreative.class);
		Join<AdcAdCreative, AdcCreative> joinO = oRoot.join("creative");
		
		criteria.select(oRoot).where(
				cb.and(cb.equal(joinO.get("id"), creativeId), cb.equal(oRoot.get("deleted"), false)));

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public int getCountByAdId(int adId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<AdcAdCreative> oRoot = criteria.from(AdcAdCreative.class);
		Join<AdcAdCreative, AdcAd> joinO = oRoot.join("ad");
		
		criteria.select(cb.count(oRoot)).where(
				cb.and(cb.equal(joinO.get("id"), adId), cb.equal(oRoot.get("deleted"), false)));
		
		return (sessionFactory.getCurrentSession().createQuery(criteria).getSingleResult()).intValue();
	}


	@Override
	public List<AdcAdCreative> getCandiListByMediumIdDate(int mediumId, Date sDate, Date eDate) {
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<AdcAdCreative> criteria = cb.createQuery(AdcAdCreative.class);
		Root<AdcAdCreative> oRoot = criteria.from(AdcAdCreative.class);
		Join<AdcAdCreative, KnlMedium> joinO1 = oRoot.join("medium");
		Join<AdcAdCreative, AdcAd> joinO2 = oRoot.join("ad");
		Join<AdcAdCreative, AdcCreative> joinO3 = oRoot.join("creative");
		
		Expression<String> exp1 = joinO2.get("status");
		Predicate pred1 = exp1.in(Arrays.asList(new String[]{"A", "R", "C"}));
		
		
		criteria.select(oRoot).where(
				cb.equal(joinO1.get("id"), mediumId),					// 현재 매체?
				pred1,													// ad의 status가 A/R/C 중 하나?
				cb.lessThanOrEqualTo(oRoot.get("startDate"), eDate),	// adCreative의 시작/종료일 사이?
				cb.greaterThanOrEqualTo(oRoot.get("endDate"), sDate),
				cb.equal(joinO2.get("paused"), false), 					// ad의 paused == false
				cb.equal(joinO2.get("deleted"), false), 				// ad의 deleted == false
				cb.equal(joinO3.get("status"), "A"),					// creative의 status == 'A'
				cb.equal(joinO3.get("paused"), false),					// creative의 paused == false
				cb.equal(joinO3.get("deleted"), false) 					// creative의 deleted == false
		);

		return session.createQuery(criteria).getResultList();
	}

	@Override
	public List<AdcAdCreative> getPlListByMediumId(int mediumId) {
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<AdcAdCreative> criteria = cb.createQuery(AdcAdCreative.class);
		Root<AdcAdCreative> oRoot = criteria.from(AdcAdCreative.class);
		Join<AdcAdCreative, KnlMedium> joinO1 = oRoot.join("medium");
		Join<AdcAdCreative, AdcAd> joinO2 = oRoot.join("ad");
		Join<AdcAdCreative, AdcCreative> joinO3 = oRoot.join("creative");
		
		Expression<String> exp1 = joinO2.get("status");
		Predicate pred1 = exp1.in(Arrays.asList(new String[]{"A", "R", "C"}));
		
		
		criteria.select(oRoot).where(
				cb.equal(joinO1.get("id"), mediumId),					// 현재 매체?
				pred1,													// ad의 status가 A/R/C 중 하나?
				cb.equal(joinO2.get("paused"), false), 					// ad의 paused == false
				cb.equal(joinO2.get("deleted"), false), 				// ad의 deleted == false
				cb.equal(joinO3.get("status"), "A"),					// creative의 status == 'A'
				cb.equal(joinO3.get("paused"), false),					// creative의 paused == false
				cb.equal(joinO3.get("deleted"), false) 					// creative의 deleted == false
		);

		return session.createQuery(criteria).getResultList();
	}

	@Override
	public AdcAdCreative getEff(int id, int mediumId, Date sDate, Date eDate) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcAdCreative> criteria = cb.createQuery(AdcAdCreative.class);
		Root<AdcAdCreative> oRoot = criteria.from(AdcAdCreative.class);
		Join<AdcAdCreative, KnlMedium> joinO1 = oRoot.join("medium");
		Join<AdcAdCreative, AdcAd> joinO2 = oRoot.join("ad");
		Join<AdcAdCreative, AdcCreative> joinO3 = oRoot.join("creative");
		
		Expression<String> exp1 = joinO2.get("status");
		Predicate pred1 = exp1.in(Arrays.asList(new String[]{"A", "R", "C"}));
		
		
		criteria.select(oRoot).where(
				cb.equal(oRoot.get("id"), id),
				cb.equal(joinO1.get("id"), mediumId),					// 현재 매체?
				pred1,													// ad의 status가 A/R/C 중 하나?
				cb.lessThanOrEqualTo(oRoot.get("startDate"), eDate),	// adCreative의 시작/종료일 사이?
				cb.greaterThanOrEqualTo(oRoot.get("endDate"), sDate),
				cb.equal(joinO2.get("paused"), false), 					// ad의 paused == false
				cb.equal(joinO2.get("deleted"), false), 				// ad의 deleted == false
				cb.equal(joinO3.get("status"), "A"),					// creative의 status == 'A'
				cb.equal(joinO3.get("paused"), false),					// creative의 paused == false
				cb.equal(joinO3.get("deleted"), false) 					// creative의 deleted == false
		);

		List<AdcAdCreative> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<AdcAdCreative> getActiveListByAdId(int adId) {
		
		//
		// Active - status 상 문제가 없고, 삭제도 아니고, 잠시 정지도 아닌...
		//
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<AdcAdCreative> criteria = cb.createQuery(AdcAdCreative.class);
		Root<AdcAdCreative> oRoot = criteria.from(AdcAdCreative.class);
		Join<AdcAdCreative, AdcAd> joinO1 = oRoot.join("ad");
		Join<AdcAdCreative, AdcCreative> joinO2 = oRoot.join("creative");
		
		Expression<String> exp1 = joinO1.get("status");
		Predicate pred1 = exp1.in(Arrays.asList(new String[]{"A", "R", "C"}));
		
		
		criteria.select(oRoot).where(
				cb.equal(joinO1.get("id"), adId),						// ad의 Id
				pred1,													// ad의 status가 A/R/C 중 하나?
				cb.equal(joinO1.get("paused"), false), 					// ad의 paused == false
				cb.equal(joinO1.get("deleted"), false), 				// ad의 deleted == false
				cb.equal(joinO2.get("status"), "A"),					// creative의 status == 'A'
				cb.equal(joinO2.get("paused"), false),					// creative의 paused == false
				cb.equal(joinO2.get("deleted"), false) 					// creative의 deleted == false
		);

		return session.createQuery(criteria).getResultList();
	}

	@Override
	public List<AdcAdCreative> getActiveListByCampaignId(int campaignId) {
		
		//
		// Active - status 상 문제가 없고, 삭제도 아니고, 잠시 정지도 아닌...
		//
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<AdcAdCreative> criteria = cb.createQuery(AdcAdCreative.class);
		Root<AdcAdCreative> oRoot = criteria.from(AdcAdCreative.class);
		Join<AdcAdCreative, AdcAd> joinO1 = oRoot.join("ad");
		Join<AdcAdCreative, AdcCreative> joinO2 = oRoot.join("creative");
		Join<AdcAd, AdcCampaign> joinO3 = joinO1.join("campaign");
		
		Expression<String> exp1 = joinO1.get("status");
		Predicate pred1 = exp1.in(Arrays.asList(new String[]{"A", "R", "C"}));
		
		
		criteria.select(oRoot).where(
				cb.equal(joinO3.get("id"), campaignId),					// campaign의 Id
				pred1,													// ad의 status가 A/R/C 중 하나?
				cb.equal(joinO1.get("paused"), false), 					// ad의 paused == false
				cb.equal(joinO1.get("deleted"), false), 				// ad의 deleted == false
				cb.equal(joinO2.get("status"), "A"),					// creative의 status == 'A'
				cb.equal(joinO2.get("paused"), false),					// creative의 paused == false
				cb.equal(joinO2.get("deleted"), false) 					// creative의 deleted == false
		);

		return session.createQuery(criteria).getResultList();
	}


}
