package com.tonggou.gsm.andclient.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.GeoPointParcel;
import com.tonggou.gsm.andclient.service.UpdateVehicleLocationBroadcastReceiver.OnUpdateVehicleLocationListener;
import com.tonggou.gsm.andclient.ui.VehicleLocationMapActivity;
import com.tonggou.gsm.andclient.util.ContextUtil;

/**
 * 行车轨迹地图 fragment
 * @author lwz
 *
 */
public class DrivingTrackMapFragment extends AbsMapFragment implements OnUpdateVehicleLocationListener, MKMapTouchListener {
	
	private ItemizedOverlay<OverlayItem> mVehicleLocationOverlay;
	private GeoPointParcel mCurrentVehicleLocation;
	
	@Override
	int getLayoutRes() {
		return R.layout.fragment_support_map;
	}

	@Override
	int getMapFragmentId() {
		return R.id.support_map_fragment;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mVehicleLocationOverlay = new ItemizedOverlay<OverlayItem>(getResources().getDrawable(R.drawable.ic_vehicle_location), mMapView);
		mMapView.getOverlays().add(mVehicleLocationOverlay);
		mMapView.regMapTouchListner(this);
	}
	
	public void setMKMapViewListener( MKMapViewListener l) {
		if( mMapView != null && l != null )
			mMapView.regMapViewListener(App.getInstance().initBMapManager(), l);
			App.getInstance().releaseBMapManager();
	}
	
	public MapView getMapView() {
		return mMapView;
	}

	@Override
	public void onUpdateVehicleLocation(GeoPointParcel geoPoint) {
		mCurrentVehicleLocation = geoPoint;
		mMapController.animateTo(geoPoint);
		// 在地图上显示车辆位置图标
		OverlayItem item = new OverlayItem(geoPoint, "", "");
		mVehicleLocationOverlay.removeAll();
		mVehicleLocationOverlay.addItem(item);
		mMapView.refresh();
		// 截图
//		new Handler().postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
//				if( mActivity == null || mActivity.isFinishing() ) {
//					return;
//				}
//				mMapView.getCurrentMap();
//			}
//		}, 200);
	}

	@Override
	public void onMapClick(GeoPoint arg0) {
		if( mCurrentVehicleLocation == null) {
			App.showShortToast(getString(R.string.info_vehicle_location_loading));
			return;
		} 
		Bundle args = new Bundle();
		args.putParcelable(VehicleLocationMapActivity.EXTRA_VEHICLE_LCOATION_GEO_POINT, mCurrentVehicleLocation);
		ContextUtil.startActivity(mActivity, VehicleLocationMapActivity.class, args);
	}

	@Override
	public void onMapDoubleClick(GeoPoint arg0) {
	}

	@Override
	public void onMapLongClick(GeoPoint arg0) {
	}
}
