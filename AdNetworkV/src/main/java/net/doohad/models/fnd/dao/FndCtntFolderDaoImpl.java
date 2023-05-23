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
import net.doohad.models.fnd.FndCtntFolder;
import net.doohad.utils.SolUtil;

@Transactional
@Component
public class FndCtntFolderDaoImpl implements FndCtntFolderDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public FndCtntFolder get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<FndCtntFolder> criteria = cb.createQuery(FndCtntFolder.class);
		Root<FndCtntFolder> oRoot = criteria.from(FndCtntFolder.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<FndCtntFolder> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(FndCtntFolder ctntFolder) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(ctntFolder);
	}

	@Override
	public void delete(FndCtntFolder ctntFolder) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), FndCtntFolder.class, ctntFolder.getId());
	}

	@Override
	public void delete(List<FndCtntFolder> ctntFolders) {

		Session session = sessionFactory.getCurrentSession();
		
        for (FndCtntFolder ctntFolder : ctntFolders) {
            session.delete(session.load(FndCtntFolder.class, ctntFolder.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), FndCtntFolder.class);
	}

	@Override
	public FndCtntFolder get(String name) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<FndCtntFolder> criteria = cb.createQuery(FndCtntFolder.class);
		Root<FndCtntFolder> oRoot = criteria.from(FndCtntFolder.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("name"), name));

		List<FndCtntFolder> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public int getCount() {
		
		return SolUtil.getCount(sessionFactory.getCurrentSession(), FndCtntFolder.class);
	}

	@Override
	public List<FndCtntFolder> getList() {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<FndCtntFolder> criteria = cb.createQuery(FndCtntFolder.class);
		Root<FndCtntFolder> oRoot = criteria.from(FndCtntFolder.class);
		
		criteria.select(oRoot);
		
		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

}
