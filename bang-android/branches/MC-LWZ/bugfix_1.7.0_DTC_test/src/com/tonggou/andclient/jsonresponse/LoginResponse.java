package com.tonggou.andclient.jsonresponse;

import java.util.List;

import com.tonggou.andclient.vo.AppConfig;
import com.tonggou.andclient.vo.OBDBindInfo;

public class LoginResponse extends BaseResponse {
	private static final long serialVersionUID = 8286943074605321540L;
	
	private List<OBDBindInfo> obdList;
	private AppConfig appConfig;
	
	public List<OBDBindInfo> getObdList() {
		return obdList;
	}
	public void setObdList(List<OBDBindInfo> obdList) {
		this.obdList = obdList;
	}
	public AppConfig getAppConfig() {
		return appConfig;
	}
	public void setAppConfig(AppConfig appConfig) {
		this.appConfig = appConfig;
	}
}
