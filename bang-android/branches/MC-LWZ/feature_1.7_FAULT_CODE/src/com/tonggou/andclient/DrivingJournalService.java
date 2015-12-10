package com.tonggou.andclient;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.tonggou.andclient.app.BaseConnectOBDService;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.app.TongGouService;
import com.tonggou.andclient.util.DJDatabase;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.SomeUtil;
import com.tonggou.andclient.vo.DrivingJournalItem;
import com.tonggou.andclient.vo.SamplePoint;
import com.tonggou.andclient.vo.type.DrivingJournalStatus;

public class DrivingJournalService extends Service {
	public static final String ACTION_START_RECORD_DRIVING_JOURNAL = "com.tonggou.andclient.DrivingJournalService";
	public static final String ACTION_STOP_RECORD_DRIVING_JOURNAL = "com.tonggou.andclient.stopRecordDrivingJournal";
	public static final String ACTION_DRAW_ROUTE_ON_MAP = "com.tonggou.andclient.drawRouteOnMap";
	public static final String ACTION_UPDATE_VEHICLE_CONDITION = "com.tonggou.andclient.vehicleCondition";
	public static final String TAG = "DrivingJournalService";
	protected BMapManager mBMapManager;
	private LocationClient mLocationClient;
	private DJDatabase mDJDatabase;
	private SharedPreferences mPreferences;
	private ScheduledExecutorService mRecordExecutor;
	private ArrayList<SamplePoint> mSamplePonits = new ArrayList<SamplePoint>();
	private ArrayList<SamplePoint> mUnhandledSamplePonits = new ArrayList<SamplePoint>();
	private SamplePointsSavingTask mSavingTask;
	private DrivingJournalItem mCurrDrivingJournal;
	private SamplePoint mCurrSamplePoint;
	private BDLocation mCurrBDLocation;
	private GeoPoint mCurrGeoPoint, mLastGeoPoint;
	private boolean isLocationFirstReceived = true;
	private boolean isRTDFirstReceived = true;
	private int mCurrIndex;
	private double mStartDistance, mOnRoadDistance, mAvgOilWear, mOilWear;
	private String mOilLeft, mTemperature;
	private long mOnRoadTime;
	private Dialog mDialog;

