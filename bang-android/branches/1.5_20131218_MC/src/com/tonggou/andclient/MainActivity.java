package com.tonggou.andclient;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tonggou.andclient.app.BaseConnectOBDService;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.app.TongGouApplication.OnBindOBDListener;
import com.tonggou.andclient.app.TongGouApplication.OnBindShopListener;
import com.tonggou.andclient.app.TongGouService;
import com.tonggou.andclient.app.UpdateFaultDic;
import com.tonggou.andclient.myview.ScrollLayout;
import com.tonggou.andclient.network.DefaultUpdateCheck;
import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.parse.LoginParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.SaveDB;
import com.tonggou.andclient.vo.CarCondition;
import com.tonggou.andclient.vo.OBDDevice;
import com.tonggou.andclient.vo.VehicleInfo;
import com.tonggou.andclient.vo.Version;

public class MainActivity extends BaseActivity implements OnBindOBDListener, OnBindShopListener {
	
	public static final String ACTION_CHANGE_TITLE = "com.tonggou.andclient.action.CHANGE_TITLE";
	public static final String KEY_ARG_TITLE = "title";
	
	private static final int REQUEST_ENABLE_BT = 123;
	
	private static final int  NETWORK_FAILD=-1;
	private static final int  PARSE_SUCCEED=0x001;
	private static final int  PARSE_FAILD=0x002;
	private static final int  ALERT_NEW_VERSION = 3;
	
	private static final int  LOGOUT_EXIT = 4;
	
	public static String defaultBrandAndModle = "";    
	
	
	public static boolean ifAutoLogin = true;     //是否自动登录(因为自动登录时跳过login页面)
	public static boolean haveFaultCode = false;  //是否有故障码
	
	private Handler handler;

//	private ScrollLayout scrollLayout;
	private int[] imageId;
	private ImageView carStatusIcon;
	private TextView carName;
	private AlertDialog mScanOBDDialog;
	
	private BroadcastReceiver mChangeTitleReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if( ACTION_CHANGE_TITLE.equals( intent.getAction() )) {
				TongGouApplication.showLog("换标题 : " + intent.getStringExtra(KEY_ARG_TITLE));
				carName.setText(intent.getStringExtra(KEY_ARG_TITLE));
			}
		}
		
	};
	
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter filter = new IntentFilter(ACTION_CHANGE_TITLE);
		registerReceiver(mChangeTitleReceiver, filter);
		Intent parentIntent = getParent().getIntent();
		if( parentIntent != null ) {
			String flag = parentIntent.getStringExtra(HomePageActivity.EXTRA_FLAG_PREVIOUS_ACTIVITY_NAME);
			if( flag != null && flag.equalsIgnoreCase(RegisterActivity.class.getName()) ) {
				showScanOBDDialog();
			}
		}

		setContentView(R.layout.main);
		View carStatusbg = findViewById(R.id.main_car_condition_state);
		carStatusbg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(!TongGouApplication.connetedOBD){
					clickUnconnectCarAction();
                }
			}
		});
		
		carStatusIcon = (ImageView) findViewById(R.id.main_car_status);
		carStatusIcon.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent =new Intent(MainActivity.this,CarConditionQueryActivity.class);
				startActivity(intent);

			}
		});
		carName = (TextView) findViewById(R.id.main_car_name);
		TongGouApplication.showLog("carName    -- " + defaultBrandAndModle);
		if( !TextUtils.isEmpty( defaultBrandAndModle ) && !defaultBrandAndModle.contains("null")) {
			carName.setText(defaultBrandAndModle);
		}
		carName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent =new Intent(MainActivity.this,BindCarsActivity.class);
				startActivity(intent);
			}
		});
		DisplayMetrics dm = new DisplayMetrics();
        MainActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int hi = dm.heightPixels;
//		Rect rect = new Rect();
//        MainActivity.this.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
//        int topS = rect.bottom;//状态栏高度
		
    
//		scrollLayout = (ScrollLayout) findViewById(R.id.menu_scrollLayout);
		
