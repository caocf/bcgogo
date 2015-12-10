package com.tonggou.gsm.andclient.service;

import org.apache.http.Header;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.Constants;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.UserBaseInfo;
import com.tonggou.gsm.andclient.bean.AppVehicleDTO;
import com.tonggou.gsm.andclient.bean.GeoPointParcel;
import com.tonggou.gsm.andclient.net.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.gsm.andclient.net.request.QueryVehicleInfoWithLocationRequest;
import com.tonggou.gsm.andclient.net.response.VehicleInfoResponse;
import com.tonggou.gsm.andclient.util.BMapUtil;
import com.tonggou.gsm.andclient.util.ContextUtil;
import com.tonggou.gsm.andclient.util.HandlerTimer;
import com.tonggou.gsm.andclient.util.HandlerTimer.OnHandleTimerListener;

/**
 * 轮询查询车辆位置服务
 * @author lwz
 *
 */
public class PollingVehicleLocationService extends BaseService implements OnHandleTimerListener {

	public static final String ACTION = "com.tonggou.gsm.andclient.action.POLLING_VEHICLE_LOCATION_SERVICE";
	
	private static final String EXTRA_OPTION = "extra_option";
	
	static enum Option {
		POLLING,
		STOP_POLLING
	}
	
	public static void startPolling(Context context) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_OPTION, Option.POLLING);
		ContextUtil.startService(context, PollingVehicleLocationService.class, args);
	}
	
	public static void stopPolling(Context context) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_OPTION, Option.STOP_POLLING);
		ContextUtil.startService(context, PollingVehicleLocationService.class, args);
	}
	
	private final int TIMER_TOKEN = 0x777;
	private final int POLLING_INTERVAL = Constants.APP_CONFIG.POLLING_VEHICLE_LOCATION_INTERVAL;
	
	private HandlerTimer mPollingTimer;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		App.debug(TAG, "onCreate");
		mPollingTimer = new HandlerTimer(TIMER_TOKEN, this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if( intent != null && intent.hasExtra(EXTRA_OPTION) ) {
			App.debug(TAG, "onStartCommand  " + intent.hasExtra(EXTRA_OPTION) );
			switch ((Option)intent.getSerializableExtra(EXTRA_OPTION)) {
			case POLLING:
				mPollingTimer.start(1000, POLLING_INTERVAL);
				break;
			default:
				mPollingTimer.stop();
				stopSelf();
				break;
			}
		}
		return START_STICKY;
	}
	
	@Override
	public void onHandleTimerMessage(int token, Message msg) {
		if( token == TIMER_TOKEN ) {
			doQueryVehicleLocation();
		}
	}
	
	private void doQueryVehicleLocation() {
		QueryVehicleInfoWithLocationRequest request = new QueryVehicleInfoWithLocationRequest();
		request.doRequest(this, new AsyncJsonBaseResponseParseHandler<VehicleInfoResponse>() {

			@Override
			public void onParseSuccess(VehicleInfoResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				AppVehicleDTO vehicleInfo = result.getVehicleInfo();
				UserBaseInfo.setVehicleInfo(vehicleInfo);
				App.debug(TAG, "车辆 位置信息： " + vehicleInfo.getCoordinateLat() + "   " + vehicleInfo.getCoordinateLon());
				notifyUpdateVehicleLocation(vehicleInfo.getCoordinateLat(), vehicleInfo.getCoordinateLon());
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseBody, Throwable error) {
				if( statusCode == Constants.NETWORK_STATUS_CODE.CODE_LOGIN_EXPIRE ) {
					super.onFailure(statusCode, headers, responseBody, error);
					stopSelf();
				} else {
					App.showShortToast(getString(R.string.query_vehicle_location_failure));
				}
			}
			
			@Override
			public Class<VehicleInfoResponse> getTypeClass() {
				return VehicleInfoResponse.class;
			}
		});
	}
	
	/**
	 * 发广播通知更新车辆位置
	 * @param lat
	 * @param lng
	 */
	private void notifyUpdateVehicleLocation(Double lat, Double lng) {
		if( lat.intValue() == 0 || lat.intValue() == 0 ) {
			return;
		}
		GeoPoint geoPoint = BMapUtil.convertWgs84ToBaidu(lat, lng);
		UpdateVehicleLocationBroadcastReceiver.sendBroadcast(this, new GeoPointParcel(geoPoint));
	}

	@Override
	public void onDestroy() {
		mPollingTimer.stop();
		mPollingTimer = null;
		App.debug(TAG, "onDestroy");
		super.onDestroy();
	}
}
