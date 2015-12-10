package com.tonggou.yf.andclient.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class PackageInfoUtil {

	public static PackageInfo getLocalPackageInfo(Context context) throws NameNotFoundException{
        PackageManager packageManager = context.getPackageManager();
        return packageManager.getPackageInfo( context.getPackageName(),0);
	}
	
	public static String getVersionName(Context context) {
		try {
			return getLocalPackageInfo(context).versionName;
		} catch (NameNotFoundException e) {
			return "";
		}
	}
	
	public static int getVersionCode(Context context) {
		try {
			return getLocalPackageInfo(context).versionCode;
		} catch (NameNotFoundException e) {
			return 0;
		}
		
	}
	
}
