package com.tonggou.yf.andclient.util;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

public class ContextUtil {

	public static void startActivity(Context context,
			Class<? extends Activity> cls) {
		startActivity(context, cls, null);
	}

	public static void startActivity(Context context,
			Class<? extends Activity> cls, Bundle args) {
		startActivity(context, cls, args, -1);
	}

	public static void startActivity(Context context,
			Class<? extends Activity> cls, int flag) {
		startActivity(context, cls, null, flag);
	}

	public static void startActivity(Context context,
			Class<? extends Activity> cls, Bundle args, int flag) {
		Intent intent = new Intent(context, cls);
		if (args != null) {
			intent.putExtras(args);
		}
		if (flag >= 0) {
			intent.setFlags(flag);
		}
		context.startActivity(intent);
	}

	public static void startActivityForResult(Activity activity,
			Class<? extends Activity> cls, int requestCode) {
		startActivityForResult(activity, cls, requestCode, null, -1);
	}

	public static void startActivityForResult(Activity activity,
			Class<? extends Activity> cls, int requestCode, Bundle args) {
		startActivityForResult(activity, cls, requestCode, args, -1);
	}

	public static void startActivityForResult(Activity activity,
			Class<? extends Activity> cls, int requestCode, int flag) {
		startActivityForResult(activity, cls, requestCode, null, flag);
	}

	public static void startActivityForResult(Activity activity,
			Class<? extends Activity> cls, int requestCode, Bundle args,
			int flag) {
		Intent intent = new Intent(activity, cls);
		if (args != null) {
			intent.putExtras(args);
		}
		if (flag >= 0) {
			intent.setFlags(flag);
		}
		activity.startActivityForResult(intent, requestCode);
	}
	
	public static void startService( Context context, String action, Bundle args ) {
		Intent service = new Intent(action);
		if (args != null) {
			service.putExtras(args);
		}
		context.startService(service);
	}
	
	public static void startService( Context context, Class<? extends Service> cls, Bundle args ) {
		Intent service = new Intent(context, cls);
		if (args != null) {
			service.putExtras(args);
		}
		context.startService(service);
	}
	
	public static void sendBroadcast( Context context, String action) {
		sendBroadcast(context, action, null);
	}
	
	public static void sendBroadcast( Context context, String action, Bundle args) {
		Intent intent = new Intent(action);
		if (args != null) {
			intent.putExtras(args);
		}
		context.sendBroadcast(intent);
	}

	public static void phoneCall(Context context, String mobile) {
		if( TextUtils.isEmpty(mobile) ) {
			return;
		}
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("tel:" + mobile));
		context.startActivity(intent);
	}
}
