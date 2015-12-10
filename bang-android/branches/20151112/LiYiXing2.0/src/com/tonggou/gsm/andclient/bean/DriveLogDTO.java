package com.tonggou.gsm.andclient.bean;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tonggou.gsm.andclient.bean.type.DriveLogStatus;

@DatabaseTable(tableName="drive_log")
public class DriveLogDTO implements Parcelable, Serializable {

	private static final long serialVersionUID = -761163060357973898L;
	
	/** 数据库名 */
	public static final String DB_NAME = "tonggou_drive_log.db";
	/** 数据库版本号 */
	public static final int DB_VERSION = 1;

	/** 轨迹 id */
	public static final String COLUMN_LOG_ID = "log_id";
	/** 状态 */
	public static final String COLUMN_STATUS = "status";
	/** 用户名 */
	public static final String COLUMN_USER_NO = "userNo";
	/** 车牌号 */
	public static final String COLUMN_VEHICLE_NO = "vehicleNo";
	/** 开始时间 */
	public static final String COLUMN_START_TIME = "startTime";
	/** 结束时间 */
	public static final String COLUMN_END_TIME = "endTime";
	/** 行车轨迹的点*/
	public static final String COLUMN_PLACE_NOTES = "placeNotes";
	
	@DatabaseField(columnName="_id", generatedId=true)
	@Expose(deserialize=false, serialize=false)
	private long _id;	// 本地数据库中的 id
	
	@DatabaseField(columnName=COLUMN_LOG_ID, dataType=DataType.STRING)
	private String id;	// 真正的 id

	@DatabaseField(columnName=COLUMN_STATUS, dataType=DataType.ENUM_STRING)
    private DriveLogStatus status = DriveLogStatus.DISABLE;

	@DatabaseField(columnName=COLUMN_USER_NO, dataType=DataType.STRING)
    private String appUserNo;

	@DatabaseField(columnName=COLUMN_VEHICLE_NO, dataType=DataType.STRING)
    private String vehicleNo;

	@DatabaseField(dataType=DataType.DOUBLE)
    private double startLat;

	@DatabaseField(dataType=DataType.DOUBLE)
    private double startLon;
	
	@DatabaseField(dataType=DataType.LONG_STRING)
    private String startPlace;
	
	@DatabaseField(dataType=DataType.DOUBLE)
    private double endLat;
	
	@DatabaseField(dataType=DataType.DOUBLE)
    private double endLon;
	
	@DatabaseField(dataType=DataType.LONG_STRING)
    private String endPlace;
	
	@DatabaseField(dataType=DataType.LONG)
    private long lastUpdateTime;
	
	@DatabaseField(columnName=COLUMN_PLACE_NOTES, dataType=DataType.LONG_STRING)
    private String placeNotes;
	
	@DatabaseField(dataType=DataType.FLOAT)
	private float oilWear;
	
	@DatabaseField(dataType=DataType.FLOAT)
    private float oilPrice;
	
	@DatabaseField(dataType=DataType.LONG)
    private long travelTime;
	
	@DatabaseField(columnName=COLUMN_START_TIME, dataType=DataType.LONG)
    private long startTime;
	
	@DatabaseField(columnName=COLUMN_END_TIME, dataType=DataType.LONG)
    private long endTime;
	
	@DatabaseField(dataType=DataType.FLOAT)
    private float distance = 0;
	
	@DatabaseField(dataType=DataType.FLOAT)
    private float totalOilMoney = 0;
	
	@DatabaseField(dataType=DataType.FLOAT)
    private float oilCost = 0;
	
	@DatabaseField(dataType=DataType.STRING)
    private String oilKind;
    
    public DriveLogDTO() {
    }
    
    DriveLogDTO(Parcel in) {
		id = in.readString();
	    status = (DriveLogStatus) in.readSerializable();
	    appUserNo = in.readString();
	    vehicleNo = in.readString();
	    startLat = in.readDouble();
	    startLon = in.readDouble();
	    startPlace = in.readString();
	    endLat = in.readDouble();
	    endLon = in.readDouble();
	    endPlace = in.readString();
	    lastUpdateTime = in.readLong();
	    placeNotes = in.readString();
		oilWear = in.readFloat();
		oilPrice = in.readFloat();
		travelTime = in.readLong();
		startTime = in.readLong();
		endTime = in.readLong();
		distance = in.readFloat();
		totalOilMoney = in.readFloat();
		oilCost = in.readFloat();
		oilKind = in.readString();
	}
    
