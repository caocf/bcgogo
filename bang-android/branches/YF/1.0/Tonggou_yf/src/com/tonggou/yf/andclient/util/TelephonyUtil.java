package com.tonggou.yf.andclient.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class TelephonyUtil {
	
	/**
	 * SIM 卡是否可用
	 * @param context
	 * @return true 可用，false 不可用
	 */
	public static boolean isSIMCardEnable(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return !(telephonyManager.getSimState() == TelephonyManager.SIM_STATE_ABSENT);
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
