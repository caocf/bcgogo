package com.tonggou.yf.andclient.net.request;

import com.tonggou.lib.net.AbsTonggouHttpRequest;
import com.tonggou.lib.net.HttpMethod;
import com.tonggou.lib.net.HttpRequestParams;
import com.tonggou.yf.andclient.net.API;

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
