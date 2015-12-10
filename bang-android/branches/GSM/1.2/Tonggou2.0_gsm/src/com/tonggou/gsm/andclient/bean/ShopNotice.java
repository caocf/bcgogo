package com.tonggou.gsm.andclient.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ShopNotice implements Parcelable {
    private String id;
    @SerializedName("imageUrl")
    private String imgUrl;
    private String description;
    @SerializedName("editDate")
    private long timestamp;
    private String title;
    private String beginDateStr;
    private String endDateStr;
    
	public ShopNotice() {
		super();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBeginDateStr() {
		return beginDateStr;
	}
	public void setBeginDateStr(String beginDateStr) {
		this.beginDateStr = beginDateStr;
	}
	public String getEndDateStr() {
		return endDateStr;
	}
	public void setEndDateStr(String endDateStr) {
		this.endDateStr = endDateStr;
	}



	public static final Creator<ShopNotice> CREATOR = new Creator<ShopNotice>() {
		
		@Override
		public ShopNotice[] newArray(int size) {
			return new ShopNotice[size];
		}
		
		@Override
		public ShopNotice createFromParcel(Parcel source) {
			return new ShopNotice(source);
		}
	};
	
	ShopNotice(Parcel in) {
		id = in.readString();
		imgUrl = in.readString();
		description = in.readString();
		timestamp = in.readLong();
		title = in.readString();
		beginDateStr = in.readString();
		endDateStr = in.readString();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(imgUrl);
		dest.writeString(description);
		dest.writeLong(timestamp);
		dest.writeString(title);
		dest.writeString(beginDateStr);
		dest.writeString(endDateStr);
	}
}
