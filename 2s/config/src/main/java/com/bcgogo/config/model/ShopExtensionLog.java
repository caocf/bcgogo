package com.bcgogo.config.model;

import com.bcgogo.config.dto.ShopExtensionLogDTO;
import com.bcgogo.enums.shop.AuditStatus;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * User: ZhangJuntao
 * Date: 13-3-31
 * Time: 下午9:39
 */
@Entity
@Table(name = "shop_extension_log")
public class ShopExtensionLog extends LongIdentifier {
  private Long shopId;
  private Integer extensionDays = 1;
  private Long operatorId;
  private Long operateTime;
  private Long trialEndTime;  //试用结束时间
  private String reason;

  public ShopExtensionLog() {
    super();
  }

  public ShopExtensionLog(long shopId, int extensionDays, long operatorId, String reason) {
    this.setExtensionDays(extensionDays);
    this.setShopId(shopId);
    this.setOperatorId(operatorId);
    this.setOperateTime(System.currentTimeMillis());
    this.setReason(reason);
  }

  public ShopExtensionLog(ShopExtensionLogDTO dto) {
    this.setId(dto.getId());
    this.setExtensionDays(dto.getExtensionDays());
    this.setShopId(dto.getShopId());
    this.setOperatorId(dto.getOperatorId());
    this.setOperateTime(dto.getOperateTime());
    this.setReason(dto.getReason());
    this.setTrialEndTime(dto.getTrialEndTime());
  }

  public ShopExtensionLogDTO toDTO() {
    ShopExtensionLogDTO dto = new ShopExtensionLogDTO();
    dto.setId(this.getId());
    dto.setExtensionDays(this.getExtensionDays());
    dto.setShopId(this.getShopId());
    dto.setOperatorId(this.getOperatorId());
    dto.setOperateTime(this.getOperateTime());
    dto.setReason(this.getReason());
    dto.setTrialEndTime(this.getTrialEndTime());
    return dto;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "extension_days")
  public Integer getExtensionDays() {
    return extensionDays;
  }

  public void setExtensionDays(Integer extensionDays) {
    this.extensionDays = extensionDays;
  }

  @Column(name = "operator_id")
  public Long getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(Long operatorId) {
    this.operatorId = operatorId;
  }

  @Column(name = "operate_time")
  public Long getOperateTime() {
    return operateTime;
  }

  public void setOperateTime(Long operateTime) {
    this.operateTime = operateTime;
  }

  @Column(name = "trial_end_time")
  public Long getTrialEndTime() {
    return trialEndTime;
  }

  public void setTrialEndTime(Long trialEndTime) {
    this.trialEndTime = trialEndTime;
  }

  @Column(name = "reason")
  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  @Override
  public String toString() {
    return "ShopExtensionLog{" +
        "shopId=" + shopId +
        ", extensionDays=" + extensionDays +
        ", operatorId=" + operatorId +
        ", operateTime=" + operateTime +
        ", trialEndTime=" + trialEndTime +
        ", reason='" + reason + '\'' +
        '}';
  }
}

