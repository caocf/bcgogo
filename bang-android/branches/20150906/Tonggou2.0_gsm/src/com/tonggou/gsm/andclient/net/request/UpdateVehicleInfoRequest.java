package com.tonggou.gsm.andclient.net.request;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.text.TextUtils;

import com.tonggou.gsm.andclient.UserBaseInfo;
import com.tonggou.gsm.andclient.bean.AppVehicleDTO;
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
	 * @param vehicleVin 车架号
	 * @param registNo 登记证书号
	 * @param engineNo 发动机号
	 */
	public void setRequestParams(final String vehicleNo, final String oilPrice, final String currentMileage, final String maintainPeriod,
			final String lastMaintainMileage, final String nextMaintainTime, final String nextExamineTime,
			final String juheCityName, final String juheCityCode, 
			final String vehilceVin, final String registNo, final String engineNo) {
		setRequestParams(vehicleNo, oilPrice, currentMileage, maintainPeriod, lastMaintainMileage, 
				convertDate(nextMaintainTime), convertDate(nextExamineTime),
				juheCityName, juheCityCode, vehilceVin, registNo, engineNo);
	}
	
	/**
	 * 
	 * @param oilPrice 油价, not null
	 * @param currentMileage 当前里程, not null
	 * @param maintainPeriod 保养周期, not null
	 * @param lastMaintainMileage 上次保养里程
	 * @param nextMaintainTime 下次保养时间
	 * @param nextExamineTime 下次验车时间
	 * @param vehicleVin 车架号
	 * @param registNo 登记证书号
	 * @param engineNo 发动机号
	 */
	public void setRequestParams(final String vehicleNo, final String oilPrice, final String currentMileage, final String maintainPeriod,
			final String lastMaintainMileage, final long nextMaintainTime, final long nextExamineTime,
			final String juheCityName, final String juheCityCode, 
			final String vehilceVin, final String registNo, final String engineNo) {
		HttpRequestParams params = new HttpRequestParams();
		params.put("vehicleNo", vehicleNo);
		params.put("oilPrice", oilPrice);
		params.put("currentMileage", currentMileage);
		params.put("maintainPeriod", maintainPeriod);
		params.put("lastMaintainMileage", lastMaintainMileage);
		params.put("nextMaintainTime", String.valueOf(nextMaintainTime));
		params.put("nextExamineTime", String.valueOf(nextExamineTime));
		params.put("nextExamineTime", String.valueOf(nextExamineTime));
		params.put("juheCityName", juheCityName);
		params.put("juheCityCode", juheCityCode);
		params.put("vehicleVin", vehilceVin);
		params.put("registNo", registNo);
		params.put("engineNo", engineNo);
		super.setRequestParams(params);
	}
	
	/**
	 * 
	 * @param vehicleVin 车架号
	 * @param registNo 登记证书号
	 * @param engineNo 发动机号
	 */
	public void setRequestParams(final String juheCityName, final String juheCityCode, final String vehilceVin, final String registNo, final String engineNo) {
		AppVehicleDTO vehicleInfo = UserBaseInfo.getVehicleInfo();
		setRequestParams(vehicleInfo.getVehicleNo(), vehicleInfo.getOilPrice(), String.valueOf(vehicleInfo.getCurrentMileage()),
				String.valueOf(vehicleInfo.getMaintainPeriod()), null, 
				null, null, juheCityName, juheCityCode, vehilceVin, registNo, engineNo);
	}
	
	/**
	 * 将时间字符串转化为 时间
	 * @param dateStr
	 * @return
	 */
	private long convertDate(String dateStr) {
		if( TextUtils.isEmpty(dateStr) ) {
			return 0;
		}
		DateTimeFormatter format = DateTimeFormat.forPattern(StringUtil.DATE_FORMAT_DATE_TIME_YYYY_MM_dd_HH_mm);
		return DateTime.parse(dateStr, format).getMillis();
	}
	
}
