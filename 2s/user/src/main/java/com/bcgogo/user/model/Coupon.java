package com.bcgogo.user.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.CouponDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-11-2
 * Time: 上午11:44
 */
@Entity
@Table(name = "coupon")
public class Coupon extends LongIdentifier {

  private String appUserNo;
  private Long createdTime;               //发放时间
  private Long expireTime;                //过期时间
  private Double balance;                 //余额
  private Long recommendPhone;            //推荐人手机号
  private Integer isShared;                   //是否是第一次分享
  private String imei;                      //绑定OBD的imei

  public Coupon fromDTO(CouponDTO couponDTO) {
    this.setId(couponDTO.getId());
    this.setAppUserNo(couponDTO.getAppUserNo());
    this.setCreatedTime(couponDTO.getCreatedTime());
    this.setExpireTime(couponDTO.getExpireTime());
    this.setBalance(couponDTO.getBalance());
    this.setImei(couponDTO.getImei());
    return this;
  }

  public CouponDTO toDTO(){
    CouponDTO couponDTO=new CouponDTO();
    couponDTO.setId(this.getId());
    couponDTO.setAppUserNo(this.getAppUserNo());
    couponDTO.setCreatedTime(this.getCreatedTime());
    couponDTO.setExpireTime(this.getExpireTime());
    couponDTO.setBalance(this.getBalance());
    couponDTO.setImei(this.getImei());
    return  couponDTO;
  }

  @Column(name = "app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  @Column(name = "created_time")
  public Long getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @Column(name = "expire_time")
  public Long getExpireTime() {
    return expireTime;
  }

  public void setExpireTime(Long expireTime) {
    this.expireTime = expireTime;
  }

  @Column(name = "balance")
  public Double getBalance() {
    return balance;
  }

  public void setBalance(Double balance) {
    this.balance = balance;
  }

  @Column(name = "recommend_phone")
  public Long getRecommendPhone() {
    return recommendPhone;
  }

  public void setRecommendPhone(Long recommendPhone) {
    this.recommendPhone = recommendPhone;
  }

  @Column(name = "is_shared")
  public Integer getIsShared() {
    return isShared;
  }

  public void setIsShared(Integer isShared) {
    this.isShared = isShared;
  }

  @Column(name = "imei")
  public String getImei() {
    return imei;
  }

  public void setImei(String imei) {
    this.imei = imei;
  }
}
