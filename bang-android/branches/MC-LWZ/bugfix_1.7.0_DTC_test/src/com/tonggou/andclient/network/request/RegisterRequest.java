package com.tonggou.andclient.network.request;

import com.tonggou.andclient.network.API;
import com.tonggou.andclient.util.INFO;

/**
 * ”√ªß◊¢≤·«Î«Û
 * @author lwz
 *
 */
public class RegisterRequest extends AbsTonggouHttpRequest {

	@Override
	public String getAPI() {
		return API.REGISTER;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.PUT;
	}
	
	public void setRequestParams(String userNo, String password, String mobile) {
		HttpRequestParams params = new HttpRequestParams();
		params.put("userNo", userNo);
		params.put("password", password);
		params.put("mobile", mobile);
		params.put("loginInfo", getLoginInfo());
		super.setRequestParams(params);
	}
	
	private HttpRequestParams getLoginInfo() {
		HttpRequestParams params = new HttpRequestParams();
			params.put("platform", INFO.MOBILE_PLATFORM);
			params.put("appVersion", INFO.VERSION);
			params.put("platformVersion", INFO.MOBILE_PLATFORM_VERSION);
			params.put("mobileModel", INFO.MOBILE_MODEL);
			params.put("imageVersion", INFO.IMAGE_VERSION);
		return params;
	}

}
