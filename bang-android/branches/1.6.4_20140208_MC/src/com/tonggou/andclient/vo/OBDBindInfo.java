package com.tonggou.andclient.vo;

import com.google.gson.annotations.SerializedName;

/**
 * obd绑定信息
 * @author think
 *
 */
public class OBDBindInfo {

	private String isDefault = "NO";
	
	public String getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(String ifDefault) {
		this.isDefault = ifDefault;
	}

	
	private String obdID;
	private String obdSN;

	private VehicleInfo vehicleInfo;  // 车辆基本信息
	
	
	public VehicleInfo getVehicleInfo() {
		return vehicleInfo;
	}
	public void setVehicleInfo(VehicleInfo vehicleInfo) {
		this.vehicleInfo = vehicleInfo;
	}
	public String getObdID() {
		return obdID;
	}
	public void setObdID(String obdID) {
		this.obdID = obdID;
	}
	public String getObdSN() {
		return obdSN;
	}
	public void setObdSN(String obdSN) {
		this.obdSN = obdSN;
	}
	
	@Override
	public boolean equals(Object o) {
		if( o instanceof OBDBindInfo ) {
			if( obdSN == null || ((OBDBindInfo) o).getObdSN() == null ) {
				return false;
			}
			OBDBindInfo oo = (OBDBindInfo) o;
			return oo.getObdSN().equals(obdSN) && oo.getIsDefault().equals(obdSN) ;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (obdSN + "").hashCode() + (isDefault + "").hashCode();
	}
	
//	public String get_default() {
//		return ifDefault;
//	}
//	public void set_default(String _default) {
//		this.ifDefault = _default;
//	}
	

}
