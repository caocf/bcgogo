package com.tonggou.gsm.andclient.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.AppVehicleDTO;
import com.tonggou.gsm.andclient.net.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.gsm.andclient.net.request.RegisterValidateRequest;
import com.tonggou.gsm.andclient.net.response.RegisterValidateResponse;
import com.tonggou.gsm.andclient.ui.view.DrawableRightClickableEditText;
import com.tonggou.gsm.andclient.ui.view.DrawableRightClickableEditText.DrawableRightClickListener;
import com.tonggou.gsm.andclient.util.ContextUtil;
import com.tonggou.gsm.andclient.util.StringUtil;

/** 
 * 注册信息验证页面
 * @author lwz
 *
 */
public class RegisterValidateActivity extends BackableTitleBarActivity {

	private final int REQUEST_CODE_SCAN_IMEI = 0x111;
	
	private final String EXTRA_PHONE_NO = "extra_phone_no";
	private final String EXTRA_PWD = "extra_pwd";
	private final String EXTRA_IMEI = "extra_imei";
	
	private EditText mPhoneNoEdit;
	private EditText mPasswordEdit;
	private DrawableRightClickableEditText mIMEIEditText;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_register);
		getTitleBar().setTitle(R.string.title_register);
		
		mPhoneNoEdit = (EditText) findViewById(R.id.phoneNo_editText);
		mPasswordEdit = (EditText) findViewById(R.id.pwd_editText);
		mIMEIEditText = (DrawableRightClickableEditText) findViewById(R.id.imei_editText);
		mPhoneNoEdit.setHintTextColor(getResources().getColor(R.color.holo_blue));
		mPasswordEdit.setHintTextColor(getResources().getColor(R.color.holo_blue));
		mIMEIEditText.setHintTextColor(getResources().getColor(R.color.holo_blue));
		mIMEIEditText.setOnDrawableRightClickListener(new DrawableRightClickListener() {
			
			@Override
			public void onClick() {
				scanQRCode();
			}
		});
		
		restoreExtras(savedInstance);
	}
	
	@Override
	protected boolean restoreExtras(Bundle extra) {
		if( isBundleEmpty(extra) || !( extra.containsKey(EXTRA_IMEI)
				&& extra.containsKey(EXTRA_PHONE_NO) && extra.containsKey(EXTRA_PWD))) {
			return false;
		}
		mPhoneNoEdit.setText(extra.getString(EXTRA_PHONE_NO));
		mPasswordEdit.setText(extra.getString(EXTRA_PWD));
		mIMEIEditText.setText(extra.getString(EXTRA_IMEI));
		return true;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(EXTRA_PHONE_NO, mPhoneNoEdit.getText().toString());
		outState.putString(EXTRA_PWD, mPasswordEdit.getText().toString());
		outState.putString(EXTRA_IMEI, mIMEIEditText.getText().toString());
		super.onSaveInstanceState(outState);
	}
	
	private void scanQRCode() {
		ContextUtil.startActivityForResult(this, ScanIMEICodeActivity.class, REQUEST_CODE_SCAN_IMEI);
	}
	
	public void onNextStepBtnClick(View view) {
		
		if( invalidateText(mIMEIEditText, R.string.info_please_input_imei)
				|| invalidateText(mPhoneNoEdit, R.string.info_please_input_phone_no)
				|| invalidateText(mPasswordEdit, R.string.info_please_input_pwd_no) ) {
			return;
		}
		
		if( !StringUtil.isIMEI(getText(mIMEIEditText))) {
			App.showShortToast(getString(R.string.txt_info_imei_invalidate));
			return;
		}
		
		if( !StringUtil.isPhoneNo(getText(mPhoneNoEdit))) {
			App.showShortToast(getString(R.string.txt_info_phone_invalidate));
			return;
		} 
		
		doValidateRegister(getText(mPhoneNoEdit), getText(mPasswordEdit), getText(mIMEIEditText));
	}
	
	private void doValidateRegister(final String phoneNo, final String password, final String imei) {
		showLoadingDialog();
		RegisterValidateRequest request = new RegisterValidateRequest();
		request.setRequestParams(phoneNo, password, imei);
		request.doRequest(this, new AsyncJsonBaseResponseParseHandler<RegisterValidateResponse>() {

			@Override
			public void onParseSuccess(RegisterValidateResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				
				toRegisterValidateVehicleInfoActivity(phoneNo,
						password, imei, result.getAppVehicleDTO());
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				dismissLoadingDialog();
			}
			
			@Override
			public Class<RegisterValidateResponse> getTypeClass() {
				return RegisterValidateResponse.class;
			}
			
		});
	}
	
	private void toRegisterValidateVehicleInfoActivity(final String phoneNo, final String password, final String imei, final AppVehicleDTO vehicleInfo) {
		Bundle args = new Bundle();
		args.putString(RegisterValidateVehicleInfoActivity.EXTRA_PHONE_NO, phoneNo);
		args.putString(RegisterValidateVehicleInfoActivity.EXTRA_PWD, password);
		args.putString(RegisterValidateVehicleInfoActivity.EXTRA_IMEI, imei);
		args.putSerializable(RegisterValidateVehicleInfoActivity.EXTRA_VEHICLE_INFO, vehicleInfo);
		ContextUtil.startActivityForResult(
				this, RegisterValidateVehicleInfoActivity.class, LoginActivity.REQUEST_CODE_REGISTER, args);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( resultCode != RESULT_OK )
			return;
		
		if( requestCode == REQUEST_CODE_SCAN_IMEI ) {
			if( data != null && data.hasExtra(ScanIMEICodeActivity.EXTRA_SCAN_RESULT_STR)) {
				String str = data.getStringExtra(ScanIMEICodeActivity.EXTRA_SCAN_RESULT_STR);
				mIMEIEditText.setText(str);
				mIMEIEditText.setSelection(str.length());
			}
			return;
		}
		// 说明注册成功，通知 LoginActivity 跳转到 MainActivity
		if( requestCode == LoginActivity.REQUEST_CODE_REGISTER ) {
			setResult(RESULT_OK);
			finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
