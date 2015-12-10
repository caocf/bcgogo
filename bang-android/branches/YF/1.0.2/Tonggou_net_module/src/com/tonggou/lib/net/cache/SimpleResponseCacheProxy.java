package com.tonggou.lib.net.cache;

import com.tonggou.lib.net.cache.db.TGNetworkCacheDao;

import android.content.Context;

/**
 * 网络响应缓存代理的简单实现。
 * <p>主要是利用数据库来存储
 * @author lwz
 *
 */
public class SimpleResponseCacheProxy implements ICacheProxy {

	@Override
	public String restoreCacheData(Context context, String userUUID,
			String cacheKey) {
		return TGNetworkCacheDao.restoreCacheData(context, cacheKey, userUUID);
	}

	@Override
	public void storeCacheData(Context context, String userUUID,
			String cacheKey, String originData) {
		TGNetworkCacheDao.storeCacheData(context, userUUID, cacheKey, originData);
	}

}
