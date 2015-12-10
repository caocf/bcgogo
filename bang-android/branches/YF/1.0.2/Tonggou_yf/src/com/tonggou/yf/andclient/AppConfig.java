package com.tonggou.yf.andclient;

public class AppConfig {
	public static final boolean DBG = false;
	
//	public static String BASE_URL = getHost();
	public static String BASE_URL = "https://phone.bcgogo.com:1443/api";
	
	public static String getHost() {
//		return "http://192.168.1.23:8080/api";
//		return "https://phone.bcgogo.com:443/api";
//		return "http://192.168.1.23:8080/api";
		return BASE_URL;
//		return "https://shop.bcgogo.com/api";
	}
	
	public static void setHost(final String url) {
		BASE_URL = url;
	}
	
}
