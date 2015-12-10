package com.tonggou.andclient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.jsonresponse.QueryShopResponse;
import com.tonggou.andclient.jsonresponse.ServiceCategoryResponse;
import com.tonggou.andclient.jsonresponse.ShopSuggestionResponse;
import com.tonggou.andclient.myview.SimpleTitleBar;
import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.network.parser.AsyncJSONResponseParseHandler;
import com.tonggou.andclient.network.request.QueryServiceScopeRequest;
import com.tonggou.andclient.network.request.QueryShopListRequest;
import com.tonggou.andclient.network.request.ShopSuggestionByKeywordRequest;
import com.tonggou.andclient.parse.PlaceParser;
import com.tonggou.andclient.parse.ShopSuggestionParser;
import com.tonggou.andclient.parse.StoreQueryParser;
import com.tonggou.andclient.util.BitmapCache;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.vo.Area;
import com.tonggou.andclient.vo.ServiceCategoryDTO;
import com.tonggou.andclient.vo.Shop;
import com.tonggou.andclient.vo.ShopIntent;
import com.tonggou.andclient.vo.ShopSuggestion;
import com.tonggou.andclient.vo.type.CoordinateType;
import com.tonggou.andclient.vo.type.ServiceScopeType;
import com.tonggou.andclient.vo.type.ShopType;
import com.tonggou.andclient.vo.type.SortType;

/**
 * 查询推荐店铺页面
 * @author think
 *
 */
public class StoreQueryActivity extends AbsBackableActivity {
	
	public static final String PARAM_KEY_SERVICE_SCOPE = "tonggou.shop.category";
	public static final String PARAM_KEY_TITLE = "tonggou.shop.categoryname";

	private static final int  NETWORK_NAME_SUCCEED=0x0011;
	private static final int  NETWORK_NAME_NODATA=0x0012;

	private static final int  NETWORK_FAILD=-1;
	private static final int  NETWORK_SUCCEED=0x001;
	private static final int  NETWORK_NODATA=0x002;
	private static final int  NETWORK_PROVINCE_SUCCEED=0x004;
	private static final int  NETWORK_CITY_SUCCEED=0x005;
	private static final int  NETWORK_CATEGORY_SUCCEED = 0x006;
	private static final int NEXTPAGE_NETWORK_SUCCEED =0x007;    //下一页成功
	private static final int NEXTPAGE_NETWORK_NODATA =0x008;     //下一页没有数据
	private static final int CLOSE_PRO=0x009;
	private static final int IMAGE_FLUSH=0x0010;

	public static final String ALLCATEGORY = "所有";
	
	private Handler handler;
	private View array,arraySet,services,servicesSet,shops,shopsSet,places,placesSet,nameListView,search_sure;
	private ListView shopsListView,nameSearch,lv_group,rv_group;
	private ListView serverTypeList;
	private ShopsListAdapter shopsListAdapter; 
	private List<Area> province,city;
	private List<ServiceCategoryDTO> servicesCategory;
	private ArrayList<String> sortTypes=new ArrayList<String>();
	private ArrayList<Shop> allShops;
	private ArrayList<ShopIntent> allShopsIntent=new ArrayList<ShopIntent>();
	private ArrayList<Shop>  nextPageAllShops;   //下一页容器
	private View	popview2,popview3 ;
	private View	popview1 ;
	private PopupWindow pw;
//	private ProgressBar progressBar;
	private int proviceNum=0,cityNum=0;     //选中的类型的序号
	private int sortNum=0; //选中排序
	private int categoryNum=0;
	private TextView chooseSortName;     //按距离排序显示名字
	private TextView chooseShopName;     //店类型显示名字

	private long provinceId=0;
	private EditText shopssearch;
	private String pageSize;
	private CoordinateType mCoordinateType = CoordinateType.LAST;
	private String coordinate = "NULL";  //苏州120.73116302,31.26842725
	private int pageNo = 1;
	private String keyWord="NULL";
	private String areaId = "NULL";
	private String cityCode="NULL"; //苏州
	//private String cityCode="289"; //上海
	private String serviceScopeIds = "NULL";  //服务范围

	private ServiceScopeType mServiceType = ServiceScopeType.ALL;	// 查询的服务类型,默认为所有
	
	private ShopType shopType= ShopType.ALL;
	private SortType sortType= SortType.DISTANCE;
	private String locationProvinceName;  //百度定位到的 省份名
	private String locationCityName;      //百度定位到的 城市名
	private String conditionStr;
	private TextView chooseCityName;
	private TextView chooseCategoryName;

	private int visbleCount=0;
	private int firstListItem = 0;                      //从哪个位置开始取图片
	private boolean nextPageLock = false,hastoLast=false;   

	private ImageView search_clean;
	private boolean  servicesCategoryHas=false;
	private ArrayList<String>  nameList = new ArrayList<String>();
	private int screenHight;
	private TextView baiduText;
	
	private boolean mIsMore = false;
	
	@Override
	protected int getContentLayout() {
		return R.layout.storequery;
	}
	
