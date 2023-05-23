package net.doohad.models.fnd.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.fnd.FndPriv;

@Transactional
@Component
public class FndPrivDaoImpl implements FndPrivDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public FndPriv get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<FndPriv> criteria = cb.createQuery(FndPriv.class);
		Root<FndPriv> oRoot = criteria.from(FndPriv.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<FndPriv> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(FndPriv priv) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(priv);
	}

	@Override
	public void delete(FndPriv priv) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(FndPriv.class, priv.getId()));
	}

	@Override
	public void delete(List<FndPriv> privs) {
		Session session = sessionFactory.getCurrentSession();
		
        for (FndPriv priv : privs) {
            session.delete(session.load(FndPriv.class, priv.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), FndPriv.class);
	}

	@Override
	public FndPriv get(String ukid) {
		return get(null, ukid);
	}

	@Override
	public FndPriv get(Session hnSession, String ukid) {
		Session session = hnSession == null ? sessionFactory.getCurrentSession()
				: hnSession;
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<FndPriv> criteria = cb.createQuery(FndPriv.class);
		Root<FndPriv> oRoot = criteria.from(FndPriv.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("ukid"), ukid));

		List<FndPriv> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

}
