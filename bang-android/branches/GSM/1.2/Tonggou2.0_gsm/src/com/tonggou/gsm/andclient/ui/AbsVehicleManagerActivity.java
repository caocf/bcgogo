package com.tonggou.gsm.andclient.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.AppVehicleDTO;
import com.tonggou.gsm.andclient.bean.Area;
import com.tonggou.gsm.andclient.ui.view.IndicatorEditText;
import com.tonggou.gsm.andclient.ui.view.IndicatorTextView;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;
import com.tonggou.gsm.andclient.ui.view.VehicleViolationQueryInfoView;
import com.tonggou.gsm.andclient.util.ContextUtil;
import com.tonggou.gsm.andclient.util.StringUtil;

/**
 * 车辆管理界面
 * @author lwz
 *
 */
public abstract class AbsVehicleManagerActivity extends DateTimePickerBackableActivity {
	
	private final int REQUEST_CODE_EDIT_VEHICLE_NO = 0x222;
	
	final String EXTRA_VEHICLE_NO = "extra_vehicle_no";
	final String EXTRA_VEHICLE_MODEL = "extra_vehicle_model";
	final String EXTRA_OIL_PRICE = "extra_oil_price";
	final String EXTRA_CURRENT_MILEAGE = "extra_current_mileage";
	final String EXTRA_MATINTAIN_PERIOD = "extra_matintain_cycle";
	final String EXTRA_LAST_MAINTAIN_MILEAGE = "extra_last_maintain_mileage";
	final String EXTRA_NEXT_MAINTAIN_TIME = "extra_next_maintain_time";
	final String EXTRA_NEXT_EXAMINATION_TIME = "extra_next_examination_time";
	
	// 必填
	IndicatorTextView mVehicleNoIndicatorText;				// 车牌
	IndicatorTextView mVehicleModelIndicatorText;			// 车型
	IndicatorEditText mOilPriceIndicatorEdit;				// 油价
	IndicatorEditText mCurrentMileageIndicatorEdit;			// 当前里程
	IndicatorEditText mMaintainPeriodIndicatorEdit;			// 保养周期
	// 选填
	IndicatorEditText mLastMaintainMileageIndicatorEdit;	// 上次保养里程
	IndicatorTextView mNextMaintainTimeIndicatorText;		// 下次保养时间
	IndicatorTextView mNextExaminationTimeIndicatorText;	// 下次验车时间
	VehicleViolationQueryInfoView mViolationQueryInfo;
	
	DatePickerDialog mDatePickerDialog;
	
	int mOilpriceMinValue = 5;
	int mOilpriceMaxValue = 15;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		
		setContentView(R.layout.activity_vehicle_manager);
		mVehicleNoIndicatorText = (IndicatorTextView) findViewById(R.id.vehicle_no_indicator_text);
		mVehicleModelIndicatorText = (IndicatorTextView) findViewById(R.id.vehicle_model_indicator_text);
		mOilPriceIndicatorEdit = (IndicatorEditText) findViewById(R.id.oil_price_indicator_edit);
		mCurrentMileageIndicatorEdit = (IndicatorEditText) findViewById(R.id.current_mileage_indicator_edit);
		mMaintainPeriodIndicatorEdit = (IndicatorEditText) findViewById(R.id.maintain_period_indicator_edit);
		
		mLastMaintainMileageIndicatorEdit = (IndicatorEditText) findViewById(R.id.last_maintain_mileage_indicator_edit);
		mNextMaintainTimeIndicatorText = (IndicatorTextView) findViewById(R.id.next_maintain_time_indicator_text);
		mNextExaminationTimeIndicatorText = (IndicatorTextView) findViewById(R.id.next_examination_time_indicator_text);
		mViolationQueryInfo = (VehicleViolationQueryInfoView) findViewById(R.id.vehicle_violation_query_info_view);
		
		mOilPriceIndicatorEdit.requestFocus();
		mOilpriceMinValue = getResources().getInteger(R.integer.oilprice_min_value);
		mOilpriceMaxValue = getResources().getInteger(R.integer.oilprice_max_value);
		mOilPriceIndicatorEdit.getEditText().setHint(
				getString(R.string.txt_hint_oil_price_range, mOilpriceMinValue, mOilpriceMaxValue));
		
