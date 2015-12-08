package com.bcgogo.config.dto;

import com.bcgogo.enums.shop.BargainStatus;

/**
 * User: ZhangJuntao
 * Date: 13-3-31
 * Time: 下午2:46
 */
public class ShopBargainRecordDTO {
  private Long id;
  private Long shopId;
  private BargainStatus bargainStatus;
  private Double originalPrice;
  private Double applicationPrice;

  private Long applicantId;
  private String applicantName;
  private Long applicationTime;
  private String applicationReason;

  private Long auditorId;
  private String auditorName;
  private Long auditTime;
  private String auditReason;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public BargainStatus getBargainStatus() {
    return bargainStatus;
  }

  public void setBargainStatus(BargainStatus bargainStatus) {
    this.bargainStatus = bargainStatus;
  }

  public Double getOriginalPrice() {
    return originalPrice;
  }

  public void setOriginalPrice(Double originalPrice) {
    this.originalPrice = originalPrice;
  }

  public Long getApplicantId() {
    return applicantId;
  }

  public void setApplicantId(Long applicantId) {
    this.applicantId = applicantId;
  }

  public String getApplicantName() {
    return applicantName;
  }

  public void setApplicantName(String applicantName) {
    this.applicantName = applicantName;
  }

  public Long getApplicationTime() {
    return applicationTime;
  }

  public void setApplicationTime(Long applicationTime) {
    this.applicationTime = applicationTime;
  }

  public Double getApplicationPrice() {
    return applicationPrice;
  }

  public void setApplicationPrice(Double applicationPrice) {
    this.applicationPrice = applicationPrice;
  }

  public String getApplicationReason() {
    return applicationReason;
  }

  public void setApplicationReason(String applicationReason) {
    this.applicationReason = applicationReason;
  }

  public Long getAuditorId() {
    return auditorId;
  }

  public void setAuditorId(Long auditorId) {
    this.auditorId = auditorId;
  }

  public String getAuditorName() {
    return auditorName;
  }

  public void setAuditorName(String auditorName) {
    this.auditorName = auditorName;
  }

  public Long getAuditTime() {
    return auditTime;
  }

  public void setAuditTime(Long auditTime) {
    this.auditTime = auditTime;
  }

  public String getAuditReason() {
    return auditReason;
  }

  public void setAuditReason(String auditReason) {
    this.auditReason = auditReason;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
