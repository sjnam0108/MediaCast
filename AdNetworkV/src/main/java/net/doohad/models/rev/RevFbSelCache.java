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

import net.doohad.models.adc.AdcCreative;
import net.doohad.models.inv.InvScreen;

@Entity
@Table(name="REV_FB_SEL_CACHES")
public class RevFbSelCache {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "FB_SEL_CACHE_ID")
	private int id;
	
	// 광고 선택 일시 == whoCreationDate
	@Column(name = "SEL_DATE", nullable = false)
	private Date selectDate;


	// WHO 컬럼들(S)
	
	//   기기에 의해 자동 생성되고, 상당한 크기가 예상되므로 WHO 컬럼 생략

	// WHO 컬럼들(E)
	
	
	// 다른 개체 연결(S)
	
	// 상위 개체: 화면
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SCREEN_ID", nullable = false)
	private InvScreen screen;
	
	// 상위 개체: 광고 소재
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CREATIVE_ID", nullable = false)
	private AdcCreative creative;
	
	// 다른 개체 연결(E)
	
	
	public RevFbSelCache() {}
	
	public RevFbSelCache(InvScreen screen, AdcCreative creative, Date selectDate) {
		
		this.screen = screen;
		this.creative = creative;
		this.selectDate = selectDate;
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getSelectDate() {
		return selectDate;
	}

	public void setSelectDate(Date selectDate) {
		this.selectDate = selectDate;
	}

	public InvScreen getScreen() {
		return screen;
	}

	public void setScreen(InvScreen screen) {
		this.screen = screen;
	}

	public AdcCreative getCreative() {
		return creative;
	}

	public void setCreative(AdcCreative creative) {
		this.creative = creative;
	}
	
}
