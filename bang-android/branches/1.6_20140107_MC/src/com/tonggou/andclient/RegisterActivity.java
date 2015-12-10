package com.tonggou.andclient;

import java.lang.ref.WeakReference;
import java.util.Locale;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.guest.GuestVehicleManager;
import com.tonggou.andclient.jsonresponse.BaseResponse;
import com.tonggou.andclient.jsonresponse.VehicleInfoSuggestionResponse;
import com.tonggou.andclient.network.parser.AsyncJSONResponseParseHandler;
import com.tonggou.andclient.network.request.QueryVehicleInfoSuggestionRequest;
import com.tonggou.andclient.network.request.RegisterRequest;
import com.tonggou.andclient.util.SoftKeyboardUtil;
import com.tonggou.andclient.util.SomeUtil;

public class RegisterActivity extends AbsBackableActivity {
	private final int REQUEEST_CODE_BIND_LOCAL_VEHICLE = 0x11;
	
	private EditText mAccountNameEdit;
	private EditText mPasswordEdit;
//	private EditText mPasswordAgainEdit;
	private EditText mPhoneEdit;

	private String mAccountName;
	private String mPassword;
	private String mPasswordAgain;
	private String mPhone;

	@Override
	protected int getContentLayout() {
		return R.layout.activity_register;
	}
	
	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	protected void findViews(Bundle saveInstanceState) {
		super.findViews(saveInstanceState);
		getSimpleTitle().setTitle(R.string.register_title);
		mAccountNameEdit=(EditText) findViewById(R.id.account_name);
		mPasswordEdit=(EditText) findViewById(R.id.password);
//		mPasswordAgainEdit=(EditText) findViewById(R.id.password_again);
		mPhoneEdit=(EditText) findViewById(R.id.phone_num);
		
		afterViews();
	}

	private void afterViews() {
		clearEditorTextFocus();
		setListener();
	}
	
