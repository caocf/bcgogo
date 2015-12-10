package com.tonggou.yf.andclient;


/**
 * 静态常量
 * 
 * @author lwz
 */
public final class Constants {
	
	/**
	 * sharedpreference name and key
	 * 
	 */
	public static class PREF {
		public static final String NAME_OTHER_INFO = "pref_other_info";
		public static final String NAME_USER_INFO = "pref_user_info";
		public static final String NAME_LONGIN_INFO = "pref_login_info";
		public static final String NAME_UMENG_INFO = "pref_umeng_info";
		
		public static final String KEY_LONGIN_INFO_IS_REMEMBER_PWD = "is_remember_pwd";
		public static final String KEY_USER_NO = "user_no";
		public static final String KEY_USER_PWD = "user_pwd";
		public static final String KEY_LONGIN_INFO = "login_info";
		public static final String KEY_DEVICE_TOKEN = "device_token";
	}
	
	public static class APP_CONFIG {
		
		public static final int QUERY_PAGE_SIZE = 10;
		
		public static final String DOWNLOAD_APK_FILE_SUFFIX = ".apk";
		
	}
}
