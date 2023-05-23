package net.doohad.viewmodels.rev;

import javax.persistence.Tuple;

public class RevScrHrlyPlySumItem {

	private int id;
	private int destId;
	
	private int adCnt;
	
	private int cnt00, cnt01, cnt02, cnt03, cnt04, cnt05;
	private int cnt06, cnt07, cnt08, cnt09, cnt10, cnt11;
	private int cnt12, cnt13, cnt14, cnt15, cnt16, cnt17;
	private int cnt18, cnt19, cnt20, cnt21, cnt22, cnt23;
	
	private int succTot, failTot, dateTot;
	
	private int screenCnt = 1;
	
	
	public RevScrHrlyPlySumItem() {}
	
	public RevScrHrlyPlySumItem(Tuple tuple, boolean fromSrc) {
		
		if (fromSrc) {
			destId = (Integer) tuple.get(0);
			adCnt = ((Long) tuple.get(1)).intValue();
			
			cnt00 = ((Long) tuple.get(2)).intValue();
			cnt01 = ((Long) tuple.get(3)).intValue();
			cnt02 = ((Long) tuple.get(4)).intValue();
			cnt03 = ((Long) tuple.get(5)).intValue();
			cnt04 = ((Long) tuple.get(6)).intValue();
			cnt05 = ((Long) tuple.get(7)).intValue();
			cnt06 = ((Long) tuple.get(8)).intValue();
			cnt07 = ((Long) tuple.get(9)).intValue();
			cnt08 = ((Long) tuple.get(10)).intValue();
			cnt09 = ((Long) tuple.get(11)).intValue();
			cnt10 = ((Long) tuple.get(12)).intValue();
			cnt11 = ((Long) tuple.get(13)).intValue();
			cnt12 = ((Long) tuple.get(14)).intValue();
			cnt13 = ((Long) tuple.get(15)).intValue();
			cnt14 = ((Long) tuple.get(16)).intValue();
			cnt15 = ((Long) tuple.get(17)).intValue();
			cnt16 = ((Long) tuple.get(18)).intValue();
			cnt17 = ((Long) tuple.get(19)).intValue();
			cnt18 = ((Long) tuple.get(20)).intValue();
			cnt19 = ((Long) tuple.get(21)).intValue();
			cnt20 = ((Long) tuple.get(22)).intValue();
			cnt21 = ((Long) tuple.get(23)).intValue();
			cnt22 = ((Long) tuple.get(24)).intValue();
			cnt23 = ((Long) tuple.get(25)).intValue();

			succTot = ((Long) tuple.get(26)).intValue();
			failTot = ((Long) tuple.get(27)).intValue();
			dateTot = ((Long) tuple.get(28)).intValue();
		} else {
			destId = (Integer) tuple.get(0);
			adCnt = (Integer) tuple.get(1);
			
			cnt00 = (Integer) tuple.get(2);
			cnt01 = (Integer) tuple.get(3);
			cnt02 = (Integer) tuple.get(4);
			cnt03 = (Integer) tuple.get(5);
			cnt04 = (Integer) tuple.get(6);
			cnt05 = (Integer) tuple.get(7);
			cnt06 = (Integer) tuple.get(8);
			cnt07 = (Integer) tuple.get(9);
			cnt08 = (Integer) tuple.get(10);
			cnt09 = (Integer) tuple.get(11);
			cnt10 = (Integer) tuple.get(12);
			cnt11 = (Integer) tuple.get(13);
			cnt12 = (Integer) tuple.get(14);
			cnt13 = (Integer) tuple.get(15);
			cnt14 = (Integer) tuple.get(16);
			cnt15 = (Integer) tuple.get(17);
			cnt16 = (Integer) tuple.get(18);
			cnt17 = (Integer) tuple.get(19);
			cnt18 = (Integer) tuple.get(20);
			cnt19 = (Integer) tuple.get(21);
			cnt20 = (Integer) tuple.get(22);
			cnt21 = (Integer) tuple.get(23);
			cnt22 = (Integer) tuple.get(24);
			cnt23 = (Integer) tuple.get(25);

			succTot = (Integer) tuple.get(26);
			failTot = (Integer) tuple.get(27);
			dateTot = (Integer) tuple.get(28);
			
			id = (Integer) tuple.get(29);
		}
	}
	
	
	public boolean isSameData(RevScrHrlyPlySumItem sumItem) {
		
		if (sumItem == null) {
			return false;
		}
		
		return adCnt == sumItem.getAdCnt() && cnt00 == sumItem.getCnt00() && cnt01 == sumItem.getCnt01() &&
				cnt02 == sumItem.getCnt02() && cnt03 == sumItem.getCnt03() && cnt04 == sumItem.getCnt04() &&
				cnt05 == sumItem.getCnt05() && cnt06 == sumItem.getCnt06() && cnt07 == sumItem.getCnt07() &&
				cnt08 == sumItem.getCnt08() && cnt09 == sumItem.getCnt09() && cnt10 == sumItem.getCnt10() &&
				cnt11 == sumItem.getCnt11() && cnt12 == sumItem.getCnt12() && cnt13 == sumItem.getCnt13() &&
				cnt14 == sumItem.getCnt14() && cnt15 == sumItem.getCnt15() && cnt16 == sumItem.getCnt16() &&
				cnt17 == sumItem.getCnt17() && cnt18 == sumItem.getCnt18() && cnt19 == sumItem.getCnt19() &&
				cnt20 == sumItem.getCnt20() && cnt21 == sumItem.getCnt21() && cnt22 == sumItem.getCnt22() &&
				cnt23 == sumItem.getCnt23() && succTot == sumItem.getSuccTot() && failTot == sumItem.getFailTot() &&
				dateTot == sumItem.getDateTot();
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAdCnt() {
		return adCnt;
	}

	public void setAdCnt(int adCnt) {
		this.adCnt = adCnt;
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

	public int getSuccTot() {
		return succTot;
	}

	public void setSuccTot(int succTot) {
		this.succTot = succTot;
	}

	public int getFailTot() {
		return failTot;
	}

	public void setFailTot(int failTot) {
		this.failTot = failTot;
	}

	public int getDateTot() {
		return dateTot;
	}

	public void setDateTot(int dateTot) {
		this.dateTot = dateTot;
	}

	public int getScreenCnt() {
		return screenCnt;
	}

	public void setScreenCnt(int screenCnt) {
		this.screenCnt = screenCnt;
	}

	public int getDestId() {
		return destId;
	}

	public void setDestId(int destId) {
		this.destId = destId;
	}

}
