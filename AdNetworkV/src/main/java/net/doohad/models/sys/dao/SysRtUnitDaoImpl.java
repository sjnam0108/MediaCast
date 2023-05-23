package net.doohad.models.sys.dao;

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
import net.doohad.models.sys.SysRtUnit;
import net.doohad.utils.SolUtil;

@Transactional
@Component
public class SysRtUnitDaoImpl implements SysRtUnitDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public SysRtUnit get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<SysRtUnit> criteria = cb.createQuery(SysRtUnit.class);
		Root<SysRtUnit> oRoot = criteria.from(SysRtUnit.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<SysRtUnit> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(SysRtUnit rtUnit) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(rtUnit);
	}

	@Override
	public void delete(SysRtUnit rtUnit) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), SysRtUnit.class, rtUnit.getId());
	}

	@Override
	public void delete(List<SysRtUnit> rtUnits) {

		Session session = sessionFactory.getCurrentSession();
		
        for (SysRtUnit rtUnit : rtUnits) {
            session.delete(session.load(SysRtUnit.class, rtUnit.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), SysRtUnit.class);
	}

	@Override
	public SysRtUnit get(String ukid) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<SysRtUnit> criteria = cb.createQuery(SysRtUnit.class);
		Root<SysRtUnit> oRoot = criteria.from(SysRtUnit.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("ukid"), ukid));

		List<SysRtUnit> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

}
