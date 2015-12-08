package com.bcgogo.txn.dto;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Created by IntelliJ IDEA.
 * User: zhuj
 * Date: 13-5-10
 * Time: 下午2:42
 * To change this template use File | Settings | File Templates.
 */
public class CustomerDepositDTO {

  private Long id;
  /*店面ID*/
  private Long shopId;
  /*现金*/
  private Double cash;
  /*银行卡*/
  private Double bankCardAmount;
  /*支票*/
  private Double checkAmount;
  /*支票号码*/
  private String checkNo;
  /*实付*/
  private Double actuallyPaid;
  /*客户id*/
  private Long customerId;
  /** 操作人 */
  private String operator;

  /**
   * payTime流水统计初始化使用
   */
  private Long payTime;

  private String memo;

  public Long getPayTime() {
    return payTime;
  }

  public void setPayTime(Long payTime) {
    this.payTime = payTime;
  }

  public CustomerDepositDTO() {
  }

  public CustomerDepositDTO(Long id, Long shopId, Double cash, Double bankCardAmount, Double checkAmount, String checkNo, Double actuallyPaid, Long customerId) {
    this.id = id;
    this.shopId = shopId;
    this.cash = cash;
    this.bankCardAmount = bankCardAmount;
    this.checkAmount = checkAmount;
    this.checkNo = checkNo;
    this.actuallyPaid = actuallyPaid;
    this.customerId = customerId;
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

  public void setCheckNo(String checkNo) {
    this.checkNo = checkNo;
  }

  public Double getActuallyPaid() {
    return actuallyPaid;
  }

  public void setActuallyPaid(Double actuallyPaid) {
    this.actuallyPaid = actuallyPaid;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public String getOperator() {
     return operator;
   }

   public void setOperator(String operator) {
     this.operator = operator;
   }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

}
