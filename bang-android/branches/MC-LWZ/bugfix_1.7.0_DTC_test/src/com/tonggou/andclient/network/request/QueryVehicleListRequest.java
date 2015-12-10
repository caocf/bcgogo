package com.tonggou.andclient.network.request;

import com.tonggou.andclient.network.API;


/**
 * 获取车辆列表请求
 * @author lwz
 *
 */
public class QueryVehicleListRequest extends AbsTonggouHttpGetRequest {

	private final String PARAM_KEY_USER_NO = "userNo";
	
	public void setApiParams(String userNo) {
		APIQueryParam params = new APIQueryParam(true);
		params.put(PARAM_KEY_USER_NO, userNo);
		super.setApiParams(params);
	}
	
	@Override
	protected String getOriginApi() {
		return API.VEHICLE_LIST ;
	}
	
}