//		scrollLayout.setScreenHeight(hi-100);  //762
//		scrollLayout.setVisiableViewNumber(3);
//		scrollLayout.setVisiableChildScale(1.0f, 0.2f, 1f, 0.2f);
//		scrollLayout.init(getViewList(),this);
		
		
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg){		
				switch(msg.what){
				case NETWORK_FAILD: 
					Toast.makeText(MainActivity.this,(String)msg.obj,Toast.LENGTH_LONG).show();
					break;
				case LOGOUT_EXIT: 
					Toast.makeText(MainActivity.this,(String)msg.obj,Toast.LENGTH_LONG).show();
					//////////////////////////////////////
					exit();
					deInit();
					///////////////////////////////////////
					new Thread(){
						public void run(){
							//停掉连接obd
							Intent intent = new Intent();
							intent.setAction(TongGouService.TONGGOU_ACTION_START);
					        intent.putExtra("com.tonggou.server","STOP");
					        sendBroadcast(intent);
						}
					}.start();
					
					Intent toLogin = new Intent(MainActivity.this, LoginActivity.class);
					toLogin.putExtra("tonggou.loginExpire", (String)msg.obj);
					toLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(toLogin);
					break;
					
				case PARSE_SUCCEED: 
					Toast.makeText(MainActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					break;
				case PARSE_FAILD: 
					Toast.makeText(MainActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					break;
				case ALERT_NEW_VERSION:		
					if(!ifAutoLogin){
						return;
					}
					if(DefaultUpdateCheck.versionAction!=null&&DefaultUpdateCheck.versionAction.getAction()==Version.UPDATE_ACTION_FORCE){  //强制升级
						new AlertDialog.Builder(MainActivity.this)
						.setTitle("强制升级提示")
						.setMessage(DefaultUpdateCheck.versionAction.getMessage()==null?"":DefaultUpdateCheck.versionAction.getMessage())
						.setPositiveButton("是", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {										
								//forceUpdateNewVersion = true;

								////////////////////////////////////////////////////////////////
								String downUrl = DefaultUpdateCheck.versionAction.getUrl();
								if(downUrl==null||"".equals(downUrl)){
									//url错误提示
									Toast.makeText(MainActivity.this, "url为空", Toast.LENGTH_LONG).show();
								}else{
									Uri uri = Uri.parse(downUrl);  
									Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
									startActivity(intent); 
								}
								////////////////////////////////////////////////////////////////
							}
						}).setNeutralButton("否", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {  //退出
								//NotificationManager notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
								//notiManager.cancel(0x7f030000);
								exit();
							}
						}).show();
					}else if(DefaultUpdateCheck.versionAction!=null&&DefaultUpdateCheck.versionAction.getAction()==Version.UPDAATE_ACTION_ALERT){     //提示升级
						AlertDialog updateAlert = new AlertDialog.Builder(MainActivity.this)
						.setTitle("升级提示")
						.setMessage(DefaultUpdateCheck.versionAction.getMessage()==null?"":DefaultUpdateCheck.versionAction.getMessage())
						.setPositiveButton("是", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {

								////////////////////////////////////////////////////////////////
								String downUrl = DefaultUpdateCheck.versionAction.getUrl();
								if(downUrl==null||"".equals(downUrl)){
									//url错误提示
									Toast.makeText(MainActivity.this, "url为空", Toast.LENGTH_LONG).show();
								}else{
									Uri uri = Uri.parse(downUrl);  
									Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
									startActivity(intent); 
								}
								/////////////////////////////////////////////////////////////////
							
							}
						}).setNeutralButton("否", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {

							}
						}).create();
						if(!isFinishing()) {
							updateAlert.show();
						}
					}							
					break;
				}
			}};
			
			
		if(ifAutoLogin){
			sendMessage(ALERT_NEW_VERSION, null);  //提示版本情况
			//后台登陆
			new Thread(){
				public void run(){
					login();
				}
			}.start();
		}else{
			doSomeAction();
		}

	}
	
	private void showScanOBDDialog() {
		dismissScanOBDDialog();
		mScanOBDDialog = new AlertDialog.Builder(this).create();
		mScanOBDDialog.setIcon(android.R.drawable.ic_dialog_info);
		mScanOBDDialog.setTitle("提示");
		mScanOBDDialog.setMessage("是否绑定 OBD ?");
		mScanOBDDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onScanOBD();
			}
		});
		mScanOBDDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onScanShop();
			}
		});
		if( !isFinishing() ) {
			mScanOBDDialog.show();
		}
	}
	
	private void dismissScanOBDDialog() {
		if( mScanOBDDialog != null && mScanOBDDialog.isShowing() ) {
			mScanOBDDialog.dismiss();
			mScanOBDDialog = null;
		}
	}
	
	private void onScanOBD() {
		TongGouApplication.getInstance().registerBindOBDListener(this);
		Intent intent = new Intent(this, ConnectOBDDialogActivity.class);
		startActivity(intent);
	}
	
	public void handleActivityResult(int requestCode, int resultCode, Intent data){  
       TongGouApplication.showLog(requestCode + "  " + resultCode);
	}
	
	public void onDestroy() {	      
       super.onDestroy();
//       if(scrollLayout != null){
//    	   scrollLayout.deInitSounds();
//       }
       dismissScanOBDDialog();
       TongGouApplication.getInstance().unregisterBindOBDListener(this);
       TongGouApplication.getInstance().unregisterBindShopListener(this);
       unregisterReceiver(mChangeTitleReceiver);
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
		TongGouApplication.showLog("request  " + requestCode + "  resultCode  " + resultCode);
		  if( resultCode != RESULT_OK ) {
			  return;
		  }
	      switch (requestCode) {
        	case REQUEST_ENABLE_BT:
	            // 当开打蓝牙返回        When the request to enable Bluetooth returns 
            	//连接obd
		        TongGouApplication.getInstance().queryVehicleList();
	            break;
	      }
    	}
	  
	  
	  /**
	 * 点击未连接成功车辆时的一些动作
	 */
	  private void clickUnconnectCarAction(){
		  BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		  if(mBtAdapter!=null){
		        if (!mBtAdapter.isEnabled()) {
		        	Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);                         //打开蓝牙
		        }	        
	       }else{		  
			  if(BaseConnectOBDService.connetState==1||BaseConnectOBDService.connetState==-1){
	      		new AlertDialog.Builder(MainActivity.this) 		
	              .setTitle(getString(R.string.exit_title)) 
	              .setMessage("是否要连接车辆？") 
	              .setPositiveButton(getString(R.string.exit_submit), new DialogInterface.OnClickListener() { 
	                  public void onClick(DialogInterface dialog, int whichButton) {
	                	  TongGouApplication.getInstance().queryVehicleList();
	              } 
	              }).setNeutralButton(getString(R.string.exit_cancel), new DialogInterface.OnClickListener(){ 
	              	public void onClick(DialogInterface dialog, int whichButton){ 
	              	} 
	              }).show();
				}else{
					Toast.makeText(MainActivity.this,"正在连接车辆",Toast.LENGTH_SHORT).show();
				}
	       }
	  }
	  
	private void doSomeAction(){
		//更新故障码
		new Thread(){
			public void run(){
				updateMyDic();
				checkErrorAlert();
				//ArrayList<FaultCodeInfo> modleFaults = SaveDB.getSaveDB(MainActivity.this).getSomeFaultCodesById("common","C0032");
			}
		}.start();
	}
	
	public void onResume(){
		super.onResume();
		startRefreshCarIcon();
	}
	
	public void onPause(){
		super.onPause();
		stopRefreshIconTimer();
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.activity_main, menu);
//		return true;
//	}
	

	private ArrayList<View> getViewList(){
		ArrayList<View> list = new ArrayList<View>();
		LayoutInflater _layoutInflater = LayoutInflater.from(this);
		int layoutID = Integer.parseInt(getPro().get(0).toString());
		String []flags = (String[]) getPro().get(1);
		int []itemIds = (int[]) getPro().get(2);
		for (int position = 0; position < getDataList().size(); position ++) {
			View convertView = _layoutInflater.inflate(layoutID, null);
			for (int i = 0; i < flags.length; i++) {
				if (convertView.findViewById(itemIds[i]) instanceof TextView) {
					((TextView) convertView.findViewById(itemIds[i])).setText(getDataList().get(position).get(flags[i]).toString());
				}
			}
			View bgv = convertView.findViewById(R.id.relativeLayout);
			bgv.setBackgroundResource(imageId[position]);  //设置图片
			convertView.setId(position);
			list.add(convertView);
		}
		return list;
	}
	
	private ArrayList<Object> getPro(){
		ArrayList<Object> list = new ArrayList<Object>();
		list.add(R.layout.mainmenu_item);
		list.add(new String[]{"title"});
		list.add(new int[]{R.id.title});
		return list;
	}
	
	private  ArrayList<HashMap<String, Object>> getDataList(){
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
		imageId = new int[]{
//				R.drawable.chekuangchaxun,R.drawable.dianmianchaxun,
//							R.drawable.yuyue,R.drawable.xiche,R.drawable.fuwuchaxun,
//							R.drawable.weizhangchaxun, R.drawable.jiayouzhan
							};
		for(int i = 0 ; i < imageId.length; i ++){
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("title", ""+i);
			list.add(map);
		}
		return list;
	}
	
	
	private void updateMyDic(){
		//更新通用字典
		UpdateFaultDic.getUpdateFaultDic(MainActivity.this).updateFaultDic("common");
		//更新我的车辆列表字典
		if(TongGouApplication.obdLists!=null&&TongGouApplication.obdLists.size()>0){
		     for(int i=0;i<TongGouApplication.obdLists.size();i++){
		    	 VehicleInfo cuVehicleInfo = TongGouApplication.obdLists.get(i).getVehicleInfo();
		    	 if(cuVehicleInfo!=null){
		    		 String modelID = cuVehicleInfo.getVehicleModelId();
			    	 if(modelID!=null&&!"".equals(modelID)){
			    		 UpdateFaultDic.getUpdateFaultDic(MainActivity.this).updateFaultDic(modelID);
			    	 }
		    	 }
		     }
		}
	}
	
	/**
	 * 更新通用故障码
	 */
