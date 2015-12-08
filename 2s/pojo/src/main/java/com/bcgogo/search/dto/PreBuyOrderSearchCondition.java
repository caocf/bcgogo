package com.bcgogo.search.dto;

import com.bcgogo.common.Sort;
import com.bcgogo.enums.txn.preBuyOrder.BusinessChanceType;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-10-22
 * Time: 上午9:15
 * To change this template use File | Settings | File Templates.
 */
public class PreBuyOrderSearchCondition {
  private Long shopId;
  private Long[] preBuyOrderItemIds;
  private Long[] productIds;
  private Long startTime;
  private Long endTime;
  private Integer pageSize;
  private Integer start = 0;
  private Integer startPageNo = 1;
  private Long nonePreBuyOrderId;    //需要排除的id
  private Long noneShopId;    //需要排除的买家shopId
  private boolean isValid;
  private BusinessChanceType businessChanceType;
  private Sort sort;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long[] getPreBuyOrderItemIds() {
    return preBuyOrderItemIds;
  }

  public void setPreBuyOrderItemIds(Long[] preBuyOrderItemIds) {
    this.preBuyOrderItemIds = preBuyOrderItemIds;
  }

  public Long[] getProductIds() {
    return productIds;
  }

  public void setProductIds(Long[] productIds) {
    this.productIds = productIds;
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

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public Integer getStart() {
    return start;
  }

  public void setStart(Integer start) {
    this.start = start;
  }

  public Long getNonePreBuyOrderId() {
    return nonePreBuyOrderId;
  }

  public void setNonePreBuyOrderId(Long nonePreBuyOrderId) {
    this.nonePreBuyOrderId = nonePreBuyOrderId;
  }

  public Long getNoneShopId() {
    return noneShopId;
  }

  public void setNoneShopId(Long noneShopId) {
    this.noneShopId = noneShopId;
  }

  public boolean isValid() {
    return isValid;
  }

  public void setValid(boolean valid) {
    isValid = valid;
  }

  public Sort getSort() {
    return sort;
  }

  public void setSort(Sort sort) {
    this.sort = sort;
  }

  public BusinessChanceType getBusinessChanceType() {
    return businessChanceType;
  }

  public void setBusinessChanceType(BusinessChanceType businessChanceType) {
    this.businessChanceType = businessChanceType;
  }

  public Integer getStartPageNo() {
    return startPageNo;
  }

  public void setStartPageNo(Integer startPageNo) {
    this.startPageNo = startPageNo;
  }
}
