package net.doohad.viewmodels.rev;

import java.util.Date;

public class RevObjEventTimeItem {

	private int objId;
	private Date date;

	// 유형
	//
	//   - 11: 화면(S)의 File API 요청
	//   - 12: 화면(S)의 Ad API 요청
	//   - 13: 화면(S)의 Report API 요청
	//   - 14: 화면(S)의 Retry Report API 요청
	//   - 21: 광고 소재(C)의 송출 완료
	//
	private int type;
	
	
	public RevObjEventTimeItem() {}
	
	public RevObjEventTimeItem(int objId, Date date, int type) {
		this.objId = objId;
		this.date = date;
		this.type = type;
	}

	
	public int getObjId() {
		return objId;
	}

	public void setObjId(int screenId) {
		this.objId = screenId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
