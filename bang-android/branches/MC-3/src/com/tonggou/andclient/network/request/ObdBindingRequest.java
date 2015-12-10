package com.tonggou.andclient.network.request;

import android.text.TextUtils;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.network.API;
import com.tonggou.andclient.vo.VehicleInfo;

/**
 * 绑定 OBD 请求
 * @author lwz
 *
 */
public class ObdBindingRequest extends AbsTonggouHttpRequest {

	@Override
	public String getAPI() {
		return API.OBD_BINDING;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}

	/**
	 * 
	 * @param userNo		用户 id, not null
	 * @param obdSN			obd硬件唯一标识号, obd MAC 地址, not null
	 * @param vehicleId		车辆 id 可为 null,  vehicleId为 null 表示新增
	 * @param vehicleNo		车牌号, not null
	 * @param vehicleVin	车架号, not null
	 * @param vehicleBrand	品牌, not null
	 * @param vehicleModel	车型, not null
	 * @param vehicle		车辆信息，直接传 vechileInfo, 会自动取出其他信息
	 * @param bindingShopId	店铺 id,可以为 null
	 * 
	 */
	public void setRequestParams(String userNo, String obdSN, String vehicleId, String vehicleNo, String vehicleVin, String vehicleBrand, String vehicleModel, VehicleInfo vehicle, String bindingShopId) {
		HttpRequestParams params = new HttpRequestParams();
		
		if( vehicle != null ) {
			String engineNo = vehicle.getEngineNo();
			String registNo = vehicle.getRegistNo();
			String nextMaintainMileage = vehicle.getNextMaintainMileage();
			String nextInsuranceTime = vehicle.getNextInsuranceTime();
			String nextExamineTime = vehicle.getNextExamineTime();
			String currentMileage = vehicle.getCurrentMileage();
			String vehicleBrandId = vehicle.getVehicleBrandId();
			String vehicleModelId = vehicle.getVehicleModelId();
			
			if( !TextUtils.isEmpty(bindingShopId) ) {
				params.put("sellShopId", bindingShopId);
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
			if( !TextUtils.isEmpty(vehicleBrandId) ) {
				params.put("vehicleBrandId", vehicleBrandId);
			}
			if( !TextUtils.isEmpty(vehicleModelId) ) {
				params.put("vehicleModelId", vehicleModelId);
			}
			if( !TextUtils.isEmpty(currentMileage) && !"null".equalsIgnoreCase(currentMileage)) {
				params.put("currentMileage",currentMileage);
			} 
			if( !TextUtils.isEmpty(nextMaintainMileage) && !"null".equalsIgnoreCase(nextMaintainMileage)) {
				params.put("nextMaintainMileage", nextMaintainMileage);
			} 
			if( !TextUtils.isEmpty(nextInsuranceTime)) {
				params.put("nextInsuranceTime", nextInsuranceTime);
			} 
			if( !TextUtils.isEmpty(nextExamineTime) ) {
				params.put("nextExamineTime", nextExamineTime);
			} 
			TongGouApplication.showLog(nextInsuranceTime + "  " + nextExamineTime);
		}
		
		params.put("userNo", userNo);
		params.put("obdSN", obdSN);
		params.put("vehicleNo", vehicleNo);
		params.put("vehicleVin", vehicleVin);
		params.put("vehicleBrand", vehicleBrand);
		params.put("vehicleModel", vehicleModel);
		
		if( !TextUtils.isEmpty(vehicleId) ) {
			params.put("vehicleId", vehicleId);
		} 
		super.setRequestParams(params);
	}

}
