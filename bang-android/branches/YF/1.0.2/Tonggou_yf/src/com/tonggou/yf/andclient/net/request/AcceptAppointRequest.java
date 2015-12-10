package com.tonggou.yf.andclient.net.request;

import com.tonggou.lib.net.AbsTonggouHttpRequest;
import com.tonggou.lib.net.HttpMethod;
import com.tonggou.lib.net.HttpRequestParams;
import com.tonggou.yf.andclient.net.API;

public class AcceptAppointRequest extends AbsTonggouHttpRequest {

	@Override
	public String getAPI() {
		return API.ACCEPT_APPOINT;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}

	public void setRequestParams(String idStr) {
		HttpRequestParams params = new HttpRequestParams();
		params.put("idStr", idStr);
		super.setRequestParams(params);
	}
	
}
