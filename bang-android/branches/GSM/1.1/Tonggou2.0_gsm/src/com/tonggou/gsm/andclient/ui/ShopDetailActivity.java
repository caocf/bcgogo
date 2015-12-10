package com.tonggou.gsm.andclient.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.loopj.android.image.SmartImageView;
import com.tonggou.gsm.andclient.App;
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
public class ShopDetailActivity extends BackableTitleBarActivity {

	SupportMapFragment mMapFragment;
	MapView mMapView;
	MapController mMapController;
	AppShopDTO mShopInfo;
	PopupOverlay mDetailPopupOverly;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		App.getInstance().initBMapManager();
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
		
		initMapView(mShopInfo.getCoordinateLat(), mShopInfo.getCoordinateLng());
	}
	
	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(R.string.title_shop_detail);
	}
	
	/**
	 * 初始化地图
	 */
	private void initMapView(double lat, double lng) {
		mMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.shop_map_fragment);
		mMapView = mMapFragment.getMapView();
		mMapView.setBuiltInZoomControls(true);
		mMapView.showScaleControl(true);
		mMapController = mMapView.getController();
		GeoPoint centerPoint = new GeoPoint(BMapUtil.convertDot(lat), BMapUtil.convertDot(lng));
		mMapController.setMapStatus(BMapUtil.newMapStatusWithGeoPointAndZoom(centerPoint, 16));
		mMapController.animateTo(centerPoint);
		
		// 在地图上添加店铺位置图标
		final OverlayItem item = new OverlayItem(centerPoint, mShopInfo.getName(), mShopInfo.getAddress());
		ItemizedOverlay<OverlayItem> overlay = 
				new ItemizedOverlay<OverlayItem>(getResources().getDrawable(R.drawable.ic_shop_location), mMapView);
		overlay.addItem(item);
		mMapView.getOverlays().add(overlay);
		showPopup(item);
		mMapView.refresh();
	}
	
	/**
	 * 显示弹出框
	 * @param overlayItem
	 */
	private void showPopup(final OverlayItem overlayItem) {
		final View popView = View.inflate(this, R.layout.widget_shop_location_pop, null);
		((TextView)popView.findViewById(R.id.shop_title)).setText(overlayItem.getTitle());
		((TextView)popView.findViewById(R.id.shop_snippet)).setText(overlayItem.getSnippet());
		mDetailPopupOverly = new PopupOverlay(mMapView, new PopupClickListener() {                  
		
	        @Override  
	        public void onClickedPopup(int index) {  
	        }  
		});  
		Drawable stationIcon = getResources().getDrawable(R.drawable.ic_shop_location);
		mDetailPopupOverly.showPopup(popView, overlayItem.getPoint(), stationIcon.getIntrinsicHeight());
		stationIcon = null;
	}
	
	@Override
	protected void onDestroy() {
		App.getInstance().releaseBMapManager();
		super.onDestroy();
	}
}
