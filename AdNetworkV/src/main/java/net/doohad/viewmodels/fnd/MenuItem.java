package net.doohad.viewmodels.fnd;

public class MenuItem implements Comparable<MenuItem> {
	private String ukid;
	private String title;
	private String url;
	private String dispGroup;
	private String icon;
	private String style;
	
	public MenuItem(String ukid, String title, String url, String dispGroup, 
			String icon, String style)
	{
		this.ukid = ukid;
		this.title = title;
		this.url = url;
		this.dispGroup = dispGroup;
		this.icon = icon;
		this.style = style;
	}

	public String getUkid() {
		return ukid;
	}

	public void setUkid(String ukid) {
		this.ukid = ukid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public String getDispGroup() {
		return dispGroup;
	}

	public void setDispGroup(String dispGroup) {
		this.dispGroup = dispGroup;
	}

	@Override
	public int compareTo(MenuItem o) {
		return this.title.compareTo(o.title); 
	}
}
