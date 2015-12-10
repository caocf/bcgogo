package com.tonggou.andclient.network.request;

/**
 * 获取车辆列表请求
 * @author lwz
 *
 */
public class QueryVehicleListRequest extends AbsTonggouHttpRequest {

	private final String PARAM_USER_NO = "userNo";
	private String userNo;
	
	@Override
	public String getAPI() {
		APIQueryParam param = new APIQueryParam(true);
		param.put(PARAM_USER_NO, userNo);
		return HttpRequestClient.getAPIWithQueryParams( API.VEHICLE_LIST , param);
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.GET;
	}
	
	public void setRequestParams(String userNo) {
		this.userNo = userNo;
	}
	
}
