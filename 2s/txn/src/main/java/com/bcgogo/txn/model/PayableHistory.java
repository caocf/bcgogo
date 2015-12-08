package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.PayableHistoryDTO;
import com.bcgogo.utils.NumberUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: zhangchuanlong
 * Date: 12-8-16
 * Time: 下午4:44
 * 付款历史表
 */
@Entity
@Table(name = "payable_history")
public class PayableHistory extends LongIdentifier {
  /*店面ID*/
  private Long shopId;
  /* 扣款*/
  private Double deduction;
  /*欠款挂账*/
  private Double creditAmount;
  /*现金*/
  private Double cash;
  /*银行卡*/
  private Double bankCardAmount;
  /*支票*/
  private Double checkAmount;
  /*支票号码*/
  private String checkNo;
  /*定金*/
  private Double depositAmount;
  /*实付*/
  private Double actuallyPaid;
  /*供应商ID*/
  private Long supplierId;
  /*状态 */
  private String status;

  //冲账
  private Double strikeAmount;

  private String payer;     //收款人
  private Long payerId;

  private Long payTime;//付款时间
  
  public PayableHistory() {
  }

  public PayableHistory(PayableHistoryDTO payableHistoryDTO) {
    this.shopId = payableHistoryDTO.getShopId();
    /* 扣款*/
    this.deduction = payableHistoryDTO.getDeduction();
    /*欠款挂账*/
    this.creditAmount = NumberUtil.numberValue(payableHistoryDTO.getCreditAmount(), 0D);
    /*现金*/
    this.cash = NumberUtil.numberValue(payableHistoryDTO.getCash(), 0D);
    /*银行卡*/
    this.bankCardAmount = NumberUtil.numberValue(payableHistoryDTO.getBankCardAmount(), 0D);
    /*支票*/
    this.checkAmount = NumberUtil.numberValue(payableHistoryDTO.getCheckAmount(), 0D);
    /*支票号码*/
    this.checkNo = payableHistoryDTO.getCheckNo();
    /*定金*/
    this.depositAmount = NumberUtil.numberValue(payableHistoryDTO.getDepositAmount(), 0D);
    /*实付*/
    this.actuallyPaid = NumberUtil.numberValue(payableHistoryDTO.getActuallyPaid(), 0D);
    /*供应商ID*/
    this.supplierId = payableHistoryDTO.getSupplierId();

    this.strikeAmount = payableHistoryDTO.getStrikeAmount();
    this.payer = payableHistoryDTO.getPayer();
    this.payerId = payableHistoryDTO.getPayerId();
    this.payTime = payableHistoryDTO.getPayTime();
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
  public String getCheckNo() {
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

  @Column(name = "supplier_id")
  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  @Column(name="strike_amount")
  public Double getStrikeAmount() {
    return strikeAmount;
  }

  public void setStrikeAmount(Double strikeAmount) {
    this.strikeAmount = strikeAmount;
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

  @Column(name="pay_time")
  public Long getPayTime() {
    return payTime;
  }

  public void setPayTime(Long payTime) {
    this.payTime = payTime;
  }

  public PayableHistoryDTO toDTO() {
    PayableHistoryDTO payableHistoryDTO = new PayableHistoryDTO();
    payableHistoryDTO.setId(this.getId());
    payableHistoryDTO.setShopId(this.getShopId());
    /* 扣款*/
    payableHistoryDTO.setDeduction(NumberUtil.numberValue(this.getDeduction(), 0D));
    /*欠款挂账*/
    payableHistoryDTO.setCreditAmount(NumberUtil.numberValue(this.getCreditAmount(), 0D));
    /*现金*/
    payableHistoryDTO.setCash(NumberUtil.numberValue(this.getCash(), 0D));
    /*银行卡*/
    payableHistoryDTO.setBankCardAmount(NumberUtil.numberValue(this.getBankCardAmount(), 0D));
    /*支票*/
    payableHistoryDTO.setCheckAmount(NumberUtil.numberValue(this.getCheckAmount(), 0D));
    /*支票号码*/
    payableHistoryDTO.setCheckNo(this.getCheckNo());
    /*定金*/
    payableHistoryDTO.setDepositAmount(NumberUtil.numberValue(this.getDepositAmount(), 0D));
    /*实付*/
    payableHistoryDTO.setActuallyPaid(NumberUtil.numberValue(this.getActuallyPaid(), 0D));
    /*供应商ID*/
    payableHistoryDTO.setSupplierId(this.getSupplierId());

    payableHistoryDTO.setStrikeAmount(this.getStrikeAmount());
    payableHistoryDTO.setPayer(getPayer());
    payableHistoryDTO.setPayerId(getPayerId());
    return payableHistoryDTO;
  }
}
