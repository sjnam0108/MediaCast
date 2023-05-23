package net.doohad.models.org.dao;

import java.util.HashMap;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
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
import net.doohad.models.org.OrgSiteCond;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;

@Transactional
@Component
public class OrgSiteCondDaoImpl implements OrgSiteCondDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public OrgSiteCond get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgSiteCond> criteria = cb.createQuery(OrgSiteCond.class);
		Root<OrgSiteCond> oRoot = criteria.from(OrgSiteCond.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<OrgSiteCond> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(OrgSiteCond siteCond) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(siteCond);
	}

	@Override
	public void delete(OrgSiteCond siteCond) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), OrgSiteCond.class, siteCond.getId());
	}

	@Override
	public void delete(List<OrgSiteCond> siteConds) {

		Session session = sessionFactory.getCurrentSession();
		
        for (OrgSiteCond siteCond : siteConds) {
            session.delete(session.load(OrgSiteCond.class, siteCond.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), OrgSiteCond.class, map);
	}

	@Override
	public OrgSiteCond get(KnlMedium medium, String code) {
		
		if (medium == null) {
			return null;
		}

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgSiteCond> criteria = cb.createQuery(OrgSiteCond.class);
		Root<OrgSiteCond> oRoot = criteria.from(OrgSiteCond.class);
		Join<OrgSiteCond, KnlMedium> joinO = oRoot.join("medium");
		
		criteria.select(oRoot).where(
				cb.and(cb.equal(joinO.get("id"), medium.getId())), cb.equal(oRoot.get("code"), code));

		List<OrgSiteCond> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<OrgSiteCond> getListByMediumId(int mediumId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgSiteCond> criteria = cb.createQuery(OrgSiteCond.class);
		Root<OrgSiteCond> oRoot = criteria.from(OrgSiteCond.class);
		Join<OrgSiteCond, KnlMedium> joinO = oRoot.join("medium");

		return sessionFactory.getCurrentSession().createQuery(criteria.select(oRoot).where(
				cb.equal(joinO.get("id"), mediumId))).getResultList();
	}

	@Override
	public List<OrgSiteCond> getListByMediumIdActiveStatus(int mediumId, boolean activeStatus) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgSiteCond> criteria = cb.createQuery(OrgSiteCond.class);
		Root<OrgSiteCond> oRoot = criteria.from(OrgSiteCond.class);
		Join<OrgSiteCond, KnlMedium> joinO = oRoot.join("medium");
		
		Expression<Boolean> exp1 = cb.equal(joinO.get("id"), mediumId);
		Expression<Boolean> exp2 = cb.equal(oRoot.get("activeStatus"), activeStatus);

		return sessionFactory.getCurrentSession()
				.createQuery(criteria.select(oRoot).where(cb.and(exp1, exp2)))
				.getResultList();
	}

	@Override
	public List<OrgSiteCond> getListByMediumIdNameLike(int mediumId, String name) {
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<OrgSiteCond> criteria = cb.createQuery(OrgSiteCond.class);
		Root<OrgSiteCond> oRoot = criteria.from(OrgSiteCond.class);
		Join<OrgSiteCond, KnlMedium> joinO = oRoot.join("medium");

		Expression<Boolean> expW1 = cb.equal(joinO.get("id"), mediumId);
		
		
		if (Util.isValid(name)) {
			return session.createQuery(criteria.select(oRoot).where(
					cb.and(
							cb.like(oRoot.get("name"), "%" + name + "%"), 
							expW1)
					))
					.getResultList();
		} else {
			return session.createQuery(criteria.select(oRoot).where(
					expW1)).getResultList();
		}
	}

}
