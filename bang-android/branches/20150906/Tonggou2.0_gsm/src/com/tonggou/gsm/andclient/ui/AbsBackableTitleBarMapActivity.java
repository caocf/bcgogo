package com.tonggou.gsm.andclient.ui;

import android.graphics.Bitmap;
import android.os.Bundle;

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

/**
 * 有返回标题栏的地图
 * @author lwz
 *
 */
public abstract class AbsBackableTitleBarMapActivity extends BackableTitleBarActivity implements MKMapViewListener {
	
	protected BMapManager mBMapManager;
	private SupportMapFragment mMapFragment;
	private MapView mMapView;
	private MapController mMapController;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		mBMapManager = App.getInstance().initBMapManager();
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
		mMapView.setBuiltInZoomControls(true);
		mMapView.showScaleControl(true);
		mMapView.regMapViewListener(mBMapManager, this);
		mMapController = mMapView.getController();
		mMapController.setMapStatus(newMapStatusWithGeoPointAndZoom(Constants.GEO_DEFAULT, 14));
		mMapController.setRotateWithTouchEventCenterEnabled(false);
		mMapController.setRotationGesturesEnabled(false);
		mMapController.setOverlookingGesturesEnabled(false);
		mMapController.setZoomWithTouchEventCenterEnabled(false);
	}
	
	protected abstract int getMapFragmentId();
	
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
	
	protected abstract void onMapCreated(MapView mapView, Bundle savedInstance);

	protected MapView getMapView() {
		return mMapView;
	}
	
	protected SupportMapFragment getSupportMapFragment() {
		return mMapFragment;
	}
	
	@Override
	protected void onDestroy() {
		App.getInstance().releaseBMapManager();
		super.onDestroy();
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
