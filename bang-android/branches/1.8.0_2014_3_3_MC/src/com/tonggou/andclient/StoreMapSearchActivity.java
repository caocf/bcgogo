package com.tonggou.andclient;

import java.net.URLEncoder;
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
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.parse.ShopSuggestionParser;
import com.tonggou.andclient.parse.StoreQueryParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.vo.Shop;
import com.tonggou.andclient.vo.ShopIntent;
import com.tonggou.andclient.vo.ShopSuggestion;
/**
 * 地图模式搜索结果页面
 *
 */
public class StoreMapSearchActivity extends BaseActivity{


	private static final int  NETWORK_FAILD=-1;
	private static final int  NETWORK_NODATA=0x002;

	private static final int  NETWORK_NAME_SUCCEED=0x0011;
	private static final int  NETWORK_NAME_NODATA=0x0012;
	private static final int  NETWORK_SUCCEED=0x003;
	private Handler handler;
	/**
	 *  MapView 是地图主控件
	 */
	private MapView mMapView = null;
	/**
	 *  用MapController完成地图控制 
	 */
	private MapController mMapController = null;
	private MyOverlay mOverlay = null;
	private PopupOverlay   pop  = null;
	private ArrayList<OverlayItem>  mItems = null; 
	private TextView  popname,popplace;
	private View viewCache = null,nameListView,search_sure;
	private ListView nameSearch;
	private View back,list;
	private  ArrayList<ShopIntent> allShops=new ArrayList<ShopIntent>();
	int chooseNum;
	boolean popisClose=true;
	ProgressBar progressBar;
	EditText shopssearch;
	double mLon5 = 0;
	double mLat5 = 0;
	private String conditionStr;
	private String coordinate = "NULL";
	private int pageNo = 1;
	private String shopName="";
	private String areaId = "NULL";
	private String cityCode="NULL";
	private String locStatues = "LAST";
	private String serviceScopeIds = "10000010001000001";  //服务范围
	private String serviceScopeIdName = "NULL";
	private String shopType="ALL";
	private String sortType="DISTANCE";
	private String pageSize,userNo,from,from2;
	private ImageView search_clean;

