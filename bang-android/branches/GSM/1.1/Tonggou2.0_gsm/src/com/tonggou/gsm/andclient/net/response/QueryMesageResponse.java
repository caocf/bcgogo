package com.tonggou.gsm.andclient.net.response;

import java.util.ArrayList;

import com.tonggou.gsm.andclient.bean.TGMessage;

/**
 * 查询消息接口响应
 * @author lwz
 *
 */
public class QueryMesageResponse extends BaseResponse {

	private static final long serialVersionUID = 8608910006582349854L;
	
	private ArrayList<TGMessage> messageList;

	public ArrayList<TGMessage> getMessageList() {
		return messageList;
	}

	public void setMessageList(ArrayList<TGMessage> messageList) {
		this.messageList = messageList;
	}

}
