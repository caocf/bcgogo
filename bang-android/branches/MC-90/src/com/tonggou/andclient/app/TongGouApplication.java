package com.tonggou.andclient.app;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.tonggou.andclient.BaseActivity;
import com.tonggou.andclient.CarConditionQueryActivity;
import com.tonggou.andclient.LoginActivity;
import com.tonggou.andclient.MainActivity;
import com.tonggou.andclient.jsonresponse.LoginResponse;
import com.tonggou.andclient.jsonresponse.VehicleListResponse;
import com.tonggou.andclient.network.parser.AsyncJSONResponseParseHandler;
import com.tonggou.andclient.network.request.QueryVehicleListRequest;
import com.tonggou.andclient.util.PreferenceUtil;
import com.tonggou.andclient.util.SaveDB;
import com.tonggou.andclient.util.ScreenSizeUtil;
import com.tonggou.andclient.util.UpdateUtil;
import com.tonggou.andclient.vo.AppConfig;
import com.tonggou.andclient.vo.FaultCodeInfo;
import com.tonggou.andclient.vo.OBDBindInfo;
import com.tonggou.andclient.vo.OBDDevice;
import com.tonggou.andclient.vo.VehicleInfo;

public class TongGouApplication extends Application {

	private static final boolean DBG = true;

	public static List<OBDBindInfo> sObdLists;
	public static List<VehicleInfo> sVehicleList;
	public static boolean connetedOBD = false; // 和obd正在保持连接状态
	public static String connetedVehicleName = ""; // 连上的车辆品牌和型号
	public static String connetedVIN = ""; // 连上的车辆唯一标识
	public static String connetedObdSN = ""; // 连上的obd设备
												// 一般形式为mac地址00:27:19:9d:bd:2e
	public static String connetedVehicleID = ""; // 连上的车辆数据库中的id

	public static String mainActivityDefaultCarInfo = "";
	public static VehicleInfo registerDefaultVehicle = null;

	private static TongGouApplication mInstance = null;
	public boolean m_bKeyRight = true;
	public BMapManager mBMapManager = null;

	public static final String strKey = "QBL30Qeee7VVGKZNFs56LWlA"; // 原版
	// public static final String strKey = "Gg9wIFnCpRFkX0XnNeTz5Qpf";

	/*
	 * 注意：为了给用户提供更安全的服务，Android SDK自v2.1.3版本开始采用了全新的Key验证体系。
	 * 因此，当您选择使用v2.1.3及之后版本的SDK时，需要到新的Key申请页面进行全新Key的申请， 申请及配置流程请参考开发指南的对应章节
	 */
	public LocationClient mLocationClient = null;
	public static BDLocation bdlocation;
	public BDLocationListener myListener = new MyLocationListener();

	public static long callbackTime;
	public static String platformVersion; // 用户手机系统平台版本 3.0 4.0
	public static String mobileModel = Build.MODEL; // 用户手机型号
	public static String imageVersion; // 手机硬件分辨率 480 X 800

	private boolean baiduIsStart = false;
	private SQLiteDatabase mCommonFaultDicDB;
	public static boolean isCheckingCommonFaultDic;

	private boolean isLogin;

