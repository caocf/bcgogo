package com.tonggou.andclient.jsonresponse;

import java.util.List;

import com.tonggou.andclient.vo.BrandModel;

public class BrandModelResponse extends BaseResponse {
	
	private static final long serialVersionUID = 2604188526304446000L;
	
	private List<BrandModel> result;
	private String vehicleInfo;
	public List<BrandModel> getBrandModel() {
		return result;
	}
	public void setBrandModel(List<BrandModel> result) {
		this.result = result;
	}
	public String getVehicleInfo() {
		return vehicleInfo;
	}
	public void setVehicleInfo(String vehicleInfo) {
		this.vehicleInfo = vehicleInfo;
	} 
}
