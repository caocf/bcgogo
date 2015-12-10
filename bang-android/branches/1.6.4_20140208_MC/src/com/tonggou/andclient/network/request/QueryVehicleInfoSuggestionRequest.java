package com.tonggou.andclient.network.request;


/**
 * 请求后台中用户已有的车辆
 * <p> 由于注册流程修改，该类已经不使用了
 * @author lwz
 *
 */
@Deprecated
public class QueryVehicleInfoSuggestionRequest extends AbsTonggouHttpGetRequest {

	@Override
	protected String getOriginApi() {
//		return API.QUERY_VEHICLE_INFO_SUGGESTION;
		return "";
	}
	
	public void setApiParams(String mobile, String vehicleNo) {
		APIQueryParam params = new APIQueryParam(false);
		params.put("mobile", mobile);
		params.put("vehicleNo", vehicleNo);
		super.setApiParams(params);
	}

}
