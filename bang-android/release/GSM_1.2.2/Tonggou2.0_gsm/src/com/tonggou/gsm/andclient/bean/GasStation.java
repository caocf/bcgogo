package com.tonggou.gsm.andclient.bean;

import java.util.HashMap;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

// 加油站数据
public class GasStation implements Parcelable {

	private final String BUNDLE_KEY_GAS_PRICE = "gastprice";

	private long id;	// 加油站 id
	private String name; // 加油站名称
	private String areaname; // 城市区域
	private String address; // 加油站地址
	private String brandname; // 运营商类型
	private String type; // 加油站类型
	private String discount; // 是否打折加油站
	private double lat; // 百度地图纬度
	private double lon; // 百度地图经度
	private HashMap<String, String> gastprice; // 省控油价
	private int distance;	// 距离

	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<GasStation> CREATOR = new Parcelable.Creator<GasStation>() {
		
		public GasStation createFromParcel(Parcel in) {
			return new GasStation(in);
		}

		public GasStation[] newArray(int size) {
			return new GasStation[size];
		}
	};
	
	public GasStation() {
	}

	@SuppressWarnings("unchecked")
	GasStation(Parcel in) {
		id = in.readLong();
		name = in.readString();
		areaname = in.readString();
		address = in.readString();
		brandname = in.readString();
		type = in.readString();
		discount = in.readString();
		lat = in.readDouble();
		lon = in.readDouble();
		distance = in.readInt();
		gastprice = (HashMap<String, String>) in.readBundle().getSerializable(
				BUNDLE_KEY_GAS_PRICE);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeString(areaname);
		dest.writeString(address);
		dest.writeString(brandname);
		dest.writeString(type);
		dest.writeString(discount);
		dest.writeDouble(lat);
		dest.writeDouble(lon);
		dest.writeInt(distance);
		Bundle bundle = new Bundle();
		bundle.putSerializable(BUNDLE_KEY_GAS_PRICE, gastprice);
		dest.writeBundle(bundle);
	}
	
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

	public String getAreaname() {
		return areaname;
	}

	public void setAreaname(String areaname) {
		this.areaname = areaname;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBrandname() {
		return brandname;
	}

	public void setBrandname(String brandname) {
		this.brandname = brandname;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public HashMap<String, String> getGastprice() {
		return gastprice;
	}

	public void setGastprice(HashMap<String, String> gastprice) {
		this.gastprice = gastprice;
	}

	/**
	 * id 相等说明是同一个加油站
	 */
	@Override
	public boolean equals(Object o) {
		if( o instanceof GasStation ) {
			return id == ((GasStation)o).getId();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return String.valueOf(id).hashCode();
	}
	
	

}
