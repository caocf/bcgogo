package com.bcgogo.txn.dto.finance;

import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-3-26
 * Time: 下午3:23
 */
public class AccountSearchCondition {
  private String shopName;
  private Boolean havePayable;
  private List<Long> shopIds;
  private int start;
  private int limit;

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

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public Boolean getHavePayable() {
    return havePayable;
  }

  public void setHavePayable(Boolean havePayable) {
    this.havePayable = havePayable;
  }

  public List<Long> getShopIds() {
    return shopIds;
  }

  public void setShopIds(List<Long> shopIds) {
    this.shopIds = shopIds;
  }
}
