package com.tonggou.gsm.andclient.db.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.bean.DriveLogDTO;
import com.tonggou.gsm.andclient.bean.type.DriveLogStatus;
import com.tonggou.gsm.andclient.db.TonggouDriveLogDBHelper;

public class TonggouDriveLogDao {

	private static final String TAG = "TonggouDriveLogDao";
	
	public static void insertLogs(Context context, ArrayList<DriveLogDTO> logs ) {
		TonggouDriveLogDBHelper helper = getDBHelper(context);
		try {
			Dao<DriveLogDTO, Long> dao = getDao(helper);
			for( DriveLogDTO log : logs ) {
				if( log.getStatus() == DriveLogStatus.ENABLED ) {
					// 若数据库中已经存在该条记录，则删除该条记录
					DeleteBuilder<DriveLogDTO, Long> deleteBuilder = dao.deleteBuilder();
					Where<DriveLogDTO, Long> deleteWhere= deleteBuilder.where().eq(DriveLogDTO.COLUMN_LOG_ID, log.getId());
					deleteBuilder.setWhere(deleteWhere);
					dao.delete(deleteBuilder.prepare());
					
					// 将记录添加到数据库中
					dao.create(log);
				}
			}
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
		} finally {
			releaseDBHelper(helper);
		}
	}
	
	public static ArrayList<DriveLogDTO> queryLogsByTimestamp(Context context, String userNo, long startTime, long stopTime) {
		ArrayList<DriveLogDTO> logs = new ArrayList<DriveLogDTO>();
		TonggouDriveLogDBHelper helper = getDBHelper(context);
		try {
			Dao<DriveLogDTO, Long> dao = getDao(helper);
			logs.addAll( dao.queryBuilder().orderBy(DriveLogDTO.COLUMN_START_TIME, false)
				.where().eq(DriveLogDTO.COLUMN_USER_NO, userNo)
				.and().ge(DriveLogDTO.COLUMN_START_TIME, startTime)
				.and().le(DriveLogDTO.COLUMN_END_TIME, stopTime).query() );
			
			App.debug(TAG, dao.queryBuilder().orderBy(DriveLogDTO.COLUMN_START_TIME, false)
			.where().eq(DriveLogDTO.COLUMN_USER_NO, userNo)
			.and().ge(DriveLogDTO.COLUMN_START_TIME, startTime)
			.and().le(DriveLogDTO.COLUMN_END_TIME, stopTime).prepare().toString());
			App.debug(TAG, logs.size());
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
		} finally {
			releaseDBHelper(helper);
		}
		return logs;
	}
	
	public static DriveLogDTO queryLogById(Context context, String logId) {
		DriveLogDTO log = null;
		TonggouDriveLogDBHelper helper = getDBHelper(context);
		try {
			Dao<DriveLogDTO, Long> dao = getDao(helper);
			List<DriveLogDTO> logs = dao.queryForEq(DriveLogDTO.COLUMN_LOG_ID, logId);
			if( logs != null && !logs.isEmpty() ) {
				log = logs.get(0);
			}
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
		} finally {
			releaseDBHelper(helper);
		}
		return log;
	}
	
	public static void updatePlaceNotes(Context context, String logId, String placeNotes) {
		TonggouDriveLogDBHelper helper = getDBHelper(context);
		try {
			Dao<DriveLogDTO, Long> dao = getDao(helper);
			UpdateBuilder<DriveLogDTO, Long> updateBuilder = dao.updateBuilder();
			Where<DriveLogDTO, Long> updateWhere = dao.updateBuilder().where().eq(DriveLogDTO.COLUMN_LOG_ID, logId);
			updateBuilder.setWhere(updateWhere);
			updateBuilder.updateColumnValue(DriveLogDTO.COLUMN_PLACE_NOTES, placeNotes);
			dao.update(updateBuilder.prepare());
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
		} finally {
			releaseDBHelper(helper);
		}
	}
	
	public static Dao<DriveLogDTO, Long> getDao(TonggouDriveLogDBHelper helper) throws SQLException {
		return helper.getTableDao();
	}
	
	public static TonggouDriveLogDBHelper getDBHelper(Context context) {
		return new TonggouDriveLogDBHelper(context);
	}
	
	public static void releaseDBHelper(TonggouDriveLogDBHelper helper) {
		if( helper != null && helper.isOpen()) { 
			helper.close();
		}
		helper = null;
	}
}
