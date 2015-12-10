package com.tonggou.andclient.network.request;

import com.tonggou.andclient.network.API;

/**
 * 查询聚合支持违章城市列表
 * @author lwz
 *
 */
public class QueryJuheCityListRequest extends AbsTonggouHttpGetRequest {

	@Override
	public String getOriginApi() {
		return API.QUERY_JUHE_CITY_LIST;
	}
	
}
