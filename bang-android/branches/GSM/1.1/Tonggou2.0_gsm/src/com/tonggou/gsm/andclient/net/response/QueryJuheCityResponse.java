package com.tonggou.gsm.andclient.net.response;

import java.util.ArrayList;

import com.tonggou.gsm.andclient.bean.Area;

public class QueryJuheCityResponse extends BaseResponse {

	private static final long serialVersionUID = -6674268390749796372L;
	
	private ArrayList<Area> areaList;

	public ArrayList<Area> getAreaList() {
		return areaList;
	}

	public void setAreaList(ArrayList<Area> areaList) {
		this.areaList = areaList;
	}
}
