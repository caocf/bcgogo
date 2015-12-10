package com.tonggou.gsm.andclient.service;

import java.util.Locale;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.Constants;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.type.UpdateAction;
import com.tonggou.gsm.andclient.net.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.gsm.andclient.net.request.UpdateRequest;
import com.tonggou.gsm.andclient.net.response.UpdateResponse;
import com.tonggou.gsm.andclient.ui.view.SingleButtonDialog;
import com.tonggou.gsm.andclient.util.PackageInfoUtil;

public class UpdateService {

	public static synchronized void update(final onUpdateCheckRequestListener listener) {
		new UpdateService(App.getInstance(), listener).doUpdate();
	}
	
	private Context mContext;
	private long mDownloadId = -1;
	
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
	
	public UpdateService(Context context, onUpdateCheckRequestListener listener) {
		mContext = context;
		mListener = listener;
	}

	public void doUpdate() {
		if( mListener != null ) {
			mListener.onStart();
		}
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
								|| !downloadUrl.trim().toLowerCase(Locale.getDefault()).startsWith("http") )
							 onNoraml();
						else
							 onAlert(downloadUrl.trim(), result.getDescription(), action == UpdateAction.force);
					}
					
					@Override
					public void onFinish() {
						super.onFinish();
						if( mListener != null ) {
							mListener.onFinish();
						}
					}

					@Override
					public Class<UpdateResponse> getTypeClass() {
						return UpdateResponse.class;
					}
				});
	}

	public void onNoraml() {
		SingleButtonDialog dialog = new SingleButtonDialog(mContext);
		dialog.showDialog(mContext.getString(R.string.info_app_connot_update),
				mContext.getString(R.string.btn_confirm), null);
	}

	public void onAlert(final String url, String desc, boolean isForceUpdate) {
		SingleButtonDialog dialog = new SingleButtonDialog(mContext);
		dialog.showDialog(desc,
				mContext.getString(R.string.info_update_version_now),
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						downloadApp( url );
					}
				});

	}
	
	public void downloadApp(String url) {
		registerDownloadBroadcastReceiver();
		DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);  
        
	    Request request = new Request(Uri.parse(url));  
	  
	    //设置允许使用的网络类型，这里是移动网络和wifi都可以    
	    request.setAllowedNetworkTypes(
	    		DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);  
	    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, getDownloadApkName() );
	    //禁止发出通知，既后台下载，如果要使用这一句必须声明一个权限：android.permission.DOWNLOAD_WITHOUT_NOTIFICATION    
	    //request.setShowRunningNotification(false);
	    
	    //不显示下载界面
	    request.setVisibleInDownloadsUi(false);  
	    mDownloadId = downloadManager.enqueue(request);  
	}
	
	private String getDownloadApkName() {
		return mContext.getResources().getString(R.string.app_name) 
				+ "_" + System.currentTimeMillis() + Constants.APP_CONFIG.DOWNLOAD_APK_FILE_SUFFIX;
	}
	
	public void registerDownloadBroadcastReceiver() {
		mContext.registerReceiver(new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				if( DownloadManager.ACTION_DOWNLOAD_COMPLETE.equalsIgnoreCase(intent.getAction()) ) {
					long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
					if( mDownloadId == id ) {
						Query query = new Query();  
			            query.setFilterById(id);  
			            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);  
			            Cursor cursor = downloadManager.query(query);
			            if(cursor.moveToFirst()) {
				            String localUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
				            installApp(localUri);
			            }
			            cursor.close();
					}
				}
			}
		}, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}
	
	public void installApp(String localUri) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse(localUri), "application/vnd.android.package-archive");
		mContext.startActivity(intent);
	}

}
