package com.tonggou.andclient.vo;
/**
 * 车况消息项
 * @author think
 *
 */
public class CarCondition {
		private  String alarmId;
	    public String getAlarmId() {
			return alarmId;
		}
		public void setAlarmId(String alarmId) {
			this.alarmId = alarmId;
		}
		private  String userID ;       				//用于不同用户账号查找
	    private  String faultCode ;       			//故障码 如果有多个故障码请以逗号 ,分开
	    private  String name = "UNREAD";       				//是否读过
		private  String content ;    				//内容描述
		private  String type ;       				//类型  
	    private  String vehicleVin ;       			//车辆唯一标识号
		private  String obdSN ;    					//obd唯一标识号
		private  String reportTime ;      			//故障时间
		public String getUserID() {
			return userID;
		}
		public void setUserID(String userID) {
			this.userID = userID;
		}
		public String getFaultCode() {
			return faultCode;
		}
		public void setFaultCode(String faultCode) {
			this.faultCode = faultCode;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
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
}
