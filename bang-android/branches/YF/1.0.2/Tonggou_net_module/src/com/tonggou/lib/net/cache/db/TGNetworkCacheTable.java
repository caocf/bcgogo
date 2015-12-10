package com.tonggou.lib.net.cache.db;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 网络缓存表
 * @author lwz
 *
 */
@DatabaseTable(tableName="network_cache")
public class TGNetworkCacheTable {
	
	/** 数据库名 */
	public static final String DB_NAME = "network_cache.db";
	/** 数据库版本号 */
	public static final int DB_VERSION = 2;
	
	/** 缓存键  */
	public static final String COLUMN_CACHE_KEY = "CACHE_KEY";
	/** 缓存数据 */
	public static final String COLUMN_CACHE_DATA = "CACHE_DATA";
	/** 用户名  */
	public static final String COLUMN_USER_NO = "USER_NO";
	/** 时间戳 */
	public static final String COLUMN_TIMESTAMP = "TIMESTAMP";
	
	@DatabaseField(columnName="_ID", generatedId=true)
	private long id;
	
	@DatabaseField(columnName=COLUMN_CACHE_KEY, dataType=DataType.LONG_STRING)
	private String key;
	
	@DatabaseField(columnName=COLUMN_CACHE_DATA, dataType=DataType.LONG_STRING)
	private String data;
	
	@DatabaseField(columnName=COLUMN_USER_NO, dataType=DataType.STRING)
	private String userNo;
	
	@DatabaseField(columnName=COLUMN_TIMESTAMP, dataType=DataType.LONG)
	private long timestamp;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
}
