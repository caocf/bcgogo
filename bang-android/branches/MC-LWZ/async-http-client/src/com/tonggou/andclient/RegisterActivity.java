package com.tonggou.andclient;

import java.util.ConcurrentModificationException;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.parse.JSONParseInterface;
import com.tonggou.andclient.parse.RegistrationParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.SoftKeyboardUtil;
import com.tonggou.andclient.util.SomeUtil;

public class RegisterActivity extends AbsBackableActivity {
	
	private final int LOGIN_SUCCEED = 0x001;
	private final int LOGIN_FAILD = 0x002;
	private final int REQUEST_SUGGESSION_DATA_SUCCESS = 0X003;
	private final int REQUEST_SUGGESSION_DATA_FAILD = 0X004;
	
	private EditText mAccountNameEdit;
	private EditText mPasswordEdit;
	private EditText mPasswordAgainEdit;
	private EditText mPhoneEdit;

	private String mAccountName;
	private String mPassword;
	private String mPasswordAgain;
	private String mPhone;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			dismissProgressDialog();
			dismissErrorDialog();
			switch (msg.what) {
				case LOGIN_SUCCEED: onRegisterSuccess( msg.obj + "" ); break;
				case LOGIN_FAILD: showErrorDialog( msg.obj + "" ); break;
				case REQUEST_SUGGESSION_DATA_SUCCESS : onSuggestionRequestSuccess(msg.obj + ""); break;
				case REQUEST_SUGGESSION_DATA_FAILD : goHome(); break; // 若加载失败，则直接进入首页
				default: break;
			}
		}
	};

	@Override
	protected int getContentLayout() {
		return R.layout.activity_register;
	}
	
	@Override
	public void onBackPressed() {
		RegisterActivityNext.registercarnameStr=null;
		RegisterActivityNext.carBrand=null;
		RegisterActivityNext.carMold=null;
		RegisterActivityNext.carBrandId=null;
		RegisterActivityNext.carMoldId=null;
		RegisterActivityNext.shop2DCodeStr=null;
		RegisterActivityNext.shop2DCodeId = null;
		RegisterActivityNext.registernextmileStr=null;
		RegisterActivityNext.registernexttimeStr=null;
		RegisterActivityNext.registernexttime2Str=null;
		RegisterActivityNext.currentMileStr=null;
		Intent toHome = new Intent(this, LoginActivity.class);
		toHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(toHome);
		RegisterActivity.this.finish();
	}

	@Override
	protected void findViews(Bundle saveInstanceState) {
		super.findViews(saveInstanceState);
		getSimpleTitle().setTitle(R.string.register_title);
		mAccountNameEdit=(EditText) findViewById(R.id.account_name);
		mPasswordEdit=(EditText) findViewById(R.id.password);
		mPasswordAgainEdit=(EditText) findViewById(R.id.password_again);
		mPhoneEdit=(EditText) findViewById(R.id.phone_num);
		
		afterViews();
	}

	private void afterViews() {
		clearEditorTextFocus();
		setListener();
	}
	
	private void clearEditorTextFocus() {
		mPasswordEdit.clearFocus();
		mPasswordAgainEdit.clearFocus();
		mPhoneEdit.clearFocus();
	}

	private void setListener() {
		mAccountNameEdit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable et) {
				String s=et.toString();
			    if( SomeUtil.isMobileNo(s) ) {
			    	mPhoneEdit.setText(s);
			    }
			    mPhoneEdit.setError(null);
			}
		});
		
	}
	
