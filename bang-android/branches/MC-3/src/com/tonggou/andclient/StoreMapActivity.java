package com.tonggou.andclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.parse.ShopSuggestionParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.vo.ShopIntent;
import com.tonggou.andclient.vo.ShopSuggestion;


/**
 * 地图搜索店面页面
 * @author think
 *
 */

public class StoreMapActivity extends BaseActivity{
	private static final int  NETWORK_NAME_SUCCEED=0x0011;
	private static final int  NETWORK_NAME_NODATA=0x0012;
	private static final int  NETWORK_FAILD=-1;
	private Handler handler;
	/**
	 *  MapView 是地图主控件
	 */
	private LocationData locData = null;
	private MapView mMapView = null;
	/**
	 *  用MapController完成地图控制 
	 */
	private MapController mMapController = null;
	private MyOverlay mOverlay = null;
	private PopupOverlay   pop  = null;
	private ArrayList<OverlayItem>  mItems = null; 
	private TextView  popname,popplace;
	private View viewCache = null;
	/**
	 * overlay 位置坐标
	 */
	private EditText shopssearch;
	private View back,list,nameListView,search_sure;
	private ListView nameSearch;
	private int chooseNum;
	private boolean popisClose=true;
	private double mLon5 = 0;
	private double mLat5 = 0;
	private ArrayList<ShopIntent> allShops;

	private String areaId = "NULL";
	private String locStatues = "LAST";
	private String cityCode="NULL";
	private String serviceScopeIds = "10000010001000001";  //服务范围
	private String serviceScopeIdName = "NULL";

