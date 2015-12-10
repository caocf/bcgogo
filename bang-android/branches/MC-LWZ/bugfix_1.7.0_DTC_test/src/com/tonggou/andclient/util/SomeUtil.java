package com.tonggou.andclient.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.R.integer;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.vo.DrivingJournalItem;
import com.tonggou.andclient.vo.SamplePoint;
import com.tonggou.andclient.vo.VehicleInfo;

public class SomeUtil {
	public static SimpleDateFormat dfOut = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static SimpleDateFormat dfOut2 = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final String FIRST_DIVIDER = "|";
	public static final String TRANS_FIRST_DIVIDER = "\\|";
	public static final String SECOND_DIVIDER = ",";
	public static final int MAX_LAT = 0;
	public static final int MIN_LAT = 1;
	public static final int MAX_LON = 2;
	public static final int MIN_LON = 3;
	private static final double MIN_REC_DIST = 300d;
	public static final int TYPE_SA = 1001;
	public static final int TYPE_EA = 1002;
	public static final int TYPE_DAY = 0;
	public static final int TYPE_WEEK = 1;
	public static final int TYPE_MONTH = 2;
	public static final double MINUTE_RATE = 1d / (1000 * 60);
	public static final double HOUR_RATE = 1d / (1000 * 60 * 60);

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
		try {
			long timeLong = new Long(timeStr).longValue();
			String dateStr = dfOut2.format(new Date(timeLong));
	
			return dateStr;
		} catch (NumberFormatException e) {
			return "未知";
		}

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

	/**
	 * 判断是否为 VIN 码（车架号）
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isVinCode(String str) {
		Pattern pattern = Pattern.compile("^[a-hj-npr-zA-HJ-NPR-Z][a-hj-npr-zA-HJ-NPR-Z0-9]*");
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

	/**
	 * 转换 OBD DTC 结果
	 * 
	 * @param originResult
	 * @return
	 */
	public static String transformDTCResult(String originResult) {
		byte[] byteArr = originResult.getBytes();
		int size = byteArr.length;
		for (int i = 0; i < size; i++) {
			byte b = byteArr[i];
			if (b >= 0x3a && b <= 0x3f) {
				byteArr[i] = (byte) (b + 7);
			}
		}
		return new String(byteArr);
	}

