package com.bcgogo.user.dto;


/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/12
 * Time: 10:02.
 */
public class CouponDTO {

  private Long id;
  private long acouponId;   //代金券ID
  private String appUserNo;
  private Long createdTime;//发放时间
  private Long expireTime; //过期时间
  private Double balance;  //余额
  private String imei;      //绑定OBD的imei

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public long getAcouponId() {
    return acouponId;
  }

  public void setAcouponId(long acouponId) {
    this.acouponId = acouponId;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public Long getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  public Long getExpireTime() {
    return expireTime;
  }

  public void setExpireTime(Long expireTime) {
    this.expireTime = expireTime;
  }

  public Double getBalance() {
    return balance;
  }

  public void setBalance(Double balance) {
    this.balance = balance;
  }

  public String getImei() {
    return imei;
  }

  public void setImei(String imei) {
    this.imei = imei;
  }
}