	public synchronized void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
		TongGouService.allowPollingMessage = isLogin;
	}

	public synchronized boolean isLogin() {
		return isLogin;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		initEngineManager(this);
		sObdLists = new ArrayList<OBDBindInfo>();
		// /////////////////////////////百度定位设置///////////////////////////////////////////////////////////
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		// mLocationClient.setAK(strKey);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");
		option.setCoorType("bd09ll");
		option.setScanSpan(15000); // 设置发起定位请求的间隔
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		mLocationClient.setLocOption(option);
		// /////////////////////////////////////////////////////////////////////////////////////////////////////////

		MyCrashHandler crashHandler = MyCrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		checkCommonFaultDicDB();
	}

	private void checkCommonFaultDicDB() {
		SharedPreferences sp = getSharedPreferences(BaseActivity.SETTING_INFOS,
				Context.MODE_PRIVATE);
		if (sp.getString(BaseActivity.COMMON_FAULT_DIC_VERSON, null) == null) {
			mCommonFaultDicTask.start();
		}
	}

	private Thread mCommonFaultDicTask = new Thread(new Runnable() {
		@Override
		public void run() {
			isCheckingCommonFaultDic = true;
			if (copyCommonFaultDicDB()) {
				if (initCommonFaultDicDB()) {
					saveCommonFaultDic();
				}
			}
			isCheckingCommonFaultDic = false;
		}
	});

	private boolean copyCommonFaultDicDB() {
		boolean result = false;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			inputStream = getAssets().open(BaseActivity.COMMON_FAULT_DIC_FILE_NAME);
			byte[] buf = new byte[1024];
			outputStream = new FileOutputStream(BaseActivity.PACKAGE_DB_PATH
					+ BaseActivity.COMMON_FAULT_DIC_FILE_NAME);
			int len = -1;
			while ((len = inputStream.read(buf)) != -1) {
				outputStream.write(buf, 0, len);
			}
			result = true;
		} catch (IOException e) {
			Log.e("TongGouApplication", "", e);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e) {
				Log.e("TongGouApplication", "", e);
			}
		}
		return result;
	}

	private boolean initCommonFaultDicDB() {
		boolean result = false;
		try {
			mCommonFaultDicDB = SQLiteDatabase.openDatabase(BaseActivity.PACKAGE_DB_PATH
					+ BaseActivity.COMMON_FAULT_DIC_FILE_NAME, null, SQLiteDatabase.OPEN_READONLY);
			result = true;
		} catch (SQLiteException e) {
			Log.e("TongGouApplication", "", e);
		}
		return result;
	}

	private void saveCommonFaultDic() {
		List<FaultCodeInfo> faultCodeList = getFaultCodeList();
		String version = getCommonFaultDicVersion();
		if (faultCodeList != null && faultCodeList.size() > 0) {
			SaveDB.getSaveDB(this).updateFaultCode("common", version, faultCodeList, true);
		}
		if (mCommonFaultDicDB.isOpen()) {
			mCommonFaultDicDB.close();
			mCommonFaultDicDB = null;
		}
	}

	private String getCommonFaultDicVersion() {
		String version = null;
		if (mCommonFaultDicDB.isOpen()) {
			Cursor query = mCommonFaultDicDB.query(SaveDB.DIC_VER_TABLE_NAME,
					SaveDB.DIC_VER_TABLE_CREATE2, null, null, null, null, null);
			if (query.moveToNext()) {
				version = query.getString(query.getColumnIndex(SaveDB.FAULT_DIC_VERSION));
			}
			query.close();
		}
		return version;
	}

	private List<FaultCodeInfo> getFaultCodeList() {
		List<FaultCodeInfo> faultCodeList = null;
		if (mCommonFaultDicDB.isOpen()) {
			Cursor query = mCommonFaultDicDB.query(SaveDB.DICTIONARY_TABLE_NAME,
					SaveDB.DICTIONARY_TABLE_CREATE2, null, null, null, null, null);
			faultCodeList = new ArrayList<FaultCodeInfo>();
			while (query.moveToNext()) {
				FaultCodeInfo faultCodeInfo = new FaultCodeInfo();
				faultCodeInfo
						.setFaultCode(query.getString(query.getColumnIndex(SaveDB.FAULT_CODE)));
				faultCodeInfo.setDescription(query.getString(query
						.getColumnIndex(SaveDB.FAULT_DESCRIPTION)));
				faultCodeList.add(faultCodeInfo);
			}
			query.close();
		}
		return faultCodeList;
	}

	public void initEngineManager(Context context) {
		if (mBMapManager == null) {
			mBMapManager = new BMapManager(context);
		}

		if (!mBMapManager.init(strKey, new MyGeneralListener())) {
			// Toast.makeText(BaseMapApp.getInstance().getApplicationContext(),
			// "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
		}
	}

	public static TongGouApplication getInstance() {
		return mInstance;
	}

	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	public static class MyGeneralListener implements MKGeneralListener {
		@Override
		public void onGetNetworkState(int iError) {
		}

		@Override
		public void onGetPermissionState(int iError) {
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				// Log.d("cccccc", "cccccccccccccccccccccccccccccccccc");
				TongGouApplication.getInstance().m_bKeyRight = false;
			}
		}
	}

	public void starBaiduLBS() {
		if (mLocationClient != null && !baiduIsStart) {
			// Log.d("DDDS", "SSSSSTART start():");
			mLocationClient.start();
			baiduIsStart = true;
		}
	}

	/**
	 * 停掉百度定位
	 */
	public void stopBaiduLBS() {
		if (mLocationClient != null && baiduIsStart) {
			Log.d("DDDS", "SSSSSTART stop():");
			mLocationClient.stop();
			baiduIsStart = false;
		}
	}

	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			callbackTime = System.currentTimeMillis();
			if (location == null) {
				// Log.d("Location","baidu location:null");
				getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit()
						.putString(BaseActivity.LOCATION_LAST_STATUES, "LAST").commit();
				return;
			}
			// Log.d("Location","baidu location type:" + location.getLocType());
			//
			// StringBuffer sb = new StringBuffer(256);
			// sb.append("Poi time : ");
			// sb.append(location.getTime());
			// sb.append("\nlatitude : ");
			// sb.append(location.getLatitude());
			// sb.append("\nlontitude : ");
			// sb.append(location.getLongitude());
			// Log.d("Location","baidu:"+sb.toString()+":"+location.getCity()+":"+location.getCityCode());

			if (location.getLocType() != 61 && location.getLocType() != 65
					&& location.getLocType() != 66 && location.getLocType() != 161) {
				getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit()
						.putString(BaseActivity.LOCATION_LAST_STATUES, "LAST").commit();
				return;
			}

			String baidubackStr = location.getLongitude() + "," + location.getLatitude();
			if (baidubackStr != null && baidubackStr.indexOf("E") == -1) {
				getSharedPreferences(BaseActivity.SETTING_INFOS, 0)
						.edit()
						.putString(BaseActivity.LOCATION_LAST_POSITION_LAT,
								location.getLatitude() + "")
						.putString(BaseActivity.LOCATION_LAST_POSITION_LON,
								location.getLongitude() + "")
						.putString(BaseActivity.LOCATION_LAST_POSITION, baidubackStr)
						.putString(BaseActivity.LOCATION_LAST_CITYNAME, location.getCity())
						.putString(BaseActivity.LOCATION_LAST_PROVINCENAME, location.getProvince())
						.putString(BaseActivity.LOCATION_LAST_STATUES, "CURRENT").commit();
//				TongGouApplication.showLog(location.getLatitude() + "  " + location.getLongitude());
				if (location.getCityCode() == null || "".equals(location.getCityCode())
						|| "null".equals(location.getCityCode())) {
					getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit()
							.putString(BaseActivity.LOCATION_LAST_CITYCODE, "").commit();
				} else {
					getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit()
							.putString(BaseActivity.LOCATION_LAST_CITYCODE, location.getCityCode())
							.commit();
				}

			} else {
				getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit()
						.putString(BaseActivity.LOCATION_LAST_STATUES, "LAST").commit();
			}
			bdlocation = location;
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

