package com.tonggou.andclient.vo;

import android.text.TextUtils;

import com.google.gson.Gson;

public class FaultCodeInfo {
	private String faultCode = ""; 
	private String description = "";
	private String category = "";
	private String backgroundInfo = "";
	
	public String getFaultCode() {
		if( "null".equalsIgnoreCase(faultCode) ) {
			return "δ֪";
		}
		return faultCode;
	}
	public void setFaultCode(String faultCode) {
		this.faultCode = faultCode;
	}
	public String getDescription() {
		if(TextUtils.isEmpty(description) || "null".equalsIgnoreCase(description) ) {
			return "δ֪";
		}
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCategory() {
		if( TextUtils.isEmpty(category) || "null".equalsIgnoreCase(category) ) {
			return "δ֪";
		}
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getBackgroundInfo() {
		if( TextUtils.isEmpty(backgroundInfo) || "null".equalsIgnoreCase(backgroundInfo) ) {
			return "δ֪";
		}
		return backgroundInfo;
	}
	public void setBackgroundInfo(String backgroundInfo) {
		this.backgroundInfo = backgroundInfo;
	}
	@Override
	public String toString() {
		return "FaultCodeInfo " + new Gson().toJson(this);
	}  
	
	public void copy(FaultCodeInfo info) {
		if( info == null ) {
			info = new FaultCodeInfo();
		}
		setFaultCode(info.getFaultCode());
		setCategory(info.getCategory());
		setBackgroundInfo(info.getBackgroundInfo());
		setDescription(info.getDescription());
	}
	
}