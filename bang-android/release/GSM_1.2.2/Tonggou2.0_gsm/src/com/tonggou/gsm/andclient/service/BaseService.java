package com.tonggou.gsm.andclient.service;

import com.tonggou.gsm.andclient.net.HttpRequestClient;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BaseService extends Service {

	public String TAG = getClass().getSimpleName();
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		HttpRequestClient.cancelRequest(this, true);
		super.onDestroy();
	}

}
