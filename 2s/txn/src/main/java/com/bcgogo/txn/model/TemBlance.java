package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: sl
 * Date: 12-2-5
 * Time: 下午2:49
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "tem_blance")
@Deprecated
public class TemBlance extends LongIdentifier {
//  private  Long id;
  private  Long shopId;
  private String name;
  private String legalRep;
  private String mobile;
  private String address;
  private float smsBalance;
  private float rechargeAmount;
 @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  @Column(name = "legal_rep")
  public String getLegalRep() {
    return legalRep;
  }

  public void setLegalRep(String legalRep) {
    this.legalRep = legalRep;
  }
  @Column(name = "mobile")
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }
 @Column(name = "address")
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

   @Column(name = "sms_balance")
  public float getSmsBalance() {
    return smsBalance;
  }

  public void setSmsBalance(float smsBalance) {
    this.smsBalance = smsBalance;
  }
  @Column(name = "recharge_amount")
  public float getRechargeAmount() {
    return rechargeAmount;
  }

  public void setRechargeAmount(float rechargeAmount) {
    this.rechargeAmount = rechargeAmount;
  }
}
