package com.tonggou.gsm.andclient.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MKMapStatus;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.Constants;

public abstract class AbsMapFragment extends BaseFragment implements MKMapViewListener {
	BMapManager mBMapManager;
	SupportMapFragment mMapFragment;
	MapView mMapView;
	MapController mMapController;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mBMapManager = App.getInstance().initBMapManager();
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
		mMapView.setBuiltInZoomControls(false);
		mMapView.regMapViewListener(mBMapManager, this);
		mMapController = mMapView.getController();
		mMapController.setMapStatus(newMapStatusWithGeoPointAndZoom(Constants.GEO_DEFAULT, 14));
		mMapController.enableClick(false);
		mMapController.setZoomWithTouchEventCenterEnabled(false);
		mMapController.setZoomGesturesEnabled(false);
		mMapController.setRotateWithTouchEventCenterEnabled(false);
		mMapController.setRotationGesturesEnabled(false);
		mMapController.setScrollGesturesEnabled(false);
	}
	
	void onMapCreated(final MapView mapView, Bundle savedInstance) {
		
	}
	
	private MKMapStatus newMapStatusWithGeoPointAndZoom(GeoPoint p, float zoom) {
        MKMapStatus status = new MKMapStatus();
        status.targetGeo = p;
        status.zoom = zoom;
        return status;
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
	public void zoomToSpanAndMoveToCenter(final int spanLatE6, final int spanLngE6, final GeoPoint centerPoint) {
		mMapController.zoomToSpan(spanLatE6, spanLngE6);
		mMapController.setCenter(centerPoint);
	}
	
	public MapView getMapView() {
		return mMapView;
	}
	
	abstract int getLayoutRes();
	
	abstract int getMapFragmentId();
	
	@Override
	public void onDestroyView() {
		App.getInstance().releaseBMapManager();
		super.onDestroyView();
	}

	@Override
	public void onClickMapPoi(MapPoi arg0) {
	}

	@Override
	public void onGetCurrentMap(Bitmap arg0) {
	}

	@Override
	public void onMapAnimationFinish() {
	}

	@Override
	public void onMapLoadFinish() {
	}

	@Override
	public void onMapMoveFinish() {
	}
	
	
}
