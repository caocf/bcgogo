package com.tonggou.andclient;

import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.parse.VehicleListParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.vo.VehicleInfo;

public class CommonRequestService extends Service {

	public static final String ACTION_GET_VEHICLE_LIST = "com.tonggou.action.getVehicleList";
	public static final String ACTION_GET_VEHICLE_LIST_RESULT = "com.tonggou.action.getVehicleList.result";
	public static final String EXTRA_RESULT_STATE = "ResponseState";
	public static final String EXTRA_VEHICLE_LIST = "VehicleList";
	public static final int RESULT_SUCCESS = 1000;
	public static final int RESULT_PARSING_ERROR = 1001;
	public static final int RESULT_NETWORK_ERROR = 1002;
	private Context mContext;

	@Override
	public void onCreate() {
		mContext = this;
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null) {
			return -1;
		}
		String action = intent.getAction();
		if (ACTION_GET_VEHICLE_LIST.equals(action)) {
			getVehicleList();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void getVehicleList() {
		new Thread() {
			@Override
			public void run() {
				ArrayList<VehicleInfo> vehicleList = new ArrayList<VehicleInfo>();
				String userNo = getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(
						BaseActivity.NAME, null);
				String url = INFO.HTTP_HEAD + INFO.HOST_IP + "/vehicle/list/userNo/" + userNo;
				VehicleListParser vehicleListParser = new VehicleListParser();
				NetworkState ns = Network.getNetwork(mContext).httpGetUpdateString(url,
						vehicleListParser);
				Intent intent = new Intent();
				intent.setAction(ACTION_GET_VEHICLE_LIST_RESULT);
				if (ns.isNetworkSuccess()) {
					if (vehicleListParser.isSuccessfull()) {
						vehicleList = vehicleListParser.getVehicleListResponse().getVehicleList();
						intent.putExtra(EXTRA_RESULT_STATE, RESULT_SUCCESS);
						intent.putParcelableArrayListExtra(EXTRA_VEHICLE_LIST, vehicleList);
					} else {
						// ½âÎö³ö´í
						intent.putExtra(EXTRA_RESULT_STATE, RESULT_PARSING_ERROR);
					}
				} else {
					// ÍøÂç³ö´í
					intent.putExtra(EXTRA_RESULT_STATE, RESULT_NETWORK_ERROR);
				}
				sendBroadcast(intent);
				stopSelf();
			}
		}.start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