//	private void updateCommonFaultDic(){
//		String dicVer = sharedPreferences.getString(BaseActivity.COMMON_FAULT_DIC_VERSON, "NULL");    //为null则取最新版
//		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/vehicle/faultDic/dicVersion/"+dicVer+"/vehicleModelId/NULL";
//		GetFaultDicParser getFaultDicParserParser = new GetFaultDicParser();		
//		NetworkState ns = Network.getNetwork(MainActivity.this).httpGetUpdateString(url,getFaultDicParserParser);	
//		if(ns.isNetworkSuccess()){
//			if(getFaultDicParserParser.isSuccessfull()){
//				String nowVersion = getFaultDicParserParser.getFaultDicResponse().getDictionaryVersion();
//				if(nowVersion!=null&&!"".equals(nowVersion)){
//					if(dicVer==null|| (!dicVer.equals(nowVersion))){
//						//版本不一样
//						if(getFaultDicParserParser.getFaultDicResponse().getFaultCodeList()!=null&&getFaultDicParserParser.getFaultDicResponse().getFaultCodeList().size()>0){
//							//更新数据库
//							SaveDB.getSaveDB(MainActivity.this).deleteModleFaultCodes("common");
//							SaveDB.getSaveDB(MainActivity.this).saveFaultCodeInfo(getFaultDicParserParser.getFaultDicResponse().getFaultCodeList(),"common");
//							sharedPreferences.edit().putString(BaseActivity.COMMON_FAULT_DIC_VERSON, nowVersion).commit();  //保存版本号
//						}
//					}
//				}
//			}
//		}else{
//			//网络出错
//			//sendMessage(NETWORK_FAILD, ns.getErrorMessage());
//		}
//	}
	
	
	/**
	 * 更新故障码
	 */
