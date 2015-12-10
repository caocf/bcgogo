package com.tonggou.yf.andclient.net.request;

import com.tonggou.lib.net.AbsTonggouHttpRequest;
import com.tonggou.lib.net.HttpMethod;
import com.tonggou.lib.net.HttpRequestParams;
import com.tonggou.yf.andclient.net.API;
import com.tonggou.yf.andclient.util.StringUtil;

public class ChangeAppointTimeRequest extends AbsTonggouHttpRequest {

	@Override
	public String getAPI() {
		return API.CHANGE_APPOINT_TIME;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}

	public void setRequestParams(String idStr, long timeMillis) {
		HttpRequestParams params = new HttpRequestParams();
		params.put("idStr", idStr);
		params.put("appointTimeStr", StringUtil.dateTimeFormate(timeMillis, "yyyy-MM-dd HH:mm"));
		super.setRequestParams(params);
	}
	
}
