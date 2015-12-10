package com.tonggou.andclient.network.request;

import android.text.TextUtils;

import com.tonggou.andclient.network.API;
import com.tonggou.andclient.vo.VehicleInfo;

public class StoreVehicleInfoRequest extends AbsTonggouHttpRequest {

	@Override
	public String getAPI() {
		return API.STORE_VEHICLE_INFO;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.PUT;
	}
	
	/**
	 * 
	 * @param userNo		用户名 不可为 null
	 * @param vehicleId		车辆id 可为 null, 当为 null 表示新增
	 * @param vehicleNo		车牌号 不可为 null
	 * @param vehicleBrand	品牌 不可为 null
	 * @param vehicleModel	车型 不可为 null
	 * @param vehicle		其他的一些信息 可以为 null
	 * @param bindingShopId 店铺 id 可以为 null
	 */
	public void setRequestParams(String userNo, String vehicleId, String vehicleNo, String vehicleBrand, String vehicleModel, VehicleInfo vehicle, String bindingShopId) {
		HttpRequestParams params = new HttpRequestParams();
		
		if( vehicle != null ) {
			String vehicleVin = vehicle.getVehicleVin();
			String engineNo = vehicle.getEngineNo();
			String registNo = vehicle.getRegistNo();
			String nextMaintainMileage = vehicle.getNextMaintainMileage();
			String nextInsuranceTime = vehicle.getNextInsuranceTime();
			String nextExamineTime = vehicle.getNextExamineTime();
			String currentMileage = vehicle.getCurrentMileage();
			String vehicleBrandId = vehicle.getVehicleBrandId();
			String vehicleModelId = vehicle.getVehicleModelId();
			
			if( !TextUtils.isEmpty(bindingShopId) ) {
				params.put("bindingShopId", bindingShopId);
			}
			if( !TextUtils.isEmpty(vehicleVin) ) {
				params.put("vehicleVin", vehicleVin);
			}
			if( !TextUtils.isEmpty(engineNo) ) {
				params.put("engineNo", engineNo);
			}
			if( !TextUtils.isEmpty(registNo) ) {
				params.put("registNo", registNo);
			}
			if( !TextUtils.isEmpty(currentMileage) && !"null".equalsIgnoreCase(currentMileage)) {
				params.put("currentMileage", Long.valueOf( currentMileage ));
			} 
			if( !TextUtils.isEmpty(nextMaintainMileage) && !"null".equalsIgnoreCase(nextMaintainMileage)) {
				params.put("nextMaintainMileage", Long.valueOf( nextMaintainMileage ));
			} 
			if( !TextUtils.isEmpty(nextInsuranceTime)) {
				params.put("nextInsuranceTime", nextInsuranceTime);
			} 
			if( !TextUtils.isEmpty(nextExamineTime)) {
				params.put("nextExamineTime", nextExamineTime );
			} 
			if( !TextUtils.isEmpty(vehicleBrandId) ) {
				params.put("vehicleBrandId", vehicleBrandId);
			}
			if( !TextUtils.isEmpty(vehicleModelId) ) {
				params.put("vehicleModelId", vehicleModelId);
			}
		}
		
		if( !TextUtils.isEmpty(vehicleId) ) {
			params.put("vehicleId", vehicleId);
		} 
		params.put("userNo", userNo);
		params.put("vehicleNo", vehicleNo);
		params.put("vehicleModel", vehicleModel);
		params.put("vehicleBrand", vehicleBrand);
		super.setRequestParams(params);
	}

	
}
