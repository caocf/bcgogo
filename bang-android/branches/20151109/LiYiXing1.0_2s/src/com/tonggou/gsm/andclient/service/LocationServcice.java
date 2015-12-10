package com.tonggou.gsm.andclient.service;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.tonggou.gsm.andclient.App;

/**
 * 定位服务类
 * @author lwz
 *
 */
public class LocationServcice {

	public final static String TAG = "LocationServcice";

	public static final String DEFAULT_COOR_TYPE = "bd09ll";
	public static final int DEFAULT_SCAN_SPAN = 5000;

	private static LocationServcice mInstance;
	private ConcurrentLinkedQueue<OnLocationReceiveListener> mListeners;
	private LocationClient mLocationClient;

	/**
	 * 定位监听
	 * @author lwz
	 *
	 */
	public static interface OnLocationReceiveListener{

		/**
		 * 接收到定位信息
		 * @param location
		 */
		public void onReceiveLocation(BDLocation location);
	}

	private synchronized static LocationServcice getInstance() {
		if( mInstance == null ) {
			mInstance = new LocationServcice();
		}
		return mInstance;
	}

	private LocationServcice() {
		mListeners = new ConcurrentLinkedQueue<OnLocationReceiveListener>();
		initLocationClient();
	}

	private void initLocationClient() {
		mLocationClient = new LocationClient(App.getInstance());
		mLocationClient.registerLocationListener(mLocationListener);
		LocationClientOption option = new LocationClientOption();
		option.setCoorType(DEFAULT_COOR_TYPE);
		option.setOpenGps(true);
		option.setAddrType("all");
		option.disableCache(true);
		option.setPriority(LocationClientOption.NetWorkFirst);
		option.setScanSpan(DEFAULT_SCAN_SPAN);
		mLocationClient.setLocOption(option);
	}

	private BDLocationListener mLocationListener = new BDLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if( location == null ) {
				return;
			}
			int type = location.getLocType();
			App.debug(TAG, location.getLongitude() + "  " + location.getLatitude() + "  " + type);
			if (type == BDLocation.TypeGpsLocation || type == BDLocation.TypeNetWorkLocation
					|| type == BDLocation.TypeOffLineLocation) {
				for( OnLocationReceiveListener listener : mListeners ) {
					listener.onReceiveLocation(location);
				}
			}
		}

	};

	/**
	 * 注册监听自动请求定位
	 * @param listener
	 */
	public static void registerListener(OnLocationReceiveListener listener) {
		getInstance().mListeners.add(listener);
		App.debug(TAG, "add listener " + String.valueOf(listener));
		mInstance.requestLocation();
	}

	/**
	 * 注销监听
	 * @param listener
	 */
	public static void unregisterListener(OnLocationReceiveListener listener) {
		getInstance().mListeners.remove(listener);
		App.debug(TAG, "remove listener " + String.valueOf(listener));
		if( mInstance.mListeners.isEmpty() ) {
			release();
		}
	}

	private static void release() {
		App.debug(TAG, "release()");
		if( mInstance == null) {
			return;
		}
		mInstance.mListeners.clear();
		mInstance.stopLocation();
		mInstance = null;
	}

	/**
	 * 请求定位信息
	 */
	protected void requestLocation() {
		App.debug(TAG, "requestLocation()");
		if (mLocationClient == null) {
			initLocationClient();
		}
		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
		}
		mLocationClient.requestLocation();
	}

	/**
	 * 停止定位
	 */
	protected void stopLocation() {
		if( mLocationClient == null ) {
			return;
		}
		if (mLocationClient.isStarted()) {
			mLocationClient.stop();
		}
		mLocationClient.unRegisterLocationListener(mLocationListener);
		mLocationClient.setLocOption(null);
		mLocationClient = null;
	}
}