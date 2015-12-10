package com.tonggou.gsm.andclient.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.mapapi.model.LatLng;

/**
 * 使 LatLng 可以被 Intent 或 Bundle 当作参数传递
 * @author peter
 *
 */
public class LatLngParcel implements Parcelable {
	private double lat;
	private double lng;

	public LatLngParcel(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}

	public LatLngParcel(LatLng point) {
		lat = point.latitude;
		lng = point.longitude;
	}

	public double getLatitude() {
		return this.lat;	
	}

	public double getLongitude() {
		return this.lng;
	}

	public LatLng getLatLng() {
		return new LatLng(lat, lng);
	}

	public void setLatitude(double lat) {
		this.lat = lat;
	}

	public void setLongitude(double lng) {
		this.lng = lng;
	}

	@Override
	public String toString() {
		return new StringBuffer("{")
			.append("lat:").append(getLatitude())
			.append(", lng:").append(getLongitude())
			.append("}").toString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<LatLngParcel> CREATOR = new Creator<LatLngParcel>() {

		@Override
		public LatLngParcel[] newArray(int size) {
			return new LatLngParcel[size];
		}

		@Override
		public LatLngParcel createFromParcel(Parcel source) {
			double lat = source.readDouble();
			double lng = source.readDouble();
			return new LatLngParcel(lat, lng);
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeDouble(getLatitude());
		dest.writeDouble(getLongitude());
	}

	public static LatLngParcel[] convert(LatLng[] geoPoint) {
		LatLngParcel[] gpw = new LatLngParcel[geoPoint.length];
		for( int i = 0; i < geoPoint.length; i++ ) {
			gpw[i] = new LatLngParcel(geoPoint[i]);
		}
		return gpw;
	}
}