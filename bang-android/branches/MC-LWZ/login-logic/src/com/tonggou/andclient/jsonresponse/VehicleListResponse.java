package com.tonggou.andclient.jsonresponse;

import java.util.ArrayList;

import com.tonggou.andclient.vo.VehicleInfo;

public class VehicleListResponse extends BaseResponse{
	
	private static final long serialVersionUID = -4406826867692264572L;
	
	private ArrayList<VehicleInfo> vehicleList;
	public ArrayList<VehicleInfo> getVehicleList() {
		return vehicleList;
	}
	public void setVehicleList(ArrayList<VehicleInfo> vehicleList) {
		this.vehicleList = vehicleList;
	}
}
