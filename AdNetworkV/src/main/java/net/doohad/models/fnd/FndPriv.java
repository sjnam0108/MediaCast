package net.doohad.models.fnd;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.doohad.utils.Util;

@Entity
@Table(name="FND_PRIVS")
public class FndPriv {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "fnd_priv_seq_gen")
	@Column(name = "PRIV_ID")
	private int id;

	// 식별자
	@Column(name = "UKID", nullable = false, length = 30, unique = true)
	private String ukid;

	// 권한명
	@Column(name = "NAME", nullable = false, length = 50, unique = true)
	private String name;
	
	
	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "CREATED_BY", nullable = false)
	private int whoCreatedBy;
	// WHO 컬럼들(E)
	
	
	// 다른 개체 연결(S)
	
	// 하위 개체: 사용자 권한
	@OneToMany(mappedBy = "priv", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<FndUserPriv> userPrivs = new HashSet<FndUserPriv>(0);
	
	// 다른 개체 연결(E)
	
	
	public FndPriv() {}
	
	public FndPriv(String ukid, String name, HttpSession session) {
		this.ukid = ukid;
		this.name = name;
		
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

	public String getUkid() {
		return ukid;
	}

	public void setUkid(String ukid) {
		this.ukid = ukid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getWhoCreationDate() {
		return whoCreationDate;
	}

	public void setWhoCreationDate(Date whoCreationDate) {
		this.whoCreationDate = whoCreationDate;
	}

	@JsonIgnore
	public Set<FndUserPriv> getUserPrivs() {
		return userPrivs;
	}

	public void setUserPrivs(Set<FndUserPriv> userPrivs) {
		this.userPrivs = userPrivs;
	}

	public int getWhoCreatedBy() {
		return whoCreatedBy;
	}

	public void setWhoCreatedBy(int whoCreatedBy) {
		this.whoCreatedBy = whoCreatedBy;
	}

}
