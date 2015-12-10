package com.tonggou.gsm.andclient.net.request;

import com.tonggou.gsm.andclient.net.API;
import com.tonggou.gsm.andclient.net.APIQueryParam;
import com.tonggou.gsm.andclient.net.AbsTonggouHttpGetRequest;

public class ResetPasswordRequest extends AbsTonggouHttpGetRequest {

	@Override
	public String getOriginApi() {
		return API.RESET_PWD;
	}
	
	/**
	 * 
	 * @param phoneNo	手机号
	 */
	public void setApiParams(String phoneNo) {
		APIQueryParam params = new APIQueryParam();
		params.put("mobile", phoneNo);
		super.setApiParams(params);
	}

}
