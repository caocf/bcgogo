package com.tonggou.gsm.andclient.net;

import com.tonggou.gsm.andclient.AppConfig;
/**
 * new API added 
 * @author peter
 */
public class APINew {

	public static class JUHE {
		// 违章
		private static final String KEY_TRANSGRESS = "60ad2a9b3c7bcda13b781dabe01fe843";
		public static final String GET_SUPPORT_CITYS = "http://v.juhe.cn/wz/citys?key=" + KEY_TRANSGRESS;
		public static final String TRANSGRESS_QUERY = "http://v.juhe.cn/weizhang/query?key=" + KEY_TRANSGRESS;

		// 加油站
		private static final String KEY_GAS_STATION = "c8adc805a4a1fdeb7a79798d03d06a46";
		public static final String QUERY_GAS_STATION = "http://apis.juhe.cn/oil/local?key=" + KEY_GAS_STATION;
	}

	private static final String BASE_URL = AppConfig.BASE_URL;

	

	public static final String QUERY_VEHICLE = BASE_URL + "/vehicle";
	private static final String QUERY_USER = BASE_URL + "/user";
	public static final String LOGIN = QUERY_USER + "/login";
	
	/* 获得碰撞列表  ***/
	public static String getQueryViedeoRecordAPI(long id, long time, int count) {
		return String.format( QUERY_VEHICLE + "/%d" + "/collision/from/" + "%d" + "/limit/" + "%d", id, time, count);
	}

	/* 主动拍照上传命令  */
	public static String getRealTimeMonitorAPI (long id) {
		return String.format(QUERY_VEHICLE + "/" + "%d" + "/monitor", id);
	}

	/* 代金券总额	***/
	public static String getQueryVoucherTotalAPI(String userId) {
		return String.format( QUERY_USER + "/" + "%s"  + "/coupon/total", userId);
	}

	/* 代金券消费请求URL	***/
	public static String setQueryVoucherConsumerAPI(String userId) {
		return String.format( QUERY_USER + "/" + "%s"  + "/coupon/consumer", userId);
	}

	/* 获得代金券消费明细URL ***/
	public static String getQueryVoucherConsumerDetailAPI(String userId, long time, int count) {
		return String.format( QUERY_USER + "/"+ "%s"  + "coupon/history/" + "%d" + "/limit/" + "%d", userId, time, count);
	}

	/*  获得周围店铺列表 	***/
	public static final String QUERY_SHOP_SURROUNDING = BASE_URL + "/shop/surrounding";
}