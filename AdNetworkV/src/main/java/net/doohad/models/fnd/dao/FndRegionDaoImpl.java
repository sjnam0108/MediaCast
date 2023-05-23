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
import net.doohad.models.fnd.FndRegion;
import net.doohad.utils.Util;

@Transactional
@Component
public class FndRegionDaoImpl implements FndRegionDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public FndRegion get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<FndRegion> criteria = cb.createQuery(FndRegion.class);
		Root<FndRegion> oRoot = criteria.from(FndRegion.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<FndRegion> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(FndRegion region) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(region);
	}

	@Override
	public void delete(FndRegion region) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(FndRegion.class, region.getId()));
	}

	@Override
	public void delete(List<FndRegion> regions) {
		Session session = sessionFactory.getCurrentSession();
		
        for (FndRegion region : regions) {
            session.delete(session.load(FndRegion.class, region.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), FndRegion.class);
	}

	@Override
	public FndRegion get(String code) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<FndRegion> criteria = cb.createQuery(FndRegion.class);
		Root<FndRegion> oRoot = criteria.from(FndRegion.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("code"), code));

		List<FndRegion> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public FndRegion getByName(String name) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<FndRegion> criteria = cb.createQuery(FndRegion.class);
		Root<FndRegion> oRoot = criteria.from(FndRegion.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("name"), name));

		List<FndRegion> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<FndRegion> getListByListIncluded(boolean listIncluded) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<FndRegion> criteria = cb.createQuery(FndRegion.class);
		Root<FndRegion> oRoot = criteria.from(FndRegion.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("listIncluded"), listIncluded));

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<FndRegion> getListByNameLike(String name) {
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<FndRegion> criteria = cb.createQuery(FndRegion.class);
		Root<FndRegion> oRoot = criteria.from(FndRegion.class);

		
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
