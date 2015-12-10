package com.tonggou.gsm.andclient.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.loopj.android.image.SmartImageView;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.UserBaseInfo;
import com.tonggou.gsm.andclient.bean.AppShopDTO;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;
import com.tonggou.gsm.andclient.util.BMapUtil;

/**
 * 店铺介绍
 * @author lwz
 *
 */
public class ShopDetailActivity extends BackableTitleBarActivity implements OnMapLoadedCallback{
	private SupportMapFragment mMapFragment;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private AppShopDTO mShopInfo;
	private Marker mShopLoctionMarker;
	private LatLng mShopLatLng;

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_shop_detail);

		SmartImageView shopIcon = (SmartImageView) findViewById(R.id.shop_icon_img);
		TextView mShopTitle = (TextView) findViewById(R.id.shop_title_text);
		TextView mShopAddress = (TextView) findViewById(R.id.shop_address_text);
		TextView mShopCallNo = (TextView) findViewById(R.id.shop_call_no_text);

		mShopInfo = UserBaseInfo.getShopInfo();
		shopIcon.setImageUrl(mShopInfo.getSmallImageUrl());
		mShopTitle.setText(mShopInfo.getName());
		mShopAddress.setText(mShopInfo.getAddress());
		String landLine = mShopInfo.getLandLine();
		mShopCallNo.setText(TextUtils.isEmpty(landLine) ? getString(R.string.txt_info_shop_landline_empty) : landLine);
		mShopLatLng = new LatLng(mShopInfo.getCoordinateLat(), mShopInfo.getCoordinateLng());

		initMapView();
	}

	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(R.string.title_shop_detail);
	}

	/**
	 * 初始化地图
	 */
	private void initMapView() {
		mMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.shop_map_fragment);
		mMapView = mMapFragment.getMapView();
		mMapView.showScaleControl(true);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setOnMapLoadedCallback(this);
	}

	/**
	 * 显示弹出框
	 * @param overlayItem
	 */
	private void showPopup(Marker marker, AppShopDTO shopInfo) {

		final View popView = View.inflate(this, R.layout.widget_shop_location_pop, null);
		((TextView)popView.findViewById(R.id.shop_title)).setText(shopInfo.getName());
		((TextView)popView.findViewById(R.id.shop_snippet)).setText(shopInfo.getAddress());
		InfoWindow of = new InfoWindow(popView, marker.getPosition(), BMapUtil.getInfoWindowYOffset(getWindowManager().getDefaultDisplay().getHeight()));

		mBaiduMap.showInfoWindow(of);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onMapLoaded() {
		mBaiduMap.setMapStatus(BMapUtil.newMapStatusWithLatLngAndZoom(mShopLatLng, 16));

		BitmapDescriptor shopLocation = BitmapDescriptorFactory.fromResource(R.drawable.ic_shop_location);

		OverlayOptions ooA = new MarkerOptions().position(mShopLatLng).icon(shopLocation).zIndex(9);
		mShopLoctionMarker = (Marker)(mBaiduMap.addOverlay(ooA));

		showPopup(mShopLoctionMarker,mShopInfo);
	}
}