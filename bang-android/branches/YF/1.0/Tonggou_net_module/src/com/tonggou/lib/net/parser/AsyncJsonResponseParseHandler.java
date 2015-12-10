package com.tonggou.lib.net.parser;

import java.net.SocketTimeoutException;

import org.apache.http.Header;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.TextHttpResponseHandler;
import com.tonggou.lib.R;
import com.tonggou.lib.net.response.IResponse;

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
	
	private Context mContext;
	
	public void setContext(Context context) {
		mContext = context;
	}
	
	@Override
	public void onFinish() {
		super.onFinish();
		Log.d(TAG, getRequestURI() + "  request finish");
	}

	@Override
	public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
			if( error != null ) {
				Log.d(TAG, getRequestURI() + "  request onFailure @ statusCode" + statusCode + "  " + error.getMessage());
			} else {
				Log.d(TAG, getRequestURI() + "  request onFailure @ statusCode" + statusCode + "  " + responseBody);
			}
			if( error instanceof SocketTimeoutException ) {
				Toast.makeText(mContext, mContext.getString(R.string.network_socket_timeout), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mContext, mContext.getString(R.string.network_invalidate), Toast.LENGTH_SHORT).show();
			}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, String result) {
		Log.d(TAG, getRequestURI() + "  " +  result);
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
				onParseFailure(String.valueOf(response.getMsgCode()), response.getMessage());
			}
			
		} catch (Exception e) {
			onParseException(e);
		}
	}
	
	@Override
	public void onParseSuccess(T result, String originResult) {
		
	}

	@Override
	public void onParseException(Exception e) {
		e.printStackTrace();
	}
	
}
