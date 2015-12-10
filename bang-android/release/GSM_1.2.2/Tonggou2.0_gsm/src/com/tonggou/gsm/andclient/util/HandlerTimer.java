package com.tonggou.gsm.andclient.util;

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
		
		if( mPostDelay > 0 )
			sendEmptyMessageDelayed(mToken, mPostDelay);
	}
	
	public void setHandlerMessageCallback(OnHandleTimerListener listener) {
		mListener = listener;
	}
	
	/**
	 * 开始计时
	 * @param firstPostDelay	第一次延时时间
	 * @param postDelay			以后每一次的延时时间， postDelay<=0 时只调用一次回调 
	 * @return Returns true if the message was successfully placed in to the message queue. Returns false on failure, usually because the looper processing the message queue is exiting.
	 */
	public boolean start(long firstPostDelay, long postDelay) {
		stop();
		mPostDelay = postDelay;
		return sendEmptyMessageDelayed(mToken, firstPostDelay);
	}
	
	public void stop() {
		removeMessages(mToken);
	}
}
