package com.tonggou.andclient.jsonresponse;


import com.tonggou.andclient.vo.ServerDetail;

public class ServerDetailResponse {
	private ServerDetail serviceDetail;
	private String status; 
	private String msgCode;  
	private String message ;  
	
	public ServerDetail getServerDetail() {
		return serviceDetail;
	}
	public void setServerDetail(ServerDetail serverDetail) {
		this.serviceDetail = serverDetail;
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

	
}
