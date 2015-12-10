package com.tonggou.andclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
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
import android.widget.ScrollView;
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
import com.tonggou.andclient.myview.AbsViewHolderAdapter;
import com.tonggou.andclient.network.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.andclient.network.request.ShopDetailRequest;
import com.tonggou.andclient.util.ListViewRun;
import com.tonggou.andclient.vo.AppShopCommentDTO;
import com.tonggou.andclient.vo.MemberService;
import com.tonggou.andclient.vo.Shop;
import com.tonggou.andclient.vo.ShopServiceCategoryDTO;
public class StoreDetilActivity extends BaseActivity {
	private static final int  NETWORK_FAILD=-1;
	private static final int  NETWORK_SUCCEED=0x003;
	private static final int  NETWORK_NODATA=0x004;
	private MapView mMapView = null;
	private ImageView like1,like2,like3,like4,like5,likes1,likes2,likes3,likes4,likes5,lView1,lView2,lView3,lView4,lView5;
	private String tell,tell2,userNo,shopId,shopnameStr,shopmeterStr,conditionStr;
	private TextView shopcall,shopname,shopmeter,shoppalce_tv,shopservice_tv,shoplistlikesorce/*,shopkeepname1,shopkeepname2,shopkeeptimes1,shopkeeptimes2,shopkeeptime1,shopkeeptime2*/;
	private Handler handler;
	private GeoPoint point;
	private View back,map,nework,call;
	private Double lat,lot;
	private Shop shopdetil;
	private ProgressBar progressBar;
	private	String[] phones;
	private ScrollView scrollView;
	private ListViewRun evaluatelistView;
    private LinearLayout memberlin,notmemberlin,storeevaluatelin;
    private TextView gradeText,evaluatetext,dmpjText,dmpjtextTextView,moreEvaluateText;
    private EvaluateAdapter evaluateAdapter;
    private int commentCounts;
    private List<AppShopCommentDTO> appShopCommentDTOs;
	
	private ArrayList<ShopServiceCategoryDTO> mytypes ; //该店提供的服务列表
	@SuppressLint("HandlerLeak")
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.storedetil);
		scrollView = (ScrollView)findViewById(R.id.storescrollview);
		dmpjText = (TextView)findViewById(R.id.dmpj1_text);
		dmpjtextTextView = (TextView)findViewById(R.id.dmpj_text);
		userNo=getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.NAME, null);
		shopId=getIntent().getStringExtra("tonggou.shopId");
		shopnameStr=getIntent().getStringExtra("tonggou.shopname");
		shopmeterStr=getIntent().getStringExtra("tonggou.shopmeter");
		conditionStr = getIntent().getStringExtra("tonggou.conditionStr");
		
		
		progressBar=(ProgressBar) findViewById(R.id.shopdetilmappro);
		progressBar.setVisibility(View.VISIBLE);
		
		afterViews();
	
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
	protected void afterViews(){
		
		TongGouApplication app = (TongGouApplication)this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			/**
			 * 如果BMapManager没有初始化则初始化BMapManager
			 */
			app.mBMapManager.init(TongGouApplication.strKey,new TongGouApplication.MyGeneralListener());
		}
		// 如果使用地图SDK，请初始化地图Activity
		mMapView = (MapView)findViewById(R.id.mapView);
		moreEvaluateText = (TextView)findViewById(R.id.moreEvaluateText);


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
		
		memberlin = (LinearLayout)findViewById(R.id.Member_lin);
		notmemberlin = (LinearLayout)findViewById(R.id.notMember_lin);
		storeevaluatelin = (LinearLayout)findViewById(R.id.storeevaluate_lin);
		gradeText = (TextView)findViewById(R.id.fenshu_text);
		likes1=(ImageView)findViewById(R.id.shoplistlik1);
		likes2=(ImageView)findViewById(R.id.shoplistlik2);
		likes3=(ImageView)findViewById(R.id.shoplistlik3);
		likes4=(ImageView)findViewById(R.id.shoplistlik4);
		likes5=(ImageView)findViewById(R.id.shoplistlik5);

		setListener();
		
	}
	
	private void setListener(){
		
		
		moreEvaluateText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				truntoActivity();
			}
		});
		
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
		nework=findViewById(R.id.yuyue_text);
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
		evaluatetext = (TextView) findViewById(R.id.evaluate_text);
		
		evaluatelistView = (ListViewRun)findViewById(R.id.evaluate_list);
