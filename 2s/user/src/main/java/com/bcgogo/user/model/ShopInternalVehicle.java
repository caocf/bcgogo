package com.bcgogo.user.model;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created by XinyuQiu on 14-12-11.
 */
@Entity
@Table(name = "shop_internal_vehicle")
public class ShopInternalVehicle extends LongIdentifier {
  private Long shopId;
  private Long vehicleId;
  private DeletedType deleted;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "vehicle_id")
  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "deleted")
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
