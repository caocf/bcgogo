package com.tonggou.andclient;

import android.app.Activity;

import com.umeng.analytics.MobclickAgent;

public class AbsUMengActivity extends Activity {
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
