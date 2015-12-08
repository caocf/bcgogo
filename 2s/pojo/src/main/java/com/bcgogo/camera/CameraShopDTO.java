package com.bcgogo.camera;

import com.bcgogo.enums.DeletedType;

import java.io.Serializable;

public class CameraShopDTO implements Serializable {
  private String id;
  private Long camera_id;
  private String white_vehicle_nos;
  private String install_date;
  private String status;
  private DeletedType deleted= DeletedType.FALSE;
  private Long shop_id;


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getWhite_vehicle_nos() {
    return white_vehicle_nos;
  }

  public void setWhite_vehicle_nos(String white_vehicle_nos) {
    this.white_vehicle_nos = white_vehicle_nos;
  }

  public String getInstall_date() {
    return install_date;
  }

  public void setInstall_date(String install_date) {
    this.install_date = install_date;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }

  public Long getShop_id() {
    return shop_id;
  }

  public void setShop_id(Long shop_id) {
    this.shop_id = shop_id;
  }

  public Long getCamera_id() {

    return camera_id;
  }

  public void setCamera_id(Long camera_id) {
    this.camera_id = camera_id;
  }
}
