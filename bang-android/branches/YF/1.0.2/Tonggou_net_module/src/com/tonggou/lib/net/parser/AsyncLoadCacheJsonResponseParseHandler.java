package com.tonggou.lib.net.parser;

import android.content.Context;

import com.tonggou.lib.net.cache.ICacheProxy;
import com.tonggou.lib.net.response.BaseResponse;

/**
 * 加载本地缓存的异步网络请求解析处理器
 * 
 * <p>当无网络时，会调用 {@link #onLoadCache(T)}, 其他的网络请求生命周期方法只会调用  {@link #onFinish()}
 * 该数据与用户无关时实现{@link #getUserUUID()}方法，只需返回 null， 否则应该返回用户 唯一身份标识。
 * 
 * <p>NOTE: {@link #onLoadCache(T)}方法会在 {@link com.loopj.android.http.AsyncHttpResponseHandle #onStart()} 方法之前被调用
 * @author lwz
 *
 * @param <T>
 */
public abstract class AsyncLoadCacheJsonResponseParseHandler<T extends BaseResponse> 
							extends AsyncJsonResponseParseHandler<T> implements ILoadCacheHandler<T> {

	private String mCacheKey;
	private Context mContext;
	private ICacheProxy mCacheProxy;
	
	@Override
	public void onParseSuccess(T result, String originResult) {
		if( mCacheProxy != null ) {
			mCacheProxy.storeCacheData(mContext, getUserUUID(), mCacheKey, originResult);
		}
		super.onParseSuccess(result, originResult);
	}
	
	/**
	 * 在 onLoadCache() 前会<b>自动调用<b>该方法
	 * @param context
	 */
	public void setContext(Context context) {
		this.mContext = context;
	}
	
	/**
	 * 在 onLoadCache() 前会<b>自动调用<b>该方法，设置正确的缓存存储键值
	 * @param cacheKey
	 */
	public void setCacheKey(String cacheKey) {
		this.mCacheKey = cacheKey;
	}
	
	/**
	 * 在 onLoadCache() 前会<b>自动调用<b>该方法，设置正确的缓存代理
	 * @param mCacheKey
	 */
	public void setCacheProxy(ICacheProxy cacheProxy) {
		this.mCacheProxy = cacheProxy;
	}
	
	/**
	 * 当该数据与用户无关时，返回 null 即可,否则返回与用户相关的唯一标识
	 * @return
	 */
	public abstract String getUserUUID();
	
	/**
	 * 当不需要自定义缓存存储键值时，子类不需要覆写此方法
	 * @return
	 */
	public String getCustomCacheKey() {
		return null;
	}
	
}
