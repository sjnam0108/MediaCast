package net.doohad.models.org.dao;

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
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgAdvertiser;
import net.doohad.utils.SolUtil;

@Transactional
@Component
public class OrgAdvertiserDaoImpl implements OrgAdvertiserDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public OrgAdvertiser get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgAdvertiser> criteria = cb.createQuery(OrgAdvertiser.class);
		Root<OrgAdvertiser> oRoot = criteria.from(OrgAdvertiser.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<OrgAdvertiser> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(OrgAdvertiser advertiser) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(advertiser);
	}

	@Override
	public void delete(OrgAdvertiser advertiser) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), OrgAdvertiser.class, advertiser.getId());
	}

	@Override
	public void delete(List<OrgAdvertiser> advertisers) {

		Session session = sessionFactory.getCurrentSession();
		
        for (OrgAdvertiser advertiser : advertisers) {
            session.delete(session.load(OrgAdvertiser.class, advertiser.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), OrgAdvertiser.class, map);
	}

	@Override
	public OrgAdvertiser get(KnlMedium medium, String name) {
		
		if (medium == null) {
			return null;
		}

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgAdvertiser> criteria = cb.createQuery(OrgAdvertiser.class);
		Root<OrgAdvertiser> oRoot = criteria.from(OrgAdvertiser.class);
		Join<OrgAdvertiser, KnlMedium> joinO = oRoot.join("medium");
		
		criteria.select(oRoot).where(
				cb.and(cb.equal(joinO.get("id"), medium.getId())), cb.equal(oRoot.get("name"), name));

		List<OrgAdvertiser> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<OrgAdvertiser> getListByMediumId(int mediumId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgAdvertiser> criteria = cb.createQuery(OrgAdvertiser.class);
		Root<OrgAdvertiser> oRoot = criteria.from(OrgAdvertiser.class);
		Join<OrgAdvertiser, KnlMedium> joinO = oRoot.join("medium");

		return sessionFactory.getCurrentSession().createQuery(criteria.select(oRoot).where(
				cb.equal(joinO.get("id"), mediumId))).getResultList();
	}

}
