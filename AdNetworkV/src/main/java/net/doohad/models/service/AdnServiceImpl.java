package net.doohad.models.service;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.adn.AdnExcelRow;
import net.doohad.models.adn.dao.AdnExcelRowDao;

@Transactional
@Service("adnService")
public class AdnServiceImpl implements AdnService {

	private static final Logger logger = LoggerFactory.getLogger(AdnServiceImpl.class);


    //
    // General
    //
    @Autowired
    private SessionFactory sessionFactory;
    
	@Override
	public void flush() {
		
		sessionFactory.getCurrentSession().flush();
	}

	
    
    //
    // DAO
    //
    @Autowired
    private AdnExcelRowDao excelRowDao;

    
    
	//
	// for AdnExcelRowDao
	//
	@Override
	public AdnExcelRow getExcelRow(int id) {
		return excelRowDao.get(id);
	}

	@Override
	public void saveOrUpdate(AdnExcelRow row) {
		excelRowDao.saveOrUpdate(row);
	}

	@Override
	public void deleteExcelRow(AdnExcelRow row) {
		excelRowDao.delete(row);
	}

	@Override
	public void deleteExcelRows(List<AdnExcelRow> rows) {
		excelRowDao.delete(rows);
	}

	@Override
	public DataSourceResult getExcelRowList(DataSourceRequest request, String type) {
		return excelRowDao.getList(request, type);
	}

	@Override
	public List<AdnExcelRow> getExcelRowListByMediumIdType(int mediumId, String type) {
		return excelRowDao.getListByMediumIdType(mediumId, type);
	}

    
    
	//
	// for Common
	//
	@Override
	public boolean deleteBulkExcelRowsByMediumId(int mediumId) {

		Session session = sessionFactory.getCurrentSession();
		if (session != null) {
	    	
	    	try {
	    		
				String sql = "DELETE FROM ADN_EXCEL_ROWS WHERE MEDIUM_ID = :mediumId";
				
				session.createNativeQuery(sql)
						.setParameter("mediumId", mediumId)
						.executeUpdate();
				
				
				return true;
				
	    	} catch (Exception e) {
	    		logger.error("deletePrevGenMasterData", e);
	    	}
		}
		
		return false;
	}

}
