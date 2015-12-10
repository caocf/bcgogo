package com.tonggou.gsm.andclient.service;

import android.content.Context;
import android.view.View;

import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.ui.view.SingleButtonDialog;

public class UpdateService extends AbsUpdateValidateService {

	/**
	 * 
	 * @param context
	 * @param isAutoCheck	是否为自动检测
	 * @param listener		升级请求回调
	 */
	public static synchronized void update(Context context, boolean isAutoCheck, final onUpdateCheckRequestListener listener) {
		new UpdateService(context, isAutoCheck, listener).doUpdate();
	}
	
	public static interface onUpdateCheckRequestListener {
		/**
		 * 开始更新检测
		 */
		public void onStart();
		
		/**
		 * 更新检测完成
		 */
		public void onFinish();
	}
	
	private onUpdateCheckRequestListener mListener;
	
	public UpdateService(Context context, boolean isAutoCheck, onUpdateCheckRequestListener listener) {
		super(context, isAutoCheck);
		mListener = listener;
	}

	@Override
	public void onNoraml() {
		SingleButtonDialog dialog = new SingleButtonDialog(mContext, false);
		dialog.showDialog(mContext.getString(R.string.info_app_connot_update),
				mContext.getString(R.string.btn_confirm), null);
	}

	@Override
	public void onAlert(final String url, String desc, boolean isForceUpdate) {
		SingleButtonDialog dialog = new SingleButtonDialog(mContext);
		dialog.showDialog(desc,
				mContext.getString(R.string.info_update_version_now),
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						new DownloadApkService(mContext).downloadApp( url );
					}
				});

	}
	
	@Override
	void onStart() {
		if( mListener != null ) {
			mListener.onStart();
		}
	}

	@Override
	void onFinish() {
		if( mListener != null ) {
			mListener.onFinish();
		}
	}

}
