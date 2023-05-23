package net.doohad.models.rev.dao;

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
import net.doohad.models.adc.AdcAd;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.rev.RevAdHourlyPlay;

@Transactional
@Component
public class RevAdHourlyPlayDaoImpl implements RevAdHourlyPlayDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RevAdHourlyPlay get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevAdHourlyPlay> criteria = cb.createQuery(RevAdHourlyPlay.class);
		Root<RevAdHourlyPlay> oRoot = criteria.from(RevAdHourlyPlay.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<RevAdHourlyPlay> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RevAdHourlyPlay hourPlay) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(hourPlay);
	}

	@Override
	public void delete(RevAdHourlyPlay hourPlay) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RevAdHourlyPlay.class, hourPlay.getId()));
	}

	@Override
	public void delete(List<RevAdHourlyPlay> hourPlays) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RevAdHourlyPlay hourPlay : hourPlays) {
            session.delete(session.load(RevAdHourlyPlay.class, hourPlay.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("ad", AdcAd.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RevAdHourlyPlay.class, map);
	}

	@Override
	public RevAdHourlyPlay get(AdcAd ad, Date playDate) {
		
		if (ad == null || playDate == null) {
			return null;
		}

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevAdHourlyPlay> criteria = cb.createQuery(RevAdHourlyPlay.class);
		Root<RevAdHourlyPlay> oRoot = criteria.from(RevAdHourlyPlay.class);
		Join<RevAdHourlyPlay, AdcAd> joinO1 = oRoot.join("ad");
		
		criteria.select(oRoot).where(
				cb.equal(oRoot.get("playDate"), playDate),
				cb.equal(joinO1.get("id"), ad.getId())
				);

		List<RevAdHourlyPlay> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

}
