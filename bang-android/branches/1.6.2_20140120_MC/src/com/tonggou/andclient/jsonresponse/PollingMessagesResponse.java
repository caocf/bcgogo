package com.tonggou.andclient.jsonresponse;

import java.util.List;
import com.tonggou.andclient.vo.TonggouMessage;

public class PollingMessagesResponse extends BaseResponse {
	
	private static final long serialVersionUID = 7352670902627644846L;
	
	private List<TonggouMessage> messageList;
	public List<TonggouMessage> getMessageList() {
		return messageList;
	}
	public void setMessageList(List<TonggouMessage> messageList) {
		this.messageList = messageList;
	}
}
