package com.tonggou.gsm.andclient.test;

import android.app.Activity;
import android.os.Bundle;

import com.tonggou.gsm.andclient.net.parser.AsyncLoadCacheJsonBaseResponseParseHandler;
import com.tonggou.gsm.andclient.net.request.LoginRequest;
import com.tonggou.gsm.andclient.net.response.BaseResponse;

public class TestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setRequestParams("15210000111", "111111");
		loginRequest.doRequest(this, new AsyncLoadCacheJsonBaseResponseParseHandler<BaseResponse>() {

			@Override
			public void onLoadCache(BaseResponse result, String originResult,
					boolean isNetworkConnected) {
				
			}
			
			@Override
			public Class<BaseResponse> getTypeClass() {
				return BaseResponse.class;
			}
		});
	}
	
	
}
