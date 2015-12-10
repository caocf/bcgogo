package com.tonggou.andclient;


import java.io.IOException;

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


public class OilErrorActivity extends BaseActivity{
	private MediaPlayer mediaPlayer;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.carerror);
		mediaPlayer = new MediaPlayer();
		mediaPlayer = MediaPlayer.create(this,R.raw.beep1 );
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); 
		
		
		String mes =getIntent().getStringExtra("tonggou.oil.alert");
		showWrongAlert(mes);
		
		playVoice();
	  }
	
	 
	private void playVoice(){
		if(!"NO".equals(getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.CHECKVOICE, null))){
			try {
				if( mediaPlayer != null ) {
					mediaPlayer.start();
				}
			} catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
		}
	}

	public void onDestroy() {	 
		if(mediaPlayer != null){
			mediaPlayer.release();
	    }
        super.onDestroy();
    }
	
	
	
	/**
	 * 查找蓝牙设备失败提示
	 */
	protected void showWrongAlert(String mess){

		final AlertDialog wrongAlert= new AlertDialog.Builder(this).create();		
		wrongAlert.show();	
		Window window = wrongAlert.getWindow();
		window.setContentView(R.layout.oil_alarm_alert);	
		TextView contentFaultCode = (TextView)window.findViewById(R.id.alarm_title);
		contentFaultCode.setText("油量提示");
		TextView contentStr = (TextView)window.findViewById(R.id.content_text_alert);
		contentStr.setText(mess);
		wrongAlert.setCancelable(false);
		wrongAlert.setOnCancelListener(new OnCancelListener(){
			@Override
			public void onCancel(DialogInterface arg0) {
//				alertCount--;
//				if(alertCount==0){
//					CarErrorActivity.this.finish();
//				}
			}			
		});
		
		View retryOper = window.findViewById(R.id.connect_again);  //知道了
		retryOper.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				wrongAlert.cancel();
				wrongAlert.dismiss();			
				OilErrorActivity.this.finish();
			}
		});
		
		View connectOK = window.findViewById(R.id.connect_ok);  //去加油
		connectOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				wrongAlert.cancel();
				wrongAlert.dismiss();			
				OilErrorActivity.this.finish();
				startActivity(new Intent(OilErrorActivity.this,GasStationMapActivity.class));
			}
		});
	
		/*View cancelOper = window.findViewById(R.id.connect_cancel);  //立即处理
		cancelOper.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				wrongAlert.cancel();
				wrongAlert.dismiss();			
				SaveDB.getSaveDB(CarErrorActivity.this).readedTimeAlarm(currentUserId,carCondition.getFaultCode(),System.currentTimeMillis()+"");
				SaveDB.getSaveDB(CarErrorActivity.this).readedOneAlarm(currentUserId,carCondition.getFaultCode());
				Intent intent = new Intent(CarErrorActivity.this, StoreQueryActivity.class);
				intent.putExtra("tonggou.shop.categoryname",getString(R.string.shopslist_service)); 
				startActivity(intent);
			}
		});*/
	}
	
	
		
//		protected void sendMessage(int what, String content) {
//			if (what < 0) {
//				what = BaseActivity.SEND_MESSAGE;
//			}
//			Message msg = Message.obtain(handler, what, content);
//			if(msg!=null){
//				msg.sendToTarget();
//			}
//		}
}
