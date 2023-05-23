package net.doohad.viewmodels.fnd;

public class MenuIconItem implements Comparable<MenuIconItem> {
	private String icon;
	private String style;
	
	public MenuIconItem(String icon, String style)
	{
		this.icon = icon;
		this.style = style;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	@Override
	public int compareTo(MenuIconItem o) {
		return this.icon.compareTo(o.icon); 
	}
}
