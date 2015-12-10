package com.tonggou.andclient.network.request;

public class API {
	
	public static class JUHE_TRANSGRESS {
		private static final String KEY = "60ad2a9b3c7bcda13b781dabe01fe843";
		public static final String GET_SUPPORT_CITYS = "http://v.juhe.cn/wz/citys?key=" + KEY;
		public static final String TRANSGRESS_QUERY = "http://v.juhe.cn/wz/query?key=" + KEY;
	}
	
	private static final String BASE_URL = "http://192.168.1.33:8080/api";
	
	/** 登录 */
	public static final String LOGIN = BASE_URL + "/login";
	
	/** 获取车辆列表 */
	public static final String VEHICLE_LIST = BASE_URL + "/vehicle/list";
}
