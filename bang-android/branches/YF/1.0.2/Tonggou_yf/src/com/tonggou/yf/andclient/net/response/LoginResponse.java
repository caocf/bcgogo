package com.tonggou.yf.andclient.net.response;

import com.google.gson.annotations.SerializedName;
import com.tonggou.lib.net.response.BaseResponse;
import com.tonggou.yf.andclient.bean.UserScope;

public class LoginResponse extends BaseResponse {

	private static final long serialVersionUID = -711642732669307263L;
	
	Object appConfig;
	Object appUserConfig;
	Object obdList;
	
	@SerializedName("privilegeMap")
    UserScope userScope;

    public UserScope getUserScope() {
		return userScope;
	}
}