		mVehicleNoIndicatorText.setImageIndicator(R.drawable.ic_edit, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doEditVehicleNo();
			}
		});
		mNextMaintainTimeIndicatorText.setImageIndicator(R.drawable.ic_calendar,
				new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDatePickerDialog(mNextMaintainTimeIndicatorText);
			}
		});
		mNextExaminationTimeIndicatorText.setImageIndicator(R.drawable.ic_calendar, 
				new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDatePickerDialog(mNextExaminationTimeIndicatorText);
			}
		});
		
		mViolationQueryInfo.setOnSelectCityIndiactorClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doSelectCity();
			}
		});
		
		if(!restoreExtras(savedInstance)) {
			restoreIntentExtras();
		}
	}
	
	protected void initViolationInfo(AppVehicleDTO vehicleInfo) {
		mViolationQueryInfo.setSelectedCity(vehicleInfo.getJuheCityName(), vehicleInfo.getJuheCityCode());
		mViolationQueryInfo.setEngineNo(vehicleInfo.getEngineNo());
		mViolationQueryInfo.setVinNo(vehicleInfo.getVehicleVin());
		mViolationQueryInfo.setRegisterNo(vehicleInfo.getRegistNo());
	}
	
	abstract void restoreIntentExtras(); 
	
	@Override
	protected boolean restoreExtras(Bundle extra) {
		if( isBundleEmpty(extra) || !extra.containsKey(EXTRA_VEHICLE_NO)) {
			return false;
		}
		
		mVehicleNoIndicatorText.setTextValue(extra.getString(EXTRA_VEHICLE_NO));
		mVehicleModelIndicatorText.setTextValue(extra.getString(EXTRA_VEHICLE_MODEL));
		mOilPriceIndicatorEdit.setEditTextValue(extra.getString(EXTRA_OIL_PRICE));
		int currentMileage = Integer.valueOf(extra.getString(EXTRA_CURRENT_MILEAGE));
		mCurrentMileageIndicatorEdit.setEditTextValue( currentMileage <= 0 ? "" : String.valueOf(currentMileage) );
		mMaintainPeriodIndicatorEdit.setEditTextValue(extra.getString(EXTRA_MATINTAIN_PERIOD));
		mLastMaintainMileageIndicatorEdit.setEditTextValue(extra.getString(EXTRA_LAST_MAINTAIN_MILEAGE));
		mNextMaintainTimeIndicatorText.setTextValue(extra.getString(EXTRA_NEXT_MAINTAIN_TIME));
		mNextExaminationTimeIndicatorText.setTextValue(extra.getString(EXTRA_NEXT_EXAMINATION_TIME));
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(EXTRA_VEHICLE_NO, mVehicleNoIndicatorText.getTextValue());
		outState.putString(EXTRA_VEHICLE_MODEL, mVehicleModelIndicatorText.getTextValue());
		outState.putString(EXTRA_OIL_PRICE, mOilPriceIndicatorEdit.getEditTextValue());
		outState.putString(EXTRA_CURRENT_MILEAGE, mCurrentMileageIndicatorEdit.getEditTextValue());
		outState.putString(EXTRA_MATINTAIN_PERIOD, mMaintainPeriodIndicatorEdit.getEditTextValue());
		outState.putString(EXTRA_LAST_MAINTAIN_MILEAGE, mLastMaintainMileageIndicatorEdit.getEditTextValue());
		outState.putString(EXTRA_NEXT_MAINTAIN_TIME, mNextMaintainTimeIndicatorText.getTextValue());
		outState.putString(EXTRA_NEXT_EXAMINATION_TIME, mNextExaminationTimeIndicatorText.getTextValue());
		outState.putAll(mViolationQueryInfo.onStoreInstance());
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mViolationQueryInfo.onRestoreInstance(savedInstanceState);
	}
	
	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(R.string.title_vehicle_manager);
		titleBar.setRightButton(getString(R.string.btn_confirm), Color.TRANSPARENT)
				.setOnRightButtonClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if( !invidateInput() ) {
							onTitleBarRightBtnClick();
						}
						
					}
				});
	}
	
	abstract void onTitleBarRightBtnClick();
	
	protected boolean invidateInput() {
		return invalidateText(mOilPriceIndicatorEdit.getEditText(), R.string.txt_info_oil_price_donot_empty)
				|| invalidateOilPriceRange()
				|| invalidateText(mCurrentMileageIndicatorEdit.getEditText(), R.string.txt_info_current_mileage_donot_empty)
				|| invalidateText(mMaintainPeriodIndicatorEdit.getEditText(), R.string.txt_info_maintain_period_donot_empty)
				|| isCurrentMileageLessEqualZero();
	}
	
	private boolean invalidateOilPriceRange() {
		boolean flag = StringUtil.validateOilPrice(mOilPriceIndicatorEdit.getEditTextValue(), mOilpriceMinValue, mOilpriceMaxValue);
		if( !flag ) {
			App.showShortToast(getString(R.string.txt_info_oil_price_out_of_range));
		}
		return !flag;
	}
	
	/**
	 * 当前里程是否小于等于 0
	 * @return
	 */
	private boolean isCurrentMileageLessEqualZero() {
		String currentMileageStr = mCurrentMileageIndicatorEdit.getEditTextValue();
		if( Integer.valueOf(currentMileageStr) <= 0 ) {
			App.showShortToast(getString(R.string.txt_info_current_mileage_too_low));
			return true;
		}
 		return false;
	}
	
	/**
	 * 通过车辆的品牌车型来得到组合的字符串
	 * @param brand	品牌
	 * @param modle 车型
	 * @return
	 */
	protected String getVehicleBrandModle(String brand, String modle) {
		return brand + " - " + modle;
	}
	
	private void doSelectCity() {
		ContextUtil.startActivityForResult(this, ViolationAreaSelectActivity.class, ViolationAreaSelectActivity.REQUEST_CODE_SELECT_CITY);
	}
	
	private void doEditVehicleNo() {
		Bundle args = new Bundle();
		args.putString(EditVehicleNoActivity.EXTRA_VEHICLE_NO, mVehicleNoIndicatorText.getTextValue());
		ContextUtil.startActivityForResult(this, EditVehicleNoActivity.class, REQUEST_CODE_EDIT_VEHICLE_NO, args);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( resultCode == RESULT_OK  ) {
			if( requestCode == ViolationAreaSelectActivity.REQUEST_CODE_SELECT_CITY ) {
				Area area = (Area)data.getParcelableExtra(ViolationAreaSelectActivity.EXTRA_AREA);
				mViolationQueryInfo.setSelectedCity(area);
				return;
			} else if( requestCode == REQUEST_CODE_EDIT_VEHICLE_NO ){
				mVehicleNoIndicatorText.setTextValue(data.getStringExtra(EditVehicleNoActivity.EXTRA_RESULT_DATA_VEHICLE_NO));
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
