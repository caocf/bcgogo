package com.tonggou.andclient.app;




import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
//import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.tonggou.andclient.BaseActivity;
import com.tonggou.andclient.HomePageActivity;
import com.tonggou.andclient.R;
import com.tonggou.andclient.jsonresponse.PollingMessagesResponse;
import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.network.RequestClient;
import com.tonggou.andclient.network.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.andclient.network.request.HttpRequestClient;
import com.tonggou.andclient.network.request.PollingMessageRequest;
import com.tonggou.andclient.parse.PollingMessagesParser;
import com.tonggou.andclient.util.HandlerTimer;
import com.tonggou.andclient.util.HandlerTimer.OnHandleTimerListener;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.SaveDB;
import com.tonggou.andclient.util.SomeUtil;
import com.tonggou.andclient.vo.TonggouMessage;
import com.tonggou.andclient.vo.type.MessageType;


public class TongGouService extends BaseConnectOBDService implements OnHandleTimerListener {
	private ReadOBDTask readOBDTask;   					 //轮询读取obd
	private Timer readOBDTimer;
	private HandlerTimer mPollingMessageTimer;			 //轮询
	private static final int TIMER_TOKEN_POLLONG_MSG = 0x123;
	
	public static boolean allowPollingMessage = true;


	private long pollingMessageInterval = 600000;   //默认10分钟
	private long readOBDInterval = 600000;   		//默认10分钟
	private static final long CONNECT_OBD_INTERVAL = 20 * 1000; // 默认20秒
	//public static long alertFaultInterval = 12;           //默认12小时
	public static int interOne = 15;
	public static int interTwo = 25;

	private StartSacnReceiver startReceiver;
	
	public void onCreate(){
		//Log.d("CONTEETTT","oncreate.....");
		super.onCreate();

		startReceiver = new StartSacnReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(TongGouService.TONGGOU_ACTION_START);
		//注册Broadcast Receiver
		registerReceiver(startReceiver, filter);
	}


	public void onStart(Intent intent, int startId) {
		//Log.d("CONTEETTT","onStart.....");
	}




	public void onDestroy() {
		//Log.d("CONTEETTT","onDestroy.....");
		super.onDestroy();
		this.unregisterReceiver(startReceiver);//取消注册Receiver
		try {
			Thread.sleep(10000);
			Intent intentSer = new Intent(this, com.tonggou.andclient.app.TongGouService.class);
			startService(intentSer);
		} catch (InterruptedException e) {
		}
	}


	public IBinder onBind(Intent arg0) {
		return null;
	}



	private synchronized void startGetMessageTimer(){
		String pollingMessageIntervalStr = getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.APPCONFIG_SERVER_READ_INTERVAL, pollingMessageInterval+"");
		if(pollingMessageIntervalStr!=null&&SomeUtil.isNumeric(pollingMessageIntervalStr)&&!"0".equals(pollingMessageIntervalStr)){
			pollingMessageInterval = Long.valueOf(pollingMessageIntervalStr);
		}
		
