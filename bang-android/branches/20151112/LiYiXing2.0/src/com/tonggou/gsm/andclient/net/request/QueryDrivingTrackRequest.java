package com.tonggou.gsm.andclient.net.request;

import com.tonggou.gsm.andclient.net.API;
import com.tonggou.gsm.andclient.net.APIQueryParam;
import com.tonggou.gsm.andclient.net.AbsTonggouHttpGetRequest;

/**
 * 查询行车轨迹
 * @author lwz
 *
 */
public class QueryDrivingTrackRequest extends AbsTonggouHttpGetRequest {

	@Override
	public String getOriginApi() {
		return API.QEURY_DRIVING_TRACK;
	}

	/**
	 * 记需要下载轨迹的记录 id
	 * @param logId
	 */
	public void setApiParams(String logId) {
		APIQueryParam params = new APIQueryParam(false);
		params.put("contactIds", "NULL");
		params.put("detailIds", logId);
		super.setApiParams(params);
	}
}