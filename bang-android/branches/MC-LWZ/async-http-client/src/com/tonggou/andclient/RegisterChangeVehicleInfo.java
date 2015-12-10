package com.tonggou.andclient;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
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
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.SoftKeyboardUtil;
import com.tonggou.andclient.util.SomeUtil;

public class RegisterChangeVehicleInfo extends AbsTitleBarActivity implements OnSelectVehicleBrandTypeListener {

	public static final String KEY_ARG_BRAND_NAME = "brand_name";
	public static final String KEY_ARG_BRAND_ID = "brand_id";
	public static final String KEY_ARG_TYPE_NAME = "type_name";
	public static final String KEY_ARG_TYPE_ID = "type_id";
	public static final String KEY_ARG_VEHICLE_NO = "vehicle_no";
	
	private EditText mVehicleNoEdit;
	private Button mSelectBrandBtn;
	private Button mSelectTypeBtn;
	
	private String mVehicleNo;
	private String mBrandName;
    private String mBrandId;
    private String mTypeName;
    private String mTypeId;
    
	@Override
	protected int getContentLayout() {
		return R.layout.activity_register_change_vehicle_info;
	}
	
	@Override
	protected void afterTitleBarCreated(SimpleTitleBar titleBar, Bundle saveInstanceState) {
		super.afterTitleBarCreated(titleBar, saveInstanceState);
		titleBar.setTitle("确认车辆信息");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if( intent == null || !restoreArgs(intent.getExtras())) {
			restoreArgs(savedInstanceState);
		}
		super.onCreate(savedInstanceState);
	}
	
