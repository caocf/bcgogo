package com.bcgogo.notification.velocity;

import com.bcgogo.api.AppUserDTO;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.txn.dto.pushMessage.appoint.AppAppointParameter;
import com.bcgogo.txn.dto.pushMessage.appoint.ShopAppointParameter;
import com.bcgogo.txn.dto.pushMessage.enquiry.AppEnquiryParameter;
import com.bcgogo.txn.dto.pushMessage.enquiry.ShopQuoteEnquiryParameter;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.utils.DateUtil;

/**
 * User: ZhangJuntao
 * Date: 13-9-11
 * Time: 下午4:29
 */
public class AppointVelocityContext {
  private ShopDTO shopDTO;
  private String appUserNo;
  private String services;
  private String newServices;
  private Long enquiryTime;
  private String enquiryTimeStr;
  private Long appointTime;
  private String appointTimeStr;
  private Long newAppointTime;
  private String newAppointTimeStr;
  private Long applyTime;
  private String applyTimeStr;
  private String vehicleNo;
  private String reason;
  private Long currentTime = System.currentTimeMillis();
  private String currentTimeStr;
  private ContactDTO contact;
  private AppVehicleDTO appVehicle;
  private AppUserDTO appUser;
  private Double appVehicleMaintainMileageLeftLimit;
  private Double appVehicleMaintainMileageRightLimit;
  private int day;

  public void from(ShopQuoteEnquiryParameter parameter) {
    setAppUserNo(parameter.getAppUserNo());
    setEnquiryTime(parameter.getEnquiryTime());
  }

  public void from(AppEnquiryParameter parameter) {
    setAppUserNo(parameter.getAppUserNo());
    setVehicleNo(parameter.getVehicleNo());
  }

  public void from(ShopAppointParameter parameter) {
    setServices(parameter.getServices());
    setNewServices(parameter.getNewServices());
    setAppUserNo(parameter.getAppUserNo());
    setAppointTime(parameter.getAppointTime());
    setNewAppointTime(parameter.getNewAppointTime());
    setReason(parameter.getReason());
    setVehicleNo(parameter.getVehicleNo());
  }

  public void from(AppAppointParameter parameter) {
    setServices(parameter.getServices());
    setAppUserNo(parameter.getAppUserNo());
    setApplyTime(parameter.getApplyTime());
    setVehicleNo(parameter.getVehicleNo());
  }

  public int getDay() {
    return day;
  }

  public void setDay(int day) {
    this.day = day;
  }

  public Double getAppVehicleMaintainMileageLeftLimit() {
    return appVehicleMaintainMileageLeftLimit;
  }

  public void setAppVehicleMaintainMileageLeftLimit(Double appVehicleMaintainMileageLeftLimit) {
    this.appVehicleMaintainMileageLeftLimit = appVehicleMaintainMileageLeftLimit;
  }

  public Double getAppVehicleMaintainMileageRightLimit() {
    return appVehicleMaintainMileageRightLimit;
  }

  public void setAppVehicleMaintainMileageRightLimit(Double appVehicleMaintainMileageRightLimit) {
    this.appVehicleMaintainMileageRightLimit = appVehicleMaintainMileageRightLimit;
  }

  public AppUserDTO getAppUser() {
    return appUser;
  }

  public void setAppUser(AppUserDTO appUser) {
    this.appUser = appUser;
  }

  public ContactDTO getContact() {
    return contact;
  }

  public void setContact(ContactDTO contact) {
    this.contact = contact;
  }

  public AppVehicleDTO getAppVehicle() {
    return appVehicle;
  }

  public void setAppVehicle(AppVehicleDTO appVehicle) {
    this.appVehicle = appVehicle;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public ShopDTO getShopDTO() {
    return shopDTO;
  }

  public void setShopDTO(ShopDTO shopDTO) {
    this.shopDTO = shopDTO;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public String getServices() {
    return services;
  }

  public void setServices(String services) {
    this.services = services;
  }

  public Long getAppointTime() {
    return appointTime;
  }

  public void setAppointTime(Long appointTime) {
    this.appointTime = appointTime;
    if (appointTime != null) {
      setAppointTimeStr(DateUtil.convertDateLongToString(appointTime, DateUtil.DATE_STRING_FORMAT_MON_DAY_HOUR_MIN_CN));
    }
  }

  public Long getEnquiryTime() {
    return enquiryTime;
  }

  public void setEnquiryTime(Long enquiryTime) {
    this.enquiryTime = enquiryTime;
    if (enquiryTime != null) {
      setEnquiryTimeStr(DateUtil.convertDateLongToString(enquiryTime, DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN));
    }
  }

  public String getEnquiryTimeStr() {
    return enquiryTimeStr;
  }

  public void setEnquiryTimeStr(String enquiryTimeStr) {
    this.enquiryTimeStr = enquiryTimeStr;
  }

  public Long getApplyTime() {
    return applyTime;
  }

  public void setApplyTime(Long applyTime) {
    this.applyTime = applyTime;
    if (applyTime != null) {
      setApplyTimeStr(DateUtil.convertDateLongToString(applyTime, DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN));
    }
  }

  public String getNewServices() {
    return newServices;
  }

  public void setNewServices(String newServices) {
    this.newServices = newServices;
  }

  public Long getNewAppointTime() {
    return newAppointTime;
  }

  public void setNewAppointTime(Long newAppointTime) {
    this.newAppointTime = newAppointTime;
    if (newAppointTime != null) {
      setNewAppointTimeStr(DateUtil.convertDateLongToString(newAppointTime, DateUtil.DATE_STRING_FORMAT_MON_DAY_HOUR_MIN_CN));
    }
  }

  public String getAppointTimeStr() {
    return appointTimeStr;
  }

  public void setAppointTimeStr(String appointTimeStr) {
    this.appointTimeStr = appointTimeStr;
  }

  public String getNewAppointTimeStr() {
    return newAppointTimeStr;
  }

  public void setNewAppointTimeStr(String newAppointTimeStr) {
    this.newAppointTimeStr = newAppointTimeStr;
  }

  public String getApplyTimeStr() {
    return applyTimeStr;
  }

  public void setApplyTimeStr(String applyTimeStr) {
    this.applyTimeStr = applyTimeStr;
  }

  public Long getCurrentTime() {
    return currentTime;
  }

  public void setCurrentTime(Long currentTime) {
    this.currentTime = currentTime;
    if (currentTime != null) {
      setCurrentTimeStr(DateUtil.convertDateLongToString(currentTime, DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN));
    }
  }

  public String getCurrentTimeStr() {
    return currentTimeStr;
  }

  public void setCurrentTimeStr(String currentTimeStr) {
    this.currentTimeStr = currentTimeStr;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }
}
