
package com.tonggou.gsm.andclient.bean;

import com.google.gson.annotations.Expose;

public class AppUserDTO {

	@Expose
	private String imei;
    @Expose
    private String name;
    @Expose
    private String id;
    @Expose
    private String password;
    @Expose
    private String userNo;
    @Expose
    private String mobile;
    @Expose
    private String gsmObdImeiMoblie;
    
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUserNo() {
		return userNo;
	}
	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getGsmObdImeiMoblie() {
		return gsmObdImeiMoblie;
	}
	public void setGsmObdImeiMoblie(String gsmObdImeiMoblie) {
		this.gsmObdImeiMoblie = gsmObdImeiMoblie;
	}
}
