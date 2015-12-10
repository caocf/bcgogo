package com.tonggou.gsm.andclient.net.request;

import com.tonggou.gsm.andclient.Constants;
import com.tonggou.gsm.andclient.net.API;
import com.tonggou.gsm.andclient.net.APIQueryParam;
import com.tonggou.gsm.andclient.net.AbsTonggouHttpGetRequest;

/**
 * 服务历史查询（我的账单）
 * @author lwz
 *
 */
public class QueryServiecHistoryRequest extends AbsTonggouHttpGetRequest {

	@Override
	public String getOriginApi() {
		return API.QUERY_SERVICE_HISTORY;
	}
	
	/**
	 * 
	 * @param isQueryFinish	是否查询 已结算订单。true 已结算， false 未结算
	 * @param userNo
	 * @param pageNo
	 */
	public void setApiParams(boolean isQueryFinish, String userNo, int pageNo) {
		APIQueryParam params = new APIQueryParam();
		params.put("serviceScope", "NULL");
		params.put("status", isQueryFinish ? "finished": "unfinished");
		params.put("userNo", userNo);
		params.put("pageNo", String.valueOf(pageNo));
		params.put("pageSize", String.valueOf(Constants.APP_CONFIG.QUERY_PAGE_SIZE));
		super.setApiParams(params);
	}

}
