package com.tonggou.andclient;


import java.util.ConcurrentModificationException;
import java.util.Timer;
import java.util.TimerTask;
import com.baidu.location.LocationClient;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.app.TongGouService;
import com.tonggou.andclient.myview.ActionBar;
import com.tonggou.andclient.util.SomeUtil;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class HomePageActivity extends ActivityGroup implements OnClickListener {
	public static final String EXTRA_FLAG_PREVIOUS_ACTIVITY_NAME = "extra_flag_previous_activity_name";
	private ViewGroup layout;
	private ActionBar actionBar;
	private ImageView tv1, tv2, tv3;
	private ImageView stv1, stv2, stv3;
	private View but1, but2, but3;
	private Window window;
	private LocalActivityManager manager;
	
	private Intent intent1 ;
	private Intent intent2 ;
	private Intent intent3 ;

	private LocationClient mLocClient;
	private RequestLocationTask requestLocationTask;   					 //轮询读取obd
	private Timer requestLocationTimer;
	
	/***
	 * init View
	 */
	public void InitView() {
		LayoutInflater.from(HomePageActivity.this).inflate(R.layout.home_tab, null, true);
		tv1 = (ImageView) findViewById(R.id.tv1);
		tv2 = (ImageView) findViewById(R.id.tv2);
		tv3 = (ImageView) findViewById(R.id.tv3);
	
		stv1 = (ImageView) findViewById(R.id.tv1_select);
		stv2 = (ImageView) findViewById(R.id.tv2_select);
		stv3 = (ImageView) findViewById(R.id.tv3_select);
		
		but1 = findViewById(R.id.tv1_select_bg);
		but2 = findViewById(R.id.tv2_select_bg);
		but3 = findViewById(R.id.tv3_select_bg);
		
		stv2.setVisibility(View.INVISIBLE);
		tv2.setImageResource(R.drawable.mes_un);
		stv3.setVisibility(View.INVISIBLE);
		tv3.setImageResource(R.drawable.set_un);
		
		but1.setOnClickListener(this);
		but2.setOnClickListener(this);
		but3.setOnClickListener(this);
		tv1.setOnClickListener(this);
		tv2.setOnClickListener(this);
		tv3.setOnClickListener(this);
	
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_page);

		manager = getLocalActivityManager();
		layout = (ViewGroup) findViewById(R.id.ll_main);
		actionBar = (ActionBar) findViewById(R.id.bottomBar1);
		InitView();

		intent1 = new Intent(HomePageActivity.this, MainActivity.class);
		intent2 = new Intent(HomePageActivity.this, MessageActivity.class);
		intent3 = new Intent(HomePageActivity.this, SettingActivity.class);
		// 初始第一项
		Window subActivity = getLocalActivityManager().startActivity("subActivity1",intent1);
		layout.addView(subActivity.getDecorView(), LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		
		haveMessageReceiver = new HaveMessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(TongGouService.TONGGOU_ACTION_NEW_MESSAGE);
		registerReceiver(haveMessageReceiver, filter);
		
		
		RegisterActivityNext.registercarnameStr=null;
		RegisterActivityNext.carBrand=null;
		RegisterActivityNext.carMold=null;
		RegisterActivityNext.carBrandId=null;
		RegisterActivityNext.carMoldId=null;
		RegisterActivityNext.shop2DCodeStr=null;
		RegisterActivityNext.shop2DCodeId = null;
		RegisterActivityNext.registernextmileStr=null;
		RegisterActivityNext.registernexttimeStr=null;
		RegisterActivityNext.registernexttime2Str=null;
		RegisterActivityNext.currentMileStr=null;
		
		//启动百度定位
		mLocClient = ((TongGouApplication)getApplication()).mLocationClient;
		((TongGouApplication)getApplication()).starBaiduLBS();
		startRequestLocationTimer();
	
	}
	
	public void onResume(){
		
		super.onResume();
		int newMessagesCount = getSharedPreferences(BaseActivity.SETTING_INFOS,0).getInt(BaseActivity.NEW_MESSAGE_COUNT, 0);
		if(newMessagesCount>0){ //有新消息			
			((TextView) findViewById(R.id.new_message_count)).setText(newMessagesCount+"");
			 findViewById(R.id.new_message_count_bg).setVisibility(View.VISIBLE);
		}else{
			 findViewById(R.id.new_message_count_bg).setVisibility(View.GONE);
		}
		
		if( ! TongGouApplication.connetedOBD ) {
			TongGouApplication.getInstance().queryVehicleList();
		}
	}
	
	public void onDestroy(){
		super.onDestroy();
		unregisterReceiver(haveMessageReceiver);
	}

	@Override
	public void onClick(View v) {
		layout.removeAllViews();
		Intent intent = null;
		switch (v.getId()) {
			case R.id.tv1:
			case R.id.tv1_select_bg:
				stv1.setVisibility(View.VISIBLE);
				stv2.setVisibility(View.INVISIBLE); 
				stv3.setVisibility(View.INVISIBLE);
				tv1.setImageResource(R.drawable.home);
				tv2.setImageResource(R.drawable.mes_un);
				tv3.setImageResource(R.drawable.set_un);
				//intent = new Intent(HomePageActivity.this, MainActivity.class);
				intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				Window subActivity1 = getLocalActivityManager().startActivity("subActivity1", intent1);
				layout.addView(subActivity1.getDecorView(), LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
				break;
			case R.id.tv2:
			case R.id.tv2_select_bg:
				stv1.setVisibility(View.INVISIBLE);
				stv2.setVisibility(View.VISIBLE);
				stv3.setVisibility(View.INVISIBLE);
				tv1.setImageResource(R.drawable.home_un);
				tv2.setImageResource(R.drawable.mes);
				tv3.setImageResource(R.drawable.set_un);
				//intent = new Intent(HomePageActivity.this, MessageActivity.class);
				intent2.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				Window subActivity2 = getLocalActivityManager().startActivity("subActivity2", intent2);
				layout.addView(subActivity2.getDecorView(), LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
				
				int newMessagesCount = getSharedPreferences(BaseActivity.SETTING_INFOS,0).getInt(BaseActivity.NEW_MESSAGE_COUNT, 0);
	    		//if(newMessagesCount>0){ 
	    			//有新消息			
		   	 		Intent intentDisplay = new Intent();
		   	 		intentDisplay.setAction(TongGouService.TONGGOU_ACTION_DISPLAY_MESSAGE);
			        sendBroadcast(intentDisplay);
	    		//}
	    		//清掉新消息提示
				getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit().putInt(BaseActivity.NEW_MESSAGE_COUNT,0).commit();
				findViewById(R.id.new_message_count_bg).setVisibility(View.GONE);
				
				
				break;
			case R.id.tv3:
			case R.id.tv3_select_bg:
				stv1.setVisibility(View.INVISIBLE);
				stv2.setVisibility(View.INVISIBLE);
				stv3.setVisibility(View.VISIBLE);
				tv1.setImageResource(R.drawable.home_un);
				tv2.setImageResource(R.drawable.mes_un);
				tv3.setImageResource(R.drawable.set);
				//intent = new Intent(HomePageActivity.this, SettingActivity.class);
				intent3.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				Window subActivity3 = getLocalActivityManager().startActivity("subActivity3", intent3);
				layout.addView(subActivity3.getDecorView(), LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
				break;
			default:
				break;
		}

		actionBar.onClick(v);
	}
	
	public void onBackPressed() {
		new AlertDialog.Builder(HomePageActivity.this) 		
        .setTitle(getString(R.string.exit_title)) 
        .setMessage(getString(R.string.exit_sure_tonggou)) 
        .setPositiveButton(getString(R.string.exit_submit), new DialogInterface.OnClickListener() { 
            public void onClick(DialogInterface dialog, int whichButton) {
            	if(BaseActivity.sAllActivities!=null){
            		try{
	        			for (Activity activity : BaseActivity.sAllActivities) {
	        				activity.finish();
	        			}
	        			BaseActivity.sAllActivities.clear();
            		}catch(ConcurrentModificationException ex){	
        				ex.printStackTrace();
        			}
        		}
            	//退出百度定位
        		TongGouApplication app = (TongGouApplication)getApplication();
        		app.stopBaiduLBS();
        		
            	HomePageActivity.this.finish();
        } 
        }).setNeutralButton(getString(R.string.exit_cancel), new DialogInterface.OnClickListener(){ 
        	public void onClick(DialogInterface dialog, int whichButton){ 
        	} 
        }).show();
	}
	
	private HaveMessageReceiver haveMessageReceiver;
	
	private class HaveMessageReceiver extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent) {
        	//Log.d("testthread", "new message BROADCAST....");
        	HomePageActivity.this.runOnUiThread(new Runnable(){
				  public void run() {
					    int newMessagesCount = getSharedPreferences(BaseActivity.SETTING_INFOS,0).getInt(BaseActivity.NEW_MESSAGE_COUNT, 0);
			    		if(newMessagesCount>0){ //有新消息			
			    			((TextView) findViewById(R.id.new_message_count)).setText(newMessagesCount+"");
			    			 findViewById(R.id.new_message_count_bg).setVisibility(View.VISIBLE);
			    		}else{
			    			 findViewById(R.id.new_message_count_bg).setVisibility(View.GONE);
			    		}
				  }
			});
        	
        	
        }                       
    }
	
	/**
	 * 百度定位请求//////////////////////////////////////////////////////////////////////////////
	 */
	private void startRequestLocationTimer(){
		stopRequestLocationTimer();
		try{
		requestLocationTask = new RequestLocationTask();
		requestLocationTimer = new Timer();
		requestLocationTimer.schedule(requestLocationTask,0,3000);
		}catch(Exception ex){}
	}

	private void stopRequestLocationTimer(){
		if(requestLocationTask != null){
			requestLocationTask.cancel();
		}
		requestLocationTask = null;
		if(requestLocationTimer != null){
			requestLocationTimer.cancel();
		}
		requestLocationTimer = null;
	}

	 private class RequestLocationTask extends TimerTask{
   	 	public void run(){  
	   	 	if (mLocClient != null && mLocClient.isStarted()){	   	 		
				int startResult = mLocClient.requestLocation();	
				//Log.d("DDDS", "SSSSSSSSSSSSSSSSSSS REQUEST:"+startResult);
				if(startResult==0){ //成功
					stopRequestLocationTimer();
					//判断wifi是否打开
					if(!SomeUtil.checkWifiEnable(HomePageActivity.this)){
						startBaiduLocationTimer();
					}
				}
			}else{
				//Log.d("DDDS", "mLocClient.isStarted(): false");
			}
   	 	}
    }
	 
	 private StartLocationTask startLocationTask;   					 //轮询读取obd
	 private Timer startLocationTimer;
	 private void startBaiduLocationTimer(){
		    try {
				Thread.sleep(3000);			
			 	stopBaiduLocationTimer();
				startLocationTask = new StartLocationTask();
				startLocationTimer = new Timer();
				startLocationTimer.schedule(startLocationTask,0,180000);
		    } catch (Exception e) {
			
			}
		}

		private void stopBaiduLocationTimer(){
			if(startLocationTask != null){
				startLocationTask.cancel();
			}
			startLocationTask = null;
			if(startLocationTimer != null){
				startLocationTimer.cancel();
			}
			startLocationTimer = null;
		}

		 private class StartLocationTask extends TimerTask{
	   	 	public void run(){  
	   	 		String lac =  getSharedPreferences(BaseActivity.SETTING_INFOS,0).getString(BaseActivity.LOCATION_LAST_POSITION_LAT, "");  //纬度 
		   	 	if (lac != null && !"".equals(lac)&& !"null".equals(lac)){					
					//Log.d("DDDS", "SSSSS LOCATION C:"+lac);
					stopBaiduLocationTimer();
				}else{
					//Log.d("DDDS", "stop - start");
					((TongGouApplication)getApplication()).stopBaiduLBS();
					((TongGouApplication)getApplication()).starBaiduLBS();
				}
	   	 	}
	    }
    //////////////////////////////////////////////////////////////////////////////////////////////
	
}