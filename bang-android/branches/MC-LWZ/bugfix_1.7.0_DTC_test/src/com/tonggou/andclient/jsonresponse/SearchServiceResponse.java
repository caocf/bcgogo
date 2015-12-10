package com.tonggou.andclient.jsonresponse;

import java.util.List;

import com.tonggou.andclient.vo.Pager;
import com.tonggou.andclient.vo.TGService;

public class SearchServiceResponse {
	private List<TGService> results;  //未完成服务列表
	private String status; 
	private String msgCode;  
	private String message ;  
	private Pager pager;
	public List<TGService> getResults() {
		return results;
	}
	public void setResults(List<TGService> results) {
		this.results = results;
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
	public Pager getPager() {
		return pager;
	}
	public void setPager(Pager pager) {
		this.pager = pager;
	}
	
}
