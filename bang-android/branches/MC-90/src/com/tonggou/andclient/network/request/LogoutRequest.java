package com.tonggou.andclient.network.request;

import com.tonggou.andclient.network.API;

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
