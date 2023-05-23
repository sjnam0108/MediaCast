package net.doohad.models;

public class Message {
	private String idInView;
	private String code;
	private Object[] args;
	
	public Message(String idInView, String code) {
		this.idInView = idInView;
		this.code = code;
	}

	public Message(String idInView, String code, Object[] args) {
		this.idInView = idInView;
		this.code = code;
		this.args = args;
	}

	public String getIdInView() {
		return idInView;
	}

	public void setIdInView(String idInView) {
		this.idInView = idInView;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}
}
