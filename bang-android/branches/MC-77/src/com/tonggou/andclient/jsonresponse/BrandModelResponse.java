package com.tonggou.andclient.jsonresponse;

import java.util.List;

import com.tonggou.andclient.vo.BrandModel;

public class BrandModelResponse {
	private List<BrandModel> result;
	private String status; 
	private String msgCode;  
	private String message ;  
	private String data ;
	private String vehicleInfo;
	public List<BrandModel> getBrandModel() {
		return result;
	}
	public void setBrandModel(List<BrandModel> result) {
		this.result = result;
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
	public String getVehicleInfo() {
		return vehicleInfo;
	}
	public void setVehicleInfo(String vehicleInfo) {
		this.vehicleInfo = vehicleInfo;
	} 
}
