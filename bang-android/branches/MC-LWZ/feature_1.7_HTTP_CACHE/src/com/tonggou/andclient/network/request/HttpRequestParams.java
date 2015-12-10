package com.tonggou.andclient.network.request;
import java.util.HashMap;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.RequestParams;

/**
 * HTTP 请求的参数
 * 
 * <p>继承了  HashMap&lt;String, Object&gt;</p>
 * <p>复写了 toString() 方法， 返回JSON</p>
 * @author lwz
 *
 */
public class HttpRequestParams extends HashMap<String, Object> {
	
	private Object realValue;
	
	public HttpRequestParams() {
	}
	
	/**
	 * 当使用该构造方法时，调用toString() 方法时会直接将该参数直接返回
	 */
	public HttpRequestParams(JsonObject realValue) {
		this.realValue = realValue;
	}

	private static final long serialVersionUID = -3736319677853216129L;
	
	public static RequestParams convert2RequestParams(HttpRequestParams params) {
		if( params == null ) {
			return null;
		}
		RequestParams requestParams = new RequestParams();
		Set<Entry<String, Object>> entries = params.entrySet();
		for( Entry<String, Object> entry : entries) {
			requestParams.put(entry.getKey(), entry.getValue());
		}
		return requestParams;
	}

	@Override
	public Object put(String key, Object value) {
		if( value instanceof Integer ) {
			return super.put(key, String.valueOf( value ));
		}
		return super.put(key, value);
	}
	
	@Override
	public String toString() {
		if( realValue != null ) {
			return String.valueOf( realValue );
		} else {
			return toJsonObject().toString();
		}
	}
	
	public JsonObject toJsonObject() {
		return new Gson().toJsonTree(this).getAsJsonObject();
	}
}
