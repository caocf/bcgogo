package com.bcgogo.notification.velocity;

import com.bcgogo.api.AppUserDTO;
import com.bcgogo.api.AppVehicleFaultInfoDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.SystemType;
import com.bcgogo.txn.dto.pushMessage.enquiry.VehicleFaultParameter;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoToShopDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;

/**
 * Created with IntelliJ IDEA.
 * User: Hans
 * Date: 13-12-3
 * Time: 上午9:25
 */
public class VehicleFaultContext {
  private String appUserNo;
  private String appUserName;
  private String vehicleNo;
  private String faultCode;
  private String description;
  private ShopDTO targetShop;
  private String mobile;
  private String time;

  public VehicleFaultContext() {
  }

  public VehicleFaultContext(VehicleFaultParameter parameter, ShopDTO shopDTO) {
    setTargetShop(shopDTO);
    setAppUserNo(parameter.getAppUserNo());
    setAppUserName(parameter.getAppUserName());
    setVehicleNo(parameter.getVehicleNo());
    setFaultCode(parameter.getFaultCode());
    setDescription(parameter.getDescription());
    setMobile(parameter.getMobile());
    setTime(DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN_CN));
  }

  public VehicleFaultContext( AppVehicleFaultInfoDTO vehicleFaultInfoDTO) {

  }

  public VehicleFaultContext(AppUserDTO appUserDTO, FaultInfoToShopDTO faultInfoToShopDTO,ShopDTO shopDTO) {
    if (faultInfoToShopDTO != null) {
      setTargetShop(shopDTO);
      setAppUserNo(faultInfoToShopDTO.getAppUserNo());
      setVehicleNo(faultInfoToShopDTO.getVehicleNo());
      setFaultCode(faultInfoToShopDTO.getFaultCode());
      setDescription(faultInfoToShopDTO.getFaultCodeDescription());
      setMobile(faultInfoToShopDTO.getMobile());
      setTime(DateUtil.convertDateLongToString(faultInfoToShopDTO.getFaultCodeReportTime(), DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN_CN));
    }
    if(appUserDTO != null){
      setAppUserName(appUserDTO.getName());
    }

  }

  public VehicleFaultContext(AppUserDTO appUserDTO, VehicleDTO vehicleDTO, CustomerDTO customerDTO, FaultInfoToShopDTO faultInfoToShopDTO, ShopDTO shopDTO) {
    if (faultInfoToShopDTO != null) {
      setTargetShop(shopDTO);
      setAppUserNo(faultInfoToShopDTO.getAppUserNo());
      setVehicleNo(faultInfoToShopDTO.getVehicleNo());
      setFaultCode(faultInfoToShopDTO.getFaultCode());
      setDescription(faultInfoToShopDTO.getFaultCodeDescription());
      setMobile(faultInfoToShopDTO.getMobile());
      setTime(DateUtil.convertDateLongToString(faultInfoToShopDTO.getFaultCodeReportTime(), DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN_CN));
    }

    if(appUserDTO != null){
      setAppUserName(appUserDTO.getName());
    }else if(customerDTO !=null){
      setAppUserName(customerDTO.getName());
    }

  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public String getAppUserName() {
    return appUserName;
  }

  public void setAppUserName(String appUserName) {
    this.appUserName = appUserName;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public String getFaultCode() {
    return faultCode;
  }

  public void setFaultCode(String faultCode) {
    this.faultCode = faultCode;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    if (StringUtil.isEmpty(description)) {
      this.description = null;
    } else {
      this.description = description;
    }
  }

  public ShopDTO getTargetShop() {
    return targetShop;
  }

  public void setTargetShop(ShopDTO targetShop) {
    this.targetShop = targetShop;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }
}
