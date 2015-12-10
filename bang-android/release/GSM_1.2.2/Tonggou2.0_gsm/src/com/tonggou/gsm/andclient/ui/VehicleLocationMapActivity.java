package com.tonggou.gsm.andclient.ui;

import android.os.Bundle;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapStatus;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.GeoPointParcel;
import com.tonggou.gsm.andclient.service.LocationServcice;
import com.tonggou.gsm.andclient.service.LocationServcice.OnLocationReceiveListener;
import com.tonggou.gsm.andclient.service.UpdateVehicleLocationBroadcastReceiver;
import com.tonggou.gsm.andclient.service.UpdateVehicleLocationBroadcastReceiver.OnUpdateVehicleLocationListener;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;
import com.tonggou.gsm.andclient.util.BMapUtil;

/**
 * 车辆位置 Activity
 * @author lwz
 *
 */
public class VehicleLocationMapActivity extends BackableTitleBarActivity implements OnLocationReceiveListener, OnUpdateVehicleLocationListener {
	
	/** 车辆位置坐标 */
	public static final String EXTRA_VEHICLE_LCOATION_GEO_POINT = "extra_vehicle_lcoation_geopoint";
	
	private MapView mMapView;
	private MapController mMapController;
	private GeoPointParcel mVehicleLocationGeoPoint;
	private ItemizedOverlay<OverlayItem> mVehicleLocationOverlay;
	private MyLocationOverlay mMyLocationOverlay;
	private boolean isNeedZoomToSpan = true;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		App.getInstance().initBMapManager();
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_map_navigation);
		
		if( !restoreExtras(getIntent()) ) {
			if( !restoreExtras(savedInstance) ) {
				App.showLongToast(getString(R.string.query_vehicle_location_failure));
				finish();
				return;
			}
		}
		LocationServcice.registerListener(this);
		SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map_fragment);
		
		mMapView = mapFragment.getMapView();
		initMapView();
	}
	
	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(R.string.title_vehicle_location);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		UpdateVehicleLocationBroadcastReceiver.register(this, this);
	}
	
	@Override
	protected void onStop() {
		UpdateVehicleLocationBroadcastReceiver.unregister(this);
		super.onStop();
	}
	
	private void initMapView() {
		mMapView.setBuiltInZoomControls(true);
		mMapView.showScaleControl(true);
		mMapController = mMapView.getController();
		mMapController.setMapStatus(newMapStatusWithGeoPointAndZoom(mVehicleLocationGeoPoint, 14));
		mVehicleLocationOverlay = new ItemizedOverlay<OverlayItem>(
				getResources().getDrawable(R.drawable.ic_vehicle_location), mMapView);
		mMapView.getOverlays().add(mVehicleLocationOverlay);  
		showVehicleLocation(mVehicleLocationGeoPoint);
	}
	
	private void showVehicleLocation(GeoPoint vehicleLocation) {
		// 在地图上显示车辆位置图标
		OverlayItem item = new OverlayItem(vehicleLocation, "", "");
		mVehicleLocationOverlay.removeAll();
		mVehicleLocationOverlay.addItem(item);
		mMapView.refresh();
	}
	
	private MKMapStatus newMapStatusWithGeoPointAndZoom(GeoPoint p, float zoom) {
        MKMapStatus status = new MKMapStatus();
        status.targetGeo = p;
        status.zoom = zoom;
        return status;
    }
	
	@Override
	protected boolean restoreExtras(Bundle extra) {
		if( extra == null || !extra.containsKey(EXTRA_VEHICLE_LCOATION_GEO_POINT)) {
			return false;
		}
		mVehicleLocationGeoPoint = extra.getParcelable(EXTRA_VEHICLE_LCOATION_GEO_POINT);
		return true;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(EXTRA_VEHICLE_LCOATION_GEO_POINT,  mVehicleLocationGeoPoint);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onReceiveLocation(BDLocation location) {
		if( mMyLocationOverlay == null ) {
			// 将用户的位置显示到地图上
			mMyLocationOverlay = new MyLocationOverlay(mMapView);  
			mMapView.getOverlays().add(mMyLocationOverlay); 
		}
		LocationData locData = new LocationData();  
		locData.latitude = location.getLatitude();  
		locData.longitude = location.getLongitude();  
		mMyLocationOverlay.setData(locData);  
		mMapView.refresh(); 
		if( isNeedZoomToSpan ) {
			zoomToSpanAndMoveToCenter(location);
			isNeedZoomToSpan = false;
		}
	}
	
	private void zoomToSpanAndMoveToCenter(BDLocation myLocation) {
		GeoPoint[] points = new GeoPoint[]{ mVehicleLocationGeoPoint, BMapUtil.convertGeoPoint(myLocation) };
		Integer[] span = new Integer[2];
		GeoPoint centerPoint = new GeoPoint(0, 0);
		BMapUtil.calculateSpanAndCenter(points, span, centerPoint);
		mMapController.zoomToSpan(span[0], span[1]);
		mMapController.animateTo(centerPoint);
	} 
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		App.getInstance().releaseBMapManager();
		LocationServcice.unregisterListener(this);
	}

	@Override
	public void onUpdateVehicleLocation(GeoPointParcel geoPoint) {
		mVehicleLocationGeoPoint = geoPoint;
		showVehicleLocation(geoPoint);
	}
	
}
