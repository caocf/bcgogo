package com.bcgogo.user.model;

import com.bcgogo.etl.ImpactDTO;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 碰撞数据
 * Author: ndong
 * Date: 2015-4-21
 * Time: 18:43
 */
@Entity
@Table(name = "impact")
public class Impact extends LongIdentifier {
  private String uuid;
  private Long shopId;
  private String appUserNo;//app用户账号
  private Long uploadTime;
  private Long uploadServerTime;
  private String lon;
  private String lat;
  private String addr;
  private String addrShort;  //地址简称
  private int type;//碰撞类型 0 普通碰撞 1停车监控检测到的碰撞 2图片


  public void fromDTO(ImpactDTO impactDTO) {
    if (impactDTO == null) {
      return;
    }
    this.setUuid(impactDTO.getUuid());
    this.setShopId(impactDTO.getShopId());
    this.setAppUserNo(impactDTO.getAppUserNo());
    this.setUploadTime(impactDTO.getUploadTime());
    this.setUploadServerTime(impactDTO.getUploadServerTime());
    this.setLon(impactDTO.getLon());
    this.setLat(impactDTO.getLat());
    this.setAddr(impactDTO.getAddr());
    this.setAddrShort(impactDTO.getAddrShort());
    this.setType(impactDTO.getType());
  }

  public ImpactDTO toDTO() {
    ImpactDTO impactDTO = new ImpactDTO();
    impactDTO.setUuid(this.getUuid());
    impactDTO.setShopId(this.getShopId());
    impactDTO.setAppUserNo(this.getAppUserNo());
    impactDTO.setUploadTime(this.getUploadTime());
    impactDTO.setUploadServerTime(this.getUploadServerTime());
    impactDTO.setLon(this.getLon());
    impactDTO.setLat(this.getLat());
    impactDTO.setAddr(this.getAddr());
    impactDTO.setAddrShort(this.getAddrShort());
    return impactDTO;
  }


  @Column(name = "uuid")
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

   @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
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


  @Column(name = "lon")
  public String getLon() {
    return lon;
  }

  public void setLon(String lon) {
    this.lon = lon;
  }

  @Column(name = "lat")
  public String getLat() {
    return lat;
  }

  public void setLat(String lat) {
    this.lat = lat;
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

    @Column(name = "type")
  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  //
//    @Enumerated(EnumType.STRING)
//    @Column(name = "deleted")
//  public DeletedType getDeleted() {
//    return deleted;
//  }
//
//  public void setDeleted(DeletedType deleted) {
//    this.deleted = deleted;
//  }
}
