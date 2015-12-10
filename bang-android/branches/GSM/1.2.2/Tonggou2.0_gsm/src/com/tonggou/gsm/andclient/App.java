package com.tonggou.gsm.andclient;

import org.joda.time.DateTimeZone;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.google.gson.JsonObject;
import com.tonggou.gsm.andclient.ui.LauncherActivity;
import com.tonggou.gsm.andclient.util.PackageInfoUtil;
import com.tonggou.gsm.andclient.util.UmengMessageUtil;
import com.umeng.message.PushAgent;
import com.umeng.message.UHandler;
import com.umeng.message.entity.UMessage;

public class App extends Application {

	private static final String TAG = "com.tonggou.gsm.andclient";
	public static final boolean DBG = AppConfig.DBG;

	private static App sINSTANCE;
	private BMapManager mBMapManager;
//	private AtomicInteger mBMapInstanceCount;
	
	@Override
	public void onCreate() {
		super.onCreate();
		sINSTANCE = this;
		initDateTimeZone();
        initUmengPushServer();
	}

	public static App getInstance() {
		return sINSTANCE;
	}
	
	private void initDateTimeZone() {
		DateTimeZone.setDefault(DateTimeZone.forOffsetHours(8));
	}
	
	private void initUmengPushServer() {
		PushAgent pushAgent = PushAgent.getInstance(this);
		pushAgent.setDebugMode(DBG);
		// 如果想使用SDK提供的通知展示消息， 但是只是想自定义点击行为， 可以调用下面的接口。
		pushAgent.setNotificationClickHandler(new UHandler() {

			@Override
			public void handleMessage(Context context, UMessage msg) {
				Intent intent = new Intent();
				intent.setClass(context, LauncherActivity.class);
				intent.setAction(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				context.startActivity(intent);
			}
		});
	}

	public synchronized BMapManager initBMapManager() {
		if (mBMapManager == null) {
//			mBMapInstanceCount = new AtomicInteger(0);
			mBMapManager = new BMapManager(App.getInstance());
			mBMapManager.init(new MKGeneralListener() {
				
				@Override
				public void onGetNetworkState(int iError) {
					if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
						App.debug("BMap ERROR_NETWORK_CONNECT");
					}
				}

				@Override
				public void onGetPermissionState(int iError) {
					if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
						App.debug("BMap ERROR_PERMISSION_DENIED");
					}
				}
			});
		}
//		mBMapInstanceCount.incrementAndGet();
		return mBMapManager;
	}
	
	/**
	 * 销毁 BMapManager，并置 null
	 */
	public void destoryBMapManager() {
		if( mBMapManager != null ) {
			mBMapManager.destroy();
		}
		mBMapManager = null;
	}
	
	/**
	 * NOTE: 该方法不需要再调用
	 * <p>在 退出 App 的时候已经调用 {@link App#destoryBMapManager()}销毁
	 */
	@Deprecated
	public void releaseBMapManager() {
//	NOTE : 在 退出 App 的时候已经销毁，故以下代码注释掉
//		if(mBMapInstanceCount.decrementAndGet()  <= 0 ) {
//			mBMapManager.destroy();
//			App.debug(TAG, "releaseBMapManager");
//			mBMapManager = null;
//		}
//		App.debug(TAG, "releaseBMapManager remain " + mBMapInstanceCount.get());
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
	
	public JsonObject getLoginInfo() {
		JsonObject loginInfoJson = new JsonObject();
		loginInfoJson.addProperty("platform", "ANDROID");
		loginInfoJson.addProperty("platformVersion", Build.VERSION.RELEASE + "-SDK" + Build.VERSION.SDK_INT);
		loginInfoJson.addProperty("mobileModel", Build.BRAND + "-" + Build.MODEL);
		loginInfoJson.addProperty("appVersion", PackageInfoUtil.getVersionName(this));
		loginInfoJson.addProperty("imageVersion", "480X800");
		loginInfoJson.addProperty("umDeviceToken", UmengMessageUtil.getDeviceToken(this));
		return loginInfoJson;
	}
	
}
