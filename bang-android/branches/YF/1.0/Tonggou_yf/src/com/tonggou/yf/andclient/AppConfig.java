package com.tonggou.yf.andclient;

public class AppConfig {
	public static final boolean DBG = true;
	
	public static final String BASE_URL = getHost();
	
	public static String getHost() {
//		return "http://192.168.1.23:8080/api";
//		return "https://phone.bcgogo.com:443/api";
		return "http://192.168.1.23:8080/api";
//		return "https://phone.bcgogo.com:443/api";
//		return "https://shop.bcgogo.com/api";
	}
	
}
