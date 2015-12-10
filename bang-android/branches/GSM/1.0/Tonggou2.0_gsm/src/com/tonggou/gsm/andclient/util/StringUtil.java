package com.tonggou.gsm.andclient.util;

import java.lang.Integer;
import java.lang.String;
import java.util.Locale;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.text.TextUtils;
import android.widget.TextView;

import com.tonggou.gsm.andclient.App;

public class StringUtil {
	
	public static String getTextViewContent(TextView textView ) {
		return textView.getText().toString().trim();
	}
	
	/**
	 * 验证字符串是否为空，若为空就弹出 Toast，内容为  errorStringRes
	 * @param content
	 * @param errorStringRes
	 * @return true 验证未通过，false 验证通过
	 */
	public static boolean invalidateContent(String content, int errorStringRes) {
		if( TextUtils.isEmpty(content) ) {
			if( errorStringRes > 0 ) {
				App.showShortToast(App.getInstance().getString(errorStringRes));
			}
			return true;
		}
		return false;
	}
	
	/**
	 * 字符串是否为 IMEI 号
	 * @param imei
	 * @return true 是 IMEI 号， false 不是 IMEI 号
	 */
	public static boolean isIMEI(String imei) {
		char[] imeiChar = imeiString.toCharArray();
		int resultInt = 0;
		for (int i = 0; i < imeiChar.length-1; i++) {
			int a = Integer.parseInt(String.valueOf(imeiChar[i]));
			i++;
			final int temp = Integer.parseInt(String.valueOf(imeiChar[i])) * 2;
			final int b = temp < 10 ? temp : temp - 9;
			resultInt += a + b;
		}
		resultInt %= 10;
		resultInt = resultInt == 0 ? 0 : 10 - resultInt;
		int crc= Integer.parseInt(String.valueOf(imeiChar[14]));
		return (resultInt == crc);
	}
	
	/**
	 * 字符串是否为手机号
	 * @param phoneNo
	 * @return true 是 手机号， false 不是 手机号
	 */
	public static boolean isPhoneNo(String phoneNo) {
		return regxStr(phoneNo, "^(\\+86)?0?1[3|4|5|7|8]\\d{9}$");
	}
	
	/**
	 * 校验是否为手机号
	 * @param text
	 * @return true 是 手机号， false 不是 手机号
	 */
	public static boolean validatePhoneNo( TextView text ) {
		return isPhoneNo( getTextViewContent(text) );
	}
	
	/**
	 *  判断车牌号格式是否正确
	 * @param vehicleNo
	 * @return
	 */
	public static boolean isVehicleNo(String vehicleNo) {
		return regxStr(vehicleNo, "^[\u4e00-\u9fa5]{1}[A-Z]{1}[A-Z_0-9]{5}$");
	}
	
	/**
	 * 正则匹配
	 * @param validateStr	待校验的字符串
	 * @param pattern	正则表达式
	 * @return
	 */
	public static boolean regxStr( String validateStr, String pattern ) {
		Pattern p = Pattern.compile(pattern);
		return p.matcher(validateStr).matches();
	}
	
	/**
	 * 校验 油价
	 * @param oilPriceStr
	 * @return
	 */
	public static boolean validateOilPrice(String oilPriceStr, float minValue, float maxValue) {
		try {
			float oilPrice = Float.valueOf(oilPriceStr);
			return minValue <= oilPrice && oilPrice <= maxValue;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	final static String hourMinuteFormat = "HH:mm:ss";
	/**
	 * 格式化时间，格式为  HH:mm:ss
	 * @param during
	 * @return
	 */
	public static String formatHourMinute(long during) {
		return new DateTime(0,1,1,0,0).plusSeconds((int)during).toString(hourMinuteFormat);
	}
	
	public final static String DATE_FORMAT_YYYYMMdd = "yyyy-MM-dd";
	/**
	 * 
	 * @param millis
	 * @return
	 */
	public static String formatDateYYYYMMdd(long millis) {
		return new DateTime(millis).toString(DATE_FORMAT_YYYYMMdd);
	}
	
	public final static String DATE_FORMAT_DATE_TIME_YYYY_MM_dd_HH_mm = "yyyy-MM-dd HH:mm";
	public static String formatDateTimeYYYYMMddHHmm(long millis) {
		return new DateTime(millis).toString(DATE_FORMAT_DATE_TIME_YYYY_MM_dd_HH_mm);
	}
	
	public static long formatDateTimeYYYYMMddHHmm(String dateTimeStr) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern(StringUtil.DATE_FORMAT_DATE_TIME_YYYY_MM_dd_HH_mm);
		return DateTime.parse(dateTimeStr, formatter).getMillis();
	}
	
	public final static String DATE_FORMAT_DATE_TIME_WEEK = "yyyy-MM-dd EEEE HH:mm";
	public static String formatDateTimeWithWeek(long millis) {
		return new DateTime(millis).toString(DATE_FORMAT_DATE_TIME_WEEK);
	}
	
	/**
	 * during/3600L
	 * @param during
	 * @return
	 */
	public static String formatHour(long during) {
		return formatFloat1( during / 3600F );
	}
	
	/**
	 * during/60L
	 * @param during
	 * @return
	 */
	public static String formatMinute(long during) {
		return formatFloat1( during / 60F );
	}
	
	/**
	 * 格式化 float 型，只留小数点后一位（"%.1f"）
	 * @param oilWear
	 * @return
	 */
	public static String formatFloat1(float oilWear) {
		return String.format(Locale.getDefault(), "%.1f", oilWear);
	}
}
