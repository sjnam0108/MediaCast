package net.doohad.models.service;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.knl.KnlAccount;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.knl.KnlMenu;
import net.doohad.models.knl.KnlUser;
import net.doohad.models.knl.dao.KnlAccountDao;
import net.doohad.models.knl.dao.KnlMediumDao;
import net.doohad.models.knl.dao.KnlMenuDao;
import net.doohad.models.knl.dao.KnlUserDao;

@Transactional
@Service("knlService")
public class KnlServiceImpl implements KnlService {

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
    private KnlAccountDao accountDao;

    @Autowired
    private KnlMediumDao mediumDao;

    @Autowired
    private KnlUserDao userDao;

    @Autowired
    private KnlMenuDao menuDao;

    
    
	//
	// for KnlAccountDao
	//
	@Override
	public KnlAccount getAccount(int id) {
		return accountDao.get(id);
	}

	@Override
	public void saveOrUpdate(KnlAccount account) {
		accountDao.saveOrUpdate(account);
	}

	@Override
	public void deleteAccount(KnlAccount account) {
		accountDao.delete(account);
	}

	@Override
	public void deleteAccounts(List<KnlAccount> accounts) {
		accountDao.delete(accounts);
	}

	@Override
	public DataSourceResult getAccountList(DataSourceRequest request) {
		return accountDao.getList(request);
	}

	@Override
	public KnlAccount getAccount(String name) {
		return accountDao.get(name);
	}

	@Override
	public List<KnlAccount> getValidAccountList() {
		return accountDao.getValidList();
	}

    
    
	//
	// for KnlMediumDao
	//
	@Override
	public KnlMedium getMedium(int id) {
		return mediumDao.get(id);
	}

	@Override
	public void saveOrUpdate(KnlMedium medium) {
		mediumDao.saveOrUpdate(medium);
	}

	@Override
	public void deleteMedium(KnlMedium medium) {
		mediumDao.delete(medium);
	}

	@Override
	public void deleteMedia(List<KnlMedium> media) {
		mediumDao.delete(media);
	}

	@Override
	public DataSourceResult getMediumList(DataSourceRequest request) {
		return mediumDao.getList(request);
	}

	@Override
	public KnlMedium getMedium(String shortName) {
		return mediumDao.get(shortName);
	}

	@Override
	public KnlMedium getMediumByApiKey(String apiKey) {
		return mediumDao.getByApiKey(apiKey);
	}

	@Override
	public List<KnlMedium> getMediumListByShortNameLike(String shortName) {
		return mediumDao.getListByShortNameLike(shortName);
	}

	@Override
	public List<KnlMedium> getValidMediumList() {
		return mediumDao.getValidList();
	}

    
    
	//
	// for KnlUserDao
	//
	@Override
	public KnlUser getUser(int id) {
		return userDao.get(id);
	}

	@Override
	public void saveOrUpdate(KnlUser user) {
		userDao.saveOrUpdate(user);
	}

	@Override
	public void deleteUser(KnlUser user) {
		userDao.delete(user);
	}

	@Override
	public void deleteUsers(List<KnlUser> users) {
		userDao.delete(users);
	}

	@Override
	public DataSourceResult getUserList(DataSourceRequest request) {
		return userDao.getList(request);
	}

	@Override
	public KnlUser getUser(String shortName) {
		return userDao.get(shortName);
	}

    
    
	//
	// for KnlMenuDao
	//
	@Override
	public KnlMenu getMenu(int id) {
		return menuDao.get(id);
	}

	@Override
	public void saveOrUpdate(KnlMenu menu) {
		menuDao.saveOrUpdate(menu);
	}

	@Override
	public void deleteMenu(KnlMenu menu) {
		menuDao.delete(menu);
	}

	@Override
	public void deleteMenus(List<KnlMenu> menus) {
		menuDao.delete(menus);
	}

	@Override
	public DataSourceResult getMenuList(DataSourceRequest request) {
		return menuDao.getList(request);
	}

	@Override
	public KnlMenu getMenu(String ukid) {
		return menuDao.get(ukid);
	}

	@Override
	public KnlMenu getMenu(Session hnSession, String ukid) {
		return menuDao.get(hnSession, ukid);
	}

	@Override
	public List<KnlMenu> getMenuListById(Integer id) {
		return menuDao.getListById(id);
	}

	@Override
	public List<KnlMenu> getMenuListById(Session hnSession, Integer id) {
		return menuDao.getListById(hnSession, id);
	}

	@Override
	public List<String> getAllChildrenMenuById(Integer id) {
		return menuDao.getAllChildrenById(id);
	}

	@Override
	public void saveAndReorderMenu(KnlMenu sourceParent, KnlMenu dest, HttpSession httpSession) {
		menuDao.saveAndReorder(sourceParent, dest, httpSession);
	}

	@Override
	public KnlMenu getMenuByUrl(String url) {
		return menuDao.getByUrl(url);
	}

	@Override
	public List<KnlMenu> getExececutableMenuList() {
		return menuDao.getExececutableList();
	}

	@Override
	public List<KnlMenu> getMenuList() {
		return menuDao.getList();
	}

    
    
	//
	// for Common
	//
}
