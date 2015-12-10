package com.tonggou.gsm.andclient.ui;

import android.os.Bundle;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.LatLngParcel;
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
public class VehicleLocationMapActivity extends BackableTitleBarActivity implements OnLocationReceiveListener, OnUpdateVehicleLocationListener, OnMapLoadedCallback{

	/** 车辆位置坐标 */
	public static final String EXTRA_VEHICLE_LCOATION_GEO_POINT = "extra_vehicle_lcoation_geopoint";

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private LatLng mVehicleLatLngLocation;
	private BitmapDescriptor bdIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_vehicle_location);
	private Marker mVehicleMarkerLocate;
	private BDLocation mBDLocation;

	@Override
	protected void onCreate(Bundle savedInstance) {
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

	private void initMapView() {
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setOnMapLoadedCallback(this);
		mMapView.showScaleControl(true);
		mBaiduMap.setMyLocationEnabled(true);
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

	@Override
	protected boolean restoreExtras(Bundle extra) {
		if( extra == null || !extra.containsKey(EXTRA_VEHICLE_LCOATION_GEO_POINT)) {
			return false;
		}
		LatLngParcel llp = extra.getParcelable(EXTRA_VEHICLE_LCOATION_GEO_POINT);
		mVehicleLatLngLocation = llp.getLatLng();
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		LatLngParcel lp = new LatLngParcel(mVehicleLatLngLocation.latitude, mVehicleLatLngLocation.longitude);
		outState.putParcelable(EXTRA_VEHICLE_LCOATION_GEO_POINT,  lp);
		super.onSaveInstanceState(outState);
	}

	private void zoomToSpanAndMoveToCenter(BDLocation myLocation) {
		LatLng[] span = new LatLng[] { BMapUtil.convertGeoPoint(myLocation).getLatLng(), BMapUtil.latLngToParcel(mVehicleLatLngLocation).getLatLng()};
		mBaiduMap.setMapStatus(BMapUtil.newMapStatusWithLatLngArray(span));
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapStatus(BMapUtil.newMapStatusWithLatLngAndZoom(mBaiduMap.getMapStatus().target, mBaiduMap.getMapStatus().zoom - 1));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LocationServcice.unregisterListener(this);
	}

	@Override
	public void onUpdateVehicleLocation(LatLngParcel geoPoint) {
		mVehicleLatLngLocation = geoPoint.getLatLng();
		mVehicleMarkerLocate.setPosition(mVehicleLatLngLocation);
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		if( mVehicleMarkerLocate == null) {
			OverlayOptions ooA = new MarkerOptions().position(mVehicleLatLngLocation).icon(bdIcon).zIndex(10).draggable(true);
			mVehicleMarkerLocate = (Marker)(mBaiduMap.addOverlay(ooA));
		}

		mBDLocation = location;
		MyLocationData locData = new MyLocationData.Builder().latitude(location.getLatitude()).longitude(location.getLongitude()).build();
		mBaiduMap.setMyLocationData(locData);

		zoomToSpanAndMoveToCenter(location);
	}

	@Override
	public void onMapLoaded() {
		mBaiduMap.setMapStatus(BMapUtil.newMapStatusWithLatLngAndZoom(mVehicleLatLngLocation, 16));
		OverlayOptions ooA = new MarkerOptions().position(mVehicleLatLngLocation).icon(bdIcon).zIndex(9).draggable(true);
		mVehicleMarkerLocate = (Marker)(mBaiduMap.addOverlay(ooA));

		if (mBDLocation != null)
			zoomToSpanAndMoveToCenter(mBDLocation);
	}
}