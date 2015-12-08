package com.bcgogo.txn.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: zhuyinjia
 * Date: 11-11-8
 * Time: 上午10:44
 * To change this template use File | Settings | File Templates.
 */
public class ScheduleServiceEventDTO implements Serializable {
  public ScheduleServiceEventDTO() {
  }

  private Long id;
  private Long shopId;
  private Long vechicleId;
  private Long customerId;
  private String serviceType;
  private String[] serviceTypeArr;
  private Long serviceDate;
  private String serviceDateStr;
  private String content;

  public String[] getServiceTypeArr() {
    return serviceTypeArr;
  }

  public void setServiceTypeArr(String[] serviceTypeArr) {
    this.serviceTypeArr = serviceTypeArr;
  }

  public String getServiceDateStr() {
     return serviceDateStr;
  }

  public void setServiceDateStr(String serviceDateStr) {
    this.serviceDateStr = serviceDateStr;
  }

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

  public Long getVechicleId() {
    return vechicleId;
  }

  public void setVechicleId(Long vechicleId) {
    this.vechicleId = vechicleId;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public String getServiceType() {
    return serviceType;
  }

  public void setServiceType(String serviceType) {
    this.serviceType = serviceType;
  }

  public Long getServiceDate() {
    return serviceDate;
  }

  public void setServiceDate(Long serviceDate) {
    this.serviceDate = serviceDate;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
