package com.tonggou.andclient.vo;

public class FaultCodeInfo {
	private String faultCode; 
	private String description;
	public String getFaultCode() {
		return faultCode;
	}
	public void setFaultCode(String faultCode) {
		this.faultCode = faultCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return "FaultCodeInfo [faultCode=" + faultCode + ", description=" + description + "]";
	}  
	
}