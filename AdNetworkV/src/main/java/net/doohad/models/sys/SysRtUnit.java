package net.doohad.models.sys;

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

@Entity
@Table(name="SYS_RT_UNITS")
public class SysRtUnit {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RT_UNIT_ID")
	private int id;

	// 식별자
	@Column(name = "UKID", nullable = false, length = 30, unique = true)
	private String ukid;

	// 서비스 응답시간 유닛
	@Column(name = "NAME", nullable = false, length = 50)
	private String name = "";
	
	// 활성화 여부
	@Column(name = "ACTIVE", nullable = false)
	private boolean active = true; 
	
	
	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	// WHO 컬럼들(E)
	
	
	// 다른 개체 연결(S)

	// 하위 개체: 서비스 응답시간
	@OneToMany(mappedBy = "rtUnit", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<SysSvcRespTime> svcRespTimes = new HashSet<SysSvcRespTime>(0);
	
	// 다른 개체 연결(E)
	
	
	public SysRtUnit() {}
	
	public SysRtUnit(String ukid) {

		this(ukid, "", true);
	}
	
	public SysRtUnit(String ukid, String name, boolean active) {
		
		this.ukid = ukid;
		this.name = name;
		this.active = active;
		
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getWhoCreationDate() {
		return whoCreationDate;
	}

	public void setWhoCreationDate(Date whoCreationDate) {
		this.whoCreationDate = whoCreationDate;
	}
	
}
