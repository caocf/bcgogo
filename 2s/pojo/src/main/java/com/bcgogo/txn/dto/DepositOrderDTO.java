package com.bcgogo.txn.dto;

import com.bcgogo.enums.DepositType;
import com.bcgogo.enums.InOutFlag;
import com.bcgogo.user.dto.UserLoginLogDTO;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 充值订单DTO
 * Created by IntelliJ IDEA.
 * User: zhuj
 * Date: 13-5-13
 * Time: 下午8:15
 * To change this template use File | Settings | File Templates.
 */
public class DepositOrderDTO {

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

  private String relatedOrderIdStr;

  private String memo;
  public String getRelatedOrderIdStr() {
    return relatedOrderIdStr;
  }

  public void setRelatedOrderIdStr(String relatedOrderIdStr) {
    this.relatedOrderIdStr = relatedOrderIdStr;
  }

  public String getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(String createdTime) {
    this.createdTime = createdTime;
  }

  public Long getRelatedOrderId() {
    return relatedOrderId;
  }

  public void setRelatedOrderId(Long relatedOrderId) {
    this.relatedOrderId = relatedOrderId;
  }


  public String getRelatedOrderNo() {
    return relatedOrderNo;
  }

  public void setRelatedOrderNo(String relatedOrderNo) {
    this.relatedOrderNo = relatedOrderNo;
  }

  public Long getInOut() {
    return inOut;
  }

  public void setInOut(Long inOut) {
    this.inOut = inOut;
  }

  public DepositOrderDTO() {
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

  public DepositOrderDTO(Long shopId, Double cash, Double bankCardAmount, Double checkAmount, String checkNo, Double actuallyPaid, String depositType, Long customerId, Long inOut, String relatedOrderNo, Long relatedOrderId, String operator) {
    this.shopId = shopId;
    this.cash = cash;
    this.bankCardAmount = bankCardAmount;
    this.checkAmount = checkAmount;
    this.checkNo = checkNo;
    this.actuallyPaid = actuallyPaid;
    this.depositType = depositType;
    this.customerId = customerId;
    this.inOut = inOut;
    this.relatedOrderNo = relatedOrderNo;
    this.relatedOrderId = relatedOrderId;
    this.operator = operator;
    this.relatedOrderIdStr = String.valueOf(relatedOrderId);
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

  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public void buildDepositType() {
    StringBuilder result = new StringBuilder(this.depositType + "|");
    if (DepositType.getDepositTypeBySceneAndInOutFlag(this.depositType, InOutFlag.IN_FLAG) != null && DepositType.getDepositTypeBySceneAndInOutFlag(this.depositType, InOutFlag.IN_FLAG) == DepositType.DEPOSIT) { // 预付金充值订单的特殊逻辑
      if (NumberUtil.round(this.cash, 2) > 0.00) {
        result.append("现金,");
      }
      if (NumberUtil.round(this.bankCardAmount, 2) > 0.00) {
        result.append("银联,");
      }
      if (NumberUtil.round(this.checkAmount, 2) > 0.00) {
        result.append("支票,");
      }
      this.depositType = result.substring(0, result.length()-1);
    } else {
      InOutFlag inOutFlag = InOutFlag.getInOutFlagEnumByCode(this.inOut);
      if (DepositType.getDepositTypeBySceneAndInOutFlag(this.depositType, inOutFlag) != null)
        this.depositType = result.append(DepositType.getDepositTypeBySceneAndInOutFlag(this.depositType, inOutFlag).getValue()).toString();
    }
  }

}
