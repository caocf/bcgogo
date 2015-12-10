package com.tonggou.andclient;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.app.TongGouService;
import com.tonggou.andclient.app.UpdateFaultDic;
import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.parse.CommonParser;
import com.tonggou.andclient.parse.LoginParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.vo.OBDDevice;
import com.tonggou.andclient.vo.VehicleInfo;

public class BindOBDActivity extends BaseActivity {
	private static final String TAG = "BindOBDActivity";
	private ListView mlvVehicles;
	private ArrayList<VehicleInfo> mVehicleInfos;
	private String mOBDSN;
	private String mVehicleVin;
	private String mUserNo;
	private long startBindingTime;

	private static final int MSG_BIND_SUCCEED = 1001;
	private static final int MSG_RESULT_FAILD = 1002;
	private static final int MSG_NETWORK_FAILD = 1003;
	private static long TIME_MIN_PROCESS = 2500;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_BIND_SUCCEED:
				long timeInterval = System.currentTimeMillis() - startBindingTime;
				if (timeInterval <= TIME_MIN_PROCESS) {
					SystemClock.sleep(TIME_MIN_PROCESS - timeInterval);
				}
				TongGouApplication.showToast("OBD绑定成功！");
				TongGouApplication.getInstance().queryVehicleList();
				finish();
				break;
			case MSG_RESULT_FAILD:
				TongGouApplication.showToast((String) msg.obj);
				break;
			case MSG_NETWORK_FAILD:
				TongGouApplication.showToast((String) msg.obj);
				break;
			}
		}
	};

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

		mlvVehicles.setAdapter(mVehiclesAdapter);
		mlvVehicles.setOnItemClickListener(mOnItemClickListener);
		rlBack.setOnClickListener(mOnclickListener);
	}

	private BaseAdapter mVehiclesAdapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = View.inflate(BindOBDActivity.this, R.layout.bind_obd_vehicle_item,
						null);
				viewHolder = new ViewHolder();
				viewHolder.tvBrand = (TextView) convertView
						.findViewById(R.id.bind_obd_vehicle_item_brand);
				viewHolder.tvModel = (TextView) convertView
						.findViewById(R.id.bind_obd_vehicle_item_model);
				viewHolder.tvVehicleNo = (TextView) convertView
						.findViewById(R.id.bind_obd_vehicle_item_vehicleNo);
				viewHolder.chbBind = (CheckBox) convertView
						.findViewById(R.id.bind_obd_vehicle_item_isBind);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			VehicleInfo vehicleInfo = mVehicleInfos.get(position);
			viewHolder.tvBrand.setText(vehicleInfo.getVehicleBrand());
			viewHolder.tvModel.setText(vehicleInfo.getVehicleModel());
			viewHolder.tvVehicleNo.setText(vehicleInfo.getVehicleNo());
			viewHolder.chbBind.setChecked(!mOBDSN.equals(vehicleInfo.getObdSN()) ? false : true);
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return mVehicleInfos.get(position);
		}

		@Override
		public int getCount() {
			return mVehicleInfos.size();
		}
	};

	private class ViewHolder {
		public TextView tvBrand;
		public TextView tvModel;
		public TextView tvVehicleNo;
		public CheckBox chbBind;
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
				TongGouApplication.showToast("正在绑定中，请稍候...");
				new Thread() {
					public void run() {
						bindObd(mUserNo, mOBDSN, mVehicleVin, vehicleInfo);
					};
				}.start();
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

	private void bindObd(String userNo, String obdSN, String vehicleVin, VehicleInfo vehicleInfo) {
		String url = INFO.HTTP_HEAD + INFO.HOST_IP + "/obd/binding";
		CommonParser commonParser = new CommonParser();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("userNo", userNo));
		nameValuePairs.add(new BasicNameValuePair("obdSN", obdSN));
		nameValuePairs.add(new BasicNameValuePair("vehicleVin", vehicleVin));

		nameValuePairs.add(new BasicNameValuePair("vehicleId", vehicleInfo.getVehicleId()));
		nameValuePairs.add(new BasicNameValuePair("vehicleNo", vehicleInfo.getVehicleNo().trim()));

		nameValuePairs
				.add(new BasicNameValuePair("vehicleModelId", vehicleInfo.getVehicleModelId()));
		nameValuePairs.add(new BasicNameValuePair("vehicleModel", vehicleInfo.getVehicleModel()));

		nameValuePairs
				.add(new BasicNameValuePair("vehicleBrandId", vehicleInfo.getVehicleBrandId()));
		nameValuePairs.add(new BasicNameValuePair("vehicleBrand", vehicleInfo.getVehicleBrand()));

		final NetworkState ns = Network.getNetwork(this).httpPostUpdateString(url, nameValuePairs,
				commonParser);
		if (ns.isNetworkSuccess()) {
			if (commonParser.isSuccessfull()) {
				// 正确的处理逻辑
				getCarList();
				UpdateFaultDic.getUpdateFaultDic(BindOBDActivity.this).updateFaultDic(
						vehicleInfo.getVehicleModelId());
				sendMessage(MSG_BIND_SUCCEED, commonParser.getCommonResponse().getMessage());
			} else {
				// 提示用户错误
				Log.d(TAG, "用户错误：" + commonParser.getErrorMessage());
				sendMessage(MSG_RESULT_FAILD, commonParser.getErrorMessage());
			}
		} else {
			// 网络出错
			Log.d(TAG, "网络出错：" + ns.getErrorMessage());
			sendMessage(MSG_NETWORK_FAILD, ns.getErrorMessage());
		}
	}

	private void sendMessage(int what, Object obj) {
		Message msg = Message.obtain(handler, what, obj);
		if (msg != null) {
			msg.sendToTarget();
		}
	}

	private void getCarList() {
		String url = INFO.HTTP_HEAD + INFO.HOST_IP + "/login";
		LoginParser loginParser = new LoginParser();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("userNo", currentUserId));
		nameValuePairs.add(new BasicNameValuePair("password", currentPassWd));
		nameValuePairs.add(new BasicNameValuePair("platform", INFO.MOBILE_PLATFORM));
		nameValuePairs.add(new BasicNameValuePair("appVersion", INFO.VERSION));
		// 可选
		nameValuePairs.add(new BasicNameValuePair("platformVersion",INFO.MOBILE_PLATFORM_VERSION));
		nameValuePairs.add(new BasicNameValuePair("mobileModel",INFO.MOBILE_MODEL));
		nameValuePairs.add(new BasicNameValuePair("imageVersion",INFO.IMAGE_VERSION));

		NetworkState ns = Network.getNetwork(this).httpPostUpdateString(url, nameValuePairs,
				loginParser);
		if (ns.isNetworkSuccess()) {
			if (loginParser.isSuccessfull()) {
				// 保存数据
				TongGouApplication.obdLists = loginParser.getLoginResponse().getObdList();

			}
		}
	}

}