	@Override
	protected void afterTitleBarCreated(SimpleTitleBar titleBar, Bundle savedInstanceState) {
		super.afterTitleBarCreated(titleBar, savedInstanceState);
		titleBar.setTitle(R.string.shopslist_title);
		titleBar.setRightImageButton(R.drawable.listtomap, android.R.color.transparent);
		titleBar.setOnRightButtonClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(allShopsIntent!=null&&allShopsIntent.size()>0){

					Intent intent=new Intent(StoreQueryActivity.this,StoreMapActivity.class);
					intent.putExtra("tonggou.shoplist",allShopsIntent);
					intent.putExtra("tonggou.cityCode",cityCode);
					intent.putExtra("tonggou.areaid",areaId);
					intent.putExtra("tonggou.serviceScopeIds",serviceScopeIds);
					intent.putExtra("tonggou.shopType",shopType.getValue());
					intent.putExtra("tonggou.sortType",sortType.getValue());
					intent.putExtra("tonggou.coordinate",coordinate);
					intent.putExtra("tonggou.conditionStr",conditionStr);
					intent.putExtra("tonggou.location.statues",mCoordinateType.getValue());
					startActivity(intent);
				}
			}
		});
		
		if( TongGouApplication.getInstance().isLogin() ) {
			titleBar.setRightSecondButton("更多", android.R.color.transparent);
			titleBar.setOnRightSecondButtonClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					pageNo = 1;
					hastoLast = false;
					doQueryStore(serviceScopeIds,areaId,shopType,keyWord,pageNo,INFO.ITEMS_PER_PAGE, true);
				}
			});
		}
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		if( getIntent().hasExtra(PARAM_KEY_SERVICE_SCOPE) ) {
			mServiceType = (ServiceScopeType) getIntent().getSerializableExtra(PARAM_KEY_SERVICE_SCOPE);
		}
		
		String categoryFormUiName = getIntent().getStringExtra(PARAM_KEY_TITLE);
		conditionStr = getIntent().getStringExtra("tonggou.shop.conditionStr");
		
		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		screenHight = metrics.heightPixels;
		LayoutInflater inflater = (LayoutInflater) StoreQueryActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		popview2= inflater.inflate(R.layout.pop_list2, null);
		popview3= inflater.inflate(R.layout.pop_list3, null);
		popview1 = inflater.inflate(R.layout.pop_list1, null);
		sortTypes.add(getString(R.string.shopslist_arrayplcae));
		sortTypes.add(getString(R.string.shopslist_arrayscroe));


		array = findViewById(R.id.array);
		arraySet= findViewById(R.id.array_set);
		array.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(pw != null){
					if(pw.isShowing()){	
						pw.dismiss();
					}else{	
						showPopup(arraySet);
					}
				}else{						
					showPopup(arraySet);
				}
			}
		});
		services = findViewById(R.id.service);
		servicesSet= findViewById(R.id.service_set);
		services.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(servicesCategory!=null&&servicesCategory.size()>0){				
					if(pw != null){
						if(pw.isShowing()){	
							pw.dismiss();
						}else{						
							showPopup3(servicesSet);
						}
					}else{		
						showPopup3(servicesSet);
					}
				}else{
					if(servicesCategoryHas){
						requestServiceScope(mServiceType);  //先去取服务类型id
					}
					Toast.makeText(StoreQueryActivity.this,"正在读取数据，请稍后...",Toast.LENGTH_SHORT).show();

				}
			}
		});
		shops = findViewById(R.id.shops);
		shopsSet= findViewById(R.id.shops_set);
		shops.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(ShopType.SHOP_4S == shopType){
					shopType= ShopType.ALL;
					shops.setBackgroundColor(Color.TRANSPARENT);
				}else{
					if( ShopType.ALL == shopType){
						shopType= ShopType.SHOP_4S;
						shops.setBackgroundColor(Color.parseColor("#33FFFFFF"));
					}
				}
				getListAction();
			}
		});
		
		places = findViewById(R.id.place);
		placesSet= findViewById(R.id.place_set);
		places.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(pw != null){
					if(pw.isShowing()){
						pw.setAnimationStyle(R.anim.bbb);
						pw .dismiss();
					}else{	
						//先加载城市列表信息，加载成功后显示列表
						showLoadingIndicator();
						new Thread(){
							public void run(){
								getProvinces("PROVINCE","NULL");
							}
						}.start();
						//						
						//						showPopup2(placesSet);
					}
				}else{	
					//先加载城市列表信息，加载成功后显示列表
					showLoadingIndicator();
					new Thread(){
						public void run(){
							getProvinces("PROVINCE","NULL");
						}
					}.start();
					//					showPopup2(placesSet);
				}
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
				shopsListView.setVisibility(View.VISIBLE);
			}
		});
		shopssearch =(EditText)this.findViewById(R.id.book_search_et);
		shopssearch.setOnKeyListener(new OnKeyListener() { 
			public boolean onKey(View v, int keyCode, KeyEvent event) { 
				if(keyCode == KeyEvent.KEYCODE_ENTER) {
					if(event.getAction() == KeyEvent.ACTION_UP) { 

						if(shopssearch.getText().toString()!=null&&!"".equals(shopssearch.getText().toString())){					
							Intent intent=new Intent(StoreQueryActivity.this,StoreQuerySearchActivity.class);
							intent.putExtra("tonggou.shopname",shopssearch.getText().toString());
							intent.putExtra("tonggou.from","StoreQueryActivity");
							intent.putExtra("tonggou.cityCode",cityCode);
							intent.putExtra("tonggou.location.statues",mCoordinateType.getValue());
							intent.putExtra("tonggou.areaid",areaId);
							intent.putExtra("tonggou.serviceScopeIds",serviceScopeIds);
							intent.putExtra("tonggou.shopType",shopType.getValue());
							intent.putExtra("tonggou.sortType",sortType.getValue());
							intent.putExtra("tonggou.coordinate",coordinate);
							intent.putExtra("tonggou.conditionStr",conditionStr);
							startActivity(intent);
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
						showLoadingIndicator();
						getSearchShopAction(key,cityCode,areaId);									
					}else{
						if(nameList.size()>0){
							nameList.clear();
						}
						nameListView.setVisibility(View.GONE);		
						shopsListView.setVisibility(View.VISIBLE);

					}
				}else{
					search_clean.setVisibility(View.GONE);		
					shopsListView.setVisibility(View.VISIBLE);

				}
			}  			
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }  
			public void onTextChanged(CharSequence s, int start, int before,  int count) {  }  
		});
		search_sure=this.findViewById(R.id.book_search_sure);
		search_sure.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(shopssearch.getText().toString()!=null&&!"".equals(shopssearch.getText().toString())){					
					Intent intent=new Intent(StoreQueryActivity.this,StoreQuerySearchActivity.class);
					intent.putExtra("tonggou.shopname",shopssearch.getText().toString());
					intent.putExtra("tonggou.from","StoreQueryActivity");
					intent.putExtra("tonggou.cityCode",cityCode);
					intent.putExtra("tonggou.location.statues",mCoordinateType.getValue());
					intent.putExtra("tonggou.areaid",areaId);
					intent.putExtra("tonggou.serviceScopeIds",serviceScopeIds);
					intent.putExtra("tonggou.shopType",shopType.getValue());
					intent.putExtra("tonggou.sortType",sortType.getValue());
					intent.putExtra("tonggou.coordinate",coordinate);
					intent.putExtra("tonggou.conditionStr",conditionStr);
					startActivity(intent);
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
				shopsListView.setVisibility(View.VISIBLE);
				if(nameList.size()>arg2){	
					if(nameList.get(arg2)!=null&&!"".equals(nameList.get(arg2))){					
						Intent intent=new Intent(StoreQueryActivity.this,StoreQuerySearchActivity.class);
						intent.putExtra("tonggou.shopname",nameList.get(arg2));
						intent.putExtra("tonggou.from","StoreQueryActivity");
						intent.putExtra("tonggou.cityCode",cityCode);
						intent.putExtra("tonggou.location.statues",mCoordinateType.getValue());
						intent.putExtra("tonggou.areaid",areaId);
						intent.putExtra("tonggou.serviceScopeIds",serviceScopeIds);
						intent.putExtra("tonggou.shopType",shopType.getValue());
						intent.putExtra("tonggou.sortType",sortType.getValue());
						intent.putExtra("tonggou.coordinate",coordinate);
						intent.putExtra("tonggou.conditionStr",conditionStr);
						startActivity(intent);
					}
				}
			}
		});



		shopsListView = (ListView) findViewById(R.id.shopslistview);
		shopsListView.setDividerHeight(0);
		shopsListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> eachAdaper, View arg1, int position, long arg3) {
				//setVoice();
				Intent intent=new Intent(StoreQueryActivity.this,StoreDetilActivity.class);
				intent.putExtra("tonggou.shopId", allShops.get(position).getId()+"");
				intent.putExtra("tonggou.shopname",allShops.get(position).getName());
				intent.putExtra("tonggou.shopmeter",allShops.get(position).getDistance()+"");
				intent.putExtra("tonggou.conditionStr",conditionStr+"");
				startActivity(intent);
			}
		});
		shopsListView.setOnScrollListener(new OnScrollListener(){
			int firstItem = 0;
			int lastItem =0;
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
				firstItem = firstVisibleItem;
				visbleCount = visibleItemCount;
				lastItem = firstVisibleItem + visibleItemCount ;
				firstListItem = firstVisibleItem ;
			}
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE||scrollState == 2){					
					if(allShops!=null && allShops.size()>0){
						new Thread(){
							public void run(){
								addPhoto(allShops,firstItem,visbleCount);									
							}
						}.start();
						new Thread(){
							public void run(){
								removePhoto(allShops,firstItem,visbleCount);
							}
						}.start();
					}
					if(!hastoLast&&allShops.size()>=INFO.ITEMS_PER_PAGE){	
						if(lastItem==allShops.size()){			
							if (!nextPageLock) {
								getNextPage();
							}
						}
					}
				}
			}        	
		});	
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg){		
				dismissLoadingDialog();
				switch(msg.what){
				case NETWORK_FAILD: 
					if(!"".equals((String)msg.obj)){
						Toast.makeText(StoreQueryActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					}
					break;
				case NETWORK_SUCCEED: 
					shopsListAdapter = new ShopsListAdapter(StoreQueryActivity.this,allShops); 
					shopsListView.setAdapter(shopsListAdapter);
					shopsListAdapter.notifyDataSetChanged();
					for(int i=0;i<allShops.size();i++){
						allShopsIntent.add(setShopIntent(allShops.get(i)));
					}
					break;
				case NETWORK_NODATA: 	
					Toast.makeText(StoreQueryActivity.this,getString(R.string.store_query_no_data), Toast.LENGTH_SHORT).show();

					break;
				case NETWORK_PROVINCE_SUCCEED: 

					//显示城市列表
					showPopup2(placesSet);
					//找出定位到的省份并高亮选择
					if(locationProvinceName!=null&&!"".equals(locationProvinceName)){
						for(int i=0;i<province.size();i++){
							if(locationProvinceName.equals(province.get(i).getName())){
								final long thisProvinceId = province.get(i).getId();
								proviceNum = i;  //选中位置
								new Thread(){
									public void run(){
										getProvinces("CITY",thisProvinceId+"");
									}
								}.start();
							}
						}
					}else{
						final long thisProvinceId = province.get(0).getId();
						proviceNum = 0;  //选中位置
						new Thread(){
							public void run(){
								getProvinces("CITY",thisProvinceId+"");
							}
						}.start();
					}


					//progressBar.setVisibility(View.GONE);
					lv_group.setDividerHeight(0);
					final  ProviceAdapter adapter = new ProviceAdapter(StoreQueryActivity.this,province);
					adapter.notifyDataSetChanged();
					lv_group.setAdapter(adapter);

					lv_group.setOnItemClickListener(new OnItemClickListener() {  
						public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) { 
							provinceId=province.get(position).getId();
							showLoadingIndicator();
							locationProvinceName = province.get(position).getName();      //选中省份
							proviceNum=position;
							new Thread(){
								public void run(){
									getProvinces("CITY",provinceId+"");
								}
							}.start();
							adapter.notifyDataSetChanged();
						}  
					});
					break;
				case NETWORK_CITY_SUCCEED: 


					//找出定位到的城市并高亮选择
					if(locationCityName!=null&&!"".equals(locationCityName)){
						for(int i=0;i<city.size();i++){
							if(locationCityName.equals(city.get(i).getName())){							
								cityNum = i;  //选中位置
								areaId = city.get(i).getId()+"";                      //////////////////////////////////////////////////////
							}
						}
					}


					rv_group.setDividerHeight(0);

					final CityAdapter adapter2 = new CityAdapter(StoreQueryActivity.this,city);
					adapter2.notifyDataSetChanged();
					rv_group.setAdapter(adapter2);
					rv_group.setOnItemClickListener(new OnItemClickListener() {  
						public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
							cityCode=city.get(position).getCityCode();
							areaId = city.get(position).getId()+"";
							locationCityName = city.get(position).getName();      //选中城市
							chooseCityName.setText(locationCityName);             //顶部显示选中城市
							cityNum= position;                                    //用于高亮
							adapter2.notifyDataSetChanged();
							pw.setAnimationStyle(R.anim.bbb);
							pw.dismiss();

							getListAction();
						}  
					});
					break;
				case NETWORK_CATEGORY_SUCCEED: 
//					StringBuffer ids=new StringBuffer();
//					for(int i=0;i<servicesCategory.size();i++){
//						ids.append(servicesCategory.get(i).getId()+",");
//					}
//					serviceScopeIds=ids.toString().substring(0, ids.toString().length()-1);
					serviceScopeIds= processingAllIds(); 
					getListAction();  //取列表
					break;
				case NEXTPAGE_NETWORK_SUCCEED:
					mergeData();
					setImages(); //取照片
					shopsListAdapter.notifyDataSetChanged();
					break;
				case NEXTPAGE_NETWORK_NODATA:
					//没有数据提示	
					hastoLast=true;						
					Toast.makeText(StoreQueryActivity.this,getString(R.string.nextpage_nodata),Toast.LENGTH_LONG).show();
					break;

				case CLOSE_PRO:	
					break;
				case IMAGE_FLUSH:	
					shopsListAdapter.notifyDataSetChanged();					
					break;
				case NETWORK_NAME_SUCCEED: 
					ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
					for(int i=0;i<nameList.size();i++){				
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("name",nameList.get(i));
						list.add(map);
					}
					SimpleAdapter adapterName = new SimpleAdapter(StoreQueryActivity.this,list,
							R.layout.manual_input_item,
							new String[]{"name"},
							new int[]{R.id.manul_book_name});
					nameSearch.setAdapter(adapterName);
					nameSearch.invalidate();
					nameListView.setVisibility(View.VISIBLE);
					shopsListView.setVisibility(View.GONE);
					break;				
				case NETWORK_NAME_NODATA :	
					nameListView.setVisibility(View.GONE);
					shopsListView.setVisibility(View.VISIBLE);

					break;

				}
			}
		};
		chooseSortName = (TextView) findViewById(R.id.array_set_tx);
		chooseCityName = (TextView) findViewById(R.id.place_name);
		chooseShopName = (TextView) findViewById(R.id.shops_set_tv);

		chooseCategoryName = (TextView) findViewById(R.id.service_category_tv);
		chooseCategoryName.setText(categoryFormUiName);
		if(categoryFormUiName!=null&&!"".equals(categoryFormUiName)){
			getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit()
			.putString(BaseActivity.SERVICE, categoryFormUiName).commit();
		} else {
			chooseCategoryName.setText("所有");
		}
		
		//////////////////////////////////////////////////////////////////////调试代码
