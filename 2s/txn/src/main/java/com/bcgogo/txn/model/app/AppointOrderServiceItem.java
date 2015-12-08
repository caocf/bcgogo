package com.bcgogo.txn.model.app;

import com.bcgogo.enums.common.ObjectStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.AppointOrderServiceItemDTO;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-9-5
 * Time: 下午3:49
 */
@Entity
@Table(name = "appoint_order_service_item")
public class AppointOrderServiceItem extends LongIdentifier {
  private Long shopId;
  private Long appointOrderId;
  private Long serviceId;//serviceCategory的外键
  private String serviceName;//服务内容
  private ObjectStatus status;//item的状态

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "appoint_order_id")
  public Long getAppointOrderId() {
    return appointOrderId;
  }

  public void setAppointOrderId(Long appointOrderId) {
    this.appointOrderId = appointOrderId;
  }

  @Column(name = "service_id")
  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  @Column(name = "service_name")
  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public ObjectStatus getStatus() {
    return status;
  }

  public void setStatus(ObjectStatus status) {
    this.status = status;
  }

  public AppointOrderServiceItemDTO toDTO(){
    AppointOrderServiceItemDTO appointOrderServiceItemDTO = new AppointOrderServiceItemDTO();
    appointOrderServiceItemDTO.setId(getId());
    appointOrderServiceItemDTO.setShopId(getShopId());
    appointOrderServiceItemDTO.setServiceId(getServiceId());
    appointOrderServiceItemDTO.setAppointOrderId(getAppointOrderId());
    appointOrderServiceItemDTO.setStatus(getStatus());
    appointOrderServiceItemDTO.setServiceName(getServiceName());
    return appointOrderServiceItemDTO;
  }

  public void fromDTO(AppointOrderServiceItemDTO appointOrderServiceItemDTO) {
    if(appointOrderServiceItemDTO != null){
      this.setId(appointOrderServiceItemDTO.getId());
      this.setShopId(appointOrderServiceItemDTO.getShopId());
      this.setServiceId(appointOrderServiceItemDTO.getServiceId());
      this.setAppointOrderId(appointOrderServiceItemDTO.getAppointOrderId());
      this.setStatus(appointOrderServiceItemDTO.getStatus());
      this.setServiceName(appointOrderServiceItemDTO.getServiceName());
    }
  }
}
