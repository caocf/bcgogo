package com.bcgogo.txn.model;

import com.bcgogo.enums.DayType;
import com.bcgogo.enums.PayStatus;
import com.bcgogo.enums.PaymentTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.PayableHistoryRecordDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: zhangchuanlong
 * Date: 12-8-16
 * Time: 下午4:59
 * 付款历史记录表
 */
@Entity
@Table(name = "payable_history_record")
public class PayableHistoryRecord extends LongIdentifier {
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
  /*材料品名*/
  private String materialName;
  /*金额*/
  private Double amount;
  /*已付金额*/
  private Double paidAmount;
  /*采购入库单ID*/
  private Long purchaseInventoryId;
  /*结算历史ID*/
  private Long payableHistoryId;
  /*供应商ID*/
  private Long supplierId;
  /*应付款ID*/
  private Long payableId;
  /*状态*/
  private PayStatus status;

  /*付款类型*/
  private PaymentTypes paymentType;

  /*付款时间*/
  private Long payTime;

  //冲账

  private Double strikeAmount;

  //流水统计扩展字段,流水统计页面下方列表:如果作废 非当天单据 流水统计下方列表要显示
  private DayType dayType;

  //退货冲账的时候才会有此记录
  private Long purchaseReturnId;

  private String payer;     //收款人
  private Long payerId;
  private Double statementAmount;//对账单支付金额

  @Column(name = "day_type")
  @Enumerated(EnumType.STRING)
  public DayType getDayType() {
    return dayType;
  }

  public void setDayType(DayType dayType) {
    this.dayType = dayType;
  }

  public PayableHistoryRecord() {
  }

  public PayableHistoryRecord(PayableHistoryRecordDTO payableHistoryRecordDTO) {
    /*店面ID*/
    this.shopId = payableHistoryRecordDTO.getShopId();
    /*扣款*/
    this.deduction =NumberUtil.numberValue(payableHistoryRecordDTO.getDeduction(),0d) ;
    /*欠款挂账*/
    this.creditAmount = NumberUtil.numberValue(payableHistoryRecordDTO.getCreditAmount(),0d) ;
    /* 现金*/
    this.cash =NumberUtil.numberValue(payableHistoryRecordDTO.getCash(),0d)  ;
    /*  银行卡*/
    this.bankCardAmount =NumberUtil.numberValue(payableHistoryRecordDTO.getBankCardAmount(),0d)  ;
    /*支票*/
    this.checkAmount =NumberUtil.numberValue(payableHistoryRecordDTO.getCheckAmount(),0d)  ;
    /*支票号码*/
    this.checkNo = payableHistoryRecordDTO.getCheckNo();
    /*用定金*/
    this.depositAmount =NumberUtil.numberValue(payableHistoryRecordDTO.getDepositAmount(),0d)  ;
    /*  实付*/
    this.actuallyPaid =NumberUtil.numberValue(payableHistoryRecordDTO.getActuallyPaid(),0d)  ;
    /*采购入库单ID*/
    this.purchaseInventoryId = payableHistoryRecordDTO.getPurchaseInventoryId();
    /*结算历史ID*/
    this.payableHistoryId = payableHistoryRecordDTO.getPayableHistoryId();
    /*供应商ID*/
    this.supplierId = payableHistoryRecordDTO.getSupplierId();
    /*应付款ID*/
    this.payableId = payableHistoryRecordDTO.getPayableId();
    /*材料品名*/
    this.materialName = StringUtil.getShortString(payableHistoryRecordDTO.getMaterialName(),0,450);
    /*金额*/
    this.amount = NumberUtil.numberValue(payableHistoryRecordDTO.getAmount(),0d) ;
    /*已付金额*/
    this.paidAmount = NumberUtil.numberValue(payableHistoryRecordDTO.getPaidAmount(),0d) ;
     /*状态*/
    this.status=payableHistoryRecordDTO.getStatus();

    this.paymentType = payableHistoryRecordDTO.getPaymentType();

    this.payTime = payableHistoryRecordDTO.getPaidTime();

    this.strikeAmount = payableHistoryRecordDTO.getStrikeAmount();

    this.dayType = payableHistoryRecordDTO.getDayType();
    this.purchaseReturnId = payableHistoryRecordDTO.getPurchaseReturnId();
    this.payer = payableHistoryRecordDTO.getPayer();
    this.payerId = payableHistoryRecordDTO.getPayerId();
    this.statementAmount = payableHistoryRecordDTO.getStatementAmount();
  }

  @Column(name = "pay_time")
  public Long getPayTime() {
    return payTime;
  }

  public void setPayTime(Long payTime) {
    this.payTime = payTime;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "deduction")
  public Double getDeduction() {
    return deduction;
  }

  public void setDeduction(Double deduction) {
    this.deduction = deduction;
  }

  @Column(name = "credit_amount")
  public Double getCreditAmount() {
    return creditAmount;
  }

  public void setCreditAmount(Double creditAmount) {
    this.creditAmount = creditAmount;
  }

  @Column(name = "cash")
  public Double getCash() {
    return cash;
  }

  public void setCash(Double cash) {
    this.cash = cash;
  }

