package com.tonggou.gsm.andclient.db.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.Where;
import com.tonggou.gsm.andclient.bean.VideoPictureDTO;
import com.tonggou.gsm.andclient.db.TonggouVideoPictureDBHelper;
/**
 * TonggouVideoPictureDao
 * @author peter
 *
 */
public class TonggouVideoPictureDao {

	private static final String TAG = "TonggouVideoRecordDao";

	public static void insertVideo(Context context, ArrayList<VideoPictureDTO> videos ) {
		TonggouVideoPictureDBHelper helper = getDBHelper(context);
		try {
			Dao<VideoPictureDTO, Long> dao = getDao(helper);
			for( VideoPictureDTO video : videos ) {
				dao.create(video);
			}
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
		} finally {
			releaseDBHelper(helper);
		}
	}

	public static ArrayList<VideoPictureDTO> queryAll(Context context) {
		ArrayList<VideoPictureDTO> mVideos = new ArrayList<VideoPictureDTO>();
		TonggouVideoPictureDBHelper helper = getDBHelper(context);
		try {
			List<VideoPictureDTO> videos = helper.getDao(VideoPictureDTO.class).queryForAll();
			for (int i = 0; i < videos.size(); i++) {
				mVideos.add(videos.get(i));
			}
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
		} finally {
			releaseDBHelper(helper);
		}

		return mVideos;
	}

	public static ArrayList<VideoPictureDTO> clearAll(Context context) {
		ArrayList<VideoPictureDTO> mlogs = new ArrayList<VideoPictureDTO>();
		TonggouVideoPictureDBHelper helper = getDBHelper(context);
		try {
			Dao<VideoPictureDTO, Long> dao = getDao(helper);
			for( VideoPictureDTO log : queryAll(context) ) {
				DeleteBuilder<VideoPictureDTO, Long> deleteBuilder = dao.deleteBuilder();
				Where<VideoPictureDTO, Long> deleteWhere= deleteBuilder.where().eq(VideoPictureDTO.COLUMN_VIDEO_ID, log.getVideoId());
				deleteBuilder.setWhere(deleteWhere);
				dao.delete(deleteBuilder.prepare());
			}
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
		} finally {
			releaseDBHelper(helper);
		}
		return mlogs;
	}

	public static Dao<VideoPictureDTO, Long> getDao(TonggouVideoPictureDBHelper helper) throws SQLException {
		return helper.getTableDao();
	}

	public static TonggouVideoPictureDBHelper getDBHelper(Context context) {
		return new TonggouVideoPictureDBHelper(context);
	}

	public static void releaseDBHelper(TonggouVideoPictureDBHelper helper) {
		if( helper != null && helper.isOpen()) {
			helper.close();
		}
		helper = null;
	}
}