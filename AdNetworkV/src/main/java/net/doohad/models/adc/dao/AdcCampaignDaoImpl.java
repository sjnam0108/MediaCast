package net.doohad.models.adc.dao;

import java.util.HashMap;
import java.util.List;

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
import net.doohad.models.adc.AdcCampaign;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgAdvertiser;
import net.doohad.utils.SolUtil;

@Transactional
@Component
public class AdcCampaignDaoImpl implements AdcCampaignDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public AdcCampaign get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcCampaign> criteria = cb.createQuery(AdcCampaign.class);
		Root<AdcCampaign> oRoot = criteria.from(AdcCampaign.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<AdcCampaign> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(AdcCampaign campaign) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(campaign);
	}

	@Override
	public void delete(AdcCampaign campaign) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), AdcCampaign.class, campaign.getId());
	}

	@Override
	public void delete(List<AdcCampaign> campaigns) {

		Session session = sessionFactory.getCurrentSession();
		
        for (AdcCampaign campaign : campaigns) {
            session.delete(session.load(AdcCampaign.class, campaign.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("advertiser", OrgAdvertiser.class);
		
		Criterion criterion = Restrictions.eq("deleted", false);

        return request.toDataSourceResult(sessionFactory.getCurrentSession(), AdcCampaign.class, map, criterion);
	}

	@Override
	public AdcCampaign get(KnlMedium medium, String name) {
		
		if (medium == null) {
			return null;
		}

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcCampaign> criteria = cb.createQuery(AdcCampaign.class);
		Root<AdcCampaign> oRoot = criteria.from(AdcCampaign.class);
		Join<AdcCampaign, KnlMedium> joinO = oRoot.join("medium");
		
		criteria.select(oRoot).where(
				cb.and(cb.equal(joinO.get("id"), medium.getId())), cb.equal(oRoot.get("name"), name));

		List<AdcCampaign> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<AdcCampaign> getListByMediumId(int mediumId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcCampaign> criteria = cb.createQuery(AdcCampaign.class);
		Root<AdcCampaign> oRoot = criteria.from(AdcCampaign.class);
		Join<AdcCampaign, KnlMedium> joinO = oRoot.join("medium");
		
		criteria.select(oRoot).where(
				cb.and(cb.equal(joinO.get("id"), mediumId), cb.equal(oRoot.get("deleted"), false)));

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<AdcCampaign> getList() {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcCampaign> criteria = cb.createQuery(AdcCampaign.class);
		Root<AdcCampaign> oRoot = criteria.from(AdcCampaign.class);
		
		criteria.select(oRoot);
		criteria.where(cb.equal(oRoot.get("deleted"), false));
		
		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<AdcCampaign> getLisyByAdvertiserId(int advertiserId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcCampaign> criteria = cb.createQuery(AdcCampaign.class);
		Root<AdcCampaign> oRoot = criteria.from(AdcCampaign.class);
		Join<AdcCampaign, OrgAdvertiser> joinO = oRoot.join("advertiser");
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(joinO.get("id"), advertiserId),
				cb.equal(oRoot.get("deleted"), false)
		);
		criteria.orderBy(cb.desc(oRoot.get("startDate")), cb.asc(oRoot.get("name")));

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

}
