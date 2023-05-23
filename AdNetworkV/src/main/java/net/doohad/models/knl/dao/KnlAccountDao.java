package net.doohad.models.knl.dao;

import java.util.List;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.knl.KnlAccount;

public interface KnlAccountDao {
	// Common
	public KnlAccount get(int id);
	public void saveOrUpdate(KnlAccount account);
	public void delete(KnlAccount account);
	public void delete(List<KnlAccount> accounts);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public KnlAccount get(String name);
	public List<KnlAccount> getValidList();
}
