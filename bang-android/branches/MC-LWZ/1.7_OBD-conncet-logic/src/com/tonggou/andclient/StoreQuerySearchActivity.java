package com.tonggou.andclient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.jsonresponse.QueryShopResponse;
import com.tonggou.andclient.jsonresponse.ShopSuggestionResponse;
import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.network.parser.AsyncJSONResponseParseHandler;
import com.tonggou.andclient.network.request.QueryShopListRequest;
import com.tonggou.andclient.network.request.ShopSuggestionByKeywordRequest;
import com.tonggou.andclient.parse.ShopSuggestionParser;
import com.tonggou.andclient.parse.StoreQueryParser;
import com.tonggou.andclient.util.BitmapCache;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.vo.Shop;
import com.tonggou.andclient.vo.ShopIntent;
import com.tonggou.andclient.vo.ShopSuggestion;
import com.tonggou.andclient.vo.type.CoordinateType;
import com.tonggou.andclient.vo.type.ShopType;
import com.tonggou.andclient.vo.type.SortType;


/**
 * 搜索推荐店铺页面
 *
 */
public class StoreQuerySearchActivity extends BaseActivity {


	private static final int  NETWORK_NAME_SUCCEED=0x0011;
	private static final int  NETWORK_NAME_NODATA=0x0012;
	private static final int  NETWORK_FAILD=-1;
	private static final int  NETWORK_NODATA=0x002;

	private static final int  NETWORK_SUCCEED=0x003;
	private static final int NEXTPAGE_NETWORK_SUCCEED =0x007;    //下一页成功
	private static final int NEXTPAGE_NETWORK_NODATA =0x008;     //下一页没有数据
	private static final int CLOSE_PRO=0x009;
	private static final int IMAGE_FLUSH=0x0010;
	private Handler handler;
	private View back,map,nameListView,search_sure;
	private ListView shopsListView,nameSearch;
	private ShopsListAdapter shopsListAdapter;
	private ArrayList<Shop> allShops=new ArrayList<Shop>();
	private ArrayList<Shop>  nextPageAllShops;   //下一页容器
	private ArrayList<ShopIntent> allShopsIntent=new ArrayList<ShopIntent>();
	private ProgressBar progressBar;
	private EditText shopssearch;
	private String conditionStr ;
	private String coordinate = "NULL";
	private int pageNo = 1;
	private String keyWord="";
	private String areaId = "NULL";
	private String cityCode="NULL";
	private CoordinateType mCoordinateType = CoordinateType.LAST;
	private String serviceScopeIds = "10000010001000001";  //服务范围
	private String serviceScopeIdName0 = "NULL";
	private ShopType shopType= ShopType.ALL;
	private SortType sortType = SortType.DISTANCE;
	private String pageSize,userNo,from,from2;
	private int visbleCount=0;
	private int firstListItem = 0;                      //从哪个位置开始取图片
	private boolean nextPageLock = false,hastoLast=false;   

	private ImageView search_clean;

