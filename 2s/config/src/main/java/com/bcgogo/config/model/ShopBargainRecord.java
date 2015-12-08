package com.bcgogo.config.model;

import com.bcgogo.config.dto.ShopBargainRecordDTO;
import com.bcgogo.enums.shop.AuditStatus;
import com.bcgogo.enums.shop.BargainStatus;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-3-30
 * Time: 下午5:05
 * 议价记录
 */
@Entity
@Table(name = "shop_bargain_record")
public class ShopBargainRecord extends LongIdentifier {
  private Long shopId;
  private BargainStatus bargainStatus;
  private Double originalPrice;

  private Long applicantId;
  private Long applicationTime;
  private Double applicationPrice;
  private String applicationReason;

  private Long auditorId;
  private Long auditTime;
  private String auditReason;

  public ShopBargainRecordDTO toDTO() {
    ShopBargainRecordDTO dto = new ShopBargainRecordDTO();
    dto.setId(this.getId());
    dto.setShopId(this.getShopId());
    dto.setBargainStatus(this.getBargainStatus());
    dto.setOriginalPrice(this.getOriginalPrice());
    dto.setApplicantId(this.getApplicantId());
    dto.setApplicationTime(this.getApplicationTime());
    dto.setApplicationPrice(this.getApplicationPrice());
    dto.setApplicationReason(this.getApplicationReason());
    dto.setAuditorId(this.getAuditorId());
    dto.setAuditTime(this.getAuditTime());
    dto.setAuditReason(this.getAuditReason());
    return dto;
  }

  public void fromDTO(ShopBargainRecordDTO dto) {
    this.setId(dto.getId());
    this.shopId = dto.getShopId();
    this.bargainStatus = dto.getBargainStatus();
    this.originalPrice = dto.getOriginalPrice();
    this.applicantId = dto.getApplicantId();
    this.applicationTime = dto.getApplicationTime();
    this.applicationPrice = dto.getApplicationPrice();
    this.applicationReason = dto.getApplicationReason();
    this.auditorId = dto.getAuditorId();
    this.auditTime = dto.getAuditTime();
    this.auditReason = dto.getAuditReason();
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "bargain_status")
  public BargainStatus getBargainStatus() {
    return bargainStatus;
  }

  public void setBargainStatus(BargainStatus bargainStatus) {
    this.bargainStatus = bargainStatus;
  }

  @Column(name = "original_price")
  public Double getOriginalPrice() {
    return originalPrice;
  }

  public void setOriginalPrice(Double originalPrice) {
    this.originalPrice = originalPrice;
  }

  @Column(name = "applicant_id")
  public Long getApplicantId() {
    return applicantId;
  }

  public void setApplicantId(Long applicantId) {
    this.applicantId = applicantId;
  }

  @Column(name = "application_time")
  public Long getApplicationTime() {
    return applicationTime;
  }

  public void setApplicationTime(Long applicationTime) {
    this.applicationTime = applicationTime;
  }

  @Column(name = "application_price")
  public Double getApplicationPrice() {
    return applicationPrice;
  }

  public void setApplicationPrice(Double applicationPrice) {
    this.applicationPrice = applicationPrice;
  }

  @Column(name = "application_reason")
  public String getApplicationReason() {
    return applicationReason;
  }

  public void setApplicationReason(String applicationReason) {
    this.applicationReason = applicationReason;
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

  @Column(name = "audit_reason")
  public String getAuditReason() {
    return auditReason;
  }

  public void setAuditReason(String auditReason) {
    this.auditReason = auditReason;
  }
}
