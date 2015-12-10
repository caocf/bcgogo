package com.tonggou.andclient;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tonggou.andclient.app.BaseConnectOBDService;
import com.tonggou.andclient.app.TongGouApplication;
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
import com.tonggou.andclient.vo.VehicleInfo;
import com.tonggou.andclient.vo.Version;

public class MainActivity extends BaseActivity {
	private static final int REQUEST_ENABLE_BT = 2;
	
	private static final int  NETWORK_FAILD=-1;
	private static final int  PARSE_SUCCEED=0x001;
	private static final int  PARSE_FAILD=0x002;
	private static final int  ALERT_NEW_VERSION = 3;
	
	private static final int  LOGOUT_EXIT = 4;
	
	public static String defaultBrandAndModle = "";    
	
	
	public static boolean ifAutoLogin = true;     //是否自动登录(因为自动登录时跳过login页面)
	public static boolean haveFaultCode = false;  //是否有故障码
	
	private Handler handler;

	private ScrollLayout scrollLayout;
	private int[] imageId;
	private ImageView carStatusIcon;
	private TextView carName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
		
    
		scrollLayout = (ScrollLayout) findViewById(R.id.menu_scrollLayout);
		
		scrollLayout.setScreenHeight(hi-100);  //762
		scrollLayout.setVisiableViewNumber(3);
		scrollLayout.setVisiableChildScale(1.0f, 0.2f, 1f, 0.2f);
		scrollLayout.init(getViewList(),this);
		
		
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
						new AlertDialog.Builder(MainActivity.this)
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
						}).show();
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
	
	public void onDestroy() {	      
       super.onDestroy();
       if(scrollLayout != null){
    	   scrollLayout.deInitSounds();
       }
    }
	
	 
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        switch (requestCode) {
	        case REQUEST_ENABLE_BT:
	            // 当开打蓝牙返回        When the request to enable Bluetooth returns 
	            if (resultCode == Activity.RESULT_OK) {
	            	//连接obd
    		        Intent intent = new Intent();//创建Intent对象
    		        intent.setAction(TongGouService.TONGGOU_ACTION_START);
    		        intent.putExtra("com.tonggou.server","SCAN_OBD");
    		        sendBroadcast(intent);//发送广播
	            }
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
	                  	//连接obd
	      		        Intent intent = new Intent();//创建Intent对象
	      		        intent.setAction(TongGouService.TONGGOU_ACTION_START);
	      		        intent.putExtra("com.tonggou.server","SCAN_OBD");
	      		        sendBroadcast(intent);//发送广播
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
		for (int position = 0; position < getDataList().size(); position++) {
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
		imageId = new int[]{R.drawable.chekuangchaxun,R.drawable.dianmianchaxun,
							R.drawable.yuyue,R.drawable.xiche,R.drawable.fuwuchaxun};
		for(int i = 0 ; i < 5; i ++){
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
    
    

    
  
}
