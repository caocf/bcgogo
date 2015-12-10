package com.tonggou.andclient;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.tonggou.andclient.app.BaseConnectOBDService;
import com.tonggou.andclient.app.MyCrashHandler;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.app.TongGouService;
import com.tonggou.andclient.util.BitmapCache;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.vo.Shop;
import com.tonggou.andclient.vo.ShopIntent;

public abstract class BaseActivity extends AbsUMengActivity{
	public static LinkedList<Activity> sAllActivities = new LinkedList<Activity>();
	public static final String SETTING_INFOS = "SETTING_Infos";

	public static final String LOGINED = "login";                              // 登录进去过

	public static final String SCREEN = "screen_size";                         // 分辨率
	public static final String PASSWORD = "PASSWORD";                          // 存储的密码
	public static final String NAME = "NAME";                                  // 存储的  用户账号
	public static final String SERVICE="SERVICE";                              // 存储的  用户选择的服务
	public static final String PHONENAME = "PHONENAME";                        // 存储的  用户联系人
	public static final String PHONE="PHONE";                                  // 存储的  用户联系方式
	public static final String BRAND = "BRAND";                                // 存储的  用户车型
	public static final String MODEL="MODEL";                                  // 存储的  用户品牌
	public static final String VEHICLENUM="VEHICLENUM";                        // 存储的  用户车牌
	public static final String CHECKVOICE="YES";                               // 用户消息提示音
	public static final String COOKIES_STR = "cookies_str";                    // cookies
	//public static final String FAULT_DIC_VERSON = "fault_dic_version";         // 具体车型故障码版本号
	//public static final String COMMON_FAULT_DIC_VERSON = "common_fault_dic_version";         // 通用故障码版本号
	public static final String VEHICLE_MODE_ID = "vehicle_mode_id";            // 车型ID

	public static final String APPCONFIG_OBD_READ_INTERVAL = "obd_Read_Interval";                    // 从obd读取数据的周期间隔，单位为毫秒 
	public static final String APPCONFIG_SERVER_READ_INTERVAL = "server_Read_Interval";              // 从服务端读取数据的周期间隔，单位为毫秒
	public static final String APPCONFIG_MILEAGE_INFORM_INTERVAL = "mileage_Inform_Interval";        // 向服务端发送车辆里程数的公里数间隔，单位为公里
	public static final String APPCONFIG_ERROR_ALERT_INTERVAL = "error_alert_Interval";              // 重复故障码提示间隔  单位为小时
	public static final String APPCONFIG_OIL_ALERT_INTERVAL = "oil_alert_Interval";                  // 油量警告区间   15_25
	public static final String APPCONFIG_OIL_LAST_STATUS = "oil_alert_status";                       // 油量上一次警告区间  0~15--0 /  15~25--1/  25以上--2

	public static final String LOCATION_LAST_POSITION_LAT = "location_last_position_lac";                       // 上一次纬度
	public static final String LOCATION_LAST_POSITION_LON = "location_last_position_lon";                       // 上一次经度
	public static final String LOCATION_LAST_POSITION = "location_last_position";                       // 上一次经纬度
	public static final String LOCATION_LAST_CITYCODE = "location_last_citycode";                       // 上一次citycode
	public static final String LOCATION_LAST_CITYNAME = "location_last_cityname";                       // 上一次城市名
	public static final String LOCATION_LAST_PROVINCENAME = "location_last_provincename";               // 上一次省份	
	public static final String LOCATION_LAST_STATUES = "location_last_statues";                         // 上一次定位状态


	public static final String NEW_MESSAGE_COUNT = "new_message_count";        //新消息个数


	/** 用于显示提示消息 */
	public static final int SEND_MESSAGE = -1;












	protected SharedPreferences sharedPreferences ;
	protected String currentUserId;   //当前用户名
	protected String currentPassWd;   //当前密码
	private BitmapCache myBitmapCache;

	//public static MediaPlayer mediaPlayer2;
	//public static SoundPool soundPool;
	//public static  HashMap<Integer, Integer> soundPoolMap = new HashMap<Integer, Integer>();     

	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		sAllActivities.add(this);
	
