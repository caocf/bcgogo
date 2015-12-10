package com.tonggou.andclient.network.request;

import com.tonggou.andclient.network.API;

/**
 * 查询 一辆车的请求
 * @author lwz
 *
 */
public class QuerySingleVehicleRequest extends AbsTonggouHttpGetRequest {

	@Override
	public String getOriginApi() {
		return API.QUERY_SINGLE_VEHICLE_INFO;
	}

	/**
	 * 
	 * @param vehicleId 车辆 id
	 */
	public void setApiParams(String vehicleId) {
		APIQueryParam params = new APIQueryParam(true);
		params.put("vehicleId", vehicleId);
		super.setApiParams(params);
	}
	
	

}
