package com.bcgogo.txn.dto;

import com.bcgogo.enums.DayType;
import com.bcgogo.enums.PayStatus;
import com.bcgogo.enums.PaymentTypes;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: zhangchuanlong
 * Date: 12-8-16
 * Time: 下午5:29
 * 付款历史记录DTO
 */
public class PayableHistoryRecordDTO {
  private Long id;
  /*店面ID*/
  private Long shopId;
  /*扣款*/
  private Double deduction;
  /*欠款挂账*/
  private Double creditAmount;
  /* 现金*/
  private Double cash;
  /*  银行卡*/
  private Double bankCardAmount;
  /*支票*/
  private Double checkAmount;
  /*支票号码*/
  private String  checkNo;
  /*用定金*/
  private Double depositAmount;
  /*  实付*/
  private Double actuallyPaid;
  /*采购入库单ID*/
  private Long purchaseInventoryId;
  /*采购单入库单id字符串*/
  private String purchaseInventoryIdStr;
  //单据编号
  private String receiptNo;
  /*结算历史ID*/
  private Long payableHistoryId;
  /*供应商ID*/
  private Long supplierId;
  /*应付款ID*/
  private Long payableId;
   /*材料品名*/
  private String materialName;
  /*金额*/
  private Double amount;
  /*已付金额*/
  private Double paidAmount;
  /*付款时间*/
  private  Long paidTime;
  /*付款时间String*/
  private String paidTimeStr;
  /*状态*/
  private PayStatus status;

  /*付款类型*/
  private PaymentTypes paymentType;

  //流水统计扩展字段,流水统计页面下方列表:如果作废 非当天单据 流水统计下方列表要显示
  private DayType dayType;

  private Double statementAmount;

  private boolean statementAccountFlag;//是否是对账单付款

  public boolean getStatementAccountFlag() {
    return statementAccountFlag;
  }

  public void setStatementAccountFlag(boolean statementAccountFlag) {
    this.statementAccountFlag = statementAccountFlag;
  }

  public Double getStatementAmount() {
    return statementAmount;
  }

  public void setStatementAmount(Double statementAmount) {
    this.statementAmount = statementAmount;
  }

  public DayType getDayType() {
    return dayType;
  }

  public void setDayType(DayType dayType) {
    this.dayType = dayType;
  }

  //流水统计扩展字段
  private String payDateStr;
  private String customerName;
  private String orderType;
  private String shortMaterialName;

  //冲账
  private Double strikeAmount;

  private Long purchaseReturnId;

  private String payer;   //收款人
  private Long payerId;
  
  public PaymentTypes getPaymentType() {
    return paymentType;
  }

  public void setPaymentType(PaymentTypes paymentType) {
    this.paymentType = paymentType;
  }

  public String getShortMaterialName() {
    return shortMaterialName;
  }

