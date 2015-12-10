package com.tonggou.yf.andclient.bean;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class UserScope implements Serializable {
	
	private static final long serialVersionUID = 1851498847204194436L;

	@SerializedName("faultInfo")
    private boolean isDTC;  //是否有故障权限
	
	@SerializedName("appoint")
    private boolean isAppoint; 	//是否有预约权限
	
	@SerializedName("customerRemind")
    private boolean isMaintain;   //是否有保养权限
	
	public boolean isDTC() {
		return isDTC;
	}
	public void setDTC(boolean isDTC) {
		this.isDTC = isDTC;
	}
	public boolean isAppoint() {
		return isAppoint;
	}
	public void setAppoint(boolean isAppoint) {
		this.isAppoint = isAppoint;
	}
	public boolean isMaintain() {
		return isMaintain;
	}
	public void setMaintain(boolean isMaintain) {
		this.isMaintain = isMaintain;
	}
	
}
