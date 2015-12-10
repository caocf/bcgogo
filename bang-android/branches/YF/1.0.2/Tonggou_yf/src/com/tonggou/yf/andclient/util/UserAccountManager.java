package com.tonggou.yf.andclient.util;

import android.content.Context;

import com.tonggou.lib.util.PreferenceUtil;
import com.tonggou.yf.andclient.Constants;

public class UserAccountManager {

	public static void storeUserAccount(Context context, String userNo, String pwd) {
		PreferenceUtil.getPreference(context, Constants.PREF.NAME_USER_INFO)
			.edit()
			.putString(Constants.PREF.KEY_USER_NO, userNo)
			.putString(Constants.PREF.KEY_USER_PWD, pwd)
			.commit();
	}
	
	public static String restoreUserNo(Context context) {
		return PreferenceUtil.getString(context, Constants.PREF.NAME_USER_INFO, Constants.PREF.KEY_USER_NO);
	}
	
	public static String restoreUserPwd(Context context) {
		return PreferenceUtil.getString(context, Constants.PREF.NAME_USER_INFO, Constants.PREF.KEY_USER_PWD);
	}
	
}
