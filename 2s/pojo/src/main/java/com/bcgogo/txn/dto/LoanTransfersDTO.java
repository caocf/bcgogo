package com.bcgogo.txn.dto;

import com.bcgogo.enums.payment.LoanTransfersStatus;
import com.bcgogo.enums.LoanType;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-10-22
 * Time: 下午4:39
 */
public class LoanTransfersDTO {
  private Long id;
  private Long shopId;
  private Long transfersTime;//转账时间
  private String transfersTimeStr;//转账时间
  private Long payTime;//付款时间
  private Long payTimeStr;//付款时间
  private String transfersNumber; //转账账号 账单号
  private Double amount;            //转账金额
  private String amountStr;
  private LoanType type;   //货款分类
  private Long shopVersionId; //店面版本
  private String shopVersionValue;
  private String[] products;  //硬件产品
  private String productsStr;
  private LoanTransfersStatus status; //转账转态
  private String memo;               //备注

  public Long getShopVersionId() {
    return shopVersionId;
  }

  public void setShopVersionId(Long shopVersionId) {
    this.shopVersionId = shopVersionId;
  }

  public String getAmountStr() {
    return amountStr;
  }

  public void setAmountStr(String amountStr) {
    this.amountStr = amountStr;
  }

  //if dto pass check, return is empty.
  //else return str
  public String verifyData() {
    if (amount == null) return "转账金额不能为空";
    else return "";
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

  public Long getTransfersTime() {
    return transfersTime;
  }

  public void setTransfersTime(Long transfersTime) {
    this.setTransfersTimeStr(DateUtil.convertDateLongToString(transfersTime, DateUtil.DATE_STRING_FORMAT_ALL));
    this.transfersTime = transfersTime;
  }

  public String getTransfersNumber() {
    return transfersNumber;
  }

  public void setTransfersNumber(String transfersNumber) {
    this.transfersNumber = transfersNumber;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.setAmountStr(NumberUtil.formatDoubleWithComma(amount, "0.0"));
    this.amount = amount;
  }


  public LoanType getType() {
    return type;
  }

  public void setType(LoanType type) {
    this.type = type;
  }

  public String[] getProducts() {
    return products;
  }

  public void setProducts(String[] products) {
    if (!ArrayUtils.isEmpty(products)) {
      this.setProductsStr(StringUtils.join(products, ","));
    }
    this.products = products;
  }

  public LoanTransfersStatus getStatus() {
    return status;
  }

  public void setStatus(LoanTransfersStatus status) {
    this.status = status;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public String getProductsStr() {
    return productsStr;
  }

  public void setProductsStr(String productsStr) {
    this.productsStr = productsStr;
  }

  public String getTransfersTimeStr() {
    return transfersTimeStr;
  }

  public void setTransfersTimeStr(String transfersTimeStr) {
    this.transfersTimeStr = transfersTimeStr;
  }

  public Long getPayTime() {
    return payTime;
  }

  public void setPayTime(Long payTime) {
    this.payTime = payTime;
  }

  public Long getPayTimeStr() {
    return payTimeStr;
  }

  public void setPayTimeStr(Long payTimeStr) {
    this.payTimeStr = payTimeStr;
  }

  public String getShopVersionValue() {
    return shopVersionValue;
  }

  public void setShopVersionValue(String shopVersionValue) {
    this.shopVersionValue = shopVersionValue;
  }

  @Override
  public String toString() {
    return new StringBuilder().append("id:").append(id)
        .append(" shopId:").append(shopId).append(" transfersTime:").append(transfersTimeStr)
        .append(" transfersNumber:").append(transfersNumber).append("amount:").append(amount)
        .append(" payTime:").append(payTimeStr).append(" productsStr:").append(productsStr).append(" memo:").append(memo)
        .append(" [status:").append(status).append("]").toString();
  }
}
