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
import net.doohad.utils.Util;


@Entity
@Table(name="FND_USER_PRIVS", uniqueConstraints = 
	@javax.persistence.UniqueConstraint(columnNames = {"USER_ID", "PRIV_ID"}))
public class FndUserPriv {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "fnd_user_priv_seq_gen")
	@Column(name = "USER_PRIV_ID")
	private int id;
	
	
	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "CREATED_BY", nullable = false)
	private int whoCreatedBy;
	// WHO 컬럼들(E)
	
	
	// 다른 개체 연결(S)
	
	// 상위 개체: 사용자
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "USER_ID", nullable = false)
	private KnlUser user;
	
	// 상위 개체: 사용자
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PRIV_ID", nullable = false)
	private FndPriv priv;
	
	// 다른 개체 연결(E)
	
	
	public FndUserPriv() {}
	
	public FndUserPriv(KnlUser user, FndPriv priv, HttpSession session) {
		this.user = user;
		this.priv = priv;
		
		touchWhoC(session);
	}

	private void touchWhoC(HttpSession session) {
		this.whoCreatedBy = Util.loginUserId(session);
		this.whoCreationDate = new Date();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getWhoCreationDate() {
		return whoCreationDate;
	}

	public void setWhoCreationDate(Date whoCreationDate) {
		this.whoCreationDate = whoCreationDate;
	}

	public int getWhoCreatedBy() {
		return whoCreatedBy;
	}

	public void setWhoCreatedBy(int whoCreatedBy) {
		this.whoCreatedBy = whoCreatedBy;
	}

	public KnlUser getUser() {
		return user;
	}

	public void setUser(KnlUser user) {
		this.user = user;
	}

	public FndPriv getPriv() {
		return priv;
	}

	public void setPriv(FndPriv priv) {
		this.priv = priv;
	}

}
