package net.doohad.viewmodels.rev;

import java.util.ArrayList;

import net.doohad.models.adc.AdcAdCreative;
import net.doohad.utils.Util;

public class RevAdOrderItem {

	private int adCreativeId;
	
	private String sortCode;
	
	private ArrayList<String> adCreatIds = new ArrayList<String>();
	private ArrayList<String> weights = new ArrayList<String>();

	
	public RevAdOrderItem() {}
	
	public RevAdOrderItem(AdcAdCreative adCreat) {
		this.adCreativeId = adCreat.getId();
		if (adCreat.getAd().getPurchType().equals("H")) {
			this.sortCode = "C";
		} else {
			if (adCreat.getAd().getPurchType().equals("G")) {
				this.sortCode = "A";
			} else {
				this.sortCode = "B";
			}
			
			this.sortCode += String.format("%02d", adCreat.getAd().getPriority());
		}
		this.adCreatIds.add(String.valueOf(adCreat.getId()));
		this.weights.add(String.valueOf(adCreat.getWeight()));
	}

	
	public void add(String id, String weight) {
		this.adCreatIds.add(id);
		this.weights.add(weight);
	}
	
	public String getItemStr() {
		if (adCreatIds.size() < 2) {
			return String.valueOf(adCreativeId);
		} else {
			String ret = "";
			for(int i = 0; i < adCreatIds.size(); i ++) {
				if (Util.isValid(ret)) {
					ret += "_";
				}
				ret += adCreatIds.get(i) + ":" + weights.get(i);
			}
			
			return ret;
		}
	}

	
	public int getAdCreativeId() {
		return adCreativeId;
	}

	public void setAdCreativeId(int adCreativeId) {
		this.adCreativeId = adCreativeId;
	}

	public String getSortCode() {
		return sortCode;
	}

	public void setSortCode(String sortCode) {
		this.sortCode = sortCode;
	}
	
}
