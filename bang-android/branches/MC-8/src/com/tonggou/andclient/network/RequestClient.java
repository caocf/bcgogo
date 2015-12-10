package com.tonggou.andclient.network;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.parse.JSONParseInterface;

public class RequestClient {
	
	private final int REQUEST_START = 0x01;		// 请求开始
	private final int REQUEST_SUCCESS = 0x02;	// 请求成功
	private final int REQUEST_FAILURE = 0x03;	// 请求失败
	private final int REQUEST_FINISH = 0x04;	// 请求结束
	
	private static final int MAX_THREAD_COUNT = 4;
	
	private Context mContext;
	private static ExecutorService sExecutorService = Executors.newFixedThreadPool(MAX_THREAD_COUNT);
	private AsyncRequestHandler mRequestHandler;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
				case REQUEST_START: mRequestHandler.onStart(); break;
				case REQUEST_SUCCESS: mRequestHandler.onSuccess(msg.obj + ""); break;
				case REQUEST_FAILURE: mRequestHandler.onFailure(msg.obj + ""); break;
				case REQUEST_FINISH: mRequestHandler.onFinish(); break;
				default: break;
			}
		}
		
	};
	
	public RequestClient(Context context) {
		mContext = context;
	}
	
	public void get(final String url, final AsyncRequestHandler handler) {
		mRequestHandler = handler;
		sExecutorService.execute(new Runnable() {
			
			@Override
			public void run() {
				syncRequest(url);
			}
		});
	}
	
	private void syncRequest(String url) {
		sendMessage(REQUEST_START, null);
		TongGouApplication.showLog(url);
		ResponseParser parser = new ResponseParser();
		NetworkState ns = Network.getNetwork(mContext).httpGetUpdateString(url, parser);	
		if(ns.isNetworkSuccess()){
			sendMessage(REQUEST_SUCCESS, parser.getResponseData());
		}else{
			//网络出错
			sendMessage(REQUEST_FAILURE, ns.getErrorMessage());
		}
		sendMessage(REQUEST_FINISH, null);
	}
	
	private void sendMessage(int status, Object data) {
		Message msg = mHandler.obtainMessage(status);
		msg.obj = data;
		mHandler.sendMessage(msg);
	}
	
	class ResponseParser implements JSONParseInterface {
		String mResponseData;
		
		@Override
		public void parsing(String dataFormServer) {
			mResponseData = dataFormServer;
		}
		
		public String getResponseData() {
			return mResponseData;
		}
		
	}
}
