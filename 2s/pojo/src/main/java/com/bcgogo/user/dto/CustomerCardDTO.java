package com.bcgogo.user.dto;

import com.bcgogo.utils.DateUtil;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-12-6
 * Time: 下午9:18
 * To change this template use File | Settings | File Templates.
 */
public class CustomerCardDTO implements Serializable {
  public CustomerCardDTO() {
  }

  private Long id;
  private Long shopId;
  private long creationDate;
  private long lastModified;
  private Long customerId;
  private Long cardType;
  private Long washRemain;
  private Long state;
  private String lastModifiedStr;

  public String getLastModifiedStr() {
    return lastModifiedStr;
  }

  public void setLastModifiedStr(String lastModifiedStr) {
    this.lastModifiedStr = lastModifiedStr;
  }

  public Long getId() {
    return this.id;
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

  public long getCreationDate() {
    return this.creationDate;
  }

  public void setCreationDate(Long creationDate) {
    this.creationDate = creationDate;
  }

  public Long getLastModified() {
    return this.lastModified;
  }

  public void setLastModified(Long lastModified) {
    this.lastModified = lastModified;
    this.lastModifiedStr=DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DEFAULT,lastModified);
  }


  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public Long getCardType() {
    return cardType;
  }

  public void setCardType(Long cardType) {
    this.cardType = cardType;
  }

  public Long getWashRemain() {
    return washRemain;
  }

  public void setWashRemain(Long washRemain) {
    this.washRemain = washRemain;
  }

  public Long getState() {
    return state;
  }

  public void setState(Long state) {
    this.state = state;
  }
}
