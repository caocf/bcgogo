package com.bcgogo.txn.dto.pushMessage;

import com.bcgogo.enums.txn.pushMessage.PushMessageType;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-7-1
 * Time: 上午10:34
 */
public class TalkMessageCondition {
  private String appUserNo;
  private Long shopId;
  private Long receiverId;
  private String type;
  private int start;
  private int limit;
  private PushMessageType[] types;

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getReceiverId() {
    return receiverId;
  }

  public void setReceiverId(Long receiverId) {
    this.receiverId = receiverId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public PushMessageType[] getTypes() {
    return types;
  }

  public void setTypes(PushMessageType[] types) {
    this.types = types;
  }

}
