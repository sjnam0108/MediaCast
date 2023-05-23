package net.doohad.models.service;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.knl.KnlAccount;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.knl.KnlMenu;
import net.doohad.models.knl.KnlUser;

@Transactional
public interface KnlService {
	
	// Common
	public void flush();

	
	//
	// for KnlAccount
	//
	// Common
	public KnlAccount getAccount(int id);
	public void saveOrUpdate(KnlAccount account);
	public void deleteAccount(KnlAccount account);
	public void deleteAccounts(List<KnlAccount> accounts);

	// for Kendo Grid Remote Read
	public DataSourceResult getAccountList(DataSourceRequest request);

	// for DAO specific
	public KnlAccount getAccount(String name);
	public List<KnlAccount> getValidAccountList();

	
	//
	// for KnlMedium
	//screeee
	// Common
	public KnlMedium getMedium(int id);
	public void saveOrUpdate(KnlMedium medium);
	public void deleteMedium(KnlMedium medium);
	public void deleteMedia(List<KnlMedium> media);

	// for Kendo Grid Remote Read
	public DataSourceResult getMediumList(DataSourceRequest request);

	// for DAO specific
	public KnlMedium getMedium(String shortName);
	public KnlMedium getMediumByApiKey(String apiKey);
	public List<KnlMedium> getMediumListByShortNameLike(String shortName);
	public List<KnlMedium> getValidMediumList();

	
	//
	// for KnlUser
	//
	// Common
	public KnlUser getUser(int id);
	public void saveOrUpdate(KnlUser user);
	public void deleteUser(KnlUser user);
	public void deleteUsers(List<KnlUser> users);

	// for Kendo Grid Remote Read
	public DataSourceResult getUserList(DataSourceRequest request);

	// for DAO specific
	public KnlUser getUser(String shortName);

	
	//
	// for KnlMenu
	//
	// Common
	public KnlMenu getMenu(int id);
	public void saveOrUpdate(KnlMenu menu);
	public void deleteMenu(KnlMenu menu);
	public void deleteMenus(List<KnlMenu> menus);

	// for Kendo Grid Remote Read
	public DataSourceResult getMenuList(DataSourceRequest request);

	// for DAO specific
	public KnlMenu getMenu(String ukid);
	public KnlMenu getMenu(org.hibernate.Session hnSession, String ukid);
	public List<KnlMenu> getMenuListById(Integer id);
	public List<KnlMenu> getMenuListById(org.hibernate.Session hnSession, Integer id); 
	public List<String> getAllChildrenMenuById(Integer id);
	public void saveAndReorderMenu(KnlMenu sourceParent, KnlMenu dest, HttpSession httpSession);
	public KnlMenu getMenuByUrl(String url);
	public List<KnlMenu> getExececutableMenuList();
	public List<KnlMenu> getMenuList();

	
	//
	// for Common
	//
}
