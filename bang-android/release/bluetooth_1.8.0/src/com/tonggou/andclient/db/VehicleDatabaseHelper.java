package com.tonggou.andclient.db;

import com.tonggou.andclient.db.VehicleDBUtil.DefaultType;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class VehicleDatabaseHelper extends SQLiteOpenHelper {

	private static VehicleDatabaseHelper mInstance = null;
	// 数据库名称
	public static final String DATABASE_NAME = "vehicle.db";
	// 数据库版本号
	private static final int DATABASE_VERSION = 3;
	// 数据库SQL语句 添加一个表---->本地 车辆表
	private static final String NATIVE_VEHICLE_TABLE = 
			"CREATE TABLE "  + VehicleDBUtil.TABLE_NAME_VEHICLE 
			+ "("
			+ VehicleDBUtil.VEHICLE_ID + " VERCHAR2(50)," 
			+ VehicleDBUtil.USER_NO + " VERCHAR2(50),"
			+ VehicleDBUtil.IS_DEFAULT + " VARCHAR2(5) NOT NULL DEFAULT '"+ DefaultType.NORMAL.getValue() +"'," 
			+ VehicleDBUtil.VEHICLE_VIN + " VARCHAR2(50)," 
			+ VehicleDBUtil.VEHICLE_NO + " VARCHAR2(20)," 
			+ VehicleDBUtil.VEHICLE_JSON_DATA + " TEXT NOT NULL"
			+ ");";

	public VehicleDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	/** 单例模式 **/
	public static synchronized VehicleDatabaseHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new VehicleDatabaseHelper(context);
		}
		return mInstance;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// 向数据中添加表
		db.execSQL(NATIVE_VEHICLE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/** 可以拿到当前数据库的版本信息 与之前数据库的版本信息 用来更新数据库 **/
		if (newVersion > oldVersion) {
			String sqlVehicle = "DROP TABLE IF EXISTS " + VehicleDBUtil.TABLE_NAME_VEHICLE;
			db.execSQL(sqlVehicle);
			onCreate(db);
		}
	}
	
	/**
	 * 删除数据库
	 * 
	 * @param context
	 * @return
	 */
	public static boolean deleteDataBase(Context context) {
		return context.deleteDatabase(DATABASE_NAME);
	}

}
