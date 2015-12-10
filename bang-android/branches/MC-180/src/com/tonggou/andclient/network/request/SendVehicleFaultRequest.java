package com.tonggou.andclient.network.request;

import com.tonggou.andclient.network.API;

/**
 * 发送故障码请求
 * @author lwz
 *
 */
public class SendVehicleFaultRequest extends AbsTonggouHttpRequest {

	@Override
	public String getAPI() {
		return API.SEND_VEHICLE_FAULT;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}

	/**
	 * 
	 * @param userNo		用户账号
	 * @param obdSN			obd唯一标识号 MAC
	 * @param vehicleVin	车辆唯一标识号
	 * @param vehicleId		后台数据主键 , not null
	 * @param reportTime	故障时间
	 * @param faultCode		故障码 如果有多个故障码请以逗号 ,分开；  一起可以接收多个故障码
	 */
	public void setRequestParams(String userNo, String obdSN, String vehicleVin, String vehicleId, long reportTime, String faultCode) {
		HttpRequestParams params = new HttpRequestParams();
		params.put("userNo", userNo);
		params.put("obdSN", obdSN);
		params.put("vehicleVin", vehicleVin);
		params.put("vehicleId", vehicleId);
		params.put("reportTime", reportTime);
		params.put("faultCode", faultCode);
		super.setRequestParams(params);
	}
	
	

}
