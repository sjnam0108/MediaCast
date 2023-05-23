package net.doohad.models.rev.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.rev.RevObjTouch;

@Transactional
@Component
public class RevObjTouchDaoImpl implements RevObjTouchDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RevObjTouch get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevObjTouch> criteria = cb.createQuery(RevObjTouch.class);
		Root<RevObjTouch> oRoot = criteria.from(RevObjTouch.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<RevObjTouch> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RevObjTouch objTouch) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(objTouch);
	}

	@Override
	public void delete(RevObjTouch objTouch) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RevObjTouch.class, objTouch.getId()));
	}

	@Override
	public void delete(List<RevObjTouch> objTouches) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RevObjTouch objTouch : objTouches) {
            session.delete(session.load(RevObjTouch.class, objTouch.getId()));
        }
	}

	@Override
	public RevObjTouch get(String type, int objId) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevObjTouch> criteria = cb.createQuery(RevObjTouch.class);
		Root<RevObjTouch> oRoot = criteria.from(RevObjTouch.class);
		
		criteria.select(oRoot).where(
				cb.equal(oRoot.get("type"), type),
				cb.equal(oRoot.get("objId"), objId)
		);

		List<RevObjTouch> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<RevObjTouch> getList() {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevObjTouch> criteria = cb.createQuery(RevObjTouch.class);
		Root<RevObjTouch> oRoot = criteria.from(RevObjTouch.class);
		
		criteria.select(oRoot);
		
		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

}
