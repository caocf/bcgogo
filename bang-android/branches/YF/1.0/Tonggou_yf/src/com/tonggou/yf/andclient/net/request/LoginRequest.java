package com.tonggou.yf.andclient.net.request;

import com.tonggou.lib.net.AbsTonggouHttpRequest;
import com.tonggou.lib.net.HttpMethod;
import com.tonggou.lib.net.HttpRequestParams;
import com.tonggou.yf.andclient.App;
import com.tonggou.yf.andclient.net.API;
import com.tonggou.yf.andclient.util.PackageInfoUtil;
import com.tonggou.yf.andclient.util.UmengMessageUtil;

public class LoginRequest extends AbsTonggouHttpRequest {

	@Override
	public String getAPI() {
		return API.LOGIN;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}
	
	/**
	 * 
	 * @param userNo 用户名
	 * @param password 密码
	 */
	public void setRequestParams(String userNo, String password) {
		HttpRequestParams params = new HttpRequestParams();
		params.put("userNo", userNo);
		params.put("password", password);
		params.put("imageVersion", "320X480");
		params.put("appVersion", PackageInfoUtil.getVersionName(App.getInstance()));
		params.put("umDeviceToken", UmengMessageUtil.getDeviceToken(App.getInstance()));
		super.setRequestParams(params);
	}

}
