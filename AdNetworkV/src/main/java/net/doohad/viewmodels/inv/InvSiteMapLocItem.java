package net.doohad.viewmodels.inv;

import net.doohad.models.inv.InvSite;
import net.doohad.utils.Util;

public class InvSiteMapLocItem {

	private double lat;
	private double lng;
	
	private double distance;
	
	private String title;
	
	private String icon;
	
	
	public InvSiteMapLocItem() {}
	
	public InvSiteMapLocItem(InvSite site) {
		this.lat = (double)Util.parseFloat(site.getLatitude());
		this.lng = (double)Util.parseFloat(site.getLongitude());
		
		this.title = site.getName();
	}
	
	public InvSiteMapLocItem(String title, double lat, double lng) {
		this.title = title;
		this.lat = lat;
		this.lng = lng;
	}
	

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
	
}
