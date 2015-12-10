package com.tonggou.yf.andclient.net;

import com.tonggou.lib.net.Constants;
import com.tonggou.lib.net.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.lib.net.response.BaseResponse;
import com.tonggou.yf.andclient.App;
import com.tonggou.yf.andclient.ui.LoginActivity;

public abstract class TonggouResponseParseHandler<T extends BaseResponse> extends AsyncJsonBaseResponseParseHandler<T> {

	@Override
	public void onParseFailure(String errorCode, String errorMsg) {
		if( !String.valueOf(Constants.NETWORK_STATUS_CODE.CODE_LOGIN_EXPIRE).equalsIgnoreCase( errorCode )) {
			App.showShortToast(errorMsg);
			App.debug("TonggouResponseParseHandler", errorMsg);
		} else {
			LoginActivity.loginExpire();
//			App.showLongToast("登录过期");
		}
	}

}