		mPollingMessageTimer = new HandlerTimer(TIMER_TOKEN_POLLONG_MSG, this);
		mPollingMessageTimer.start(0, pollingMessageInterval);
//		
//		try{
//		getMessageTask = new UpdateNoticeAndMessageTask();
//		getMessageTimer = new Timer();		
//		getMessageTimer.schedule(getMessageTask,0,pollingMessageInterval);
//		}catch(Exception ex){}
	}

	private void stopGetMessageTimer(){
		TongGouApplication.showLog("stopGetMessageTimer");
		if( mPollingMessageTimer != null ) {
			mPollingMessageTimer.stop();
		}
		HttpRequestClient.cancelRequest(TongGouService.this, true);
		mPollingMessageTimer = null;
//		if(getMessageTask != null){
//			getMessageTask.cancel();
//		}
//		getMessageTask = null;
//		if(getMessageTimer != null){
//			getMessageTimer.cancel();
//		}
    }

	/**
	 * 间隔性读取obd信息
	 */
	private synchronized void startReadOBDTimer(){
		stopReadOBDTimer();
		String readObdIntervalStr = getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.APPCONFIG_OBD_READ_INTERVAL, readOBDInterval+"");
		if(readObdIntervalStr!=null&&SomeUtil.isNumeric(readObdIntervalStr)&&!"0".equals(readObdIntervalStr)){
			readOBDInterval = Long.valueOf(readObdIntervalStr);
		}
		try{
			readOBDTask = new ReadOBDTask();
			readOBDTimer = new Timer();	
			readOBDTimer.schedule(readOBDTask,0,readOBDInterval);
			TongGouApplication.showLog("readOBDInterval   " + readOBDInterval);
		}catch(Exception ex){}
	}

	private void stopReadOBDTimer(){
		if(readOBDTask != null){
			readOBDTask.cancel();
		}
		readOBDTask = null;
		if(readOBDTimer != null){
			readOBDTimer.cancel();
		}
		readOBDTimer = null;
	}

	 private class ReadOBDTask extends TimerTask{
   	 	public void run(){  	 	
   	 	//Log.d("CONTEETTT", "OBD conditon .............................");
   	 		if( !TongGouApplication.getInstance().isLogin() ) {
   	 			return;
   	 		}
   	 		
	   	 	if(TongGouApplication.connetedOBD){	 			
	 			//读取车况
	   	 		Intent intent = new Intent();
		        intent.setAction(TongGouService.TONGGOU_ACTION_READ_CURRENT_RTD_CONDITION);
		        sendBroadcast(intent);
	 		}
	   	 	
   	 	}
    }
    //////////////////////////////////////////////////////////////////////////////////////////////


    
    
	    /**
		 * 间隔连接obd
		 */
		private ConnectOBDTask connectOBDTask;   					 
		private Timer connectOBDTimer;
		private synchronized void startConnectOBDTimer(){
			stopConnectOBDTimer();
			try{
			connectOBDTask = new ConnectOBDTask();
			connectOBDTimer = new Timer();			
			connectOBDTimer.schedule(connectOBDTask,CONNECT_OBD_INTERVAL,CONNECT_OBD_INTERVAL);
			}catch(Exception ex){}
		}

		private void stopConnectOBDTimer(){
			if(connectOBDTask != null){
				connectOBDTask.cancel();
			}
			connectOBDTask = null;
			if(connectOBDTimer != null){
				connectOBDTimer.cancel();
			}
			connectOBDTimer = null;
		}

		 private class ConnectOBDTask extends TimerTask{
	   	 	public void run(){  	 	
	   	 		
	   	 		if( !TongGouApplication.getInstance().isLogin() ) {
	   	 			return;
	   	 		}
	   	 		
	   	 	//Log.d("testthread", "OBD connect............................."+BaseConnectOBDService.connetState+":"+BaseConnectOBDService.addingCar);
//		   	 	if(!TongGouApplication.connetedOBD){	
	   	 	TongGouApplication.showLog("OBD start connecting.. state " + BaseConnectOBDService.connetState);
		   	 		if( Math.abs( BaseConnectOBDService.connetState)==1 && !BaseConnectOBDService.addingCar){	 			
			   	 		TongGouApplication.showLog("OBD start connecting.............................");
			   	 		new Thread(){
	        				public void run(){
	        					startConnect();
	        				}
	        			}.start();
		   	 		}
//		 		}
	   	 	}
	    }
	    //////////////////////////////////////////////////////////////////////////////////////////////
    
    

    //取通知
	private void getNotification(MessageType...types ){
		if(!allowPollingMessage || !TongGouApplication.getInstance().isLogin()){
			 return;
		}
		final String userName = getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.NAME, null);
		PollingMessageRequest request = new PollingMessageRequest();
		request.setApiParams(userName, types);
		request.doRequest(this, new AsyncJsonBaseResponseParseHandler<PollingMessagesResponse>() {
			
			@Override
			public void onParseSuccess(PollingMessagesResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				handlerPollingMessage(userName, result.getMessageList());
			}
			
			@Override
			public void onParseFailure(String errorCode, String errorMsg) {
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
			}
			
			@Override
			public Class<PollingMessagesResponse> getTypeClass() {
				return PollingMessagesResponse.class;
			}
			
		});
		
	}
		
	/**
	 * 处理轮询下来的 消息
	 * @param username
	 * @param messages
	 */
	private void handlerPollingMessage(String username, List<TonggouMessage> messages) {
		
		if(messages!=null&&messages.size()>0){
			////////////给消息添加时间
			for(int i=0;i<messages.size();i++){
				messages.get(i).setTime(System.currentTimeMillis()+"");
			}
			
			////////////////////////
			
			 //Log.d("FFF", "parser ------------------------------size："+messages.size());
			 int nowMessageCount = messages.size();
			 int allmesInDB = SaveDB.getSaveDB(this).getAllMessagesCount(username);
			 if((allmesInDB+nowMessageCount)>100){  //大于100条
				 int deleteCount = allmesInDB+nowMessageCount-100;
				 SaveDB.getSaveDB(this).deleteEarlyMessage(username, deleteCount); //删除最早消息
				 if(SaveDB.getSaveDB(this).saveMessages(messages,username)){
					 //保存新消息数
					 getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit().putInt(BaseActivity.NEW_MESSAGE_COUNT, messages.size()).commit();
					 notice(messages.size()+"条新消息","点击查看");	
					 //通知首页提示
					 Intent intent = new Intent();
				     intent.setAction(TongGouService.TONGGOU_ACTION_NEW_MESSAGE);
				     sendBroadcast(intent);
				 }
			 }else{
				 if(SaveDB.getSaveDB(this).saveMessages(messages,username)){
					 //保存新消息数
					 getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit().putInt(BaseActivity.NEW_MESSAGE_COUNT, messages.size()).commit();
					 notice(messages.size()+"条新消息","点击查看");	
					//通知首页提示
					 Intent intent = new Intent();
				     intent.setAction(TongGouService.TONGGOU_ACTION_NEW_MESSAGE);
				     sendBroadcast(intent);
				 }
			 }
		}
	}




	private void notice(String newMesContent,String message){
		NotificationManager notiManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);			
		android.app.Notification nf = new android.app.Notification(R.drawable.icon , getString(R.string.app_name) + "消息"/*小标题*/,System.currentTimeMillis());
		Intent toNewMess = new Intent(TongGouService.this, HomePageActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(TongGouService.this, 0,toNewMess, 0);			
		nf.setLatestEventInfo(TongGouService.this, newMesContent/*拉开后显示的标题*/, message/*拉开后显示的内容*/, contentIntent);
		nf.defaults = android.app.Notification.DEFAULT_SOUND;
		notiManager.notify(0x7f030301, nf);	

	}

	

	public static final String SCAN_OBD = "SCAN_OBD";
	public static final String STOP_SCAN = "STOP";
	public static final String POLLING = "POLLING";
	public static final String STOP_POLLING = "STOP_POLLING";

    
    /**
     * 用于登录后启动扫描
     * @author think
     *
     */
    private class StartSacnReceiver extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent) {
        	//Log.d("testthread", "START BROADCAST....");
        	String act = intent.getStringExtra("com.tonggou.server");
        	if( TextUtils.isEmpty(act)) {
        		return;
        	}
        	TongGouApplication.showLog( act );
        	
        	if( POLLING.equals(act)){
        		
        		//启动轮询
        		startGetMessageTimer();
        		startReadOBDTimer();
        		
        	}else if( SCAN_OBD.equals(act)){
        		//连接obd
    			new Thread(){
    				public void run(){
    					startConnect();
    				}
    			}.start();
        		startConnectOBDTimer();
        	}else if( STOP_SCAN.equals(act)){
        		//停掉服务
        		stopConnectOBDTimer();
        		stopConnect();
        	} else if( STOP_POLLING.equals(act) ) {
        		stopGetMessageTimer();
        	}
        }                       
    }

	@Override
	public void onHandleTimerMessage(int token, Message msg) {
		if( token == TIMER_TOKEN_POLLONG_MSG ) {
			getNotification(MessageType.ALL);
		} 
	}
    
}
