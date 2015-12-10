package com.tonggou.gsm.andclient.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.service.PollingMessageService;

public class TestPollingMessageServiceActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.test_activity_polling_msg_service);
	}
	
	public void onStartPollingBtnClick(View view) {
		PollingMessageService.startPolling(this);
	} 
	
	public void onStopPollingBtnClick(View view) {
		PollingMessageService.stopPolling(this);
	}
	
	public void onDestoryPollingServiceBtnClick(View view) {
		
	}
}
