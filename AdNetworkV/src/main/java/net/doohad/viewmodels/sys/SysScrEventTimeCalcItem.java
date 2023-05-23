package net.doohad.viewmodels.sys;

import java.util.Date;

import javax.persistence.Tuple;

public class SysScrEventTimeCalcItem {

	private int screenId;
	private Date date;
	private int type;
	
	private int cnt00, cnt01, cnt02, cnt03, cnt04, cnt05;
	private int cnt06, cnt07, cnt08, cnt09, cnt10, cnt11;
	private int cnt12, cnt13, cnt14, cnt15, cnt16, cnt17;
	private int cnt18, cnt19, cnt20, cnt21, cnt22, cnt23;
	
	
	public SysScrEventTimeCalcItem() {}

	public SysScrEventTimeCalcItem(Tuple tuple) {
		this.screenId = (int) tuple.get(0);
		this.date = (Date) tuple.get(1);
		this.type = (int) tuple.get(2);
	}
	
	public void addCount(int hourOfDay) {
		
        switch (hourOfDay) {
        case 0: cnt00 += 1; break;
        case 1: cnt01 += 1; break;
        case 2: cnt02 += 1; break;
        case 3: cnt03 += 1; break;
        case 4: cnt04 += 1; break;
        case 5: cnt05 += 1; break;
        case 6: cnt06 += 1; break;
        case 7: cnt07 += 1; break;
        case 8: cnt08 += 1; break;
        case 9: cnt09 += 1; break;
        case 10: cnt10 += 1; break;
        case 11: cnt11 += 1; break;
        case 12: cnt12 += 1; break;
        case 13: cnt13 += 1; break;
        case 14: cnt14 += 1; break;
        case 15: cnt15 += 1; break;
        case 16: cnt16 += 1; break;
        case 17: cnt17 += 1; break;
        case 18: cnt18 += 1; break;
        case 19: cnt19 += 1; break;
        case 20: cnt20 += 1; break;
        case 21: cnt21 += 1; break;
        case 22: cnt22 += 1; break;
        case 23: cnt23 += 1; break;
        }
	}

	
	public int getScreenId() {
		return screenId;
	}

	public void setScreenId(int screenId) {
		this.screenId = screenId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getCnt00() {
		return cnt00;
	}

	public void setCnt00(int cnt00) {
		this.cnt00 = cnt00;
	}

	public int getCnt01() {
		return cnt01;
	}

	public void setCnt01(int cnt01) {
		this.cnt01 = cnt01;
	}

	public int getCnt02() {
		return cnt02;
	}

	public void setCnt02(int cnt02) {
		this.cnt02 = cnt02;
	}

	public int getCnt03() {
		return cnt03;
	}

	public void setCnt03(int cnt03) {
		this.cnt03 = cnt03;
	}

	public int getCnt04() {
		return cnt04;
	}

	public void setCnt04(int cnt04) {
		this.cnt04 = cnt04;
	}

	public int getCnt05() {
		return cnt05;
	}

	public void setCnt05(int cnt05) {
		this.cnt05 = cnt05;
	}

	public int getCnt06() {
		return cnt06;
	}

	public void setCnt06(int cnt06) {
		this.cnt06 = cnt06;
	}

	public int getCnt07() {
		return cnt07;
	}

	public void setCnt07(int cnt07) {
		this.cnt07 = cnt07;
	}

	public int getCnt08() {
		return cnt08;
	}

	public void setCnt08(int cnt08) {
		this.cnt08 = cnt08;
	}

	public int getCnt09() {
		return cnt09;
	}

	public void setCnt09(int cnt09) {
		this.cnt09 = cnt09;
	}

	public int getCnt10() {
		return cnt10;
	}

	public void setCnt10(int cnt10) {
		this.cnt10 = cnt10;
	}

	public int getCnt11() {
		return cnt11;
	}

	public void setCnt11(int cnt11) {
		this.cnt11 = cnt11;
	}

	public int getCnt12() {
		return cnt12;
	}

	public void setCnt12(int cnt12) {
		this.cnt12 = cnt12;
	}

	public int getCnt13() {
		return cnt13;
	}

	public void setCnt13(int cnt13) {
		this.cnt13 = cnt13;
	}

	public int getCnt14() {
		return cnt14;
	}

	public void setCnt14(int cnt14) {
		this.cnt14 = cnt14;
	}

	public int getCnt15() {
		return cnt15;
	}

	public void setCnt15(int cnt15) {
		this.cnt15 = cnt15;
	}

	public int getCnt16() {
		return cnt16;
	}

	public void setCnt16(int cnt16) {
		this.cnt16 = cnt16;
	}

	public int getCnt17() {
		return cnt17;
	}

	public void setCnt17(int cnt17) {
		this.cnt17 = cnt17;
	}

	public int getCnt18() {
		return cnt18;
	}

	public void setCnt18(int cnt18) {
		this.cnt18 = cnt18;
	}

	public int getCnt19() {
		return cnt19;
	}

	public void setCnt19(int cnt19) {
		this.cnt19 = cnt19;
	}

	public int getCnt20() {
		return cnt20;
	}

	public void setCnt20(int cnt20) {
		this.cnt20 = cnt20;
	}

	public int getCnt21() {
		return cnt21;
	}

	public void setCnt21(int cnt21) {
		this.cnt21 = cnt21;
	}

	public int getCnt22() {
		return cnt22;
	}

	public void setCnt22(int cnt22) {
		this.cnt22 = cnt22;
	}

	public int getCnt23() {
		return cnt23;
	}

	public void setCnt23(int cnt23) {
		this.cnt23 = cnt23;
	}

}
