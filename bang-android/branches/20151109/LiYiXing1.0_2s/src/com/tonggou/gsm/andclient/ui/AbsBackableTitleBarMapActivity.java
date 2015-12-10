package com.tonggou.gsm.andclient.ui;

import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.Constants;

/**
 * 有返回标题栏的地图
 * @author lwz
 *
 */
public abstract class AbsBackableTitleBarMapActivity extends BackableTitleBarActivity {

	private SupportMapFragment mMapFragment;
	private MapView mMapView;
	private BaiduMap mBaiDuMap;

	@Override
	protected void onCreate(Bundle savedInstance) {
		App.getInstance().InitDituSDK();
		super.onCreate(savedInstance);

		setContentView(getContentView());
		initMapView();

		onMapCreated(mMapView, savedInstance);
	}

	protected abstract int getContentView();

	/**
	 * 初始化地图
	 */
	private void initMapView() {
		mMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(getMapFragmentId());
		mMapView = mMapFragment.getMapView();
		mBaiDuMap = mMapView.getMap();
		mBaiDuMap.getUiSettings().setZoomGesturesEnabled(true);

		mBaiDuMap.setMapStatus(newMapStatusWithGeoPointAndZoom(Constants.GEO_DEFAULT, 14));
	}

	protected abstract int getMapFragmentId();

	private MapStatusUpdate newMapStatusWithGeoPointAndZoom(LatLng p, float zoom) {
		MapStatus mapStatus = new MapStatus
				.Builder()
				.target(p)
				.zoom(zoom)
				.build();
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
		return mapStatusUpdate;
	}

	/**
	 * 缩放地图到能容下指定的经纬度范围,并移动到指定的中心点
	 *
	 * <p>NOTE : 要在地图加载完毕后调用，否则可能会出现黑屏无响应
	 * <br> 要监听地图加载事件，{@link MapView #regMapViewListener(BMapManager, com.baidu.mapapi.map.MKMapViewListener)}
	 * @param spanLatE6 经度范围
	 * @param spanLngE6 纬度范围
	 * @param centerPoint 中点坐标，地图将使用该坐标作为中点
	 */
	public void zoomToSpanAndMoveToCenter(final int spanLatE6, final int spanLngE6, final LatLng centerPoint) {
		mBaiDuMap.setMapStatus(newMapStatusWithGeoPointAndZoom(centerPoint, 14));
	}

	protected abstract void onMapCreated(MapView mapView, Bundle savedInstance);

	protected MapView getMapView() {
		return mMapView;
	}

	protected SupportMapFragment getSupportMapFragment() {
		return mMapFragment;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}