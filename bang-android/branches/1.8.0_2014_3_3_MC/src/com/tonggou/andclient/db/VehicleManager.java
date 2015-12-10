package com.tonggou.andclient.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.db.VehicleDBUtil.DefaultType;
import com.tonggou.andclient.vo.VehicleInfo;

/**
 * 车辆管理
 * @author lwz
 *
 */
public class VehicleManager {
	
	public boolean add(VehicleInfo vehicle) {
		if( isUserVehicleNoExist(vehicle.getUserNo(), null, vehicle.getVehicleNo()) ) {
			TongGouApplication.showToast("车牌号已存在"); 
			return false;
		}
		if( isUserVehicleVinExist(vehicle.getUserNo(), null, vehicle.getVehicleVin()) ) {
			TongGouApplication.showToast("车架号已存在");
			return false;
		}
		if( getDefaultVehicle(vehicle.getUserNo()) == null ) {
			vehicle.setIsDefault(DefaultType.DEFAULT.getValue());
		}
		SQLiteDatabase db = VehicleDBUtil.getDatabase();
		long row = db.insert(VehicleDBUtil.TABLE_NAME_VEHICLE, null, getCVByVehicle(vehicle));
		boolean isSuccess = row > -1;
		VehicleDBUtil.closeDatabase(db);
		return isSuccess;
	}
	
	public void updateAllVehicle(String userNo, List<VehicleInfo> newData) {
		deleteAllVehicle(userNo);
		if( newData == null || newData.isEmpty() ) {
			return;
		}
		SQLiteDatabase db = VehicleDBUtil.getDatabase();
		TongGouApplication.showLog( "insert -- " + newData.size());
		for( VehicleInfo item : newData ) {
			TongGouApplication.showLog( "insert -- "+ userNo + "  " + item.getVehicleId());
			item.setUserNo(getUserNo( userNo ));
			long id = db.insert(VehicleDBUtil.TABLE_NAME_VEHICLE, null, getCVByVehicle(item));
			TongGouApplication.showLog( "insert -- " + id  + "  " + userNo + "  " + item.getVehicleId());
		}
		VehicleDBUtil.closeDatabase(db);
	}
	
	public void deleteAllVehicle(String userNo) {
		SQLiteDatabase db = VehicleDBUtil.getDatabase();
		String whereClause = VehicleDBUtil.USER_NO + "=?";
		String[] whereArgs = new String[]{ getUserNo(userNo) };
		db.delete(VehicleDBUtil.TABLE_NAME_VEHICLE, whereClause, whereArgs);
		VehicleDBUtil.closeDatabase(db);
	}
	
	public boolean deleteByVehicleNo(String userNo, String vehicleNo) {
		SQLiteDatabase db = VehicleDBUtil.getDatabase();
		String whereClause = VehicleDBUtil.USER_NO + "=? AND " + VehicleDBUtil.VEHICLE_NO + "=?";
		String[] whereArgs = new String[]{ getUserNo(userNo), vehicleNo };
		int row = db.delete(VehicleDBUtil.TABLE_NAME_VEHICLE, whereClause, whereArgs);
		boolean isSuccess = row > -1;
		VehicleDBUtil.closeDatabase(db);
		return isSuccess;
	}
	
	public boolean deleteByVehicleId(String userNo, String vehicleId) {
		SQLiteDatabase db = VehicleDBUtil.getDatabase();
		String whereClause = VehicleDBUtil.USER_NO + "=? AND " + VehicleDBUtil.VEHICLE_ID + "=?";
		String[] whereArgs = new String[]{ userNo, vehicleId };
		int row = db.delete(VehicleDBUtil.TABLE_NAME_VEHICLE, whereClause, whereArgs);
		boolean isSuccess = row > -1;
		VehicleDBUtil.closeDatabase(db);
		return isSuccess;
	}
	
