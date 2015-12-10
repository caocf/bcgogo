package com.tonggou.gsm.andclient.bean;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.tonggou.gsm.andclient.bean.type.JuheCityStatus;

public class Area implements Parcelable {

	public static final Parcelable.Creator<Area> CREATOR = new Creator<Area>() {

		@Override
		public Area[] newArray(int size) {
			return new Area[size];
		}

		@Override
		public Area createFromParcel(Parcel source) {
			return new Area(source);
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(code);
		dest.writeList(children);
		dest.writeString(juheStatus.toString());
	}
	
	private String name;
	@SerializedName("juheCityCode")
	private String code;
	private ArrayList<Area> children;
	private JuheCityStatus juheStatus;

	@SuppressWarnings("unchecked")
	public Area(Parcel in) {
		name = in.readString();
		code = in.readString();
		children = in.readArrayList(Area.class.getClassLoader());
		juheStatus = JuheCityStatus.valueOf(in.readString());
	}
	
	public Area() {
		juheStatus = JuheCityStatus.IN_ACTIVE;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public ArrayList<Area> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<Area> children) {
		this.children = children;
	}

	public JuheCityStatus getJuheStatus() {
		return juheStatus;
	}

	public void setJuheStatus(JuheCityStatus juheStatus) {
		this.juheStatus = juheStatus;
	}
}