  public void setShortMaterialName(String shortMaterialName) {
    this.shortMaterialName = shortMaterialName;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getPayDateStr() {
    return payDateStr;
  }

  public void setPayDateStr(String payDateStr) {
    this.payDateStr = payDateStr;
  }

  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
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

  public Double getCash() {
    return cash;
  }

  public void setCash(Double cash) {
    this.cash = cash;
  }

  public Double getBankCardAmount() {
    return bankCardAmount;
  }

  public void setBankCardAmount(Double bankCardAmount) {
    this.bankCardAmount = bankCardAmount;
  }

  public Double getCheckAmount() {
    return checkAmount;
  }

  public void setCheckAmount(Double checkAmount) {
    this.checkAmount = checkAmount;
  }

  public String getCheckNo() {
    return checkNo;
  }

  public void setCheckNo(String  checkNo) {
    this.checkNo = checkNo;
  }

  public Double getDepositAmount() {
    return depositAmount;
  }

  public void setDepositAmount(Double depositAmount) {
    this.depositAmount = depositAmount;
  }

  public Double getActuallyPaid() {
    return actuallyPaid;
  }

  public void setActuallyPaid(Double actuallyPaid) {
    this.actuallyPaid = actuallyPaid;
  }

  public Long getPurchaseInventoryId() {
    return purchaseInventoryId;
  }

  public void setPurchaseInventoryId(Long purchaseInventoryId) {
    this.purchaseInventoryId = purchaseInventoryId;
  }

  public Long getPayableHistoryId() {
    return payableHistoryId;
  }

  public void setPayableHistoryId(Long payableHistoryId) {
    this.payableHistoryId = payableHistoryId;
  }

  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  public Long getPayableId() {
    return payableId;
  }

  public void setPayableId(Long payableId) {
    this.payableId = payableId;
  }

  public String getMaterialName() {
    return materialName;
  }

  public void setMaterialName(String materialName) {
    this.materialName = StringUtil.getShortString(materialName,0,450);

    setShortMaterialName(StringUtil.getShortString(materialName,0,8));
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

  public Long getPaidTime() {
    return paidTime;
  }

  public void setPaidTime(Long paidTime) {
    this.paidTime = paidTime;
    this.paidTimeStr= DateUtil.dateLongToStr(paidTime,DateUtil.DATE_STRING_FORMAT_DEFAULT);
  }

  public String getPaidTimeStr() {
    return paidTimeStr;
  }

  public void setPaidTimeStr(String paidTimeStr) {
    this.paidTimeStr = paidTimeStr;
  }

  public PayStatus getStatus() {
    return status;
  }

  public void setStatus(PayStatus status) {
    this.status = status;
  }


  public String getPurchaseInventoryIdStr() {
    return purchaseInventoryIdStr;
  }

  public void setPurchaseInventoryIdStr(String purchaseInventoryIdStr) {
    this.purchaseInventoryIdStr = purchaseInventoryIdStr;
  }

  public Double getStrikeAmount() {
    return strikeAmount;
  }

  public void setStrikeAmount(Double strikeAmount) {
    this.strikeAmount = strikeAmount;
  }

  public Long getPurchaseReturnId() {
    return purchaseReturnId;
  }

  public void setPurchaseReturnId(Long purchaseReturnId) {
    this.purchaseReturnId = purchaseReturnId;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public String getPayer() {
    return payer;
  }

  public void setPayer(String payer) {
    this.payer = payer;
  }

  public Long getPayerId() {
    return payerId;
  }

  public void setPayerId(Long payerId) {
    this.payerId = payerId;
  }

  @Override
  public String toString() {
    return "PayableHistoryRecordDTO{" +
        "id=" + id +
        ", shopId=" + shopId +
        ", deduction=" + deduction +
        ", creditAmount=" + creditAmount +
        ", cash=" + cash +
        ", bankCardAmount=" + bankCardAmount +
        ", checkAmount=" + checkAmount +
        ", checkNo='" + checkNo + '\'' +
        ", depositAmount=" + depositAmount +
        ", actuallyPaid=" + actuallyPaid +
        ", purchaseInventoryId=" + purchaseInventoryId +
        ", purchaseInventoryIdStr='" + purchaseInventoryIdStr + '\'' +
        ", payableHistoryId=" + payableHistoryId +
        ", supplierId=" + supplierId +
        ", payableId=" + payableId +
        ", materialName='" + materialName + '\'' +
        ", amount=" + amount +
        ", paidAmount=" + paidAmount +
        ", paidTime=" + paidTime +
        ", paidTimeStr='" + paidTimeStr + '\'' +
        ", status=" + status +
        ", paymentType=" + paymentType +
        ", dayType=" + dayType +
        ", payDateStr='" + payDateStr + '\'' +
        ", customerName='" + customerName + '\'' +
        ", orderType='" + orderType + '\'' +
        ", shortMaterialName='" + shortMaterialName + '\'' +
        '}';
  }
}
