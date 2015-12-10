package com.tonggou.gsm.andclient.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.tonggou.gsm.andclient.Constants;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.LatLngParcel;
import com.tonggou.gsm.andclient.service.LocationServcice;
import com.tonggou.gsm.andclient.service.LocationServcice.OnLocationReceiveListener;
import com.tonggou.gsm.andclient.service.UpdateVehicleLocationBroadcastReceiver.OnUpdateVehicleLocationListener;
import com.tonggou.gsm.andclient.util.BMapUtil;

/**
 * Main fragment
 * @author peter
 *
 */

public class MainVehicleAndUserMapFragment extends AbsMapFragment implements OnLocationReceiveListener, OnUpdateVehicleLocationListener, OnMapLoadedCallback, OnGetGeoCoderResultListener, OnMarkerClickListener{
	private LatLng mCurrentVehicleLocation;
	private BitmapDescriptor bdicon;
	private BitmapDescriptor bdUsericon;
	private OverlayOptions ooA;
	private OverlayOptions ooB;
	private Marker mVehicleMarker;
	private Marker mUserMarker;
	private GeoCoder mSearch;
	boolean mIsUserInfoWindowShow = false;
	boolean mIsNeedLocate = true;
	private ReverseGeoCodeResult mResult;
	private BDLocation mUserBDLocation;

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

		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);

		mBaiduMap.getUiSettings().setAllGesturesEnabled(true);
		mCurrentVehicleLocation = Constants.GEO_DEFAULT;
		mBaiduMap.setOnMapLoadedCallback(this);
		mBaiduMap.getUiSettings().setCompassEnabled(false);
	}

	public MapView getMapView() {
		return mMapView;
	}

	public void onResume() {
		super.onResume();
		LocationServcice.registerListener(this);
	}

	public void onPause() {
		super.onPause();
		LocationServcice.unregisterListener(this);
	}

	@Override
	public void onUpdateVehicleLocation(LatLngParcel geoPoint) {
		mCurrentVehicleLocation = geoPoint.getLatLng();

		if (mVehicleMarker == null) {
			bdicon = BitmapDescriptorFactory.fromResource(R.drawable.ic_vehicle_location);
			ooA = new MarkerOptions().position(mCurrentVehicleLocation).icon(bdicon).zIndex(9);
			mVehicleMarker = (Marker) mBaiduMap.addOverlay(ooA);
		} else {
			mVehicleMarker.setPosition(mCurrentVehicleLocation);
		}

		mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(mCurrentVehicleLocation));
		LocationServcice.registerListener(this);
	}

	private void zoomToSpanAndMoveToCenter(BDLocation myLocation) {
		LatLng[] span = new LatLng[] { BMapUtil.convertGeoPoint(myLocation).getLatLng(), BMapUtil.latLngToParcel(mCurrentVehicleLocation).getLatLng()};
		mBaiduMap.animateMapStatus(BMapUtil.newMapStatusWithLatLngArray(span));
		mBaiduMap.setOnMarkerClickListener(this);
	}

	@Override
	public void onMapLoaded() {
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		mUserBDLocation = location;

		if (mUserBDLocation != null) {
			LocationServcice.unregisterListener(this);

			if (mCurrentVehicleLocation != null)
				zoomToSpanAndMoveToCenter(location);

			if ( mUserMarker == null) {
				bdUsericon = BitmapDescriptorFactory.fromResource(R.drawable.ic_user_location);
				ooB = new MarkerOptions().position(new LatLng(mUserBDLocation.getLatitude(), mUserBDLocation.getLongitude())).icon(bdUsericon).zIndex(9);

				mUserMarker = (Marker) mBaiduMap.addOverlay(ooB);
			} else {
				mUserMarker.setPosition(new LatLng(mUserBDLocation.getLatitude(), mUserBDLocation.getLongitude()));
			}

			if (mCurrentVehicleLocation != null) {
				BMapUtil.setDistanceWithUserAndVehicle(mCurrentVehicleLocation, new LatLng(mUserBDLocation.getLatitude(), mUserBDLocation.getLongitude()));
				MainFragment.requestData();
			}
		}
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null) {
			return;
		}

		mResult = result;

		if (mUserBDLocation != null) {
			zoomToSpanAndMoveToCenter(mUserBDLocation);
		} else {
			mBaiduMap.setMapStatus(BMapUtil.newMapStatusWithLatLngAndZoom(mCurrentVehicleLocation, 16));
			mVehicleMarker.setPosition(mCurrentVehicleLocation);
		}

		if (result.getAddress() != null)
			showPopup(mCurrentVehicleLocation, mResult.getAddress(), 1);

		if (mUserBDLocation != null) {
			BMapUtil.setDistanceWithUserAndVehicle(mResult.getLocation(), new LatLng(mUserBDLocation.getLatitude(), mUserBDLocation.getLongitude()));
			MainFragment.requestData();
		}
	}

	private void showPopup(LatLng ll, String addr, int flag) {
		final View popView = View.inflate(getActivity().getApplicationContext(), R.layout.widget_vehicle_location_pop, null);
		((TextView)popView.findViewById(R.id.vehicle_addr)).setText(addr);
		InfoWindow of;
		if (flag == 1)
			of = new InfoWindow(popView, ll, BMapUtil.getInfoWindowYOffset(getActivity().getWindowManager().getDefaultDisplay().getHeight()));
		else
			of = new InfoWindow(popView, ll, -20);

		mBaiduMap.showInfoWindow(of);
	}

	@Override
	public boolean onMarkerClick(Marker v) {
		if (v == mUserMarker) {
			showPopup(new LatLng(mUserBDLocation.getLatitude(), mUserBDLocation.getLongitude()), mUserBDLocation.getAddrStr(), 2);
		}

		if (v == mVehicleMarker) {
			showPopup(mCurrentVehicleLocation, mResult.getAddress(), 1);
		}

		return true;
	}
}