package com.tonggou.andclient.vo;

import java.io.Serializable;

public class DrivingJournalItem implements Serializable {
	private static final long serialVersionUID = 8202533025890171645L;
	
	/**
	 * 没写进数据库用*注释
	 */
	private String appDriveLogId; // app里记录的行车日志的Id
	private String appUserNo; // 用户名
	private String vehicleNo; // 当前日志的车牌号
	private Long startTime; // 开始时间
	private String startLat; // 开始维度
	private String startLon; // 开始经度
	private String startPlace; // 开始地址
	private Long endTime; // 结束时间
	private String endLat; // 结束维度
	private String endLon; // 结束经度
	private String endPlace; // 结束地址
	private long travelTime; // 行驶时间
	private Double distance; // 路程 （千米）
	private Double oilWear; // 油耗
	private String oilKind; // 油品
	private Double oilPrice; // 油价
	private Double totalOilMoney; // 油钱
	private String placeNotes; // 踩点信息
	private long lastUpdateTime; // 最新更新时间
	private String status; // 日志的状态
	private String appPlatform; /**系统平台( ANDROID,IOS)**/

	public String getAppUserNo() {
		return appUserNo;
	}

	public void setAppUserNo(String appUserNo) {
		this.appUserNo = appUserNo;
	}

	public String getAppDriveLogId() {
		return appDriveLogId;
	}

	public void setAppDriveLogId(String appDriveLogId) {
		this.appDriveLogId = appDriveLogId;
	}

	public String getVehicleNo() {
		return vehicleNo;
	}

	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public String getStartLat() {
		return startLat;
	}

	public void setStartLat(String startLat) {
		this.startLat = startLat;
	}

	public String getStartLon() {
		return startLon;
	}

	public void setStartLon(String startLon) {
		this.startLon = startLon;
	}

	public String getStartPlace() {
		return startPlace;
	}

	public void setStartPlace(String startPlace) {
		this.startPlace = startPlace;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public String getEndLat() {
		return endLat;
	}

	public void setEndLat(String endLat) {
		this.endLat = endLat;
	}

	public String getEndLon() {
		return endLon;
	}

	public void setEndLon(String endLon) {
		this.endLon = endLon;
	}

	public String getEndPlace() {
		return endPlace;
	}

	public void setEndPlace(String endPlace) {
		this.endPlace = endPlace;
	}

	public long getTravelTime() {
		return travelTime;
	}

	public void setTravelTime(long travelTime) {
		this.travelTime = travelTime;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public Double getOilWear() {
		return oilWear;
	}

	public void setOilWear(Double oilWear) {
		this.oilWear = oilWear;
	}

	public Double getOilPrice() {
		return oilPrice;
	}

	public void setOilPrice(Double oilPrice) {
		this.oilPrice = oilPrice;
	}

	public String getOilKind() {
		return oilKind;
	}

	public void setOilKind(String oilKind) {
		this.oilKind = oilKind;
	}

	public Double getTotalOilMoney() {
		return totalOilMoney;
	}

	public void setTotalOilMoney(Double totalOilMoney) {
		this.totalOilMoney = totalOilMoney;
	}

	public String getPlaceNotes() {
		return placeNotes;
	}

	public void setPlaceNotes(String placeNotes) {
		this.placeNotes = placeNotes;
	}

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAppPlatform() {
		return appPlatform;
	}

	public void setAppPlatform(String appPlatform) {
		this.appPlatform = appPlatform;
	}

	@Override
	public String toString() {
		return "DrivingJournalItem [appDriveLogId=" + appDriveLogId + ", appUserNo=" + appUserNo
				+ ", vehicleNo=" + vehicleNo + ", startTime=" + startTime + ", startLat="
				+ startLat + ", startLon=" + startLon + ", startPlace=" + startPlace + ", endTime="
				+ endTime + ", endLat=" + endLat + ", endLon=" + endLon + ", endPlace=" + endPlace
				+ ", travelTime=" + travelTime + ", distance=" + distance + ", oilWear=" + oilWear
				+ ", oilKind=" + oilKind + ", oilPrice=" + oilPrice + ", totalOilMoney="
				+ totalOilMoney + ", placeNotes=" + placeNotes + ", lastUpdateTime="
				+ lastUpdateTime + ", status=" + status + ", appPlatform=" + appPlatform + "]";
	}

}
