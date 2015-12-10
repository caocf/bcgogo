package com.tonggou.andclient.util;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.tonggou.andclient.BaseActivity;
import com.tonggou.andclient.vo.DrivingJournalItem;
import com.tonggou.andclient.vo.SamplePoint;
import com.tonggou.andclient.vo.type.DrivingJournalStatus;

public class DJDatabase {
	private static DJDatabase djDatabase;
	private DrivingJournalOpenHelper mOpenHelper;
	private SQLiteDatabase mWDatabase;
	private SQLiteDatabase mRDatabase;
	private Context mContext;
	private String mVehiNo;
	private static final String DATABASE_NAME = "DrivingJournal.db";
	private static final int VERSION = 1;
	// 只记录行车过程中的采点信息
	private static final String TABLE_ON_ROAD_SAMPLE_PONIT = "OnRoadSamplePonit";
	private static final String FIELD_LATITUDE = "Latitude";
	private static final String FIELD_LONGITUDE = "Longitude";
	private static final String FIELD_SAVEDTIME = "SavedTime";
	private static final String[] COLUMNS_ON_ROAD_SAMPLE_PONIT = { FIELD_LATITUDE, FIELD_LONGITUDE,
			FIELD_SAVEDTIME };
	// 行车日志
	private static final String TABLE_DRIVING_JOURNAL_ITEM = "DrivingJournalItem";
	private static final String FIELD_ID = "ID";
	private static final String FIELD_USERNO = "UserNo";
	private static final String FIELD_VEHICLENO = "VehicleNo";
	private static final String FIELD_STARTTIME = "StartTime";
	private static final String FIELD_STARTLAT = "StartLat";
	private static final String FIELD_STARTLON = "StartLon";
	private static final String FIELD_STARTPLACE = "StartPlace";
	private static final String FIELD_ENDTIME = "EndTime";
	private static final String FIELD_ENDLAT = "EndLat";
	private static final String FIELD_ENDLON = "EndLon";
	private static final String FIELD_ENDPLACE = "EndPlace";
	private static final String FIELD_TRAVELTIME = "TravelTime";
	private static final String FIELD_DISTANCE = "Distance";
	private static final String FIELD_OILWEAR = "OilWear";
	private static final String FIELD_OILKIND = "OilKind";
	private static final String FIELD_OILPRICE = "OilPrice";
	private static final String FIELD_OILCOST = "OilCost";
	private static final String FIELD_SAMPLEPOINTS = "SamplePoints";
	private static final String FIELD_LASTUPDATETIME = "LastUpdateTime";
	private static final String FIELD_STATUS = "status";
	private static final String[] COLUMNS_DRIVING_JOURNAL_ITEM = { FIELD_ID, FIELD_USERNO, FIELD_VEHICLENO,
			FIELD_STARTTIME, FIELD_STARTLAT, FIELD_STARTLON, FIELD_STARTPLACE, FIELD_ENDTIME, FIELD_ENDLAT,
			FIELD_ENDLON, FIELD_ENDPLACE, FIELD_TRAVELTIME, FIELD_DISTANCE, FIELD_OILWEAR, FIELD_OILKIND,
			FIELD_OILPRICE, FIELD_OILCOST, FIELD_SAMPLEPOINTS, FIELD_LASTUPDATETIME, FIELD_STATUS };

	private static final String AND = " AND ";

	public static DJDatabase getInstance(Context context) {
		if (djDatabase == null) {
			djDatabase = new DJDatabase(context);
		}
		return djDatabase;
	}

	private DJDatabase(Context context) {
		mOpenHelper = new DrivingJournalOpenHelper(context, DATABASE_NAME, null, VERSION);
		mWDatabase = mOpenHelper.getWritableDatabase();
		mRDatabase = mOpenHelper.getReadableDatabase();
		mContext = context;
	}

	private class DrivingJournalOpenHelper extends SQLiteOpenHelper {

		public DrivingJournalOpenHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TABLE_ON_ROAD_SAMPLE_PONIT + " (" + FIELD_LATITUDE + " TEXT,"
					+ FIELD_LONGITUDE + " TEXT," + FIELD_SAVEDTIME + " TEXT" + ")");

