package net.doohad.models.adn;

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

import net.doohad.models.knl.KnlMedium;
import net.doohad.utils.Util;

@Entity
@Table(name="ADN_EXCEL_ROWS")
public class AdnExcelRow {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "adn_excel_row_seq_gen")
	@Column(name = "EXCEL_ROW_ID")
	private int id;
	
	// 유형
	//
	//    사이트	T
	//    화면		C
	//
	@Column(name = "TYPE", nullable = false, length = 1)
	private String type = "";
	
	// DB upsert 결과 코드
	//
	//    초기		I
	//    성공		S
	//    실패		F
	//    패스      P
	//
	@Column(name = "RESULT", nullable = false, length = 1)
	private String result = "I";
	
	// A 컬럼
	@Column(name = "COL_A", length = 200)
	private String colA;
	
	// B 컬럼
	@Column(name = "COL_B", length = 200)
	private String colB;
	
	// C 컬럼
	@Column(name = "COL_C", length = 200)
	private String colC;
	
	// D 컬럼
	@Column(name = "COL_D", length = 200)
	private String colD;
	
	// E 컬럼
	@Column(name = "COL_E", length = 200)
	private String colE;
	
	// F 컬럼
	@Column(name = "COL_F", length = 200)
	private String colF;
	
	// G 컬럼
	@Column(name = "COL_G", length = 200)
	private String colG;
	
	// H 컬럼
	@Column(name = "COL_H", length = 200)
	private String colH;
	
	// I 컬럼
	@Column(name = "COL_I", length = 200)
	private String colI;
	
	// J 컬럼
	@Column(name = "COL_J", length = 200)
	private String colJ;
	
	// K 컬럼
	@Column(name = "COL_K", length = 200)
	private String colK;
	
	// L 컬럼
	@Column(name = "COL_L", length = 200)
	private String colL;
	
	
	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "CREATED_BY", nullable = false)
	private int whoCreatedBy;
	
	@Column(name = "LAST_UPDATE_LOGIN", nullable = false)
	private int whoLastUpdateLogin;
	// WHO 컬럼들(E)
	
	
	// 다른 개체 연결(S)
	
	// 상위 개체: 매체
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MEDIUM_ID", nullable = false)
	private KnlMedium medium;
	
	// 다른 개체 연결(E)
	
	
	public AdnExcelRow() {}
	
	public AdnExcelRow(KnlMedium medium, String type, HttpSession session) {
		
		this.medium = medium;
		this.type = type;
		
		touchWhoC(session);
	}

	private void touchWhoC(HttpSession session) {
		this.whoCreatedBy = Util.loginUserId(session);
		this.whoCreationDate = new Date();
		this.whoLastUpdateLogin = Util.loginId(session);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getColA() {
		return colA;
	}

	public void setColA(String colA) {
		this.colA = colA;
	}

	public String getColB() {
		return colB;
	}

	public void setColB(String colB) {
		this.colB = colB;
	}

	public String getColC() {
		return colC;
	}

	public void setColC(String colC) {
		this.colC = colC;
	}

	public String getColD() {
		return colD;
	}

	public void setColD(String colD) {
		this.colD = colD;
	}

	public String getColE() {
		return colE;
	}

	public void setColE(String colE) {
		this.colE = colE;
	}

	public String getColF() {
		return colF;
	}

	public void setColF(String colF) {
		this.colF = colF;
	}

	public String getColG() {
		return colG;
	}

	public void setColG(String colG) {
		this.colG = colG;
	}

	public String getColH() {
		return colH;
	}

	public void setColH(String colH) {
		this.colH = colH;
	}

	public String getColI() {
		return colI;
	}

	public void setColI(String colI) {
		this.colI = colI;
	}

	public String getColJ() {
		return colJ;
	}

	public void setColJ(String colJ) {
		this.colJ = colJ;
	}

	public String getColK() {
		return colK;
	}

	public void setColK(String colK) {
		this.colK = colK;
	}

	public String getColL() {
		return colL;
	}

	public void setColL(String colL) {
		this.colL = colL;
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

}
