package com.tonggou.andclient.jsonresponse;

import java.util.ArrayList;

import com.tonggou.andclient.vo.VehicleInfo;

public class VehicleListResponse {
	private ArrayList<VehicleInfo> vehicleList;
	private String status; 
	private String msgCode;  
	private String message ;  
	private String data ;
	public ArrayList<VehicleInfo> getVehicleList() {
		return vehicleList;
	}
	public void setVehicleList(ArrayList<VehicleInfo> vehicleList) {
		this.vehicleList = vehicleList;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMsgCode() {
		return msgCode;
	}
	public void setMsgCode(String msgCode) {
		this.msgCode = msgCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
}