//	private void updateFaultDic(String modleId,String versionStr){
//        String changeModleId;
//        if("common".equals(modleId)){
//        	changeModleId = "common";
//        }else{
//        	changeModleId = modleId;
//        }		
//		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/vehicle/faultDic/dicVersion/"+versionStr+"/vehicleModelId/"+changeModleId;
//		GetFaultDicParser getFaultDicParserParser = new GetFaultDicParser();		
//		NetworkState ns = Network.getNetwork(MainActivity.this).httpGetUpdateString(url,getFaultDicParserParser);	
//		if(ns.isNetworkSuccess()){
//			if(getFaultDicParserParser.isSuccessfull()){
//				String nowVersion = getFaultDicParserParser.getFaultDicResponse().getDictionaryVersion();
//				if(nowVersion!=null&&!"".equals(nowVersion)){
//					if(!versionStr.equals(nowVersion)){
//						//版本不一样
//						if(getFaultDicParserParser.getFaultDicResponse().getFaultCodeList()!=null&&getFaultDicParserParser.getFaultDicResponse().getFaultCodeList().size()>0){
//							//更新数据库
//							SaveDB.getSaveDB(MainActivity.this).upDateFaultCodeVersion(modleId, nowVersion);  //更新版本表
//							SaveDB.getSaveDB(MainActivity.this).deleteModleFaultCodes(modleId);               //删除老的数据
//							SaveDB.getSaveDB(MainActivity.this).saveFaultCodeInfo(getFaultDicParserParser.getFaultDicResponse().getFaultCodeList(),modleId); //保存新的数据
//						}
//					}
//				}
//			}
//		}
//	}
	

	
	
	private void login(){
		if(TongGouApplication.imageVersion==null||"".equals(TongGouApplication.imageVersion)){
			TongGouApplication.imageVersion = sharedPreferences.getString(BaseActivity.SCREEN, "480X800");
		}	
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/login";
		LoginParser loginParser = new LoginParser();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("userNo",currentUserId));	
		nameValuePairs.add(new BasicNameValuePair("password",currentPassWd));
		nameValuePairs.add(new BasicNameValuePair("platform",INFO.CLIENT_PLATFORM));
		nameValuePairs.add(new BasicNameValuePair("appVersion",INFO.VERSION));
		//可选
		nameValuePairs.add(new BasicNameValuePair("platformVersion",TongGouApplication.platformVersion));
		nameValuePairs.add(new BasicNameValuePair("mobileModel",TongGouApplication.mobileModel));
		nameValuePairs.add(new BasicNameValuePair("imageVersion",TongGouApplication.imageVersion));


		NetworkState ns = Network.getNetwork(MainActivity.this).httpPostUpdateString(url,nameValuePairs,loginParser);	
		if(ns.isNetworkSuccess()){
			if(loginParser.isSuccessfull()){
				//保存数据
				TongGouApplication app = (TongGouApplication)this.getApplication();
				app.saveSomeInformation(loginParser,sharedPreferences,currentUserId,currentPassWd);

				doSomeAction();
			}else{
				//解析出错
				sendMessage(LOGOUT_EXIT, loginParser.getErrorMessage());
			}
		}else{
			//网络出错
			sendMessage(LOGOUT_EXIT, "网络不通，请重新登录");
			
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
	}
	
	
	/////////////////////////////////////////////更新车辆状态标识
	private RefreshTask refreshIconTask;   
	private Timer refreshIconTimer;
	private void startRefreshCarIcon(){
		try{
    	refreshIconTask = new RefreshTask();
    	refreshIconTimer = new Timer();
    	refreshIconTimer.schedule(refreshIconTask,0,3000);
		} catch (Exception e) {}
    }
    
    private void stopRefreshIconTimer(){
    	if(refreshIconTask != null){
    		refreshIconTask.cancel();
		}
    	refreshIconTask = null;
		if(refreshIconTimer != null){
			refreshIconTimer.cancel();
		}
		refreshIconTimer = null;
    }
  
    private class RefreshTask extends TimerTask{
   	 	public void run(){  	
	   	 	MainActivity.this.runOnUiThread(new Runnable(){
				  public void run() {
					  if(TongGouApplication.connetedOBD){
						  if(haveFaultCode){
							  carStatusIcon.setImageResource(R.drawable.car3);
						  }else{
							  carStatusIcon.setImageResource(R.drawable.car2);
						  }
						  carName.setText(TongGouApplication.connetedVehicleName);
					  }else{
						  carStatusIcon.setImageResource(R.drawable.car1);
						  if(defaultBrandAndModle!=null&&defaultBrandAndModle.indexOf("null")==-1){
							  carName.setText(defaultBrandAndModle);
						  }else{
							  carName.setText("");
						  }
						  
//						  if(BaseConnectOBDService.connetState==-1){
//							  carName.setText("");
//						  }else if(BaseConnectOBDService.connetState==0){
//							  carName.setText("连接车辆...");
//						  }else if(BaseConnectOBDService.connetState==1){
//							  carName.setText("未连接到车辆");
//						  }else if(BaseConnectOBDService.connetState==3){
//							  carName.setText("读取车辆数据...");
//						  }else if(BaseConnectOBDService.connetState==4){
//							  carName.setText("未连接到车辆 ,蓝牙未打开");
//						  }
					  }
				  }
			});	
   	 	}
    }
    //////////////////////////////////////////////////////////////////////
    
    
    private void checkErrorAlert(){ 
    	if(!TongGouApplication.connetedOBD){
    		return;
    	}
    	ArrayList<CarCondition> havealarms = SaveDB.getSaveDB(this).getAllCarConditons(currentUserId);		
		if(havealarms.size()>0){
			haveFaultCode = true;
		}else{
			haveFaultCode = false;
		}
    	ArrayList<CarCondition> alarms = SaveDB.getSaveDB(this).getAllUnReadCarConditons(currentUserId);		
		for(int i=0;i<alarms.size();i++){
			Intent intent =new Intent(this,CarErrorActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);	
		}
    }

    private OBDDevice mBindOBDDevice;
    private String mVIN;
    private String mShopName;
    private String mShopId;
    
	@Override
	public void onBindOBDSuccess(OBDDevice device, String vin) {
		TongGouApplication.showLog("绑定成功 -- " + device.toString() + "  " + vin);
		TongGouApplication.connetedObdSN = device.getDeviceAddress();
		mBindOBDDevice = device;
		mVIN = vin;
		
		TongGouApplication.getInstance().unregisterBindOBDListener(this);
		onScanShop();
	}

	@Override
	public void onBindOBDCancle() {
		TongGouApplication.getInstance().unregisterBindOBDListener(this);
		onScanShop();
	}
	
	private void onScanShop() {
		TongGouApplication.getInstance().registerBindShopListener(this);
		Intent intent = new Intent(this, CaptureActivity.class);
		intent.putExtra(CaptureActivity.KEY_ARG_IS_SHOW_BACK_BUTTON, false);
		startActivity(intent);
	}

	@Override
	public void onBindShopSuccess(String shopName, String shopId) {
		TongGouApplication.getInstance().unregisterBindShopListener(this);
		TongGouApplication.showLog("店面绑定成功 -- " + shopName + "  " + shopId);
		mShopName = shopName;
		mShopId = shopId;
		onBindShopFinish();
	}

	@Override
	public void onBindShopCancle() {
		TongGouApplication.getInstance().unregisterBindShopListener(this);
		
		if( mBindOBDDevice != null || !TextUtils.isEmpty( mShopId)) {
			onBindShopFinish();
		}
		// 否则直接到首页
	}
	
	private void onBindShopFinish() {
		turnToConfirmActivty();
	}
	
	private void turnToConfirmActivty() {
		Bundle args = new Bundle();
		args.putSerializable(BindConfirmActivity.KEY_ARG_OBD_DEVICE, mBindOBDDevice);
		args.putString(BindConfirmActivity.KEY_ARG_VIN, mVIN);
		args.putString(BindConfirmActivity.KEY_ARG_SHOP_NAME, mShopName);
		args.putString(BindConfirmActivity.KEY_ARG_SHOP_ID, mShopId);
		Intent intent = new Intent();
		intent.setClass(this, BindConfirmActivity.class);
		intent.putExtras(args);
		startActivity(intent);
	}
	
	////// 首页按钮点击监听
	
	public void onWashCarServiceClickListener(View view) {
		Bundle args = new Bundle();
		args.putString("tonggou.shop.category", "WASH");
		turnToActivity(StoreQueryActivity.class, R.string.reservation_xiche, args);
//		TongGouApplication.showToast("洗车");
	}
	
	public void onTransgressQueryClickListener(View view) {
		turnToActivity(TransgressQueryActivity.class, R.string.transgress_query_title);
//		TongGouApplication.showToast("违章查询");
	}
	
	public void onBespeakClickListener(View view) {
		turnToActivity(ReservationServiceActivity.class, R.string.reservation_title);
//		TongGouApplication.showToast("预约");
	}
	
	public void onShopQueryClickListener(View view) {
		turnToActivity(StoreQueryActivity.class, R.string.shopslist_service);
//		TongGouApplication.showLog("店铺查询");
	}
	
	public void onGasStationClickListener(View view) {
		turnToActivity(GasStationMapActivity.class, R.string.gas_station_title);
//		TongGouApplication.showToast("加油站");
	}
	
	public void onServiceQueryClickListener(View view) {
		turnToActivity(SearchServiceActivity.class, R.string.service_title);
//		TongGouApplication.showToast("服务查询");
	}
	
	public void onConditionClickListener(View view) {
		turnToActivity(CarConditionQueryActivity.class, R.string.car_conditon_title);
//		TongGouApplication.showToast("车况查询");
	}
	
	private void turnToActivity(Class<? extends Activity> clazz, int titleRes) {
		Intent intent = new Intent(this, clazz);
		intent.putExtra("tonggou.shop.categoryname",getString(titleRes)); 
		startActivity(intent);
	}
	
	private void turnToActivity(Class<? extends Activity> clazz, int titleRes, Bundle extra) {
		Intent intent = new Intent(this, clazz);
		intent.putExtra("tonggou.shop.categoryname",getString(titleRes)); 
		intent.putExtras(extra);
		startActivity(intent);
	}
}
