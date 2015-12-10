package com.tonggou.andclient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.tonggou.andclient.DrivingJournalCalendarLayout.DJCalendarListener;
import com.tonggou.andclient.app.BaseConnectOBDService;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.app.TongGouService;
import com.tonggou.andclient.myview.AbsViewHolderAdapter;
import com.tonggou.andclient.util.DJDatabase;
import com.tonggou.andclient.util.SaveDB;
import com.tonggou.andclient.util.ScreenSizeUtil;
import com.tonggou.andclient.util.SomeUtil;
import com.tonggou.andclient.vo.CarCondition;
import com.tonggou.andclient.vo.DrivingJournalItem;

public class MyVehicleConditionActivity extends BDMapBaseActivity implements DJCalendarListener {
	public static final String TAG = "MyVehicleConditionActivity";
	private Resources mResources;
	private SharedPreferences mPreferences;
	private DJDatabase mDJDatabase;
	private MapView mMapView;
	private int mScreenHeight, mScreenWidth, mCcMapHeight, mDjMapHeight;
	private float mCcScaleRate, mDjScaleRate;
	private LocationClient mLocationClient;
	private MyLocationOverlay mMyLocationOverlay;
	private PopupOverlay mPopupOverlay;
	private MKSearch mMKSearch;
	private Symbol mLineSymbol;
	private DrivingJournalPullLayout mDjPullLayout;
	private DrivingJournalCalendarLayout mDjCalendarLayout;
	private HorizontalScrollView mCcHSDatas;
	private ListView mDjListView;
	private LinearLayout mLlClip, mLlOilPrice, mLlpop;
	private FrameLayout mCcFlDTCHead, mCcFlDTC;
	private EditText mEtPrice;
	private ProgressBar mPbClip;
	private ImageView[] mDjIvCalendars;
	private TextView[] mTxtSubtitles;
	private ImageView mCcIvNoDtc, mCcIvDtc1, mCcIvDtc2, mCcIvRefresh;
	private TextView mDjTxtNow, mTopTxtOilPrice, mCcTxtAvgOilWear, mCcTxtVoltage, mCcTxtTemperature,
			mCcTxtDistance, mCcTxtTime, mDjTxtDistance, mDjTxtTime, mDjTxtOilWear, mDjTxtOilCost,
			mPopTxtContent;

	private DjitemUpdateTask mDjitemUpdateTask;
	private OnRoadQueryTask mOnRoadQueryTask;
	private AddressUpdateTask mAddrUpdateTask;
	private ReadDTCTask mReadDTCTask;
	private ArrayList<DrivingJournalItem> mAddrUpdateList;
	private DJItemAdapter mDJItemAdapter;
	private LocationData mCurrLocData;
	private GeoPoint mParkedGeopOint;
	private String mDefVehiNo, mDefOilPrice, mParkedAddr;
	private PopupWindow mPopupOilPrice;
	private Dialog mDialogEnter;
	private Animation mDTCAnim1;
	private AnimationDrawable mDTCAnim2;
	private int mSelectedIndex, mUpdateIndex, mStartAddrCount, mEndAddrCount;
	private int mCurrType = TYPE_CC;
	private static final double MIN_OIL_PRICE = 5d;
	protected static final long CLIP_TIME = 150;
	private static final int TYPE_CC = 0;
	private static final int TYPE_DJ = 1;
	private static final int NOW_FONT_SIZE_NORMAL = 13;
	private static final int NOW_FONT_SIZE_SMALL = 12;
	private static final int CC_DATA_WIDITH_DIVIDER = 4;
	public static final String POINT_PARKED = "parked";
	public static final String BATTERY_VOLTAGE_UNIT = "V";
	public static final String TANK_TEMPERATURE_UNIT = "℃";
	public static final String AVG_OIL_WEAR_UNIT = "\nL/100km";
	public static final String OIL_WEAR_UNIT = "L";
	public static final String DISTANCE_UNIT = "km";
	public static final String COST_UNIT = "元";

	private int[] imgCalendarSelected = { R.drawable.icon_calendar_day_selected,
			R.drawable.icon_calendar_week_selected, R.drawable.icon_calendar_month_selected };
	private int[] imgCalendarNormal = { R.drawable.icon_calendar_day_normal,
			R.drawable.icon_calendar_week_normal, R.drawable.icon_calendar_month_normal };

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		initBMap(mMKGeneralListener);
		setContentView(R.layout.my_vehicle_condition);
		initCommon();
		initAllViews();
		initBMapParams();
		registerReceiver();
		dependToShowDialog();
		
