package net.doohad.viewmodels.inv;

import net.doohad.models.inv.InvSite;

public class InvSiteAutoCompleteItem {

	private int id;
	
	private String shortName;
	private String name;
	private String latitude;
	private String longitude;
	private String regionName;
	private String address;
	private String venueType;
	
	public InvSiteAutoCompleteItem(InvSite site, String regionName) {
		this.id = site.getId();
		this.shortName = site.getShortName();
		this.name = site.getName();
		this.latitude = site.getLatitude();
		this.longitude = site.getLongitude();
		this.regionName = regionName;
		this.address = site.getAddress();
		this.venueType = site.getVenueType();
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

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getVenueType() {
		return venueType;
	}

	public void setVenueType(String venueType) {
		this.venueType = venueType;
	}
	
}
