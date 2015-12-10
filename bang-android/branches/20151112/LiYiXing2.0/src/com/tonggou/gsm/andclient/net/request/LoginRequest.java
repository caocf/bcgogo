package com.tonggou.gsm.andclient.net.request;

import android.os.Build;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.net.API;
import com.tonggou.gsm.andclient.net.AbsTonggouHttpRequest;
import com.tonggou.gsm.andclient.net.HttpMethod;
import com.tonggou.gsm.andclient.net.HttpRequestParams;
import com.tonggou.gsm.andclient.util.PackageInfoUtil;
import com.tonggou.gsm.andclient.util.UmengMessageUtil;

/**
 * 用户登录请求
 * @author lwz
 *
 */
public class LoginRequest extends AbsTonggouHttpRequest {

	@Override
	public String getAPI() {
		return API.LOGIN;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}

	public void setRequestParams(String userNo, String password ) {
		HttpRequestParams params = new HttpRequestParams();
		params.put("userNo", userNo);
		params.put("password", password);
		params.put("platform", "ANDROID");
		params.put("platformVersion", Build.VERSION.RELEASE + "-SDK" + Build.VERSION.SDK_INT);
		params.put("mobileModel", Build.BRAND + "-" + Build.MODEL);
		params.put("appVersion", PackageInfoUtil.getVersionName(App.getInstance()));
		params.put("imageVersion", "480X800");
		params.put("umDeviceToken", UmengMessageUtil.getDeviceToken(App.getInstance()));
		super.setRequestParams(params);
	}
}