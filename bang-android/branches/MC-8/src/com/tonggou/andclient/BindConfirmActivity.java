package com.tonggou.andclient;

import java.util.ArrayList;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.tonggou.andclient.app.BaseConnectOBDService;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.app.TongGouApplication.OnSelectVehicleBrandTypeListener;
import com.tonggou.andclient.app.TongGouService;
import com.tonggou.andclient.app.UpdateFaultDic;
import com.tonggou.andclient.myview.SimpleTitleBar;
import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.parse.AddBindCarParser;
import com.tonggou.andclient.parse.CommonParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.SomeUtil;
import com.tonggou.andclient.vo.OBDBindInfo;
import com.tonggou.andclient.vo.OBDDevice;
import com.tonggou.andclient.vo.VehicleInfo;

public class BindConfirmActivity extends AbsBackableActivity implements OnSelectVehicleBrandTypeListener {

	public static final String KEY_ARG_OBD_DEVICE = "obd_device";
	public static final String KEY_ARG_VIN = "vin";
	public static final String KEY_ARG_SHOP_NAME = "shop_name";
	public static final String KEY_ARG_SHOP_ID = "shop_id";
	private final String KEY_ARG_BRAND_NAME = "brand_name";
	private final String KEY_ARG_BRAND_ID = "brand_id";
	private final String KEY_ARG_TYPE_NAME = "type_name";
	private final String KEY_ARG_TYPE_ID = "type_id";
	private final String KEY_ARG_VEHICLE_NO = "vehicle_no";
	
	private ViewGroup mShopContainer;
	private ViewGroup mOBDInfoContainer;
	private EditText mOBDDeviceEdit;
	private EditText mVehicleNoEdit;
	private EditText mShopNameEdit;
	private Button mSelectBrandBtn;
	private Button mSelectTypeBtn;
	
	
	private OBDDevice mBindOBDDevice;
    private String mVIN;
    private String mVehicleNo;
    private String mShopName;
    private String mShopId;
    private String mBrandName;
    private String mBrandId;
    private String mTypeName;
    private String mTypeId;
    
	@Override
	protected int getContentLayout() {
		return R.layout.activity_bind_confirm;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if( intent == null || !restoreArgs(intent.getExtras())) {
			restoreArgs(savedInstanceState);
		}
		super.onCreate(savedInstanceState);
		TongGouApplication.getInstance().registerSelectVehicleBrandTypeListener(this);
	}

