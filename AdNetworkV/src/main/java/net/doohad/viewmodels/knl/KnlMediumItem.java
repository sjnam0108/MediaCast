package net.doohad.viewmodels.knl;

public class KnlMediumItem {
	
	private int id;
	
	private String name;
	private String shortName;
	
	public KnlMediumItem(int id, String shortName, String name) {
		this.id = id;
		this.shortName = shortName;
		this.name = name;
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

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

}
