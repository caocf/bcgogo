package com.tonggou.gsm.andclient.net.request;

import android.text.TextUtils;

import com.tonggou.gsm.andclient.bean.type.DTCStatus;
import com.tonggou.gsm.andclient.net.API;
import com.tonggou.gsm.andclient.net.AbsTonggouHttpRequest;
import com.tonggou.gsm.andclient.net.HttpMethod;
import com.tonggou.gsm.andclient.net.HttpRequestParams;

/**
 * 修改故障码请求
 * @author lwz
 *
 */
public class ModifyDTCStatusRequest extends AbsTonggouHttpRequest {

	@Override
	public String getAPI() {
		return API.MODIFY_DTC_STATUS;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}

	/**
	 * 
	 * @param dtcId		can be null, 若为 null 则说明是本地故障，不为 null 则说明是 历史故障
	 * @param dtcCode			故障码
	 * @param currentStatus		当前状态
	 * @param destStatus		目标状态
	 * @param vehicleId			发生故障的车辆 id
	 */
	public void setRequestParams(String dtcId, String dtcCode, DTCStatus currentStatus, DTCStatus destStatus, String vehicleId) {
		HttpRequestParams params = new HttpRequestParams();
		if( !TextUtils.isEmpty(dtcId) ) {
			params.put("appVehicleFaultInfoDTOs[0].id", dtcId);
		}
		params.put("appVehicleFaultInfoDTOs[0].errorCode", dtcCode);
		params.put("appVehicleFaultInfoDTOs[0].status", destStatus.toString());
		params.put("appVehicleFaultInfoDTOs[0].lastStatus", currentStatus.toString());
		params.put("appVehicleFaultInfoDTOs[0].appVehicleId", vehicleId);
		super.setRequestParams(params);
	}
	
	

}