//	@Override 
//	public void onFocusChange(View v, boolean hasFocus) {
//		switch (v.getId()) {
//			case R.id.account_name: isAccountNameValidate(); break;
//			case R.id.password: isPasswordValidate(); break;
//			case R.id.password_again: isPasswordAgainValidate(); break;
//			case R.id.phone_num: isPhoneValidate(); break;
//			default: break;
//		}
//	}
	
	public void onRegisterBtnClick(View view) {
		clearEditorTextFocus();
		SoftKeyboardUtil.hide(this, view.getWindowToken());
		if( validateData() ) {
			showProgressDialog(getString( R.string.register_running ));
			new Thread() {

				@Override
				public void run() {
					super.run();
					register(mAccountName, mPassword, mPhone);
				}
				
			}.start();
		}
	}
	
	private boolean validateData() {
		return isAccountNameValidate() && isPasswordValidate() 
				&& isPasswordAgainValidate() && isPhoneValidate();
	}
	
	private boolean isAccountNameValidate() {
		boolean isValidate = true;
		mAccountName = mAccountNameEdit.getText().toString().trim();
		if( TextUtils.isEmpty(mAccountName) ){	 
			isValidate = false;
		}
		
		if(SomeUtil.isMobileNo(mAccountName)){  //isMobileNO  isNumeric
			mPhone = mAccountName;
			isValidate = true;
		} else if( SomeUtil.isNumeric(mAccountName) ) {
			mAccountNameEdit.setError("请输入正确的手机号 或 直接用车牌号注册");
			isValidate = false;
		} else if( !SomeUtil.isVehicleNo(mAccountName.toUpperCase(Locale.getDefault())) ) {
			mAccountNameEdit.setError("请输入正确的车牌号 或 直接用手机号注册");
			isValidate = false;
		}
		
		return isValidate;
	}
	
	private boolean isPasswordValidate() {
		mPassword = mPasswordEdit.getText().toString().trim();
		if( TextUtils.isEmpty(mPassword) ) {
			mPasswordEdit.setError("请输入密码");
			return false;
		}
		return true;
	}
	
	private boolean isPasswordAgainValidate() {
		mPasswordAgain = mPasswordAgainEdit.getText().toString().trim();
		if( TextUtils.isEmpty(mPasswordAgain) || !mPasswordAgain.equals(mPassword) ) {
			mPasswordAgainEdit.setError("两次密码输入的不一致");
			return false;
		}
		return true;
	}
	
	private boolean isPhoneValidate() {
		mPhone = mPhoneEdit.getText().toString().trim();
		if( TextUtils.isEmpty(mPhone) ) {
			mPhoneEdit.setError("请输入手机号");
			return false;
		}
		
		if( !SomeUtil.isMobileNo(mPhone)) {
			mPhoneEdit.setError("请输入正确的手机号");
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @param accountName      //用户名  必填
	 * @param password         //密码       必填
	 * @param phonenumber      //手机号  必填
	 */
	private void register(String accountName,String password,String phonenumber){

		RegistrationParser registrationParser = new RegistrationParser();
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/user/registration";	

		StringBuffer sb1 = new StringBuffer();
		//email={email}&name={name}&shopEmployee={shopEmployee}
		//必填的 用户名 ，密码，手机号
		String value1 = "{\"userNo\":\""+accountName+"\", " +" \"password\":\""+password+"\", " +
				" \"mobile\":\""+phonenumber+"\"" ;
		//" \"mobile\":\""+phonenumber+"\",\"name\":\""+nickname+"\"" ;

		sb1.append(value1);
// 默认不行增车
//		if(SomeUtil.isVehicleNo(accountName)){//车牌号
//			String valueVB = ",\"vehicleNo\":\""+accountName.toUpperCase(Locale.getDefault())+"\"" ;
//			sb1.append(valueVB);
//		}
		sb1.append(",\"loginInfo\":{");

		String platformStr = "\"platform\":\""+INFO.MOBILE_PLATFORM+"\""  ;
		sb1.append(platformStr);

		if(INFO.VERSION!=null){
			String appVersionStr = ",\"appVersion\":\""+INFO.VERSION+"\""  ;
			sb1.append(appVersionStr);
		}
		String platformVersionStr = ",\"platformVersion\":\""+INFO.MOBILE_PLATFORM_VERSION+"\""  ;
		sb1.append(platformVersionStr);
		String mobileModelStr = ",\"mobileModel\":\""+INFO.MOBILE_MODEL+"\""  ;
		sb1.append(mobileModelStr);
		String imageVersionStr = ",\"imageVersion\":\""+INFO.IMAGE_VERSION+"\""  ;
		sb1.append(imageVersionStr);

		String valueMK = "}}";
		sb1.append(valueMK);

		String resultStr1 = sb1.toString();

		NetworkState ns = Network.getNetwork(RegisterActivity.this).httpPutUpdateString(url,resultStr1.getBytes(),registrationParser);
		if(ns.isNetworkSuccess()){
			if(registrationParser.isSuccessfull()){
				//正确的处理逻辑 
				String data = registrationParser.getRegistrationReponse().getData();
				sendMessage(LOGIN_SUCCEED, data);
				
//				Intent intent = new Intent(RegisterActivity.this,RegisterActivityNext.class);
//				intent.putExtra("tonggou.carNum",carNum);
				/*intent.putExtra("tonggou.name",nameStr);
				intent.putExtra("tonggou.password",passwordStr);
				
				intent.putExtra("tonggou.phonenumber",phonenumberStr);*/
//				startActivity(intent);
			}else{
				//提示用户错误
				String errorMsg = registrationParser.getErrorMessage();
				sendMessage(LOGIN_FAILD, errorMsg);
			}
		}else{
			//网络错误
			sendMessage(LOGIN_FAILD, ns.getErrorMessage());
		}
	}
	
	protected void sendMessage(int what, String msgContent) {
		Message msg = mHandler.obtainMessage(what);
		msg.obj = msgContent;
		mHandler.sendMessage(msg);
	}
	
	private void onRegisterSuccess(String data) {
		sharedPreferences.edit()
			.putString(NAME, mAccountName)
			.putString(PASSWORD, mPassword)
			.putString(PHONE, mPhone)
			.commit();
		TongGouApplication.showToast("注册完成");
		TongGouApplication.showLog(data);
		
		showProgressDialog("正在加载用户数据");
		new Thread() {

			@Override
			public void run() {
				requestVehicleSuggestionData();
				super.run();
			} 
			
		}.start();
		
//		if( TextUtils.isEmpty( data ) || "null".equalsIgnoreCase(data.trim()) ) {
//			goHome();
//		} else {
//			
//		}
	}
	
	private void requestVehicleSuggestionData() {
		String url = INFO.HTTP_HEAD+INFO.HOST_IP + "/vehicle/info/suggestion"; 
//		url += "/18001211000/NULL";
		if( SomeUtil.isMobileNo(mAccountName) ) {
			url += "/" + mAccountName +"/NULL";
		} else {
			url += "/NULL/" + mAccountName;
		}
		
		final NetworkState ns = Network.getNetwork(this).httpGetUpdateString(url, new JSONParseInterface() {
			
			@Override
			public void parsing(String dataFormServer) {
				try {
					TongGouApplication.showLog("注册后查询车辆信息 ： ---   " + dataFormServer);
					JSONObject result = new JSONObject(dataFormServer);
					if( "SUCCESS".equalsIgnoreCase(result.getString("status"))) {
						if( result.has("result") && !result.isNull("result")) {
							sendMessage(REQUEST_SUGGESSION_DATA_SUCCESS, result.getJSONObject("result").toString());
						} else {
							sendMessage(REQUEST_SUGGESSION_DATA_SUCCESS,"");
						}
						
					} else {
						//提示用户错误
						sendMessage(REQUEST_SUGGESSION_DATA_FAILD, result.getString("message"));
					}
				} catch (JSONException e) {
					sendMessage(REQUEST_SUGGESSION_DATA_FAILD, "数据解析失败");
					e.printStackTrace();
				}
			}
		});	
		if( !ns.isNetworkSuccess()){
			//网络出错
			sendMessage(REQUEST_SUGGESSION_DATA_FAILD, ns.getErrorMessage());
		}
	}
	
	private void onSuggestionRequestSuccess(String data) {
		TongGouApplication.showLog("返回的数据   " + data);
		if( TextUtils.isEmpty(data) || "null".equalsIgnoreCase(data)) {
			goHome();
		} else {
			String vehicleNo = "";
			String brandName = "";
			String brandId = "";
			String typeName = "";
			String typeId = "";
			try {
				JSONObject jsonData = new JSONObject(data);
				vehicleNo = jsonData.getString("vehicleNo");
				JSONObject jsonBrandTypeData = jsonData.getJSONObject("brandModel");
				brandName = jsonBrandTypeData.getString("brandName");
				brandId = jsonBrandTypeData.getString("brandId");
				typeName = jsonBrandTypeData.getString("modelName");
				typeId = jsonBrandTypeData.getString("modelId");
				
			} catch (JSONException e) {
				TongGouApplication.showToast("数据解析失败");
				e.printStackTrace();
			}
			turnToChangeVehicleInfoActivity(vehicleNo, brandName, brandId, typeName, typeId);
		}
	}
	
	private void goHome() {
		//跳转主页
		MainActivity.ifAutoLogin = true;
		if(sAllActivities!=null){
			try{
				for (Activity activity : sAllActivities) {
					activity.finish();
				}
				sAllActivities.clear();
			}catch(ConcurrentModificationException ex){	
			}
		}
		Intent intent = new Intent();
		intent.putExtra(HomePageActivity.EXTRA_FLAG_PREVIOUS_ACTIVITY_NAME, RegisterActivity.class.getName());
		intent.setClass(this, HomePageActivity.class);
		startActivity(intent);	
		finish();
		
	}
	
	private void turnToChangeVehicleInfoActivity(String vehicleNo, String brandName, String brandId, String typeName, String typeId) {
		Intent intent = new Intent();
		intent.setClass(this, RegisterChangeVehicleInfo.class);
		Bundle args = new Bundle();
		args.putString(RegisterChangeVehicleInfo.KEY_ARG_VEHICLE_NO, vehicleNo);
		args.putString(RegisterChangeVehicleInfo.KEY_ARG_BRAND_NAME, brandName);
		args.putString(RegisterChangeVehicleInfo.KEY_ARG_BRAND_ID, brandId);
		args.putString(RegisterChangeVehicleInfo.KEY_ARG_TYPE_NAME, typeName);
		args.putString(RegisterChangeVehicleInfo.KEY_ARG_TYPE_ID, typeId);
		intent.putExtras(args);
		startActivity(intent);
		finish();
	}
}
