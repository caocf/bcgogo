package com.bcgogo.search.dto;

import com.bcgogo.common.Sort;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-11-11
 * Time: 下午3:14
 * To change this template use File | Settings | File Templates.
 */
public class QuotedPreBuyOrderSearchConditionDTO {
  private Long shopId;
  private Long customerShopId;   //求购客户shopId
  private Long preBuyOrderId;
  private Long startTime;
  private Long endTime;
  private Long[] preBuyOrderIds;
  private Long[] preBuyOrderItemIds;
  private Long quotedPreBuyOrderId;

  private Sort sort;
  private Integer start=0;
  private Integer limit;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getCustomerShopId() {
    return customerShopId;
  }

  public void setCustomerShopId(Long customerShopId) {
    this.customerShopId = customerShopId;
  }

  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    this.endTime = endTime;
  }

  public Long[] getPreBuyOrderItemIds() {
    return preBuyOrderItemIds;
  }

  public void setPreBuyOrderItemIds(Long[] preBuyOrderItemIds) {
    this.preBuyOrderItemIds = preBuyOrderItemIds;
  }

  public Long[] getPreBuyOrderIds() {
    return preBuyOrderIds;
  }

  public void setPreBuyOrderIds(Long[] preBuyOrderIds) {
    this.preBuyOrderIds = preBuyOrderIds;
  }

  public Long getPreBuyOrderId() {
    return preBuyOrderId;
  }

  public void setPreBuyOrderId(Long preBuyOrderId) {
    this.preBuyOrderId = preBuyOrderId;
  }

  public Long getQuotedPreBuyOrderId() {
    return quotedPreBuyOrderId;
  }

  public void setQuotedPreBuyOrderId(Long quotedPreBuyOrderId) {
    this.quotedPreBuyOrderId = quotedPreBuyOrderId;
  }

  public Sort getSort() {
    return sort;
  }

  public void setSort(Sort sort) {
    this.sort = sort;
  }

  public Integer getStart() {
    return start;
  }

  public void setStart(Integer start) {
    this.start = start;
  }

  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }
}
