package com.tonggou.yf.andclient.service;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.tonggou.yf.andclient.R;

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
		AlertDialog dialog = new AlertDialog.Builder(mContext)
			.setTitle(R.string.info_no_version_update)
			.setPositiveButton(R.string.dialog_btn_comfirm, null)
			.create();
		dialog.show();
	}

	@Override
	public void onAlert(final String url, String desc, boolean isForceUpdate) {
		AlertDialog dialog = new AlertDialog.Builder(mContext)
			.setTitle(R.string.info_has_version_update)
			.setMessage(desc)
			.setCancelable(!isForceUpdate)
			.setPositiveButton(R.string.dialog_btn_comfirm, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					new DownloadApkService(mContext).downloadApp( url );
				}
			})
			.create();
		dialog.show();
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
