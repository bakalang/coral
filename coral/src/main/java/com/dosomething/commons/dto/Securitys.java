package com.dosomething.commons.dto;

import com.dosomething.commons.annotation.Column;
import com.dosomething.util.JSONUtils;

public class Securitys {
	
	@Column(name = "SECURITY_ID")
	private String securityId;
	@Column(name = "URL")
	private String url;
	@Column(name = "LAST_MODIFIED_DATE")
	private String lastModifiedDate;
		
	public String getSecurityId() {
		return securityId;
	}
	public void setSecurityId(String securityId) {
		this.securityId = securityId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	
	@Override
	public String toString() {
		return JSONUtils.toJsonString(this);
	}
}
