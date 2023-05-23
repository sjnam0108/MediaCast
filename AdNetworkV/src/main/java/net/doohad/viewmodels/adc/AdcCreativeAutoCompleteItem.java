package net.doohad.viewmodels.adc;

public class AdcCreativeAutoCompleteItem {

	private int id;
	
	private String name;
	
	public AdcCreativeAutoCompleteItem(int id, String name) {
		this.id = id;
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
	
	public String getText() {
		return name + " - #" + String.valueOf(id);
	}
	
}
