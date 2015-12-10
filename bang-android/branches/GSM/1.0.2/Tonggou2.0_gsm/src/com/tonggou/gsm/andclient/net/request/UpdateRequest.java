package com.tonggou.gsm.andclient.net.request;

import android.os.Build;

import com.tonggou.gsm.andclient.net.API;
import com.tonggou.gsm.andclient.net.APIQueryParam;
import com.tonggou.gsm.andclient.net.AbsTonggouHttpGetRequest;

public class UpdateRequest extends AbsTonggouHttpGetRequest {

	@Override
	public String getOriginApi() {
		return API.UPDATE;
	}
	
	public void setApiParams(String appVersion) {
		APIQueryParam params = new APIQueryParam();
		params.put("platform", "ANDROID");
		params.put("appVersion", appVersion);
		params.put("platformVersion", Build.VERSION.RELEASE + "-SDK" + Build.VERSION.SDK_INT);
		params.put("mobileModel", Build.BRAND + "-" + Build.MODEL);
		super.setApiParams(params);
	}

}