	private static final int SAMPLE_FREQUENCY = 5;
	private static final int SAMPLE_POINTS_SIZE = 5;
	public static final String EXTRA_CURR_OIL_LEFT = "extra_curr_oil_left";
	public static final String EXTRA_CURR_TANK_TEMPERATURE = "extra_curr_tank_temperature";
	public static final String EXTRA_CURR_OIL_WEAR = "extra_curr_oil_wear";
	public static final String EXTRA_CURR_ON_ROAD_DISTANCE = "extra_curr_on_road_distance";
	public static final String EXTRA_CURR_ON_ROAD_TIME = "extra_curr_on_road_time";
	private static final double MIN_DISTANCE = 5d;
	private static final double MAX_DISTANCE = SAMPLE_FREQUENCY * 150d / 3.6;
	private static final float MIN_SPEED = 5f;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		initBMap(mMKGeneralListener);
		initLocationClient();
		initCommon();
		pullRTDDatas();
		registerReceiver();
		dependToShowDialog();
	}

	private void dependToShowDialog() {
		if (!SomeUtil.isGPSOn(this)) {
			mDialog = new Dialog(this, R.style.Dialog_style_dim2);
			View view = View.inflate(this, R.layout.dialog_my_vehicle_condition, null);
			TextView txt1 = (TextView) view.findViewById(R.id.txt_dialog_my_vehicle_condition_heading);
			TextView txt2 = (TextView) view.findViewById(R.id.txt_dialog_my_vehicle_condition_subheading);
			ImageView ivYes = (ImageView) view.findViewById(R.id.iv_dialog_my_vehicle_condition_yes);
			ImageView ivNo = (ImageView) view.findViewById(R.id.iv_dialog_my_vehicle_condition_no);

			txt1.setText(getResources().getString(R.string.myVehicleCondition_heading_no_gps));
			txt2.setText(getResources().getString(R.string.myVehicleCondition_subheading_no_gps));
			ivYes.setOnClickListener(mDialogOnClickListener);
			ivNo.setOnClickListener(mDialogOnClickListener);

			mDialog.setContentView(view);
			mDialog.setCanceledOnTouchOutside(true);
			mDialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
			mDialog.show();
		}
	}

	private OnClickListener mDialogOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_dialog_my_vehicle_condition_yes:
				Intent intent = new Intent();
				intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				mDialog.cancel();
				break;
			case R.id.iv_dialog_my_vehicle_condition_no:
				mDialog.cancel();
				break;
			}
		}
	};

	private void initCommon() {
		mDJDatabase = DJDatabase.getInstance(this);
		mPreferences = getSharedPreferences(BaseActivity.SETTING_INFOS, Context.MODE_PRIVATE);
	}

	private void pullRTDDatas() {
		Intent intent = new Intent();
		intent.setAction(TongGouService.TONGGOU_ACTION_READ_CURRENT_RTD_CONDITION);
		sendBroadcast(intent);
	}

	@Override
	public void onDestroy() {
		onActionStopReceived();
		if (mLocationClient != null) {
			mLocationClient.stop();
		}
		if (mBMapManager != null) {
			mBMapManager = null;
		}
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null) {
			stopSelf();
			return super.onStartCommand(intent, flags, startId);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(ACTION_STOP_RECORD_DRIVING_JOURNAL);
		filter.addAction(BaseConnectOBDService.ACTION_ON_GET_RTD_DATAS);
		registerReceiver(mBroadcastReceiver, filter);
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_STOP_RECORD_DRIVING_JOURNAL.equals(action)) {
				stopSelf();
			} else if (BaseConnectOBDService.ACTION_ON_GET_RTD_DATAS.equals(action)) {
				updateCC(intent);
			}
		}
	};

	private void updateCC(Intent intent) {
		if (isRTDFirstReceived) {
			mStartDistance = SomeUtil.getDouble(intent
					.getStringExtra(BaseConnectOBDService.EXTRA_TOTAL_DISTANCE));
			isRTDFirstReceived = false;
		}
		if (mCurrDrivingJournal != null) {
			sendToUpdateCC(intent);
		}
	}

	private void sendToUpdateCC(Intent intent) {
		mOilLeft = intent.getStringExtra(BaseConnectOBDService.EXTRA_OIL_LEFT);
		mTemperature = intent.getStringExtra(BaseConnectOBDService.EXTRA_OIL_TANK_TEMPERATURE);
		mOnRoadTime = System.currentTimeMillis() - mCurrDrivingJournal.getStartTime();
		mOnRoadDistance = SomeUtil.getDouble(intent
				.getStringExtra(BaseConnectOBDService.EXTRA_TOTAL_DISTANCE)) - mStartDistance;
		mAvgOilWear = SomeUtil.getDouble(intent.getStringExtra(BaseConnectOBDService.EXTRA_OIL_AVG_OIL_WEAR));
		if (mOnRoadDistance > 0 && mAvgOilWear > 0) {
			mOilWear = mAvgOilWear * mOnRoadDistance / 100;
		}

		Intent broadCast = new Intent(ACTION_UPDATE_VEHICLE_CONDITION);
		broadCast.putExtra(EXTRA_CURR_OIL_LEFT, mOilLeft);
		broadCast.putExtra(EXTRA_CURR_TANK_TEMPERATURE, mTemperature);
		broadCast.putExtra(EXTRA_CURR_OIL_WEAR, mOilWear);
		broadCast.putExtra(EXTRA_CURR_ON_ROAD_DISTANCE, mOnRoadDistance);
		broadCast.putExtra(EXTRA_CURR_ON_ROAD_TIME, mOnRoadTime);
		sendBroadcast(broadCast);

		saveCC();
	}

	private void saveCC() {
		mPreferences.edit().putString(BaseActivity.LAST_CC_TANK_TEMPERATURE, mTemperature)
				.putString(BaseActivity.LAST_CC_OIL_LEFT, mOilLeft)
				.putFloat(BaseActivity.LAST_CC_OIL_WEAR, (float) mOilWear)
				.putFloat(BaseActivity.LAST_CC_ON_ROAD_DISTANCE, (float) mOnRoadDistance)
				.putLong(BaseActivity.LAST_CC_ON_ROAD_TIME, mOnRoadTime).commit();
	}

	private void initLocationClient() {
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(mLocationListener);
		LocationClientOption clientOption = new LocationClientOption();
		clientOption.setOpenGps(true);
		clientOption.setAddrType("all");
		clientOption.setCoorType(BDMapBaseActivity.BD09LL);
		clientOption.setScanSpan(BDMapBaseActivity.SCAN_SPAN);
		clientOption.disableCache(false);
		clientOption.setPriority(LocationClientOption.GpsFirst);
		mLocationClient.setLocOption(clientOption);
		mLocationClient.start();
	}

	private BDLocationListener mLocationListener = new BDLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation location) {
			int type = location.getLocType();
			if (location != null
					&& (type == BDLocation.TypeGpsLocation || type == BDLocation.TypeNetWorkLocation)) {
				mCurrBDLocation = location;
				dependToRecord();
			}
		}

		@Override
		public void onReceivePoi(BDLocation location) {
		}
	};

	private void dependToRecord() {
		if (isLocationFirstReceived) {
			onLocationFirstReceived();
			isLocationFirstReceived = false;
		} else {
			if (mCurrBDLocation.getSpeed() < MIN_SPEED) {
				return;
			}
		}
		recordSamplePoint();
	}

	private void recordSamplePoint() {
		mCurrGeoPoint = SomeUtil.toGeoPointE6(mCurrBDLocation.getLatitude(), mCurrBDLocation.getLongitude());
		double distance = DistanceUtil.getDistance(mCurrGeoPoint, mLastGeoPoint);
		Log.d(TAG, distance + "");
		if (distance < MIN_DISTANCE || distance > MAX_DISTANCE) {
			return;
		} else {
			mLastGeoPoint = SomeUtil.toGeoPointE6(mCurrBDLocation.getLatitude(),
					mCurrBDLocation.getLongitude());
		}
		
		getCurrSamplePoint();
		if (mCurrIndex < SAMPLE_POINTS_SIZE) {
			mSamplePonits.add(mCurrSamplePoint);
			mCurrIndex++;
			Log.d(TAG, mCurrSamplePoint.toString());
		} else {
			if (mSavingTask == null || mSavingTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
				Log.d(TAG, "DBRecording");
				mSavingTask = new SamplePointsSavingTask();
				mSavingTask.execute();
			} else {
				mUnhandledSamplePonits.add(mCurrSamplePoint);
			}
		}
	}

	private void getCurrSamplePoint() {
		mCurrSamplePoint = new SamplePoint();
		mCurrSamplePoint.setLatitude(mCurrBDLocation.getLatitude());
		mCurrSamplePoint.setLongitude(mCurrBDLocation.getLongitude());
		mCurrSamplePoint.setSavedTime(System.currentTimeMillis());
	}

	private void onLocationFirstReceived() {
		mDJDatabase.clearSamplePoints();
		getCurrSamplePoint();
		mCurrDrivingJournal = new DrivingJournalItem();
		mCurrDrivingJournal.setAppDriveLogId(UUID.randomUUID().toString().toUpperCase());
		mCurrDrivingJournal.setAppUserNo(mPreferences.getString(BaseActivity.NAME, ""));
		mCurrDrivingJournal.setAppPlatform(INFO.MOBILE_PLATFORM);
		mCurrDrivingJournal.setVehicleNo(mPreferences.getString(BaseActivity.VEHICLENUM, ""));
		mCurrDrivingJournal.setStartTime(mCurrSamplePoint.getSavedTime());
		mCurrDrivingJournal.setStartLat(mCurrSamplePoint.getLatitude() + "");
		mCurrDrivingJournal.setStartLon(mCurrSamplePoint.getLongitude() + "");
		mCurrDrivingJournal.setStartPlace(mCurrBDLocation.getAddrStr());
		mCurrDrivingJournal.setOilKind(mPreferences.getString(BaseActivity.EXTRA_DEFAUL_TOIL_KIND, ""));
		mCurrDrivingJournal.setOilPrice(SomeUtil.getDouble(mPreferences.getString(
				BaseActivity.EXTRA_DEFAULT_OIL_PRICE, "")));
		mLastGeoPoint = SomeUtil.toGeoPointE6(mCurrBDLocation.getLatitude(), mCurrBDLocation.getLongitude());
		startRecord();
	}

	private void onActionStopReceived() {
		stopRecord();
		ArrayList<SamplePoint> samplePonitList = mDJDatabase.querySPList();
		if (!SomeUtil.dependToRecord(samplePonitList)) {
			mDJDatabase.clearSamplePoints();
			return;
		} else {
			mCurrDrivingJournal.setPlaceNotes(SomeUtil.getStrSamplePoints(samplePonitList));
		}
		mCurrDrivingJournal.setEndTime(mCurrSamplePoint.getSavedTime());
		mCurrDrivingJournal.setEndLat(mCurrSamplePoint.getLatitude() + "");
		mCurrDrivingJournal.setEndLon(mCurrSamplePoint.getLongitude() + "");
		mCurrDrivingJournal.setEndPlace(mCurrBDLocation.getAddrStr());
		mCurrDrivingJournal.setTravelTime(mCurrDrivingJournal.getEndTime()
				- mCurrDrivingJournal.getStartTime());
		mCurrDrivingJournal.setDistance(mOnRoadDistance);
		mCurrDrivingJournal.setOilWear(mOilWear);
		mCurrDrivingJournal.setTotalOilMoney(mCurrDrivingJournal.getOilPrice()
				* mCurrDrivingJournal.getOilWear());
		mCurrDrivingJournal.setStatus(DrivingJournalStatus.NOT_UPLOAD.toString());
		mCurrDrivingJournal.setLastUpdateTime(System.currentTimeMillis());
		mDJDatabase.insertDJItem(mCurrDrivingJournal);
		mDJDatabase.clearSamplePoints();

		saveCC();
	}

	private class SamplePointsSavingTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			mDJDatabase.insertSamplePonits(mSamplePonits);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mSamplePonits.clear();
			if (mUnhandledSamplePonits.size() > 0) {
				mSamplePonits.addAll(mUnhandledSamplePonits);
				mUnhandledSamplePonits.clear();
				mCurrIndex = mSamplePonits.size();
			} else {
				mCurrIndex = 0;
			}
			sendBroadcast(new Intent(ACTION_DRAW_ROUTE_ON_MAP));
		}
	}

	private void startRecord() {
		mRecordExecutor = Executors.newSingleThreadScheduledExecutor();
		mRecordExecutor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (mLocationClient != null && mLocationClient.isStarted()) {
					mLocationClient.requestLocation();
				}
			}
		}, SAMPLE_FREQUENCY, SAMPLE_FREQUENCY, TimeUnit.SECONDS);
	}

	private void stopRecord() {
		if (mRecordExecutor != null) {
			mRecordExecutor.shutdown();
			mRecordExecutor = null;
		}
	}

	protected void initBMap(MKGeneralListener listener) {
		mBMapManager = new BMapManager(getApplicationContext());
		mBMapManager.init(TongGouApplication.strKey, listener);
	}

	protected MKGeneralListener mMKGeneralListener = new MKGeneralListener() {
		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				Log.e(TAG, "ÍøÂçÁ¬½Ó´íÎó!");
			}
		}

		@Override
		public void onGetPermissionState(int iError) {
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				Log.e(TAG, "API KEY´íÎó!");
			}
		}
	};

}
