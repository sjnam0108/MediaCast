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
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.adc.AdcCreatFile;
import net.doohad.models.adc.AdcCreative;
import net.doohad.models.fnd.FndCtntFolder;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgAdvertiser;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;

@Transactional
@Component
public class AdcCreatFileDaoImpl implements AdcCreatFileDao {

    @Autowired
    private SessionFactory sessionFactory;
    

	@Override
	public AdcCreatFile get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcCreatFile> criteria = cb.createQuery(AdcCreatFile.class);
		Root<AdcCreatFile> oRoot = criteria.from(AdcCreatFile.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<AdcCreatFile> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(AdcCreatFile creatFile) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(creatFile);
	}

	@Override
	public void delete(AdcCreatFile creatFile) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), AdcCreatFile.class, creatFile.getId());
	}

	@Override
	public void delete(List<AdcCreatFile> creatFiles) {

		Session session = sessionFactory.getCurrentSession();
		
        for (AdcCreatFile creatFile : creatFiles) {
            session.delete(session.load(AdcCreatFile.class, creatFile.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("creative", AdcCreative.class);
		map.put("ctntFolder", FndCtntFolder.class);
		
		Criterion criterion = Restrictions.eq("deleted", false);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), AdcCreatFile.class, 
        		map, criterion);
	}

	@Override
	public List<AdcCreatFile> getListByCreativeId(int creativeId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcCreatFile> criteria = cb.createQuery(AdcCreatFile.class);
		Root<AdcCreatFile> oRoot = criteria.from(AdcCreatFile.class);
		Join<AdcCreatFile, AdcCreative> joinO = oRoot.join("creative");
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(joinO.get("id"), creativeId),
				cb.equal(oRoot.get("deleted"), false)
		);

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public AdcCreatFile getByCreativeIdResolution(int creativeId, String resolution) {
		
		if (Util.isNotValid(resolution)) {
			return null;
		}

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcCreatFile> criteria = cb.createQuery(AdcCreatFile.class);
		Root<AdcCreatFile> oRoot = criteria.from(AdcCreatFile.class);
		Join<AdcCreatFile, AdcCreative> joinO = oRoot.join("creative");
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(joinO.get("id"), creativeId),
				cb.equal(oRoot.get("deleted"), false)
		);

		List<AdcCreatFile> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		// 일치하는 것을 먼저
		for(AdcCreatFile creatFile : list) {
			if (creatFile.getResolution().equals(resolution)) {
				return creatFile;
			}
		}
		
		// 일치하는 것이 없다면 20 범위로 적합도 판정
		float ratio = Util.getResolutionRatio(resolution);
		if (ratio > 0f) {
			for(AdcCreatFile creatFile : list) {
				float rt = Util.getResolutionRatio(creatFile.getResolution());
				if (rt > 0f && Util.getPctDifference(ratio, rt) <= 20) {
					return creatFile;
				}
			}
		}
		
		return null;
	}

	@Override
	public List<Tuple> getCountGroupByMediumMediaType(int mediumId) {

		// 결과 예)
		//
		//     V  5
		//     I  3
		//
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Tuple> q = cb.createTupleQuery();
		Root<AdcCreatFile> oRoot = q.from(AdcCreatFile.class);
		Join<AdcCreatFile, KnlMedium> joinO = oRoot.join("medium");
		
		q.multiselect(oRoot.get("mediaType"), cb.count(oRoot));
		q.where(
				cb.equal(joinO.get("id"), mediumId),
				cb.equal(oRoot.get("deleted"), false)
		);
		q.groupBy(oRoot.get("mediaType"));
		
		return sessionFactory.getCurrentSession().createQuery(q).getResultList();
	}

	@Override
	public List<Tuple> getCountGroupByCtntFolderId() {

		// 결과 예)
		//
		//     1  5
		//     2  3
		//
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Tuple> q = cb.createTupleQuery();
		Root<AdcCreatFile> oRoot = q.from(AdcCreatFile.class);
		Join<AdcCreatFile, FndCtntFolder> joinO = oRoot.join("ctntFolder");
		
		q.multiselect(joinO.get("id"), cb.count(oRoot));
		q.where(
				cb.equal(oRoot.get("deleted"), false)
		);
		q.groupBy(joinO.get("id"));
		
		return sessionFactory.getCurrentSession().createQuery(q).getResultList();
	}

	@Override
	public int getCountByAdvertiserId(int advertiserId) {
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<AdcCreatFile> oRoot = criteria.from(AdcCreatFile.class);
		Join<AdcCreatFile, AdcCreative> joinO1 = oRoot.join("creative");
		Join<AdcCreative, OrgAdvertiser> joinO2 = joinO1.join("advertiser");
		
		criteria.select(cb.count(oRoot));
		criteria.where(
				cb.equal(joinO2.get("id"), advertiserId),
				cb.equal(oRoot.get("deleted"), false),
				cb.equal(joinO1.get("deleted"), false)
		);

		return sessionFactory.getCurrentSession().createQuery(criteria).getSingleResult().intValue();
	}

}
