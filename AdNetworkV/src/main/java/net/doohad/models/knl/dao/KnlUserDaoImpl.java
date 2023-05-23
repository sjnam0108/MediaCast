package net.doohad.models.knl.dao;

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
import net.doohad.models.knl.KnlAccount;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.knl.KnlUser;

@Transactional
@Component
public class KnlUserDaoImpl implements KnlUserDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public KnlUser get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<KnlUser> criteria = cb.createQuery(KnlUser.class);
		Root<KnlUser> oRoot = criteria.from(KnlUser.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<KnlUser> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(KnlUser user) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(user);
	}

	@Override
	public void delete(KnlUser user) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(KnlUser.class, user.getId()));
	}

	@Override
	public void delete(List<KnlUser> users) {
		Session session = sessionFactory.getCurrentSession();
		
        for (KnlUser user : users) {
            session.delete(session.load(KnlUser.class, user.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("account", KnlAccount.class);
		
		Criterion criterion = Restrictions.eq("deleted", false);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), KnlUser.class, 
        		map, criterion);
	}

	@Override
	public KnlUser get(String shortName) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<KnlUser> criteria = cb.createQuery(KnlUser.class);
		Root<KnlUser> oRoot = criteria.from(KnlUser.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("shortName"), shortName));

		List<KnlUser> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<KnlUser> getListByMediumId(int mediumId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<KnlUser> criteria = cb.createQuery(KnlUser.class);
		Root<KnlUser> oRoot = criteria.from(KnlUser.class);
		Join<KnlUser, KnlMedium> joinO = oRoot.join("medium");
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(joinO.get("id"), mediumId),
				cb.equal(oRoot.get("deleted"), false)
		);

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<KnlUser> getM1List() {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<KnlUser> criteria = cb.createQuery(KnlUser.class);
		Root<KnlUser> oRoot = criteria.from(KnlUser.class);
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(oRoot.get("deleted"), false),
				cb.equal(oRoot.get("role"), "M1")
		);

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

}
