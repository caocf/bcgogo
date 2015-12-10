package com.tonggou.andclient;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.myview.SimpleTitleBar;
import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.parse.AddBindCarParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.vo.JuheTransgressArea;
import com.tonggou.andclient.vo.VehicleInfo;

public class ChangeTransgressQueryConditionActivity extends AbsBackableActivity {

	public static final String KEY_PARAM_VEHICLE_INFO = "vehicle_info";
	public static final String KEY_PARAM_SELECTED_CITY = "selected_city";
	
	private final int ADD_SUCCEED = 0x02;	// 请求成功
	private final int ADD_FAILD = 0x03;	// 请求失败
	private final int NETWORK_FAILD = 0x04;	// 网络连接失败
	
	
	private VehicleInfo mVehicleInfo;
	private JuheTransgressArea mSelectedCity;
	
	private EditText mVehicleVinEdit;
	private EditText mEngineNoEdit;
	private EditText mRegistNoEdit;
	private Button mSubmitBtn;
	
	private String mVehicleVin;
	private String mEngineNo;
	private String mRegistNo;
	
	private boolean[] isNeed;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mSubmitBtn.setEnabled(true);
			getSimpleTitle().hideLoadingIndicator();
			switch (msg.what) {
				case ADD_SUCCEED: onSuccess(); break;
				case ADD_FAILD: onFaild(msg.obj + ""); break;
				case NETWORK_FAILD: onFaild(msg.obj + ""); break;
				default: break;
			}
		}

	};
	
	@Override
	protected int getContentLayout() {
		return R.layout.activity_change_transgress_query_condition;
	}
	
	@Override
	protected void afterTitleBarCreated(SimpleTitleBar titleBar, Bundle savedInstanceState) {
		super.afterTitleBarCreated(titleBar, savedInstanceState);
		titleBar.setTitle("添加查询信息");
	}
	
	@Override
	protected void findViews(Bundle savedInstanceState) {
		super.findViews(savedInstanceState);
		if( !restoreArgs(getIntent()) ) {
			restoreArgs(savedInstanceState);
		}
		
		mVehicleVinEdit = (EditText) findViewById(R.id.vehicle_vin);
		mEngineNoEdit = (EditText) findViewById(R.id.vehicle_engine);
		mRegistNoEdit = (EditText) findViewById(R.id.vehicle_regist);
		mSubmitBtn = (Button) findViewById(R.id.btn_ok);
		mSubmitBtn.setEnabled(true);
		
		isNeed = TransgressQueryActivity.getQueryCondition(mSelectedCity);

		mEngineNoEdit.setBackgroundResource( isNeed[0] ? R.drawable.important_editback : R.drawable.register_editback );
		mVehicleVinEdit.setBackgroundResource( isNeed[1] ? R.drawable.important_editback : R.drawable.register_editback );
		mRegistNoEdit.setBackgroundResource( isNeed[2] ? R.drawable.important_editback : R.drawable.register_editback );
		
		mVehicleVinEdit.setText( mVehicleInfo.getVehicleVin() );
		mEngineNoEdit.setText( mVehicleInfo.getEngineNo());
		mRegistNoEdit.setText(mVehicleInfo.getRegistNo());
		
		if( isNeed[0] ) {
			mEngineNoEdit.requestFocus();
		} else if( isNeed[1] ) {
			mVehicleVinEdit.requestFocus();
		} else if( isNeed[2] ) {
			mRegistNoEdit.requestFocus();
		}
		
	}

	private boolean restoreArgs( Bundle args ) {
		if( args != null && args.containsKey(KEY_PARAM_VEHICLE_INFO) ) {
			mVehicleInfo = (VehicleInfo) args.getSerializable(KEY_PARAM_VEHICLE_INFO);
			mSelectedCity = (JuheTransgressArea) args.getSerializable(KEY_PARAM_SELECTED_CITY);
			return true;
		}
		return false;
	}
	
	public boolean restoreArgs( Intent intent ) {
		if( intent != null && intent.hasExtra(KEY_PARAM_VEHICLE_INFO) ) {
			mVehicleInfo = (VehicleInfo) intent.getParcelableExtra(KEY_PARAM_VEHICLE_INFO);
			mSelectedCity = (JuheTransgressArea) intent.getSerializableExtra(KEY_PARAM_SELECTED_CITY);
			return true;
		}
		return false;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(KEY_PARAM_VEHICLE_INFO, mVehicleInfo);
		outState.putSerializable(KEY_PARAM_SELECTED_CITY, mSelectedCity);
		super.onSaveInstanceState(outState);
	}
	
	public void onSubmitBtnClick(View view) {
		mVehicleVin = mVehicleVinEdit.getText().toString().trim();
		mEngineNo = mEngineNoEdit.getText().toString().trim();
		mRegistNo = mRegistNoEdit.getText().toString().trim();
		
		if( isNeed[0] && TextUtils.isEmpty( mEngineNo ) ) {
			TongGouApplication.showToast("请输入发动机号");
			return;
		} 
		
		if( isNeed[1] && TextUtils.isEmpty( mVehicleVin ) ) {
			TongGouApplication.showToast("请输入车架号");
			return;
		} 
		
		if( isNeed[2] && TextUtils.isEmpty( mRegistNo ) ) {
			TongGouApplication.showToast("请输入登记证书号");
			return;
		} 
		
		doSubmit(mVehicleVin, mEngineNo, mRegistNo);
	}

	private void doSubmit(final String vehicleVin, final String engineNo, final String registNo) {
		getSimpleTitle().showLoadingIndicator();
		mSubmitBtn.setEnabled(false);
		new Thread() {
			
			@Override
			public void run() {
				requestUpdateVehicleInfor(vehicleVin, engineNo, registNo);
			};
		}.start();
	}
	
	//保存车辆信息
	private void requestUpdateVehicleInfor( String vehicleVin, String engineNo, String registNo){

		AddBindCarParser addBindCarParser = new AddBindCarParser();
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/vehicle/vehicleInfo";
		
		JSONObject params = new JSONObject();
		try {
			params.put("vehicleId", mVehicleInfo.getVehicleId());
			params.put("userNo", mVehicleInfo.getUserNo());
			params.put("vehicleNo", mVehicleInfo.getVehicleNo());
			params.put("vehicleBrand", mVehicleInfo.getVehicleBrand());
			params.put("vehicleModel", mVehicleInfo.getVehicleModel());
			if( !TextUtils.isEmpty( vehicleVin) ) {
				params.put("vehicleVin", vehicleVin);
			}
			if( !TextUtils.isEmpty( engineNo) ) {
				params.put("engineNo", engineNo);
			}
			if( !TextUtils.isEmpty( registNo) ) {
				params.put("registNo", registNo);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		TongGouApplication.showLog( params.toString() );
		NetworkState ns = Network.getNetwork(this).httpPutUpdateString(url,params.toString().getBytes(),addBindCarParser);
		if(ns.isNetworkSuccess()){
			if(addBindCarParser.isSuccessfull()){
				//正确的处理逻辑 
				String mes = addBindCarParser.getAddBindCarResponse().getMessage();
				sendMessage(ADD_SUCCEED, mes);
			}else{
				//提示用户错误
				String errorAlert = addBindCarParser.getErrorMessage();
				sendMessage(ADD_FAILD, errorAlert);
			}
		}else{
			//网络错误
			sendMessage(NETWORK_FAILD, ns.getErrorMessage());
		}
	}
	
	private void sendMessage(int what, Object msg) {
		Message message = mHandler.obtainMessage(what);
		message.obj = msg;
		mHandler.sendMessage(message);
	}
		
	private void onFaild(String msg) {
		TongGouApplication.showToast(msg);
	}

	private void onSuccess() {
		setResult(RESULT_OK);
		finish();
	}
}
