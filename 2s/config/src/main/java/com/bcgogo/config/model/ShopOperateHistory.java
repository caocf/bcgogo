package com.bcgogo.config.model;

import com.bcgogo.config.dto.ShopOperateHistoryDTO;
import com.bcgogo.enums.config.AttachmentType;
import com.bcgogo.enums.shop.ShopOperateType;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: XiaoJian
 * Date: 10/10/11
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "shop_operate_history")
public class ShopOperateHistory extends LongIdentifier {
  private ShopOperateType operateType;
  private Long operateShopId;
  private Long operateUserId;
  private Long operateTime;
  private Long trialStartTime;  //使用开始时间
  private Long trialEndTime;  //使用结束时间
  private String reason;

  public ShopOperateHistoryDTO toDTO() {
    ShopOperateHistoryDTO dto = new ShopOperateHistoryDTO();
    dto.setId(this.getId());
    dto.setOperateShopId(this.getOperateShopId());
    dto.setOperateTime(this.getOperateTime());
    dto.setOperateUserId(this.getOperateUserId());
    dto.setOperateType(this.getOperateType());
    dto.setTrialEndTime(this.getTrialEndTime());
    dto.setTrialStartTime(this.getTrialStartTime());
    dto.setReason(this.getReason());
    return dto;
  }

  public ShopOperateHistory fromDTO(ShopOperateHistoryDTO dto) {
    this.setId(dto.getId());
    this.setOperateShopId(dto.getOperateShopId());
    this.setOperateTime(dto.getOperateTime());
    this.setOperateUserId(dto.getOperateUserId());
    this.setOperateType(dto.getOperateType());
    this.setTrialEndTime(dto.getTrialEndTime());
    this.setTrialStartTime(dto.getTrialStartTime());
    this.setReason(dto.getReason());
    return this;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "operate_type")
  public ShopOperateType getOperateType() {
    return operateType;
  }

  public void setOperateType(ShopOperateType operateType) {
    this.operateType = operateType;
  }

  @Column(name = "operate_shop_id")
  public Long getOperateShopId() {
    return operateShopId;
  }

  public void setOperateShopId(Long operateShopId) {
    this.operateShopId = operateShopId;
  }

  @Column(name = "operate_user_id")
  public Long getOperateUserId() {
    return operateUserId;
  }

  public void setOperateUserId(Long operateUserId) {
    this.operateUserId = operateUserId;
  }

  @Column(name = "operate_time")
  public Long getOperateTime() {
    return operateTime;
  }

  public void setOperateTime(Long operateTime) {
    this.operateTime = operateTime;
  }

  @Column(name = "reason")
  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  @Column(name = "trial_start_time")
  public Long getTrialStartTime() {
    return trialStartTime;
  }

  public void setTrialStartTime(Long trialStartTime) {
    this.trialStartTime = trialStartTime;
  }

  @Column(name = "trial_end_time")
  public Long getTrialEndTime() {
    return trialEndTime;
  }

  public void setTrialEndTime(Long trialEndTime) {
    this.trialEndTime = trialEndTime;
  }
}