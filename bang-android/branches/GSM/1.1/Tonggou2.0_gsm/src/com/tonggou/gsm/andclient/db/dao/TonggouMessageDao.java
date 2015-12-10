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
import com.tonggou.gsm.andclient.Constants;
import com.tonggou.gsm.andclient.bean.TGMessage;
import com.tonggou.gsm.andclient.bean.type.MessageType;
import com.tonggou.gsm.andclient.db.TonggouMessageDBHelper;

public class TonggouMessageDao {
	
	private static final String TAG = "TonggouMessageDao";
	
	public static void deleteMessage(Context context, String msgId) {
		TonggouMessageDBHelper helper = getDBHelper(context);
		try {
			Dao<TGMessage, Long> dao = getDao(helper);
			DeleteBuilder<TGMessage, Long> deleteBuilder = dao.deleteBuilder();
			Where<TGMessage, Long> deleteWhere = deleteBuilder.where().eq(TGMessage.COLUMN_MSG_ID, msgId);
			deleteBuilder.setWhere(deleteWhere);
			dao.delete(deleteBuilder.prepare());
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
		} finally {
			releaseDBHelper(helper);
		}
	}
	
	/**
	 * 插入消息
	 * @param context
	 * @param userNo
	 * @param msgs
	 * @return 故障消息的条数
	 */
	public static int insertMessages(Context context, String userNo, ArrayList<TGMessage> msgs ) {
		TonggouMessageDBHelper helper = getDBHelper(context);
		int dtcMsgCount = 0;
		try {
			Dao<TGMessage, Long> dao = getDao(helper);
			for( TGMessage msg : msgs ) {
				if( msg.getType() == MessageType.VEHICLE_FAULT_2_APP ) {
					++ dtcMsgCount;
				}
				msg.setUserNo(userNo);
				msg.setRead(false);
				msg.setTimestamp(System.currentTimeMillis());
				List<TGMessage> result = dao.queryForEq(TGMessage.COLUMN_MSG_ID, msg.getId());
				if( result != null && !result.isEmpty() ) {
					msg.set_id(result.get(0).get_id());
				}
				dao.createOrUpdate(msg);
			}
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
		} finally {
			releaseDBHelper(helper);
		}
		return dtcMsgCount;
	}
	
	public static ArrayList<TGMessage> queryAllMessage(Context context, String userNo) {
		TonggouMessageDBHelper helper = getDBHelper(context);
		ArrayList<TGMessage> messages = new ArrayList<TGMessage>();
		try {
			Dao<TGMessage, Long> dao = getDao(helper);
			List<TGMessage> result = dao.queryBuilder()
				.orderBy(TGMessage.COLUMN_TIMESTAMP, false)
				.where().eq(TGMessage.COLUMN_USER_NO, userNo)
				.query();
			messages.addAll(result);
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
		} finally {
			releaseDBHelper(helper);
		}
		return messages;
	}
	
	public static ArrayList<TGMessage> queryAllMessageAndUpdateToRead(Context context, String userNo, int pageNo) {
		return queryAllMessageAndUpdateToRead(context, userNo, pageNo, Constants.APP_CONFIG.QUERY_PAGE_SIZE);
	}
	
	public static ArrayList<TGMessage> queryAllMessageAndUpdateToRead(Context context, String userNo, int pageNo, int pageSize) {
		pageNo = pageNo < 1 ? 1 : pageNo;
		ArrayList<TGMessage> messages = new ArrayList<TGMessage>();
		TonggouMessageDBHelper helper = getDBHelper(context);
		try {
			Dao<TGMessage, Long> dao = getDao(helper);
			List<TGMessage> result = dao.queryBuilder()
				.limit((long)pageSize).offset( (long)((pageNo - 1) * pageSize) )	// 分页
				.orderBy(TGMessage.COLUMN_TIMESTAMP, false)
				.where().eq(TGMessage.COLUMN_USER_NO, userNo)
				.query();
			messages.addAll(result);
			
			// 将 未读 的消息全部变为 已读
			UpdateBuilder<TGMessage, Long> updateBuilder = dao.updateBuilder();
			Where<TGMessage, Long> where = updateBuilder.where().eq(TGMessage.COLUMN_USER_NO, userNo).and().eq(TGMessage.COLUMN_IS_READ, false);
			updateBuilder.setWhere(where);
			updateBuilder.updateColumnValue(TGMessage.COLUMN_IS_READ, true);
			dao.update(updateBuilder.prepare());
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
		} finally {
			releaseDBHelper(helper);
		}
		return messages;
	}
	
	public static long getUnreadMessageCount(Context context, String userNo) {
		TonggouMessageDBHelper helper = getDBHelper(context);
		try {
			Dao<TGMessage, Long> dao =	getDao(helper);
			return dao.queryBuilder()
				.where().eq(TGMessage.COLUMN_USER_NO, userNo)
				.and().eq(TGMessage.COLUMN_IS_READ, false)
				.countOf();
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
		} finally {
			releaseDBHelper(helper);
		}
		return 0;
	}
	
	public static void updateUnreadMesageToRead(Context context, String msgId) {
		TonggouMessageDBHelper helper = getDBHelper(context);
		try {
			Dao<TGMessage, Long> dao = getDao(helper);
			UpdateBuilder<TGMessage, Long> updateBuilder = dao.updateBuilder();
			Where<TGMessage, Long> where = updateBuilder.where().eq(TGMessage.COLUMN_MSG_ID, msgId);
			updateBuilder.setWhere(where);
			updateBuilder.updateColumnValue(TGMessage.COLUMN_IS_READ, true);
			dao.update(updateBuilder.prepare());
			
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
		} finally {
			releaseDBHelper(helper);
		}
	}
	
	public static Dao<TGMessage, Long> getDao(TonggouMessageDBHelper helper) throws SQLException {
		return helper.getTableDao();
	}
	
	public static TonggouMessageDBHelper getDBHelper(Context context) {
		return new TonggouMessageDBHelper(context);
	}
	
	public static void releaseDBHelper(TonggouMessageDBHelper helper) {
		if( helper != null && helper.isOpen()) { 
			helper.close();
		}
		helper = null;
	}
	
}
