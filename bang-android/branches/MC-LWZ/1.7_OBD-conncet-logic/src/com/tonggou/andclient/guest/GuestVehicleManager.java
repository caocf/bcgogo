package com.tonggou.andclient.guest;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.guest.VehicleDBUtil.DefaultType;
import com.tonggou.andclient.vo.VehicleInfo;

/**
 * 游客车辆管理
 * @author lwz
 *
 */
public class GuestVehicleManager {
	
	public boolean add(VehicleInfo vehicle) {
		if( isVehicleNoExist(vehicle.getVehicleId(), vehicle.getVehicleNo()) ) {
			TongGouApplication.showToast("车牌号已存在");
			return false;
		}
		if( isVehicleVinExist(vehicle.getVehicleId(), vehicle.getVehicleVin()) ) {
			TongGouApplication.showToast("车架号已存在");
			return false;
		}
		if( getDefaultVehicle() == null ) {
			vehicle.setIsDefault(DefaultType.DEFAULT.getValue());
		}
		SQLiteDatabase db = VehicleDBUtil.getDatabase();
		long row = db.insert(VehicleDBUtil.TABLE_NAME_GUEST_VEHICLE, null, getCVByVehicle(vehicle));
		boolean isSuccess = row > -1;
		VehicleDBUtil.closeDatabase(db);
		return isSuccess;
	}
	
	public boolean delete(String vehicleId) {
		SQLiteDatabase db = VehicleDBUtil.getDatabase();
		String whereClause = VehicleDBUtil.VEHICLE_ID + "=?";
		String[] whereArgs = new String[]{ vehicleId };
		int row = db.delete(VehicleDBUtil.TABLE_NAME_GUEST_VEHICLE, whereClause, whereArgs);
		boolean isSuccess = row > -1;
		VehicleDBUtil.closeDatabase(db);
		return isSuccess;
	}
	
	public boolean update(VehicleInfo vehicle) {
		if( isVehicleNoExist(vehicle.getVehicleId(), vehicle.getVehicleNo()) ) {
			TongGouApplication.showToast("车牌号已存在");
			return false;
		}
		if( isVehicleVinExist(vehicle.getVehicleId(), vehicle.getVehicleVin()) ) {
			TongGouApplication.showToast("车架号已存在");
			return false;
		}
		SQLiteDatabase db = VehicleDBUtil.getDatabase();
		String whereClause = VehicleDBUtil.VEHICLE_ID + "=?";
		String[] whereArgs = new String[]{ vehicle.getVehicleId() };
		int row = db.update(VehicleDBUtil.TABLE_NAME_GUEST_VEHICLE, getCVByVehicle(vehicle), whereClause, whereArgs);
		boolean isSuccess = row > -1;
		VehicleDBUtil.closeDatabase(db);
		
		TongGouApplication.showLog( "db update isSuccess " + isSuccess);
		return isSuccess;
	}
	
	public List<VehicleInfo> getAllVehicle() {
		SQLiteDatabase db = VehicleDBUtil.getDatabase();
		Cursor cursor = db.query(VehicleDBUtil.TABLE_NAME_GUEST_VEHICLE, null, null, null, null, null, null);
		List<VehicleInfo> vehicles = new ArrayList<VehicleInfo>();
		boolean hasDefault = false;
		while( cursor.moveToNext() ) {
			VehicleInfo vehicle = getVehicleByCursor(cursor);
			if(vehicle != null) {
				if( VehicleDBUtil.DefaultType.DEFAULT.getValue().equals( vehicle.getIsDefault() )) {
					hasDefault = true;
				}
				vehicles.add(vehicle);
			} 
 		}
		VehicleDBUtil.close(db, cursor);
		if( !vehicles.isEmpty() && !hasDefault ) {
			vehicles.get(0).setIsDefault( DefaultType.DEFAULT.getValue() );
			
			// 设置默认信息
			updateVehicle2Default( vehicles.get(0) );
		}
		return vehicles;
	}
	
	public VehicleInfo getVehicle(String vehicleId) {
		SQLiteDatabase db = VehicleDBUtil.getDatabase();
		String defaultSelection = VehicleDBUtil.VEHICLE_ID + "=?";
		String[] defaultSelectionArgs = new String[]{ vehicleId };
		Cursor cursor = db.query(VehicleDBUtil.TABLE_NAME_GUEST_VEHICLE, null, defaultSelection, defaultSelectionArgs, null, null, null);
		VehicleInfo vehicle = null;
		if( cursor.moveToFirst() ) {
			vehicle = getVehicleByCursor(cursor);
		}
		VehicleDBUtil.close(db, cursor);
		return vehicle;
	}
	
	/**
	 * 获取默认的车辆
	 * @return 有则返回 默认车辆, 没有则返回 null
	 */
	public VehicleInfo getDefaultVehicle() {
		SQLiteDatabase db = VehicleDBUtil.getDatabase();
		String defaultSelection = VehicleDBUtil.IS_DEFAULT + "=?";
		String[] defaultSelectionArgs = new String[]{ DefaultType.DEFAULT.getValue() };
		Cursor cursor = db.query(VehicleDBUtil.TABLE_NAME_GUEST_VEHICLE, null, defaultSelection, defaultSelectionArgs, null, null, null);
		VehicleInfo vehicle = null;
		if( cursor.moveToFirst() ) {
			vehicle = getVehicleByCursor(cursor);
		}
		VehicleDBUtil.close(db, cursor);
		return vehicle;
	}
	
