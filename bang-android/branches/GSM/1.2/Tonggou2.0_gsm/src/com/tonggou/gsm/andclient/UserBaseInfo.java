package com.tonggou.gsm.andclient;

import com.google.gson.Gson;
import com.tonggou.gsm.andclient.bean.AppShopDTO;
import com.tonggou.gsm.andclient.bean.AppUserDTO;
import com.tonggou.gsm.andclient.bean.AppVehicleDTO;
import com.tonggou.gsm.andclient.net.response.LoginResponse;
import com.tonggou.gsm.andclient.util.BMapUtil;
import com.tonggou.gsm.andclient.util.PreferenceUtil;

/**
 * 用户基本数据信息
 * @author lwz
 *
 */
public class UserBaseInfo {

	private static AppUserDTO sUserInfo;
	private static AppVehicleDTO sVehicleInfo;
	private static AppShopDTO sShopInfo;

	public static void setInfos(LoginResponse response) {
		setUserInfo(response.getAppUserDTO());
		setShopInfo(response.getAppShopDTO());
		setVehicleInfo(response.getAppVehicleDTO());
	}
	
	public static AppUserDTO getUserInfo() {
		if( sUserInfo == null && isContains(Constants.PREF.PREF_KEY_USER_INFO_JSON_STR)) {
			sUserInfo = new Gson().fromJson(
					restoreJsonData(Constants.PREF.PREF_KEY_USER_INFO_JSON_STR), AppUserDTO.class);
		}
		return sUserInfo == null ? new AppUserDTO() : sUserInfo;
	}
	
	public static void setUserInfo(AppUserDTO userInfo) {
		sUserInfo = userInfo;
		storeJsonData(Constants.PREF.PREF_KEY_USER_INFO_JSON_STR, new Gson().toJson(userInfo));
	}
	
	public static AppVehicleDTO getVehicleInfo() {
		if( sVehicleInfo == null && isContains(Constants.PREF.PREF_KEY_VEHICLE_INFO_JSON_STR)) {
			sVehicleInfo = new Gson().fromJson(
					restoreJsonData(Constants.PREF.PREF_KEY_VEHICLE_INFO_JSON_STR), AppVehicleDTO.class);
		}
		return sVehicleInfo;
	}

	public static void setVehicleInfo(AppVehicleDTO vehicleInfo) {
		sVehicleInfo = vehicleInfo;
		if( Double.valueOf( vehicleInfo.getCoordinateLat() * vehicleInfo.getCoordinateLon()).intValue() != 0 ) {
			App.getInstance().initBMapManager();
			// 更新车辆位置信息
			Constants.GEO_DEFAULT = 
					BMapUtil.convertWgs84ToBaidu( vehicleInfo.getCoordinateLat(), vehicleInfo.getCoordinateLon());
			App.getInstance().releaseBMapManager();
		}
		storeJsonData(Constants.PREF.PREF_KEY_VEHICLE_INFO_JSON_STR, new Gson().toJson(vehicleInfo));
	}

	public static AppShopDTO getShopInfo() {
		if( sShopInfo == null && isContains(Constants.PREF.PREF_KEY_SHOP_INFO_JSON_STR)) {
			sShopInfo = new Gson().fromJson(
					restoreJsonData(Constants.PREF.PREF_KEY_SHOP_INFO_JSON_STR), AppShopDTO.class);
		}
		return sShopInfo;
	}

	public static void setShopInfo(AppShopDTO shopInfo) {
		sShopInfo = shopInfo;
		storeJsonData(Constants.PREF.PREF_KEY_SHOP_INFO_JSON_STR, new Gson().toJson(shopInfo));
	}
	
	private static String restoreJsonData(String prefKey) {
		return PreferenceUtil.getString(App.getInstance(), 
				Constants.PREF.PREF_NAME_USER_INFO, prefKey);
	}
	
	private static void storeJsonData(String prefKey, String jsonData) {
		PreferenceUtil.putString(App.getInstance(), 
				Constants.PREF.PREF_NAME_USER_INFO, prefKey, jsonData);
		App.debug("UserBaseInfo-jsonData", jsonData);
	}
	
	private static boolean isContains(String prefKey) {
		return PreferenceUtil.getPreference(
				App.getInstance(), Constants.PREF.PREF_NAME_USER_INFO).contains(prefKey);
	}

}
