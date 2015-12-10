package com.tonggou.gsm.andclient.bean;

import android.text.TextUtils;

public class AppShopDTO {
	private String address;// 店铺地址
	private String name;// 店铺名称
	private String id;	// 店铺ID
	private String accidentMobile; // 救援号码
	private String mobile;// 联系电话
	private String landLine;// 店铺座机
	private String smallImageUrl;// 店铺图片URL
	private String bigImageUrl;// 店铺图片URL
	private String distance;
	private String coordinate;// 店铺地址(lng,lat)
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAccidentMobile() {
		if( !TextUtils.isEmpty( accidentMobile ) ) {
			return accidentMobile;
		}
		if( !TextUtils.isEmpty(mobile) ) {
			return mobile;
		}
		if( !TextUtils.isEmpty(landLine) ) {
			return landLine;
		}
		return "";
	}

	public void setAccidentMobile(String accidentMobile) {
		this.accidentMobile = accidentMobile;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getLandLine() {
		if( !TextUtils.isEmpty(landLine) ) {
			return landLine;
		}
		if( !TextUtils.isEmpty(mobile) ) {
			return mobile;
		}
		if( !TextUtils.isEmpty( accidentMobile ) ) {
			return accidentMobile;
		}
		return "";
	}
	public void setLandLine(String landLine) {
		this.landLine = landLine;
	}
	public String getSmallImageUrl() {
		return smallImageUrl;
	}
	public void setSmallImageUrl(String smallImageUrl) {
		this.smallImageUrl = smallImageUrl;
	}
	public String getBigImageUrl() {
		return bigImageUrl;
	}
	public void setBigImageUrl(String bigImageUrl) {
		this.bigImageUrl = bigImageUrl;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String getCoordinate() {
		return coordinate;
	}
	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}
	public double getCoordinateLat() {
		return convertCoordinate()[1];
	}
	public double getCoordinateLng() {
		return convertCoordinate()[0];
	}
	
	private double[] convertCoordinate() {
		double[] coordinatesD = new double[]{120.733165, 31.296266};
		if( TextUtils.isEmpty(coordinate) || !coordinate.contains(",") ) {
			return coordinatesD;
		}
		String[] coordinatesStr = coordinate.split(",");
		for(int i=0; i<Math.min(2, coordinatesStr.length); i++) {
			try {
				coordinatesD[i] = Double.valueOf(coordinatesStr[i].trim());
			} catch (NumberFormatException e) {}
		}
		return coordinatesD;
	}
}
