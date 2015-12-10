package com.tonggou.gsm.andclient.net.request;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.text.TextUtils;

import com.tonggou.gsm.andclient.net.API;
import com.tonggou.gsm.andclient.net.AbsTonggouHttpRequest;
import com.tonggou.gsm.andclient.net.HttpMethod;
import com.tonggou.gsm.andclient.net.HttpRequestParams;
import com.tonggou.gsm.andclient.util.StringUtil;

public class UpdateVehicleInfoRequest extends AbsTonggouHttpRequest {

	@Override
	public String getAPI() {
		return API.UPDATE_VEHICLE_INFO;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.PUT;
	}

	/**
	 * 
	 * @param oilPrice 油价, not null
	 * @param currentMileage 当前里程, not null
	 * @param maintainPeriod 保养周期, not null
	 * @param lastMaintainMileage 上次保养里程
	 * @param nextMaintainTime 下次保养时间
	 * @param nextExamineTime 下次验车时间
	 */
	public void setRequestParams(final String oilPrice, final String currentMileage, final String maintainPeriod,
			final String lastMaintainMileage, final String nextMaintainTime, final String nextExamineTime) {
		HttpRequestParams params = new HttpRequestParams();
		params.put("oilPrice", oilPrice);
		params.put("currentMileage", currentMileage);
		params.put("maintainPeriod", maintainPeriod);
		params.put("lastMaintainMileage", lastMaintainMileage);
		params.put("nextMaintainTime", convertDate(nextMaintainTime));
		params.put("nextExamineTime", convertDate(nextExamineTime));
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
		DateTimeFormatter format = DateTimeFormat.forPattern(StringUtil.DATE_FORMAT_DATE_TIME_YYYY_MM_dd_HH_mm);
		return String.valueOf(DateTime.parse(dateStr, format).getMillis());
	}
	
}
