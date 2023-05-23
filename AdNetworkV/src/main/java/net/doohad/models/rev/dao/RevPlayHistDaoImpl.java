package net.doohad.models.rev.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.rev.RevPlayHist;
import net.doohad.utils.Util;

@Transactional
@Component
public class RevPlayHistDaoImpl implements RevPlayHistDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RevPlayHist get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevPlayHist> criteria = cb.createQuery(RevPlayHist.class);
		Root<RevPlayHist> oRoot = criteria.from(RevPlayHist.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<RevPlayHist> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RevPlayHist playHist) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(playHist);
	}

	@Override
	public void delete(RevPlayHist playHist) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RevPlayHist.class, playHist.getId()));
	}

	@Override
	public void delete(List<RevPlayHist> playHists) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RevPlayHist playHist : playHists) {
            session.delete(session.load(RevPlayHist.class, playHist.getId()));
        }
	}

	@Override
	public int getCountByScreenIdStartDate(int screenId, Date startDate) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<RevPlayHist> oRoot = criteria.from(RevPlayHist.class);
		
		criteria.select(cb.count(oRoot));
		criteria.where(
				cb.equal(oRoot.get("screenId"), screenId),
				cb.equal(oRoot.get("playBeginDate"), startDate)
		);

		return sessionFactory.getCurrentSession().createQuery(criteria).getSingleResult().intValue();
	}

	@Override
	public List<RevPlayHist> getFirstList(int maxRecords) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevPlayHist> criteria = cb.createQuery(RevPlayHist.class);
		Root<RevPlayHist> oRoot = criteria.from(RevPlayHist.class);
		
		// 인덱스 생성 후, 선택날짜 기준으로 변경
		criteria.select(oRoot);
		criteria.where(cb.lessThan(oRoot.get("selectDate"), Util.addDays(new Date(), -1)));
		criteria.orderBy(cb.asc(oRoot.get("selectDate")));

		return sessionFactory.getCurrentSession().createQuery(criteria).setMaxResults(maxRecords).getResultList();
	}

	@Override
	public void deleteBulkRowsInIds(List<Integer> ids) {

		Session session = sessionFactory.getCurrentSession();

		String sql = "DELETE FROM REV_PLAY_HISTS WHERE PLAY_HIST_ID in :ids";
		
		session.createNativeQuery(sql)
				.setParameterList("ids", ids)
				.executeUpdate();
	}

	@Override
	public List<RevPlayHist> getLastListByScreenId(int screenId, int maxRecords) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevPlayHist> criteria = cb.createQuery(RevPlayHist.class);
		Root<RevPlayHist> oRoot = criteria.from(RevPlayHist.class);
		
		criteria.select(oRoot);
		criteria.where(cb.equal(oRoot.get("screenId"), screenId));
		criteria.orderBy(cb.desc(oRoot.get("selectDate")));
		
		return sessionFactory.getCurrentSession().createQuery(criteria)
				.setMaxResults(maxRecords).getResultList();
	}

	@Override
	public List<RevPlayHist> getListByScreenId(int screenId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevPlayHist> criteria = cb.createQuery(RevPlayHist.class);
		Root<RevPlayHist> oRoot = criteria.from(RevPlayHist.class);
		
		criteria.select(oRoot);
		criteria.where(cb.equal(oRoot.get("screenId"), screenId));
		
		return sessionFactory.getCurrentSession().createQuery(criteria)
				.getResultList();
	}

}
