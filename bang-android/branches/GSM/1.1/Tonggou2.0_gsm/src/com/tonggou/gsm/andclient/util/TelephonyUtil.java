package com.tonggou.gsm.andclient.util;

import android.content.Context;
import android.telephony.TelephonyManager;

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
		
}
