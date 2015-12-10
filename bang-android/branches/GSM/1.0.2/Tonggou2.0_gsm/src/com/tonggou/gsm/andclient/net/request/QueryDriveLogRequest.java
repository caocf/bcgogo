package com.tonggou.gsm.andclient.net.request;

import com.tonggou.gsm.andclient.net.API;
import com.tonggou.gsm.andclient.net.APIQueryParam;
import com.tonggou.gsm.andclient.net.AbsTonggouHttpGetRequest;

public class QueryDriveLogRequest extends AbsTonggouHttpGetRequest {

	@Override
	public String getOriginApi() {
		return API.QEURY_DRIVE_LOG;
	}
	
	/**
	 * 
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 */
	public void setApiParams(long startTime, long endTime) {
		APIQueryParam params = new APIQueryParam(false);
		params.put("startTime", startTime);
		params.put("endTime", endTime);
		super.setApiParams(params);
	}
	
}
