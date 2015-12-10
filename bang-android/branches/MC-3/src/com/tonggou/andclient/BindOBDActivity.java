package com.tonggou.andclient;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.app.UpdateFaultDic;
import com.tonggou.andclient.jsonresponse.BaseResponse;
import com.tonggou.andclient.myview.AbsViewHolderAdapter;
import com.tonggou.andclient.network.parser.AsyncJSONResponseParseHandler;
import com.tonggou.andclient.network.request.ObdBindingRequest;
import com.tonggou.andclient.vo.OBDDevice;
import com.tonggou.andclient.vo.VehicleInfo;

public class BindOBDActivity extends BaseActivity {
	public final String TAG = "BindOBDActivity";
	private ListView mlvVehicles;
	private ArrayList<VehicleInfo> mVehicleInfos;
	private String mOBDSN;
	private String mVehicleVin;
	private String mUserNo;
	private long startBindingTime;
	private BaseAdapter mVehiclesAdapter;

	private static long TIME_MIN_PROCESS = 2500;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.bind_obd);
		
		receiveData();
		initViews();
	}

	private void receiveData() {
		Intent intent = getIntent();
		if (intent != null) {
			mVehicleInfos = intent
					.getParcelableArrayListExtra(CommonRequestService.EXTRA_VEHICLE_LIST);
			mVehicleVin = intent.getStringExtra(ConnectOBDDialogActivity.EXTRA_VEHICLE_VIN);
			OBDDevice obdDevice = (OBDDevice) intent
					.getSerializableExtra(ConnectOBDDialogActivity.EXTRA_OBD_DEVICE);
			mOBDSN = obdDevice.getDeviceAddress();
			mUserNo = sharedPreferences.getString(BaseActivity.NAME, "");
		}
	}

	private void initViews() {
		mlvVehicles = (ListView) findViewById(R.id.lv_bind_obd_vehicles);
		TextView tvOBDSN = (TextView) findViewById(R.id.tv_bin_obd_sn);
		RelativeLayout rlBack = (RelativeLayout) findViewById(R.id.rl_bind_obd_back);

		tvOBDSN.setText(mOBDSN);
		mVehiclesAdapter = new VehicleAdapter(this, mVehicleInfos, R.layout.bind_obd_vehicle_item);
		mlvVehicles.setAdapter(mVehiclesAdapter);
		mlvVehicles.setOnItemClickListener(mOnItemClickListener);
		rlBack.setOnClickListener(mOnclickListener);
	}
	
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			showAsureDialog((VehicleInfo) mlvVehicles.getItemAtPosition(position));
		}
	};

	private void showAsureDialog(final VehicleInfo vehicleInfo) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("确定绑定该车么？");
		builder.setTitle("友情提示");
		builder.setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				startBindingTime = System.currentTimeMillis();
				dialog.dismiss();
				bindObd(mUserNo, mOBDSN, mVehicleVin, vehicleInfo);
			}
		});

		builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.create().show();
	}

	private OnClickListener mOnclickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.rl_bind_obd_back:
				BindOBDActivity.this.finish();
				break;
			}
		}
	};

	private void bindObd(String userNo, String obdSN, String vehicleVin, final VehicleInfo vehicleInfo) {
		showLoadingDialog("绑定中...");
		ObdBindingRequest request = new ObdBindingRequest();
		request.setRequestParams(userNo, obdSN, vehicleInfo.getVehicleId(), vehicleInfo.getVehicleNo(),
				vehicleVin, vehicleInfo.getVehicleBrand(), vehicleInfo.getVehicleModel(), vehicleInfo,null);
		request.doRequest(this, new AsyncJSONResponseParseHandler<BaseResponse>() {

			@Override
			public void onParseSuccess(BaseResponse result, byte[] originResult) {
				super.onParseSuccess(result, originResult);
				onBindingSuccess(vehicleInfo.getVehicleModelId());
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
	
	private void onBindingSuccess(final String vehicleModelId) {
		UpdateFaultDic.getUpdateFaultDic(BindOBDActivity.this).updateFaultDic(vehicleModelId);
		long timeInterval = System.currentTimeMillis() - startBindingTime;
		if (timeInterval <= TIME_MIN_PROCESS) {
			SystemClock.sleep(TIME_MIN_PROCESS - timeInterval);
		}
		TongGouApplication.showToast("OBD绑定成功！");
		TongGouApplication.getInstance().queryVehicleList();
		finish();
	}
	
	class VehicleAdapter extends AbsViewHolderAdapter<VehicleInfo> {
		public VehicleAdapter(Context context, List<VehicleInfo> data, int layoutRes) {
			super(context, data, layoutRes);
		}

		@Override
		protected void setData(int pos, View convertView, VehicleInfo itemData) {
			TextView brandText = (TextView) getViewFromHolder(convertView, R.id.bind_obd_vehicle_item_brand);
			TextView modelText = (TextView) getViewFromHolder(convertView, R.id.bind_obd_vehicle_item_model);
			TextView vehicleNoText = (TextView) getViewFromHolder(convertView, R.id.bind_obd_vehicle_item_vehicleNo);
			CheckBox isBindCheck = (CheckBox) getViewFromHolder(convertView, R.id.bind_obd_vehicle_item_isBind);
			
			brandText.setText(itemData.getVehicleBrand());
			modelText.setText(itemData.getVehicleModel());
			vehicleNoText.setText(itemData.getVehicleNo());
			isBindCheck.setChecked(!mOBDSN.equals(itemData.getObdSN()) ? false : true);
		}
	}

}
