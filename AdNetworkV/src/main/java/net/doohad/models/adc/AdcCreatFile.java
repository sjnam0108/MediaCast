package net.doohad.models.adc;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
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
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

import net.doohad.models.fnd.FndCtntFolder;
import net.doohad.models.knl.KnlMedium;
import net.doohad.utils.Util;

@Entity
@Table(name="ADC_CREAT_FILES", uniqueConstraints = {
	@javax.persistence.UniqueConstraint(columnNames = {"CREATIVE_ID", "RESOLUTION"}),
})
public class AdcCreatFile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CREAT_FILE_ID")
	private int id;
	
	// 삭제 여부
	//
	//   소프트 삭제 플래그
	//
	//   삭제 요청 시 아래 값처럼 변경하고 실제 삭제는 진행하지 않음
	//
	//        resolution = resolution + '_yyyyMMdd_HHmm'
	//
	@Column(name = "DELETED", nullable = false)
	private boolean deleted;

	//
	// 파일 업로드에서 최종 위치 폴더까지 정리
	//
	//   - 업로드 임시 위치: getPhysicalRoot("UpCtntTemp") + "/ABCD1234/" + 원본파일명
	//   - 썸네일 임시 위치: getPhysicalRoot("UpCtntTemp") + "/ABCD1234/th.png"
	//
	//   - 미디어 파일 위치: ctntFolder.getLocalPath() + "/" + ctntFolder.getName() + "/" + uuid.toString()  + "/" + "V00100.mp4"
	//   - 썸네일 임시 위치: getPhysicalRoot("Thumb") + "/" + ctntFolder.getName() + "/" + uuid.toString()  + "/" + "th.png"
	//
	
	// 등록 전 파일명: 예) 빅히트_3840.mp4
	@Column(name = "SRC_FILENAME", nullable = false, length = 100)
	private String srcFilename = "";
	
	// 파일명
	//
	//   leading zero 형식. 최대 99999까지. 예) V00015.mp4
	//
	@Column(name = "FILENAME", nullable = false, length = 100)
	private String filename = "";
	
	// 파일크기
	@Column(name = "FILE_LENGTH", nullable = false)
	private long fileLength = 0;

	// 썸네일 파일명
	//
	//   {uuid}.{소스파일 파일확장명}
	//
	@Column(name = "THUMB_FILENAME", nullable = false, length = 100)
	private String thumbFilename = "";
	
	// 미디어 유형
	//
	//   V	동영상
	//   I  이미지
	//
	@Column(name = "MEDIA_TYPE", nullable = false, length = 1)
	private String mediaType = "V";
	
	// 화면 해상도
	//   픽셀 단위 가로x세로 형식으로 저장: 예) 1920x1080
	//
	@Column(name = "RESOLUTION", nullable = false, length = 40)
	private String resolution;
	
	// MIME TYPE
	@Column(name = "MIME_TYPE", length = 100)
	private String mimeType = "";
	
	// UUID
	@Column(name = "UUID", nullable = false, columnDefinition = "binary(16)")
	private UUID uuid;
	
	// 외부 광고 소재 파일 UUID
	@Column(name = "EXT_UUID", nullable = false, columnDefinition = "binary(16)")
	private UUID extUuid;
	
	
	// 재생 시간 및 원본 재생 시간은 파일로부터 그 시간 값을 설정하게 된다
	//   원본 재생 시간값에 따라 반올림하여 정수값의 재생 시간을 설정한다
	//
	// 재생 시간
	@Column(name = "DURATION", nullable = false)
	private int durSecs = 0;
	
	// 원본 재생 시간
	//
	//   V 유형의 경우는 원본의 재생 시간
	//   I 유형의 경우는 0
	//
	@Column(name = "SRC_DURATION", nullable = false)
	private double srcDurSecs = 0;
	

	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "CREATED_BY", nullable = false)
	private int whoCreatedBy;
	
	@Column(name = "LAST_UPDATE_LOGIN", nullable = false)
	private int whoLastUpdateLogin;
	// WHO 컬럼들(E)
	
	
	// 소재 파일의 해상도 적합도
	//
	//     1  -  가장 적합(매체의 선택된 해상도와 정확히 일치)
	//     0  -  매체의 선택된 해상도 중 하나와 15% 범위 내 비율
	//    -1  -  매체의 어떤 해상도와도 15% 범위를 벗어남
	//
	@Transient
	private int fitnessOfResRatio = 0;
	
	// 공식적인 재생 시간
	// 
	//   동영상인 경우 명확한 재생 시간 정보가 설정되어 있으나, 나머지 유형인 경우에는
	//   현재 값과 달리 외부적인 값(매체의 기본값이나, 화면의 재정의값)이 전달되어야 하는 경우에 이용됨
	//   durSecs에 우선해서 null이 아닐 경우 이 값이 우선된다. 
	@Transient
	private Integer formalDurSecs = null;
	
	
	// 다른 개체 연결(S)
	
	// 상위 개체: 매체
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MEDIUM_ID", nullable = false)
	private KnlMedium medium;
	
	// 상위 개체: 광고 소재
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CREATIVE_ID", nullable = false)
	private AdcCreative creative;
	
	// 상위 개체: 컨텐츠 폴더
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CTNT_FOLDER_ID", nullable = false)
	private FndCtntFolder ctntFolder;
	
	// 다른 개체 연결(E)
	

	public AdcCreatFile() {}
	
	public AdcCreatFile(AdcCreative creative, FndCtntFolder ctntFolder,
			String srcFilename, long fileLength, String mediaType, String resolution, 
			String mimeType, UUID uuid, UUID extUuid, double srcDurSecs, HttpSession session) {
		this.medium = creative.getMedium();
		this.creative = creative;
		this.ctntFolder = ctntFolder;
		
		this.srcFilename = srcFilename;
		this.fileLength = fileLength;
		this.mediaType = mediaType;
		this.resolution = resolution;
		this.mimeType = mimeType;
		this.uuid = uuid;
		this.extUuid = extUuid;
		this.srcDurSecs = srcDurSecs;
		
		// 최소 재생시간을 5초로 설정(이미지 제외)
		if (this.mediaType.equals("I")) {
			this.srcDurSecs = 0d;
		} else {
			this.durSecs = srcDurSecs > 5 ? (int) Math.round(srcDurSecs) : 5;
		}
		
		
		this.whoCreatedBy = Util.loginUserId(session);
		this.whoCreationDate = new Date();
		this.whoLastUpdateLogin = Util.loginId(session);
	}

	
	public String getDispSrcDurSecs() {
		return new DecimalFormat("###,##0.00").format(srcDurSecs) + "s";
	}

	public String getSmartLength() {
		return Util.getSmartFileLength(fileLength);
	}
	
	public String getDispFileLength() {
		return new DecimalFormat("##,###,###,##0").format(fileLength) + " bytes";
	}
	
	public long getResolutionArea() {
		List<String> items = Util.tokenizeValidStr(resolution, "x");
		if (items.size() == 2) {
			return Util.parseInt(items.get(0), 0) * Util.parseInt(items.get(1), 0);
		} else {
			return 0;
		}
	}
	
	public String getHttpFilename() {
		return ctntFolder.getWebPath() + "/" + ctntFolder.getName() + "/" + uuid.toString() + "/" + filename;
	}
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSrcFilename() {
		return srcFilename;
	}

	public void setSrcFilename(String srcFilename) {
		this.srcFilename = srcFilename;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public long getFileLength() {
		return fileLength;
	}

	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}

	public String getThumbFilename() {
		return thumbFilename;
	}

	public void setThumbFilename(String thumbFilename) {
		this.thumbFilename = thumbFilename;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public UUID getExtUuid() {
		return extUuid;
	}

	public void setExtUuid(UUID extUuid) {
		this.extUuid = extUuid;
	}

	public int getDurSecs() {
		return (formalDurSecs == null) ? durSecs : formalDurSecs.intValue();
	}

	public void setDurSecs(int durSecs) {
		this.durSecs = durSecs;
	}

	public double getSrcDurSecs() {
		return srcDurSecs;
	}

	public void setSrcDurSecs(double srcDurSecs) {
		this.srcDurSecs = srcDurSecs;
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

	public int getWhoLastUpdateLogin() {
		return whoLastUpdateLogin;
	}

	public void setWhoLastUpdateLogin(int whoLastUpdateLogin) {
		this.whoLastUpdateLogin = whoLastUpdateLogin;
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

	public FndCtntFolder getCtntFolder() {
		return ctntFolder;
	}

	public void setCtntFolder(FndCtntFolder ctntFolder) {
		this.ctntFolder = ctntFolder;
	}

	public int getFitnessOfResRatio() {
		return fitnessOfResRatio;
	}

	public void setFitnessOfResRatio(int fitnessOfResRatio) {
		this.fitnessOfResRatio = fitnessOfResRatio;
	}

	public Integer getFormalDurSecs() {
		return formalDurSecs;
	}

	public void setFormalDurSecs(Integer formalDurSecs) {
		this.formalDurSecs = formalDurSecs;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
}
