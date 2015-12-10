package com.tonggou.yf.andclient.bean;

import com.google.gson.annotations.SerializedName;

public class ServiceJobDTO {
	private String id;				// 预约单 id
	private float remindMileage;	// 保养里程
	private float currentMileage; // 当前里程  当前里程-保养里程 为正值 显示超出 当前里程-保养里程 公里 否则显示还剩 当前里程-保养里程 公里 
	@SerializedName("licenceNo")
	private String vehicleNo;	// 车牌号
	private String customerName;	// 车主
	private String mobile;		// 手机号 
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public float getRemindMileage() {
		return remindMileage;
	}
	public void setRemindMileage(float remindMileage) {
		this.remindMileage = remindMileage;
	}
	public float getCurrentMileage() {
		return currentMileage;
	}
	public void setCurrentMileage(float currentMileage) {
		this.currentMileage = currentMileage;
	}
	public String getVehicleNo() {
		return vehicleNo;
	}
	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	/**
	 * 得到保养剩余里程 (当前里程-保养里程)
	 * <p> NOTE : 为正值 显示超出 X 公里, 否则显示还剩 X 公里 
	 * @return 
	 */
	public float getMaintainLeftMileage() {
		return currentMileage - remindMileage;
	}
}