	private String shopType="ALL";
	private String sortType="DISTANCE";
	private String coordinate = "NULL";
	private String conditionStr ;
	private ImageView search_clean;
	private ProgressBar progressBar;
	//public MyLocationListenner myListener = new MyLocationListenner();
	//定位图层
	private MyLocationOverlay myLocationOverlay = null;
	private ArrayList<String>  nameList = new ArrayList<String>();
	//boolean first=true;
	private String latStr ;  //纬度 
	private String lonStr ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.storequerymap);

		findViewById(R.id.title).setFocusable(true);
		findViewById(R.id.title).setFocusableInTouchMode(true);


		cityCode=getIntent().getStringExtra("tonggou.cityCode");
		areaId = getIntent().getStringExtra("tonggou.areaid");
		serviceScopeIds=getIntent().getStringExtra("tonggou.serviceScopeIds");
		shopType=getIntent().getStringExtra("tonggou.shopType");
		sortType=getIntent().getStringExtra("tonggou.sortType");
		coordinate=getIntent().getStringExtra("tonggou.coordinate");
		locStatues =getIntent().getStringExtra("tonggou.location.statues");
		conditionStr = getIntent().getStringExtra("tonggou.conditionStr");

		allShops=(ArrayList<ShopIntent>) getIntent().getSerializableExtra("tonggou.shoplist");

		progressBar=(ProgressBar) findViewById(R.id.shopdetilmappro);
		back=findViewById(R.id.left_button);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StoreMapActivity.this.finish();
			}
		});

		list=findViewById(R.id.right_button);
		list.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StoreMapActivity.this.finish();

			}
		});
		search_clean=(ImageView)findViewById(R.id.book_search_close_icon);
		search_clean.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				shopssearch.setText("");
				//search.setBackgroundResource(R.drawable.search_bg_d);
				search_clean.setVisibility(View.GONE);
				nameListView.setVisibility(View.GONE);
				mMapView.setVisibility(View.VISIBLE);
			}
		});
		shopssearch =(EditText)this.findViewById(R.id.book_search_et);
		shopssearch.setCursorVisible(false);
		shopssearch.setOnKeyListener(new OnKeyListener() { 
			public boolean onKey(View v, int keyCode, KeyEvent event) { 
				if(keyCode == KeyEvent.KEYCODE_ENTER) {
					if(event.getAction() == KeyEvent.ACTION_UP) { 
						if(shopssearch.getText().toString()!=null&&!"".equals(shopssearch.getText().toString())){					
							Intent intent=new Intent(StoreMapActivity.this,StoreMapSearchActivity.class);
							intent.putExtra("tonggou.shopname",shopssearch.getText().toString());
							intent.putExtra("tonggou.from","StoreMapActivity");
							intent.putExtra("tonggou.cityCode",cityCode);
							intent.putExtra("tonggou.location.statues",locStatues);
							intent.putExtra("tonggou.areaid",areaId);
							intent.putExtra("tonggou.serviceScopeIds",serviceScopeIds);
							intent.putExtra("tonggou.shopType",shopType);
							intent.putExtra("tonggou.sortType",sortType);
							intent.putExtra("tonggou.coordinate",coordinate);

							intent.putExtra("tonggou.conditionStr",conditionStr);
							startActivity(intent);
							StoreMapActivity.this.finish();
						}
					}
				} 
				return false; 
			} 
		}); 
		shopssearch.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				//search.setBackgroundResource(R.drawable.search_bg_d);
				search_sure.setVisibility(View.VISIBLE);
				shopssearch.setCursorVisible(true);
				return false;
			}
		});
		shopssearch.addTextChangedListener(new TextWatcher(){  
			public void afterTextChanged(Editable s) { 
				final String key=s.toString(); 
				if(key!=null&&!"".equals(key)){	
					search_clean.setVisibility(View.VISIBLE);
					if(key.length()>0){	
						if(nameList.size()>0){
							nameList.clear();
						}
						progressBar.setVisibility(View.VISIBLE);
						new Thread(){
							public void run(){
								getSearchShopAction(key,cityCode,areaId);									
							}
						}.start();							
					}else{
						if(nameList.size()>0){
							nameList.clear();
						}
						nameListView.setVisibility(View.GONE);		
						mMapView.setVisibility(View.VISIBLE);

					}
				}else{
					search_clean.setVisibility(View.GONE);		
					mMapView.setVisibility(View.VISIBLE);

				}
			}  			
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }  
			public void onTextChanged(CharSequence s, int start, int before,  int count) {  }  
		});
		search_sure=this.findViewById(R.id.book_search_sure);
		search_sure.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(shopssearch.getText().toString()!=null&&!"".equals(shopssearch.getText().toString())){					
					Intent intent=new Intent(StoreMapActivity.this,StoreMapSearchActivity.class);
					intent.putExtra("tonggou.shopname",shopssearch.getText().toString());
					intent.putExtra("tonggou.from","StoreMapActivity");
					intent.putExtra("tonggou.cityCode",cityCode);
					intent.putExtra("tonggou.location.statues",locStatues);
					intent.putExtra("tonggou.areaid",areaId);					
					intent.putExtra("tonggou.serviceScopeIds",serviceScopeIds);
					intent.putExtra("tonggou.shopType",shopType);
					intent.putExtra("tonggou.sortType",sortType);
					intent.putExtra("tonggou.coordinate",coordinate);
					intent.putExtra("tonggou.conditionStr",conditionStr);
					startActivity(intent);
					StoreMapActivity.this.finish();
				}
			}
		});
		nameListView= findViewById(R.id.nameListView);
		nameListView.setVisibility(View.GONE);
		nameSearch=(ListView) findViewById(R.id.nameList);
		nameSearch.setCacheColorHint(0);
		nameSearch.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				nameListView.setVisibility(View.GONE);
				mMapView.setVisibility(View.VISIBLE);
				if(nameList.size()>arg2){	
					if(nameList.get(arg2)!=null&&!"".equals(nameList.get(arg2))){					
						Intent intent=new Intent(StoreMapActivity.this,StoreMapSearchActivity.class);
						intent.putExtra("tonggou.shopname",nameList.get(arg2));
						intent.putExtra("tonggou.from","StoreMapActivity");
						intent.putExtra("tonggou.cityCode",cityCode);
						intent.putExtra("tonggou.location.statues",locStatues);
						intent.putExtra("tonggou.areaid",areaId);
						intent.putExtra("tonggou.serviceScopeIds",serviceScopeIds);
						intent.putExtra("tonggou.shopType",shopType);
						intent.putExtra("tonggou.sortType",sortType);
						intent.putExtra("tonggou.coordinate",coordinate);
						intent.putExtra("tonggou.conditionStr",conditionStr);
						startActivity(intent);
						StoreMapActivity.this.finish();
					}
				}
			}
		});
		TongGouApplication app = (TongGouApplication)this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			/**
			 * 如果BMapManager没有初始化则初始化BMapManager
			 */
			app.mBMapManager.init(TongGouApplication.strKey,new TongGouApplication.MyGeneralListener());
		}
		/**
		 * 由于MapView在setContentView()中初始化,所以它需要在BMapManager初始化之后
		 */
		mMapView = (MapView)findViewById(R.id.map_View);
		/**
		 * 获取地图控制器
		 */
		mMapController = mMapView.getController();
		/**
		 *  设置地图是否响应点击事件  .
		 */
		mMapController.enableClick(true);
		/**
		 * 设置地图缩放级别
		 */
		mMapController.setZoom(13);
		/**
		 * 显示内置缩放控件
		 */
		mMapView.setBuiltInZoomControls(true);

		locData = new LocationData();
		latStr = sharedPreferences.getString(BaseActivity.LOCATION_LAST_POSITION_LAT, "");  //纬度 
		lonStr = sharedPreferences.getString(BaseActivity.LOCATION_LAST_POSITION_LON, "");  //经度 
		if(latStr!=null&&!"".equals(latStr)&&lonStr!=null&&!"".equals(lonStr)){
			try{
				locData.latitude = Double.parseDouble(latStr);
				locData.longitude =Double.parseDouble(lonStr);
			}catch(NumberFormatException ex){}
		}
