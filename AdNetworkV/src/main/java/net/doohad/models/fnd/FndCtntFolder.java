package net.doohad.models.fnd;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.doohad.models.adc.AdcCreatFile;
import net.doohad.utils.Util;


@Entity
@Table(name="FND_CTNT_FOLDERS")
public class FndCtntFolder {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "fnd_ctnt_folder_seq_gen")
	@Column(name = "CTNT_FOLDER_ID")
	private int id;
	
	// 폴더명
	@Column(name = "NAME", nullable = false, length = 20, unique = true)
	private String name;

	// 웹 접근 경로
	@Column(name = "WEB_PATH", nullable = false, length = 100)
	private String webPath;

	// 로컬 네트워크 접근 경로(웹 애플리케이션 기준)
	//
	//   개발자 환경 : 네트워크 드라이브 명시. 예) Z:
	//   서버운영환경: IP 및 사용자 명시. 예) \\121.254.176.103\tomcat
	//
	@Column(name = "LOCAL_PATH", nullable = false, length = 100)
	private String localPath;
	
	// 현재 기본 이용 여부
	@Column(name = "CURR", nullable = false)
	private boolean curr; 
	
	
	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "LAST_UPDATE_DATE", nullable = false)
	private Date whoLastUpdateDate;
	
	@Column(name = "CREATED_BY", nullable = false)
	private int whoCreatedBy;
	
	@Column(name = "LAST_UPDATED_BY", nullable = false)
	private int whoLastUpdatedBy;
	
	@Column(name = "LAST_UPDATE_LOGIN", nullable = false)
	private int whoLastUpdateLogin;
	// WHO 컬럼들(E)
	
	
	// 소재 파일의 갯수
	@Transient
	private int creatFileCount = 0;
	
	
	// 다른 개체 연결(S)
	
	// 하위 개체: 광고 소재 파일
	//
	//   컨텐츠 폴더가 삭제될 때 광고 소재 파일이 삭제되면 안됨!!
	//   cascade = CascadeType.REMOVE 제외
	//
	@OneToMany(mappedBy = "creative", fetch = FetchType.LAZY)
	private Set<AdcCreatFile> creatFiles = new HashSet<AdcCreatFile>(0);
	
	// 다른 개체 연결(E)
	

	public FndCtntFolder() {}
	
	public FndCtntFolder(String name, String webPath, String localPath, HttpSession session) {
		this.name = name;
		this.webPath = webPath;
		this.localPath = localPath;
		
		touchWhoC(session);
	}

	private void touchWhoC(HttpSession session) {
		this.whoCreatedBy = Util.loginUserId(session);
		this.whoCreationDate = new Date();
		touchWho(session);
	}
	
	public void touchWho(HttpSession session) {
		this.whoLastUpdatedBy = Util.loginUserId(session);
		this.whoLastUpdateDate = new Date();
		this.whoLastUpdateLogin = Util.loginId(session);
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWebPath() {
		return webPath;
	}

	public void setWebPath(String webPath) {
		this.webPath = webPath;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public boolean isCurr() {
		return curr;
	}

	public void setCurr(boolean curr) {
		this.curr = curr;
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

	public int getWhoCreatedBy() {
		return whoCreatedBy;
	}

	public void setWhoCreatedBy(int whoCreatedBy) {
		this.whoCreatedBy = whoCreatedBy;
	}

	public int getWhoLastUpdatedBy() {
		return whoLastUpdatedBy;
	}

	public void setWhoLastUpdatedBy(int whoLastUpdatedBy) {
		this.whoLastUpdatedBy = whoLastUpdatedBy;
	}

	public int getWhoLastUpdateLogin() {
		return whoLastUpdateLogin;
	}

	public void setWhoLastUpdateLogin(int whoLastUpdateLogin) {
		this.whoLastUpdateLogin = whoLastUpdateLogin;
	}

	@JsonIgnore
	public Set<AdcCreatFile> getCreatFiles() {
		return creatFiles;
	}

	public void setCreatFiles(Set<AdcCreatFile> creatFiles) {
		this.creatFiles = creatFiles;
	}

	public int getCreatFileCount() {
		return creatFileCount;
	}

	public void setCreatFileCount(int creatFileCount) {
		this.creatFileCount = creatFileCount;
	}

}
