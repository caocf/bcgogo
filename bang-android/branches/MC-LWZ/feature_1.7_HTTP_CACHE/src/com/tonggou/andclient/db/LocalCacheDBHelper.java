package com.tonggou.andclient.db;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class LocalCacheDBHelper extends OrmLiteSqliteOpenHelper {

	private static final String TAG = "LocalCacheDBHealper";
	
	private static final String DB_NAME = "local_cache.db";
	private static final int DB_VERSION = 2;
	
	private Dao<LocalCache, Integer> mDao;
	
	private LocalCacheDBHelper(Context context, String databaseName, CursorFactory factory,
			int databaseVersion) {
		super(context, databaseName, factory, databaseVersion);
	}
	
	public static synchronized LocalCacheDBHelper getDBHealper(Context context) {
		return new LocalCacheDBHelper(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		try {
			TableUtils.createTable(arg1, LocalCache.class);
		} catch (SQLException e) {
			Log.i(TAG, e.getCause().getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		if( oldVersion < newVersion  ) {
			try {
				TableUtils.dropTable(connectionSource, LocalCache.class, true);
				onCreate(db, connectionSource);
			} catch (SQLException e) {
				Log.i(TAG, e.getCause().getMessage());
			}
		}
	}
	
	public Dao<LocalCache, Integer> getDao() throws SQLException {
		if( mDao == null ) {
			mDao = getDao(LocalCache.class);
		}
		return mDao;
	}
	
	@Override
	public void close() {
		super.close();
		mDao = null;
	}

}
