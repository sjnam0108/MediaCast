package net.doohad.models.fnd.dao;

import java.util.HashMap;
import java.util.List;

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
import net.doohad.models.fnd.FndPriv;
import net.doohad.models.fnd.FndUserPriv;
import net.doohad.models.knl.KnlUser;

@Transactional
@Component
public class FndUserPrivDaoImpl implements FndUserPrivDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public FndUserPriv get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<FndUserPriv> criteria = cb.createQuery(FndUserPriv.class);
		Root<FndUserPriv> oRoot = criteria.from(FndUserPriv.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<FndUserPriv> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(FndUserPriv userPriv) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(userPriv);
	}

	@Override
	public void delete(FndUserPriv userPriv) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(FndUserPriv.class, userPriv.getId()));
	}

	@Override
	public void delete(List<FndUserPriv> userPrivs) {
		Session session = sessionFactory.getCurrentSession();
		
        for (FndUserPriv userPriv : userPrivs) {
            session.delete(session.load(FndUserPriv.class, userPriv.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("user", KnlUser.class);
		map.put("priv", FndPriv.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), FndUserPriv.class, map);
	}

	@Override
	public boolean isRegistered(int userId, int privId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<FndUserPriv> criteria = cb.createQuery(FndUserPriv.class);
		Root<FndUserPriv> oRoot = criteria.from(FndUserPriv.class);
		Join<FndUserPriv, KnlUser> joinO1 = oRoot.join("user");
		Join<FndUserPriv, FndPriv> joinO2 = oRoot.join("priv");
		
		criteria.select(oRoot).where(
				cb.and(cb.equal(joinO1.get("id"), userId), cb.equal(joinO2.get("id"), privId)));

		List<FndUserPriv> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return !list.isEmpty();
	}

	@Override
	public List<FndUserPriv> getListByUserId(int userId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<FndUserPriv> criteria = cb.createQuery(FndUserPriv.class);
		Root<FndUserPriv> oRoot = criteria.from(FndUserPriv.class);
		Join<FndUserPriv, KnlUser> joinO = oRoot.join("user");
		
		criteria.select(oRoot).where(cb.equal(joinO.get("id"), userId));

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

}