//		baiduText = (TextView) findViewById(R.id.baidu_location);
//		if(TongGouApplication.bdlocation == null){			
//				baiduText.setText("百度定位 ：null");			
//		}else{
//			String callbackTime = SomeUtil.longToStringDate2(TongGouApplication.callbackTime+"");			
//			if("CURRENT".equals(lastSta)){
//				String sss = "百度定位(新位置"+callbackTime+")  经纬度："+TongGouApplication.bdlocation.getLatitude()+":"+TongGouApplication.bdlocation.getLongitude()+":citycode:"+
//				TongGouApplication.bdlocation.getCityCode()+":city:"+TongGouApplication.bdlocation.getCity()+":Province:"+TongGouApplication.bdlocation.getProvince()+":时间:"+TongGouApplication.bdlocation.getTime();
//				baiduText.setText(sss);
//			}else{
//				String sss = "百度定位(上一次)  经纬度："+TongGouApplication.bdlocation.getLatitude()+":"+TongGouApplication.bdlocation.getLongitude()+":citycode:"+
//				TongGouApplication.bdlocation.getCityCode()+":city:"+TongGouApplication.bdlocation.getCity()+":Province:"+TongGouApplication.bdlocation.getProvince()+":时间:"+TongGouApplication.bdlocation.getTime();
//				baiduText.setText(sss);
//			}
//		}
		findViewById(R.id.baidu_location_bg).setVisibility(View.GONE);
		///////////////////////////////////////////////////////////////////////////////////////////////////////

		init(); //初始化些选项
		requestServiceScope(mServiceType);  //先去取服务类型id
		

	}
	
	private void showLoadingIndicator() {
		showLoadingDialog("加载中...");
	}
	
	private void hideLoadingIndiactor() {
		dismissLoadingDialog();
	}
	
	@Override
	protected void onDestroy() {
		hideLoadingIndiactor();
		mIsMore = false;
		super.onDestroy();
	}

	private void init(){
		allShops = new ArrayList<Shop>();
		shopsListAdapter = new ShopsListAdapter(this, allShops);
		shopsListView.setAdapter(shopsListAdapter);
		
		String lastCoordinate = sharedPreferences.getString(BaseActivity.LOCATION_LAST_POSITION, "120.73116302,31.26842725");  //经纬度 
		String lastCityCode = sharedPreferences.getString(BaseActivity.LOCATION_LAST_CITYCODE, "");  //citycode
		String lastCityName = sharedPreferences.getString(BaseActivity.LOCATION_LAST_CITYNAME, "");  //城市名 
		String lastProvinceName = sharedPreferences.getString(BaseActivity.LOCATION_LAST_PROVINCENAME, "");  //省份	
		String lastStatues = sharedPreferences.getString(BaseActivity.LOCATION_LAST_STATUES, "");    //status
		
		if(lastCoordinate!=null&&!"".equals(lastCoordinate)){
			coordinate = lastCoordinate;
		}
		if(lastCityCode!=null&&!"".equals(lastCityCode)){
			cityCode = lastCityCode;
		}
		if(lastCityName!=null&&!"".equals(lastCityName)){
			locationCityName = lastCityName;
			chooseCityName.setText(locationCityName);
		}
		if(lastProvinceName!=null&&!"".equals(lastProvinceName)){
			locationProvinceName = lastProvinceName;
		}
		if(lastStatues!=null&&!"".equals(lastStatues)){
			if("CURRENT".equals(lastStatues)){
				mCoordinateType = CoordinateType.CURRENT;
			}else if("LAST".equals(lastStatues)){
				mCoordinateType = CoordinateType.LAST;
			}
		}

//		if(TongGouApplication.getInstance().bdlocation!=null){
//			String baidubackStr = TongGouApplication.getInstance().bdlocation.getLongitude()+","+TongGouApplication.getInstance().bdlocation.getLatitude();
//			if(baidubackStr!=null&&baidubackStr.indexOf("E")==-1){				
//				coordinate =baidubackStr;
//				locationProvinceName = TongGouApplication.getInstance().bdlocation.getProvince();  //省份名
//				locationCityName = TongGouApplication.getInstance().bdlocation.getCity();          //市
//				cityCode = TongGouApplication.getInstance().bdlocation.getCityCode();
//			}
//
//			if(locationCityName!=null){
//				chooseCityName.setText(locationCityName);
//			}
//		}
	}

	
	/**
	 * 处理服务类型idStr
	 * @return
	 */
	private String processingAllIds(){
		String resultStr = "NULL";
		StringBuffer ids=new StringBuffer();
		for(int i=0;i<servicesCategory.size();i++){
			String name = servicesCategory.get(i).getName();
			if(name==null||"".equals(name)||ALLCATEGORY.equals(name)){
				continue;
			}
			ids.append(servicesCategory.get(i).getId()+",");
		}
		if(ids.toString().length()>1){
			resultStr=ids.toString().substring(0, ids.toString().length()-1);
		}
		return resultStr;
	}
	
	private void getListAction(){
		if(allShops!=null){
			allShops.clear();                                     //清空数据
			if(shopsListAdapter!=null){
				shopsListAdapter.notifyDataSetChanged();    
			}
		}
		if(allShopsIntent!=null){
			allShopsIntent.clear();                                     //清空数据
		}

		doQueryStore(serviceScopeIds,areaId,shopType,keyWord,pageNo,INFO.ITEMS_PER_PAGE, true);
	}
	
