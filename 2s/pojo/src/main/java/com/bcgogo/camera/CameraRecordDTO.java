package com.bcgogo.camera;

import java.io.Serializable;

public class CameraRecordDTO implements Serializable {
  private String id;
  private String camera_id;
  private String vehicle_no;
  private String arrive_date;
  private String ref_order_type;
  private String name;
  private Long order_id;
  private String shop_id;
  private String order_idStr;

  public Long getOrder_id() {
    return order_id;
  }

  public void setOrder_id(Long order_id) {
    this.order_id = order_id;
    if(order_id != null){
      setOrder_idStr(order_id.toString());
    }else {
      setOrder_idStr("");
    }
  }

  public String getOrder_idStr() {
    return order_idStr;
  }

  public void setOrder_idStr(String order_idStr) {
    this.order_idStr = order_idStr;
  }


  public String getShop_id() {
    return shop_id;
  }

  public void setShop_id(String shop_id) {
    this.shop_id = shop_id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }



  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCamera_id() {
    return camera_id;
  }

  public void setCamera_id(String camera_id) {
    this.camera_id = camera_id;
  }

  public String getVehicle_no() {
    return vehicle_no;
  }

  public void setVehicle_no(String vehicle_no) {
    this.vehicle_no = vehicle_no;
  }

  public String getArrive_date() {
    return arrive_date;
  }

  public void setArrive_date(String arrive_date) {
    this.arrive_date = arrive_date;
  }

  public String getRef_order_type() {
    return ref_order_type;
  }

  public void setRef_order_type(String ref_order_type) {
    this.ref_order_type = ref_order_type;
  }

}
