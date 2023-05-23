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
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.inv.InvSite;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.rev.RevSitHrlyPlyTot;

@Transactional
@Component
public class RevSitHrlyPlyTotDaoImpl implements RevSitHrlyPlyTotDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RevSitHrlyPlyTot get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevSitHrlyPlyTot> criteria = cb.createQuery(RevSitHrlyPlyTot.class);
		Root<RevSitHrlyPlyTot> oRoot = criteria.from(RevSitHrlyPlyTot.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<RevSitHrlyPlyTot> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RevSitHrlyPlyTot hrlyPlyTot) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(hrlyPlyTot);
	}

	@Override
	public void delete(RevSitHrlyPlyTot hrlyPlyTot) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RevSitHrlyPlyTot.class, hrlyPlyTot.getId()));
	}

	@Override
	public void delete(List<RevSitHrlyPlyTot> hrlyPlyTots) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RevSitHrlyPlyTot hrlyPlyTot : hrlyPlyTots) {
            session.delete(session.load(RevSitHrlyPlyTot.class, hrlyPlyTot.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request, Date playDate) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("site", InvSite.class);
		
		Criterion criterion = Restrictions.eq("playDate", playDate);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RevSitHrlyPlyTot.class, map, criterion);
	}

	@Override
	public RevSitHrlyPlyTot get(InvSite site, Date playDate) {
		
		if (site == null || playDate == null) {
			return null;
		}

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevSitHrlyPlyTot> criteria = cb.createQuery(RevSitHrlyPlyTot.class);
		Root<RevSitHrlyPlyTot> oRoot = criteria.from(RevSitHrlyPlyTot.class);
		Join<RevSitHrlyPlyTot, InvSite> joinO = oRoot.join("site");
		
		criteria.select(oRoot).where(
				cb.equal(joinO.get("id"), site.getId()), 
				cb.equal(oRoot.get("playDate"), playDate)
		);

		List<RevSitHrlyPlyTot> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<RevSitHrlyPlyTot> getListByMediumIdPlayDate(int mediumId, Date playDate) {
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<RevSitHrlyPlyTot> criteria = cb.createQuery(RevSitHrlyPlyTot.class);
		Root<RevSitHrlyPlyTot> oRoot = criteria.from(RevSitHrlyPlyTot.class);
		Join<RevSitHrlyPlyTot, KnlMedium> joinO = oRoot.join("medium");
		
		
		criteria.select(oRoot).where(
				cb.equal(joinO.get("id"), mediumId),
				cb.equal(oRoot.get("playDate"), playDate)
		);

		return session.createQuery(criteria).getResultList();
	}

	@Override
	public List<RevSitHrlyPlyTot> getListByPlayDate(Date playDate) {
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<RevSitHrlyPlyTot> criteria = cb.createQuery(RevSitHrlyPlyTot.class);
		Root<RevSitHrlyPlyTot> oRoot = criteria.from(RevSitHrlyPlyTot.class);
		
		
		criteria.select(oRoot).where(
				cb.equal(oRoot.get("playDate"), playDate)
		);

		return session.createQuery(criteria).getResultList();
	}

	@Override
	public List<Tuple> getTupleListByPlayDate(Date playDate) {
		
		Session session = sessionFactory.getCurrentSession();

		String sql = "SELECT SITE_ID AS DEST_ID, AD_CNT, CNT_00, CNT_01, CNT_02, CNT_03, CNT_04, CNT_05, " +
					"CNT_06, CNT_07, CNT_08, CNT_09, CNT_10, CNT_11, CNT_12, CNT_13, CNT_14, " +
					"CNT_15, CNT_16, CNT_17, CNT_18, CNT_19, CNT_20, CNT_21, CNT_22, CNT_23, " +
					"SUCC_TOT, FAIL_TOT, DATE_TOT, SIT_HRLY_PLY_TOT_ID AS ID " +
					"FROM REV_SIT_HRLY_PLY_TOTS " +
					"WHERE PLAY_DATE = :playDate";

		return session.createNativeQuery(sql, Tuple.class).setParameter("playDate", playDate).getResultList();
	}

	@Override
	public Tuple getStatByMediumIdPlayDate(int mediumId, Date playDate) {

		// 결과 예)
		//
		//     3318
		//
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Tuple> criteria = cb.createTupleQuery();
		Root<RevSitHrlyPlyTot> oRoot = criteria.from(RevSitHrlyPlyTot.class);
		Join<RevSitHrlyPlyTot, KnlMedium> joinO = oRoot.join("medium");
		
		criteria.multiselect(
				cb.countDistinct(oRoot.get("id"))
		);
		criteria.where(
				cb.equal(joinO.get("id"), mediumId),
				cb.equal(oRoot.get("playDate"), playDate)
		);
		
		return sessionFactory.getCurrentSession().createQuery(criteria).getSingleResult();
	}
}