			db.execSQL("CREATE TABLE " + TABLE_DRIVING_JOURNAL_ITEM + " (" + FIELD_ID + " TEXT,"
					+ FIELD_USERNO + " TEXT," + FIELD_VEHICLENO + " TEXT," + FIELD_STARTTIME + " TEXT,"
					+ FIELD_STARTLAT + " TEXT," + FIELD_STARTLON + " TEXT," + FIELD_STARTPLACE + " TEXT,"
					+ FIELD_ENDTIME + " TEXT," + FIELD_ENDLAT + " TEXT," + FIELD_ENDLON + " TEXT,"
					+ FIELD_ENDPLACE + " TEXT," + FIELD_TRAVELTIME + " TEXT," + FIELD_DISTANCE + " TEXT,"
					+ FIELD_OILWEAR + " TEXT," + FIELD_OILKIND + " TEXT," + FIELD_OILPRICE + " TEXT,"
					+ FIELD_OILCOST + " TEXT," + FIELD_SAMPLEPOINTS + " TEXT," + FIELD_LASTUPDATETIME
					+ " TEXT," + FIELD_STATUS + " TEXT" + ")");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}

	public void insertSamplePonits(ArrayList<SamplePoint> samplePonits) {
		if (samplePonits != null && samplePonits.size() > 0) {
			for (SamplePoint samplePoint : samplePonits) {
				mWDatabase.insert(TABLE_ON_ROAD_SAMPLE_PONIT, null, createSPContentValues(samplePoint));
			}
		}
	}

	public void insertDJItems(ArrayList<DrivingJournalItem> djItems) {
		if (djItems != null && djItems.size() > 0) {
			for (DrivingJournalItem djItem : djItems) {
				mWDatabase.insert(TABLE_DRIVING_JOURNAL_ITEM, null, createDJContentValues(djItem));
			}
		}
	}

	public void insertDJItem(DrivingJournalItem djItem) {
		if (djItem != null) {
			mWDatabase.insert(TABLE_DRIVING_JOURNAL_ITEM, null, createDJContentValues(djItem));
		}
	}

	public void clearSamplePoints() {
		mWDatabase.delete(TABLE_ON_ROAD_SAMPLE_PONIT, null, null);
	}

	public GeoPoint[] querySPArray() {
		ArrayList<GeoPoint> samplePoints = new ArrayList<GeoPoint>();
		Cursor query = mRDatabase.query(TABLE_ON_ROAD_SAMPLE_PONIT, COLUMNS_ON_ROAD_SAMPLE_PONIT, null, null,
				null, null, null);
		if (query != null) {
			while (query.moveToNext()) {
				double latitude = query.getDouble(0);
				double longitude = query.getDouble(1);
				GeoPoint samplePoint = SomeUtil.toGeoPointE6(latitude, longitude);
				samplePoints.add(samplePoint);
			}
			query.close();
		}
		if (samplePoints.size() > 0) {
			return (GeoPoint[]) samplePoints.toArray(new GeoPoint[0]);
		} else {
			return null;
		}
	}

	public ArrayList<SamplePoint> querySPList() {
		ArrayList<SamplePoint> samplePonits = new ArrayList<SamplePoint>();
		Cursor query = mRDatabase.query(TABLE_ON_ROAD_SAMPLE_PONIT, COLUMNS_ON_ROAD_SAMPLE_PONIT, null, null,
				null, null, null);
		if (query != null) {
			while (query.moveToNext()) {
				samplePonits.add(createSamplePoint(query));
			}
			query.close();
		}
		if (samplePonits.size() > 0) {
			return samplePonits;
		} else {
			return null;
		}
	}

	private ArrayList<DrivingJournalItem> queryDJItems(String selection, String[] selectionArgs) {
		if (getVehiNo() == null) {
			return null;
		}
		ArrayList<DrivingJournalItem> djItems = new ArrayList<DrivingJournalItem>();
		Cursor query = mRDatabase.query(TABLE_DRIVING_JOURNAL_ITEM, COLUMNS_DRIVING_JOURNAL_ITEM, selection,
				selectionArgs, null, null, null);
		if (query != null) {
			while (query.moveToNext()) {
				djItems.add(createDJItem(query));
			}
			query.close();
		}
		if (djItems.size() > 0) {
			return djItems;
		} else {
			return null;
		}
	}

	public ArrayList<DrivingJournalItem> queryDJItems() {
		return queryDJItems(FIELD_VEHICLENO + " =?", new String[] { getVehiNo() });
	}

	public ArrayList<DrivingJournalItem> queryNotUploadDJItems() {
		String selection = FIELD_VEHICLENO + " =?" + AND + FIELD_STATUS + " =?";
		String selectionArgs[] = new String[] { getVehiNo(), DrivingJournalStatus.NOT_UPLOAD.toString() };
		return queryDJItems(selection, selectionArgs);
	}

	public ArrayList<DrivingJournalItem> queryDJItems(int type, String data) {
		if (getVehiNo() == null || data == null) {
			return null;
		}
		String selection = FIELD_VEHICLENO + " =?" + AND + FIELD_STARTTIME + " >=?" + AND + FIELD_STARTTIME
				+ " <?";
		String[] selectionArgs;
		String beginDay = null, endDay = null;
		switch (type) {
		case SomeUtil.TYPE_DAY:
			beginDay = SomeUtil.getLongDay(data);
			endDay = SomeUtil.getLongNextDay(data);
			break;
		case SomeUtil.TYPE_WEEK:
			beginDay = SomeUtil.getLongDay(data);
			endDay = SomeUtil.getLongLastDayOfWeek(data);
			break;
		case SomeUtil.TYPE_MONTH:
			beginDay = SomeUtil.getLongDay(data + "-01");
			endDay = SomeUtil.getLongLastDayOfMonth(data + "-01");
			break;
		}
		if (TextUtils.isEmpty(beginDay) || TextUtils.isEmpty(endDay)) {
			return null;
		}
		selectionArgs = new String[] { getVehiNo(), beginDay, endDay };
		return queryDJItems(selection, selectionArgs);
	}

	public void updateAdrr(String id, int type, String addr) {
		ContentValues values = new ContentValues();
		if (SomeUtil.TYPE_SA == type) {
			values.put(FIELD_STARTPLACE, addr);
		} else if (SomeUtil.TYPE_EA == type) {
			values.put(FIELD_ENDPLACE, addr);
		}
		mWDatabase.update(TABLE_DRIVING_JOURNAL_ITEM, values, FIELD_ID + " =? ", new String[] { id });
	}

	public void updateVCAdjust(DrivingJournalItem djItem) {
		if (djItem == null) {
			return;
		}
		ContentValues values = new ContentValues();
		values.put(FIELD_OILPRICE, djItem.getOilPrice());
		values.put(FIELD_OILWEAR, djItem.getOilWear());
		values.put(FIELD_OILCOST, djItem.getTotalOilMoney());
		values.put(FIELD_DISTANCE, djItem.getDistance());
		mWDatabase.update(TABLE_DRIVING_JOURNAL_ITEM, values, FIELD_ID + " =? ",
				new String[] { djItem.getAppDriveLogId() });
	}

	public void updateUploadedDJItem(DrivingJournalItem djItem) {
		if (djItem == null) {
			return;
		}
		ContentValues values = new ContentValues();
		values.put(FIELD_LASTUPDATETIME, djItem.getLastUpdateTime());
		values.put(FIELD_STATUS, DrivingJournalStatus.UPLOADED.toString());
		mWDatabase.update(TABLE_DRIVING_JOURNAL_ITEM, values, FIELD_ID + " =? ",
				new String[] { djItem.getAppDriveLogId() });
	}

	private SamplePoint createSamplePoint(Cursor query) {
		SamplePoint samplePoint = new SamplePoint();
		samplePoint.setLatitude(query.getDouble(0));
		samplePoint.setLongitude(query.getDouble(1));
		samplePoint.setSavedTime(query.getLong(2));
		return samplePoint;
	}

	private ContentValues createSPContentValues(SamplePoint samplePoint) {
		ContentValues values = new ContentValues();
		values.put(FIELD_LATITUDE, samplePoint.getLatitude());
		values.put(FIELD_LONGITUDE, samplePoint.getLongitude());
		values.put(FIELD_SAVEDTIME, samplePoint.getSavedTime());
		return values;
	}

	private DrivingJournalItem createDJItem(Cursor query) {
		DrivingJournalItem djItem = new DrivingJournalItem();
		djItem.setAppDriveLogId(query.getString(query.getColumnIndex(FIELD_ID)));
		djItem.setAppUserNo(query.getString(query.getColumnIndex(FIELD_USERNO)));
		djItem.setVehicleNo(query.getString(query.getColumnIndex(FIELD_VEHICLENO)));
		djItem.setStartTime(query.getLong(query.getColumnIndex(FIELD_STARTTIME)));
		djItem.setStartLat(query.getString(query.getColumnIndex(FIELD_STARTLAT)));
		djItem.setStartLon(query.getString(query.getColumnIndex(FIELD_STARTLON)));
		djItem.setStartPlace(query.getString(query.getColumnIndex(FIELD_STARTPLACE)));
		djItem.setEndTime(query.getLong(query.getColumnIndex(FIELD_ENDTIME)));
		djItem.setEndLat(query.getString(query.getColumnIndex(FIELD_ENDLAT)));
		djItem.setEndLon(query.getString(query.getColumnIndex(FIELD_ENDLON)));
		djItem.setEndPlace(query.getString(query.getColumnIndex(FIELD_ENDPLACE)));
		djItem.setTravelTime(query.getLong(query.getColumnIndex(FIELD_TRAVELTIME)));
		djItem.setDistance(query.getDouble(query.getColumnIndex(FIELD_DISTANCE)));
		djItem.setOilWear(query.getDouble(query.getColumnIndex(FIELD_OILWEAR)));
		djItem.setOilKind(query.getString(query.getColumnIndex(FIELD_OILKIND)));
		djItem.setOilPrice(query.getDouble(query.getColumnIndex(FIELD_OILPRICE)));
		djItem.setTotalOilMoney(query.getDouble(query.getColumnIndex(FIELD_OILCOST)));
		djItem.setPlaceNotes(query.getString(query.getColumnIndex(FIELD_SAMPLEPOINTS)));
		djItem.setLastUpdateTime(query.getLong(query.getColumnIndex(FIELD_LASTUPDATETIME)));
		djItem.setStatus(query.getString(query.getColumnIndex(FIELD_STATUS)));
		djItem.setAppPlatform(INFO.MOBILE_PLATFORM);
		return djItem;
	}

	private ContentValues createDJContentValues(DrivingJournalItem djItem) {
		ContentValues values = new ContentValues();
		values.put(FIELD_ID, djItem.getAppDriveLogId());
		values.put(FIELD_USERNO, djItem.getAppUserNo());
		values.put(FIELD_VEHICLENO, djItem.getVehicleNo());
		values.put(FIELD_STARTTIME, djItem.getStartTime());
		values.put(FIELD_STARTLAT, djItem.getStartLat());
		values.put(FIELD_STARTLON, djItem.getStartLon());
		values.put(FIELD_STARTPLACE, djItem.getStartPlace());
		values.put(FIELD_ENDTIME, djItem.getEndTime());
		values.put(FIELD_ENDLAT, djItem.getEndLat());
		values.put(FIELD_ENDLON, djItem.getEndLon());
		values.put(FIELD_ENDPLACE, djItem.getEndPlace());
		values.put(FIELD_TRAVELTIME, djItem.getTravelTime());
		values.put(FIELD_DISTANCE, djItem.getDistance());
		values.put(FIELD_OILWEAR, djItem.getOilWear());
		values.put(FIELD_OILKIND, djItem.getOilKind());
		values.put(FIELD_OILPRICE, djItem.getOilPrice());
		values.put(FIELD_OILCOST, djItem.getTotalOilMoney());
		values.put(FIELD_SAMPLEPOINTS, djItem.getPlaceNotes());
		values.put(FIELD_LASTUPDATETIME, djItem.getLastUpdateTime());
		values.put(FIELD_STATUS, djItem.getStatus());
		return values;
	}

	private String getVehiNo() {
		if (mVehiNo == null) {
			mVehiNo = PreferenceUtil.getString(mContext, BaseActivity.SETTING_INFOS, BaseActivity.VEHICLENUM);
		}
		return mVehiNo;
	}
}
