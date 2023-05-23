package net.doohad.models.sys;

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

@Entity
@Table(name="SYS_SVC_RESP_TIMES", uniqueConstraints = {
	@javax.persistence.UniqueConstraint(columnNames = {"RT_UNIT_ID", "CHK_DATE"}),
})
public class SysSvcRespTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SVC_RESP_TIME_ID")
	private int id;

	// 측정 일시(분 단위까지만 유효)
	@Column(name = "CHK_DATE", nullable = false)
	private Date checkDate = new Date();

	// 응답시간
	@Column(name = "TIME_MILLIS", nullable = false)
	private int timeMillis;

	// 동일 측정 일시에서의 보고 횟수
	@Column(name = "COUNT", nullable = false)
	private int count = 0;
	
	
	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	// WHO 컬럼들(E)
	
	
	// 다른 개체 연결(S)
	
	// 상위 개체: 서비스 응답시간 유닛
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "RT_UNIT_ID", nullable = false)
	private SysRtUnit rtUnit;
	
	// 다른 개체 연결(E)
	
	
	public SysSvcRespTime() {}
	
	public SysSvcRespTime(SysRtUnit rtUnit, Date checkDate, int timeMillis) {
		
		this.rtUnit = rtUnit;
		this.checkDate = checkDate;
		this.timeMillis = timeMillis;
		this.count = 1;
		
		this.whoCreationDate = new Date();
	}
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}

	public int getTimeMillis() {
		return timeMillis;
	}

	public void setTimeMillis(int timeMillis) {
		this.timeMillis = timeMillis;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Date getWhoCreationDate() {
		return whoCreationDate;
	}

	public void setWhoCreationDate(Date whoCreationDate) {
		this.whoCreationDate = whoCreationDate;
	}

	public SysRtUnit getRtUnit() {
		return rtUnit;
	}

	public void setRtUnit(SysRtUnit rtUnit) {
		this.rtUnit = rtUnit;
	}

}
