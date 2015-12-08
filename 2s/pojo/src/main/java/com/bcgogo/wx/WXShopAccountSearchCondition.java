package com.bcgogo.wx;

import com.bcgogo.common.Pager;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-12-4
 * Time: 上午10:24
 */
public class WXShopAccountSearchCondition {
  private Long shopId;
  private Long accountId;
  private WXAccountType accountType;
  private Pager pager;
  private int currentPage;
  private int pageSize;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getAccountId() {
    return accountId;
  }

  public void setAccountId(Long accountId) {
    this.accountId = accountId;
  }

  public WXAccountType getAccountType() {
    return accountType;
  }

  public void setAccountType(WXAccountType accountType) {
    this.accountType = accountType;
  }

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }

  public int getCurrentPage() {
    return currentPage;
  }

  public void setCurrentPage(int currentPage) {
    this.currentPage = currentPage;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }
}
