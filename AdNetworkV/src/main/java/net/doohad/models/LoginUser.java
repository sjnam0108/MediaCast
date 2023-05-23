package net.doohad.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.doohad.models.knl.KnlUser;
import net.doohad.viewmodels.knl.KnlAccountItem;
import net.doohad.viewmodels.knl.KnlMediumItem;

public class LoginUser {
	private int id;
	private int loginId;
	
	// 사용자의 계정 id
	private int accountId;

	private String shortName;
	private String name;
	
	private String dispViewName = "";
	
	private String userViewId;
	
	private String icon;
	
	private Date loginDate;

	private List<String> allowedUrlList = new ArrayList<String>();
	
	
	private boolean viewSwitcherShown;
	
	
	
	private List<KnlMediumItem> availMediumList;
	private List<KnlAccountItem> availAdAccountList;
	
	private boolean mediumSwitcherAvailable;
	
	private String dispName = "";

	
	
	public LoginUser(KnlUser user, int loginId) {
		this.id = user.getId();
		this.shortName = user.getShortName();
		this.name = user.getName();
		this.loginId = loginId;
		this.loginDate = new Date();
		
		this.accountId = user.getAccount().getId();
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
	
	public int getLoginId() {
		return loginId;
	}
	
	public void setLoginId(int loginId) {
		this.loginId = loginId;
	}
	
	public Date getLoginDate() {
		return loginDate;
	}
	
	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	public boolean isViewSwitcherShown() {
		return viewSwitcherShown;
	}

	public void setViewSwitcherShown(boolean viewSwitcherShown) {
		this.viewSwitcherShown = viewSwitcherShown;
	}

	public List<String> getAllowedUrlList() {
		return allowedUrlList;
	}

	public void setAllowedUrlList(List<String> allowedUrlList) {
		this.allowedUrlList = allowedUrlList;
	}

	public String getUserViewId() {
		return userViewId;
	}

	public void setUserViewId(String userViewId) {
		this.userViewId = userViewId;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getDispViewName() {
		return dispViewName;
	}

	public void setDispViewName(String dispViewName) {
		this.dispViewName = dispViewName;
	}

	
	
	public List<KnlMediumItem> getAvailMediumList() {
		return availMediumList;
	}

	public void setAvailMediumList(List<KnlMediumItem> availMediumList) {
		this.availMediumList = availMediumList;
	}

	public List<KnlAccountItem> getAvailAdAccountList() {
		return availAdAccountList;
	}

	public void setAvailAdAccountList(List<KnlAccountItem> availAdAccountList) {
		this.availAdAccountList = availAdAccountList;
	}

	
	public String getFirstMediumIdInAvailMediumList() {
		for(KnlMediumItem item : availMediumList) {
			return String.valueOf(item.getId());
		}
		
		return null;
	}
	
	public boolean hasMediumIdInAvailMediumList(String value) {
		for(KnlMediumItem item : availMediumList) {
			if (String.valueOf(item.getId()).equals(value)) {
				return true;
			}
		}
		
		return false;
	}

	
	public String getFirstAccountIdInAvailAdAccountList() {
		for(KnlAccountItem item : availAdAccountList) {
			return String.valueOf(item.getId());
		}
		
		return null;
	}
	
	public boolean hasAccountIdInAvailAdAccountList(String value) {
		for(KnlAccountItem item : availAdAccountList) {
			if (String.valueOf(item.getId()).equals(value)) {
				return true;
			}
		}
		
		return false;
	}

	public boolean isMediumSwitcherAvailable() {
		return mediumSwitcherAvailable;
	}

	public void setMediumSwitcherAvailable(boolean mediumSwitcherAvailable) {
		this.mediumSwitcherAvailable = mediumSwitcherAvailable;
	}

	public String getDispName() {
		return dispName;
	}

	public void setDispName(String dispName) {
		this.dispName = dispName;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	
}
