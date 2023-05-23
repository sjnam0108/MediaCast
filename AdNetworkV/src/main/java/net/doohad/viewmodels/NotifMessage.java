package net.doohad.viewmodels;

import net.doohad.utils.Util;

public class NotifMessage {

	public String groupID;
	public String title;
	public String body;
	public String[] bodyParams;
	
	public NotifMessage(String groupID, String title, String body, String[] bodyParams) {
		
		this.groupID = groupID;
		this.title = title;
		this.body = body;
		this.bodyParams = bodyParams;
	}
	
	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String[] getBodyParams() {
		return bodyParams;
	}

	public void setBodyParams(String[] bodyParams) {
		this.bodyParams = bodyParams;
	}


	public String getLocalTitle() {
		return getLocalTitle("ko");
	}
	
	public String getLocalTitle(String lang) {
		
		if (Util.isValid(groupID)) {
			return Util.getMessage(title, lang) + " • " + groupID;
		} else {
			return Util.getMessage(title, lang);
		}
	}
	
	public String getLocalBody() {
		return getLocalBody("ko");
	}
	
	public String getLocalBody(String lang) {
		
		return Util.getMessage(body, bodyParams, lang);
	}
}
