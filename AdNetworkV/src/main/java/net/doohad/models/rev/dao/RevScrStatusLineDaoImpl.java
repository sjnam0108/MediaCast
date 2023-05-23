package net.doohad.models.rev.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.inv.InvScreen;
import net.doohad.models.rev.RevScrStatusLine;

@Transactional
@Component
public class RevScrStatusLineDaoImpl implements RevScrStatusLineDao {

	private static final Logger logger = LoggerFactory.getLogger(RevScrStatusLineDaoImpl.class);
	

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RevScrStatusLine get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevScrStatusLine> criteria = cb.createQuery(RevScrStatusLine.class);
		Root<RevScrStatusLine> oRoot = criteria.from(RevScrStatusLine.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<RevScrStatusLine> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RevScrStatusLine statusLine) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(statusLine);
	}

	@Override
	public void delete(RevScrStatusLine statusLine) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RevScrStatusLine.class, statusLine.getId()));
	}

	@Override
	public void delete(List<RevScrStatusLine> statusLines) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RevScrStatusLine statusLine : statusLines) {
            session.delete(session.load(RevScrStatusLine.class, statusLine.getId()));
        }
	}

	@Override
	public RevScrStatusLine get(int screenId, Date playDate) {
		
		if (playDate == null) {
			return null;
		}

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevScrStatusLine> criteria = cb.createQuery(RevScrStatusLine.class);
		Root<RevScrStatusLine> oRoot = criteria.from(RevScrStatusLine.class);
		Join<RevScrStatusLine, InvScreen> joinO = oRoot.join("screen");
		
		criteria.select(oRoot).where(
				cb.equal(joinO.get("id"), screenId), 
				cb.equal(oRoot.get("playDate"), playDate)
		);

		List<RevScrStatusLine> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<RevScrStatusLine> getListByScreenId(int screenId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevScrStatusLine> criteria = cb.createQuery(RevScrStatusLine.class);
		Root<RevScrStatusLine> oRoot = criteria.from(RevScrStatusLine.class);
		Join<RevScrStatusLine, InvScreen> joinO = oRoot.join("screen");
		
		criteria.select(oRoot).where(
				cb.equal(joinO.get("id"), screenId)
		);

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<RevScrStatusLine> getListByPlayDate(Date playDate) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevScrStatusLine> criteria = cb.createQuery(RevScrStatusLine.class);
		Root<RevScrStatusLine> oRoot = criteria.from(RevScrStatusLine.class);
		
		criteria.select(oRoot).where(
				cb.equal(oRoot.get("playDate"), playDate)
		);

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public Tuple getTuple(int screenId, Date playDate) {
		
		Session session = sessionFactory.getCurrentSession();

		String sql = "SELECT STATUS_LINE, SCR_STATUS_LINE_ID FROM REV_SCR_STATUS_LINES " +
					"WHERE SCREEN_ID = :screenId AND PLAY_DATE = :playDate";
		
		
		List<Tuple> list = session.createNativeQuery(sql, Tuple.class)
				.setParameter("screenId", screenId)
				.setParameter("playDate", playDate)
				.getResultList();
				
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void insert(int screenId, Date playDate, String statusLine) {
		
		Session session = sessionFactory.getCurrentSession();

		String sql = "INSERT INTO REV_SCR_STATUS_LINES(PLAY_DATE, STATUS_LINE, SCREEN_ID) VALUES(:playDate, :statusLine, :screenId)";

		try {
			session.createNativeQuery(sql)
			.setParameter("screenId", screenId)
			.setParameter("playDate", playDate)
			.setParameter("statusLine", statusLine)
			.executeUpdate();
		} catch (Exception e) {
			logger.error("insert", e);
		}
	}

	@Override
	public void update(int id, String statusLine) {
		
		Session session = sessionFactory.getCurrentSession();

		String sql = "UPDATE REV_SCR_STATUS_LINES SET STATUS_LINE = :statusLine WHERE SCR_STATUS_LINE_ID = :id";
		session.createNativeQuery(sql)
				.setParameter("id", id)
				.setParameter("statusLine", statusLine)
				.executeUpdate();
	}

}
