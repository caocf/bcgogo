package com.tonggou.andclient.network.parser;

import org.apache.http.Header;

import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tonggou.andclient.jsonresponse.BaseResponse;

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
	}

	@Override
	public void onFinish() {
		super.onFinish();
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
		Log.i(TAG, new String(result));
		BaseResponseParser<T> parser = new BaseResponseParser<T>(getTypeClass());
		try {
			T response = parser.parse(new String(result));
			if( response.isSuccess() ) {
				onParseSuccess(response, result);
			} else {
				onParseFailure(response.getMsgCode() + "", response.getMessage());
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
		
	}

	@Override
	public void onParseException(Exception e) {
		e.printStackTrace();
	}
	
	public abstract Class<T> getTypeClass();
	
}