  @Column(name = "bank_card_amount")
  public Double getBankCardAmount() {
    return bankCardAmount;
  }

  public void setBankCardAmount(Double bankCardAmount) {
    this.bankCardAmount = bankCardAmount;
  }

  @Column(name = "check_amount")
  public Double getCheckAmount() {
    return checkAmount;
  }

  public void setCheckAmount(Double checkAmount) {
    this.checkAmount = checkAmount;
  }

  @Column(name = "check_no")
  public String  getCheckNo() {
    return checkNo;
  }

  public void setCheckNo(String checkNo) {
    this.checkNo = checkNo;
  }

  @Column(name = "deposit_amount")
  public Double getDepositAmount() {
    return depositAmount;
  }

  public void setDepositAmount(Double depositAmount) {
    this.depositAmount = depositAmount;
  }

  @Column(name = "actually_paid")
  public Double getActuallyPaid() {
    return actuallyPaid;
  }

  public void setActuallyPaid(Double actuallyPaid) {
    this.actuallyPaid = actuallyPaid;
  }

  @Column(name = "purchase_inventory_id")
  public Long getPurchaseInventoryId() {
    return purchaseInventoryId;
  }

  public void setPurchaseInventoryId(Long purchaseInventoryId) {
    this.purchaseInventoryId = purchaseInventoryId;
  }

  @Column(name = "payable_history_id")
  public Long getPayableHistoryId() {
    return payableHistoryId;
  }

  public void setPayableHistoryId(Long payableHistoryId) {
    this.payableHistoryId = payableHistoryId;
  }

  @Column(name = "supplier_id")
  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  @Column(name = "payable_id")
  public Long getPayableId() {
    return payableId;
  }

  public void setPayableId(Long payableId) {
    this.payableId = payableId;
  }

  @Column(name = "material_name")
  public String getMaterialName() {
    return materialName;
  }

  public void setMaterialName(String materialName) {
    this.materialName = StringUtil.getShortString(materialName,0,450);
  }

  @Column(name = "amount")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  @Column(name = "paid_amount")
  public Double getPaidAmount() {
    return paidAmount;
  }

