package net.doohad.models.sys.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
public class SysTmpHrlyEventDaoImpl implements SysTmpHrlyEventDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public void insert(int screenId, Date eventDate, int type) {
		
		Session session = sessionFactory.getCurrentSession();

		String sql = "INSERT INTO SYS_TMP_HRLY_EVENTS(SCREEN_ID, EVENT_DATE, TYPE, CREATION_DATE) VALUES(:screenId, :eventDate, :type, NOW())";

		session.createNativeQuery(sql)
				.setParameter("screenId", screenId)
				.setParameter("eventDate", eventDate)
				.setParameter("type", type)
				.executeUpdate();
	}

	@Override
	public void deleteBulkRowsInIds(List<Integer> ids) {

		Session session = sessionFactory.getCurrentSession();

		String sql = "DELETE FROM SYS_TMP_HRLY_EVENTS WHERE HRLY_EVENT_ID in :ids";
		
		session.createNativeQuery(sql)
				.setParameterList("ids", ids)
				.executeUpdate();
	}

	@Override
	public List<Tuple> getTupleList() {
		
		Session session = sessionFactory.getCurrentSession();

		String sql = "SELECT SCREEN_ID, EVENT_DATE, TYPE, HRLY_EVENT_ID FROM SYS_TMP_HRLY_EVENTS " +
					"ORDER BY TYPE, SCREEN_ID, EVENT_DATE ASC LIMIT 0, 10000";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.getResultList();
	}

}
