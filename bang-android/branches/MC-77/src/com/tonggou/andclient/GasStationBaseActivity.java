package com.tonggou.andclient;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.tonggou.andclient.app.TongGouApplication;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public abstract class GasStationBaseActivity extends BaseActivity {
	protected static final String TAG = "GasStationBaseActivity";
	protected Resources mResources;
	protected Toast mToast;
	protected BMapManager mBMapManager;
	protected MapController mMapController;
	
	public static final String GAS_STATIONS = "GasStations";
	public static final String GAS_STATION = "GasStation";
	public static final float ZOOM_LEVEL = 14;

	protected void showToast(String text) {
		if (mToast == null) {
			mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		mResources = getResources();
	}

	protected void initBMap(MKGeneralListener listener) {
		mBMapManager = new BMapManager(this);
		mBMapManager.init(TongGouApplication.strKey, listener);
	}
	
	protected void initMapController(MapView mapView) {
		mMapController = mapView.getController();
		mMapController.enableClick(true);
		mMapController.setZoom(ZOOM_LEVEL);
		mapView.setBuiltInZoomControls(true);
		mapView.showScaleControl(true);
	}

	protected MKGeneralListener mMKGeneralListener = new MKGeneralListener() {
		@Override
		public void onGetNetworkState(int iError) {
			// 一些网络状态的错误处理回调函数
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				Log.d(TAG, "网络连接错误!");
				//showToast("您的网络出错啦！");
			}
		}

		@Override
		public void onGetPermissionState(int iError) {
			// 授权错误的时候调用的回调函数
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				Log.d(TAG, "API KEY错误!");
			}
		}
	};

	@Override
	protected void onStop() {
		if( mBMapManager != null ) {
			mBMapManager.stop();
		}
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		if( mBMapManager != null ) {
			mBMapManager.destroy();
		}
		super.onDestroy();
	}
}
