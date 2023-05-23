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
import net.doohad.models.inv.InvScreen;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.rev.RevScrHrlyPlyTot;

@Transactional
@Component
public class RevScrHrlyPlyTotDaoImpl implements RevScrHrlyPlyTotDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RevScrHrlyPlyTot get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevScrHrlyPlyTot> criteria = cb.createQuery(RevScrHrlyPlyTot.class);
		Root<RevScrHrlyPlyTot> oRoot = criteria.from(RevScrHrlyPlyTot.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<RevScrHrlyPlyTot> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RevScrHrlyPlyTot hrlyPlyTot) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(hrlyPlyTot);
	}

	@Override
	public void delete(RevScrHrlyPlyTot hrlyPlyTot) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RevScrHrlyPlyTot.class, hrlyPlyTot.getId()));
	}

	@Override
	public void delete(List<RevScrHrlyPlyTot> hrlyPlyTots) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RevScrHrlyPlyTot hrlyPlyTot : hrlyPlyTots) {
            session.delete(session.load(RevScrHrlyPlyTot.class, hrlyPlyTot.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request, Date playDate) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("screen", InvScreen.class);
		
		Criterion criterion = Restrictions.eq("playDate", playDate);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RevScrHrlyPlyTot.class, map, criterion);
	}

	@Override
	public RevScrHrlyPlyTot get(InvScreen screen, Date playDate) {
		
		if (screen == null || playDate == null) {
			return null;
		}

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevScrHrlyPlyTot> criteria = cb.createQuery(RevScrHrlyPlyTot.class);
		Root<RevScrHrlyPlyTot> oRoot = criteria.from(RevScrHrlyPlyTot.class);
		Join<RevScrHrlyPlyTot, InvScreen> joinO = oRoot.join("screen");
		
		criteria.select(oRoot).where(
				cb.equal(joinO.get("id"), screen.getId()), 
				cb.equal(oRoot.get("playDate"), playDate)
		);

		List<RevScrHrlyPlyTot> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<RevScrHrlyPlyTot> getListByMediumIdPlayDate(int mediumId, Date playDate) {
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<RevScrHrlyPlyTot> criteria = cb.createQuery(RevScrHrlyPlyTot.class);
		Root<RevScrHrlyPlyTot> oRoot = criteria.from(RevScrHrlyPlyTot.class);
		Join<RevScrHrlyPlyTot, KnlMedium> joinO = oRoot.join("medium");
		
		
		criteria.select(oRoot).where(
				cb.equal(joinO.get("id"), mediumId),
				cb.equal(oRoot.get("playDate"), playDate)
		);

		return session.createQuery(criteria).getResultList();
	}

	@Override
	public List<RevScrHrlyPlyTot> getListByPlayDate(Date playDate) {
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<RevScrHrlyPlyTot> criteria = cb.createQuery(RevScrHrlyPlyTot.class);
		Root<RevScrHrlyPlyTot> oRoot = criteria.from(RevScrHrlyPlyTot.class);
		
		
		criteria.select(oRoot).where(
				cb.equal(oRoot.get("playDate"), playDate)
		);

		return session.createQuery(criteria).getResultList();
	}

	@Override
	public List<Tuple> getTupleListByPlayDate(Date playDate) {
		
		Session session = sessionFactory.getCurrentSession();

		String sql = "SELECT SCREEN_ID AS DEST_ID, AD_CNT, CNT_00, CNT_01, CNT_02, CNT_03, CNT_04, CNT_05, " +
					"CNT_06, CNT_07, CNT_08, CNT_09, CNT_10, CNT_11, CNT_12, CNT_13, CNT_14, " +
					"CNT_15, CNT_16, CNT_17, CNT_18, CNT_19, CNT_20, CNT_21, CNT_22, CNT_23, " +
					"SUCC_TOT, FAIL_TOT, DATE_TOT, SCR_HRLY_PLY_TOT_ID AS ID " +
					"FROM REV_SCR_HRLY_PLY_TOTS " +
					"WHERE PLAY_DATE = :playDate";

		return session.createNativeQuery(sql, Tuple.class).setParameter("playDate", playDate).getResultList();
	}

	@Override
	public Tuple getStatByMediumIdPlayDate(int mediumId, Date playDate) {

		// 결과 예)
		//
		//     4034874    4025018    9856    3318
		//
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Tuple> criteria = cb.createTupleQuery();
		Root<RevScrHrlyPlyTot> oRoot = criteria.from(RevScrHrlyPlyTot.class);
		Join<RevScrHrlyPlyTot, KnlMedium> joinO = oRoot.join("medium");
		
		criteria.multiselect(
				cb.sum(oRoot.get("dateTotal")), cb.sum(oRoot.get("succTotal")), cb.sum(oRoot.get("failTotal")),
				cb.countDistinct(oRoot.get("id")),
				cb.avg(oRoot.get("dateTotal")), cb.avg(oRoot.get("succTotal")), cb.avg(oRoot.get("failTotal"))
		);
		criteria.where(
				cb.equal(joinO.get("id"), mediumId),
				cb.equal(oRoot.get("playDate"), playDate)
		);
		
		return sessionFactory.getCurrentSession().createQuery(criteria).getSingleResult();
	}

	@Override
	public Double getStdByMediumIdPlayDate(int mediumId, Date playDate) {
		
		Session session = sessionFactory.getCurrentSession();

		String sql = "SELECT STD(DATE_TOT) " +
					"FROM REV_SCR_HRLY_PLY_TOTS " +
					"WHERE MEDIUM_ID = :mediumId AND PLAY_DATE = :playDate";

		Tuple tuple = session.createNativeQuery(sql, Tuple.class).setParameter("mediumId", mediumId)
				.setParameter("playDate", playDate).getSingleResult();
		
		return (Double) tuple.get(0);
	}

	@Override
	public Tuple getAvgByMediumIdBetweenPlayDates(int mediumId, Date date1, Date date2) {
		
		Session session = sessionFactory.getCurrentSession();

		String sql = "SELECT AVG(DATE_TOT), AVG(FAIL_TOT) " +
					"FROM REV_SCR_HRLY_PLY_TOTS " +
					"WHERE MEDIUM_ID = :mediumId AND PLAY_DATE BETWEEN :date1 AND :date2";
		
		return session.createNativeQuery(sql, Tuple.class).setParameter("mediumId", mediumId)
				.setParameter("date1", date1).setParameter("date2", date2).getSingleResult();
	}

}