//	private void doGetStoreQuery(final String serviceScope,final String areadid,final String shopType,final String shopName,final int pageNo,final String pageSize, final boolean isMore) {
//		showLoadingIndicator();
//		//走网络
//		new Thread(){
//			public void run(){
//				getStoreQuery(serviceScope, areadid, shopType, shopName, pageNo, pageSize, isMore);
//			}
//		}.start();
//	}
	
	public void doQueryStore(String serviceScopeIds, String areaId, final ShopType shopType, String keyword, int pageNo, int pageSize, final boolean isRefresh) {
		showLoadingDialog("加载中...");
		nextPageLock = true;
		QueryShopListRequest request = new QueryShopListRequest();
		request.setGuestMode( !TongGouApplication.getInstance().isLogin() );
		if( isRefresh ) {
			pageNo = 1;
		}
		request.setApiParams(mCoordinateType, coordinate, serviceScopeIds, sortType, areaId, cityCode, shopType, keyword, mIsMore, pageNo, pageSize);
		request.setGuestApiParams(mCoordinateType, coordinate, areaId, serviceScopeIds, sortType, shopType, keyword, pageNo, pageSize);
			
		request.doRequest(this, new AsyncJSONResponseParseHandler<QueryShopResponse>() {

			@Override
			public void onParseSuccess(QueryShopResponse result, byte[] originResult) {
				super.onParseSuccess(result, originResult);
				mIsMore = true;
				ArrayList<Shop> shops = (ArrayList<Shop>)result.getShopList();
				if( shops != null ) {
					if( shops.isEmpty() ) {
						if( isRefresh ) {
							TongGouApplication.showToast( (shopType == ShopType.SHOP_4S) ? getString(R.string.store_query_no_data) : result.getMessage());
						} else {
							hastoLast=true;						
							TongGouApplication.showToast(getString(R.string.nextpage_nodata));
						}
					} 
					updateShopData( shops, isRefresh );
				} else {
					onParseFailure("-1", getString(R.string.store_query_no_data));
				}
				
			}
			
			@Override
			public Class<QueryShopResponse> getTypeClass() {
				return QueryShopResponse.class;
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				nextPageLock = false;
				dismissLoadingDialog();
			}
			
		});
	}
	
	/**
	 * 更新商店数据
	 * @param data
	 * @param isRefresh
	 */
	private void updateShopData( ArrayList<Shop> data, boolean isRefresh ) {
		if( isRefresh ) {
			allShops.clear();
			pageNo = 1;
		}
		pageNo ++;
		allShops.addAll(data);
		shopsListAdapter.notifyDataSetChanged();
		for(int i=0;i<allShops.size();i++){
			allShopsIntent.add(setShopIntent(allShops.get(i)));
		}
		setImages();
		
	}
	
	private void getNextPage() {
		doQueryStore(serviceScopeIds, areaId, shopType, keyWord, pageNo, INFO.ITEMS_PER_PAGE, false);
	}

	/**
	 * 合并下一页数据到现在的数据中 （需要时进行去重复）
	 */
	private void mergeData() {
		int begin = 0;
		for (int j = begin; j < nextPageAllShops.size(); j++) {
			if(allShops!=null){
				allShops.add(nextPageAllShops.get(j));
				allShopsIntent.add(setShopIntent(nextPageAllShops.get(j)));
			}
		}
	}
	public void setImages(){
		new Thread(){
			public void run(){
				addPhoto(allShops,firstListItem,12);	
				sendMessage(CLOSE_PRO, null);
			}
		}.start();
	}

	private void getSearchShopAction(String key,String cityCode,String areaid){
		if(cityCode==null||"".equals(cityCode)||"null".equals(cityCode)){
			cityCode = "NULL";
		}
		
		String keywords = "NULL";
		try {
			keywords = URLEncoder.encode(key, "utf-8");
		} catch (UnsupportedEncodingException e) {
			;
		} 
		
		ShopSuggestionByKeywordRequest request = new ShopSuggestionByKeywordRequest();
		request.setGuestMode( !TongGouApplication.getInstance().isLogin() );
		request.setApiParams(keywords, cityCode, areaid, serviceScopeIds);
		request.setGuestApiParams(keywords, cityCode, areaid, serviceScopeIds);
		request.doRequest(this, new AsyncJSONResponseParseHandler<ShopSuggestionResponse>() {

			@Override
			public void onParseSuccess(ShopSuggestionResponse result, byte[] originResult) {
				super.onParseSuccess(result, originResult);
				
				ArrayList<ShopSuggestion> list=(ArrayList<ShopSuggestion>) result.getShopSuggestionList();
				if(list!=null && !list.isEmpty()){
					if(nameList!=null){
						nameList.clear();
					}
					for(int i=0;i<list.size();i++){
						nameList.add(list.get(i).getName());
					}
					if(nameList!=null&&nameList.size()>0){
						StoreQueryActivity.this.sendMessage(NETWORK_NAME_SUCCEED, result.getMessage());	
					}else{
						StoreQueryActivity.this.sendMessage(NETWORK_NAME_NODATA,null);
					}
				}else{
					StoreQueryActivity.this.sendMessage(NETWORK_NAME_NODATA,null);
				}
			}
			
			@Override
			public void onParseFailure(String errorCode, String errorMsg) {
//				super.onParseFailure(errorCode, errorMsg);
				StoreQueryActivity.this.sendMessage(NETWORK_NAME_NODATA,null);
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				super.onFailure(arg0, arg1, arg2, arg3);
				StoreQueryActivity.this.sendMessage(NETWORK_NAME_NODATA,null);
			}
			
			@Override
			public Class<ShopSuggestionResponse> getTypeClass() {
				return ShopSuggestionResponse.class;
			}
		});
	}
	
	private class ShopsListAdapter extends BaseAdapter{	
		private LayoutInflater layoutInflater;
		private List<Shop>  shops;
		StoreQueryActivity context;
		public ShopsListAdapter(StoreQueryActivity context, List<Shop>  shops){
			layoutInflater = LayoutInflater.from(context);	
			this.context=context;
			this.shops =  shops;
		}
		public int getCount() {			
			return shops.size();
		}
		public Shop getItem(int position) {
			return shops.get(position);
		}
		public long getItemId(int position) {		
			return position;
		}
		public View getView(int position, View convertView, ViewGroup parent) {	
			if(convertView == null){
				convertView = layoutInflater.inflate(R.layout.storequery_item, null);
			}				
			View main;ImageView shoplistpicView;
			if(position==0){
				convertView.findViewById(R.id.item).setVisibility(View.GONE);
				main=convertView.findViewById(R.id.item_top);
				main.setVisibility(View.VISIBLE);
				shoplistpicView=(ImageView) main.findViewById(R.id.shoplistpicView);
				shoplistpicView.setBackgroundResource(R.drawable.default_img);
				shoplistpicView.setImageBitmap(getItem(position).getBigbtm());
				
			}else{
				convertView.findViewById(R.id.item_top).setVisibility(View.GONE);
				main=convertView.findViewById(R.id.item);
				main.setVisibility(View.VISIBLE);
				shoplistpicView=(ImageView) main.findViewById(R.id.shoplistpicView);
				shoplistpicView.setImageBitmap(getItem(position).getSamllbtm());

			}
			ImageView like1=(ImageView)main.findViewById(R.id.shoplistlike1);
			ImageView like2=(ImageView)main.findViewById(R.id.shoplistlike2);
			ImageView like3=(ImageView)main.findViewById(R.id.shoplistlike3);
			ImageView like4=(ImageView)main.findViewById(R.id.shoplistlike4);
			ImageView like5=(ImageView) main.findViewById(R.id.shoplistlike5);
			TextView name = ((TextView)main.findViewById(R.id.shoplistname));
			TextView score = ((TextView)main.findViewById(R.id.shoplistlikesorce));
			TextView distance = ((TextView)main.findViewById(R.id.shoplistmeter));
			TextView place = ((TextView)main.findViewById(R.id.shoplistpalce_tv));
			TextView service = ((TextView)main.findViewById(R.id.shoplistservice_tv));
			if(getItem(position).getTotalScore()!=0){		
				score.setText(getItem(position).getTotalScore()+"分");
				like1.setVisibility(View.VISIBLE);
				like2.setVisibility(View.VISIBLE);
				like3.setVisibility(View.VISIBLE);
				like4.setVisibility(View.VISIBLE);
				like5.setVisibility(View.VISIBLE);
				context.setLikes(getItem(position).getTotalScore()+"",like1,like2,like3,like4,like5);
			}else{
				like1.setVisibility(View.GONE);
				like2.setVisibility(View.GONE);
				like3.setVisibility(View.GONE);
				like4.setVisibility(View.GONE);
				like5.setVisibility(View.GONE);
				score.setText("暂无评分");
			}
			if((getItem(position).getAddress())!=null){		
				place.setText(getItem(position).getAddress());
			}
			if((getItem(position).getServiceScope())!=null){		
				service.setText(getItem(position).getServiceScope());
			}else{
				service.setText("");
			}

			if((getItem(position).getDistance())+""!=null){	
				if(position==0){
					distance.setText(getItem(position).getDistance()+"km");
				}else{
					distance.setText("距离："+getItem(position).getDistance()+"km");
				}
			}
			name.setText(getItem(position).getName());
			final String url=getItem(position).getBigImageUrl();
			shoplistpicView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					new Thread(){
						public void run(){
							getPicture(url);
							if(BitmapCache.movePicToDisplay2(url)){
								Intent intento = new Intent(Intent.ACTION_VIEW);
								Uri uri = Uri.parse("file://"+android.os.Environment.getExternalStorageDirectory() + "/.tonggou/buffer.jpg");
								intento.setDataAndType(uri, "image/*");
								StoreQueryActivity.this.startActivity(intento);
							}else{
								/*Intent intent = new Intent(StoreQueryActivity.this, ShopDetilMoreCommentsPicActivity.class);
								intent.putExtra("url",(String)item);
								activity.startActivity(intent);*/
							}
						}
					}.start();
				}
			});
			return convertView;	
		}		
	}

	//距离选项
	private void showPopup(final View v){
		pw = new PopupWindow(popview1,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); 
		pw.showAsDropDown(v);
		pw.setFocusable(true);
		popview1.setFocusable(true); // 这个很重要
		popview1.setFocusableInTouchMode(true);
		popview1.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					pw.dismiss();
					pw = null;
					return true;
				}
				return false;
			}
		});
		pw.update();   
		pw.getContentView().setOnTouchListener(new OnTouchListener(){  

			public boolean onTouch(View v, MotionEvent event) {  
				// TODO Auto-generated method stub  
				pw.setFocusable(false);  
				pw.dismiss();  
				return true;  
			}  

		}); 
		int [] location = new int[2];
		v.getLocationOnScreen(location);
		int x = location[0];
		int y = location[1];
		int	shangchuHeigth=0;
		int	shangchuWidth=0;
		shangchuHeigth=getResources().getDrawable(R.drawable.shopschooseback).getMinimumHeight();
		shangchuWidth=getResources().getDrawable(R.drawable.shopschooseback).getMinimumWidth();

		popview1.setBackgroundDrawable(getResources().getDrawable(R.drawable.shopschooseback));
		serverTypeList = (ListView)popview1.findViewById(R.id.server_type_list);
		serverTypeList.setCacheColorHint(0); 
		serverTypeList.setDividerHeight(0);

		pw.update(x-(shangchuWidth-v.getWidth())/2,y+v.getHeight(),shangchuWidth,shangchuHeigth+v.getHeight());


		final OtherAdapter adapter1 = new OtherAdapter(StoreQueryActivity.this,sortTypes);
		adapter1.notifyDataSetChanged();
		serverTypeList.setAdapter(adapter1);
		serverTypeList.setOnItemClickListener(new OnItemClickListener() {  
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

				sortNum = position;
				chooseSortName.setText(sortTypes.get(position));
				if(position==0){
					sortType=SortType.DISTANCE;
				}if(position==1){
					sortType=SortType.EVALUATION;
				}
				pw.setAnimationStyle(R.anim.bbb);
				pw.dismiss();
				getListAction();
			}  
		});
		pw.setAnimationStyle(R.anim.aaa);
	}
	/**
	 * 城市弹出框
	 * @param v
	 * @param mUserNameEdit
	 */
	private void showPopup2(final View v) {  	

		
		pw = new PopupWindow(popview2,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); 
		pw.showAsDropDown(v);
		pw.setFocusable(true);
		popview2.setFocusable(true); // 这个很重要
		popview2.setFocusableInTouchMode(true);
		popview2.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					pw.dismiss();
					pw = null;
					return true;
				}
				return false;
			}
		});
		pw.update();   
		pw.getContentView().setOnTouchListener(new OnTouchListener(){  

			public boolean onTouch(View v, MotionEvent event) {  
				// TODO Auto-generated method stub  
				pw.setFocusable(false);  
				pw.dismiss();  
				return true;  
			}  

		}); 
		lv_group = (ListView)popview2.findViewById(R.id.lvGroup); 
		rv_group = (ListView)popview2.findViewById(R.id.riGroup);
		pw.setAnimationStyle(R.anim.aaa);;
	}


	/**
	 * 服务范围弹出框
	 * @param v
	 * @param mUserNameEdit
	 */
	private void showPopup3(final View v) {  	
		int [] location = new int[2];
		v.getLocationOnScreen(location);
		int x = location[0];
		int y = location[1];
		int shangchuHeigth=getResources().getDrawable(R.drawable.shopschooseback2).getMinimumHeight();
		int shangchuWidth=getResources().getDrawable(R.drawable.shopschooseback2).getMinimumWidth();

		pw = new PopupWindow(popview3,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); 
		//popview3.setBackgroundDrawable(getResources().getDrawable(R.drawable.shopschooseback2));

		pw.showAsDropDown(v);
		pw.setFocusable(true);
		popview3.setFocusable(true); // 这个很重要
		popview3.setFocusableInTouchMode(true);
		popview3.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					pw.dismiss();
					pw = null;
					return true;
				}
				return false;
			}
		});



		pw.getContentView().setOnTouchListener(new OnTouchListener(){  

			public boolean onTouch(View v, MotionEvent event) {  
				// TODO Auto-generated method stub  
				pw.setFocusable(false);  
				pw.dismiss();  
				return true;  
			}  

		}); 
		serverTypeList = (ListView)popview3.findViewById(R.id.server_type_list);
		serverTypeList.setCacheColorHint(0); 
		serverTypeList.setDividerHeight(0);

		if(servicesCategory.size()==1){
			pw.update(x-(shangchuWidth-v.getWidth())/2,y+v.getHeight(),shangchuWidth,shangchuHeigth+v.getHeight());
		}else{
			
			int hight = (servicesCategory.size()*(v.getHeight()*2))+(v.getHeight()*2);
			if(hight>screenHight-v.getHeight()*5){
				hight = screenHight-v.getHeight()*5;
			}
			pw.update(x-(shangchuWidth-v.getWidth())/2,y+v.getHeight(),shangchuWidth,hight);			
		}
		pw.setAnimationStyle(R.anim.aaa);

		final CategoryAdapter adapter3 = new CategoryAdapter(StoreQueryActivity.this,servicesCategory);
		adapter3.notifyDataSetChanged();
		serverTypeList.setAdapter(adapter3);
		serverTypeList.setOnItemClickListener(new OnItemClickListener() {  
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

				chooseCategoryName.setText(servicesCategory.get(position).getName());             //顶部显示选中
				categoryNum= position;                                    //用于高亮
				getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit()
				.putString(BaseActivity.SERVICE, servicesCategory.get(position).getName()).commit();
				adapter3.notifyDataSetChanged();
				pw.setAnimationStyle(R.anim.bbb);
				pw.dismiss();
				if(ALLCATEGORY.equals(servicesCategory.get(position).getName())){
					serviceScopeIds = processingAllIds();
				}else{
					serviceScopeIds=servicesCategory.get(position).getId();
				}
				getListAction();
			}  
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(pw != null){
				if(pw.isShowing()){
					pw.setAnimationStyle(R.anim.bbb);
					pw .dismiss();
				}else{
					this.finish();
				}
			}else{
				this.finish();
			}	
			return true;
		} 
		return super.onKeyDown(keyCode, event);
	}


	private class ProviceAdapter extends BaseAdapter{	
		private LayoutInflater layoutInflater;
		private List<Area>  places;
		public ProviceAdapter(StoreQueryActivity context, List<Area>  places){
			layoutInflater = LayoutInflater.from(context);	
			this.places =  places;
		}
		public int getCount() {			
			return places.size();
		}
		public Area getItem(int position) {
			return places.get(position);
		}
		public long getItemId(int position) {		
			return position;
		}
		public View getView(int position, View convertView, ViewGroup parent) {	
			if(convertView == null){
				convertView = layoutInflater.inflate(R.layout.popview_item, null);
			}				
			TextView name = ((TextView)convertView.findViewById(R.id.popview_name));
			if(places.get(position)!=null){		
				name.setText(places.get(position).getName());
			}
			if(proviceNum==position){
				convertView.setBackgroundDrawable(getResources().getDrawable(R.drawable.proviceback));
			}else{
				convertView.setBackgroundDrawable(null);
			}
			return convertView;	
		}		
	}


	private class CityAdapter extends BaseAdapter{	
		private LayoutInflater layoutInflater;
		private List<Area>  places;
		public CityAdapter(StoreQueryActivity context, List<Area>  places){
			layoutInflater = LayoutInflater.from(context);	
			this.places =  places;
		}
		public int getCount() {			
			return places.size();
		}
		public Area getItem(int position) {
			return places.get(position);
		}
		public long getItemId(int position) {		
			return position;
		}
		public View getView(int position, View convertView, ViewGroup parent) {	
			if(convertView == null){
				convertView = layoutInflater.inflate(R.layout.popview_item, null);
			}				
			TextView name = ((TextView)convertView.findViewById(R.id.popview_name));
			if(places.get(position)!=null){		
				name.setText(places.get(position).getName());
			}
			if(cityNum==position){
				convertView.setBackgroundDrawable(getResources().getDrawable(R.drawable.cityback));
			}else{
				convertView.setBackgroundDrawable(null);
			}
			return convertView;	
		}		
	}
	private class OtherAdapter extends BaseAdapter{	
		private LayoutInflater layoutInflater;
		private ArrayList<String>  others;
		public OtherAdapter(StoreQueryActivity context, ArrayList<String>  others){
			layoutInflater = LayoutInflater.from(context);	
			this.others =  others;
		}
		public int getCount() {			
			return others.size();
		}
		public String getItem(int position) {
			return others.get(position);
		}
		public long getItemId(int position) {		
			return position;
		}
		public View getView(int position, View convertView, ViewGroup parent) {	
			if(convertView == null){
				convertView = layoutInflater.inflate(R.layout.popview_item, null);
			}				
			TextView name = ((TextView)convertView.findViewById(R.id.popview_name));
			if(others.get(position)!=null){		
				name.setText(others.get(position));
			}	
			if(sortNum==position){
				convertView.setBackgroundDrawable(getResources().getDrawable(R.drawable.cityback));
			}else{
				convertView.setBackgroundDrawable(null);
			}
			return convertView;	
		}		
	}

	private void getProvinces(String type,String provinceId) {
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/area/list/"+type+"/"+provinceId;
		PlaceParser placeParser = new PlaceParser();		
		NetworkState ns = Network.getNetwork(StoreQueryActivity.this).httpGetUpdateString(url,placeParser);	

		if(ns.isNetworkSuccess()){
			if(placeParser.isSuccessfull()){
				if("NULL".equals(provinceId)){
					province=placeParser.getPlaceResponse().getAreaList();
					if(province!=null&&province.size()>0){
						sendMessage(NETWORK_PROVINCE_SUCCEED, placeParser.getPlaceResponse().getMessage());
					}else{
						sendMessage(NETWORK_NODATA,placeParser.getPlaceResponse().getMessage());
					}				
				}else{
					city=placeParser.getPlaceResponse().getAreaList();
					if(city!=null){
						sendMessage(NETWORK_CITY_SUCCEED, placeParser.getPlaceResponse().getMessage());
					}else{
						sendMessage(NETWORK_NODATA,placeParser.getPlaceResponse().getMessage());
					}	
				}
			}else{
				//解析出错
				sendMessage(NETWORK_NODATA, placeParser.getErrorMessage());
			}
		}else{
			//网络出错
			sendMessage(NETWORK_FAILD, ns.getErrorMessage());
		}
	}



	private void requestServiceScope(ServiceScopeType type) {
		
		QueryServiceScopeRequest request = new QueryServiceScopeRequest();
		request.setApiParams(type);
		request.doRequest(this, new AsyncJSONResponseParseHandler<ServiceCategoryResponse>() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				super.onFailure(arg0, arg1, arg2, arg3);
			}

			@Override
			public void onFinish() {
				super.onFinish();
				getSimpleTitle().hideLoadingIndicator();
			}

			@Override
			public void onStart() {
				getSimpleTitle().showLoadingIndicator();
			}

			@Override
			public void onParseSuccess(ServiceCategoryResponse result, byte[] originResult) {
				super.onParseSuccess(result, originResult);
				servicesCategory = result.getServiceCategoryDTOList();
				if( servicesCategory!=null && servicesCategory.size()>0 ){
					if( servicesCategory.size()>1 ){
						ServiceCategoryDTO categoryTitle = new ServiceCategoryDTO();
						categoryTitle.setId("NULL");
						categoryTitle.setName(ALLCATEGORY);
						servicesCategory.add(0, categoryTitle);
					}
					doQueryStore(serviceScopeIds, areaId, shopType, keyWord, pageNo, INFO.ITEMS_PER_PAGE, true);
				}else{
					TongGouApplication.showLog("无服务范围返回");
				}		
			}
			
			@Override
			public void onParseFailure(String errorCode, String errorMsg) {
				handlerParserFailure(errorCode, errorMsg);			
			}

			@Override
			public Class<ServiceCategoryResponse> getTypeClass() {
				return ServiceCategoryResponse.class;
			}
			
		});
		
