package com.tonggou.andclient.jsonresponse;

import java.util.ArrayList;

import com.tonggou.andclient.vo.VehicleInfo;

public class VehicleListResponse extends BaseResponse {

	private static final long serialVersionUID = -4406826867692264572L;

	private ArrayList<VehicleInfo> vehicleList;
	private String defaultOilPrice;
	private String defaultOilKind;
	
	public String getDefaultOilPrice() {
		return defaultOilPrice;
	}

	public void setDefaultOilPrice(String defaultOilPrice) {
		this.defaultOilPrice = defaultOilPrice;
	}

	public String getDefaultOilKind() {
		return defaultOilKind;
	}

	public void setDefaultOilKind(String defaultOilKind) {
		this.defaultOilKind = defaultOilKind;
	}

	public ArrayList<VehicleInfo> getVehicleList() {
		return vehicleList;
	}

	public void setVehicleList(ArrayList<VehicleInfo> vehicleList) {
		this.vehicleList = vehicleList;
	}
}
