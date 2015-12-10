package com.tonggou.gsm.andclient.net.parser;

/**
 * 加载本地数据处理器接口
 * @author lwz
 *
 * @param <T>
 */
public interface ILoadCacheHandler<T> {

	/**
	 * 加载本地数据
	 * <p>若本地没有缓存数据，则不会调用此方法</p>
	 * 
	 * @param result
	 */
	public void onLoadCache(T result, String originResult, boolean isNetworkConnected);
	
}
