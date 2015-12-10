package com.tonggou.lib.net.response;

public interface IResponse {
	
	public boolean isSuccess();
	
	public String getMessage();
	
	public int getMsgCode();
}
