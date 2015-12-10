package com.tonggou.andclient.db;

import java.sql.SQLException;

import android.text.TextUtils;
import android.util.Log;

import com.tonggou.andclient.app.TongGouApplication;

public class LocalCacheDao {
	
	private static final String TAG = "LocalCacheDao";
	
	public static LocalCache getCache( String userNo, String cacheKey) {
		LocalCacheDBHelper helper = getDBHelper();
		try {
			LocalCache cache = helper.getDao().queryBuilder().
					where().eq(LocalCache.COLUMN_USER_NO, getUserNo(userNo))
					.and().eq(LocalCache.COLUMN_CACHE_KEY, cacheKey)
					.queryForFirst();
			return cache;
		} catch (SQLException e) {
			return null;
		} finally {
			helper.close();
		}
	}
	
	public static boolean isEmpty( LocalCache cache ) {
		return cache == null || TextUtils.isEmpty( cache.content );
	}
	
	public static void store( String userNo, String cacheKey, String content) {
		LocalCacheDBHelper helper = getDBHelper();
		try {
			LocalCache cache = getCache(userNo, cacheKey);
			if( cache == null) {
				// 说明不存在该记录
				cache = new LocalCache(cacheKey, content, userNo);
				helper.getDao().create(cache);
			} else {
				cache.content = content;
				helper.getDao().update(cache);
			}
		} catch (SQLException e) {
			Log.i(TAG, e.getCause().getMessage());
		} finally {
			helper.close();
		}
	}
	
	private static String getUserNo(String userNo) {
		return TextUtils.isEmpty(userNo) ? VehicleDBUtil.FLAG_GUEST_USER_NO : userNo; 
	}
	
	public static LocalCacheDBHelper getDBHelper() {
		return LocalCacheDBHelper.getDBHealper(TongGouApplication.getInstance());
	}
	
}
