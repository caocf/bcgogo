package com.tonggou.gsm.andclient.net.response;

import java.util.ArrayList;

import com.tonggou.gsm.andclient.bean.DriveLogDTO;

/**
 * 行车轨迹响应
 * @author lwz
 *
 */
public class DrivingTrackResponse extends BaseResponse {

	private static final long serialVersionUID = 5460883339908594735L;

	private ArrayList<DriveLogDTO> detailDriveLogs;

	public ArrayList<DriveLogDTO> getDetailDriveLogs() {
		return detailDriveLogs;
	}

	public void setDetailDriveLogs(ArrayList<DriveLogDTO> detailDriveLogs) {
		this.detailDriveLogs = detailDriveLogs;
	}
}