	private boolean restoreArgs(Bundle args) {
		if( args != null && args.containsKey(KEY_ARG_VEHICLE_NO) ) {
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
		outState.putString(KEY_ARG_BRAND_NAME, mSelectBrandBtn.getText().toString());
		outState.putString(KEY_ARG_BRAND_ID, mBrandId);
		outState.putString(KEY_ARG_TYPE_NAME, mSelectTypeBtn.getText().toString());
		outState.putString(KEY_ARG_TYPE_ID, mTypeId);
		outState.putString(KEY_ARG_VEHICLE_NO, mVehicleNo);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void findViews(Bundle saveInstanceState) {
		super.findViews(saveInstanceState);
		
		mVehicleNoEdit = (EditText) findViewById(R.id.vehicle_no);
		mSelectBrandBtn = (Button) findViewById(R.id.select_brand_btn);
		mSelectTypeBtn = (Button) findViewById(R.id.select_type_btn);
		afterViews();
	}
	
	private void afterViews() {
		TongGouApplication.getInstance().registerSelectVehicleBrandTypeListener(this);
		mVehicleNoEdit.setText(mVehicleNo);
		mSelectBrandBtn.setText(mBrandName);
		mSelectTypeBtn.setText(mTypeName);
		
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
	}
	
	public void onBtnOkClick(View view) {
		SoftKeyboardUtil.hide(this, view.getWindowToken());
		mVehicleNo = mVehicleNoEdit.getText().toString();
		if( !TextUtils.isEmpty(mVehicleNo) && !SomeUtil.isVehicleNo(mVehicleNo) ) {
			mVehicleNoEdit.setError("请填入正确的车牌号");
			return;
		}
		
		if(isBrandTypeValidate()) {
			
			//停掉连接obd
			BaseConnectOBDService.addingCar = true;
			Intent intentS = new Intent();
			intentS.setAction(TongGouService.TONGGOU_ACTION_START);
			intentS.putExtra("com.tonggou.server","STOP");
			sendBroadcast(intentS);
			
			TongGouApplication.showLog("注册 确认车请求" );
			showProgressDialog("正在请求数据，请稍候...");
			new Thread() {

				@Override
				public void run() {
					super.run();
					bindVehicle(mVehicleNo, mTypeName, mTypeId, mBrandName, mBrandId );
				}
			}.start();
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
	
	// 接口部分
	private static final int  NETWORK_FAILD = -1;
	private static final int  REQUEST_SUCCEED = 0x001;
	private static final int  REQUEST_FAILD = 0x002;
	
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			dismissProgressDialog();
			TongGouApplication.showLog("信息：" + msg.obj);
			switch (msg.what) {
				case NETWORK_FAILD: onRequestFailure(msg.obj + ""); break;
				case REQUEST_SUCCEED: onRequestSuccess(msg.obj + "");break;
				case REQUEST_FAILD: onRequestFailure(msg.obj + ""); break;
				default: break;
			}
		}
		
	};
	
	private void bindVehicle(String vehicleNo,String vehicleModel,
			String vehicleModelId,String vehicleBrand,String vehicleBrandId){
		
		AddBindCarParser addBindCarParser = new AddBindCarParser();
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/vehicle/vehicleInfo";
		String valuePairs ="{\"vehicleNo\":\""+vehicleNo.trim()+"\"," +
				" \"vehicleModel\":\""+vehicleModel+"\",\"vehicleModelId\":"+vehicleModelId +
				",\"vehicleBrand\":\""+vehicleBrand+"\"" +
				",\"vehicleBrandId\":"+vehicleBrandId+
				/*",\"obdSN\":\""+obdSN+"\"" +*/
				",\"userNo\":\""+ sharedPreferences.getString(BaseActivity.NAME, "") +"\"}";
		
		final NetworkState ns = Network.getNetwork(this).httpPutUpdateString(url,valuePairs.getBytes(),addBindCarParser);
		if(ns.isNetworkSuccess()){
			if(addBindCarParser.isSuccessfull()){
				//更新字典
				UpdateFaultDic.getUpdateFaultDic(this).updateFaultDic(vehicleModelId);				
				String mes = addBindCarParser.getAddBindCarResponse().getMessage();
				TongGouApplication.registerDefaultVehicle = addBindCarParser.getAddBindCarResponse().getVehicleInfo();
				TongGouApplication.showLog( "添加的车辆信息   " + addBindCarParser.getAddBindCarResponse().getVehicleInfo().getVehicleBrand());
				sendMessage(REQUEST_SUCCEED, mes);
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
	
	private void sendMessage(int what, String msg) {
		Message message = mHandler.obtainMessage(what);
		message.obj = msg;
		mHandler.sendMessage(message);
	}
	
	private void onRequestSuccess(String msg) {
		TongGouApplication.showLog( "保存车辆成功   " + msg );
		
		sharedPreferences.edit().putString(BaseActivity.BRAND, mBrandName)
		.putString(BaseActivity.MODEL, mTypeName)
		.putString(BaseActivity.VEHICLENUM, mVehicleNo).commit();
		MainActivity.defaultBrandAndModle = mBrandName + " " + mTypeName;
		TongGouApplication.showLog(MainActivity.defaultBrandAndModle);
		TongGouApplication.getInstance().sendMainActivityChangeTitleBroadcast(MainActivity.defaultBrandAndModle);
		TongGouApplication.mainActivityDefaultCarInfo = mBrandName + " " + mTypeName;
		goHome();
	}
	
	private void onRequestFailure(String msg) {
		showErrorDialog(msg);
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
	public void onBackPressed() {
		super.onBackPressed();
		goHome();
		finish();
	}
	
	private void goHome() {
		
		//跳转主页
		MainActivity.ifAutoLogin = true;
		Intent intent = new Intent();
		intent.putExtra(HomePageActivity.EXTRA_FLAG_PREVIOUS_ACTIVITY_NAME, RegisterActivity.class.getName());
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.setClass(this, HomePageActivity.class);
		startActivity(intent);	
		finish();
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
	
	@Override
	protected void onDestroy() {
		TongGouApplication.getInstance().unregisterSelectVehicleBrandTypeListener(this);
		super.onDestroy();
	}
}
