package net.doohad.viewmodels.fnd;

import java.util.ArrayList;
import java.util.Collections;

import net.doohad.models.CustomComparator;

public class TreeViewItem {
    private int id;
    private String text;
    private boolean expanded;
    private String icon;
    private Integer siblingSeq;
    
    private boolean enabled;
    private boolean checked;
    
    private int childrenCount;
    
    private String custom1;		// ukid
    private String custom2;		// url
    private String custom3;		// iconType
    private String custom4;		// 표시 그룹
    private String custom5;		// siteSelector 표시 여부
    private String custom6;		// 맞춤형 메뉴 여부
    private String custom7;		// 사용자 친화형 메뉴 여부
    
    private ArrayList<TreeViewItem> items;       
    
    public TreeViewItem() {}
    
    public TreeViewItem(int id, String text) {
    	this(id, text, "", false, 0);
    }
    
    public TreeViewItem(int id, String text, Integer siblingSeq) {
    	this(id, text, "", false, siblingSeq);
    }
    
    public TreeViewItem(int id, String text, String icon, boolean expanded, Integer siblingSeq) {
    	this.id = id;
    	this.text = text;
    	this.icon = icon;
    	this.expanded = expanded;
    	this.siblingSeq = siblingSeq;
    	this.enabled = true;
    }
    
    public String getText() {
        return this.text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public boolean getExpanded() {
        return this.expanded;
    }
    
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
    
    public ArrayList<TreeViewItem> getItems() {        
        return this.items;
    }

    public Integer getSiblingSeq() {
    	return this.siblingSeq;
    }
    
    public void setSiblingSeq(Integer siblingSeq) {
    	this.siblingSeq = siblingSeq;
    }
    
    public String getCustom1() {
		return custom1;
	}

	public void setCustom1(String custom1) {
		this.custom1 = custom1;
	}

	public String getCustom2() {
		return custom2;
	}

	public void setCustom2(String custom2) {
		this.custom2 = custom2;
	}

	public String getCustom3() {
		return custom3;
	}

	public void setCustom3(String custom3) {
		this.custom3 = custom3;
	}
	
	public String getCustom4() {
		return custom4;
	}

	public void setCustom4(String custom4) {
		this.custom4 = custom4;
	}

	public String getCustom5() {
		return custom5;
	}

	public void setCustom5(String custom5) {
		this.custom5 = custom5;
	}

	public String getCustom6() {
		return custom6;
	}

	public void setCustom6(String custom6) {
		this.custom6 = custom6;
	}

	public String getCustom7() {
		return custom7;
	}

	public void setCustom7(String custom7) {
		this.custom7 = custom7;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
    
    public int getChildrenCount() {
		return childrenCount;
	}

	public void setChildrenCount(int childrenCount) {
		this.childrenCount = childrenCount;
	}
	
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	
	public void addSubItem(TreeViewItem item) {
        if(this.items == null){
            this.items = new ArrayList<TreeViewItem>();
        }
    	
        this.items.add(item);
        
        Collections.sort(this.items, CustomComparator.TreeViewItemSiblingSeqComparator);
    }
	
	public Boolean getHasChildren() {
		return childrenCount > 0;
	}
	
	public String getBootstrapTreeClassLI() {
		if (this.expanded && this.checked) {
			return "sidenav-item open active";
		} else if (this.expanded) {
			return "sidenav-item open";
		} else if (this.checked) {
			return "sidenav-item active";
		} else {
			return "sidenav-item";
		}
	}
	
	public String getBootstrapTreeClassA() {
		if (this.childrenCount > 0) {
			return "sidenav-link sidenav-toggle";
		} else {
			return "sidenav-link";
		}
	}
}
