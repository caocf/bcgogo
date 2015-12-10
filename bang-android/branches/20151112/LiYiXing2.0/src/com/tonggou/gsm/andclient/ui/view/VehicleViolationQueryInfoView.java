package com.tonggou.gsm.andclient.ui.view;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.Area;
import com.tonggou.gsm.andclient.bean.type.JuheCityStatus;

public class VehicleViolationQueryInfoView extends LinearLayout {

	private final String KEY_SELECTED_CITY = "KEY_SELECTED_CITY";
	private final String KEY_ENGINE_NO = "KEY_ENGINE_NO";
	private final String KEY_VIN_NO = "KEY_VIN_NO";
	private final String KEY_REGISTER_NO = "KEY_REGISTER_NO";
	
	private IndicatorTextView mSelectedCityIndText;
	private IndicatorEditText mEngineNoIndEidt;
	private IndicatorEditText mVinNoIndEdit;
	
	// NOTE : 需求改变，登记证书号去除.但是原先的设置登记证书号的方法还保留. 
	//private IndicatorEditText mRegisterNoIndEdit;
	
	private Area mSelectedCity;
	
	public VehicleViolationQueryInfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public VehicleViolationQueryInfoView(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		setOrientation(LinearLayout.VERTICAL);
		LayoutInflater inflater = LayoutInflater.from(getContext());
		mSelectedCityIndText = (IndicatorTextView) inflater.inflate(R.layout.widget_base_indicator_text, this, false);
		mSelectedCityIndText.setIndicatorTextValues(R.string.violation_query_city);
		mSelectedCityIndText.getTextView().setHint(R.string.hint_select_query_city);
		addView(mSelectedCityIndText);
		
		mEngineNoIndEidt = (IndicatorEditText) inflater.inflate(R.layout.widget_base_indicator_edit, this, false);
		mEngineNoIndEidt.setIndicatorTextValues(R.string.vehicle_engine_no);
		setEditIndicatorDefaultAttribute( mEngineNoIndEidt );
		addView(mEngineNoIndEidt);
		
		mVinNoIndEdit = (IndicatorEditText) inflater.inflate(R.layout.widget_base_indicator_edit, this, false);
		mVinNoIndEdit.setIndicatorTextValues(R.string.vehicle_frame_no);
		setEditIndicatorDefaultAttribute( mVinNoIndEdit );
		addView(mVinNoIndEdit);
		
//		mRegisterNoIndEdit = (IndicatorEditText) inflater.inflate(R.layout.widget_base_indicator_edit, this, false);
//		mRegisterNoIndEdit.setIndicatorTextValues(R.string.vehicle_register_no);
//		mRegisterNoIndEdit.setBackgroundColor(Color.TRANSPARENT);
//		setEditIndicatorDefaultAttribute( mRegisterNoIndEdit );
//		addView(mRegisterNoIndEdit);
		
		mSelectedCity = new Area();
	}
	
	public void hideSelectCityIndicator() {
		mSelectedCityIndText.setVisibility(View.GONE);
	}
	
	private void setEditIndicatorDefaultAttribute(IndicatorEditText editIndicator) {
		EditText editText = editIndicator.getEditText();
		editText.setKeyListener(new LettersDigitsKeyListener());
		editText.setTransformationMethod(new AllCapTransformationMethod());
		editIndicator.setEditTextMaxLength(getResources().getInteger(R.integer.violation_edit_max_length));
	}

	public void setOnSelectCityIndiactorClickListener(View.OnClickListener l) {
		mSelectedCityIndText.setOnClickListener(l);
	}
	
	public void setSelectedCity(Area area) {
		if( area != null ) {
			mSelectedCity = area;
		} else {
			mSelectedCity = new Area();
		}
		mSelectedCityIndText.setTextValue(mSelectedCity.getName()); 
	}
	
	public void setSelectedCity(String juheCityName, String juheCityCode) {
		mSelectedCity = new Area();
		mSelectedCity.setCode(juheCityCode);
		mSelectedCity.setName(juheCityName);
		mSelectedCity.setJuheStatus(JuheCityStatus.ACTIVE);
		mSelectedCity.setChildren(new ArrayList<Area>());
		mSelectedCityIndText.setTextValue(juheCityName);
	}
	
	public String getSelectedCityCode() {
		return mSelectedCity.getCode();
	}
	
	public String getSelectedCityName() {
		return mSelectedCity.getName();
	}
	
	public void setEngineNo(String engineNo) {
		mEngineNoIndEidt.setEditTextValue(engineNo);
	}
	
	public String getEngineNo() {
		return mEngineNoIndEidt.getEditTextValue();
	}
	
	public void setVinNo(String vinNo) {
		mVinNoIndEdit.setEditTextValue(vinNo);
	}
	
	public String getVinNo() {
		return mVinNoIndEdit.getEditTextValue();
	}
	
	public void setRegisterNo(String registerNo) {
//		mRegisterNoIndEdit.setEditTextValue(registerNo);
	}
	
	public String getRegisterNo() {
//		return mRegisterNoIndEdit.getEditTextValue();
		return "NULL";
	}
	
	public boolean invalidateValues() {
		if( TextUtils.isEmpty( getSelectedCityCode())) {
			App.showShortToast(getResources().getString(R.string.txt_info_query_city_donot_empty));
			return true;
		}
		if( TextUtils.isEmpty( getEngineNo())) {
			App.showShortToast(getResources().getString(R.string.txt_info_vehicle_engin_no_donot_empty));
			return true;
		}
		if( TextUtils.isEmpty( getVinNo())) {
			App.showShortToast(getResources().getString(R.string.txt_info_vehicle_frame_no_donot_empty));
			return true;
		}
		if( TextUtils.isEmpty( getRegisterNo())) {
			App.showShortToast(getResources().getString(R.string.txt_info_register_no_donot_empty));
			return true;
		}
		return false;
	}
	
	public Bundle onStoreInstance() {
		Bundle bundle = new Bundle();
		bundle.putParcelable(KEY_SELECTED_CITY, mSelectedCity);
		bundle.putString(KEY_ENGINE_NO, mEngineNoIndEidt.getEditTextValue());
		bundle.putString(KEY_VIN_NO, mVinNoIndEdit.getEditTextValue());
//		bundle.putString(KEY_REGISTER_NO, mRegisterNoIndEdit.getEditTextValue());
		bundle.putString(KEY_REGISTER_NO, "NULL");
		return bundle;
	}
	
	public void onRestoreInstance(Bundle savedInstance) {
		if( savedInstance == null ) {
			return;
		}
		setSelectedCity((Area)savedInstance.getParcelable(KEY_SELECTED_CITY));
		setEngineNo(savedInstance.getString(KEY_ENGINE_NO) );
		setVinNo(savedInstance.getString(KEY_VIN_NO) );
		setRegisterNo(savedInstance.getString(KEY_REGISTER_NO) );
	}
	
}
