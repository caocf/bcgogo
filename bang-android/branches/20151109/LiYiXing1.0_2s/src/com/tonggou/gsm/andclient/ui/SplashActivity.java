package com.tonggou.gsm.andclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.tonggou.gsm.andclient.R;
import com.umeng.analytics.MobclickAgent;

public class SplashActivity extends BaseActivity implements Runnable {
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		MobclickAgent.updateOnlineConfig( this );
		setContentView(R.layout.activity_splash);
		
		new Handler().postDelayed(this, 2 * 1000);
	}

	@Override
	public void run() {
		startActivity(new Intent(this, LoginActivity.class));
		finish();
	}
}
