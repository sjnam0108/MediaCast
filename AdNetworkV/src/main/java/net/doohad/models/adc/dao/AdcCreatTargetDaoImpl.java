package net.doohad.models.adc.dao;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Tuple;
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
import net.doohad.models.adc.AdcCreatTarget;
import net.doohad.models.adc.AdcCreative;
import net.doohad.models.knl.KnlMedium;
import net.doohad.utils.SolUtil;

@Transactional
@Component
public class AdcCreatTargetDaoImpl implements AdcCreatTargetDao {

    @Autowired
    private SessionFactory sessionFactory;
    

	@Override
	public AdcCreatTarget get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcCreatTarget> criteria = cb.createQuery(AdcCreatTarget.class);
		Root<AdcCreatTarget> oRoot = criteria.from(AdcCreatTarget.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<AdcCreatTarget> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(AdcCreatTarget creatTarget) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(creatTarget);
	}

	@Override
	public void delete(AdcCreatTarget creatTarget) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), AdcCreatTarget.class, creatTarget.getId());
	}

	@Override
	public void delete(List<AdcCreatTarget> creatTargets) {

		Session session = sessionFactory.getCurrentSession();
		
        for (AdcCreatTarget creatTarget : creatTargets) {
            session.delete(session.load(AdcCreatTarget.class, creatTarget.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("creative", AdcCreative.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), AdcCreatTarget.class, 
        		map);
	}

	@Override
	public List<AdcCreatTarget> getListByCreativeId(int creativeId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcCreatTarget> criteria = cb.createQuery(AdcCreatTarget.class);
		Root<AdcCreatTarget> oRoot = criteria.from(AdcCreatTarget.class);
		Join<AdcCreatTarget, AdcCreative> joinO = oRoot.join("creative");
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(joinO.get("id"), creativeId)
		);
		criteria.orderBy(cb.asc(oRoot.get("siblingSeq")));

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	private void reorder(int creativeId) {
		
		List<AdcCreatTarget> list = getListByCreativeId(creativeId);
		
		int cnt = 1;
		for(AdcCreatTarget item : list) {
			item.setSiblingSeq((cnt++) * 10);
			sessionFactory.getCurrentSession().saveOrUpdate(item);
		}
	}

	@Override
	public void saveAndReorder(AdcCreatTarget creatTarget) {
		
		saveOrUpdate(creatTarget);
		reorder(creatTarget.getCreative().getId());
	}

	@Override
	public List<Tuple> getCountGroupByMediumCreativeId(int mediumId) {

		// 결과 예)
		//
		//     1  5
		//     2  3
		//
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Tuple> q = cb.createTupleQuery();
		Root<AdcCreatTarget> oRoot = q.from(AdcCreatTarget.class);
		Join<AdcCreatTarget, KnlMedium> joinO1 = oRoot.join("medium");
		Join<AdcCreatTarget, AdcCreative> joinO2 = oRoot.join("creative");
		
		q.multiselect(joinO2.get("id"), cb.count(oRoot));
		q.where(
				cb.equal(joinO1.get("id"), mediumId)
		);
		q.groupBy(joinO2.get("id"));
		
		return sessionFactory.getCurrentSession().createQuery(q).getResultList();
	}

	@Override
	public List<Tuple> getCountGroupByCreativeId() {

		// 결과 예)
		//
		//     1  5
		//     2  3
		//
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Tuple> q = cb.createTupleQuery();
		Root<AdcCreatTarget> oRoot = q.from(AdcCreatTarget.class);
		Join<AdcCreatTarget, AdcCreative> joinO = oRoot.join("creative");
		
		q.multiselect(joinO.get("id"), cb.count(oRoot));
		q.groupBy(joinO.get("id"));
		
		return sessionFactory.getCurrentSession().createQuery(q).getResultList();
	}
}
