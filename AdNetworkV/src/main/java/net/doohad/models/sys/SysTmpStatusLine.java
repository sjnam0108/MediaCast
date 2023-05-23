package net.doohad.models.sys;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="SYS_TMP_STATUS_LINES")
public class SysTmpStatusLine {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "STATUS_LINE_ID")
	private int id;

	// 화면 id
	@Column(name = "SCREEN_ID", nullable = false)
	private int screenId;
	
	// 방송 일시
	@Column(name = "PLAY_DATE", nullable = false)
	private Date playDate;

	// 하루 전체 분당 상태 문자열(1일 = 1440분)
	@Column(name = "STATUS_LINE", nullable = false, length = 1440)
	private String statusLine;
	
	
	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	// WHO 컬럼들(E)
	
	
	public SysTmpStatusLine() {}

	public SysTmpStatusLine(int screenId, Date playDate, String statusLine) {
		
		this.screenId = screenId;
		this.playDate = playDate;
		this.statusLine = statusLine;
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

	public Date getPlayDate() {
		return playDate;
	}

	public void setPlayDate(Date playDate) {
		this.playDate = playDate;
	}

	public String getStatusLine() {
		return statusLine;
	}

	public void setStatusLine(String statusLine) {
		this.statusLine = statusLine;
	}

	public Date getWhoCreationDate() {
		return whoCreationDate;
	}

	public void setWhoCreationDate(Date whoCreationDate) {
		this.whoCreationDate = whoCreationDate;
	}

}
