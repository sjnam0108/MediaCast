package net.doohad.viewmodels;


public class QuickLinkItem {
    private String ukid;
	private String icon;
	private String iconStyle;
	private String link;
	private String title;
	private String subtitle;
	
	public QuickLinkItem(String icon, String iconStyle, String ukid, String link, String title, 
			String subtitle) {
		this.icon = icon;
		this.iconStyle = iconStyle;
		this.ukid = ukid;
		this.link = link;
		this.title = title;
		this.subtitle = subtitle;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getUkid() {
		return ukid;
	}

	public void setUkid(String ukid) {
		this.ukid = ukid;
	}

	public String getIconStyle() {
		return iconStyle;
	}

	public void setIconStyle(String iconStyle) {
		this.iconStyle = iconStyle;
	}
}
