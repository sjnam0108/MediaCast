package net.doohad.models.org.dao;

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
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgPlaylist;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;

@Transactional
@Component
public class OrgPlaylistDaoImpl implements OrgPlaylistDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public OrgPlaylist get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgPlaylist> criteria = cb.createQuery(OrgPlaylist.class);
		Root<OrgPlaylist> oRoot = criteria.from(OrgPlaylist.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<OrgPlaylist> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(OrgPlaylist playlist) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(playlist);
	}

	@Override
	public void delete(OrgPlaylist playlist) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), OrgPlaylist.class, playlist.getId());
	}

	@Override
	public void delete(List<OrgPlaylist> playlists) {

		Session session = sessionFactory.getCurrentSession();
		
        for (OrgPlaylist playlist : playlists) {
            session.delete(session.load(OrgPlaylist.class, playlist.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), OrgPlaylist.class, map);
	}

	@Override
	public OrgPlaylist get(KnlMedium medium, String name) {
		
		if (medium == null) {
			return null;
		}

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgPlaylist> criteria = cb.createQuery(OrgPlaylist.class);
		Root<OrgPlaylist> oRoot = criteria.from(OrgPlaylist.class);
		Join<OrgPlaylist, KnlMedium> joinO = oRoot.join("medium");
		
		criteria.select(oRoot).where(
				cb.and(cb.equal(joinO.get("id"), medium.getId())), cb.equal(oRoot.get("name"), name));

		List<OrgPlaylist> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<OrgPlaylist> getListByMediumId(int mediumId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgPlaylist> criteria = cb.createQuery(OrgPlaylist.class);
		Root<OrgPlaylist> oRoot = criteria.from(OrgPlaylist.class);
		Join<OrgPlaylist, KnlMedium> joinO = oRoot.join("medium");
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(joinO.get("id"), mediumId)
		);
		criteria.orderBy(cb.asc(oRoot.get("siblingSeq")));

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	private void reorder(int mediumId) {
		
		List<OrgPlaylist> list = getListByMediumId(mediumId);
		
		int cnt = 1;
		for(OrgPlaylist item : list) {
			item.setSiblingSeq((cnt++) * 10);
			sessionFactory.getCurrentSession().saveOrUpdate(item);
		}
	}

	@Override
	public void saveAndReorder(OrgPlaylist playlist) {
		
		saveOrUpdate(playlist);
		reorder(playlist.getMedium().getId());
	}

	@Override
	public List<OrgPlaylist> getEffListByMediumId(int mediumId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgPlaylist> criteria = cb.createQuery(OrgPlaylist.class);
		Root<OrgPlaylist> oRoot = criteria.from(OrgPlaylist.class);
		Join<OrgPlaylist, KnlMedium> joinO = oRoot.join("medium");

		
		Date today = Util.removeTimeOfDate(new Date());
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(joinO.get("id"), mediumId),
				cb.or(
						oRoot.get("startDate").isNull(),
						cb.lessThanOrEqualTo(oRoot.get("startDate"), today)
				),
				cb.or(
						oRoot.get("endDate").isNull(),
						cb.greaterThanOrEqualTo(oRoot.get("endDate"), today)
				)
		);
		criteria.orderBy(cb.asc(oRoot.get("siblingSeq")));

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<OrgPlaylist> getEffListByMediumIdDate(int mediumId, Date sDate, Date eDate) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgPlaylist> criteria = cb.createQuery(OrgPlaylist.class);
		Root<OrgPlaylist> oRoot = criteria.from(OrgPlaylist.class);
		Join<OrgPlaylist, KnlMedium> joinO = oRoot.join("medium");

		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(joinO.get("id"), mediumId),
				cb.or(
						oRoot.get("startDate").isNull(),
						cb.lessThanOrEqualTo(oRoot.get("startDate"), eDate)
				),
				cb.or(
						oRoot.get("endDate").isNull(),
						cb.greaterThanOrEqualTo(oRoot.get("endDate"), sDate)
				)
		);
		criteria.orderBy(cb.asc(oRoot.get("siblingSeq")));

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

}
