package com.tonggou.lib.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;
import com.tonggou.lib.net.cache.ICacheProxy;
import com.tonggou.lib.net.parser.AsyncJsonResponseParseHandler;
import com.tonggou.lib.net.parser.AsyncLoadCacheJsonResponseParseHandler;
import com.tonggou.lib.util.NetworkUtil;

/**
 * 所有的请求都应该继承此方法
 * @author lwz
 *
 */
public abstract class AbsTonggouHttpRequest implements ITonggouHttpRequest {
	
	public final String TAG = getClass().getSimpleName();
	
	private boolean isGuestMode = false;
	private HttpRequestParams requestParams = null;
	private HttpRequestParams guestRequestParams = null;
	private ICacheProxy mCacheProxy;
	
	/**
	 * 默认为 登录会员模式，若要设置游客模式，请使用 {@link #AbsTonggouHttpRequest(boolean)}
	 */
	public AbsTonggouHttpRequest() {
	}
	
	/**
	 * @param isGuestMode 
	 * 				是否为 游客模式  true 游客模式，false 登录会员模式
	 */
	public AbsTonggouHttpRequest(boolean isGuestMode) {
		this.isGuestMode  = isGuestMode;
	}
	
	/**
	 * 
	 * @param cacheProxy 网络响应结果缓存代理
	 */
	public AbsTonggouHttpRequest(ICacheProxy cacheProxy) {
		mCacheProxy = cacheProxy;
	}
	
	public void setGuestMode(boolean isGuestMode) {
		this.isGuestMode = isGuestMode;
	}
	
	/**
	 * cacheProxy 网络响应结果缓存代理
	 * @param cacheProxy
	 */
	public void setCacheProxy(ICacheProxy cacheProxy) {
		this.mCacheProxy = cacheProxy;
	}
	
	@Override
	public abstract String getAPI();
	
	@Override
	public abstract HttpMethod getHttpMethod();
	
	public void setRequestParams(HttpRequestParams params) {
		requestParams = params;
	}
	
	public void setGuestRequestParams(HttpRequestParams params) {
		guestRequestParams = params;
	}
	
	@Override
	public HttpRequestParams getRequestParams() {
		return requestParams;
	}
	
	@Override
	public HttpRequestParams getGuestRequestParams() {
		return guestRequestParams;
	}
	
	@Override
	public boolean isGuestMode() {
		return isGuestMode;
	}
	
	/**
	 * 开始请求网络
	 * @param context			上下文
	 * @param responseHandler	响应结果解析器
	 */
	@SuppressWarnings("rawtypes")
	public void doRequest(Context context, final AsyncHttpResponseHandler responseHandler) {
		if( responseHandler instanceof AsyncJsonResponseParseHandler ) {
			((AsyncJsonResponseParseHandler)responseHandler).setContext(context);
		}
		String api = getAPI();
		HttpRequestParams params = isGuestMode() ? getGuestRequestParams() : getRequestParams();
		
		if( mCacheProxy != null ) {
			String cacheKey = getDefaultCacheKey(api, getHttpMethod(), params);
			loadCahceCallback(context, cacheKey, responseHandler);
		}
		
		if( !NetworkUtil.isNetworkConnected(context) ) {
			responseHandler.onStart();
			responseHandler.onFinish();
			return;
		}
		HttpRequestClient client = new HttpRequestClient(context);
		switch (getHttpMethod()) {
			case GET: client.get(api, params, responseHandler); break;
			case POST: client.post(api, params, responseHandler); break;
			case DELETE: client.delete(api, responseHandler); break;
			case PUT: client.put(api, params, responseHandler); break;
			default: break;
		}
		Log.d(TAG, "url @ " + api + "   param @ " + params);
	}
	
	/**
	 * 加载缓存数据回调
	 * 
	 * @param url
	 * @param params
	 * @param responseHandler
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadCahceCallback(final Context context, String cacheKey, final ResponseHandlerInterface responseHandler) {
		if( responseHandler instanceof AsyncLoadCacheJsonResponseParseHandler ) {
			AsyncLoadCacheJsonResponseParseHandler handler = (AsyncLoadCacheJsonResponseParseHandler) responseHandler;
			handler.setContext(context);
			handler.setCacheProxy(mCacheProxy);
			String customCacheKey = handler.getCustomCacheKey();
			// 若有自定义了存储缓存数据的键值，那么就使用它
			if( !TextUtils.isEmpty(customCacheKey) ) {
				cacheKey = customCacheKey;
			}
			// 这里设置，是为了在请求成功后使用正确的 cacheKey 来存储缓存数据
			handler.setCacheKey(cacheKey);
			String cacheData = mCacheProxy.restoreCacheData(context, cacheKey, handler.getUserUUID() );
			handler.onLoadCache(
					handler.getJsonResponseParser().parse( cacheData ),
					cacheData,
					NetworkUtil.isNetworkConnected(context));
		}
	}
	
	/**
	 * 默认缓存键的格式为  httpUrl@httpMethod@paramsStr
	 * @param url
	 * @param requestMethod
	 * @param params
	 * @return
	 */
	public static String getDefaultCacheKey(String url, HttpMethod requestMethod, HttpRequestParams params) {
        StringBuffer sb = new StringBuffer();
		if( ! TextUtils.isEmpty(url) ) {
			sb.append( url.replaceAll("[.:/,%?&=]", "+").replaceAll("[+]+", "+") );
			sb.append("@");
        }
		sb.append( String.valueOf( requestMethod ));
		if( params != null ) {
			sb.append("@").append( params.toString() );
		}
		return sb.toString();
    }
}
