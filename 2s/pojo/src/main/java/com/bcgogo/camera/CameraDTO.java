package com.bcgogo.camera;

import java.io.Serializable;

public class CameraDTO implements Serializable {
  //摄像头信息
  private String id;
  private String serial_no;
  private String last_heart_date;
  private String lan_ip;
  private String lan_port;
  private String username;
  private String password;
  private String domain_name;
  private String domain_username;
  private String domain_password;
  private String status;
  private String remark;
  private String external_address;
  //店铺信息
  private String name;
  //关联信息
  private String white_vehicle_nos;
  private String install_date;
  private String camera_shop_id;
  private String ids;

  public String getIds() {
    return ids;
  }

  public void setIds(String ids) {
    this.ids = ids;
  }

  public String getCamera_shop_id() {
    return camera_shop_id;
  }

  public void setCamera_shop_id(String camera_shop_id) {
    this.camera_shop_id = camera_shop_id;
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

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSerial_no() {
    return serial_no;
  }

  public void setSerial_no(String serial_no) {
    this.serial_no = serial_no;
  }

  public String getLast_heart_date() {
    return last_heart_date;
  }

  public void setLast_heart_date(String last_heart_date) {
    this.last_heart_date = last_heart_date;
  }

  public String getLan_ip() {
    return lan_ip;
  }

  public void setLan_ip(String lan_ip) {
    this.lan_ip = lan_ip;
  }

  public String getLan_port() {
    return lan_port;
  }

  public void setLan_port(String lan_port) {
    this.lan_port = lan_port;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getDomain_username() {
    return domain_username;
  }

  public void setDomain_username(String domain_username) {
    this.domain_username = domain_username;
  }

  public String getDomain_password() {
    return domain_password;
  }

  public void setDomain_password(String domain_password) {
    this.domain_password = domain_password;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public String getExternal_address() {
    return external_address;
  }

  public void setExternal_address(String external_address) {
    this.external_address = external_address;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDomain_name() {
    return domain_name;
  }

  public void setDomain_name(String domain_name) {
    this.domain_name = domain_name;
  }


}
