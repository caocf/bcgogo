package com.tonggou.andclient;




import java.util.ConcurrentModificationException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


public class ConnectOBDActivity extends BaseConnectOBDActivity{

    
    private Handler handler;
    private String fromUi="";
    private String erWeiMa;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connect_obd);	
		fromUi = getIntent().getStringExtra("tonggou.connectobd.from");
		
		erWeiMa = getIntent().getStringExtra("tonggou.connectobd.shopscanid");
	
		TextView nextStepButton = (TextView)findViewById(R.id.connect_obd_next);
		
		
		if(fromUi!=null&&"addcar".equals(fromUi)){  //新增车辆进来
			nextStepButton.setText("返回");
	        nextStepButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {	
					cancelShowingDevise = true;
					ConnectOBDActivity.this.finish();
				}
			});
			//启动扫描
			startConnect(fromUi,erWeiMa);
		}else if(fromUi!=null&&"changecar".equals(fromUi)){  //修改车辆进来
			nextStepButton.setText("返回");
	        nextStepButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					cancelShowingDevise = true;
					ConnectOBDActivity.this.finish();
				}
			});
			//启动扫描
			startConnect(fromUi,erWeiMa);
		}else if(fromUi!=null&&"register".equals(fromUi)){//注册时进来
			nextStepButton.setOnClickListener(new OnClickListener() {
     			public void onClick(View v) {
     				if(sAllActivities!=null){
     					try{
	     					for (Activity activity : sAllActivities) {
	     						activity.finish();
	     					}
	     					sAllActivities.clear();
     					}catch(ConcurrentModificationException ex){	
     					}
     				}
     				MainActivity.ifAutoLogin = true;
     				Intent intent = new Intent();
     				intent.setClass(ConnectOBDActivity.this, HomePageActivity.class);
     				startActivity(intent);		
     				cancelShowingDevise = true;
     				ConnectOBDActivity.this.finish();
     				
     			}
     		});
			if(RegisterActivityNext.registercarnameStr==null||"".equals(RegisterActivityNext.registercarnameStr)
					||RegisterActivityNext.carMoldId==null||"".equals(RegisterActivityNext.carMoldId)
					||RegisterActivityNext.carMold==null||"".equals(RegisterActivityNext.carMold)
					||RegisterActivityNext.carBrandId==null||"".equals(RegisterActivityNext.carBrand)
					||RegisterActivityNext.carBrand==null||"".equals(RegisterActivityNext.carBrand)){   //没有填写车辆信息时
				
				if(sAllActivities!=null){
 					try{
     					for (Activity activity : sAllActivities) {
     						activity.finish();
     					}
     					sAllActivities.clear();
 					}catch(ConcurrentModificationException ex){	
 					}
 				}
				Intent intent = new Intent();
 				intent.setClass(ConnectOBDActivity.this, HomePageActivity.class);
 				startActivity(intent);	
 				cancelShowingDevise = true;
 				ConnectOBDActivity.this.finish();
 				return;
			}else{
				//启动扫描
				startConnect(fromUi,erWeiMa);
			}
		}

	}
	
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onBackPressed();
			return true;
		}else {
			return super.onKeyDown(keyCode, event);
		}
	}
	
	public void onBackPressed() {
		if(fromUi!=null&&"addcar".equals(fromUi)){  //新增车辆进来		
			cancelShowingDevise = true;
			ConnectOBDActivity.this.finish();				
		}else if(fromUi!=null&&"changecar".equals(fromUi)){
			cancelShowingDevise = true;
			ConnectOBDActivity.this.finish();
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
	
}