//		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/serviceCategory/list/serviceScope/"+type.getTypeValue();
//		ServiceCategoryParser serviceCategoryParser = new ServiceCategoryParser();		
//		NetworkState ns = Network.getNetwork(StoreQueryActivity.this).httpGetUpdateString(url,serviceCategoryParser);	
//
//		servicesCategoryHas=true;
//		if(ns.isNetworkSuccess()){
//			if(serviceCategoryParser.isSuccessfull()){
//				servicesCategory = serviceCategoryParser.getPlaceResponse().getServiceCategoryDTOList();
//				if(servicesCategory!=null&&servicesCategory.size()>0){
//					if(servicesCategory.size()>1){
//						ServiceCategoryDTO categoryTitle = new ServiceCategoryDTO();
//						categoryTitle.setId("NULL");
//						categoryTitle.setName(ALLCATEGORY);
//						servicesCategory.add(0, categoryTitle);
//					}
//					sendMessage(NETWORK_CATEGORY_SUCCEED, serviceCategoryParser.getPlaceResponse().getMessage());
//				}else{
//					sendMessage(NETWORK_NODATA,"无服务范围返回");
//				}				
//
//			}else{
//				//解析出错
//				sendMessage(NETWORK_NODATA, serviceCategoryParser.getErrorMessage());
//			}
//		}else{
//			//网络出错
//
//			sendMessage(NETWORK_FAILD, ns.getErrorMessage());
//
//		}
	}


	private class CategoryAdapter extends BaseAdapter{	
		private LayoutInflater layoutInflater;
		private List<ServiceCategoryDTO>  categorys;
		public CategoryAdapter(StoreQueryActivity context, List<ServiceCategoryDTO>  places){
			layoutInflater = LayoutInflater.from(context);	
			this.categorys =  places;
		}
		public int getCount() {			
			if(categorys!=null){
				return categorys.size();
			}else{
				return 0;
			}
		}

		public ServiceCategoryDTO getItem(int position) {
			return categorys.get(position);
		}
		public long getItemId(int position) {		
			return position;
		}
		public View getView(int position, View convertView, ViewGroup parent) {	
			if(convertView == null){
				convertView = layoutInflater.inflate(R.layout.popview_item, null);
			}				
			TextView name = ((TextView)convertView.findViewById(R.id.popview_name));
			if(categorys.get(position)!=null){		
				name.setText(categorys.get(position).getName());
			}
			if(categoryNum==position){
				convertView.setBackgroundDrawable(getResources().getDrawable(R.drawable.cityback));
			}else{
				convertView.setBackgroundDrawable(null);
			}
			return convertView;	
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

	/*private void processCategoryId(){
		//找到对应的id用“，”号分割
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<servicesCategory.size();i++){
			sb.append(servicesCategory.get(i).getId());
			sb.append(",");
		}
		String allId = sb.toString();
		if(allId.length()>2){
			allId = allId.substring(0,allId.length()-1);
			serviceScopeIds = allId;
		}
	}*/

	public void addPhoto(ArrayList<Shop> scanBooks, int first, int count) {
		int begin = first /*- 3*/;
		if (begin < 0) {
			begin = 0;
		}
		int all = scanBooks.size();
		int end = first + count /*+ 12*/;

		if (end > all) {
			end = all;
		}
		Shop concise = null;
		String imageUrlsmall= null;
		String imageUrlbig = null;
		for (int i = begin; i < end; i++) {
			if(i<scanBooks.size()){
				concise = scanBooks.get(i);
					
				imageUrlsmall = concise.getSmallImageUrl();
				imageUrlbig = concise.getBigImageUrl();
				if (first==0&&i==0&&concise.getBigbtm() == null &&imageUrlbig!= null) {
					concise.setBigbtm(getPicture(imageUrlbig));
				}else{
					//getPicture(imageUrlbig);
					if ( concise.getSamllbtm() == null &&imageUrlsmall!= null) {
						concise.setSamllbtm(getPicture(imageUrlsmall));
					}
				}

				sendMessage(IMAGE_FLUSH,null);
			}
		}
	}
	
	public void removePhoto(ArrayList<Shop> scanBooks, int first, int count) {

		int all = scanBooks.size();
		int removeup = first - 3;
		int end = first + count + 3;
		Shop concise = null;
		if (removeup > 0) {// 清除上面
			for (int i = 0; i < removeup; i++) {
				if(i<scanBooks.size()){					
					concise = scanBooks.get(i);
					
					if ((concise.getSamllbtm()) != null) {
						/** 要先置空，在回收 */
						concise.setSamllbtm(null);
					}
				}
			}
		}
		if (all > end) {// 清除下面
			for (int j = end; j < all; j++) {
				if(j<scanBooks.size()){					
					
					concise = scanBooks.get(j);
					if ((concise.getSamllbtm()) != null) {
						/** 要先置空，在回收 */
						concise.setSamllbtm(null);
					}
				}
			}
		}
	}
}
