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
import net.doohad.models.fnd.FndLoginLog;
import net.doohad.models.knl.KnlUser;

@Transactional
@Component
public class FndLoginLogDaoImpl implements FndLoginLogDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public FndLoginLog get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<FndLoginLog> criteria = cb.createQuery(FndLoginLog.class);
		Root<FndLoginLog> oRoot = criteria.from(FndLoginLog.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<FndLoginLog> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(FndLoginLog loginLog) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(loginLog);
	}

	@Override
	public void delete(FndLoginLog loginLog) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(FndLoginLog.class, loginLog.getId()));
	}

	@Override
	public void delete(List<FndLoginLog> loginLogs) {
		Session session = sessionFactory.getCurrentSession();
		
        for (FndLoginLog loginLog : loginLogs) {
            session.delete(session.load(FndLoginLog.class, loginLog.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("user", KnlUser.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), FndLoginLog.class, map);
	}

	@Override
	public FndLoginLog getLastByUserId(int userId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<FndLoginLog> criteria = cb.createQuery(FndLoginLog.class);
		Root<FndLoginLog> oRoot = criteria.from(FndLoginLog.class);
		Join<FndLoginLog, KnlUser> joinO = oRoot.join("user");
		
		criteria.select(oRoot).where(cb.equal(joinO.get("id"), userId))
				.orderBy(cb.desc(oRoot.get("id")));

		List<FndLoginLog> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

}
