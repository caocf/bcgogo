package com.tonggou.andclient.network.request;

import com.tonggou.andclient.network.API;

/**
 * 修改密码请求
 * @author lwz
 *
 */
public class ModifyPasswordRequest extends AbsTonggouHttpRequest {

	@Override
	public String getAPI() {
		return API.MODIFY_PASSWORD;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.PUT;
	}

	/**
	 * 
	 * @param userNo	用户名
	 * @param oldPwd	旧密码
	 * @param newPwd	新密码
	 */
	public void setRequestParams(String userNo, String oldPwd, String newPwd) {
		HttpRequestParams params = new HttpRequestParams();
		params.put("userNo", userNo);
		params.put("oldPassword", oldPwd);
		params.put("newPassword", newPwd);
		super.setRequestParams(params);
	}
	
}
