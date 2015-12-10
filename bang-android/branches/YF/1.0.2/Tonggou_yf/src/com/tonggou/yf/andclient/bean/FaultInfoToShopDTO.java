package com.tonggou.yf.andclient.bean;

public class FaultInfoToShopDTO {
	private String id; // 故障 id
	private String faultCode; // 故障码
    private String vehicleNo; // 车牌号
    private String faultCodeReportTimeStr; // 故障报告时间
    private String customerName; // 车主
    private String faultCodeDescription; //描述 可能为空
    private String faultAlertTypeValue; // 类型
    private String mobile; // 手机
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFaultCode() {
		return faultCode;
	}
	public void setFaultCode(String faultCode) {
		this.faultCode = faultCode;
	}
	public String getVehicleNo() {
		return vehicleNo;
	}
	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}
	public String getFaultCodeReportTimeStr() {
		return faultCodeReportTimeStr;
	}
	public void setFaultCodeReportTimeStr(String faultCodeReportTimeStr) {
		this.faultCodeReportTimeStr = faultCodeReportTimeStr;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getFaultCodeDescription() {
		return faultCodeDescription;
	}
	public void setFaultCodeDescription(String faultCodeDescription) {
		this.faultCodeDescription = faultCodeDescription;
	}
	public String getFaultAlertTypeValue() {
		return faultAlertTypeValue;
	}
	public void setFaultAlertTypeValue(String faultAlertTypeValue) {
		this.faultAlertTypeValue = faultAlertTypeValue;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
}
