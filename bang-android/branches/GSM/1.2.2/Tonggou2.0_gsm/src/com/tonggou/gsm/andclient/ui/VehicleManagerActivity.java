package com.tonggou.gsm.andclient.ui;

import org.joda.time.DateTime;

import android.text.TextUtils;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.UserBaseInfo;
import com.tonggou.gsm.andclient.bean.AppVehicleDTO;
import com.tonggou.gsm.andclient.net.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.gsm.andclient.net.request.UpdateVehicleInfoRequest;
import com.tonggou.gsm.andclient.net.response.BaseResponse;
import com.tonggou.gsm.andclient.util.StringUtil;

/**
 * 车辆管理 页面
 * @author lwz
 *
 */
public class VehicleManagerActivity extends AbsVehicleManagerActivity {

	@Override
	void restoreIntentExtras() {
		AppVehicleDTO vehicleInfo = UserBaseInfo.getVehicleInfo();
		mVehicleNoIndicatorText.setTextValue(vehicleInfo.getVehicleNo());
		mVehicleModelIndicatorText.setTextValue(getVehicleBrandModle(vehicleInfo.getVehicleBrand(), vehicleInfo.getVehicleModel()));
		mOilPriceIndicatorEdit.setEditTextValue(vehicleInfo.getOilPrice());
		mCurrentMileageIndicatorEdit.setEditTextValue(String.valueOf(vehicleInfo.getCurrentMileage()));
		mMaintainPeriodIndicatorEdit.setEditTextValue(String.valueOf(vehicleInfo.getMaintainPeriod()));
		mLastMaintainMileageIndicatorEdit.setEditTextValue(String.valueOf(vehicleInfo.getLastMaintainMileage()));
		mNextMaintainTimeIndicatorText.setTextValue(getDate(vehicleInfo.getNextExamineTime()));
		mNextExaminationTimeIndicatorText.setTextValue(getDate(vehicleInfo.getNextMaintainTime()));
		
		initViolationInfo(vehicleInfo);
	}

	@Override
	void onTitleBarRightBtnClick() {
		doUpdateRequest(mVehicleNoIndicatorText.getTextValue(), mOilPriceIndicatorEdit.getEditTextValue(), mCurrentMileageIndicatorEdit.getEditTextValue(), 
				mMaintainPeriodIndicatorEdit.getEditTextValue(), mLastMaintainMileageIndicatorEdit.getEditTextValue(), 
				mNextMaintainTimeIndicatorText.getTextValue(), mNextExaminationTimeIndicatorText.getTextValue(),
				mViolationQueryInfo.getSelectedCityName(), mViolationQueryInfo.getSelectedCityCode(),
				mViolationQueryInfo.getVinNo(), mViolationQueryInfo.getRegisterNo(), mViolationQueryInfo.getEngineNo());
	}
	
	/**
	 * 更新请求
	 * @param oilPrice 油价, not null
	 * @param currentMileage 当前里程, not null
	 * @param maintainPeriod 保养周期, not null
	 * @param lastMaintainMileage 上次保养里程
	 * @param nextMaintainTime 下次保养时间
	 * @param nextExamineTime 下次验车时间
	 * @param vehicleVin 车架号
	 * @param registNo 登记证书号
	 * @param engineNo 发动机号
	 */
	private void doUpdateRequest(final String vehicleNo, final String oilPrice, final String currentMileage, final String maintainPeriod,
			final String lastMaintainMileage, final String nextMaintainTime, final String nextExamineTime,
			final String juheCityName, final String juheCityCode,
			final String vehilceVin, final String registNo, final String engineNo) {
		
		showLoadingDialog();
		UpdateVehicleInfoRequest request = new UpdateVehicleInfoRequest();
		request.setRequestParams(vehicleNo, oilPrice, currentMileage, maintainPeriod, 
				lastMaintainMileage, nextMaintainTime, nextExamineTime, 
				juheCityName, juheCityCode,
				vehilceVin, registNo, engineNo);
		request.doRequest(this, new AsyncJsonBaseResponseParseHandler<BaseResponse>() {

			@Override
			public void onParseSuccess(BaseResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				AppVehicleDTO vehicleInfo = UserBaseInfo.getVehicleInfo();
				vehicleInfo.setVehicleNo(vehicleNo);
				vehicleInfo.setOilPrice(oilPrice);
				vehicleInfo.setCurrentMileage(Integer.valueOf(currentMileage));
				vehicleInfo.setMaintainPeriod(Integer.valueOf(maintainPeriod));
				vehicleInfo.setLastMaintainMileage(0);
				if( !TextUtils.isEmpty(lastMaintainMileage) ) { 
					vehicleInfo.setLastMaintainMileage(Integer.valueOf(lastMaintainMileage));
				}
				if( !TextUtils.isEmpty(nextExamineTime) ) {
					DateTime time = formatDateTimeYYYYHHddHHmm(nextExamineTime);
					vehicleInfo.setNextExamineTime(time.getMillis());
					vehicleInfo.setNextExamineTimeStr(nextExamineTime);
				}
				if( !TextUtils.isEmpty(nextMaintainTime) ) {
					DateTime time = formatDateTimeYYYYHHddHHmm(nextMaintainTime);
					vehicleInfo.setNextMaintainTime(time.getMillis());
					vehicleInfo.setNextMaintainTimeStr(nextMaintainTime);
				}
				vehicleInfo.setJuheCityCode(juheCityCode);
				vehicleInfo.setJuheCityName(juheCityName);
				vehicleInfo.setEngineNo(engineNo);
				vehicleInfo.setVehicleVin(vehilceVin);
				vehicleInfo.setRegistNo(registNo);
				UserBaseInfo.setVehicleInfo(vehicleInfo);
				App.showShortToast(result.getMessage());
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				dismissLoadingDialog();
			}
			
			@Override
			public Class<BaseResponse> getTypeClass() {
				return BaseResponse.class;
			}
			
		});
	}
	
	private String getDate(long mills) {
		if( mills <= 0 ) {
			return "";
		}
		return new DateTime(mills).toString(StringUtil.DATE_FORMAT_DATE_TIME_YYYY_MM_dd_HH_mm);
	}
	
	@Override
	protected void onVehicleNoChanged() {
		performTitlebarRightBtnClick();
	}
	
}
