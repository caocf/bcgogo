package com.tonggou.andclient.jsonresponse;

import java.util.List;

import com.tonggou.andclient.vo.FaultCodeInfo;

public class FaultDicResponse {
	private List<FaultCodeInfo> faultCodeList;
	private String dictionaryId;
	private String dictionaryVersion;
	private String isCommon;
	
	private String status; 
	private String msgCode;  
	private String message ;          
	private String data ;
	public List<FaultCodeInfo> getFaultCodeList() {
		return faultCodeList;
	}
	public void setFaultCodeList(List<FaultCodeInfo> faultCodeList) {
		this.faultCodeList = faultCodeList;
	}
	public String getDictionaryId() {
		return dictionaryId;
	}
	public void setDictionaryId(String dictionaryId) {
		this.dictionaryId = dictionaryId;
	}
	public String getDictionaryVersion() {
		return dictionaryVersion;
	}
	public void setDictionaryVersion(String dictionaryVersion) {
		this.dictionaryVersion = dictionaryVersion;
	}
	public String getIsCommon() {
		return isCommon;
	}
	public void setIsCommon(String isCommon) {
		this.isCommon = isCommon;
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
