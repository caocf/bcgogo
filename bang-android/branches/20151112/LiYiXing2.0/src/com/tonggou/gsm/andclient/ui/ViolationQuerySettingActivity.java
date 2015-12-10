package com.tonggou.gsm.andclient.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.UserBaseInfo;
import com.tonggou.gsm.andclient.bean.AppVehicleDTO;
import com.tonggou.gsm.andclient.bean.Area;
import com.tonggou.gsm.andclient.net.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.gsm.andclient.net.request.UpdateVehicleInfoRequest;
import com.tonggou.gsm.andclient.net.response.BaseResponse;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;
import com.tonggou.gsm.andclient.ui.view.VehicleViolationQueryInfoView;
import com.tonggou.gsm.andclient.util.ContextUtil;

/**
 * 违章查询设置界面
 * @author lwz
 *
 */
public class ViolationQuerySettingActivity extends BackableTitleBarActivity {

	public static final String EXTRA_DEST_ACTIVITY_CLASS = "extra_dest_activity_class";
	VehicleViolationQueryInfoView mQueryInfoView;
	private Class<? extends Activity> mDestActivityClass;
 	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_violation_query_setting);
		
		mQueryInfoView = (VehicleViolationQueryInfoView) findViewById(R.id.vehicle_violation_query_info_view);
		AppVehicleDTO vehicleInfo = UserBaseInfo.getVehicleInfo();
		if( !TextUtils.isEmpty(vehicleInfo.getJuheCityCode()) ) {
			mQueryInfoView.setSelectedCity(vehicleInfo.getJuheCityName(), vehicleInfo.getJuheCityCode());
		}
		mQueryInfoView.setVinNo(vehicleInfo.getVehicleVin());
		mQueryInfoView.setEngineNo(vehicleInfo.getEngineNo());
		mQueryInfoView.setRegisterNo(vehicleInfo.getRegistNo());
		mQueryInfoView.setOnSelectCityIndiactorClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onSelectCity();
			}
		});
		
		if( !restoreExtras(getIntent()) ) {
			restoreExtras(savedInstance);
		}
	}
	
	void onSelectCity() {
		ContextUtil.startActivityForResult(this, ViolationAreaSelectActivity.class, ViolationAreaSelectActivity.REQUEST_CODE_SELECT_CITY);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected boolean restoreExtras(Bundle extra) {
		if( extra != null && extra.containsKey(EXTRA_DEST_ACTIVITY_CLASS) ) {
			mDestActivityClass = (Class<? extends Activity>) extra.getSerializable(EXTRA_DEST_ACTIVITY_CLASS);
			return true;
		}
		return false;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(EXTRA_DEST_ACTIVITY_CLASS, mDestActivityClass);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(R.string.title_violation_query_setting);
	}
	
	public void onSubmitBtnClick(View view) {
		if( mQueryInfoView.invalidateValues() ) {
			return;
		}
		doUpdateVehicleInfo(mQueryInfoView.getSelectedCityName(), mQueryInfoView.getSelectedCityCode(), 
				mQueryInfoView.getVinNo(),mQueryInfoView.getRegisterNo(), mQueryInfoView.getEngineNo());
	}
	
	private void doUpdateVehicleInfo(final String cityName, final String cityCode, final String vinNo, final String registerNo, final String engineNo) {
		showLoadingDialog();
		UpdateVehicleInfoRequest request = new UpdateVehicleInfoRequest();
		request.setRequestParams(cityName, cityCode, vinNo, registerNo, engineNo);
		request.doRequest(this, new AsyncJsonBaseResponseParseHandler<BaseResponse>() {

			@Override
			public void onParseSuccess(BaseResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				
				AppVehicleDTO vehicleInfo = UserBaseInfo.getVehicleInfo();
				vehicleInfo.setJuheCityCode(cityCode);
				vehicleInfo.setJuheCityName(cityName);
				vehicleInfo.setVehicleVin(vinNo);
				vehicleInfo.setRegistNo(registerNo);
				vehicleInfo.setEngineNo(engineNo);
				UserBaseInfo.setVehicleInfo(vehicleInfo);
				
				onRequestSuccess();
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
	
	private void onRequestSuccess() {
		if( mDestActivityClass != null ) {
			ContextUtil.startActivity(this, mDestActivityClass);
		}
		// 为了使 违章查询界面重新查询
		setResult(RESULT_OK);
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( requestCode == ViolationAreaSelectActivity.REQUEST_CODE_SELECT_CITY && resultCode == RESULT_OK ) {
			Area area = (Area)data.getParcelableExtra(ViolationAreaSelectActivity.EXTRA_AREA);
			mQueryInfoView.setSelectedCity(area);
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
