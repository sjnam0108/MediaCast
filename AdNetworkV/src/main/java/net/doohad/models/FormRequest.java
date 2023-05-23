package net.doohad.models;


public class FormRequest {
	// for Menu model
	private int id;
	private String ukid;
	private String url;
	private String oper;
	private String icon;
	private boolean scopeKernel;
	private boolean scopeMedium;
	private boolean scopeAd;
	
	// for Password Update
	private String currentPassword;
	private String newPassword;
	private String confirmPassword;

	
	public FormRequest() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUkid() {
		return ukid;
	}

	public void setUkid(String ukid) {
		this.ukid = ukid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getOper() {
		return oper;
	}

	public void setOper(String oper) {
		this.oper = oper;
	}
	
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}
	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}
	
	public String getNewPassword() {
		return newPassword;
	}
	
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
	public String getConfirmPassword() {
		return confirmPassword;
	}
	
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
	public boolean isScopeKernel() {
		return scopeKernel;
	}

	public void setScopeKernel(boolean scopeKernel) {
		this.scopeKernel = scopeKernel;
	}

	public boolean isScopeMedium() {
		return scopeMedium;
	}

	public void setScopeMedium(boolean scopeMedium) {
		this.scopeMedium = scopeMedium;
	}

	public boolean isScopeAd() {
		return scopeAd;
	}

	public void setScopeAd(boolean scopeAd) {
		this.scopeAd = scopeAd;
	}
}
