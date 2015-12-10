package com.tonggou.andclient.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.net.wifi.WifiManager;

public class SomeUtil {
	public static SimpleDateFormat dfOut = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static SimpleDateFormat dfOut2 = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 2013-09-03 12:12转换成时间戳
	 * 
	 * @param dateStr
	 * @return
	 */
	public static long StringDateToLong(String dateStr) {
		if (dateStr == null || dateStr.equals("")) {
			return 0;
		}
		Date dd = null;
		try {
			dd = dfOut.parse(dateStr);
			if (dd != null) {
				return dd.getTime();
			}
		} catch (Exception e) {
			return 0;
		}

		return 0;
	}

	/**
	 * 时间戳转换成2013-09-03
	 * 
	 * @param dateStr
	 * @return
	 */

	public static String longToStringDate(String timeStr) {
		if (timeStr == null || timeStr.equals("")) {
			return "";
		}

		/*
		 * if(!isNumeric(timeStr)){ return ""; }
		 */

		long timeLong = new Long(timeStr).longValue();

		String dateStr = dfOut.format(new Date(timeLong));

		return dateStr;
	}

	public static String longToStringDate2(String timeStr) {
		if (timeStr == null || timeStr.equals("")) {
			return "";
		}

		/*
		 * if(!isNumeric(timeStr)){ return ""; }
		 */

		long timeLong = new Long(timeStr).longValue();

		String dateStr = dfOut2.format(new Date(timeLong));

		return dateStr;
	}

	public static long parseShop2DCode(String dateStr) {
		if (dateStr == null || dateStr.equals("")) {
			return 0;
		}
		if (dateStr.indexOf(",") == -1) {
			return 0;
		}
		String num = dateStr.substring(0, dateStr.indexOf(","));
		if (num == null || num.equals("")) {
			return 0;
		}
		if (!isNumeric(num)) {
			return 0;
		}

		return Long.valueOf(num);
	}

	public static boolean isNumeric(String str) { // 判断纯数字
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	/*
	 * public static boolean isPhoneNumberValid(String phoneNumber) { //String
	 * regExp =
	 * "^13[0-9]{1}[0-9]{8}$|15[0125689]{1}[0-9]{8}$|18[0-3,5-9]{1}[0-9]{8}$";
	 * String regExp = "^(\\+86)?0?1[3|4|5|8]\\d{9}$"; Pattern p =
	 * Pattern.compile(regExp); Matcher m = p.matcher(phoneNumber); return
	 * m.matches();
	 * 
	 * }
	 */
	public static boolean isPhoneNumberValid(String phoneNumber) {
		Pattern p = Pattern.compile("^(\\+86)?0?1[3|4|5|8]\\d{9}$");
		Matcher m = p.matcher(phoneNumber);
		return m.matches();

	}

	// 判断手机格式是否正确
	public static boolean isMobileNo(String mobiles) {
		Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
		return p.matcher(mobiles).matches();
	}

	// 判断车牌号格式是否正确
	public static boolean isVehicleNo(String cainum) {
		Pattern p = Pattern.compile("^[\u4e00-\u9fa5]{1}[A-Z]{1}[A-Z_0-9]{5}$");
		Matcher m = p.matcher(cainum);

		return m.matches();
	}

	// 判断email格式是否正确
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);

		return m.matches();
	}

	public static boolean isRegisterName(String str) {
		for (int i = 0; i < str.length(); i++) {
			char tmp = str.charAt(i);
			if ((tmp >= 'A' && tmp <= 'Z') || (tmp >= 'a' && tmp <= 'z')) {
			} else if ((tmp >= '0') && (tmp <= '9')) {
			} else if ((tmp == '@') || (tmp == '_') || (tmp == '.')) {
			} else if (isChinese(tmp)) {
			} else {
				return false;
			}
		}
		// else if (isEmail(str)) {} //判断是否是邮箱

		if (isPhoneNumberValid(str)) {
		} else if (isVehicleNo(str)) {
		} else {
			return false;
		}
		return true;
	}

	public static boolean isName(String str) {
		for (int i = 0; i < str.length(); i++) {
			char tmp = str.charAt(i);
			if ((tmp >= 'A' && tmp <= 'Z') || (tmp >= 'a' && tmp <= 'z')) {
			} else if ((tmp >= '0') && (tmp <= '9')) {
			} else if (isChinese(tmp)) {
			} else {
				return false;
			}
		}
		return true;
	}

	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	// 判断是否是中文输入
	/**
	 * true 是中文 false 是英文
	 */
	public static boolean justIfChineseInput(String test) {
		byte[] bytes = test.getBytes();
		int i = bytes.length; // i为字节长度
		int j = test.length(); // j为字符长度
		// i是否等于j就可判断是纯中文或纯英语
		if (i == j) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean checkWifiEnable(Activity activitiy) {
		WifiManager mWifiManager = (WifiManager) activitiy.getSystemService(Context.WIFI_SERVICE);
		if (mWifiManager.isWifiEnabled()) {
			// System.out.println("**** WIFI is on");
			return true;
		} else {
			// System.out.println("**** WIFI is off");
			return false;
		}
	}

	public static boolean isActivityOn(Context context, Class<?> activityCls) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTasks = activityManager.getRunningTasks(20);
		String clsName = activityCls.getName();
		for (RunningTaskInfo taskInfo : runningTasks) {
			if (clsName.equals(taskInfo.topActivity.getClass().getName())) {
				return true;
			}
		}
		return false;
	}
}
