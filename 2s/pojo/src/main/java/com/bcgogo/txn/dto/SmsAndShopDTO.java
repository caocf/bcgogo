package com.bcgogo.txn.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: sl
 * Date: 12-2-5
 * Time: 上午10:15
 * To change this template use File | Settings | File Templates.
 */
public class SmsAndShopDTO implements Serializable {
  private  Long id;
  private  Long shopId;
  private String name;
  private String legalRep;
  private String mobile;
  private String address;
  private double smsBalance;
  private double rechargeAmount;

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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLegalRep() {
    return legalRep;
  }

  public void setLegalRep(String legalRep) {
    this.legalRep = legalRep;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public double getSmsBalance() {
    return smsBalance;
  }

  public void setSmsBalance(double smsBalance) {
    this.smsBalance = smsBalance;
  }

  public double getRechargeAmount() {
    return rechargeAmount;
  }

  public void setRechargeAmount(double rechargeAmount) {
    this.rechargeAmount = rechargeAmount;
  }
}
