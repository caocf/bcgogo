package com.tonggou.andclient.app;

import java.util.List;

import org.apache.http.Header;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.text.TextUtils;

import com.loopj.android.http.TextHttpResponseHandler;
import com.tonggou.andclient.network.request.SendMultiFaultCodeRequest;
import com.tonggou.andclient.util.SaveDB;
import com.tonggou.andclient.vo.CarCondition;
import com.tonggou.andclient.vo.VehicleInfo;

public class UploadLocalCarCondition extends Service {

	private VehicleInfo mVehicleInfo;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mVehicleInfo = TongGouApplication.getInstance().getDefaultVehicle();
		if( mVehicleInfo == null || TextUtils.isEmpty( mVehicleInfo.getObdSN() )) {
			stopSelf();
		} else {
			new QueryLocalCarConditionTask().execute();
		}
	}
	
	class QueryLocalCarConditionTask extends AsyncTask<Void, Void, List<CarCondition>> {

		@Override
		protected List<CarCondition> doInBackground(Void... params) {
			
			String vehicleId = mVehicleInfo.getVehicleId();
			String userNo = mVehicleInfo.getUserNo();
			return SaveDB.getSaveDB(getApplicationContext()).getAllCarConditons(userNo, vehicleId);
		}
		
		@Override
		protected void onPostExecute(List<CarCondition> result) {
			TongGouApplication.showLog( result == null ? "null" : result.size() );
			if( result == null || result.isEmpty() ) {
				stopSelf();
			} else {
				doUpload(result);
			}
			
		}
	}
	
	private void doUpload(List<CarCondition> conditions ) {
		SendMultiFaultCodeRequest request = new SendMultiFaultCodeRequest();
		request.setRequestParams(conditions);
		request.doRequest(this, new TextHttpResponseHandler() {
			
			@Override
			public void onStart() {
				super.onStart();
				TongGouApplication.showLog("start upload local vehicle condition");
			}
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				TongGouApplication.showLog( responseString );
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString,
					Throwable throwable) {
				TongGouApplication.showLog( responseString );
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				stopSelf();
			}
		});
	}

}
