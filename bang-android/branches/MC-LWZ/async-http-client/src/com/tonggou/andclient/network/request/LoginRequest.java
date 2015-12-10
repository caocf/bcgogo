package com.tonggou.andclient.network.request;

import com.loopj.android.http.RequestParams;
import com.tonggou.andclient.util.INFO;

/**
 * ÓÃ»§µÇÂ¼ÇëÇó
 * @author lwz
 *
 */
public class LoginRequest extends AbsTonggouHttpRequest {
	
	public static final String PARAM_USER_NO = "userNo";
	public static final String PARAM_PWD = "password";
	public static final String PARAM_PLATFORM = "platform";
	public static final String PARAM_PLATFORM_VERSION = "platformVersion";
	public static final String PARAM_MOBILE_MODE = "mobileModel";
	public static final String PARAM_APP_VERSION = "appVersion";
	public static final String PARAM_IMAGE_VERSION = "imageVersion";

	@Override
	public String getAPI() {
		return API.LOGIN;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}
	
	public void setRequestParams(String userNo, String password ) {
		RequestParams params = new RequestParams();
		params.put(PARAM_USER_NO, userNo);
		params.put(PARAM_PWD, password);
		params.put(PARAM_PLATFORM, INFO.MOBILE_PLATFORM);
		params.put(PARAM_PLATFORM_VERSION, INFO.MOBILE_PLATFORM_VERSION);
		params.put(PARAM_MOBILE_MODE, INFO.MOBILE_MODEL);
		params.put(PARAM_APP_VERSION, INFO.VERSION);
		params.put(PARAM_IMAGE_VERSION, INFO.IMAGE_VERSION);
		super.setRequestParams(params);
	}
	
}
