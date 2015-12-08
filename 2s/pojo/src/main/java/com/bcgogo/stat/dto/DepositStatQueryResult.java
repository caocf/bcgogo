package com.bcgogo.stat.dto;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: 13-5-22
 * Time: 下午4:33
 * To change this template use File | Settings | File Templates.
 */
public class DepositStatQueryResult {

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
  /**
   * 使用类型
   */
  private String depositType;
  /*客户id*/
  private Long customerId;
  /**
   * 供应商Id
   */
  private Long supplierId;
  /**
   * 出入标示位
   */
  private Long inOut;
  /**
   * 关联订单号
   */
  private String relatedOrderNo;
  /**
   * 关联订单id
   */
  private Long relatedOrderId;
  /**
   * 预收款订单结算时间
   */
  private String createdTime;
  /**
   * 操作人
   */
  private String operator;

  private String name; //姓名
  private String mobile;// 手机
  private String relatedOrderIdStr;
  private String depositTypeStr;    //打印专用



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

  public String getDepositType() {
    return depositType;
  }

  public void setDepositType(String depositType) {
    this.depositType = depositType;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  public Long getInOut() {
    return inOut;
  }

  public void setInOut(Long inOut) {
    this.inOut = inOut;
  }

  public String getRelatedOrderNo() {
    return relatedOrderNo;
  }

  public void setRelatedOrderNo(String relatedOrderNo) {
    this.relatedOrderNo = relatedOrderNo;
  }

  public Long getRelatedOrderId() {
    return relatedOrderId;
  }

  public void setRelatedOrderId(Long relatedOrderId) {
    this.relatedOrderId = relatedOrderId;
  }

  public String getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(String createdTime) {
    this.createdTime = createdTime;
  }

  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getRelatedOrderIdStr() {
    return relatedOrderId == null?"":relatedOrderId.toString();
  }

  public void setRelatedOrderIdStr(String relatedOrderIdStr) {
    this.relatedOrderIdStr = relatedOrderIdStr;
  }

  public String getDepositTypeStr() {
    return depositTypeStr;
  }

  public void setDepositTypeStr(String depositTypeStr) {
    this.depositTypeStr = depositTypeStr;
  }
}
