package com.tonggou.andclient.test;

import org.apache.http.Header;

import android.app.Activity;
import android.os.Bundle;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.jsonresponse.BaseResponse;
import com.tonggou.andclient.jsonresponse.VehicleListResponse;
import com.tonggou.andclient.network.parser.AsyncJSONResponseParseHandler;
import com.tonggou.andclient.network.request.LoginRequest;
import com.tonggou.andclient.network.request.QueryVehicleListRequest;

public class LoginActivity extends Activity {
	
	public String mUserNo = "15200000011";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		doLogin();
		
		
		
	}
	
	public void doLogin() {
		LoginRequest request = new LoginRequest();
		request.setRequestParams(mUserNo, "111111");
		request.doRequest(this, new AsyncJSONResponseParseHandler<BaseResponse>() {
			
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] result) {
				super.onSuccess(statusCode, headers, result);
				doQueryVehicleList(mUserNo);
			}

			@Override
			public Class<BaseResponse> getTypeClass() {
				return BaseResponse.class;
			}
			
		});
	}
	
	public void doQueryVehicleList(String userNo) {
		QueryVehicleListRequest request = new QueryVehicleListRequest();
		request.setRequestParams(userNo);
		request.doRequest(this, new AsyncJSONResponseParseHandler<VehicleListResponse>() {

			@Override
			public void onStart() {
				super.onStart();
				TongGouApplication.showLog("onStart");
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				TongGouApplication.showLog("onFinish");
			}
			
			@Override
			public void onParseSuccess(VehicleListResponse result, byte[] originResult) {
				TongGouApplication.showLog( 
						"vehicleNo = " + result.getVehicleList().get(0).getVehicleNo() );
			}
			
			@Override
			public void onParseFailure(String errorCode, String errorMsg) {
				super.onParseFailure(errorCode, errorMsg);
				TongGouApplication.showLog(errorCode + "  " + errorMsg);
			}

			@Override
			public Class<VehicleListResponse> getTypeClass() {
				return VehicleListResponse.class;
			}
			
		});
	}
	
}
