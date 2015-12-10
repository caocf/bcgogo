package com.tonggou.gsm.andclient.db;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * 一个数据库一个表创建代理
 * <p>子类实现抽象方法 getTableClass(), 将该数据库中要创建的表通过该方法给父类创建
 * 
 * @author lwz
 *
 */
public abstract class AbsSingleTableDatebaseHelper extends OrmLiteSqliteOpenHelper {

	public final String TAG = "AbsDatebaseHelper";
	@SuppressWarnings("rawtypes")
	private Dao mDao;
	
	public AbsSingleTableDatebaseHelper(Context context, String databaseName, int databaseVersion) {
		super(context, databaseName, null, databaseVersion);
	}
	
	/**
	 * 将该数据库中要创建的表通过该方法给父类创建
	 * @return 要创建的表类
	 */
	abstract Class<?> getTableClass();
	
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		Log.i(TAG, "onCreate database  ");
		try {
			TableUtils.createTable(connectionSource, getTableClass());
			Log.i(TAG, "onCreate table " + getTableClass().getSimpleName());
		} catch (SQLException e) {
			Log.e(TAG, "Can't create table " + getTableClass().getSimpleName(), e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		Log.i(TAG, "onUpgrade database");
		try {
			TableUtils.dropTable(connectionSource, getTableClass(), true);
			Log.i(TAG, "onUpgrade table " + getTableClass().getSimpleName());
		} catch (SQLException e) {
			Log.e(TAG, "Can't create table " + getTableClass().getSimpleName(), e);
			throw new RuntimeException(e);
		}
		onCreate(db, connectionSource);
	}
	
	@SuppressWarnings("unchecked")
	public <T, ID> Dao<T, ID> getTableDao() {
		if( mDao == null ) {
			try {
				mDao = getDao(getTableClass());
			} catch (SQLException e) {
				Log.e(TAG, "Can't getTableDao " + getTableClass().getSimpleName(), e);
			}
		}
		return mDao;
	}
	
	@Override
	public void close() {
		super.close();
		mDao = null;
	}
}
