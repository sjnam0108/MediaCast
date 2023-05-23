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
import net.doohad.models.adc.AdcAd;
import net.doohad.models.adc.AdcAdTarget;
import net.doohad.models.knl.KnlMedium;
import net.doohad.utils.SolUtil;

@Transactional
@Component
public class AdcAdTargetDaoImpl implements AdcAdTargetDao {

    @Autowired
    private SessionFactory sessionFactory;
    

	@Override
	public AdcAdTarget get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcAdTarget> criteria = cb.createQuery(AdcAdTarget.class);
		Root<AdcAdTarget> oRoot = criteria.from(AdcAdTarget.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<AdcAdTarget> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(AdcAdTarget adTarget) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(adTarget);
	}

	@Override
	public void delete(AdcAdTarget adTarget) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), AdcAdTarget.class, adTarget.getId());
	}

	@Override
	public void delete(List<AdcAdTarget> adTargets) {

		Session session = sessionFactory.getCurrentSession();
		
        for (AdcAdTarget adTarget : adTargets) {
            session.delete(session.load(AdcAdTarget.class, adTarget.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("ad", AdcAd.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), AdcAdTarget.class, 
        		map);
	}

	@Override
	public List<AdcAdTarget> getListByAdId(int adId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcAdTarget> criteria = cb.createQuery(AdcAdTarget.class);
		Root<AdcAdTarget> oRoot = criteria.from(AdcAdTarget.class);
		Join<AdcAdTarget, AdcAd> joinO = oRoot.join("ad");
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(joinO.get("id"), adId)
		);
		criteria.orderBy(cb.asc(oRoot.get("siblingSeq")));

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	private void reorder(int adId) {
		
		List<AdcAdTarget> list = getListByAdId(adId);
		
		int cnt = 1;
		for(AdcAdTarget item : list) {
			item.setSiblingSeq((cnt++) * 10);
			sessionFactory.getCurrentSession().saveOrUpdate(item);
		}
	}

	@Override
	public void saveAndReorder(AdcAdTarget adTarget) {
		
		saveOrUpdate(adTarget);
		reorder(adTarget.getAd().getId());
	}

	@Override
	public List<Tuple> getCountGroupByMediumAdId(int mediumId) {

		// 결과 예)
		//
		//     1  5
		//     2  3
		//
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Tuple> q = cb.createTupleQuery();
		Root<AdcAdTarget> oRoot = q.from(AdcAdTarget.class);
		Join<AdcAdTarget, KnlMedium> joinO1 = oRoot.join("medium");
		Join<AdcAdTarget, AdcAd> joinO2 = oRoot.join("ad");
		
		q.multiselect(joinO2.get("id"), cb.count(oRoot));
		q.where(
				cb.equal(joinO1.get("id"), mediumId)
		);
		q.groupBy(joinO2.get("id"));
		
		return sessionFactory.getCurrentSession().createQuery(q).getResultList();
	}

	@Override
	public List<Tuple> getCountGroupByAdId() {

		// 결과 예)
		//
		//     1  5
		//     2  3
		//
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Tuple> q = cb.createTupleQuery();
		Root<AdcAdTarget> oRoot = q.from(AdcAdTarget.class);
		Join<AdcAdTarget, AdcAd> joinO = oRoot.join("ad");
		
		q.multiselect(joinO.get("id"), cb.count(oRoot));
		q.groupBy(joinO.get("id"));
		
		return sessionFactory.getCurrentSession().createQuery(q).getResultList();
	}

}
