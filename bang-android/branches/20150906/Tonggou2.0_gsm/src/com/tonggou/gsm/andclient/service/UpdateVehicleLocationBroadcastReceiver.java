package com.tonggou.gsm.andclient.service;

import java.util.concurrent.ConcurrentHashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.tonggou.gsm.andclient.bean.GeoPointParcel;
import com.tonggou.gsm.andclient.util.ContextUtil;

public class UpdateVehicleLocationBroadcastReceiver extends BroadcastReceiver {

	public static final String ACTION = "com.tonggou.gsm.andclient.action.UPDATE_VEHICLE_LOCATION";
	private static final String EXTRA_VEHICLE_LOCATION_GEOPOINT = "extra_vehicle_location_geopoint";
	
	private OnUpdateVehicleLocationListener mListener;
	private static ConcurrentHashMap<Context, UpdateVehicleLocationBroadcastReceiver> sReceiverMap;
	
	public static void sendBroadcast(Context context, GeoPointParcel geoPint) {
		Bundle args = new Bundle();
		args.putParcelable(EXTRA_VEHICLE_LOCATION_GEOPOINT, geoPint);
		ContextUtil.sendBroadcast(context, ACTION, args);
	}
	
	public static void register(Context context, OnUpdateVehicleLocationListener l) {
		UpdateVehicleLocationBroadcastReceiver receiver = new UpdateVehicleLocationBroadcastReceiver(l);
		context.registerReceiver(receiver, new IntentFilter(ACTION));
		sReceiverMap.put(context, receiver);
	}
	
	public static void unregister(Context context) {
		if( sReceiverMap != null ) {
			UpdateVehicleLocationBroadcastReceiver receiver = sReceiverMap.remove(context);
			if( receiver != null ) {
				context.unregisterReceiver( receiver );
			}
			if( sReceiverMap.isEmpty() ) {
				sReceiverMap = null;
			}
		}
	}
	
	private UpdateVehicleLocationBroadcastReceiver(OnUpdateVehicleLocationListener l) {
		if( sReceiverMap == null ) {
			sReceiverMap = new ConcurrentHashMap<Context, UpdateVehicleLocationBroadcastReceiver>();
		}
		mListener = l;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if( ACTION.equals(intent.getAction()) && intent.hasExtra(EXTRA_VEHICLE_LOCATION_GEOPOINT) ) {
			if( mListener != null ) {
				GeoPointParcel geoPoint = intent.getParcelableExtra(EXTRA_VEHICLE_LOCATION_GEOPOINT);
				mListener.onUpdateVehicleLocation(geoPoint);
			}
 		}
	}
	
	/**
	 * 更新车辆位置监听
	 * @author lwz
	 *
	 */
	public static interface OnUpdateVehicleLocationListener {
		/**
		 * 跟新车辆位置
		 * @param geoPoint
		 */
		public void onUpdateVehicleLocation(GeoPointParcel geoPoint);
	}
	

}
