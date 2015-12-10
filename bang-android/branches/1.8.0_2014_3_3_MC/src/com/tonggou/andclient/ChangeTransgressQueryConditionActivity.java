package com.tonggou.andclient;

import java.util.List;

import org.apache.http.Header;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.db.VehicleManager;
import com.tonggou.andclient.jsonresponse.BaseResponse;
import com.tonggou.andclient.jsonresponse.VehicleListResponse;
import com.tonggou.andclient.myview.SimpleTitleBar;
import com.tonggou.andclient.network.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.andclient.network.request.QueryVehicleListRequest;
import com.tonggou.andclient.network.request.StoreVehicleInfoRequest;
import com.tonggou.andclient.util.PreferenceUtil;
import com.tonggou.andclient.vo.JuheTransgressArea;
import com.tonggou.andclient.vo.VehicleInfo;

public class ChangeTransgressQueryConditionActivity extends AbsBackableActivity {

	public static final String KEY_PARAM_VEHICLE_INFO = "vehicle_info";
	public static final String KEY_PARAM_SELECTED_CITY = "selected_city";
	
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
			TongGouApplication.showLongToast("请维护发动机号");
			
		} else if( isNeed[1] ) {
			mVehicleVinEdit.requestFocus();
			TongGouApplication.showLongToast("请维护车架号");
			
		} else if( isNeed[2] ) {
			mRegistNoEdit.requestFocus();
			TongGouApplication.showLongToast("请维护车辆登记证书号");
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
		showLoadingDialog("保存中...");
		if( TextUtils.isEmpty(mVehicleInfo.getVehicleId()) ) {
			queryVehicleList(this, mVehicleVin, mEngineNo, mRegistNo);
		} else {
			doSubmit(mVehicleVin, mEngineNo, mRegistNo);
		}
	}

	private void doSubmit(final String vehicleVin, final String engineNo, final String registNo) {
		mVehicleInfo.setVehicleVin(mVehicleVin);
		mVehicleInfo.setEngineNo(mEngineNo);
		mVehicleInfo.setRegistNo(mRegistNo);
		
		if( !TongGouApplication.getInstance().isLogin() ) {
			VehicleManager manager = new VehicleManager();
			if( manager.update(mVehicleInfo) ) {
				onSuccess();
			} else {
				TongGouApplication.showToast("操作失败");
				finish();
			}
			return;
		}
		
		requestUpdateVehicleInfor(vehicleVin, engineNo, registNo);
	}
	
	//保存车辆信息
	private void requestUpdateVehicleInfor( final String vehicleVin, final String engineNo, final String registNo){
		mSubmitBtn.setEnabled(false);
		StoreVehicleInfoRequest request = new StoreVehicleInfoRequest();
		request.setRequestParams(mVehicleInfo.getUserNo(), mVehicleInfo.getVehicleId(),
				mVehicleInfo.getVehicleNo(),  mVehicleInfo.getVehicleBrand(),
				mVehicleInfo.getVehicleModel(), mVehicleInfo, null);
		request.doRequest(this, new AsyncJsonBaseResponseParseHandler<BaseResponse>() {

			@Override
			public void onParseSuccess(BaseResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				updateGlobalVehicleData(mVehicleInfo.getVehicleId(), vehicleVin, engineNo, registNo);
				ChangeTransgressQueryConditionActivity.this.onSuccess();
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				mSubmitBtn.setEnabled(true);
				dismissLoadingDialog();
			}
			
			@Override
			public Class<BaseResponse> getTypeClass() {
				return BaseResponse.class;
			}
			
		});
	}
	
	/**
	 * 请求车辆列表
	 * 
	 * @param userName
	 */
	public void queryVehicleList(Context context, final String vehicleVin, final String engineNo, final String registNo) {

		final String userName = PreferenceUtil.getString(this, BaseActivity.SETTING_INFOS,
				BaseActivity.NAME);
		QueryVehicleListRequest request = new QueryVehicleListRequest();
		request.setApiParams(userName);
		request.doRequest(context, new AsyncJsonBaseResponseParseHandler<VehicleListResponse>() {

			@Override
			public void onParseSuccess(VehicleListResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				List<VehicleInfo> vehicleList = result.getVehicleList();
				TongGouApplication.getInstance().setDefaultVehicleBindOBDs(vehicleList);
				for( VehicleInfo vehicle : vehicleList ) {
					if(mVehicleInfo.getVehicleNo().equals(vehicle.getVehicleNo())) {
						mVehicleInfo.setVehicleId( vehicle.getVehicleId() );
						break;
					}
				}
				
				doSubmit(mVehicleVin, mEngineNo, mRegistNo);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseBody,
					Throwable error) {
				super.onFailure(statusCode, headers, responseBody, error);
				mSubmitBtn.setEnabled(true);
				dismissLoadingDialog();
			}

			@Override
			public Class<VehicleListResponse> getTypeClass() {
				return VehicleListResponse.class;
			}

		});

	}
	
	private void updateGlobalVehicleData(String vehicleId, String vehicleVin, String engineNo, String registNo) {
		if( TongGouApplication.sVehicleList == null ) {
			return;
		}
		final int size = TongGouApplication.sVehicleList.size(); 
		for( int i=0; i<size; i++) {
			VehicleInfo vehicle = TongGouApplication.sVehicleList.get(i);
			if( !vehicleId.equals( vehicle.getVehicleId() ) ) {
				continue;
			}
			if( !TextUtils.isEmpty(vehicleVin) ) {
				vehicle.setVehicleVin(vehicleVin);
			}
			if( !TextUtils.isEmpty(engineNo) ) {
				vehicle.setEngineNo( engineNo);
			}
			if( !TextUtils.isEmpty(registNo) ) {
				vehicle.setRegistNo(registNo);
			}
		}
		
	}

	private void onSuccess() {
		setResult(RESULT_OK);
		finish();
	}
}
