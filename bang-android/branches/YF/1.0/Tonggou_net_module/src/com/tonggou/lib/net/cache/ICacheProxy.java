package com.tonggou.lib.net.cache;

import android.content.Context;

public interface ICacheProxy {
	
	/**
	 * 取得缓存数据
	 * @param context
	 * @param userUUID	用户唯一标识
	 * @param cacheKey	缓存的 key
	 * @return
	 */
	public String restoreCacheData(Context context, String userUUID, String cacheKey );
	
	/**
	 * 存储缓存的数据
	 * @param context
	 * @param userUUID 用户唯一身份标识
	 * @param cacheKey 缓存的 key
	 * @param originResult 缓存数据
	 */
	public void storeCacheData(Context context, String userUUID, String cacheKey, String originData);
	
}
