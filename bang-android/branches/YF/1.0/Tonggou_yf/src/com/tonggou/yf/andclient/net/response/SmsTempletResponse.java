package com.tonggou.yf.andclient.net.response;

import com.google.gson.annotations.SerializedName;
import com.tonggou.lib.net.response.BaseResponse;

public class SmsTempletResponse extends BaseResponse {

	private static final long serialVersionUID = 4108122519597708892L;

	@SerializedName("msgContent")
	private String msgTemplet;
	
	public String getMsgTemplet() {
		return msgTemplet;
	}
}