	private boolean restoreArgs(Bundle args) {
		if( args != null && args.containsKey(KEY_ARG_OBD_DEVICE) ) {
			mBindOBDDevice = (OBDDevice)args.getSerializable(KEY_ARG_OBD_DEVICE);
			mVIN = args.getString(KEY_ARG_VIN);
			mShopName = args.getString(KEY_ARG_SHOP_NAME);
			mShopId = args.getString(KEY_ARG_SHOP_ID);
			mBrandId = args.getString(KEY_ARG_BRAND_ID);
			mBrandName = args.getString(KEY_ARG_BRAND_NAME); 
			mTypeId = args.getString(KEY_ARG_TYPE_ID);
			mTypeName = args.getString(KEY_ARG_TYPE_NAME);
			mVehicleNo = args.getString(KEY_ARG_VEHICLE_NO);
			return true;
		}
		return false;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(KEY_ARG_OBD_DEVICE, mBindOBDDevice);
		outState.putString(KEY_ARG_VIN, mVIN);
		outState.putString(KEY_ARG_SHOP_NAME, mShopName);
		outState.putString(KEY_ARG_SHOP_ID, mShopId);
		outState.putString(KEY_ARG_BRAND_NAME, mSelectBrandBtn.getText().toString());
		outState.putString(KEY_ARG_BRAND_ID, mBrandId);
		outState.putString(KEY_ARG_TYPE_NAME, mSelectTypeBtn.getText().toString());
		outState.putString(KEY_ARG_TYPE_ID, mTypeId);
		outState.putString(KEY_ARG_VEHICLE_NO, mVehicleNo);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void afterTitleBarCreated(SimpleTitleBar titleBar, Bundle savedInstanceState) {
		super.afterTitleBarCreated(titleBar, savedInstanceState);
		titleBar.setTitle(R.string.title_confirm_bind_info);
	}

	@Override
	protected void findViews(Bundle savedInstanceState) {
		super.findViews(savedInstanceState);
		mOBDInfoContainer = (ViewGroup) findViewById(R.id.obd_info_container);
		mShopContainer = (ViewGroup) findViewById(R.id.shop_container);
		mOBDDeviceEdit = (EditText) findViewById(R.id.obd_device);
		mVehicleNoEdit = (EditText) findViewById(R.id.vehicle_no);
		mShopNameEdit = (EditText) findViewById(R.id.shop_name);
		mSelectBrandBtn = (Button) findViewById(R.id.select_brand_btn);
		mSelectTypeBtn = (Button) findViewById(R.id.select_type_btn);
		afterViews();
	}

	private void afterViews() {
		if( TongGouApplication.registerDefaultVehicle != null) {
			mBrandName = TongGouApplication.registerDefaultVehicle.getVehicleBrand();
			mTypeName = TongGouApplication.registerDefaultVehicle.getVehicleModel();
		}
		mSelectBrandBtn.setText(mBrandName);
		mSelectTypeBtn.setText(mTypeName);
		if( mBindOBDDevice != null ) {
			mOBDDeviceEdit.setText( mBindOBDDevice.getDeviceAddress() );
			String accountName = sharedPreferences.getString(NAME, "");
			if( SomeUtil.isVehicleNo(accountName) ) {
				mVehicleNo = accountName;
				mVehicleNoEdit.setText(mVehicleNo);
			}
		} else {
			mOBDInfoContainer.setVisibility(View.GONE);
		}
		if( !TextUtils.isEmpty(mShopName) ) {
			mShopNameEdit.setText(mShopName);
		} else {
			mShopContainer.setVisibility(View.GONE);
		}
		
		mVehicleNoEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if( hasFocus ) {
					mVehicleNoEdit.setError(null);
				}
			}
		});
		
		mVehicleNoEdit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable et) {
				
				String s=et.toString();
			    if(!s.equals(s.toUpperCase(Locale.getDefault()))) {
			      s=s.toUpperCase(Locale.getDefault());
			      mVehicleNoEdit.setText(s);
			      mVehicleNoEdit.setSelection(s.length()); // 设置光标位置
			    }
			}
		});
		
		
		//停掉连接obd
		BaseConnectOBDService.addingCar = true;
		Intent intentS = new Intent();
		intentS.setAction(TongGouService.TONGGOU_ACTION_START);
		intentS.putExtra("com.tonggou.server","STOP");
		sendBroadcast(intentS);
	}
	
	public void onBindInfoBtnClick(View view) {
		mVehicleNo = mVehicleNoEdit.getText().toString();
		if( !TextUtils.isEmpty(mVehicleNo) && !SomeUtil.isVehicleNo(mVehicleNo) ) {
			mVehicleNoEdit.setError("请填入正确的车牌号");
			return;
		}
		
		if( mOBDInfoContainer.getVisibility() != View.GONE 
				&& !TextUtils.isEmpty( mOBDDeviceEdit.getText() ) ) {
			
			if( TextUtils.isEmpty(mVehicleNo) ) {
				TongGouApplication.showToast("请填写车牌号码");
				return;
			}
			
			if( isBrandTypeValidate() ) {
				TongGouApplication.showLog("绑定 OBD 接口");
				showProgressDialog("绑定数据中，请稍候。。。");
				new Thread() {

					@Override
					public void run() {
						super.run();
						bindObd(mVIN, mVehicleNo, mTypeName, mTypeId, mBrandName, 
								mBrandId, mBindOBDDevice.getDeviceAddress(),
								sharedPreferences.getString(NAME, ""), mShopId);
					}
					
				}.start();
			}
			return;
		}
		
		if( mShopContainer.getVisibility() != View.GONE && !TextUtils.isEmpty( mShopName ) ) {
			showProgressDialog("绑定数据中，请稍候。。。");
			// 若车牌号不为空，品牌车型必填
			if( !TextUtils.isEmpty(mVehicleNo) && isBrandTypeValidate()) {
				TongGouApplication.showLog("保存车辆");
				new Thread() {

					@Override
					public void run() {
						super.run();
							bindVehicleShop(mShopId, mVehicleNo, mTypeName, mTypeId, mBrandName, mBrandId);
					}
				}.start();
				
			} else {
				TongGouApplication.showLog("店铺绑定接口");
				new Thread() {

					@Override
					public void run() {
						super.run();
						bindShop(mShopId);
					}
					
				}.start();
			}
		}
			
	}
	
	public void onSelectBrandBtnClick(View view) {
		Intent intent = new Intent(this, AppointmentNetWorkSearch.class);
		intent.putExtra("tonggou.from", "pinpai");
		intent.putExtra("tonggou.pinpai", "");
		startActivityForResult(intent, 1010);
	}
	
	public void onSelectTypeBtnClick(View view) {
		CharSequence brand = mSelectBrandBtn.getText();
		if( TextUtils.isEmpty(brand) ) {
			TongGouApplication.showToast(getString(R.string.brand_first));
			return;
		}
		
		Intent intent = new Intent(this,AppointmentNetWorkSearch.class);
		intent.putExtra("tonggou.from", "chexing");
		intent.putExtra("tonggou.pinpai",mBrandId);
		startActivityForResult(intent, 2020);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		TongGouApplication.showLog(requestCode + "  " + resultCode);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBrandSelected(boolean isCancle, String brandName, String brandId) {
		if( !isCancle ) {
			mBrandName = brandName;
			mSelectBrandBtn.setText(brandName);
			mBrandId = brandId;
			mTypeName = null;
			mTypeId = null;
			mSelectTypeBtn.setText(null);
		} 
	}

	@Override
	public void onTypeSelected(boolean isCancle, String typeName, String typeId) {
		if( !isCancle ) {
			mTypeName = typeName;
			mSelectTypeBtn.setText(typeName);
			mTypeId = typeId;
		} 
	}
	
	private boolean isBrandTypeValidate() {
		if( TextUtils.isEmpty( mBrandName )) {
			TongGouApplication.showToast("请选择品牌");
			return false;
		} 
		
		if( TextUtils.isEmpty(mTypeName) ) {
			TongGouApplication.showToast("请选择车型");
			return false;
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		TongGouApplication.getInstance().unregisterSelectVehicleBrandTypeListener(this);
		super.onDestroy();
	}
	
	// 接口部分
	private static final int  NETWORK_FAILD = -1;
	private static final int  REQUEST_SUCCEED = 0x001;
	private static final int  REQUEST_BIND_OBD_SUCCEED = 0x0011;
	private static final int  REQUEST_ADD_VEHICLE_SUCCEED = 0x0012;
	private static final int  REQUEST_FAILD = 0x002;
	
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			dismissProgressDialog();
			switch (msg.what) {
				case NETWORK_FAILD: showErrorDialog(msg.obj + ""); break;
				case REQUEST_SUCCEED: onRequestSuccess();break;
				case REQUEST_BIND_OBD_SUCCEED: onBindOBDSuccess();break;
				case REQUEST_ADD_VEHICLE_SUCCEED: onAddvehicleSuccess();break;
				case REQUEST_FAILD: showErrorDialog(msg.obj + ""); break;
				default: break;
			}
			TongGouApplication.showToast(msg.obj);
		}
		
	};
	
	private void bindObd(String vehicleVin,String vehicleNo,String vehicleModel,
			String vehicleModelId,String vehicleBrand,String vehicleBrandId,String obdSN,String userNo, String shopId){
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/obd/binding";
		CommonParser commonParser = new CommonParser();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("userNo",sharedPreferences.getString(BaseActivity.NAME, "")));	
		nameValuePairs.add(new BasicNameValuePair("obdSN",obdSN));
		nameValuePairs.add(new BasicNameValuePair("vehicleVin",vehicleVin));

		if( TongGouApplication.registerDefaultVehicle != null ) {
			String vehicleId = TongGouApplication.registerDefaultVehicle.getVehicleId();
			if( !TextUtils.isEmpty( vehicleId )  ) {
				TongGouApplication.showLog("bindConfirmActivity 车辆ID -- " + vehicleId);
				nameValuePairs.add(new BasicNameValuePair("vehicleId", vehicleId));
			}
		}
		

		nameValuePairs.add(new BasicNameValuePair("vehicleNo",vehicleNo.trim()));

		nameValuePairs.add(new BasicNameValuePair("vehicleModelId",vehicleModelId));
		nameValuePairs.add(new BasicNameValuePair("vehicleModel",vehicleModel));

		nameValuePairs.add(new BasicNameValuePair("vehicleBrandId",vehicleBrandId));
		nameValuePairs.add(new BasicNameValuePair("vehicleBrand",vehicleBrand));
		
		// 若有 店铺 ID 那么就加上
		if( !TextUtils.isEmpty(shopId) ) {
			nameValuePairs.add(new BasicNameValuePair("sellShopId",shopId));
		}

		final NetworkState ns = Network.getNetwork(this).httpPostUpdateString(url,nameValuePairs,commonParser);	
		if(ns.isNetworkSuccess()){
			if(commonParser.isSuccessfull()){
				//更新字典
				UpdateFaultDic.getUpdateFaultDic(this).updateFaultDic(vehicleModelId);				
				String mes = commonParser.getCommonResponse().getMessage();
				sendMessage(REQUEST_BIND_OBD_SUCCEED, mes);
			}else{
				//提示用户错误
				String errorAlert = commonParser.getErrorMessage();
				sendMessage(REQUEST_FAILD, errorAlert);
			}
		}else{
			//网络出错
			sendMessage(NETWORK_FAILD, ns.getErrorMessage());
		}
	}
	
	private void bindVehicleShop(String shopId, String vehicleNo,String vehicleModel,
			String vehicleModelId,String vehicleBrand,String vehicleBrandId){
		
		AddBindCarParser addBindCarParser = new AddBindCarParser();
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/vehicle/vehicleInfo";
		String valuePairs ="{\"vehicleNo\":\""+vehicleNo.trim()+"\"," +
				" \"vehicleModel\":\""+vehicleModel+"\",\"vehicleModelId\":\""+vehicleModelId+"\"" +
				",\"vehicleBrand\":\""+vehicleBrand+"\"" +
				",\"vehicleBrandId\":\""+vehicleBrandId+"\"" +
				",\"bindingShopId\":\""+shopId+"\"" +
				",\"userNo\":\""+ sharedPreferences.getString(BaseActivity.NAME, "") +"\"}";
		
		final NetworkState ns = Network.getNetwork(this).httpPutUpdateString(url,valuePairs.getBytes(),addBindCarParser);
		if(ns.isNetworkSuccess()){
			if(addBindCarParser.isSuccessfull()){
				//更新字典
				UpdateFaultDic.getUpdateFaultDic(this).updateFaultDic(vehicleModelId);				
				String mes = addBindCarParser.getAddBindCarResponse().getMessage();
				sendMessage(REQUEST_ADD_VEHICLE_SUCCEED, mes);
			}else{
				//提示用户错误
				String errorAlert = addBindCarParser.getErrorMessage();
				sendMessage(REQUEST_FAILD, errorAlert);
			}
		}else{
			//网络出错
			sendMessage(NETWORK_FAILD, ns.getErrorMessage());
		}
	}
	
	private void bindShop(String shopId){
		
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/shop/binding";
		CommonParser commonParser = new CommonParser();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("shopId", shopId));	
		final NetworkState ns = Network.getNetwork(this).httpPostUpdateString(url,nameValuePairs,commonParser);	
		if(ns.isNetworkSuccess()){
			if(commonParser.isSuccessfull()){
				//更新字典
				String mes = commonParser.getCommonResponse().getMessage();
				sendMessage(REQUEST_SUCCEED, mes);
				
			}else{
				//提示用户错误
				String errorAlert = commonParser.getErrorMessage();
				sendMessage(REQUEST_FAILD, errorAlert);
			}
		}else{
			//网络出错
			sendMessage(NETWORK_FAILD, ns.getErrorMessage());
		}
	}
	
	private void sendMessage(int what, String msg) {
		Message message = mHandler.obtainMessage(what);
		message.obj = msg;
		mHandler.sendMessage(message);
	}
	
	private void onRequestSuccess() {
		// 将注册默认返回的车信息去掉
		TongGouApplication.registerDefaultVehicle = null;
		finish();
	}
	
	private void onAddvehicleSuccess() {
		sharedPreferences.edit().putString(BaseActivity.BRAND, mBrandName)
		.putString(BaseActivity.MODEL, mTypeName)
		.putString(BaseActivity.VEHICLENUM, mVehicleNo).commit();
		MainActivity.defaultBrandAndModle = mBrandName + " " + mTypeName;
		TongGouApplication.getInstance().sendMainActivityChangeTitleBroadcast(mBrandName + " " + mTypeName);
		onRequestSuccess();
	}
	
	private void onBindOBDSuccess() {
		TongGouApplication.showLog("onBindOBDSuccess");
		if( TongGouApplication.obdLists == null) {
			TongGouApplication.obdLists = new ArrayList<OBDBindInfo>();
		}
		
		OBDBindInfo obdBindInfo = new OBDBindInfo();
		obdBindInfo.setIsDefault("1");
		obdBindInfo.setObdSN(mBindOBDDevice.getDeviceAddress());
		VehicleInfo vehicleInfo = new VehicleInfo();
		if( TongGouApplication.registerDefaultVehicle != null ) {
			vehicleInfo = TongGouApplication.registerDefaultVehicle;
		}
		vehicleInfo.setObdSN(mBindOBDDevice.getDeviceAddress());
		vehicleInfo.setVehicleBrand(mBrandName);
		vehicleInfo.setVehicleBrandId(mBrandId);
		vehicleInfo.setVehicleModel(mTypeName);
		vehicleInfo.setVehicleModelId(mTypeId);
		vehicleInfo.setVehicleVin(mVIN);
		obdBindInfo.setVehicleInfo(vehicleInfo);
		TongGouApplication.obdLists.add(obdBindInfo);
		MainActivity.defaultBrandAndModle = mBrandName + " " + mTypeName;
		
		//连接obd
		Intent intent = new Intent();//创建Intent对象
		intent.setAction(TongGouService.TONGGOU_ACTION_START);
		intent.putExtra("com.tonggou.server","SCAN_OBD");
		sendBroadcast(intent);//发送广播
		BaseConnectOBDService.addingCar = false;
		
		onRequestSuccess();
	}
	
}
