package com.bcgogo.txn.dto.finance;

import com.bcgogo.enums.txn.finance.PaymentMethod;
import com.bcgogo.enums.txn.finance.PaymentStatus;
import com.bcgogo.utils.DateUtil;

/**
 * User: ZhangJuntao
 * Date: 13-3-25
 * Time: 下午3:15
 */
public class InstalmentPlanItemDTO {
  private Long id;
  private Long shopId;
  private Long instalmentPlanId;   //分期id
  private Double currentAmount;     //本期金额
  private Double payableAmount;     //应付金额
  private Double paidAmount;        //已付金额
  private Double proportion;       //所占比例
  private Integer periodNumber;     //第几期
  private Long endTime;             //截止日期
  private String endTimeStr;             //截止日期
  private PaymentStatus status;       //状态
  private String statusValue;       //状态
  private PaymentMethod paymentMethod;//支付方式
  private String memo;
  private Long nextItemId;
  private InstalmentPlanDTO instalmentPlanDTO;
  private InstalmentPlanItemDTO nextItem;
  private boolean expired = false;

  public boolean isExpired() {
    return expired;
  }

  public void setExpired(boolean expired) {
    this.expired = expired;
  }

  public String getEndTimeStr() {
    return endTimeStr;
  }

  public void setEndTimeStr(String endTimeStr) {
    this.endTimeStr = endTimeStr;
  }

  public String getStatusValue() {
    return statusValue;
  }

  public void setStatusValue(String statusValue) {
    this.statusValue = statusValue;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getInstalmentPlanId() {
    return instalmentPlanId;
  }

  public void setInstalmentPlanId(Long instalmentPlanId) {
    this.instalmentPlanId = instalmentPlanId;
  }

  public Double getCurrentAmount() {
    return currentAmount;
  }

  public void setCurrentAmount(Double currentAmount) {
    this.currentAmount = currentAmount;
  }

  public Double getPayableAmount() {
    return payableAmount;
  }

  public void setPayableAmount(Double payableAmount) {
    this.payableAmount = payableAmount;
  }

  public Double getPaidAmount() {
    return paidAmount;
  }

  public void setPaidAmount(Double paidAmount) {
    this.paidAmount = paidAmount;
  }

  public Double getProportion() {
    return proportion;
  }

  public void setProportion(Double proportion) {
    this.proportion = proportion;
  }

  public Integer getPeriodNumber() {
    return periodNumber;
  }

  public void setPeriodNumber(Integer periodNumber) {
    this.periodNumber = periodNumber;
  }

  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    if (endTime != null) {
      this.setEndTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DEFAULT, endTime));
      this.expired = endTime<System.currentTimeMillis();
    }
    this.endTime = endTime;
  }

  public PaymentStatus getStatus() {
    return status;
  }

  public void setStatus(PaymentStatus status) {
    if (status != null) {
      if (status == PaymentStatus.FULL_PAYMENT) {
        setStatusValue("已付");
      } else if (status == PaymentStatus.NON_PAYMENT) {
        setStatusValue("待付");
      } else {
        setStatusValue("已付" + this.getPaidAmount());
      }
    }
    this.status = status;
  }

  public PaymentMethod getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(PaymentMethod paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public Long getNextItemId() {
    return nextItemId;
  }

  public void setNextItemId(Long nextItemId) {
    this.nextItemId = nextItemId;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public InstalmentPlanItemDTO getNextItem() {
    return nextItem;
  }

  public void setNextItem(InstalmentPlanItemDTO nextItem) {
    this.nextItem = nextItem;
  }

  public InstalmentPlanDTO getInstalmentPlanDTO() {
    return instalmentPlanDTO;
  }

  public void setInstalmentPlanDTO(InstalmentPlanDTO instalmentPlanDTO) {
    this.instalmentPlanDTO = instalmentPlanDTO;
  }
}
