package net.doohad.models.rev;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.doohad.models.adc.AdcAdCreative;
import net.doohad.models.adc.AdcCreative;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.knl.KnlMedium;

@Entity
@Table(name="REV_AD_SELECTS")
public class RevAdSelect {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AD_SELECT_ID")
	private int id;
	
	// 광고 선택 UUID
	//
	//   이 값으로 결과 보고 진행
	//
	@Column(name = "UUID", nullable = false, columnDefinition = "binary(16)")
	private UUID uuid;
	
	// 광고 선택 일시 == whoCreationDate
	@Column(name = "SEL_DATE", nullable = false)
	private Date selectDate;
	
	// 보고 일시
	@Column(name = "REPORT_DATE")
	private Date reportDate;
	
	// 보고 결과
	//
	//    true		성공
	//    false		실패
	//    null		초기 상태. 광고만 선택되고 아직 보고받지 못한 상태
	//
	@Column(name = "RESULT")
	private Boolean result;

	
	// 방송 시작 일시(Optional)
	@Column(name = "PLAY_BEGIN_DATE")
	private Date playBeginDate;
	
	// 방송 종료 일시(Optional)
	@Column(name = "PLAY_END_DATE")
	private Date playEndDate;
	
	// 재생 시간(밀리초 단위)(Optional)
	//
	//   최대 1시간(60 x 60 x 1000)
	//
	@Column(name = "DURATION")
	private Integer duration;


	// WHO 컬럼들(S)
	
	//   기기에 의해 자동 생성되고, 상당한 크기가 예상되므로 WHO 컬럼 생략

	// WHO 컬럼들(E)
	
	
	// 다른 개체 연결(S)
	
	// 상위 개체: 매체
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MEDIUM_ID", nullable = false)
	private KnlMedium medium;
	
	// 상위 개체: 광고 소재
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CREATIVE_ID", nullable = false)
	private AdcCreative creative;
	
	// 상위 개체: 화면
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SCREEN_ID", nullable = false)
	private InvScreen screen;
	
	// 상위 개체: 광고/광고 소재
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "AD_CREATIVE_ID", nullable = false)
	private AdcAdCreative adCreative;
	
	// 다른 개체 연결(E)
	
	
	public RevAdSelect() {}
	
	public RevAdSelect(InvScreen screen, AdcAdCreative adCreative) {
		
		this.medium = screen.getMedium();
		this.screen = screen;
		this.creative = adCreative.getCreative();
		this.adCreative = adCreative;
		
		this.uuid = UUID.randomUUID();
		this.selectDate = new Date();
	}
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public Date getSelectDate() {
		return selectDate;
	}

	public void setSelectDate(Date selectDate) {
		this.selectDate = selectDate;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public Boolean getResult() {
		return result;
	}

	public void setResult(Boolean result) {
		this.result = result;
	}

	public Date getPlayBeginDate() {
		return playBeginDate;
	}

	public void setPlayBeginDate(Date playBeginDate) {
		this.playBeginDate = playBeginDate;
	}

	public Date getPlayEndDate() {
		return playEndDate;
	}

	public void setPlayEndDate(Date playEndDate) {
		this.playEndDate = playEndDate;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public KnlMedium getMedium() {
		return medium;
	}

	public void setMedium(KnlMedium medium) {
		this.medium = medium;
	}

	public AdcCreative getCreative() {
		return creative;
	}

	public void setCreative(AdcCreative creative) {
		this.creative = creative;
	}

	public InvScreen getScreen() {
		return screen;
	}

	public void setScreen(InvScreen screen) {
		this.screen = screen;
	}

	public AdcAdCreative getAdCreative() {
		return adCreative;
	}

	public void setAdCreative(AdcAdCreative adCreative) {
		this.adCreative = adCreative;
	}

}
