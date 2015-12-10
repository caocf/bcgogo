package com.tonggou.andclient.vo;

import android.os.Parcel;
import android.os.Parcelable;

public class SamplePoint implements Parcelable {
	private double latitude;
	private double longitude;
	private long savedTime;

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public long getSavedTime() {
		return savedTime;
	}

	public void setSavedTime(long savedTime) {
		this.savedTime = savedTime;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public SamplePoint() {
	}

	public SamplePoint(Parcel source) {
		latitude = source.readDouble();
		longitude = source.readDouble();
		savedTime = source.readLong();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
		dest.writeLong(savedTime);

	}

	public static final Parcelable.Creator<SamplePoint> CREATOR = new Parcelable.Creator<SamplePoint>() {
		@Override
		public SamplePoint createFromParcel(Parcel source) {
			return new SamplePoint(source);
		}

		@Override
		public SamplePoint[] newArray(int size) {
			return new SamplePoint[size];
		}
	};

	@Override
	public String toString() {
		return latitude + "," + longitude + "," + savedTime;
	}
}
