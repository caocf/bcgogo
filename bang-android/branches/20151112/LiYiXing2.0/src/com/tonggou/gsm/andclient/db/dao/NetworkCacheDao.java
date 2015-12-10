package com.tonggou.gsm.andclient.db.dao;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.tonggou.gsm.andclient.db.NetworkCacheDBHelper;
import com.tonggou.gsm.andclient.db.table.TGNetworkCacheTable;
import com.tonggou.gsm.andclient.net.HttpMethod;
import com.tonggou.gsm.andclient.net.HttpRequestParams;

public class NetworkCacheDao {

	public static final String TAG = "NetworkCacheUtil";

	/**
	 * 缓存的格式为  httpUrl@httpMethod@paramsStr
	 * @param url
	 * @param requestMethod
	 * @param params
	 * @return
	 */
	public static String getCacheKey(String url, HttpMethod requestMethod, HttpRequestParams params) {
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
	
	/**
	 *	取出存储数据 
	 * @param cacheKey
	 * @param userNo
	 * @return
	 */
	public static String restoreCacheData(Context context, String cacheKey, String userNo ) {
		NetworkCacheDBHelper helper = getDBHelper(context);
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
	public static void storeCache(Context context, String userNo, String cacheKey, String cacheData) {
		NetworkCacheDBHelper helper = getDBHelper(context);
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

	public static Dao<TGNetworkCacheTable, Integer> getDao(NetworkCacheDBHelper helper) throws SQLException {
		return helper.getTableDao();
	}

	public static NetworkCacheDBHelper getDBHelper(Context context) {
		return new NetworkCacheDBHelper(context);
	}

	public static void releaseDBHelper(NetworkCacheDBHelper helper) {
		if( helper != null && helper.isOpen()) {
			helper.close();
		}
		helper = null;
	}
}