    @Expose(serialize=false, deserialize=false)
    public static final Parcelable.Creator<DriveLogDTO> CREATOR = new Creator<DriveLogDTO>() {
		
		@Override
		public DriveLogDTO[] newArray(int size) {
			return new DriveLogDTO[size];
		}
		
		@Override
		public DriveLogDTO createFromParcel(Parcel source) {
			return new DriveLogDTO(source);
		}
	};
	
	@Override
	public int describeContents() {
		return 1;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeSerializable(status);
		dest.writeString(appUserNo);
		dest.writeString(vehicleNo);
		dest.writeDouble(startLat);
		dest.writeDouble(startLon);
	    dest.writeString(startPlace);
	    dest.writeDouble(endLat);
	    dest.writeDouble(endLon);
	    dest.writeString(endPlace);
	    dest.writeLong(lastUpdateTime);
	    dest.writeString(placeNotes);
		dest.writeFloat(oilWear);
		dest.writeFloat(oilPrice);
		dest.writeLong(travelTime);
		dest.writeLong(startTime);
		dest.writeLong(endTime);
		dest.writeFloat(distance);
		dest.writeFloat(totalOilMoney);
		dest.writeString(oilKind);
	}
	
	public long get_id() {
		return _id;
	}

	public void set_id(long _id) {
		this._id = _id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DriveLogStatus getStatus() {
		return status;
	}

	public void setStatus(DriveLogStatus status) {
		this.status = status;
	}

	public String getAppUserNo() {
		return appUserNo;
	}

	public void setAppUserNo(String appUserNo) {
		this.appUserNo = appUserNo;
	}

	public String getVehicleNo() {
		return vehicleNo;
	}

	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}

	public double getStartLat() {
		return startLat;
	}

	public void setStartLat(double startLat) {
		this.startLat = startLat;
	}

	public double getStartLon() {
		return startLon;
	}

	public void setStartLon(double startLon) {
		this.startLon = startLon;
	}

	public String getStartPlace() {
		return startPlace;
	}

	public void setStartPlace(String startPlace) {
		this.startPlace = startPlace;
	}

	public double getEndLat() {
		return endLat;
	}

	public void setEndLat(double endLat) {
		this.endLat = endLat;
	}

	public double getEndLon() {
		return endLon;
	}

	public void setEndLon(double endLon) {
		this.endLon = endLon;
	}

	public String getEndPlace() {
		return endPlace;
	}

	public void setEndPlace(String endPlace) {
		this.endPlace = endPlace;
	}

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	
	public String getPlaceNotes() {
		return placeNotes;
	}

	public void setPlaceNotes(String placeNotes) {
		this.placeNotes = placeNotes;
	}
	
	public float getOilWear() {
		return oilWear;
	}
	public void setOilWear(float oilWear) {
		this.oilWear = oilWear;
	}
	public float getOilPrice() {
		return oilPrice;
	}
	public void setOilPrice(float oilPrice) {
		this.oilPrice = oilPrice;
	}
	public long getTravelTime() {
		return travelTime;
	}
	public void setTravelTime(long travelTime) {
		this.travelTime = travelTime;
	}
	
	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public float getDistance() {
		return distance;
	}
	public void setDistance(float distance) {
		this.distance = distance;
	}
	public float getTotalOilMoney() {
		return totalOilMoney;
	}
	public void setTotalOilMoney(float totalOilMoney) {
		this.totalOilMoney = totalOilMoney;
	}
	public float getOilCost() {
		return oilCost;
	}
	public void setOilCost(float oilCost) {
		this.oilCost = oilCost;
	}
	public Object getOilKind() {
		return oilKind;
	}

	public void setOilKind(String oilKind) {
		this.oilKind = oilKind;
	}
}
