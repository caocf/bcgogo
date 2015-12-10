package com.tonggou.gsm.andclient;

import com.baidu.mapapi.model.LatLng;

/**
 * 静态常量
 *
 * @author lwz
 */
public final class Constants {

	// 苏州 GEO
	public static LatLng GEO_DEFAULT = new LatLng(31.296266, 120.733165);

	/**
	 * sharedpreference name and key
	 *
	 */
	public static class PREF {
		public static final String PREF_NAME_USER_INFO = "pref_user_info";
		public static final String PREF_KEY_USER_INFO_JSON_STR = "user_info_json_str";
		public static final String PREF_KEY_VEHICLE_INFO_JSON_STR = "app_vehicle_info_json_str";
		public static final String PREF_KEY_SHOP_INFO_JSON_STR = "app_shop_info_json_str";

		public static final String PREF_NAME_OTHER_INFO = "pref_other_info";
		public static final String PREF_KEY_STATISTICS_VALUE_JSON_STR = "statistics_values_json_str";
		public static final String PREF_KEY_SHOP_NOTICE_JSON_STR = "shop_notice_json_str";

		public static final String PREF_NAME_LONGIN_INFO = "pref_login_info";
		public static final String PREF_KEY_LONGIN_INFO_IS_REMEMBER_PWD = "is_remember_pwd";

		public static final String PREF_NAME_UMENG_INFO = "pref_umeng_info";
		public static final String PREF_KEY_DEVICE_TOKEN = "device_token";

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
		public static final int POLLING_VEHICLE_LOCATION_INTERVAL = 60 * 1000;

		public static final String DOWNLOAD_APK_FILE_SUFFIX = ".apk";

	}
}
