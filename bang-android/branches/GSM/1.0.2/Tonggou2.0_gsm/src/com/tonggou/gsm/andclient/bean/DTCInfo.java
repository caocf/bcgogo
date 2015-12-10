
package com.tonggou.gsm.andclient.bean;

import android.os.Parcel;
import android.os.Parcelable;


public class DTCInfo implements Parcelable {
	
	public static final Parcelable.Creator<DTCInfo> CREATOR = new Creator<DTCInfo>() {
		
		@Override
		public DTCInfo[] newArray(int size) {
			return new DTCInfo[size];
		}
		
		@Override
		public DTCInfo createFromParcel(Parcel source) {
			return new DTCInfo(source);
		}
	};
	
    private String id;
    private String content;
    private String status;
    private String errorCode;
    private String category;
    private String appUserNo;
    private long reportTime;
    private String statusStr;
    private String obdId;
    private String appVehicleId;
    private String lastStatus;
    private long lastOperateTime;
    private String backgroundInfo;

    public DTCInfo() {
    }
    
    DTCInfo(Parcel in) {
	 	id = in.readString();
	    content = in.readString();
	    status = in.readString();
	    errorCode = in.readString();
	    category = in.readString();
	    appUserNo = in.readString();
	    reportTime = in.readLong();
	    statusStr = in.readString();
	    obdId = in.readString();
	    appVehicleId = in.readString();
	    lastStatus = in.readString();
	    lastOperateTime = in.readLong();
	    backgroundInfo = in.readString();
    }
    
    @Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(content);
		dest.writeString(status);
		dest.writeString(errorCode);
		dest.writeString(category);
		dest.writeString(appUserNo);
		dest.writeLong(reportTime);
		dest.writeString(statusStr);
		dest.writeString(obdId);
		dest.writeString(appVehicleId);
		dest.writeString(lastStatus);
		dest.writeLong(lastOperateTime);
		dest.writeString(backgroundInfo);
	}
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAppUserNo() {
        return appUserNo;
    }

    public void setAppUserNo(String appUserNo) {
        this.appUserNo = appUserNo;
    }

    public Long getReportTime() {
        return reportTime;
    }

    public void setReportTime(Long reportTime) {
        this.reportTime = reportTime;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public String getObdId() {
        return obdId;
    }

    public void setObdId(String obdId) {
        this.obdId = obdId;
    }

    public String getAppVehicleId() {
        return appVehicleId;
    }

    public void setAppVehicleId(String appVehicleId) {
        this.appVehicleId = appVehicleId;
    }

    public String getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(String lastStatus) {
        this.lastStatus = lastStatus;
    }

    public Long getLastOperateTime() {
        return lastOperateTime;
    }

    public void setLastOperateTime(Long lastOperateTime) {
        this.lastOperateTime = lastOperateTime;
    }

    public String getBackgroundInfo() {
        return backgroundInfo;
    }

    public void setBackgroundInfo(String backgroundInfo) {
        this.backgroundInfo = backgroundInfo;
    }

}
