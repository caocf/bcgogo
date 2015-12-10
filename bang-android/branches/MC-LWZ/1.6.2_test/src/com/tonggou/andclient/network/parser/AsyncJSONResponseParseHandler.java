package com.tonggou.andclient.network.parser;

import java.net.SocketTimeoutException;

import org.apache.http.Header;

import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.jsonresponse.BaseResponse;
import com.tonggou.andclient.network.NetworkState;

/**
 * 异步网络请求解析处理器
 * <p> 请求是异步的，JSON解析是同步的
 * <p> 该类是对 AsyncHttpResponseHandler 类的扩展，在其的 OnSuccess 方法中进行 JSON 解析，以及 Session/Cookie 的存储
 * @author lwz
 *
 * @param <T>
 */
public abstract class AsyncJSONResponseParseHandler<T extends BaseResponse> extends AsyncHttpResponseHandler implements IJSONParseHandler<T> {
	
	private static String TAG = "AsyncJSONResponseParseHandler";
	
	@Override
	public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		super.onFailure(arg0, arg1, arg2, arg3);
		if( arg3 != null ) {
			TongGouApplication.showLog("request onFailure @ " + arg3.getMessage());
		} else {
			TongGouApplication.showLog("request onFailure @ " + new String(arg2));
		}
		if( arg3 instanceof SocketTimeoutException ) {
			TongGouApplication.showToast(NetworkState.ERROR_CLIENT_ERROR_SOCKETTIMEOUT_MESSAGE);
		} else {
			TongGouApplication.showToast(NetworkState.ERROR_CLIENT_ERROR_TIMEOUT_MESSAGE);
		}
 	}

	@Override
	public void onFinish() {
		super.onFinish();
		TongGouApplication.showLog("request finish");
	}

	@Override
	public void onRetry() {
		super.onRetry();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, byte[] result) {
		super.onSuccess(statusCode, headers, result);
		Log.i(TAG, new String(result));
		BaseResponseParser<T> parser = new BaseResponseParser<T>(getTypeClass());
		try {
			T response = parser.parse(new String(result));
			if( response == null ) {
				onFailure(statusCode, headers, ("返回的数据" + new String(result)  + "解析为 null").getBytes(), null);
				return;
			}
			if( response.isSuccess() ) {
				onParseSuccess(response, result);
			} else {
				
				onParseFailure(response.getMsgCode() + "", response.getMessage());
				if( response.getMsgCode() == -202 ) {
					 // 登录过期
					TongGouApplication.getInstance().doExpireLogin();
				}
			}
			
		} catch (Exception e) {
			onParseException(e);
		}
	}

	@Override
	public void onParseSuccess(T result, byte[] originResult) {
		
	}

	@Override
	public void onParseFailure(String errorCode, String errorMsg) {
		if( !"-202".equals( errorCode ) ) {
			TongGouApplication.showToast(errorMsg);
		}
	}

	@Override
	public void onParseException(Exception e) {
		e.printStackTrace();
	}
	
	public abstract Class<T> getTypeClass();
	
}
