package com.bcgogo.etl;

import com.bcgogo.api.GsmTBoxDataDTO;
import com.bcgogo.api.GsmVehicleDataDTO;
import com.bcgogo.enums.DeletedType;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-4-17
 * Time: 17:54
 */
public class ImpactDTO {
  private Long id;
  private String uuid;
   private Long shopId;
  private String appUserNo;//app用户账号
  private Long uploadTime;
  private Long uploadServerTime;
  private String lon;
  private String lat;
  private String addr;
  private String addrShort;  //地址简称
  private GsmVehicleDataDTO[] data;
  private GsmTBoxDataDTO[] dataTBox; //2s的车况
  private int type;//碰撞类型 0 普通碰撞 1停车监控检测到的碰撞

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public Long getUploadTime() {
    return uploadTime;
  }

  public void setUploadTime(Long uploadTime) {
    this.uploadTime = uploadTime;
  }

  public Long getUploadServerTime() {
    return uploadServerTime;
  }

  public void setUploadServerTime(Long uploadServerTime) {
    this.uploadServerTime = uploadServerTime;
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

  public String getAddr() {
    return addr;
  }

  public void setAddr(String addr) {
    this.addr = addr;
  }

  public String getAddrShort() {
    return addrShort;
  }

  public void setAddrShort(String addrShort) {
    this.addrShort = addrShort;
  }

  public GsmVehicleDataDTO[] getData() {
    return data;
  }

  public void setData(GsmVehicleDataDTO[] data) {
    this.data = data;
  }

  public GsmTBoxDataDTO[] getDataTBox() {
    return dataTBox;
  }

  public void setDataTBox(GsmTBoxDataDTO[] dataTBox) {
    this.dataTBox = dataTBox;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }
}
