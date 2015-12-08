package com.bcgogo.txn.dto;

import com.bcgogo.enums.ConsumeType;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-1-7
 * Time: 下午4:02
 * To change this template use File | Settings | File Templates.
 */
public class InsuranceOrderServiceDTO implements Serializable {

  private Long id;
  private Long shopId;
  private Long insuranceOrderId;
  private Long serviceId;
  private String service;
  private Double total;

  public void fromRepairOrderServiceDTO(RepairOrderServiceDTO serviceDTO) {
    if(serviceDTO == null){
      return;
    }
    this.setServiceId(serviceDTO.getServiceId());
    this.setService(serviceDTO.getService());
    this.setTotal(serviceDTO.getTotal());
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getInsuranceOrderId() {
    return insuranceOrderId;
  }

  public void setInsuranceOrderId(Long insuranceOrderId) {
    this.insuranceOrderId = insuranceOrderId;
  }

  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }
}
