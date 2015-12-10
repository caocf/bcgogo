package com.tonggou.gsm.andclient.net.request;

import com.tonggou.gsm.andclient.net.API;
import com.tonggou.gsm.andclient.net.AbsTonggouHttpRequest;
import com.tonggou.gsm.andclient.net.HttpMethod;
import com.tonggou.gsm.andclient.net.HttpRequestParams;

/**
 * 修改密码
 * @author lwz
 *
 */
public class ModifyPasswordRequest extends AbsTonggouHttpRequest {

	@Override
	public String getAPI() {
		return API.MODIFY_PWD;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.PUT;
	}

	/**
	 * 
	 * @param userNo
	 * @param oldPassword	旧密码
	 * @param newPassword	新密码
	 */
	public void setRequestParams(String userNo, String oldPassword, String newPassword) {
		HttpRequestParams params = new HttpRequestParams();
		params.put("userNo", userNo);
		params.put("oldPassword", oldPassword);
		params.put("newPassword", newPassword);
		super.setRequestParams(params);
	}
	
}
