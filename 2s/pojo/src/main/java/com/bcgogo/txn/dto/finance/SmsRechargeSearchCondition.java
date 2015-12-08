package com.bcgogo.txn.dto.finance;

import com.bcgogo.enums.RechargeMethod;
import com.bcgogo.enums.txn.finance.BcgogoReceivableStatus;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-12-26
 * Time: 上午11:07
 * To change this template use File | Settings | File Templates.
 */
public class SmsRechargeSearchCondition {
  private String shopName;
  private int start;
  private int limit;
  private Long startTime;
  private Long endTime;
  private String startTimeStr;
  private String endTimeStr;
  private String receiptNo;
  private List<Long> shopIds;
  private BcgogoReceivableStatus[] statuses;
  private RechargeMethod[] rechargeMethods;
  private String groupField;

  public String getGroupField() {
    return groupField;
  }

  public void setGroupField(String groupField) {
    this.groupField = groupField;
  }

  public RechargeMethod[] getRechargeMethods() {
    return rechargeMethods;
  }

  public void setRechargeMethods(RechargeMethod[] rechargeMethods) {
    this.rechargeMethods = rechargeMethods;
  }

  public BcgogoReceivableStatus[] getStatuses() {

    return statuses;
  }

  public void setStatuses(BcgogoReceivableStatus[] statuses) {
    this.statuses = statuses;
  }

  public List<Long> getShopIds() {
    return shopIds;
  }

  public void setShopIds(List<Long> shopIds) {
    this.shopIds = shopIds;
  }

  public String getEndTimeStr() {
    return endTimeStr;
  }

  public void setEndTimeStr(String endTimeStr) {
    this.endTimeStr = endTimeStr;
    if (StringUtils.isNotBlank(endTimeStr)){
      try {
        endTime = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, endTimeStr)+DateUtil.DAY_MILLION_SECONDS-1;
      } catch (ParseException e) {
      }
    }else{
      endTime = null;
    }
  }

  public String getStartTimeStr() {

    return startTimeStr;
  }

  public void setStartTimeStr(String startTimeStr) {
    this.startTimeStr = startTimeStr;
    if (StringUtils.isNotBlank(startTimeStr)){
      try {
        startTime = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, startTimeStr);
      } catch (ParseException e) {
      }
    }else{
      startTime = null;
    }
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public Long getEndTime() {

    return endTime;
  }

  public void setEndTime(Long endTime) {
    this.endTime = endTime;
  }

  public Long getStartTime() {

    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  public int getLimit() {

    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public int getStart() {

    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public String getShopName() {

    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

}
