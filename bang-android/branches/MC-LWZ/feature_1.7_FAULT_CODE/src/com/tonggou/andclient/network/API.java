package com.tonggou.andclient.network;

import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.vo.type.DataKindType;

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
	
	private static final String BASE_URL = INFO.HTTP_HEAD + INFO.HOST_IP;
	
	/** 注册 */
	public static final String REGISTER = BASE_URL + "/user/registration";
	/** 登录 */
	public static final String LOGIN = BASE_URL + "/login";
	/** 获取车辆列表 */
	public static final String VEHICLE_LIST = BASE_URL + "/vehicle/list";
	/** 获取服务范围 */
	public static final String SERVICE_CATS_SCOPE = BASE_URL + "/serviceCategory/list";
	/** 获取店铺列表 */
	public static final String QUERY_SHOP_LIST = BASE_URL + "/shop/list";
	/** 找回密码 */
	public static final String FIND_PASSWORD = BASE_URL + "/user/password";
	/** 轮询消息 */
	public static final String POLLING_MESSAGE = BASE_URL + "/message/polling";
	/** 版本升级检测 */
	public static final String UPDATE_CHECK = BASE_URL + "/newVersion";
	/** 查询聚合支持城市的规则 */
	public static final String QUERY_JUHE_CITY_LIST = BASE_URL + "/violateRegulations/juhe/area/list";
	/** 保存车辆信息*/
	public static final String STORE_VEHICLE_INFO = BASE_URL + "/vehicle/vehicleInfo";
	/** 请求一辆车的信息 */
	public static final String QUERY_SINGLE_VEHICLE_INFO = BASE_URL + "/vehicle/singleVehicle";
	/** 删除车辆信息 */
	public static final String DELETE_VEHICLE_INFO = BASE_URL + "/vehicle/singleVehicle";
	/** 更新默认车辆*/
	public static final String UPDATE_DEFAULT_VEHICLE = BASE_URL + "/vehicle/updateDefault";
	/** 店铺详情 */
	public static final String SHOP_DETAIL = BASE_URL + "/shop/detail";
	/** 根据关键字获取店铺建议列表 */
	public static final String SHOP_SUGGESTION_BY_KEYWORD = BASE_URL + "/shop/suggestions";
//	/** 获取后台车辆信息建议 */
//	public static final String QUERY_VEHICLE_INFO_SUGGESTION = BASE_URL + "/vehicle/info/suggestion";
	/** 用户反馈*/
	public static final String FEEDBACK = BASE_URL + "/user/feedback";
	/** 绑定 OBD */
	public static final String OBD_BINDING = BASE_URL + "/obd/binding";
	/** 更新车辆故障字典 */
	public static final String UPDATE_VEHICLE_FAULT_DIC = BASE_URL + "/vehicle/faultDic";
	/** 发送车辆故障码 */
	public static final String SEND_VEHICLE_FAULT = BASE_URL + "/vehicle/fault";
	/** 修改车辆故障码状态 */
	public static final String MODIFY_VEHICLE_FAULT_STATUS = BASE_URL + "/vehicle/faultCode";
	/** 保存多车辆 */
	public static final String STORE_VEHICLE_INFOS = BASE_URL + "/vehicle/list/guest";
	/**修改密码*/
	public static final String MODIFY_PASSWORD = BASE_URL + "/user/password";
	/**发送车况信息*/
	public static final String SEND_VEHICLE_CONDITION = BASE_URL + "/vehicle/condition";
	/**登出*/
	public static final String LOGOUT = BASE_URL + "/logout";
	/** 查询故障码列表 */
	public static final String QUERY_FAULT_CODE_LIST = BASE_URL + "/vehicle/faultCodeList";
	/** 在线预约*/
	public static final String SEND_APPOINTMENT = BASE_URL + "/service/appointment";
	/**上传行车日志*/
	public static final String UPLOAD_DJITEM = BASE_URL + "/driveLog/newDriveLog";
	/**
	 * 游客模式的 API
	 * @author lwz
	 */
	public static final class GUEST {
		public static final DataKindType CURRENT_DATA_KIND = DataKindType.OFFICIAL;
		private static final String DATA_KIND = "/" + CURRENT_DATA_KIND.getValue();
		private static final String GUEST_MODE = "/guest";
		private static final String IMAGE_VERSION = "/IV_480X800";
		
		/** 获取店铺列表 */
		public static final String QUERY_SHOP_LIST = API.QUERY_SHOP_LIST + GUEST_MODE + DATA_KIND + IMAGE_VERSION ;
		/** 获取店铺列表 */
		public static final String SHOP_DETAIL = API.SHOP_DETAIL + GUEST_MODE + IMAGE_VERSION ;
		/** 根据关键字获取店铺建议列表 */
		public static final String SHOP_SUGGESTION_BY_KEYWORD = BASE_URL + "/shop/suggestions" + GUEST_MODE;
		/** 用户反馈*/
		public static final String FEEDBACK = BASE_URL + "/guest/feedback";
	}
}
