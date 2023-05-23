package net.doohad.models.rev.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.adc.AdcAd;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.rev.RevScrHrlyNoAdTot;

@Transactional
@Component
public class RevScrHrlyNoAdTotDaoImpl implements RevScrHrlyNoAdTotDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RevScrHrlyNoAdTot get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevScrHrlyNoAdTot> criteria = cb.createQuery(RevScrHrlyNoAdTot.class);
		Root<RevScrHrlyNoAdTot> oRoot = criteria.from(RevScrHrlyNoAdTot.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<RevScrHrlyNoAdTot> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RevScrHrlyNoAdTot hrlyNoAdTot) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(hrlyNoAdTot);
	}

	@Override
	public void delete(RevScrHrlyNoAdTot hrlyNoAdTot) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RevScrHrlyNoAdTot.class, hrlyNoAdTot.getId()));
	}

	@Override
	public void delete(List<RevScrHrlyNoAdTot> hrlyNoAdTots) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RevScrHrlyNoAdTot hrlyNoAdTot : hrlyNoAdTots) {
            session.delete(session.load(RevScrHrlyNoAdTot.class, hrlyNoAdTot.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request, Date playDate) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("screen", InvScreen.class);
		
		Criterion criterion = Restrictions.eq("playDate", playDate);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RevScrHrlyNoAdTot.class, map, criterion);
	}

	@Override
	public RevScrHrlyNoAdTot get(InvScreen screen, Date playDate) {
		
		if (screen == null || playDate == null) {
			return null;
		}

		return get(screen.getId(), playDate);
	}

	@Override
	public RevScrHrlyNoAdTot get(int screenId, Date playDate) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevScrHrlyNoAdTot> criteria = cb.createQuery(RevScrHrlyNoAdTot.class);
		Root<RevScrHrlyNoAdTot> oRoot = criteria.from(RevScrHrlyNoAdTot.class);
		Join<RevScrHrlyNoAdTot, AdcAd> joinO = oRoot.join("screen");
		
		criteria.select(oRoot).where(
				cb.equal(joinO.get("id"), screenId), 
				cb.equal(oRoot.get("playDate"), playDate)
		);

		List<RevScrHrlyNoAdTot> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public Tuple getStatByMediumIdPlayDate(int mediumId, Date playDate) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Tuple> criteria = cb.createTupleQuery();
		Root<RevScrHrlyNoAdTot> oRoot = criteria.from(RevScrHrlyNoAdTot.class);
		Join<RevScrHrlyNoAdTot, KnlMedium> joinO = oRoot.join("medium");
		
		criteria.multiselect(
				cb.sum(oRoot.get("dateTotal")), 
				cb.countDistinct(oRoot.get("id")), 
				cb.avg(oRoot.get("dateTotal"))
		);
		criteria.where(
				cb.equal(joinO.get("id"), mediumId),
				cb.equal(oRoot.get("playDate"), playDate)
		);
		
		return sessionFactory.getCurrentSession().createQuery(criteria).getSingleResult();
	}

}
