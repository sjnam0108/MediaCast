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
import net.doohad.models.knl.KnlAccount;

@Transactional
@Component
public class KnlAccountDaoImpl implements KnlAccountDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public KnlAccount get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<KnlAccount> criteria = cb.createQuery(KnlAccount.class);
		Root<KnlAccount> oRoot = criteria.from(KnlAccount.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<KnlAccount> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(KnlAccount account) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(account);
	}

	@Override
	public void delete(KnlAccount account) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(KnlAccount.class, account.getId()));
	}

	@Override
	public void delete(List<KnlAccount> accounts) {
		Session session = sessionFactory.getCurrentSession();
		
        for (KnlAccount account : accounts) {
            session.delete(session.load(KnlAccount.class, account.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), KnlAccount.class);
	}

	@Override
	public KnlAccount get(String name) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<KnlAccount> criteria = cb.createQuery(KnlAccount.class);
		Root<KnlAccount> oRoot = criteria.from(KnlAccount.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("name"), name));

		List<KnlAccount> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<KnlAccount> getValidList() {
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<KnlAccount> criteria = cb.createQuery(KnlAccount.class);
		Root<KnlAccount> oRoot = criteria.from(KnlAccount.class);
		
		
		Date now = new Date();
		
		Expression<Boolean> exp1 = cb.lessThan(oRoot.get("effectiveStartDate"), now);
		Expression<Boolean> exp2 = oRoot.get("effectiveEndDate").isNull();
		Expression<Boolean> exp3 = cb.greaterThan(oRoot.get("effectiveEndDate"), now);
		
		return session.createQuery(criteria.select(oRoot).where(cb.and(exp1, cb.or(exp2, exp3)))).getResultList();
	}

}