	private void clearEditorTextFocus() {
		mPasswordEdit.clearFocus();
//		mPasswordAgainEdit.clearFocus();
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
	
	public void onRegisterBtnClick(View view) {
		clearEditorTextFocus();
		SoftKeyboardUtil.hide(this, view.getWindowToken());
		if( validateData() ) {
			register(mAccountName, mPassword, mPhone);
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
			mAccountNameEdit.setError("请输入用户名");
			return false;
		}
		
		if(SomeUtil.isMobileNo(mAccountName)){  //isMobileNO  isNumeric
			mPhone = mAccountName;
			isValidate = true;
		} else if( SomeUtil.isNumeric(mAccountName) ) {
			mAccountNameEdit.setError("请输入正确的手机号或车牌号");
			isValidate = false;
		} else if( !SomeUtil.isVehicleNo(mAccountName.toUpperCase(Locale.getDefault())) ) {
			mAccountNameEdit.setError("请输入正确的车牌号或手机号");
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
//		mPasswordAgain = mPasswordAgainEdit.getText().toString().trim();
//		if(  TextUtils.isEmpty(mPasswordAgain) ) {
//			mPasswordAgainEdit.setError("请输入确认密码");
//			return false;
//		}
//		if( !mPasswordAgain.equals(mPassword) ) {
//			mPasswordAgainEdit.setError("密码与确认密码不一致");
//			return false;
//		}
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
		showLoadingDialog("注册中...");
		RegisterRequest request = new RegisterRequest();
		request.setRequestParams(accountName, password, phonenumber);
		request.doRequest(this, new AsyncJSONResponseParseHandler<BaseResponse>() {

			@Override
			public void onParseSuccess(BaseResponse result, byte[] originResult) {
				super.onParseSuccess(result, originResult);
				onRegisterSuccess();
			}
			
			@Override
			public Class<BaseResponse> getTypeClass() {
				return BaseResponse.class;
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				dismissLoadingDialog();
			}
		});
		
	}
	
	private void onRegisterSuccess() {
		sharedPreferences.edit()
			.putString(NAME, mAccountName)
			.putString(PASSWORD, mPassword)
			.putString(PHONE, mPhone)
			.commit();
		TongGouApplication.getInstance().setLogin(true);
		MainActivity.ifAutoLogin = false;
		
		GuestVehicleManager manager = new GuestVehicleManager();
		if( manager.isEmpty() ) {	// 本地没有车辆数据直接跳转到首页
			TongGouApplication.showToast("注册完成");
			goHome();
			
		} else { // 本地有车辆数据，则跳转到本地车辆数据绑定界面，使本地车辆与用户绑定
			Intent intent = new Intent();
			intent.setClass(this, RegisterBindLocaleVehicleActivity.class);
			startActivityForResult(intent, REQUEEST_CODE_BIND_LOCAL_VEHICLE);
		}
	}
	
	@Deprecated
	private void requestVehicleSuggestionData() {
		showLoadingDialog("正在加载用户数据");
		QueryVehicleInfoSuggestionRequest request = new QueryVehicleInfoSuggestionRequest();
		if( SomeUtil.isMobileNo(mAccountName) ) {
			request.setApiParams(mAccountName, null);
		} else {
			request.setApiParams(null, mAccountName);
		}
		request.doRequest(this, new AsyncJSONResponseParseHandler<VehicleInfoSuggestionResponse>() {

			@Override
			public void onParseSuccess(VehicleInfoSuggestionResponse result, byte[] originResult) {
				super.onParseSuccess(result, originResult);
				
				if( result.getResult() == null ) {
					goHome();
				} else {
				
					String vehicleNo = "";
					String brandName = "";
					String brandId = "";
					String typeName = "";
					String typeId = "";
					try {
						JSONObject jsonData = result.getResult();
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
			
			@Override
			public void onParseFailure(String errorCode, String errorMsg) {
//				super.onParseFailure(errorCode, errorMsg);
				goHome();
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				super.onFailure(arg0, arg1, arg2, arg3);
				goHome();
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				dismissLoadingDialog();
			}
			
			@Override
			public Class<VehicleInfoSuggestionResponse> getTypeClass() {
				return VehicleInfoSuggestionResponse.class;
			}
			
		});
	}
	
	/**
	 * 由于注册流程改了，所以废弃
	 */
	@Deprecated
	private void turnToChangeVehicleInfoActivity(String vehicleNo, String brandName, String brandId, String typeName, String typeId) {
		Intent intent = new Intent();
		intent.setClass(this, RegisterChangeVehicleInfoActivity.class);
		Bundle args = new Bundle();
		args.putString(RegisterChangeVehicleInfoActivity.KEY_ARG_VEHICLE_NO, vehicleNo);
		args.putString(RegisterChangeVehicleInfoActivity.KEY_ARG_BRAND_NAME, brandName);
		args.putString(RegisterChangeVehicleInfoActivity.KEY_ARG_BRAND_ID, brandId);
		args.putString(RegisterChangeVehicleInfoActivity.KEY_ARG_TYPE_NAME, typeName);
		args.putString(RegisterChangeVehicleInfoActivity.KEY_ARG_TYPE_ID, typeId);
		intent.putExtras(args);
		startActivity(intent);
		finish();
	}
		
	private void goHome() {
		//跳转主页
		MainActivity.ifAutoLogin = true;
		if(sAllActivities!=null){
			while( !sAllActivities.isEmpty()) {
				WeakReference<Activity> reference = sAllActivities.getFirst();
				Activity activity = reference.get();
				if( activity != null && !activity.isFinishing() ) {
					activity.finish();
				}
				sAllActivities.removeFirst();
			}
		}
		
		Activity parentActivity = getParent();
		// 跳转到 HomeActivity 之前先结束 登录 Activity
		if( parentActivity != null ) {
			parentActivity.finish();
		}
		Intent intent = new Intent();
		intent.putExtra(HomePageActivity.EXTRA_FLAG_PREVIOUS_ACTIVITY_NAME, RegisterActivity.class.getName());
		intent.setClass(this, HomePageActivity.class);
		startActivity(intent);	
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( requestCode == REQUEEST_CODE_BIND_LOCAL_VEHICLE ) {
			goHome();
		}
	}
	
}
