package com.bcgogo.stat.dto;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-12-7
 * Time: 下午3:22
 * To change this template use File | Settings | File Templates.
 */
public class PayablePrintDTO {
  private String receiptNo;
  private String payTimeStr;
  private String supplierName;
  private String orderType;
  private Double amount;
  private Double paidAmount;
  private Double deduction;
  private Double creditAmount;
  private Double totalCostPrice;

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public String getPayTimeStr() {
    return payTimeStr;
  }

  public void setPayTimeStr(String payTimeStr) {
    this.payTimeStr = payTimeStr;
  }

  public String getSupplierName() {
    return supplierName;
  }

  public void setSupplierName(String supplierName) {
    this.supplierName = supplierName;
  }

  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public Double getPaidAmount() {
    return paidAmount;
  }

  public void setPaidAmount(Double paidAmount) {
    this.paidAmount = paidAmount;
  }

  public Double getDeduction() {
    return deduction;
  }

  public void setDeduction(Double deduction) {
    this.deduction = deduction;
  }

  public Double getCreditAmount() {
    return creditAmount;
  }

  public void setCreditAmount(Double creditAmount) {
    this.creditAmount = creditAmount;
  }

  public Double getTotalCostPrice() {
    return totalCostPrice;
  }

  public void setTotalCostPrice(Double totalCostPrice) {
    this.totalCostPrice = totalCostPrice;
  }
}
