package com.tonggou.gsm.andclient.net.request;

import com.tonggou.gsm.andclient.net.API;
import com.tonggou.gsm.andclient.net.AbsTonggouHttpGetRequest;

/**
 * 查询支持违章查询的城市
 * @author lwz
 *
 */
public class QueryJuheCityRequest extends AbsTonggouHttpGetRequest {

	@Override
	public String getOriginApi() {
		return API.QUERY_JUHE_CITY;
	}

}
