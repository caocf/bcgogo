package com.tonggou.andclient.jsonresponse;

import java.util.List;
import com.tonggou.andclient.vo.TonggouMessage;

public class PollingMessagesResponse {
	private List<TonggouMessage> messageList;
	private String status; 
	private String msgCode;  
	private String message ;          
	private String data ;
	public List<TonggouMessage> getMessageList() {
		return messageList;
	}
	public void setMessageList(List<TonggouMessage> messageList) {
		this.messageList = messageList;
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
