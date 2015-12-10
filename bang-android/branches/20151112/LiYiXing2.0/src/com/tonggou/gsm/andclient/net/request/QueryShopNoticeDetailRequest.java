package com.tonggou.gsm.andclient.net.request;

import com.tonggou.gsm.andclient.net.API;
import com.tonggou.gsm.andclient.net.APIQueryParam;
import com.tonggou.gsm.andclient.net.AbsTonggouHttpGetRequest;

public class QueryShopNoticeDetailRequest extends AbsTonggouHttpGetRequest {

	@Override
	public String getOriginApi() {
		return API.QUERY_SHOP_NOTICE_DETAIL;
	}

	public void setApiParams(String noticeId) {
		APIQueryParam params = new APIQueryParam(false);
		params.put("noticeId", noticeId);
		super.setApiParams(params);
	}
}
