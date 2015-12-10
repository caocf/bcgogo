package com.tonggou.gsm.andclient.net.response;

public interface IResponse {
	
	public boolean isSuccess();
	
	public String getMessage();
	
	public int getMsgCode();
}
