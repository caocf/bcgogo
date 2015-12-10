package com.tonggou.yf.andclient.ui;

import org.androidannotations.annotations.EActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.tonggou.yf.andclient.util.UmengMessageUtil;
import com.umeng.message.PushAgent;

/**
 * 项目启动的 Activity,退出程序时也从这里退出。
 * <p>使用 Intent.FLAG_ACTIVITY_CLEAR_TOP,将会清楚该 Activity 之上的所有 Activity
 * 在该 Activity 中添加配置  android:launchMode="singleTop",这样就会调用  onNewIntent() 方法，
 * 在其中 finish 掉该 Activity 就可以实现退出 所有可见的 Activity, 从而实现退出 App
 * @author lwz
 * 
 */
@EActivity
public class LauncherActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if( intent != null && (Intent.FLAG_ACTIVITY_CLEAR_TOP & intent.getFlags()) != 0 ) {
			finish();
			return;
		}
		
		PushAgent.getInstance(this).enable();
		UmengMessageUtil.getDeviceToken(this);
		LoginActivity_.intent(this).start();
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
		LauncherActivity_.intent(context)
			.flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP)
			.start();
	}

}
