package com.tonggou.gsm.andclient.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapStatus;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.Constants;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.GasStation;
import com.tonggou.gsm.andclient.bean.GeoPointParcel;
import com.tonggou.gsm.andclient.service.LocationServcice;
import com.tonggou.gsm.andclient.service.LocationServcice.OnLocationReceiveListener;
import com.tonggou.gsm.andclient.ui.fragment.AbsGasStationLoadDataActivity;
import com.tonggou.gsm.andclient.util.BMapUtil;

/**
 * 加油站地图 Activity
 * @author lwz
 *
 */
public class GasStationMapActivity extends AbsGasStationLoadDataActivity implements OnLocationReceiveListener {
	
	public static final int REQUEST_CODE_SELECTED_GAS_STATION = 0x100;
	public static final String EXTRA_SELECTED_GAS_STATION_ID = "extra_selected_gas_station_id";
	
	private final int GAS_STATION_ICON_RES = R.drawable.ic_gas_station;	// 加油站图标资源 id
	
	private SupportMapFragment mMapFragment;
	private MapView mMapView;
	private MapController mMapController;
	private GasStationOverlay mGasStationOverlay;
	private MyLocationOverlay mMyLocationOverlay;
	private BDLocation mLastLocation;
	private PopupOverlay mDetailPopupOverly;
	private int mGasStationIconHeight = 0;
	
	@Override
	public void onCreate(Bundle arg0) {
		App.getInstance().initBMapManager();
		LocationServcice.registerListener(this);
		super.onCreate(arg0);
		setContentView(R.layout.activity_gas_station_map);
		
		initTitleBar();
		initMapView();
	}
	
