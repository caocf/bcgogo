package com.tonggou.andclient.jsonresponse;

import com.tonggou.andclient.vo.VehicleInfo;

public class AddBindCarResponse extends BaseResponse {
	
	private static final long serialVersionUID = 3243180096372522843L;
	private VehicleInfo vehicleInfo;
	
	public VehicleInfo getVehicleInfo() {
		return vehicleInfo;
	}
	public void setVehicleInfo(VehicleInfo vehicleInfo) {
		this.vehicleInfo = vehicleInfo;
	}        
	  
}

