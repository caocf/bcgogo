package com.tonggou.gsm.andclient.net.request;

import com.tonggou.gsm.andclient.net.API;
import com.tonggou.gsm.andclient.net.AbsTonggouHttpGetRequest;
import com.tonggou.gsm.andclient.net.HttpRequestParams;

public class QueryJuheGasStationRequest extends AbsTonggouHttpGetRequest {

	@Override
	public String getOriginApi() {
		return API.JUHE.QUERY_GAS_STATION;
	}

	/**
	 * 
	 * @param longitude	经度
	 * @param latitude	纬度
	 * @param radius	半径
	 */
	public void setRequestParams(double longitude, double latitude, int page) {
		HttpRequestParams params = new HttpRequestParams();
		params.put("lon", longitude);
		params.put("lat", latitude);
		params.put("r", "10000");	// 最高只支持 10000, 超过该值就没有数据显示了
		params.put("page", page);
		super.setRequestParams(params);
	}
	
}
