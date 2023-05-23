package net.doohad.models.knl.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.knl.KnlMedium;
import net.doohad.utils.Util;

@Transactional
@Component
public class KnlMediumDaoImpl implements KnlMediumDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public KnlMedium get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<KnlMedium> criteria = cb.createQuery(KnlMedium.class);
		Root<KnlMedium> oRoot = criteria.from(KnlMedium.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<KnlMedium> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(KnlMedium medium) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(medium);
	}

	@Override
	public void delete(KnlMedium medium) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(KnlMedium.class, medium.getId()));
	}

	@Override
	public void delete(List<KnlMedium> media) {
		Session session = sessionFactory.getCurrentSession();
		
        for (KnlMedium medium : media) {
            session.delete(session.load(KnlMedium.class, medium.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), KnlMedium.class);
	}

	@Override
	public KnlMedium get(String shortName) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<KnlMedium> criteria = cb.createQuery(KnlMedium.class);
		Root<KnlMedium> oRoot = criteria.from(KnlMedium.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("shortName"), shortName));

		List<KnlMedium> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public KnlMedium getByApiKey(String apiKey) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<KnlMedium> criteria = cb.createQuery(KnlMedium.class);
		Root<KnlMedium> oRoot = criteria.from(KnlMedium.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("apiKey"), apiKey));

		List<KnlMedium> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<KnlMedium> getListByShortNameLike(String shortName) {
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<KnlMedium> criteria = cb.createQuery(KnlMedium.class);
		Root<KnlMedium> oRoot = criteria.from(KnlMedium.class);

		
		if (Util.isValid(shortName)) {
			return session.createQuery(criteria.select(oRoot).where(cb.like(oRoot.get("shortName"), "%" + shortName + "%")))
					.getResultList();
		} else {
			return session.createQuery(criteria.select(oRoot)).getResultList();
		}
	}

	@Override
	public List<KnlMedium> getValidList() {
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<KnlMedium> criteria = cb.createQuery(KnlMedium.class);
		Root<KnlMedium> oRoot = criteria.from(KnlMedium.class);
		
		
		Date now = new Date();
		
		Expression<Boolean> exp1 = cb.lessThan(oRoot.get("effectiveStartDate"), now);
		Expression<Boolean> exp2 = oRoot.get("effectiveEndDate").isNull();
		Expression<Boolean> exp3 = cb.greaterThan(oRoot.get("effectiveEndDate"), now);
		
		return session.createQuery(criteria.select(oRoot).where(cb.and(exp1, cb.or(exp2, exp3)))).getResultList();
	}

}