//		evaluatelistView.setDividerHeight(0);
		evaluateAdapter = new EvaluateAdapter(StoreDetilActivity.this,new ArrayList<AppShopCommentDTO>(), R.layout.storedetil_evaluate_item);
		evaluatelistView.setAdapter(evaluateAdapter);

		evaluatelistView.setParentScrollView(scrollView);
		//固定高度，不然没有滚动效果
		evaluatelistView.setMaxHeight(8000);
		
		
		call=findViewById(R.id.call_lin);
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
		
			}
		});
	}
	/**
	 * 根据店铺 店铺id和用户名获取店铺相关信息
	 * @param shopId
	 * @param userNo
	 */
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
				commentCounts = result.getCommentCount();
				appShopCommentDTOs  = result.getAppShopCommentDTOs();
				evaluateAdapter.update(appShopCommentDTOs);
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
		


	}
	
	private void onRequestSuccess() {
		
		if(commentCounts != 0){
			dmpjText.setText("最近评价(共"+commentCounts+"条)");
			dmpjtextTextView.setText("最近评价(共"+commentCounts+"条)");
			evaluatetext.setVisibility(View.GONE);
			evaluatelistView.setVisibility(View.VISIBLE);
			moreEvaluateText.setVisibility(View.VISIBLE);
		}else{
			evaluatetext.setVisibility(View.VISIBLE);
			evaluatelistView.setVisibility(View.GONE);
			moreEvaluateText.setVisibility(View.GONE);
		}
		
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
		notmemberlin.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if(commentCounts != 0 ){
					truntoActivity();
				}else{
					TongGouApplication.showToast("暂时没有评论消息");
				}
			}
		});

		storeevaluatelin.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if(commentCounts != 0 ){
					truntoActivity();
				}else{
					TongGouApplication.showToast("暂时没有评论消息");
				}
			}
		});
		

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
			
			memberlin.setVisibility(View.VISIBLE);
			notmemberlin.setVisibility(View.GONE);
			storeevaluatelin.setVisibility(View.VISIBLE);
			
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
			
			gradeText.setText(shopdetil.getTotalScore()+"分");
			likes1.setVisibility(View.VISIBLE);
			likes2.setVisibility(View.VISIBLE);
			likes3.setVisibility(View.VISIBLE);
			likes4.setVisibility(View.VISIBLE);
			likes5.setVisibility(View.VISIBLE);
			setLikes(shopdetil.getTotalScore()+"",likes1,likes2,likes3,likes4,likes5);
			
		}else{
			like1.setVisibility(View.GONE);
			like2.setVisibility(View.GONE);
			like3.setVisibility(View.GONE);
			like4.setVisibility(View.GONE);
			like5.setVisibility(View.GONE);
			shoplistlikesorce.setText("暂无评分");
			
			likes1.setVisibility(View.GONE);
			likes2.setVisibility(View.GONE);
			likes3.setVisibility(View.GONE);
			likes4.setVisibility(View.GONE);
			likes5.setVisibility(View.GONE);
			gradeText.setText("暂无评分");

		}
		tell=shopdetil.getMobile();
		tell2=shopdetil.getLandLine();
		
		
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
	
	class EvaluateAdapter extends AbsViewHolderAdapter<AppShopCommentDTO>{

		public EvaluateAdapter(Context context, List<AppShopCommentDTO> data,int layoutRes) {
			super(context, data, layoutRes);
			
		}

		@Override
		protected void setData(int pos, View convertView,AppShopCommentDTO itemData) {
			TextView userNameText = getViewFromHolder(convertView, R.id.userName_text);
			TextView timeText = getViewFromHolder(convertView, R.id.time_text);
			TextView shoplistlikesorceText = getViewFromHolder(convertView, R.id.pjshoplistlikesorce);
			TextView evaluateText = (TextView)convertView.findViewById(R.id.evaluate_content_text);
			
			userNameText.setText(itemData.getCommentatorName());
			timeText.setText(itemData.getCommentTimeStr());
			evaluateText.setText(itemData.getCommentContent());

			lView1 = getViewFromHolder(convertView,R.id.shoplistlike11);
			lView2 = getViewFromHolder(convertView,R.id.shoplistlike21);
			lView3 = getViewFromHolder(convertView,R.id.shoplistlike31);
			lView4 = getViewFromHolder(convertView,R.id.shoplistlike41);
			lView5 = getViewFromHolder(convertView,R.id.shoplistlike51);

			if(itemData.getCommentScore() != 0){
				shoplistlikesorceText.setText(itemData.getCommentScore()+"分");
				lView1.setVisibility(View.VISIBLE);
				lView2.setVisibility(View.VISIBLE);
				lView3.setVisibility(View.VISIBLE);
				lView4.setVisibility(View.VISIBLE);
				lView5.setVisibility(View.VISIBLE);
				setLikes(itemData.getCommentScore()+"",lView1,lView2,lView3,lView4,lView5);
			}else{
				lView1.setVisibility(View.GONE);
				lView2.setVisibility(View.GONE);
				lView3.setVisibility(View.GONE);
				lView4.setVisibility(View.GONE);
				lView5.setVisibility(View.GONE);
				shoplistlikesorceText.setText("暂无评分");
			}

		}
	}
	private void truntoActivity(){
		Intent intent = new Intent();
		intent.putExtra("mShopid", shopId);
		intent.setClass(StoreDetilActivity.this, ShopEvaluateActivity.class);
		startActivity(intent);
	}

}