//	private Handler mLoginHandler = new Handler() {
//
//		public void handleMessage(Message msg) {
//			queryVehicleList();
//		};
//	};

	public void saveSomeInformation(Context context, LoginResponse loginResponse,
			SharedPreferences sharedPreferences, String userID, String userPassword) {
		if ( !TextUtils.isEmpty(userID) ) {
			PreferenceUtil.putString(context, BaseActivity.SETTING_INFOS, BaseActivity.NAME, userID);
		}
		if ( !TextUtils.isEmpty(userPassword) ) {
			PreferenceUtil.putString(context, BaseActivity.SETTING_INFOS, BaseActivity.PASSWORD, userPassword);
		}
		// if(imageVersion!=null&&!"".equals(imageVersion)){
		// sharedPreferences.edit()
		// .putString(BaseActivity.SCREEN, imageVersion).commit();
		// }

//		sharedPreferences.edit().putBoolean(BaseActivity.LOGINED, true).commit();

		// obdLists = loginParser.getLoginResponse().getObdList();
//		mLoginHandler.sendEmptyMessage(0);
		setLogin(true);
		AppConfig appConfig = loginResponse.getAppConfig();
		if (appConfig != null) {
			String obdReadInterval = appConfig.getObdReadInterval();
			if (obdReadInterval != null && !"".equals(obdReadInterval)) {
				sharedPreferences.edit()
						.putString(BaseActivity.APPCONFIG_OBD_READ_INTERVAL, obdReadInterval)
						.commit();
			}
			String serverReadInterval = appConfig.getServerReadInterval();
			if (serverReadInterval != null && !"".equals(serverReadInterval)) {
				sharedPreferences.edit()
						.putString(BaseActivity.APPCONFIG_SERVER_READ_INTERVAL, serverReadInterval)
						.commit();
			}
			String mileageInformInterval = appConfig.getMileageInformInterval();
			if (mileageInformInterval != null && !"".equals(mileageInformInterval)) {
				sharedPreferences
						.edit()
						.putString(BaseActivity.APPCONFIG_MILEAGE_INFORM_INTERVAL,
								mileageInformInterval).commit();
			}

			String appVehicleErrorCodeWarnIntervals = appConfig
					.getAppVehicleErrorCodeWarnIntervals();
			if (appVehicleErrorCodeWarnIntervals != null
					&& !"".equals(appVehicleErrorCodeWarnIntervals)) {
				sharedPreferences
						.edit()
						.putString(BaseActivity.APPCONFIG_ERROR_ALERT_INTERVAL,
								appVehicleErrorCodeWarnIntervals).commit();
			}

			String remainOilMassWarn = appConfig.getRemainOilMassWarn();
			if (remainOilMassWarn != null && !"".equals(remainOilMassWarn)) {
				// sharedPreferences.edit().putString(BaseActivity.APPCONFIG_OIL_ALERT_INTERVAL,
				// remainOilMassWarn).commit();
				if (remainOilMassWarn.indexOf("_") != -1) {
					try {
						TongGouService.interOne = Integer.parseInt(remainOilMassWarn.substring(0,
								remainOilMassWarn.indexOf("_")));
						TongGouService.interTwo = Integer.parseInt(remainOilMassWarn
								.substring(remainOilMassWarn.indexOf("_") + 1));
					} catch (NumberFormatException er) {
					}
				}
			}
		}
		
		List<OBDBindInfo> obdList = loginResponse.getObdList();
		List<VehicleInfo> vehicleList = new ArrayList<VehicleInfo>();
		for( OBDBindInfo obd : obdList ) {
			VehicleInfo vehicleInfo = obd.getVehicleInfo();
			if( vehicleInfo != null ) {
				vehicleInfo.setObdSN( obd.getObdSN() );
				vehicleList.add(vehicleInfo);
			}
		}
		if( vehicleList.isEmpty() ) {
			queryVehicleList(context);
			
		} else {
			setDefaultVehicleBindOBDs( vehicleList );
		}
	}

	/**
	 * 连接 OBD 以及轮询读取信息
	 */
	public void doScanOBDAndPulling() {

		// 连接obd
		Intent intent = new Intent();// 创建Intent对象
		intent.setAction(TongGouService.TONGGOU_ACTION_START);
		intent.putExtra("com.tonggou.server", TongGouService.SCAN_OBD);
		sendBroadcast(intent);// 发送广播

		TongGouService.allowPollingMessage = true;
		// 启动轮询
		Intent intent2 = new Intent();// 创建Intent对象
		intent2.setAction(TongGouService.TONGGOU_ACTION_START);
		intent2.putExtra("com.tonggou.server", TongGouService.POLLING);
		sendBroadcast(intent2);// 发送广播
	}
	
	public void doStopOBDAndPulling() {
		// 断开obd
		Intent intent = new Intent();// 创建Intent对象
		intent.setAction(TongGouService.TONGGOU_ACTION_START);
		intent.putExtra("com.tonggou.server", TongGouService.STOP_SCAN);
		sendBroadcast(intent);// 发送广播

		TongGouService.allowPollingMessage = false;
		// 停止轮询
		Intent intent2 = new Intent();// 创建Intent对象
		intent2.setAction(TongGouService.TONGGOU_ACTION_START);
		intent2.putExtra("com.tonggou.server", TongGouService.STOP_POLLING);
		sendBroadcast(intent2);// 发送广播
	}

	public synchronized void setDefaultVehicleBindOBDs(List<VehicleInfo> data) {
		if( sVehicleList == null ) {
			sVehicleList = new ArrayList<VehicleInfo>();
		}
		sVehicleList.clear();
		sVehicleList.addAll(data);
		List<OBDBindInfo> newObdData = new ArrayList<OBDBindInfo>();
		MainActivity.defaultBrandAndModle = "";
		for (VehicleInfo vehicle : data) {
			if ("YES".equals(vehicle.getIsDefault())) {
				String vehiBrand = vehicle.getVehicleBrand();
				String vehiModel = vehicle.getVehicleModel();
				String vehiNo = vehicle.getVehicleNo();
				String vehiModelId = vehicle.getVehicleModelId();

				if (TextUtils.isEmpty(vehiBrand)) {
					vehiBrand = "";
				} else {
					PreferenceUtil.putString(this, BaseActivity.SETTING_INFOS, BaseActivity.BRAND,
							vehiBrand);
				}
				if (TextUtils.isEmpty(vehiModel)) {
					vehiModel = "";
				} else {
					PreferenceUtil.putString(this, BaseActivity.SETTING_INFOS, BaseActivity.MODEL,
							vehiModel);
				}
				if (!TextUtils.isEmpty(vehiNo)) {
					PreferenceUtil.putString(this, BaseActivity.SETTING_INFOS,
							BaseActivity.VEHICLENUM, vehiNo);
				}
				if (!TextUtils.isEmpty(vehiModelId)) {
					PreferenceUtil.putString(this, BaseActivity.SETTING_INFOS,
							BaseActivity.VEHICLE_MODE_ID, vehiModelId);
				}
				MainActivity.defaultBrandAndModle = vehiBrand + " " + vehiModel;
			}
			String obdAddress = vehicle.getObdSN();
			if (TextUtils.isEmpty(obdAddress)) {
				continue;
			}
			OBDBindInfo obd = new OBDBindInfo();
			obd.setVehicleInfo(vehicle);
			obd.setObdSN(obdAddress);
			obd.setIsDefault(vehicle.getIsDefault());
			TongGouApplication.showLog(obdAddress + "  " + vehicle.getVehicleNo());
			newObdData.add(obd);
		}
		if(TongGouApplication.connetedOBD 
				&& sObdLists.containsAll(newObdData) && newObdData.containsAll(sObdLists)) {
			TongGouApplication.showLog( "obdLists.containsAll(newObdData) && newObdData.containsAll(obdLists)" );
		} else {
			sObdLists.clear();
			sObdLists.addAll(newObdData);
			doScanOBDAndPulling();
		}
		
	}

	public String getVersionName() {
		try {
			return UpdateUtil.getLocalPackageInfo(this).versionName;
		} catch (NameNotFoundException e) {
			return "1.0";
		}
	}

	public String getImageVersion() {
		ScreenSizeUtil util = new ScreenSizeUtil(this);
		return util.getScreenWidth() + "x" + util.getScreenHeight();
	}

	/**
	 * 请求车辆列表
	 * 
	 * @param userName
	 */
	public synchronized void queryVehicleList(Context context) {
		if (!isLogin) {
			return;
		}

		String userName = PreferenceUtil.getString(this, BaseActivity.SETTING_INFOS,
				BaseActivity.NAME);
		QueryVehicleListRequest request = new QueryVehicleListRequest();
		request.setApiParams(userName);
		request.doRequest(context, new AsyncJSONResponseParseHandler<VehicleListResponse>() {

			@Override
			public void onParseSuccess(VehicleListResponse result, byte[] originResult) {
				super.onParseSuccess(result, originResult);
				List<VehicleInfo> vehicleList = new ArrayList<VehicleInfo>();
				vehicleList = result.getVehicleList();
				if (vehicleList == null || vehicleList.isEmpty()) {
					// 原来的连接该停掉
					stopScanOBD();
				} else {
					setDefaultVehicleBindOBDs(vehicleList);
				}
			}

			@Override
			public Class<VehicleListResponse> getTypeClass() {
				return VehicleListResponse.class;
			}

		});

	}
	
	private void stopScanOBD() {
		Intent intentS = new Intent();
		intentS.setAction(TongGouService.TONGGOU_ACTION_START);
		intentS.putExtra("com.tonggou.server", TongGouService.STOP_SCAN);
		sendBroadcast(intentS);
		MainActivity.defaultBrandAndModle = "";
		sObdLists.clear();
	}
	
	public void stopPollingMsg() {
		Intent intentS = new Intent();
		intentS.setAction(TongGouService.TONGGOU_ACTION_START);
		intentS.putExtra("com.tonggou.server", TongGouService.STOP_POLLING);
		sendBroadcast(intentS);
	}
	
	public void startPollingMsg() {
		Intent intentS = new Intent();
		intentS.setAction(TongGouService.TONGGOU_ACTION_START);
		intentS.putExtra("com.tonggou.server", TongGouService.POLLING);
		sendBroadcast(intentS);
	}

	/**
	 * 显示 Toast
	 * 
	 * @param msg
	 */
	public static void showToast(Object msg) {
		Toast.makeText(mInstance, msg + "", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 显示 Toast
	 * 
	 * @param msg
	 */
	public static void showLongToast(Object msg) {
		Toast.makeText(mInstance, msg + "", Toast.LENGTH_LONG).show();
	}

	/**
	 * 打印 log
	 * 
	 * @param msg
	 */
	public static void showLog(Object msg) {
		if (DBG)
			Log.i("Tonggou Log", msg + "");
	}

	public void sendMainActivityChangeTitleBroadcast(String title) {
		Intent intent = new Intent(MainActivity.ACTION_CHANGE_TITLE);
		intent.putExtra(MainActivity.KEY_ARG_TITLE, title);
		sendBroadcast(intent);
	}
	
	public void doExpireLogin() {
			//////////////////////////////////////
			BaseActivity.exit();
			deInit();
			///////////////////////////////////////
			setLogin(false);
			Intent toLogin = new Intent(this, LoginActivity.class);
			toLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			toLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			toLogin.putExtra(LoginActivity.ARG_KEY_LOGIN_EXPIRE, "登录过期，请重新登录。");
			startActivity(toLogin);
	}
	
	public void deInit(){
		stopScanOBD();
        stopPollingMsg();
		TongGouApplication.showLog("deInit()   @@@@@@@@@@@@@@@@@@@");
		TongGouService.allowPollingMessage = false;
		if(TongGouApplication.sObdLists!=null){
			TongGouApplication.sObdLists.clear();
		}
		MainActivity.haveFaultCode = false;
		setLogin(false);
		getSharedPreferences(BaseActivity.SETTING_INFOS, 0)
		.edit()
//		.putBoolean(BaseActivity.LOGINED, false)
		.putString(BaseActivity.PHONENAME, null)
		.putString(BaseActivity.SERVICE, null)
		.putString(BaseActivity.PHONE, null)
		.putString(BaseActivity.BRAND, null)
		.putString(BaseActivity.MODEL, null)
		.putString(BaseActivity.VEHICLENUM, null)
		.putString(BaseActivity.CHECKVOICE, null)
		.putString(BaseActivity.VEHICLE_MODE_ID, "")
		.putString(BaseActivity.APPCONFIG_OIL_LAST_STATUS, "2")
		.putInt(BaseActivity.NEW_MESSAGE_COUNT,0)
		.commit();
		
		CarConditionQueryActivity.ssyhStr = "- -";
        CarConditionQueryActivity.pjyhStr = "- - l/h";
        CarConditionQueryActivity.syylStr = "- -";
        CarConditionQueryActivity.sxwdStr = "- -";
        TongGouApplication.connetedVehicleName = "";  
        TongGouApplication.connetedVIN = "";          
        TongGouApplication.connetedObdSN = "";   
        TongGouApplication.connetedVehicleID = "";   
        MainActivity.defaultBrandAndModle = "";
        BaseConnectOBDService.cmile = null;
        sVehicleList = null;
        
	}
	
	////////////////           绑定 OBD 监听
	public static interface OnBindOBDListener {
		public void onBindOBDSuccess(OBDDevice device, String vin);

		public void onBindOBDCancle();
	}

	private List<OnBindOBDListener> mBindOBDListeners = new ArrayList<OnBindOBDListener>();

	public void registerBindOBDListener(OnBindOBDListener listener) {
		mBindOBDListeners.add(listener);
	}

	public void unregisterBindOBDListener(OnBindOBDListener listener) {
		mBindOBDListeners.remove(listener);
	}

	public void notifyBindOBDSuccess(OBDDevice device, String vin) {
		for (OnBindOBDListener listener : mBindOBDListeners) {
			listener.onBindOBDSuccess(device, vin);
		}
	}

	public void notifyBindOBDCancle() {
		for (OnBindOBDListener listener : mBindOBDListeners) {
			listener.onBindOBDCancle();
		}
	}

	// / 绑定 店面 监听
	public static interface OnBindShopListener {
		public void onBindShopSuccess(String shopName, String shopId);

		public void onBindShopCancle();
	}

	private List<OnBindShopListener> mBindShopListeners = new ArrayList<OnBindShopListener>();

	public void registerBindShopListener(OnBindShopListener listener) {
		mBindShopListeners.add(listener);
	}

	public void unregisterBindShopListener(OnBindShopListener listener) {
		mBindShopListeners.remove(listener);
	}

	public void notifyBindShopSuccess(String shopName, String shopId) {
		for (OnBindShopListener listener : mBindShopListeners) {
			listener.onBindShopSuccess(shopName, shopId);
		}
	}

	public void notifyBindShopCancle() {
		for (OnBindShopListener listener : mBindShopListeners) {
			listener.onBindShopCancle();
		}
	}

	// / 选择 车牌车型
	public static interface OnSelectVehicleBrandTypeListener {
		public void onBrandSelected(boolean isCancle, String brandName, String brandId);

		public void onTypeSelected(boolean isCancle, String typeName, String typeId);
	}

	private List<OnSelectVehicleBrandTypeListener> mOnSelectVehicleBrandTypeListeners = new ArrayList<OnSelectVehicleBrandTypeListener>();

	public void registerSelectVehicleBrandTypeListener(OnSelectVehicleBrandTypeListener listener) {
		mOnSelectVehicleBrandTypeListeners.add(listener);
	}

	public void unregisterSelectVehicleBrandTypeListener(OnSelectVehicleBrandTypeListener listener) {
		mOnSelectVehicleBrandTypeListeners.remove(listener);
	}

	public void notifyBrandSelected(boolean isCancle, String brandName, String brandId) {
		for (OnSelectVehicleBrandTypeListener l : mOnSelectVehicleBrandTypeListeners) {
			l.onBrandSelected(isCancle, brandName, brandId);
		}
	}

	public void notifyTypeSelected(boolean isCancle, String typeName, String typeId) {
		for (OnSelectVehicleBrandTypeListener l : mOnSelectVehicleBrandTypeListeners) {
			l.onTypeSelected(isCancle, typeName, typeId);
		}
	}

}
