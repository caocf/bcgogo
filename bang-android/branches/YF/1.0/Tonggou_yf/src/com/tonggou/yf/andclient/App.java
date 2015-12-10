package com.tonggou.yf.andclient;

import org.androidannotations.annotations.EApplication;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.tonggou.yf.andclient.ui.LauncherActivity_;
import com.umeng.message.PushAgent;
import com.umeng.message.UHandler;
import com.umeng.message.entity.UMessage;

@EApplication
public class App extends Application {

	private static final String TAG = "com.tonggou.yf.andclient";
	public static final boolean DBG = AppConfig.DBG;

	private static App sINSTANCE;
	
	@Override
	public void onCreate() {
		super.onCreate();
		sINSTANCE = this;
		
		initUmengPushServer();
	}

	public static App getInstance() {
		return sINSTANCE;
	}
	
	private void initUmengPushServer() {
		PushAgent pushAgent = PushAgent.getInstance(this);
		pushAgent.setDebugMode(DBG);
		// 如果想使用SDK提供的通知展示消息， 但是只是想自定义点击行为， 可以调用下面的接口。
		pushAgent.setNotificationClickHandler(new UHandler() {

			@Override
			public void handleMessage(Context context, UMessage msg) {
				Intent intent = new Intent();
				intent.setClass(context, LauncherActivity_.class);
				intent.setAction(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				context.startActivity(intent);
			}
		});
	}
	
	//////// util method
	public static void showShortToast(Object msg) {
		Toast.makeText(sINSTANCE, String.valueOf(msg), Toast.LENGTH_SHORT).show();
	}

	public static void showLongToast(Object msg) {
		Toast.makeText(sINSTANCE, String.valueOf(msg), Toast.LENGTH_LONG).show();
	}

	public static void debug(String tag, Object msg) {
		if (DBG)
			Log.d(TextUtils.isEmpty(tag) ? TAG : tag, String.valueOf(msg));
	}

	public static void debug(Object msg) {
		debug(null, msg);
	}

	public static void error(Throwable e, String... tag) {
		if (DBG)
			Log.e(tag != null && tag.length > 0 ? tag[0] : TAG, "", e);
	}
	
}
