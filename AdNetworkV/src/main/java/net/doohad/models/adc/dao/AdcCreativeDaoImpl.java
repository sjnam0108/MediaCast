package net.doohad.models.adc.dao;

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
import net.doohad.models.adc.AdcCreative;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgAdvertiser;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;

@Transactional
@Component
public class AdcCreativeDaoImpl implements AdcCreativeDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public AdcCreative get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcCreative> criteria = cb.createQuery(AdcCreative.class);
		Root<AdcCreative> oRoot = criteria.from(AdcCreative.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<AdcCreative> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(AdcCreative creative) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(creative);
	}

	@Override
	public void delete(AdcCreative creative) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), AdcCreative.class, creative.getId());
	}

	@Override
	public void delete(List<AdcCreative> creatives) {

		Session session = sessionFactory.getCurrentSession();
		
        for (AdcCreative creative : creatives) {
            session.delete(session.load(AdcCreative.class, creative.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("advertiser", OrgAdvertiser.class);
		
		Criterion criterion = Restrictions.eq("deleted", false);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), AdcCreative.class, 
        		map, criterion);
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request, int advertiserId) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("advertiser", OrgAdvertiser.class);
		
		Criterion criterion = Restrictions.and(
				Restrictions.eq("deleted", false),
				Restrictions.eq("advertiser.id", advertiserId));
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), AdcCreative.class, 
        		map, criterion);
	}

	@Override
	public DataSourceResult getPendApprList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("advertiser", OrgAdvertiser.class);
		
		Criterion restW1 = Restrictions.eq("status", "P");
		Criterion restW2 = Restrictions.eq("deleted", false);
		
		Criterion criterion = Restrictions.and(restW1, restW2);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), AdcCreative.class, 
        		map, criterion);
	}

	@Override
	public List<AdcCreative> getListByMediumIdNameLike(int mediumId, String name) {
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<AdcCreative> criteria = cb.createQuery(AdcCreative.class);
		Root<AdcCreative> oRoot = criteria.from(AdcCreative.class);
		Join<AdcCreative, KnlMedium> joinO = oRoot.join("medium");

		
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
	public List<Tuple> getCountGroupByMediumAdvertiserId(int mediumId) {

		// 결과 예)
		//
		//     1  5
		//     2  3
		//
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Tuple> q = cb.createTupleQuery();
		Root<AdcCreative> oRoot = q.from(AdcCreative.class);
		Join<AdcCreative, OrgAdvertiser> joinO1 = oRoot.join("advertiser");
		Join<AdcCreative, KnlMedium> joinO2 = oRoot.join("medium");
		
		q.multiselect(joinO1.get("id"), cb.count(oRoot));
		q.where(
				cb.equal(joinO2.get("id"), mediumId),
				cb.equal(oRoot.get("deleted"), false)
		);
		q.groupBy(joinO1.get("id"));
		
		return sessionFactory.getCurrentSession().createQuery(q).getResultList();
	}

	@Override
	public List<AdcCreative> getListByAdvertiserId(int advertiserId) {
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<AdcCreative> criteria = cb.createQuery(AdcCreative.class);
		Root<AdcCreative> oRoot = criteria.from(AdcCreative.class);
		Join<AdcCreative, OrgAdvertiser> joinO = oRoot.join("advertiser");


		return session.createQuery(criteria.select(oRoot).where(
				cb.and(cb.equal(joinO.get("id"), advertiserId),
						cb.equal(oRoot.get("deleted"), false))))
				.getResultList();
	}

	@Override
	public List<AdcCreative> getValidList() {
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<AdcCreative> criteria = cb.createQuery(AdcCreative.class);
		Root<AdcCreative> oRoot = criteria.from(AdcCreative.class);
		Join<AdcCreative, KnlMedium> joinO = oRoot.join("medium");
		
		
		Date now = new Date();
		
		Expression<Boolean> exp1 = cb.lessThan(joinO.get("effectiveStartDate"), now);
		Expression<Boolean> exp2 = joinO.get("effectiveEndDate").isNull();
		Expression<Boolean> exp3 = cb.greaterThan(joinO.get("effectiveEndDate"), now);

		
		criteria.select(oRoot);
		criteria.where(
				cb.and(exp1, cb.or(exp2, exp3)),
				cb.equal(oRoot.get("deleted"), false),
				cb.equal(oRoot.get("paused"), false),
				cb.equal(oRoot.get("status"), "A")
		);
		

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<AdcCreative> getValidFallbackListByMediumId(int mediumId) {
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<AdcCreative> criteria = cb.createQuery(AdcCreative.class);
		Root<AdcCreative> oRoot = criteria.from(AdcCreative.class);
		Join<AdcCreative, KnlMedium> joinO = oRoot.join("medium");
		
		
		Date now = new Date();
		
		Expression<Boolean> exp1 = cb.lessThan(joinO.get("effectiveStartDate"), now);
		Expression<Boolean> exp2 = joinO.get("effectiveEndDate").isNull();
		Expression<Boolean> exp3 = cb.greaterThan(joinO.get("effectiveEndDate"), now);

		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(joinO.get("id"), mediumId),
				cb.and(exp1, cb.or(exp2, exp3)),
				cb.equal(oRoot.get("deleted"), false),
				cb.equal(oRoot.get("paused"), false),
				cb.equal(oRoot.get("status"), "A"),
				cb.equal(oRoot.get("type"), "F")
		);
		

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<AdcCreative> getListByMediumIdName(int mediumId, String name) {
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<AdcCreative> criteria = cb.createQuery(AdcCreative.class);
		Root<AdcCreative> oRoot = criteria.from(AdcCreative.class);
		Join<AdcCreative, KnlMedium> joinO = oRoot.join("medium");
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(joinO.get("id"), mediumId),
				cb.equal(oRoot.get("name"), name)
		);
		criteria.orderBy(cb.desc(oRoot.get("whoCreationDate")));

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public int getCountByAdvertiserId(int advertiserId) {
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<AdcCreative> oRoot = criteria.from(AdcCreative.class);
		Join<AdcCreative, OrgAdvertiser> joinO = oRoot.join("advertiser");
		
		criteria.select(cb.count(oRoot));
		criteria.where(
				cb.equal(joinO.get("id"), advertiserId),
				cb.equal(oRoot.get("deleted"), false)
		);

		return sessionFactory.getCurrentSession().createQuery(criteria).getSingleResult().intValue();
	}

}
