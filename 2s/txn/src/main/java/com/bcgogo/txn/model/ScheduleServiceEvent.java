package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.ScheduleServiceEventDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: zhuyinjia
 * Date: 11-11-8
 * Time: 上午10:40
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "schedule_service_event")
public class ScheduleServiceEvent extends LongIdentifier {
  public ScheduleServiceEvent() {
  }

  public ScheduleServiceEventDTO toDTO() {
    ScheduleServiceEventDTO scheduleServiceEventDTO = new ScheduleServiceEventDTO();

    scheduleServiceEventDTO.setId(getId());
    scheduleServiceEventDTO.setShopId(getShopId());
    scheduleServiceEventDTO.setVechicleId(getVechicleId());
    scheduleServiceEventDTO.setCustomerId(getCustomerId());
    scheduleServiceEventDTO.setServiceType(getServiceType());
    scheduleServiceEventDTO.setServiceDate(getServiceDate());
    scheduleServiceEventDTO.setContent(getContent());
    return scheduleServiceEventDTO;
  }

  public ScheduleServiceEvent fromDTO(ScheduleServiceEventDTO scheduleServiceEventDTO) {
    if(scheduleServiceEventDTO == null)
      return this;
    setId(scheduleServiceEventDTO.getId());
    this.shopId = scheduleServiceEventDTO.getShopId();
    this.vechicleId = scheduleServiceEventDTO.getVechicleId();
    this.customerId = scheduleServiceEventDTO.getCustomerId();
    this.serviceType = scheduleServiceEventDTO.getServiceType();
    this.serviceDate = scheduleServiceEventDTO.getServiceDate();
    this.content = scheduleServiceEventDTO.getContent();
    return this;
  }

  private Long shopId;
  private Long vechicleId;
  private Long customerId;
  private String serviceType;
  private Long serviceDate;
  private String content;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "vechicle_id")
  public Long getVechicleId() {
    return vechicleId;
  }

  public void setVechicleId(Long vechicleId) {
    this.vechicleId = vechicleId;
  }

  @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name = "service_type")
  public String getServiceType() {
    return serviceType;
  }

  public void setServiceType(String serviceType) {
    this.serviceType = serviceType;
  }

  @Column(name = "service_date")
  public Long getServiceDate() {
    return serviceDate;
  }

  public void setServiceDate(Long serviceDate) {
    this.serviceDate = serviceDate;
  }

  @Column(name = "content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

}
