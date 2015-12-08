package com.bcgogo.txn.dto;

import com.bcgogo.enums.ServiceStatus;
import com.bcgogo.enums.ServiceTimeType;
import com.bcgogo.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-12-13
 * Time: 下午2:09
 */
public class ServiceHistoryDTO {
  private Long id;
  private Long serviceId;
  private Long historyVersion;
  private Long shopId;
  private String name;
  private Double price;
  private String memo;
  private Double percentage;
  private Double percentageAmount;
  private String pointsExchangeable;
  private ServiceStatus status;
  private ServiceTimeType timeType;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  public Long getHistoryVersion() {
    return historyVersion;
  }

  public void setHistoryVersion(Long historyVersion) {
    this.historyVersion = historyVersion;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Double getPercentage() {
    return percentage;
  }

  public void setPercentage(Double percentage) {
    this.percentage = percentage;
  }

  public Double getPercentageAmount() {
    return percentageAmount;
  }

  public void setPercentageAmount(Double percentageAmount) {
    this.percentageAmount = percentageAmount;
  }

  public String getPointsExchangeable() {
    return pointsExchangeable;
  }

  public void setPointsExchangeable(String pointsExchangeable) {
    this.pointsExchangeable = pointsExchangeable;
  }

  public ServiceStatus getStatus() {
    return status;
  }

  public void setStatus(ServiceStatus status) {
    this.status = status;
  }

  public ServiceTimeType getTimeType() {
    return timeType;
  }

  public void setTimeType(ServiceTimeType timeType) {
    this.timeType = timeType;
  }

  public boolean compareSame(ServiceDTO serviceDTO) {
    if(serviceDTO == null){
      return false;
    }
    if(!StringUtil.compareSame(getName(), serviceDTO.getName())){
      return false;
    }
    return true;
  }
}
