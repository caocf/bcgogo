package com.tonggou.gsm.andclient.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.platform.comapi.basestruct.GeoPoint;

/**
 * 使 GeoPoint 可以被 Intent 或 Bundle 当作参数传递
 * @author lwz
 *
 */
public class GeoPointParcel extends GeoPoint implements Parcelable {

	public GeoPointParcel(int latE6, int lngE6) {
		super(latE6, lngE6);
	}
	
	public GeoPointParcel(GeoPoint point) {
		super(point.getLatitudeE6(), point.getLongitudeE6());
	}
	
	@Override
	public String toString() {
		return new StringBuffer("{")
			.append("lat:").append(getLatitudeE6())
			.append(", lng:").append(getLongitudeE6())
			.append("}").toString();
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<GeoPointParcel> CREATOR = new Creator<GeoPointParcel>() {
		
		@Override
		public GeoPointParcel[] newArray(int size) {
			return new GeoPointParcel[size];
		}
		
		@Override
		public GeoPointParcel createFromParcel(Parcel source) {
			int lat = source.readInt();
			int lng = source.readInt();
			return new GeoPointParcel(lat, lng);
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeInt( getLatitudeE6() );
		dest.writeInt( getLongitudeE6() );
	}
	
	public static GeoPointParcel[] convert(GeoPoint[] geoPoint) {
		GeoPointParcel[] gpw = new GeoPointParcel[geoPoint.length];
		for( int i=0; i<geoPoint.length; i++ ) {
			gpw[i] = new GeoPointParcel(geoPoint[i]);
		}
		return gpw;
	}

}
