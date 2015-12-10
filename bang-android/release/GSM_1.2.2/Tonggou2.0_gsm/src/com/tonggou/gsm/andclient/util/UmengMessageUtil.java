package com.tonggou.gsm.andclient.util;

import com.tonggou.gsm.andclient.Constants;
import com.umeng.message.UmengRegistrar;

import android.content.Context;
import android.text.TextUtils;

public class UmengMessageUtil {

	/**
	 * 获得 设备唯一的标识 {@link UmengRegistrar #getRegistrationId(context)}
	 * @param context
	 * @return
	 */
	public static String getDeviceToken(Context context) {
		String deviceToken = UmengRegistrar.getRegistrationId(context);
		if( TextUtils.isEmpty(deviceToken) ) {
			deviceToken = restoreDeviceToken(context);
		} else {
			storeDeviceToken(context, deviceToken);
		}
		return deviceToken;
	}
	
	private static void storeDeviceToken(Context context, String deviceToken) {
		PreferenceUtil.putString(context,
				Constants.PREF.PREF_NAME_UMENG_INFO, Constants.PREF.PREF_KEY_DEVICE_TOKEN, deviceToken);
	}
	
	private static String restoreDeviceToken(Context context) {
		return PreferenceUtil.getString(context,
				Constants.PREF.PREF_NAME_UMENG_INFO, Constants.PREF.PREF_KEY_DEVICE_TOKEN);
	}
}
