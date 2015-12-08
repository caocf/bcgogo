package com.bcgogo.user.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.CustomerVehicleStatDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: sunyingzi
 * Date: 11-12-19
 * Time: 上午9:50
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "customer_vehicle_stat")
public class CustomerVehicleStat extends LongIdentifier {
  public Long customerId;
  public Long vehicleId;
  public Long lastDate;
  public double totalAmount;
  public double totalArrears;

  @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name = "vehicle_id")
  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }

  @Column(name = "last_date")
  public Long getLastDate() {
    return lastDate;
  }

  public void setLastDate(Long lastDate) {
    this.lastDate = lastDate;
  }

  @Column(name = "total_amount")
  public double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(double totalAmount) {
    this.totalAmount = totalAmount;
  }

  @Column(name = "total_arrears")
  public double getTotalArrears() {
    return totalArrears;
  }

  public void setTotalArrears(double totalArrears) {
    this.totalArrears = totalArrears;
  }

  public CustomerVehicleStat() {

  }

  public CustomerVehicleStat(CustomerVehicleStatDTO customerVehicleStatDTO) {

    this.setId(customerVehicleStatDTO.getId());
    this.setCustomerId(customerVehicleStatDTO.getCustomerId());
    this.setVehicleId(customerVehicleStatDTO.getVehicleId());
    this.setLastDate(customerVehicleStatDTO.getLastDate());
    this.setTotalAmount(customerVehicleStatDTO.getTotalAmount());
    this.setTotalArrears(customerVehicleStatDTO.getTotalArrears());
  }

  public CustomerVehicleStat fromDTO(CustomerVehicleStatDTO customerVehicleStatDTO) {
    this.setId(customerVehicleStatDTO.getId());
    this.setCustomerId(customerVehicleStatDTO.getCustomerId());
    this.setVehicleId(customerVehicleStatDTO.getVehicleId());
    this.setLastDate(customerVehicleStatDTO.getLastDate());
    this.setTotalAmount(customerVehicleStatDTO.getTotalAmount());
    this.setTotalArrears(customerVehicleStatDTO.getTotalArrears());

    return this;
  }

  public CustomerVehicleStatDTO toDTO() {
    CustomerVehicleStatDTO customerVehicleStatDTO = new CustomerVehicleStatDTO();

    customerVehicleStatDTO.setId(this.getId());
    customerVehicleStatDTO.setCustomerId(this.getCustomerId());
    customerVehicleStatDTO.setVehicleId(this.getVehicleId());
    customerVehicleStatDTO.setLastDate(this.getLastDate());
    customerVehicleStatDTO.setTotalAmount(this.getTotalAmount());
    customerVehicleStatDTO.setTotalArrears(this.getTotalArrears());

    return customerVehicleStatDTO;
  }


}
