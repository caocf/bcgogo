package com.tonggou.yf.andclient.net.request;

import android.os.Build;

import com.tonggou.lib.net.APIQueryParam;
import com.tonggou.lib.net.AbsTonggouHttpGetRequest;
import com.tonggou.yf.andclient.net.API;

public class UpdateVersionRequest extends AbsTonggouHttpGetRequest {

	@Override
	public String getOriginApi() {
		return API.UPDATE_VERSION;
	}

	public void setApiParams(String version) {
		APIQueryParam params = new APIQueryParam(true);
		params.put("platform", "ANDROID");
		params.put("appVersion", version);
		params.put("platformVersion", Build.VERSION.RELEASE + "-SDK" + Build.VERSION.SDK_INT);
		params.put("mobileModel", Build.BRAND + "-" + Build.MODEL);
		super.setApiParams(params);
	}
}
