package net.doohad.models.knl;

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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.doohad.models.fnd.FndLoginLog;
import net.doohad.models.fnd.FndUserPriv;
import net.doohad.utils.Util;

@Entity
@Table(name="KNL_USERS")
public class KnlUser {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "knl_user_seq_gen")
	@Column(name = "USER_ID")
	private int id;
	
	//
	//  사용자 역할(Role)
	//
	//    M1	Genernal Manager
	//    M2	Manager
	//    S1	Scheduler
	//    SM	Sales Manager
	//    R1	Report only
	//    R2	Read only
	//
	
	// 사용자ID
	//
	//    삭제 요청 시 이 값이 변경되기 때문에, 실제 입력받을 시의 길이는 50으로 함
	//
	@Column(name = "SHORT_NAME", nullable = false, length = 70, unique = true)
	private String shortName;
	
	// 사용자명
	@Column(name = "NAME", nullable = false, length = 100)
	private String name;

	// 삭제 여부
	//
	//   소프트 삭제 플래그
	//
	//   삭제 요청 시 아래 값처럼 변경하고 실제 삭제는 진행하지 않음
	//
	//        shortName = shortName + '_yyyyMMdd_HHmm'
	//
	@Column(name = "DELETED", nullable = false)
	private boolean deleted;

	
	// 암호화를 위한 SALT 값(내부용)
	@Column(name = "SALT", nullable = false, length = 22)
	private String salt;
	
	// 패스워드
	@Column(name = "PASSWORD", nullable = false, length = 50)
	private String password;
	
	// 패스워드 변경일시(기록용)
	@Column(name = "PASSWORD_UPDATE_DATE", nullable = false)
	private Date passwordUpdateDate;

	// 역할
	//
	//    대상 유형(커널, 매체, 광고)에 대한 공통 이름 역할 지정
	//
	//    유효 값 목록:
	//        M1 - 총괄 관리자(*)
	//        M2 - 관리자(*)
	//        S1 - 스케줄러
	//        SM - 영업 관리자
	//        AA - 광고 승인자(*)
	//        R1 - 보고 조회
	//        R2 - 읽기 전용
	//
	@Column(name = "ROLE", nullable = false, length = 5)
	private String role = "";
	
	// 운영자 메모
	@Column(name = "MEMO", length = 300)
	private String memo;


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
	
	
	// 최근 로그인 일시
	@Transient
	private Date lastLoginDate;
	
	
	// 다른 개체 연결(S)
	
	// 하위 개체: 로그인 로그
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<FndLoginLog> loginLogs = new HashSet<FndLoginLog>(0);
	
	// 하위 개체: 사용자 권한
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<FndUserPriv> userPrivs = new HashSet<FndUserPriv>(0);
	
	// 상위 개체: 계정
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ACCOUNT_ID", nullable = false)
	private KnlAccount account;
	
	// 다른 개체 연결(E)
	
	
	public KnlUser() {}
	
	public KnlUser(KnlAccount account, 
			String shortName, String name, String password, String role, String memo, HttpSession session) {
		
		this.account = account;
		
		this.shortName = shortName;
		this.name = name;
		this.salt = Util.getRandomSalt();
		this.password = Util.encrypt(password, salt);
		this.role = role;
		this.memo = memo;
		
		touchWhoC(session);
	}

	private void touchWhoC(HttpSession session) {
		this.whoCreatedBy = Util.loginUserId(session);
		this.whoCreationDate = new Date();
		this.passwordUpdateDate = new Date();
		
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

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getPasswordUpdateDate() {
		return passwordUpdateDate;
	}

	public void setPasswordUpdateDate(Date passwordUpdateDate) {
		this.passwordUpdateDate = passwordUpdateDate;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
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

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@JsonIgnore
	public Set<FndLoginLog> getLoginLogs() {
		return loginLogs;
	}

	public void setLoginLogs(Set<FndLoginLog> loginLogs) {
		this.loginLogs = loginLogs;
	}

	@JsonIgnore
	public Set<FndUserPriv> getUserPrivs() {
		return userPrivs;
	}

	public void setUserPrivs(Set<FndUserPriv> userPrivs) {
		this.userPrivs = userPrivs;
	}

	public KnlAccount getAccount() {
		return account;
	}

	public void setAccount(KnlAccount account) {
		this.account = account;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

}
