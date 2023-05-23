package net.doohad.models.inv.dao;

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
import net.doohad.models.inv.InvScreen;
import net.doohad.models.inv.InvSite;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgSiteCond;
import net.doohad.utils.Util;

@Transactional
@Component
public class InvScreenDaoImpl implements InvScreenDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public InvScreen get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvScreen> criteria = cb.createQuery(InvScreen.class);
		Root<InvScreen> oRoot = criteria.from(InvScreen.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<InvScreen> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(InvScreen screen) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(screen);
	}

	@Override
	public void delete(InvScreen screen) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(InvScreen.class, screen.getId()));
	}

	@Override
	public void delete(List<InvScreen> screens) {
		Session session = sessionFactory.getCurrentSession();
		
        for (InvScreen screen : screens) {
            session.delete(session.load(InvScreen.class, screen.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("site", InvSite.class);
		
		Criterion criterion = Restrictions.eq("deleted", false);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), InvScreen.class, 
        		map, criterion);
	}

	@Override
	public DataSourceResult getMonitList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("site", InvSite.class);

		
		// 모니터링 대상: 유효 기간 && activeStatus && 광고 서버에 이용
		Date time = new Date();

		Criterion restEf1 = Restrictions.lt("effectiveStartDate", time);
		Criterion restEf2 = Restrictions.isNull("effectiveEndDate");
		Criterion restEf3 = Restrictions.gt("effectiveEndDate", time);
		
		Criterion restW1 = Restrictions.eq("activeStatus", true);
		Criterion restW2 = Restrictions.eq("adServerAvailable", true);
		
		Criterion restDel = Restrictions.eq("deleted", false);
		
		Criterion criterion = Restrictions.and(
				Restrictions.and(restEf1, Restrictions.or(restEf2, restEf3)), 
				Restrictions.and(restDel, Restrictions.and(restW1, restW2)));
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), InvScreen.class, 
        		map, criterion);
	}


	@Override
	public InvScreen get(KnlMedium medium, String shortName) {
		
		if (medium == null) {
			return null;
		}

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvScreen> criteria = cb.createQuery(InvScreen.class);
		Root<InvScreen> oRoot = criteria.from(InvScreen.class);
		Join<InvScreen, KnlMedium> joinO = oRoot.join("medium");
		
		criteria.select(oRoot).where(
				cb.and(cb.equal(joinO.get("id"), medium.getId())), cb.equal(oRoot.get("shortName"), shortName));

		List<InvScreen> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<InvScreen> getListBySiteId(int siteId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvScreen> criteria = cb.createQuery(InvScreen.class);
		Root<InvScreen> oRoot = criteria.from(InvScreen.class);
		Join<InvScreen, InvSite> joinO = oRoot.join("site");
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(joinO.get("id"), siteId),
				cb.equal(oRoot.get("deleted"), false)
		);

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
		Root<InvScreen> oRoot = q.from(InvScreen.class);
		Join<InvScreen, KnlMedium> joinO1 = oRoot.join("medium");
		Join<InvScreen, InvSite> joinO2 = oRoot.join("site");
		Join<InvSite, OrgSiteCond> joinO3 = joinO2.join("siteCond");
		
		q.multiselect(joinO3.get("id"), cb.count(oRoot));
		q.where(
				cb.equal(joinO1.get("id"), mediumId),
				cb.equal(oRoot.get("deleted"), false)
		);
		q.groupBy(joinO3.get("id"));
		
		return sessionFactory.getCurrentSession().createQuery(q).getResultList();
	}

	@Override
	public List<InvScreen> getMonitListByMediumId(int mediumId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvScreen> criteria = cb.createQuery(InvScreen.class);
		Root<InvScreen> oRoot = criteria.from(InvScreen.class);
		Join<InvScreen, KnlMedium> joinO = oRoot.join("medium");

		
		Date now = new Date();
		
		Expression<Boolean> expEf1 = cb.lessThan(oRoot.get("effectiveStartDate"), now);
		Expression<Boolean> expEf2 = oRoot.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf3 = cb.greaterThan(oRoot.get("effectiveEndDate"), now);
		
		Expression<Boolean> expW1 = cb.equal(oRoot.get("activeStatus"), true);
		Expression<Boolean> expW2 = cb.equal(oRoot.get("adServerAvailable"), true);
		Expression<Boolean> expW3 = cb.equal(joinO.get("id"), mediumId);

		Expression<Boolean> expDel = cb.equal(oRoot.get("deleted"), false);

		
		criteria.select(oRoot).where(
				cb.and(
						cb.and(expEf1, cb.or(expEf2, expEf3)),
						cb.and(cb.and(expW1, expDel), cb.and(expW2, expW3)))
				);

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<InvScreen> getMonitList() {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvScreen> criteria = cb.createQuery(InvScreen.class);
		Root<InvScreen> oRoot = criteria.from(InvScreen.class);

		
		Date now = new Date();
		
		Expression<Boolean> expEf1 = cb.lessThan(oRoot.get("effectiveStartDate"), now);
		Expression<Boolean> expEf2 = oRoot.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf3 = cb.greaterThan(oRoot.get("effectiveEndDate"), now);
		
		Expression<Boolean> expW1 = cb.equal(oRoot.get("activeStatus"), true);
		Expression<Boolean> expW2 = cb.equal(oRoot.get("adServerAvailable"), true);

		Expression<Boolean> expDel = cb.equal(oRoot.get("deleted"), false);

		
		criteria.select(oRoot).where(
				cb.and(
						cb.and(expEf1, cb.or(expEf2, expEf3)),
						cb.and(expDel, cb.and(expW1, expW2)))
				);

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<Tuple> getCountGroupByMediumResolution(int mediumId) {

		// 결과 예)
		//
		//     1920x1080  5
		//     1080x1920  3
		//
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Tuple> q = cb.createTupleQuery();
		Root<InvScreen> oRoot = q.from(InvScreen.class);
		Join<InvScreen, KnlMedium> joinO = oRoot.join("medium");
		
		q.multiselect(oRoot.get("resolution"), cb.count(oRoot));
		q.where(
				cb.equal(joinO.get("id"), mediumId),
				cb.equal(oRoot.get("deleted"), false)
		);
		q.groupBy(oRoot.get("resolution"));
		
		return sessionFactory.getCurrentSession().createQuery(q).getResultList();
	}

	@Override
	public List<InvScreen> getMonitListByMediumNameLike(int mediumId, String name) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvScreen> criteria = cb.createQuery(InvScreen.class);
		Root<InvScreen> oRoot = criteria.from(InvScreen.class);
		Join<InvScreen, KnlMedium> joinO = oRoot.join("medium");

		
		Date now = new Date();
		
		Expression<Boolean> expEf1 = cb.lessThan(oRoot.get("effectiveStartDate"), now);
		Expression<Boolean> expEf2 = oRoot.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf3 = cb.greaterThan(oRoot.get("effectiveEndDate"), now);
		
		Expression<Boolean> expW1 = cb.equal(oRoot.get("activeStatus"), true);
		Expression<Boolean> expW2 = cb.equal(oRoot.get("adServerAvailable"), true);
		Expression<Boolean> expW3 = cb.equal(joinO.get("id"), mediumId);

		Expression<Boolean> expDel = cb.equal(oRoot.get("deleted"), false);
		
		
		if (Util.isValid(name)) {
			criteria.select(oRoot).where(
					cb.and(
							cb.and(expEf1, cb.or(expEf2, expEf3)),
							cb.and(cb.and(expW1, expDel), cb.and(expW2, expW3))),
					cb.like(oRoot.get("name"), "%" + name + "%")
					);
		} else {
			criteria.select(oRoot).where(
					cb.and(
							cb.and(expEf1, cb.or(expEf2, expEf3)),
							cb.and(cb.and(expW1, expDel), cb.and(expW2, expW3)))
					);
		}
		

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public InvScreen getMonit(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvScreen> criteria = cb.createQuery(InvScreen.class);
		Root<InvScreen> oRoot = criteria.from(InvScreen.class);

		
		Date now = new Date();
		
		criteria.select(oRoot).where(
				cb.equal(oRoot.get("id"), id),
				cb.lessThan(oRoot.get("effectiveStartDate"), now),
				cb.or(
						oRoot.get("effectiveEndDate").isNull(),
						cb.greaterThan(oRoot.get("effectiveEndDate"), now)
				),
				cb.equal(oRoot.get("deleted"), false),
				cb.equal(oRoot.get("activeStatus"), true),
				cb.equal(oRoot.get("adServerAvailable"), true)
		);

		
		List<InvScreen> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<Integer> getMonitIdsByMediumId(int mediumId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Integer> criteria = cb.createQuery(Integer.class);
		Root<InvScreen> oRoot = criteria.from(InvScreen.class);
		Join<InvScreen, KnlMedium> joinO = oRoot.join("medium");

		
		Date now = new Date();
		
		criteria.select(oRoot.get("id"));
		criteria.where(
				cb.lessThan(oRoot.get("effectiveStartDate"), now),
				cb.or(
						oRoot.get("effectiveEndDate").isNull(),
						cb.greaterThan(oRoot.get("effectiveEndDate"), now)
				),
				cb.equal(oRoot.get("deleted"), false),
				cb.equal(oRoot.get("activeStatus"), true),
				cb.equal(oRoot.get("adServerAvailable"), true),
				cb.equal(joinO.get("id"), mediumId)
		);
		
		return (sessionFactory.getCurrentSession().createQuery(criteria).getResultList());
	}

	@Override
	public List<Tuple> getIdResoListByScreenIdIn(List<Integer> list) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();

		CriteriaQuery<Tuple> q = cb.createTupleQuery();
		Root<InvScreen> oRoot = q.from(InvScreen.class);
		
		q.multiselect(oRoot.get("id"), oRoot.get("resolution"));
		q.where(
				oRoot.get("id").in(list)
		);

		return sessionFactory.getCurrentSession().createQuery(q).getResultList();
	}

	@Override
	public List<InvScreen> getList() {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvScreen> criteria = cb.createQuery(InvScreen.class);
		Root<InvScreen> oRoot = criteria.from(InvScreen.class);
		// site lazy loading
		oRoot.fetch("site");
		
		criteria.select(oRoot);
		
		// 상태 및 삭제 여부와 상관없이 모든 자료 Query

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public void updateLastAdReportDate(int id, Date lastAdReportDate) {
		
		Session session = sessionFactory.getCurrentSession();

		String sql = "UPDATE INV_SCREENS SET LAST_REPORT_DATE = :lastAdReportDate WHERE SCREEN_ID = :id";
		session.createNativeQuery(sql)
				.setParameter("id", id)
				.setParameter("lastAdReportDate", lastAdReportDate)
				.executeUpdate();
	}

	@Override
	public void updateLastAdRequestDate(int id, Date lastAdRequestDate) {
		
		Session session = sessionFactory.getCurrentSession();

		String sql = "UPDATE INV_SCREENS SET LAST_REQUEST_DATE = :lastAdRequestDate WHERE SCREEN_ID = :id";
		session.createNativeQuery(sql)
				.setParameter("id", id)
				.setParameter("lastAdRequestDate", lastAdRequestDate)
				.executeUpdate();
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
		
		criteria.select(cb.count(oRoot));
		criteria.where(
				cb.and(expEf1, cb.or(expEf2, expEf3)),
				cb.equal(joinO1.get("id"), mediumId),
				cb.equal(oRoot.get("activeStatus"), true),
				cb.equal(oRoot.get("deleted"), false),
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
		
		criteria.select(cb.count(oRoot));
		criteria.where(
				cb.and(expEf1, cb.or(expEf2, expEf3)),
				cb.equal(joinO1.get("id"), mediumId),
				cb.equal(oRoot.get("activeStatus"), true),
				cb.equal(oRoot.get("deleted"), false),
				cb.substring(joinO2.get("regionCode"), 1, 2).in(list)
		);
		
		return (sessionFactory.getCurrentSession().createQuery(criteria).getSingleResult()).intValue();
	}

	@Override
	public int getMonitCountByMediumScreenIdIn(int mediumId, List<Integer> list) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<InvScreen> oRoot = criteria.from(InvScreen.class);
		Join<InvScreen, KnlMedium> joinO = oRoot.join("medium");

		
		Date now = new Date();
		
		Expression<Boolean> expEf1 = cb.lessThan(oRoot.get("effectiveStartDate"), now);
		Expression<Boolean> expEf2 = oRoot.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf3 = cb.greaterThan(oRoot.get("effectiveEndDate"), now);
		
		criteria.select(cb.count(oRoot));
		criteria.where(
				cb.and(expEf1, cb.or(expEf2, expEf3)),
				cb.equal(joinO.get("id"), mediumId),
				cb.equal(oRoot.get("activeStatus"), true),
				cb.equal(oRoot.get("deleted"), false),
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
		
		criteria.select(cb.count(oRoot));
		criteria.where(
				cb.and(expEf1, cb.or(expEf2, expEf3)),
				cb.equal(joinO1.get("id"), mediumId),
				cb.equal(oRoot.get("activeStatus"), true),
				cb.equal(oRoot.get("deleted"), false),
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
		
		criteria.select(cb.count(oRoot));
		criteria.where(
				cb.and(expEf1, cb.or(expEf2, expEf3)),
				cb.equal(joinO1.get("id"), mediumId),
				cb.equal(oRoot.get("activeStatus"), true),
				cb.equal(oRoot.get("deleted"), false),
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
		
		criteria.select(oRoot.get("id"));
		criteria.where(
				cb.and(expEf1, cb.or(expEf2, expEf3)),
				cb.equal(joinO1.get("id"), mediumId),
				cb.equal(oRoot.get("activeStatus"), true),
				cb.equal(oRoot.get("deleted"), false),
				joinO2.get("regionCode").in(list)
		);
		
		return (sessionFactory.getCurrentSession().createQuery(criteria).getResultList());
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
		
		criteria.select(oRoot.get("id"));
		criteria.where(
				cb.and(expEf1, cb.or(expEf2, expEf3)),
				cb.equal(joinO1.get("id"), mediumId),
				cb.equal(oRoot.get("activeStatus"), true),
				cb.equal(oRoot.get("deleted"), false),
				cb.substring(joinO2.get("regionCode"), 1, 2).in(list)
		);
		
		return (sessionFactory.getCurrentSession().createQuery(criteria).getResultList());
	}

	@Override
	public List<Integer> getMonitIdsByMediumScreenIdIn(int mediumId, List<Integer> list) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Integer> criteria = cb.createQuery(Integer.class);
		Root<InvScreen> oRoot = criteria.from(InvScreen.class);
		Join<InvScreen, KnlMedium> joinO = oRoot.join("medium");

		
		Date now = new Date();
		
		Expression<Boolean> expEf1 = cb.lessThan(oRoot.get("effectiveStartDate"), now);
		Expression<Boolean> expEf2 = oRoot.get("effectiveEndDate").isNull();
		Expression<Boolean> expEf3 = cb.greaterThan(oRoot.get("effectiveEndDate"), now);
		
		criteria.select(oRoot.get("id"));
		criteria.where(
				cb.and(expEf1, cb.or(expEf2, expEf3)),
				cb.equal(joinO.get("id"), mediumId),
				cb.equal(oRoot.get("activeStatus"), true),
				cb.equal(oRoot.get("deleted"), false),
				oRoot.get("id").in(list)
		);
		
		return (sessionFactory.getCurrentSession().createQuery(criteria).getResultList());
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
		
		criteria.select(oRoot.get("id"));
		criteria.where(
				cb.and(expEf1, cb.or(expEf2, expEf3)),
				cb.equal(joinO1.get("id"), mediumId),
				cb.equal(oRoot.get("activeStatus"), true),
				cb.equal(oRoot.get("deleted"), false),
				joinO2.get("id").in(list)
		);
		
		return (sessionFactory.getCurrentSession().createQuery(criteria).getResultList());
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
		
		criteria.select(oRoot.get("id"));
		criteria.where(
				cb.and(expEf1, cb.or(expEf2, expEf3)),
				cb.equal(joinO1.get("id"), mediumId),
				cb.equal(oRoot.get("activeStatus"), true),
				cb.equal(oRoot.get("deleted"), false),
				joinO3.get("code").in(list)
		);
		
		return (sessionFactory.getCurrentSession().createQuery(criteria).getResultList());
	}

}
