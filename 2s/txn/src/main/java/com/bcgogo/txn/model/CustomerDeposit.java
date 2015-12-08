package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.CustomerDepositDTO;
import com.bcgogo.utils.NumberUtil;
import org.springframework.beans.BeanUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: zhuj
 * Date: 13-5-10
 * Time: 下午2:12
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "customer_deposit")
public class CustomerDeposit extends LongIdentifier {

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

  private String memo;

  public CustomerDeposit() {
  }

  public CustomerDeposit(CustomerDepositDTO customrDepositDTO) {
    this.actuallyPaid = NumberUtil.numberValue(customrDepositDTO.getActuallyPaid(), 0d);
    this.bankCardAmount = NumberUtil.numberValue(customrDepositDTO.getBankCardAmount(), 0d);
    this.cash = NumberUtil.numberValue(customrDepositDTO.getCash(), 0d);
    this.checkAmount = NumberUtil.numberValue(customrDepositDTO.getCheckAmount(), 0d);
    this.checkNo = customrDepositDTO.getCheckNo();
    this.customerId = customrDepositDTO.getCustomerId();
    this.shopId = customrDepositDTO.getShopId();
    this.memo = customrDepositDTO.getMemo();
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

  @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name = "memo", length = 500)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public CustomerDepositDTO toDTO() {
    CustomerDepositDTO customerDepositDTO = new CustomerDepositDTO();
    BeanUtils.copyProperties(this, customerDepositDTO);
    customerDepositDTO.setId(this.getId());
    return customerDepositDTO;
  }

  public void FromDTO(CustomerDepositDTO customerDepositDTO) {
    BeanUtils.copyProperties(customerDepositDTO, this, new String[]{"id", "payTime"});
  }

}
