package net.doohad.models.sys.dao;

import java.util.Date;
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
import net.doohad.models.sys.SysRtUnit;
import net.doohad.models.sys.SysSvcRespTime;
import net.doohad.utils.SolUtil;

@Transactional
@Component
public class SysSvcRespTimeDaoImpl implements SysSvcRespTimeDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public SysSvcRespTime get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<SysSvcRespTime> criteria = cb.createQuery(SysSvcRespTime.class);
		Root<SysSvcRespTime> oRoot = criteria.from(SysSvcRespTime.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<SysSvcRespTime> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(SysSvcRespTime svcRespTime) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(svcRespTime);
	}

	@Override
	public void delete(SysSvcRespTime svcRespTime) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), SysSvcRespTime.class, svcRespTime.getId());
	}

	@Override
	public void delete(List<SysSvcRespTime> svcRespTimes) {

		Session session = sessionFactory.getCurrentSession();
		
        for (SysSvcRespTime svcRespTime : svcRespTimes) {
            session.delete(session.load(SysSvcRespTime.class, svcRespTime.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("rtUnit", SysRtUnit.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), SysSvcRespTime.class, map);
	}

	@Override
	public SysSvcRespTime get(SysRtUnit rtUnit, Date checkDate) {
		
		if (rtUnit == null) {
			return null;
		}

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<SysSvcRespTime> criteria = cb.createQuery(SysSvcRespTime.class);
		Root<SysSvcRespTime> oRoot = criteria.from(SysSvcRespTime.class);
		Join<SysSvcRespTime, SysRtUnit> joinO = oRoot.join("rtUnit");
		
		criteria.select(oRoot).where(
				cb.and(cb.equal(joinO.get("id"), rtUnit.getId())), cb.equal(oRoot.get("checkDate"), checkDate));

		List<SysSvcRespTime> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

}
