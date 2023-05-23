package net.doohad.viewmodels.sys;

import java.util.Date;

public class SysAdSelItem {

	private Date date;
	
	private long sub1, sub2, sub3, sub4, sub5, sub0;
	
	
	public SysAdSelItem() {}
	
	public SysAdSelItem(Date date, long sub1, long sub2, long sub3, long sub4, long sub5, long sub0) {
		this.date = date;
		
		this.sub1= sub1;
		this.sub2 = sub2;
		this.sub3 = sub3;
		this.sub4 = sub4;
		this.sub5 = sub5;
		
		this.sub0 = sub0;
	}


	private String getDisp(long val) {
		
		StringBuffer sb = new StringBuffer();
		String s = String.valueOf(val);
		
		sb.append("<span class='large-text weight-300'>");
		
		if (s.length() >= 6) {
			sb.append(s.subSequence(0, s.length() - 3));
		} else if (s.length() == 5) {
			sb.append("<span class='text-dim weight-300'>0</span>");
			sb.append(s.subSequence(0, 2));
		} else if (s.length() == 4) {
			sb.append("<span class='text-dim weight-300'>00</span>");
			sb.append(s.subSequence(0, 1));
		} else {
			sb.append("<span class='text-dim weight-300'>000</span>");
		}
		
		sb.append("<small class='pl-1 weight-300'>");
		
		String ss = String.valueOf(val % 1000);
		
		if (ss.length() == 3) {
			sb.append(ss);
		} else if (ss.length() == 2) {
			if (val > 999) {
				sb.append("0");
			} else {
				sb.append("<span class='text-dim weight-300'>0</span>");
			}
			sb.append(ss);
		} else {
			if (val > 999) {
				sb.append("00");
			} else {
				sb.append("<span class='text-dim weight-300'>00</span>");
			}
			sb.append(ss);
		}
		
		sb.append("</small>");
		sb.append("</span>");
		
		return sb.toString();
	}
	
	public long getCount() {
		return sub1 + sub2 + sub3 + sub4 + sub5 + sub0;
	}
	
	public String getCountDisp() {
		return getDisp(getCount());
	}
	
	public String getSub1Disp() {
		return getDisp(sub1);
	}
	
	public String getSub2Disp() {
		return getDisp(sub2);
	}
	
	public String getSub3Disp() {
		return getDisp(sub3);
	}
	
	public String getSub4Disp() {
		return getDisp(sub4);
	}
	
	public String getSub5Disp() {
		return getDisp(sub5);
	}
	
	public String getSub0Disp() {
		return getDisp(sub0);
	}

	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public long getSub1() {
		return sub1;
	}

	public void setSub1(long sub1) {
		this.sub1 = sub1;
	}

	public long getSub2() {
		return sub2;
	}

	public void setSub2(long sub2) {
		this.sub2 = sub2;
	}

	public long getSub3() {
		return sub3;
	}

	public void setSub3(long sub3) {
		this.sub3 = sub3;
	}

	public long getSub4() {
		return sub4;
	}

	public void setSub4(long sub4) {
		this.sub4 = sub4;
	}

	public long getSub5() {
		return sub5;
	}

	public void setSub5(long sub5) {
		this.sub5 = sub5;
	}

	public long getSub0() {
		return sub0;
	}

	public void setSub0(long sub0) {
		this.sub0 = sub0;
	}

}
