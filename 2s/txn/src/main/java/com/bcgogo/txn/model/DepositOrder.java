package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.DepositOrderDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import org.springframework.beans.BeanUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 预付金使用订单
 * Created by IntelliJ IDEA.
 * User: zhuj
 * Date: 13-5-13
 * Time: 下午8:10
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "deposit_order")
public class DepositOrder extends LongIdentifier {

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
   * 供应商id
   */
  private Long supplierId;


  /**
   * 出、入标示位
   */
  private Long inOut; // TODO zhuj 这个修改为枚举


  /**
   * 关联订单号
   */
  private String relatedOrderNo;
  
  /**
   * 关联订单id
   */
  private Long relatedOrderId;



  /**
   * 操作人（记录当前上下文 userName）
   */
  private String operator;

  private String memo;

  public DepositOrder() {
  }


  public DepositOrder(Long shopId, Double cash, Double bankCardAmount, Double checkAmount, String checkNo, Double actuallyPaid, String depositType, Long customerId, Long supplierId, Long inOut, String relatedOrderNo,Long relatedOrderId, String operator) {
    this.shopId = shopId;
    this.cash = NumberUtil.numberValue(cash, 0d);
    this.bankCardAmount = NumberUtil.numberValue(bankCardAmount, 0d);
    this.checkAmount = NumberUtil.numberValue(checkAmount, 0d);
    this.checkNo = checkNo;
    this.actuallyPaid = NumberUtil.numberValue(actuallyPaid, 0d);
    this.depositType = depositType;
    this.customerId = customerId;
    this.supplierId = supplierId;
    this.inOut = inOut;
    this.relatedOrderNo = relatedOrderNo;
    this.relatedOrderId=relatedOrderId;
    this.operator = operator;
  }

  public DepositOrder(DepositOrderDTO depositOrderDTO) {
    this.shopId = depositOrderDTO.getShopId();
    this.cash = NumberUtil.numberValue(depositOrderDTO.getCash(), 0d);
    this.bankCardAmount = NumberUtil.numberValue(depositOrderDTO.getBankCardAmount(), 0d);
    this.checkAmount = NumberUtil.numberValue(depositOrderDTO.getCheckAmount(), 0d);
    this.checkNo = depositOrderDTO.getCheckNo();
    this.actuallyPaid = NumberUtil.numberValue(depositOrderDTO.getActuallyPaid(), 0d);
    this.depositType = depositOrderDTO.getDepositType();
    this.customerId = depositOrderDTO.getCustomerId();
    this.supplierId=depositOrderDTO.getSupplierId();
    this.inOut = depositOrderDTO.getInOut();
    this.relatedOrderNo = depositOrderDTO.getRelatedOrderNo();
    this.relatedOrderId=depositOrderDTO.getRelatedOrderId();
    this.operator = depositOrderDTO.getOperator();
    this.memo = depositOrderDTO.getMemo();
  }

  public DepositOrderDTO toDTO() {
    DepositOrderDTO dto = new DepositOrderDTO();
    BeanUtils.copyProperties(this, dto);
     //TODO 这里要设置ID？？
    dto.setCreatedTime(DateUtil.convertDateLongToString(this.getCreationDate(),"yyyy-MM-dd HH:mm"));
    return dto;
  }


  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
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

  @Column(name = "actually_paid")
  public Double getActuallyPaid() {
    return actuallyPaid;
  }

  public void setActuallyPaid(Double actuallyPaid) {
    this.actuallyPaid = actuallyPaid;
  }

  @Column(name = "deposit_type")
  public String getDepositType() {
    return depositType;
  }

  public void setDepositType(String depositType) {
    this.depositType = depositType;
  }

  @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }


  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name = "supplier_id")
  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }


  @Column(name = "in_out")
  public Long getInOut() {
    return inOut;
  }

  public void setInOut(Long inOut) {
    this.inOut = inOut;
  }

  @Column(name = "related_order_no")
  public String getRelatedOrderNo() {
    return relatedOrderNo;
  }

  public void setRelatedOrderNo(String relatedOrderNo) {
    this.relatedOrderNo = relatedOrderNo;
  }

  @Column(name ="related_order_id")
   public Long getRelatedOrderId() {
    return relatedOrderId;
  }

  public void setRelatedOrderId(Long relatedOrderId) {
    this.relatedOrderId = relatedOrderId;
  }

  @Column(name = "operator")
  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  @Column(name = "memo", length = 500)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }
}
