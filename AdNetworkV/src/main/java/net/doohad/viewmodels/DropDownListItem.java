package net.doohad.viewmodels;

public class DropDownListItem {
	private String text;
	private String value;
	private String icon;
	
	private String subIcon;
	
	public DropDownListItem(String text, String value) {
		this.text = text;
		this.value = value;
	}
	
	public DropDownListItem(String icon, String text, String value) {
		this.icon = icon;
		this.text = text;
		this.value = value;
	}
	
	public DropDownListItem(String icon, String subIcon, String text, String value) {
		this.icon = icon;
		this.subIcon = subIcon;
		this.text = text;
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getSubIcon() {
		return subIcon;
	}

	public void setSubIcon(String subIcon) {
		this.subIcon = subIcon;
	}
}
