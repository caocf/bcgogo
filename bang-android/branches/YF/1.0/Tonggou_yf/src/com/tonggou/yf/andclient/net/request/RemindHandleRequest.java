package com.tonggou.yf.andclient.net.request;

import com.tonggou.lib.net.AbsTonggouHttpRequest;
import com.tonggou.lib.net.HttpMethod;
import com.tonggou.lib.net.HttpRequestParams;
import com.tonggou.yf.andclient.bean.type.TodoType;
import com.tonggou.yf.andclient.net.API;

public class RemindHandleRequest extends AbsTonggouHttpRequest {

	@Override
	public String getAPI() {
		return API.REMIND_HANDLE;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}

	public void setRequestParams(TodoType type, String idStr) {
		HttpRequestParams params = new HttpRequestParams();
		params.put("type", type.getType());
		params.put("idStr", idStr);
		super.setRequestParams(params);
	}
	
}
