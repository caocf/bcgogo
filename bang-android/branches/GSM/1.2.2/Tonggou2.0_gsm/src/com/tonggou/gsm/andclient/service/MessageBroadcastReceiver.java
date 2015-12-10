package com.tonggou.gsm.andclient.service;

import java.util.concurrent.ConcurrentHashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.tonggou.gsm.andclient.util.ContextUtil;

public class MessageBroadcastReceiver extends BroadcastReceiver {

	public static final String ACTION = "com.tonggou.gsm.andclient.action.GOT_NEW_MESAGE";
	
	private static ConcurrentHashMap<Context, MessageBroadcastReceiver> sReceiverMap;
	private OnGotNewMessageListener mListener;
	
	public static void sendBroadcast(Context context) {
		ContextUtil.sendBroadcast(context, ACTION);
	}
	
	public static void register(Context context, OnGotNewMessageListener l ) {
		MessageBroadcastReceiver receiver = new MessageBroadcastReceiver(context, l);
		context.registerReceiver(receiver, new IntentFilter(ACTION));
		sReceiverMap.put(context, receiver);
	}
	
	public static void unregister(Context context) {
		if( sReceiverMap == null ) {
			return;
		}
		MessageBroadcastReceiver receiver = sReceiverMap.remove(context);
		if( receiver != null ) {
			context.unregisterReceiver(receiver);
		}
		if( sReceiverMap.isEmpty() ) {
			sReceiverMap = null;
		}
	}
	
	public MessageBroadcastReceiver(Context context, OnGotNewMessageListener l) {
		if( sReceiverMap == null ) {
			sReceiverMap = new ConcurrentHashMap<Context, MessageBroadcastReceiver>();
		}
		mListener = l;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if( ACTION.equals(intent.getAction()) ) {
			if( mListener != null ) {
				mListener.onGotNewMessage();
			}
		}
	}
	
	/**
	 * 得到新的消息监听
	 * @author lwz
	 *
	 */
	public static interface OnGotNewMessageListener {
		public void onGotNewMessage();
	}
}
