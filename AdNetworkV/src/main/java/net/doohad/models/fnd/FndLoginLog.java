package net.doohad.models.fnd;

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
import javax.servlet.http.HttpSession;

import net.doohad.models.knl.KnlUser;


@Entity
@Table(name="FND_LOGIN_LOGS")
public class FndLoginLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "fnd_login_log_seq_gen")
	@Column(name = "LOGIN_LOG_ID")
	private int id;
	
	// 접속 IP
	@Column(name = "IP", nullable = false, length = 50)
	private String ip;
	
	// 로그아웃 여부
	@Column(name = "LOGOUT", nullable = false)
	private boolean logout; 
	
	// 강제 로그아웃 여부
	@Column(name = "FORCED_LOGOUT", nullable = false)
	private boolean forcedLogout; 
	
	// 로그아웃 일시
	@Column(name = "LOGOUT_DATE")
	private Date logoutDate;
	
	
	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "LAST_UPDATE_DATE", nullable = false)
	private Date whoLastUpdateDate;
	// WHO 컬럼들(E)
	
	
	// 다른 개체 연결(S)
	
	// 상위 개체: 사용자
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "USER_ID", nullable = false)
	private KnlUser user;
	
	// 다른 개체 연결(E)
	
	
	public FndLoginLog() {}

	public FndLoginLog(KnlUser user, String ip, HttpSession session) {
		this(user, ip, false, false, session);
	}
	
	public FndLoginLog(KnlUser user, String ip, boolean logout, HttpSession session) {
		this(user, ip, logout, false, session);
	}
	
	public FndLoginLog(KnlUser user, String ip, boolean logout, boolean forcedLogout, HttpSession session) {
		this.ip = ip;
		this.logout = logout;
		this.forcedLogout = forcedLogout;
		
		this.user = user;
		
		touchWhoC(session);
	}

	private void touchWhoC(HttpSession session) {
		this.whoCreationDate = new Date();
		touchWho(session);
	}
	
	public void touchWho(HttpSession session) {
		this.whoLastUpdateDate = new Date();
	}
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public boolean isLogout() {
		return logout;
	}

	public void setLogout(boolean logout) {
		this.logout = logout;
	}

	public boolean isForcedLogout() {
		return forcedLogout;
	}

	public void setForcedLogout(boolean forcedLogout) {
		this.forcedLogout = forcedLogout;
	}

	public Date getLogoutDate() {
		return logoutDate;
	}

	public void setLogoutDate(Date logoutDate) {
		this.logoutDate = logoutDate;
	}

	public Date getWhoCreationDate() {
		return whoCreationDate;
	}

	public void setWhoCreationDate(Date whoCreationDate) {
		this.whoCreationDate = whoCreationDate;
	}

	public Date getWhoLastUpdateDate() {
		return whoLastUpdateDate;
	}

	public void setWhoLastUpdateDate(Date whoLastUpdateDate) {
		this.whoLastUpdateDate = whoLastUpdateDate;
	}

	public KnlUser getUser() {
		return user;
	}

	public void setUser(KnlUser user) {
		this.user = user;
	}

}
