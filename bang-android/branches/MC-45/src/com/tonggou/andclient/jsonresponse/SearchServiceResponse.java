package com.tonggou.andclient.jsonresponse;

import java.util.List;

import com.tonggou.andclient.vo.TGService;

public class SearchServiceResponse {
	private List<TGService> unFinishedServiceList;  //未完成服务列表
	private List<TGService> finishedServiceList;    //完成服务列表
	private String status; 
	private String msgCode;  
	private String message ;  
	private String unFinishedServiceCount ;
	private String finishedServiceCount;
	public List<TGService> getUnFinishedServiceList() {
		return unFinishedServiceList;
	}
	public void setUnFinishedServiceList(List<TGService> unFinishedServiceList) {
		this.unFinishedServiceList = unFinishedServiceList;
	}
	public List<TGService> getFinishedServiceList() {
		return finishedServiceList;
	}
	public void setFinishedServiceList(List<TGService> finishedServiceList) {
		this.finishedServiceList = finishedServiceList;
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
	public String getUnFinishedServiceCount() {
		return unFinishedServiceCount;
	}
	public void setUnFinishedServiceCount(String unFinishedServiceCount) {
		this.unFinishedServiceCount = unFinishedServiceCount;
	}
	public String getFinishedServiceCount() {
		return finishedServiceCount;
	}
	public void setFinishedServiceCount(String finishedServiceCount) {
		this.finishedServiceCount = finishedServiceCount;
	}
	
}
