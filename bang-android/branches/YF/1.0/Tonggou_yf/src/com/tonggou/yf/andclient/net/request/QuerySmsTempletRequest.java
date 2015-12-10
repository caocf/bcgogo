package com.tonggou.yf.andclient.net.request;

import com.tonggou.lib.net.APIQueryParam;
import com.tonggou.lib.net.AbsTonggouHttpGetRequest;
import com.tonggou.yf.andclient.bean.type.TodoType;
import com.tonggou.yf.andclient.net.API;

public class QuerySmsTempletRequest extends AbsTonggouHttpGetRequest {

	@Override
	public String getOriginApi() {
		return API.QUERY_SMS_TEMPLET;
	}

	public void setApiParams(TodoType type, String idStr) {
		APIQueryParam params = new APIQueryParam();
		params.put("type", type.getType());
		params.put("idStr", idStr);
		super.setApiParams(params);
	}
	
	

}
