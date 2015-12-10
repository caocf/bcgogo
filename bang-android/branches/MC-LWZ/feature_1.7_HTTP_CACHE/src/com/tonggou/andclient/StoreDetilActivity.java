package com.tonggou.andclient;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.jsonresponse.StoreDetilResponse;
import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.network.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.andclient.network.request.ShopDetailRequest;
import com.tonggou.andclient.parse.StoreDetilParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.SomeUtil;
import com.tonggou.andclient.vo.MemberService;
import com.tonggou.andclient.vo.OrderItem;
import com.tonggou.andclient.vo.Shop;
import com.tonggou.andclient.vo.ShopServiceCategoryDTO;
public class StoreDetilActivity extends BaseActivity {
	private static final int  NETWORK_FAILD=-1;
	private static final int  NETWORK_SUCCEED=0x003;
	private static final int  NETWORK_NODATA=0x004;
	private MapView mMapView = null;
	private ImageView like1,like2,like3,like4,like5;
	private String tell,tell2,userNo,shopId,shopnameStr,shopmeterStr,conditionStr;
	private TextView shopcall,shopname,shopmeter,shoppalce_tv,shopservice_tv,shoplistlikesorce/*,shopkeepname1,shopkeepname2,shopkeeptimes1,shopkeeptimes2,shopkeeptime1,shopkeeptime2*/;
	private Handler handler;
	private GeoPoint point;
	private View back,map,nework,call;
	private Double lat,lot;
	private Shop shopdetil;
	private ProgressBar progressBar;
	private	String[] phones;

