package com.tonggou.gsm.andclient.net;

import com.tonggou.gsm.andclient.AppConfig;

public class API {
	
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
	
	/** 注册 验证*/
	public static final String REGISTER_VALIDATE = BASE_URL + "/register/gsm/validateRegister";
	/** 注册 */
	public static final String REGISTER = BASE_URL + "/register/gsm/register";
	/** 登录 */
	public static final String LOGIN = BASE_URL + "/gsm/login";
	/** 更新车辆信息 */
	public static final String UPDATE_VEHICLE_INFO = BASE_URL + "/vehicle/saveGsmVehicle";
	/** 查询行车日志请求 */
	public static final String QEURY_DRIVE_LOG = BASE_URL + "/driveLog/driveLogContents/";
	/** 查询行车轨迹*/
	public static final String QEURY_DRIVING_TRACK = BASE_URL + "/driveLog/detail/";
	/** 查询车辆信息(主要用来查询 位置信息)*/
	public static final String QEURY_VEHICLE_INFO_WITH_LOCATION = BASE_URL + "/vehicle/gsmUserGetAppVehicle";
	/** 查询故障码 */
	public static final String QEURY_DTC = BASE_URL + "/vehicle/faultCodeList";
	/** 修改故障码状态 */
	public static final String MODIFY_DTC_STATUS = BASE_URL + "/vehicle/faultCode";
	/** 预约服务*/
	public static final String APPOINTMENT_SERVICE = BASE_URL + "/service/appointment";
	/** 服务历史查询（我的 账单）*/
	public static final String QUERY_SERVICE_HISTORY = BASE_URL + "/service/historyList";
	/** 服务详情*/
	public static final String QUERY_SERVICE_HISTORY_DETAIL = BASE_URL + "/service/historyDetail";
	/** 查询消息*/
	public static final String QUERY_MESSAGE = BASE_URL + "/message/polling";
	/** 重置密码*/
	public static final String RESET_PWD = BASE_URL + "/user/gsm/password";
	/** 修改密码*/
	public static final String MODIFY_PWD = BASE_URL + "/user/password";
	/** 注销登录*/
	public static final String LOGOUT = BASE_URL + "/logout";
	/** 检测更新*/
	public static final String UPDATE = BASE_URL + "/newVersion/";
	/** 查询聚合违章城市 */
	public static final String QUERY_JUHE_CITY = BASE_URL + "/area/juhe/list";
	/** 查询违章 */
	public static final String QUERY_VIOLATION = BASE_URL + "/violateRegulations/queryVehicleViolateRegulation";
	/** 店铺公告 */
	public static final String QUERY_SHOP_NOTICE = BASE_URL + "/advert/advertList";
	/** 店铺公告详情 */
	public static final String QUERY_SHOP_NOTICE_DETAIL = BASE_URL + "/advert/advertDetail";
	/** 查询车辆数据统计信息*/
	public static final String QUERY_VEHICLE_DATA_STATISTIC = BASE_URL + "/driveStat/yearList";
	
}
