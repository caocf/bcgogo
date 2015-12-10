package com.tonggou.andclient.network.request;

import java.util.List;

import android.text.TextUtils;

import com.tonggou.andclient.network.API;
import com.tonggou.andclient.vo.VehicleInfo;

/**
 * 添加多辆车辆信息
 * @author lwz
 *
 */
public class StoreVehicleInfosRequest extends AbsTonggouHttpRequest {

	@Override
	public String getAPI() {
		return API.STORE_VEHICLE_INFOS;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.PUT;
	}

	public void setRequestParams(String userNo, List<VehicleInfo> vehicles) {
		HttpRequestParams params = new HttpRequestParams();
		for( VehicleInfo vehicle : vehicles ) {
			vehicle.setUserNo(userNo);
			vehicle.setIsDefault("NO");
			vehicle.setVehicleId(null);
			if( TextUtils.isEmpty( vehicle.getMileage()) ) {
				vehicle.setMileage(null);
			}
			if( TextUtils.isEmpty( vehicle.getCurrentMileage() ) ) {
				vehicle.setCurrentMileage(null);
			}
			if( TextUtils.isEmpty( vehicle.getNextMaintainMileage() ) ) {
				vehicle.setNextMaintainMileage(null);
			}
			if( TextUtils.isEmpty(vehicle.getNextExamineTime()) ) {
				vehicle.setNextExamineTime(null);
			}
			if( TextUtils.isEmpty( vehicle.getNextInsuranceTime() ) ) {
				vehicle.setNextInsuranceTime(null);
			}
			if( TextUtils.isEmpty(  vehicle.getCurrentMileage() ) ) {
				vehicle.setCurrentMileage(null);
			}
		}
		params.put("vehicles", vehicles);
		super.setRequestParams(params);
	}
	
}
