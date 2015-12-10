package com.tonggou.gsm.andclient.net.parser;

import java.net.SocketTimeoutException;

import org.apache.http.Header;

import com.loopj.android.http.TextHttpResponseHandler;
import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.Constants;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.net.response.IResponse;
import com.tonggou.gsm.andclient.ui.LoginActivity;

/**
 * 异步网络请求解析处理器
 * <p> 请求是异步的，JSON解析是同步的
 * <p> 该类是对 {@link com.loopj.android.http.AsyncHttpResponseHandler} 类的扩展，在其的 OnSuccess 方法中进行 JSON 解析
 * @author lwz
 *
 * @param <T>
 */
public abstract class AsyncJsonResponseParseHandler<T extends IResponse> extends TextHttpResponseHandler implements IJSONParseHandler<T> {
	
	private static String TAG = "AsyncJSONResponseParseHandler";
	
	@Override
	public void onFinish() {
		super.onFinish();
		App.debug(TAG, getRequestURI() + "  request finish");
	}

	@Override
	public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
			if( error != null ) {
				App.debug(TAG, getRequestURI() + "  request onFailure @ statusCode" + statusCode + "  " + error.getMessage());
			} else {
				App.debug(TAG, getRequestURI() + "  request onFailure @ statusCode" + statusCode + "  " + responseBody);
			}
			if( error instanceof SocketTimeoutException ) {
				App.showShortToast(App.getInstance().getString(R.string.network_socket_timeout));
			} else {
				App.showShortToast(App.getInstance().getString(R.string.network_invalidate));
			}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, String result) {
		App.debug(TAG, getRequestURI() + "  " +  result);
		IResponseParser<T> parser = getJsonResponseParser();
		try {
			T response = parser.parse(result);
			if( response == null ) {
				onFailure(statusCode, headers, ("返回的数据" + result  + "解析为 null").getBytes(), null);
				return;
			}
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
	public void onParseSuccess(T result, String originResult) {
		
	}

	@Override
	public void onParseFailure(String errorCode, String errorMsg) {
		if( !String.valueOf(Constants.NETWORK_STATUS_CODE.CODE_LOGIN_EXPIRE).equalsIgnoreCase( errorCode )) {
			App.showShortToast(errorMsg);
			App.debug(TAG, errorMsg);
		} else {
			LoginActivity.loginExpire();
		}
	}

	@Override
	public void onParseException(Exception e) {
		e.printStackTrace();
	}
	
}
