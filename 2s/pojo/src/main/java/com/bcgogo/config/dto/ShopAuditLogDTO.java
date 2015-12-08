package com.bcgogo.config.dto;

import com.bcgogo.enums.shop.AuditStatus;
import com.bcgogo.utils.DateUtil;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-10-8
 * Time: 下午4:24
 * To change this template use File | Settings | File Templates.
 */
public class ShopAuditLogDTO implements Serializable {
  private Long id;
  private Long shopId;
  private AuditStatus auditStatus;
  private Long auditorId;
  private Long auditTime;
  private String reason;
  private String auditorName;
  private String auditTimeStr;

  public String getAuditTimeStr() {
    return auditTimeStr;
  }

  public void setAuditTimeStr(String auditTimeStr) {
    this.auditTimeStr = auditTimeStr;
  }

  public String getAuditorName() {
    return auditorName;
  }

  public void setAuditorName(String auditorName) {
    this.auditorName = auditorName;
  }

  public String getReason() {

    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public Long getAuditTime() {

    return auditTime;
  }

  public void setAuditTime(Long auditTime) {
    this.auditTime = auditTime;
    if(auditTime != null) {
      setAuditTimeStr(DateUtil.dateLongToStr(auditTime,DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN));
    }
  }

  public Long getAuditorId() {

    return auditorId;
  }

  public void setAuditorId(Long auditorId) {
    this.auditorId = auditorId;
  }

  public AuditStatus getAuditStatus() {

    return auditStatus;
  }

  public void setAuditStatus(AuditStatus auditStatus) {
    this.auditStatus = auditStatus;
  }

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
}