	public static int Dp2Px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	public static int Px2Dp(Context context, float px) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}

	public static String getStrSamplePoints(ArrayList<SamplePoint> samplePoints) {
		StringBuilder builder = new StringBuilder();
		for (SamplePoint samplePoint : samplePoints) {
			builder.append(samplePoint.toString()).append(FIRST_DIVIDER);
		}
		String str = builder.toString();
		if (str.length() > 1) {
			return str.substring(0, str.length() - 2);
		}
		return null;
	}

	public static GeoPoint[] getSamplePointArray(String strSamplePoints) {
		GeoPoint[] samplePoints = null;
		if (strSamplePoints == null) {
			return null;
		}
		String[] splitItems = strSamplePoints.split(TRANS_FIRST_DIVIDER);
		if (splitItems != null && splitItems.length > 0) {
			samplePoints = new GeoPoint[splitItems.length];
			for (int i = 0; i < splitItems.length; i++) {
				String[] sencondSplitItems = splitItems[i].split(SECOND_DIVIDER);
				GeoPoint geoPoint = toGeoPointE6(sencondSplitItems[0], sencondSplitItems[1]);
				samplePoints[i] = geoPoint;
			}
			// Log.d(MyVehicleConditionActivity.TAG, "SamplePoints length:" +
			// samplePoints.length);
		}
		return samplePoints;
	}

	public static boolean dependToRecord(ArrayList<SamplePoint> samplePoints) {
		if (samplePoints == null || samplePoints.size() < 1) {
			return false;
		}
		int[] params = sGetSpanParams(smpList2GeoArr(samplePoints));
		double latDist = DistanceUtil.getDistance(new GeoPoint(params[MAX_LAT], params[MAX_LON]),
				new GeoPoint(params[MIN_LAT], params[MAX_LON]));
		double lonDist = DistanceUtil.getDistance(new GeoPoint(params[MAX_LAT], params[MAX_LON]),
				new GeoPoint(params[MAX_LAT], params[MIN_LON]));
		return latDist > MIN_REC_DIST || lonDist > MIN_REC_DIST;
	}

	public static GeoPoint[] smpList2GeoArr(ArrayList<SamplePoint> samplePoints) {
		if (samplePoints == null || samplePoints.size() == 0) {
			return null;
		}
		GeoPoint[] geoPonits = new GeoPoint[samplePoints.size()];
		int i = 0;
		for (SamplePoint samplePoint : samplePoints) {
			geoPonits[i++] = toGeoPointE6(samplePoint.getLatitude(), samplePoint.getLongitude());
		}
		return geoPonits;
	}

	public static int[] getSpanParams(GeoPoint[] geoSamplePoints) {
		if (geoSamplePoints == null || geoSamplePoints.length < 1) {
			return null;
		}
		return sGetSpanParams(geoSamplePoints);
	}

	private static int[] sGetSpanParams(GeoPoint[] geoSamplePoints) {
		int[] params = new int[4];
		int maxLat = 0, maxLon = 0, minLat = 0, minLon = 0;
		boolean isFirst = true;
		for (GeoPoint geoPoint : geoSamplePoints) {
			int lat = geoPoint.getLatitudeE6();
			int lon = geoPoint.getLongitudeE6();
			if (isFirst) {
				maxLat = minLat = lat;
				maxLon = minLon = lon;
				isFirst = false;
			} else {
				if (lat < minLat) {
					minLat = lat;
				} else if (lat > maxLat) {
					maxLat = lat;
				}
				if (lon < minLon) {
					minLon = lon;
				} else if (lon > maxLon) {
					maxLon = lon;
				}
			}
		}
		params[MAX_LAT] = maxLat;
		params[MIN_LAT] = minLat;
		params[MAX_LON] = maxLon;
		params[MIN_LON] = minLon;
		return params;
	}

	public static ArrayList<String>[] getCalendarDatas(ArrayList<DrivingJournalItem> djItems) {
		if (djItems == null || djItems.size() == 0) {
			return null;
		}
		ArrayList<String>[] arrCalendar = new ArrayList[3];
		HashMap<String, String> dayMap = new HashMap<String, String>();
		HashMap<String, String> weekMap = new HashMap<String, String>();
		HashMap<String, String> monthMap = new HashMap<String, String>();
		for (DrivingJournalItem djItem : djItems) {
			Date date = new Date(djItem.getStartTime());
			String strDay = new SimpleDateFormat("yyyy-MM-dd").format(date);
			if (!dayMap.containsKey(strDay)) {
				dayMap.put(strDay, strDay);
			}
			String strWeek = new SimpleDateFormat("yyyy-w").format(date);
			if (!weekMap.containsKey(strWeek)) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				calendar.set(Calendar.DAY_OF_WEEK, 1);
				String strDay2 = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
				weekMap.put(strDay2, strDay2);
			}
			String strMonth = new SimpleDateFormat("yyyy-MM").format(date);
			if (!monthMap.containsKey(strMonth)) {
				monthMap.put(strMonth, strMonth);
			}
		}

		if (dayMap.size() == 0 || weekMap.size() == 0 || monthMap.size() == 0) {
			return null;
		}

		arrCalendar[TYPE_DAY] = new ArrayList<String>(dayMap.keySet());
		arrCalendar[TYPE_WEEK] = new ArrayList<String>(weekMap.keySet());
		arrCalendar[TYPE_MONTH] = new ArrayList<String>(monthMap.keySet());
		for (ArrayList<String> calendarList : arrCalendar) {
			Collections.sort(calendarList);
		}
		return arrCalendar;
	}

	/**
	 * @param "yyyy-MM-dd"或"yyyy-MM"
	 * @return eg:"1月"
	 */
	public static String getMonth(String strDate) {
		return Integer.parseInt(strDate.substring(strDate.indexOf("-") + 1, strDate.indexOf("-") + 3)) + "月";
	}

	/**
	 * @param "yyyy-MM-dd"
	 * @return eg:"1","12"
	 */
	public static String getDay(String strDate) {
		return strDate.substring(strDate.lastIndexOf("-") + 1, strDate.lastIndexOf("-") + 3);
	}

	/**
	 * @param "yyyy-MM-dd"
	 * @return eg:"1390789779169"
	 */
	public static String getLongDay(String strDate) {
		Date date;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(strDate);
		} catch (ParseException e) {
			return null;
		}
		return date.getTime() + "";
	}

	/**
	 * @param "yyyy-MM-dd"
	 * @return eg:"1390789779169"
	 */
	public static String getLongNextDay(String strDate) {
		Date date;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(strDate);
		} catch (ParseException e) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		setCalendarNextDay(calendar);
		return calendar.getTimeInMillis() + "";
	}

	private static void setCalendarNextDay(Calendar calendar) {
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
	}

	/**
	 * @param "yyyy-MM-dd"
	 * @return eg:"1390789779169"
	 */
	public static String getLongLastDayOfWeek(String strDate) {
		Date date;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(strDate);
		} catch (ParseException e) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_WEEK, 7); // 此时直接调用calendar.getTimeInMillis()，获取的是一周中最后一天的第一秒
		setCalendarNextDay(calendar); // 查询范围的下限应该是第二周的第一天的第一秒
		return calendar.getTimeInMillis() + "";
	}

	/**
	 * @param "yyyy-MM"
	 * @return eg:"1390789779169"
	 */
	public static String getLongLastDayOfMonth(String strMonth) {
		Date date;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(strMonth);
		} catch (ParseException e) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		setCalendarNextDay(calendar); // 原理同上
		return calendar.getTimeInMillis() + "";
	}

	public static GeoPoint toGeoPointE6(double lat, double lon) {
		return new GeoPoint((int) (lat * 1e6), (int) (lon * 1e6));
	}

	public static GeoPoint toGeoPointE6(String lat, String lon) {
		return toGeoPointE6(Double.parseDouble(lat), Double.parseDouble(lon));
	}

	public static GeoPoint toGeoPoint(int lat, int lon) {
		return new GeoPoint(lat, lon);
	}

	public static GeoPoint toGeoPoint(String lat, String lon) {
		return toGeoPoint(Integer.parseInt(lat), Integer.parseInt(lon));
	}

	public static String getStrGeoPoint(GeoPoint geoPoint) {
		if (geoPoint == null) {
			return null;
		}
		return geoPoint.getLatitudeE6() + "," + geoPoint.getLongitudeE6();
	}

	public static String fmtMilliseconds(long millScd) {
		DecimalFormat dfmt = new DecimalFormat("0.0");
		return millScd * HOUR_RATE > 1 ? dfmt.format(millScd * HOUR_RATE) + "h" : dfmt.format(millScd
				* MINUTE_RATE)
				+ "min";
	}

	public static boolean isDefVehiOBDBinded() {
		List<VehicleInfo> vehicleList = TongGouApplication.sVehicleList;
		for (VehicleInfo vehicle : vehicleList) {
			if ("YES".equals(vehicle.getIsDefault())) {
				return TextUtils.isEmpty(vehicle.getObdSN()) ? false : true;
			}
		}
		return false;
	}

	public static boolean isActivityRunning(Context context, Class<?> destCls) {
		if (destCls == null) {
			return false;
		}
		ActivityManager aManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTasks = aManager.getRunningTasks(20);
		for (RunningTaskInfo runningTask : runningTasks) {
			if (destCls.getName().equals(runningTask.topActivity.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public static boolean isServiceRunning(Context context, Class<?> destCls) {
		if (destCls == null) {
			return false;
		}
		ActivityManager aManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningServices = aManager.getRunningServices(100);
		for (RunningServiceInfo runningService : runningServices) {
			if (destCls.getName().equals(runningService.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public static double getDouble(String str) {
		try {
			return (Double.parseDouble(str));
		} catch (Exception e) {
			return 0d;
		}
	}

	public static final boolean isGPSOn(final Context context) {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
}
