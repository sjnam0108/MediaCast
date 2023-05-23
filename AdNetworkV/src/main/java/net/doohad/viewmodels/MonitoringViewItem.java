package net.doohad.viewmodels;

public class MonitoringViewItem {
	public static enum ViewType {
		SiteAll, Site, ChildSite
	}
	
	private ViewType view;
	private String text;
	
	private int siteId;
	private int indentWidth;
	
	private String category;
	
	public MonitoringViewItem(ViewType view, int indentWidth, String text, int siteId, int stbGroupId) {
		this.view = view;
		this.indentWidth = indentWidth;
		this.text = text;
		this.siteId = siteId;
	}
	
	public MonitoringViewItem(ViewType view, int indentWidth, String text, String category, int siteId, int stbGroupId) {
		this.view = view;
		this.indentWidth = indentWidth;
		this.text = text;
		this.category = category;
		this.siteId = siteId;
	}
	
	public String getTypeImageCssKeyword() {
		switch (view) {
		case SiteAll:
			return "fas fa-star";
		case Site:
		case ChildSite:
			return "fas fa-globe";
		}
		
		return "";
	}
	
	public String getValue() {
		return String.format("%s|-1", siteId);
	}

	public ViewType getView() {
		return view;
	}

	public void setView(ViewType view) {
		this.view = view;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public int getIndentWidth() {
		return indentWidth;
	}

	public void setIndentWidth(int indentWidth) {
		this.indentWidth = indentWidth;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
