package com.tonggou.andclient.vo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.tonggou.andclient.vo.type.FaultCodeStatusType;

/**
 * 车况消息项
 * 
 * @author think
 * 
 */
public class CarCondition implements Serializable {
	
	private static final long serialVersionUID = -1514666602521716222L;
	
	private String alarmId;
	private String userID; // 用于不同用户账号查找
	private String name = "UNREAD"; // 是否读过
	private String type; // 类型
	private String vehicleVin; // 车辆唯一标识号
	private String obdSN; // obd唯一标识号
	private String reportTime; // 故障时间
	private String vehicleId; // 车辆 id
	private FaultCodeInfo faultCodeInfo = new FaultCodeInfo(); // 故障码信息
	private String statusStr;				// 状态码	- 历史故障中使用
	private FaultCodeStatusType status; 	// 状态 - 历史故障中使用

	public String getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getFaultCode() {
		return faultCodeInfo.getFaultCode();
	}

	public void setFaultCode(String faultCode) {
		faultCodeInfo.setFaultCode(faultCode);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return faultCodeInfo.getDescription();
	}

	public void setContent(String content) {
		faultCodeInfo.setDescription(content);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVehicleVin() {
		return vehicleVin;
	}

	public void setVehicleVin(String vehicleVin) {
		this.vehicleVin = vehicleVin;
	}

	public String getObdSN() {
		return obdSN;
	}

	public void setObdSN(String obdSN) {
		this.obdSN = obdSN;
	}

	public String getReportTime() {
		return reportTime;
	}

	public void setReportTime(String reportTime) {
		this.reportTime = reportTime;
	}

	public FaultCodeInfo getFaultCodeInfo() {
		return faultCodeInfo;
	}

	public void setFaultCodeInfo(FaultCodeInfo faultCodeInfo) {
		this.faultCodeInfo.copy(faultCodeInfo);
	}

	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getStatusStr() {
		return statusStr;
	}

	public void setStatusStr(String statusStr) {
		this.statusStr = statusStr;
	}

	public FaultCodeStatusType getStatus() {
		return status;
	}

	public void setStatus(FaultCodeStatusType status) {
		this.status = status;
	}
}
