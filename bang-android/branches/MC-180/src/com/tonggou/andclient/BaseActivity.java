package com.tonggou.andclient;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.app.TongGouService;
import com.tonggou.andclient.myview.AbsCustomAlertDialog;
import com.tonggou.andclient.myview.LoadingDialog;
import com.tonggou.andclient.myview.MessageDialog;
import com.tonggou.andclient.myview.MessageDialog.DialogType;
import com.tonggou.andclient.network.request.HttpRequestClient;
import com.tonggou.andclient.util.BitmapCache;
import com.tonggou.andclient.vo.Shop;
import com.tonggou.andclient.vo.ShopIntent;

public abstract class BaseActivity extends AbsUMengActivity{
	public static LinkedList<WeakReference<Activity>> sAllActivities = new LinkedList<WeakReference<Activity>>();
	public static final String SETTING_INFOS = "SETTING_Infos";

//	public static final String LOGINED = "login";                              // 登录进去过

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
	public static final String COMMON_FAULT_DIC_VERSON = "common_fault_dic_version";         // 通用故障码版本号
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
	public static final String PACKAGE_DB_PATH = "/data/data/com.tonggou.andclient/databases/";        
	public static final String COMMON_FAULT_DIC_FILE_NAME = "common_fault_dic.db";        

	/** 用于显示提示消息 */
	public static final int SEND_MESSAGE = -1;











	protected SharedPreferences sharedPreferences ;
	protected String currentUsername;   //当前用户名
	protected String currentPwd;   //当前密码
	private BitmapCache myBitmapCache;

	//public static MediaPlayer mediaPlayer2;
	//public static SoundPool soundPool;
	//public static  HashMap<Integer, Integer> soundPoolMap = new HashMap<Integer, Integer>();     

	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		sAllActivities.add(new WeakReference<Activity>(this));
	
		//mediaPlayer2 = new MediaPlayer();
		//mediaPlayer2 = MediaPlayer.create(this,R.raw.tink);
		//mediaPlayer2.setAudioStreamType(AudioManager.STREAM_MUSIC); 

//		soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);  
//		soundPoolMap = new HashMap<Integer, Integer>();  
//		soundPoolMap.put(1,soundPool.load(this, R.raw.tink, 1));

		myBitmapCache = BitmapCache.getInstance();
		sharedPreferences = getSharedPreferences(BaseActivity.SETTING_INFOS, 0);
		currentUsername = sharedPreferences.getString(BaseActivity.NAME, "NULL");
		currentPwd = sharedPreferences.getString(BaseActivity.PASSWORD, "NULL");

		
	
		

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

	public static void removeAllReferenceActivity() {
		if(sAllActivities!=null){
			while( !sAllActivities.isEmpty()) {
				WeakReference<Activity> reference = sAllActivities.getFirst();
				Activity activity = reference.get();
				if( activity != null && !activity.isFinishing() ) {
					TongGouApplication.showLog( "finish activity @ " + activity.getClass().getSimpleName());
					activity.finish();
				}
				if( !sAllActivities.isEmpty() ) {
					sAllActivities.removeFirst();
				}
			}
		}
	}
	
	public static void cancleAllRequest() {
		if(sAllActivities!=null){
			for(int i=0; i<sAllActivities.size(); i++) {
				WeakReference<Activity> reference = sAllActivities.getFirst();
				Activity activity = reference.get();
				if( activity != null) {
					HttpRequestClient.cancelRequest(activity, true);
				}
			}
		}
	}

	/***
	 * 退出程序
	 */
	public static void exit() {	
		cancleAllRequest();
		removeAllReferenceActivity();
		//退出百度定位
		TongGouApplication.getInstance().stopBaiduLBS();
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
	
	
	private MessageDialog mMessageDialog;
	private LoadingDialog mLoadingDialog;
	
	/**
	 * 显示错误对话框
	 * @param msg
	 */
	protected void showErrorMessageDialog(String errorMsg) {
		showConfirmDialog(errorMsg, DialogType.NEGATIVE);
	}
	
	/**
	 * 显示正确对话框
	 * @param msg
	 */
	protected void showSuccessMessageDialog(String msg) {
		showConfirmDialog(msg, DialogType.POSITIVE);
	}
	
	private void showConfirmDialog(String msg, DialogType type) {
		dismissAllDialog();
		mMessageDialog = new MessageDialog(this, type);
		mMessageDialog.showDialog(msg);
	}
	
	/**
	 * 显示正在加载的进度框
	 */
	protected void showLoadingDialog(String msg) {
		dismissAllDialog();
		mLoadingDialog = new LoadingDialog(this);
		mLoadingDialog.showDialog(msg);
	}
	
	/**
	 * 卸载加载进度框
	 */
	protected void dismissLoadingDialog() {
		AbsCustomAlertDialog.dismissDialog(mLoadingDialog);
	}
	
	/**
	 * 卸载错误对话框
	 */
	protected void dismissMessageDialog() {
		AbsCustomAlertDialog.dismissDialog(mMessageDialog);
	}
	
	/**
	 * 卸载所有对话框
	 */
	protected void dismissAllDialog() {
		dismissLoadingDialog();
		dismissMessageDialog();
	}
	
	protected void onDestroy() {
		super.onDestroy();
		HttpRequestClient.cancelRequest(this, true);
		dismissAllDialog();
		sAllActivities.remove(this);
	}
	
	
	/**
	 * 网络请求成功但是 解析 到错误时的处理方法
	 * @param errorCode	错误码
	 * @param errorMsg 错误信息
	 */
	public void handlerParserFailure(String errorCode, String errorMsg) {
		// 登录过期
		if( "-202".equalsIgnoreCase( errorCode )) {
			handlerLoginExpire(errorCode, errorMsg);
		} else {
			TongGouApplication.showToast(errorMsg);
			TongGouApplication.showLog(errorCode + "  " + errorMsg);
		}
	}
	
	/**
	 * 处理登录超时的方法
	 * @param errorCode	错误码
	 * @param errorMsg 错误信息
	 */
	public void handlerLoginExpire(String errorCode, String errorMsg) {
		stopOBDService();
		        
        TongGouApplication.getInstance().doExpireLogin();
	}
	
	/**
	 * 停止 OBD 服务
	 */
	public void stopOBDService() {
		//停掉连接obd
		Intent stopObdService = new Intent();
		stopObdService.setAction(TongGouService.TONGGOU_ACTION_START);
        stopObdService.putExtra("com.tonggou.server","STOP");
        sendBroadcast(stopObdService);
	}
	
//
//	public static  void playVoice0(){
//		//mediaPlayer2.start();
//
//		soundPool.play(soundPoolMap.get(1), 1, 1, 0, 0, 1);  
//	}

}
