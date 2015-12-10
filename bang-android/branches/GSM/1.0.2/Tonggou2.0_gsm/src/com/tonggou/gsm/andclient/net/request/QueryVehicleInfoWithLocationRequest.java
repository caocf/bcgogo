package com.tonggou.gsm.andclient.net.request;

import com.tonggou.gsm.andclient.net.API;
import com.tonggou.gsm.andclient.net.AbsTonggouHttpGetRequest;

/**
 * 查询车辆信息，包含位置信息
 * @author lwz
 *
 */
public class QueryVehicleInfoWithLocationRequest extends AbsTonggouHttpGetRequest {

	@Override
	public String getOriginApi() {
		return API.QEURY_VEHICLE_INFO_WITH_LOCATION;
	}

}
