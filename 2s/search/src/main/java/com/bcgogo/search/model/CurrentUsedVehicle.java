package com.bcgogo.search.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.search.dto.CurrentUsedVehicleDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-5-29
 * Time: 上午9:42
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "current_used_vehicle")
public class CurrentUsedVehicle extends LongIdentifier {
  private Long shopId;
  private Long timeOrder;
  private String brand;
//  private String type;

  public CurrentUsedVehicle() {
  }

  public CurrentUsedVehicle(CurrentUsedVehicleDTO currentUsedVehicleDTO) {
    this.shopId = currentUsedVehicleDTO.getShopId();
    this.timeOrder = currentUsedVehicleDTO.getTimeOrder();
    this.brand = currentUsedVehicleDTO.getBrand();
  }

  public CurrentUsedVehicle fromDTO(CurrentUsedVehicleDTO currentUsedVehicleDTO) {
    this.shopId = currentUsedVehicleDTO.getShopId();
    this.timeOrder = currentUsedVehicleDTO.getTimeOrder();
    this.brand = currentUsedVehicleDTO.getBrand();
    return this;
  }

  public boolean equals(CurrentUsedVehicleDTO currentUsedVehicleDTO) {
    if (currentUsedVehicleDTO == null) return false;
    if (currentUsedVehicleDTO.getBrand() == null || this.getBrand() == null) return false;
    return this.getBrand().equals(currentUsedVehicleDTO.getBrand()) ? true : false;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }


  @Column(name = "time_order")
  public Long getTimeOrder() {
    return timeOrder;
  }

  public void setTimeOrder(Long timeOrder) {
    this.timeOrder = timeOrder;
  }

  @Column(name = "brand", length = 200)
  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }
}
