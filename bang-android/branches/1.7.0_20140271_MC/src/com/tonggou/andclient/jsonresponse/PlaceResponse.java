package com.tonggou.andclient.jsonresponse;

import java.util.List;

import com.tonggou.andclient.vo.Area;

public class PlaceResponse extends BaseResponse {
	private static final long serialVersionUID = 2330897712142994544L;
	private List<Area>  areaList;
	public List<Area> getAreaList() {
		return areaList;
	}
	public void setAreaList(List<Area> areaList) {
		this.areaList = areaList;
	}
}