	private ArrayList<String>  nameList = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.storequerysearch);

		from=getIntent().getStringExtra("tonggou.from");
		keyWord=getIntent().getStringExtra("tonggou.shopname");

		cityCode=getIntent().getStringExtra("tonggou.cityCode");
		areaId=getIntent().getStringExtra("tonggou.areaid");
		serviceScopeIds=getIntent().getStringExtra("tonggou.serviceScopeIds");
		
		String shopTypeStr =getIntent().getStringExtra("tonggou.shopType");
		if( ShopType.ALL.getValue().equalsIgnoreCase(shopTypeStr) ) {
			shopType = ShopType.ALL;
		} else {
			shopType = ShopType.SHOP_4S;
		}
		String sortTypeStr=getIntent().getStringExtra("tonggou.sortType");
		if( SortType.DISTANCE.getValue().equalsIgnoreCase(sortTypeStr) ) {
			sortType = SortType.DISTANCE;
		} else {
			sortType = sortType.EVALUATION;
		}
		coordinate=getIntent().getStringExtra("tonggou.coordinate");
		conditionStr = getIntent().getStringExtra("tonggou.conditionStr");
		String coTypeStr = getIntent().getStringExtra("tonggou.location.statues"); 
		if( CoordinateType.LAST.getValue().equalsIgnoreCase( coTypeStr ) ) {
			mCoordinateType = CoordinateType.LAST;
		} else {
			mCoordinateType = CoordinateType.CURRENT;
		}
		userNo=getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.NAME, null);
		progressBar=(ProgressBar) findViewById(R.id.shopdetilmappro);
		progressBar.setVisibility(View.GONE);
		back=findViewById(R.id.left_button);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StoreQuerySearchActivity.this.finish();
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
		shopssearch.setText(keyWord);
		shopssearch.setCursorVisible(true);
		shopssearch.setSelection((keyWord + "").length());
		shopssearch.setOnKeyListener(new OnKeyListener() { 
			public boolean onKey(View v, int keyCode, KeyEvent event) { 
				if(keyCode==KeyEvent.KEYCODE_ENTER){//修改回车键功能
					if(event.getAction() == KeyEvent.ACTION_UP) { 
						if(shopssearch.getText().toString()!=null&&!"".equals(shopssearch.getText().toString())){					
							progressBar.setVisibility(View.VISIBLE);
							if(allShops!=null){
								if(shopsListAdapter!=null){
									//清空数据
									shopsListAdapter.notifyDataSetChanged();      
								}                             //清空数据   
							}
							if(allShopsIntent!=null){
								allShopsIntent.clear();                                     //清空数据
							}
							//走网络

							doQueryStore(serviceScopeIds,areaId,shopType,keyWord,pageNo,INFO.ITEMS_PER_PAGE, true);
//							new Thread(){
//								public void run(){
//									getStoreQuery(coordinate,serviceScopeIds,areaId,cityCode,shopType,sortType,shopssearch.getText().toString(),pageNo,INFO.ITEMS_PER_PAGE+"");
//								}
//							}.start();
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
					progressBar.setVisibility(View.VISIBLE);
					nameListView.setVisibility(View.GONE);
					shopsListView.setVisibility(View.VISIBLE);
					if(allShops!=null){
						allShops.clear();      
						if(shopsListAdapter!=null)
							//清空数据
							shopsListAdapter.notifyDataSetChanged();      
					}
					if(allShopsIntent!=null){
						allShopsIntent.clear();                                     //清空数据
					}
					keyWord = shopssearch.getText().toString();
					//走网络

					doQueryStore(serviceScopeIds,areaId,shopType,keyWord,pageNo,INFO.ITEMS_PER_PAGE, true);
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
				
				progressBar.setVisibility(View.VISIBLE);
				final String key=nameList.get(arg2);
				if(nameList.size()>arg2){	
					if(nameList.get(arg2)!=null&&!"".equals(nameList.get(arg2))){					
						if(allShops!=null){
							allShops.clear();    
							if(shopsListAdapter!=null){
								//清空数据
								shopsListAdapter.notifyDataSetChanged();      
							}
						}
						if(allShopsIntent!=null){
							allShopsIntent.clear();                                     //清空数据
						}
						//走网络
						keyWord = key;
						doQueryStore(serviceScopeIds,areaId,shopType,keyWord,pageNo,INFO.ITEMS_PER_PAGE, true);
					}
				}
			}
		});
		map=findViewById(R.id.right_button);
		map.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if("StoreQueryActivity".equals(from)){	
					if(allShopsIntent!=null&&allShopsIntent.size()>0){
						Intent intent=new Intent(StoreQuerySearchActivity.this,StoreMapSearchActivity.class);

						intent.putExtra("tonggou.shopname",shopssearch.getText().toString());
						intent.putExtra("tonggou.from","StoreQueryActivity");
						intent.putExtra("tonggou.areaid",areaId);
						intent.putExtra("tonggou.serviceScopeIds",serviceScopeIds);
						intent.putExtra("tonggou.shopType",shopType.getValue());
						intent.putExtra("tonggou.sortType",sortType.getValue());
						intent.putExtra("tonggou.coordinate",coordinate);
						intent.putExtra("tonggou.conditionStr",conditionStr);
						intent.putExtra("tonggou.shoplist",allShopsIntent);
						intent.putExtra("tonggou.cityCode",cityCode);
						intent.putExtra("tonggou.from","StoreQuerySearchActivity");
						intent.putExtra("tonggou.conditionStr",conditionStr);
						intent.putExtra("tonggou.location.statues",mCoordinateType.getValue());
						startActivityForResult(intent, 6060);
					}
				}else{
					if("StoreQuerySearchActivity".equals(from2)){	
						if(allShopsIntent!=null&&allShopsIntent.size()>0){
							Intent dataIntent = new Intent();
							dataIntent.putExtra("tonggou.cityCode",cityCode);
							dataIntent.putExtra("tonggou.shoplist",allShopsIntent);
							dataIntent.putExtra("tonggou.from","StoreQuerySearchActivity");
							dataIntent.putExtra("tonggou.conditionStr",conditionStr);
							dataIntent.putExtra("tonggou.location.statues",mCoordinateType.getValue());
							setResult(5050, dataIntent);
						}
					}
					StoreQuerySearchActivity.this.finish();
				}
			}
		});

		
		shopsListView = (ListView) findViewById(R.id.shopslistview);
		shopsListView.setDividerHeight(0);
		shopsListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> eachAdaper, View arg1, int position, long arg3) {
				Intent intent=new Intent(StoreQuerySearchActivity.this,StoreDetilActivity.class);
				intent.putExtra("tonggou.shopId", allShops.get(position).getId()+"");
				intent.putExtra("tonggou.shopname",allShops.get(position).getName());
				intent.putExtra("tonggou.shopmeter",allShops.get(position).getDistance()+"");
				intent.putExtra("tonggou.conditionStr",conditionStr);
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
					/*if(firstItem==0){
						if(!canNext){
							return;
						}
					}*/
					if(!hastoLast&&allShops.size()>=INFO.ITEMS_PER_PAGE){	
						if(lastItem==allShops.size()){			
							if (!nextPageLock) {
								nextPageLock = true;
								progressBar.setVisibility(View.VISIBLE);	
								doQueryStore(serviceScopeIds,areaId,shopType,keyWord,pageNo,INFO.ITEMS_PER_PAGE, false);
							}
						}
					}
				}
			}        	
		});	
		allShops = new ArrayList<Shop>();
		shopsListAdapter = new ShopsListAdapter(this, allShops);
		shopsListView.setAdapter(shopsListAdapter);
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg){		
				switch(msg.what){
				case NETWORK_FAILD: 
					progressBar.setVisibility(View.GONE);
					Toast.makeText(StoreQuerySearchActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					break;
				case NETWORK_NODATA: 	
					progressBar.setVisibility(View.GONE);						
					Toast.makeText(StoreQuerySearchActivity.this,getString(R.string.shoplist_no), Toast.LENGTH_SHORT).show();

					break;
				case NETWORK_SUCCEED: 
					progressBar.setVisibility(View.GONE);
					shopsListAdapter = new ShopsListAdapter(StoreQuerySearchActivity.this,allShops);   			
					shopsListView.setAdapter(shopsListAdapter);
					from2="StoreQuerySearchActivity";
					for(int i=0;i<allShops.size();i++){
						ShopIntent intentShop=new ShopIntent();
						intentShop.setId(allShops.get(i).getId());
						intentShop.setName(allShops.get(i).getName());
						intentShop.setCoordinate(allShops.get(i).getCoordinate());
						intentShop.setAddress(allShops.get(i).getAddress());
						allShopsIntent.add(intentShop);
					}
					break;
				case NEXTPAGE_NETWORK_SUCCEED:
					mergeData();
					setImages(); //取照片
					progressBar.setVisibility(View.GONE);	
					shopsListAdapter.notifyDataSetChanged();
					break;
				case NEXTPAGE_NETWORK_NODATA:
					//没有数据提示	
					hastoLast=true;						
					progressBar.setVisibility(View.GONE);						
					Toast.makeText(StoreQuerySearchActivity.this,getString(R.string.nextpage_nodata),Toast.LENGTH_LONG).show();
					break;

				case CLOSE_PRO:	
					progressBar.setVisibility(View.GONE);			
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
					SimpleAdapter adapterName = new SimpleAdapter(StoreQuerySearchActivity.this,list,
							R.layout.manual_input_item,
							new String[]{"name"},
							new int[]{R.id.manul_book_name});
					adapterName.notifyDataSetChanged();
					nameSearch.setAdapter(adapterName);
					nameListView.setVisibility(View.VISIBLE);
					shopsListView.setVisibility(View.GONE);
					progressBar.setVisibility(View.GONE);
					break;				
				case NETWORK_NAME_NODATA :	
					nameListView.setVisibility(View.GONE);
					shopsListView.setVisibility(View.VISIBLE);

					progressBar.setVisibility(View.GONE);
					break;

				}
			}
		};
		if("StoreQueryActivity".equals(from)){	
			doQueryStore(serviceScopeIds,areaId,shopType,keyWord,pageNo,INFO.ITEMS_PER_PAGE, true);
		}else  if("StoreMapSearchActivity".equals(from)){	
			if(allShops!=null){
				allShops.clear();                                     //清空数据
			}
			if(allShopsIntent!=null){
				allShopsIntent.clear();                                     //清空数据
			}
			progressBar.setVisibility(View.GONE);
			allShopsIntent=(ArrayList<ShopIntent>) getIntent().getSerializableExtra("tonggou.shoplist");
			for(int i=0;i<allShopsIntent.size();i++){
				allShops.add(setShop(allShopsIntent.get(i)));
			}
			progressBar.setVisibility(View.GONE);	
			shopsListAdapter = new ShopsListAdapter(StoreQuerySearchActivity.this,allShops);   			
			shopsListView.setAdapter(shopsListAdapter);
			setImages();
			shopsListAdapter.notifyDataSetChanged();      
		}
	}
	
	public void doQueryStore(String serviceScopeIds, String areaId, ShopType shopType, String keyword, int pageNo, int pageSize, final boolean isRefresh) {
		showLoadingDialog("加载中...");
		nextPageLock = true;
		QueryShopListRequest request = new QueryShopListRequest();
		request.setGuestMode( !TongGouApplication.getInstance().isLogin() );
		if( isRefresh ) {
			hastoLast = false;
			pageNo = 1;
		}
		request.setApiParams(mCoordinateType, coordinate, serviceScopeIds, sortType, areaId, cityCode, shopType, keyword, false, pageNo, pageSize);
		request.setGuestApiParams(mCoordinateType, coordinate, areaId, serviceScopeIds, sortType, shopType, keyword, pageNo, pageSize);
			
		request.doRequest(this, new AsyncJSONResponseParseHandler<QueryShopResponse>() {

			@Override
			public void onParseSuccess(QueryShopResponse result, byte[] originResult) {
				super.onParseSuccess(result, originResult);
				ArrayList<Shop> shops = (ArrayList<Shop>)result.getShopList();
				if( shops != null ) {
					if( shops.isEmpty() ) {
						if( isRefresh ) {
							TongGouApplication.showToast(result.getMessage());
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
	

//	private void getStoreQuery(String coordinate,String serviceScope,String areaid,String cityCode,String shopType,String sortType,String shopName,int pageNo,String pageSize) {
//		if(cityCode==null||"".equals(cityCode)||"null".equals(cityCode)){
//			cityCode = "NULL";
//		}
//		/*if(areaid==null||"".equals(areaid)||"null".equals(areaid)){
//			areaid="NULL";
//		}*/
//
////		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/shop/searchList/"+locStatues+"/"+coordinate+"/serviceScopeIds/"+serviceScope+"/sortType/"+sortType+"/areaId/"+areaid+"/cityCode/"+cityCode+"/shopType/"+shopType+"/keywords/"+URLEncoder.encode(shopName)+"/pageNo/"+pageNo+"/pageSize/"+pageSize;
////		StoreQueryParser storeQueryParser = new StoreQueryParser();		
////		
////		NetworkState ns = Network.getNetwork(StoreQuerySearchActivity.this).httpGetUpdateString(url,storeQueryParser);	
////		if(ns.isNetworkSuccess()){
////			if(storeQueryParser.isSuccessfull()){
////				allShops=(ArrayList<Shop>) storeQueryParser.getStoreQueryResponse().getShopList();
////				if(allShops!=null&&allShops.size()>0){
////					sendMessage(NETWORK_SUCCEED, storeQueryParser.getStoreQueryResponse().getMessage());
////					setImages();
////				}else{
////					sendMessage(NETWORK_NODATA,storeQueryParser.getStoreQueryResponse().getMessage());
////				}
////
////			}else{
////				//解析出错
////				sendMessage(NETWORK_NODATA, storeQueryParser.getErrorMessage());
////			}
////		}else{
////			//网络出错
////			sendMessage(NETWORK_FAILD, ns.getErrorMessage());
////		}
//	}	
//	private  boolean nextPageNetworking(String coordinate,String serviceScope,String areaid,String cityCode,String shopType,String sortType,String shopName,int pageNo,String pageSize) {
//		if(cityCode==null||"".equals(cityCode)||"null".equals(cityCode)){
//			cityCode = "NULL";
//		}
//		/*if(areaid==null||"".equals(areaid)||"null".equals(areaid)){
//			areaid="NULL";
//		}*/
//		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/shop/searchList/"+locStatues+"/"+coordinate+"/serviceScopeIds/"+serviceScope+"/sortType/"+sortType+"/areaId/"+areaid+"/cityCode/"+cityCode+"/shopType/"+shopType+"/keywords/"+URLEncoder.encode(shopName)+"/pageNo/"+pageNo+"/pageSize/"+pageSize;
//		StoreQueryParser storeQueryParser = new StoreQueryParser();		
//		NetworkState ns = Network.getNetwork(StoreQuerySearchActivity.this).httpGetUpdateString(url,storeQueryParser);	
//
//		if(ns.isNetworkSuccess()){
//			if(storeQueryParser.isSuccessfull()){
//				nextPageAllShops=(ArrayList<Shop>) storeQueryParser.getStoreQueryResponse().getShopList();
//				if(nextPageAllShops==null||nextPageAllShops.size()<=0){
//					sendMessage(NEXTPAGE_NETWORK_NODATA, storeQueryParser.getErrorMessage());
//					return false;
//				}
//				return true;
//
//			}else{
//				//解析出错
//				sendMessage(NEXTPAGE_NETWORK_NODATA, storeQueryParser.getErrorMessage());
//				return false;
//			}
//		}else{
//			//网络出错
//			sendMessage(NEXTPAGE_NETWORK_NODATA, ns.getErrorMessage());
//			return false;
//		}
//	}
//	private void getNextPage() {
//		pageNo++;
//		if (!nextPageNetworking(coordinate,serviceScopeIds,areaId,cityCode,shopType,sortType,keyWord,pageNo,INFO.ITEMS_PER_PAGE+"")) {
//			pageNo--;
//			nextPageLock = false;
//		} else {
//			sendMessage(NEXTPAGE_NETWORK_SUCCEED, "");
//			nextPageLock = false;
//		}
//	}

	/**
	 * 合并下一页数据到现在的数据中 （需要时进行去重复）
	 */
	private void mergeData() {
		int begin = 0;
		for (int j = begin; j < nextPageAllShops.size(); j++) {
			if(allShops!=null){
				allShops.add(nextPageAllShops.get(j));
				ShopIntent intentShop=new ShopIntent();
				intentShop.setId(nextPageAllShops.get(j).getId());
				intentShop.setName(nextPageAllShops.get(j).getName());
				intentShop.setCoordinate(nextPageAllShops.get(j).getCoordinate());
				intentShop.setAddress(nextPageAllShops.get(j).getAddress());
				allShopsIntent.add(intentShop);
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
//	public void getSearchShopAction(String key,String cityCode,String areaid){
//		if(cityCode==null||"".equals(cityCode)||"null".equals(cityCode)){
//			cityCode = "NULL";
//		}
//		if(areaid==null||"".equals(areaid)||"null".equals(areaid)){
//			areaid="NULL";
//		}
//		String  url=INFO.HTTP_HEAD+INFO.HOST_IP+"/shop/suggestions/keywords/"+URLEncoder.encode(key)+"/cityCode/"+cityCode+"/areaId/"+areaid;
//		ShopSuggestionParser shopSuggestionParser= new ShopSuggestionParser();	
//		NetworkState ns = Network.getNetwork(StoreQuerySearchActivity.this).httpGetUpdateString(url,shopSuggestionParser);	
//
//		if(ns.isNetworkSuccess()){
//			if(shopSuggestionParser.isSuccessfull()){
//				ArrayList<ShopSuggestion> list=(ArrayList<ShopSuggestion>) shopSuggestionParser.getShopSuggestionResponse().getShopSuggestionList();
//
//				if(list!=null&&list.size()>0){
//					if(nameList.size()>0){
//						nameList.clear();
//					}
//					for(int i=0;i<list.size();i++){
//						nameList.add(list.get(i).getName());
//					}
//					if(nameList!=null&&nameList.size()>0){
//						sendMessage(NETWORK_NAME_SUCCEED, shopSuggestionParser.getShopSuggestionResponse().getMessage());	
//					}else{
//						sendMessage(NETWORK_NAME_NODATA,null);
//					}
//				}else{
//					sendMessage(NETWORK_NAME_NODATA,null);
//				}
//
//
//			}else{
//				//解析出错
//				sendMessage(NETWORK_NAME_NODATA,null);
//			}
//		}else{
//			//网络出错
//			sendMessage(NETWORK_FAILD, ns.getErrorMessage());
//		}
//	}
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
						StoreQuerySearchActivity.this.sendMessage(NETWORK_NAME_SUCCEED, result.getMessage());	
					}else{
						StoreQuerySearchActivity.this.sendMessage(NETWORK_NAME_NODATA,null);
					}
				}else{
					StoreQuerySearchActivity.this.sendMessage(NETWORK_NAME_NODATA,null);
				}
			}
			
			@Override
			public void onParseFailure(String errorCode, String errorMsg) {
//				super.onParseFailure(errorCode, errorMsg);
				StoreQuerySearchActivity.this.sendMessage(NETWORK_NAME_NODATA,null);
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				super.onFailure(arg0, arg1, arg2, arg3);
				StoreQuerySearchActivity.this.sendMessage(NETWORK_NAME_NODATA,null);
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
		StoreQuerySearchActivity context;
		public ShopsListAdapter(StoreQuerySearchActivity context, List<Shop>  shops){
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
			View main=convertView.findViewById(R.id.item);
			main.setVisibility(View.VISIBLE);
			convertView.findViewById(R.id.item_top).setVisibility(View.GONE);
			ImageView like1=(ImageView)main.findViewById(R.id.shoplistlike1);
			ImageView like2=(ImageView)main.findViewById(R.id.shoplistlike2);
			ImageView like3=(ImageView)main.findViewById(R.id.shoplistlike3);
			ImageView like4=(ImageView)main.findViewById(R.id.shoplistlike4);
			ImageView like5=(ImageView) main.findViewById(R.id.shoplistlike5);
			ImageView shoplistpicView=(ImageView) main.findViewById(R.id.shoplistpicView);
			shoplistpicView.setImageBitmap(getItem(position).getSamllbtm());

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
			}if((getItem(position).getAddress())!=null){		
				place.setText(getItem(position).getAddress());
			}if((getItem(position).getServiceScope())!=null){		
				service.setText(getItem(position).getServiceScope());
			}if((getItem(position).getDistance())+""!=null){		
				distance.setText("距离："+getItem(position).getDistance()+"km");
			}
			name.setText(getItem(position).getName());
			final String url=getItem(position).getBigImageUrl();
			shoplistpicView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					new Thread(){
						public void run(){
							if(BitmapCache.movePicToDisplay2(url)){
								Intent intento = new Intent(Intent.ACTION_VIEW);
								Uri uri = Uri.parse("file://"+android.os.Environment.getExternalStorageDirectory() + "/.tonggou/buffer.jpg");
								intento.setDataAndType(uri, "image/*");
								StoreQuerySearchActivity.this.startActivity(intento);
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
	protected void sendMessage(int what, String content) {
		if (what < 0) {
			what = BaseActivity.SEND_MESSAGE;
		}
		Message msg = Message.obtain(handler, what, content);
		if(msg!=null){
			msg.sendToTarget();
		}
	}public void addPhoto(ArrayList<Shop> scanBooks, int first, int count) {
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

				if ( concise.getSamllbtm() == null &&imageUrlsmall!= null) {
					concise.setSamllbtm(getPicture(imageUrlsmall));
				}
				if (first==0&&concise.getBigbtm() == null &&imageUrlbig!= null) {
					concise.setBigbtm(getPicture(imageUrlbig));
				}
				sendMessage(IMAGE_FLUSH,null);
			}
		}
	}public void removePhoto(ArrayList<Shop> scanBooks, int first, int count) {
		/*
		 * 关于后面的先置空后回收的原因，考虑这种情形：
		 *
		 * 第一次滚动停止：T1开始加载6-10，T2开始回收0-5和11-15
		 *
		 * 第二次滚动停止：T3开始加载11-15（但此时可能T2还被被调度，T3在它前面调度了）
		 * T3开始加载，然后T2开始回收。在T2刚好回收图片还没置空前，控制权被切换给T3，此时
		 * T3判断到图片还不是空的，则将其给ListView显示。此时就会引起异常，即显示已回收的
		 * 图片。关键就是保证回收和置空是原子的
		 */
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
	}public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==6060){		
			if(allShops!=null){
				allShops.clear();                                     //清空数据
			}
			if(allShopsIntent!=null){
				allShopsIntent.clear();                                     //清空数据
			}
			progressBar.setVisibility(View.GONE);
			allShopsIntent=(ArrayList<ShopIntent>)data.getSerializableExtra("tonggou.shoplist");
			for(int i=0;i<allShopsIntent.size();i++){
				allShops.add(setShop(allShopsIntent.get(i)));
			}
			progressBar.setVisibility(View.GONE);	
			shopsListAdapter = new ShopsListAdapter(StoreQuerySearchActivity.this,allShops);   			
			shopsListView.setAdapter(shopsListAdapter);
			setImages();
			shopsListAdapter.notifyDataSetChanged();  
		}
	}
}