	/**
	 * 数据库是否为空
	 * @return true 数据库为空， false 数据库不为空
	 */
	public boolean isEmpty() {
		SQLiteDatabase db = VehicleDBUtil.getDatabase();
		Cursor cursor = db.query(VehicleDBUtil.TABLE_NAME_GUEST_VEHICLE, null, null, null, null, null, null);
		int rowCount = cursor.getCount();
		TongGouApplication.showLog(rowCount);
		VehicleDBUtil.close(db, cursor);
		return rowCount <= 0;
	}
	
	public void updateVehicle2Default(VehicleInfo vehicle ) {
		SQLiteDatabase db = VehicleDBUtil.getDatabase();
		String defaultSelection = VehicleDBUtil.IS_DEFAULT + "=?";
		String[] defaultSelectionArgs = new String[]{ DefaultType.DEFAULT.getValue() };
		Cursor cursor = db.query(VehicleDBUtil.TABLE_NAME_GUEST_VEHICLE, 
				null, defaultSelection, defaultSelectionArgs, null, null, null);
		if( cursor.moveToFirst() ) {
			VehicleInfo defaultVehicle = getVehicleByCursor(cursor);
			VehicleDBUtil.close(db, cursor);
			
			defaultVehicle.setIsDefault( DefaultType.NORMAL.getValue() );
			update(defaultVehicle);
		}
		vehicle.setIsDefault( DefaultType.DEFAULT.getValue() );
		update(vehicle);
	}
	
	private ContentValues getCVByVehicle(VehicleInfo vehicle) {
		ContentValues cv = new ContentValues();
		String isDefault = vehicle.getIsDefault();
		if( TextUtils.isEmpty(isDefault) ) {
			isDefault = DefaultType.NORMAL.getValue();
		}
		cv.put(VehicleDBUtil.IS_DEFAULT, isDefault);
		cv.put(VehicleDBUtil.VEHICLE_VIN, vehicle.getVehicleVin());
		cv.put(VehicleDBUtil.VEHICLE_NO, vehicle.getVehicleNo());
		cv.put(VehicleDBUtil.VEHICLE_JSON_DATA, new Gson().toJson(vehicle));
		TongGouApplication.showLog( cv.get(VehicleDBUtil.VEHICLE_JSON_DATA) );
		return cv;
	}
	
	private VehicleInfo getVehicleByCursor(Cursor cursor) {
		String jsonData = cursor.getString( cursor.getColumnIndex( VehicleDBUtil.VEHICLE_JSON_DATA ) );
		VehicleInfo vehicle = new Gson().fromJson(jsonData, VehicleInfo.class);
		if( vehicle != null ) {
			vehicle.setVehicleId( cursor.getInt( cursor.getColumnIndex( VehicleDBUtil.VEHICLE_ID ) ) + "");
			vehicle.setIsDefault( cursor.getString( cursor.getColumnIndex( VehicleDBUtil.IS_DEFAULT ) ) );
		}
		return vehicle;
	}
	
	/**
	 * 车辆 车架号（VIN） 是否已经存在
	 * @param vehicleId	可为 null 当 id 为 null 时为新增， 不为空时为 更新
	 * @param vin
	 * @return true 已存在车架号， false 不存在车架号
	 */
	public boolean isVehicleVinExist(String vehicleId, String vin) {
		if( TextUtils.isEmpty(vin) ) {
			return false;
		}
		String selection = VehicleDBUtil.VEHICLE_VIN + "=?";
		String[] selectionArgs = new String[]{vin};
		if( !TextUtils.isEmpty(vehicleId) ) {
			selection += " AND " + VehicleDBUtil.VEHICLE_ID + "<>?";
			selectionArgs = new String[]{vin, vehicleId};
		}
		SQLiteDatabase db =  VehicleDBUtil.getDatabase();
 		Cursor cursor = db.query(VehicleDBUtil.TABLE_NAME_GUEST_VEHICLE, null, selection, selectionArgs, null, null, null);
		boolean isExist =  cursor.moveToFirst();
		VehicleDBUtil.close(db, cursor);
 		return isExist;
	}
	
	/**
	 * 车辆 车牌号 是否已经存在
	 * @param vehicleId	可为 null 当 id 为 null 时为新增， 不为空时为 更新
	 * @param vehicleNo
	 * @return true 已存在车牌号， false 不存在车牌号
	 */
	public boolean isVehicleNoExist(String vehicleId, String vehicleNo) {
		String selection = VehicleDBUtil.VEHICLE_NO + "=?";
		String[] selectionArgs = new String[]{vehicleNo};
		if( !TextUtils.isEmpty(vehicleId) ) {
			selection += " AND " + VehicleDBUtil.VEHICLE_ID + "<>?";
			selectionArgs = new String[]{vehicleNo, vehicleId};
		}
		SQLiteDatabase db =  VehicleDBUtil.getDatabase();
 		Cursor cursor = db.query(VehicleDBUtil.TABLE_NAME_GUEST_VEHICLE, null, selection, selectionArgs, null, null, null);
		boolean isExist =  cursor.moveToFirst();
		VehicleDBUtil.close(db, cursor);
 		return isExist;
	}
	
}
