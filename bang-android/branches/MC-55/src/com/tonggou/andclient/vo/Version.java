/**
 * Version 参数定义
 * 参考《开开API手册》15.1
 */
package com.tonggou.andclient.vo;


public class Version {
	public static final int UPDATE_ACTION_NORMAL = 0;
	public static final int UPDATE_ACTION_FORCE = -1;
	public static final int UPDAATE_ACTION_ALERT = 1;
	public static final int UPDATE_ACTION_MINOR = 2;
	
	
	private int action = UPDATE_ACTION_NORMAL;
	private int platform ;
	private String model;
	private String language;
	private String ver;
	private String message;
	private String code;
	private String url;
	
	
	

	public Version() {
		
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public int getPlatform() {
		return platform;
	}

	public void setPlatform(int platform) {
		this.platform = platform;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	
}
