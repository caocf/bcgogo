package com.bcgogo.user.dto;

import com.bcgogo.enums.ExeStatus;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-8-20
 * Time: 下午2:16
 * To change this template use File | Settings | File Templates.
 */
public class CusOrSupOrderIndexScheduleDTO implements Serializable {
  private Long id;
  private Long shopId;
  private Long customerId;
  private Long supplierId;
  private Long createdTime;
  private Long finishedTime;
  private ExeStatus exeStatus;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  public Long getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  public Long getFinishedTime() {
    return finishedTime;
  }

  public void setFinishedTime(Long finishedTime) {
    this.finishedTime = finishedTime;
  }

  public ExeStatus getExeStatus() {
    return exeStatus;
  }

  public void setExeStatus(ExeStatus exeStatus) {
    this.exeStatus = exeStatus;
  }
}
