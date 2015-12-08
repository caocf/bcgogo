package com.bcgogo.txn.model;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.RemindEventType;
import com.bcgogo.enums.RepairRemindEventTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.remind.dto.RemindEventDTO;
import com.bcgogo.user.dto.CustomerServiceJobDTO;
import com.bcgogo.utils.UserConstant;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: Wei Lingfeng
 * Date: 13-1-5
 * Time: 下午2:54
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "remind_event")
public class RemindEvent extends LongIdentifier {
  public RemindEvent(){}

  private Long shopId;
  private Long orderId;
  private String orderType;
  private String eventType;
  private String eventStatus;
  private Long remindTime;
  private String remindStatus;
  private Long customerId;
  private String customerName;
  private Long supplierId;
  private String supplierName;
  private Long objectId;
  private Double debt;
  private String licenceNo;
  private String mobile;
  private Long appointServiceId;
  private Long serviceId;
  private String services;
  private Long oldRemindEventId;
  private Long remindMileage;//下次保养里程
  private DeletedType deletedType;
  private String wxRemindStatus;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "order_id")
  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  @Column(name = "order_type")
  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }

  @Column(name = "event_type")
  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  @Column(name = "event_status")
  public String getEventStatus() {
    return eventStatus;
  }

  public void setEventStatus(String eventStatus) {
    this.eventStatus = eventStatus;
  }

  @Column(name = "remind_time")
  public Long getRemindTime() {
    return remindTime;
  }

  public void setRemindTime(Long remindTime) {
    this.remindTime = remindTime;
  }

  @Column(name = "remind_status")
  public String getRemindStatus() {
    return remindStatus;
  }

  public void setRemindStatus(String remindStatus) {
    this.remindStatus = remindStatus;
  }

  @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name = "customer_name")
  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  @Column(name = "supplier_id")
  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  @Column(name = "supplier_name")
  public String getSupplierName() {
    return supplierName;
  }

  public void setSupplierName(String supplierName) {
    this.supplierName = supplierName;
  }

  @Column(name = "mobile")
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  @Column(name = "object_id")
  public Long getObjectId() {
    return objectId;
  }

  public void setObjectId(Long objectId) {
    this.objectId = objectId;
  }

  @Column(name = "debt")
  public Double getDebt() {
    return debt;
  }

  public void setDebt(Double debt) {
    this.debt = debt;
  }

  @Column(name = "licence_no")
  public String getLicenceNo() {
    return licenceNo;
  }

  public void setLicenceNo(String licenceNo) {
    this.licenceNo = licenceNo;
  }

  @Column(name = "appoint_service_id")
  public Long getAppointServiceId() {
    return appointServiceId;
  }

  public void setAppointServiceId(Long appointServiceId) {
    this.appointServiceId = appointServiceId;
  }

  @Column(name = "service_id")
  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  @Column(name = "services")
  public String getServices() {
    return services;
  }

  public void setServices(String services) {
    this.services = services;
  }

  @Column(name = "old_remind_event_id")
  public Long getOldRemindEventId() {
    return oldRemindEventId;
  }

  public void setOldRemindEventId(Long oldRemindEventId) {
    this.oldRemindEventId = oldRemindEventId;
  }

  @Column(name = "remind_mileage")
  public Long getRemindMileage() {
    return remindMileage;
  }

  public void setRemindMileage(Long remindMileage) {
    this.remindMileage = remindMileage;
  }

  @Column(name = "wx_remind_status")
  public String getWxRemindStatus() {
    return wxRemindStatus;
  }

  public void setWxRemindStatus(String wxRemindStatus) {
    this.wxRemindStatus = wxRemindStatus;
  }

  @Enumerated(EnumType.STRING)
  @Column(name="deleted_type")
  public DeletedType getDeletedType() {
    return deletedType;
  }

  public void setDeletedType(DeletedType deletedType) {
    this.deletedType = deletedType;
  }

  public RemindEventDTO toDTO(){
    RemindEventDTO remindEventDTO = new RemindEventDTO();
    remindEventDTO.setId(this.getId());
    remindEventDTO.setShopId(getShopId());
    remindEventDTO.setOrderId(getOrderId());
    remindEventDTO.setOrderType(getOrderType());
    remindEventDTO.setEventType(getEventType());
    if(getEventStatus()!=null && RemindEventType.REPAIR.toString().equals(getEventStatus())){
      remindEventDTO.setEventStatus(RepairRemindEventTypes.valueOf(getEventStatus()).getName());
    }else{
      remindEventDTO.setEventStatus(getEventStatus());
    }
    remindEventDTO.setRemindTime(getRemindTime());
    remindEventDTO.setRemindStatus(getRemindStatus());
    remindEventDTO.setCustomerId(getCustomerId());
    remindEventDTO.setCustomerName(getCustomerName());
    remindEventDTO.setSupplierId(getSupplierId());
    remindEventDTO.setSupplierName(getSupplierName());
    remindEventDTO.setMobile(getMobile());
    remindEventDTO.setObjectId(getObjectId());
    remindEventDTO.setAppointServiceId(getAppointServiceId());
    remindEventDTO.setLicenceNo(getLicenceNo());
    remindEventDTO.setServiceId(getServiceId());
    remindEventDTO.setService(getServices());
    remindEventDTO.setOldRemindEventId(getOldRemindEventId());
    remindEventDTO.setDebt(getDebt());
    remindEventDTO.setRemindMileage(getRemindMileage());
    remindEventDTO.setDeletedType(getDeletedType());
    return remindEventDTO;
  }

  public RemindEvent fromDTO(RemindEventDTO remindEventDTO){
    this.setId(remindEventDTO.getId());
    this.setShopId(remindEventDTO.getShopId());
    this.setOrderId(remindEventDTO.getOrderId());
    this.setOrderType(remindEventDTO.getOrderType());
    this.setEventType(remindEventDTO.getEventType());
    this.setEventStatus(remindEventDTO.getEventStatus());
    this.setRemindTime(remindEventDTO.getRemindTime());
    this.setRemindStatus(remindEventDTO.getRemindStatus());
    this.setCustomerId(remindEventDTO.getCustomerId());
    this.setCustomerName(remindEventDTO.getCustomerName());
    this.setSupplierId(remindEventDTO.getSupplierId());
    this.setSupplierName(remindEventDTO.getSupplierName());
    this.setMobile(remindEventDTO.getMobile());
    this.setObjectId(remindEventDTO.getObjectId());
    this.setDebt(remindEventDTO.getDebt());
    this.setAppointServiceId(remindEventDTO.getAppointServiceId());
    this.setLicenceNo(remindEventDTO.getLicenceNo());
    this.setServiceId(remindEventDTO.getServiceId());
    this.setServices(remindEventDTO.getService());
    this.setDebt(remindEventDTO.getDebt());
    this.setRemindMileage(remindEventDTO.getRemindMileage());
    return this;
  }

  public void setCustomerServiceJob(CustomerServiceJobDTO customerServiceJobDTO) {
    if(customerServiceJobDTO != null){
      this.setShopId(customerServiceJobDTO.getShopId());
      this.setEventStatus(UserConstant.getCustomerRemindType(customerServiceJobDTO.getRemindType()));
      this.setRemindTime(customerServiceJobDTO.getRemindTime());
      this.setRemindStatus(customerServiceJobDTO.getStatus());
      this.setCustomerId(customerServiceJobDTO.getCustomerId());
      this.setAppointServiceId(customerServiceJobDTO.getAppointServiceId());
      this.setOldRemindEventId(customerServiceJobDTO.getId());
      this.setRemindMileage(customerServiceJobDTO.getRemindMileage());
    }
  }
}
