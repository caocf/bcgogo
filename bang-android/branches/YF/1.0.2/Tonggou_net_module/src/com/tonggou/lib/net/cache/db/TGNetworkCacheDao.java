package com.tonggou.lib.net.cache.db;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

public class TGNetworkCacheDao {
	
	public static final String TAG = "NetworkCacheUtil";

	/**
	 *	取出存储数据 
	 * @param cacheKey
	 * @param userNo
	 * @return
	 */
	public static String restoreCacheData(Context context, String cacheKey, String userNo ) {
		TGNetworkCacheDBHelper helper = getDBHelper(context);
		try {
			Dao<TGNetworkCacheTable, Integer> dao = getDao(helper);
			// 构造查询条件
			TGNetworkCacheTable mapping = new TGNetworkCacheTable();
			mapping.setKey(cacheKey);
			mapping.setUserNo(userNo);
			// 先根据 userNo 和 cacheKey 查找有无记录，若有则更新，否则新建
			List<TGNetworkCacheTable> queryResult = dao.queryForMatchingArgs(mapping);
			return queryResult.isEmpty() ? "" : queryResult.get(0).getData();
		} catch (SQLException e) {
			Log.e(TAG, "query cache data SQLException " + e.getMessage());
		} finally {
			releaseDBHelper(helper);
		}
		return "";
	}
	
	/**
	 * 存储缓存数据
	 * @param context	上下文，用来开启数据库
	 * @param userNo	用户名
	 * @param cacheKey	缓存键
	 * @param cacheData 缓存数据
	 */
	public static void storeCacheData(Context context, String userNo, String cacheKey, String cacheData) {
		TGNetworkCacheDBHelper helper = getDBHelper(context);
		try {
			Dao<TGNetworkCacheTable, Integer> dao = getDao(helper);
			// 构造查询条件
			TGNetworkCacheTable mapping = new TGNetworkCacheTable();
			mapping.setKey(cacheKey);
			mapping.setUserNo(userNo);
			// 先根据 userNo 和 cacheKey 查找有无记录，若有则更新，否则新建
			List<TGNetworkCacheTable> queryResult = dao.queryForMatchingArgs(mapping);
			TGNetworkCacheTable newCache = new TGNetworkCacheTable();
			newCache.setId( queryResult.isEmpty() ? 0 : queryResult.get(0).getId() );
			newCache.setKey(cacheKey);
			newCache.setUserNo(userNo);
			newCache.setData(cacheData);
			newCache.setTimestamp(System.currentTimeMillis());
			dao.createOrUpdate(newCache);
		} catch (SQLException e) {
			Log.e(TAG, "store cache data SQLException " + e.getMessage());
		} finally {
			releaseDBHelper(helper);
		}
	}
	
	public static Dao<TGNetworkCacheTable, Integer> getDao(TGNetworkCacheDBHelper helper) throws SQLException {
		return helper.getTableDao();
	}
	
	public static TGNetworkCacheDBHelper getDBHelper(Context context) {
		return new TGNetworkCacheDBHelper(context);
	}
	
	public static void releaseDBHelper(TGNetworkCacheDBHelper helper) {
		if( helper != null && helper.isOpen()) { 
			helper.close();
		}
		helper = null;
	}
}