//		locData.latitude = TongGouApplication.getInstance().bdlocation.getLatitude();
//		locData.longitude =TongGouApplication.getInstance().bdlocation.getLongitude();
		initOverlay();

		/**
		 * 设定地图中心点
		 */
		String[] location;
		if(allShops!=null&&allShops.size()>0){
			if(allShops.size()>1){
				location=allShops.get(1).getCoordinate().split(",");	
			}	else{
				location=allShops.get(0).getCoordinate().split(",");
			}
			mLon5=Double.parseDouble(location[0]);
			mLat5=Double.parseDouble(location[1]);
			GeoPoint p = new GeoPoint((int)(mLat5 * 1E6), (int)(mLon5* 1E6));
			mMapController.setCenter(p);
		}		
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg){		
				switch(msg.what){
				case NETWORK_FAILD: 
					progressBar.setVisibility(View.GONE);
					Toast.makeText(StoreMapActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					break;

				case NETWORK_NAME_SUCCEED: 
					ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
					for(int i=0;i<nameList.size();i++){				
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("name",nameList.get(i));
						list.add(map);
					}
					SimpleAdapter adapterName = new SimpleAdapter(StoreMapActivity.this,list,
							R.layout.manual_input_item,
							new String[]{"name"},
							new int[]{R.id.manul_book_name});
					adapterName.notifyDataSetChanged();
					nameSearch.setAdapter(adapterName);
					nameListView.setVisibility(View.VISIBLE);
					mMapView.setVisibility(View.GONE);
					progressBar.setVisibility(View.GONE);
					break;				
				case NETWORK_NAME_NODATA :	
					nameListView.setVisibility(View.GONE);
					mMapView.setVisibility(View.VISIBLE);

					progressBar.setVisibility(View.GONE);
					break;

				}
			}
		};
	} 
	
	
	public void getSearchShopAction(String key,String cityCode,String areaid){
		if(cityCode==null||"".equals(cityCode)||"null".equals(cityCode)){
			cityCode = "NULL";
		}
		String  url=INFO.HTTP_HEAD+INFO.HOST_IP+"/shop/suggestions/keywords/"+key+"/cityCode/"+cityCode+"/areaId/"+areaid;
		ShopSuggestionParser shopSuggestionParser= new ShopSuggestionParser();	
		NetworkState ns = Network.getNetwork(StoreMapActivity.this).httpGetUpdateString(url,shopSuggestionParser);	

		if(ns.isNetworkSuccess()){
			if(shopSuggestionParser.isSuccessfull()){
				ArrayList<ShopSuggestion> list=(ArrayList<ShopSuggestion>) shopSuggestionParser.getShopSuggestionResponse().getShopSuggestionList();

				if(list!=null&&list.size()>0){
					if(nameList.size()>0){
						nameList.clear();
					}
					for(int i=0;i<list.size();i++){
						nameList.add(list.get(i).getName());
					}
					if(nameList!=null&&nameList.size()>0){
						sendMessage(NETWORK_NAME_SUCCEED, shopSuggestionParser.getShopSuggestionResponse().getMessage());	
					}else{
						sendMessage(NETWORK_NAME_NODATA,null);
					}
				}else{
					sendMessage(NETWORK_NAME_NODATA,null);
				}


			}else{
				//解析出错
				sendMessage(NETWORK_NAME_NODATA,null);
			}
		}else{
			//网络出错
			sendMessage(NETWORK_FAILD, ns.getErrorMessage());
		}
	}
	public void initOverlay(){
		//定位图层初始化
		myLocationOverlay = new MyLocationOverlay(mMapView);
		//设置定位数据
		myLocationOverlay.setData(locData);
		//添加定位图层
		mMapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableCompass();
		/**
		 * 创建自定义overlay
		 */
		mOverlay = new MyOverlay(getResources().getDrawable(R.drawable.position),mMapView);	

		/**
		 * 准备overlay 数据
		 */
		for(int i=0;i<allShops.size();i++){
			String[] location=allShops.get(i).getCoordinate().split(",");			

			GeoPoint   point =new GeoPoint((int)(Double.parseDouble(location[1])*1e6), (int)(Double.parseDouble(location[0])*1e6));
			OverlayItem item = new OverlayItem(point,allShops.get(i).getName(),allShops.get(i).getAddress());
			/**
			 * 设置overlay图标，如不设置，则使用创建ItemizedOverlay时的默认图标.
			 */
			//item.setMarker(getResources().getDrawable(R.drawable.back));
			/**
			 * 将item 添加到overlay中
			 * 注意： 同一个itme只能add一次
			 */
			mOverlay.addItem(item);
		}

		/**
		 * 保存所有item，以便overlay在reset后重新添加
		 */
		mItems = new ArrayList<OverlayItem>();
		mItems.addAll(mOverlay.getAllItem());
		/**
		 * 将overlay 添加至MapView中
		 */
		mMapView.getOverlays().add(mOverlay);
		/**
		 * 刷新地图
		 */
		mMapView.refresh();

		/**
		 * 向地图添加自定义View.
		 */


		viewCache = getLayoutInflater().inflate(R.layout.popmapview, null);
		popname = (TextView) viewCache.findViewById(R.id.name);
		popplace =(TextView) viewCache.findViewById(R.id.place);


		/**
		 * 创建一个popupoverlay
		 */
		PopupClickListener popListener = new PopupClickListener(){
			@Override
			public void onClickedPopup(int index) {
				//更新item位置
				pop.hidePop();
				Intent intent=new Intent(StoreMapActivity.this,StoreDetilActivity.class);
				intent.putExtra("tonggou.shopId", allShops.get(chooseNum).getId()+"");
				intent.putExtra("tonggou.shopname",allShops.get(chooseNum).getName());
				intent.putExtra("tonggou.shopmeter",allShops.get(chooseNum).getDistance()+"");
				intent.putExtra("tonggou.conditionStr",conditionStr);
				startActivity(intent);
			}
		};
		pop = new PopupOverlay(mMapView,popListener);


	}
	@Override
	protected void onPause() {
		/**
		 *  MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		 */
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		/**
		 *  MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
		 */
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		/**
		 *  MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		 */
		mMapView.destroy();
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
	}

	public class MyOverlay extends ItemizedOverlay{

		public MyOverlay(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
		}


		public boolean onTap(int index){
			if(popisClose){
				popisClose=false;
				OverlayItem item = getItem(index);
				chooseNum=index;
				popname.setText(getItem(index).getTitle());
				popplace.setText(getItem(index).getSnippet());
				Bitmap bitMaps=getBitmapFromView(viewCache);
				pop.showPopup(bitMaps,item.getPoint(),32);
				mMapController.setCenter(item.getPoint());

			}else{
				if (pop != null){
					popisClose=true;
					pop.hidePop();
				}

			}
			return true;
		}
	}

	private void sendMessage(int what, String content) {
		if (what < 0) {
			what = BaseActivity.SEND_MESSAGE;
		}
		Message msg = Message.obtain(handler, what, content);
		if(msg!=null){
			msg.sendToTarget();
		}
	}

}
