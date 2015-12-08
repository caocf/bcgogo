package com.bcgogo.txn.dto;

/**
 * Created by IntelliJ IDEA.
 * User: WWW
 * Date: 12-8-25
 * Time: 下午4:50
 * To change this template use File | Settings | File Templates.
 */
public class SupplierReturnPayableDTO {
  //        <!-- 店面ID-->、
  private Long shopId;
//            <!-- 现金-->
    private Double cash;
//            <!-- 定金-->
  private Double deposit;
//            <!-- 总额-->
  private Double total;
//            <!-- 退货单ID-->
  private Long purchaseReturnId ;
//            <!-- 供应商ID-->
  private Long supplierId;
//            <!-- 材料品名-->
  private String  materialName;
  //冲账
  private Double strikeAmount;

  private Long payeeId;
  private String payee;

  private Double accountDiscount;   //优惠
  private Double accountDebtAmount;//欠款
  private Double settledAmount;//实收
  private Long returnDate;//退货时间

  private String receiptNo;

  private Long vestDate;

    //银联
  private Double bankAmount;

    //支票
  private Double bankCheckAmount;
    //支票号
  private String bankCheckNo;
  public Long getReturnDate() {
    return returnDate;
  }

  public void setReturnDate(Long returnDate) {
    this.returnDate = returnDate;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Double getCash() {
    return cash;
  }

  public void setCash(Double cash) {
    this.cash = cash;
  }

  public Double getDeposit() {
    return deposit;
  }

  public void setDeposit(Double deposit) {
    this.deposit = deposit;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Long getPurchaseReturnId() {
    return purchaseReturnId;
  }

  public void setPurchaseReturnId(Long purchaseReturnId) {
    this.purchaseReturnId = purchaseReturnId;
  }

  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  public String getMaterialName() {
    return materialName;
  }

  public void setMaterialName(String materialName) {
    this.materialName = materialName;
  }

  public Double getStrikeAmount() {
    return strikeAmount;
  }

  public void setStrikeAmount(Double strikeAmount) {
    this.strikeAmount = strikeAmount;
  }

  public Long getPayeeId() {
    return payeeId;
  }

  public void setPayeeId(Long payeeId) {
    this.payeeId = payeeId;
  }

  public String getPayee() {
    return payee;
  }

  public void setPayee(String payee) {
    this.payee = payee;
  }

  public Double getAccountDiscount() {
    return accountDiscount;
  }

  public void setAccountDiscount(Double accountDiscount) {
    this.accountDiscount = accountDiscount;
  }

  public Double getAccountDebtAmount() {
    return accountDebtAmount;
  }

  public void setAccountDebtAmount(Double accountDebtAmount) {
    this.accountDebtAmount = accountDebtAmount;
  }

  public Double getSettledAmount() {
    return settledAmount;
  }

  public void setSettledAmount(Double settledAmount) {
    this.settledAmount = settledAmount;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }

  public Double getBankAmount() {
    return bankAmount;
  }

  public void setBankAmount(Double bankAmount) {
    this.bankAmount = bankAmount;
  }

  public Double getBankCheckAmount() {
    return bankCheckAmount;
  }

  public void setBankCheckAmount(Double bankCheckAmount) {
    this.bankCheckAmount = bankCheckAmount;
  }

  public String getBankCheckNo() {
    return bankCheckNo;
  }

  public void setBankCheckNo(String bankCheckNo) {
    this.bankCheckNo = bankCheckNo;
  }
}
