package com.tonggou.andclient.guest;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tonggou.andclient.app.TongGouApplication;

public class VehicleDBUtil {
	
	public static final String TABLE_NAME_GUEST_VEHICLE = "guest_vehicle";
	public static final String VEHICLE_ID = "_id";
	public static final String IS_DEFAULT = "is_default";
	public static final String VEHICLE_VIN = "vehicle_vin";
	public static final String VEHICLE_NO = "vehicle_no";
	public static final String VEHICLE_JSON_DATA = "vehicle_json_data";
	
	public static enum DefaultType {
		DEFAULT("YES"),	
		NORMAL("NO");
		
		private String value;
		
		private DefaultType( String value ) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}
	
	/**
	 * 获取数据库操作类的对象
	 * 
	 * @param context
	 * @return
	 */
	private static VehicleDatabaseHelper getBaseHelper() {
		return VehicleDatabaseHelper.getInstance(TongGouApplication.getInstance().getBaseContext());
	}
	
	public static SQLiteDatabase getDatabase() {
		return getBaseHelper().getWritableDatabase();
	}
	
	public static void closeDatabase(SQLiteDatabase db) {
		if( db != null && db.isOpen() ) {
			db.close();
		}
		db = null;
	}
	
	public static void closrCurosr(Cursor c) {
		if( c != null && !c.isClosed() ) {
			c.close();
		}
		c = null;
	}
	
	public static void close( SQLiteDatabase db, Cursor c ) {
		closrCurosr(c);
		closeDatabase(db);
	}

}
