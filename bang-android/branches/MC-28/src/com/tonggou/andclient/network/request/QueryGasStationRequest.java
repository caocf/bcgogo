package com.tonggou.andclient.network.request;

import com.tonggou.andclient.network.API;

public class QueryGasStationRequest extends AbsTonggouHttpRequest {

	@Override
	public String getAPI() {
		return API.JUHE.QUERY_GAS_STATION;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.GET;
	}
	
	/**
	 * 
	 * @param longitude	¾­¶È
	 * @param latitude	Î³¶È
	 * @param radius	°ë¾¶
	 */
	public void setRequestParams(double longitude, double latitude, int range, int page) {
		HttpRequestParams params = new HttpRequestParams();
		params.put("lon", longitude + "");
		params.put("lat", latitude + "");
		params.put("r", range + "");
		params.put("page", page + "");
		super.setRequestParams(params);
	}

}
