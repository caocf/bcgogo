package com.tonggou.andclient.network.request;

import com.tonggou.andclient.network.API;

/**
 * 更新故障字典
 * @author lwz
 *
 */
public class UpdateVehicleFaultDicRequest extends AbsTonggouHttpGetRequest {

	@Override
	public String getOriginApi() {
		return API.UPDATE_VEHICLE_FAULT_DIC;
	}

	/**
	 * 
	 * @param dicVersion	字典版本 (如果版本号为空则取最新版）
	 * @param vehicleModelId	车型ID ( 如果车型Id为空则取通用字典)
	 */
	public void setApiParams(String dicVersion, String vehicleModelId ) {
		APIQueryParam params = new APIQueryParam();
		params.put("dicVersion", dicVersion);
		params.put("vehicleModelId", vehicleModelId);
		super.setApiParams(params);
	}
	
	

}
