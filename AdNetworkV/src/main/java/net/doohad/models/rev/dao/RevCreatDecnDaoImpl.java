package net.doohad.models.rev.dao;

import java.util.HashMap;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.adc.AdcCreative;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.rev.RevCreatDecn;

@Transactional
@Component
public class RevCreatDecnDaoImpl implements RevCreatDecnDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RevCreatDecn get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevCreatDecn> criteria = cb.createQuery(RevCreatDecn.class);
		Root<RevCreatDecn> oRoot = criteria.from(RevCreatDecn.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<RevCreatDecn> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RevCreatDecn creatDecn) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(creatDecn);
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("creative", AdcCreative.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RevCreatDecn.class, map);
	}

}