		//mediaPlayer2 = new MediaPlayer();
		//mediaPlayer2 = MediaPlayer.create(this,R.raw.tink);
		//mediaPlayer2.setAudioStreamType(AudioManager.STREAM_MUSIC); 

//		soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);  
//		soundPoolMap = new HashMap<Integer, Integer>();  
//		soundPoolMap.put(1,soundPool.load(this, R.raw.tink, 1));

		myBitmapCache = BitmapCache.getInstance();
		sharedPreferences = getSharedPreferences(BaseActivity.SETTING_INFOS, 0);
		currentUserId = sharedPreferences.getString(BaseActivity.NAME, "NULL");
		currentPassWd = sharedPreferences.getString(BaseActivity.PASSWORD, "NULL");

		
	
		

	}	

	protected void sendMessage(Handler handler,int what, String content) {
		if (what < 0) {
			what = BaseActivity.SEND_MESSAGE;
		}
		Message msg = Message.obtain(handler, what, content);
		if(msg!=null){
			msg.sendToTarget();
		}
	}
	public void setLikes(String like,ImageView  like1,ImageView  like2,ImageView  like3,ImageView  like4,ImageView  like5) {
		if(Double.parseDouble(like)<=0.0){	
			like1.setImageDrawable(getResources().getDrawable(R.drawable.whitestar));
			like2.setImageDrawable(getResources().getDrawable(R.drawable.whitestar));
			like3.setImageDrawable(getResources().getDrawable(R.drawable.whitestar));
			like4.setImageDrawable(getResources().getDrawable(R.drawable.whitestar));
			like5.setImageDrawable(getResources().getDrawable(R.drawable.whitestar));
		}else{
			if(Double.parseDouble(like)<=0.5){
				like1.setImageDrawable(getResources().getDrawable(R.drawable.halfstar));
				like2.setImageDrawable(getResources().getDrawable(R.drawable.whitestar));
				like3.setImageDrawable(getResources().getDrawable(R.drawable.whitestar));
				like4.setImageDrawable(getResources().getDrawable(R.drawable.whitestar));
				like5.setImageDrawable(getResources().getDrawable(R.drawable.whitestar));									
			}else{						
				if(Double.parseDouble(like)<=1.0){
					like1.setImageDrawable(getResources().getDrawable(R.drawable.yellowstar));
					like2.setImageDrawable(getResources().getDrawable(R.drawable.whitestar));
					like3.setImageDrawable(getResources().getDrawable(R.drawable.whitestar));
					like4.setImageDrawable(getResources().getDrawable(R.drawable.whitestar));
					like5.setImageDrawable(getResources().getDrawable(R.drawable.whitestar));						
				}else{
					if(Double.parseDouble(like)<=1.5){
						like1.setImageDrawable(getResources().getDrawable(R.drawable.yellowstar));
						like2.setImageDrawable(getResources().getDrawable(R.drawable.halfstar));
						like3.setImageDrawable(getResources().getDrawable(R.drawable.whitestar));
						like4.setImageDrawable(getResources().getDrawable(R.drawable.whitestar));
						like5.setImageDrawable(getResources().getDrawable(R.drawable.whitestar));							
					}else{
						if(Double.parseDouble(like)<=2.0){
							like1.setImageDrawable(getResources().getDrawable(R.drawable.yellowstar));
							like2.setImageDrawable(getResources().getDrawable(R.drawable.yellowstar));
							like3.setImageDrawable(getResources().getDrawable(R.drawable.whitestar));
							like4.setImageDrawable(getResources().getDrawable(R.drawable.whitestar));
							like5.setImageDrawable(getResources().getDrawable(R.drawable.whitestar));
						}else{
							if(Double.parseDouble(like)<=2.5){
								like1.setImageDrawable(getResources().getDrawable(R.drawable.yellowstar));
								like2.setImageDrawable(getResources().getDrawable(R.drawable.yellowstar));
								like3.setImageDrawable(getResources().getDrawable(R.drawable.halfstar));
								like4.setImageDrawable(getResources().getDrawable(R.drawable.whitestar));
								like5.setImageDrawable(getResources().getDrawable(R.drawable.whitestar));
							}else{
								if(Double.parseDouble(like)<=3.0){
									like1.setImageDrawable(getResources().getDrawable(R.drawable.yellowstar));
									like2.setImageDrawable(getResources().getDrawable(R.drawable.yellowstar));
									like3.setImageDrawable(getResources().getDrawable(R.drawable.yellowstar));
									like4.setImageDrawable(getResources().getDrawable(R.drawable.whitestar));
									like5.setImageDrawable(getResources().getDrawable(R.drawable.whitestar));
								}else{
									if(Double.parseDouble(like)<=3.5){
										like1.setImageDrawable(getResources().getDrawable(R.drawable.yellowstar));
										like2.setImageDrawable(getResources().getDrawable(R.drawable.yellowstar));
										like3.setImageDrawable(getResources().getDrawable(R.drawable.yellowstar));
										like4.setImageDrawable(getResources().getDrawable(R.drawable.halfstar));
										like5.setImageDrawable(getResources().getDrawable(R.drawable.whitestar));
									}else{
										if(Double.parseDouble(like)<=4.0){
											like1.setImageDrawable(getResources().getDrawable(R.drawable.yellowstar));
											like2.setImageDrawable(getResources().getDrawable(R.drawable.yellowstar));
											like3.setImageDrawable(getResources().getDrawable(R.drawable.yellowstar));
											like4.setImageDrawable(getResources().getDrawable(R.drawable.yellowstar));
											like5.setImageDrawable(getResources().getDrawable(R.drawable.whitestar));
										}else{
											if(Double.parseDouble(like)<=4.5){
												like1.setImageDrawable(getResources().getDrawable(R.drawable.yellowstar));
												like2.setImageDrawable(getResources().getDrawable(R.drawable.yellowstar));
												like3.setImageDrawable(getResources().getDrawable(R.drawable.yellowstar));
												like4.setImageDrawable(getResources().getDrawable(R.drawable.yellowstar));
												like5.setImageDrawable(getResources().getDrawable(R.drawable.halfstar));
											}else{
												if(Double.parseDouble(like)<=5.0){
													like1.setImageDrawable(getResources().getDrawable(R.drawable.yellowstar));
													like2.setImageDrawable(getResources().getDrawable(R.drawable.yellowstar));
													like3.setImageDrawable(getResources().getDrawable(R.drawable.yellowstar));
													like4.setImageDrawable(getResources().getDrawable(R.drawable.yellowstar));
													like5.setImageDrawable(getResources().getDrawable(R.drawable.yellowstar));
												}
											}

										}
									}
								}
							}
						}
					}
				}
			}
		}
	}


	/***
	 * 退出程序
	 */
	public void exit() {		
		if(sAllActivities!=null){
			try{
			for (Activity activity : sAllActivities) {
				activity.finish();
			}
			sAllActivities.clear();
			}catch(ConcurrentModificationException ex){	
				ex.printStackTrace();
			}
		}
		//退出百度定位
		TongGouApplication app = (TongGouApplication)getApplication();
		app.stopBaiduLBS();
	}


	public void deInit(){
		TongGouApplication.showLog("deInit()   @@@@@@@@@@@@@@@@@@@");
		TongGouService.allowPollingMessage = false;
		if(TongGouApplication.obdLists!=null){
			TongGouApplication.obdLists.clear();
		}
		MainActivity.haveFaultCode = false;
		sharedPreferences.edit().putBoolean(BaseActivity.LOGINED, false).commit();
		getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit()
		.putString(BaseActivity.PHONENAME, null)
		.putString(BaseActivity.SERVICE, null)
		.putString(BaseActivity.PHONE, null)
		.putString(BaseActivity.BRAND, null)
		.putString(BaseActivity.MODEL, null)
		.putString(BaseActivity.VEHICLENUM, null)
		.putString(BaseActivity.CHECKVOICE, null)
		.putString(BaseActivity.VEHICLE_MODE_ID, "")
		.putString(BaseActivity.APPCONFIG_OIL_LAST_STATUS, "2")
		.putInt(BaseActivity.NEW_MESSAGE_COUNT,0).commit();
		
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
	}


	protected Bitmap getPicture(String portraitUrl) {
		return myBitmapCache.getPicture(portraitUrl,this);       

	}

	protected Bitmap getBitmapFromView(View view) {
		view.destroyDrawingCache();
		view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.setDrawingCacheEnabled(true);
		Bitmap bitmap = view.getDrawingCache(true);
		return bitmap;
	}

	protected ShopIntent setShopIntent(Shop allShops){
		ShopIntent intentShop=new ShopIntent();

		intentShop.setId(allShops.getId());
		intentShop.setName(allShops.getName());
		intentShop.setCoordinate(allShops.getCoordinate());
		intentShop.setAddress(allShops.getAddress());

		intentShop.setServiceScope(allShops.getServiceScope());
		intentShop.setDistance(allShops.getDistance());
		intentShop.setTotalScore(allShops.getTotalScore());
		intentShop.setBigImageUrl(allShops.getBigImageUrl());	

		intentShop.setSmallImageUrl(allShops.getSmallImageUrl());
		intentShop.setMobile(allShops.getMobile());
		intentShop.setShopScore(allShops.getShopScore());
		intentShop.setCityCode(allShops.getCityCode());
		intentShop.setMemberInfo(allShops.getMemberInfo());
		return intentShop;
	}
	protected Shop setShop(ShopIntent allShops){
		Shop intentShop=new Shop();

		intentShop.setId(allShops.getId());
		intentShop.setName(allShops.getName());
		intentShop.setCoordinate(allShops.getCoordinate());
		intentShop.setAddress(allShops.getAddress());

		intentShop.setServiceScope(allShops.getServiceScope());
		intentShop.setDistance(allShops.getDistance());
		intentShop.setTotalScore(allShops.getTotalScore());
		intentShop.setBigImageUrl(allShops.getBigImageUrl());	

		intentShop.setSmallImageUrl(allShops.getSmallImageUrl());
		intentShop.setMobile(allShops.getMobile());
		intentShop.setShopScore(allShops.getShopScore());
		intentShop.setCityCode(allShops.getCityCode());
		intentShop.setMemberInfo(allShops.getMemberInfo());
		return intentShop;
	}
	
	
	private ProgressDialog mProgressDialog;
	private AlertDialog mErrorDialog;
	
	/**
	 * 显示正在加载的进度框
	 */
	protected void showProgressDialog(String msg) {
		dismissProgressDialog();
		dismissErrorDialog();
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setMessage(msg);
		mProgressDialog.setCancelable(false);
		if( !isFinishing() ){
			mProgressDialog.show();
		}
	}
	
	/**
	 * 卸载进度框
	 */
	protected void dismissProgressDialog() {
		if( mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}
	
	/**
	 * 显示错误对话框
	 * @param msg
	 */
	protected void showErrorDialog(String msg) {
		dismissProgressDialog();
		dismissErrorDialog();
		mErrorDialog = new AlertDialog.Builder(this)
			.setTitle("注册失败")
			.setIcon(android.R.drawable.ic_dialog_info)
			.setMessage(msg)
			.setNegativeButton(R.string.button_ok, null)
			.create();
		mErrorDialog.setCanceledOnTouchOutside(false);
		if( !isFinishing() ){
			mErrorDialog.show();
		}
	}
	
	/**
	 * 卸载错误对话框
	 */
	protected void dismissErrorDialog() {
		if( mErrorDialog != null && mErrorDialog.isShowing()) {
			mErrorDialog.dismiss();
			mErrorDialog = null;
		}
	}
	
	protected void onDestroy() {
		super.onDestroy();
		dismissErrorDialog();
		dismissProgressDialog();
		sAllActivities.remove(this);
	}
	
//
//	public static  void playVoice0(){
//		//mediaPlayer2.start();
//
//		soundPool.play(soundPoolMap.get(1), 1, 1, 0, 0, 1);  
//	}

}
