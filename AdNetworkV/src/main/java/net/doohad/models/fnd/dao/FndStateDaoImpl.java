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
import net.doohad.models.fnd.FndState;
import net.doohad.utils.Util;

@Transactional
@Component
public class FndStateDaoImpl implements FndStateDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public FndState get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<FndState> criteria = cb.createQuery(FndState.class);
		Root<FndState> oRoot = criteria.from(FndState.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<FndState> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(FndState state) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(state);
	}

	@Override
	public void delete(FndState state) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(FndState.class, state.getId()));
	}

	@Override
	public void delete(List<FndState> states) {
		Session session = sessionFactory.getCurrentSession();
		
        for (FndState state : states) {
            session.delete(session.load(FndState.class, state.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), FndState.class);
	}

	@Override
	public FndState get(String name) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<FndState> criteria = cb.createQuery(FndState.class);
		Root<FndState> oRoot = criteria.from(FndState.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("name"), name));

		List<FndState> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<FndState> getListByListIncluded(boolean listIncluded) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<FndState> criteria = cb.createQuery(FndState.class);
		Root<FndState> oRoot = criteria.from(FndState.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("listIncluded"), listIncluded));

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<FndState> getListByNameLike(String name) {
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<FndState> criteria = cb.createQuery(FndState.class);
		Root<FndState> oRoot = criteria.from(FndState.class);

		
		if (Util.isValid(name)) {
			return session.createQuery(criteria.select(oRoot).where(
					cb.and(
							cb.like(oRoot.get("name"), "%" + name + "%"), 
							cb.equal(oRoot.get("listIncluded"), true))))
					.getResultList();
		} else {
			return session.createQuery(criteria.select(oRoot).where(
					cb.equal(oRoot.get("listIncluded"), true))).getResultList();
		}
	}

}
