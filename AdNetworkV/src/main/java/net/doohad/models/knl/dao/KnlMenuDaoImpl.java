package net.doohad.models.knl.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.knl.KnlMenu;

@Transactional
@Component
public class KnlMenuDaoImpl implements KnlMenuDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public KnlMenu get(int id) {
		/*
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<KnlMenu> list = session.createCriteria(KnlMenu.class)
				.add(Restrictions.eq("id", id)).list();
		
		return (list.isEmpty() ? null : list.get(0));
		*/
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<KnlMenu> criteria = cb.createQuery(KnlMenu.class);
		Root<KnlMenu> oRoot = criteria.from(KnlMenu.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<KnlMenu> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(KnlMenu menu) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(menu);
	}

	@Override
	public void delete(KnlMenu menu) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(KnlMenu.class, menu.getId()));
	}

	@Override
	public void delete(List<KnlMenu> menus) {
		Session session = sessionFactory.getCurrentSession();
		
        for (KnlMenu menu : menus) {
            session.delete(session.load(KnlMenu.class, menu.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), KnlMenu.class);
	}

	@Override
	public KnlMenu get(String ukid) {
		return get(null, ukid);
	}

	@Override
	public KnlMenu get(Session hnSession, String ukid) {
		Session session = hnSession == null ? sessionFactory.getCurrentSession()
				: hnSession;
		
		/*
		@SuppressWarnings("unchecked")
		List<KnlMenu> list = session.createCriteria(KnlMenu.class)
				.add(Restrictions.eq("ukid", ukid)).list();
		*/
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<KnlMenu> criteria = cb.createQuery(KnlMenu.class);
		Root<KnlMenu> oRoot = criteria.from(KnlMenu.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("ukid"), ukid));

		List<KnlMenu> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<KnlMenu> getListById(Integer id) {
		return getListById(null, id);
	}

	@Override
	public List<KnlMenu> getListById(Session hnSession, Integer id) {
		Session session = hnSession == null ? sessionFactory.getCurrentSession()
				: hnSession;
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		if (id == null) {
			CriteriaQuery<KnlMenu> criteria = cb.createQuery(KnlMenu.class);
			Root<KnlMenu> oRoot = criteria.from(KnlMenu.class);
			
			Expression<Boolean> exp = oRoot.get("parent").isNull();
			
			return session.createQuery(criteria.select(oRoot).where(exp)).getResultList();
		} else {
			CriteriaQuery<KnlMenu> criteria = cb.createQuery(KnlMenu.class);
			Root<KnlMenu> oRoot = criteria.from(KnlMenu.class);
			Join<KnlMenu, KnlMenu> selfJoin = oRoot.join("parent", JoinType.LEFT);
			
			Expression<Boolean> exp = cb.equal(selfJoin.get("id"), id);
			
			return session.createQuery(criteria.select(oRoot).where(exp)).getResultList();
		}
	}

	private ArrayList<String> getChildren(ArrayList<String> retList, List<KnlMenu> list) {
		for (KnlMenu menu : list) {
			retList.add(menu.getUkid());
			getChildren(retList, getListById(menu.getId()));
		}
		
		return retList;
	}
	
	@Override
	public List<String> getAllChildrenById(Integer id) {
		ArrayList<String> retList = new ArrayList<String>();

		if (id != null) {
			KnlMenu current = get(id);
			if (!retList.contains(current.getUkid())) {
				retList.add(current.getUkid());
			}
			
			retList = getChildren(retList, getListById(id));
		}
		
		return retList;
	}

	private void reorder(KnlMenu parent, org.hibernate.Session session, HttpSession httpSession) {
		List<KnlMenu> children = new ArrayList<KnlMenu>();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		if (parent == null) {
			CriteriaQuery<KnlMenu> criteria = cb.createQuery(KnlMenu.class);
			Root<KnlMenu> oRoot = criteria.from(KnlMenu.class);
			
			Expression<Boolean> exp = oRoot.get("parent").isNull();
			
			children = session.createQuery(criteria.select(oRoot).where(exp)).getResultList();
		} else {
			CriteriaQuery<KnlMenu> criteria = cb.createQuery(KnlMenu.class);
			Root<KnlMenu> oRoot = criteria.from(KnlMenu.class);
			Join<KnlMenu, KnlMenu> selfJoin = oRoot.join("parent", JoinType.LEFT);
			
			Expression<Boolean> exp = cb.equal(selfJoin.get("id"), parent.getId());
			
			children = session.createQuery(criteria.select(oRoot).where(exp)).getResultList();
		}

		/*
		if (parent == null) {
			children = session.createCriteria(KnlMenu.class)
					.add(Restrictions.isNull("parent")).list();
			
		} else {
			children = session.createCriteria(KnlMenu.class)
					.add(Restrictions.eq("parent.id", parent.getId())).list();
		}
		*/
		
		Collections.sort(children, new Comparator<KnlMenu>() {
	    	public int compare(KnlMenu item1, KnlMenu item2) {
	    		return Integer.compare(item1.getSiblingSeq(), item2.getSiblingSeq());
	    	}
	    });
		
		int cnt = 1;
		for (KnlMenu item : children) {
			item.setSiblingSeq((cnt++) * 10);
			item.touchWho(httpSession);
			
			session.saveOrUpdate(item);
		}
	}

	@Override
	public void saveAndReorder(KnlMenu sourceParent, KnlMenu dest, HttpSession httpSession) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(dest);
		session.flush();
		
		reorder(dest.getParent(), session, httpSession);
		
		if (sourceParent != dest.getParent()) {
			reorder(sourceParent, session, httpSession);
		}
	}

	@Override
	public KnlMenu getByUrl(String url) {
		Session session = sessionFactory.getCurrentSession();
		
		/*
		@SuppressWarnings("unchecked")
		List<KnlMenu> list = session.createCriteria(KnlMenu.class)
				.add(Restrictions.eq("url", url)).list();
		
		return (list.isEmpty() ? null : list.get(0));
		*/
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<KnlMenu> criteria = cb.createQuery(KnlMenu.class);
		Root<KnlMenu> oRoot = criteria.from(KnlMenu.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("url"), url));

		List<KnlMenu> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<KnlMenu> getExececutableList() {
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<KnlMenu> criteria = cb.createQuery(KnlMenu.class);
		Root<KnlMenu> oRoot = criteria.from(KnlMenu.class);
		
		Expression<Boolean> exp1 = oRoot.get("url").isNotNull();
		Expression<Boolean> exp2 = cb.notEqual(oRoot.get("url"), "");
		
		/*
		Criterion rest1 = Restrictions.isNotNull("url");
		Criterion rest2 = Restrictions.ne("url", "");
		
		return session.createCriteria(KnlMenu.class).add(
				Restrictions.and(rest1, rest2)).list();
		*/

		return session.createQuery(criteria.select(oRoot).where(cb.and(exp1, exp2))).getResultList();
	}

	@Override
	public List<KnlMenu> getList() {
		/*
		return sessionFactory.getCurrentSession().createCriteria(KnlMenu.class).list();
		*/
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<KnlMenu> criteria = cb.createQuery(KnlMenu.class);
		Root<KnlMenu> oRoot = criteria.from(KnlMenu.class);
		
		criteria.select(oRoot);

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
	}

}
