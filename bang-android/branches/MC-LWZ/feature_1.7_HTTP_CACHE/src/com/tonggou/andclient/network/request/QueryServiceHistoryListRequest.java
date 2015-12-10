package com.tonggou.andclient.network.request;

import com.tonggou.andclient.network.API;

/**
 * 查询用户历史服务
 * @author lwz
 *
 */
public class QueryServiceHistoryListRequest extends AbsTonggouHttpGetRequest {

	@Override
	public String getOriginApi() {
		return API.QUERY_SERVICE_ALL_HISTORY_LIST;
	}

	public void setApiParams(int pageNo, int pageSize) {
		APIQueryParam params = new APIQueryParam();
		params.put("pageNo", pageNo);
		params.put("pageSize", pageSize);
		super.setApiParams(params);
	}
	
	

}
