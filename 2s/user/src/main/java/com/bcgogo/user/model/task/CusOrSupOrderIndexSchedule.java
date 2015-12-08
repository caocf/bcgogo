package com.bcgogo.user.model.task;

import com.bcgogo.enums.ExeStatus;
import com.bcgogo.user.dto.CusOrSupOrderIndexScheduleDTO;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-8-20
 * Time: 下午2:03
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "cus_sup_order_index_schedule")
public class CusOrSupOrderIndexSchedule extends LongIdentifier {
  private Long shopId;
  private Long customerId;
  private Long supplierId;
  private Long createdTime;
  private Long finishedTime;
  private ExeStatus exeStatus;

  public CusOrSupOrderIndexSchedule() {

  }

  public CusOrSupOrderIndexSchedule(CusOrSupOrderIndexScheduleDTO cusOrSupOrderIndexScheduleDTO) {
    setShopId(cusOrSupOrderIndexScheduleDTO.getShopId());
    setCustomerId(cusOrSupOrderIndexScheduleDTO.getCustomerId());
    setSupplierId(cusOrSupOrderIndexScheduleDTO.getSupplierId());
    setCreatedTime(cusOrSupOrderIndexScheduleDTO.getCreatedTime());
    setFinishedTime(cusOrSupOrderIndexScheduleDTO.getFinishedTime());
    setExeStatus(cusOrSupOrderIndexScheduleDTO.getExeStatus());
  }

  public CusOrSupOrderIndexScheduleDTO toDTO() {
    CusOrSupOrderIndexScheduleDTO cusOrSupOrderIndexScheduleDTO = new CusOrSupOrderIndexScheduleDTO();
    cusOrSupOrderIndexScheduleDTO.setId(getId());
    cusOrSupOrderIndexScheduleDTO.setShopId(getShopId());
    cusOrSupOrderIndexScheduleDTO.setCustomerId(getCustomerId());
    cusOrSupOrderIndexScheduleDTO.setSupplierId(getSupplierId());
    cusOrSupOrderIndexScheduleDTO.setCreatedTime(getCreatedTime());
    cusOrSupOrderIndexScheduleDTO.setFinishedTime(getFinishedTime());
    cusOrSupOrderIndexScheduleDTO.setExeStatus(getExeStatus());
    return cusOrSupOrderIndexScheduleDTO;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name = "supplier_id")

  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  @Column(name = "exe_status")
  @Enumerated(EnumType.STRING)
  public ExeStatus getExeStatus() {
    return exeStatus;
  }

  public void setExeStatus(ExeStatus exeStatus) {
    this.exeStatus = exeStatus;
  }

  @Column(name = "created_time")

  public Long getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @Column(name = "finished_time")

  public Long getFinishedTime() {
    return finishedTime;
  }

  public void setFinishedTime(Long finishedTime) {
    this.finishedTime = finishedTime;
  }
}
