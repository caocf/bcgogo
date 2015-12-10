package com.tonggou.andclient.vo;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class VehicleInfo implements Parcelable{
	private String status;
	private String userNo;
	private String obdSN;
	private String mobile;
	private String email;
	private String contact;
	private String nextMaintainMileage;
	private String nextMaintainTime;
	private String nextExamineTime;
	private String nextInsuranceTime;
	private String oilWear;
	private String mileage;
	private String reportTime;
	private String engineNo;			// 发动机号
	private String vehicleFrameNo;
	private String vehicleId;
	private String vehicleVin;			// 车辆唯一标识号、车架号
	private String vehicleNo;
	private String vehicleModel;
	private String vehicleModelId;
	private String vehicleBrand;
	private String vehicleBrandId;
	private String currentMileage;
	private String isDefault;
	private String batteryVoltage;
	private String engineCoolantTemperature;
	private String oilMass;
	private String oilWearPerHundred;
	private String instantOilWear;
	private String appUserId;
	private String recommendShopId;
	private String registNo;			// 登记证书号 
	
	public String getRecommendShopId() {
		return recommendShopId;
	}
	public void setRecommendShopId(String recommendShopId) {
		this.recommendShopId = recommendShopId;
	}
	public String getRecommendShopName() {
		return recommendShopName;
	}
	public void setRecommendShopName(String recommendShopName) {
		this.recommendShopName = recommendShopName;
	}
	private String recommendShopName;

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUserNo() {
		return userNo;
	}
	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}
	public String getObdSN() {
		return obdSN;
	}
	public void setObdSN(String obdSN) {
		this.obdSN = obdSN;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getNextMaintainMileage() {
		return nextMaintainMileage;
	}
	public void setNextMaintainMileage(String nextMaintainMileage) {
		this.nextMaintainMileage = nextMaintainMileage;
	}
	public String getNextMaintainTime() {
		return nextMaintainTime;
	}
	public void setNextMaintainTime(String nextMaintainTime) {
		this.nextMaintainTime = nextMaintainTime;
	}
	public String getNextExamineTime() {
		return nextExamineTime;
	}
	public void setNextExamineTime(String nextExamineTime) {
		this.nextExamineTime = nextExamineTime;
	}
	public String getNextInsuranceTime() {
		return nextInsuranceTime;
	}
	public void setNextInsuranceTime(String nextInsuranceTime) {
		this.nextInsuranceTime = nextInsuranceTime;
	}
	public String getOilWear() {
		return oilWear;
	}
	public void setOilWear(String oilWear) {
		this.oilWear = oilWear;
	}
	public String getMileage() {
		return mileage;
	}
	public void setMileage(String mileage) {
		this.mileage = mileage;
	}
	public String getReportTime() {
		return reportTime;
	}
	public void setReportTime(String reportTime) {
		this.reportTime = reportTime;
	}
	public String getEngineNo() {
		return engineNo;
	}
	public void setEngineNo(String engineNo) {
		this.engineNo = engineNo;
	}
	public String getVehicleFrameNo() {
		return vehicleFrameNo;
	}
	public void setVehicleFrameNo(String vehicleFrameNo) {
		this.vehicleFrameNo = vehicleFrameNo;
	}
	public String getVehicleId() {
		return vehicleId;
	}
	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}
	public String getVehicleVin() {
		return vehicleVin;
	}
	public void setVehicleVin(String vehicleVin) {
		this.vehicleVin = vehicleVin;
	}
	public String getVehicleNo() {
		return vehicleNo;
	}
	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}
	public String getVehicleModel() {
		return vehicleModel;
	}
	public void setVehicleModel(String vehicleModel) {
		this.vehicleModel = vehicleModel;
	}
	public String getVehicleModelId() {
		return vehicleModelId;
	}
	public void setVehicleModelId(String vehicleModelId) {
		this.vehicleModelId = vehicleModelId;
	}
	public String getVehicleBrand() {
		return vehicleBrand;
	}
	public void setVehicleBrand(String vehicleBrand) {
		this.vehicleBrand = vehicleBrand;
	}
	public String getVehicleBrandId() {
		return vehicleBrandId;
	}
	public void setVehicleBrandId(String vehicleBrandId) {
		this.vehicleBrandId = vehicleBrandId;
	}
	public String getCurrentMileage() {
		return currentMileage;
	}
	public void setCurrentMileage(String currentMileage) {
		this.currentMileage = currentMileage;
	}
	public String getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}
	public String getBatteryVoltage() {
		return batteryVoltage;
	}
	public void setBatteryVoltage(String batteryVoltage) {
		this.batteryVoltage = batteryVoltage;
	}
	public String getEngineCoolantTemperature() {
		return engineCoolantTemperature;
	}
	public void setEngineCoolantTemperature(String engineCoolantTemperature) {
		this.engineCoolantTemperature = engineCoolantTemperature;
	}
	public String getOilMass() {
		return oilMass;
	}
	public void setOilMass(String oilMass) {
		this.oilMass = oilMass;
	}
	public String getOilWearPerHundred() {
		return oilWearPerHundred;
	}
	public void setOilWearPerHundred(String oilWearPerHundred) {
		this.oilWearPerHundred = oilWearPerHundred;
	}
	public String getInstantOilWear() {
		return instantOilWear;
	}
	public void setInstantOilWear(String instantOilWear) {
		this.instantOilWear = instantOilWear;
	}
	public String getAppUserId() {
		return appUserId;
	}
	public void setAppUserId(String appUserId) {
		this.appUserId = appUserId;
	}
	public String getRegistNo() {
		return registNo;
	}
	public void setRegistNo(String registNo) {
		this.registNo = registNo;
	}
	
	@Override
	public boolean equals(Object o) {
		if( !(o instanceof VehicleInfo) ) {
			return false;
		}
		VehicleInfo other = (VehicleInfo)o;
		return other.getVehicleNo().equals(vehicleNo) && !TextUtils.isEmpty( vehicleNo );
	}
	
	@Override
	public int hashCode() {
		return (vehicleNo + "").hashCode();
	}
	
	public VehicleInfo(){}
	private VehicleInfo(Parcel source){
		status=source.readString();
		userNo=source.readString();
		obdSN=source.readString();
		mobile=source.readString();
		email=source.readString();
		contact=source.readString();
		nextMaintainMileage=source.readString();
		nextMaintainTime=source.readString();
		nextExamineTime=source.readString();
		nextInsuranceTime=source.readString();
		oilWear=source.readString();
		mileage=source.readString();
		reportTime=source.readString();
		engineNo=source.readString();
		vehicleFrameNo=source.readString();
		vehicleId=source.readString();
		vehicleVin=source.readString();
		vehicleNo=source.readString();
		vehicleModel=source.readString();
		vehicleModelId=source.readString();
		vehicleBrand=source.readString();
		vehicleBrandId=source.readString();
		currentMileage=source.readString();
		isDefault=source.readString();
		batteryVoltage=source.readString();
		engineCoolantTemperature=source.readString();
		oilMass=source.readString();
		oilWearPerHundred=source.readString();
		instantOilWear=source.readString();
		appUserId=source.readString();
		recommendShopId=source.readString();
		registNo=source.readString();
	}
	
	public static final Parcelable.Creator<VehicleInfo> CREATOR = new Parcelable.Creator<VehicleInfo>() {

		@Override
		public VehicleInfo createFromParcel(Parcel source) {
			return new VehicleInfo(source);
		}

		@Override
		public VehicleInfo[] newArray(int size) {
			return new VehicleInfo[size];
		}
	};
	
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(status);
		dest.writeString(userNo);
		dest.writeString(obdSN);
		dest.writeString(mobile);
		dest.writeString(email);
		dest.writeString(contact);
		dest.writeString(nextMaintainMileage);
		dest.writeString(nextMaintainTime);
		dest.writeString(nextExamineTime);
		dest.writeString(nextInsuranceTime);
		dest.writeString(oilWear);
		dest.writeString(mileage);
		dest.writeString(reportTime);
		dest.writeString(engineNo);
		dest.writeString(vehicleFrameNo);
		dest.writeString(vehicleId);
		dest.writeString(vehicleVin);
		dest.writeString(vehicleNo);
		dest.writeString(vehicleModel);
		dest.writeString(vehicleModelId);
		dest.writeString(vehicleBrand);
		dest.writeString(vehicleBrandId);
		dest.writeString(currentMileage);
		dest.writeString(isDefault);
		dest.writeString(batteryVoltage);
		dest.writeString(engineCoolantTemperature);
		dest.writeString(oilMass);
		dest.writeString(oilWearPerHundred);
		dest.writeString(instantOilWear);
		dest.writeString(appUserId);
		dest.writeString(recommendShopId);
		dest.writeString(registNo);
	}
}
