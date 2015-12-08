package com.bcgogo.user.dto;

import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.UserConstant;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-12-7
 * Time: 上午10:55
 * To change this template use File | Settings | File Templates.
 */
public class CustomerServiceJobDTO implements Serializable {
  public CustomerServiceJobDTO(){}

  private Long id;
  private Long shopId;
  private Long remindType;
  private Long customerId;
  private String customerIdStr;
  private Long vehicleId;
  private Long remindTime;
  private Long remindMode;
  private String status;
  private String customerName;
  private String contact;
  private Long contactId;
  private String contactIdStr;
  private String vehicleIdStr;
  private String vehicleCustomerName;
  private String vehicleMobile;
  private String licenceNo;
  private String mobile;
  private String remindTimeStr;
  private String appointName;
  private Long birthDay;
  private String birthStr;
  private String remindTypeStr;
  private String idStr;
  private Long appointServiceId;
  private Long remindMileage;//提醒距离
  private Double currentMileage;


  public String getRemindTypeStr() {
    if (null != remindType) {
      if (remindType.equals(UserConstant.MAINTAIN_TIME)) {
        remindTypeStr = "保养";
      }
      if (remindType.equals(UserConstant.INSURE_TIME)) {
        remindTypeStr = "保险";
      }
      if (remindType.equals(UserConstant.EXAMINE_TIME)) {
        remindTypeStr = "验车";
      }
      if (remindType.equals(UserConstant.BIRTH_TIME)) {
        this.remindTypeStr = "生日";
      }
    }
    return remindTypeStr;
  }

  public Long getContactId() {
    return contactId;
  }

  public void setContactId(Long contactId) {
    this.contactId = contactId;
    if(contactId!=null) this.contactIdStr = contactId.toString();
  }

  public String getContactIdStr() {
    return contactIdStr;
  }

  public void setContactIdStr(String contactIdStr) {
    this.contactIdStr = contactIdStr;
  }

  public String getVehicleIdStr() {
    return vehicleIdStr;
  }

  public void setVehicleIdStr(String vehicleIdStr) {
    this.vehicleIdStr = vehicleIdStr;
  }

  public void setRemindTypeStr(String remindTypeStr) {
        this.remindTypeStr = remindTypeStr;
    }

    public String getBirthStr() {
        return DateUtil.convertDateLongToDateString("yyyy-MM-dd",birthDay);
    }

    public void setBirthStr(String birthStr) {
        this.birthStr = birthStr;
    }

    public Long getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Long birthDay) {
        this.birthDay = birthDay;
        if(null!=birthDay)
        this.birthStr = DateUtil.convertDateLongToDateString("yyyy-MM-dd",birthDay);
    }

    public void setRemindTimeStr(String remindTimeStr){
         this.remindTimeStr=remindTimeStr;
    }
  
    public String getRemindTimeStr(){
        return DateUtil.convertDateLongToDateString("yyyy-MM-dd",remindTime);
    }

  public String getAppointName() {
    return appointName;
  }

  public void setAppointName(String appointName) {
    this.appointName = appointName;
  }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getLicenceNo() {
        return licenceNo;
    }

    public void setLicenceNo(String licenceNo) {
        this.licenceNo = licenceNo;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
    if(id!=null){
      this.idStr =String.valueOf(id);
    }
  }

  public String getIdStr() {
    return String.valueOf(id);
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getRemindType() {
    return remindType;
  }

  public void setRemindType(Long remindType) {
    this.remindType = remindType;
    if (remindType.equals(UserConstant.MAINTAIN_TIME)) {
      this.remindTypeStr = "保养";
    } else if (remindType.equals(UserConstant.INSURE_TIME)) {
      this.remindTypeStr = "保险";
    } else if (remindType.equals(UserConstant.EXAMINE_TIME)) {
      this.remindTypeStr = "验车";
    } else if (remindType.equals(UserConstant.BIRTH_TIME)) {
      this.remindTypeStr = "生日";
    } else if (remindType.equals(UserConstant.MAINTAIN_MILEAGE)) {
      this.remindTypeStr = "保养";
    } else {
      this.remindTypeStr = this.getAppointName();
    }
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    if (customerId != null) {
      this.customerIdStr = customerId.toString();
    }
    this.customerId = customerId;
  }

  public String getCustomerIdStr() {
    return customerIdStr;
  }

  public void setCustomerIdStr(String customerIdStr) {
    this.customerIdStr = customerIdStr;
  }

  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
    if(vehicleId!=null) this.vehicleIdStr = vehicleId.toString();
  }

  public Long getRemindTime() {
    return remindTime;
  }

  public void setRemindTime(Long remindTime) {
    this.remindTime = remindTime;
    if(remindTime!=null)
    this.remindTimeStr=  DateUtil.convertDateLongToDateString("yyyy-MM-dd",remindTime);
  }

  public Long getRemindMode() {
    return remindMode;
  }

  public void setRemindMode(Long remindMode) {
    this.remindMode = remindMode;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Long getAppointServiceId() {
    return appointServiceId;
  }

  public void setAppointServiceId(Long appointServiceId) {
    this.appointServiceId = appointServiceId;
  }

  public Long getRemindMileage() {
    return remindMileage;
  }

  public void setRemindMileage(Long remindMileage) {
    this.remindMileage = remindMileage;
  }

  public String getVehicleCustomerName() {
    return vehicleCustomerName;
  }

  public void setVehicleCustomerName(String vehicleCustomerName) {
    this.vehicleCustomerName = vehicleCustomerName;
  }

  public String getVehicleMobile() {
    return vehicleMobile;
  }

  public void setVehicleMobile(String vehicleMobile) {
    this.vehicleMobile = vehicleMobile;
  }

  public void setCurrentMileage(Double currentMileage) {
    this.currentMileage = currentMileage;
  }

  public Double getCurrentMileage() {
    return currentMileage;
  }
}
