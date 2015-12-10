package com.tonggou.gsm.andclient.net.request;

import com.tonggou.gsm.andclient.net.APINew;
import com.tonggou.gsm.andclient.net.APIQueryParam;
import com.tonggou.gsm.andclient.net.AbsTonggouHttpGetRequest;
/**
 * QueryMonitorPictureRequest
 * @author peter
 * 
 */
public class QueryMonitorPictureRequest extends AbsTonggouHttpGetRequest {
	private long vehicleId;

	@Override
	public String getOriginApi() {
		return APINew.getRealTimeMonitorAPI(vehicleId);
	}

	/**
	 * @param vehicleId 汽车ime号
	 */
	public void setApiParams(String vehicleId) {
		this.vehicleId = Long.parseLong(vehicleId);

		APIQueryParam params = new APIQueryParam(true);
		super.setApiParams(params);
	}
}