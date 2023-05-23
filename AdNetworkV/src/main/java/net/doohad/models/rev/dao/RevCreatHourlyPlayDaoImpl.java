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
import net.doohad.models.adc.AdcCreative;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.rev.RevCreatHourlyPlay;

@Transactional
@Component
public class RevCreatHourlyPlayDaoImpl implements RevCreatHourlyPlayDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RevCreatHourlyPlay get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevCreatHourlyPlay> criteria = cb.createQuery(RevCreatHourlyPlay.class);
		Root<RevCreatHourlyPlay> oRoot = criteria.from(RevCreatHourlyPlay.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<RevCreatHourlyPlay> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RevCreatHourlyPlay hourPlay) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(hourPlay);
	}

	@Override
	public void delete(RevCreatHourlyPlay hourPlay) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RevCreatHourlyPlay.class, hourPlay.getId()));
	}

	@Override
	public void delete(List<RevCreatHourlyPlay> hourPlays) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RevCreatHourlyPlay hourPlay : hourPlays) {
            session.delete(session.load(RevCreatHourlyPlay.class, hourPlay.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("creative", AdcCreative.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RevCreatHourlyPlay.class, map);
	}

	@Override
	public RevCreatHourlyPlay get(AdcCreative creative, Date playDate) {
		
		if (creative == null || playDate == null) {
			return null;
		}

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevCreatHourlyPlay> criteria = cb.createQuery(RevCreatHourlyPlay.class);
		Root<RevCreatHourlyPlay> oRoot = criteria.from(RevCreatHourlyPlay.class);
		Join<RevCreatHourlyPlay, AdcCreative> joinO1 = oRoot.join("creative");
		
		criteria.select(oRoot).where(
				cb.equal(oRoot.get("playDate"), playDate),
				cb.equal(joinO1.get("id"), creative.getId())
				);

		List<RevCreatHourlyPlay> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

}
