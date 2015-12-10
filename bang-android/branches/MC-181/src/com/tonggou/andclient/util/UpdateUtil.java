package com.tonggou.andclient.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * 更新工具类
 * <p>
 * 	通过该类，可以连接服务器来判断当前是否有新的应用版本
 * </p>
 * 
 * @author lwz
 */
public class UpdateUtil {
	
	private OnUpdateListener mUpdateListener;
	
	/**
	 * 更新接口
	 * @author lwz
	 */
	public interface OnUpdateListener{
		/**
		 * 更新动作
		 */
		public void onUpdate();
		
		/**
		 * 当前版本已是最新
		 */
		public void onNoNeedUpdate();
	}
	
	public void setOnUpdateListener( OnUpdateListener listener ){
		mUpdateListener = listener;
	}
	
	/**
	 * 获得本地安装包的信息
	 * 
	 * @param context 上下文对象
	 * @throws NameNotFoundException 
	 */
	public static PackageInfo getLocalPackageInfo(Context context) throws NameNotFoundException{
		// 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        return packageManager.getPackageInfo( context.getPackageName(),0);
	}
	
	/**
	 * 验证是否要更新
	 * 
	 * @param context 上下文对象
	 * @param versionCode 服务器中传回的版本号
	 * @return 有可用更新返回 true, 否则返回 false
	 * @throws NameNotFoundException
	 */
	private boolean validateUpdate( Context context, String versionCode , String versionName) throws NameNotFoundException{
		return getLocalPackageInfo(context).versionName.compareTo( versionName ) < 0 ? true : false;
	}
	
	/**
	 * 更新
	 * 
	 * @param context 上下文对象
	 * @param versionCode 服务器中传回的版本号
	 * @throws NameNotFoundException 
	 */
	public void update(Context context, String versionCode, String versionName) throws NameNotFoundException{
		if( validateUpdate(context, versionCode, versionName) ){
			if( mUpdateListener != null ){
				mUpdateListener.onUpdate();
			}
		} else {
			if( mUpdateListener != null ){
				mUpdateListener.onNoNeedUpdate();
			}
		}
	}
}
