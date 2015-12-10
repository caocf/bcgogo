package com.tonggou.andclient.network.request;

import java.util.List;

import com.tonggou.andclient.network.API;
import com.tonggou.andclient.vo.CarCondition;

public class SendMultiFaultCodeRequest extends AbsTonggouHttpRequest {

	@Override
	public String getAPI() {
		return API.SEND_MULTI_FAULT;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}
	
	public void setRequestParams(List<CarCondition> conditions) {
		HttpRequestParams params = new HttpRequestParams();
		final int size = conditions.size();
		for( int i=0; i<size; i++ ) {
			CarCondition item = conditions.get(i);
			String paramsprefix = "vehicleFaults[" + i;
			params.put(paramsprefix + "].faultCode", item.getFaultCode());
			params.put(paramsprefix + "].obdSN", item.getObdSN());
			params.put(paramsprefix + "].reportTime", item.getReportTime());
			params.put(paramsprefix + "].vehicleId", item.getVehicleId());
		}
		super.setRequestParams(params);
	}

}
