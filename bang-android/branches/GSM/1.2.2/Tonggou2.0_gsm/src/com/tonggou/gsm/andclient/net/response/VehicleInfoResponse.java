package com.tonggou.gsm.andclient.net.response;

import com.tonggou.gsm.andclient.bean.AppVehicleDTO;

public class VehicleInfoResponse extends BaseResponse {

	private static final long serialVersionUID = -6421989810930889885L;

	private AppVehicleDTO vehicleInfo;

	public AppVehicleDTO getVehicleInfo() {
		return vehicleInfo;
	}

	public void setVehicleInfo(AppVehicleDTO vehicleInfo) {
		this.vehicleInfo = vehicleInfo;
	}
}