	private void initTitleBar() {
		getTitleBar().setTitle(R.string.title_gas_station_map, R.color.black)
		.setRightButton("列表", R.color.black)
		.setOnRightButtonClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onTitleBarRightBtnClick();
			}
		});
	}
	
	private void onTitleBarRightBtnClick() {
		Intent intent = new Intent(this, GasStationListActivity.class);
		intent.putExtra(GasStationListActivity.EXTRA_GAS_STATION_LIST_DATA, getData());
		startActivityForResult(intent, REQUEST_CODE_SELECTED_GAS_STATION);
	}
	
	/**
	 * 初始化地图
	 */
	private void initMapView() {
		mMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.gas_station_map_fragment);
		mMapView = mMapFragment.getMapView();
		mMapView.setBuiltInZoomControls(true);
		mMapView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				App.debug(TAG, "@onTouch  GasStationMapActivity");
				return false;
			}
		});
		mMapView.showScaleControl(true);
		mMapController = mMapView.getController();
		mMapController.setMapStatus(newMapStatusWithGeoPointAndZoom(Constants.GEO_DEFAULT, 14));
		mMapController.setRotateWithTouchEventCenterEnabled(false);
		mMapController.setRotationGesturesEnabled(false);
		
		// 初始化加油站图层，所有加油站都在该图层上
		mGasStationOverlay = new GasStationOverlay(GAS_STATION_ICON_RES, mMapView);
		mMapView.getOverlays().add(mGasStationOverlay); 
	}
	
	private MKMapStatus newMapStatusWithGeoPointAndZoom(GeoPoint p, float zoom) {
        MKMapStatus status = new MKMapStatus();
        status.targetGeo = p;
        status.zoom = zoom;
        return status;
    }
	
	@Override
	public void onUpdateData(List<GasStation> data, boolean isRefresh) {
		if( isFinishing() ) {
			return;
		}
		ArrayList<OverlayItem> overlayItems = new ArrayList<OverlayItem>();
		
		if(isRefresh) { // 如果是刷新的数据源，那么把所有已经有的 item 全部去除
			mGasStationOverlay.getAllItem().clear();
		}
		for( GasStation station : data ) {
			overlayItems.add(new GasStationOverlayItem(station));
		}
		mGasStationOverlay.addItem(overlayItems);	// 在加油站图层上 添加该次 加载数据后的所有加油站点
		App.debug(TAG, mMapView.getOverlays().size());
		mMapView.refresh();		// 刷新地图，显示所有加油站
		App.debug(TAG, mMapView.getOverlays().size());
		
	}
	
	/**
	 * GasStation 在地图图层上的点
	 */
	class GasStationOverlayItem extends OverlayItem {

		public long stationId;	// 用于查找该 overlayItem, 其值等于  GasStation 的 id 属性
		
		public GasStationOverlayItem(GasStation station) {
			super(BMapUtil.convertGeoPoint( 
					station.getLat(), station.getLon()), station.getName(), station.getAddress());
			stationId = station.getId();
		}
		
		public void setMarker(int drawableRes) {
			super.setMarker(getResources().getDrawable(drawableRes));
		}
		
	}
	
	/**
	 * 加油站图层
	 * <p> 所有加油站 点都画在该图层上
	 * @author lwz
	 *
	 */
	private class GasStationOverlay extends ItemizedOverlay<OverlayItem> {

		public GasStationOverlay(int markRes, MapView mapView) {
			super(getResources().getDrawable(markRes), mapView);
		}

		protected boolean onTap(int index) {
			GasStationOverlayItem item = (GasStationOverlayItem)mGasStationOverlay.getItem(index);
			App.debug(TAG, index + "   " + item.stationId + "  " + item.getTitle() + "  " + item.getSnippet());
			showPopup(item);
			return true;
		}

		public boolean onTap(GeoPoint pt, MapView mapView) {
			return false;
		}

	}
	
	/**
	 * 显示弹出框
	 * @param overlayItem
	 */
	private void showPopup(final OverlayItem overlayItem) {
		final View popView = View.inflate(this, R.layout.widget_gas_station_pop, null);
		((TextView)popView.findViewById(R.id.station_title)).setText(overlayItem.getTitle());
		((TextView)popView.findViewById(R.id.station_snippet)).setText(overlayItem.getSnippet());
		popView.findViewById(R.id.station_navigation_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				App.showShortToast( overlayItem.getTitle() );
				turnToNavigationActivity( overlayItem.getPoint() );
			}
		});
		mDetailPopupOverly = new PopupOverlay(mMapView, new PopupClickListener() {                  
		
	        @Override  
	        public void onClickedPopup(int index) {  
	        }  
		});  
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if( isFinishing() ) {
					return;
				}
				mDetailPopupOverly.showPopup(popView, overlayItem.getPoint(), getGasStationIconHeight());
				mMapController.animateTo(overlayItem.getPoint());
			}
		}, 500);
	}
	
	private void turnToNavigationActivity(GeoPoint destPoint) {
		if( mLastLocation == null ) {
			App.showShortToast("定位失败");
			return;
		}
		Intent intent = new Intent(this, MapNavigationActivity.class);
		intent.putExtra(MapNavigationActivity.EXTRA_START_GEO_POINT, new GeoPointParcel(BMapUtil.convertGeoPoint(mLastLocation)));
		intent.putExtra(MapNavigationActivity.EXTRA_END_GEO_POINT, new GeoPointParcel(destPoint));
		startActivity(intent);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		if( mDetailPopupOverly != null ) {
			mDetailPopupOverly.hidePop();
		}
	}
	
	/**
	 * 得到 加油站图标的高度
	 * @return
	 */
	private int getGasStationIconHeight() {
		if( mGasStationIconHeight <=0 ) {
			Drawable stationIcon = getResources().getDrawable(GAS_STATION_ICON_RES);
			mGasStationIconHeight = stationIcon.getIntrinsicHeight();
			stationIcon = null;
		}
		return mGasStationIconHeight;
	}

	@Override
	public void onRequestDataFinish(boolean isSuccess, boolean isRefresh) {
		
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		if( mLastLocation != null && BMapUtil.isLocationEquals(mLastLocation, location) ) {
			return;
		}
		// 将用户的位置显示到地图上
		mLastLocation = location;
		if( mMyLocationOverlay == null ) {
			// 将用户的位置显示到地图上
			mMyLocationOverlay = new MyLocationOverlay(mMapView);  
			mMapView.getOverlays().add(mMyLocationOverlay); 
		}
		LocationData locData = new LocationData();  
		locData.latitude = location.getLatitude();  
		locData.longitude = location.getLongitude();  
		mMyLocationOverlay.setData(locData);  
		mMapView.setEnabled(false);
		mMapView.refresh();
		mMapController.animateTo(BMapUtil.convertGeoPoint(location));
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( requestCode == REQUEST_CODE_SELECTED_GAS_STATION && resultCode == Activity.RESULT_OK ) {
			if( data != null ) {
				long stationId = data.getLongExtra(EXTRA_SELECTED_GAS_STATION_ID, -1);
				if( stationId != -1 ) {
					showSelectedGasStationById(stationId);
				}
			}
		}
	}
	
	/**
	 * 根据 id 查找加油站点并显示
	 * @param stationId	加油站 id
	 */
	private void showSelectedGasStationById(long stationId) {
		App.debug(TAG, "selected gas station " + stationId);
		ArrayList<OverlayItem> overlayItems = mGasStationOverlay.getAllItem();
		for( OverlayItem item : overlayItems ) {
			GasStationOverlayItem overlay = (GasStationOverlayItem)item;
			if( overlay.stationId == stationId ) {
				showPopup(overlay);
				break;
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		LocationServcice.unregisterListener(this);
		App.getInstance().releaseBMapManager();
	}
	
}
