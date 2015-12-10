package com.tonggou.gsm.andclient.net.request;

import com.tonggou.gsm.andclient.net.API;
import com.tonggou.gsm.andclient.net.AbsTonggouHttpRequest;
import com.tonggou.gsm.andclient.net.HttpMethod;
import com.tonggou.gsm.andclient.net.HttpRequestParams;

/**
 * 注册验证请求
 * @author lwz
 *
 */
public class RegisterValidateRequest extends AbsTonggouHttpRequest {

	@Override
	public String getAPI() {
		return API.REGISTER_VALIDATE;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.PUT;
	}

	/**
	 * 
	 * @param userNo 用户名 （手机号）
	 * @param pwd	密码
	 * @param IMEI	IMEI 号
	 */
	public void setRequestParams(String userNo, String pwd, String IMEI) {
		HttpRequestParams params = new HttpRequestParams();
		params.put("mobile", userNo);
		params.put("password", pwd);
		params.put("imei", IMEI);
		super.setRequestParams(params);
	}

}
