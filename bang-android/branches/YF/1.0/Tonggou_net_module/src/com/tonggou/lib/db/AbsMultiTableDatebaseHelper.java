package com.tonggou.lib.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * 一个数据库多个表创建代理
 * <p>子类实现抽象方法 getTableClassSet(), 将该数据库中要创建的表的集合通过该方法给父类创建
 * 
 * @author lwz
 *
 */
@SuppressWarnings("rawtypes")
public abstract class AbsMultiTableDatebaseHelper extends OrmLiteSqliteOpenHelper {

	public final String TAG = "AbsDatebaseHelper";
	private HashMap<String, Dao> mDaoCacheMap;			// Dao 类缓存，缓存所有使用过的指定表的 Dao 对象
	
	
	public AbsMultiTableDatebaseHelper(Context context, String databaseName, int databaseVersion) {
		super(context, databaseName, null, databaseVersion);
		mDaoCacheMap = new HashMap<String, Dao>();
	}
	
	/**
	 * 将该数据库中要创建的表的集合通过该方法给父类创建
	 * @return 要创建的表类集合，这些表在一个数据库中
	 */
	protected abstract Set<Class<?>> getTableClassSet();
	
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		Log.i(TAG, "onCreate database  ");
		Set<Class<?>> classSet = getTableClassSet();
		for( Class<?> clazz : classSet ) {
			try {
				TableUtils.createTable(connectionSource, clazz);
				Log.i(TAG, "onCreate table " + clazz.getSimpleName());
			} catch (SQLException e) {
				Log.e(TAG, "Can't create table " + clazz.getSimpleName(), e);
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		Log.i(TAG, "onUpgrade database");
		Set<Class<?>> classSet = getTableClassSet();
		for( Class<?> clazz : classSet ) {
			try {
				TableUtils.dropTable(connectionSource, clazz, true);
				Log.i(TAG, "onUpgrade table " + clazz.getSimpleName());
			} catch (SQLException e) {
				Log.e(TAG, "Can't create table " + clazz.getSimpleName(), e);
				throw new RuntimeException(e);
			}
		}
		onCreate(db, connectionSource);
	}
	
	/**
	 * 得到指定表的 Dao 类, 由于该数据库有多张表，故需要指定表的类名
	 * @param tableClass	
	 * @return Dao
	 */
	public <T, ID> Dao<T, ID> getTableDao(Class<?> tableClass) {
		if( !getTableClassSet().contains(tableClass) ) {
			new IllegalArgumentException("该表类没有在表类集合中，也就是该表没有被创建");
		}
		try {
			final String daoCacheKey = tableClass.getName();
			@SuppressWarnings("unchecked")
			Dao<T, ID> dao = mDaoCacheMap.get(daoCacheKey);
			if( dao == null ) {
				dao = getDao(tableClass);
				mDaoCacheMap.put(daoCacheKey, dao);
			}
			return dao;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void close() {
		super.close();
		mDaoCacheMap.clear();
		mDaoCacheMap= null;
	}
}
