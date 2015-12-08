package com.bcgogo.config.model;

import com.bcgogo.config.dto.ShopAuditLogDTO;
import com.bcgogo.enums.DonationType;
import com.bcgogo.enums.shop.AuditStatus;
import com.bcgogo.enums.shop.RegisterType;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-3-30
 * Time: 下午5:05
 * 店铺审核日志
 */
@Entity
@Table(name = "shop_audit_log")
public class ShopAuditLog extends LongIdentifier {
  private Long shopId;
  private AuditStatus auditStatus;
  private Long auditorId;
  private Long auditTime;
  private String reason;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "audit_status")
  public AuditStatus getAuditStatus() {
    return auditStatus;
  }

  public void setAuditStatus(AuditStatus auditStatus) {
    this.auditStatus = auditStatus;
  }

  @Column(name = "auditor_id")
  public Long getAuditorId() {
    return auditorId;
  }

  public void setAuditorId(Long auditorId) {
    this.auditorId = auditorId;
  }

  @Column(name = "audit_time")
  public Long getAuditTime() {
    return auditTime;
  }

  public void setAuditTime(Long auditTime) {
    this.auditTime = auditTime;
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
    return "ShopAuditLog{" +
        "id=" + this.getId() +
        "shopId=" + shopId +
        ", auditStatus=" + auditStatus +
        ", auditorId=" + auditorId +
        ", auditTime=" + auditTime +
        ", reason='" + reason + '\'' +
        '}';
  }

  public ShopAuditLogDTO toDTO() {
    ShopAuditLogDTO shopAuditLogDTO = new ShopAuditLogDTO();
    shopAuditLogDTO.setId(getId());
    shopAuditLogDTO.setAuditorId(getAuditorId());
    shopAuditLogDTO.setShopId(getShopId());
    shopAuditLogDTO.setAuditStatus(getAuditStatus());
    shopAuditLogDTO.setAuditTime(getAuditTime());
    shopAuditLogDTO.setReason(getReason());
    return shopAuditLogDTO;
  }
}
