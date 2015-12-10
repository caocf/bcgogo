package com.tonggou.gsm.andclient.service;

import java.util.concurrent.ConcurrentHashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.tonggou.gsm.andclient.util.ContextUtil;

public class NewVersionBroadcastReceiver extends BroadcastReceiver {

public static final String ACTION = "com.tonggou.gsm.andclient.action.GOT_NEW_VERSION";
	
	private static ConcurrentHashMap<Context, NewVersionBroadcastReceiver> sReceiverMap;
	private OnGotNewVersionListener mListener;
	
	public static void sendBroadcast(Context context) {
		ContextUtil.sendBroadcast(context, ACTION);
	}
	
	public static void register(Context context, OnGotNewVersionListener l ) {
		NewVersionBroadcastReceiver receiver = new NewVersionBroadcastReceiver(context, l);
		context.registerReceiver(receiver, new IntentFilter(ACTION));
		sReceiverMap.put(context, receiver);
	}
	
	public static void unregister(Context context) {
		if( sReceiverMap == null ) {
			return;
		}
		NewVersionBroadcastReceiver receiver = sReceiverMap.remove(context);
		if( receiver != null ) {
			context.unregisterReceiver(receiver);
		}
		if( sReceiverMap.isEmpty() ) {
			sReceiverMap = null;
		}
	}
	
	public NewVersionBroadcastReceiver(Context context, OnGotNewVersionListener l) {
		if( sReceiverMap == null ) {
			sReceiverMap = new ConcurrentHashMap<Context, NewVersionBroadcastReceiver>();
		}
		mListener = l;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if( ACTION.equals(intent.getAction()) ) {
			if( mListener != null ) {
				mListener.onGotNewVersion();
			}
		}
	}
	
	/**
	 * 得到新的消息监听
	 * @author lwz
	 *
	 */
	public static interface OnGotNewVersionListener {
		public void onGotNewVersion();
	}

}
