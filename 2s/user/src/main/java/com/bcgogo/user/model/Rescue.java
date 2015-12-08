package com.bcgogo.user.model;

import com.bcgogo.api.RescueDTO;
import com.bcgogo.enums.user.userGuide.SosStatus;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * 后视镜车主sos求救记录
 * Author: ndong
 * Date: 15-6-16
 * Time: 下午2:02
 */
@Entity
@Table(name = "rescue")
public class Rescue extends LongIdentifier {
  private String appUserNo;
  private Long shopId;
  private String lat;
  private String lon;
  private Long uploadTime;
  private Long uploadServerTime;
  private String addr;
  private String addrShort;  //地址简称
  private SosStatus sosStatus;



  public void fromDTO(RescueDTO rescueDTO) {
    this.setId(rescueDTO.getId());
    this.setAppUserNo(rescueDTO.getAppUserNo());
    this.setAddr(rescueDTO.getAddr());
    this.setAddrShort(rescueDTO.getAddrShort());
    this.setLat(rescueDTO.getLat());
    this.setLon(rescueDTO.getLon());
    this.setShopId(rescueDTO.getShopId());
    this.setUploadTime(rescueDTO.getUploadTime());
    this.setUploadServerTime(rescueDTO.getUploadServerTime());
    this.setSosStatus(rescueDTO.getSosStatus());
  }

  @Column(name = "app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "lat")
  public String getLat() {
    return lat;
  }

  public void setLat(String lat) {
    this.lat = lat;
  }

  @Column(name = "lon")
  public String getLon() {
    return lon;
  }

  public void setLon(String lon) {
    this.lon = lon;
  }

  @Column(name = "addr")
  public String getAddr() {
    return addr;
  }

  public void setAddr(String addr) {
    this.addr = addr;
  }

  @Column(name = "addr_short")
  public String getAddrShort() {
    return addrShort;
  }

  public void setAddrShort(String addrShort) {
    this.addrShort = addrShort;
  }

  @Column(name = "upload_time")
  public Long getUploadTime() {
    return uploadTime;
  }

  public void setUploadTime(Long uploadTime) {
    this.uploadTime = uploadTime;
  }

  @Column(name = "upload_server_time")
  public Long getUploadServerTime() {
    return uploadServerTime;
  }

  public void setUploadServerTime(Long uploadServerTime) {
    this.uploadServerTime = uploadServerTime;
  }

  @Column(name = "sos_status")
  @Enumerated(EnumType.STRING)
  public SosStatus getSosStatus() {
    return sosStatus;
  }

  public void setSosStatus(SosStatus sosStatus) {
    this.sosStatus = sosStatus;
  }
}
