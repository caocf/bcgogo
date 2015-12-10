package com.tonggou.gsm.andclient.net;

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
	
//	private static final String HTTP = "http://";
//	private static final String HOST = "192.168.1.23:8080/api";
	
//	private static final String HTTP = "https://";
//	private static final String HOST = "phone.bcgogo.cn:1443/api";
	
	private static final String HTTP = "https://";
	private static final String HOST = "ios.bcgogo.com:1443/api";
	
//	public static final String HTTP = "https://"; 
//	public static final String HOST_IP = "phone.bcgogo.cn:1443/api";      //测试
//	public static final String HOST = "shop.bcgogo.com/api";    //真实运营
//  public static final String HTTP_HEAD = "http://";
//	public static final String HOST_IP = "192.168.1.33:8080/api";      //测试
	
	private static final String BASE_URL = HTTP + HOST;
	
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
	
}
