package com.tonggou.andclient.test;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.apache.http.Header;
import org.apache.http.conn.ssl.SSLSocketFactory;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.jsonresponse.BaseResponse;
import com.tonggou.andclient.jsonresponse.VehicleListResponse;
import com.tonggou.andclient.network.SSLSocketFactoryEx;
import com.tonggou.andclient.network.parser.AsyncJSONResponseParseHandler;
import com.tonggou.andclient.network.request.LoginRequest;
import com.tonggou.andclient.network.request.PollingMessageRequest;
import com.tonggou.andclient.network.request.QueryShopListRequest;
import com.tonggou.andclient.network.request.QueryVehicleListRequest;
import com.tonggou.andclient.vo.type.CoordinateType;
import com.tonggou.andclient.vo.type.MessageType;
import com.tonggou.andclient.vo.type.ShopType;
import com.tonggou.andclient.vo.type.SortType;

public class LoginActivity extends Activity {
	
	public String mUserNo = "15200000011";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		doLogin();
		
		
		
	}
	
	public static final String PARAM_USER_NO = "userNo";
	public static final String PARAM_PWD = "password";
	public static final String PARAM_PLATFORM = "platform";
	public static final String PARAM_PLATFORM_VERSION = "platformVersion";
	public static final String PARAM_MOBILE_MODE = "mobileModel";
	public static final String PARAM_APP_VERSION = "appVersion";
	public static final String PARAM_IMAGE_VERSION = "imageVersion";
	
	public void doLogin() {
		LoginRequest request = new LoginRequest();
		request.setRequestParams(mUserNo, "111111");
		request.doRequest(this, new AsyncJSONResponseParseHandler<BaseResponse>() {
			
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] result) {
				super.onSuccess(statusCode, headers, result);
				doPollingMsg();
				TongGouApplication.showLog("onSuccess   " + new String(result));
			}

			@Override
			public Class<BaseResponse> getTypeClass() {
				return BaseResponse.class;
			}
			
		});
		
//		AsyncHttpClient client = new AsyncHttpClient();
//		RequestParams params = new RequestParams();
//		params.put(PARAM_USER_NO, "15200000011");
//		params.put(PARAM_PWD, "111111");
//		params.put(PARAM_PLATFORM, INFO.MOBILE_PLATFORM);
//		params.put(PARAM_PLATFORM_VERSION, INFO.MOBILE_PLATFORM_VERSION);
//		params.put(PARAM_MOBILE_MODE, INFO.MOBILE_MODEL);
//		params.put(PARAM_APP_VERSION, INFO.VERSION);
//		params.put(PARAM_IMAGE_VERSION, INFO.IMAGE_VERSION);
//		
//		client.setSSLSocketFactory(getSSLSocketFactory());
//		client.post(API.LOGIN, params, new AsyncHttpResponseHandler() {
//
//			@Override
//			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
//				super.onFailure(arg0, arg1, arg2, arg3);
//				TongGouApplication.showLog("onFailure  " + arg3.getMessage());
//			}
//
//			@Override
//			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
//				super.onSuccess(arg0, arg1, arg2);
//				TongGouApplication.showLog("onSuccess   " + new String(arg2));
//			}
//			
//		});
	}
	
	private SSLSocketFactory getSSLSocketFactory() {
		SSLSocketFactory sf = null;
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);        
			sf = new SSLSocketFactoryEx(trustStore);  
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  
		return sf;
	}
	
	public void doQueryVehicleList() {
		QueryVehicleListRequest request = new QueryVehicleListRequest();
		request.setApiParams(mUserNo);
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
	
	public void doPollingMsg() {
		PollingMessageRequest request = new PollingMessageRequest();
		request.setApiParams(mUserNo, MessageType.ALL);
		request.doRequest(this, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				super.onSuccess(arg0, arg1, arg2);
				TongGouApplication.showLog( new String(arg2) );
			}
			
			@Override
			public void onStart() {
				super.onStart();
				TongGouApplication.showLog("onStart  doPollingMsg");
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				TongGouApplication.showLog("onFinish  doPollingMsg");
				doQueryVehicleList();
			}
		});
	}
	
	public void doQueryShopListRequest() {
		QueryShopListRequest request = new QueryShopListRequest();
		request.setApiParams(CoordinateType.CURRENT, "119.936427,31.934001", null, SortType.DISTANCE, null, null, ShopType.ALL, null, false, 1, 10);
		request.doRequest(this, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				super.onSuccess(arg0, arg1, arg2);
				TongGouApplication.showLog( new String(arg2) );
			}
			
			@Override
			public void onStart() {
				super.onStart();
				TongGouApplication.showLog("onStart  doQueryShopListRequest");
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				TongGouApplication.showLog("onFinish  doQueryShopListRequest");
			}
		});
	}
	
}
