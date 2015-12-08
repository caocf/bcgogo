package com.bcgogo.txn.dto.finance;

import com.bcgogo.enums.sms.StatType;
import com.bcgogo.enums.txn.finance.SmsCategory;

import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-3-27
 * Time: 下午8:41
 */
public class SmsRecordSearchCondition {
  private String shopName;
  private List<Long> shopIds;
  private int start;
  private int limit;
  private Long startTime;
  private Long endTime;
  private SmsCategory[] smsCategories;
  private int startPageNo = 1;
  private int maxRows = 10;
  private StatType statType;

  public StatType getStatType() {
    return statType;
  }

  public void setStatType(StatType statType) {
    this.statType = statType;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public List<Long> getShopIds() {
    return shopIds;
  }

  public void setShopIds(List<Long> shopIds) {
    this.shopIds = shopIds;
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

  public SmsCategory[] getSmsCategories() {
    return smsCategories;
  }

  public void setSmsCategories(SmsCategory[] smsCategories) {
    this.smsCategories = smsCategories;
  }

  public int getMaxRows() {
    return maxRows;
  }

  public void setMaxRows(int maxRows) {
    this.maxRows = maxRows;
  }

  public int getStartPageNo() {

    return startPageNo;
  }

  public void setStartPageNo(int startPageNo) {
    this.startPageNo = startPageNo;
  }
}
