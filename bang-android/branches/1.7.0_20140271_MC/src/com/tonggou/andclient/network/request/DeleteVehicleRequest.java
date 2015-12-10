package com.tonggou.andclient.network.request;

import com.tonggou.andclient.network.API;

/**
 * 删除车辆请求
 * @author lwz
 *
 */
public class DeleteVehicleRequest extends AbsTonggouHttpGetRequest {

	@Override
	public String getOriginApi() {
		return API.DELETE_VEHICLE_INFO;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.DELETE;
	}

	/**
	 * 要删除的 车辆 id
	 * @param vehicleId
	 */
	public void setApiParams(String vehicleId ) {
		APIQueryParam params = new APIQueryParam(true);
		params.put("vehicleId", vehicleId);
		super.setApiParams(params);
	}
	
}
