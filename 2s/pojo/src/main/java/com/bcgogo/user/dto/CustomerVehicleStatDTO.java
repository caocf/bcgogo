package com.bcgogo.user.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: sunyingzi
 * Date: 11-12-19
 * Time: 上午10:02
 * To change this template use File | Settings | File Templates.
 */
public class CustomerVehicleStatDTO implements Serializable {

  private Long id;
  public Long customerId;
  public Long vehicleId;
  public Long lastDate;
  public double totalAmount;
  public double totalArrears;

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public double getTotalArrears() {
    return totalArrears;
  }

  public void setTotalArrears(double totalArrears) {
    this.totalArrears = totalArrears;
  }

  public double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(double totalAmount) {
    this.totalAmount = totalAmount;
  }

  public Long getLastDate() {
    return lastDate;
  }

  public void setLastDate(Long lastDate) {
    this.lastDate = lastDate;
  }

  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }
}
