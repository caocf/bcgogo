package com.tonggou.gsm.andclient.net.request;

import com.tonggou.gsm.andclient.net.API;
import com.tonggou.gsm.andclient.net.AbsTonggouHttpRequest;
import com.tonggou.gsm.andclient.net.HttpMethod;
import com.tonggou.gsm.andclient.net.HttpRequestParams;

public class LogoutRequest extends AbsTonggouHttpRequest {

	@Override
	public String getAPI() {
		return API.LOGOUT;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.PUT;
	}
	
	public void setRequestParams(String userNo) {
		HttpRequestParams params = new HttpRequestParams();
		params.put("userNo", userNo);
		super.setRequestParams(params);
	}

}
