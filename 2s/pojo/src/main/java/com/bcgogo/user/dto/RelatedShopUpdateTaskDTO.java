package com.bcgogo.user.dto;

import com.bcgogo.enums.ExeStatus;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-8-8
 * Time: 下午3:16
 */
public class RelatedShopUpdateTaskDTO implements Serializable {
  private Long id;
  private Long shopId;
  private ExeStatus exeStatus;
  private Long createdTime;
  private Long finishTime;

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

  public ExeStatus getExeStatus() {
    return exeStatus;
  }

  public void setExeStatus(ExeStatus exeStatus) {
    this.exeStatus = exeStatus;
  }

  public Long getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  public Long getFinishTime() {
    return finishTime;
  }

  public void setFinishTime(Long finishTime) {
    this.finishTime = finishTime;
  }
}
