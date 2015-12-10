package com.tonggou.andclient.jsonresponse;

import java.util.List;

import com.tonggou.andclient.vo.AppConfig;
import com.tonggou.andclient.vo.OBDBindInfo;

public class LoginResponse {
	private List<OBDBindInfo> obdList;
	private AppConfig appConfig;
	private String status; 
	private String msgCode;  
	private String message ;          
	private String data ;
	
	public List<OBDBindInfo> getObdList() {
		return obdList;
	}
	public void setObdList(List<OBDBindInfo> obdList) {
		this.obdList = obdList;
	}
	public AppConfig getAppConfig() {
		return appConfig;
	}
	public void setAppConfig(AppConfig appConfig) {
		this.appConfig = appConfig;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMsgCode() {
		return msgCode;
	}
	public void setMsgCode(String msgCode) {
		this.msgCode = msgCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}    
}
