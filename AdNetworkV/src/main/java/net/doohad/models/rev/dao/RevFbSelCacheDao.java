package net.doohad.models.rev.dao;

import java.util.List;

import javax.persistence.Tuple;

import net.doohad.models.rev.RevFbSelCache;

public interface RevFbSelCacheDao {
	// Common
	public RevFbSelCache get(int id);
	public void saveOrUpdate(RevFbSelCache fbSelCache);
	public void delete(RevFbSelCache fbSelCache);
	public void delete(List<RevFbSelCache> fbSelCaches);

	// for Kendo Grid Remote Read

	// for DAO specific
	public Tuple getLastTupleByScreenId(int screenId);

}
