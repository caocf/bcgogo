package com.tonggou.andclient;



import java.util.Timer;
import java.util.TimerTask;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.network.DefaultUpdateCheck;
import com.tonggou.andclient.network.UpdateCheck;


public class PreLoginActivity extends BaseActivity {

	private  StopLocTask locTask;
	private  Timer locTimer;
	private SharedPreferences settingPres = null;
	protected UpdateCheck chk;  

   
    
    
 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prelogin);		
		initKaiHttpHead();
		Log.d("CONTEETTT", "AAA.");
		//启动服务
		Intent intentSer = new Intent(this, com.tonggou.andclient.app.TongGouService.class);
        startService(intentSer);
		
		if (locTask == null) {
			locTask = new StopLocTask();
			locTimer = new Timer();
			locTimer.schedule(locTask, 3000);//1秒钟
		}

		chk = new DefaultUpdateCheck(this, false);      //检查升级通讯
		
		new Thread(){
			public void run(){
			    ////检查升级通讯				
				chk.checkUpgradeAction();
			}
		}.start();
	
	}

//	private void testee(){
//		StringBuffer sb = new StringBuffer();
//		String itemStr ="U0022\r\n";
//		//String itemStr ="U0022\r\n##DTC:U0021";
//		//String itemStr ="U0022\r\n##DTC:U0021\r\n##DTC:U0023";
//		//String itemStr ="U0022\r\n##DTC:U0021\r\n##DTC:U0023\r\n";
//		 if(itemStr.indexOf("\r\n")!=-1){
//         	//String strPart1 = itemStr.substring(0, itemStr.indexOf("\r\n"));
//         	String lastPart = "";
//
//         	String[] ends = itemStr.split("\r\n");
//         	for(int i=0;i<ends.length;i++){
//         		if(i==0){                      //第一个
//         			sb.append(ends[i]);                   	
//                 	String endStr = sb.toString().trim();
//         			//mHandler.obtainMessage(MESSAGE_READ,endStr).sendToTarget();
//         		}else if(i== (ends.length-1)){ //最后一个
//         			if((itemStr.length())>2&&(itemStr.indexOf("\r\n", itemStr.length()-3)!=-1)){  //最后的字符串是\r\n结束
//         				String endStr = ends[i];
//             			//mHandler.obtainMessage(MESSAGE_READ,endStr).sendToTarget();
//         			}else{
//         				lastPart = ends[i];
//         			}
//         		}else{
//         			String endStr = ends[i];
//         			//mHandler.obtainMessage(MESSAGE_READ,endStr).sendToTarget();
//         		}
//         	}
//         	sb = new StringBuffer();
//         	if(!"".equals(lastPart)){
//         		sb.append(lastPart);
//         	}
//         }
//	}


	private class StopLocTask extends TimerTask {
		public void run() {
			
			settingPres = getSharedPreferences(BaseActivity.SETTING_INFOS, 0);
			if (settingPres!=null&&settingPres.getBoolean(BaseActivity.LOGINED, false)) {  //如果已经登录过，自动登录			
				MainActivity.ifAutoLogin = true;
				Intent intent =new Intent(PreLoginActivity.this,HomePageActivity.class);
				PreLoginActivity.this.startActivity(intent);
			
			}else{
				MainActivity.ifAutoLogin = false;
				Intent toLogin = new Intent(PreLoginActivity.this, LoginActivity.class);
				startActivity(toLogin);			
			}
			
			//stopLocTask(); 
			PreLoginActivity.this.finish();
		}
	}


	
	
	
	private void initKaiHttpHead(){   		
		
//		Display display = getWindowManager().getDefaultDisplay();
//		DisplayMetrics metrics = new DisplayMetrics();
//		display.getMetrics(metrics);
		//resolutionBuilder.append(metrics.widthPixels).append("X").append(metrics.heightPixels);
	
//		if(metrics.widthPixels==320||metrics.widthPixels==480||metrics.widthPixels==720||metrics.widthPixels==640){
//			if(metrics.widthPixels==320){
//				TongGouApplication.imageVersion = "320X480";
//			}else if(metrics.widthPixels==480){
//				TongGouApplication.imageVersion = "480X800";
//			}else if(metrics.widthPixels==640){
//				if(metrics.heightPixels>960){
//					TongGouApplication.imageVersion = "640X1136";
//				}else{
//					TongGouApplication.imageVersion = "640X960";
//				}
//			}else if(metrics.widthPixels==720){
//				TongGouApplication.imageVersion = "720X1280";
//			}else{
//				//默认
//				TongGouApplication.imageVersion = "480X800";
//			}
//		}else{
//			int min1 = Math.abs(metrics.widthPixels-320);
//			int min2 = Math.abs(metrics.widthPixels-480);
//			int min3 = Math.abs(metrics.widthPixels-640);
//			int min4 = Math.abs(metrics.widthPixels-720);
//		
//			if(min1<min2&&min1<min3&&min1<min4){   //min1最接近
//				TongGouApplication.imageVersion = "320X480";
//			}else if(min2<min1&&min2<min3&&min2<min4){
//				TongGouApplication.imageVersion = "480X800";
//			}else if(min3<min1&&min3<min2&&min3<min4){
//				if(metrics.heightPixels>960){
//					TongGouApplication.imageVersion = "640X1136";
//				}else{
//					TongGouApplication.imageVersion = "640X960";
//				}
//			}else if(min4<min1&&min4<min2&&min4<min3){
//				TongGouApplication.imageVersion = "720X1280";
//			}else{
//				//默认
//				TongGouApplication.imageVersion = "480X800";
//			}
//		}
	
		
	}
	
	
	

}
