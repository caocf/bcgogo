package com.bcgogo.user.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.AppointServiceDTO;
import com.bcgogo.user.dto.CustomerServiceJobDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.UserConstant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-12-7
 * Time: 上午10:48
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "customer_service_job")
public class CustomerServiceJob extends LongIdentifier {
  public CustomerServiceJob(){}


  public CustomerServiceJob(CustomerServiceJobDTO customerServiceJobDTO) {
    this.setId(customerServiceJobDTO.getId());
    this.setShopId(customerServiceJobDTO.getShopId());
    this.setRemindType(customerServiceJobDTO.getRemindType());
    this.setCustomerId(customerServiceJobDTO.getCustomerId());
    this.setVehicleId(customerServiceJobDTO.getVehicleId());
    this.setRemindTime(customerServiceJobDTO.getRemindTime());
    this.setRemindMode(customerServiceJobDTO.getRemindMode());
    this.setStatus(customerServiceJobDTO.getStatus());
    this.setAppointServiceId(customerServiceJobDTO.getAppointServiceId());
    this.setRemindMileage(customerServiceJobDTO.getRemindMileage());
  }

  public CustomerServiceJob fromDTO(CustomerServiceJobDTO customerServiceJobDTO) {
    this.setId(customerServiceJobDTO.getId());
    this.setShopId(customerServiceJobDTO.getShopId());
    this.setRemindType(customerServiceJobDTO.getRemindType());
    this.setCustomerId(customerServiceJobDTO.getCustomerId());
    this.setVehicleId(customerServiceJobDTO.getVehicleId());
    this.setRemindTime(customerServiceJobDTO.getRemindTime());
    this.setRemindMode(customerServiceJobDTO.getRemindMode());
    this.setStatus(customerServiceJobDTO.getStatus());
    this.setAppointServiceId(customerServiceJobDTO.getAppointServiceId());
    this.setRemindMileage(customerServiceJobDTO.getRemindMileage());
    return this;
  }

   public CustomerServiceJob fromAppointServiceDTO(AppointServiceDTO appointServiceDTO) throws ParseException {
    this.setShopId(appointServiceDTO.getShopId());
    this.setRemindType(UserConstant.APPOINT_SERVICE);
    this.setCustomerId(NumberUtil.longValue(appointServiceDTO.getCustomerId()));
    this.setVehicleId(NumberUtil.longValue(appointServiceDTO.getVehicleId()));
    this.setRemindTime(DateUtil.convertDateStringToDateLong(DateUtil.DEFAULT,appointServiceDTO.getAppointDate()));
    this.setStatus("activity");
    this.setAppointServiceId(appointServiceDTO.getId());
    return this;
  }

  public CustomerServiceJobDTO toDTO() {
    CustomerServiceJobDTO customerServiceJobDTO = new CustomerServiceJobDTO();

    customerServiceJobDTO.setId(this.getId());
    customerServiceJobDTO.setShopId(this.getShopId());
    customerServiceJobDTO.setAppointName(this.getAppointName());
    if(this.getRemindType()==null){
      customerServiceJobDTO.setRemindType(UserConstant.APPOINT_SERVICE); //自定义预约服务类型
    }else {
    customerServiceJobDTO.setRemindType(this.getRemindType());
    }
    customerServiceJobDTO.setCustomerId(this.getCustomerId());
    customerServiceJobDTO.setVehicleId(this.getVehicleId());
    customerServiceJobDTO.setRemindTime(this.getRemindTime());
    customerServiceJobDTO.setRemindMode(this.getRemindMode());
    customerServiceJobDTO.setStatus(this.getStatus());
    customerServiceJobDTO.setAppointServiceId(this.getAppointServiceId());
    customerServiceJobDTO.setRemindMileage(this.getRemindMileage());
    return customerServiceJobDTO;
  }
  private Long shopId;
  private Long remindType;
  private Long customerId;
  private Long vehicleId;
  private Long remindTime;
  private Long appointServiceId;
  private String appointName;
  private Long remindMode;
  private String status;
  private Long remindMileage;//提醒距离

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "remind_type")
  public Long getRemindType() {
    return remindType;
  }

  public void setRemindType(Long remindType) {
    this.remindType = remindType;
  }

  @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name = "vehicle_id")
  public Long getVehicleId() {
    return vehicleId;
  }

  @Column(name = "appoint_service_id")
  public Long getAppointServiceId() {
    return appointServiceId;
  }

  public void setAppointServiceId(Long appointServiceId) {
    this.appointServiceId = appointServiceId;
  }

  @Column(name = "appoint_name")
  public String getAppointName() {
    return appointName;
  }

  public void setAppointName(String appointName) {
    this.appointName = appointName;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }

  @Column(name = "remind_time")
  public Long getRemindTime() {
    return remindTime;
  }

  public void setRemindTime(Long remindTime) {
    this.remindTime = remindTime;
  }

  @Column(name = "remind_mode")
  public Long getRemindMode() {
    return remindMode;
  }

  public void setRemindMode(Long remindMode) {
    this.remindMode = remindMode;
  }

  @Column(name = "status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Column(name = "remind_mileage")
  public Long getRemindMileage() {
    return remindMileage;
  }

  public void setRemindMileage(Long remindMileage) {
    this.remindMileage = remindMileage;
  }
}
