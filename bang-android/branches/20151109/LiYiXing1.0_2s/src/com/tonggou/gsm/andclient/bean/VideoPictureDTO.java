package com.tonggou.gsm.andclient.bean;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
/**
 * 视频数据DTO
 * @author peter
 *
 */
public class VideoPictureDTO implements Parcelable, Serializable {

	private static final long serialVersionUID = 5127392262246682492L;

	/** 数据库名 */
	public static final String DB_NAME = "tonggou_video_record.db";
	/** 数据库版本号 */
	public static final int DB_VERSION = 1;
	/** 视频 id */
	public static final String COLUMN_VIDEO_ID = "video_id";
	/** 车牌号 */
	public static final String COLUMN_VEHICLE_NO = "vehicleNo";
	/** 视频日期 */
	public static final String COLUMN_VIDEO_DATE = "videoDate";
	/** 视频地址 */
	public static final String COLUMN_VIDEO_ADDR = "videoAddr";
	/** 视频照片地址*/
	public static final String COLUMN_PICTURE_PATH = "picturePath";
	/** 视频路径地址*/
	public static final String COLUMN_VIDEO_PATH = "videoPath";
	/** 视频路径地址*/
	public static final String COLUMN_VIDEO_TYPE = "videoType";
	/** 视频路径地址*/
	public static final String COLUMN_VIDEO_UPLOAD_PROGRESS = "videoUploadProgress";
	/** 视频路径地址*/
	public static final String COLUMN_STATUS = "status";

	@DatabaseField(columnName="_id", generatedId=true)
	@Expose(deserialize=false, serialize=false)
	private long _id;	// 本地数据库中的 id

	@DatabaseField(columnName=COLUMN_VIDEO_TYPE, dataType=DataType.INTEGER)
	private int type;
	@DatabaseField(columnName=COLUMN_VIDEO_DATE, dataType=DataType.LONG)
	private long date;
	@DatabaseField(columnName=COLUMN_VIDEO_PATH, dataType=DataType.STRING)
	private String videoStr;
	@DatabaseField(columnName=COLUMN_VIDEO_UPLOAD_PROGRESS, dataType=DataType.INTEGER)
	private int progress;
	@DatabaseField(columnName=COLUMN_VIDEO_ID, dataType=DataType.LONG)
    private long vid;
	@DatabaseField(columnName=COLUMN_PICTURE_PATH, dataType=DataType.STRING)
	private String pictureStr;
	@DatabaseField(columnName=COLUMN_VIDEO_ADDR, dataType=DataType.STRING)
	private String place;

	private String[] picture;
	private String[] video;

	@Expose(serialize=false, deserialize=false)
	public static Parcelable.Creator<VideoPictureDTO> CREATOR = new Creator<VideoPictureDTO>() {

		@Override
		public VideoPictureDTO[] newArray(int size) {
			return new VideoPictureDTO[size];
		}

		@Override
		public VideoPictureDTO createFromParcel(Parcel source) {
			return new VideoPictureDTO(source);
		}
	};

	public VideoPictureDTO() {
	}

	public VideoPictureDTO(Parcel in) {
		type = in.readInt();
		date = in.readLong();
		in.readStringArray(video);
		videoStr = arrayToString(video);
		progress = in.readInt();
		vid = in.readLong();
		in.readStringArray(picture);
		pictureStr = arrayToString(picture);
		place = in.readString();
	}

	@Override
	public int describeContents() {
		return 1;
	}

	@Override
	public void writeToParcel(Parcel dest, int flag) {
		dest.writeInt(type);
		dest.writeLong(date);
		dest.writeStringArray(video);
		dest.writeInt(progress);
		dest.writeLong(vid);
		dest.writeStringArray(picture);
		dest.writeString(place);
	}

	public String getRecordPlace() {
		return place;
	}

	public void setRecordPlace(String area) {
		this.place = area;
	}
	
	public String  getPicture() {
		return pictureStr;
	}

	public void setPicture(String str) {
		this.pictureStr = str;
	}

	public String[] getPictureArray() {
		return picture;
	}

	public void setPictureArray(String[] path) {
		this.picture = path;
		setPicture(arrayToString(this.picture));
	}

	public String[] getVideoPath() {
		return video;
	}
	public void setVideoPath(String[] path) {
		this.video = path;
	}

	public int getUploadProgress() {
		return progress;
	}

	public void setUploadProgress(int progress) {
		this.progress = progress;
	}

	public int getVideoType() {
		return type;
	}

	public void setVideoType(int type) {
		this.type = type;
	}

	public Long getRecordDate() {
 		return date;
	}

	public void setRecordDate(long date) {
		this.date = date;
	}

	public long getVideoId() {
		return vid;
	}

	public void setVideoId(long id) {
		vid = id;
	}

	public String arrayToString(String[] strArray) {
		String str = null;

		if (null == strArray)
			return str;

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < strArray.length; i++) {
			buffer.append(strArray[i]).append(",");
		}

		str = buffer.substring(0, buffer.length() - 1);

		return str;
	}

	public String[] stringToArray(String str) {
		String strArray[] = null;

		if (null == str)
			return strArray;

		strArray = str.split(",");

		return strArray;
	}
}