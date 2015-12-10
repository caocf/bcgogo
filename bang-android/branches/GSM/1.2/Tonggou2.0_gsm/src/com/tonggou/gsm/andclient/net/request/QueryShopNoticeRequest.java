package com.tonggou.gsm.andclient.net.request;

import com.tonggou.gsm.andclient.Constants;
import com.tonggou.gsm.andclient.net.API;
import com.tonggou.gsm.andclient.net.APIQueryParam;
import com.tonggou.gsm.andclient.net.AbsTonggouHttpGetRequest;

public class QueryShopNoticeRequest extends AbsTonggouHttpGetRequest {

	@Override
	public String getOriginApi() {
		return API.QUERY_SHOP_NOTICE;
	}

	public void setApiParams(int pageNo) {
		APIQueryParam params = new APIQueryParam(false);
		params.put("pageNo", pageNo);
		params.put("pageSize", Constants.APP_CONFIG.QUERY_PAGE_SIZE);
		super.setApiParams(params);
	}
}
