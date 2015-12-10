package com.tonggou.gsm.andclient.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.tonggou.gsm.andclient.Constants;
import com.tonggou.gsm.andclient.bean.LatLngParcel;

public abstract class AbsMapFragment extends BaseFragment implements OnMapLoadedCallback {
	SupportMapFragment mMapFragment;
	MapView mMapView;
	BaiduMap mBaiduMap;
	OverlayManager om;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(getLayoutRes(), container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mMapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(getMapFragmentId());

		initMapView();
		onMapCreated(mMapView, savedInstanceState);
	}

	/**
	 * 初始化地图
	 */
	private void initMapView() {
		mMapView = mMapFragment.getMapView();
		mMapView.showZoomControls(false);
		mMapView.showScaleControl(false);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.animateMapStatus(newMapStatusWithGeoPointAndZoom(Constants.GEO_DEFAULT, 16));
		mBaiduMap.getUiSettings().setAllGesturesEnabled(false);
		mBaiduMap.setOnMapLoadedCallback(this);
	}

	void onMapCreated(final MapView mapView, Bundle savedInstance) {
	}

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
	 * @param span 经度范围
	 * @param span2 纬度范围
	 * @param centerPoint 中点坐标，地图将使用该坐标作为中点
	 */
	public void zoomToSpanAndMoveToCenter(final Double span, final Double span2, final LatLngParcel centerPoint) {
		mBaiduMap.setMapStatus(newMapStatusWithGeoPointAndZoom(centerPoint.getLatLng(), 16));
	}

	public MapView getMapView() {
		return mMapView;
	}

	abstract int getLayoutRes();

	abstract int getMapFragmentId();

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
}