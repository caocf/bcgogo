package com.tonggou.gsm.andclient;

import com.baidu.platform.comapi.basestruct.GeoPoint;

/**
 * 静态常量
 * 
 * @author lwz
 */
public final class Constants {
	
	/**
	 * Baidu map authorization key
	 */
	public static final String BMAP_KEY = "HxuoGACnVjp4XXM8UuiKww9d"; // 正式的 KEY,正式发布时使用
//	public static final String BMAP_KEY = "XA0j6mZ7lN16kKnAPA9me9F0"; // lwz PC DEBUG 
	
	// 苏州 GEO
	public static final GeoPoint GEO_SUZHOU = new GeoPoint((int) (31.296266 * 1E6), (int) (120.733165 * 1E6)); 
	
	/**
	 * sharedpreference name and key
	 * 
	 */
	public static class PREF {
		public static final String PREF_NAME_USER_INFO = "pref_user_info";
		public static final String PREF_KEY_USER_INFO_JSON_STR = "user_info_json_str";
		public static final String PREF_KEY_VEHICLE_INFO_JSON_STR = "vehicle_info_json_str";
		public static final String PREF_KEY_SHOP_INFO_JSON_STR = "app_shop_info_json_str";
		
		public static final String PREF_NAME_OTHER_INFO = "pref_other_info";
		public static final String PREF_KEY_STATISTICS_VALUE_JSON_STR = "statistics_values_json_str";
		
		public static final String PREF_NAME_LONGIN_INFO = "pref_login_info";
		public static final String PREF_KEY_LONGIN_INFO_IS_REMEMBER_PWD = "is_remember_pwd";
	}
	
	public static class NETWORK_STATUS_CODE {
		public static final int CODE_LOGIN_EXPIRE = -202;
	}
	
	public static class DEVICE_PHONE_NO_SET {
		public static final String ORDER_SEPARATOR = ",";
		/**
		 * 主控号码设置
		 */
		public static final String PRIMARY_PHONE_NO_SET_ORDER_PREFIX = "adm123456";
		/**
		 * 救援号码设置
		 */
		public static final String SOS_PHONE_SET_ORDER_PREFIX = "sos123456";
	}
	
	public static class APP_CONFIG {
		public static final int QUERY_PAGE_SIZE = 10;
		/**
		 * 轮询消息间隔时间
		 */
		public static final int POLLING_MESSAGE_INTERVAL = 60 * 1000;
		/**
		 * 轮询车辆位置间隔时间
		 */
		public static final int POLLING_VEHICLE_LOCATION_INTERVAL = 30 * 1000;
		
		public static final String DOWNLOAD_APK_FILE_SUFFIX = ".apk";
		
	}
}
