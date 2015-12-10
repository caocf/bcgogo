package com.tonggou.andclient.db;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="local_cache")
public class LocalCache {
	
	public static final String COLUMN_CACHE_KEY = "CACHE_KEY";
	public static final String COLUMN_CONTENT = "CONTENT";
	public static final String COLUMN_USER_NO = "USER_NO";

	@DatabaseField(generatedId=true)
	public int id;
	
	/**
	 * 缓存键值
	 * <p> TEXT
	 */
	@DatabaseField(dataType=DataType.LONG_STRING, columnName=COLUMN_CACHE_KEY)
	public String cacheKey;
	
	/**
	 * 缓存内容
	 * <p> TEXT
	 */
	@DatabaseField(dataType=DataType.LONG_STRING, columnName=COLUMN_CONTENT)
	public String content;
	
	/**
	 * 用户名
	 * <p> VEHICLE2(50)
	 */
	@DatabaseField(dataType=DataType.STRING, width=50,
			columnName=COLUMN_USER_NO, defaultValue=VehicleDBUtil.FLAG_GUEST_USER_NO)
	public String userNo;
	
	public LocalCache() {
		
	}

	public LocalCache(String cacheKey, String content, String userNo) {
		super();
		this.cacheKey = cacheKey;
		this.content = content;
		this.userNo = userNo;
	}
	
	
	
}
