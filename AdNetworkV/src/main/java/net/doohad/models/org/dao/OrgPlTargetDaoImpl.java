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
import net.doohad.models.org.OrgPlTarget;
import net.doohad.models.org.OrgPlaylist;
import net.doohad.utils.SolUtil;

@Transactional
@Component
public class OrgPlTargetDaoImpl implements OrgPlTargetDao {

    @Autowired
    private SessionFactory sessionFactory;
    

	@Override
	public OrgPlTarget get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgPlTarget> criteria = cb.createQuery(OrgPlTarget.class);
		Root<OrgPlTarget> oRoot = criteria.from(OrgPlTarget.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<OrgPlTarget> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(OrgPlTarget plTarget) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(plTarget);
	}

	@Override
	public void delete(OrgPlTarget plTarget) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), OrgPlTarget.class, plTarget.getId());
	}

	@Override
	public void delete(List<OrgPlTarget> plTargets) {

		Session session = sessionFactory.getCurrentSession();
		
        for (OrgPlTarget plTarget : plTargets) {
            session.delete(session.load(OrgPlTarget.class, plTarget.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("playlist", OrgPlaylist.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), OrgPlTarget.class, 
        		map);
	}

	@Override
	public List<OrgPlTarget> getListByPlaylistId(int playlistId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgPlTarget> criteria = cb.createQuery(OrgPlTarget.class);
		Root<OrgPlTarget> oRoot = criteria.from(OrgPlTarget.class);
		Join<OrgPlTarget, OrgPlaylist> joinO = oRoot.join("playlist");
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(joinO.get("id"), playlistId)
		);
		criteria.orderBy(cb.asc(oRoot.get("siblingSeq")));

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	private void reorder(int playlistId) {
		
		List<OrgPlTarget> list = getListByPlaylistId(playlistId);
		
		int cnt = 1;
		for(OrgPlTarget item : list) {
			item.setSiblingSeq((cnt++) * 10);
			sessionFactory.getCurrentSession().saveOrUpdate(item);
		}
	}

	@Override
	public void saveAndReorder(OrgPlTarget plTarget) {
		
		saveOrUpdate(plTarget);
		reorder(plTarget.getPlaylist().getId());
	}

}
