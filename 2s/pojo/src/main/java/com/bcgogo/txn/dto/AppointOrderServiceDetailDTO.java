package com.bcgogo.txn.dto;

import com.bcgogo.api.AppOrderItemDTO;
import com.bcgogo.enums.common.ObjectStatus;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-2-27
 * Time: 下午5:21
 */
public class AppointOrderServiceDetailDTO implements Serializable {
  private Long id;
  private Long appointOrderId;
  private Long shopId;
  private String idStr;
  private Long serviceId;
  private String service;
  private Double total;   //也是实际工时费  =标准工时 *  标准工时单价
  private Double standardHours;//标准工时
  private Double standardUnitPrice;//标准工时单价
  private ObjectStatus status;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if(id != null){
      setIdStr(id.toString());
    }else{
      setIdStr("");
    }
  }

  public Long getAppointOrderId() {
    return appointOrderId;
  }

  public void setAppointOrderId(Long appointOrderId) {
    this.appointOrderId = appointOrderId;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
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

  public Double getStandardHours() {
    return standardHours;
  }

  public void setStandardHours(Double standardHours) {
    this.standardHours = standardHours;
  }

  public Double getStandardUnitPrice() {
    return standardUnitPrice;
  }

  public void setStandardUnitPrice(Double standardUnitPrice) {
    this.standardUnitPrice = standardUnitPrice;
  }

  public ObjectStatus getStatus() {
    return status;
  }

  public void setStatus(ObjectStatus status) {
    this.status = status;
  }

  public AppOrderItemDTO toAppOrderItemDTO() {
    AppOrderItemDTO appOrderItemDTO = new AppOrderItemDTO();
    appOrderItemDTO.setContent(this.getService());
    appOrderItemDTO.setType(AppOrderItemDTO.itemTypeService);
    appOrderItemDTO.setAmount(this.getTotal());
    return appOrderItemDTO;
  }
}
