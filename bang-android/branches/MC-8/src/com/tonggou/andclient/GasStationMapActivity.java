package com.tonggou.andclient;

import java.util.ArrayList;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviPara;
import com.baidu.mapapi.utils.CoordinateConvert;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.vo.GasStation;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class GasStationMapActivity extends BaseActivity {
	private Resources mResources;
	private Toast mToast;
	private MapView mMapView;
	private LinearLayout mLLPop;
	private TextView mTVStationName;
	private TextView mTVStationAdd;

	private BMapManager mBMapManager;
	private MapController mMapController;
	private CustomLocationOverlay mMyLocationOverlay;
	private PopupOverlay mPopupOverlay;
	private GasStation mCurrGasStation;
	private ArrayList<GasStation> mGasStations;
	private LocationData mCurrLocData;
	private GasStation mDestGasStation;
	private SparseArray<GasStation> mGasStationMap = new SparseArray<GasStation>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mResources = getResources();
		receiveIntentData();
		initBMap();
		setContentView(R.layout.gas_station_map);
		initViews();
		initMapController();
		initMyLocationOverlay();
		initaPopupOverlay();
		mMapView.post(new Runnable() {
			@Override
			public void run() {
				if (mCurrLocData != null) {
					displayMylocationOnMap(mCurrLocData);
				}
				if (mGasStations != null) {
					displayGasStationsOnMap(mGasStations);
				}
				mMapView.refresh();
			}
		});
		if (mDestGasStation != null) {
			mMapView.postDelayed(new Runnable() {
				@Override
				public void run() {
					GeoPoint destGeoPoint = getBDGeoPoint(mDestGasStation);
					mMapController.animateTo(destGeoPoint);
					mMapController.setCenter(destGeoPoint);
					showPopupOverlay(mDestGasStation);
					mCurrGasStation = mDestGasStation;
				}
			}, 1000);
		}
	}

	private void receiveIntentData() {
		Intent intent = getIntent();
		if (intent != null) {
			mGasStations = intent
					.getParcelableArrayListExtra(GasStationDetailActivity.GAS_STATIONS);
			mCurrLocData = new LocationData();
			mCurrLocData.latitude = intent.getDoubleExtra(
					GasStationDetailActivity.CURR_LAT, 0d);
			mCurrLocData.longitude = intent.getDoubleExtra(
					GasStationDetailActivity.CURR_LON, 0d);
			mCurrLocData.accuracy = intent.getFloatExtra(
					GasStationDetailActivity.CURR_ACCU, 0f);
			mDestGasStation = intent
					.getParcelableExtra(GasStationDetailActivity.DEST_GAS_STATION);
		}
	}

	private void initBMap() {
		mBMapManager = new BMapManager(this);
		mBMapManager.init(TongGouApplication.strKey, mMKGeneralListener);
		mBMapManager.start();
	}

	private void initMapController() {
		mMapController = mMapView.getController();
		mMapController.enableClick(true);
		mMapController.setZoom(15);
		mMapView.setBuiltInZoomControls(true);
		mMapView.showScaleControl(true);
	}

	private void initaPopupOverlay() {
		mPopupOverlay = new PopupOverlay(mMapView, mPopupClickListener);
		mLLPop = (LinearLayout) View.inflate(this,
				R.layout.gas_station_popupoverlay, null);
		mTVStationName = (TextView) mLLPop
				.findViewById(R.id.tv_gas_staion_name);
		mTVStationAdd = (TextView) mLLPop
				.findViewById(R.id.tv_gas_staion_address);
	}

	private void initMyLocationOverlay() {
		mMyLocationOverlay = new CustomLocationOverlay(mMapView);
		mMyLocationOverlay.enableCompass();
		mMapView.getOverlays().add(mMyLocationOverlay);
	}

	private void initViews() {
		mMapView = (MapView) findViewById(R.id.mv_gas_staion_map);

		RelativeLayout rlBack = (RelativeLayout) findViewById(R.id.rl_gas_staion_map_back);
		RelativeLayout rlShowDetail = (RelativeLayout) findViewById(R.id.rl_gas_staion_map_detail);

		rlBack.setOnClickListener(mOnClickListener);
		rlShowDetail.setOnClickListener(mOnClickListener);
	}

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.rl_gas_staion_map_back:
				GasStationMapActivity.this.finish();
				break;
			case R.id.rl_gas_staion_map_detail:
				Intent intent = new Intent(GasStationMapActivity.this,
						GasStationDetailActivity.class);
				startActivity(intent);
				break;
			}
		}
	};

	private MKGeneralListener mMKGeneralListener = new MKGeneralListener() {
		@Override
		public void onGetNetworkState(int iError) {
			// 一些网络状态的错误处理回调函数
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				showToast("您的网络出错啦！");
			}
		}

		@Override
		public void onGetPermissionState(int iError) {
			// 授权错误的时候调用的回调函数
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				showToast("API KEY错误, 请检查！");
			}
		}
	};

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
			mCurrGasStation = mGasStationMap.get(index);
			showPopupOverlay(mCurrGasStation);
			return true;
		}

		public boolean onTap(GeoPoint pt, MapView mapView) {
			// 在此处理MapView的点击事件，当返回 true时
			if (mPopupOverlay != null) {
				mPopupOverlay.hidePop();
			}
			return false;
		}

	}

	private PopupClickListener mPopupClickListener = new PopupClickListener() {
		@Override
		public void onClickedPopup(int index) {
			Log.d("FFF", "点了");
			mPopupOverlay.hidePop();
			startNavigation();
		}
	};

	private void displayMylocationOnMap(LocationData locData) {
		if (locData != null) {
			mMapController.animateTo(new GeoPoint(
					(int) (locData.latitude * 1e6),
					(int) (locData.longitude * 1e6)));
			mMyLocationOverlay.setData(locData);
		}
	}

	private void displayGasStationsOnMap(ArrayList<GasStation> gasStations) {
		if (gasStations != null) {
			GasStationOverlay gasStationOverlay = new GasStationOverlay(
					mResources.getDrawable(R.drawable.icon_gas_station),
					mMapView);
			mMapView.getOverlays().add(gasStationOverlay);
			mGasStationMap.clear();
			int i = 0;
			for (GasStation gasStation : gasStations) {
				OverlayItem overlayItem = new OverlayItem(
						getBDGeoPoint(gasStation), gasStation.getId() + "", "");
				gasStationOverlay.addItem(overlayItem);
				mGasStationMap.put(i++, gasStation);
			}
		}
	}

	private GeoPoint getBDGeoPoint(GasStation gasStation) {
		GeoPoint gcjGeoPoint = new GeoPoint((int) (gasStation.getLat() * 1e6),
				(int) (gasStation.getLon() * 1e6));
		GeoPoint bdGeoPoint = CoordinateConvert.fromGcjToBaidu(gcjGeoPoint);
		gasStation.setLat(((double) bdGeoPoint.getLatitudeE6()) / 1e6);
		gasStation.setLon(((double) bdGeoPoint.getLongitudeE6()) / 1e6);
		return bdGeoPoint;
	}

	private void showPopupOverlay(GasStation gasStation) {
		mTVStationName.setText(gasStation.getName());
		mTVStationAdd.setText(gasStation.getAddress());
		mPopupOverlay.showPopup(mLLPop, new GeoPoint(
				(int) (gasStation.getLat() * 1e6),
				(int) (gasStation.getLon() * 1e6)), 32);
	}

	private void startNavigation() {
		LocationData myLocData = mMyLocationOverlay.getMyLocation();
		GeoPoint startPoint = new GeoPoint((int) (myLocData.latitude * 1e6),
				(int) (myLocData.longitude * 1e6));
		GeoPoint endPoint = new GeoPoint(
				(int) (mCurrGasStation.getLat() * 1e6),
				(int) (mCurrGasStation.getLon() * 1e6));
		NaviPara naviPara = new NaviPara();
		naviPara.startPoint = startPoint;
		naviPara.endPoint = endPoint;
		naviPara.startName = "从这里开始";
		naviPara.endName = "到这里结束";
		try {
			BaiduMapNavigation.openBaiduMapNavi(naviPara,
					GasStationMapActivity.this);
		} catch (BaiduMapAppNotSupportNaviException e) {
			showNotSupportDialog();
		}
	}

	private void showNotSupportDialog() {
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
					}
				});

		builder.create().show();
	}

	private void showToast(String text) {
		if (mToast == null) {
			mToast = Toast.makeText(GasStationMapActivity.this, text,
					Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}

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
