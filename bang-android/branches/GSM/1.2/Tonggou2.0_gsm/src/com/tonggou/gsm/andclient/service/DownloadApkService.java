package com.tonggou.gsm.andclient.service;

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

import com.tonggou.gsm.andclient.Constants;
import com.tonggou.gsm.andclient.R;

public class DownloadApkService {
	
	private Context mContext;
	private long mDownloadId = -1;
	
	public DownloadApkService(Context context) {
		mContext = context;
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
	
	private void registerDownloadBroadcastReceiver() {
		try {
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
				            context.unregisterReceiver(this);
						}
					}
				}
			}, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		} catch (Exception e) {
			// do-nothing
		}
	}
	
	public void installApp(String localUri) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse(localUri), "application/vnd.android.package-archive");
		mContext.startActivity(intent);
	}
}
