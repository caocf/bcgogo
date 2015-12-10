package com.tonggou.andclient.vo;

public class AppConfig {

	private String appVehicleErrorCodeWarnIntervals;	
	private String obdReadInterval;
	private String serverReadInterval;
	private String mileageInformInterval;
	private String customerServicePhone;
	private String remainOilMassWarn;
	
	public String getRemainOilMassWarn() {
		return remainOilMassWarn;
	}
	
	public void setRemainOilMassWarn(String remainOilMassWarn) {
		this.remainOilMassWarn = remainOilMassWarn;
	}
	
	public String getAppVehicleErrorCodeWarnIntervals() {
		return appVehicleErrorCodeWarnIntervals;
	}
	
	public void setAppVehicleErrorCodeWarnIntervals(
			String appVehicleErrorCodeWarnIntervals) {
		this.appVehicleErrorCodeWarnIntervals = appVehicleErrorCodeWarnIntervals;
	}
	
	public String getObdReadInterval() {
		return obdReadInterval;
	}
	public void setObdReadInterval(String obdReadInterval) {
		this.obdReadInterval = obdReadInterval;
	}
	public String getServerReadInterval() {
		return serverReadInterval;
	}
	public void setServerReadInterval(String serverReadInterval) {
		this.serverReadInterval = serverReadInterval;
	}
	public String getMileageInformInterval() {
		return mileageInformInterval;
	}
	public void setMileageInformInterval(String mileageInformInterval) {
		this.mileageInformInterval = mileageInformInterval;
	}
	public String getCustomerServicePhone() {
		return customerServicePhone;
	}
	public void setCustomerServicePhone(String customerServicePhone) {
		this.customerServicePhone = customerServicePhone;
	}
}
