package com.tonggou.gsm.andclient.test;

import android.graphics.Color;
import android.os.Bundle;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.service.LocationServcice;
import com.tonggou.gsm.andclient.service.LocationServcice.OnLocationReceiveListener;
import com.tonggou.gsm.andclient.ui.BackableTitleBarActivity;

public class TestBMapActivity extends BackableTitleBarActivity implements OnLocationReceiveListener {
	private static final LatLng GEO_SUZHOU = new LatLng((31.296266 * 1E6), (120.733165 * 1E6));

	private BaiduMap mBaiduMap;
	private SupportMapFragment mMapFragment;

	private boolean isFirstLoc = true;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		App.getInstance().InitDituSDK();

		setContentView(R.layout.test_activity_bmap);
		getTitleBar().setTitle("地图", Color.BLACK);

		mMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map_fragment);
		mBaiduMap = mMapFragment.getMapView().getMap();
		mBaiduMap.setMapStatus(newMapStatusWithGeoPointAndZoom(GEO_SUZHOU, 11));
	}

	private MapStatusUpdate newMapStatusWithGeoPointAndZoom(LatLng p, float zoom) {
        MapStatus status = new MapStatus.Builder().target(p).zoom(zoom).build();
        MapStatusUpdate statusUpdate = MapStatusUpdateFactory.newMapStatus(status);
        return statusUpdate;
    }

	@Override
	public void onStart() {
		super.onStart();
		LocationServcice.registerListener(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		LocationServcice.unregisterListener(this);
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		/*
		MapView mapView = mMapFragment.getMapView();
		MyLocationOverlay myLocationOverlay = new MyLocationOverlay(mapView);
		LocationData locData = new LocationData();
		//手动将位置源置为天安门，在实际应用中，请使用百度定位SDK获取位置信息，要在SDK中显示一个位置，需要使用百度经纬度坐标（bd09ll）
		locData.latitude = location.getLatitude();
		locData.longitude = location.getLongitude();
		myLocationOverlay.setData(locData);
		mapView.getOverlays().add(myLocationOverlay);
		mapView.refresh();
		mBaiduMap.animateTo(BMapUtil.convertGeoPoint(location));  */
		if (location == null || mMapFragment.getMapView() == null)
			return;
		MyLocationData locData = new MyLocationData.Builder()
				.accuracy(location.getRadius())
				// 此处设置开发者获取到的方向信息，顺时针0-360
				.direction(100).latitude(location.getLatitude())
				.longitude(location.getLongitude()).build();
		mBaiduMap.setMyLocationData(locData);
		if (isFirstLoc) {
			isFirstLoc = false;
			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
