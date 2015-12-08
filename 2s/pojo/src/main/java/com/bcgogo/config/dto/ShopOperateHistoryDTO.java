package com.bcgogo.config.dto;

import com.bcgogo.enums.shop.ShopOperateType;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-30
 * Time: 下午3:55
 */
public class ShopOperateHistoryDTO {
  private Long id;
  private ShopOperateType operateType;
  private Long operateShopId;
  private Long operateUserId;
  private String operateUserName;
  private Long operateTime;
  private String reason;
  private Long trialStartTime;  //使用开始时间
  private Long trialEndTime;  //使用结束时间

  public Long getTrialStartTime() {
    return trialStartTime;
  }

  public void setTrialStartTime(Long trialStartTime) {
    this.trialStartTime = trialStartTime;
  }

  public Long getTrialEndTime() {
    return trialEndTime;
  }

  public void setTrialEndTime(Long trialEndTime) {
    this.trialEndTime = trialEndTime;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ShopOperateType getOperateType() {
    return operateType;
  }

  public void setOperateType(ShopOperateType operateType) {
    this.operateType = operateType;
  }

  public Long getOperateShopId() {
    return operateShopId;
  }

  public void setOperateShopId(Long operateShopId) {
    this.operateShopId = operateShopId;
  }

  public Long getOperateUserId() {
    return operateUserId;
  }

  public void setOperateUserId(Long operateUserId) {
    this.operateUserId = operateUserId;
  }

  public Long getOperateTime() {
    return operateTime;
  }

  public void setOperateTime(Long operateTime) {
    this.operateTime = operateTime;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public String getOperateUserName() {
    return operateUserName;
  }

  public void setOperateUserName(String operateUserName) {
    this.operateUserName = operateUserName;
  }

}
