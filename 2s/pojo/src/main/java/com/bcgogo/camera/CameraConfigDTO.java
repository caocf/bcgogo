package com.bcgogo.camera;

import java.io.Serializable;

public class CameraConfigDTO implements Serializable {
  private String id;
  private String interval_time_warn;
  private String white_vehicle_nos;
  private String order_type;
  private String camera_id;
  private String shop_id;
  private String member_card;
  private String serial_no;
  private String construction_project_value;
  private String construction_project_text;
  private String printer_serial_no;

  public String getConstruction_project_value() {
    return construction_project_value;
  }

  public void setConstruction_project_value(String construction_project_value) {
    this.construction_project_value = construction_project_value;
  }

  public String getConstruction_project_text() {
    return construction_project_text;
  }

  public void setConstruction_project_text(String construction_project_text) {
    this.construction_project_text = construction_project_text;
  }

  public String getSerial_no() {
    return serial_no;
  }

  public void setSerial_no(String serial_no) {
    this.serial_no = serial_no;
  }

  public String getInterval_time_warn() {
    return interval_time_warn;
  }

  public void setInterval_time_warn(String interval_time_warn) {
    this.interval_time_warn = interval_time_warn;
  }

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

  public String getOrder_type() {
    return order_type;
  }

  public void setOrder_type(String order_type) {
    this.order_type = order_type;
  }

  public String getCamera_id() {
    return camera_id;
  }

  public void setCamera_id(String camera_id) {
    this.camera_id = camera_id;
  }

  public String getShop_id() {
    return shop_id;
  }

  public void setShop_id(String shop_id) {
    this.shop_id = shop_id;
  }

  public String getMember_card() {
    return member_card;
  }

  public void setMember_card(String member_card) {
    this.member_card = member_card;
  }

  public String getPrinter_serial_no() {
    return printer_serial_no;
  }

  public void setPrinter_serial_no(String printer_serial_no) {
    this.printer_serial_no = printer_serial_no;
  }
}
