package com.tonggou.andclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviPara;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.mapapi.utils.CoordinateConvert;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.tonggou.andclient.parse.JuHeDataManager;
import com.tonggou.andclient.vo.GasStation;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class GasStationMapActivity extends GasStationBaseActivity {
	public static final String TAG = "GasStationMapActivity";
	public static final String TAG2 = "JuHeDataManager";
	private MapView mMapView;
	private RelativeLayout mrlCovering;
	private LinearLayout mllNoNetWork;
	private LinearLayout mLLPop;
	private RelativeLayout mrlBack;
	private RelativeLayout mrlShowDetail;
	private TextView mtvTitle;
	private TextView mTVStationName;
	private TextView mTVStationPrice;
	private boolean isRoutePlanOn;
	private boolean isBackPressed;

	private List<Overlay> mMapViewOverlays;
	private LocationClient mLocationClient;
	private CustomLocationOverlay mMyLocationOverlay;
	private PopupOverlay mPopupOverlay;
	private MKSearch mMKSearch;
	private RouteOverlay mRouteOverlay;

	private GasStationsTask mGasStationsTask;
	private GasStation mSelectedGasStation;
	private LocationData mCurrLocData;
	private GeoPoint mCurrGeoPoint;
	private GeoPoint mDestGeoPoint;
	private ArrayList<GasStation> mGasStations;
	private SparseArray<GasStation> mGasStationMap;

	private static final String BD09LL = "bd09ll"; // "gcj02"
	private static final int SUCCESS_FROM_GPS = 61;
	private static final int SUCCESS_FROM_NETWORK = 161;
	private static final int PARSING_NETWORK_ERROR = 10001;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PARSING_NETWORK_ERROR:
				mllNoNetWork.setVisibility(View.VISIBLE);
				mrlCovering.setVisibility(View.GONE);
				// showToast("网络连接错误");
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBMap(mMKGeneralListener);
		setContentView(R.layout.gas_station_map);
		initViews();
		if (!isNetworkAvailable()) {
			mllNoNetWork.setVisibility(View.VISIBLE);
			mrlCovering.setVisibility(View.GONE);
			return;
		}
		initMapController(mMapView);
		initMyLocationOverlay();
		initaPopupOverlay();
		initMKSearch();
		initLocationClient();
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isAvailable()) {
			return true;
		}
		return false;
	}

	private void initLocationClient() {
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(mLocationListener);
		LocationClientOption clientOption = new LocationClientOption();
		clientOption.setOpenGps(true);
		clientOption.setAddrType("all");
		clientOption.setCoorType(BD09LL);
		clientOption.disableCache(true);
		clientOption.setPriority(LocationClientOption.NetWorkFirst);
		mLocationClient.setLocOption(clientOption);
		mLocationClient.start();
	}

	private BDLocationListener mLocationListener = new BDLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation location) {
			int type = location.getLocType();
			if (location != null
					&& (type == SUCCESS_FROM_GPS || type == SUCCESS_FROM_NETWORK)) {
				mCurrLocData = getLocData(location);

				mGasStationsTask = new GasStationsTask();
				mGasStationsTask.execute(mCurrLocData);
			}
		}

		@Override
		public void onReceivePoi(BDLocation location) {
		}
	};

	private class GasStationsTask extends
			AsyncTask<LocationData, Void, ArrayList<GasStation>> {
		@Override
		protected ArrayList<GasStation> doInBackground(LocationData... params) {
			ArrayList<GasStation> allGasStations = new ArrayList<GasStation>();
			JuHeDataManager dataManager = JuHeDataManager.getInstance();
			LocationData locData = params[0];
			dataManager.initRequest();
			String baseUri = dataManager.getBaseUri(locData);
			int i = 1;
			while (true) {
				if (isCancelled() || isBackPressed) {
					Log.d(TAG, "Has cancelled");
					return null;
				}
				int page = i++;
				try {
					ArrayList<GasStation> gasStations = dataManager
							.getGasStations(baseUri, page);
					if (gasStations != null && gasStations.size() == 0) {
						Log.d(TAG2, "ending page：" + page);
						break;
					}
					allGasStations.addAll(gasStations);
				} catch (ClientProtocolException e) {
					Log.e(TAG2, "page：" + page, e);
					return null;
				} catch (IOException e) {
					handler.sendEmptyMessage(PARSING_NETWORK_ERROR);
					Log.e(TAG2, "page：" + page, e);
					return null;
				} catch (JSONException e) {
					Log.e(TAG2, "page：" + page, e);
					return null;
				}
			}
			return allGasStations;
		}

		@Override
		protected void onPostExecute(ArrayList<GasStation> result) {
			if (result != null && result.size() > 0) {
				mGasStations = new ArrayList<GasStation>();
				mGasStations.addAll(result);
				mMapView.setVisibility(View.VISIBLE);
				mrlCovering.setVisibility(View.GONE);
				displayMylocationOnMap(mCurrLocData);
				displayGasStationsOnMap(result, true);
			}
		}

	}

	private LocationData getLocData(BDLocation location) {
		LocationData locData = new LocationData();
		locData.latitude = location.getLatitude();
		locData.longitude = location.getLongitude();
		locData.accuracy = location.getRadius();
		locData.direction = location.getDerect();

		return locData;
	}

	private void initMyLocationOverlay() {
		mMyLocationOverlay = new CustomLocationOverlay(mMapView);
		mMyLocationOverlay.enableCompass();
		mMapViewOverlays.add(mMyLocationOverlay);
	}

	private void initaPopupOverlay() {
		mPopupOverlay = new PopupOverlay(mMapView, mPopupClickListener);
		mLLPop = (LinearLayout) View.inflate(this,
				R.layout.gas_station_popupoverlay, null);
		mTVStationName = (TextView) mLLPop
				.findViewById(R.id.tv_gas_staion_name);
		mTVStationPrice = (TextView) mLLPop
				.findViewById(R.id.tv_gas_staion_price);
	}

	private void initMKSearch() {
		mMKSearch = new MKSearch();
		mMKSearch.init(mBMapManager, mMKSearchListener);
	}

	private void initViews() {
		mMapView = (MapView) findViewById(R.id.mv_gas_staion_map);
		mMapView.regMapViewListener(mBMapManager, mMKMapViewListener);
		mMapViewOverlays = mMapView.getOverlays();

		mrlCovering = (RelativeLayout) findViewById(R.id.rl_gas_staion_map_covering);
		mllNoNetWork = (LinearLayout) findViewById(R.id.ll_gas_staion_map_no_network);

		mrlBack = (RelativeLayout) findViewById(R.id.rl_gas_staion_map_back);
		mrlShowDetail = (RelativeLayout) findViewById(R.id.rl_gas_staion_map_detail);
		mtvTitle = (TextView) findViewById(R.id.tv_gas_staion_map_title);

		mrlBack.setOnClickListener(mOnClickListener);
		mrlShowDetail.setOnClickListener(mOnClickListener);
	}

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.rl_gas_staion_map_back:
				onBackPressed();
				break;
			case R.id.rl_gas_staion_map_detail:
				startDetailActivity();
				break;
			}
		}
	};

	public void onBackPressed() {
		if (isRoutePlanOn) {
			refreshInitOverlays();
		} else {
			synchronized (this) {
				isBackPressed = true;
				if (mGasStationsTask != null) {
					mGasStationsTask.cancel(true);
				}
				GasStationMapActivity.this.finish();
			}
		}
	};

	private void startDetailActivity() {
		if (mPopupOverlay != null) {
			mPopupOverlay.hidePop();
		}
		Intent intent = new Intent();
		intent.putParcelableArrayListExtra(GAS_STATIONS, mGasStations);
		intent.setAction(GasStationDetailActivity.ACTION_GAS_STATION_DETAIL);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			final GasStation gasStation = data.getParcelableExtra(GAS_STATION);
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					prepareToShowPopupOverlay(gasStation, true);
					mMapController.animateTo(new GeoPoint((int) (gasStation
							.getLat() * 1e6), (int) (gasStation.getLon() * 1e6)));
					mSelectedGasStation = gasStation;
				}
			}, 1000);

		}
	}

	private class CustomLocationOverlay extends MyLocationOverlay {

		public CustomLocationOverlay(MapView mapView) {
			super(mapView);
		}

		@Override
		protected boolean dispatchTap() {
			// TODO
			showToast("dispatchTap");
			return super.dispatchTap();
		}

	}

	private class GasStationOverlay extends ItemizedOverlay<OverlayItem> {

		public GasStationOverlay(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
		}

		protected boolean onTap(int index) {
			// 在此处理item点击事件
			mSelectedGasStation = mGasStationMap.get(index);
			showPopupOverlay(mSelectedGasStation);
			return true;
		}

		public boolean onTap(GeoPoint pt, MapView mapView) {
			// 在此处理MapView的点击事件
			if (mPopupOverlay != null) {
				mPopupOverlay.hidePop();
			}
			return false;
		}

	}

	private PopupClickListener mPopupClickListener = new PopupClickListener() {
		@Override
		public void onClickedPopup(int index) {
			Log.d(TAG, "popup clicked：" + mSelectedGasStation.toString());
			mPopupOverlay.hidePop();
			showAsureDialog();
		}
	};

	private void displayMylocationOnMap(LocationData locData) {
		if (locData != null) {
			mCurrGeoPoint = new GeoPoint((int) (locData.latitude * 1e6),
					(int) (locData.longitude * 1e6));
			mMapController.animateTo(mCurrGeoPoint);
			mMyLocationOverlay.setData(locData);
			mMapView.refresh();
		}
	}

	private void displayMylocationOnMap() {
		if (mCurrGeoPoint != null) {
			mMapViewOverlays.add(mMyLocationOverlay);
		}
	}

	private void displayGasStationsOnMap(ArrayList<GasStation> gasStations,
			boolean changeToBD) {
		if (gasStations != null) {
			GasStationOverlay gasStationOverlay = new GasStationOverlay(
					mResources.getDrawable(R.drawable.icon_gas_station),
					mMapView);
			mMapViewOverlays.add(gasStationOverlay);
			mGasStationMap = new SparseArray<GasStation>();
			int i = 0;
			for (GasStation gasStation : gasStations) {
				OverlayItem overlayItem = new OverlayItem(getGeoPoint(
						gasStation, changeToBD), gasStation.getId() + "", "");
				gasStationOverlay.addItem(overlayItem);
				mGasStationMap.put(i++, gasStation);
			}
			mMapView.refresh();
		}
	}

	private void refreshInitOverlays() {
		mMapViewOverlays.clear();
		mMapController.setZoom(ZOOM_LEVEL);
		mrlShowDetail.setVisibility(View.VISIBLE);
		mtvTitle.setText(mResources.getText(R.string.gas_station_map));
		displayMylocationOnMap();
		displayGasStationsOnMap(mGasStations, false);
		mMapController.setCenter(mCurrGeoPoint);
		isRoutePlanOn = false;
	}

	private void layoutOnRoutePlanOn() {
		mMapViewOverlays.clear();
		mrlShowDetail.setVisibility(View.GONE);
		mtvTitle.setText(mResources.getText(R.string.gas_station_map_route));
		isRoutePlanOn = true;
	}

	private GeoPoint getGeoPoint(GasStation gasStation, boolean changeToBD) {
		GeoPoint geoPoint;
		GeoPoint gcjGeoPoint = new GeoPoint((int) (gasStation.getLat() * 1e6),
				(int) (gasStation.getLon() * 1e6));
		if (changeToBD) {
			GeoPoint bdGeoPoint = CoordinateConvert.fromGcjToBaidu(gcjGeoPoint);
			gasStation.setLat(((double) bdGeoPoint.getLatitudeE6()) / 1e6);
			gasStation.setLon(((double) bdGeoPoint.getLongitudeE6()) / 1e6);
			geoPoint = bdGeoPoint;
		} else {
			geoPoint = gcjGeoPoint;
		}
		return geoPoint;
	}

	private void showPopupOverlay(GasStation gasStation) {
		GeoPoint geoPoint = prepareToShowPopupOverlay(gasStation, false);
		mPopupOverlay.showPopup(mLLPop, geoPoint, 32);
	}

	private GeoPoint prepareToShowPopupOverlay(GasStation gasStation,
			boolean isDest) {
		mTVStationName.setText(gasStation.getName());
		mTVStationPrice.setText(String.format(
				mResources.getString(R.string.gas_station_detail_item_price2),
				gasStation.getE0() + "", gasStation.getE93() + "",
				gasStation.getE97() + ""));
		GeoPoint geoPoint = new GeoPoint((int) (gasStation.getLat() * 1e6),
				(int) (gasStation.getLon() * 1e6));
		if (isDest) {
			mDestGeoPoint = geoPoint;
		}
		return geoPoint;
	}

	private MKMapViewListener mMKMapViewListener = new MKMapViewListener() {
		@Override
		public void onMapMoveFinish() {
			Log.d(TAG, "onMapMoveFinish");
		}

		@Override
		public void onMapLoadFinish() {
			Log.d(TAG, "onMapLoadFinish");
		}

		@Override
		public void onMapAnimationFinish() {
			Log.d(TAG, "onMapAnimationFinish");
			if (mDestGeoPoint != null && mPopupOverlay != null) {
				mPopupOverlay.hidePop();
				mPopupOverlay.showPopup(mLLPop, mDestGeoPoint, 32);
			}
		}

		@Override
		public void onGetCurrentMap(Bitmap arg0) {
			Log.d(TAG, "onGetCurrentMap");
		}

		@Override
		public void onClickMapPoi(MapPoi arg0) {
			Log.d(TAG, "onClickMapPoi");
		}
	};

	private void startNavigation() {
		GeoPoint endPoint = new GeoPoint(
				(int) (mSelectedGasStation.getLat() * 1e6),
				(int) (mSelectedGasStation.getLon() * 1e6));
		NaviPara naviPara = new NaviPara();
		naviPara.startPoint = mCurrGeoPoint;
		naviPara.endPoint = endPoint;
		naviPara.startName = "从这里开始";
		naviPara.endName = "到这里结束";
		try {
			BaiduMapNavigation.openBaiduMapNavi(naviPara,
					GasStationMapActivity.this);
		} catch (BaiduMapAppNotSupportNaviException e) {
			showNotSupportDialog(endPoint);
		}
	}

	private void showAsureDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("确认打开导航吗？");
		builder.setTitle("友情提示");
		builder.setPositiveButton("确认",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						startNavigation();
					}
				});

		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	private void showNotSupportDialog(final GeoPoint endPoint) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
		builder.setTitle("百度地图");
		builder.setPositiveButton("确认",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						BaiduMapNavigation
								.GetLatestBaiduMapApp(GasStationMapActivity.this);
					}
				});

		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						startDrivingRoutePlan(endPoint);
					}
				});

		builder.create().show();
	}

	private void startDrivingRoutePlan(GeoPoint endPoint) {
		MKPlanNode stNode = new MKPlanNode();
		stNode.pt = mCurrGeoPoint;
		MKPlanNode enNode = new MKPlanNode();
		enNode.pt = endPoint;

		mMKSearch.drivingSearch("开始", stNode, "结束", enNode);
	}

	private MKSearchListener mMKSearchListener = new MKSearchListener() {

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult res, int error) {
			if (error == MKEvent.ERROR_ROUTE_ADDR) {
				return;
			}
			if (error != 0 || res == null) {
				showToast("抱歉，未找到结果");
				return;
			}

			mRouteOverlay = new RouteOverlay(GasStationMapActivity.this,
					mMapView);
			mRouteOverlay.setData(res.getPlan(0).getRoute(0));
			layoutOnRoutePlanOn();
			mMapViewOverlays.add(mRouteOverlay);
			mMapView.refresh();
			mMapController.zoomToSpan(mRouteOverlay.getLatSpanE6(),
					mRouteOverlay.getLonSpanE6());
			mMapController.setCenter(res.getStart().pt);
		}

		@Override
		public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
		}

		@Override
		public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
		}

		@Override
		public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
				int arg2) {
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
	};

	@Override
	protected void onPause() {
		if (mMapView != null) {
			mMapView.onPause();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (mMapView != null) {
			mMapView.onResume();
			if (mCurrGeoPoint != null) {
				mMapController.setCenter(mCurrGeoPoint);
			}
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (mMapView != null) {
			mMapView.destroy();
		}
		if (mBMapManager != null) {
			mBMapManager = null;
		}
		super.onDestroy();
	}

}
