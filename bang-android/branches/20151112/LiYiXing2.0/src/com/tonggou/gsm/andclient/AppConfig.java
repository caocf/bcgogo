package com.tonggou.gsm.andclient;

public class AppConfig {
	public static final boolean DBG = false;

	public static final String BASE_URL = getBaseURL();

	/** Baidu map authorization key */
	// 转移到 AndroidManifest meta 中了
//	public static final String BMAP_KEY = "HxuoGACnVjp4XXM8UuiKww9d"; // 正式的 KEY,正式发布时使用
//	public static final String BMAP_KEY = "XA0j6mZ7lN16kKnAPA9me9F0"; // lwz PC DEBUG

	public static String getBaseURL() {
//		return "http://192.168.1.23:8080/api";
//		return "https://phone.bcgogo.com:443/api";
//		return "http://192.168.1.23:8080/api";
		//return "https://phone.bcgogo.com:443/api";
		return "http://phone.bcgogo.com:8035/api";
		//return "https://shop.bcgogo.com/api";
		//return "http://192.168.1.174:8080/api";
	}
}