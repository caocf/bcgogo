package com.bcgogo.remind.dto;

import com.bcgogo.utils.DateUtil;

import java.text.ParseException;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 12-11-30
 * Time: 下午1:56
 * To change this template use File | Settings | File Templates.
 */
public class MessageSearchConditionDTO {
  private Long shopId;
  private Long shopVersionId;
  private String messageType;
  private Long startDate;
  private Long endDate;
  private String startDateStr;
  private String endDateStr;
  private Integer startPageNo;
  private Integer maxRows;
  private String sort;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getShopVersionId() {
    return shopVersionId;
  }

  public void setShopVersionId(Long shopVersionId) {
    this.shopVersionId = shopVersionId;
  }

  public Integer getStartPageNo() {
    return startPageNo;
  }

  public void setStartPageNo(Integer startPageNo) {
    this.startPageNo = startPageNo;
  }

  public Integer getMaxRows() {
    return maxRows;
  }

  public void setMaxRows(Integer maxRows) {
    this.maxRows = maxRows;
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }

  public String getMessageType() {
    return messageType;
  }

  public void setMessageType(String messageType) {
    this.messageType = messageType;
  }

  public Long getStartDate() {
    return startDate;
  }

  public void setStartDate(Long startDate) {
    this.startDate = startDate;
  }

  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    this.endDate = endDate;
  }

  public String getStartDateStr() {
    return startDateStr;
  }

  public void setStartDateStr(String startDateStr) {
    this.startDateStr = startDateStr;
    try {
      this.startDate = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, startDateStr);
    } catch (ParseException e) {
    }
  }

  public String getEndDateStr() {
    return endDateStr;
  }

  public void setEndDateStr(String endDateStr) {
    this.endDateStr = endDateStr;
    try {
      this.endDate = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, endDateStr);
      if (endDate != null) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(endDate);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        endDate = cal.getTimeInMillis();
      }
    } catch (ParseException e) {
    }
  }
}
