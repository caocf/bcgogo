package com.tonggou.gsm.andclient.net.parser;

import android.content.Context;

import com.tonggou.gsm.andclient.UserBaseInfo;
import com.tonggou.gsm.andclient.db.dao.NetworkCacheDao;
import com.tonggou.gsm.andclient.net.response.BaseResponse;

/**
 * 加载本地缓存的异步网络请求解析处理器
 * 
 * <p>当无网络时，会调用 {@link #onLoadCache(T)}, 其他的网络请求生命周期方法只会调用  {@link #onFinish()}
 * <p>子类只需要覆写 {@link #isCache() }方法自定义是否缓存。覆写{@link #getCacheKey()} 方法自定义缓存的键。
 * 该数据与用户无关时请覆写{@link #getUserNo()}方法，返回 null 即可。
 * 
 * <p>NOTE: {@link #onLoadCache(T)}方法会在 {@link com.loopj.android.http.AsyncHttpResponseHandle #onStart()} 方法之前被调用
 * @author lwz
 *
 * @param <T>
 */
public abstract class AsyncLoadCacheJsonResponseParseHandler<T extends BaseResponse> 
							extends AsyncJsonResponseParseHandler<T> implements ILoadCacheHandler<T> {

	private String cacheKey;
	private Context context;
	
	@Override
	public void onParseSuccess(T result, String originResult) {
		if( isCache() ) {
			NetworkCacheDao.storeCache(context, getUserNo(), cacheKey, originResult);
		}
		super.onParseSuccess(result, originResult);
	}
	
	/**
	 * 在 onLoadCache() 前会<b>自动调用<b>该方法
	 * @param context
	 */
	public void setContext(Context context) {
		this.context = context;
	}
	
	/**
	 * 在 onLoadCache() 前会<b>自动调用<b>该方法，设置正确的缓存存储键值
	 * @param cacheKey
	 */
	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}
	
	/**
	 * 当该数据与用户无关时请覆写该方法，返回 null 即可
	 * @return
	 */
	public String getUserNo() {
		return UserBaseInfo.getUserInfo().getUserNo();
	}
	
	/**
	 * 当不需要自定义缓存存储键值时，子类不需要覆写此方法
	 * @return
	 */
	public String getCacheKey() {
		return null;
	}
	
	/**
	 * 是否缓存
	 * <p><b> 若 返回值为 false, 那么不会回调  onLoadCache() 方法 
	 * @return true 缓存， false 不缓存
	 */
	public boolean isCache() {
		return true;
	}
	
}
