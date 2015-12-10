package com.tonggou.andclient.network;

public abstract class AsyncRequestHandler {
	
	public void onStart(){};
	
	public void onSuccess(String result){};
	
	public void onFailure(String msg){};
	
	public void onFinish(){};
	
}
