package net.doohad.models.knl.dao;

import java.util.List;

import javax.servlet.http.HttpSession;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.knl.KnlMenu;

public interface KnlMenuDao {
	// Common
	public KnlMenu get(int id);
	public void saveOrUpdate(KnlMenu menu);
	public void delete(KnlMenu menu);
	public void delete(List<KnlMenu> menus);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public KnlMenu get(String ukid);
	public KnlMenu get(org.hibernate.Session hnSession, String ukid);
	public List<KnlMenu> getListById(Integer id);
	public List<KnlMenu> getListById(org.hibernate.Session hnSession, Integer id); 
	public List<String> getAllChildrenById(Integer id);
	public void saveAndReorder(KnlMenu sourceParent, KnlMenu dest, HttpSession httpSession);
	public KnlMenu getByUrl(String url);
	public List<KnlMenu> getExececutableList();
	public List<KnlMenu> getList();

}
