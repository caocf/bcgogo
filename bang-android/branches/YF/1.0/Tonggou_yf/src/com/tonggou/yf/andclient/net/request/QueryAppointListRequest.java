package com.tonggou.yf.andclient.net.request;

import com.tonggou.lib.net.APIQueryParam;
import com.tonggou.lib.net.AbsTonggouHttpGetRequest;
import com.tonggou.yf.andclient.Constants;
import com.tonggou.yf.andclient.net.API;

public class QueryAppointListRequest extends AbsTonggouHttpGetRequest {

	@Override
	public String getOriginApi() {
		return API.QUERY_APPOINT_LIST;
	}
	
	public void setApiParams(int pageNo) {
		APIQueryParam params = new APIQueryParam(false);
		params.put("pageNo", pageNo);
		params.put("pageSize", Constants.APP_CONFIG.QUERY_PAGE_SIZE);
		super.setApiParams(params);
	}

}
