package com.tonggou.andclient.network.request;

import android.text.TextUtils;

import com.tonggou.andclient.network.API;
import com.tonggou.andclient.vo.type.FaultCodeStatusType;

/**
 * 修改故障码请求
 * @author lwz
 *
 */
public class ModifyVehicleFaultStatusRequest extends AbsTonggouHttpRequest {

	@Override
	public String getAPI() {
		return API.MODIFY_VEHICLE_FAULT_STATUS;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}

	/**
	 * 
	 * @param faultCodeId		can be null, 若为 null 则说明是本地故障，不为 null 则说明是 历史故障
	 * @param faultCode			故障码
	 * @param currentStatus		当前状态
	 * @param destStatus		目标状态
	 * @param vehicleId			发生故障的车辆 id
	 */
	public void setRequestParams(String faultCodeId, String faultCode, FaultCodeStatusType currentStatus, FaultCodeStatusType destStatus, String vehicleId) {
		HttpRequestParams params = new HttpRequestParams();
		if( !TextUtils.isEmpty(faultCodeId) ) {
			params.put("appVehicleFaultInfoDTOs[0].id", faultCodeId);
		}
		params.put("appVehicleFaultInfoDTOs[0].errorCode", faultCode);
		params.put("appVehicleFaultInfoDTOs[0].status", destStatus.getValue());
		params.put("appVehicleFaultInfoDTOs[0].lastStatus", currentStatus.getValue());
		params.put("appVehicleFaultInfoDTOs[0].appVehicleId", vehicleId);
		super.setRequestParams(params);
	}
	
	

}
