package com.bcgogo.txn.dto;

import com.bcgogo.base.BaseDTO;
import com.bcgogo.enums.common.ObjectStatus;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-9-5
 * Time: 下午4:34
 */
public class AppointOrderServiceItemDTO extends BaseDTO {
  private Long shopId;
  private Long appointOrderId;
  private Long serviceId;//预约服务id 是本店服务表的外键
  private String serviceName;//服务内容
  private ObjectStatus status;//item的状态

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getAppointOrderId() {
    return appointOrderId;
  }

  public void setAppointOrderId(Long appointOrderId) {
    this.appointOrderId = appointOrderId;
  }

  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public ObjectStatus getStatus() {
    return status;
  }

  public void setStatus(ObjectStatus status) {
    this.status = status;
  }
}
