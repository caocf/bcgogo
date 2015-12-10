package com.tonggou.gsm.andclient.bean;

import java.io.Serializable;

import android.text.TextUtils;

public class AppVehicleDTO implements Serializable {

	private static final long serialVersionUID = 3218279776596411800L;

	private String status;
	private String vehicleId; // 汽车ID
	private String vehicleNo; // 车牌号
	private String vehicleModel; // 车型
	private String vehicleBrand; // 品牌
	private String oilPrice; // 油价
	private float currentMileage; // 当前里程
	private float maintainPeriod; // 保养周期
	private float lastMaintainMileage; // 上次保养里程
	private String nextMaintainTimeStr; // 下次保养时间
	private String nextExamineTimeStr; // 下次验车时间
	private long nextMaintainTime;
	private long nextExamineTime;
	private double coordinateLat; // 经度
	private double coordinateLon; // 纬度
	private String registNo;
	private String engineNo;
	private String vehicleVin;
	private String juheCityCode;
	private String juheCityName;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
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

	public String getVehicleBrand() {
		return vehicleBrand;
	}

	public void setVehicleBrand(String vehicleBrand) {
		this.vehicleBrand = vehicleBrand;
	}

	public String getOilPrice() {
		return oilPrice;
	}

	public void setOilPrice(String oilPrice) {
		this.oilPrice = oilPrice;
	}

	public int getCurrentMileage() {
		return Float.valueOf(currentMileage).intValue();
	}

	public void setCurrentMileage(float currentMileage) {
		this.currentMileage = currentMileage;
	}

	public int getMaintainPeriod() {
		return Float.valueOf(maintainPeriod).intValue();
	}

	public void setMaintainPeriod(int maintainPeriod) {
		this.maintainPeriod = maintainPeriod;
	}

	public int getLastMaintainMileage() {
		return Float.valueOf(lastMaintainMileage).intValue();
	}

	public void setLastMaintainMileage(float lastMaintainMileage) {
		this.lastMaintainMileage = lastMaintainMileage;
	}

	public String getNextMaintainTimeStr() {
		return nextMaintainTimeStr;
	}

	public void setNextMaintainTimeStr(String nextMaintainTimeStr) {
		this.nextMaintainTimeStr = nextMaintainTimeStr;
	}

	public String getNextExamineTimeStr() {
		return nextExamineTimeStr;
	}

	public void setNextExamineTimeStr(String nextExamineTimeStr) {
		this.nextExamineTimeStr = nextExamineTimeStr;
	}

	public long getNextMaintainTime() {
		return nextMaintainTime;
	}

	public void setNextMaintainTime(long nextMaintainTime) {
		this.nextMaintainTime = nextMaintainTime;
	}

	public long getNextExamineTime() {
		return nextExamineTime;
	}

	public void setNextExamineTime(long nextExamineTime) {
		this.nextExamineTime = nextExamineTime;
	}

	public double getCoordinateLat() {
		return coordinateLat;
	}

	public void setCoordinateLat(double coordinateLat) {
		this.coordinateLat = coordinateLat;
	}

	public double getCoordinateLon() {
		return coordinateLon;
	}

	public void setCoordinateLon(double coordinateLon) {
		this.coordinateLon = coordinateLon;
	}

	public String getRegistNo() {
		return registNo;
	}

	public void setRegistNo(String registNo) {
		this.registNo = registNo;
	}

	public String getEngineNo() {
		return engineNo;
	}

	public void setEngineNo(String engineNo) {
		this.engineNo = engineNo;
	}

	public String getVehicleVin() {
		return vehicleVin;
	}

	public void setVehicleVin(String vehicleVin) {
		this.vehicleVin = vehicleVin;
	}
	
	public String getJuheCityCode() {
		return juheCityCode;
	}

	public void setJuheCityCode(String juheCityCode) {
		this.juheCityCode = juheCityCode;
	}

	public String getJuheCityName() {
		return juheCityName;
	}

	public void setJuheCityName(String juheCityName) {
		this.juheCityName = juheCityName;
	}

	/**
	 * 是否满足查询违章的条件
	 * @return
	 */
	public boolean isCanQueryViolation() {
		return !TextUtils.isEmpty(juheCityCode)
				&& !TextUtils.isEmpty(registNo)
				&& !TextUtils.isEmpty(engineNo)
				&& !TextUtils.isEmpty(vehicleVin);
	}
}
