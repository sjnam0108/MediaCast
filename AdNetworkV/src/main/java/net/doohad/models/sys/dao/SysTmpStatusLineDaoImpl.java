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
public class SysTmpStatusLineDaoImpl implements SysTmpStatusLineDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public void insert(int screenId, Date playDate, String statusLine) {
		
		Session session = sessionFactory.getCurrentSession();

		String sql = "INSERT INTO SYS_TMP_STATUS_LINES(SCREEN_ID, PLAY_DATE, STATUS_LINE, CREATION_DATE) VALUES(:screenId, :playDate, :statusLine, NOW())";

		session.createNativeQuery(sql)
				.setParameter("screenId", screenId)
				.setParameter("playDate", playDate)
				.setParameter("statusLine", statusLine)
				.executeUpdate();
	}

	@Override
	public void deleteBulkRowsInIds(List<Integer> ids) {

		Session session = sessionFactory.getCurrentSession();

		String sql = "DELETE FROM SYS_TMP_STATUS_LINES WHERE STATUS_LINE_ID in :ids";
		
		session.createNativeQuery(sql)
				.setParameterList("ids", ids)
				.executeUpdate();
	}

	@Override
	public List<Tuple> getTupleList() {
		
		Session session = sessionFactory.getCurrentSession();

		String sql = "SELECT SCREEN_ID, PLAY_DATE, STATUS_LINE, STATUS_LINE_ID FROM SYS_TMP_STATUS_LINES " +
					"ORDER BY CREATION_DATE ASC LIMIT 0, 30000";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.getResultList();
	}

}
