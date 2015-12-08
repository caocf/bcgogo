package com.bcgogo.txn.model.finance;

import com.bcgogo.enums.txn.finance.PaymentStatus;
import com.bcgogo.enums.txn.finance.PaymentMethod;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.finance.InstalmentPlanItemDTO;

import javax.persistence.*;

/**
 * User: ZhangJuntao
 * Date: 13-3-19
 * Time: 上午9:01
 * 分期付款详细
 */
@Entity
@Table(name = "instalment_plan_item")
public class InstalmentPlanItem extends LongIdentifier {
  private Long shopId;
  private Long instalmentPlanId;   //分期id
  private Double currentAmount;     //本期金额
  private Double payableAmount;     //应付金额
  private Double paidAmount;        //已付金额
  private Double proportion;       //所占比例
  private Integer periodNumber;     //第几期
  private Long endTime;             //截止日期
  private PaymentStatus status;       //状态
  private PaymentMethod paymentMethod;//支付方式
  private Long nextItemId;
  private String memo;

  public InstalmentPlanItemDTO toDTO() {
    InstalmentPlanItemDTO dto = new InstalmentPlanItemDTO();
    dto.setId(this.getId());
    dto.setShopId(this.getShopId());
    dto.setInstalmentPlanId(this.getInstalmentPlanId());
    dto.setCurrentAmount(this.getCurrentAmount());
    dto.setPayableAmount(this.getPayableAmount());
    dto.setPaidAmount(this.getPaidAmount());
    dto.setProportion(this.getProportion());
    dto.setEndTime(this.getEndTime());
    dto.setPeriodNumber(this.getPeriodNumber());
    dto.setPaymentMethod(this.getPaymentMethod());
    dto.setNextItemId(this.getNextItemId());
    dto.setMemo(this.getMemo());
    dto.setStatus(this.getStatus());
    return dto;
  }

  @Column(name = "memo")
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "instalment_plan_id")
  public Long getInstalmentPlanId() {
    return instalmentPlanId;
  }

  public void setInstalmentPlanId(Long instalmentPlanId) {
    this.instalmentPlanId = instalmentPlanId;
  }

  @Column(name = "current_amount")
  public Double getCurrentAmount() {
    return currentAmount;
  }

  public void setCurrentAmount(Double currentAmount) {
    this.currentAmount = currentAmount;
  }

  @Column(name = "proportion")
  public Double getProportion() {
    return proportion;
  }

  public void setProportion(Double proportion) {
    this.proportion = proportion;
  }

  @Column(name = "period_number")
  public Integer getPeriodNumber() {
    return periodNumber;
  }

  public void setPeriodNumber(Integer periodNumber) {
    this.periodNumber = periodNumber;
  }

  @Column(name = "payable_amount")
  public Double getPayableAmount() {
    return payableAmount;
  }

  public void setPayableAmount(Double payableAmount) {
    this.payableAmount = payableAmount;
  }

  @Column(name = "paid_amount")
  public Double getPaidAmount() {
    return paidAmount;
  }

  public void setPaidAmount(Double paidAmount) {
    this.paidAmount = paidAmount;
  }

  @Column(name = "end_time")
  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    this.endTime = endTime;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  public PaymentStatus getStatus() {
    return status;
  }

  public void setStatus(PaymentStatus status) {
    this.status = status;
  }

  @Column(name = "payment_method")
  @Enumerated(EnumType.STRING)
  public PaymentMethod getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(PaymentMethod paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  @Column(name = "next_item_id")
  public Long getNextItemId() {
    return nextItemId;
  }

  public void setNextItemId(Long nextItemId) {
    this.nextItemId = nextItemId;
  }

  @Override
  public String toString() {
    return "InstalmentPlanItem{" +
        "shopId=" + shopId +
        ", instalmentPlanId=" + instalmentPlanId +
        ", currentAmount=" + currentAmount +
        ", payableAmount=" + payableAmount +
        ", paidAmount=" + paidAmount +
        ", proportion=" + proportion +
        ", periodNumber=" + periodNumber +
        ", endTime=" + endTime +
        ", status=" + status +
        ", paymentMethod=" + paymentMethod +
        ", nextItemId=" + nextItemId +
        ", memo='" + memo + '\'' +
        '}';
  }
}
