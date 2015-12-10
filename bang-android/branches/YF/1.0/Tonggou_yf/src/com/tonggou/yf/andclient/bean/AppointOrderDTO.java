package com.tonggou.yf.andclient.bean;

public class AppointOrderDTO {

	private String id;				// 预约单 id
	private String appointTimeStr;	// 预约时间
	private String appointServiceType; // 服务类型
	private String vehicleNo;	// 车牌
	private String customer;	// 车主
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAppointTimeStr() {
		return appointTimeStr;
	}
	public void setAppointTimeStr(String appointTimeStr) {
		this.appointTimeStr = appointTimeStr;
	}
	public String getAppointServiceType() {
		return appointServiceType;
	}
	public void setAppointServiceType(String appointServiceType) {
		this.appointServiceType = appointServiceType;
	}
	public String getVehicleNo() {
		return vehicleNo;
	}
	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	
}