		// 读取当前车况
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Intent intent = new Intent(BaseConnectOBDService.TONGGOU_ACTION_READ_CURRENT_RTD_CONDITION);
				sendBroadcast(intent);
			}
		}, 1000);
	}

	private void initCommon() {
		mDJDatabase = DJDatabase.getInstance(this);
		mPreferences = getSharedPreferences(BaseActivity.SETTING_INFOS, Context.MODE_PRIVATE);
		mDefVehiNo = mPreferences.getString(BaseActivity.VEHICLENUM, "");
		mDefOilPrice = mPreferences.getString(BaseActivity.EXTRA_DEFAULT_OIL_PRICE, "0.0");
		pullParkedInfo();
		ScreenSizeUtil screenUtil = new ScreenSizeUtil(getApplicationContext());
		mScreenHeight = screenUtil.getScreenHeight();
		mScreenWidth = screenUtil.getScreenWidth();
	}

	private DrivingJournalItem pullParkedInfo() {
		mParkedGeopOint = null;
		mParkedAddr = null;
		DrivingJournalItem djitem = mDJDatabase.queryLastDJItem();
		if (djitem != null) {
			mParkedGeopOint = SomeUtil.toGeoPointE6(djitem.getEndLat(), djitem.getEndLon());
			mParkedAddr = djitem.getEndPlace();
		}
		return djitem;
	}

	private void initBMapParams() {
		initMapController(mMapView);
		initMyLocationOverlay();
		initLineSymbol();
		initMKSearch();
		initPopupOverlay();
		initLocationClient();
		mMapView.setBuiltInZoomControls(false);
		mMapView.post(new Runnable() {
			@Override
			public void run() {
				mCcMapHeight = mMapView.getHeight();
				mDjMapHeight = (int) mResources.getDimension(R.dimen.vehicle_condition_mapview_height);
				mCcScaleRate = ((float) mCcMapHeight) / ((float) mScreenHeight);
				mDjScaleRate = ((float) mDjMapHeight) / ((float) mScreenHeight);
			}
		});
	}

	private void initAllViews() {
		mResources = getResources();
		initTopViews();
		initMyViews();
		initDJViews();
		initCCViews();
	}

	private void initMyViews() {
		mMapView = (MapView) findViewById(R.id.mv_vehicle_condition);
		mLlClip = (LinearLayout) findViewById(R.id.ll_vehicle_condition_clip);
		mPbClip = (ProgressBar) findViewById(R.id.pb_vehicle_condition_clip);
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(DrivingJournalService.ACTION_DRAW_ROUTE_ON_MAP);
		filter.addAction(DrivingJournalService.ACTION_UPDATE_VEHICLE_CONDITION);
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		filter.addAction(BaseConnectOBDService.ACTION_ON_GET_DTC_DATAS);
		filter.addAction(DrivingJournalService.ACTION_ON_DRIVING_JOURNAL_FINISHED);
		filter.addAction(BaseConnectOBDService.ACTION_ON_GET_RTD_DATAS);
		registerReceiver(mBroadcastReceiver, filter);
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (DrivingJournalService.ACTION_DRAW_ROUTE_ON_MAP.equals(action)) {
				if (mCurrType == TYPE_CC
						&& (mOnRoadQueryTask == null || AsyncTask.Status.FINISHED.equals(mOnRoadQueryTask
								.getStatus()))) {
					mOnRoadQueryTask = new OnRoadQueryTask();
					mOnRoadQueryTask.execute();
				}
			} else if (DrivingJournalService.ACTION_UPDATE_VEHICLE_CONDITION.equals(action)) {
				ccUpdate(intent);
			} else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
				checkConnectivity();
			} else if (BaseConnectOBDService.ACTION_ON_GET_DTC_DATAS.equals(action)) {
				showDTC();
			} else if (DrivingJournalService.ACTION_ON_DRIVING_JOURNAL_FINISHED.equals(action)) {
				onDJFinished();
			} else if (BaseConnectOBDService.ACTION_ON_GET_RTD_DATAS.equals(action)) {
				if (!SomeUtil.isServiceRunning(getApplicationContext(), DrivingJournalService.class)) {
					onRTDReceived(intent);
				}
			}
		}
	};

	private void onRTDReceived(Intent intent) {
		String voltage = intent.getStringExtra(BaseConnectOBDService.EXTRA_BATTERY_VOLTAGE);
		String temperature = intent.getStringExtra(BaseConnectOBDService.EXTRA_OIL_TANK_TEMPERATURE);
		long onRoadTime = 0;
		String onRoadDistance = "0";
		String avgOilWear = SomeUtil.doubleToString(SomeUtil.fmtDouble(intent
				.getStringExtra(BaseConnectOBDService.EXTRA_AVG_OIL_WEAR)));
		setCCDatas(voltage, temperature, avgOilWear, onRoadDistance, onRoadTime);
	}

	private void onDJFinished() {
		DrivingJournalItem djItem = pullParkedInfo();
		if (djItem != null) {
			LocationData locationData = new LocationData();
			locationData.latitude = SomeUtil.stringToDouble(djItem.getEndLat());
			locationData.longitude = SomeUtil.stringToDouble(djItem.getEndLon());
			drawOneOrTwoLoc(locationData);
		}
	}

	private void ccUpdate(Intent intent) {
		String voltage = intent.getStringExtra(DrivingJournalService.EXTRA_CURR_BATTERY_VOLTAGE);
		String temperature = intent.getStringExtra(DrivingJournalService.EXTRA_CURR_TANK_TEMPERATURE);
		String avgOilWear = intent.getStringExtra(DrivingJournalService.EXTRA_CURR_AVG_OIL_WEAR);
		String onRoadDistance = intent.getStringExtra(DrivingJournalService.EXTRA_CURR_ON_ROAD_DISTANCE);
		long onRoadTime = intent.getLongExtra(DrivingJournalService.EXTRA_CURR_ON_ROAD_TIME, 0l);
		setCCDatas(voltage, temperature, avgOilWear, onRoadDistance, onRoadTime);
	}

	private void initCCDatas() {
		String voltage = mPreferences.getString(BaseActivity.LAST_CC_BATTERY_VOLTAGE, null);
		String temperature = mPreferences.getString(BaseActivity.LAST_CC_TANK_TEMPERATURE, null);
		String avgOilWear = mPreferences.getString(BaseActivity.LAST_CC_AVG_OIL_WEAR, "0");
		String onRoadDistance = mPreferences.getString(BaseActivity.LAST_CC_ON_ROAD_DISTANCE, "0");
		long onRoadTime = mPreferences.getLong(BaseActivity.LAST_CC_ON_ROAD_TIME, 0l);
		setCCDatas(voltage, temperature, avgOilWear, onRoadDistance, onRoadTime);
	}

	private void setCCDatas(String voltage, String temperature, String avgOilWear, String onRoadDistance,
			long onRoadTime) {
		mCcTxtVoltage.setText(TextUtils.isEmpty(voltage) ? "--" : voltage + BATTERY_VOLTAGE_UNIT);
		mCcTxtTemperature
				.setText(TextUtils.isEmpty(temperature) ? "--" : temperature + TANK_TEMPERATURE_UNIT);
		mCcTxtAvgOilWear.setText(avgOilWear + AVG_OIL_WEAR_UNIT);
		mCcTxtDistance.setText(onRoadDistance + DISTANCE_UNIT);
		mCcTxtTime.setText(SomeUtil.fmtMilliseconds(onRoadTime));
	}

	private void checkConnectivity() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
		if (activeNetworkInfo != null && activeNetworkInfo.isAvailable()) {
			NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mobileInfo != null && mobileInfo.isConnected()) {
				Log.d(TAG, "连接到移动网络");
			} else if (wifiInfo != null && wifiInfo.isConnected()) {
				Log.d(TAG, "连接到wifi");
				if (mAddrUpdateTask == null || AsyncTask.Status.FINISHED.equals(mAddrUpdateTask.getStatus())) {
					mAddrUpdateTask = new AddressUpdateTask();
					mAddrUpdateTask.execute();
				}
			}
		} else {
			Log.d(TAG, "断开网络");
		}
	}

	private class AddressUpdateTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			updateDJAddress();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (!SomeUtil.isActivityRunning(getApplicationContext(), MyVehicleConditionActivity.class)) {
				return;
			}
			if (mAddrUpdateList != null && mAddrUpdateList.size() > 0) {
				reverseAddr(mAddrUpdateList.get(mUpdateIndex));
			}
		}

		private void updateDJAddress() {
			ArrayList<DrivingJournalItem> djItems = mDJDatabase.queryDJItems();
			if (djItems != null) {
				mAddrUpdateList = new ArrayList<DrivingJournalItem>();
				mUpdateIndex = mStartAddrCount = mEndAddrCount = 0;
				ArrayList<DrivingJournalItem> startList = new ArrayList<DrivingJournalItem>();
				ArrayList<DrivingJournalItem> endList = new ArrayList<DrivingJournalItem>();
				for (DrivingJournalItem djItem : djItems) {
					if (djItem.getStartPlace() == null) {
						startList.add(djItem);
					}
					if (djItem.getEndPlace() == null) {
						endList.add(djItem);
					}
				}
				mStartAddrCount = startList.size();
				mEndAddrCount = endList.size();
				if (mStartAddrCount > 0) {
					mAddrUpdateList.addAll(startList);
				}
				if (mEndAddrCount > 0) {
					mAddrUpdateList.addAll(endList);
				}
			}
		}
	}

	private void reverseAddr(DrivingJournalItem djItem) {
		GeoPoint geoPoint;
		if (mUpdateIndex < mStartAddrCount) {
			geoPoint = SomeUtil.toGeoPointE6(djItem.getStartLat(), djItem.getStartLon());
		} else {
			geoPoint = SomeUtil.toGeoPointE6(djItem.getEndLat(), djItem.getEndLon());
		}
		mMKSearch.reverseGeocode(geoPoint);
	}

	private void showOilPriceWindow() {
		if (mPopupOilPrice == null) {
			View contentView = View.inflate(this, R.layout.popup_update_oil_price, null);
			mPopupOilPrice = new PopupWindow(contentView, WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.WRAP_CONTENT);
			mPopupOilPrice.setFocusable(true);
			mPopupOilPrice.setBackgroundDrawable(new BitmapDrawable());
			mPopupOilPrice.setOutsideTouchable(true);
			mEtPrice = (EditText) contentView.findViewById(R.id.et_popup_oil_price);

			mPopupOilPrice.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					double oilPrice = SomeUtil.stringToDouble(mEtPrice.getText().toString());
					if (oilPrice > MIN_OIL_PRICE) {
						mDefOilPrice = String.valueOf(oilPrice);
						mPreferences.edit().putString(BaseActivity.EXTRA_DEFAULT_OIL_PRICE, mDefOilPrice)
								.commit();
						mTopTxtOilPrice.setText(String.format(
								mResources.getString(R.string.title_myVehicleCondition_oil_price),
								mDefOilPrice));
					}
				}
			});
		}
		mEtPrice.setText(mDefOilPrice);
		mEtPrice.requestFocus();
		mEtPrice.setSelection(mEtPrice.getText().toString().length());
		// 如果开启输入法，行车日志的记录很多时，界面显示会有BUG，所以只在当前车况显示
		mEtPrice.post(new Runnable() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});
		mPopupOilPrice.showAsDropDown(mLlOilPrice);
	}

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.rl_myVehicleCondition_back:
				onBackPressed();
				break;
			case R.id.ll_myVehicleCondition_oil_price:
				showOilPriceWindow();
				break;
			case R.id.iv_curr_condtion_refresh:
				pullRTDDatas();
				break;
			case R.id.fl_curr_condtion_dtc_head:
				startActivity(new Intent(MyVehicleConditionActivity.this, FaultCodeCenterActivity.class));
				break;
			case R.id.ll_myVehicleCondition_curr_condtion:
				setSubtitle(0);
				changePage(0);
				break;
			case R.id.ll_myVehicleCondition_driving_journal:
				setSubtitle(1);
				changePage(1);
				break;
			case R.id.imgView_driving_journal_calendar_day:
				setCalendarType(0);
				mDjCalendarLayout.updateDatas(SomeUtil.TYPE_DAY);
				djUpdate();
				break;
			case R.id.imgView_driving_journal_calendar_week:
				setCalendarType(1);
				mDjCalendarLayout.updateDatas(SomeUtil.TYPE_WEEK);
				djUpdate();
				break;
			case R.id.imgView_driving_journal_calendar_month:
				setCalendarType(2);
				mDjCalendarLayout.updateDatas(SomeUtil.TYPE_MONTH);
				djUpdate();
				break;
			}
		}
	};

	private void changePage(final int index) {
		switch (index) {
		case 0:
			mCurrType = TYPE_CC;
			setMainVisible(View.VISIBLE, View.GONE);
			setMapParams(0);
			ccDependToShow();
			break;
		case 1:
			mCurrType = TYPE_DJ;
			setMainVisible(View.GONE, View.VISIBLE);
			clearOverlays();
			djUpdate();
			setMapParams(1);
			break;
		}
	}

	private void setMainVisible(int v1, int v2) {
		if (v1 == View.GONE) {
			hidePopup();
		}
		mCcHSDatas.setVisibility(v1);
		mCcFlDTCHead.setVisibility(v1);
		mLlOilPrice.setVisibility(v1);
		mCcIvRefresh.setVisibility(v1);
		mDjPullLayout.setVisibility(v2);
		mDjListView.setVisibility(v2);
	}

	private void setMapParams(final int index) {
		if (index == 1) {
			mPbClip.setVisibility(View.VISIBLE);
		} else {
			mPbClip.setVisibility(View.GONE);
		}
		mLlClip.setVisibility(View.VISIBLE);

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				SystemClock.sleep(CLIP_TIME);
				return null;
			}

			protected void onPostExecute(Void result) {
				mLlClip.setVisibility(View.GONE);
			};
		}.execute();

		mMapView.post(new Runnable() {
			@Override
			public void run() {
				if (index == 0) {
					LayoutParams layoutParams = (LayoutParams) mMapView.getLayoutParams();
					layoutParams.height = mCcMapHeight;
					mMapView.setLayoutParams(layoutParams);
					mMapView.showScaleControl(true);
					mMapView.refresh();
				} else {
					LayoutParams layoutParams = (LayoutParams) mMapView.getLayoutParams();
					layoutParams.height = mDjMapHeight;
					mMapView.setLayoutParams(layoutParams);
					mMapView.showScaleControl(false);
					mMapView.refresh();
				}
			}
		});
	}

	private void djUpdate() {
		if (mDjitemUpdateTask == null || AsyncTask.Status.FINISHED.equals(mDjitemUpdateTask.getStatus())) {
			mDjitemUpdateTask = new DjitemUpdateTask();
			mDjitemUpdateTask.execute();
		}
	}

	private class DjitemUpdateTask extends AsyncTask<Void, Void, ArrayList<DrivingJournalItem>> {
		@Override
		protected ArrayList<DrivingJournalItem> doInBackground(Void... params) {
			ArrayList<DrivingJournalItem> djItems = mDJDatabase.queryDJItems(
					mDjCalendarLayout.getSelectedType(), mDjCalendarLayout.getSelectedData());
			return djItems;
		}

		@Override
		protected void onPostExecute(ArrayList<DrivingJournalItem> result) {
			if (result != null) {
				mDJItemAdapter.update(result);
				calculateDjDatas(result);
				setNowData();
			}
		}
	}

	private void setNowData() {
		String data;
		if (mDjCalendarLayout.getSelectedType() == SomeUtil.TYPE_WEEK) {
			String startDay = mDjCalendarLayout.getSelectedData();
			String endDay = SomeUtil.getLastDayOfWeek(startDay);
			data = SomeUtil.getWeekPattern(startDay, endDay);
			mDjTxtNow.setTextSize(NOW_FONT_SIZE_SMALL);
		} else {
			data = mDjCalendarLayout.getSelectedData();
			mDjTxtNow.setTextSize(NOW_FONT_SIZE_NORMAL);
		}
		mDjTxtNow.setText(data);
	}

	private void calculateDjDatas(ArrayList<DrivingJournalItem> djItems) {
		double sumDistance = 0, sumOilWear = 0, sumOilCost = 0;
		long sumTravelTime = 0;
		for (DrivingJournalItem djItem : djItems) {
			double distance = djItem.getDistance();
			sumDistance += distance;
			// sumOilWear是实际油耗，而getOilWear得到的是百公里油耗
			sumOilWear += SomeUtil.getOilWear(djItem.getOilWear(), distance);
			sumOilCost += djItem.getTotalOilMoney();
			sumTravelTime += djItem.getTravelTime();
		}
		mDjTxtDistance.setText(SomeUtil.doubleToString(SomeUtil.fmtDouble(sumDistance)) + DISTANCE_UNIT);
		mDjTxtOilWear.setText(SomeUtil.doubleToString(SomeUtil.fmtDouble(sumOilWear)) + OIL_WEAR_UNIT);
		mDjTxtOilCost.setText(SomeUtil.doubleToString(SomeUtil.fmtDouble(sumOilCost)) + COST_UNIT);
		mDjTxtTime.setText(SomeUtil.fmtMilliseconds(sumTravelTime));
	}

	private void setSubtitle(int postion) {
		mTxtSubtitles[postion].setTextColor(mResources.getColor(android.R.color.white));
		mTxtSubtitles[(postion + 1) % mTxtSubtitles.length].setTextColor(mResources
				.getColor(android.R.color.darker_gray));
	}

	private void setCalendarType(int postion) {
		for (int i = 0; i < mDjIvCalendars.length; i++) {
			if (postion == i) {
				mDjIvCalendars[i].setImageResource(imgCalendarSelected[i]);
			} else {
				mDjIvCalendars[i].setImageResource(imgCalendarNormal[i]);
			}
		}
	}

	private void initMyLocationOverlay() {
		mMyLocationOverlay = new MyLocationOverlay(mMapView);
		mMyLocationOverlay.enableCompass();
	}

	private void initLineSymbol() {
		mLineSymbol = new Symbol();
		Symbol.Color lineColor = mLineSymbol.new Color();
		lineColor.red = 38;
		lineColor.green = 131;
		lineColor.blue = 239;
		lineColor.alpha = 200;
		mLineSymbol.setLineSymbol(lineColor, 7);
	}

	private void initMKSearch() {
		mMKSearch = new MKSearch();
		mMKSearch.init(mBMapManager, new AddrSearchListener());
	}

	private void initPopupOverlay() {
		mPopupOverlay = new PopupOverlay(mMapView, mPopupClickListener);
		mLlpop = (LinearLayout) View.inflate(this, R.layout.my_vehicle_condition_popupoverlay, null);
		mPopTxtContent = (TextView) mLlpop.findViewById(R.id.txt_myVehicleCondition_popup_content);
	}

	private PopupClickListener mPopupClickListener = new PopupClickListener() {
		@Override
		public void onClickedPopup(int index) {
			hidePopup();
		}
	};

	private void initLocationClient() {
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(mLocationListener);
		LocationClientOption clientOption = new LocationClientOption();
		clientOption.setOpenGps(true);
		clientOption.setAddrType("all");
		clientOption.setCoorType(BD09LL);
		clientOption.disableCache(true);
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
				if (!SomeUtil.isActivityRunning(getApplicationContext(), MyVehicleConditionActivity.class)) {
					return;
				}
				mCurrLocData = getLocData(location);
				ccDependToShow();
			}
		}

		@Override
		public void onReceivePoi(BDLocation location) {
		}
	};

	private void ccDependToShow() {
		clearOverlays();
		if (mCurrLocData != null) {
			if (mOnRoadQueryTask == null || AsyncTask.Status.FINISHED.equals(mOnRoadQueryTask.getStatus())) {
				mOnRoadQueryTask = new OnRoadQueryTask();
				mOnRoadQueryTask.execute(mCurrLocData);
			} else {
				drawOneOrTwoLoc(mCurrLocData);
			}
		}
	}

	private class OnRoadQueryTask extends AsyncTask<LocationData, Void, GeoPoint[]> {
		private LocationData[] mParams;

		@Override
		protected GeoPoint[] doInBackground(LocationData... params) {
			mParams = params;
			return mDJDatabase.querySPArray();
		}

		@Override
		protected void onPostExecute(GeoPoint[] result) {
			if (!SomeUtil.isActivityRunning(getApplicationContext(), MyVehicleConditionActivity.class)) {
				return;
			}
			if (result == null && mParams != null && mParams.length > 0) {
				drawOneOrTwoLoc(mParams[0]);
			} else {
				if (!SomeUtil.isServiceRunning(getApplicationContext(), DrivingJournalService.class)) {
					// TODO:如果正在行驶途中强杀掉Service，下次再次进入时应该开启Service应继续上次未完成的；这里先简单处理，删除上次的记录
					mDJDatabase.clearSamplePoints();
				}
				drawRoute(result, mCcScaleRate);
			}
		}
	}

	private synchronized void drawRoute(final GeoPoint[] samplePoints, float scaleRate) {
		if (mMapView != null && mMapView.getOverlays() != null && samplePoints.length > 1) {
			clearOverlays();
			GraphicsOverlay graphicsOverlay = new GraphicsOverlay(mMapView);
			mMapView.getOverlays().add(graphicsOverlay);
			Geometry lineGeometry = new Geometry();
			lineGeometry.setPolyLine(samplePoints);
			Graphic lineGraphic = new Graphic(lineGeometry, mLineSymbol);
			graphicsOverlay.setData(lineGraphic);

			if (mCurrType == TYPE_CC) {
				addPointOverlays(R.drawable.icon_cc_start, R.drawable.icon_cc_on_road, samplePoints);
			} else {
				addPointOverlays(R.drawable.icon_dj_start, R.drawable.icon_dj_end, samplePoints);
			}
			mMapView.refresh();
			zoomToSpan(samplePoints, scaleRate);
		}
	}

	private void zoomToSpan(GeoPoint[] geoPoints, final float scaleRate) {
		final int[] spanParams = SomeUtil.getSpanParams(geoPoints);
		mMapView.post(new Runnable() {
			@Override
			public void run() {
				int maxLat = spanParams[SomeUtil.MAX_LAT];
				int minLat = spanParams[SomeUtil.MIN_LAT];
				int maxLon = spanParams[SomeUtil.MAX_LON];
				int minLon = spanParams[SomeUtil.MIN_LON];
				mMapController.setCenter(new GeoPoint((maxLat + minLat) / 2, (maxLon + minLon) / 2));
				mMapController.zoomToSpan((int) ((maxLat - minLat) * scaleRate),
						(int) ((maxLon - minLon) * scaleRate));
				mMapView.refresh();
			}
		});
	}

	private void addPointOverlays(int startResId, int endResId, GeoPoint[] samplePoints) {
		PointOverlay startOverlay = getPointOverlay(startResId);
		PointOverlay endOverlay = getPointOverlay(endResId);
		OverlayItem startItem = new OverlayItem(samplePoints[0], "start", "start");
		OverlayItem endItem = new OverlayItem(samplePoints[samplePoints.length - 1], "end", "end");
		startOverlay.addItem(startItem);
		endOverlay.addItem(endItem);
		mMapView.getOverlays().add(startOverlay);
		mMapView.getOverlays().add(endOverlay);
	}

	private PointOverlay getPointOverlay(int resId) {
		return new PointOverlay(mResources.getDrawable(resId), mMapView);
	}

	private class PointOverlay extends ItemizedOverlay<OverlayItem> {
		public PointOverlay(Drawable mark, MapView mapView) {
			super(mark, mapView);
		}

		protected boolean onTap(int index) {
			OverlayItem overlayItem = getItem(index);
			String title = overlayItem.getTitle();
			if (POINT_PARKED.equals(title)) {
				final GeoPoint geoPoint = overlayItem.getPoint();
				mMapController.animateTo(geoPoint);
				if (mParkedAddr != null) {
					mCcHSDatas.postDelayed(new Runnable() {
						@Override
						public void run() {
							mPopTxtContent.setText(mParkedAddr);
							mPopupOverlay.showPopup(mLlpop, geoPoint, 32);
						}
					}, 350);
				}
			}
			return true;
		}

		public boolean onTap(GeoPoint pt, MapView mapView) {
			super.onTap(pt, mapView);
			hidePopup();
			return false;
		}
	}

	private void hidePopup() {
		if (mPopupOverlay != null) {
			mPopupOverlay.hidePop();
		}
	}

	private void drawOneOrTwoLoc(LocationData locData) {
		if (mParkedGeopOint == null) {
			drawMylocation(locData);
		} else {
			drawTwoLocation(locData);
		}
	}

	/**
	 * 画当前位置和上次停车位置
	 * 
	 * @param locData
	 */
	private void drawTwoLocation(LocationData locData) {
		clearOverlays();
		GeoPoint myLocGeoPoint = SomeUtil.toGeoPointE6(locData.latitude, locData.longitude);
		mMyLocationOverlay.setData(locData);
		mMapView.getOverlays().add(mMyLocationOverlay);
		PointOverlay parkedOverlay = getPointOverlay(R.drawable.icon_vehicle_condition_last_parked_location);
		OverlayItem parkedItem = new OverlayItem(mParkedGeopOint, POINT_PARKED, POINT_PARKED);
		parkedOverlay.addItem(parkedItem);
		mMapView.getOverlays().add(parkedOverlay);
		mMapView.refresh();
		zoomToSpan(new GeoPoint[] { myLocGeoPoint, mParkedGeopOint }, mCcScaleRate);
		mMapView.post(new Runnable() {
			@Override
			public void run() {
				mMapController.zoomOut();
			}
		});
	}

	/**
	 * 只画当前位置
	 * 
	 * @param locData
	 */
	private void drawMylocation(LocationData locData) {
		clearOverlays();
		mMyLocationOverlay.setData(locData);
		mMapView.getOverlays().add(mMyLocationOverlay);
		mMapView.refresh();
		mMapController.setZoom(ZOOM_LEVEL);
		GeoPoint geoPoint = SomeUtil.toGeoPointE6(locData.latitude, locData.longitude);
		mMapController.setCenter(geoPoint);
		mMapController.animateTo(geoPoint);
	}

	private void clearOverlays() {
		if (mMapView != null) {
			mMapView.getOverlays().clear();
		}
	}

	private void dependToShowDialog() {
		mMapView.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (TextUtils.isEmpty(mDefVehiNo)) {
					startToShowDialog(0);
					return;
				} else if (!SomeUtil.isDefVehiOBDBinded()) {
					startToShowDialog(1);
					return;
				}
			}
		}, 1000);
	}

	private void startToShowDialog(int type) {
		mDialogEnter = new Dialog(this, R.style.Dialog_style_dim2);
		View view = View.inflate(this, R.layout.dialog_my_vehicle_condition, null);
		TextView txt1 = (TextView) view.findViewById(R.id.txt_dialog_my_vehicle_condition_heading);
		TextView txt2 = (TextView) view.findViewById(R.id.txt_dialog_my_vehicle_condition_subheading);
		ImageView ivYes = (ImageView) view.findViewById(R.id.iv_dialog_my_vehicle_condition_yes);
		ImageView ivNo = (ImageView) view.findViewById(R.id.iv_dialog_my_vehicle_condition_no);

		if (type == 0) {
			txt1.setText(mResources.getString(R.string.myVehicleCondition_heading_no_vehicle));
			txt2.setText(mResources.getString(R.string.myVehicleCondition_subheading_no_vehicle));
			ivYes.setOnClickListener(new DialogOnClickListener(0));
			ivNo.setOnClickListener(new DialogOnClickListener(0));
		} else if (type == 1) {
			txt1.setText(mResources.getString(R.string.myVehicleCondition_heading_no_obd));
			txt2.setText(mResources.getString(R.string.myVehicleCondition_subheading_no_obd));
			ivYes.setOnClickListener(new DialogOnClickListener(1));
			ivNo.setOnClickListener(new DialogOnClickListener(1));
		}

		mDialogEnter.setContentView(view);
		mDialogEnter.setCanceledOnTouchOutside(true);
		// 当前方法是进入Activity启动1秒后执行，如果不加下面判断，做如下操作：秒进秒退,会BadTokenException
		if (SomeUtil.isActivityRunning(getApplicationContext(), MyVehicleConditionActivity.class)) {
			mDialogEnter.show();
		}
	}

	private class DialogOnClickListener implements OnClickListener {
		private int mType = -1;

		private DialogOnClickListener(int type) {
			mType = type;
		}

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.iv_dialog_my_vehicle_condition_yes:
				if (mType == 0) {
					startActivity(new Intent(getApplicationContext(), AddBindCarActivity.class));
				} else if (mType == 1) {
					startActivity(new Intent(getApplicationContext(), ConnectOBDDialogActivity.class));
				}
				mDialogEnter.cancel();
				break;
			case R.id.iv_dialog_my_vehicle_condition_no:
				mDialogEnter.cancel();
				break;
			}
		}
	};

	private class DJItemAdapter extends AbsViewHolderAdapter<DrivingJournalItem> {

		public DJItemAdapter(Context context, List<DrivingJournalItem> data, int layoutRes) {
			super(context, data, layoutRes);
		}

		@Override
		protected void setData(int pos, View convertView, final DrivingJournalItem itemData) {
			String startDate = getFmtDate(itemData.getStartTime());
			String endDate = getFmtDate(itemData.getEndTime());
			setTxtContent(convertView, R.id.txt_driving_journal_item_startTime, getTime(startDate));
			setTxtContent(convertView, R.id.txt_driving_journal_item_startDay, SomeUtil.getDay(startDate));
			setTxtContent(convertView, R.id.txt_driving_journal_item_startMonth, SomeUtil.getMonth(startDate));
			setTxtContent(convertView, R.id.txt_driving_journal_item_endTime, getTime(endDate));
			setTxtContent(convertView, R.id.txt_driving_journal_item_endDay, SomeUtil.getDay(endDate));
			setTxtContent(convertView, R.id.txt_driving_journal_item_endMonth, SomeUtil.getMonth(endDate));
			setTxtContent(convertView, R.id.txt_driving_journal_item_startAddr,
					getAddr(itemData.getStartPlace()));
			setTxtContent(convertView, R.id.txt_driving_journal_item_endAddr, getAddr(itemData.getEndPlace()));
			setTxtContent(convertView, R.id.txt_driving_journal_item_distance,
					SomeUtil.doubleToString(itemData.getDistance()) + DISTANCE_UNIT);
			setTxtContent(convertView, R.id.txt_driving_journal_item_cost,
					SomeUtil.doubleToString(itemData.getTotalOilMoney()) + "");
			ImageView ivSpan = getViewFromHolder(convertView, R.id.iv_driving_journal_item_span);
			ImageView ivEdit = getViewFromHolder(convertView, R.id.iv_driving_journal_item_edit);
			ivEdit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getApplicationContext(), VehicleConditionAdjustActivity.class);
					intent.putExtra(VehicleConditionAdjustActivity.EXTRA_DJITEM, itemData);
					startActivity(intent);
				}
			});
			if (pos == mSelectedIndex) {
				ivSpan.setImageResource(R.drawable.icon_driving_journal_item_route_light);
				showJournal(itemData.getPlaceNotes());
			} else {
				ivSpan.setImageResource(R.drawable.icon_driving_journal_item_route_dim);
			}
		}

		private String getAddr(String srcAddr) {
			return TextUtils.isEmpty(srcAddr) ? mResources.getString(R.string.myVehicleCondition_no_address)
					: srcAddr;
		}

		private String getFmtDate(long time) {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(time));
		}

		private String getTime(String strFmt) {
			return strFmt.substring(strFmt.lastIndexOf(" ") + 1);
		}

		private void setTxtContent(View convertView, int resId, String content) {
			TextView textView = getViewFromHolder(convertView, resId);
			textView.setText(content);
		}

		private void showJournal(String strSamplePoints) {
			GeoPoint[] result = SomeUtil.getSamplePointArray(strSamplePoints);
			if (result != null) {
				drawRoute(result, mDjScaleRate);
			}
		}
	}

	private void initTopViews() {
		RelativeLayout rlBack = (RelativeLayout) findViewById(R.id.rl_myVehicleCondition_back);
		LinearLayout llCurrCondition = (LinearLayout) findViewById(R.id.ll_myVehicleCondition_curr_condtion);
		LinearLayout llDrivingJournal = (LinearLayout) findViewById(R.id.ll_myVehicleCondition_driving_journal);
		mLlOilPrice = (LinearLayout) findViewById(R.id.ll_myVehicleCondition_oil_price);
		mTopTxtOilPrice = (TextView) findViewById(R.id.txt_myVehicleCondition_oil_price);

		mTxtSubtitles = new TextView[2];
		mTxtSubtitles[0] = (TextView) findViewById(R.id.txt_myVehicleCondition_curr_condtion);
		mTxtSubtitles[1] = (TextView) findViewById(R.id.txt_myVehicleCondition_driving_journal);

		rlBack.setOnClickListener(mOnClickListener);
		mLlOilPrice.setOnClickListener(mOnClickListener);
		llCurrCondition.setOnClickListener(mOnClickListener);
		llDrivingJournal.setOnClickListener(mOnClickListener);

		mTopTxtOilPrice.setText(String.format(
				mResources.getString(R.string.title_myVehicleCondition_oil_price), mDefOilPrice));
	}

	private void initCCViews() {
		mCcHSDatas = (HorizontalScrollView) findViewById(R.id.ll_curr_condtion_datas);
		mCcTxtVoltage = (TextView) findViewById(R.id.txt_curr_condition_battery_voltage);
		mCcTxtTemperature = (TextView) findViewById(R.id.txt_curr_condition_tank_temperature);
		mCcTxtAvgOilWear = (TextView) findViewById(R.id.txt_curr_condition_oil_wear);
		mCcTxtDistance = (TextView) findViewById(R.id.txt_curr_condition_distance);
		mCcTxtTime = (TextView) findViewById(R.id.txt_curr_condition_time);
		mCcFlDTCHead = (FrameLayout) findViewById(R.id.fl_curr_condtion_dtc_head);
		mCcFlDTC = (FrameLayout) findViewById(R.id.fl_curr_condtion_dtc);
		mCcIvNoDtc = (ImageView) findViewById(R.id.iv_curr_condtion_no_dtc);
		mCcIvDtc1 = (ImageView) findViewById(R.id.iv_curr_condtion_dtc1);
		mCcIvDtc2 = (ImageView) findViewById(R.id.iv_curr_condtion_dtc2);
		mCcIvRefresh = (ImageView) findViewById(R.id.iv_curr_condtion_refresh);
		mCcIvRefresh.setOnClickListener(mOnClickListener);
		mCcFlDTCHead.setOnClickListener(mOnClickListener);

		mCcHSDatas.post(new Runnable() {
			@Override
			public void run() {
				mCcTxtVoltage.setWidth((int) (mScreenWidth / CC_DATA_WIDITH_DIVIDER));
				mCcTxtTemperature.setWidth((int) (mScreenWidth / CC_DATA_WIDITH_DIVIDER));
				mCcTxtAvgOilWear.setWidth((int) (mScreenWidth / CC_DATA_WIDITH_DIVIDER));
				mCcTxtDistance.setWidth((int) (mScreenWidth / CC_DATA_WIDITH_DIVIDER));
				mCcTxtTime.setWidth((int) (mScreenWidth / CC_DATA_WIDITH_DIVIDER));
			}
		});

		initCCDatas();
	}

	private void initDJViews() {
		mDjListView = (ListView) findViewById(R.id.lv_driving_journal);
		mDJItemAdapter = new DJItemAdapter(this, new ArrayList<DrivingJournalItem>(),
				R.layout.driving_journal_item);
		mDjListView.setAdapter(mDJItemAdapter);
		mDjListView.setOnItemClickListener(mDJItemClickListener);

		mDjCalendarLayout = (DrivingJournalCalendarLayout) findViewById(R.id.djcl_driving_journal_calendar);
		mDjCalendarLayout.setDJCalendarListener(this);

		mDjPullLayout = (DrivingJournalPullLayout) findViewById(R.id.djpl_journal_pull_layout);
		mDjTxtNow = (TextView) findViewById(R.id.text_driving_journal_now);
		mDjTxtDistance = (TextView) findViewById(R.id.txt_driving_distance);
		mDjTxtTime = (TextView) findViewById(R.id.txt_driving_time);
		mDjTxtOilWear = (TextView) findViewById(R.id.txt_driving_oil_wear);
		mDjTxtOilCost = (TextView) findViewById(R.id.txt_driving_oil_cost);

		mDjIvCalendars = new ImageView[3];
		mDjIvCalendars[0] = (ImageView) findViewById(R.id.imgView_driving_journal_calendar_day);
		mDjIvCalendars[1] = (ImageView) findViewById(R.id.imgView_driving_journal_calendar_week);
		mDjIvCalendars[2] = (ImageView) findViewById(R.id.imgView_driving_journal_calendar_month);

		for (ImageView imageView : mDjIvCalendars) {
			imageView.setOnClickListener(mOnClickListener);
		}
	}

	private OnItemClickListener mDJItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			mSelectedIndex = position;
			mDJItemAdapter.notifyDataSetChanged();
			dependToCollapse(position);
		}

		private void dependToCollapse(int pos) {
			if (mDjPullLayout.getStatus() == DrivingJournalPullLayout.PullLayoutStatus.CALENDAR_COLLAPSED) {
				mDjPullLayout.collapseDatas();
			}
		}
	};

	private class AddrSearchListener implements MKSearchListener {
		@Override
		public void onGetAddrResult(MKAddrInfo mkAddrInfo, int error) {
			if (error != 0)
				return;
			String strGeoPoint = SomeUtil.getStrGeoPoint(mkAddrInfo.geoPt);
			String strAddr = mkAddrInfo.strAddr;
			if (strAddr == null || strGeoPoint == null) {
				return;
			}
			DrivingJournalItem djItem = mAddrUpdateList.get(mUpdateIndex);
			if (mUpdateIndex < mStartAddrCount) {
				mDJDatabase.updateAdrr(djItem.getAppDriveLogId(), SomeUtil.TYPE_SA, strAddr);
			} else {
				mDJDatabase.updateAdrr(djItem.getAppDriveLogId(), SomeUtil.TYPE_EA, strAddr);
			}
			if (++mUpdateIndex < mAddrUpdateList.size()) {
				reverseAddr(mAddrUpdateList.get(mUpdateIndex));
			}
			if (mUpdateIndex == mAddrUpdateList.size()) {
				djUpdate();
			}
		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
		}

		@Override
		public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
		}

		@Override
		public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1, int arg2) {
		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
		}
	}

	private void showDTC() {
		mCcFlDTC.setVisibility(View.VISIBLE);
		mCcIvNoDtc.setVisibility(View.GONE);
		startDTCAnim();
	}

	private void hideDtc() {
		mCcFlDTC.setVisibility(View.GONE);
		mCcIvNoDtc.setVisibility(View.VISIBLE);
		stopDTCAnim();
	}

	private void startDTCAnim() {
		if (mDTCAnim1 == null) {
			mDTCAnim1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.dtc_shinning);
		}
		mCcIvDtc1.startAnimation(mDTCAnim1);

		if (mDTCAnim2 == null) {
			mDTCAnim2 = (AnimationDrawable) mCcIvDtc2.getDrawable();
		}
		mDTCAnim2.start();
	}

	private void stopDTCAnim() {
		if (mDTCAnim1 != null) {
			mDTCAnim1.cancel();
		}
		if (mDTCAnim2 != null) {
			mDTCAnim2.stop();
		}
	}

	private void readDTC() {
		if (mReadDTCTask == null || AsyncTask.Status.FINISHED.equals(mReadDTCTask.getStatus())) {
			mReadDTCTask = new ReadDTCTask();
			mReadDTCTask.execute();
		}
	}

	private void pullRTDDatas() {
		Intent intent = new Intent();
		intent.setAction(TongGouService.TONGGOU_ACTION_READ_CURRENT_RTD_CONDITION);
		sendBroadcast(intent);
	}

	private class ReadDTCTask extends AsyncTask<Void, Void, List<CarCondition>> {
		@Override
		protected List<CarCondition> doInBackground(Void... params) {
			String vehicleId = TongGouApplication.getInstance().getDefaultVehicleId();
			return SaveDB.getSaveDB(getApplicationContext()).getAllCarConditons(currentUsername, vehicleId);
		}

		@Override
		protected void onPostExecute(List<CarCondition> result) {
			if (SomeUtil.isActivityRunning(getApplicationContext(), MyVehicleConditionActivity.class)) {
				if (result != null && result.size() > 0) {
					showDTC();
				} else {
					hideDtc();
				}
			}
		}
	}

	@Override
	protected void onResume() {
		if (mMapView != null) {
			mMapView.onResume();
		}
		if (mCurrType == TYPE_CC) {
			readDTC();
		} else {
			djUpdate();
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (mMapView != null) {
			mMapView.onPause();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (mLocationClient != null) {
			mLocationClient.stop();
		}
		if (mMKSearch != null) {
			mMKSearch.destory();
		}
		if (mMapView != null) {
			mMapView.destroy();
		}
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	@Override
	public void onDateSelected() {
		mDjPullLayout.collapseDatas();
		djUpdate();
	}
}
