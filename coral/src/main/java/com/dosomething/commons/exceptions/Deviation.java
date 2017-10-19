package com.dosomething.commons.exceptions;

public class Deviation extends RuntimeException {
	//private String message;

	private String statusCode;
	private String I18Nkey;
	private String[] I18NValues;

	private static final long serialVersionUID = -2588096601668585710L;

	public Deviation() {
		super();
	}

	public Deviation(String s) {
		if (null == this.I18Nkey) {
			this.I18Nkey = s;
		}
	}

	public Deviation(Throwable cause) {
		if (null == this.I18Nkey) {
			this.I18Nkey = cause.getMessage();
		}
	}

	public Deviation(Throwable cause, String I18Nkey) {
		if (null == this.I18Nkey) {
			this.I18Nkey = cause.getMessage();
		}
		this.I18Nkey = I18Nkey;
	}

	public Deviation setI18N(String key, String... values) {
		this.I18Nkey = key;
		if (values != null) {
			this.I18NValues = values;
		}
		return this;
	}

	public String getMessage() {
		if (super.getMessage() != null && super.getMessage().length() > 0) {
			return super.getMessage();
		}
		if (null != this.I18Nkey) {
			return this.I18Nkey;
		}
		return "";
	}

	public String getI18Nkey() {
		if (null != this.I18Nkey) {
			return this.I18Nkey;
		}
		return "";
	}

	public String[] getI18NValues() {
		if (null != this.I18NValues) {
			return this.I18NValues;
		}
		return null;
	}
	
	public String getStatusCode() {
		return statusCode;
	}
	
	public Deviation setStatusCode(String statusCode) {
		this.statusCode = statusCode;
		return this;
	}
	
}
