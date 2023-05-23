package net.doohad.models.org.dao;

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

import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgMediumOpt;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;

@Transactional
@Component
public class OrgMediumOptDaoImpl implements OrgMediumOptDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public OrgMediumOpt get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgMediumOpt> criteria = cb.createQuery(OrgMediumOpt.class);
		Root<OrgMediumOpt> oRoot = criteria.from(OrgMediumOpt.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<OrgMediumOpt> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(OrgMediumOpt mediumOpt) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(mediumOpt);
	}

	@Override
	public void delete(OrgMediumOpt mediumOpt) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), OrgMediumOpt.class, mediumOpt.getId());
	}

	@Override
	public void delete(List<OrgMediumOpt> mediumOpts) {

		Session session = sessionFactory.getCurrentSession();
		
        for (OrgMediumOpt mediumOpt : mediumOpts) {
            session.delete(session.load(OrgMediumOpt.class, mediumOpt.getId()));
        }
	}

	@Override
	public OrgMediumOpt get(KnlMedium medium, String code) {
		
		if (medium == null) {
			return null;
		}

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgMediumOpt> criteria = cb.createQuery(OrgMediumOpt.class);
		Root<OrgMediumOpt> oRoot = criteria.from(OrgMediumOpt.class);
		Join<OrgMediumOpt, KnlMedium> joinO = oRoot.join("medium");
		
		criteria.select(oRoot).where(
				cb.and(cb.equal(joinO.get("id"), medium.getId())), cb.equal(oRoot.get("code"), code));

		List<OrgMediumOpt> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<OrgMediumOpt> getListByMediumId(int mediumId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgMediumOpt> criteria = cb.createQuery(OrgMediumOpt.class);
		Root<OrgMediumOpt> oRoot = criteria.from(OrgMediumOpt.class);
		Join<OrgMediumOpt, KnlMedium> joinO = oRoot.join("medium");

		return sessionFactory.getCurrentSession().createQuery(criteria.select(oRoot).where(
				cb.equal(joinO.get("id"), mediumId))).getResultList();
	}

	@Override
	public String getValue(int mediumId, String code) {
		
		if (Util.isValid(code)) {
			String sql = "SELECT VALUE FROM ORG_MEDIUM_OPTS " +
					"WHERE MEDIUM_ID = :mediumId AND CODE = :code";
			
			List<Tuple> list = sessionFactory.getCurrentSession()
					.createNativeQuery(sql, Tuple.class)
					.setParameter("mediumId", mediumId)
					.setParameter("code", code)
					.getResultList();
			
			if (!list.isEmpty()) {
				return (String) list.get(0).get(0);
			}
		}
		
		return "";
	}

}
