package com.tonggou.andclient;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.tonggou.andclient.jsonresponse.VehicleListResponse;
import com.tonggou.andclient.network.parser.AsyncJSONResponseParseHandler;
import com.tonggou.andclient.network.request.QueryVehicleListRequest;
import com.tonggou.andclient.util.PreferenceUtil;
import com.tonggou.andclient.vo.VehicleInfo;

public class CommonRequestService extends Service {

	public static final String ACTION_GET_VEHICLE_LIST = "com.tonggou.action.getVehicleList";
	public static final String ACTION_GET_VEHICLE_LIST_RESULT = "com.tonggou.action.getVehicleList.result";
	public static final String EXTRA_RESULT_STATE = "ResponseState";
	public static final String EXTRA_VEHICLE_LIST = "VehicleList";
	public static final int RESULT_SUCCESS = 1000;
	public static final int RESULT_PARSING_ERROR = 1001;
	public static final int RESULT_NETWORK_ERROR = 1002;

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
		
		QueryVehicleListRequest request = new QueryVehicleListRequest();
		request.setApiParams(PreferenceUtil.getString(this, BaseActivity.SETTING_INFOS, BaseActivity.NAME));
		final Intent intent = new Intent();
		intent.setAction(ACTION_GET_VEHICLE_LIST_RESULT);
		request.doRequest(this, new AsyncJSONResponseParseHandler<VehicleListResponse>() {
			
			@Override
			public void onParseSuccess(VehicleListResponse result, byte[] originResult) {
				super.onParseSuccess(result, originResult);
				ArrayList<VehicleInfo> vehicleList = new ArrayList<VehicleInfo>();
				vehicleList = result.getVehicleList();
				intent.putExtra(EXTRA_RESULT_STATE, RESULT_SUCCESS);
				intent.putParcelableArrayListExtra(EXTRA_VEHICLE_LIST, vehicleList);
			}
			
			@Override
			public void onParseFailure(String errorCode, String errorMsg) {
				super.onParseFailure(errorCode, errorMsg);
				// ½âÎö³ö´í
				intent.putExtra(EXTRA_RESULT_STATE, RESULT_PARSING_ERROR);
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				sendBroadcast(intent);
				stopSelf();
			}

			@Override
			public Class<VehicleListResponse> getTypeClass() {
				return VehicleListResponse.class;
			}

		});
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
