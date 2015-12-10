package com.tonggou.gsm.andclient.net.request;

import android.text.TextUtils;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.net.API;
import com.tonggou.gsm.andclient.net.AbsTonggouHttpRequest;
import com.tonggou.gsm.andclient.net.HttpMethod;
import com.tonggou.gsm.andclient.net.HttpRequestParams;
import com.tonggou.gsm.andclient.util.StringUtil;

public class RegisterRequest extends AbsTonggouHttpRequest {

	@Override
	public String getAPI() {
		return API.REGISTER;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.PUT;
	}

	/**
	 * 
	 * @param phoneNo 手机号, not null
	 * @param pwd 密码, 	not null
	 * @param imei IMEI 号, not null
	 * @param oilPrice 油价, not null
	 * @param currentMileage 当前里程, not null
	 * @param maintainPeriod 保养周期, not null
	 * @param lastMaintainMileage 上次保养里程
	 * @param nextMaintainTime 下次保养时间
	 * @param nextExamineTime 下次验车时间
	 */
	public void setRequestParams(final String phoneNo, final String pwd, final String imei,
			final String oilPrice, final String currentMileage, final String maintainPeriod,
			final String lastMaintainMileage, final String nextMaintainTime, final String nextExamineTime,
			final String juheCityName, final String juheCityCode, 
			final String vehilceVin, final String registNo, final String engineNo) {
		HttpRequestParams params = new HttpRequestParams();
		params.put("mobile", phoneNo);
		params.put("password", pwd);
		params.put("imei", imei);
		params.put("oilPrice", oilPrice);
		params.put("currentMileage", currentMileage);
		params.put("maintainPeriod", maintainPeriod);
		params.put("lastMaintainMileage", lastMaintainMileage);
		params.put("nextMaintainTime", convertDate(nextMaintainTime));
		params.put("nextExamineTime", convertDate(nextExamineTime));
		params.put("juheCityName", juheCityName);
		params.put("juheCityCode", juheCityCode);
		params.put("vehicleVin", vehilceVin);
		params.put("registNo", registNo);
		params.put("engineNo", engineNo);
		params.put("loginInfo", App.getInstance().getLoginInfo());
		super.setRequestParams(params);
	}
	
	/**
	 * 将时间字符串转化为 时间
	 * @param dateStr
	 * @return
	 */
	private String convertDate(String dateStr) {
		if( TextUtils.isEmpty(dateStr) ) {
			return null;
		}
		return String.valueOf(StringUtil.formatDateTimeYYYYMMddHHmm(dateStr));
	}
	
}
