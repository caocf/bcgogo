package com.tonggou.andclient.network.request;

import com.tonggou.andclient.network.API;

/**
 * 更新默认车辆
 * @author lwz
 *
 */
public class UpdateDefaultVehicleRequest extends AbsTonggouHttpRequest {

	@Override
	public String getAPI() {
		return API.UPDATE_DEFAULT_VEHICLE;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}

	/**
	 * 
	 * @param vehicleId	要设置为默认车辆的车辆 id
	 */
	public void setRequestParams(String vehicleId) {
		HttpRequestParams params = new HttpRequestParams();
		params.put("vehicleId", vehicleId);
		super.setRequestParams(params);
	}
	
	

}
