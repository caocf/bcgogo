package com.bcgogo.txn.dto.secondary;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;

public class RepairOrderSecondaryCondition {
  private String startDateStr;
  private String endDateStr;
  private Long startDate;
  private Long endDate;
  private OrderSecondaryStatus[] status;
  private String[] statusStr;
  private String receipt;
  private Long customerId;
  private String customerInfo;

  private int maxRows = 15;
  private int startPageNo = 1;

  public String getStartDateStr() {
    return startDateStr;
  }

  public void setStartDateStr(String startDateStr) {
    if (StringUtils.isNotEmpty(startDateStr)) {
      try {
        Long startDate = DateUtil.convertDateStringToDateLong("yyyy-MM-dd hh:mm", startDateStr + " 00:00");
        setStartDate(startDate);
      } catch (ParseException e) {
        throw new RuntimeException("时间解析异常！");
      }
    }
    this.startDateStr = startDateStr;
  }

  public String getEndDateStr() {
    return endDateStr;
  }

  public void setEndDateStr(String endDateStr) {
    if (StringUtils.isNotEmpty(endDateStr)) {
      try {
        Long endDate = DateUtil.convertDateStringToDateLong("yyyy-MM-dd hh:mm", endDateStr + " 23:59");
        setEndDate(endDate);
      } catch (ParseException e) {
        throw new RuntimeException("时间解析异常！");
      }
    }
    this.endDateStr = endDateStr;
  }

  public OrderSecondaryStatus[] getStatus() {
    return status;
  }

  public void setStatus(OrderSecondaryStatus[] status) {
    this.status = status;
  }

  public String[] getStatusStr() {
    return statusStr;
  }

  public void setStatusStr(String[] statusStr) {
    this.statusStr = statusStr;
    if (statusStr != null) {
      status = new OrderSecondaryStatus[statusStr.length];
      for (int i = 0; i < statusStr.length; i++) {
        status[i] = OrderSecondaryStatus.valueOf(statusStr[i]);
      }
    }
  }

  public String getReceipt() {
    return receipt;
  }

  public void setReceipt(String receipt) {
    this.receipt = receipt;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
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

  public String getCustomerInfo() {
    return customerInfo;
  }

  public void setCustomerInfo(String customerInfo) {
    this.customerInfo = customerInfo;
  }
}
