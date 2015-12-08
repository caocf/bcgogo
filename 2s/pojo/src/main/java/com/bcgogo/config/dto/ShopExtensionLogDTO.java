package com.bcgogo.config.dto;

import com.bcgogo.enums.shop.BargainStatus;

/**
 * User: ZhangJuntao
 * Date: 13-3-31
 * Time: 下午2:46
 */
public class ShopExtensionLogDTO {
  private Long id;
  private Long shopId;
  private Integer extensionDays = 1;
  private Long operatorId;
  private String operatorName;
  private Long operateTime;
  private Long trialEndTime;  //试用结束时间
  private String reason;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getExtensionDays() {
    return extensionDays;
  }

  public void setExtensionDays(Integer extensionDays) {
    this.extensionDays = extensionDays;
  }

  public Long getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(Long operatorId) {
    this.operatorId = operatorId;
  }

  public String getOperatorName() {
    return operatorName;
  }

  public void setOperatorName(String operatorName) {
    this.operatorName = operatorName;
  }

  public Long getOperateTime() {
    return operateTime;
  }

  public void setOperateTime(Long operateTime) {
    this.operateTime = operateTime;
  }

  public Long getTrialEndTime() {
    return trialEndTime;
  }

  public void setTrialEndTime(Long trialEndTime) {
    this.trialEndTime = trialEndTime;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  @Override
  public String toString() {
    return "ShopExtensionLogDTO{" +
        "id=" + id +
        ", shopId=" + shopId +
        ", extensionDays=" + extensionDays +
        ", operatorId=" + operatorId +
        ", operatorName='" + operatorName + '\'' +
        ", operateTime=" + operateTime +
        ", trialEndTime=" + trialEndTime +
        ", reason='" + reason + '\'' +
        '}';
  }
}
