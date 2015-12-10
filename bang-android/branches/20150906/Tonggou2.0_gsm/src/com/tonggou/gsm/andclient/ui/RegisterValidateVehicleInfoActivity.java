package com.tonggou.gsm.andclient.ui;


import android.content.Intent;
import android.os.Bundle;

import com.tonggou.gsm.andclient.UserBaseInfo;
import com.tonggou.gsm.andclient.bean.AppVehicleDTO;
import com.tonggou.gsm.andclient.net.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.gsm.andclient.net.request.RegisterRequest;
import com.tonggou.gsm.andclient.net.response.LoginResponse;

/**
 * 注册验证车辆信息页面
 * @author lwz
 *
 */
public class RegisterValidateVehicleInfoActivity extends AbsVehicleManagerActivity {

	public static final String EXTRA_IMEI = "extra_imei";
	public static final String EXTRA_PHONE_NO = "extra_phone_no";
	public static final String EXTRA_PWD = "extra_pwd";
	public static final String EXTRA_VEHICLE_INFO = "extra_vehicle_info";
	
	private String mPhoneNoStr;
	private String mPasswordStr;
	private String mIMEIStr;
	private AppVehicleDTO mVehicleInfo;
	
	@Override
	void restoreIntentExtras() {
		Intent intent = getIntent();
		if( intent == null ) {
			return;
		}
		restoreIntentBundleExtras(intent.getExtras());
	}
	
	@Override
	protected boolean restoreExtras(Bundle extra) {
		if( !isBundleEmpty(extra) && extra.containsKey(EXTRA_IMEI)) {
			mPhoneNoStr = extra.getString(EXTRA_PHONE_NO);
			mPasswordStr = extra.getString(EXTRA_PWD);
			mIMEIStr = extra.getString(EXTRA_IMEI);
			mVehicleInfo = (AppVehicleDTO) extra.getSerializable(EXTRA_VEHICLE_INFO);
			initViolationInfo(mVehicleInfo);
		}
		return super.restoreExtras(extra);
	}
	
	private void restoreIntentBundleExtras( Bundle extra ) {
		if( isBundleEmpty(extra) || !extra.containsKey(EXTRA_IMEI) ) {
			return;
		}
		mPhoneNoStr = extra.getString(EXTRA_PHONE_NO);
		mPasswordStr = extra.getString(EXTRA_PWD);
		mIMEIStr = extra.getString(EXTRA_IMEI);
		mVehicleInfo = (AppVehicleDTO) extra.getSerializable(EXTRA_VEHICLE_INFO);
		
		initVehicleInfo(extra, mVehicleInfo);
		restoreExtras(extra);
	}
	
	private void initVehicleInfo(Bundle extra, AppVehicleDTO vehicleInfo) {
		extra.putString(EXTRA_VEHICLE_NO, mVehicleInfo.getVehicleNo());
		extra.putString(EXTRA_VEHICLE_MODEL,  getVehicleBrandModle( mVehicleInfo.getVehicleBrand(), mVehicleInfo.getVehicleModel()));
		extra.putString(EXTRA_CURRENT_MILEAGE, String.valueOf((int)mVehicleInfo.getCurrentMileage()));
		extra.putString(EXTRA_MATINTAIN_PERIOD, String.valueOf((int)mVehicleInfo.getMaintainPeriod() == 0 ? 5000 : (int)mVehicleInfo.getMaintainPeriod() ));
		extra.putString(EXTRA_LAST_MAINTAIN_MILEAGE, String.valueOf((int)mVehicleInfo.getLastMaintainMileage()));
		extra.putString(EXTRA_NEXT_MAINTAIN_TIME,mVehicleInfo.getNextMaintainTimeStr());
		extra.putString(EXTRA_NEXT_EXAMINATION_TIME,  mVehicleInfo.getNextExamineTimeStr());
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(EXTRA_PHONE_NO, mPhoneNoStr);
		outState.putString(EXTRA_IMEI, mIMEIStr);
		outState.putString(EXTRA_PWD, mPasswordStr);
		outState.putSerializable(EXTRA_VEHICLE_INFO, mVehicleInfo);
		super.onSaveInstanceState(outState);
	}

	@Override
	void onTitleBarRightBtnClick() {
		doRegisterRequest(mPhoneNoStr, mPasswordStr, mIMEIStr, mVehicleNoIndicatorText.getTextValue(),
				mOilPriceIndicatorEdit.getEditTextValue(), mCurrentMileageIndicatorEdit.getEditTextValue(), 
				mMaintainPeriodIndicatorEdit.getEditTextValue(), mLastMaintainMileageIndicatorEdit.getEditTextValue(), 
				mNextMaintainTimeIndicatorText.getTextValue(), mNextExaminationTimeIndicatorText.getTextValue(),
				mViolationQueryInfo.getSelectedCityName(), mViolationQueryInfo.getSelectedCityCode(), 
				mViolationQueryInfo.getVinNo(), mViolationQueryInfo.getRegisterNo(), mViolationQueryInfo.getEngineNo());
	}

	/**
	 * 注册请求
	 * @param phoneNo 手机号, not null
	 * @param pwd 密码, 	not null
	 * @param imei IMEI 号, not null
	 * @param oilPrice 油价, not null
	 * @param currentMileage 当前里程, not null
	 * @param maintainPeriod 保养周期, not null
	 * @param lastMaintainMileage 上次保养里程
	 * @param nextMaintainTime 下次保养时间
	 * @param nextExamineTime 下次验车时间
	 */
	private void doRegisterRequest(final String phoneNo, final String pwd, final String imei, final String vehicleNo,
			final String oilPrice, final String currentMileage, final String maintainPeriod,
			final String lastMaintainMileage, final String nextMaintainTime, final String nextExamineTime,
			final String juheCityName, final String juheCityCode, 
			final String vehilceVin, final String registNo, final String engineNo) {
		showLoadingDialog();
		RegisterRequest request = new RegisterRequest();
		request.setRequestParams(phoneNo, pwd, imei, vehicleNo, oilPrice, currentMileage, maintainPeriod,
				lastMaintainMileage, nextMaintainTime, nextExamineTime, 
				juheCityName, juheCityCode, vehilceVin, registNo, engineNo);
		request.doRequest(this, new AsyncJsonBaseResponseParseHandler<LoginResponse>() {

			@Override
			public void onParseSuccess(LoginResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				UserBaseInfo.setInfos(result);
				onRegisterSuccess();
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				dismissLoadingDialog();
			}
			
			@Override
			public Class<LoginResponse> getTypeClass() {
				return LoginResponse.class;
			}
			
		});
	}
	
	private void onRegisterSuccess() {
		setResult(RESULT_OK);
		finish();
	}
}
