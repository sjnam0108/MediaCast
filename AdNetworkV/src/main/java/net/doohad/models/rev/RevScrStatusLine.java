package net.doohad.models.rev;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.doohad.models.inv.InvScreen;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;

@Entity
@Table(name="REV_SCR_STATUS_LINES", uniqueConstraints = {
	@javax.persistence.UniqueConstraint(columnNames = {"SCREEN_ID", "PLAY_DATE"}),
})
public class RevScrStatusLine {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SCR_STATUS_LINE_ID")
	private int id;
	
	// 방송 일시
	@Column(name = "PLAY_DATE", nullable = false)
	private Date playDate;

	// 하루 전체 분당 상태 문자열(1일 = 1440분)
	@Column(name = "STATUS_LINE", nullable = false, length = 1440)
	private String statusLine;

	
	// WHO 컬럼들 생략

	
	// 다른 개체 연결(S)
	
	// 상위 개체: 화면
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SCREEN_ID", nullable = false)
	private InvScreen screen;
	
	// 다른 개체 연결(E)

	
	public RevScrStatusLine() {}

	public RevScrStatusLine(InvScreen screen, Date date) {
		
		this.screen = screen;
		this.playDate = Util.removeTimeOfDate(date);
		
		this.statusLine = SolUtil.getScrStatusLine("", date, "2");
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public InvScreen getScreen() {
		return screen;
	}

	public void setScreen(InvScreen screen) {
		this.screen = screen;
	}

}
