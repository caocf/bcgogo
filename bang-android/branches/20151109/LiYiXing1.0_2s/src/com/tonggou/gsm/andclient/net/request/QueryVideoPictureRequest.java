package com.tonggou.gsm.andclient.net.request;

import com.tonggou.gsm.andclient.net.APINew;
import com.tonggou.gsm.andclient.net.APIQueryParam;
import com.tonggou.gsm.andclient.net.AbsTonggouHttpGetRequest;
/**
 * QueryVideoPictureRequest
 * @author peter
 * 
 */
public class QueryVideoPictureRequest extends AbsTonggouHttpGetRequest {
	private long vehicleId;
	private long dateTime;
	private int count;

	@Override
	public String getOriginApi() {
		return APINew.getQueryViedeoRecordAPI(vehicleId, dateTime, count);
	}

	/**
	 * @param vehicleId 汽车ime号
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 */
	public void setApiParams(String vehicleId, long dateTime, int count) {
		this.vehicleId = Long.parseLong(vehicleId);
		this.dateTime = dateTime;
		this.count = count;

		APIQueryParam params = new APIQueryParam(false);
		super.setApiParams(params);
	}
}