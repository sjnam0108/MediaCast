package net.doohad.models.sys;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="SYS_TMP_HRLY_EVENTS")
public class SysTmpHrlyEvent {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "HRLY_EVENT_ID")
	private int id;

	// 화면 id
	@Column(name = "SCREEN_ID", nullable = false)
	private int screenId;
	
	// 이벤트 일시
	@Column(name = "EVENT_DATE", nullable = false)
	private Date eventDate;

	// 이벤트 유형
	//
	//  - 1		실패
	//	- 2		일정 없음
	//  - 3		대체 광고
	//
	@Column(name = "TYPE", nullable = false)
	private int type;
	
	
	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	// WHO 컬럼들(E)
	
	
	public SysTmpHrlyEvent() {}

	public SysTmpHrlyEvent(int screenId, Date eventDate, int type) {
		
		this.screenId = screenId;
		this.eventDate = eventDate;
		this.type = type;
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getScreenId() {
		return screenId;
	}

	public void setScreenId(int screenId) {
		this.screenId = screenId;
	}

	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Date getWhoCreationDate() {
		return whoCreationDate;
	}

	public void setWhoCreationDate(Date whoCreationDate) {
		this.whoCreationDate = whoCreationDate;
	}

}