	private ArrayList<String>  nameList = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.storequerymap);
		progressBar=(ProgressBar) findViewById(R.id.shopdetilmappro);
		userNo=getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.NAME, null);
		progressBar.setVisibility(View.VISIBLE);
		shopName=getIntent().getStringExtra("tonggou.shopname");
		from=getIntent().getStringExtra("tonggou.from");
		areaId = getIntent().getStringExtra("tonggou.areaid");
		cityCode = getIntent().getStringExtra("tonggou.cityCode");
		serviceScopeIds=getIntent().getStringExtra("tonggou.serviceScopeIds");
		shopType=getIntent().getStringExtra("tonggou.shopType");
		sortType=getIntent().getStringExtra("tonggou.sortType");
		coordinate=getIntent().getStringExtra("tonggou.coordinate");
		conditionStr = getIntent().getStringExtra("tonggou.conditionStr");
		locStatues =getIntent().getStringExtra("tonggou.location.statues");
		((TextView)findViewById(R.id.title_tx)).setText(R.string.storequery_search_title);
		back=findViewById(R.id.left_button);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StoreMapSearchActivity.this.finish();
			}
		});

		list=findViewById(R.id.right_button);
		list.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				StoreMapSearchActivity.this.finish();
				/*if("StoreMapActivity".equals(from)){	
					if(allShops!=null&&allShops.size()>0){
						Intent intent=new Intent(StoreMapSearchActivity.this,StoreQuerySearchActivity.class);
						intent.putExtra("tonggou.shoplist",allShops);
						intent.putExtra("tonggou.from","StoreMapSearchActivity");
						intent.putExtra("tonggou.cityCode",cityCode);
						intent.putExtra("tonggou.conditionStr",conditionStr);
						intent.putExtra("tonggou.location.statues",locStatues);
						startActivityForResult(intent, 5050);
						StoreMapSearchActivity.this.finish();
					}
				}else{
					if("StoreMapSearchActivity".equals(from2)){	
						if(allShops!=null&&allShops.size()>0){
							Intent dataIntent = new Intent();
							dataIntent.putExtra("tonggou.shoplist",allShops);
							dataIntent.putExtra("tonggou.location.statues",locStatues);
							dataIntent.putExtra("tonggou.from","StoreMapSearchActivity");
							dataIntent.putExtra("tonggou.cityCode",cityCode);
							dataIntent.putExtra("tonggou.conditionStr",conditionStr);
							setResult(6060, dataIntent);
						}
					}
					StoreMapSearchActivity.this.finish();
				}
*/
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
		shopssearch.setText(shopName);
		shopssearch.setOnKeyListener(new OnKeyListener() { 
			public boolean onKey(View v, int keyCode, KeyEvent event) { 
				if(keyCode == KeyEvent.KEYCODE_ENTER) {
					if(event.getAction() == KeyEvent.ACTION_UP) { 
						if(shopssearch.getText().toString()!=null&&!"".equals(shopssearch.getText().toString())){					
							if(allShops!=null){
								allShops.clear();                                     //清空数据
							}			
							progressBar.setVisibility(View.VISIBLE);
							new Thread(){
								public void run(){
									getStoreQuery(coordinate,serviceScopeIds,areaId,cityCode,shopType,sortType,shopssearch.getText().toString(),pageNo,INFO.ITEMS_PER_PAGE+"");
								}
							}.start();
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
					if(allShops!=null){
						allShops.clear();                                     //清空数据
					}			
					nameListView.setVisibility(View.GONE);		
					mMapView.setVisibility(View.VISIBLE);
					progressBar.setVisibility(View.VISIBLE);
					new Thread(){
						public void run(){
							getStoreQuery(coordinate,serviceScopeIds,areaId,cityCode,shopType,sortType,shopssearch.getText().toString(),pageNo,INFO.ITEMS_PER_PAGE+"");
						}
					}.start();
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
				progressBar.setVisibility(View.VISIBLE);
				final String key=nameList.get(arg2);
				if(nameList.size()>arg2){	
					if(nameList.get(arg2)!=null&&!"".equals(nameList.get(arg2))){					
						if(allShops!=null){
							allShops.clear();                                     //清空数据
						}			
						progressBar.setVisibility(View.VISIBLE);
						new Thread(){
							public void run(){
								getStoreQuery(coordinate,serviceScopeIds,areaId,cityCode,shopType,sortType,key,pageNo,INFO.ITEMS_PER_PAGE+"");
							}
						}.start();
					}
				}
			}
		});
		shopssearch = (EditText) findViewById(R.id.book_search_et);
		shopssearch.setOnKeyListener(new OnKeyListener() { 
			public boolean onKey(View v, int keyCode, KeyEvent event) { 
				if(keyCode==KeyEvent.KEYCODE_ENTER){//修改回车键功能
					if(event.getAction() == KeyEvent.ACTION_UP) { 

					}
				}
				return false; 
			} 
		});
		TongGouApplication app = (TongGouApplication)this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(getApplicationContext());
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
		mMapController.setZoom(14);
		/**
		 * 显示内置缩放控件
		 */
		mMapView.setBuiltInZoomControls(true);


		/**
		 * 设定地图中心点
		 */
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg){		
				switch(msg.what){
				case NETWORK_FAILD: 
					progressBar.setVisibility(View.GONE);
					Toast.makeText(StoreMapSearchActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					break;
				case NETWORK_NODATA: 	
					progressBar.setVisibility(View.GONE);						
					Toast.makeText(StoreMapSearchActivity.this,getString(R.string.shoplist_no), Toast.LENGTH_SHORT).show();

					break;
				case NETWORK_SUCCEED: 
					progressBar.setVisibility(View.GONE);
					String[] location=allShops.get(0).getCoordinate().split(",");			

					mLon5=Double.parseDouble(location[0]);
					mLat5=Double.parseDouble(location[1]);
					GeoPoint p = new GeoPoint((int)(mLat5 * 1E6), (int)(mLon5* 1E6));
					mMapController.setCenter(p);
					initOverlay();
					from2="StoreMapSearchActivity";
					break;
				case NETWORK_NAME_SUCCEED: 
					ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
					for(int i=0;i<nameList.size();i++){				
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("name",nameList.get(i));
						list.add(map);
					}
					SimpleAdapter adapterName = new SimpleAdapter(StoreMapSearchActivity.this,list,
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
		if("StoreMapActivity".equals(from)){	
			new Thread(){
				public void run(){
					getStoreQuery(coordinate,serviceScopeIds,areaId,cityCode,shopType,sortType,shopName,pageNo,INFO.ITEMS_PER_PAGE+"");
				}
			}.start();
		}else  if("StoreQuerySearchActivity".equals(from)){	
			if(allShops!=null){
				allShops.clear();                                     //清空数据
			}

			mMapView.refresh();
			allShops=(ArrayList<ShopIntent>) getIntent().getSerializableExtra("tonggou.shoplist");
			progressBar.setVisibility(View.GONE);
			String[] location=allShops.get(0).getCoordinate().split(",");			

			mLon5=Double.parseDouble(location[0]);
			mLat5=Double.parseDouble(location[1]);
			GeoPoint p = new GeoPoint((int)(mLat5 * 1E6), (int)(mLon5* 1E6));
			mMapController.setCenter(p);

			initOverlay();
		}
	} 
	private void getStoreQuery(String coordinate,String serviceScope,String areaid,String cityCode,String shopType,String sortType,String shopName,int pageNo,String pageSize) {
		if(cityCode==null||"".equals(cityCode)||"null".equals(cityCode)){
			cityCode = "NULL";
		}


		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/shop/searchList/"+locStatues+"/"+coordinate+"/serviceScopeIds/"+serviceScope+"/sortType/"+sortType+"/areaId/"+areaid+"/cityCode/"+cityCode+"/shopType/"+shopType+"/keywords/"+URLEncoder.encode(shopName)+"/pageNo/"+pageNo+"/pageSize/"+pageSize;
		StoreQueryParser storeQueryParser = new StoreQueryParser();		
		NetworkState ns = Network.getNetwork(StoreMapSearchActivity.this).httpGetUpdateString(url,storeQueryParser);	

		if(ns.isNetworkSuccess()){
			if(storeQueryParser.isSuccessfull()){
				ArrayList<Shop> allShop=(ArrayList<Shop>) storeQueryParser.getStoreQueryResponse().getShopList();
				for(int i=0;i<allShop.size();i++){
					allShops.add(setShopIntent(allShop.get(i)));
				}

				if(allShops!=null&&allShops.size()>0){
					sendMessage(NETWORK_SUCCEED, storeQueryParser.getStoreQueryResponse().getMessage());
				}else{
					sendMessage(NETWORK_NODATA,storeQueryParser.getStoreQueryResponse().getMessage());
				}

			}else{
				//解析出错
				sendMessage(NETWORK_NODATA, storeQueryParser.getErrorMessage());
			}
		}else{
			//网络出错
			sendMessage(NETWORK_FAILD, ns.getErrorMessage());
		}
	}
	public void getSearchShopAction(String key,String cityCode,String areaid){
		if(cityCode==null||"".equals(cityCode)||"null".equals(cityCode)){
			cityCode = "NULL";
		}
		if(areaid==null||"".equals(areaid)||"null".equals(areaid)){
			areaid="NULL";
		}

		String  url=INFO.HTTP_HEAD+INFO.HOST_IP+"/shop/suggestions/keywords/"+URLEncoder.encode(key)+"/cityCode/"+cityCode+"/areaId/"+areaid;
		ShopSuggestionParser shopSuggestionParser= new ShopSuggestionParser();	
		NetworkState ns = Network.getNetwork(StoreMapSearchActivity.this).httpGetUpdateString(url,shopSuggestionParser);	

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
		if( mMapView == null && "".equals(mMapView)) {
			return;
		}
		/**
		 * 创建自定义overlay
		 */
		mOverlay = new MyOverlay(getResources().getDrawable(R.drawable.back),mMapView);	
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
			item.setMarker(getResources().getDrawable(R.drawable.position));
			/**
			 * 将item 添加到overlay中
			 * 注意： 同一个itme只能add一次
			 */
			mOverlay.addItem(item);
		}


		/*GeoPoint p2 = new GeoPoint ((int)(mLat2*1E6),(int)(mLon2*1E6));
		OverlayItem item2 = new OverlayItem(p2,"覆盖物2","");
		item2.setMarker(getResources().getDrawable(R.drawable.back));

		GeoPoint p3 = new GeoPoint ((int)(mLat3*1E6),(int)(mLon3*1E6));
		OverlayItem item3 = new OverlayItem(p3,"覆盖物3","");
		item3.setMarker(getResources().getDrawable(R.drawable.back));

		GeoPoint p4 = new GeoPoint ((int)(mLat4*1E6),(int)(mLon4*1E6));
		OverlayItem item4 = new OverlayItem(p4,"覆盖物4","");
		item4.setMarker(getResources().getDrawable(R.drawable.back));

		GeoPoint p5 = new GeoPoint ((int)(mLat5*1E6),(int)(mLon5*1E6));
		OverlayItem item5 = new OverlayItem(p5,"覆盖物5","");
		item5.setMarker(getResources().getDrawable(R.drawable.back));*/
		/**
		 * 保存所有item，以便overlay在reset后重新添加
		 */
		mItems = new ArrayList<OverlayItem>();
		mItems.addAll(mOverlay.getAllItem());
		/**
		 * 将overlay 添加至MapView中
		 */
		try {
			mMapView.getOverlays().add(mOverlay);
		} catch (Exception e) {
		}
		
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
				Intent intent=new Intent(StoreMapSearchActivity.this,StoreDetilActivity.class);
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
		handler = null;
		if( mMapView != null ) {
			mMapView.destroy();
		}
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


		@Override
		public boolean onTap(int index){
			if(popisClose){
				popisClose=false;
				OverlayItem item = getItem(index);
				chooseNum=index;
				popname.setText(getItem(index).getTitle());
				popplace.setText(getItem(index).getSnippet());
				Bitmap bitMaps=getBitmapFromView(viewCache);
				pop.showPopup(bitMaps,item.getPoint(),32);
			}else{
				if (pop != null){
					popisClose=true;
					pop.hidePop();
				}

			}
			return true;
		}

	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}
	protected void sendMessage(int what, String content) {
		if( isFinishing() || handler == null ) {
			return;
		}
		if (what < 0) {
			what = BaseActivity.SEND_MESSAGE;
		}
		Message msg = Message.obtain(handler, what, content);
		if(msg!=null){
			msg.sendToTarget();
		}
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==5050){		
			if(allShops!=null){
				allShops.clear();                                     //清空数据
			}

			mMapView.refresh();
			allShops=(ArrayList<ShopIntent>) data.getSerializableExtra("tonggou.shoplist");
			progressBar.setVisibility(View.GONE);
			String[] location=allShops.get(0).getCoordinate().split(",");			

			mLon5=Double.parseDouble(location[0]);
			mLat5=Double.parseDouble(location[1]);
			GeoPoint p = new GeoPoint((int)(mLat5 * 1E6), (int)(mLon5* 1E6));
			mMapController.setCenter(p);

			initOverlay();
		}
	}
}
