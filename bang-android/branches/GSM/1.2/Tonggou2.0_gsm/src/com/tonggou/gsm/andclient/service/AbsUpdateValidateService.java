package com.tonggou.gsm.andclient.service;

import java.util.Locale;

import android.content.Context;
import android.text.TextUtils;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.bean.type.UpdateAction;
import com.tonggou.gsm.andclient.net.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.gsm.andclient.net.request.UpdateRequest;
import com.tonggou.gsm.andclient.net.response.UpdateResponse;
import com.tonggou.gsm.andclient.util.PackageInfoUtil;

public abstract class AbsUpdateValidateService {
	
	protected Context mContext;
	private boolean mIsAutoCheck;
	
	public AbsUpdateValidateService(Context context, boolean isAutoCheck) {
		mContext = context;
		mIsAutoCheck = isAutoCheck;
	}

	public void doUpdate() {
		onStart();
		UpdateRequest request = new UpdateRequest();
		request.setApiParams(PackageInfoUtil.getVersionName(App.getInstance()));
		request.doRequest(App.getInstance(),
				new AsyncJsonBaseResponseParseHandler<UpdateResponse>() {
					
					@Override
					public void onParseSuccess(UpdateResponse result,
							String originResult) {
						super.onParseSuccess(result, originResult);
						UpdateAction action = result.getAction();
						String downloadUrl = result.getUrl();
						if( action == UpdateAction.normal 
								|| TextUtils.isEmpty(downloadUrl) 
								|| !downloadUrl.trim().toLowerCase(Locale.getDefault()).startsWith("http") ) {
							if( !mIsAutoCheck ) {
								onNoraml();
							}
						} else {
							notifyHasNewVersion();
							
							if( !mIsAutoCheck || action == UpdateAction.force ) {
								onAlert(downloadUrl.trim(),
										result.getDescription(), action == UpdateAction.force);
							}
							
						}
					}
					
					@Override
					public void onFinish() {
						super.onFinish();
						AbsUpdateValidateService.this.onFinish();
					}

					@Override
					public Class<UpdateResponse> getTypeClass() {
						return UpdateResponse.class;
					}
				});
	}
	
	abstract void onStart();

	abstract void onNoraml();

	abstract void onAlert(final String url, String desc, boolean isForceUpdate);
	
	abstract void onFinish();
	
	private void notifyHasNewVersion() {
		NewVersionBroadcastReceiver.sendBroadcast(mContext);
	}
	
}
