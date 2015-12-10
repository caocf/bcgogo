package com.tonggou.andclient.util;

import android.os.Handler;
import android.os.Message;

public class HandlerTimer extends Handler {
	
	private OnHandleTimerListener mListener;
	private final int mToken;
	private long mPostDelay;

	public interface OnHandleTimerListener {
        public void onHandleTimerMessage(int token, Message msg);
    }
	
	/**
	 * 
	 * @param token		为 Message.what 值来唯一标记该消息
	 * @param listener
	 */
	public HandlerTimer(int token, OnHandleTimerListener listener) {
		mToken = token;
		mListener = listener;
	}
	
	@Override
	public void handleMessage(Message msg) {
		if( mListener != null ) 
			mListener.onHandleTimerMessage(mToken, msg);
		sendEmptyMessageDelayed(mToken, mPostDelay);
	}
	
	public void setHandlerMessageCallback(OnHandleTimerListener listener) {
		mListener = listener;
	}
	
	public boolean start(int firstPostDelay, long postDelay) {
		stop();
		mPostDelay = postDelay;
		return sendEmptyMessageDelayed(mToken, firstPostDelay);
	}
	
	public void stop() {
		removeMessages(mToken);
	}
}
