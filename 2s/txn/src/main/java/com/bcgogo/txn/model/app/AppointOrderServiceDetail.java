package com.bcgogo.txn.model.app;

import com.bcgogo.enums.common.ObjectStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.AppointOrderServiceDetailDTO;
import com.bcgogo.txn.dto.AppointOrderServiceDetailDTO;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-2-28
 * Time: 下午3:10
 */
@Entity
@Table(name = "appoint_order_service_detail")
public class AppointOrderServiceDetail extends LongIdentifier {
  private Long appointOrderId;
  private Long shopId;
  private Long serviceId;
  private String service;
  private Double total;   //也是实际工时费  =标准工时 *  标准工时单价
  private Double standardHours;//标准工时
  private Double standardUnitPrice;//标准工时单价
  private ObjectStatus status;

  public void fromDTO(AppointOrderServiceDetailDTO serviceDTO) {
    if (serviceDTO != null) {
      this.setAppointOrderId(serviceDTO.getAppointOrderId());
      this.setShopId(serviceDTO.getShopId());
      this.setServiceId(serviceDTO.getServiceId());
      this.setService(serviceDTO.getService());
      this.setTotal(serviceDTO.getTotal());
      this.setStandardHours(serviceDTO.getStandardHours());
      this.setStandardUnitPrice(serviceDTO.getStandardUnitPrice());
      this.setStatus(serviceDTO.getStatus());
    }
  }

  public AppointOrderServiceDetailDTO toDTO() {
    AppointOrderServiceDetailDTO serviceDTO = new AppointOrderServiceDetailDTO();
    serviceDTO.setId(this.getId());
    serviceDTO.setAppointOrderId(this.getAppointOrderId());
    serviceDTO.setShopId(this.getShopId());
    serviceDTO.setServiceId(this.getServiceId());
    serviceDTO.setService(this.getService());
    serviceDTO.setTotal(this.getTotal());
    serviceDTO.setStandardHours(this.getStandardHours());
    serviceDTO.setStandardUnitPrice(this.getStandardUnitPrice());
    serviceDTO.setStatus(this.getStatus());
    return serviceDTO;
  }

  @Column(name = "appoint_order_id")
  public Long getAppointOrderId() {
    return appointOrderId;
  }

  public void setAppointOrderId(Long appointOrderId) {
    this.appointOrderId = appointOrderId;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "service_id")
  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  @Column(name = "service")
  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }

  @Column(name = "total")
  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  @Column(name = "standard_hours")
  public Double getStandardHours() {
    return standardHours;
  }

  public void setStandardHours(Double standardHours) {
    this.standardHours = standardHours;
  }

  @Column(name = "standard_unit_price")
  public Double getStandardUnitPrice() {
    return standardUnitPrice;
  }

  public void setStandardUnitPrice(Double standardUnitPrice) {
    this.standardUnitPrice = standardUnitPrice;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public ObjectStatus getStatus() {
    return status;
  }

  public void setStatus(ObjectStatus status) {
    this.status = status;
  }



}
