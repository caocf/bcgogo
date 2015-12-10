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


public class CarErrorActivity extends BaseActivity{

	public static final String EXTRA_NEW_CAR_CONDITION_COUNT = "extra_new_car_condition_count";
   
    ///////////////////////////////////////////////////
	private MediaPlayer mediaPlayer;
	private AlertDialog wrongAlert;
  
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.carerror);
		mediaPlayer = new MediaPlayer();
		mediaPlayer = MediaPlayer.create(this,R.raw.condition_has_dtc);
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); 
		try {
			if (mediaPlayer != null) {  
				 mediaPlayer.stop();  
		    }  
			mediaPlayer.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Intent intent = getIntent();
		int count = intent.getIntExtra(EXTRA_NEW_CAR_CONDITION_COUNT, 0);
		if( count < 1 ) {
			finish();
			return;
		}
		playVoice();
		showWrongAlert( count );
		
	  }
	
	public void onDestroy() {	
		dismissErrorDialog();
		if(mediaPlayer != null){
			mediaPlayer.release();
		}
		mediaPlayer = null;
		super.onDestroy();
    }
	
	protected void showWrongAlert(int newErrorCount){
		dismissErrorDialog();
		wrongAlert = new AlertDialog.Builder(this).create();		
		wrongAlert.show();	
		Window window = wrongAlert.getWindow();
		window.setContentView(R.layout.car_alarm_alert);	
		TextView title = (TextView)window.findViewById(R.id.alarm_title);
		title.setText("故障消息");
		TextView contentStr = (TextView)window.findViewById(R.id.content_text_alert);
		contentStr.setText("您有 " + newErrorCount + " 条故障, 是否立即查看");
		wrongAlert.setCancelable(false);
		wrongAlert.setOnCancelListener(new OnCancelListener(){
			@Override
			public void onCancel(DialogInterface arg0) {
					CarErrorActivity.this.finish();
			}			
		});
		
		View retryOper = window.findViewById(R.id.connect_again);  //知道了
		retryOper.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				wrongAlert.cancel();
				wrongAlert.dismiss();			
			}
		});
	
		View cancelOper = window.findViewById(R.id.connect_cancel);  //立即处理
		cancelOper.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				wrongAlert.cancel();
				wrongAlert.dismiss();			
				Intent intent = new Intent(CarErrorActivity.this, FaultCodeCenterActivity.class);
				startActivity(intent);
			}
		});
	}
	
	private void dismissErrorDialog() {
		if( wrongAlert != null && wrongAlert.isShowing() ) {
			wrongAlert.dismiss();
		}
		wrongAlert = null;
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
