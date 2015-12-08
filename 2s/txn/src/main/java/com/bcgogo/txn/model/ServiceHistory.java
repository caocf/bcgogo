package com.bcgogo.txn.model;

import com.bcgogo.enums.ServiceStatus;
import com.bcgogo.enums.ServiceTimeType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.ServiceDTO;
import com.bcgogo.txn.dto.ServiceHistoryDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-19
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "service_history")
public class ServiceHistory extends LongIdentifier {
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
  
  public ServiceHistory() {
  }

  public void setServiceDTO(ServiceDTO serviceDTO){
    serviceId = serviceDTO.getId();
    shopId = serviceDTO.getShopId();
    name = serviceDTO.getName();
    price = serviceDTO.getPrice();
    memo = serviceDTO.getMemo();
    percentage = serviceDTO.getPercentage();
    percentageAmount = serviceDTO.getPercentageAmount();
    pointsExchangeable = serviceDTO.getPointsExchangeable();
    status = serviceDTO.getStatus();
    timeType = serviceDTO.getTimeType();
    historyVersion = serviceDTO.getVersion();
  }

  @Column(name="service_id")
  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  @Column(name="history_version")
  public Long getHistoryVersion() {
    return historyVersion;
  }

  public void setHistoryVersion(Long historyVersion) {
    this.historyVersion = historyVersion;
  }

  public ServiceHistory(Long shopId, String name, Double price, ServiceStatus status) {
    this.shopId = shopId;
    this.name = name;
    this.price = price;
    this.status = status;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "name",length = 200)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "price")
  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  @Column(name = "memo", length = 100)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name="percentage")
  public Double getPercentage() {
      return percentage;
  }
  @Column(name="percentage_amount")
  public Double getPercentageAmount() {
      return percentageAmount;
  }
  @Column(name="points_exchangeable")
  public String getPointsExchangeable() {
      return pointsExchangeable;
  }

  public void setPercentage(Double percentage) {
      this.percentage = percentage;
  }

  public void setPercentageAmount(Double percentageAmount) {
      this.percentageAmount = percentageAmount;
  }

  public void setPointsExchangeable(String pointsExchangeable) {
      this.pointsExchangeable = pointsExchangeable;
  }

  @Enumerated(EnumType.STRING)
  @Column(name="status")
  public ServiceStatus getStatus() {
    return status;
  }

  public void setStatus(ServiceStatus status) {
    this.status = status;
  }

  @Enumerated(EnumType.STRING)
  @Column(name="time_type")
  public ServiceTimeType getTimeType() {
    return timeType;
  }

  public void setTimeType(ServiceTimeType timeType) {
    this.timeType = timeType;
  }

  public ServiceHistoryDTO toDTO() {
    ServiceHistoryDTO serviceHistoryDTO = new ServiceHistoryDTO();
    serviceHistoryDTO.setServiceId(serviceId);
    serviceHistoryDTO.setHistoryVersion(historyVersion);
    serviceHistoryDTO.setShopId(shopId);
    serviceHistoryDTO.setName(name);
    serviceHistoryDTO.setPrice(price);
    serviceHistoryDTO.setMemo(memo);
    serviceHistoryDTO.setPercentage(percentage);
    serviceHistoryDTO.setPercentageAmount(percentageAmount);
    serviceHistoryDTO.setPointsExchangeable(pointsExchangeable);
    serviceHistoryDTO.setStatus(status);
    serviceHistoryDTO.setTimeType(timeType);
    serviceHistoryDTO.setId(getId());
    return serviceHistoryDTO;
  }

}