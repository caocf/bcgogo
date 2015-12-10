package com.tonggou.andclient.jsonresponse;

import com.tonggou.andclient.vo.VehicleInfo;

public class VehicleResponse extends BaseResponse {
	
	private static final long serialVersionUID = 7445997839172416499L;
	
	private VehicleInfo vehicleInfo;

	public VehicleInfo getVehicleInfo() {
		return vehicleInfo;
	}

	public void setVehicleInfo(VehicleInfo vehicleInfo) {
		this.vehicleInfo = vehicleInfo;
	}
}
