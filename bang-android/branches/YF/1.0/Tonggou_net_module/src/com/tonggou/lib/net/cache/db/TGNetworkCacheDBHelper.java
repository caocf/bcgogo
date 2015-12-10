package com.tonggou.lib.net.cache.db;

import android.content.Context;

import com.tonggou.lib.db.AbsSingleTableDatebaseHelper;

/**
 * 网络缓存数据数据库助理类
 * @author lwz
 *
 */
public class TGNetworkCacheDBHelper extends AbsSingleTableDatebaseHelper {

	public TGNetworkCacheDBHelper(Context context) {
		super(context, TGNetworkCacheTable.DB_NAME, TGNetworkCacheTable.DB_VERSION);
	}

	@Override
	public Class<?> getTableClass() {
		return TGNetworkCacheTable.class;
	}

}
