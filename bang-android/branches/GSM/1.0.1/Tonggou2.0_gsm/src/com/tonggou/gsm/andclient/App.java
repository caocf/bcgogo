package com.tonggou.gsm.andclient;

import java.util.concurrent.atomic.AtomicInteger;

import android.app.Application;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.google.gson.JsonObject;
import com.tonggou.gsm.andclient.util.PackageInfoUtil;

public class App extends Application {

	private static final String TAG = "com.tonggou.gsm.andclient";
	public static final boolean DBG = false;

	private static App sINSTANCE;
	private BMapManager mBMapManager;
	private AtomicInteger mBMapInstanceCount;
	
	@Override
	public void onCreate() {
		super.onCreate();
		sINSTANCE = this;
	}

	public static App getInstance() {
		return sINSTANCE;
	}

	public BMapManager initBMapManager() {
		if (mBMapManager == null) {
			mBMapInstanceCount = new AtomicInteger(0);
			mBMapManager = new BMapManager(App.getInstance());
			mBMapManager.init(Constants.BMAP_KEY, new MKGeneralListener() {
				
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
		mBMapInstanceCount.incrementAndGet();
		App.debug(TAG, "initBMapManager  " + mBMapInstanceCount.get());
		return mBMapManager;
	}
	
	public void releaseBMapManager() {
		if(mBMapInstanceCount.decrementAndGet()  <= 0 ) {
			mBMapManager.destroy();
			App.debug(TAG, "releaseBMapManager");
			mBMapManager = null;
		}
		App.debug(TAG, "releaseBMapManager remain " + mBMapInstanceCount.get());
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
		return loginInfoJson;
	}
	
}
