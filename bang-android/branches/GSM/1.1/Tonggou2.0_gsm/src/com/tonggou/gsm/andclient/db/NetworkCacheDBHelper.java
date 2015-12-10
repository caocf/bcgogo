package com.tonggou.gsm.andclient.db;

import com.tonggou.gsm.andclient.db.table.TGNetworkCacheTable;

import android.content.Context;

/**
 * 网络缓存数据数据库助理类
 * @author lwz
 *
 */
public class NetworkCacheDBHelper extends AbsSingleTableDatebaseHelper {

	public NetworkCacheDBHelper(Context context) {
		super(context, TGNetworkCacheTable.DB_NAME, TGNetworkCacheTable.DB_VERSION);
	}

	@Override
	Class<?> getTableClass() {
		return TGNetworkCacheTable.class;
	}

}
