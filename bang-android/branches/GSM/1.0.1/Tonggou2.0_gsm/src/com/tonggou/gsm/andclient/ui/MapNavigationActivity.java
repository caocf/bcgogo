package com.tonggou.gsm.andclient.ui;

import android.graphics.Color;
import android.os.Bundle;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapStatus;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.GeoPointParcel;
import com.tonggou.gsm.andclient.service.LocationServcice;
import com.tonggou.gsm.andclient.service.LocationServcice.OnLocationReceiveListener;

/**
 * 地图导航 Activity
 * @author lwz
 *
 */
public class MapNavigationActivity extends BackableTitleBarActivity implements OnLocationReceiveListener {
	
	/** 起点坐标 */
	public static final String EXTRA_START_GEO_POINT = "extra_start_geopoint";
	/** 终点坐标 */
	public static final String EXTRA_END_GEO_POINT = "extra_end_geopoint";
	
	private MapView mMapView;
	private MapController mMapController;
	private BMapManager mBMapManager;
	private MKSearch mMKSearch;
	private GeoPointParcel mStartGeoPoint;
	private GeoPointParcel mEndGeoPoint;
	private MyLocationOverlay mMyLocationOverlay;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		mBMapManager = App.getInstance().initBMapManager();
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_map_navigation);
		getTitleBar().setTitle("导航", Color.BLACK);
		
		if( !restoreExtras(getIntent()) ) {
			if( !restoreExtras(savedInstance) ) {
				App.showLongToast("定位数据获取失败");
				finish();
				return;
			}
		}
		LocationServcice.registerListener(this);
		SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map_fragment);
		
		mMapView = mapFragment.getMapView();
		initMapView();
		initMkSearch();
		doNavigation(mStartGeoPoint, mEndGeoPoint);
	}
	
	private void initMapView() {
		mMapView.setBuiltInZoomControls(true);
		mMapView.showScaleControl(true);
		mMapController = mMapView.getController();
		mMapController.setMapStatus(newMapStatusWithGeoPointAndZoom(mStartGeoPoint, 14));
		mMapController.setRotateWithTouchEventCenterEnabled(false);
		mMapController.setRotationGesturesEnabled(false);
	}
	
	private MKMapStatus newMapStatusWithGeoPointAndZoom(GeoPoint p, float zoom) {
        MKMapStatus status = new MKMapStatus();
        status.targetGeo = p;
        status.zoom = zoom;
        return status;
    }
	
	private void initMkSearch() {
		mMKSearch = new MKSearch();
		mMKSearch.setDrivingPolicy(MKSearch.ECAR_DIS_FIRST);  
		mMKSearch.init(mBMapManager, new MKSearchListener() {
			
			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
			}
			
			@Override
			public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
			}
			
			@Override
			public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			}
			
			@Override
			public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1, int arg2) {
			}
			
			@Override
			public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
			}
			
			@Override
			public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			}
			
			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult result, int arg1) {
				if (result == null) {  
	                return;  
				}  
				RouteOverlay routeOverlay = new RouteOverlay(MapNavigationActivity.this, mMapView);
		        routeOverlay.setData(result.getPlan(0).getRoute(0));  
		        mMapView.getOverlays().add(routeOverlay);
		        mMapView.refresh();  
			}
			
			@Override
			public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			}
			
			@Override
			public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
			}
		});
	}

	@Override
	protected boolean restoreExtras(Bundle extra) {
		if( extra == null || !extra.containsKey(EXTRA_START_GEO_POINT) || !extra.containsKey(EXTRA_END_GEO_POINT) ) {
			return false;
		}
		mStartGeoPoint = extra.getParcelable(EXTRA_START_GEO_POINT);
		mEndGeoPoint = extra.getParcelable(EXTRA_END_GEO_POINT);
		return true;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(EXTRA_START_GEO_POINT,  mStartGeoPoint);
		outState.putParcelable(EXTRA_END_GEO_POINT, mEndGeoPoint);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onReceiveLocation(BDLocation location) {
		if( mMyLocationOverlay == null ) {
			mMyLocationOverlay = new MyLocationOverlay(mMapView);  
			mMapView.getOverlays().add(mMyLocationOverlay);  
		}
		// 将用户的位置显示到地图上
		LocationData locData = new LocationData();  
		locData.latitude = location.getLatitude();  
		locData.longitude = location.getLongitude();  
		mMyLocationOverlay.setData(locData);  
		mMapView.refresh();  
	}
	
	public void doNavigation(GeoPoint startPoint, GeoPoint endPoint) {
		MKPlanNode start = new MKPlanNode();  
		start.pt = startPoint;  
		MKPlanNode end = new MKPlanNode();  
		end.pt = endPoint;
		mMKSearch.drivingSearch(null, start, null, end);  
		mMapController.animateTo(start.pt);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if( mMKSearch != null ) {
			mMKSearch.destory();
		}
		App.getInstance().releaseBMapManager();
		LocationServcice.unregisterListener(this);
	}
	
}