  public void setPaidAmount(Double paidAmount) {
    this.paidAmount = paidAmount;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public PayStatus getStatus() {
    return status;
  }

  public void setStatus(PayStatus status) {
    this.status = status;
  }

  @Column(name = "payment_type_enum")
  @Enumerated(EnumType.STRING)
  public PaymentTypes getPaymentType() {
    return paymentType;
  }

  public void setPaymentType(PaymentTypes paymentTypes) {
    this.paymentType = paymentTypes;
  }

  @Column(name="strike_amount")
  public Double getStrikeAmount() {
    return strikeAmount;
  }

  public void setStrikeAmount(Double strikeAmount) {
    this.strikeAmount = strikeAmount;
  }

  @Column(name="purchase_return_id")
  public Long getPurchaseReturnId() {
    return purchaseReturnId;
  }

  public void setPurchaseReturnId(Long purchaseReturnId) {
    this.purchaseReturnId = purchaseReturnId;
  }

  @Column(name="payer")
  public String getPayer() {
    return payer;
  }

  public void setPayer(String payer) {
    this.payer = payer;
  }

  @Column(name="payer_id")
  public Long getPayerId() {
    return payerId;
  }

  public void setPayerId(Long payerId) {
    this.payerId = payerId;
  }

  @Column(name="statement_amount")
  public Double getStatementAmount() {
    return statementAmount;
  }

  public void setStatementAmount(Double statementAmount) {
    this.statementAmount = statementAmount;
  }

  public PayableHistoryRecordDTO toDTO() {
    PayableHistoryRecordDTO payableHistoryRecordDTO = new PayableHistoryRecordDTO();
    payableHistoryRecordDTO.setId(this.getId());
    /*店面ID*/
    payableHistoryRecordDTO.setShopId(this.shopId);
    /*扣款*/
    payableHistoryRecordDTO.setDeduction(NumberUtil.numberValue(this.deduction,0d));
    /*欠款挂账*/
    payableHistoryRecordDTO.setCreditAmount(NumberUtil.numberValue(this.getCreditAmount(),0d));
    /* 现金*/
    payableHistoryRecordDTO.setCash(NumberUtil.numberValue(this.getCash(),0d));
    /*  银行卡*/
    payableHistoryRecordDTO.setBankCardAmount(NumberUtil.numberValue(this.getBankCardAmount(),0d));
    /*支票*/
    payableHistoryRecordDTO.setCheckAmount(NumberUtil.numberValue(this.getCheckAmount(),0d));
    /*支票号码*/
    payableHistoryRecordDTO.setCheckNo(this.checkNo);
    /*用定金*/
    payableHistoryRecordDTO.setDepositAmount(NumberUtil.numberValue(this.getDepositAmount(),0d));
    /*  实付*/
    payableHistoryRecordDTO.setActuallyPaid(NumberUtil.numberValue(this.getActuallyPaid(),0d));
    /*采购入库单ID*/
    payableHistoryRecordDTO.setPurchaseInventoryId(this.getPurchaseInventoryId());
    /*结算历史ID*/
    payableHistoryRecordDTO.setPayableHistoryId(this.getPayableHistoryId());
    /*供应商ID*/
    payableHistoryRecordDTO.setSupplierId(this.getSupplierId());
    /*应付款ID*/
    payableHistoryRecordDTO.setPayableId(this.getPayableId());
    /*材料品名*/
    payableHistoryRecordDTO.setMaterialName(StringUtil.getShortString(this.getMaterialName(),0,450));
    /*金额*/
    payableHistoryRecordDTO.setAmount(NumberUtil.numberValue(this.getAmount(),0d));
    /*已付金额*/
    payableHistoryRecordDTO.setPaidAmount(NumberUtil.numberValue(this.getPaidAmount(),0d));

    /*付款时间*/
    if(this.getPayTime() == null){
      payableHistoryRecordDTO.setPaidTime(this.getCreationDate());
    }else{
      payableHistoryRecordDTO.setPaidTime(this.getPayTime());
    }

    /*状态*/
    payableHistoryRecordDTO.setStatus(this.getStatus());

    /*付款类型*/
    payableHistoryRecordDTO.setPaymentType(this.getPaymentType());

    /*付款时间*/
    payableHistoryRecordDTO.setPaidTimeStr(DateUtil.dateLongToStr(payableHistoryRecordDTO.getPaidTime()));

    if(this.getPurchaseInventoryId() != null){
      payableHistoryRecordDTO.setPurchaseInventoryIdStr(String.valueOf(this.getPurchaseInventoryId()));
    }

    payableHistoryRecordDTO.setStrikeAmount(this.getStrikeAmount());
    payableHistoryRecordDTO.setDayType(this.getDayType());

    payableHistoryRecordDTO.setPurchaseReturnId(this.getPurchaseReturnId());
    payableHistoryRecordDTO.setPayer(getPayer());
    payableHistoryRecordDTO.setPayerId(getPayerId());
    payableHistoryRecordDTO.setStatementAmount(getStatementAmount());
    return payableHistoryRecordDTO;

  }


  public PayableHistoryRecord fromDTO(PayableHistoryRecordDTO payableHistoryRecordDTO, boolean setId) {
    if (payableHistoryRecordDTO == null) {
      return null;
    }
    if (setId) {
      setId(payableHistoryRecordDTO.getId());
    }
    /*店面ID*/
    this.shopId = payableHistoryRecordDTO.getShopId();
    /*扣款*/
    this.deduction = NumberUtil.numberValue(payableHistoryRecordDTO.getDeduction(), 0d);
    /*欠款挂账*/
    this.creditAmount = NumberUtil.numberValue(payableHistoryRecordDTO.getCreditAmount(), 0d);
    /* 现金*/
    this.cash = NumberUtil.numberValue(payableHistoryRecordDTO.getCash(), 0d);
    /*  银行卡*/
    this.bankCardAmount = NumberUtil.numberValue(payableHistoryRecordDTO.getBankCardAmount(), 0d);
    /*支票*/
    this.checkAmount = NumberUtil.numberValue(payableHistoryRecordDTO.getCheckAmount(), 0d);
    /*支票号码*/
    this.checkNo = payableHistoryRecordDTO.getCheckNo();
    /*用定金*/
    this.depositAmount = NumberUtil.numberValue(payableHistoryRecordDTO.getDepositAmount(), 0d);
    /*  实付*/
    this.actuallyPaid = NumberUtil.numberValue(payableHistoryRecordDTO.getActuallyPaid(), 0d);
    /*采购入库单ID*/
    this.purchaseInventoryId = payableHistoryRecordDTO.getPurchaseInventoryId();
    /*结算历史ID*/
    this.payableHistoryId = payableHistoryRecordDTO.getPayableHistoryId();
    /*供应商ID*/
    this.supplierId = payableHistoryRecordDTO.getSupplierId();
    /*应付款ID*/
    this.payableId = payableHistoryRecordDTO.getPayableId();
    /*材料品名*/
    this.materialName = StringUtil.getShortString(payableHistoryRecordDTO.getMaterialName(),0,450);
    /*金额*/
    this.amount = NumberUtil.numberValue(payableHistoryRecordDTO.getAmount(), 0d);
    /*已付金额*/
    this.paidAmount = NumberUtil.numberValue(payableHistoryRecordDTO.getPaidAmount(), 0d);
    /*状态*/
    this.status = payableHistoryRecordDTO.getStatus();

    this.paymentType = payableHistoryRecordDTO.getPaymentType();
    this.payTime = payableHistoryRecordDTO.getPaidTime();

    this.strikeAmount = payableHistoryRecordDTO.getStrikeAmount();
    this.dayType = payableHistoryRecordDTO.getDayType();

    this.purchaseReturnId = payableHistoryRecordDTO.getPurchaseReturnId();
    this.payer = payableHistoryRecordDTO.getPayer();
    this.payerId = payableHistoryRecordDTO.getPayerId();

    this.statementAmount = payableHistoryRecordDTO.getStatementAmount();
    return this;
  }

}
