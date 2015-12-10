package com.tonggou.andclient.network.request;

import android.text.TextUtils;

import com.tonggou.andclient.network.API;

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
	 */
	public void setRequestParams(String userNo, String obdSN, String vehicleId, String vehicleNo, String vehicleVin, String vehicleBrand, String vehicleModel) {
		HttpRequestParams params = new HttpRequestParams();
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
