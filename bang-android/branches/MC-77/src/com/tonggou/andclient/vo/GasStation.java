package com.tonggou.andclient.vo;

import android.os.Parcel;
import android.os.Parcelable;

public class GasStation implements Parcelable {

	private int id;
	private String name;
	private String address;
	private String type;
	private String discount;
	private double lon;
	private double lat;
	private float E90;
	private float E93;
	private float E97;
	private float E0;
	private int distance;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public float getE90() {
		return E90;
	}

	public void setE90(float e90) {
		E90 = e90;
	}

	public float getE93() {
		return E93;
	}

	public void setE93(float e93) {
		E93 = e93;
	}

	public float getE97() {
		return E97;
	}

	public void setE97(float e97) {
		E97 = e97;
	}

	public float getE0() {
		return E0;
	}

	public void setE0(float e0) {
		E0 = e0;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public static final Parcelable.Creator<GasStation> CREATOR = new Parcelable.Creator<GasStation>() {

		@Override
		public GasStation createFromParcel(Parcel source) {
			return new GasStation(source);
		}

		@Override
		public GasStation[] newArray(int size) {
			return new GasStation[size];
		}
	};
	
	public GasStation(){}

	private GasStation(Parcel source) {
		id = source.readInt();
		name = source.readString();
		address = source.readString();
		type = source.readString();
		discount = source.readString();
		lon = source.readDouble();
		lat = source.readDouble();
		E90 = source.readFloat();
		E93 = source.readFloat();
		E97 = source.readFloat();
		E0 = source.readFloat();
		distance = source.readInt();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeString(address);
		dest.writeString(type);
		dest.writeString(discount);
		dest.writeDouble(lon);
		dest.writeDouble(lat);
		dest.writeFloat(E90);
		dest.writeFloat(E93);
		dest.writeFloat(E97);
		dest.writeFloat(E0);
		dest.writeInt(distance);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public String toString() {
		return "GasStation [id=" + id + ", name=" + name + ", address="
				+ address + ", type=" + type + ", discount=" + discount
				+ ", lon=" + lon + ", lat=" + lat + ", E90=" + E90 + ", E93="
				+ E93 + ", E97=" + E97 + ", E0=" + E0 + ", distance="
				+ distance + "]";
	}

}