	private ArrayList<ShopServiceCategoryDTO> mytypes ; //该店提供的服务列表
	@SuppressLint("HandlerLeak")
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.storedetil);
		userNo=getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.NAME, null);
		shopId=getIntent().getStringExtra("tonggou.shopId");
		shopnameStr=getIntent().getStringExtra("tonggou.shopname");
		shopmeterStr=getIntent().getStringExtra("tonggou.shopmeter");
		conditionStr = getIntent().getStringExtra("tonggou.conditionStr");
		
		progressBar=(ProgressBar) findViewById(R.id.shopdetilmappro);
		progressBar.setVisibility(View.VISIBLE);
		TongGouApplication app = (TongGouApplication)this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(getApplicationContext());
			/**
			 * 如果BMapManager没有初始化则初始化BMapManager
			 */
			app.mBMapManager.init(TongGouApplication.strKey,new TongGouApplication.MyGeneralListener());
		}
		// 如果使用地图SDK，请初始化地图Activity
		mMapView = (MapView)findViewById(R.id.mapView);


		shopname=(TextView) findViewById(R.id.shopname);
		shopmeter=(TextView) findViewById(R.id.shopmeter);
		shoppalce_tv=(TextView) findViewById(R.id.shoppalce_tv);

		shopservice_tv=(TextView) findViewById(R.id.shopservice_tv);
		shoplistlikesorce=(TextView) findViewById(R.id.shoplistlikesorce);
		like1=(ImageView)findViewById(R.id.shoplistlike1);
		like2=(ImageView)findViewById(R.id.shoplistlike2);
		like3=(ImageView)findViewById(R.id.shoplistlike3);
		like4=(ImageView)findViewById(R.id.shoplistlike4);
		like5=(ImageView)findViewById(R.id.shoplistlike5);

		//		shopkeepname1=(TextView) findViewById(R.id.shopkeep_name1);
		//		shopkeepname2=(TextView) findViewById(R.id.shopkeep_name2);
		//		shopkeeptimes1=(TextView) findViewById(R.id.shopkeep_times1);
		//		shopkeeptimes2=(TextView) findViewById(R.id.shopkeep_times2);
		//		shopkeeptime1=(TextView) findViewById(R.id.shopkeep_time1);
		//		shopkeeptime2=(TextView) findViewById(R.id.shopkeep_time2);

		shopcall=(TextView) findViewById(R.id.shopapp_call_num);
		back=findViewById(R.id.left_button);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StoreDetilActivity.this.finish();
			}
		});

		map=findViewById(R.id.right_button);
		map.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(StoreDetilActivity.this,StoreDetilMapActivity.class);
				intent.putExtra("lat", lat);
				intent.putExtra("lot", lot);
				startActivity(intent);
				//StoreDetilActivity.this.finish();


			}
		});
		nework=findViewById(R.id.shopapp_nework);
		nework.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if( !TongGouApplication.getInstance().isLogin() ) {
					
					Intent intent = new Intent();
					intent.setClass(StoreDetilActivity.this, LoginActivity.class);
					startActivity(intent);
					return;
				}
				
				//预约服务
				if(shopdetil!=null){
					Intent intent=new Intent(StoreDetilActivity.this,AppointmentNetWork.class);
					if(shopdetil.getName()!=null&&!"".equals(shopdetil.getName())){
						intent.putExtra("tonggou.shop.name", shopdetil.getName());
					}else{
						intent.putExtra("tonggou.shop.name", shopnameStr+"");
					}
					intent.putExtra("tonggou.shop.categorys",mytypes);

					intent.putExtra("tonggou.shop.id", shopId);
					intent.putExtra("tonggou.conditionStr",conditionStr);
					startActivity(intent);  
				}
			}
		});
		call=findViewById(R.id.shopapp_call);
		call.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub	
				if(tell2!=null&&!"".equals(tell2)){

					// TODO Auto-generated method stub
					List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
					phones=new String[]{tell,tell2};	

					for(int i=0;i<phones.length;i++){		
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("name",phones[i].trim());
						list.add(map);
					}
					View shoutCamrea = LayoutInflater.from(StoreDetilActivity.this).inflate(R.layout.pop_list3, null);
					shoutCamrea.setBackgroundDrawable(null);
					ListView synList = (ListView)shoutCamrea.findViewById( R.id.server_type_list );
					SimpleAdapter adapter = new SimpleAdapter(StoreDetilActivity.this,list,
							R.layout.popview_item,
							new String[]{"name"},
							new int[]{R.id.popview_name});
					synList.setAdapter(adapter);
					synList.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
								long arg3) {
							// TODO Auto-generated method stub
							toPhone(phones[arg2]);
						}

					});

					AlertDialog	phonenums = new AlertDialog.Builder(StoreDetilActivity.this) 
					.setView(shoutCamrea)
					.setTitle(getString(R.string.choosephone)) 
					.setNegativeButton(getString(R.string.exit_cancel), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {

						}
					}).show();
				
				}else{

					toPhone(tell);
				}
				/*String phonenumber;
				phonenumber=tell;
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				if(phonenumber.indexOf("，")>0){
					if(phone.getText().toString().length()>=27){
						phonenumber=phone.getText().toString().substring(0,27);
						phones=phonenumber.split("，");							
					}else{
						phones=phonenumber.split("，");		
					}
					for(int i=0;i<phones.length;i++){		
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("name",phones[i].trim());
						list.add(map);
					}
					View shoutCamrea = LayoutInflater.from(StoreDetilActivity.this).inflate(R.layout.dialog, null);
					ListView synList = (ListView)shoutCamrea.findViewById( R.id.remindedlist );
					SimpleAdapter adapter = new SimpleAdapter(StoreDetilActivity.this,list,
							R.layout.dialogitem,
							new String[]{"name"},
							new int[]{R.id.dialogname});
					synList.setAdapter(adapter);
					synList.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
								long arg3) {
							// TODO Auto-generated method stub
							toPhone(phones[arg2]);
						}

					});
					phonenums = new AlertDialog.Builder(ShopDetilActivity.this) 
					.setView(shoutCamrea)
					.setTitle(getString(R.string.choosephone)) 
					.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {

						}
					}).show();

				}else{
					if(phonenumber.indexOf(",")>0){
						if(phone.getText().toString().length()>=27){
							phonenumber=phone.getText().toString().substring(0,27);
							phones=phonenumber.split(",");							
						}else{
							phones=phonenumber.split(",");		
						}
						for(int i=0;i<phones.length;i++){		
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("name",phones[i].trim());
							list.add(map);
						}
						View shoutCamrea = LayoutInflater.from(ShopDetilActivity.this).inflate(R.layout.dialog, null);
						ListView synList = (ListView)shoutCamrea.findViewById( R.id.remindedlist );
						SimpleAdapter adapter = new SimpleAdapter(ShopDetilActivity.this,list,
								R.layout.dialogitem,
								new String[]{"name"},
								new int[]{R.id.dialogname});
						synList.setAdapter(adapter);
						synList.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
								// TODO Auto-generated method stub
								toPhone(phones[arg2]);
							}

						});
						phonenums = new AlertDialog.Builder(ShopDetilActivity.this) 
						.setView(shoutCamrea)
						.setTitle(getString(R.string.choosephone)) 
						.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {

							}
						}).show();

					}else{
						toPhone(phonenumber);
					}
				}
				 */
			}
		});
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg){		
				progressBar.setVisibility(View.GONE);
				switch(msg.what){

				case NETWORK_SUCCEED: 
					
					break;				
				case NETWORK_NODATA :
					Toast.makeText(StoreDetilActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					break;
				case NETWORK_FAILD :
					Toast.makeText(StoreDetilActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					break;				

				}
			}
		};
		
		getStoreDetil(shopId,userNo);
	}
	public void getStoreDetil(String shopId,String userNo) {
		showLoadingDialog("加载中...");
		ShopDetailRequest request = new ShopDetailRequest();
		request.setGuestMode( !TongGouApplication.getInstance().isLogin() );
		request.setApiParams(userNo, shopId);
		request.setGuestApiParams(shopId);
		request.doRequest(this, new AsyncJsonBaseResponseParseHandler<StoreDetilResponse>() {

			@Override
			public void onParseSuccess(StoreDetilResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				shopdetil = result.getShop();
				if(shopdetil != null){
					mytypes = shopdetil.getProductCategoryList();
					onRequestSuccess();
				} else {
					TongGouApplication.showLongToast( result.getMessage() );
				}
			}
			
			@Override
			public Class<StoreDetilResponse> getTypeClass() {
				return StoreDetilResponse.class;
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				dismissLoadingDialog();
			}
			
		});
		
//		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/shop/detail/"+shopId+"/userNo/"+URLEncoder.encode(userNo);
//		StoreDetilParser storeDetilParser = new StoreDetilParser();		
//		NetworkState ns = Network.getNetwork(StoreDetilActivity.this).httpGetUpdateString(url,storeDetilParser);	
//
//		if(ns.isNetworkSuccess()){
//			if(storeDetilParser.isSuccessfull()){
//				shopdetil=storeDetilParser.getStoreDetilResponse().getShop();
//				if(shopdetil!=null){
//					mytypes = shopdetil.getProductCategoryList();
//					sendMessage(NETWORK_SUCCEED, storeDetilParser.getStoreDetilResponse().getMessage());
//				}else{
//					sendMessage(NETWORK_NODATA,storeDetilParser.getStoreDetilResponse().getMessage());
//				}
//
//			}else{
//				//解析出错
//				sendMessage(NETWORK_NODATA, storeDetilParser.getErrorMessage());
//			}
//		}else{
//			//网络出错
//			sendMessage(NETWORK_FAILD, ns.getErrorMessage());
//		}


	}
	
	private void onRequestSuccess() {
		if(shopdetil.getName()!=null&&!"".equals(shopdetil.getName())){
			shopname.setText(shopdetil.getName());
		}else{
			shopname.setText(shopnameStr+"");
		}
		if(shopdetil.getDistance()!=0.0){
			shopmeter.setText(getResources().getString(R.string.shop_distance)+shopdetil.getDistance()+"km");
		}else{
			if(shopmeterStr!=null&&!"".equals(shopmeterStr))
			{
				shopmeter.setText(getResources().getString(R.string.shop_distance)+shopmeterStr+"km");
			}
		}


		if(shopdetil.getAddress()!=null&&!"".equals(shopdetil.getAddress())){
			shoppalce_tv.setText(shopdetil.getAddress());
		}
		if(shopdetil.getServiceScope()!=null&&!"".equals(shopdetil.getServiceScope())){
			shopservice_tv.setText(shopdetil.getServiceScope());
		}
		if(shopdetil.getMemberInfo()!=null&&!"".equals(shopdetil.getMemberInfo().getMemberServiceList().size()==2)){
			findViewById(R.id.tail).setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.shop_balance)).setText(String.format("￥ %.1f", shopdetil.getMemberInfo().getBalance()));
			((TextView)findViewById(R.id.member_no)).setText( shopdetil.getMemberInfo().getMemberNo());
			//						if(shopdetil.getMemberInfo().getMemberServiceList().get(0).getConsumeType()!=null&&!"".equals(shopdetil.getMemberInfo().getMemberServiceList().get(0).getConsumeType())){
			//							shopkeepname1.setText(shopdetil.getMemberInfo().getMemberServiceList().get(0).getConsumeType());
			//						}
			//						if(shopdetil.getMemberInfo().getMemberServiceList().get(1).getConsumeType()!=null&&!"".equals(shopdetil.getMemberInfo().getMemberServiceList().get(1).getConsumeType())){
			//							shopkeepname2.setText(shopdetil.getMemberInfo().getMemberServiceList().get(1).getConsumeType());
			//						}
			//						shopkeeptimes1.setText(shopdetil.getMemberInfo().getMemberServiceList().get(0).getTimes()+"");
			//						shopkeeptimes2.setText(shopdetil.getMemberInfo().getMemberServiceList().get(1).getTimes()+"");
			//						shopkeeptime1.setText(shopdetil.getMemberInfo().getMemberServiceList().get(0).getDeadline()+"");
			//						shopkeeptime2.setText(shopdetil.getMemberInfo().getMemberServiceList().get(1).getDeadline()+"");
		}

		//vip 信息
		if(shopdetil.getMemberInfo()!=null&&shopdetil.getMemberInfo().getMemberServiceList()!=null
				&&shopdetil.getMemberInfo().getMemberServiceList().size()>0){						
			LinearLayout orderItems = (LinearLayout)findViewById(R.id.shop_vip_item);
			for(int i=0;i<shopdetil.getMemberInfo().getMemberServiceList().size();i++){
				MemberService ot = shopdetil.getMemberInfo().getMemberServiceList().get(i);
				View tempItem = getLayoutInflater().inflate(R.layout.shopdetil_vip_item, null);


				TextView content = (TextView)tempItem.findViewById(R.id.item_content);
				if(ot.getServiceName()!=null){
					content.setText(ot.getServiceName());
				}
				TextView type = (TextView)tempItem.findViewById(R.id.item_type);

				//type.setText(ot.getTimes()+"");
				type.setText(ot.getTimesStr());

				TextView amount = (TextView)tempItem.findViewById(R.id.item_amount);
				//amount.setText(SomeUtil.longToStringDate(ot.getDeadline()+""));
				amount.setText(ot.getDeadlineStr());
				orderItems.addView(tempItem,new LayoutParams(LayoutParams.FILL_PARENT ,LayoutParams.WRAP_CONTENT ));
			}
			findViewById(R.id.tail).setVisibility(View.VISIBLE);
		}
		if(shopdetil.getTotalScore()!=0){		
			shoplistlikesorce.setText(shopdetil.getTotalScore()+"分");
			like1.setVisibility(View.VISIBLE);
			like2.setVisibility(View.VISIBLE);
			like3.setVisibility(View.VISIBLE);
			like4.setVisibility(View.VISIBLE);
			like5.setVisibility(View.VISIBLE);
			setLikes(shopdetil.getTotalScore()+"",like1,like2,like3,like4,like5);
		}else{
			like1.setVisibility(View.GONE);
			like2.setVisibility(View.GONE);
			like3.setVisibility(View.GONE);
			like4.setVisibility(View.GONE);
			like5.setVisibility(View.GONE);
			shoplistlikesorce.setText("暂无评分");;

		}
		tell=shopdetil.getMobile();
		tell2=shopdetil.getLandLine();
		if(tell!=null&&!"".equals(tell)){
			shopcall.setText(tell);
		}else{
			if(tell2!=null&&!"".equals(tell2)){
				shopcall.setText(tell2);
				tell=tell2;
				tell=null;
			}
		}
		if( shopdetil == null || shopdetil.getCoordinate() == null) {
			return;
		}
		String[] location=shopdetil.getCoordinate().split(",");			
		point =new GeoPoint((int)(Double.parseDouble(location[1])*1e6), (int)(Double.parseDouble(location[0])*1e6));
		if(mMapView != null) { // 当界面已经退出后，线程还没有跑完，刚好调用了该方法，就会空指向
			try{
			mMapView.getController().setCenter(point);
			mMapView.getController().setZoom(16);
			mMapView.getController().animateTo(point);
			}catch(NullPointerException ex){						
			}
			// 为maker定义位置和边界   
			// 将标记添加到图层中（可添加多个OverlayItem）   
			Drawable marker = StoreDetilActivity.this.getResources().getDrawable(R.drawable.position);  
			marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());  
			ItemizedOverlay<OverlayItem> itemOverlay = new ItemizedOverlay<OverlayItem>(null, mMapView);

			OverlayItem overlayItem = new OverlayItem(point,"", "");  
			overlayItem.setMarker(marker);  
			itemOverlay.addItem(overlayItem);
			if(mMapView!=null){
				if(mMapView.getOverlays()!=null){
					if(itemOverlay!=null){
						mMapView.getOverlays().add(itemOverlay);  
					}
				}
			}
		}

		lot=Double.parseDouble(location[0]);
		lat=Double.parseDouble(location[1]);
		progressBar.setVisibility(View.GONE);
	}


	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		handler = null;
		mMapView.destroy();
		super.onDestroy();
	}
	
	protected void sendMessage(int what, String content) {
		if( isFinishing() || handler == null) {
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
	
	public void toPhone(String PhoneNum){
		if(PhoneNum!=null ){
			Uri uri = Uri.parse("tel:"+PhoneNum); 
			Intent it = new Intent(Intent.ACTION_DIAL, uri);   
			startActivity(it);  			     	        
		}
	}
}
