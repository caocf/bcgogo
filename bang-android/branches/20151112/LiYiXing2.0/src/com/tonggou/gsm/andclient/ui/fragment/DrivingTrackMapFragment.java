package com.tonggou.gsm.andclient.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.Constants;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.LatLngParcel;
import com.tonggou.gsm.andclient.service.UpdateVehicleLocationBroadcastReceiver.OnUpdateVehicleLocationListener;
import com.tonggou.gsm.andclient.ui.VehicleLocationMapActivity;
import com.tonggou.gsm.andclient.util.BMapUtil;
import com.tonggou.gsm.andclient.util.ContextUtil;

/**
 * 行车轨迹地图 fragment
 * @author lwz
 *
 */
public class DrivingTrackMapFragment extends AbsMapFragment implements OnUpdateVehicleLocationListener, OnMapClickListener, OnMapLoadedCallback{
	private LatLng mCurrentVehicleLocation;
	BitmapDescriptor bdicon;
	OverlayOptions ooA;
	Marker mMarker;

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

		mBaiduMap.getUiSettings().setAllGesturesEnabled(true);
		mCurrentVehicleLocation = Constants.GEO_DEFAULT;
		mBaiduMap.setOnMapClickListener(this);
		mBaiduMap.setOnMapLoadedCallback(this);
	}

	public MapView getMapView() {
		return mMapView;
	}

	@Override
	public void onUpdateVehicleLocation(LatLngParcel geoPoint) {
		mCurrentVehicleLocation = geoPoint.getLatLng();

		if (mMarker == null) {
			bdicon = BitmapDescriptorFactory.fromResource(R.drawable.ic_vehicle_location);
			ooA = new MarkerOptions().position(mCurrentVehicleLocation).icon(bdicon).zIndex(9);
			mMarker = (Marker) mBaiduMap.addOverlay(ooA);
		}

		mBaiduMap.animateMapStatus(BMapUtil.newMapStatusWithLatLngAndZoom(mCurrentVehicleLocation, 16));
		mMarker.setPosition(mCurrentVehicleLocation);
	}

	@Override
	public void onMapClick(LatLng arg0) {
		if ( mCurrentVehicleLocation == null) {
			App.showLongToast(getString(R.string.info_vehicle_location_loading));
		}
		LatLngParcel llp = new LatLngParcel(mCurrentVehicleLocation.latitude, mCurrentVehicleLocation.longitude);
		Bundle args = new Bundle();
		args.putParcelable(VehicleLocationMapActivity.EXTRA_VEHICLE_LCOATION_GEO_POINT, llp);
		ContextUtil.startActivity(mActivity, VehicleLocationMapActivity.class, args);
	}

	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		return false;
	}

	@Override
	public void onMapLoaded() {
	}
}