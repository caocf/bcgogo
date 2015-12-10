package com.tonggou.yf.andclient.net;

import com.tonggou.yf.andclient.AppConfig;

public class API {

	private static final String BASE_URL = AppConfig.getHost() + "/bcgogoApp";
	
	/**
	 * 登录
	 */
	public static final String LOGIN = BASE_URL + "/login";
	
	/**
	 * 短信模版
	 */
	public static final String QUERY_SMS_TEMPLET = BASE_URL;
	
	/**
	 * 短信模版
	 */
	public static final String SEND_SMS = BASE_URL + "/sendMsg";
	
	/**
	 * 预约或者保养更改为已处理
	 */
	public static final String REMIND_HANDLE = BASE_URL + "/remindHandle";
	
	/**
	 * 接受预约单
	 */
	public static final String ACCEPT_APPOINT = BASE_URL + "/acceptAppoint";
	
	/**
	 * 更改服务时间
	 */
	public static final String CHANGE_APPOINT_TIME = BASE_URL + "/changeAppointTime";
	
	/**
	 * 查询故障列表
	 */
	public static final String QUERY_DTC_LIST = BASE_URL + "/vehicleFaultInfoList";
	
	/**
	 * 查询预约列表
	 */
	public static final String QUERY_APPOINT_LIST = BASE_URL + "/appointOrderList";
	
	/**
	 * 查询保养列表
	 */
	public static final String QUERY_MAINTAIN_LIST = BASE_URL + "/customerRemindList";
	
	/**
	 * 检测更新版本
	 */
	public static final String UPDATE_VERSION = AppConfig.getHost() + "/bcgogoNewVersion";
	
	/**
	 * 登出
	 */
	public static final String LOGOUT = BASE_URL + "/logout";
	
}