	public boolean update(VehicleInfo vehicle) {
		if( isUserVehicleNoExist(vehicle.getUserNo(), vehicle.getVehicleId(), vehicle.getVehicleNo()) ) {
			TongGouApplication.showToast("车牌号已存在");
			return false;
		}
		if( isUserVehicleVinExist(vehicle.getUserNo(), vehicle.getVehicleId(), vehicle.getVehicleVin()) ) {
			TongGouApplication.showToast("车架号已存在");
			return false;
		}
		SQLiteDatabase db = VehicleDBUtil.getDatabase();
		String whereClause = VehicleDBUtil.USER_NO + "=? AND " + VehicleDBUtil.VEHICLE_NO + "=?";
		String[] whereArgs = new String[]{ getUserNo( vehicle.getUserNo() ) , vehicle.getVehicleNo()};
		int row = db.update(VehicleDBUtil.TABLE_NAME_VEHICLE, getCVByVehicle(vehicle), whereClause, whereArgs);
		boolean isSuccess = row > -1;
		VehicleDBUtil.closeDatabase(db);
		return isSuccess;
	}
	
	public List<VehicleInfo> getAllVehicle(String userNo) {
		SQLiteDatabase db = VehicleDBUtil.getDatabase();
		String selection = VehicleDBUtil.USER_NO + "=? ";
		String[] selectionArgs = new String[]{getUserNo(userNo)};
		Cursor cursor = db.query(VehicleDBUtil.TABLE_NAME_VEHICLE, null, selection, selectionArgs, null, null, null);
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
	
	public VehicleInfo getVehicle(String userNo, String vehicleNo) {
		SQLiteDatabase db = VehicleDBUtil.getDatabase();
		String defaultSelection =  VehicleDBUtil.USER_NO + " =? AND " + VehicleDBUtil.VEHICLE_NO + "=?";
		String[] defaultSelectionArgs = new String[]{ getUserNo(userNo), vehicleNo };
		Cursor cursor = db.query(VehicleDBUtil.TABLE_NAME_VEHICLE, null, defaultSelection, defaultSelectionArgs, null, null, null);
		VehicleInfo vehicle = null;
		if( cursor.moveToFirst() ) {
			vehicle = getVehicleByCursor(cursor);
		}
		VehicleDBUtil.close(db, cursor);
		return vehicle;
	}
	
	private String getUserNo(String userNo) {
		return TextUtils.isEmpty(userNo) ? VehicleDBUtil.FLAG_GUEST_USER_NO : userNo; 
	}
	
	/**
	 * 获取默认的车辆
	 * @return 有则返回 默认车辆, 没有则返回 null
	 */
	public VehicleInfo getDefaultVehicle(String userNo) {
		SQLiteDatabase db = VehicleDBUtil.getDatabase();
		String defaultSelection = VehicleDBUtil.USER_NO + " =? AND " + VehicleDBUtil.IS_DEFAULT + "=?";
		String[] defaultSelectionArgs = new String[]{getUserNo(userNo), DefaultType.DEFAULT.getValue() };
		Cursor cursor = db.query(VehicleDBUtil.TABLE_NAME_VEHICLE, null, defaultSelection, defaultSelectionArgs, null, null, null);
		VehicleInfo vehicle = null;
		if( cursor.moveToFirst() ) {
			vehicle = getVehicleByCursor(cursor);
		}
		VehicleDBUtil.close(db, cursor);
		return vehicle;
	}
	
	/**
	 * 数据库是否为空
	 * @param userNo, 当为 null 时为游客车辆
	 * @return true 数据库为空， false 数据库不为空
	 */
	public boolean isUserVehicleEmpty(String userNo) {
		SQLiteDatabase db = VehicleDBUtil.getDatabase();
		String selection = VehicleDBUtil.USER_NO + " =?";
		String[] selectionArgs = new String[]{getUserNo(userNo)};
		Cursor cursor = db.query(VehicleDBUtil.TABLE_NAME_VEHICLE, null, selection, selectionArgs, null, null, null);
		int rowCount = cursor.getCount();
		TongGouApplication.showLog(rowCount);
		VehicleDBUtil.close(db, cursor);
		return rowCount <= 0;
	}
	
	public void updateVehicle2Default(VehicleInfo vehicle ) {
		SQLiteDatabase db = VehicleDBUtil.getDatabase();
		String defaultSelection = VehicleDBUtil.USER_NO + "=? AND " + VehicleDBUtil.IS_DEFAULT + "=?";
		String[] defaultSelectionArgs = new String[]{ getUserNo(vehicle.getUserNo()), DefaultType.DEFAULT.getValue() };
		Cursor cursor = db.query(VehicleDBUtil.TABLE_NAME_VEHICLE, 
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
		if( VehicleDBUtil.FLAG_GUEST_USER_NO.equals(vehicle.getUserNo()) && TextUtils.isEmpty( vehicle.getVehicleId() ) ) {
			String localVehicleId = String.valueOf(System.currentTimeMillis());
			vehicle.setVehicleId(localVehicleId);
			cv.put(VehicleDBUtil.VEHICLE_ID, localVehicleId);
		}
		cv.put(VehicleDBUtil.USER_NO,  getUserNo( vehicle.getUserNo()));
		cv.put(VehicleDBUtil.IS_DEFAULT, isDefault);
		cv.put(VehicleDBUtil.VEHICLE_VIN, vehicle.getVehicleVin());
		cv.put(VehicleDBUtil.VEHICLE_NO, vehicle.getVehicleNo());
		cv.put(VehicleDBUtil.VEHICLE_JSON_DATA, new Gson().toJson(vehicle));
		return cv;
	}
	
	private VehicleInfo getVehicleByCursor(Cursor cursor) {
		String jsonData = cursor.getString( cursor.getColumnIndex( VehicleDBUtil.VEHICLE_JSON_DATA ) );
		VehicleInfo vehicle = new Gson().fromJson(jsonData, VehicleInfo.class);
		return vehicle;
	}
	
	/**
	 * 当前用户的车辆 车架号（VIN） 是否已经存在
	 * @param userNo
	 * @param vin
	 * @return true 已存在车架号， false 不存在车架号
	 */
	public boolean isUserVehicleVinExist(String userNo, String vehicleId, String vin) {
		if( TextUtils.isEmpty(vin) ) {
			return false;
		}
		if( TextUtils.isEmpty( vehicleId ) ) {
			
		}
		String selection = VehicleDBUtil.USER_NO + " =? AND " + VehicleDBUtil.VEHICLE_ID + " <>? AND " + VehicleDBUtil.VEHICLE_VIN + "=?";
		String[] selectionArgs = new String[]{ getUserNo(userNo), vehicleId + "", vin};
		SQLiteDatabase db =  VehicleDBUtil.getDatabase();
 		Cursor cursor = db.query(VehicleDBUtil.TABLE_NAME_VEHICLE, null, selection, selectionArgs, null, null, null);
		boolean isExist =  cursor.moveToFirst();
		VehicleDBUtil.close(db, cursor);
 		return isExist;
	}
	
	/**
	 * 车辆 车牌号 是否已经存在
	 * @param userNo
	 * @param vehicleNo
	 * @return true 已存在车牌号， false 不存在车牌号
	 */
	public boolean isUserVehicleNoExist(String userNo, String vehicleId, String vehicleNo) {
		String selection = VehicleDBUtil.USER_NO + " =? AND " + VehicleDBUtil.VEHICLE_ID + "<>? AND " + VehicleDBUtil.VEHICLE_NO + "=?";
		String[] selectionArgs = new String[]{ getUserNo(userNo), vehicleId + "", vehicleNo};
		SQLiteDatabase db =  VehicleDBUtil.getDatabase();
 		Cursor cursor = db.query(VehicleDBUtil.TABLE_NAME_VEHICLE, null, selection, selectionArgs, null, null, null);
		boolean isExist =  cursor.moveToFirst();
		VehicleDBUtil.close(db, cursor);
 		return isExist;
	}
	
}
