package com.tonggou.andclient;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.tonggou.andclient.network.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.andclient.network.request.DJItemUploadRequest;
import com.tonggou.andclient.network.request.DJItemUploadResponse;
import com.tonggou.andclient.util.DJDatabase;
import com.tonggou.andclient.vo.DrivingJournalItem;

public class DJUploadService extends Service {
	private static final String TAG = "DJUploadService";
	public static final String ACTION_UPLOAD_DRIVING_JOURNALS = "com.tonggou.andclient.DJUploadService";
	private DJDatabase mDJDatabase;
	private NotUploadDJQueryTask mQueryTask;
	private ArrayList<DrivingJournalItem> mNotUploadDjItems;
	private int mCurrIndex;

	@Override
	public void onCreate() {
		super.onCreate();
		mDJDatabase = DJDatabase.getInstance(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null) {
			stopSelf();
			return super.onStartCommand(intent, flags, startId);
		}
		if (mQueryTask == null || AsyncTask.Status.FINISHED.equals(mQueryTask.getStatus())) {
			mQueryTask = new NotUploadDJQueryTask();
			mQueryTask.execute();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class NotUploadDJQueryTask extends AsyncTask<Void, Void, ArrayList<DrivingJournalItem>> {
		@Override
		protected ArrayList<DrivingJournalItem> doInBackground(Void... params) {
			return mDJDatabase.queryNotUploadDJItems();
		}

		@Override
		protected void onPostExecute(ArrayList<DrivingJournalItem> result) {
			if (result != null) {
				mNotUploadDjItems = result;
				uploadDJItem(result.get(mCurrIndex++));
			}
		}
	}

	private void uploadDJItem(DrivingJournalItem djItem) {
		DJItemUploadRequest uploadRequest = new DJItemUploadRequest();
		uploadRequest.setRequestParams(djItem);
		uploadRequest.doRequest(getApplicationContext(),
				new AsyncJsonBaseResponseParseHandler<DJItemUploadResponse>() {
					@Override
					public void onParseSuccess(DJItemUploadResponse result, String originResult) {
						DrivingJournalItem djItem = result.getResult();
						mDJDatabase.updateUploadedDJItem(djItem);
						if (mCurrIndex < mNotUploadDjItems.size()) {
							uploadDJItem(mNotUploadDjItems.get(mCurrIndex++));
						} else {
							stopSelf();
						}
						super.onParseSuccess(result, originResult);
					}

					@Override
					public Class<DJItemUploadResponse> getTypeClass() {
						return DJItemUploadResponse.class;
					}
				});
	}
}
