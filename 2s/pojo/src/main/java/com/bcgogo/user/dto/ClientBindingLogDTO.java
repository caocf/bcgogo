package com.bcgogo.user.dto;

/**
 * User: ZhangJuntao
 * Date: 13-6-21
 * Time: 下午5:35
 */
public class ClientBindingLogDTO {
  private Long id;
  private Long shopId;
  private String userNo;
  private String mac;//登录mac 地址
  private Long bindingTime;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getUserNo() {
    return userNo;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
  }

  public String getMac() {
    return mac;
  }

  public void setMac(String mac) {
    this.mac = mac;
  }

  public Long getBindingTime() {
    return bindingTime;
  }

  public void setBindingTime(Long bindingTime) {
    this.bindingTime = bindingTime;
  }
}
