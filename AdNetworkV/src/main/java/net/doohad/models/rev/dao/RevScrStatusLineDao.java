package net.doohad.models.rev.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import net.doohad.models.rev.RevScrStatusLine;

public interface RevScrStatusLineDao {
	// Common
	public RevScrStatusLine get(int id);
	public void saveOrUpdate(RevScrStatusLine statusLine);
	public void delete(RevScrStatusLine statusLine);
	public void delete(List<RevScrStatusLine> statusLines);

	// for Kendo Grid Remote Read

	// for DAO specific
	public RevScrStatusLine get(int screenId, Date playDate);
	public List<RevScrStatusLine> getListByScreenId(int screenId);
	public List<RevScrStatusLine> getListByPlayDate(Date playDate);
	public Tuple getTuple(int screenId, Date playDate);
	public void insert(int screenId, Date playDate, String statusLine);
	public void update(int id, String statusLine);
}
