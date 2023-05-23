package net.doohad.models.inv.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
import net.doohad.models.inv.InvScreen;
import net.doohad.models.inv.InvSite;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgSiteCond;
import net.doohad.utils.Util;

@Transactional
@Component
public class InvSiteDaoImpl implements InvSiteDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public InvSite get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvSite> criteria = cb.createQuery(InvSite.class);
		Root<InvSite> oRoot = criteria.from(InvSite.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<InvSite> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(InvSite site) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(site);
	}

	@Override
	public void delete(InvSite site) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(InvSite.class, site.getId()));
	}

	@Override
	public void delete(List<InvSite> sites) {
		Session session = sessionFactory.getCurrentSession();
		
        for (InvSite site : sites) {
            session.delete(session.load(InvSite.class, site.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("siteCond", OrgSiteCond.class);
		
		Criterion criterion = Restrictions.eq("deleted", false);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), InvSite.class, 
        		map, criterion);
	}

	@Override
	public InvSite get(KnlMedium medium, String shortName) {
		
		if (medium == null) {
			return null;
		}

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvSite> criteria = cb.createQuery(InvSite.class);
		Root<InvSite> oRoot = criteria.from(InvSite.class);
		Join<InvSite, KnlMedium> joinO = oRoot.join("medium");
		
		criteria.select(oRoot).where(
				cb.and(cb.equal(joinO.get("id"), medium.getId())), cb.equal(oRoot.get("shortName"), shortName));

		List<InvSite> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<InvSite> getListByMediumIdNameLike(int mediumId, String name) {
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<InvSite> criteria = cb.createQuery(InvSite.class);
		Root<InvSite> oRoot = criteria.from(InvSite.class);
		Join<InvSite, KnlMedium> joinO = oRoot.join("medium");

		
		if (Util.isValid(name)) {
			return session.createQuery(criteria.select(oRoot).where(
					cb.and(cb.equal(joinO.get("id"), mediumId), 
							cb.and(cb.like(oRoot.get("name"), "%" + name + "%"), cb.equal(oRoot.get("deleted"), false)))))
					.getResultList();
		} else {
			return session.createQuery(criteria.select(oRoot).where(
					cb.and(cb.equal(joinO.get("id"), mediumId), cb.equal(oRoot.get("deleted"), false)))).getResultList();
		}
	}

	@Override
	public List<InvSite> getListByMediumIdShortNameLike(int mediumId, String shortName) {
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<InvSite> criteria = cb.createQuery(InvSite.class);
		Root<InvSite> oRoot = criteria.from(InvSite.class);
		Join<InvSite, KnlMedium> joinO = oRoot.join("medium");

		
		if (Util.isValid(shortName)) {
			return session.createQuery(criteria.select(oRoot).where(
					cb.and(cb.equal(joinO.get("id"), mediumId), 
							cb.like(oRoot.get("shortName"), "%" + shortName + "%"), cb.equal(oRoot.get("deleted"), false))))
					.getResultList();
		} else {
			return session.createQuery(criteria.select(oRoot).where(
					cb.and(cb.equal(joinO.get("id"), mediumId), cb.equal(oRoot.get("deleted"), false)))).getResultList();
		}
	}

	@Override
	public List<InvSite> getList() {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvSite> criteria = cb.createQuery(InvSite.class);
		Root<InvSite> oRoot = criteria.from(InvSite.class);
		
		criteria.select(oRoot);
		
		// 상태 및 삭제 여부와 상관없이 모든 자료 Query
		
		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<Tuple> getCountGroupByMediumSiteCondId(int mediumId) {

		// 결과 예)
		//
		//     1  5
		//     2  3
		//
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Tuple> q = cb.createTupleQuery();
		Root<InvSite> oRoot = q.from(InvSite.class);
		Join<InvSite, OrgSiteCond> joinO1 = oRoot.join("siteCond");
		Join<InvSite, KnlMedium> joinO2 = oRoot.join("medium");
		
		q.multiselect(joinO1.get("id"), cb.count(oRoot));
		q.where(
				cb.equal(joinO2.get("id"), mediumId),
				cb.equal(oRoot.get("deleted"), false)
		);
		q.groupBy(joinO1.get("id"));
		
		return sessionFactory.getCurrentSession().createQuery(q).getResultList();
	}

	@Override
	public List<InvSite> getMonitList() {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvSite> criteria = cb.createQuery(InvSite.class);
		Root<InvSite> oRoot = criteria.from(InvSite.class);

		
		Date now = new Date();
		
		Expression<Boolean> expEf1 = cb.lessThan(oRoot.get("effectiveStartDate"), now);
		Expression<Boolean> expEf2 = oRoot.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf3 = cb.greaterThan(oRoot.get("effectiveEndDate"), now);
		
		Expression<Boolean> expW1 = cb.equal(oRoot.get("activeStatus"), true);

		Expression<Boolean> expDel = cb.equal(oRoot.get("deleted"), false);

		
		criteria.select(oRoot).where(
				cb.and(
						cb.and(expEf1, cb.or(expEf2, expEf3)),
						cb.and(expW1, expDel))
				);

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<InvSite> getMonitListByMediumId(int mediumId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvSite> criteria = cb.createQuery(InvSite.class);
		Root<InvSite> oRoot = criteria.from(InvSite.class);
		Join<InvSite, KnlMedium> joinO = oRoot.join("medium");

		
		Date now = new Date();
		
		Expression<Boolean> expEf1 = cb.lessThan(oRoot.get("effectiveStartDate"), now);
		Expression<Boolean> expEf2 = oRoot.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf3 = cb.greaterThan(oRoot.get("effectiveEndDate"), now);
		
		Expression<Boolean> expW1 = cb.equal(oRoot.get("activeStatus"), true);
		Expression<Boolean> expW2 = cb.equal(joinO.get("id"), mediumId);

		Expression<Boolean> expDel = cb.equal(oRoot.get("deleted"), false);

		
		criteria.select(oRoot).where(
				cb.and(
						cb.and(expEf1, cb.or(expEf2, expEf3)),
						cb.and(expDel, cb.and(expW1, expW2)))
				);

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<InvSite> getMonitListByMediumNameLike(int mediumId, String name) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvSite> criteria = cb.createQuery(InvSite.class);
		Root<InvSite> oRoot = criteria.from(InvSite.class);
		Join<InvSite, KnlMedium> joinO = oRoot.join("medium");

		
		Date now = new Date();
		
		Expression<Boolean> expEf1 = cb.lessThan(oRoot.get("effectiveStartDate"), now);
		Expression<Boolean> expEf2 = oRoot.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf3 = cb.greaterThan(oRoot.get("effectiveEndDate"), now);
		
		Expression<Boolean> expW1 = cb.equal(oRoot.get("activeStatus"), true);
		Expression<Boolean> expW2 = cb.equal(joinO.get("id"), mediumId);

		Expression<Boolean> expDel = cb.equal(oRoot.get("deleted"), false);
		
		
		if (Util.isValid(name)) {
			criteria.select(oRoot).where(
					cb.and(
							cb.and(expEf1, cb.or(expEf2, expEf3)),
							cb.and(cb.and(expW1, expDel), expW2)),
					cb.like(oRoot.get("name"), "%" + name + "%")
					);
		} else {
			criteria.select(oRoot).where(
					cb.and(
							cb.and(expEf1, cb.or(expEf2, expEf3)),
							cb.and(cb.and(expW1, expDel), expW2))
					);
		}
		

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<Tuple> getLocListBySiteIdIn(List<Integer> list) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();

		CriteriaQuery<Tuple> q = cb.createTupleQuery();
		Root<InvSite> oRoot = q.from(InvSite.class);
		
		q.multiselect(oRoot.get("name"), oRoot.get("latitude"), oRoot.get("longitude"));
		q.where(
				oRoot.get("id").in(list)
		);

		return sessionFactory.getCurrentSession().createQuery(q).getResultList();
	}

	@Override
	public List<Tuple> getLocListByVenueType(String venueType) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();

		CriteriaQuery<Tuple> q = cb.createTupleQuery();
		Root<InvSite> oRoot = q.from(InvSite.class);
		
		q.multiselect(oRoot.get("name"), oRoot.get("latitude"), oRoot.get("longitude"));
		q.where(
				cb.equal(oRoot.get("venueType"), venueType),
				cb.equal(oRoot.get("deleted"), false)
		);

		return sessionFactory.getCurrentSession().createQuery(q).getResultList();
	}

	@Override
	public int getMonitCountByMediumRegionCodeIn(int mediumId, List<String> list) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<InvScreen> oRoot = criteria.from(InvScreen.class);
		Join<InvScreen, KnlMedium> joinO1 = oRoot.join("medium");
		Join<InvScreen, InvSite> joinO2 = oRoot.join("site");

		
		Date now = new Date();
		
		Expression<Boolean> expEf1 = cb.lessThan(oRoot.get("effectiveStartDate"), now);
		Expression<Boolean> expEf2 = oRoot.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf3 = cb.greaterThan(oRoot.get("effectiveEndDate"), now);
		
		Expression<Boolean> expEf4 = cb.lessThan(joinO2.get("effectiveStartDate"), now);
		Expression<Boolean> expEf5 = joinO2.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf6 = cb.greaterThan(joinO2.get("effectiveEndDate"), now);
		
		criteria.select(cb.countDistinct(joinO2));
		criteria.where(
				cb.and(expEf1, cb.or(expEf2, expEf3)),
				cb.and(expEf4, cb.or(expEf5, expEf6)),
				cb.equal(joinO1.get("id"), mediumId),
				cb.equal(oRoot.get("activeStatus"), true),
				cb.equal(oRoot.get("deleted"), false),
				cb.equal(joinO2.get("deleted"), false),
				joinO2.get("regionCode").in(list)
		);
		
		return (sessionFactory.getCurrentSession().createQuery(criteria).getSingleResult()).intValue();
	}

	@Override
	public int getMonitCountByMediumStateCodeIn(int mediumId, List<String> list) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<InvScreen> oRoot = criteria.from(InvScreen.class);
		Join<InvScreen, KnlMedium> joinO1 = oRoot.join("medium");
		Join<InvScreen, InvSite> joinO2 = oRoot.join("site");

		
		Date now = new Date();
		
		Expression<Boolean> expEf1 = cb.lessThan(oRoot.get("effectiveStartDate"), now);
		Expression<Boolean> expEf2 = oRoot.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf3 = cb.greaterThan(oRoot.get("effectiveEndDate"), now);
		
		Expression<Boolean> expEf4 = cb.lessThan(joinO2.get("effectiveStartDate"), now);
		Expression<Boolean> expEf5 = joinO2.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf6 = cb.greaterThan(joinO2.get("effectiveEndDate"), now);
		
		criteria.select(cb.countDistinct(joinO2));
		criteria.where(
				cb.and(expEf1, cb.or(expEf2, expEf3)),
				cb.and(expEf4, cb.or(expEf5, expEf6)),
				cb.equal(joinO1.get("id"), mediumId),
				cb.equal(oRoot.get("activeStatus"), true),
				cb.equal(oRoot.get("deleted"), false),
				cb.equal(joinO2.get("deleted"), false),
				cb.substring(joinO2.get("regionCode"), 1, 2).in(list)
		);
		
		return (sessionFactory.getCurrentSession().createQuery(criteria).getSingleResult()).intValue();
	}

	@Override
	public int getMonitCountByMediumScreenIdIn(int mediumId, List<Integer> list) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<InvScreen> oRoot = criteria.from(InvScreen.class);
		Join<InvScreen, KnlMedium> joinO1 = oRoot.join("medium");
		Join<InvScreen, InvSite> joinO2 = oRoot.join("site");

		
		Date now = new Date();
		
		Expression<Boolean> expEf1 = cb.lessThan(oRoot.get("effectiveStartDate"), now);
		Expression<Boolean> expEf2 = oRoot.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf3 = cb.greaterThan(oRoot.get("effectiveEndDate"), now);
		
		Expression<Boolean> expEf4 = cb.lessThan(joinO2.get("effectiveStartDate"), now);
		Expression<Boolean> expEf5 = joinO2.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf6 = cb.greaterThan(joinO2.get("effectiveEndDate"), now);
		
		criteria.select(cb.countDistinct(joinO2));
		criteria.where(
				cb.and(expEf1, cb.or(expEf2, expEf3)),
				cb.and(expEf4, cb.or(expEf5, expEf6)),
				cb.equal(joinO1.get("id"), mediumId),
				cb.equal(oRoot.get("activeStatus"), true),
				cb.equal(oRoot.get("deleted"), false),
				cb.equal(joinO2.get("deleted"), false),
				oRoot.get("id").in(list)
		);
		
		return (sessionFactory.getCurrentSession().createQuery(criteria).getSingleResult()).intValue();
	}

	@Override
	public int getMonitCountByMediumSiteIdIn(int mediumId, List<Integer> list) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<InvScreen> oRoot = criteria.from(InvScreen.class);
		Join<InvScreen, KnlMedium> joinO1 = oRoot.join("medium");
		Join<InvScreen, InvSite> joinO2 = oRoot.join("site");

		
		Date now = new Date();
		
		Expression<Boolean> expEf1 = cb.lessThan(oRoot.get("effectiveStartDate"), now);
		Expression<Boolean> expEf2 = oRoot.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf3 = cb.greaterThan(oRoot.get("effectiveEndDate"), now);
		
		Expression<Boolean> expEf4 = cb.lessThan(joinO2.get("effectiveStartDate"), now);
		Expression<Boolean> expEf5 = joinO2.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf6 = cb.greaterThan(joinO2.get("effectiveEndDate"), now);
		
		criteria.select(cb.countDistinct(joinO2));
		criteria.where(
				cb.and(expEf1, cb.or(expEf2, expEf3)),
				cb.and(expEf4, cb.or(expEf5, expEf6)),
				cb.equal(joinO1.get("id"), mediumId),
				cb.equal(oRoot.get("activeStatus"), true),
				cb.equal(oRoot.get("deleted"), false),
				cb.equal(joinO2.get("deleted"), false),
				joinO2.get("id").in(list)
		);
		
		return (sessionFactory.getCurrentSession().createQuery(criteria).getSingleResult()).intValue();
	}

	@Override
	public int getMonitCountByMediumSiteCondCodeIn(int mediumId, List<String> list) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<InvScreen> oRoot = criteria.from(InvScreen.class);
		Join<InvScreen, KnlMedium> joinO1 = oRoot.join("medium");
		Join<InvScreen, InvSite> joinO2 = oRoot.join("site");
		Join<InvSite, OrgSiteCond> joinO3 = joinO2.join("siteCond");

		
		Date now = new Date();
		
		Expression<Boolean> expEf1 = cb.lessThan(oRoot.get("effectiveStartDate"), now);
		Expression<Boolean> expEf2 = oRoot.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf3 = cb.greaterThan(oRoot.get("effectiveEndDate"), now);
		
		Expression<Boolean> expEf4 = cb.lessThan(joinO2.get("effectiveStartDate"), now);
		Expression<Boolean> expEf5 = joinO2.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf6 = cb.greaterThan(joinO2.get("effectiveEndDate"), now);
		
		criteria.select(cb.countDistinct(joinO2));
		criteria.where(
				cb.and(expEf1, cb.or(expEf2, expEf3)),
				cb.and(expEf4, cb.or(expEf5, expEf6)),
				cb.equal(joinO1.get("id"), mediumId),
				cb.equal(oRoot.get("activeStatus"), true),
				cb.equal(oRoot.get("deleted"), false),
				cb.equal(joinO2.get("deleted"), false),
				joinO3.get("code").in(list)
		);
		
		return (sessionFactory.getCurrentSession().createQuery(criteria).getSingleResult()).intValue();
	}

	@Override
	public List<Integer> getMonitIdsByMediumRegionCodeIn(int mediumId, List<String> list) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Integer> criteria = cb.createQuery(Integer.class);
		Root<InvScreen> oRoot = criteria.from(InvScreen.class);
		Join<InvScreen, KnlMedium> joinO1 = oRoot.join("medium");
		Join<InvScreen, InvSite> joinO2 = oRoot.join("site");

		
		Date now = new Date();
		
		Expression<Boolean> expEf1 = cb.lessThan(oRoot.get("effectiveStartDate"), now);
		Expression<Boolean> expEf2 = oRoot.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf3 = cb.greaterThan(oRoot.get("effectiveEndDate"), now);
		
		Expression<Boolean> expEf4 = cb.lessThan(joinO2.get("effectiveStartDate"), now);
		Expression<Boolean> expEf5 = joinO2.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf6 = cb.greaterThan(joinO2.get("effectiveEndDate"), now);
		
		criteria.select(joinO2.get("id"));
		criteria.where(
				cb.and(expEf1, cb.or(expEf2, expEf3)),
				cb.and(expEf4, cb.or(expEf5, expEf6)),
				cb.equal(joinO1.get("id"), mediumId),
				cb.equal(oRoot.get("activeStatus"), true),
				cb.equal(oRoot.get("deleted"), false),
				cb.equal(joinO2.get("deleted"), false),
				joinO2.get("regionCode").in(list)
		);
		
		return (sessionFactory.getCurrentSession().createQuery(criteria).getResultList())
				.stream().distinct().collect(Collectors.toList());
	}

	@Override
	public List<Integer> getMonitIdsByMediumStateCodeIn(int mediumId, List<String> list) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Integer> criteria = cb.createQuery(Integer.class);
		Root<InvScreen> oRoot = criteria.from(InvScreen.class);
		Join<InvScreen, KnlMedium> joinO1 = oRoot.join("medium");
		Join<InvScreen, InvSite> joinO2 = oRoot.join("site");

		
		Date now = new Date();
		
		Expression<Boolean> expEf1 = cb.lessThan(oRoot.get("effectiveStartDate"), now);
		Expression<Boolean> expEf2 = oRoot.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf3 = cb.greaterThan(oRoot.get("effectiveEndDate"), now);
		
		Expression<Boolean> expEf4 = cb.lessThan(joinO2.get("effectiveStartDate"), now);
		Expression<Boolean> expEf5 = joinO2.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf6 = cb.greaterThan(joinO2.get("effectiveEndDate"), now);
		
		criteria.select(joinO2.get("id"));
		criteria.where(
				cb.and(expEf1, cb.or(expEf2, expEf3)),
				cb.and(expEf4, cb.or(expEf5, expEf6)),
				cb.equal(joinO1.get("id"), mediumId),
				cb.equal(oRoot.get("activeStatus"), true),
				cb.equal(oRoot.get("deleted"), false),
				cb.equal(joinO2.get("deleted"), false),
				cb.substring(joinO2.get("regionCode"), 1, 2).in(list)
		);
		
		return (sessionFactory.getCurrentSession().createQuery(criteria).getResultList())
				.stream().distinct().collect(Collectors.toList());
	}

	@Override
	public List<Integer> getMonitIdsByMediumScreenIdIn(int mediumId, List<Integer> list) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Integer> criteria = cb.createQuery(Integer.class);
		Root<InvScreen> oRoot = criteria.from(InvScreen.class);
		Join<InvScreen, KnlMedium> joinO1 = oRoot.join("medium");
		Join<InvScreen, InvSite> joinO2 = oRoot.join("site");

		
		Date now = new Date();
		
		Expression<Boolean> expEf1 = cb.lessThan(oRoot.get("effectiveStartDate"), now);
		Expression<Boolean> expEf2 = oRoot.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf3 = cb.greaterThan(oRoot.get("effectiveEndDate"), now);
		
		Expression<Boolean> expEf4 = cb.lessThan(joinO2.get("effectiveStartDate"), now);
		Expression<Boolean> expEf5 = joinO2.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf6 = cb.greaterThan(joinO2.get("effectiveEndDate"), now);
		
		criteria.select(joinO2.get("id"));
		criteria.where(
				cb.and(expEf1, cb.or(expEf2, expEf3)),
				cb.and(expEf4, cb.or(expEf5, expEf6)),
				cb.equal(joinO1.get("id"), mediumId),
				cb.equal(oRoot.get("activeStatus"), true),
				cb.equal(oRoot.get("deleted"), false),
				cb.equal(joinO2.get("deleted"), false),
				oRoot.get("id").in(list)
		);
		
		return (sessionFactory.getCurrentSession().createQuery(criteria).getResultList())
				.stream().distinct().collect(Collectors.toList());
	}

	@Override
	public List<Integer> getMonitIdsByMediumSiteIdIn(int mediumId, List<Integer> list) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Integer> criteria = cb.createQuery(Integer.class);
		Root<InvScreen> oRoot = criteria.from(InvScreen.class);
		Join<InvScreen, KnlMedium> joinO1 = oRoot.join("medium");
		Join<InvScreen, InvSite> joinO2 = oRoot.join("site");

		
		Date now = new Date();
		
		Expression<Boolean> expEf1 = cb.lessThan(oRoot.get("effectiveStartDate"), now);
		Expression<Boolean> expEf2 = oRoot.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf3 = cb.greaterThan(oRoot.get("effectiveEndDate"), now);
		
		Expression<Boolean> expEf4 = cb.lessThan(joinO2.get("effectiveStartDate"), now);
		Expression<Boolean> expEf5 = joinO2.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf6 = cb.greaterThan(joinO2.get("effectiveEndDate"), now);
		
		criteria.select(joinO2.get("id"));
		criteria.where(
				cb.and(expEf1, cb.or(expEf2, expEf3)),
				cb.and(expEf4, cb.or(expEf5, expEf6)),
				cb.equal(joinO1.get("id"), mediumId),
				cb.equal(oRoot.get("activeStatus"), true),
				cb.equal(oRoot.get("deleted"), false),
				cb.equal(joinO2.get("deleted"), false),
				joinO2.get("id").in(list)
		);
		
		return (sessionFactory.getCurrentSession().createQuery(criteria).getResultList())
				.stream().distinct().collect(Collectors.toList());
	}

	@Override
	public List<Integer> getMonitIdsByMediumSiteCondCodeIn(int mediumId, List<String> list) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Integer> criteria = cb.createQuery(Integer.class);
		Root<InvScreen> oRoot = criteria.from(InvScreen.class);
		Join<InvScreen, KnlMedium> joinO1 = oRoot.join("medium");
		Join<InvScreen, InvSite> joinO2 = oRoot.join("site");
		Join<InvSite, OrgSiteCond> joinO3 = joinO2.join("siteCond");

		
		Date now = new Date();
		
		Expression<Boolean> expEf1 = cb.lessThan(oRoot.get("effectiveStartDate"), now);
		Expression<Boolean> expEf2 = oRoot.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf3 = cb.greaterThan(oRoot.get("effectiveEndDate"), now);
		
		Expression<Boolean> expEf4 = cb.lessThan(joinO2.get("effectiveStartDate"), now);
		Expression<Boolean> expEf5 = joinO2.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf6 = cb.greaterThan(joinO2.get("effectiveEndDate"), now);
		
		criteria.select(joinO2.get("id"));
		criteria.where(
				cb.and(expEf1, cb.or(expEf2, expEf3)),
				cb.and(expEf4, cb.or(expEf5, expEf6)),
				cb.equal(joinO1.get("id"), mediumId),
				cb.equal(oRoot.get("activeStatus"), true),
				cb.equal(oRoot.get("deleted"), false),
				cb.equal(joinO2.get("deleted"), false),
				joinO3.get("code").in(list)
		);
		
		return (sessionFactory.getCurrentSession().createQuery(criteria).getResultList())
				.stream().distinct().collect(Collectors.toList());
	}

}
