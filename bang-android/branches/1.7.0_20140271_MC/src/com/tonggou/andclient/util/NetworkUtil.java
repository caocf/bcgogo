package com.tonggou.andclient.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
	
	public static boolean isNetworkConnected(Context context) { 
		if (context != null) { 
			return isNetworkInfoAvailable(getConnectivityManager(context).getActiveNetworkInfo());
		} 
		return false; 
	}
	
	public static boolean isWifiConnected(Context context) { 
		return isNetworkConnected(context, ConnectivityManager.TYPE_WIFI);  
	}
	
	public static boolean isMobileConnected(Context context) { 
		return isNetworkConnected(context, ConnectivityManager.TYPE_MOBILE); 
	}
	
	private static boolean isNetworkConnected(Context context, int networkType) {
		if (context != null) { 
			return isNetworkInfoAvailable(getConnectivityManager(context).getNetworkInfo(networkType));
		} 
		return false;
	}
	
	private static ConnectivityManager getConnectivityManager(Context context) {
		return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	private static boolean isNetworkInfoAvailable(NetworkInfo info) {
		if( info == null ) {
			return false;
		}
		return info.isAvailable();
	}
	
}
