package net.doohad.models;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.doohad.utils.Util;

public class UserCookie {
	
	private String appMode = "";
	private int currAdId = -1;

	
	public UserCookie() {}
	
	public UserCookie(HttpServletRequest request) {
		setAppMode(Util.cookieValue(request, "appMode"));
		setCurrAdId(Util.parseInt(Util.cookieValue(request, "currAdId")));
	}

	
	public String getAppMode() {
		return appMode;
	}

	public void setAppMode(String appMode) {
		if (Util.isValid(appMode)) {
			this.appMode = appMode;
		}
	}

	public void setAppMode(String appMode, HttpServletResponse response) {
		if (Util.isValid(appMode)) {
			this.appMode = appMode;
			response.addCookie(Util.cookie("appMode", appMode));
		}
	}

	
	public int getCurrAdId() {
		return currAdId;
	}

	public void setCurrAdId(int currAdId) {
		this.currAdId = currAdId;
	}

	public void setCurrAdId(int currAdId, HttpServletResponse response) {
		this.currAdId = currAdId;
		response.addCookie(Util.cookie("currAdId", String.valueOf(currAdId)));
	}
}
