package net.doohad.models.adc.dao;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
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
import net.doohad.models.adc.AdcCampaign;
import net.doohad.models.knl.KnlMedium;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;

@Transactional
@Component
public class AdcAdDaoImpl implements AdcAdDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public AdcAd get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcAd> criteria = cb.createQuery(AdcAd.class);
		Root<AdcAd> oRoot = criteria.from(AdcAd.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<AdcAd> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(AdcAd ad) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(ad);
	}

	@Override
	public void delete(AdcAd ad) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), AdcAd.class, ad.getId());
	}

	@Override
	public void delete(List<AdcAd> ads) {

		Session session = sessionFactory.getCurrentSession();
		
        for (AdcAd ad : ads) {
            session.delete(session.load(AdcAd.class, ad.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("campaign", AdcCampaign.class);
		
		Criterion criterion = Restrictions.eq("deleted", false);

        return request.toDataSourceResult(sessionFactory.getCurrentSession(), AdcAd.class, map, criterion);
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request, int campaignId) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("campaign", AdcCampaign.class);
		
		Criterion criterion = Restrictions.and(
				Restrictions.eq("deleted", false),
				Restrictions.eq("campaign.id", campaignId));

        return request.toDataSourceResult(sessionFactory.getCurrentSession(), AdcAd.class, map, criterion);
	}

	@Override
	public AdcAd get(KnlMedium medium, String name) {
		
		if (medium == null) {
			return null;
		}

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcAd> criteria = cb.createQuery(AdcAd.class);
		Root<AdcAd> oRoot = criteria.from(AdcAd.class);
		Join<AdcAd, KnlMedium> joinO = oRoot.join("medium");
		
		criteria.select(oRoot).where(
				cb.and(cb.equal(joinO.get("id"), medium.getId())), cb.equal(oRoot.get("name"), name));

		List<AdcAd> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<AdcAd> getListByMediumId(int mediumId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcAd> criteria = cb.createQuery(AdcAd.class);
		Root<AdcAd> oRoot = criteria.from(AdcAd.class);
		Join<AdcAd, KnlMedium> joinO = oRoot.join("medium");
		
		criteria.select(oRoot).where(
				cb.and(cb.equal(joinO.get("id"), mediumId), cb.equal(oRoot.get("deleted"), false)));

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<AdcAd> getListByCampaignId(int campaignId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcAd> criteria = cb.createQuery(AdcAd.class);
		Root<AdcAd> oRoot = criteria.from(AdcAd.class);
		Join<AdcAd, AdcCampaign> joinO = oRoot.join("campaign");
		
		criteria.select(oRoot).where(
				cb.and(cb.equal(joinO.get("id"), campaignId), cb.equal(oRoot.get("deleted"), false)));

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public int getCountByCampaignId(int campaignId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<AdcAd> oRoot = criteria.from(AdcAd.class);
		Join<AdcAd, AdcCampaign> joinO = oRoot.join("campaign");
		
		criteria.select(cb.count(oRoot)).where(
				cb.and(cb.equal(joinO.get("id"), campaignId), cb.equal(oRoot.get("deleted"), false)));
		
		return (sessionFactory.getCurrentSession().createQuery(criteria).getSingleResult()).intValue();
	}

	@Override
	public List<AdcAd> getList() {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcAd> criteria = cb.createQuery(AdcAd.class);
		Root<AdcAd> oRoot = criteria.from(AdcAd.class);
		
		criteria.select(oRoot);
		criteria.where(cb.equal(oRoot.get("deleted"), false));
		
		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<AdcAd> getListByMediumIdNameLike(int mediumId, String name) {
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<AdcAd> criteria = cb.createQuery(AdcAd.class);
		Root<AdcAd> oRoot = criteria.from(AdcAd.class);
		Join<AdcAd, KnlMedium> joinO = oRoot.join("medium");

		
		if (Util.isValid(name)) {
			return session.createQuery(criteria.select(oRoot).where(
					cb.and(
							cb.and(cb.equal(joinO.get("id"), mediumId), cb.like(oRoot.get("name"), "%" + name + "%")),
							cb.equal(oRoot.get("deleted"), false))))
					.getResultList();
		} else {
			return session.createQuery(criteria.select(oRoot).where(
					cb.and(cb.equal(joinO.get("id"), mediumId), cb.equal(oRoot.get("deleted"), false)))).getResultList();
		}
	}

	@Override
	public List<Tuple> getCountGroupByMediumStatus(int mediumId) {

		// 결과 예)
		//
		//     D  5
		//     P  3
		//     A  1
		//     ...
		//
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Tuple> q = cb.createTupleQuery();
		Root<AdcAd> oRoot = q.from(AdcAd.class);
		Join<AdcAd, KnlMedium> joinO = oRoot.join("medium");
		
		q.multiselect(oRoot.get("status"), cb.count(oRoot));
		q.where(
				cb.equal(joinO.get("id"), mediumId),
				cb.equal(oRoot.get("deleted"), false)
		);
		q.groupBy(oRoot.get("status"));
		
		return sessionFactory.getCurrentSession().createQuery(q).getResultList();
	}

	@Override
	public List<AdcAd> getValidList() {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcAd> criteria = cb.createQuery(AdcAd.class);
		Root<AdcAd> oRoot = criteria.from(AdcAd.class);
		Join<AdcAd, KnlMedium> joinO = oRoot.join("medium");
		
		Expression<String> exp1 = oRoot.get("status");
		
		
		Date now = new Date();
		Date today = Util.removeTimeOfDate(now);
		
		criteria.select(oRoot);
		criteria.where(
				cb.lessThanOrEqualTo(oRoot.get("startDate"), today),
				cb.greaterThanOrEqualTo(oRoot.get("endDate"), today),
				cb.equal(oRoot.get("deleted"), false),
				cb.equal(oRoot.get("paused"), false),
				exp1.in(Arrays.asList(new String[]{"A", "R", "C"})),
				cb.lessThan(joinO.get("effectiveStartDate"), now),
				cb.or(
						joinO.get("effectiveEndDate").isNull(),
						cb.greaterThan(joinO.get("effectiveEndDate"), now)
				)
		);
		
		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

}
