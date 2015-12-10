package com.tonggou.andclient.vo;

public class Area {
	long id;//主键                            
	String name;//地名                          
	String cityCode;//地图数据中的城市编号         
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

}
