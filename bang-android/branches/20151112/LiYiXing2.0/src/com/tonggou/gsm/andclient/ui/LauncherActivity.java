package com.tonggou.gsm.andclient.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.util.ContextUtil;
import com.umeng.message.PushAgent;

/**
 * 项目启动的 Activity,退出程序时也从这里退出。
 * <p>使用 Intent.FLAG_ACTIVITY_CLEAR_TOP,将会清楚该 Activity 之上的所有 Activity
 * 在该 Activity 中添加配置  android:launchMode="singleTop",这样就会调用  onNewIntent() 方法，
 * 在其中 finish 掉该 Activity 就可以实现退出 所有可见的 Activity, 从而实现退出 App
 * @author lwz
 * 
 */
public class LauncherActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if( intent != null && (Intent.FLAG_ACTIVITY_CLEAR_TOP & intent.getFlags()) != 0 ) {
			finish();
			return;
		}

		App.getInstance().InitDituSDK();
		PushAgent.getInstance(this).enable();
		super.onCreate(savedInstanceState);
		ContextUtil.startActivity(this, SplashActivity.class);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		if ((Intent.FLAG_ACTIVITY_CLEAR_TOP & intent.getFlags()) != 0) {
			finish();
		}
	}

	/**
	 * 退出 App
	 * @param context
	 */
	public static void exitApp(Context context) {
		ContextUtil.startActivity(context, LauncherActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
	}
}