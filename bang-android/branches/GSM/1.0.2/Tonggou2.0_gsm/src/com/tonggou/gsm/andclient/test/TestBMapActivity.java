package com.tonggou.gsm.andclient.test;

import android.graphics.Color;
import android.os.Bundle;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapStatus;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.service.LocationServcice;
import com.tonggou.gsm.andclient.service.LocationServcice.OnLocationReceiveListener;
import com.tonggou.gsm.andclient.ui.BackableTitleBarActivity;
import com.tonggou.gsm.andclient.util.BMapUtil;

public class TestBMapActivity extends BackableTitleBarActivity implements OnLocationReceiveListener {
	private static final GeoPoint GEO_SUZHOU = new GeoPoint((int) (31.296266 * 1E6), (int) (120.733165 * 1E6));
	
	private MapController mMapController;
	private SupportMapFragment mMapFragment;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		App.getInstance().initBMapManager();
		
		setContentView(R.layout.test_activity_bmap);
		getTitleBar().setTitle("地图", Color.BLACK);
		
		mMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map_fragment);
		mMapController = mMapFragment.getMapView().getController();
		mMapController.setMapStatus(newMapStatusWithGeoPointAndZoom(GEO_SUZHOU, 11));
	}
	
	private MKMapStatus newMapStatusWithGeoPointAndZoom(GeoPoint p, float zoom) {
        MKMapStatus status = new MKMapStatus();
        status.targetGeo = p;
        status.zoom = zoom;
        return status;
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
		mMapController.setCenter(BMapUtil.convertGeoPoint(location));
		
		MapView mapView = mMapFragment.getMapView();
		MyLocationOverlay myLocationOverlay = new MyLocationOverlay(mapView);  
		LocationData locData = new LocationData();  
		//手动将位置源置为天安门，在实际应用中，请使用百度定位SDK获取位置信息，要在SDK中显示一个位置，需要使用百度经纬度坐标（bd09ll）  
		locData.latitude = location.getLatitude();  
		locData.longitude = location.getLongitude();  
		myLocationOverlay.setData(locData);  
		mapView.getOverlays().add(myLocationOverlay);  
		mapView.refresh();  
		mMapController.animateTo(BMapUtil.convertGeoPoint(location));  
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		App.getInstance().releaseBMapManager();
	}
}
