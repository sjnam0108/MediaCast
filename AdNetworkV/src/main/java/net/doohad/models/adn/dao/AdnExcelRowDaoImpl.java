package net.doohad.models.adn.dao;

import java.util.HashMap;
import java.util.List;

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
import net.doohad.models.adn.AdnExcelRow;
import net.doohad.models.knl.KnlMedium;

@Transactional
@Component
public class AdnExcelRowDaoImpl implements AdnExcelRowDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public AdnExcelRow get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdnExcelRow> criteria = cb.createQuery(AdnExcelRow.class);
		Root<AdnExcelRow> oRoot = criteria.from(AdnExcelRow.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<AdnExcelRow> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(AdnExcelRow row) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(row);
	}

	@Override
	public void delete(AdnExcelRow row) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(AdnExcelRow.class, row.getId()));
	}

	@Override
	public void delete(List<AdnExcelRow> rows) {
		Session session = sessionFactory.getCurrentSession();
		
        for (AdnExcelRow row : rows) {
            session.delete(session.load(AdnExcelRow.class, row.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request, String type) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		
		Criterion criterion = Restrictions.eq("type", type);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), AdnExcelRow.class, map, criterion);
	}

	@Override
	public List<AdnExcelRow> getListByMediumIdType(int mediumId, String type) {
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<AdnExcelRow> criteria = cb.createQuery(AdnExcelRow.class);
		Root<AdnExcelRow> oRoot = criteria.from(AdnExcelRow.class);
		Join<AdnExcelRow, KnlMedium> joinO = oRoot.join("medium");

		
		return session.createQuery(criteria.select(oRoot).where(
				cb.and(cb.equal(joinO.get("id"), mediumId), cb.equal(oRoot.get("type"), type))))
				.getResultList();
	}

}
