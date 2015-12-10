package com.tonggou.andclient;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.app.UpdateFaultDic;
import com.tonggou.andclient.myview.AbsViewHolderAdapter;
import com.tonggou.andclient.myview.SingleListPopupWindow;
import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.parse.CommonParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.vo.VehicleInfo;

public class BindShopActivity extends BaseActivity {
	private static final String TAG = "BindShopActivity";
	private ArrayList<VehicleInfo> mVehicleInfos;
	private SingleListPopupWindow mVehicleSelectPopupWindow;
	private VehicleInfoAdapter mVehicleInfoAdapter;;
	private TextView mTvVehicleNo;
	private Button mRlBind;
	private String mShopName;
	private String mShopId;
	private String mUserNo;
	private VehicleInfo mCurrVehicleInfo;
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
				TongGouApplication.showToast("店铺绑定成功！");
				finish();
				break;
			case MSG_RESULT_FAILD:
				TongGouApplication.showToast((String) msg.obj);
				mRlBind.setEnabled(true);
				break;
			case MSG_NETWORK_FAILD:
				TongGouApplication.showToast((String) msg.obj);
				mRlBind.setEnabled(true);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.bind_shop);
		receiveData();
		initViews();
		initPopup();
	}

	private void receiveData() {
		Intent intent = getIntent();
		if (intent != null) {
			mShopName = intent.getStringExtra(CaptureActivity.ICON_NAMES);
			mShopId = intent.getStringExtra(CaptureActivity.ICON_ID);
			mVehicleInfos = intent
					.getParcelableArrayListExtra(CommonRequestService.EXTRA_VEHICLE_LIST);
			mUserNo = sharedPreferences.getString(BaseActivity.NAME, "");
			Log.d(TAG, "VehicleInfos：" + mVehicleInfos.size());
		}
	}

	private void initViews() {
		mTvVehicleNo = (TextView) findViewById(R.id.tv_bind_shop_vehicle_number);
		final TextView tvShopName = (TextView) findViewById(R.id.tv_bind_shop_shop_name);
		RelativeLayout rlBack = (RelativeLayout) findViewById(R.id.rl_bind_shop_back);
		mRlBind = (Button) findViewById(R.id.btn_bind_shop_save);

		mTvVehicleNo.setOnClickListener(mOnClickListener);
		mRlBind.setOnClickListener(mOnClickListener);
		rlBack.setOnClickListener(mOnClickListener);

		if (mShopName != null) {
			tvShopName.setText(mShopName);
			mCurrVehicleInfo = mVehicleInfos.get(0);
			mTvVehicleNo.setText(mCurrVehicleInfo.getVehicleNo());
		}
	}

	private void initPopup() {
		mVehicleInfoAdapter = new VehicleInfoAdapter(this, mVehicleInfos, R.layout.popview_item);
		mVehicleSelectPopupWindow = new SingleListPopupWindow(this, mVehicleInfoAdapter);
		mVehicleSelectPopupWindow.setOnListItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				VehicleInfo vehicleInfo = mVehicleInfoAdapter.getData().get(position);
				mTvVehicleNo.setText(vehicleInfo.getVehicleNo());
				mCurrVehicleInfo = vehicleInfo;
			}
		});
	}

	private class VehicleInfoAdapter extends AbsViewHolderAdapter<VehicleInfo> {

		public VehicleInfoAdapter(Context context, List<VehicleInfo> data, int layoutRes) {
			super(context, data, layoutRes);
		}

		@Override
		protected void setData(int pos, View convertView, VehicleInfo itemData) {
			TextView tvPop = getViewFromHolder(convertView, R.id.popview_name);
			tvPop.setText(itemData.getVehicleNo());
		}

	}

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_bind_shop_vehicle_number:
				mVehicleSelectPopupWindow.showAsDropDown(mTvVehicleNo);
				break;
			case R.id.rl_bind_shop_back:
				BindShopActivity.this.finish();
				break;
			case R.id.btn_bind_shop_save:
				if (TextUtils.isEmpty(mTvVehicleNo.getText())) {
					TongGouApplication.showToast("请选择一辆车进行绑定");
				} else {
					startBindingTime = System.currentTimeMillis();
					mRlBind.setEnabled(false);
					TongGouApplication.showToast("正在绑定中，请稍候...");
					if (mCurrVehicleInfo.getObdSN() != null) {
						new Thread() {
							public void run() {
								bindObd(mUserNo, mCurrVehicleInfo.getObdSN(),
										mCurrVehicleInfo.getVehicleVin(), mCurrVehicleInfo, mShopId);
							}
						}.start();
					} else {
						new Thread() {
							public void run() {
								bindVehicleShop(mShopId, mCurrVehicleInfo);
							}
						}.start();
					}
				}

				break;
			}
		}
	};

	private void sendMessage(int what, Object obj) {
		Message msg = Message.obtain(handler, what, obj);
		if (msg != null) {
			msg.sendToTarget();
		}
	}

	private void bindObd(String userNo, String obdSN, String vehicleVin, VehicleInfo vehicleInfo,
			String shopId) {
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

		nameValuePairs.add(new BasicNameValuePair("sellShopId", shopId));

		final NetworkState ns = Network.getNetwork(this).httpPostUpdateString(url, nameValuePairs,
				commonParser);
		if (ns.isNetworkSuccess()) {
			if (commonParser.isSuccessfull()) {
				// 更新字典
				UpdateFaultDic.getUpdateFaultDic(this).updateFaultDic(
						vehicleInfo.getVehicleModelId());
				sendMessage(MSG_BIND_SUCCEED, commonParser.getCommonResponse().getMessage());
			} else {
				// 提示用户错误
				sendMessage(MSG_RESULT_FAILD, commonParser.getErrorMessage());
			}
		} else {
			// 网络出错
			sendMessage(MSG_NETWORK_FAILD, ns.getErrorMessage());
		}
	}

	private void bindVehicleShop(String shopId, VehicleInfo vehicleInfo) {

		String url = INFO.HTTP_HEAD + INFO.HOST_IP + "/shop/binding";
		CommonParser commonParser = new CommonParser();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("shopId", shopId));
		nameValuePairs.add(new BasicNameValuePair("vehicleId", vehicleInfo.getVehicleId()));
		final NetworkState ns = Network.getNetwork(this).httpPostUpdateString(url, nameValuePairs,
				commonParser);
		if (ns.isNetworkSuccess()) {
			if (commonParser.isSuccessfull()) {
				// 更新字典
				sendMessage(MSG_BIND_SUCCEED, commonParser.getCommonResponse().getMessage());
			} else {
				// 提示用户错误
				sendMessage(MSG_RESULT_FAILD, commonParser.getErrorMessage());
			}
		} else {
			// 网络出错
			sendMessage(MSG_NETWORK_FAILD, ns.getErrorMessage());
		}
	}

}
