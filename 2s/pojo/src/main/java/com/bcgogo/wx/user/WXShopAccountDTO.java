package com.bcgogo.wx.user;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.utils.DateUtil;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-12-4
 * Time: 下午1:28
 */
public class WXShopAccountDTO {
  private Long id;
  private String idStr;
  private Long shopId;
  private String shopName;
  private Long accountId;
  private String accountName;
  private Double balance;
  private Long expireDate;
  private String expireDateStr;
  private String remark;
  private DeletedType deleted=DeletedType.FALSE;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    setIdStr(String.valueOf(id));
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public Long getAccountId() {
    return accountId;
  }

  public void setAccountId(Long accountId) {
    this.accountId = accountId;
  }

  public String getAccountName() {
    return accountName;
  }

  public void setAccountName(String accountName) {
    this.accountName = accountName;
  }

  public Double getBalance() {
    return balance;
  }

  public void setBalance(Double balance) {
    this.balance = balance;
  }

  public Long getExpireDate() {
    return expireDate;
  }

  public void setExpireDate(Long expireDate) {
    this.expireDate = expireDate;
    this.setExpireDateStr(DateUtil.convertDateLongToString(expireDate,DateUtil.DEFAULT));
  }

  public String getExpireDateStr() {
    return expireDateStr;
  }

  public void setExpireDateStr(String expireDateStr) {
    this.expireDateStr = expireDateStr;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
