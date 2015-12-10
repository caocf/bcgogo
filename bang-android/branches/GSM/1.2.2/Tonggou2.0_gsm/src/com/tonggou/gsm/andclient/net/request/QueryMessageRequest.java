package com.tonggou.gsm.andclient.net.request;

import com.tonggou.gsm.andclient.net.API;
import com.tonggou.gsm.andclient.net.APIQueryParam;
import com.tonggou.gsm.andclient.net.AbsTonggouHttpGetRequest;

/**
 * 查询消息请求
 * @author lwz
 *
 */
public class QueryMessageRequest extends AbsTonggouHttpGetRequest {

	@Override
	public String getOriginApi() {
		return API.QUERY_MESSAGE;
	}
	
	public void setApiParams(String userNo) {
		APIQueryParam params = new APIQueryParam();
		params.put("types", "NULL");
		params.put("userNo", userNo);
		super.setApiParams(params);
	}

}
