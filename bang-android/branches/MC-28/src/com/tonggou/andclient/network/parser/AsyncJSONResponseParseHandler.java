package com.tonggou.andclient.network.parser;

import java.net.SocketTimeoutException;

import org.apache.http.Header;

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
	
//	private static String TAG = "AsyncJSONResponseParseHandler";
	
	@Override
	public void onFinish() {
		super.onFinish();
		TongGouApplication.showLog(getRequestURI() + "  request finish");
	}

	@Override
	public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
			if( error != null ) {
				TongGouApplication.showLog(getRequestURI() + "  request onFailure @ " + error.getMessage());
			} else {
				TongGouApplication.showLog(getRequestURI() + "  request onFailure @ " + new String(responseBody));
			}
			if( error instanceof SocketTimeoutException ) {
				TongGouApplication.showToast(NetworkState.ERROR_CLIENT_ERROR_SOCKETTIMEOUT_MESSAGE);
			} else {
				TongGouApplication.showToast(NetworkState.ERROR_CLIENT_ERROR_TIMEOUT_MESSAGE);
			}
		
	}

	@Override
	public void onRetry(int retryNo) {
		super.onRetry(retryNo);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, byte[] result) {
		TongGouApplication.showLog(getRequestURI() + "  " +  new String(result));
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
