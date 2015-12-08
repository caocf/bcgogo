package com.bcgogo.api;

import java.io.Serializable;

/**
 * 碰撞数据详情
 * Created with IntelliJ IDEA.
 * User: zj
 * Date: 15-7-3
 * Time: 上午11:10
 */
public class ImpactDetailDTO implements Serializable {
  private Long id;
  private String idStr;
  private String uuid;
  private String uploadTime;
  private String lon;    //经度
  private String lat;    //纬度
  private String rdtc;  //故障代码 例如：U0021,B0090,C0032,P0006,P0007,P1233,P0008,P0009,U0022
  private String rpm;  //发动机转速                2000
  private String vss;  //车辆速度                  90km/h
  private String address;

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getUploadTime() {
    return uploadTime;
  }

  public void setUploadTime(String uploadTime) {
    this.uploadTime = uploadTime;
  }

  public String getLon() {
    return lon;
  }

  public void setLon(String lon) {
    this.lon = lon;
  }

  public String getLat() {
    return lat;
  }

  public void setLat(String lat) {
    this.lat = lat;
  }

  public String getRdtc() {
    return rdtc;
  }

  public void setRdtc(String rdtc) {
    this.rdtc = rdtc;
  }

  public String getRpm() {
    return rpm;
  }

  public void setRpm(String rpm) {
    this.rpm = rpm;
  }

  public String getVss() {
    return vss;
  }

  public void setVss(String vss) {
    this.vss = vss;
  }
}
