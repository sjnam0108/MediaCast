package net.doohad.models.rev.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
import net.doohad.models.adc.AdcCreative;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.rev.RevAdSelect;

@Transactional
@Component
public class RevAdSelectDaoImpl implements RevAdSelectDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RevAdSelect get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevAdSelect> criteria = cb.createQuery(RevAdSelect.class);
		Root<RevAdSelect> oRoot = criteria.from(RevAdSelect.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<RevAdSelect> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RevAdSelect adSelect) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(adSelect);
	}

	@Override
	public void delete(RevAdSelect adSelect) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RevAdSelect.class, adSelect.getId()));
	}

	@Override
	public void delete(List<RevAdSelect> adSelects) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RevAdSelect adSelect : adSelects) {
            session.delete(session.load(RevAdSelect.class, adSelect.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("creative", AdcCreative.class);
		map.put("screen", InvScreen.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RevAdSelect.class, map);
	}

	@Override
	public RevAdSelect get(UUID uuid) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevAdSelect> criteria = cb.createQuery(RevAdSelect.class);
		Root<RevAdSelect> oRoot = criteria.from(RevAdSelect.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("uuid"), uuid));

		List<RevAdSelect> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<RevAdSelect> getLastListByScreenId(int screenId, int maxRecords) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevAdSelect> criteria = cb.createQuery(RevAdSelect.class);
		Root<RevAdSelect> oRoot = criteria.from(RevAdSelect.class);
		Join<RevAdSelect, InvScreen> joinO1 = oRoot.join("screen");
		
		criteria.select(oRoot);
		criteria.where(cb.equal(joinO1.get("id"), screenId));
		criteria.orderBy(cb.desc(oRoot.get("selectDate")));
		
		return sessionFactory.getCurrentSession().createQuery(criteria)
				.setMaxResults(maxRecords).getResultList();
	}

	@Override
	public List<RevAdSelect> getListByScreenId(int screenId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevAdSelect> criteria = cb.createQuery(RevAdSelect.class);
		Root<RevAdSelect> oRoot = criteria.from(RevAdSelect.class);
		Join<RevAdSelect, InvScreen> joinO1 = oRoot.join("screen");
		
		criteria.select(oRoot);
		criteria.where(cb.equal(joinO1.get("id"), screenId));
		
		return sessionFactory.getCurrentSession().createQuery(criteria)
				.getResultList();
	}

	@Override
	public List<RevAdSelect> getReportedListOrderBySelDateBeforeReportDate(Date date) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevAdSelect> criteria = cb.createQuery(RevAdSelect.class);
		Root<RevAdSelect> oRoot = criteria.from(RevAdSelect.class);
		
		criteria.select(oRoot);
		criteria.where(
				cb.isNotNull(oRoot.get("result")),
				cb.lessThan(oRoot.get("reportDate"), date)
		);
		criteria.orderBy(cb.asc(oRoot.get("selectDate")));
		
		return sessionFactory.getCurrentSession().createQuery(criteria)
				.getResultList();
	}

	@Override
	public List<RevAdSelect> getListBeforeSelectDateOrderBySelDate(Date selectDate) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevAdSelect> criteria = cb.createQuery(RevAdSelect.class);
		Root<RevAdSelect> oRoot = criteria.from(RevAdSelect.class);
		
		criteria.select(oRoot);
		criteria.where(cb.lessThan(oRoot.get("selectDate"), selectDate));
		criteria.orderBy(cb.asc(oRoot.get("selectDate")));
		
		return sessionFactory.getCurrentSession().createQuery(criteria)
				.getResultList();
	}

	@Override
	public void deleteBulkRowsInIds(List<Integer> ids) {

		Session session = sessionFactory.getCurrentSession();

		String sql = "DELETE FROM REV_AD_SELECTS WHERE AD_SELECT_ID in :ids";
		
		session.createNativeQuery(sql)
				.setParameterList("ids", ids)
				.executeUpdate();
	}

	@Override
	public List<Tuple> getHourStatTupleList1() {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT date_format(sel_date, '%Y%m%d%H'), medium_id, count(*)
		//		FROM rev_ad_selects
		//		GROUP BY date_format(sel_date, '%Y%m%d%H'), medium_id
		//
		String sql = "SELECT date_format(sel_date, '%Y%m%d%H'), medium_id, count(*) " +
					"FROM rev_ad_selects " +
					"GROUP BY date_format(sel_date, '%Y%m%d%H'), medium_id";
		
		return session.createNativeQuery(sql, Tuple.class)
				.getResultList();
	}

	@Override
	public List<Tuple> getHourStatTupleList2() {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT date_format(sel_date, '%Y%m%d%H'), count(*)
		//		FROM rev_ad_selects
		//		GROUP BY date_format(sel_date, '%Y%m%d%H')
		//      ORDER BY 1 desc
		//
		String sql = "SELECT date_format(sel_date, '%Y%m%d%H'), count(*) " +
					"FROM rev_ad_selects " +
					"GROUP BY date_format(sel_date, '%Y%m%d%H') " +
					"ORDER BY 1 desc";
		
		return session.createNativeQuery(sql, Tuple.class)
				.getResultList();
	}

	@Override
	public List<Tuple> getMediumStatTupleList() {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT a.medium_id, m.short_name, count(*) 
		//		FROM rev_ad_selects a, knl_media m
		//      WHERE a.medium_id = m.medium_id
		//		GROUP BY a.medium_id, m.short_name
		//      ORDER BY 3 desc
		//
		String sql = "SELECT a.medium_id, m.short_name, count(*) " +
					"FROM rev_ad_selects a, knl_media m " +
					"WHERE a.medium_id = m.medium_id " +
					"GROUP BY a.medium_id, m.short_name " +
					"ORDER BY 3 desc";
		
		return session.createNativeQuery(sql, Tuple.class)
				.getResultList();
	}

	@Override
	public List<Tuple> getMinStatTupleList1() {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT date_format(sel_date, '%Y%m%d%H%i'), medium_id, count(*)
		//		FROM rev_ad_selects
		//		GROUP BY date_format(sel_date, '%Y%m%d%H%i'), medium_id
		//
		String sql = "SELECT date_format(sel_date, '%Y%m%d%H%i'), medium_id, count(*) " +
					"FROM rev_ad_selects " +
					"GROUP BY date_format(sel_date, '%Y%m%d%H%i'), medium_id";
		
		return session.createNativeQuery(sql, Tuple.class)
				.getResultList();
	}

	@Override
	public List<Tuple> getMinStatTupleList2() {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT date_format(sel_date, '%Y%m%d%H%i'), count(*)
		//		FROM rev_ad_selects
		//		GROUP BY date_format(sel_date, '%Y%m%d%H%i')
		//      ORDER BY 1 desc
		//
		String sql = "SELECT date_format(sel_date, '%Y%m%d%H%i'), count(*) " +
					"FROM rev_ad_selects " +
					"GROUP BY date_format(sel_date, '%Y%m%d%H%i') " +
					"ORDER BY 1 desc";
		
		return session.createNativeQuery(sql, Tuple.class)
				.getResultList();
	}

}
