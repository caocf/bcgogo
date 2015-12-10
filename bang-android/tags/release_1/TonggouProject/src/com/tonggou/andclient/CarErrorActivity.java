package com.tonggou.andclient;


import java.io.IOException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import com.tonggou.andclient.util.SaveDB;
import com.tonggou.andclient.util.SomeUtil;
import com.tonggou.andclient.vo.CarCondition;


public class CarErrorActivity extends BaseActivity{


   
    ///////////////////////////////////////////////////
	private MediaPlayer mediaPlayer;
  
    private int alertCount = 0;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.carerror);
		mediaPlayer = new MediaPlayer();
		mediaPlayer = MediaPlayer.create(this,R.raw.beep1 );
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); 
		try {
			mediaPlayer.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		new Thread(){
			public void run(){
				ArrayList<CarCondition> havealarms = SaveDB.getSaveDB(CarErrorActivity.this).getAllCarConditons(currentUserId);		
				if(havealarms.size()>0){
					  MainActivity.haveFaultCode = true;					
			          boolean needToAlert = false;
					  for(int i=0;i<havealarms.size();i++){
						  final CarCondition tempCarc = havealarms.get(i);
						  if("UNREAD".equals(tempCarc.getName())){
							  needToAlert = true;
							  playVoice();
							  CarErrorActivity.this.runOnUiThread(new Runnable(){
								  public void run() {
									  showWrongAlert(tempCarc);
								  }
							  });
						  }else{						  
							  long longAlert = Long.parseLong(tempCarc.getAlarmId());
							  String pollingMessageIntervalStr = sharedPreferences.getString(BaseActivity.APPCONFIG_ERROR_ALERT_INTERVAL, "12");
							  if(SomeUtil.isNumeric(pollingMessageIntervalStr)){
								  long interval = Long.parseLong(pollingMessageIntervalStr);
								  if((System.currentTimeMillis()-longAlert)>(interval*3600*1000)){  //大于间隔时间
									  needToAlert = true;
									  playVoice();
									  CarErrorActivity.this.runOnUiThread(new Runnable(){
										  public void run() {
											  showWrongAlert(tempCarc);
										  }
									  });
								  }
								}
						  }
					  }
					  if(!needToAlert){
						  CarErrorActivity.this.finish();
					  }				
				}else{
					MainActivity.haveFaultCode = false;
					CarErrorActivity.this.finish();
				}

			}
		}.start();
		
	  }
	
	 
	
	public void onDestroy() {	      
	       super.onDestroy();
	       if(mediaPlayer != null){
	    	   mediaPlayer.release();
	       }
	    }
	
	
	

	

	
	/**
	 * 查找蓝牙设备失败提示
	 */
	protected void showWrongAlert(final CarCondition carCondition){
		alertCount++;
		final AlertDialog wrongAlert= new AlertDialog.Builder(this).create();		
		wrongAlert.show();	
		Window window = wrongAlert.getWindow();
		window.setContentView(R.layout.car_alarm_alert);	
		TextView contentFaultCode = (TextView)window.findViewById(R.id.alarm_title);
		contentFaultCode.setText(carCondition.getFaultCode());
		TextView contentStr = (TextView)window.findViewById(R.id.content_text_alert);
		contentStr.setText(carCondition.getContent());
		wrongAlert.setCancelable(false);
		wrongAlert.setOnCancelListener(new OnCancelListener(){
			@Override
			public void onCancel(DialogInterface arg0) {
				alertCount--;
				if(alertCount==0){
					CarErrorActivity.this.finish();
				}
			}			
		});
		
		View retryOper = window.findViewById(R.id.connect_again);  //知道了
		retryOper.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				wrongAlert.cancel();
				wrongAlert.dismiss();			
				SaveDB.getSaveDB(CarErrorActivity.this).readedTimeAlarm(currentUserId,carCondition.getFaultCode(),System.currentTimeMillis()+"");
				SaveDB.getSaveDB(CarErrorActivity.this).readedOneAlarm(currentUserId,carCondition.getFaultCode());
			}
		});
	
		View cancelOper = window.findViewById(R.id.connect_cancel);  //立即处理
		cancelOper.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				wrongAlert.cancel();
				wrongAlert.dismiss();			
				SaveDB.getSaveDB(CarErrorActivity.this).readedTimeAlarm(currentUserId,carCondition.getFaultCode(),System.currentTimeMillis()+"");
				SaveDB.getSaveDB(CarErrorActivity.this).readedOneAlarm(currentUserId,carCondition.getFaultCode());
				Intent intent = new Intent(CarErrorActivity.this, StoreQueryActivity.class);
				intent.putExtra("tonggou.shop.categoryname",getString(R.string.shopslist_service)); 
				String discrip = carCondition.getContent();
				if("不能识别的故障码".equals(discrip)){
					intent.putExtra("tonggou.shop.conditionStr",carCondition.getFaultCode());
				}else{
					intent.putExtra("tonggou.shop.conditionStr",carCondition.getFaultCode()+":"+carCondition.getContent());
				}
				startActivity(intent);
			}
		});
	}
	
	private void playVoice(){
		if(!"NO".equals(getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.CHECKVOICE, null))){
			try {
				//mediaPlayer.prepare();
				mediaPlayer.start();
			} catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }/* catch (IOException e) {
                e.printStackTrace();
            }*/
		}
	}
		

}
