package com.tonggou.andclient;

import com.tonggou.andclient.app.UploadLocalCarCondition;
import com.tonggou.andclient.util.SomeUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
		if (activeNetworkInfo != null && activeNetworkInfo.isAvailable()) {
			NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			
			// 有网络的时候就上传故障码
			uploadLocalCarCondition(context);
			
			if (mobileInfo != null && mobileInfo.isConnected()) {
				// 连接到移动网络
			} else if (wifiInfo != null && wifiInfo.isConnected()) {
				// 连接到wifi
				uploadDJs(context);
			}
		} else {
			// 网络断开
		}
	}

	private void uploadDJs(Context context) {
		// 同一事件，该广播有可能接收多次，下面的判断防止重复开启
		if (!SomeUtil.isServiceRunning(context, DJUploadService.class)) {
			context.startService(new Intent(DJUploadService.ACTION_UPLOAD_DRIVING_JOURNALS));
		}
	}
	
	public void uploadLocalCarCondition(Context context) {
		context.startService(new Intent(context, UploadLocalCarCondition.class));
	}

}
