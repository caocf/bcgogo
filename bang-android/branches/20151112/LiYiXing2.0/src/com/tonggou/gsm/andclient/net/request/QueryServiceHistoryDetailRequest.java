package com.tonggou.gsm.andclient.net.request;

import com.tonggou.gsm.andclient.net.API;
import com.tonggou.gsm.andclient.net.APIQueryParam;
import com.tonggou.gsm.andclient.net.AbsTonggouHttpGetRequest;

public class QueryServiceHistoryDetailRequest extends AbsTonggouHttpGetRequest {

	@Override
	public String getOriginApi() {
		return API.QUERY_SERVICE_HISTORY_DETAIL;
	}

	/**
	 * @param serviceHistoryId  服务id
	 */
	public void setApiParams(String serviceHistoryId) {
		APIQueryParam params = new APIQueryParam();
		params.put("orderId", serviceHistoryId);
		params.put("serviceScope", "NULL");
		super.setApiParams(params);
	}
	
	

}
