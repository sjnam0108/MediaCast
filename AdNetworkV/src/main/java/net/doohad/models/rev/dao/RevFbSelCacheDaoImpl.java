package net.doohad.models.rev.dao;

import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.rev.RevFbSelCache;

@Transactional
@Component
public class RevFbSelCacheDaoImpl implements RevFbSelCacheDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RevFbSelCache get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevFbSelCache> criteria = cb.createQuery(RevFbSelCache.class);
		Root<RevFbSelCache> oRoot = criteria.from(RevFbSelCache.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<RevFbSelCache> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RevFbSelCache fbSelCache) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(fbSelCache);
	}

	@Override
	public void delete(RevFbSelCache fbSelCache) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RevFbSelCache.class, fbSelCache.getId()));
	}

	@Override
	public void delete(List<RevFbSelCache> fbSelCaches) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RevFbSelCache fbSelCache : fbSelCaches) {
            session.delete(session.load(RevFbSelCache.class, fbSelCache.getId()));
        }
	}

	@Override
	public Tuple getLastTupleByScreenId(int screenId) {
		
		Session session = sessionFactory.getCurrentSession();

		String sql = "SELECT FB_SEL_CACHE_ID, SEL_DATE, CREATIVE_ID " +
					"FROM REV_FB_SEL_CACHES " +
					"WHERE SCREEN_ID = :screenId " +
					"ORDER BY SEL_DATE DESC LIMIT 0, 1";
		
		
		List<Tuple> list = session.createNativeQuery(sql, Tuple.class)
				.setParameter("screenId", screenId).getResultList();
				
		return (list.isEmpty() ? null : list.get(0));
	}

}
