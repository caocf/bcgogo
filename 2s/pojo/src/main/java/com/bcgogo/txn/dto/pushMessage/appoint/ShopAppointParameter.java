package com.bcgogo.txn.dto.pushMessage.appoint;

import com.bcgogo.enums.app.AppointOrderStatus;
import com.bcgogo.txn.dto.AppointOrderDTO;
import org.apache.commons.lang.StringUtils;

/**
 * User: ZhangJuntao
 * Date: 13-9-11
 * Time: 下午4:47
 */
public class ShopAppointParameter {
  private Long shopId;
  private String appUserNo;
  private String services;
  private String newServices;
  private Long appointOrderId;
  private Long serviceOrderId;
  private Long appointTime;
  private Long newAppointTime;
  private String reason;
  private String vehicleNo;


  public ShopAppointParameter(Long shopId, String appUserNo, String services, Long appointOrderId, Long appointTime,String vehicleNo) {
    this.shopId = shopId;
    this.appUserNo = appUserNo;
    this.services = services;
    this.appointOrderId = appointOrderId;
    this.appointTime = appointTime;
    this.vehicleNo = vehicleNo;
  }

  public ShopAppointParameter(AppointOrderDTO dto) {
    setShopId(dto.getShopId());
    setAppUserNo(dto.getAppUserNo());
    setServices(dto.getAppointServiceType());
    setAppointOrderId(dto.getId());
    setAppointTime(dto.getAppointTime());
    if (AppointOrderStatus.CANCELED == dto.getStatus()) {
      setReason(dto.getCancelMsg());
    } else if (AppointOrderStatus.REFUSED == dto.getStatus()) {
      setReason(dto.getRefuseMsg());
    }
    setServiceOrderId(dto.getOrderId());
    setVehicleNo(dto.getVehicleNo());
  }
  public ShopAppointParameter(Long shopId, String appUserNo, String services, Long appointOrderId, Long appointTime,String reason,String vehicleNo) {
    this.shopId = shopId;
    this.appUserNo = appUserNo;
    this.services = services;
    this.appointOrderId = appointOrderId;
    this.appointTime = appointTime;
    this.reason = reason;
    this.vehicleNo = vehicleNo;
  }

  public ShopAppointParameter(Long shopId, String appUserNo, String services, String newServices, Long appointOrderId, Long appointTime, Long newAppointTime,String vehicleNo) {
    this.shopId = shopId;
    this.appUserNo = appUserNo;
    this.services = services;
    this.newServices = newServices;
    this.appointOrderId = appointOrderId;
    this.appointTime = appointTime;
    this.newAppointTime = newAppointTime;
    this.vehicleNo = vehicleNo;
  }

  public ShopAppointParameter(AppointOrderDTO dto, String userNo) {
    setShopId(dto.getShopId());
    setAppUserNo(userNo);
    setServices(dto.getAppointServiceType());
    setAppointOrderId(dto.getId());
    setAppointTime(dto.getAppointTime());
    if (AppointOrderStatus.CANCELED == dto.getStatus()) {
      setReason(dto.getCancelMsg());
    } else if (AppointOrderStatus.REFUSED == dto.getStatus()) {
      setReason(dto.getRefuseMsg());
    }
    setServiceOrderId(dto.getOrderId());
    setVehicleNo(dto.getVehicleNo());
  }

  public String validate() {
    if (shopId == null) return "shop id is null";
    if (appointOrderId == null) return "appoint order id is null";
    if (appointTime == null) return "appoint time is null";
    if (StringUtils.isBlank(appUserNo)) return "app user no is null";
    if (StringUtils.isBlank(services)) return "services is null";
    return "";
  }

  public String changeAppointValidate() {
    String result = validate();
    if (StringUtils.isNotBlank(result)) return result;
    if (StringUtils.isBlank(newServices)) return "new services is null";
    if (appointTime == null) return "appoint time is null";
    return "";
  }

  public boolean needSenChangeMessage() {
    return !appointTime.equals(newAppointTime) || !newServices.equals(services);
  }

  public String getNewServices() {
    return newServices;
  }

  public void setNewServices(String newServices) {
    this.newServices = newServices;
  }

  public Long getAppointTime() {
    return appointTime;
  }

  public void setAppointTime(Long appointTime) {
    this.appointTime = appointTime;
  }

  public Long getNewAppointTime() {
    return newAppointTime;
  }

  public void setNewAppointTime(Long newAppointTime) {
    this.newAppointTime = newAppointTime;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
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

  public Long getAppointOrderId() {
    return appointOrderId;
  }

  public void setAppointOrderId(Long appointOrderId) {
    this.appointOrderId = appointOrderId;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public Long getServiceOrderId() {
    return serviceOrderId;
  }

  public void setServiceOrderId(Long serviceOrderId) {
    this.serviceOrderId = serviceOrderId;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }
}
