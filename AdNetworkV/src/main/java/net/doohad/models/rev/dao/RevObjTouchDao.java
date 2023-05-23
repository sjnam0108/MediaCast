package net.doohad.models.rev.dao;

import java.util.List;

import net.doohad.models.rev.RevObjTouch;

public interface RevObjTouchDao {
	// Common
	public RevObjTouch get(int id);
	public void saveOrUpdate(RevObjTouch objTouch);
	public void delete(RevObjTouch objTouch);
	public void delete(List<RevObjTouch> objTouches);

	// for Kendo Grid Remote Read

	// for DAO specific
	public RevObjTouch get(String type, int objId);
	public List<RevObjTouch> getList();

}
